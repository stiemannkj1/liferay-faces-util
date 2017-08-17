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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Assert;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.util.cache.internal.CacheFactoryImpl;


/**
 * @author  Kyle Stiemann
 */
public class CacheTest {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(CacheTest.class);

	@Test
	public void runCacheTest() {

		CacheFactoryImpl cacheFactoryImpl = new CacheFactoryImpl();
		Cache<String, String> cache = cacheFactoryImpl.<String, String>getCache();
		testCache(cache, 1000);
	}

	@Test
	public void runConcurrentCacheTest() throws Exception {

		CacheFactoryImpl cacheFactoryImpl = new CacheFactoryImpl();
		Cache<String, String> cache1 = cacheFactoryImpl.<String, String>getConcurrentCache();
		testCache(cache1, 1000);

		final Cache<String, String> cache2 = cacheFactoryImpl.<String, String>getConcurrentCache();
		final Queue<AssertionError> testFailures = new ConcurrentLinkedQueue<AssertionError>();
		final Queue<Throwable> testErrors = new ConcurrentLinkedQueue<Throwable>();

		for (int i = 0; i < 100; i++) {
			new TestConcurrentCacheThread(cache2, testFailures, testErrors, "key" + (i % 25), "value" + i).start();
		}

		errorOrFailTestIfNecessary(testErrors, testFailures);
	}

	@Test
	public void runConcurrentMaxCacheSizeCacheTest() throws Exception {

		CacheFactoryImpl cacheFactoryImpl = new CacheFactoryImpl();
		Cache<String, String> cache1 = cacheFactoryImpl.<String, String>getConcurrentCache(1000);
		testCache(cache1, 1000);

		final Cache<String, String> cache2 = cacheFactoryImpl.<String, String>getConcurrentCache(10);
		final Queue<AssertionError> testFailures = new ConcurrentLinkedQueue<AssertionError>();
		final Queue<Throwable> testErrors = new ConcurrentLinkedQueue<Throwable>();

		for (int i = 0; i < 100; i++) {
			new TestConcurrentCacheThread(cache2, testFailures, testErrors, "key" + (i % 25), "value" + i).start();
		}

		errorOrFailTestIfNecessary(testErrors, testFailures);

		final Cache<String, String> cache3 = cacheFactoryImpl.<String, String>getConcurrentCache(10);
		testConcurrentMaxCacheSizeCache(cache3, 10, testFailures, testErrors);
		errorOrFailTestIfNecessary(testErrors, testFailures);
	}

	@Test
	public void runMaxSizeCacheTest() throws Exception {

		CacheFactoryImpl cacheFactoryImpl = new CacheFactoryImpl();
		Cache<String, String> cache1 = cacheFactoryImpl.<String, String>getCache(1000);
		testCache(cache1, 1000);

		Cache<String, String> cache2 = cacheFactoryImpl.<String, String>getConcurrentCache(10);

		for (int i = 0; i < 100; i++) {

			String key = "key" + (i % 25);
			String value = "value" + i;
			String cachedString = cache2.get(key);

			if (cachedString == null) {
				cachedString = cache2.putIfAbsent(key, value);
			}

			Assert.assertNotNull(cachedString);
			cachedString = cache2.put(key, value);
			Assert.assertEquals(value, cachedString);
		}

		int maxCacheSize = 10;
		Cache<String, String> cache3 = cacheFactoryImpl.<String, String>getCache(maxCacheSize);

		for (int i = 0; i < maxCacheSize; i++) {
			cache3.putIfAbsent("key" + i, "value" + i);
		}

		// Verify that get(), put(), and putIfAbsent() all mark the entry as recently used.
		Assert.assertNotNull(cache3.get("key0"));
		cache3.putIfAbsent("key1", "value1");
		cache3.put("key2", "value2");
		cache3.put("key" + maxCacheSize, "value" + maxCacheSize);
		Assert.assertNull(cache3.get("key3"));
	}

