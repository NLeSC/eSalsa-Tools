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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import nl.esciencecenter.esalsa.util.Distribution;

/**
 * TextToDistribution is an application that converts a POP distribution represented in ASCI text to the binary format used in 
 * POP itself.   
 *  
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * @version 1.0
 * @since 1.0
 */
public class TextToDistribution {

	/** 
	 * Main entry point into application. 
	 * 
	 * @param args the command line arguments provided by the user. 
	 */
	public static void main(String [] args) { 
		
		if (args.length != 2) { 			
			System.err.println("Usage: TextToDistribution [text-file] [distribution file]");
			System.exit(1);
		}
		
		BufferedReader r = null;
		
		try { 
			r = new BufferedReader(new FileReader(new File(args[0])));
		} catch (IOException e) {  
			Utils.fatal("Failed to open input file: " + args[0] + "\n", e);
		}
		
		Distribution d = null;
		
		try { 
			int topographyWidth = Integer.parseInt(r.readLine().trim());
			int topographyHeight = Integer.parseInt(r.readLine().trim());
			
			int blockWidth = Integer.parseInt(r.readLine().trim());
			int blockHeight = Integer.parseInt(r.readLine().trim());

			int clusters = Integer.parseInt(r.readLine().trim());
			int nodesPerCluster = Integer.parseInt(r.readLine().trim());
			int coresPerNode = Integer.parseInt(r.readLine().trim());
			
			int minBlocksPerCore = Integer.parseInt(r.readLine().trim());
			int maxBlocksPerCore = Integer.parseInt(r.readLine().trim());
			
			int totalBlocks = Integer.parseInt(r.readLine().trim());
			
			int [] distribution = new int[totalBlocks];
			
			for (int i=0;i<totalBlocks;i++) { 
				distribution[i] = Integer.parseInt(r.readLine().trim());
			}

			r.close();
			
			d = new Distribution(topographyWidth, topographyHeight, blockWidth, blockHeight, 
					clusters, nodesPerCluster, coresPerNode, minBlocksPerCore, maxBlocksPerCore, totalBlocks, distribution);

		} catch (IOException e) {
			Utils.fatal("Failed to read input file: " + args[0] + "\n", e);					
		}
		
		try { 
			d.write(args[1]);
		} catch (IOException e) {
			Utils.fatal("Failed to write output file: " + args[1] + "\n", e);					
		}
	}
}
