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

/**
 * PrimeFactorization is a utility class for prime factorization.
 *  
 * Using <code>factor</code>, a value can be split into its prime factors using a simple trial division algorithm. 
 * 
 * This implementation assumes the largest possible prime factor used is 997. 
 * 
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * @version 1.0
 * @since 1.0
 * @see Block
 *
 */
public class PrimeFactorization {

	/** All primes smaller than 1000. */
	private static final int [] PRIMES = new int [] 
			 {   2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 
	            47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 
	           107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 
	           167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 
	           229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 
	           283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 
	           359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 
	           431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 
	           491, 499, 503, 509, 521, 523, 541, 547, 557, 563, 569, 
	           571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 
	           641, 643, 647, 653, 659, 661, 673, 677, 683, 691, 701, 
	           709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 
	           787, 797, 809, 811, 821, 823, 827, 829, 839, 853, 857, 
	           859, 863, 877, 881, 883, 887, 907, 911, 919, 929, 937, 
	           941, 947, 953, 967, 971, 977, 983, 991, 997 };
	
	/**
	 * Find the smallest prime that divides <code>value</code> cleanly.
	 *  
	 * @param value the value to split. 
	 * @return the smallest prime that divides value cleanly. 
	 * @throws Exception if no divider could be found. 
	 */
	private static int findDivider(int value) throws Exception { 
				
		for (int i=0;i<PRIMES.length;i++) {
			
			int d = PRIMES[i];
			
			if (d > value) { 
				break;
			}
			
			if (value % d == 0) { 
				return d;
			}
		}
			
		throw new Exception("No divider not found!");
	}
	
	/**
	 * Split value into its prime factors using a simple trial division algorithm. 
	 * 
	 * This implementation assumes the largest possible prime factor used is 997. 
	 * 
	 * @param value the value to split (must be 2 or greater).
	 * @return an array containing the prime factors of value, sorted by size (largest first).
	 * @throws Exception if value could not be split because it is smaller than two or contains 
	 * a prime factor larger than 997. 
	 */
	public static int [] factor(int value) throws Exception { 
		
		if (value <= 1) { 
			throw new Exception("Value must be >= 2!");
		}
		
		ArrayList<Integer> dividers = new ArrayList<Integer>();
		
		while (value > 1) {
			
			int div = findDivider(value);
			
			dividers.add(div);
			
			value = value/div;
		}

		int [] result = new int[dividers.size()];
		
		for (int i=0;i<dividers.size();i++) { 
			result[i] = dividers.get(dividers.size()-i-1);
		}
		
		return result;
	}
}
