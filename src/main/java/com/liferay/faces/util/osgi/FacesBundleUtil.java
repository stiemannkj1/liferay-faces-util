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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

import com.liferay.faces.util.osgi.internal.InternalFacesBundleUtil;
import org.osgi.framework.wiring.BundleRequirement;


/**
 * @author  Kyle Stiemann
 */
public final class FacesBundleUtil {

	// Package-Private Constants
	/* package-private */ static final long CURRENT_WAB_KEY = Long.MIN_VALUE;
	/* package-private */ static final long MOJARRA_KEY = CURRENT_WAB_KEY + 1;
	/* package-private */ static final long LIFERAY_FACES_UTIL_KEY = MOJARRA_KEY + 1;
	/* package-private */ static final long PRIMEFACES_KEY = LIFERAY_FACES_UTIL_KEY + 1;
	/* package-private */ static final long LIFERAY_FACES_BRIDGE_API_KEY = PRIMEFACES_KEY + 1;
	/* package-private */ static final long LIFERAY_FACES_BRIDGE_IMPL_KEY = LIFERAY_FACES_BRIDGE_API_KEY + 1;
	/* package-private */ static final long LIFERAY_FACES_BRIDGE_EXT_KEY = LIFERAY_FACES_BRIDGE_IMPL_KEY + 1;
	/* package-private */ static final long LIFERAY_FACES_CLAY_KEY = LIFERAY_FACES_BRIDGE_EXT_KEY + 1;
	/* package-private */ static final long LIFERAY_FACES_PORTAL_KEY = LIFERAY_FACES_CLAY_KEY + 1;
	/* package-private */ static final long LIFERAY_FACES_ALLOY_KEY = LIFERAY_FACES_PORTAL_KEY + 1;

	private FacesBundleUtil() {
		throw new AssertionError();
	}

	public static Collection<Bundle> getFacesBundles(ServletContext servletContext) {

		if (servletContext == null) {
			throw new NullPointerException("ServletContext is null, so FacesBundles cannot be obtained.");
		}

		Map<Long, Bundle> facesBundles = getFacesBundlesUsingServletContext(servletContext);

		return facesBundles.values();
	}

	public static Collection<Bundle> getFacesBundles(FacesContext facesContext) {

		if (facesContext == null) {
			throw new NullPointerException("FacesContext is null, so FacesBundles cannot be obtained.");
		}

		Map<Long, Bundle> facesBundles = getFacesBundlesUsingServletContext(facesContext);

		return facesBundles.values();
	}

	public static boolean isCurrentWarThinWab() {
		return InternalFacesBundleUtil.OSGI_ENVIRONMENT_DETECTED && !isCurrentBundleThickWab();
	}

