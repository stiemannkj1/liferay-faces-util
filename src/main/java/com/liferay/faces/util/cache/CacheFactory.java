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

import javax.faces.FacesWrapper;
import javax.faces.context.ExternalContext;

import com.liferay.faces.util.factory.FactoryExtensionFinder;


/**
 * @author  Kyle Stiemann
 * @since   3.1
 * @since   2.1
 * @since   1.1
 */
public abstract class CacheFactory implements FacesWrapper<CacheFactory> {

	/**
	 * Returns a new instance of {@link Cache} from the {@link CacheFactory} found by the {@link
	 * FactoryExtensionFinder}. The returned instance is designed to be used during execution of a request thread, so it
	 * is not guaranteed to be {@link java.io.Serializable}.
	 *
	 * @param  externalContext  The external context associated with the current faces context. It is needed in order
	 *                          for the {@link FactoryExtensionFinder} to be able to find the factory.
	 */
	public static <K, V> Cache<K, V> getCacheInstance(ExternalContext externalContext) {

		CacheFactory cacheFactory = (CacheFactory) FactoryExtensionFinder.getFactory(externalContext,
				CacheFactory.class);

		return cacheFactory.getCache();
	}

	/**
	 * Returns a new instance of {@link Cache} from the {@link CacheFactory} found by the {@link
	 * FactoryExtensionFinder}. The returned instance is designed to be used during execution of a request thread, so it
	 * is not guaranteed to be {@link java.io.Serializable}.
	 *
	 * @param  externalContext  The external context associated with the current faces context. It is needed in order
	 *                          for the {@link FactoryExtensionFinder} to be able to find the factory.
	 * @param  maxCacheSize     The max size of the cache.
	 */
	public static <K, V> Cache<K, V> getCacheInstance(ExternalContext externalContext, int maxCacheSize) {

		CacheFactory cacheFactory = (CacheFactory) FactoryExtensionFinder.getFactory(externalContext,
				CacheFactory.class);

		return cacheFactory.getCache(maxCacheSize);
	}

	/**
	 * Returns a new instance of {@link Cache} from the {@link CacheFactory} found by the {@link
	 * FactoryExtensionFinder}. The returned instance is designed to be used during execution of a request thread, so it
	 * is not guaranteed to be {@link java.io.Serializable}.
	 *
	 * @param  externalContext            The external context associated with the current faces context. It is needed
	 *                                    in order for the {@link FactoryExtensionFinder} to be able to find the
	 *                                    factory.
	 * @param  maxCacheSizeInitParamName  The name of the init-param which should used to determine the optional max
	 *                                    size of the cache. If the init-param is unset, the cache can grow infinitely.
	 */
	public static <K, V> Cache<K, V> getCacheInstance(ExternalContext externalContext,
		String maxCacheSizeInitParamName) {

		CacheFactory cacheFactory = (CacheFactory) FactoryExtensionFinder.getFactory(externalContext,
				CacheFactory.class);

		return cacheFactory.getCache(externalContext, maxCacheSizeInitParamName);
	}

	/**
	 * Returns a new instance of {@link Cache} from the {@link CacheFactory} found by the {@link
	 * FactoryExtensionFinder}. The returned instance is designed to be accessed and modified by multiple threads
	 * concurrently, so it is guaranteed to be {@link java.io.Serializable}.
	 *
	 * @param  K                The type of the cache's keys.
	 * @param  V                The type of the cache's values.
	 * @param  externalContext  The external context associated with the current faces context.
	 */
	public static <K, V> Cache<K, V> getConcurrentCacheInstance(ExternalContext externalContext) {

		CacheFactory cacheFactory = (CacheFactory) FactoryExtensionFinder.getFactory(externalContext,
				CacheFactory.class);

		return cacheFactory.getConcurrentCache();
	}

	/**
	 * Returns a new instance of {@link Cache} from the {@link CacheFactory} found by the {@link
	 * FactoryExtensionFinder}. The returned instance is designed to be accessed and modified by multiple threads
	 * concurrently, so it is guaranteed to be {@link java.io.Serializable}.
	 *
	 * @param  K                The type of the cache's keys.
	 * @param  V                The type of the cache's values.
	 * @param  externalContext  The external context associated with the current faces context.
	 * @param  maxCacheSize     The max size of the cache.
	 */
	public static <K, V> Cache<K, V> getConcurrentCacheInstance(ExternalContext externalContext, int maxCacheSize) {

		CacheFactory cacheFactory = (CacheFactory) FactoryExtensionFinder.getFactory(externalContext,
				CacheFactory.class);

		return cacheFactory.getConcurrentCache(maxCacheSize);
	}

