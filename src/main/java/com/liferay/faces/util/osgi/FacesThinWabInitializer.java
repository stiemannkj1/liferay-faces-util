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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.Bundle;

import com.liferay.faces.util.config.WebConfig;
import com.liferay.faces.util.config.WebConfigParam;
import com.liferay.faces.util.config.internal.ResourceReader;
import com.liferay.faces.util.config.internal.ResourceReaderImpl;
import com.liferay.faces.util.config.internal.WebConfigScanner;
import com.liferay.faces.util.config.internal.WebConfigScannerImpl;
import com.liferay.faces.util.helper.BooleanHelper;
import com.liferay.faces.util.osgi.internal.FacesBundlesHandlerBase;
import com.liferay.faces.util.xml.ConcurrentSAXParserFactory;

import com.sun.faces.config.FacesInitializer;


/**
 * @author  Kyle Stiemann
 */
public final class FacesThinWabInitializer implements ServletContainerInitializer {

	private static boolean getBooleanValue(ServletContext servletContext, String name, String alternateName,
		boolean defaultBooleanValue) {

		boolean booleanValue = defaultBooleanValue;

		String configuredValue = getConfiguredValue(servletContext, name, alternateName);

		if (configuredValue != null) {
			booleanValue = BooleanHelper.isTrueToken(configuredValue);
		}

		return booleanValue;
	}

	private static String getConfiguredValue(ServletContext servletContext, String name, String alternateName) {

		String configuredValue = servletContext.getInitParameter(name);

		if ((configuredValue == null) && (alternateName != null)) {
			configuredValue = servletContext.getInitParameter(alternateName);
		}

		return configuredValue;
	}

	@Override
	public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {

		FacesInitializer facesInitializer = new FacesInitializer();
		facesInitializer.onStartup(classes, servletContext);

		// Obtain the current ClassLoader
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		// Obtain a ResourceReader
		ResourceReader resourceReader = new ResourceReaderImpl(servletContext);

		// Obtain a SAX Parser Factory.
		SAXParserFactory saxParserFactory = ConcurrentSAXParserFactory.newInstance();
		saxParserFactory.setValidating(false);
		saxParserFactory.setNamespaceAware(true);

		try {

			// Obtain a SAX Parser from the factory.
			SAXParser saxParser = saxParserFactory.newSAXParser();
			String resolveXMLEntitiesName = WebConfigParam.ResolveXMLEntities.getName();
			String resolveXMLEntitiesAlternateName = WebConfigParam.ResolveXMLEntities.getAlternateName();
			boolean resolveEntities = getBooleanValue(servletContext, resolveXMLEntitiesName,
					resolveXMLEntitiesAlternateName, false);

			// Scan all the web-fragment.xml descriptors in bundles that this bundle depends on.
			WebConfigScanner webConfigScanner = new WebConfigScannerImpl(classLoader, resourceReader, saxParser,
					resolveEntities);
			FacesBundlesHandlerBase<List<URL>> facesBundlesHandlerGetWebFragmentImpl =
				new FacesBundlesHandlerGetWebFragmentImpl();
			List<URL> webFragmentURLs = facesBundlesHandlerGetWebFragmentImpl.handleFacesBundles(servletContext);
			WebConfig webFragmentConfig = webConfigScanner.scanWebFragments(Collections.enumeration(webFragmentURLs));
			Map<String, String> webFragmentContextParams = webFragmentConfig.getConfiguredContextParams();
			Set<Map.Entry<String, String>> entrySet = webFragmentContextParams.entrySet();

			if (!webFragmentContextParams.isEmpty()) {

				List<String> initParameterNames = Collections.list(servletContext.getInitParameterNames());

				for (Map.Entry<String, String> webFragmentContextParam : entrySet) {

					String name = webFragmentContextParam.getKey();

					if (!initParameterNames.contains(name)) {

						String value = webFragmentContextParam.getValue();
						servletContext.setInitParameter(name, value);
					}
				}
			}
		}
		catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private static final class FacesBundlesHandlerGetWebFragmentImpl extends FacesBundlesHandlerBase<List<URL>> {

		@Override
		protected List<URL> getInitialReturnValueObject() {
			return new ArrayList<URL>();
		}

		@Override
		protected void handleFacesBundle(Long bundleKey, Bundle bundle,
			ReturnValueReference<List<URL>> returnValueReference) {

			URL entry = bundle.getEntry(WebConfigScanner.WEB_FRAGMENT_META_INF_PATH);

			if (entry != null) {
				returnValueReference.get().add(entry);
			}
		}
	}
}
