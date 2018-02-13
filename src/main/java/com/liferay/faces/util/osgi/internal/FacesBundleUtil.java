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

import java.net.URL;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;


/**
 * @author  Kyle Stiemann
 */
public final class FacesBundleUtil {

	// Public Constants
	public static final Long CURRENT_WAB_KEY = Long.MIN_VALUE;
	public static final Long MOJARRA_KEY = CURRENT_WAB_KEY + 1;
	public static final Long LIFERAY_FACES_UTIL_KEY = MOJARRA_KEY + 1;
	public static final Long PRIMEFACES_KEY = LIFERAY_FACES_UTIL_KEY + 1;
	public static final Long LIFERAY_FACES_BRIDGE_API_KEY = PRIMEFACES_KEY + 1;
	public static final Long LIFERAY_FACES_BRIDGE_IMPL_KEY = LIFERAY_FACES_BRIDGE_API_KEY + 1;
	public static final Long LIFERAY_FACES_BRIDGE_EXT_KEY = LIFERAY_FACES_BRIDGE_IMPL_KEY + 1;
	public static final Long LIFERAY_FACES_CLAY_KEY = LIFERAY_FACES_BRIDGE_EXT_KEY + 1;
	public static final Long LIFERAY_FACES_PORTAL_KEY = LIFERAY_FACES_CLAY_KEY + 1;
	public static final Long LIFERAY_FACES_ALLOY_KEY = LIFERAY_FACES_PORTAL_KEY + 1;
	public static final boolean LIFERAY_FACES_OSGI_WEAVER_DETECTED;

	// Private Constants
	private static final boolean OSGI_ENVIRONMENT_DETECTED;
	private static final Long OSGI_FRAMEWORK_BUNDLE_ID = 0L;
	private static final String MOJARRA_SYMBOLIC_NAME = "org.glassfish.javax.faces";
	private static final String PRIMEFACES_SYMBOLIC_NAME = "org.primefaces";

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

		boolean osgiEnvironmentDetected = false;
		boolean osgiWeaverDetected = false;

		if (frameworkUtilDetected) {

			Bundle currentBundle = FrameworkUtil.getBundle(FacesBundleUtil.class);

			if (currentBundle != null) {

				osgiEnvironmentDetected = true;

				BundleContext bundleContext = currentBundle.getBundleContext();

				if (bundleContext != null) {

					Bundle[] bundles = bundleContext.getBundles();

					if (bundles != null) {

						for (Bundle bundle : bundles) {

							String symbolicName = bundle.getSymbolicName();

							if ("com.liferay.faces.osgi.weaver".equals(symbolicName)) {

								osgiWeaverDetected = true;

								break;
							}
						}
					}
				}
			}
		}

