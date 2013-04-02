/*
 * Copyright 2013 Netherlands eScience Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.esciencecenter.esalsa.loadbalancer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import nl.esciencecenter.esalsa.util.Neighbours;
import nl.esciencecenter.esalsa.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A SearchSplit is an extension of a RoughlyRectangularSplit that generates all valid roughly rectangular grid of sets and tests 
 * each of them to see which solution offers the minimal amount of communication between nodes. 
 * 
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * @version 1.0
 * @since 1.0
 * 
 */
public class SearchSplit extends RoughlyRectangularSplit {
	
	/** A logger used for debugging. */
	private static final Logger logger = LoggerFactory.getLogger(SearchSplit.class);
	
	/** The neighbour function to use */
	protected final Neighbours neighbours;
	
	private class Solution { 
		final Set [] solution;
		final int [] permutation;
		
		Solution(Set [] solution, int [] permutation) { 
			this.solution = solution;
			this.permutation = permutation;
		}
	}
	
	/**
	 * Create a new SearchSplit for a given set, number of subsets and neigbour function. 
	 * 
	 * @param set the set to split
	 * @param subsets the number of subsets to create.
	 */
	public SearchSplit(Set set, int subsets, Neighbours neigbours) { 

		super(set, subsets);
		
		this.neighbours = neigbours;
		
		if (set.size() < subsets) { 
			throw new IllegalArgumentException("Cannot split set with " + set.size() + " work into " + subsets + " parts!");
		}
	}
	
	private int getCommunication(Set [] sets) { 
		
		int communication = 0;
		
		for (int i=0;i<sets.length;i++) { 
			communication += sets[i].getCommunication(neighbours);
		}
				
		return communication;		
	}

	private Set [] findBestSplitFourWays(Set set, int [] workPerSlice) { 

		// We now have 4 options here: >, <, ^, v
		Set [][] solutions = new Set[4][];
		
		solutions[0] = splitHorizontal(set, workPerSlice, false);
		solutions[1] = splitHorizontal(set, workPerSlice, true);
		solutions[2] = splitVertical(set, workPerSlice, false);
		solutions[3] = splitVertical(set, workPerSlice, true);
		
		// Now select the best of the four
		Set [] best = solutions[0];
		int bestCommunication = getCommunication(solutions[0]);
		
		if (logger.isDebugEnabled()) { 
			logger.debug("   solution[0] " + bestCommunication);
		}
		
		for (int i=1;i<4;i++) { 
					
			int tmp = getCommunication(solutions[i]);
			
			if (logger.isDebugEnabled()) { 
				logger.debug("   solution[" + i + "] " + tmp);
			}
			
			if (tmp < bestCommunication) { 
				best = solutions[i];
				bestCommunication = tmp;
			}
		}

		if (logger.isDebugEnabled()) { 
			logger.debug("   best solution -- " + bestCommunication);
		}
		
		return best;
	}
	
	@SuppressWarnings("rawtypes")
	private Solution findBestSplit(Set set, int [] workPerSlice) { 

		Set [] best = null;
		int [] bestPerm = null;
		int bestCommunication = Integer.MAX_VALUE;
		
		// We should test all permutations of workPerSlice here.
		ArrayList permutations = new ArrayList();
		
		getIndexPermutations(workPerSlice.length, permutations);
		// getPermutations(workPerSlice, permutations);

		for (int i=0;i<permutations.size();i++) { 

			int [] perm = (int []) permutations.get(i);
			int [] work = new int[workPerSlice.length];
			
			for (int j=0;j<workPerSlice.length;j++) { 
				work[j] = workPerSlice[perm[j]];
			}
		
			if (logger.isDebugEnabled()) { 
				logger.debug(" TESTING: " + Arrays.toString(perm) + " " + Arrays.toString(work));
			}
			
			Set [] tmp = findBestSplitFourWays(set, work);
			int communication = getCommunication(tmp);
			
			if (communication < bestCommunication) { 
				best = tmp;
				bestCommunication = communication;
				bestPerm = perm;
			
				if (logger.isDebugEnabled()) { 
					logger.debug("+++ RESULT: " + Arrays.toString(perm) + " " + Arrays.toString(work) + " " + communication);
				}
			} else {
				if (logger.isDebugEnabled()) { 
					logger.debug("--- RESULT: " + Arrays.toString(perm) + " " + Arrays.toString(work) + " " + communication);
				}
			}
		}
		
		return new Solution(best, bestPerm);
		
/*		
		System.out.println("Current = " + bestCommunication);
		
		for (int i=current+1;i<workPerSlice.length;i++) { 
		
			int [] permute = workPerSlice.clone();
			
			int swap = permute[current];
			permute[current] = permute[i];
			permute[i] = swap;
		
			System.out.println("Testing = " + Arrays.toString(permute));
			
			Set [] tmp = findBestSplitFourWays(set, permute);
			int tmpCommunication = getCommunication(best);
			
			if (tmpCommunication < bestCommunication) { 
				bestCommunication = tmpCommunication;
				best = tmp;
				
				System.out.println("Current = " + bestCommunication);					
			}
		}
		return best;
*/		
	}

	private int [] swap(int [] input, int i, int j) { 
		int [] copy = input.clone();
		copy[i] = input[j];
		copy[j] = input[i];
		return copy;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getPermutation(int [] input, int start, Collection output) { 
		
		if (start == input.length) { 
			output.add(input.clone());
			System.out.println(Arrays.toString(input));
			return;
		}

		getPermutation(input, start+1, output);
		
		for (int i=start+1;i<input.length;i++) { 
			if (input[start] != input[i]) { 
				input = swap(input, start, i);
				getPermutation(input, start+1, output);
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void getIndexPermutations(int len, Collection output) {
		
		int [] index = new int[len];
		
		for (int  i=0;i<index.length;i++) { 
			index[i] = i;
		}
		
		// getPermutation(input, 0, output);
		
		getPermutation(index, 0, output);
	}

	/** 
	 * Split the set into <code>subSlices.length</code> subsets, after which each subset <code>i</code> is split into 
	 * <code>subSlices[i]</code> subsets. These subsets are that stored in the collection.  
	 * 
	 * @param subSlices array describing how the set should be split. 
	 * @param result a Collection in which the resulting subsets are stored. 
	 */
	protected void split(int [] subSlices, Collection<Set> result) { 

		if (logger.isDebugEnabled()) { 
			logger.debug("Splitting set of size " + set.size() + " into " + Arrays.toString(subSlices));
		}
		
		int [] workPerSlice = splitWork(set.size(), subSlices, parts);

		if (logger.isDebugEnabled()) { 
			logger.debug(" Work per slice: " + Arrays.toString(workPerSlice));
		}
		
		Solution solution = findBestSplit(set, workPerSlice);
						
		for (int i=0;i<solution.solution.length;i++) { 

			if (logger.isDebugEnabled()) { 
				logger.debug("Splitting SUB " + i + " ---------------------");
			}
			
			int [] workPerPart = splitWork(solution.solution[i].size(), subSlices[solution.permutation[i]]);
			
			Solution tmp = findBestSplit(solution.solution[i], workPerPart);
			
			for (int j=0;j<tmp.solution.length;j++) { 
				result.add(tmp.solution[j]);
			}
		}
	}
}
