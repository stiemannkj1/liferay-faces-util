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
package com.liferay.faces.util.cache.internal;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.liferay.faces.util.cache.Cache;


/**
 * A simple {@link Cache} which can be accessed/modified concurrently and limits the cache size by removing the least
 * recently used entry when a new value is added to the full cache. This implementation locks on writes in order to
 * ensure that the cache cannot grow infinitely. For more details, see {@link
 * #removeLeastRecentlyUsedCacheValuesIfNecessary(java.lang.Object)}. A simple alternative would be to use {@link
 * Collections#synchronizedMap(java.util.Map)} on a {@link LinkedHashMap} with accessOrder set to true. However,
 * Collections.synchronizedMap() causes the map to lock on reads which is unacceptable for a cache. For more details,
 * see here: https://stackoverflow.com/questions/221525/how-would-you-implement-an-lru-cache-in-java
 *
 * @author  Kyle Stiemann
 */
public class ConcurrentCacheMaxCapacityLRUImpl<K, V> implements Serializable, Cache<K, V> {

	// serialVersionUID
	private static final long serialVersionUID = 6181106754606500765L;

	// Private Final Data Members
	private final ConcurrentHashMap<K, CachedValue<V>> internalCache;
	private final Integer maxCapacity;

	public ConcurrentCacheMaxCapacityLRUImpl(int initialCapacity, int maxCapacity) {

		this.internalCache = new ConcurrentHashMap<K, CachedValue<V>>(initialCapacity);
		this.maxCapacity = maxCapacity;
	}

	@Override
	public boolean containsKey(K key) {
		return internalCache.containsKey(key);
	}

	@Override
	public V get(K key) {

		CachedValue<V> cachedValue = internalCache.get(key);

		if (cachedValue != null) {
			return cachedValue.getValue();
		}
		else {
			return null;
		}
	}

	@Override
	public V put(K key, V value) {

		removeLeastRecentlyUsedCacheValuesIfNecessary(key);
		internalCache.put(key, new CachedValue<V>(value));

		return value;
	}

	@Override
	public V putIfAbsent(K key, V value) {

		removeLeastRecentlyUsedCacheValuesIfNecessary(key);

		CachedValue<V> cachedValue = internalCache.putIfAbsent(key, new CachedValue<V>(value));
		V retValue;

		if (cachedValue != null) {
			retValue = cachedValue.getValue();
		}
		else {
			retValue = value;
		}

		return retValue;
	}

	/**
	 * This method must be called before putting values into the map. It ensures that if the map is full and we are
	 * trying to add a new key, the least recently used value will be removed. This method must ensure that at most one
	 * thread will remove a value at a time. Otherwise, thread A might remove a value causing threads B, C, D, E, F,...
	 * and Z all to see that the map is not full and add their values at the same time. This would cause the map to
	 * expand past its set max size (potentially infinitely).
	 */
	private void removeLeastRecentlyUsedCacheValuesIfNecessary(K key) {

		// Don't synchronize on the ConcurrentHashMap in case it synchronizes on itself internally (avoid locking on
		// reads).
		synchronized (maxCapacity) {

			if ((internalCache.size() >= maxCapacity) && !internalCache.containsKey(key)) {

				Set<Map.Entry<K, CachedValue<V>>> entrySet = internalCache.entrySet();
				Map.Entry<K, CachedValue<V>> leastRecentlyAccessedEntry = null;

				for (Map.Entry<K, CachedValue<V>> entry : entrySet) {

					if (leastRecentlyAccessedEntry != null) {

						CachedValue<V> cachedValue = entry.getValue();
						CachedValue<V> leastRecentlyAccessedCacheValue = leastRecentlyAccessedEntry.getValue();

						if (cachedValue.wasLastAccessedBefore(leastRecentlyAccessedCacheValue)) {
							leastRecentlyAccessedEntry = entry;
						}
					}
					else {
						leastRecentlyAccessedEntry = entry;
					}
				}

				internalCache.remove(leastRecentlyAccessedEntry.getKey());
			}
		}
	}

	private static final class CachedValue<V> {

		// Private Final Data Members
		private final V value;

		//J-
		// Private Volatile Data Members
		//J+

		// Reads and writes to volatile long primitives are atmoic:
		// https://stackoverflow.com/questions/3038203/is-there-any-point-in-using-a-volatile-long
		private volatile long lastAccessed;

		public CachedValue(V value) {

			this.lastAccessed = System.nanoTime();
			this.value = value;
		}

		public long getLastAccessedNanoTime() {
			return lastAccessed;
		}

		public V getValue() {

			lastAccessed = System.nanoTime();

			return value;
		}

		public boolean wasLastAccessedBefore(CachedValue otherCachedValue) {

			boolean lastAccessedBefore = true;

			if (otherCachedValue != null) {

				long lastAccessedTime = getLastAccessedNanoTime();
				long otherLastAccessedTime = otherCachedValue.getLastAccessedNanoTime();

				// Since System.nanoTime() is not garaunteed to return a positive value, the javadocs recommend the
				// following method for comparing System.nanoTime() results.. For more details, see:
				// https://docs.oracle.com/javase/8/docs/api/java/lang/System.html#nanoTime--
				lastAccessedBefore = ((lastAccessedTime - otherLastAccessedTime) < 0);
			}

			return lastAccessedBefore;
		}
	}
}
