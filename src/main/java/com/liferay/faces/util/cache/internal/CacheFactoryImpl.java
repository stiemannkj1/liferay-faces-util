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

import javax.faces.context.ExternalContext;

import com.liferay.faces.util.cache.Cache;
import com.liferay.faces.util.cache.CacheFactory;


/**
 * @author  Kyle Stiemann
 */
public class CacheFactoryImpl extends CacheFactory {

	@Override
	public <K, V> Cache<K, V> getCache() {
		return new CacheImpl<K, V>();
	}

	@Override
	public <K, V> Cache<K, V> getCache(int maxCacheSize) {

		validateMaxCacheSize(maxCacheSize);

		return new CacheMaxSizeImpl<K, V>(maxCacheSize);
	}

	@Override
	public <K, V> Cache<K, V> getCache(ExternalContext externalContext, String maxCacheSizeInitParamName) {

		Integer maxCacheSize = getMaxCacheSize(externalContext, maxCacheSizeInitParamName);

		if (maxCacheSize != null) {
			return getCache(maxCacheSize);
		}
		else {
			return getCache();
		}
	}

	@Override
	public <K, V> Cache<K, V> getConcurrentCache() {
		return new ConcurrentCacheImpl<K, V>();
	}

	@Override
	public <K, V> Cache<K, V> getConcurrentCache(int maxCacheSize) {

		validateMaxCacheSize(maxCacheSize);

		return new ConcurrentCacheMaxSizeImpl<K, V>(maxCacheSize);
	}

	@Override
	public <K, V> Cache<K, V> getConcurrentCache(ExternalContext externalContext, String maxCacheSizeInitParamName) {

		Integer maxCacheSize = getMaxCacheSize(externalContext, maxCacheSizeInitParamName);

		if (maxCacheSize != null) {
			return getConcurrentCache(maxCacheSize);
		}
		else {
			return getConcurrentCache();
		}
	}

	@Override
	public CacheFactory getWrapped() {

		// Since this is the default factory instance, it will never wrap another factory.
		return null;
	}

	private Integer getMaxCacheSize(ExternalContext externalContext, String maxCacheSizeInitParamName) {

		Integer maxCacheSize = null;
		String maxCacheSizeString = externalContext.getInitParameter(maxCacheSizeInitParamName);

		if (maxCacheSizeString != null) {
			maxCacheSize = Integer.parseInt(maxCacheSizeString);
		}

		return maxCacheSize;
	}

	private void validateMaxCacheSize(int maxCacheSize) {

		if (maxCacheSize < 1) {
			throw new IllegalArgumentException("Invalid maxCacheSize of " + maxCacheSize +
				". maxCacheSize must be greater than 0.");
		}
	}
}
