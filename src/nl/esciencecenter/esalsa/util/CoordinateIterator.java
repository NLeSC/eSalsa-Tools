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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator for Coordinates. 
 * 
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * @version 1.0
 * @since 1.0
 * @see Coordinate
 * 
 */
public class CoordinateIterator implements Iterator<Coordinate> {

	/** The Coordinates to iterate over. */
	private final Coordinate [] coordinates;
	
	/** The index of the next Coordinate to return. */
	private int index;
	
	/** 
	 * Creates a CoordinateIterator that will iterate over the {@link Coordinate}s in the provided array.    
	 * 
	 * @param coordinates an array of Coordinates to iterate over. 
	 */
	public CoordinateIterator(Coordinate [] coordinates) { 	
		this.coordinates = coordinates;
		this.index = 0;		
	}
	
	@Override
	public boolean hasNext() {
		return (index < coordinates.length); 
	}

	@Override
	public Coordinate next() {
		
		if (index < coordinates.length) { 
			return coordinates[index++];
		}

		throw new NoSuchElementException("Iterator ran out of elements!");
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Remove not supported!");
	} 		
}