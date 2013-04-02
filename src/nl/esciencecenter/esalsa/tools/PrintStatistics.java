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
import nl.esciencecenter.esalsa.util.Grid;
import nl.esciencecenter.esalsa.util.Neighbours;
import nl.esciencecenter.esalsa.util.Statistics;
import nl.esciencecenter.esalsa.util.Topography;

/**
 * PrintStatistics is an application that prints information about a given POP distribution.
 *  
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * @version 1.0
 * @since 1.0
 */
public class PrintStatistics {
	
	/**
	 * Main entry point into the application.
	 *  
	 * @param args the command line arguments.
	 */
	public static void main(String [] args) { 

		if (args.length < 3) { 			
			System.out.println("Usage: PrintStatistics topography_file distribution_file statistics_name\n" + 
					"\n" + 
					"Read a topography file and work distribution file and print statistics on the work distribution and " + 
					"communication per cluster, node or core.\n" + 
					"\n" + 
					"  topography_file    a topography file that contains the index of the deepest ocean level at " + 
					"each gridpoint.\n" + 
					"  distribution_file  a work distribution file.\n" + 
					"  statistics_name    name of the statistics to print. Valid values are CLUSTER, NODE, CORE, ALL.");
			
			System.exit(1);
		}
		
		try { 			
			Distribution d = new Distribution(args[1]);			
			Topography t = new Topography(d.topographyWidth, d.topographyHeight, args[0]);
			Grid g = new Grid(t, d.blockWidth, d.blockHeight);
			Neighbours n = new Neighbours(g, d.blockWidth, d.blockHeight, Neighbours.CYCLIC, Neighbours.TRIPOLE);
		
			Statistics s = new Statistics(d.toLayers(), n);
			s.printStatistics(args[2], System.out);
			
		} catch (Exception e) {
			Utils.fatal("Failed to print statistics!", e);
		}
	}
}
