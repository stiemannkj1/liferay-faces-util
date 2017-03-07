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
package com.liferay.faces.util.view.facelets;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.el.MethodExpression;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.Metadata;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributeException;


/**
 * @author  Neil Griffin
 */
public class MethodMetadata extends Metadata {

	// Private Data Members
	Class<?>[] args;
	TagAttribute tagAttribute;
	Method writeMethod;

	public MethodMetadata(TagAttribute tagAttribute, Method writeMethod, Class<?>[] args) {
		this.tagAttribute = tagAttribute;
		this.writeMethod = writeMethod;
		this.args = args;
	}

	// Java 1.6+ @Override
	public void applyMetadata(FaceletContext faceletContext, Object instance) {
		MethodExpression methodExpression = tagAttribute.getMethodExpression(faceletContext, null, args);

		try {
			writeMethod.invoke(instance, methodExpression);
		}
		catch (InvocationTargetException e) {
			throw new TagAttributeException(tagAttribute, e.getCause());
		}
		catch (Exception e) {
			throw new TagAttributeException(tagAttribute, e);
		}
	}

}
