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

import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;


/**
 * @author  Kyle Stiemann
 */
public final class InternalFacesBundleUtil {

	// Public Constants
	public static final String MOJARRA_SYMBOLIC_NAME = "org.glassfish.javax.faces";
	public static final String PRIMEFACES_SYMBOLIC_NAME = "org.primefaces";
	public static final boolean OSGI_ENVIRONMENT_DETECTED;
	public static final boolean LIFERAY_FACES_OSGI_WEAVER_DETECTED;

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

			Bundle currentBundle = FrameworkUtil.getBundle(InternalFacesBundleUtil.class);

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

	private InternalFacesBundleUtil() {
		throw new AssertionError();
	}

	public static Bundle getCurrentFacesWab(Object context) {

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

	public static Object getServletContextAttribute(Object context, String servletContextAttributeName) {

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

	public static void throwIllegalContextClassException(Object context) throws IllegalArgumentException {

		String contextClassName = "null";

		if (context != null) {
			contextClassName = context.getClass().getName();
		}

		throw new IllegalArgumentException("context [" + contextClassName + "] is not an instance of " +
			FacesContext.class.getName() + " or " + ExternalContext.class.getName() + " or " +
			ServletContext.class.getName());
	}
}
