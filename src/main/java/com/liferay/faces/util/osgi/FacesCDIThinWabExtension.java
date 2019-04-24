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
package com.liferay.faces.util.osgi;

import com.liferay.faces.util.osgi.internal.OnDemandBeanManagerKey;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.servlet.ServletContext;
import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Kyle Stiemann
 */
@ProviderType
public final class FacesCDIThinWabExtension implements Extension {

	private void intializeBeanManager(@Observes ServletContext servletContext, BeanManager beanManager) {

		if (servletContext.getAttribute(OnDemandBeanManagerKey.INSTANCE) == null) {
			servletContext.setAttribute(OnDemandBeanManagerKey.INSTANCE, beanManager);
		}
	}
}
