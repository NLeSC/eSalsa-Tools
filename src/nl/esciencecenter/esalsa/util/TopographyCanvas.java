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

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.imageio.ImageIO;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TopographyCanvas is an extension of {@link java.awt.Canvas} is capable of showing a {@link Topography}. 
 * <p>
 * In addition, one or more named layers can be created on top of the topography that can be used to draw lines, fill blocks, or
 * display text on top of the topography.   
 * 
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * @version 1.0
 * @since 1.0
 * 
 */
public class TopographyCanvas extends Canvas {
	
	/** Generated */
	private static final long serialVersionUID = -8649482969047330657L;

	/** A logger used for debugging */
	private static final Logger logger = LoggerFactory.getLogger(TopographyCanvas.class);

	/** A BufferedImage containing a image showing the topography. */ 
	private final BufferedImage topo;
	
	/** A store for the various layers. */ 
	private final LinkedHashMap<String, BufferedImage> layers = new LinkedHashMap<String, BufferedImage>();
	
	/** The topography we are displaying */
	public final Topography topography;

	/** The grid overlay on the topography. */ 
	public final Grid grid;
	
	/**
	 * Create a TopographyCanvas for a specified topography and grid. 
	 * 
	 * @param topography the Topography to use. 
	 * @param grid the grid to use. 
	 * @see Topography
	 * @see Grid
	 */
	public TopographyCanvas(Topography topography, Grid grid) { 
		
		this.topography = topography;
		this.grid = grid;
		
		int range = topography.max - topography.min + 1;
		
		if (logger.isDebugEnabled()) { 
			logger.debug("Color range: " + topography.min + " ... " + topography.max + " (" + range + ")");
		}
			
		int [] colors = new int[range];
		colors[0] = 0xFFFFFFFF;
		
		for (int i=1;i<range;i++) {
			int tmp = 255-(int)((255.0/topography.max)*i);			
			colors[i] = (0xFF000000 | tmp);
		}
	
		// Create the topography image
		topo = new BufferedImage(topography.width, topography.height, BufferedImage.TYPE_INT_ARGB);
		
		for (int x=0;x<topography.width;x++) { 
			for (int y=0;y<topography.height;y++) { 
				// Flip image, as the topography is stored upside down!
				topo.setRGB(x, topography.height-y-1, colors[topography.get(x, y)]);
			}				
		}
	}

	/** 
	 * Fill a rectangle in a specified layer with a color. 
	 * 
	 * The rectangle is specified as the smallest rectangular area between to coordinates. 
	 * 
	 * @param layer the layer at which to draw.  
	 * @param start the first coordinate that defines the rectangle.
	 * @param end the second coordinate that defines the rectangle.
	 * @param color the color to fill the rectangle with. 
	 * @throws Exception if the specified layer does not exist. 
	 */
	public void fill(String layer, Coordinate start, Coordinate end, Color color) throws Exception {
		
		BufferedImage tmp = getLayer(layer);
		
		if (start.x == end.x || start.y == end.y) {
			// Nothing to fill!
			return;
		}
		
		int lowX = start.x < end.x ? start.x : end.x;
		int lowY = start.y < end.y ? start.y : end.y;
		
		int highX = start.x > end.x ? start.x : end.x;
		int highY = start.y > end.y ? start.y : end.y;
		
		// We should now fill the area between (lowX, lowY) (inclusive) and (highX, highY) (exclusive)
		Graphics2D g = tmp.createGraphics();
		g.setColor(color);
		g.fillRect(lowX, lowY, (highX-lowX), (highY-lowY));
	}
	
	/**
	 * Fill a block in a specified layer with a color. 
	 * 
	 * @param layer the layer at which to draw.  
	 * @param x the x coordinate of the block.
	 * @param y the y coordinate of the block.
	 * @param color the color to fill the block with. 
	 * @throws Exception if the specified layer does not exist.
	 */ 
	public void fillBlock(String layer, int x, int y, Color color) throws Exception {		
		int ny = grid.height-y-1;		
		fill(layer, new Coordinate(x*grid.blockWidth, ny*grid.blockHeight), 
				new Coordinate((x+1)*grid.blockWidth, (ny+1)*grid.blockHeight), color);
	}
		
	/** 
	 * Draw a line with a certain color and width in a specified layer. 
	 * 
	 * @param layer the layer at which to draw.  
	 * @param line the line to draw. 
	 * @param color the color of the line. 
	 * @param lineWidth the width of the line. 
	 * @throws Exception if the layer does not exist. 
	 */
	public void draw(String layer, Line line, Color color, float lineWidth) throws Exception {
		
		// Calculate the scale of this line. 
		int sx = topography.width / grid.width;
		int sy = topography.height / grid.height;
		
		Graphics2D g = (Graphics2D) getLayer(layer).getGraphics();
		
		g.setColor(color);
		g.setStroke(new BasicStroke(lineWidth));		
		g.drawLine(line.start.x*sx, (topography.height-line.start.y*sy), line.end.x*sx, (topography.height-line.end.y*sy));	
	}

	/** 
	 * Draw a point with a certain color in a specified layer. 
	 * 
	 * @param layer the layer at which to draw.  
	 * @param x x coordinate of point to draw.
	 * @param y y coordinate of point to draw. 
	 * @param color the color of the point. 
	 * @throws Exception if the layer does not exist. 
	 */
	public void draw(String layer, int x, int y, Color color) throws Exception {
		getLayer(layer).setRGB(x, topography.height-y-1, color.getRGB());
	}
	
