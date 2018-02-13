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
package com.liferay.faces.util.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.osgi.internal.FacesBundleUtil;
import com.liferay.faces.util.osgi.internal.FacesBundlesHandlerBase;
import com.liferay.faces.util.osgi.internal.ResourceBundleControlOSGiFriendlyImpl;
import com.liferay.faces.util.resource.internal.ResourceProviderUtil;


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

	// logger
	private static final Logger logger = LoggerFactory.getLogger(ResourceProviderUtil.class);

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
	 * OSGi environment. This method attempts to load the named class by iterating over the list of OSGi bundles that
	 * the current Faces WAB depends on and checking if the bundle's ClassLoader can load the class (using {@link
	 * Class#forName(java.lang.String, boolean, java.lang.ClassLoader)}). If the class cannot be loaded by any bundle,
	 * the suggested ClassLoader is used in a final attempt to load the class.
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

	/**
	 * TODO
	 *
	 * @param   name
	 * @param   facesContext
	 * @param   suggestedClassLoader
	 *
	 * @return
	 */
	public static URL getResource(String name, FacesContext facesContext, ClassLoader suggestedClassLoader) {

		FacesBundlesHandlerBase<URL> facesBundlesHandler = new FacesBundlesHandlerGetResourceImpl(name);
		URL resourceURL = facesBundlesHandler.handleFacesBundles(facesContext);

		if (resourceURL == null) {
			resourceURL = suggestedClassLoader.getResource(name);
		}

		return resourceURL;
	}

	/**
	 * TODO
	 *
	 * @param   name
	 * @param   facesContext
	 * @param   suggestedClassLoader
	 *
	 * @return
	 */
	public static InputStream getResourceAsStream(String name, FacesContext facesContext,
		ClassLoader suggestedClassLoader) {

		InputStream inputStream = null;
		URL resource = getResource(name, facesContext, suggestedClassLoader);

		try {
			inputStream = resource.openStream();
		}
		catch (IOException e) {
			// Do nothing.
		}

		return inputStream;
	}

	public static ResourceBundle getResourceBundle(String baseName) {
		return ResourceBundle.getBundle(baseName, new ResourceBundleControlOSGiFriendlyImpl(baseName));
	}

	public static ResourceBundle getResourceBundle(String baseName, Locale locale) {
		return ResourceBundle.getBundle(baseName, locale, new ResourceBundleControlOSGiFriendlyImpl(baseName));
	}

	public static ResourceBundle getResourceBundle(String baseName, ResourceBundle.Control control) {
		return ResourceBundle.getBundle(baseName, new ResourceBundleControlOSGiFriendlyImpl(control));
	}

	public static ResourceBundle getResourceBundle(String baseName, Locale locale, ClassLoader classLoader) {
		return ResourceBundle.getBundle(baseName, locale, classLoader,
				new ResourceBundleControlOSGiFriendlyImpl(baseName));
	}

	public static ResourceBundle getResourceBundle(String baseName, Locale locale, ResourceBundle.Control control) {
		return ResourceBundle.getBundle(baseName, locale, new ResourceBundleControlOSGiFriendlyImpl(control));
	}

	public static ResourceBundle getResourceBundle(String baseName, Locale locale, ClassLoader classLoader,
		ResourceBundle.Control control) {
		return ResourceBundle.getBundle(baseName, locale, classLoader,
				new ResourceBundleControlOSGiFriendlyImpl(control));
	}

	/**
	 * TODO
	 *
	 * @param   name
	 * @param   facesContext
	 * @param   suggestedClassLoader
	 *
	 * @return
	 *
	 * @throws  IOException
	 */
	public static Enumeration<URL> getResources(String name, FacesContext facesContext,
		ClassLoader suggestedClassLoader) throws IOException {

		FacesBundlesHandlerGetResourcesImpl facesBundlesHandlerGetResourcesImpl =
			new FacesBundlesHandlerGetResourcesImpl(name);
		List<URL> resourceURLs = facesBundlesHandlerGetResourcesImpl.getResourcesFromFacesBundles(facesContext,
				suggestedClassLoader);

		return Collections.enumeration(resourceURLs);
	}

	/**
	 * This method is intended to replace {@link ClassLoader#loadClass(java.lang.String)} in an OSGi environment. This
	 * method attempts to load the named class by iterating over the list of OSGi bundles that the current Faces WAB
	 * depends on and checking if the bundle's ClassLoader can load the class (using {@link
	 * ClassLoader#loadClass(java.lang.String)}). If the class cannot be loaded by any bundle, the suggested ClassLoader
	 * is used in a final attempt to load the class.
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

	private static Class<?> getClass(String name, Boolean initialize, Bundle bundle) {

		ClassLoader classLoader = FacesBundleUtil.getFacesBundleWiringClassLoader(bundle);
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

			Map<Long, Bundle> facesBundles = FacesBundleUtil.getFacesBundles(context);

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

					FacesBundlesHandlerBase<Class<?>> facesBundlesHandler = new FacesBundlesHandlerGetClassImpl(
							className, initialize);
					clazz = facesBundlesHandler.handleFacesBundles(context, FacesBundleUtil.MOJARRA_KEY,
							FacesBundleUtil.LIFERAY_FACES_UTIL_KEY, FacesBundleUtil.PRIMEFACES_KEY,
							FacesBundleUtil.LIFERAY_FACES_BRIDGE_API_KEY, FacesBundleUtil.LIFERAY_FACES_BRIDGE_EXT_KEY,
							FacesBundleUtil.LIFERAY_FACES_BRIDGE_IMPL_KEY, FacesBundleUtil.LIFERAY_FACES_CLAY_KEY,
							FacesBundleUtil.LIFERAY_FACES_PORTAL_KEY, FacesBundleUtil.LIFERAY_FACES_ALLOY_KEY);
				}
			}
		}

		// If all else fails, try to do what the code originally intended to do.
		if (clazz == null) {
			clazz = getClass(className, initialize, suggestedClassLoader);
		}

		return clazz;
	}

	private static final class FacesBundlesHandlerGetClassImpl extends FacesBundlesHandlerBase<Class<?>> {

		// Private Data Members
		private final String className;
		private final String classFilePath;
		private final Boolean initialize;

		public FacesBundlesHandlerGetClassImpl(String className, Boolean initialize) {
			this.className = className;
			this.classFilePath = "/" + className.replace(".", "/") + ".class";
			this.initialize = initialize;
		}

		@Override
		protected Class<?> getInitialReturnValueObject() {
			return null;
		}

		@Override
		protected void handleFacesBundle(Long bundleKey, Bundle bundle,
			ReturnValueReference<Class<?>> returnValueReference) {

			if (FacesBundleUtil.shouldLoadClassWithBundle(classFilePath, bundleKey, bundle)) {
				returnValueReference.set(OSGiClassLoaderUtil.getClass(className, initialize, bundle));
			}
		}

		@Override
		protected boolean skipHandlingRemaingFacesBundles(ReturnValueReference<Class<?>> returnValueReference) {
			return !returnValueReference.isEmpty();
		}
	}

	private static final class FacesBundlesHandlerGetResourceImpl extends FacesBundlesHandlerBase<URL> {

		// Private Data Members
		private final String name;

		public FacesBundlesHandlerGetResourceImpl(String name) {
			this.name = name;
		}

		@Override
		protected URL getInitialReturnValueObject() {
			return null;
		}

		@Override
		protected void handleCurrentFacesWab(Long bundleKey, Bundle currentFacesWab,
			ReturnValueReference<URL> returnValueReference) {
			returnValueReference.set(currentFacesWab.getResource(name));
		}

		@Override
		protected void handleFacesBundle(Long bundleKey, Bundle bundle,
			ReturnValueReference<URL> returnValueReference) {
			returnValueReference.set(bundle.getEntry(name));
		}

		@Override
		protected boolean skipHandlingRemaingFacesBundles(ReturnValueReference<URL> returnValueReference) {
			return !returnValueReference.isEmpty();
		}
	}

	private static final class FacesBundlesHandlerGetResourcesImpl
		extends FacesBundlesHandlerBase<List<FacesBundlesHandlerGetResourcesImpl.URLI>> {

		// Private Final Data Members
		private final String name;

		public FacesBundlesHandlerGetResourcesImpl(String name) {
			this.name = name;
		}

		public List<URL> getResourcesFromFacesBundles(FacesContext facesContext, ClassLoader suggestedClassLoader)
			throws IOException {

			List<URL> resourceURLs = new ArrayList<URL>();
			List<URLI> urlis = handleFacesBundles(facesContext);
			Enumeration<URL> resources = suggestedClassLoader.getResources(name);
			URLI.addAllUniqueURLsToListOfURLI(resources, urlis);

			for (URLI urli : urlis) {
				resourceURLs.add(urli.getURL());
			}

			return Collections.unmodifiableList(resourceURLs);
		}

		@Override
		protected List<URLI> getInitialReturnValueObject() {
			return new ArrayList<URLI>();
		}

		@Override
		protected void handleCurrentFacesWab(Long bundleKey, Bundle currentFacesWab,
			ReturnValueReference<List<URLI>> returnValueReference) {
			handleFacesBundle(bundleKey, currentFacesWab, returnValueReference);
		}

		@Override
		protected void handleFacesBundle(Long bundleKey, Bundle bundle,
			ReturnValueReference<List<URLI>> returnValueReference) {

			Enumeration<URL> resources = null;

			try {

				// Ideally, a method like getEntries() would be called here instead in order to only search the inside
				// of the bundle (getResourcesFromFacesBundles() searches the bundle's classpath which may include files
				// outside of the bundle). However, getEntries() does not exist and getEntry() cannot be used because it
				// will not find all the resources in a fat JAR or WAR (findEntries() also should not be used since it
				// uses special patterns and there is no standards-based methods to escape the special characters).
				resources = bundle.getResources(name);
			}
			catch (IOException e) {

				long bundleId = bundle.getBundleId();
				String symbolicName = bundle.getSymbolicName();
				logger.error(
					"Failed to obtain URLs of resources with path \"{0}\" from Faces WAB with bundle id \"{1}\" and symbolic name \"{2}\" due to the following error:",
					name, bundleId, symbolicName);
				logger.error(e);
			}

			List<URLI> urlis = returnValueReference.get();
			URLI.addAllUniqueURLsToListOfURLI(resources, urlis);
		}

		/**
		 * This class is designed to wrap a URL and provide the ability to check for equality without making network
		 * requests. Unfortunately {@link URL#equals(java.lang.Object)} and {@link URL#hashCode()} may actually make
		 * blocking network requests before returning. For more information see this StackOverflow Q&A:
		 * https://stackoverflow.com/questions/18280818/what-java-library-can-i-use-to-compare-two-urls-for-equality.
		 */
		private static final class URLI {

			// Private Final Data Members
			private final URL url;
			private final URI uri;

			public URLI(URL url) {

				this.url = url;

				URI uri = null;

				try {
					uri = url.toURI();
				}
				catch (URISyntaxException e) {
					// Do nothing.
				}

				this.uri = uri;
			}

			private static void addAllUniqueURLsToListOfURLI(Enumeration<URL> urls, List<URLI> urlis) {

				while ((urls != null) && urls.hasMoreElements()) {

					URL resource = urls.nextElement();
					URLI urli = new URLI(resource);

					if (!urlis.contains(urli)) {
						urlis.add(urli);
					}
				}
			}

			@Override
			public boolean equals(Object object) {

				boolean equals = false;

				if ((object != null) && (object instanceof URLI) && (this.uri != null)) {

					URLI urli = (URLI) object;
					URI uri = urli.getURI();
					equals = this.uri.equals(uri);
				}

				return equals;
			}

			public URI getURI() {
				return uri;
			}

			public URL getURL() {
				return url;
			}

			@Override
			public int hashCode() {

				int hashCode;

				if (uri != null) {
					hashCode = uri.hashCode();
				}
				else {
					hashCode = System.identityHashCode(uri);
				}

				return hashCode;
			}
		}
	}
}
