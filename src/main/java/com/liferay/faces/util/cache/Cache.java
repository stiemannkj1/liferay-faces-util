/**
 * Copyright (c) 2000-2017 Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.faces.util.cache;

/**
 * @author  Kyle Stiemann
 * @since   3.1
 * @since   2.1
 * @since   1.1
 */
public interface Cache<K, V> {

	/**
	 * Returns true if the key (and therefor a corresponding value) is present in the map or false otherwise.
	 *
	 * @param  key  The key that the value is mapped to.
	 */
	public boolean containsKey(K key);

	/**
	 * Returns the cached value that is mapped to this key or null if the value is not in the map.
	 *
	 * @param  key  The key that the value is mapped to.
	 */
	public V get(K key);

	/**
	 * Puts the cached key-value pair into the map, overwriting any previous value mapped to that key if necessary.
	 * Returns the passed value for convenience.
	 *
	 * @param  key    The key that the value is mapped to.
	 * @param  value  The value to be cached.
	 */
	public V put(K key, V value);

	/**
	 * Puts the cached key-value pair into the map if the key does not already exist in the map. If the key was not
	 * already added to the map, this method returns the passed value for convenience. Otherwise this method returns the
	 * value associated with the key fromm the map.
	 *
	 * @param  key    The key that the value is mapped to.
	 * @param  value  The value to be cached.
	 */
	public V putIfAbsent(K key, V value);
}
