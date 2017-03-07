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

import javax.faces.context.FacesContext;

import com.liferay.faces.util.helper.Wrapper;


/**
 * @author  Neil Griffin
 */
public abstract class ResourceValidatorWrapper implements ResourceValidator, Wrapper<ResourceValidator> {

	// Java 1.6+ @Override
	public abstract ResourceValidator getWrapped();

	// Java 1.6+ @Override
	public boolean containsBannedPath(String resourceId) {
		return getWrapped().containsBannedPath(resourceId);
	}

	// Java 1.6+ @Override
	public boolean isBannedSequence(String resourceId) {
		return getWrapped().isBannedSequence(resourceId);
	}

	// Java 1.6+ @Override
	public boolean isFaceletDocument(FacesContext facesContext, String resourceId) {
		return getWrapped().isFaceletDocument(facesContext, resourceId);
	}

	// Java 1.6+ @Override
	public boolean isSelfReferencing(FacesContext facesContext, String resourceId) {
		return getWrapped().isSelfReferencing(facesContext, resourceId);
	}

	// Java 1.6+ @Override
	public boolean isValidLibraryName(String libraryName) {
		return getWrapped().isValidLibraryName(libraryName);
	}

	// Java 1.6+ @Override
	public boolean isValidResourceName(String resourceName) {
		return getWrapped().isValidResourceName(resourceName);
	}
}
