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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;


/**
 * This static utility is designed to replace calls to class loading methods such as {@link
 * Class#forName(java.lang.String)}, {@link Class#forName(java.lang.String, boolean, java.lang.ClassLoader)}, and {@link
 * ClassLoader#loadClass(java.lang.String)}. This utility is designed to be used in conjunction with the Liferay Faces
 * OSGi Weaver which replaces calls to the aforementioned methods with appropriate calls to this utility's methods at
 * the bytecode level (however, code authors are free to call this utility's methods directly as well).
 *
 * @author  Kyle Stiemann
 */
public final class OSGiClassLoaderUtil {

	private OSGiClassLoaderUtil() {
		throw new AssertionError();
	}

	/**
	 * This method is intended to replace {@link Class#forName(java.lang.String)} in an OSGi environment, so in
	 * accordance with the <code>Class.forName()</code> JavaDoc, this method obtains the {@link ClassLoader} of the
	 * calling class and calls through to {@link #classForName(java.lang.String, boolean,
	 * javax.faces.context.FacesContext, java.lang.ClassLoader)} passing the class name, <code>true</code>, the {@link
	 * ServletContext}, and the ClassLoader obtained from the calling class.
	 *
	 * @param   className       The name of the class to be obtained. For more information, see {@link
	 *                          Class#forName(java.lang.String)}.
	 * @param   servletContext  The {@link ServletContext} of the current WAB or null if the ServletContext cannot be
	 *                          obtained.
	 * @param   callingClass    The calling class which will be used to obtain the ClassLoader which the caller wished
	 *                          to use to load the class. In static contexts pass MyClass.class. In non-static contexts
	 *                          pass the return value of this.getClass().
	 *
	 * @return
	 *
	 * @throws  ClassNotFoundException
	 */
	public static Class<?> classForName(String className, ServletContext servletContext, Class<?> callingClass)
		throws ClassNotFoundException {

		ClassLoader classLoader = callingClass.getClassLoader();

		return getClass(className, true, servletContext, classLoader);
	}

	/**
	 * This method is intended to replace {@link Class#forName(java.lang.String)} in an OSGi environment, so in
	 * accordance with the <code>Class.forName()</code> JavaDoc, this method obtains the {@link ClassLoader} of the
	 * calling class and calls through to {@link #classForName(java.lang.String, boolean,
	 * javax.faces.context.FacesContext, java.lang.ClassLoader)} passing the class name, <code>true</code>, the {@link
	 * FacesContext}, and the ClassLoader obtained from the calling class.
	 *
	 * @see     #classForName(java.lang.String, boolean, javax.faces.context.FacesContext, java.lang.ClassLoader)
	 *
	 * @param   className     The name of the class to be obtained. For more information, see {@link
	 *                        Class#forName(java.lang.String)}.
	 * @param   facesContext  The {@link FacesContext} of the current WAB (which can be obtained from {@link
	 *                        FacesContext#getCurrentInstance()}) or null if the FacesContext hasn't been initialized
	 *                        yet.
	 * @param   callingClass  The calling class which will be used to obtain the ClassLoader which the caller wished to
	 *                        use to load the class. In static contexts pass MyClass.class. In non-static contexts pass
	 *                        the return value of this.getClass().
	 *
	 * @return
	 *
	 * @throws  ClassNotFoundException
	 */
	public static Class<?> classForName(String className, FacesContext facesContext, Class<?> callingClass)
		throws ClassNotFoundException {

		ClassLoader classLoader = callingClass.getClassLoader();

		return classForName(className, true, facesContext, classLoader);
	}

	/**
	 * This method is intended to replace {@link Class#forName(java.lang.String, boolean, java.lang.ClassLoader)} in an
	 * OSGi environment. This method attempts to load the named class by iterating over the list of OSGi bundles
	 * returned by {@link FacesBundleUtil#getFacesBundles(java.lang.Object)} and checking if the bundle's ClassLoader
	 * can load the class (using {@link Class#forName(java.lang.String, boolean, java.lang.ClassLoader)}). If the class
	 * cannot be loaded by any bundle, the suggested ClassLoader is used in a final attempt to load the class.
	 *
	 * @param   className             The name of the class to be obtained. For more information, see {@link
	 *                                Class#forName(java.lang.String, boolean, java.lang.ClassLoader)}.
	 * @param   initialize            Determines whether the class should be initialized before it is returned. For more
	 *                                information, see {@link Class#forName(java.lang.String, boolean,
	 *                                java.lang.ClassLoader). @paramsuggestedClassLoaderThe ClassLoader which the caller
	 *                                wished to use to load the class. This is ignored unless all relevant OSGi bundles
	 *                                fail to load the class in which case it will be used in a final attempt to load
	 *                                the class. For more information, see {@link Class#forName(java.lang.String,
	 *                                boolean, java.lang.ClassLoader)}.
	 * @param   facesContext          The {@link FacesContext} of the current WAB (which can be obtained from {@link
	 *                                FacesContext#getCurrentInstance()}) or null if the FacesContext hasn't been
	 *                                initialized yet.
	 * @param   suggestedClassLoader  The ClassLoader which the caller wished to use to load the class. This is ignored
	 *                                unless all relevant OSGi bundles fail to load the class in which case it will be
	 *                                used in a final attempt to load the class. For more information, see {@link
	 *                                Class#forName(java.lang.String, boolean, java.lang.ClassLoader)}.
	 *
	 * @return
	 *
	 * @throws  ClassNotFoundException
	 */
	public static Class<?> classForName(String className, boolean initialize, FacesContext facesContext,
		ClassLoader suggestedClassLoader) throws ClassNotFoundException {
		return getClass(className, initialize, facesContext, suggestedClassLoader);
	}

