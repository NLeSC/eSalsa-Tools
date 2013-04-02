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
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Distribution represents a POP block distribution. 
 * 
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * @version 1.0 
 * @since 1.0
 * 
 */
public class Distribution {

	/** The width of the topography for which this distribution was generated. */ 
	public final int topographyWidth;
	
	/** The height of the topography for which this distribution was generated. */
	public final int topographyHeight;
	
	/** The width of the blocks for which this distribution was generated. */
	public final int blockWidth;
	
	/** The height of the topography for which this distribution was generated. */
	public final int blockHeight;
	
	/** The number of clusters for which this distribution was generated. */
	public final int clusters;
	
	/** The number of nodes per cluster for which this distribution was generated. */
	public final int nodesPerCluster;
	
	/** The number of cores per node for which this distribution was generated. */
	public final int coresPerNode;
	
	/** The minimal number of blocks per core in this distribution */
	public final int minBlocksPerCore;
	
	/** The maximal number of blocks per core in this distribution */
	public final int maxBlocksPerCore;
	
	/** The total number of block in this distribution */
	public final int totalBlocks;

	/** The distribution itself. Position <code>i</code> contains the core on which the block should be placed   */  
	private final int [] distribution;
	
	/** 
	 * Create a new distribution.
	 * 
	 * @param topographyWidth the width of the topography used for the distribution.  
	 * @param topographyHeight the height of the topography used for the distribution.
	 * @param blockWidth the width of the blocks used for the distribution.
	 * @param blockHeight the height of the blocks use for the distribution.
	 * @param clusters the number of clusters used for the distribution.
	 * @param nodesPerCluster the number of nodes per clusters used for the distribution.
	 * @param coresPerNode the number of cores per node used for the distribution.
	 * @param minBlocksPerCore the minimal number of blocks per core in the distribution.
	 * @param maxBlocksPerCore the maximal number of blocks per core in the distribution.
	 * @param totalBlocks the total number of blocks in the distribution.
	 * @param distribution the distribution to store.
	 */
	public Distribution(int topographyWidth, int topographyHeight, 
			int blockWidth, int blockHeight, 
			int clusters, int nodesPerCluster, int coresPerNode, 
			int minBlocksPerCore, int maxBlocksPerCore,
			int totalBlocks, int[] distribution) {

		this.topographyWidth = topographyWidth;
		this.topographyHeight = topographyHeight;
		this.blockWidth = blockWidth;
		this.blockHeight = blockHeight;
		this.clusters = clusters;
		this.nodesPerCluster = nodesPerCluster;
		this.coresPerNode = coresPerNode;
		this.minBlocksPerCore = minBlocksPerCore;
		this.maxBlocksPerCore = maxBlocksPerCore;
		this.totalBlocks = totalBlocks;
		this.distribution = distribution;
	}

	/** 
	 * Create a new Distribution by reading it contents for a file. 
	 * 
	 * @param filename the file to read. 
	 * @throws IOException if the file could not be read.
	 * @throws IOException if the file could not be read.
	 */
	@SuppressWarnings("resource")
	public Distribution(String filename) throws Exception { 

		DataInputStream in = null;
		
		try { 
			in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));

			topographyWidth = in.readInt();
			topographyHeight = in.readInt();
			
			if (topographyWidth <= 0 || topographyHeight <= 0) { 
				throw new Exception("Illegal topography dimensions " + topographyWidth + "x" + topographyHeight);
			}
			
			blockWidth = in.readInt();
			blockHeight = in.readInt();
			
			if (blockWidth <= 0 || blockHeight <= 0) { 
				throw new Exception("Illegal block dimensions " + blockWidth + "x" + blockHeight);
			}

			if (topographyWidth % blockWidth != 0 || topographyHeight % blockHeight != 0) { 
				throw new Exception("Blocks do not perfectly fit topography (" + topographyWidth + "x" + topographyHeight + " " 
						+ blockWidth + "x" + blockHeight +")");
			}
			
			clusters = in.readInt();
			
			if (clusters <= 0) { 
				throw new Exception("Illegal cluster count " + clusters);
			}
			
			nodesPerCluster = in.readInt();
			
			if (nodesPerCluster <= 0) { 
				throw new Exception("Illegal nodesPerCluster count " + nodesPerCluster);
			}
			
			coresPerNode = in.readInt();
		
			if (coresPerNode <= 0) { 
				throw new Exception("Illegal coresPerNode count " + nodesPerCluster);
			}
			
			minBlocksPerCore = in.readInt();
			maxBlocksPerCore = in.readInt();
			
			if (minBlocksPerCore < 0) {  
				throw new Exception("Illegal minBlocksPerCore count " + minBlocksPerCore);
			}

			if (maxBlocksPerCore < 0) {  
				throw new Exception("Illegal maxBlocksPerCore count " + maxBlocksPerCore);
			}
			
			if (maxBlocksPerCore < minBlocksPerCore) {  
				throw new Exception("maxBlocksPerCore is smaller than minBlockPerCore " + maxBlocksPerCore + " < " 
						+ minBlocksPerCore);
			}
			
			totalBlocks = in.readInt();

			int expectedBlocks = (topographyWidth / blockWidth) * (topographyHeight / blockHeight);
			
			if (totalBlocks != expectedBlocks) { 
				throw new Exception("totalblock is inconsistent with topography and blocksize! " + totalBlocks + " != " 
						+ expectedBlocks);
			}
			
			distribution = new int[totalBlocks];
			