	private void errorOrFailTestIfNecessary(final Queue<Throwable> testErrors, final Queue<AssertionError> testFailures)
		throws AssertionError, Exception {

		for (Throwable testError : testErrors) {
			logger.error("", testError);
		}

		for (AssertionError testFailure : testFailures) {
			logger.error("", testFailure);
		}

		int testErrorsSize = testErrors.size();

		if (testErrorsSize > 0) {
			throw new Exception(testErrorsSize + " threads threw an error during test execution.");
		}

		int testFailuresSize = testFailures.size();

		if (testFailuresSize > 0) {
			throw new AssertionError(testFailuresSize + " threads reported a failure during test execution.");
		}
	}

	private void testCache(Cache cache, int iterations) {

		for (int i = 0; i < iterations; i++) {

			String key = "key" + i;
			String value = "value" + i;
			Assert.assertNull(cache.get(key));
			Assert.assertEquals(value, cache.put(key, value));
			Assert.assertEquals(value, cache.putIfAbsent(key, "different" + value));
			Assert.assertEquals(value, cache.get(key));
			Assert.assertEquals("different" + value, cache.put(key, "different" + value));
			Assert.assertEquals("different" + value, cache.get(key));
		}
	}

	private void testConcurrentMaxCacheSizeCache(final Cache cache, final int maxCacheSize,
		Queue<AssertionError> testFailures, Queue<Throwable> testErrors) {

		if (maxCacheSize < 10) {
			throw new IllegalArgumentException("This test must be run with at least 10 values.");
		}

		new TestThreadBase(cache, testFailures, testErrors) {

				@Override
				protected void testCache() {

					for (int i = 0; i < maxCacheSize; i++) {
						cache.putIfAbsent("key" + i, "value" + i);
					}
				}
			}.run();

		new TestThreadBase(cache, testFailures, testErrors) {

				@Override
				protected void testCache() {
					Assert.assertNotNull(cache.get("key0"));
				}
			}.run();

		new TestThreadBase(cache, testFailures, testErrors) {

				@Override
				protected void testCache() {
					cache.putIfAbsent("key1", "value1");
				}
			}.run();

		new TestThreadBase(cache, testFailures, testErrors) {

				@Override
				protected void testCache() {
					cache.put("key2", "value2");
				}
			}.run();

		new TestThreadBase(cache, testFailures, testErrors) {

				@Override
				protected void testCache() {
					cache.put("key" + maxCacheSize, "value" + maxCacheSize);
				}
			}.run();

		new TestThreadBase(cache, testFailures, testErrors) {

				@Override
				protected void testCache() {
					Assert.assertNull(cache.get("key3"));
				}
			}.run();
	}

	private abstract static class TestThreadBase extends Thread {

		// Protected Final Data Members
		protected final Cache<String, String> cache;
		protected final Queue<AssertionError> testFailures;
		protected final Queue<Throwable> testErrors;

		public TestThreadBase(Cache<String, String> cache, Queue<AssertionError> testFailures,
			Queue<Throwable> testErrors) {
			this.cache = cache;
			this.testFailures = testFailures;
			this.testErrors = testErrors;
		}

		@Override
		public void run() {

			try {
				testCache();
			}
			catch (AssertionError e) {
				testFailures.add(e);
			}
			catch (Throwable e) {
				testErrors.add(e);
			}
		}

		protected abstract void testCache();
	}

	private static final class TestConcurrentCacheThread extends TestThreadBase {

		// Private Final Data Members
		protected final String key;
		protected final String value;

		public TestConcurrentCacheThread(Cache<String, String> cache, Queue<AssertionError> testFailures,
			Queue<Throwable> testErrors, String key, String value) {

			super(cache, testFailures, testErrors);
			this.key = key;
			this.value = value;
		}

		@Override
		protected void testCache() {

			String cachedString = cache.get(key);

			if (cachedString == null) {
				cachedString = cache.putIfAbsent(key, value);
			}

			Assert.assertNotNull(cachedString);
			cachedString = cache.put(key, value);
			Assert.assertEquals(value, cachedString);
		}
	}
}