	public static URL getResource(String name, FacesContext facesContext, ClassLoader suggestedClassLoader) {

		URL resourceURL = null;

		if (FacesBundleUtil.isCurrentWarThinWab() && (facesContext != null)) {

			Collection<Bundle> facesBundles = FacesBundleUtil.getFacesBundles(facesContext);

			if (!facesBundles.isEmpty()) {

				for (Bundle facesBundle : facesBundles) {

					resourceURL = facesBundle.getResource(name);

					if (resourceURL != null) {
						break;
					}
				}
			}
		}

		if (resourceURL == null) {
			resourceURL = suggestedClassLoader.getResource(name);
		}

		return resourceURL;
	}

	public static InputStream getResourceAsStream(String name, FacesContext facesContext,
		ClassLoader suggestedClassLoader) {

		InputStream inputStream = null;

		if (FacesBundleUtil.isCurrentWarThinWab() && (facesContext != null)) {

			Collection<Bundle> facesBundles = FacesBundleUtil.getFacesBundles(facesContext);

			if (!facesBundles.isEmpty()) {

				for (Bundle facesBundle : facesBundles) {

					ClassLoader classLoader = getFacesBundleWiringClassLoader(facesBundle);

					if (classLoader == null) {
						continue;
					}

					inputStream = classLoader.getResourceAsStream(name);

					if (inputStream != null) {
						break;
					}
				}
			}
		}

		if (inputStream == null) {
			inputStream = suggestedClassLoader.getResourceAsStream(name);
		}

		return inputStream;
	}

	public static Enumeration<URL> getResources(String name, FacesContext facesContext,
		ClassLoader suggestedClassLoader) throws IOException {

		Set<URL> resourceURLs = new HashSet<URL>();

		if (FacesBundleUtil.isCurrentWarThinWab() && (facesContext != null)) {

			Collection<Bundle> facesBundles = FacesBundleUtil.getFacesBundles(facesContext);

			if (!facesBundles.isEmpty()) {

				for (Bundle facesBundle : facesBundles) {

					try {

						Enumeration<URL> facesBundleResourceURLs = facesBundle.getResources(name);
						addAllEnumerationElementsToSet(facesBundleResourceURLs, resourceURLs);
					}
					catch (IOException e) {
						// Do nothing.
					}
				}
			}
		}

		Enumeration<URL> suggestedResourceURLs = suggestedClassLoader.getResources(name);
		addAllEnumerationElementsToSet(suggestedResourceURLs, resourceURLs);

		return Collections.enumeration(resourceURLs);
	}

	/**
	 * This method is intended to replace {@link ClassLoader#loadClass(java.lang.String)} in an OSGi environment. This
	 * method attempts to load the named class by iterating over the list of OSGi bundles returned by {@link
	 * FacesBundleUtil#getFacesBundles(java.lang.Object)} and checking if the bundle's ClassLoader can load the class
	 * (using {@link ClassLoader#loadClass(java.lang.String)}). If the class cannot be loaded by any bundle, the
	 * suggested ClassLoader is used in a final attempt to load the class.
	 *
	 * @param   className             The name of the class to be obtained. For more information, see {@link
	 *                                ClassLoader#loadClass(java.lang.String)}.
	 * @param   facesContext          The {@link FacesContext} of the current WAB (which can be obtained from {@link
	 *                                FacesContext#getCurrentInstance()}) or null if the FacesContext hasn't been
	 *                                initialized yet.
	 * @param   suggestedClassLoader  The ClassLoader which the caller wished to use to load the class. This is ignored
	 *                                unless all relevant OSGi bundles fail to load the class in which case it will be
	 *                                used in a final attempt to load the class. For more information, see {@link
	 *                                ClassLoader#loadClass(java.lang.String)}.
	 *
	 * @return
	 *
	 * @throws  ClassNotFoundException
	 */
	public static Class<?> loadClass(String className, FacesContext facesContext, ClassLoader suggestedClassLoader)
		throws ClassNotFoundException {
		return getClass(className, null, facesContext, suggestedClassLoader);
	}

	private static void addAllEnumerationElementsToSet(Enumeration<URL> urlsToAdd, Set<URL> urls) {

		if (urlsToAdd != null) {

			List<URL> facesBundleResourceURLs = Collections.list(urlsToAdd);
			urls.addAll(facesBundleResourceURLs);
		}
	}

