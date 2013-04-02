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
 * Coordinate represents a location in (x,y) coordinate space, specified in integer precision.
 *
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * @version 1.0
 * @since 1.0
 * 
 */
public class Coordinate {

	/** The x coordinate. */
	public final int x;
	
	/** The y coordinate. */
	public final int y;
	
	/** 
	 * Create a new Coordinate representing the specified (x,y) location. 
	 * 
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 */
	public Coordinate(int x, int y) { 
		this.x = x;
		this.y = y;
	}

	/** 
	 * Create a new Coordinate by adding an offset to the current coordinate. 
	 * 
	 * @param dx the x offset to add.
	 * @param dy the y offset to add. 
	 * @return a new Coordinate containing the current location plus the offsets. 
	 */
	public Coordinate offset(int dx, int dy) { 
		return new Coordinate(x+dx, y+dy);
	}
	
	@Override
	public int hashCode() {
		return x + y * 31;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
			
		Coordinate other = (Coordinate) obj;
		return x == other.x && y == other.y;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}	
}
