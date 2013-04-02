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

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Layers provides a simple storage class for a collection of layers. A layer can be retrieved using its name. 
 *  
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * @version 1.0
 * @since 1.0
 * @see Layer
 * 
 */
public class Layers {

	private static final Logger logger = LoggerFactory.getLogger(Layers.class);
	
	private final HashMap<String, Layer> layers = new HashMap<String, Layer>();

	/**
	 * Add a Layer. 
	 * 
	 * @param layer the Layer to add.
	 */
	public void add(Layer layer) {
		
		if (logger.isDebugEnabled()) { 
			logger.debug("Add layer " + layer.name + " with " + layer.size() + " elements at layer: " + layers.size());
		}

		layers.put(layer.name, layer);
	}
	
	/** 
	 * Retrieve a Layer with a certain name.
	 * 
	 * @param name the name of the Layer to retrieve.
	 * @return the Layer requested, or null if the Layer does not exist.
	 */
	public Layer get(String name) { 

		if (logger.isDebugEnabled()) { 
			logger.debug("Get layer " + name);
		}
		
		if (name == null) { 
			throw new IllegalArgumentException("A layer name must be provided!");
		} 

		return layers.get(name);
	}
	
	/** 
	 * Check if a layer with a certain name exists.
	 * 
	 * @param name the name of the layer to check.
	 * @return true if a layer with the provided name exists, false otherwise.
	 */	
	public boolean contains(String name) { 

		if (layers.size() == 0) { 
			return false;
		}
		
		if (name == null) { 
			throw new IllegalArgumentException("A layer name must be provided!");
		} 

		return layers.containsKey(name);
	}

	/** 
	 * Returns the number of available layer. 
	 * 
	 * @return the number of available layers.
	 */
	public int size() { 
		return layers.size();
	}

	/** 
	 * Returns the names of all layers currently stored.
	 * 
	 * @return an array containing the names of all layers. 
	 * The name of layer N will be stored at position N in the array. 
	 */
	public String [] listLayers() { 
		return layers.keySet().toArray(new String[layers.size()]);
	}
}
