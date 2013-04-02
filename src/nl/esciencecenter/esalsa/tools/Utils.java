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

/**
 * Utils is a container class for various static methods used in the applications in this package. 
 *  
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * @version 1.0
 * @since 1.0
 */
public class Utils {
	
	/** 
	 * Check if enough parameters are available for the current option. 
	 * If not, an error is printed and the application is terminated.  
	 * 
	 * @param option the current command line option. 
	 * @param parameters the number of parameters required.
	 * @param index the current index in the command line parameter list.  
	 * @param length the length of the command line parameter list.
	 */
	public static void checkOptions(String option, int parameters, int index, int length) { 
		if (index+parameters >= length) { 
			fatal("Missing arguments for option " + option + " (required parameters " + parameters + ")");
		}
	}
	
	/** 
	 * Print an error and terminate the application.  
	 * 
	 * @param error the error to print.
	 */
	public static void fatal(String error) { 
		System.err.println(error);
		System.exit(1);	 
	}
	
	/** 
	 * Print an error and terminate the application.  
	 * 
	 * @param e Exception that describes the fatal fault.
	 */
	public static void fatal(Exception e) { 
		System.err.println(e.getLocalizedMessage());
		e.printStackTrace(System.err);
		System.exit(1);	 
	}

	/** 
	 * Print an error and terminate the application.  
	 * 
	 * @param e Exception that describes the fatal fault.
	 */
	public static void fatal(String message, Exception e) { 
		System.err.println(message + " " + e.getLocalizedMessage());
		e.printStackTrace(System.err);
		System.exit(1);	 
	}
	
	/** 
	 * Parse a string containing an integer and ensure its value is at least {@code minValue}.
	 * If the string cannot be parsed, or the integer is smaller than minValue, an error is printed 
	 * and the application is terminated. 
	 * 
	 * @param option the current command line option. 
	 * @param toParse the string to parse
	 * @param minValue the required minimal value for the integer. 
	 * @return the int value in the option string. 
	 */
	public static int parseInt(String option, String toParse, int minValue) { 
		
		int result = -1;
		
		try { 
			result = Integer.parseInt(toParse);
		} catch (Exception e) {
			fatal("Failed to read argument for option " + option + " (parameters " + toParse + " is not a number)");
		}
		
		if (result < minValue) { 
			fatal("Argument for option " + option + " must have a value of at least " + minValue + " (got " + result + ")");
		}
		
		return result;		
	}
	
	/** 
	 * Parse a string containing an integer and ensure its value is at least <code>minValue</code> and at most 
	 * <code>maxValue</code>.
	 * If the string cannot be parsed, or the integer is smaller than minValue or larger than maxValue, an error is printed 
	 * and the application is terminated. 
	 * 
	 * @param option the current command line option. 
	 * @param toParse the string to parse
	 * @param minValue the minimum value for the integer.
	 * @param maxValue the maximum value for the integer. 
	 * @return the int value in the option string. 
	 */
	public static int parseInt(String option, String toParse, int minValue, int maxValue) { 
		
		int result = parseInt(option, toParse, minValue);
		
		if (result > maxValue) { 
			fatal("Argument for option " + option + " must have a value of at most " + maxValue + " (got " + result + ")");
		}
		
		return result;		
	}	
}
