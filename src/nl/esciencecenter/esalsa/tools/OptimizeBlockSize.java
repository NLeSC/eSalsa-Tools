package nl.esciencecenter.esalsa.tools;

import java.util.ArrayList;
import java.util.Arrays;

import nl.esciencecenter.esalsa.util.Grid;
import nl.esciencecenter.esalsa.util.Topography;

public class OptimizeBlockSize {

	private final static int HALO = 2;
	
	private static int [] findDividers(int value) { 
		
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		for (int i=1;i<=value;i++) { 
			if (value % i == 0) { 
				result.add(value / i);
			}
		}
		
		int [] tmp = new int[result.size()];
		
		for (int i=0;i<result.size();i++) { 
			tmp[i] = result.get(i);
		}
		
		return tmp;
	}
	
	private static int test(Topography t, int blockWidth, int blockHeight) throws Exception { 
		return new Grid(t, blockWidth, blockHeight).getCount() * (blockWidth + 2*HALO) * (blockHeight + 2*HALO); 
	}
	
	public static void main(String [] args) { 
		
		if (args.length < 3) { 
			System.out.println("Usage: OptimizeBlockSize topography_file topography_width topography_height\n" + 
					"\n" + 
					"Read a topography file of topography_width x topography_height, and find the optimal block size. " + 
					"This optimization takes into account that smaller blocks allow more land to discarded, while each block " + 
					"also adds extra computations in the HALO.\n");

			System.exit(1);
		}

		String topographyFile = args[0];
		int width = Utils.parseInt("topography_width", args[1], 1);
		int height = Utils.parseInt("topography_height", args[2], 1);
	
		try { 			
			Topography t = new Topography(width, height, topographyFile);
	
			int [] blockWidths = findDividers(width);
			int [] blockHeights = findDividers(height);
			
			System.out.println("# Possible block widths " + Arrays.toString(blockWidths));
			System.out.println("# Possible block heights " + Arrays.toString(blockHeights));

			int best = Integer.MAX_VALUE;
			int bestW = 0;
			int bestH = 0;
			
			for (int w=0;w<blockWidths.length;w++) { 
				for (int h=0;h<blockHeights.length;h++) { 
					int tmp = test(t, blockWidths[w], blockHeights[h]);
					
//					System.out.println(blockWidths[w] + " " + blockHeights[h] + " " + tmp);
					
					if (tmp < best) { 
						best = tmp;
						bestW = blockWidths[w];
						bestH = blockHeights[h];
					}
				}	
			}
		
			System.out.println("# Best block size " + bestW + " x " + bestH + " => " + best + " work");
			
			int [] max = new int[5];
			
			max[0] = (int) (best * 1.005);
			max[1] = (int) (best * 1.01);
			max[2] = (int) (best * 1.015);
			max[3] = (int) (best * 1.02);
			max[4] = (int) (best * 1.025);
			
			System.out.println("# Finding solutions within 2.5% of best (" + best + " ... " + Arrays.toString(max) + ")");
			
			for (int w=0;w<blockWidths.length;w++) { 
				for (int h=0;h<blockHeights.length;h++) { 
					int tmp = test(t, blockWidths[w], blockHeights[h]);

					for (int i=0;i<max.length;i++) { 
						if (tmp < max[i]) { 
							System.out.println("[" + ((i+1)*0.5) + "] " + blockWidths[w] + "x" + blockHeights[h] + " " + (blockWidths[w] * blockHeights[h]) + " " + tmp);	
							break;
						}
					}
				}	
			}
		} catch (Exception e) { 
			Utils.fatal("EEP", e);
		}	
	}
}
