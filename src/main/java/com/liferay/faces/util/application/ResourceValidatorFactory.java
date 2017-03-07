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
package com.liferay.faces.util.application;

import com.liferay.faces.util.factory.FactoryExtensionFinder;
import com.liferay.faces.util.helper.Wrapper;


/**
 * @author  Neil Griffin
 */
public abstract class ResourceValidatorFactory implements Wrapper<ResourceValidatorFactory> {

	/**
	 * Returns a stateless, thread-safe singleton instance of {@link ResourceValidator} from the {@link
	 * ResourceValidatorFactory} found by the {@link FactoryExtensionFinder}. ResourceValidator is a stateless,
	 * thread-safe singleton because it is designed to be used by a {@link javax.faces.application.ResourceHandler},
	 * which is a <a
	 * href="https://javaserverfaces.java.net/nonav/docs/2.2/javadocs/javax/faces/application/Application.html#getResourceHandler()">
	 * stateless, thread-safe singleton</a>.
	 */
	public static ResourceValidator getResourceValidatorInstance() {

		ResourceValidatorFactory resourceValidatorFactory = (ResourceValidatorFactory) FactoryExtensionFinder
			.getFactory(ResourceValidatorFactory.class);

		return resourceValidatorFactory.getResourceValidator();
	}

	/**
	 * Returns a stateless, thread-safe singleton instance of {@link ResourceValidator}. ResourceValidator is stateless,
	 * thread-safe singleton because it is designed to be used by a {@link javax.faces.application.ResourceHandler},
	 * which is a <a
	 * href="https://javaserverfaces.java.net/nonav/docs/2.2/javadocs/javax/faces/application/Application.html#getResourceHandler()">
	 * stateless, thread-safe singleton</a>.
	 */
	public abstract ResourceValidator getResourceValidator();

	/**
	 * Returns the wrapped factory instance if this factory decorates another. Otherwise, this method returns null.
	 */
	// Java 1.6+ @Override
	public abstract ResourceValidatorFactory getWrapped();
}
