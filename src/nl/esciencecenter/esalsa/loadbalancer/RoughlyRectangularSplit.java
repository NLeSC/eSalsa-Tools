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

import nl.esciencecenter.esalsa.util.Block;
import nl.esciencecenter.esalsa.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A RoughlyRectangularSplit is capable of splitting a set of blocks into a specified number of subsets that are arranged in a 
 * roughly rectangular grid of sets. See {@link #split(Collection)} for details.
 * 
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * @version 1.0
 * @since 1.0
 * 
 */
public class RoughlyRectangularSplit extends Split {
	
	/** A logger used for debugging. */
	private static final Logger logger = LoggerFactory.getLogger(RoughlyRectangularSplit.class);
	
	/** The amount of blocks per subset. */ 
	protected final int workPerPart;
	
	/** The amount of blocks left over. */
	protected final int workLeft;
	
	/**
	 * Create a new RoughlyRectangularSplit for a given set and number of subsets.
	 * 
	 * @param set the set to split
	 * @param subsets the number of subsets to create.
	 */
	public RoughlyRectangularSplit(Set set, int subsets) { 

		super(set, subsets);

		if (set.size() < subsets) { 
			throw new IllegalArgumentException("Cannot split set with " + set.size() + " work into " + subsets + " parts!");
		}
		
		workPerPart = set.size() / subsets;
		workLeft = set.size() % subsets;
	}

	/**
	 * Splits a given amount of <code>work</code> into <code>subsets</code> parts.
	 * 
	 * @param work the amount work to split.
	 * @param subsets the number of subsets to split into. 
	 * @return an array of length <code>parts</code> containing the size of each subset.  
	 */
	protected int [] splitWork(int work, int subsets) { 
		
		int [] result = new int[subsets];

		int workPerPart = work / subsets;
		int workLeft = work % subsets;
		
		for (int i=0;i<subsets;i++) {
			result[i] = workPerPart;

			if (i < workLeft) { 
				result[i]++;
			}
		}
		
		return result;
	}
	
	/**
	 * Splits a given amount of <code>work</code> into <code>slices.length</code> subsets, according to the relative sizes 
	 * described in <code>slices</code>. 
	 * <p>
	 * For example, <code>slices</code> could contain [2,2,3] to indicate that the work should be split into 3 parts with the 
	 * relative sizes 2,2,3 (the last row has an extra column). 
	 * <p>
	 * If <code>work</code> has a value of 100. If is now up to this method to determine the division of 100 work into 7 parts, 
	 * such that the relative sizes of the slices adhere to the 2,2,3 distribution.     
	 * 
	 * @param work the amount of work to split
	 * @param slices an array describing the relative amount of work for each row. 
	 * @param totalParts the total number of subsets to create. 
	 * @return an array containing the amount of work for each slice.
	 */
	protected int [] splitWork(int work, int [] slices, int totalParts) { 
		
		int [] result = new int[slices.length];

		int workPerSlice = work / totalParts;
		int workLeftPerSlice = work % totalParts;
		
		for (int i=0;i<slices.length;i++) {
			result[i] = workPerSlice * slices[i];
		}

		int index = 0;
		
		while (workLeftPerSlice > 0) {
			int assign = Math.min(slices[index], workLeftPerSlice);
			result[index] += assign;
			workLeftPerSlice -= assign;
			index++;
		}
		
		
		return result;
	}
	
	/** 
	 * Splits a set into <code>targetWork.length</code> subsets, such that subset <code>i</code> contains 
	 * <code>targetWork[i]</code> work. 
	 * <p>
	 * The split is performed by traversing horizontally over the source set, moving from bottom to top in a zigzag pattern.  
	 *  
	 * @param s the set to split. 
	 * @param targetWork an array containing the number of block for each subset. 
	 * @param reverse reverse the direction of the zigzag pattern.
	 * @return an array containing the generates subsets. 
	 */
	protected Set [] splitHorizontal(Set s, int [] targetWork, boolean reverse) { 
		
		final int direction = reverse ? 1 : 0;
		
		Set [] result = new Set[targetWork.length];
		ArrayList<Block> tmp = new ArrayList<Block>();

		int index = 0;
		
		// start bottom left and zigzag horizontally 
		// until we reach top right.
		for (int y=s.minY;y<=s.maxY;y++) { 
			if (y % 2 == direction) { 
				// left to right
				for (int x=s.minX;x<=s.maxX;x++) { 

					Block b = s.get(x, y);

					if (b != null) { 
						tmp.add(b);

						if (tmp.size() == targetWork[index]) { 
							result[index] = new Set(tmp, index);
							tmp.clear();
							index++;
						}
					}	
				}
			} else { 
				// right to left
				for (int x=s.maxX;x>=s.minX;x--) { 

					Block b = s.get(x, y);

					if (b != null) { 
						tmp.add(b);

						if (tmp.size() == targetWork[index]) { 
							result[index] = new Set(tmp, index);
							tmp.clear();
							index++;
						}
					}	
				}
			}
		}

		return result;
	}

	/** 
	 * Splits a set into <code>targetWork.length</code> subsets, such that subset <code>i</code> contains 
	 * <code>targetWork[i]</code> work. 
	 * <p>
	 * The split is performed by traversing vertically over the source set, left to right up in a zigzag pattern.  
	 *  
	 * @param s the set to split. 
	 * @param targetWork an array containing the number of block for each subset.
	 * @param reverse reverse the direction of the zigzag pattern. 
	 * @return an array containing the generates subsets.
	 */
	protected Set [] splitVertical(Set s, int [] targetWork, boolean reverse) { 
		
		final int direction = reverse ? 1 : 0;
		
		Set [] result = new Set[targetWork.length];
		ArrayList<Block> tmp = new ArrayList<Block>();

		int index = 0;

		for (int x=s.minX;x<=s.maxX;x++) { 
			if (x % 2 == direction) { 
				// top to bottom
				for (int y=s.minY;y<=s.maxY;y++) { 

					Block b = s.get(x, y);

					if (b != null) { 
						tmp.add(b);

						if (tmp.size() == targetWork[index]) { 
							result[index] = new Set(tmp, index);
							tmp.clear();
							index++;
						}
					}	
				}
			} else { 
				// right to left
				for (int y=s.maxY;y>=s.minY;y--) { 

					Block b = s.get(x, y);

					if (b != null) { 
						tmp.add(b);

						if (tmp.size() == targetWork[index]) { 
							result[index] = new Set(tmp, index);
							tmp.clear();
							index++;
						}
					}	
				}
			}
		}

		return result;
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
		
		if (set.getWidth() < set.getHeight()) { 
			
			Set [] slices = splitHorizontal(set, workPerSlice, false);
			
			for (int i=0;i<slices.length;i++) { 

				int [] workPerPart = splitWork(slices[i].size(), subSlices[i]);
				
				Set [] parts = splitVertical(slices[i], workPerPart, false);
				
				for (int j=0;j<parts.length;j++) { 
					result.add(parts[j]);
				}
			}
		
		} else { 
			
			Set [] slices = splitVertical(set, workPerSlice, false);

			for (int i=0;i<slices.length;i++) { 

				int [] workPerPart = splitWork(slices[i].size(), subSlices[i]);

				Set [] parts = splitHorizontal(slices[i], workPerPart, false);

				for (int j=0;j<parts.length;j++) { 
					result.add(parts[j]);
				}
			}
		}
	}
	
	/**
	 * Divides <code>parts*subParts+leftOver</code> elements as evenly as possible over <code>parts</code> slots.
	 * 
	 * Note that <code>leftOver < parts</code>.    
	 * 
	 * @param parts the number of slots to divide over. 
	 * @param subParts the minimal number of subParts in each slot. 
	 * @param leftOver the leftOver parts that need to be divided evenly over the slots. 
	 * @return an array of length <code>parts</code> containing the number of subsets assigned to each part. 
	 */
	protected int [] createSubParts(int parts, int subParts, int leftOver) { 

		int [] result = new int[parts];
		
		for (int i=0;i<parts;i++) { 
			result[i] = subParts;
		}

		if (leftOver > 0) { 
			for (int i=0;i<leftOver;i++) { 
				result[i]++;
			}	
		} else if (leftOver < 0) {			
			for (int i=parts+leftOver;i<parts;i++) { 
				result[i]--;
			}
		}
		
		return result;
	}		
	
	/**
	 * Perform the split of the set into <code>subset</code> subsets and store the result in the Collection.
	 * <p>
	 * By taking the square root of the desired number of subsets, an initial estimate is made on what the dimension of this grid 
	 * should be. The possible results are:
	 * <p>  
	 * <ul>
	 * <li> if <code>floor(root) == ceil(root)</code> the grid is a perfect square.</li>   
	 * 
	 * <li> if <code>floor(root) * ceil(root) == subsets</code> the grid is a perfect rectangle.</li>
	 * <li> otherwise the grid is an imperfect rectangle, where some rows of the grid contain more columns than others</li> 
	 * </ul>
	 * <p>
	 * Based on the initial estimation, the division onto subsets is then made by splitting the set first row wise and then 
	 * column wise (if the set is wider than it is height), or first column wise and then row wise (if the set is higher than it 
	 * is wide). 
	 * <p>
	 * When the initial split is made, the number of subsets that will be created in the second split is taken into account. For 
	 * example, when a set is split into 3 rows, which will subsequently be split into 2, 2, and 3 columns, the last row will 
	 * receive 1/7th more blocks than the first two because of the extra column.
	 * 
	 * @param result the Collection to store the result in. 
	 */
	@Override
	public void split(Collection<Set> result) { 

		if (logger.isDebugEnabled()) { 
			logger.debug("Attempting to split set of size " + set.size() + " into " + parts + " parts.");
			logger.debug("Work per part: " + workPerPart);
			logger.debug("Work left over: " + workLeft);
		}
		
		// We would like to split the set into a rectangular grid. This 
		// may not always be possible, due to the number of parts required, 
		// or the shape of the set of blocks provided. By taking the root of 
		// the number of parts, we can come up with an approximation of the 
		// grid we want. 
		
		double root = Math.sqrt(parts);
		
		int lowRoot = (int) Math.floor(root);
		int highRoot = (int) Math.ceil(root);

		if (logger.isDebugEnabled()) { 
			logger.debug("Determining grid size:");
		}
		
		// We have a number of possible results here:
		//
		// If (lowRoot == highRoot) the grid is a perfect square 
		// If (lowRoot * highRoot == parts) the grid is a perfect rectangle
		// If (parts < lowRoot * highRoot) the grid is an imperfect rectangle
		// If (parts > lowRoot * highRoot) the grid is an imperfect rectangle
				
		if (lowRoot == highRoot) { 
			// The number of parts can be split into a perfect square grid.
			if (logger.isDebugEnabled()) { 
				logger.debug("Grid is perfect square: " + lowRoot + "x" + lowRoot);
			}
			
			split(createSubParts(lowRoot, lowRoot, 0), result);
			
		} else if (parts == lowRoot*highRoot) { 
			// The number of parts can be split into a perfect rectangular grid.
			if (logger.isDebugEnabled()) { 
				logger.debug("Grid is perfect rectangle: " + highRoot + "x" + lowRoot);
				logger.debug("  Work per slice (low): " + (set.size() / lowRoot) + " leftover " + (set.size() % lowRoot));
				logger.debug("  Work per slice (high): " + (set.size() / highRoot) + " leftover " + (set.size() % highRoot));
			}

			split(createSubParts(highRoot, lowRoot, 0), result);
			// split(createSubParts(lowRoot, highRoot, 0));
		
		} else if (parts < lowRoot*highRoot) {  
			// The number of parts cannot be split perfectly.			
			int below = parts - (lowRoot * lowRoot);
			int above = (lowRoot * highRoot) - parts;
			
			if (logger.isDebugEnabled()) { 
				logger.debug("Grid is imperfect, lowRoot=" + lowRoot + " highRoot=" + highRoot);
				logger.debug(" Solution below: " + lowRoot + "x" + lowRoot + " + " + below);
				logger.debug(" Solution above: " + highRoot + "x" + lowRoot + " - " + above);
			}
			
			//split(createSubParts(lowRoot, lowRoot, below));
			split(createSubParts(highRoot, lowRoot, -above), result);
			
		} else { // (parts > lowRoot*highRoot)
		
			int below = parts - (lowRoot * highRoot);
			int above = (highRoot * highRoot) - parts;
			
			if (logger.isDebugEnabled()) { 
				logger.debug("Grid is imperfect, lowRoot=" + lowRoot + " highRoot=" + highRoot);
				logger.debug(" Solution below: " + lowRoot + "x" + highRoot + " + " + below);
				logger.debug(" Solution above: " + highRoot + "x" + highRoot + " - " + above);
			}
			
			// split(createSubParts(lowRoot, highRoot, below));
			split(createSubParts(highRoot, highRoot, -above), result);
		} 
	}	
}
