/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.util.osgi.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.spi.ResourceBundleControlProvider;

import com.liferay.faces.util.i18n.UTF8Control;


/**
 * @author  Kyle Stiemann
 */
public class ResourceBundleUtil {

	// Private Constants
	private static final BaseControl BASE_CONTROL = new BaseControl();
	private static final List<ResourceBundleControlProvider> RESOURCE_BUNDLE_CONTROL_PROVIDERS;
	private static final ResourceBundle.Control UTF8_CONTROL = new UTF8Control();
	private static final ResourceBundle.Control OSGI_FRIENDLY_UTF8_CONTROL = new OSGiFriendlyControl(UTF8_CONTROL,
			ResourceBundleUtil.class);

	static {

		ServiceLoader<ResourceBundleControlProvider> serviceLoaders = ServiceLoader.loadInstalled(
				ResourceBundleControlProvider.class);
		List<ResourceBundleControlProvider> resourceBundleControlProviders =
			new ArrayList<ResourceBundleControlProvider>();
		Iterator<ResourceBundleControlProvider> iterator = serviceLoaders.iterator();

		while (iterator.hasNext()) {
			resourceBundleControlProviders.add(iterator.next());
		}

		RESOURCE_BUNDLE_CONTROL_PROVIDERS = Collections.unmodifiableList(resourceBundleControlProviders);
	}

	private ResourceBundleUtil() {
		throw new AssertionError();
	}

	public static ResourceBundle getOSGiFriendlyResourceBundle(String baseName, Locale targetLocale,
		ClassLoader threadContextClassLoader, Class<?> callingClass) throws MissingResourceException {

		ResourceBundle.Control osgiFriendlyControl = getOSGiFriendlyControl(baseName, callingClass);

		return ResourceBundle.getBundle(baseName, targetLocale, threadContextClassLoader, osgiFriendlyControl);
	}

	public static ResourceBundle getOSGiFriendlyResourceBundle(String baseName, Locale targetLocale,
		ClassLoader threadContextClassLoader, ResourceBundle.Control control, Class<?> callingClass)
		throws MissingResourceException {

		OSGiFriendlyControl osgiFriendlyControl = new OSGiFriendlyControl(control, callingClass);

		return ResourceBundle.getBundle(baseName, targetLocale, threadContextClassLoader, osgiFriendlyControl);
	}

	public static ResourceBundle getOSGiFriendlyUTF8ResourceBundle(String baseName, Locale targetLocale,
		ClassLoader threadContextClassLoader) throws MissingResourceException {
		return ResourceBundle.getBundle(baseName, targetLocale, threadContextClassLoader, OSGI_FRIENDLY_UTF8_CONTROL);
	}

	public static ResourceBundle getOSGiFriendlyUTF8ResourceBundle(String baseName, Locale targetLocale,
		ClassLoader threadContextClassLoader, Class<?> callingClass) throws MissingResourceException {

		ResourceBundle.Control osgiFriendlyUTF8Control = new OSGiFriendlyControl(UTF8_CONTROL, callingClass);

		return ResourceBundle.getBundle(baseName, targetLocale, threadContextClassLoader, osgiFriendlyUTF8Control);
	}

	private static ResourceBundle.Control getOSGiFriendlyControl(String baseName, Class<?> callingClass) {

		ResourceBundle.Control control = null;

		for (ResourceBundleControlProvider resourceBundleControlProvider : RESOURCE_BUNDLE_CONTROL_PROVIDERS) {

			ResourceBundle.Control providedControl = resourceBundleControlProvider.getControl(baseName);

			if (providedControl != null) {

				control = new OSGiFriendlyControl(providedControl, callingClass);

				break;
			}
		}

		if (control == null) {
			control = new OSGiFriendlyControl(BASE_CONTROL, callingClass);
		}

		return control;
	}

	private static final class BaseControl extends ResourceBundle.Control {

		private BaseControl() {
			// Work around the fact that Control's constructor is protected and inaccessible.
		}
	}

	private static final class OSGiFriendlyControl extends ResourceBundle.Control {

		private ResourceBundle.Control wrappedControl;
		private ClassLoader bundleClassLoader;

		public OSGiFriendlyControl(ResourceBundle.Control wrappedControl, Class<?> callingClass) {

			this.wrappedControl = wrappedControl;
			this.bundleClassLoader = callingClass.getClassLoader();
		}

		@Override
		public List<Locale> getCandidateLocales(String baseName, Locale locale) {
			return wrappedControl.getCandidateLocales(baseName, locale);
		}

		@Override
		public Locale getFallbackLocale(String baseName, Locale locale) {
			return wrappedControl.getFallbackLocale(baseName, locale);
		}

		@Override
		public List<String> getFormats(String baseName) {
			return wrappedControl.getFormats(baseName);
		}

		@Override
		public long getTimeToLive(String baseName, Locale locale) {
			return wrappedControl.getTimeToLive(baseName, locale);
		}

		@Override
		public boolean needsReload(String baseName, Locale locale, String format, ClassLoader classLoader,
			ResourceBundle bundle, long loadTime) {
			return wrappedControl.needsReload(baseName, locale, format, classLoader, bundle, loadTime);
		}

		@Override
		public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader classLoader,
			boolean reload) throws IllegalAccessException, InstantiationException, IOException {

			ResourceBundle resourceBundle = wrappedControl.newBundle(baseName, locale, format, classLoader, reload);

			if ((resourceBundle == null) && !bundleClassLoader.equals(classLoader)) {
				resourceBundle = wrappedControl.newBundle(baseName, locale, format, bundleClassLoader, reload);
			}

			return resourceBundle;
		}

		@Override
		public String toBundleName(String baseName, Locale locale) {
			return wrappedControl.toBundleName(baseName, locale);
		}
	}
}