			for (int i=0;i<totalBlocks;i++) { 
				distribution[i] = in.readInt();
				
				if (distribution[i] < 0 || distribution[i] > (clusters * nodesPerCluster * coresPerNode)) { 
					throw new Exception("Inconsistent block number at position " + i + ": " + distribution[i]);
				}
			}			
		} finally {
			try { 
				in.close();
			} catch (Exception e) {
				// ignored
			}
		}
	}
	
	/** 
	 * Returns the number of the core that owns the block at the given index. 
	 * 
	 * @param index the index of the block. 
	 * @return the owner of the block.
	 */
	public int getOwner(int index) { 
		return distribution[index];
	}
	
	/** 
	 * Writes a block distribution to disk. 
	 * 
	 * @param filename the filename of the file to write the distribution to. 
	 * @throws IOException if an error occurred while writing to the file.
	 */
	public void write(String filename) throws IOException {
		
		DataOutputStream out = null;
		
		try { 
			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));

			out.writeInt(topographyWidth);
			out.writeInt(topographyHeight);
			
			out.writeInt(blockWidth);
			out.writeInt(blockHeight);
			
			out.writeInt(clusters);
			out.writeInt(nodesPerCluster);
			out.writeInt(coresPerNode);
		
			out.writeInt(minBlocksPerCore);
			out.writeInt(maxBlocksPerCore);
			
			out.writeInt(totalBlocks);
			
			for (int i=0;i<totalBlocks;i++) { 
				out.writeInt(distribution[i]);
			}
		} finally {
			try { 
				out.close();
			} catch (Exception e) {
				// ignored
			}
		}
	}
	
	/**
	 * Convert the block distribution into a representation using five layers of sets.
	 * <p>
	 * The following layers are created (top-to-bottom):
	 * <p>
	 * <ul>
	 * <li> <code>"CLUSTERS"</code> containing one set for each cluster. 
	 * <li> <code>"NODES"</code> containing one set for each node.  
	 * <li> <code>"CORES"</code> containing one set for each core.
	 * <li> <code>"BLOCKS"</code> containing one set for each block.
	 * <li> <code>"ALL"</code> containing a single set of all blocks.    
	 * </ul>
	 * <p>
	 * Each <code>"CORES"</code>, <code>"NODES"</code>, <code>"CLUSTERS"</code> set contains the blocks as assigned in the
	 * distribution. In addition, the subset relations are also returned. For example, a <code>"NODES"</code> set will contain
	 * {@link #coresPerNode} <code>"CORES"</code> subsets.     
	 * 
	 * @return the block distribution represented as five layers of sets.  
	 * @see Layer 
	 * @see Set
	 * @see Block
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Layers toLayers() { 

		// Start by computing some constants. 
		int blocksPerRow = topographyWidth / blockWidth;		
		int totalNodes = clusters * nodesPerCluster;
		int totalCores = totalNodes * coresPerNode;

		// Next, create all layers. 
		Layers layers = new Layers();
		
		Layer clusterLayer = new Layer("CLUSTERS");		
		Layer nodesLayer = new Layer("NODES");
		Layer coresLayer = new Layer("CORES");
		Layer blockLayer = new Layer("BLOCKS");
		Layer combinedLayer = new Layer("ALL");
		
		layers.add(combinedLayer);
		layers.add(blockLayer);
		layers.add(coresLayer);
		layers.add(nodesLayer);
		layers.add(clusterLayer);

		// Then create all non-zero blocks in the distribution.  
		Collection [] tmp = new Collection[totalCores];
		ArrayList<Block> allBlocks = new ArrayList<Block>();
		
		// Traverse the distribution array.
		for (int i=0;i<totalBlocks;i++) { 
			// Note: we subtract one here, since the distribution uses a Fortran friendly 1-based notation (0=unused, 1...N=used).
			int coreNumber = distribution[i]-1;
			
			// If a coreNumber is zero or higher, it represents a valid block 
			if (coreNumber >= 0) { 
				ArrayList current = (ArrayList) tmp[coreNumber];
				
				if (current == null) { 
					current = new ArrayList();
					tmp[coreNumber] = current;
				}

				// Create the block, and add to the block list of the core, the blockLayer, and the allBlocks list.
				Block b = new Block(new Coordinate(i % blocksPerRow, i / blocksPerRow));
				
				current.add(b);
				blockLayer.add(new Set(b, i));
				allBlocks.add(b);
			} 
		}

		// Create a layer containing one set with all blocks. 
		combinedLayer.add(new Set(allBlocks, 0));
		
		// Create a layers containing one set per core. 
		for (int i=0;i<totalCores;i++) { 
			coresLayer.add(new Set(tmp[i], i));
		}
		
		// Create a layers containing one set per node. 
		for (int i=0;i<totalNodes;i++) {
			
			ArrayList<Set> coresOfNode = new ArrayList<Set>();
			ArrayList<Block> blocksOfNode = new ArrayList<Block>();
			
			for (int j=0;j<coresPerNode;j++) {
				Set core = coresLayer.get(i*coresPerNode+j);

				coresOfNode.add(core);
				core.getAll(blocksOfNode);
			}
			
			Set node = new Set(blocksOfNode, i);
			node.addSubSets(coresOfNode);
			nodesLayer.add(node);
		}

		// Create a layers containing one set per cluster. 
		for (int i=0;i<clusters;i++) {
					
			ArrayList<Set> nodesOfCluster = new ArrayList<Set>();
			ArrayList<Block> blocksOfCluster = new ArrayList<Block>();
					
			for (int j=0;j<nodesPerCluster;j++) {
				Set node = nodesLayer.get(i*nodesPerCluster+j);
				
				nodesOfCluster.add(node);
				node.getAll(blocksOfCluster);
			}
					
			Set cluster = new Set(blocksOfCluster, i);
			cluster.addSubSets(nodesOfCluster);
			clusterLayer.add(cluster);
		}
		
		return layers;
	}
}
