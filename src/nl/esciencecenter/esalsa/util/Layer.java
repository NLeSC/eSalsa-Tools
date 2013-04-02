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

package nl.esciencecenter.esalsa.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** 
 * Layer represents a subdivision of a grid into one or more sets of blocks.
 * 
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * @version 1.0
 * @since 1.0
 * @see Grid
 * @see Set
 * @see Block
 *
 */
public class Layer implements Iterable<Set> {

	/** The name of this layer */
	public final String name; 

	// The sets in this layer.  
	private final ArrayList<Set> sets = new ArrayList<Set>();
	
	/** 
	 * Creates an empty layer. 
	 * 
	 * @param name the name for the layer. 
	 */
	public Layer(String name) { 
		this.name = name;
	}

	/**
	 * Add all Sets in the Collection to this layer. 
	 * 
	 * @param collection the collection of sets to add.
	 * @see Set
	 * @see Collection
	 */
	public void addAll(Collection<Set> collection) {
		
		if (collection == null || collection.size() == 0) {  
			throw new NullPointerException("Set may not be null!");
		}

		for (Set s : collection) { 
			add(s);
		}
	}
	
	/** 
	 * Add a Set to this layer. 
	 * 
	 * @param set the set to add.
	 * @see Set 
	 */	
	public void add(Set set) { 
		
		if (set == null) {  
			throw new NullPointerException("Set may not be null!");
		}
		
		sets.add(set);
	}
	
	/** 
	 * Retrieves the Set at a given index. 
	 * 
	 * @param index the index of the set to retrieve. Must be between 0 (inclusive) and {@link #size()} (exclusive). 
	 * @return the set at the given index.
	 * @see Set 
	 */	
	public Set get(int index) { 
		
		if (index < 0 || index >= sets.size()) {  
			throw new NoSuchElementException("Invalid index: " + index);
		}
		
		return sets.get(index);
	}
	
	/** 
	 * Retrieves a Set in this layer that contains the Block with Coordinate (x,y).
	 * 
	 * @param x the x location of the block.
	 * @param y the y location of the block.
	 * @return the set that contains the specified block, or null if no set contained the block.
	 * @see Set
	 * @see Block 
	 * @see Coordinate 
	 */	
	public Set locate(int x, int y) { 	
		
		for (Set s : sets) { 			
			if (s.contains(x, y)) { 
				return s;
			}
		}
		
		return null;
	}

	/** 
	 * Returns the number of Sets in this layer.  
	 * 
	 * @return the number of sets in this layer.
	 * @see Set 
	 */
	public int size() { 
		return sets.size();
	}

	@Override
	public Iterator<Set> iterator() {
		return sets.iterator();
	}
}
