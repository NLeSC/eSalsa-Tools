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

import java.io.PrintStream;

import nl.esciencecenter.esalsa.util.Layer;
import nl.esciencecenter.esalsa.util.Layers;
import nl.esciencecenter.esalsa.util.Neighbours;
import nl.esciencecenter.esalsa.util.Set;

/**
 * Statistics is a utility class capable of printing statistics for a given block distribution.
 * 
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * @version 1.0
 * @since 1.0
 * @see Layers
 * @see Neighbours
 */
public class Statistics {

	private final Layers layers;
	private final Neighbours neighbours;
	
	/**
	 * Create a Statistics for a given set of layers.
	 * 
	 * @param layers the layer for which the statistics must be printed.
	 * @param neighbours the neighbour function to use.
	 */
	public Statistics(Layers layers, Neighbours neighbours) { 
		this.layers = layers;
		this.neighbours = neighbours;
	}
	
	/** 
	 * Print statistics on work distribution and communication in <code>layer</code> to console. 
	 *   
	 * @param layer the layer to print statistics for. 
	 */
	private void printStatistics(Layer layer, PrintStream output) { 
		
		if (layer == null) { 
			return;
		}

		output.println("Statistics for layer: " + layer.name);		
		output.println("  Sets: " + layer.size());
	
		for (int i=0;i<layer.size();i++) { 
			Set tmp = layer.get(i);			
			// output.println("   " + i + " (" + tmp.minX + "," + tmp.minY + ") - (" + tmp.maxX + "," + tmp.maxY + ") " + 
			output.println("   " + i + " " + tmp.size() + " " + tmp.getCommunication(neighbours));
		}
	}

	/** 
	 * Print statistics on work distribution and communication in <code>layer</code> to the provided print stream. 
	 *   
	 * @param layer the name of the layer to print statistics for, or <code>ALL</code> to print statistics on all layers.  
	 * @param output the stream to print the statistics to.  
	 */
	public void printStatistics(String layer, PrintStream output) throws Exception {
		
		if (layer.equalsIgnoreCase("ALL")) { 
			printStatistics(layers.get("CLUSTERS"), output);
			printStatistics(layers.get("NODES"), output);
			printStatistics(layers.get("CORES"), output);
		} else { 
			Layer l = layers.get(layer);

			if (l == null) { 
				throw new Exception("Layer " + layer + " not found!");
			}
			
			printStatistics(l, output);
		}
	}	
}