	private static Class<?> getClass(String name, Boolean initialize, Bundle bundle) {

		ClassLoader classLoader = getFacesBundleWiringClassLoader(bundle);
		Class<?> clazz = null;

		if (classLoader != null) {

			try {
				clazz = getClass(name, initialize, classLoader);
			}
			catch (ClassNotFoundException e) {
				// no-op
			}
		}

		return clazz;
	}

	private static Class<?> getClass(String name, Boolean initialize, ClassLoader classLoader)
		throws ClassNotFoundException {

		Class<?> clazz;

		if (initialize != null) {
			clazz = Class.forName(name, initialize, classLoader);
		}
		else {
			clazz = classLoader.loadClass(name);
		}

		return clazz;
	}

	private static Class<?> getClass(String className, Boolean initialize, Object context,
		ClassLoader suggestedClassLoader) throws ClassNotFoundException {

		Class<?> clazz = null;

		if (FacesBundleUtil.isCurrentWarThinWab() && (context != null)) {

			Map<Long, Bundle> facesBundles = FacesBundleUtil.getFacesBundlesUsingServletContext(context);

			if (!facesBundles.isEmpty()) {

				if (className.startsWith("com.sun.faces") || className.startsWith("javax.faces")) {

					Bundle bundle = facesBundles.get(FacesBundleUtil.MOJARRA_KEY);
					clazz = getClass(className, initialize, bundle);
				}
				else if (className.startsWith("com.liferay.faces.util")) {

					Bundle bundle = facesBundles.get(FacesBundleUtil.LIFERAY_FACES_UTIL_KEY);
					clazz = getClass(className, initialize, bundle);
				}
				else if (className.startsWith("org.primefaces")) {

					Bundle bundle = facesBundles.get(FacesBundleUtil.PRIMEFACES_KEY);
					clazz = getClass(className, initialize, bundle);
				}
				else if (className.startsWith("javax.portlet.faces")) {

					Bundle bundle = facesBundles.get(FacesBundleUtil.LIFERAY_FACES_BRIDGE_API_KEY);
					clazz = getClass(className, initialize, bundle);
				}
				else if (className.startsWith("com.liferay.faces.bridge.ext")) {

					Bundle bundle = facesBundles.get(FacesBundleUtil.LIFERAY_FACES_BRIDGE_EXT_KEY);
					clazz = getClass(className, initialize, bundle);
				}
				else if (className.startsWith("com.liferay.faces.bridge") ||
						className.startsWith("com.liferay.faces.portlet")) {

					if (!className.contains(".internal.")) {

						Bundle bundle = facesBundles.get(FacesBundleUtil.LIFERAY_FACES_BRIDGE_API_KEY);
						clazz = getClass(className, initialize, bundle);
					}

					if (clazz == null) {

						Bundle bundle = facesBundles.get(FacesBundleUtil.LIFERAY_FACES_BRIDGE_IMPL_KEY);
						clazz = getClass(className, initialize, bundle);
					}
				}
				else if (className.startsWith("com.liferay.faces.clay")) {

					Bundle bundle = facesBundles.get(FacesBundleUtil.LIFERAY_FACES_CLAY_KEY);
					clazz = getClass(className, initialize, bundle);
				}
				else if (className.startsWith("com.liferay.faces.portal")) {

					Bundle bundle = facesBundles.get(FacesBundleUtil.LIFERAY_FACES_PORTAL_KEY);
					clazz = getClass(className, initialize, bundle);
				}
				else if (className.startsWith("com.liferay.faces.alloy")) {

					Bundle bundle = facesBundles.get(FacesBundleUtil.LIFERAY_FACES_ALLOY_KEY);
					clazz = getClass(className, initialize, bundle);
				}

				if (clazz == null) {

					Collection<Bundle> bundles = facesBundles.values();

					for (Bundle bundle : bundles) {

						if (!isClassFileInBundle(className, bundle)) {
							continue;
						}

						clazz = getClass(className, initialize, bundle);

						if (clazz != null) {
							break;
						}
					}
				}
			}
		}

		// If all else fails, try to do what the code originally intended to do.
		if (clazz == null) {
			clazz = getClass(className, initialize, suggestedClassLoader);
		}

		return clazz;
	}

	private static ClassLoader getFacesBundleWiringClassLoader(Bundle facesBundle) {

		ClassLoader classLoader = null;

		if (facesBundle != null) {

			BundleWiring bundleWiring = facesBundle.adapt(BundleWiring.class);

			if (bundleWiring != null) {
				classLoader = bundleWiring.getClassLoader();
			}
		}

		return classLoader;
	}

	private static boolean isClassFileInBundle(String className, Bundle bundle) {

		String classFilePath = "/" + className.replace(".", "/") + ".class";

		// Use bundle.getEntry() to ensure that only the bundle is searched (and not the entire classpath).
		URL classFileURL = bundle.getEntry(classFilePath);

		return classFileURL != null;
	}
}
