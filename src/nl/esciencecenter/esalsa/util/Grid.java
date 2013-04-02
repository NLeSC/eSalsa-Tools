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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Grid represents an rectangular grid of Blocks.
 *    
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * @version 1.0
 * @since 1.0
 * @see Block
 *
 */
public class Grid implements Iterable<Block> {
	
	/** A Logger used for debugging. */
	private static final Logger logger = LoggerFactory.getLogger(Grid.class);
	
	/** The width of the grid. */
	public final int width;
	
	/** The height of the grid. */
	public final int height;

	/** The width of a block in topography points. */
	public final int blockWidth;
	
	/** The height of a block in topography points. */
	public final int blockHeight;
	
	/** An array of length width*height to store blocks in this grid. */ 
	private final Block [] blocks;

	/** The number of active blocks in this grid. */ 
	private int count = 0; 
	
	/** 
	 * Create an empty grid of size width x height.  
	 * 
	 * @param width the width of the grid to create.  
	 * @param height the height of the grid to create.
	 */
	public Grid(int width, int height, int blockWidth, int blockHeight) { 
	
		this.width = width;
		this.height = height;
		this.blockWidth = blockWidth;
		this.blockHeight = blockHeight;
		
		blocks = new Block[width*height];
		
		if (logger.isDebugEnabled()) {   
			logger.debug("Created Grid " + width + "x" + height);
		}		
	}
	
	/** 
	 * Create grid by subdividing a Topography into blocks of size blockWidth x blockHeight points.
	 *  
	 * Only Blocks containing at least one ocean point will be stored. As a result, after creation, 
	 * some locations in the grid may not contain a block.      
	 * 
	 * @param topo the Topography to divide. 
	 * @param blockWidth the width of a block in topography points.  
	 * @param blockHeight the height of a block in topography points.
	 * @throws Exception if the block size does not divide the topography equally.
	 * @see Topography
	 * @see Block 
	 */	
	public Grid(Topography topo, int blockWidth, int blockHeight) throws Exception {
		
		if (topo.width % blockWidth != 0) {
			throw new Exception("Cannot subdivide topography: block width " + blockWidth + 
					" is not a divider of width " + topo.width);
		}
		
		if (topo.height % blockHeight != 0) {		
			throw new Exception("Cannot subdivide topography: block height " + blockHeight + 
					" is not a divider of height " + topo.height);
		}

		this.width = topo.width / blockWidth;
		this.height = topo.height / blockHeight;

		this.blockWidth = blockWidth;
		this.blockHeight = blockHeight;
		
		if (logger.isDebugEnabled()) { 
			logger.debug("Creating new grid from topography " + width + "x" + height);
		}
		
		blocks = new Block[width*height];
		
		for (int y=0;y<height;y++) { 
			for (int x=0;x<width;x++) {
				
				int tmp = topo.getRectangleWork(x*blockWidth, y*blockHeight, blockWidth, blockHeight);
				
				if (tmp > 0) {
					put(new Block(new Coordinate(x, y)));					
				} 				
			}
		}

		if (logger.isDebugEnabled()) { 
			logger.debug("Created new grid from topography with " + getCount() + " active elements.");
		}
	}		
	
	/**
	 *  Checks if the given coordinate falls within this grid. 
	 * 
	 * @param x the x coordinate to check. 
	 * @param y the y coordinate to check.
	 * @return if the given coordinate falls within this grid.
	 */
	private boolean inRange(int x, int y) {
		return !(x < 0 || x >= width || y < 0 || y >= height);
	}
	
	/**
	 *  Checks if the given coordinate fall within this grid. 
	 * 
	 * @param c the Coordinate to check. 
	 * @return if the given coordinate falls within this grid.
	 */
	private boolean inRange(Coordinate c) {
		return !(c.x < 0 || c.x >= width || c.y < 0 || c.y >= height);
	}
	
	/**
	 * Returns the number of non-empty blocks in this grid. 
	 *  
	 * @return the number of non-empty blocks in this grid.
	 * @see Block 
	 */
	public int getCount() { 
		return count;
	}
	
	/**
	 * Stores a Block in the grid. 
	 * 
	 * @param b the Block to store.
	 * @see Block
	 */
	public void put(Block b) { 
		
		if (!inRange(b.coordinate)) { 
			throw new IllegalArgumentException("Coordinate out of bounds! " + b.coordinate);
		}
		
		if (blocks[b.coordinate.y*width + b.coordinate.x] == null) { 
			count++;
		}
		
		blocks[b.coordinate.y*width + b.coordinate.x] = b;
	}
	
	/** 
	 * Stores all Blocks in a Collection in this grid.
	 * 
	 * @param elts the blocks to store.
	 * @see Block
	 * @see Collection
	 */	
	public void putAll(Collection<Block> elts) {
		for (Block b : elts) { 
			put(b);
		}
	}
	
	/** 
	 * Retrieves all Blocks in this grid and store them in a Collection. 
	 * 
	 * @param out the collection to which all blocks in this grid will be added.
 	 * @see Block
	 * @see Collection
	 */	
	public void getAll(Collection<Block> out) {
		for (Block b : blocks) {
			if (b != null) { 
				out.add(b);
			}
		}
	}

	/** 
	 * Retrieves a Block at a given location.
	 * 
	 * @param c the location of the block to retrieve.  
	 * @return the Block at the given location, or null if the location is empty.
	 * @see Block 
	 */
	public Block get(Coordinate c) {
		return get(c.x, c.y);
	}

	/**
	 * Retrieves a Block at a given location.
	 * 
	 * @param x the x coordinate of the location to retrieve.
	 * @param y the y coordinate of the location to retrieve.
	 * @return the Block at the given location, or null if the location is empty.
	 * @see Block
	 */
	public Block get(int x, int y) { 
		
		if (!inRange(x, y)) { 
			throw new IllegalArgumentException("Coordiate out of bounds! " + x + "x" + y);
		}
		
		return blocks[y*width + x];
	}
	
	@Override
	public Iterator<Block> iterator() {
		return new BlockIterator(blocks);
	}
	
	/** 
	 * Retrieve all Blocks in a given rectangular subsection of this grid.
	 * 
	 * The subsection is an area in a coordinate space that is enclosed by the locations (x,y) (inclusive) and 
	 * (x+width,y+height) (exclusive) in the coordinate space.  
	 * 
	 * @param x the x coordinate of the subsection.  
	 * @param y the y coordinate of the subsection.	  
	 * @param width the width of the subsection. 
	 * @param height the height of the subsection.
	 * @return All blocks enclosed by the specified subsection.
	 * @see Block 
	 */
	public Block [] getRectangle(int x, int y, int width, int height) { 
		
		ArrayList<Block> result = new ArrayList<Block>();
		
		for (int j=y;j<y+height;j++) { 
			for (int i=x;i<x+width;i++) {
				if (inRange(x, y)) {
					
					Block b = blocks[j*this.width + i];	
					
					if (b != null) {
						result.add(b);
					}
				}				
			}
		}
		
		return result.toArray(new Block[result.size()]);		
	}
}