		OSGI_ENVIRONMENT_DETECTED = osgiEnvironmentDetected;
		LIFERAY_FACES_OSGI_WEAVER_DETECTED = osgiWeaverDetected;
	}

	private FacesBundleUtil() {
		throw new AssertionError();
	}

	public static Map<Long, Bundle> getFacesBundles(Object context) {

		Map<Long, Bundle> facesBundles = null;

		if (OSGI_ENVIRONMENT_DETECTED) {

			facesBundles = (Map<Long, Bundle>) getServletContextAttribute(context, FacesBundleUtil.class.getName());

			if (facesBundles == null) {

				Bundle wabBundle = getCurrentFacesWab(context);

				if (wabBundle != null) {

					// TreeMap is used along with negative keys for Mojarra, Liferay Faces, and Primefaces bundles to
					// ensure that bundles explicitly related to JSF appear first when iterating over the bundles.
					facesBundles = new TreeMap<Long, Bundle>();
					facesBundles.put(CURRENT_WAB_KEY, wabBundle);

					// If the WAB's dependencies are not contained in the WAB's WEB-INF/lib, find all the WAB's
					// dependencies and return them as well.
					if (!isCurrentBundleThickWab()) {
						addRequiredBundlesRecurse(facesBundles, wabBundle);
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

	public static ClassLoader getFacesBundleWiringClassLoader(Bundle facesBundle) {

		ClassLoader classLoader = null;

		if (facesBundle != null) {

			BundleWiring bundleWiring = facesBundle.adapt(BundleWiring.class);

			if (bundleWiring != null) {
				classLoader = bundleWiring.getClassLoader();
			}
		}

		return classLoader;
	}

	public static boolean isCurrentWarThinWab() {
		return OSGI_ENVIRONMENT_DETECTED && !isCurrentBundleThickWab();
	}

	public static boolean shouldLoadClassWithBundle(String classFilePath, Long bundleKey, Bundle bundle) {

		URL classFileURL;
		boolean isBundleCurrentFacesWab = CURRENT_WAB_KEY.equals(bundleKey);

		// If the current bundle is the current Faces WAB search the entire class path for the class file.
		if (isBundleCurrentFacesWab) {
			classFileURL = bundle.getResource(classFilePath);
		}

		// Otherwise, the current bundle is not the current Faces WAB. Instead of searching the entire class path of
		// the bundle (like above with the current Faces WAB), only search the inside of the bundle for the
		// file. If a bundle uses an overly-broad DynamicImport-Package header, unnecessary and even erroneous
		// classes can be loaded by that bundle. For example, some Liferay bundles use headers like
		// "DynamicImport-Package:com.liferay.*" which will allow that bundle to load "com.liferay.portal.*"
		// packages thus causing Liferay Faces Portal to be detected for Faces WABs which do not actually rely on
		// Liferay Faces Portal. This can cause portlets such as the JSF Showcase portlet to detect Liferay Faces
		// Portal, Liferay Faces Alloy, and Liferay Faces Clay unnecessarily, which causes bugs in the JSF
		// Showcase. Similarly, it's possible (though unlikely) that PrimeFaces classes could be accessible to a
		// bundle (if the bundle uses an extremely broad header like "DynamicImport-Package:org.*") and if the
		// classes are allowed to be loaded, they could completely change the h:head renderer and add unnecessary
		// front-end resources to every page causing performance issues and other bugs. Therefore it is necesary
		// to ensure that for every bundle that is not the current Faces WAB, only classes that are
		// actually contained within the bundle are loaded by that bundle.
		else {
			classFileURL = bundle.getEntry(classFilePath);
		}

		return classFileURL != null;
	}

	private static void addRequiredBundlesRecurse(Map<Long, Bundle> facesBundles, Bundle bundle) {

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		if (bundleWiring != null) {

			List<BundleWire> bundleWires = bundleWiring.getRequiredWires(BundleRevision.PACKAGE_NAMESPACE);

			if (bundleWires != null) {

				for (BundleWire bundleWire : bundleWires) {

					BundleRevision provider = bundleWire.getProvider();

					if (provider == null) {
						continue;
					}

					Bundle providerBundle = provider.getBundle();
					Long key = providerBundle.getBundleId();

					if (OSGI_FRAMEWORK_BUNDLE_ID.equals(key)) {
						continue;
					}

					String symbolicName = providerBundle.getSymbolicName();

					if (symbolicName.equals(MOJARRA_SYMBOLIC_NAME)) {
						key = MOJARRA_KEY;
					}
					else if (isLiferayFacesBundle(symbolicName, "util")) {
						key = LIFERAY_FACES_UTIL_KEY;
					}
					else if (symbolicName.equals(PRIMEFACES_SYMBOLIC_NAME)) {
						key = PRIMEFACES_KEY;
					}
					else if (isBridgeBundle(symbolicName, "api")) {
						key = LIFERAY_FACES_BRIDGE_API_KEY;
					}
					else if (isBridgeBundle(symbolicName, "impl")) {
						key = LIFERAY_FACES_BRIDGE_IMPL_KEY;
					}
					else if (isBridgeBundle(symbolicName, "ext")) {
						key = LIFERAY_FACES_BRIDGE_EXT_KEY;
					}
					else if (isLiferayFacesBundle(symbolicName, "clay")) {
						key = LIFERAY_FACES_CLAY_KEY;
					}
					else if (isLiferayFacesBundle(symbolicName, "portal")) {
						key = LIFERAY_FACES_PORTAL_KEY;
					}
					else if (isLiferayFacesBundle(symbolicName, "alloy")) {
						key = LIFERAY_FACES_ALLOY_KEY;
					}

					// If the provider bundle is Mojarra, PrimeFaces, or a Liferay Faces bundle and it was
					// obtained dynamically, skip it. If a bundle uses an overly-broad DynamicImport-Package
					// header, unnecessary and even erroneous bundles can be included in the list of Faces
					// bundles. For example, some Liferay bundles use headers like
					// "DynamicImport-Package:com.liferay.*" which will cause portlets such as the JSF Showcase
					// portlet to include Liferay Faces Portal, Liferay Faces Alloy, and Liferay Faces Clay
					// unnecessarily, which causes bugs in the JSF Showcase. Similarly, if PrimeFaces is added
					// to the list of Faces bundles for a portlet which does not expect it, it can completely
					// change the h:head renderer and add unnecessary front-end resources to every page causing
					// performance issues and other bugs.
					if (facesBundles.containsKey(key) ||
							(isFacesLibraryBundle(key) && isDynamicDependency(bundleWire))) {
						continue;
					}

					facesBundles.put(key, providerBundle);

					if (LIFERAY_FACES_BRIDGE_API_KEY.equals(key)) {

						Map<Long, Bundle> bridgeImplBundles = getBridgeImplBundles(providerBundle);
						Set<Long> bridgeImplBundleKeys = bridgeImplBundles.keySet();

						for (Long bridgeImplBundleKey : bridgeImplBundleKeys) {

							Bundle bridgeImplBundle = bridgeImplBundles.get(bridgeImplBundleKey);
							facesBundles.put(bridgeImplBundleKey, bridgeImplBundle);
							addRequiredBundlesRecurse(facesBundles, bridgeImplBundle);
						}
					}

					addRequiredBundlesRecurse(facesBundles, providerBundle);
				}
			}
		}
	}

	private static Map<Long, Bundle> getBridgeImplBundles(Bundle bridgeAPIBundle) {

		Map<Long, Bundle> bridgeImplBundles = new TreeMap<Long, Bundle>();
		BundleWiring bundleWiring = bridgeAPIBundle.adapt(BundleWiring.class);

		if (bundleWiring != null) {

			List<BundleWire> bundleWires = bundleWiring.getProvidedWires(BundleRevision.PACKAGE_NAMESPACE);

			if (bundleWires != null) {

				for (BundleWire bundleWire : bundleWires) {

					Bundle bundleDependingOnBridgeAPI = bundleWire.getRequirer().getBundle();
					String symbolicName = bundleDependingOnBridgeAPI.getSymbolicName();

					if (isBridgeBundle(symbolicName, "impl")) {
						bridgeImplBundles.put(LIFERAY_FACES_BRIDGE_IMPL_KEY, bundleDependingOnBridgeAPI);
					}
					else if (isBridgeBundle(symbolicName, "ext")) {
						bridgeImplBundles.put(LIFERAY_FACES_BRIDGE_EXT_KEY, bundleDependingOnBridgeAPI);
					}

					if (bridgeImplBundles.containsKey(LIFERAY_FACES_BRIDGE_IMPL_KEY) &&
							bridgeImplBundles.containsKey(LIFERAY_FACES_BRIDGE_EXT_KEY)) {
						break;
					}
				}
			}
		}

		return Collections.unmodifiableMap(bridgeImplBundles);
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

		Object servletContextAttributeValue = null;
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
			throwIllegalContextClassException(context);
		}

		return servletContextAttributeValue;
	}

	private static boolean isBridgeBundle(String symbolicName, String bundleSymbolicNameSuffix) {

		String bridgeBundleSymbolicNameSuffix = "bridge." + bundleSymbolicNameSuffix;

		return isLiferayFacesBundle(symbolicName, bridgeBundleSymbolicNameSuffix);
	}

	private static boolean isCurrentBundleThickWab() {

		// If the current bundle is a WAB, then Liferay Faces Util must be inside the WAB's WEB-INF/lib folder and the
		// WAB is a thick WAB.
		Bundle bundle = FrameworkUtil.getBundle(FacesBundleUtil.class);

		return isWab(bundle);
	}

	private static boolean isDynamicDependency(BundleWire bundleWire) {

		boolean dynamicDependency = false;
		BundleRequirement requirement = bundleWire.getRequirement();

		if (requirement != null) {

			Map<String, String> directives = requirement.getDirectives();
			String resolution = directives.get("resolution");
			dynamicDependency = "dynamic".equalsIgnoreCase(resolution);
		}

		return dynamicDependency;
	}

	private static boolean isFacesLibraryBundle(Long key) {
		return key < 0;
	}

	private static boolean isLiferayFacesBundle(String symbolicName, String bundleSymbolicNameSuffix) {

		String liferayFacesBundleSymbolicName = "com.liferay.faces." + bundleSymbolicNameSuffix;

		return symbolicName.equals(liferayFacesBundleSymbolicName);
	}

	private static boolean isWab(Bundle bundle) {

		String webContextPathHeader = null;

		if (bundle != null) {

			Dictionary<String, String> headers = bundle.getHeaders();
			webContextPathHeader = headers.get("Web-ContextPath");
		}

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
			throwIllegalContextClassException(context);
		}
	}

	private static void throwIllegalContextClassException(Object context) throws IllegalArgumentException {

		String contextClassName = "null";

		if (context != null) {
			contextClassName = context.getClass().getName();
		}

		throw new IllegalArgumentException("context [" + contextClassName + "] is not an instance of " +
			FacesContext.class.getName() + " or " + ExternalContext.class.getName() + " or " +
			ServletContext.class.getName());
	}
}
