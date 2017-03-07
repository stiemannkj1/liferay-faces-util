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
package com.liferay.faces.util.render;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;

import com.liferay.faces.util.helper.Wrapper;


/**
 * @author  Neil Griffin
 */
public abstract class RendererWrapper extends Renderer implements Wrapper<Renderer> {

	public abstract Renderer getWrapped();

	// Java 1.6+ @Override
	public String convertClientId(FacesContext context, String clientId) {
		return getWrapped().convertClientId(context, clientId);
	}

	// Java 1.6+ @Override
	public void decode(FacesContext context, UIComponent component) {
		getWrapped().decode(context, component);
	}

	// Java 1.6+ @Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		getWrapped().encodeBegin(context, component);
	}

	// Java 1.6+ @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		getWrapped().encodeChildren(context, component);
	}

	// Java 1.6+ @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		getWrapped().encodeEnd(context, component);
	}

	// Java 1.6+ @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue)
		throws ConverterException {
		return getWrapped().getConvertedValue(context, component, submittedValue);
	}

	// Java 1.6+ @Override
	public boolean getRendersChildren() {
		return getWrapped().getRendersChildren();
	}
}