	/**
	 * Returns a new instance of {@link Cache} from the {@link CacheFactory} found by the {@link
	 * FactoryExtensionFinder}. The returned instance is designed to be accessed and modified by multiple threads
	 * concurrently, so it is guaranteed to be {@link java.io.Serializable}.
	 *
	 * @param  K                          The type of the cache's keys.
	 * @param  V                          The type of the cache's values.
	 * @param  externalContext            The external context associated with the current faces context.
	 * @param  maxCacheSizeInitParamName  The name of the init-param which should used to determine the optional max
	 *                                    size of the cache. If the init-param is unset, the cache can grow infinitely.
	 */
	public static <K, V> Cache<K, V> getConcurrentCacheInstance(ExternalContext externalContext,
		String maxCacheSizeInitParamName) {

		CacheFactory cacheFactory = (CacheFactory) FactoryExtensionFinder.getFactory(externalContext,
				CacheFactory.class);

		return cacheFactory.getConcurrentCache(externalContext, maxCacheSizeInitParamName);
	}

	/**
	 * Returns a new instance of {@link Cache}. The returned instance is designed to be used during execution of a
	 * request thread, so it is not guaranteed to be {@link java.io.Serializable}.
	 *
	 * @param  K  The type of the cache's keys.
	 * @param  V  The type of the cache's values.
	 */
	public abstract <K, V> Cache<K, V> getCache();

	/**
	 * Returns a new instance of {@link Cache}. The returned instance is designed to be used during execution of a
	 * request thread, so it is not guaranteed to be {@link java.io.Serializable}.
	 *
	 * @param  K             The type of the cache's keys.
	 * @param  V             The type of the cache's values.
	 * @param  maxCacheSize  The max size of the cache.
	 */
	public abstract <K, V> Cache<K, V> getCache(int maxCacheSize);

	/**
	 * Returns a new instance of {@link Cache}. The returned instance is designed to be used during execution of a
	 * request thread, so it is not guaranteed to be {@link java.io.Serializable}.
	 *
	 * @param  K                          The type of the cache's keys.
	 * @param  V                          The type of the cache's values.
	 * @param  externalContext            The external context associated with the current faces context.
	 * @param  maxCacheSizeInitParamName  The name of the init-param which should used to determine the optional max
	 *                                    size of the cache. If the init-param is unset, the cache can grow infinitely.
	 */
	public abstract <K, V> Cache<K, V> getCache(ExternalContext externalContext, String maxCacheSizeInitParamName);

	/**
	 * Returns a new instance of {@link Cache}. The returned instance is designed to be accessed and modified by
	 * multiple threads concurrently, so it is guaranteed to be {@link java.io.Serializable}.
	 *
	 * @param  K  The type of the cache's keys.
	 * @param  V  The type of the cache's values.
	 */
	public abstract <K, V> Cache<K, V> getConcurrentCache();

	/**
	 * Returns a new instance of {@link Cache}. The returned instance is designed to be accessed and modified by
	 * multiple threads concurrently, so it is guaranteed to be {@link java.io.Serializable}.
	 *
	 * @param  K             The type of the cache's keys.
	 * @param  V             The type of the cache's values.
	 * @param  maxCacheSize  The max size of the cache.
	 */
	public abstract <K, V> Cache<K, V> getConcurrentCache(int maxCacheSize);

	/**
	 * Returns a new instance of {@link Cache}. The returned instance is designed to be accessed and modified by
	 * multiple threads concurrently, so it is guaranteed to be {@link java.io.Serializable}.
	 *
	 * @param  K                          The type of the cache's keys.
	 * @param  V                          The type of the cache's values.
	 * @param  externalContext            The external context associated with the current faces context.
	 * @param  maxCacheSizeInitParamName  The name of the init-param which should used to determine the optional max
	 *                                    size of the cache. If the init-param is unset, the cache can grow infinitely.
	 */
	public abstract <K, V> Cache<K, V> getConcurrentCache(ExternalContext externalContext,
		String maxCacheSizeInitParamName);

	/**
	 * Returns the wrapped factory instance if this factory decorates another. Otherwise, this method returns null.
	 */
	@Override
	public abstract CacheFactory getWrapped();
}