	/**
	 * Retrieves the BufferedImage associated with a layer.
	 *  
	 * @param layer the name of the layer.  
	 * @return the BufferedImage associated with a layer.
	 * @throws Exception if the layer does not exist. 
	 * @see BufferedImage
	 */
	private BufferedImage getLayer(String layer) throws Exception { 

		BufferedImage tmp = layers.get(layer);
		
		if (tmp == null) {
			throw new Exception("No such layer: " + layer);
		}
		
		return tmp;		
	}

	/**
	 * Add a layer.
	 * 
	 * @param layer the name of the layer to add. 
	 * @throws Exception if a layer with the given name already exists. 
	 */
	public void addLayer(String layer) throws Exception { 
		
		if (layers.containsKey(layer)) {
			throw new Exception("Layer " + layer + " + already exists!");
		}
		
		BufferedImage tmp = new BufferedImage(topography.width, topography.height, BufferedImage.TYPE_INT_ARGB);

		for (int x=0;x<topography.width;x++) { 
			for (int y=0;y<topography.height;y++) { 
				tmp.setRGB(x, topography.height-y-1, 0x00000000);
			}				
		}

		layers.put(layer, tmp);
	}
	
	/**
	 * Delete a layer. 
	 *   
	 * @param layer the name of the layer to delete.
	 * @return if the layer existed. 
	 */
	public boolean deleteLayer(String layer) {
		return (layers.remove(layer) != null);
	}
	
	/** 
	 * Clears a layer by setting all its pixels to transparent. 
	 * 
	 * @param layer the name of the layer to clear.
	 * @throws Exception if the layer does not exist. 
	 */	
	public void clearLayer(String layer) throws Exception {
		
		BufferedImage tmp = getLayer(layer);
		
		for (int x=0;x<topography.width;x++) { 
			for (int y=0;y<topography.height;y++) { 
				tmp.setRGB(x, topography.height-y-1, 0x00000000);
			}				
		}		
	}

	/** 
	 * Returns the names of the currently defined layers. 
	 * 
	 * @return an array containing the names of the currently defined layers.
	 */
	public String[] listLayers() {
		Set<String> set = layers.keySet();
		return set.toArray(new String [set.size()]);
	}
	
	/** 
	 * Clear all layers. 
	 */
	public void clearAll() {
		
		for (String name : layers.keySet()) {
			
			try { 
				clearLayer(name);
			} catch (Exception e) {
				throw new Error("Internal error while clearing layer " + name);
			}
		}
	}		
	
	@Override
	public void paint(Graphics graphics) {
		update(graphics);
	}
	
	private BufferedImage getCurrentImage(int w, int h) { 
		
		// Create buffered image of topography
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		
		g2.drawImage(topo, 0, 0, w, h, 0, 0, topography.width, topography.height, null);
		
		for (BufferedImage tmp : layers.values()) {
			g2.drawImage(tmp, 0, 0, w, h, 0, 0, topography.width, topography.height, null);					
		}
		
		return img;
	}
	
	@Override
	public void update(Graphics graphics) { 
		
		int w = getWidth();
		int h = getHeight();

		BufferedImage img = getCurrentImage(w, h);
		
/*		
		// Create buffered image of topography
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		
		g2.drawImage(topo, 0, 0, w, h, 0, 0, topography.width, topography.height, null);
		
		/*		
		LinkedList<BufferedImage> reverse = new LinkedList<BufferedImage>();
		
		for (BufferedImage tmp : layers.values()) {
			reverse.addFirst(tmp);
		}
		
		for (BufferedImage tmp : reverse) {
			g2.drawImage(tmp, 0, 0, w, h, 0, 0, topography.width, topography.height, null);					
		}
		
		for (BufferedImage tmp : layers.values()) {
			g2.drawImage(tmp, 0, 0, w, h, 0, 0, topography.width, topography.height, null);					
		}
*/		
		Graphics2D g2 = (Graphics2D) graphics;
		g2.drawImage(img, 0, 0, w, h, 0, 0, w, h, null);
	}

	/** 
	 * Save the current image as a png file.  
	 *  
	 * @param file the filename of the file to save. 
	 * @throws IOException if the file could not be saved. 
	 */
	public void save(String file) throws IOException { 
		
//		int w = topography.width;
		//int h = topography.height;
		
		// Create buffered image of topography
		
		// BufferedImage img = getCurrentImage(topography.width, topography.height);
		
		/*
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		
		g2.drawImage(topo, 0, 0, w, h, 0, 0, topography.width, topography.height, null);
	*/
		/*
		LinkedList<BufferedImage> reverse = new LinkedList<BufferedImage>();
		
		for (BufferedImage tmp : layers.values()) {
			reverse.addFirst(tmp);
		}
		
		BufferedImage bi = reverse.removeLast();
		reverse.addFirst(bi);
		
		for (BufferedImage tmp : reverse) {
			g2.drawImage(tmp, 0, 0, w, h, 0, 0, topography.width, topography.height, null);					
		}
		*/
		
		ImageIO.write(getCurrentImage(topography.width, topography.height), "png", new File(file));
		
		System.out.println("Done writing " + file);
	}
}
