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

package nl.esciencecenter.esalsa.tools;

import nl.esciencecenter.esalsa.loadbalancer.LoadBalancer;
import nl.esciencecenter.esalsa.util.Distribution;
import nl.esciencecenter.esalsa.util.Grid;
import nl.esciencecenter.esalsa.util.Layers;
import nl.esciencecenter.esalsa.util.Neighbours;
import nl.esciencecenter.esalsa.util.Topography;

/**
 * LoadBalancing is an application that generates a block distribution for POP. 
 * 
 * The block distribution is generated based on an ocean bottom topography, the desired block size, and the 
 * desired number of clusters, nodes per cluster, and cores per node, and a desired split heuristic.
 * 
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * @version 1.0
 * @since 1.0
 *
 */
public class LoadBalancing {

	/** The topographyWidth as set by user */
	private static int topographyWidth = -1;
	
	/** The topographyHeight as set by user */
	private static int topographyHeight = -1;
	
	/** The blockWidth as set by user */
	private static int blockWidth = -1;
	
	/** The blockHeight as set by user */
	private static int blockHeight = -1;

	/** The number of clusters as set by user */
	private static int clusters = 1;
	
	/** The number of nodes per cluster as set by user */
	private static int nodesPerCluster = -1;
	
	/** The number of cores per node as set by user */
	private static int coresPerNode = -1;

	/** Should we show the GUI ? */
	private static boolean showGUI = false;
	
	/** Statistics to print */
	private static String statistics = null;
	
	/** File name of topography input file to load */ 
	private static String topographyFile = null; 
	
	/** File name of output distribution file to write */
	private static String outputDistribution = null; 
	
	/** File name of output image file to write */
	private static String outputImage = null; 

	/** Name of the split methods to use */
	private static String splitMethod = "roughlyrect";
	
	/**
	 * Print the usage on the console. 
	 */
	private static void usage() { 
		System.out.println(
				"Usage: LoadBalancing topography_file [OPTION]*\n" + 
				"\n" + 
				"Reads a POP topography file and determines a block distribution suitable for a number nodes each" +
				" containing a number of cores (and optionally divided into a number of clusters).\n" +
				"\n" + 
				"Mandatory arguments:\n" + 
				"   topography_file            the topography file that contains the index of the deepest ocean levels.\n" + 
				"   --grid WIDTH HEIGHT        dimensions of the topography file grid (WIDTHxHEIGHT).\n" + 
				"   --blocksize WIDTH HEIGHT   dimensions of the blocks to use (WIDTHxHEIGHT).\n" + 
				"   --nodes NODES              number of nodes for which the distribution must be calculated.\n" + 
				"   --cores CORES              number of cores in each node.\n" +
				"\n" + 
				"Optional arguments:\n" + 
				"   --clusters CLUSTERS        number of clusters to calculate ditribution for (default is 1).\n" +
				"   --output FILE              store the resulting distribution in FILE.\n" + 
				"   --image FILE               store an image of the resulting distribution in FILE.\n" + 
				"   --statistics LAYER         print statistics on the resulting distribution on layer LAYER. Valid" +
				" value for LAYER are CORES, NODES, CLUSTERS, ALL.\n" +
				"   --method METHOD            method used to distribute the blocks. Valid values for METHOD are" + 
				" SIMPLE, ROUGHLYRECT, and SEARCH. Default is ROUGHLYRECT.\n" + 				
				"   --showgui                  show a graphical interface that allows the user to explore the distribution.\n" + 		
				"   --help                     show this help.");
		
			System.exit(0);
	}
	
