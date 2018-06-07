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
package com.liferay.faces.util.osgi;

import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;


/**
 * @author  Kyle Stiemann
 */
public final class FacesBundleUtil {

	// Private Constants
	private static final boolean FRAMEWORK_UTIL_DETECTED;

	// Package-Private Constants
	/* package-private */ static final String PRIMEFACES_SYMBOLIC_NAME = "org.primefaces";
	/* package-private */ static final String MOJARRA_SYMBOLIC_NAME = "org.glassfish.javax.faces";

	static {

		boolean frameworkUtilDetected = false;

		try {

			Class.forName("org.osgi.framework.FrameworkUtil");
			frameworkUtilDetected = true;
		}
		catch (Throwable t) {

			if (!((t instanceof NoClassDefFoundError) || (t instanceof ClassNotFoundException))) {

				System.err.println("An unexpected error occurred when attempting to detect OSGi:");
				t.printStackTrace(System.err);
			}
		}

		FRAMEWORK_UTIL_DETECTED = frameworkUtilDetected;
	}

	private FacesBundleUtil() {
		throw new AssertionError();
	}

	public static Collection<Bundle> getFacesBundles(ServletContext servletContext) {

		if (servletContext == null) {
			throw new NullPointerException("ServletContext is null, so FacesBundles cannot be obtained.");
		}

		Map<String, Bundle> facesBundles = getFacesBundlesUsingServletContext(servletContext);

		return facesBundles.values();
	}

	public static Collection<Bundle> getFacesBundles(FacesContext facesContext) {

		if (facesContext == null) {
			throw new NullPointerException("FacesContext is null, so FacesBundles cannot be obtained.");
		}

		Map<String, Bundle> facesBundles = getFacesBundlesUsingServletContext(facesContext);

		return facesBundles.values();
	}

	public static boolean isCurrentWarThinWab() {
		return FRAMEWORK_UTIL_DETECTED && !isCurrentBundleThickWab();
	}

	/* package-private */ static Map<String, Bundle> getFacesBundlesUsingServletContext(Object context) {

		Map<String, Bundle> facesBundles = null;

		if (FRAMEWORK_UTIL_DETECTED) {

			facesBundles = (Map<String, Bundle>) getServletContextAttribute(context, FacesBundleUtil.class.getName());

			if (facesBundles == null) {

				Bundle wabBundle = getCurrentFacesWab(context);

				if (wabBundle != null) {

					// LinkedHashMap is used to ensure that the WAB is the first bundle when iterating over all bundles.
					facesBundles = new LinkedHashMap<String, Bundle>();
					facesBundles.put("currentFacesWab", wabBundle);

					// If the WAB's dependencies are not contained in the WAB's WEB-INF/lib, find all the WAB's
					// dependencies and return them as well.
					if (!FacesBundleUtil.isCurrentBundleThickWab()) {

						addRequiredBundlesRecurse(facesBundles, wabBundle);
						addBridgeImplBundles(facesBundles);
					}

					facesBundles = Collections.unmodifiableMap(facesBundles);
					setServletContextAttribute(context, FacesBundleUtil.class.getName(), facesBundles);
				}
			}
		}

		if (facesBundles == null) {
			facesBundles = Collections.emptyMap();
		}

		return facesBundles;
	}

	private static void addBridgeImplBundles(Map<String, Bundle> facesBundles) {

		Collection<Bundle> bundles = facesBundles.values();

		for (Bundle bundle : bundles) {

			String symbolicName = bundle.getSymbolicName();

			if (isBridgeBundle(symbolicName, "api")) {

				BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
				List<BundleWire> bundleWires = bundleWiring.getProvidedWires(BundleRevision.PACKAGE_NAMESPACE);
				boolean addedBridgeImplBundle = false;
				boolean addedBridgeExtBundle = false;

				for (BundleWire bundleWire : bundleWires) {

					Bundle bundleDependingOnBridgeAPI = bundleWire.getRequirer().getBundle();
					symbolicName = bundleDependingOnBridgeAPI.getSymbolicName();

					if (isBridgeBundle(symbolicName, "impl")) {

						facesBundles.put(symbolicName, bundleDependingOnBridgeAPI);
						addRequiredBundlesRecurse(facesBundles, bundleDependingOnBridgeAPI);
						addedBridgeImplBundle = true;
					}
					else if (isBridgeBundle(symbolicName, "ext")) {

						facesBundles.put(symbolicName, bundleDependingOnBridgeAPI);
						addRequiredBundlesRecurse(facesBundles, bundleDependingOnBridgeAPI);
						addedBridgeExtBundle = true;
					}

					if (addedBridgeImplBundle && addedBridgeExtBundle) {
						break;
					}
				}

				break;
			}
		}
	}

