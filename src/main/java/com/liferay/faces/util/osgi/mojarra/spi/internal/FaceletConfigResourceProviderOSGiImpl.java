/**
 * Copyright (c) 2000-2019 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.util.osgi.mojarra.spi.internal;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import com.liferay.faces.util.osgi.internal.FacesBundlesHandlerBase;
import com.liferay.faces.util.osgi.internal.FacesBundlesHandlerResourceProviderOSGiImpl;
import com.liferay.faces.util.resource.internal.ResourceProviderUtil;

import com.sun.faces.spi.ConfigurationResourceProvider;
import com.sun.faces.spi.FaceletConfigResourceProvider;


/**
 * This class implements the Mojarra {@link com.sun.faces.spi.ConfigurationResourceProvider} SPI in order to enable the
 * discovery of resources within the OSGi bundle that match the "*.taglib.xml" wildcard.
 *
 * @author  Kyle Stiemann
 */
public class FaceletConfigResourceProviderOSGiImpl implements ConfigurationResourceProvider,
	FaceletConfigResourceProvider {

	/**
	 * Returns the list of *.taglib.xml resources found in Faces OSGi bundles. For more information, see {@link
	 * com.sun.faces.spi.ConfigurationResourceProvider#getResources(ServletContext)}.
	 */
	@Override
	public Collection<URI> getResources(ServletContext servletContext) {

		FacesBundlesHandlerBase<List<URL>> facesBundlesHandler = new FacesBundlesHandlerResourceProviderOSGiImpl(
				ResourceProviderUtil.META_INF_PATH, "*.taglib.xml");
		List<URL> resourceURLs = new ArrayList<URL>(facesBundlesHandler.handleFacesBundles(servletContext));

		if (FacesBundlesHandlerBase.isCurrentWarThinWab()) {

			Iterator<URL> iterator = resourceURLs.iterator();
			ConfigUtil.removeUnloadableConfigFiles(false, iterator, servletContext);
		}

		return ResourceProviderUtil.getResourcesAsURIs(Collections.unmodifiableList(resourceURLs));
	}
}