	/** 
	 * Create  a LoadBalancer for the given topography and core configuration, run it, and save the desired output.   
	 */	
	private static void run() { 
			
		try { 
			Topography topography = new Topography(topographyWidth, topographyHeight, topographyFile);
			Grid grid = new Grid(topography, blockWidth, blockHeight);
			
			Neighbours neighbours = new Neighbours(grid, blockWidth, blockHeight, Neighbours.CYCLIC, Neighbours.TRIPOLE);
			
			Layers layers = new Layers();
			
			LoadBalancer lb = new LoadBalancer(layers, neighbours, topography, grid, blockWidth, blockHeight, 
					clusters, nodesPerCluster, coresPerNode, splitMethod);
			
			Distribution distribution = lb.split();
			
			if (showGUI || outputImage != null) {

				DistributionViewer sv = new DistributionViewer(distribution, topography, grid, neighbours, showGUI);					
				sv.drawBlocks();
					
				if (clusters > 1) { 
					sv.drawClusters();
				}
	
				if (nodesPerCluster > 1) { 
					sv.drawNodes();
				}
	
				if (coresPerNode > 1) { 
					sv.drawCores();
				}
				
				if (outputImage != null) { 
					try { 
						sv.save(outputImage);		
					} catch (Exception e) {
						System.err.println("Failed to save distribution to file " + outputDistribution + ": " 
								+ e.getLocalizedMessage());
					}
				}
			}	

			if (statistics != null) {
				try { 
					lb.printStatistics(statistics);
				} catch (Exception e) {
					System.err.println("Failed to print statistics " + statistics + ": " + e.getLocalizedMessage());
				}
			}

			if (outputDistribution != null) {
				try { 
					distribution.write(outputDistribution);
				} catch (Exception e) {
					System.err.println("Failed to save distribution to file " + outputDistribution + ": " + e);
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			Utils.fatal(e);
		}
	}
	
	/** 
	 * Main entry point into application. 
	 * 
	 * @param args the command line arguments provided by the user. 
	 */
	public static void main(String [] args) { 

		boolean topographySet = false;
		boolean blockSet = false;
		boolean nodesSet = false;
		boolean coresSet = false;
		
		if (args.length < 1) { 
			usage();
		}

		topographyFile = args[0];
		
		int index = 1;
		
		while (index < args.length) { 

			if (args[index].equals("--grid")) { 
				Utils.checkOptions("--grid", 2, index, args.length);
				topographyWidth = Utils.parseInt("--grid", args[index+1], 1);
				topographyHeight = Utils.parseInt("--grid", args[index+2], 1);
				index += 3;
				topographySet = true;
				
			} else if (args[index].equals("--blocksize")) { 
				Utils.checkOptions("--blocksize", 2, index, args.length);
				blockWidth = Utils.parseInt("--blocksize", args[index+1], 1);
				blockHeight = Utils.parseInt("--blocksize", args[index+2], 1);
				index += 3;
				blockSet = true;
				 
			} else if (args[index].equals("--clusters")) { 
				Utils.checkOptions("--clusters", 1, index, args.length);
				clusters = Utils.parseInt("--clusters", args[index+1], 1);
				index += 2;

			} else if (args[index].equals("--nodes")) { 
				Utils.checkOptions("--nodes", 1, index, args.length);
				nodesPerCluster = Utils.parseInt("--nodes", args[index+1], 1);
				index += 2;
				nodesSet = true;
				
			} else if (args[index].equals("--cores")) { 
				Utils.checkOptions("--cores", 1, index, args.length);
				coresPerNode = Utils.parseInt("--cores", args[index+1], 1);
				index += 2;
				coresSet = true;
				
			} else if (args[index].equals("--output")) { 
				Utils.checkOptions("--output", 1, index, args.length);
				outputDistribution = args[index+1];
				index += 2;

			} else if (args[index].equals("--image")) { 
				Utils.checkOptions("--image", 1, index, args.length);
				outputImage = args[index+1];
				index += 2;

			} else if (args[index].equals("--showGUI")) { 
				showGUI = true;
				index++;
			
			} else if (args[index].equals("--help")) { 
				usage();
				
			} else if (args[index].equals("--statistics")) { 
				Utils.checkOptions("--statistics", 1, index, args.length);
				statistics = args[index+1];
				index += 2;
				
			} else if (args[index].equals("--method")) { 
				Utils.checkOptions("--method", 1, index, args.length);
				splitMethod = args[index+1];
				index += 2;
				
			} else { 
				Utils.fatal("Unknown option: " + args[index]);
			}		
		}

		if (!topographySet) { 
			Utils.fatal("Please specify topography dimension using \"--grid WIDTH HEIGHT\"");
		}
		
		if (!blockSet) { 
			Utils.fatal("Please specify block dimension using \"--blocksize WIDTH HEIGHT\"");
		}
		
		if (!nodesSet) { 
			Utils.fatal("Please specify node count using \"--nodes NODES\"");
		}
		
		if (!coresSet) { 
			Utils.fatal("Please specify core per node using \"--cores CORES\"");
		}
		
		if (blockWidth > topographyWidth) { 
			Utils.fatal("Block width cannot be larger that grid width");
		}
		
		if (topographyWidth % blockWidth != 0) { 
			Utils.fatal("Block width must divide grid width equally!");
		}
		
		if (blockHeight > topographyHeight) { 
			Utils.fatal("Block height cannot be larger that grid height");
		}
		
		if (topographyHeight % blockHeight != 0) { 
			Utils.fatal("Block height must divide grid height equally!");
		}
		
		if (!showGUI && outputDistribution == null && outputImage == null && statistics == null) { 
			System.out.println("WARNING: This application will not produce any output, since none of the outputs is selected!");
		}
		
		run();
	}
}