	/* package-private */ static Map<Long, Bundle> getFacesBundlesUsingServletContext(Object context) {

		Map<Long, Bundle> facesBundles = null;

		if (InternalFacesBundleUtil.OSGI_ENVIRONMENT_DETECTED) {

			facesBundles = (Map<Long, Bundle>) InternalFacesBundleUtil.getServletContextAttribute(context,
					FacesBundleUtil.class.getName());

			if (facesBundles == null) {

				Bundle wabBundle = InternalFacesBundleUtil.getCurrentFacesWab(context);

				if (wabBundle != null) {

					// TreeMap is used along with negative keys for Mojarra, Liferay Faces, and Primefaces bundles to
					// ensure that bundles explicitly related to JSF appear first when iterating over the bundles.
					facesBundles = new TreeMap<Long, Bundle>();
					facesBundles.put(CURRENT_WAB_KEY, wabBundle);

					// If the WAB's dependencies are not contained in the WAB's WEB-INF/lib, find all the WAB's
					// dependencies and return them as well.
					if (!FacesBundleUtil.isCurrentBundleThickWab()) {
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

	private static void addRequiredBundlesRecurse(Map<Long, Bundle> facesBundles, Bundle bundle) {

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		if (bundleWiring != null) {

			List<BundleWire> bundleWires = bundleWiring.getRequiredWires(BundleRevision.PACKAGE_NAMESPACE);

			if (bundleWires != null) {

				Dictionary<String, String> headers = bundle.getHeaders();
				String importPackageHeader = headers.get("Import-Package");
				String dynamicImportPackageHeader = headers.get("DynamicImport-Package");

				for (BundleWire bundleWire : bundleWires) {

					BundleRevision provider = bundleWire.getProvider();

					if (provider != null) {

						Bundle providerBundle = provider.getBundle();
						long key = providerBundle.getBundleId();

						if (key != 0) {
						
							String symbolicName = providerBundle.getSymbolicName();

							if (symbolicName.equals(InternalFacesBundleUtil.MOJARRA_SYMBOLIC_NAME)) {

								if (bundleDynamicallyDependsOnMojarra(importPackageHeader,
											dynamicImportPackageHeader)) {
									continue;
								}

								key = MOJARRA_KEY;
							}
							else if (isLiferayFacesBundle(symbolicName, "util")) {

								if (bundleDynamicallyDependsOnLiferayFaces(importPackageHeader,
											dynamicImportPackageHeader, "util")) {
									continue;
								}

								key = LIFERAY_FACES_UTIL_KEY;
							}
							else if (symbolicName.equals(InternalFacesBundleUtil.PRIMEFACES_SYMBOLIC_NAME)) {

								if (bundleDynamicallyDependsOnPrimeFaces(importPackageHeader,
											dynamicImportPackageHeader)) {
									continue;
								}

								key = PRIMEFACES_KEY;
							}
							else if (isBridgeBundle(symbolicName, "api")) {

								if (bundleDynamicallyDependsOnLiferayFaces(importPackageHeader,
											dynamicImportPackageHeader, "bridge")) {
									continue;
								}

								key = LIFERAY_FACES_BRIDGE_API_KEY;
							}
							else if (isBridgeBundle(symbolicName, "impl")) {

								// All Bridge Impl packages are private, so they cannot be imported (dynamically or
								// statically) and Bridge Impl cannot be a dependency of a bundle.
								key = LIFERAY_FACES_BRIDGE_IMPL_KEY;
							}
							else if (isBridgeBundle(symbolicName, "ext")) {

								if (bundleDynamicallyDependsOnLiferayFaces(importPackageHeader,
											dynamicImportPackageHeader, "bridge.ext")) {
									continue;
								}

								key = LIFERAY_FACES_BRIDGE_EXT_KEY;
							}
							else if (isLiferayFacesBundle(symbolicName, "clay")) {

								if (bundleDynamicallyDependsOnLiferayFaces(importPackageHeader,
											dynamicImportPackageHeader, "clay")) {
									continue;
								}

								key = LIFERAY_FACES_CLAY_KEY;
							}
							else if (isLiferayFacesBundle(symbolicName, "portal")) {

								if (bundleDynamicallyDependsOnLiferayFaces(importPackageHeader,
											dynamicImportPackageHeader, "portal")) {
									continue;
								}

								key = LIFERAY_FACES_PORTAL_KEY;
							}
							else if (isLiferayFacesBundle(symbolicName, "alloy")) {

								if (bundleDynamicallyDependsOnLiferayFaces(importPackageHeader,
											dynamicImportPackageHeader, "alloy")) {
									continue;
								}

								key = LIFERAY_FACES_ALLOY_KEY;
							}

							if (!facesBundles.containsValue(providerBundle)) {

								facesBundles.put(key, providerBundle);

								if (key == LIFERAY_FACES_BRIDGE_API_KEY) {

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
			}
		}
	}

	/**
	 * Returns true if a Liferay Faces bundle package is imported dynamically by an overly broad <code>
	 * DynamicImport-Package</code> header value (such as <code>*</code>, <code>com.*</code>, <code>
	 * com.liferay.*</code>, or <code>com.liferay.faces.*</code>) and <strong>zero</strong> Liferay Faces bundle
	 * packages are imported statically.
	 */
	private static boolean bundleDynamicallyDependsOnLiferayFaces(String importPackageHeader,
		String dynamicImportPackageHeader, String packageSuffix) {
		return !isEmpty(dynamicImportPackageHeader) &&

			// Ensure that the Liferay Faces bundle's packages aren't statically imported.
			!bundleImportsPackage(importPackageHeader, "com.liferay.faces." + packageSuffix) &&
			(bundleImportsPackage(dynamicImportPackageHeader, "*") ||
				bundleImportsPackage(dynamicImportPackageHeader, "com.*") ||
				bundleImportsPackage(dynamicImportPackageHeader, "com.liferay.*") ||
				bundleImportsPackage(dynamicImportPackageHeader, "com.liferay.faces.*"));
	}

	/**
	 * Returns true if a Mojarra package is imported dynamically by an overly broad <code>DynamicImport-Package</code>
	 * header value (such as <code>*</code>, <code>javax.*</code>, <code>com.*</code>, or <code>com.sun.*</code>) and
	 * <strong>zero</strong> Mojarra packages are imported statically.
	 */
	private static boolean bundleDynamicallyDependsOnMojarra(String importPackageHeader,
		String dynamicImportPackageHeader) {
		return !isEmpty(dynamicImportPackageHeader) &&

			// Ensure that Mojarra's packages aren't statically imported.
			!(bundleImportsPackage(importPackageHeader, "javax.faces") ||
				bundleImportsPackage(importPackageHeader, "com.sun.faces")) &&
			(bundleImportsPackage(dynamicImportPackageHeader, "*") ||
				bundleImportsPackage(dynamicImportPackageHeader, "javax.*") ||
				bundleImportsPackage(dynamicImportPackageHeader, "com.*") ||
				bundleImportsPackage(dynamicImportPackageHeader, "com.sun.*"));
	}

	/**
	 * Returns true if a PrimeFaces package is imported dynamically by an overly broad <code>
	 * DynamicImport-Package</code> header value (such as <code>*</code> or <code>org.*</code>) and <strong>
	 * zero</strong> Mojarra packages are imported statically.
	 */
	private static boolean bundleDynamicallyDependsOnPrimeFaces(String importPackageHeader,
		String dynamicImportPackageHeader) {

		return !isEmpty(dynamicImportPackageHeader) &&

			// Ensure that PrimeFaces's packages aren't statically imported.
			!bundleImportsPackage(importPackageHeader, "org.primefaces") &&
			(bundleImportsPackage(dynamicImportPackageHeader, "*") ||
				bundleImportsPackage(dynamicImportPackageHeader, "org.*"));
	}

	/**
	 * Returns true if the package pattern is represented in the <code>DynamicImport-Package</code> header.
	 */
	private static boolean bundleImportsPackage(String importPackageHeader, String packagePattern) {

		return importPackageHeader.contains(packagePattern) &&
			(importPackageHeader.startsWith(packagePattern) ||
				importPackageHeader.contains("," + packagePattern) ||
				importPackageHeader.contains(" " + packagePattern) ||
				importPackageHeader.contains("\t" + packagePattern) ||
				importPackageHeader.contains("\n" + packagePattern));
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

	private static boolean isEmpty(String string) {
		return ((string == null) || string.equals(""));
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
			InternalFacesBundleUtil.throwIllegalContextClassException(context);
		}
	}
}
