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

/**
 * Block represents a block in a POP work distribution. 
 * 
 * It consists of an immutable <code>Coordinate</code>, and an <code>int</code> mark that can be used to label the block with an 
 * arbitrary integer value.   
 * 
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * @version 1.0
 * @since 1.0
 * @see Coordinate
 */
public class Block {

	/** The Coordinate of the block. */
	public final Coordinate coordinate;
	
	/** Mark use to label the block */
	private int mark = 0;

	/**
	 * Constructor to create a new Block. 
	 * 
	 * @param coordinate coordinate of the block.
	 */
	public Block(Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + coordinate.x;
		result = prime * result + coordinate.y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
	
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		Block other = (Block) obj;
		return coordinate.equals(other.coordinate);
	}

	/** 
	 * Mark the block with the given value.
	 *  
	 * @param value the value to mark the block with. 
	 */
	public void setMark(int value) {
		mark = value;
	}

	/**
	 * Add the given value to the current mark. 
	 * 
	 * @param value value to add to the current mark.  
	 */	
	public void addToMark(int value) {
		mark += value;
	}

	/** 
	 * Retrieve the value of the current mark. 
	 * 
	 * @return the current value of the mark.
	 */
	public int getMark() {
		return mark;
	}
}
