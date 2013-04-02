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
import java.util.Collection;

import nl.esciencecenter.esalsa.util.Block;
import nl.esciencecenter.esalsa.util.Set;

/**
 * A SimpleSplit is capable of splitting a set of blocks into a specified number of subsets that are arranged in a roughly 
 * linear fashion. See {@link #split(Collection)} for details.
 * 
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * @version 1.0
 * @since 1.0
 * 
 */
public class SimpleSplit extends Split {

	/** The sizes of each of the subsets. */
	private final int [] targetWork;
		
	/** 
	 * Create a SimpleSplit that will split the <code>set</code> into <code>subsets</code> parts.
	 * 
	 * @param set the set to split.
	 * @param subsets the number of subsets to create.
	 */
	public SimpleSplit(Set set, int subsets) { 
	
		super(set, subsets);
		
		targetWork = new int[subsets];
		
		if (set.size() < subsets) { 
			throw new IllegalArgumentException("Cannot split set with " + set.size() + " work into " + subsets + " parts!");
		}
		
		int div = set.size() / subsets;
		int mod = set.size() % subsets;
		
		for (int i=0;i<subsets;i++) { 
			targetWork[i] = div;
			
			if (i < mod) { 
				targetWork[i]++;
			}
		}
	}
	
	/** 
	 * Splits a set into <code>targetWork.length</code> subsets, such that subset <code>i</code> contains 
	 * <code>targetWork[i]</code> work. 
	 * <p>
	 * The split is performed by traversing horizontally over the source set, moving from bottom to top in a zigzag pattern.  
	 *  
	 * @param s the set to split. 
	 * @param targetWork an array containing the number of block for each subset. 
	 */
	private void zigzagHorizontal(Collection<Set> result) { 

		int index = 0;
		
		ArrayList<Block> tmp = new ArrayList<Block>();
	
		// start bottom left and zigzag horizontally 
		// until we reach top right.
		for (int y=set.minY;y<=set.maxY;y++) { 
			if (y % 2 == 0) { 
				// left to right
				for (int x=set.minX;x<=set.maxX;x++) { 

					Block b = set.get(x, y);

					if (b != null) { 
						tmp.add(b);

						if (tmp.size() == targetWork[index]) { 
							result.add(new Set(tmp, index));
							tmp.clear();
							index++;
						}
					}	
				}
			} else { 
				// right to left
				for (int x=set.maxX;x>=set.minX;x--) { 

					Block b = set.get(x, y);

					if (b != null) { 
						tmp.add(b);

						if (tmp.size() == targetWork[index]) { 
							result.add(new Set(tmp, index));
							tmp.clear();
							index++;
						}
					}	
				}
			}
		}
	}

	/** 
	 * Splits the set into <code>targetWork.length</code> subsets, such that subset <code>i</code> contains 
	 * <code>targetWork[i]</code> work. 
	 * <p>
	 * The split is performed by traversing vertically over the source set, left to right up in a zigzag pattern.  
	 *  
	 * @param s the set to split. 
	 * @param targetWork an array containing the number of block for each subset. 
	 */
	private void zigzagVertical(Collection<Set> result) { 

		int index = 0;
		
		ArrayList<Block> tmp = new ArrayList<Block>();
	
		for (int x=set.minX;x<=set.maxX;x++) { 
			if (x % 2 == 0) { 
				// top to bottom
				for (int y=set.minY;y<=set.maxY;y++) { 

					Block b = set.get(x, y);

					if (b != null) { 
						tmp.add(b);

						if (tmp.size() == targetWork[index]) { 
							result.add(new Set(tmp, index));
							tmp.clear();
							index++;
						}
					}	
				}
			} else { 
				// right to left
				for (int y=set.maxY;y>=set.minY;y--) { 

					Block b = set.get(x, y);

					if (b != null) { 
						tmp.add(b);

						if (tmp.size() == targetWork[index]) { 
							result.add(new Set(tmp, index));
							tmp.clear();
							index++;
						}
					}	
				}
			}
		}
	}
	
	/**
	 * Perform the split of the set into subsets and store these in the provided Collection.
	 * <p>
	 * Set is split into subsets in a linear fashion (resulting in a 1xN or Nx1 division). If the set is wider that it is high,  
     * it will be split horizontally (Nx1), if it is higher than it is wide, it will be split vertically (1xN).   
	 * 
	 * @param result the Collection to store the result in. 
	 */
	@Override
	public void split(Collection<Set> result) { 
	
		if (set.getWidth() < set.getHeight()) { 
			zigzagHorizontal(result);
		} else { 
			zigzagVertical(result);
		}
	}
}