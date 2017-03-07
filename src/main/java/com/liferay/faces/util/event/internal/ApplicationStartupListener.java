/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.util.event.internal;

import java.io.IOException;
import java.util.EventObject;
import java.util.List;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;

import com.liferay.faces.util.config.ApplicationConfig;
import com.liferay.faces.util.config.ConfiguredElement;
import com.liferay.faces.util.config.FacesConfig;
import com.liferay.faces.util.config.WebConfigParam;
import com.liferay.faces.util.config.internal.ApplicationConfigInitializer;
import com.liferay.faces.util.config.internal.ApplicationConfigInitializerImpl;
import com.liferay.faces.util.factory.FactoryExtensionFinder;


/**
 * @author  Neil Griffin
 */
public class ApplicationStartupListener extends ApplicationStartupListenerCompat_2_2 {

	// Java 1.6+ @Override
	public void processSystemEvent(EventObject systemEvent) throws AbortProcessingException {

		UtilDependencyVerifier.verify();
		Application application = (Application) systemEvent.getSource();
		FacesContext initFacesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = initFacesContext.getExternalContext();
		Map<String, Object> applicationMap = externalContext.getApplicationMap();
		String appConfigAttrName = ApplicationConfig.class.getName();
		ApplicationConfig applicationConfig = (ApplicationConfig) applicationMap.get(appConfigAttrName);

		if (applicationConfig == null) {

			boolean resolveEntities = WebConfigParam.ResolveXMLEntities.getBooleanValue(externalContext);

			String contextPath = getApplicationContextPath(externalContext);
			ApplicationConfigInitializer applicationConfigInitializer = new ApplicationConfigInitializerImpl(
					contextPath, resolveEntities);

			try {
				applicationConfig = applicationConfigInitializer.initialize();
				applicationMap.put(appConfigAttrName, applicationConfig);

				// Register the configured factories with the factory extension finder.
				FacesConfig facesConfig = applicationConfig.getFacesConfig();
				List<ConfiguredElement> configuredFactoryExtensions = facesConfig.getConfiguredFactoryExtensions();

				if (configuredFactoryExtensions != null) {

					FactoryExtensionFinder factoryExtensionFinder = FactoryExtensionFinder.getInstance();

					for (ConfiguredElement configuredFactoryExtension : configuredFactoryExtensions) {
						factoryExtensionFinder.registerFactory(configuredFactoryExtension);
					}
				}
			}
			catch (IOException e) {
				throw new AbortProcessingException(e);
			}

			publishEvent(application, initFacesContext, applicationConfig);
		}
	}
}
