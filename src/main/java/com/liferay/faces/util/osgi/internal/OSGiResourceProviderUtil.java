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
package com.liferay.faces.util.osgi.internal;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.osgi.FacesBundleUtil;


/**
 * @author  Kyle Stiemann
 */
public final class OSGiResourceProviderUtil {

	// logger
	private static final Logger logger = LoggerFactory.getLogger(OSGiResourceProviderUtil.class);

	private OSGiResourceProviderUtil() {
		throw new AssertionError();
	}

	public static Collection<URL> getResources(String path, String resourcefilePattern, Bundle bundle) {

		List<URL> resources = new ArrayList<URL>();
		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		if (bundleWiring != null) {

			Collection<String> resourceFilePaths = bundleWiring.listResources(path, resourcefilePattern,
					BundleWiring.LISTRESOURCES_RECURSE);

			for (String resourceFilePath : resourceFilePaths) {

				try {

					// FACES-2650 Because there may be multiple jars in our bundle, some resources may have exactly
					// the same reourceFilePath. We need to find all the resources with this resourceFilePath in all
					// jars.
					resources.addAll(Collections.list(bundle.getResources(resourceFilePath)));
				}
				catch (IOException e) {

					long bundleId = bundle.getBundleId();
					String symbolicName = bundle.getSymbolicName();
					logger.error(
						"Failed to obtain URLs of resources with path \"{0}\" from bundle with bundle id \"{1}\" and symbolic name \"{2}\" due to the following error:",
						resourceFilePath, bundleId, symbolicName);
					logger.error(e);
				}
			}

			resources.removeAll(Collections.singleton(null));
		}

		return Collections.unmodifiableList(resources);
	}

	public static Collection<URI> getResourcesAsURIs(String path, String resourceFilePattern,
		ServletContext servletContext) {

		List<URI> resourceURIs = new ArrayList<URI>();
		Collection<URL> resourceURLs = getResources(path, resourceFilePattern, servletContext);

		for (URL resourceURL : resourceURLs) {

			try {
				resourceURIs.add(resourceURL.toURI());
			}
			catch (URISyntaxException e) {
				logger.error(e);
			}
		}

		return Collections.unmodifiableList(resourceURIs);
	}

	private static Collection<URL> getResources(String path, String resourcefilePattern,
		ServletContext servletContext) {

		Collection<Bundle> facesBundles = FacesBundleUtil.getFacesBundles(servletContext);
		List<URL> resources;

		if (!facesBundles.isEmpty()) {

			resources = new ArrayList<URL>();

			for (Bundle bundle : facesBundles) {

				String bundleSymbolicName = bundle.getSymbolicName();

				if (!bundleSymbolicName.equals(InternalFacesBundleUtil.MOJARRA_SYMBOLIC_NAME)) {
					resources.addAll(getResources(path, resourcefilePattern, bundle));
				}
			}

			resources = Collections.unmodifiableList(resources);
		}
		else {

			// FACES-3233 Bridge Ext not working outside OSGI context
			resources = Collections.<URL>emptyList();
		}

		return resources;
	}
}
