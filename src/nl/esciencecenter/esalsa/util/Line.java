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
 * Line represents a line between two coordinates. 
 * 
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * @version 1.0
 * @since 1.0
 * @see Coordinate 
 *
 */
public class Line {
	
	/** The start of the line. */
	public final Coordinate start;
	
	/** The end of the line. */
	public final Coordinate end;
	
	/** 
	 * Creates a new Line between the two specified coordinates.
	 * 
	 * @param start the start of the line.
	 * @param end the end of the line.
	 */	
	public Line(Coordinate start, Coordinate end) {
		
		if (start == null || end == null) { 
			throw new IllegalArgumentException("Coordinates in a line cannot be null!");
		}
		
		this.start = start;
		this.end = end;
	}

	@Override
	public int hashCode() {
		return start.hashCode() + end.hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) { 
			return true;
		}
		
		if (obj == null || getClass() != obj.getClass()) { 
			return false;
		}
		
		Line other = (Line) obj;
		return (start.equals(other.start) && end.equals(other.end)) || (start.equals(other.end) && end.equals(other.start)); 
	}	
}
