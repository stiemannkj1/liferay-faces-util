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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.spi.ResourceBundleControlProvider;

import javax.faces.context.FacesContext;

import org.osgi.framework.Bundle;


/**
 * @author  Kyle Stiemann
 */
public class ResourceBundleControlOSGiFriendlyImpl extends ResourceBundle.Control {

	// Private Constants
	private static final List<ResourceBundleControlProvider> RESOURCE_BUNDLE_CONTROL_PROVIDERS;

	static {

		List<ResourceBundleControlProvider> resourceBundleControlProviders =
			new ArrayList<ResourceBundleControlProvider>();
		ServiceLoader<ResourceBundleControlProvider> serviceLoader = ServiceLoader.loadInstalled(
				ResourceBundleControlProvider.class);
		Iterator<ResourceBundleControlProvider> iterator = serviceLoader.iterator();

		while (iterator.hasNext()) {
			resourceBundleControlProviders.add(iterator.next());
		}

		RESOURCE_BUNDLE_CONTROL_PROVIDERS = Collections.unmodifiableList(resourceBundleControlProviders);
	}

	// Private Data Members
	private final ResourceBundle.Control wrappedResourceBundleControl;

	public ResourceBundleControlOSGiFriendlyImpl(ResourceBundle.Control resourceBundleControl) {
		this.wrappedResourceBundleControl = resourceBundleControl;
	}

	public ResourceBundleControlOSGiFriendlyImpl(String baseName) {

		ResourceBundle.Control control = null;

		for (ResourceBundleControlProvider resourceBundleControlProvider : RESOURCE_BUNDLE_CONTROL_PROVIDERS) {

			control = resourceBundleControlProvider.getControl(baseName);

			if (control != null) {
				break;
			}
		}

		wrappedResourceBundleControl = control;
	}

	@Override
	public List<Locale> getCandidateLocales(String baseName, Locale locale) {

		if (getWrapped() == null) {
			return super.getCandidateLocales(baseName, locale);
		}
		else {
			return getWrapped().getCandidateLocales(baseName, locale);
		}
	}

	@Override
	public Locale getFallbackLocale(String baseName, Locale locale) {

		if (getWrapped() == null) {
			return super.getFallbackLocale(baseName, locale);
		}
		else {
			return getWrapped().getFallbackLocale(baseName, locale);
		}
	}

	@Override
	public List<String> getFormats(String baseName) {

		if (getWrapped() == null) {
			return super.getFormats(baseName);
		}
		else {
			return getWrapped().getFormats(baseName);
		}
	}

	@Override
	public long getTimeToLive(String baseName, Locale locale) {

		if (getWrapped() == null) {
			return super.getTimeToLive(baseName, locale);
		}
		else {
			return getWrapped().getTimeToLive(baseName, locale);
		}
	}

	@Override
	public boolean needsReload(String baseName, Locale locale, String format, ClassLoader loader, ResourceBundle bundle,
		long loadTime) {

		if (getWrapped() == null) {
			return super.needsReload(baseName, locale, format, loader, bundle, loadTime);
		}
		else {
			return getWrapped().needsReload(baseName, locale, format, loader, bundle, loadTime);
		}
	}

	@Override
	public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader suggestedClassLoader,
		boolean reload) throws IllegalAccessException, InstantiationException, IOException {

		ResourceBundle resourceBundle = null;

		if (FacesBundleUtil.isCurrentWarThinWab()) {

			String bundleName = toBundleName(baseName, locale);
			String resourceName = toResourceName(bundleName, format.replace("java.", ""));

			FacesBundlesHandlerBase<ClassLoader> facesBundlesHandler =
				new FacesBundlesHandlerGetClassLoaderForResourceImpl(resourceName);
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ClassLoader classLoader = facesBundlesHandler.handleFacesBundles(facesContext);

			if (classLoader != null) {
				resourceBundle = newBundleFromWrappedControl(baseName, locale, format, classLoader, reload);
			}
		}

		if (resourceBundle == null) {
			resourceBundle = newBundleFromWrappedControl(baseName, locale, format, suggestedClassLoader, reload);
		}

		return resourceBundle;
	}

	@Override
	public String toBundleName(String baseName, Locale locale) {

		if (getWrapped() == null) {
			return super.toBundleName(baseName, locale);
		}
		else {
			return getWrapped().toBundleName(baseName, locale);
		}
	}

	private ResourceBundle.Control getWrapped() {
		return wrappedResourceBundleControl;
	}

	private ResourceBundle newBundleFromWrappedControl(String baseName, Locale locale, String format,
		ClassLoader suggestedClassLoader, boolean reload) throws IllegalAccessException, InstantiationException,
		IOException {

		if (getWrapped() == null) {
			return super.newBundle(baseName, locale, format, suggestedClassLoader, reload);
		}
		else {
			return getWrapped().newBundle(baseName, locale, format, suggestedClassLoader, reload);
		}
	}

	public static final class FacesBundlesHandlerGetClassLoaderForResourceImpl
		extends FacesBundlesHandlerBase<ClassLoader> {

		// Private Final Data Members
		private final String resourcName;

		public FacesBundlesHandlerGetClassLoaderForResourceImpl(String resourcName) {
			this.resourcName = resourcName;
		}

		@Override
		protected ClassLoader getInitialReturnValueObject() {
			return null;
		}

		@Override
		protected void handleCurrentFacesWab(Long bundleKey, Bundle currentFacesWab,
			ReturnValueReference<ClassLoader> returnValueReference) {

			URL resource = currentFacesWab.getResource(resourcName);

			if (resource != null) {
				returnValueReference.set(FacesBundleUtil.getFacesBundleWiringClassLoader(currentFacesWab));
			}
		}

		@Override
		protected void handleFacesBundle(Long bundleKey, Bundle bundle,
			ReturnValueReference<ClassLoader> returnValueReference) {

			URL resource = bundle.getEntry(resourcName);

			if (resource != null) {
				returnValueReference.set(FacesBundleUtil.getFacesBundleWiringClassLoader(bundle));
			}
		}

		@Override
		protected boolean skipHandlingRemaingFacesBundles(ReturnValueReference<ClassLoader> returnValueReference) {
			return !returnValueReference.isEmpty();
		}
	}
}
