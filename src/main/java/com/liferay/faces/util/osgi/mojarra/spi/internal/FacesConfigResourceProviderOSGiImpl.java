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
package com.liferay.faces.util.osgi.mojarra.spi.internal;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;

import com.liferay.faces.util.osgi.FacesBundleUtil;
import com.liferay.faces.util.osgi.internal.InternalFacesBundleUtil;
import com.liferay.faces.util.osgi.internal.OSGiResourceProviderUtil;

import com.sun.faces.spi.FacesConfigResourceProvider;


/**
 * This class implements the Mojarra {@link com.sun.faces.spi.ConfigurationResourceProvider} SPI in order to enable the
 * discovery of resources within the OSGi bundle that match the "*.faces-config.xml" wildcard.
 *
 * @author  Kyle Stiemann
 */
public class FacesConfigResourceProviderOSGiImpl implements FacesConfigResourceProvider {

	private static boolean isDefaultFacesConfig(URI facesConfigURI) {

		String path = facesConfigURI.getPath();

		return path.equals("/META-INF/faces-config.xml");
	}

	private static boolean isFacesConfigInCurrentFacesWab(URI facesConfigURI, Bundle currentFacesWab) {

		long currentFacesWabId = currentFacesWab.getBundleId();
		String authority = facesConfigURI.getAuthority();

		return authority.startsWith(currentFacesWabId + ".");
	}

	/**
	 * Returns the list of resources matching the "*.faces-config.xml" wildcard found within the OSGi bundle. For more
	 * information, see {@link com.sun.faces.spi.ConfigurationResourceProvider#getResources(ServletContext)}.
	 */
	@Override
	public Collection<URI> getResources(ServletContext servletContext) {

		String facesConfigPattern = "*.faces-config.xml";
		boolean isCurrentWarThinWab = FacesBundleUtil.isCurrentWarThinWab();

		if (isCurrentWarThinWab) {

			// Get all faces-config.xml files and *.faces-config.xml files.
			facesConfigPattern = "*faces-config.xml";
		}

		Collection<URI> facesConfigURIs = new ArrayList<URI>(OSGiResourceProviderUtil.getResourcesAsURIs("/META-INF/",
					facesConfigPattern, servletContext));
		Bundle currentFacesWab = InternalFacesBundleUtil.getCurrentFacesWab(servletContext);
		Iterator<URI> iterator = facesConfigURIs.iterator();

		while (iterator.hasNext()) {

			URI facesConfigURI = iterator.next();

			// Mojarra finds all the META-INF/faces-config.xml files that included inside each thin war.
			if (isCurrentWarThinWab && isFacesConfigInCurrentFacesWab(facesConfigURI, currentFacesWab) &&
					isDefaultFacesConfig(facesConfigURI)) {
				iterator.remove();
			}
		}

		return Collections.unmodifiableCollection(facesConfigURIs);
	}
}
