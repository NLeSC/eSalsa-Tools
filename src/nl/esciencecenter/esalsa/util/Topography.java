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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Topography represents an ocean bottom topography definition used as in POP. 
 * 
 * A bottom topography contains the index (> 0) of the deepest ocean level for each grid point. A value of 0 at any position 
 * indicates a land point. 
 * 
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * @version 1.0
 * @since 1.0
 * 
 */
public class Topography {

	/** A logger used for debugging */
	private static final Logger logger = LoggerFactory.getLogger(Topography.class);
	
	/** The topography data as read from the input file */
	private final int [][] topography;
	
	/** The width of the topography */
	public final int width;
	
	/** The height of the topography */
	public final int height;
	
	/** The maximum value found in the topography */
	public final int max;
	
	/** The minimum value found in the topopgrapy */
	public final int min;
	
	/** 
	 * Create a new topography by reading the data from the input file.  
	 * 
	 * @param width the width of the topography to create.
	 * @param height the height of the topography to create.
	 * @param inputfile the input file from which to read the topography data.
	 * @throws Exception if the topography could not be created. 
	 */
	public Topography(int width, int height, String inputfile) throws Exception {
	
		int tmpMax = Integer.MIN_VALUE;
		int tmpMin = Integer.MAX_VALUE;
		
		this.width = width;
		this.height = height;
		topography = new int[width][height];

		int work = 0;
		long sum = 0;
		
		DataInputStream in = null;
		
		try { 
			in = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(inputfile))));

			for (int y=0;y<height;y++) { 
				for (int x=0;x<width;x++) {
					int tmp = in.readInt();

					//topography[x][height-y-1] = tmp;
					topography[x][y] = tmp;

					if (tmp > tmpMax) { 
						tmpMax = tmp;
					}

					if (tmp < tmpMin) { 
						tmpMin = tmp;
					}

					if (tmp > 0) { 
						work++;
					}

					sum += tmp;
				}
			}

			in.close();
		} catch (Exception e) { 
			throw new Exception("Failed to read topography from file " + inputfile, e);
		} finally { 
			try { 
				in.close();
			} catch (Exception e) {
				// ignored
			}
		}
			
		max = tmpMax;
		min = tmpMin;		
		
		if (logger.isDebugEnabled()) { 
			logger.debug("Topography contains " + work + " nonzero fields (sum = " + sum + ")");
		}
	}

	/** 
	 * Create a new topography from existing data.  
	 * 
	 * @param data a square int matrix containing the data to store in the topography.
	 * @throws Exception if the topography could not be created. 
	 */
	public Topography(int [][] data) throws Exception {
	
		int tmpMax = Integer.MIN_VALUE;
		int tmpMin = Integer.MAX_VALUE;
		
		this.width = data.length;
		this.height = data[0].length;
		
		int work = 0;
		long sum = 0;
		
		topography = data.clone(); 
		
		for (int y=0;y<height;y++) { 
			for (int x=0;x<width;x++) {
					
				int tmp = topography[x][y];

				if (tmp > tmpMax) { 
					tmpMax = tmp;
				}

				if (tmp < tmpMin) { 
					tmpMin = tmp;
				}

				if (tmp > 0) { 
					work++;
				}

				sum += tmp;
			}
		}
			
		max = tmpMax;
		min = tmpMin;		
		
		if (logger.isDebugEnabled()) { 
			logger.debug("Topography contains " + work + " nonzero fields (sum = " + sum + ")");
		}
	}
	
	/** 
	 * Create a new topography by down scaling an existing one.
	 * 
	 * The down scaling will be performed block wise. The original topography will be divided in blocks of size 
	 * (blockWidth x blockHeight). Each block will become a single point in the new topography. The value of this point will be the 
	 * sum of all values in the block in the original topography.       
	 *  
	 * @param orig the original topography. 
	 * @param blockWidth the block width to use for down scaling.  
	 * @param blockHeight the block height to use for down scaling.
	 * @throws Exception if the topography could not be scaled down. 
	 */
	public Topography(Topography orig, int blockWidth, int blockHeight) throws Exception {
		
		int tmpMax = Integer.MIN_VALUE;
		int tmpMin = Integer.MAX_VALUE;
		
		if (orig.width % blockWidth != 0) { 
			throw new Exception("Illegal blockWidth");
		}
		
		if (orig.height % blockHeight != 0) { 
			throw new Exception("Illegal blockHeight");
		}
		
		this.width = orig.width / blockWidth;
		this.height = orig.height / blockHeight;
		topography = new int[width][height];
		
		int work = 0;
		long sum = 0;
		
		for (int y=0;y<height;y++) { 
			for (int x=0;x<width;x++) {

				int tmp = orig.getRectangleSum(x*blockWidth, y*blockHeight, blockWidth, blockHeight);
				 
				topography[x][y] = tmp;
						
				if (tmp > tmpMax) { 
					tmpMax = tmp;
				}
				
				if (tmp < tmpMin) { 
					tmpMin = tmp;
				}
				
				if (tmp > 0) { 
					work++;
				}
				
				sum += tmp;
			}
		}
		
		max = tmpMax;
		min = tmpMin;		
		
		if (logger.isDebugEnabled()) { 
			logger.debug("Topography contains " + work + " nonzero fields (sum = " + sum + ")");
		}
	}

	/** 
	 * Returns the maximum value found in a rectangular area of the topography.  
	 * 
	 * @param x the x position of the rectangle. 
	 * @param y the y position of the rectangle.
	 * @param w the width of the rectangle.
	 * @param h the height of the rectangle.
	 * @return the maximum value found in the specified rectangular area.
	 */
	public int getRectangleMax(int x, int y, int w, int h) {
		
		int max = Integer.MIN_VALUE;
		
		for (int i=x;i<x+w && i<width;i++) {
			for (int j=y;j<y+h && j<height;j++) {
				int tmp = topography[i][j];
				if (tmp > max) { 
					max = tmp;
				}
			}
		}
		
		return max;
	}
	
	/** 
	 * Returns the average value found in a rectangular area of the topography.  
	 * 
	 * @param x the x position of the rectangle. 
	 * @param y the y position of the rectangle.
	 * @param w the width of the rectangle.
	 * @param h the height of the rectangle.
	 * @return the average value found in the specified rectangular area.
	 */
	public int getRectangleAvg(int x, int y, int w, int h) {
	
		int sum = 0;
		int count = 0;
		
		for (int i=x;i<x+w && i<width;i++) {
			for (int j=y;j<y+h && j<height;j++) {
				sum += topography[i][j];
				count++;
			}
		}
		
		if (count > 0) { 
			return sum/count;
		}
		
		return 0;
	}
	
	/** 
	 * Returns the sum of all values found in a rectangular area of the topography.  
	 * 
	 * @param x the x position of the rectangle. 
	 * @param y the y position of the rectangle.
	 * @param w the width of the rectangle.
	 * @param h the height of the rectangle.
	 * @return the sum of all value in the specified rectangular area.
	 */
	public int getRectangleSum(int x, int y, int w, int h) {
		
		int sum = 0;
		
		for (int i=x;i<x+w && i<width;i++) {
			for (int j=y;j<y+h && j<height;j++) {
				sum += topography[i][j];
			}
		}
		
		return sum;
	}
	
	/** 
	 * Returns the amount of work (that is, non-0 values) found in a rectangular area of the topography.  
	 * 
	 * @param x the x position of the rectangle. 
	 * @param y the y position of the rectangle.
	 * @param w the width of the rectangle.
	 * @param h the height of the rectangle.
	 * @return the amount of work in the specified rectangular area.
	 */
	public int getRectangleWork(int x, int y, int w, int h) {
		
		int work = 0;
		
		for (int i=x;i<x+w && i<width;i++) {
			for (int j=y;j<y+h && j<height;j++) {
				if (topography[i][j] > 0) {
					work++;
				}
			}
		}
		
		return work;
	}

	/**
	 * Retrieves the value of a specific location of the topography. 
	 * 
	 * @param x the x coordinate of the value to retrieve.
	 * @param y the y coordinate of the value to retrieve.
	 * @return the value found at the specified location in the topography.
	 */
	public int get(int x, int y) {
		return topography[x][y];
	}
}
