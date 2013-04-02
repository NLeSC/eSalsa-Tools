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

import nl.esciencecenter.esalsa.util.Distribution;

/**
 * DistributionToText is an application that converts a POP distribution from the binary format used in POP itself to an 
 * ASCI text representation.   
 *  
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * @version 1.0
 * @since 1.0
 */
public class DistributionToText {

	/** 
	 * Main entry point into application. 
	 * 
	 * @param args the command line arguments provided by the user. 
	 */
	public static void main(String [] args) { 
		
		if (args.length != 1) { 			
			System.err.println("Usage: DistributionToText [distribution file]");
			System.exit(1);
		}
		
		Distribution d = null;
		
		try { 
			d = new Distribution(args[0]);
		} catch (Exception e) { 
			Utils.fatal("Failed to open ditribution " + args[0] + "\n", e);
		} 

		System.out.println(d.topographyWidth);
		System.out.println(d.topographyHeight);

		System.out.println(d.blockWidth);
		System.out.println(d.blockHeight);

		System.out.println(d.clusters);
		System.out.println(d.nodesPerCluster);
		System.out.println(d.coresPerNode);

		System.out.println(d.minBlocksPerCore);
		System.out.println(d.maxBlocksPerCore);

		System.out.println(d.totalBlocks);

		for (int i=0;i<d.totalBlocks;i++) { 
			System.out.println(d.getOwner(i));
		}
	}
}
