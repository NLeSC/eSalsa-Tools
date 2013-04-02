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

/**
 * This package contains various classes related to eSalsa LoadBalancer.
 * <p>
 * The <code>LoadBalancer</code> is the main entry point into this library. It is used to generate a block distribution for the 
 * Parallel Ocean Program (POP).   
 * <p>
 * This block distribution is based on the ocean bottom topography, the desired block size, and the desired number of clusters, 
 * nodes per cluster, and cores per node.
 * <p> 
 * The LoadBalancer uses one or more of the subclasses of <code>Split</code> to generate a block distribution that:
 * <p>
 * <ul>
 * <li>distributes the work equally over the available cores.</li> 
 * <li>attempts to minimize the amount of communication required between cluster, nodes, and cores.</li>
 * </ul>  
 * <p>
 */
package nl.esciencecenter.esalsa.loadbalancer;

// Rest of file is left intentionally blank.
