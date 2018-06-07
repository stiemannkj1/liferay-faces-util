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
package com.liferay.faces.util.render;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.osgi.annotation.versioning.ProviderType;


/**
 * @deprecated  Please use {@link RendererUtil#encodePassThroughAttributes(javax.faces.context.ResponseWriter,
 *              javax.faces.component.UIComponent, java.util.List)} instead.
 * @author      Kyle Stiemann
 */
@Deprecated
@ProviderType
public class PassThroughRenderer extends Renderer {

	// Protected Constants
	/**
	 * @deprecated  Please use {@link RendererUtil#MOUSE_DOM_EVENTS} instead.
	 */
	@Deprecated
	protected static final String[] MOUSE_DOM_EVENTS = {
			"onclick", "ondblclick", "onmousedown", "onmousemove", "onmouseout", "onmouseover", "onmouseup"
		};

	/**
	 * @deprecated  Please use {@link RendererUtil#KEYBOARD_DOM_EVENTS} instead.
	 */
	@Deprecated
	protected static final String[] KEYBOARD_DOM_EVENTS = { "onkeydown", "onkeypress", "onkeyup" };

	/**
	 * This method exists as a convenience for Component developers to encode attributes that pass through to the DOM in
	 * JSF 2.1.
	 *
	 * @deprecated  Please use {@link RendererUtil#encodePassThroughAttributes(javax.faces.context.ResponseWriter,
	 *              javax.faces.component.UIComponent, java.util.List)} instead.
	 */
	@Deprecated
	protected void encodePassThroughAttributes(ResponseWriter responseWriter, UIComponent uiComponent,
		final String[] PASS_THROUGH_ATTRIBUTES) throws IOException {

		Map<String, Object> attributes = uiComponent.getAttributes();

		for (final String PASS_THROUGH_ATTRIBUTE : PASS_THROUGH_ATTRIBUTES) {

			Object passThroughAttributeValue = attributes.get(PASS_THROUGH_ATTRIBUTE);

			if (passThroughAttributeValue != null) {
				responseWriter.writeAttribute(PASS_THROUGH_ATTRIBUTE, passThroughAttributeValue,
					PASS_THROUGH_ATTRIBUTE);
			}
		}
	}
}
