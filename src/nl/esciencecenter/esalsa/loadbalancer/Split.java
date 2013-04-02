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

import java.util.Collection;

import nl.esciencecenter.esalsa.util.Set;

/**
 * A Split is an abstract parent class of all splitters. 
 * 
 * A splitter is capable of splitting a set of blocks into a specified number of subsets. 
 * See {@link #split(Collection)} for details.
 * 
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * @version 1.0
 * @since 1.0
 */
public abstract class Split {
	
	/** The set to split */
	protected final Set set;
	
	/** The number of subsets to create. */
	protected final int parts;
	
	/**
	 * Create a Split for the given set. 
	 * 
	 * @param set the set to split.
	 * @param subsets the number of subsets to split the set into. 
	 */
	protected Split(Set set, int subsets) { 
		this.set = set;
		this.parts = subsets;
	}
	
	/**
	 * Perform the split of the set into subsets and store these in the provided Collection.
	 *  
	 * @param result
	 */
	public abstract void split(Collection<Set> result);
}
