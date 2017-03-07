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
package com.liferay.faces.util.context;

import java.util.Iterator;

import javax.el.ELContext;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;

import com.liferay.faces.util.helper.Wrapper;


/**
 * @author  Neil Griffin
 */
public abstract class FacesContextWrapper extends FacesContext implements Wrapper<FacesContext> {

	public abstract FacesContext getWrapped();

	// Java 1.6+ @Override
	public void addMessage(String clientId, FacesMessage message) {
		getWrapped().addMessage(clientId, message);
	}

	// Java 1.6+ @Override
	public Application getApplication() {
		return getWrapped().getApplication();
	}

	// Java 1.6+ @Override
	public Iterator<String> getClientIdsWithMessages() {
		return getWrapped().getClientIdsWithMessages();
	}

	// Java 1.6+ @Override
	public ELContext getELContext() {
		return getWrapped().getELContext();
	}

	// Java 1.6+ @Override
	public ExternalContext getExternalContext() {
		return getWrapped().getExternalContext();
	}

	// Java 1.6+ @Override
	public Severity getMaximumSeverity() {
		return getWrapped().getMaximumSeverity();
	}

	// Java 1.6+ @Override
	public Iterator<FacesMessage> getMessages() {
		return getWrapped().getMessages();
	}

	// Java 1.6+ @Override
	public Iterator<FacesMessage> getMessages(String clientId) {
		return getWrapped().getMessages(clientId);
	}

	// Java 1.6+ @Override
	public RenderKit getRenderKit() {
		return getWrapped().getRenderKit();
	}

	// Java 1.6+ @Override
	public boolean getRenderResponse() {
		return getWrapped().getRenderResponse();
	}

	// Java 1.6+ @Override
	public boolean getResponseComplete() {
		return getWrapped().getResponseComplete();
	}

	// Java 1.6+ @Override
	public ResponseStream getResponseStream() {
		return getWrapped().getResponseStream();
	}

	// Java 1.6+ @Override
	public ResponseWriter getResponseWriter() {
		return getWrapped().getResponseWriter();
	}

	// Java 1.6+ @Override
	public UIViewRoot getViewRoot() {
		return getWrapped().getViewRoot();
	}

	// Java 1.6+ @Override
	public void release() {
		getWrapped().release();
	}

	// Java 1.6+ @Override
	public void renderResponse() {
		getWrapped().renderResponse();
	}

	// Java 1.6+ @Override
	public void responseComplete() {
		getWrapped().responseComplete();
	}

	// Java 1.6+ @Override
	public void setResponseStream(ResponseStream responseStream) {
		getWrapped().setResponseStream(responseStream);
	}

	// Java 1.6+ @Override
	public void setResponseWriter(ResponseWriter responseWriter) {
		getWrapped().setResponseWriter(responseWriter);
	}

	// Java 1.6+ @Override
	public void setViewRoot(UIViewRoot root) {
		getWrapped().setViewRoot(root);
	}
}