	private static void addRequiredBundlesRecurse(Map<String, Bundle> facesBundles, Bundle bundle) {

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
		List<BundleWire> bundleWires = bundleWiring.getRequiredWires(BundleRevision.PACKAGE_NAMESPACE);

		for (BundleWire bundleWire : bundleWires) {

			bundle = bundleWire.getProvider().getBundle();

			long bundleId = bundle.getBundleId();

			if (!((bundleId == 0) || facesBundles.containsValue(bundle))) {

				String key = Long.toString(bundleId);
				String symbolicName = bundle.getSymbolicName();

				if (symbolicName.startsWith("com.liferay.faces") || MOJARRA_SYMBOLIC_NAME.equals(symbolicName) ||
						PRIMEFACES_SYMBOLIC_NAME.equals(symbolicName)) {
					key = symbolicName;
				}

				facesBundles.put(key, bundle);
				addRequiredBundlesRecurse(facesBundles, bundle);
			}
		}
	}

	private static Bundle getCurrentFacesWab(Object context) {

		BundleContext bundleContext = (BundleContext) getServletContextAttribute(context, "osgi-bundlecontext");
		Bundle bundle;

		try {
			bundle = bundleContext.getBundle();
		}
		catch (IllegalStateException e) {
			bundle = null;
		}

		return bundle;
	}

	private static Object getServletContextAttribute(Object context, String servletContextAttributeName) {

		Object servletContextAttributeValue;
		boolean isFacesContext = context instanceof FacesContext;

		if (isFacesContext || (context instanceof ExternalContext)) {

			ExternalContext externalContext;

			if (isFacesContext) {

				FacesContext facesContext = (FacesContext) context;
				externalContext = facesContext.getExternalContext();
			}
			else {
				externalContext = (ExternalContext) context;
			}

			Map<String, Object> applicationMap = externalContext.getApplicationMap();
			servletContextAttributeValue = applicationMap.get(servletContextAttributeName);
		}
		else if (context instanceof ServletContext) {

			ServletContext servletContext = (ServletContext) context;
			servletContextAttributeValue = servletContext.getAttribute(servletContextAttributeName);
		}
		else {

			String contextClassName = "null";

			if (context != null) {
				contextClassName = context.getClass().getName();
			}

			throw new IllegalArgumentException("context [" + contextClassName + "] is not an instanceof " +
				FacesContext.class.getName() + " or " + ExternalContext.class.getName() + " or " +
				ServletContext.class.getName());
		}

		return servletContextAttributeValue;
	}

	private static boolean isBridgeBundle(String symbolicName, String bundleSymbolicNameSuffix) {

		String bridgeBundleSymbolicName = "com.liferay.faces.bridge." + bundleSymbolicNameSuffix;

		return symbolicName.equals(bridgeBundleSymbolicName);
	}

	private static boolean isCurrentBundleThickWab() {

		// If the current bundle is a WAB, then Liferay Faces Util must be inside the WAB's WEB-INF/lib folder and the
		// WAB is a thick WAB.
		Bundle bundle = FrameworkUtil.getBundle(FacesBundleUtil.class);

		return isWab(bundle);
	}

	private static boolean isWab(Bundle bundle) {

		Dictionary<String, String> headers = bundle.getHeaders();
		String webContextPathHeader = headers.get("Web-ContextPath");

		return webContextPathHeader != null;
	}

	private static void setServletContextAttribute(Object context, String servletContextAttributeName,
		Object servletContextAttributeValue) {

		boolean isFacesContext = context instanceof FacesContext;

		if (isFacesContext || (context instanceof ExternalContext)) {

			ExternalContext externalContext;

			if (isFacesContext) {

				FacesContext facesContext = (FacesContext) context;
				externalContext = facesContext.getExternalContext();
			}
			else {
				externalContext = (ExternalContext) context;
			}

			Map<String, Object> applicationMap = externalContext.getApplicationMap();
			applicationMap.put(servletContextAttributeName, servletContextAttributeValue);
		}
		else if (context instanceof ServletContext) {

			ServletContext servletContext = (ServletContext) context;
			servletContext.setAttribute(servletContextAttributeName, servletContextAttributeValue);
		}
		else {
			throw new IllegalArgumentException("context [" + context.getClass().getName() + "] is not an instanceof " +
				FacesContext.class.getName() + " or " + ExternalContext.class.getName() + " or " +
				ServletContext.class.getName());
		}
	}
}
