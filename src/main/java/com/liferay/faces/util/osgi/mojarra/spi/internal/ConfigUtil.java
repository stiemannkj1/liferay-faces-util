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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.FacesException;
import javax.lang.model.SourceVersion;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.osgi.framework.Bundle;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.liferay.faces.util.internal.CloseableUtil;
import com.liferay.faces.util.internal.TCCLUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.osgi.internal.FacesBundlesHandlerBase;


/**
 * @author  kylestiemann
 */
/* package-private */ final class ConfigUtil {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ConfigUtil.class);

	private ConfigUtil() {
		throw new AssertionError();
	}

	/**
	 * This method attempts to remove config files that should not be available to the current Faces WAB. For example,
	 * if Liferay Faces is deployed in an uber JAR, the current Faces WAB may have access to the faces-config.xml for
	 * PrimeFaces even though it doesn't import PrimeFaces classes. In that case, the faces-config.xml will cause
	 * startup errors, so this method attempts to load all classes from that faces-config.xml and removes the
	 * faces-config.xml from the list if those classes are unloadable.
	 *
	 * @param  handlingFacesConfigs
	 * @param  iterator
	 * @param  servletContext
	 */
	public static void removeUnloadableConfigFiles(boolean handlingFacesConfigs, Iterator<URL> iterator,
		ServletContext servletContext) {

		ClassLoader threadContextClassLoader = TCCLUtil.getThreadContextClassLoaderOrDefault(ConfigUtil.class);
		DocumentBuilder documentBuilder;
		XPathExpression getLeavesXpathExpression;
		XPathExpression getNameXpathExpression;

		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = documentBuilderFactory.newDocumentBuilder();

			XPathFactory xpathFactory = XPathFactory.newInstance();
			getLeavesXpathExpression = xpathFactory.newXPath().compile("//*[count(./*) = 0]");
			getNameXpathExpression = xpathFactory.newXPath().compile("//*[local-name() = 'name']");
		}
		catch (Exception e) {

			String configFileType = "faces-config.xml";

			if (!handlingFacesConfigs) {
				configFileType = "*.taglib.xml";
			}

			throw new FacesException("Failed to parse " + configFileType + " files due to the following error:", e);
		}

		while (iterator.hasNext()) {

			URL configFileURL = iterator.next();
			InputStream inputStream = null;
			String configFileName = "no name found";

			if (!handlingFacesConfigs) {
				configFileName = configFileURL.getPath();
			}

			try {

				inputStream = configFileURL.openStream();

				Document document = documentBuilder.parse(inputStream);
				configFileName = (String) getNameXpathExpression.evaluate(document, XPathConstants.STRING);

				if (configFileName == null) {
					configFileName = "no name found";
				}

				String loadingAllConfigClassesMessage =
					"Loading all classes from faces-config.xml with name \"{0}\" to determine if it should be ignored.";

				if (!handlingFacesConfigs) {
					loadingAllConfigClassesMessage =
						"Loading all classes from {0} to determine if it should be ignored.";
				}

				logger.info(loadingAllConfigClassesMessage, configFileName);

				NodeList nodeList = (NodeList) getLeavesXpathExpression.evaluate(document, XPathConstants.NODESET);
				int nodeListLength = nodeList.getLength();

				for (int i = 0; i < nodeListLength; i++) {

					Node node = nodeList.item(i);
					String potentialClassName = node.getTextContent();

					if ((potentialClassName != null) && SourceVersion.isName(potentialClassName)) {

						String classFilePath = potentialClassName.replace(".", "/") + ".class";
						FacesBundlesHandlerBase<List<URL>> facesBundlesHandlerBase =
							new FacesBundlesHandlerClassFileProviderOSGiImpl(classFilePath);
						List<URL> classFiles = facesBundlesHandlerBase.handleFacesBundles(servletContext);

						if (!classFiles.isEmpty()) {
							Class.forName(potentialClassName, false, threadContextClassLoader);
						}
					}
				}
			}
			catch (Throwable t) {

				if ((t instanceof ClassNotFoundException) || (t instanceof LinkageError)) {

					iterator.remove();

					String failedToLoadAllConfigFileClassesMessage =
						"Loading all classes from faces-config.xml with name \"{0}\" to determine if it should be ignored.";

					if (!handlingFacesConfigs) {
						failedToLoadAllConfigFileClassesMessage =
							"Loading all classes from {0} to determine if it should be ignored.";
					}

					logger.warn(failedToLoadAllConfigFileClassesMessage, configFileName);
				}
				else {

					String failedToParseConfigFileMessage =
						"Failed to parse faces-config.xml files due to the following error:";

					if (!handlingFacesConfigs) {
						failedToParseConfigFileMessage = "Failed to parse " + configFileName +
							" files due to the following error:";
					}

					throw new FacesException(failedToParseConfigFileMessage, t);
				}

			}
			finally {
				CloseableUtil.close(inputStream);
			}
		}
	}

	private static final class FacesBundlesHandlerClassFileProviderOSGiImpl extends FacesBundlesHandlerBase<List<URL>> {

		// Private Final Data Members
		private final String classFilePath;

		public FacesBundlesHandlerClassFileProviderOSGiImpl(String classFilePath) {
			this.classFilePath = classFilePath;
		}

		private static void handleFacesBundle(Bundle bundle, String classFilePath,
			FacesBundlesHandlerBase.ReturnValueReference<List<URL>> returnValueReference) {

			URL classFileURL = bundle.getEntry(classFilePath);

			if (classFileURL != null) {
				returnValueReference.get().add(classFileURL);
			}
		}

		@Override
		protected List<URL> getInitialReturnValueObject() {
			return new ArrayList<URL>();
		}

		@Override
		protected void handleCurrentFacesWab(Bundle currentFacesWab,
			FacesBundlesHandlerBase.ReturnValueReference<List<URL>> returnValueReference) {
			handleFacesBundle(currentFacesWab, "WEB-INF/classes/" + classFilePath, returnValueReference);
		}

		@Override
		protected void handleFacesBundle(Bundle bundle,
			FacesBundlesHandlerBase.ReturnValueReference<List<URL>> returnValueReference) {
			handleFacesBundle(bundle, classFilePath, returnValueReference);
		}
	}
}
