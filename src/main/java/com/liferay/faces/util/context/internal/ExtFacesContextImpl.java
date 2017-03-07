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
package com.liferay.faces.util.context.internal;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.el.ELContext;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseListener;
import javax.faces.render.RenderKit;

import com.liferay.faces.util.context.ExtFacesContext;
import com.liferay.faces.util.context.FacesContextHelper;
import com.liferay.faces.util.context.FacesContextHelperUtil;


/**
 * @author  Neil Griffin
 */
public class ExtFacesContextImpl extends ExtFacesContext implements Serializable {

	// serialVersionUID Note: This class implements Serializable only to avoid extraneous stacktraces from being thrown
	// by Mojarra.
	private static final long serialVersionUID = 102195020822157073L;

	public ExtFacesContextImpl() {
		setInstance(this);
	}

	/**
	 * @see  {@link FacesContextHelper#addComponentErrorMessage(String, String)}
	 */
	// Java 1.6+ @Override
	public void addComponentErrorMessage(String clientId, String messageId) {
		FacesContextHelperUtil.addComponentErrorMessage(clientId, messageId);
	}

	/**
	 * @see  {@link FacesContextHelper#addComponentErrorMessage(String, String, Object...)}
	 */
	// Java 1.6+ @Override
	public void addComponentErrorMessage(String clientId, String messageId, Object... arguments) {
		FacesContextHelperUtil.addComponentErrorMessage(clientId, messageId, arguments);
	}

	/**
	 * @see  {@link FacesContextHelper#addComponentErrorMessage(String, String)}
	 */
	// Java 1.6+ @Override
	public void addComponentErrorMessage(FacesContext facesContext, String clientId, String messageId) {
		FacesContextHelperUtil.addComponentErrorMessage(facesContext, clientId, messageId);
	}

	/**
	 * @see  {@link FacesContextHelper#addComponentErrorMessage(String, String, Object...)}
	 */
	// Java 1.6+ @Override
	public void addComponentErrorMessage(FacesContext facesContext, String clientId, String messageId,
		Object... arguments) {
		FacesContextHelperUtil.addComponentErrorMessage(facesContext, clientId, messageId, arguments);
	}

	/**
	 * @see  {@link FacesContextHelper#addComponentInfoMessage(String, String)}
	 */
	// Java 1.6+ @Override
	public void addComponentInfoMessage(String clientId, String messageId) {
		FacesContextHelperUtil.addComponentInfoMessage(clientId, messageId);
	}

	/**
	 * @see  {@link FacesContextHelper#addComponentInfoMessage(String, String, Object...)}
	 */
	// Java 1.6+ @Override
	public void addComponentInfoMessage(String clientId, String messageId, Object... arguments) {
		FacesContextHelperUtil.addComponentInfoMessage(clientId, messageId, arguments);
	}

	/**
	 * @see  {@link FacesContextHelper#addComponentInfoMessage(String, String)}
	 */
	// Java 1.6+ @Override
	public void addComponentInfoMessage(FacesContext facesContext, String clientId, String messageId) {
		FacesContextHelperUtil.addComponentInfoMessage(facesContext, clientId, messageId);
	}

	/**
	 * @see  {@link FacesContextHelper#addComponentInfoMessage(String, String, Object...)}
	 */
	// Java 1.6+ @Override
	public void addComponentInfoMessage(FacesContext facesContext, String clientId, String messageId,
		Object... arguments) {
		FacesContextHelperUtil.addComponentInfoMessage(facesContext, clientId, messageId, arguments);
	}

	/**
	 * @see  {@link FacesContextHelper#addGlobalErrorMessage(String)}
	 */
	// Java 1.6+ @Override
	public void addGlobalErrorMessage(String messageId) {
		FacesContextHelperUtil.addGlobalErrorMessage(messageId);
	}

	/**
	 * @see  {@link FacesContextHelper#addGlobalErrorMessage(String, Object...)}
	 */
	// Java 1.6+ @Override
	public void addGlobalErrorMessage(String messageId, Object... arguments) {
		FacesContextHelperUtil.addGlobalErrorMessage(messageId, arguments);
	}

	/**
	 * @see  {@link FacesContextHelper#addGlobalErrorMessage(String)}
	 */
	// Java 1.6+ @Override
	public void addGlobalErrorMessage(FacesContext facesContext, String messageId) {
		FacesContextHelperUtil.addGlobalErrorMessage(facesContext, messageId);
	}

	/**
	 * @see  {@link FacesContextHelper#addGlobalErrorMessage(String, Object...)}
	 */
	// Java 1.6+ @Override
	public void addGlobalErrorMessage(FacesContext facesContext, String messageId, Object... arguments) {
		FacesContextHelperUtil.addGlobalErrorMessage(facesContext, messageId, arguments);
	}

	/**
	 * @see  {@link FacesContextHelper#addGlobalInfoMessage(String)}
	 */
	// Java 1.6+ @Override
	public void addGlobalInfoMessage(String messageId) {
		FacesContextHelperUtil.addGlobalInfoMessage(messageId);
	}

	/**
	 * @see  {@link FacesContextHelper#addGlobalInfoMessage(String, Object...)}
	 */
	// Java 1.6+ @Override
	public void addGlobalInfoMessage(String messageId, Object... arguments) {
		FacesContextHelperUtil.addGlobalInfoMessage(messageId, arguments);
	}

	/**
	 * @see  {@link FacesContextHelper#addGlobalInfoMessage(String)}
	 */
	// Java 1.6+ @Override
	public void addGlobalInfoMessage(FacesContext facesContext, String messageId) {
		FacesContextHelperUtil.addGlobalInfoMessage(facesContext, messageId);
	}

	/**
	 * @see  {@link FacesContextHelper#addGlobalInfoMessage(String, Object...)}
	 */
	// Java 1.6+ @Override
	public void addGlobalInfoMessage(FacesContext facesContext, String messageId, Object... arguments) {
		FacesContextHelperUtil.addGlobalInfoMessage(facesContext, messageId, arguments);
	}

	/**
	 * @see  {@link FacesContextHelper#addGlobalSuccessInfoMessage()}
	 */
	// Java 1.6+ @Override
	public void addGlobalSuccessInfoMessage() {
		FacesContextHelperUtil.addGlobalSuccessInfoMessage();
	}

	/**
	 * @see  {@link FacesContextHelper#addGlobalSuccessInfoMessage()}
	 */
	// Java 1.6+ @Override
	public void addGlobalSuccessInfoMessage(FacesContext facesContext) {
		FacesContextHelperUtil.addGlobalSuccessInfoMessage(facesContext);
	}

	/**
	 * @see  {@link FacesContextHelper#addGlobalUnexpectedErrorMessage()}
	 */
	// Java 1.6+ @Override
	public void addGlobalUnexpectedErrorMessage() {
		FacesContextHelperUtil.addGlobalUnexpectedErrorMessage();
	}

	/**
	 * @see  {@link FacesContextHelper#addGlobalUnexpectedErrorMessage()}
	 */
	// Java 1.6+ @Override
	public void addGlobalUnexpectedErrorMessage(FacesContext facesContext) {
		FacesContextHelperUtil.addGlobalUnexpectedErrorMessage(facesContext);
	}

	/**
	 * @since  JSF 1.0
	 */
	// Java 1.6+ @Override
	public void addMessage(String clientId, FacesMessage facesMessage) {
		FacesContext.getCurrentInstance().addMessage(clientId, facesMessage);
	}

	/**
	 * @see  {@link FacesContextHelper#addMessage(String, Severity, String)}
	 */
	// Java 1.6+ @Override
	public void addMessage(String clientId, Severity severity, String messageId) {
		FacesContextHelperUtil.addMessage(clientId, severity, messageId);
	}

	/**
	 * @see  {@link FacesContextHelper#addMessage(String, Severity, String, Object...)}
	 */
	// Java 1.6+ @Override
	public void addMessage(String clientId, Severity severity, String messageId, Object... arguments) {
		FacesContextHelperUtil.addMessage(clientId, severity, messageId, arguments);
	}

	/**
	 * @see  {@link FacesContextHelper#addMessage(String, Severity, String)}
	 */
	// Java 1.6+ @Override
	public void addMessage(FacesContext facesContext, String clientId, Severity severity, String messageId) {
		FacesContextHelperUtil.addMessage(facesContext, clientId, severity, messageId);
	}

	/**
	 * @see  {@link FacesContextHelper#addMessage(String, Severity, String, Object...)}
	 */
	// Java 1.6+ @Override
	public void addMessage(FacesContext facesContext, String clientId, Severity severity, String messageId,
		Object... arguments) {
		FacesContextHelperUtil.addMessage(facesContext, clientId, severity, messageId, arguments);
	}

	/**
	 * @since  JSF 1.0
	 */
	// Java 1.6+ @Override
	public Application getApplication() {
		return FacesContext.getCurrentInstance().getApplication();
	}

	/**
	 * @since  JSF 1.0
	 */
	// Java 1.6+ @Override
	public Iterator<String> getClientIdsWithMessages() {
		return FacesContext.getCurrentInstance().getClientIdsWithMessages();
	}

	/**
	 * @since  JSF 1.2
	 */
	// Java 1.6+ @Override
	public ELContext getELContext() {
		return FacesContext.getCurrentInstance().getELContext();
	}

	/**
	 * @since  JSF 1.0
	 */
	// Java 1.6+ @Override
	public ExternalContext getExternalContext() {
		return FacesContext.getCurrentInstance().getExternalContext();
	}

	/**
	 * @see  {@link FacesContextHelper#getFacesContext()}
	 */
	// Java 1.6+ @Override
	public FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	/**
	 * @see  {@link FacesContextHelper#getLocale()}
	 */
	// Java 1.6+ @Override
	public Locale getLocale() {
		return FacesContextHelperUtil.getLocale();
	}

	/**
	 * @see  {@link FacesContextHelper#getLocale()}
	 */
	// Java 1.6+ @Override
	public Locale getLocale(FacesContext facesContext) {
		return FacesContextHelperUtil.getLocale(facesContext);
	}

	/**
	 * @since  JSF 1.0
	 */
	// Java 1.6+ @Override
	public Severity getMaximumSeverity() {
		return FacesContext.getCurrentInstance().getMaximumSeverity();
	}

	/**
	 * @see  {@link FacesContextHelper#getMessage(String)}
	 */
	// Java 1.6+ @Override
	public String getMessage(String messageId) {
		return FacesContextHelperUtil.getMessage(messageId);
	}

	/**
	 * @see  {@link FacesContextHelper#getMessage(String, Object...)}
	 */
	// Java 1.6+ @Override
	public String getMessage(String messageId, Object... arguments) {
		return FacesContextHelperUtil.getMessage(messageId, arguments);
	}

	/**
	 * @see  {@link FacesContextHelper#getMessage(Locale, String)}
	 */
	// Java 1.6+ @Override
	public String getMessage(Locale locale, String messageId) {
		return FacesContextHelperUtil.getMessage(locale, messageId);
	}

	/**
	 * @see  {@link FacesContextHelper#getMessage(String)}
	 */
	// Java 1.6+ @Override
	public String getMessage(FacesContext facesContext, String messageId) {
		return FacesContextHelperUtil.getMessage(facesContext, messageId);
	}

	/**
	 * @see  {@link FacesContextHelper#getMessage(Locale, String, Object...)}
	 */
	// Java 1.6+ @Override
	public String getMessage(Locale locale, String messageId, Object... arguments) {
		return FacesContextHelperUtil.getMessage(locale, messageId, arguments);
	}

	/**
	 * @see  {@link FacesContextHelper#getMessage(String, Object...)}
	 */
	// Java 1.6+ @Override
	public String getMessage(FacesContext facesContext, String messageId, Object... arguments) {
		return FacesContextHelperUtil.getMessage(facesContext, messageId, arguments);
	}

	/**
	 * @see  {@link FacesContextHelper#getMessage(Locale, String)}
	 */
	// Java 1.6+ @Override
	public String getMessage(FacesContext facesContext, Locale locale, String messageId) {
		return FacesContextHelperUtil.getMessage(facesContext, locale, messageId);
	}

	/**
	 * @see  {@link FacesContextHelper#getMessage(Locale, String, Object...)}
	 */
	// Java 1.6+ @Override
	public String getMessage(FacesContext facesContext, Locale locale, String messageId, Object... arguments) {
		return FacesContextHelperUtil.getMessage(facesContext, locale, messageId, arguments);
	}

	/**
	 * @since  JSF 1.0
	 */
	// Java 1.6+ @Override
	public Iterator<FacesMessage> getMessages() {
		return FacesContext.getCurrentInstance().getMessages();
	}

	/**
	 * @since  JSF 1.0
	 */
	// Java 1.6+ @Override
	public Iterator<FacesMessage> getMessages(String clientId) {
		return FacesContext.getCurrentInstance().getMessages(clientId);
	}

	/**
	 * @see  {@link FacesContextHelper#getNamespace()}
	 */
	// Java 1.6+ @Override
	public String getNamespace() {
		return FacesContextHelperUtil.getNamespace();
	}

	/**
	 * @see  {@link FacesContextHelper#getNamespace()}
	 */
	// Java 1.6+ @Override
	public String getNamespace(FacesContext facesContext) {
		return FacesContextHelperUtil.getNamespace(facesContext);
	}

	/**
	 * @see  {@link FacesContextHelper#getParentForm(UIComponent)}
	 */
	// Java 1.6+ @Override
	public UIForm getParentForm(UIComponent uiComponent) {
		return FacesContextHelperUtil.getParentForm(uiComponent);
	}

	/**
	 * @since  JSF 1.0
	 */
	// Java 1.6+ @Override
	public RenderKit getRenderKit() {
		return FacesContext.getCurrentInstance().getRenderKit();
	}

	/**
	 * @since  JSF 1.0
	 */
	// Java 1.6+ @Override
	public boolean getRenderResponse() {
		return FacesContext.getCurrentInstance().getRenderResponse();
	}

	/**
	 * @see  {@link FacesContextHelper#getRequestAttribute(String)}
	 */
	// Java 1.6+ @Override
	public Object getRequestAttribute(String name) {
		return FacesContextHelperUtil.getRequestAttribute(name);
	}

	/**
	 * @see  {@link FacesContextHelper#getRequestAttribute(String)}
	 */
	// Java 1.6+ @Override
	public Object getRequestAttribute(FacesContext facesContext, String name) {
		return FacesContextHelperUtil.getRequestAttribute(facesContext, name);
	}

	/**
	 * @see  {@link FacesContextHelper#getRequestContextPath()}
	 */
	// Java 1.6+ @Override
	public String getRequestContextPath() {
		return FacesContextHelperUtil.getRequestContextPath();
	}

	/**
	 * @see  {@link FacesContextHelper#getRequestContextPath()}
	 */
	// Java 1.6+ @Override
	public String getRequestContextPath(FacesContext facesContext) {
		return FacesContextHelperUtil.getRequestContextPath(facesContext);
	}

	/**
	 * @see  {@link FacesContextHelper#getRequestParameter(String)}
	 */
	// Java 1.6+ @Override
	public String getRequestParameter(String name) {
		return FacesContextHelperUtil.getRequestParameter(name);
	}

	/**
	 * @see  {@link FacesContextHelper#getRequestParameter(String)}
	 */
	// Java 1.6+ @Override
	public String getRequestParameter(FacesContext facesContext, String name) {
		return FacesContextHelperUtil.getRequestParameter(facesContext, name);
	}

	/**
	 * @see  {@link FacesContextHelper#getRequestParameterAsBool(String, boolean)}
	 */
	// Java 1.6+ @Override
	public boolean getRequestParameterAsBool(String name, boolean defaultValue) {
		return FacesContextHelperUtil.getRequestParameterAsBool(name, defaultValue);
	}

	/**
	 * @see  {@link FacesContextHelper#getRequestParameterAsBool(String, boolean)}
	 */
	// Java 1.6+ @Override
	public boolean getRequestParameterAsBool(FacesContext facesContext, String name, boolean defaultValue) {
		return FacesContextHelperUtil.getRequestParameterAsBool(facesContext, name, defaultValue);
	}

	/**
	 * @see  {@link FacesContextHelper#getRequestParameterAsInt(String, int)}
	 */
	// Java 1.6+ @Override
	public int getRequestParameterAsInt(String name, int defaultValue) {
		return FacesContextHelperUtil.getRequestParameterAsInt(name, defaultValue);
	}

	/**
	 * @see  {@link FacesContextHelper#getRequestParameterAsInt(String, int)}
	 */
	// Java 1.6+ @Override
	public int getRequestParameterAsInt(FacesContext facesContext, String name, int defaultValue) {
		return FacesContextHelperUtil.getRequestParameterAsInt(facesContext, name, defaultValue);
	}

	/**
	 * @see  {@link FacesContextHelper#getRequestParameterAsLong(String, long)}
	 */
	// Java 1.6+ @Override
	public long getRequestParameterAsLong(String name, long defaultValue) {
		return FacesContextHelperUtil.getRequestParameterAsLong(name, defaultValue);
	}

	/**
	 * @see  {@link FacesContextHelper#getRequestParameterAsLong(String, long)}
	 */
	// Java 1.6+ @Override
	public long getRequestParameterAsLong(FacesContext facesContext, String name, long defaultValue) {
		return FacesContextHelperUtil.getRequestParameterAsLong(facesContext, name, defaultValue);
	}

	/**
	 * @see  {@link FacesContextHelper#getRequestParameterFromMap(String)}
	 */
	// Java 1.6+ @Override
	public String getRequestParameterFromMap(String name) {
		return FacesContextHelperUtil.getRequestParameterFromMap(name);
	}

	/**
	 * @see  {@link FacesContextHelper#getRequestParameterFromMap(String)}
	 */
	// Java 1.6+ @Override
	public String getRequestParameterFromMap(FacesContext facesContext, String name) {
		return FacesContextHelperUtil.getRequestParameterFromMap(facesContext, name);
	}

	/**
	 * @see  {@link FacesContextHelper#getRequestParameterMap()}
	 */
	// Java 1.6+ @Override
	public Map<String, String> getRequestParameterMap() {
		return FacesContextHelperUtil.getRequestParameterMap();
	}

	/**
	 * @see  {@link FacesContextHelper#getRequestParameterMap()}
	 */
	// Java 1.6+ @Override
	public Map<String, String> getRequestParameterMap(FacesContext facesContext) {
		return FacesContextHelperUtil.getRequestParameterMap(facesContext);
	}

	/**
	 * @see  {@link FacesContextHelper#getRequestQueryString()}
	 */
	// Java 1.6+ @Override
	public String getRequestQueryString() {
		return FacesContextHelperUtil.getRequestQueryString();
	}

	/**
	 * @see  {@link FacesContextHelper#getRequestQueryString()}
	 */
	// Java 1.6+ @Override
	public String getRequestQueryString(FacesContext facesContext) {
		return FacesContextHelperUtil.getRequestQueryString(facesContext);
	}

	/**
	 * @see  {@link FacesContextHelper#getRequestQueryStringParameter(String)}
	 */
	// Java 1.6+ @Override
	public String getRequestQueryStringParameter(String name) {
		return FacesContextHelperUtil.getRequestQueryStringParameter(name);
	}

	/**
	 * @see  {@link FacesContextHelper#getRequestQueryStringParameter(String)}
	 */
	// Java 1.6+ @Override
	public String getRequestQueryStringParameter(FacesContext facesContext, String name) {
		return FacesContextHelperUtil.getRequestQueryStringParameter(facesContext, name);
	}

	/**
	 * @since  JSF 1.0
	 */
	// Java 1.6+ @Override
	public boolean getResponseComplete() {
		return FacesContext.getCurrentInstance().getRenderResponse();
	}

	/**
	 * @since  JSF 1.0
	 */
	// Java 1.6+ @Override
	public ResponseStream getResponseStream() {
		return FacesContext.getCurrentInstance().getResponseStream();
	}

	/**
	 * @since  JSF 1.0
	 */
	// Java 1.6+ @Override
	public ResponseWriter getResponseWriter() {
		return FacesContext.getCurrentInstance().getResponseWriter();
	}

	/**
	 * @see  {@link FacesContextHelper#getSession(boolean)}
	 */
	// Java 1.6+ @Override
	public Object getSession(boolean create) {
		return FacesContextHelperUtil.getSession(create);
	}

	/**
	 * @see  {@link FacesContextHelper#getSession(boolean)}
	 */
	// Java 1.6+ @Override
	public Object getSession(FacesContext facesContext, boolean create) {
		return FacesContextHelperUtil.getSession(facesContext, create);
	}

	/**
	 * @see  {@link FacesContextHelper#getSessionAttribute(String)}
	 */
	// Java 1.6+ @Override
	public Object getSessionAttribute(String name) {
		return FacesContextHelperUtil.getSessionAttribute(name);
	}

	/**
	 * @see  {@link FacesContextHelper#getSessionAttribute(String)}
	 */
	// Java 1.6+ @Override
	public Object getSessionAttribute(FacesContext facesContext, String name) {
		return FacesContextHelperUtil.getSessionAttribute(facesContext, name);
	}

	/**
	 * @since  JSF 1.0
	 */
	// Java 1.6+ @Override
	public UIViewRoot getViewRoot() {
		return FacesContext.getCurrentInstance().getViewRoot();
	}

	/**
	 * @see  {@link FacesContextHelper#matchComponentInHierarchy(UIComponent, String)}
	 */
	// Java 1.6+ @Override
	public UIComponent matchComponentInHierarchy(UIComponent parent, String partialClientId) {
		return FacesContextHelperUtil.matchComponentInHierarchy(parent, partialClientId);
	}

	/**
	 * @see  {@link FacesContextHelper#matchComponentInHierarchy(UIComponent, String)}
	 */
	// Java 1.6+ @Override
	public UIComponent matchComponentInHierarchy(FacesContext facesContext, UIComponent parent,
		String partialClientId) {
		return FacesContextHelperUtil.matchComponentInHierarchy(facesContext, parent, partialClientId);
	}

	/**
	 * @see  {@link FacesContextHelper#matchComponentInViewRoot(String)}
	 */
	// Java 1.6+ @Override
	public UIComponent matchComponentInViewRoot(String partialClientId) {
		return FacesContextHelperUtil.matchComponentInViewRoot(partialClientId);
	}

	/**
	 * @see  {@link FacesContextHelper#matchComponentInViewRoot(String)}
	 */
	// Java 1.6+ @Override
	public UIComponent matchComponentInViewRoot(FacesContext facesContext, String partialClientId) {
		return FacesContextHelperUtil.matchComponentInViewRoot(facesContext, partialClientId);
	}

	/**
	 * @see  {@link FacesContextHelper#navigate(String, String)}
	 */
	// Java 1.6+ @Override
	public void navigate(String fromAction, String outcome) {
		FacesContextHelperUtil.navigate(fromAction, outcome);
	}

	/**
	 * @see  {@link FacesContextHelper#navigate(String, String)}
	 */
	// Java 1.6+ @Override
	public void navigate(FacesContext facesContext, String fromAction, String outcome) {
		FacesContextHelperUtil.navigate(facesContext, fromAction, outcome);
	}

	/**
	 * @see  {@link FacesContextHelper#navigateTo(String)}
	 */
	// Java 1.6+ @Override
	public void navigateTo(String outcome) {
		FacesContextHelperUtil.navigateTo(outcome);
	}

	/**
	 * @see  {@link FacesContextHelper#navigateTo(String)}
	 */
	// Java 1.6+ @Override
	public void navigateTo(FacesContext facesContext, String outcome) {
		FacesContextHelperUtil.navigateTo(facesContext, outcome);
	}

	/**
	 * @see  {@link FacesContextHelper#recreateComponentTree()}
	 */
	// Java 1.6+ @Override
	public void recreateComponentTree() {
		FacesContextHelperUtil.recreateComponentTree();
	}

	/**
	 * @see  {@link FacesContextHelper#recreateComponentTree()}
	 */
	// Java 1.6+ @Override
	public void recreateComponentTree(FacesContext facesContext) {
		FacesContextHelperUtil.recreateComponentTree(facesContext);
	}

	/**
	 * @see  {@link FacesContextHelper#registerPhaseListener(PhaseListener)}
	 */
	// Java 1.6+ @Override
	public void registerPhaseListener(PhaseListener phaseListener) {
		FacesContextHelperUtil.registerPhaseListener(phaseListener);
	}

	/**
	 * @since  JSF 1.0
	 */
	// Java 1.6+ @Override
	public void release() {
		FacesContext.getCurrentInstance().release();
	}

	/**
	 * @see  {@link FacesContextHelper#removeChildrenFromComponentTree(String)}
	 */
	// Java 1.6+ @Override
	public void removeChildrenFromComponentTree(String clientId) {
		FacesContextHelperUtil.removeChildrenFromComponentTree(clientId);
	}

	/**
	 * @see  {@link FacesContextHelper#removeChildrenFromComponentTree(String)}
	 */
	// Java 1.6+ @Override
	public void removeChildrenFromComponentTree(FacesContext facesContext, String clientId) {
		FacesContextHelperUtil.removeChildrenFromComponentTree(facesContext, clientId);
	}

	/**
	 * @see  {@link FacesContextHelper#removeMessages(String)}
	 */
	// Java 1.6+ @Override
	public void removeMessages(String clientId) {
		FacesContextHelperUtil.removeMessages(clientId);
	}

	/**
	 * @see  {@link FacesContextHelper#removeMessages(String)}
	 */
	// Java 1.6+ @Override
	public void removeMessages(FacesContext facesContext, String clientId) {
		FacesContextHelperUtil.removeMessages(facesContext, clientId);
	}

	/**
	 * @see  {@link FacesContextHelper#removeMessagesForImmediateComponents()}
	 */
	// Java 1.6+ @Override
	public void removeMessagesForImmediateComponents() {
		FacesContextHelperUtil.removeMessagesForImmediateComponents();
	}

	/**
	 * @see  {@link FacesContextHelper#removeMessagesForImmediateComponents(UIComponent)}
	 */
	// Java 1.6+ @Override
	public void removeMessagesForImmediateComponents(UIComponent uiComponent) {
		FacesContextHelperUtil.removeMessagesForImmediateComponents(uiComponent);
	}

	/**
	 * @see  {@link FacesContextHelper#removeMessagesForImmediateComponents()}
	 */
	// Java 1.6+ @Override
	public void removeMessagesForImmediateComponents(FacesContext facesContext) {
		FacesContextHelperUtil.removeMessagesForImmediateComponents(facesContext);
	}

	/**
	 * @see  {@link FacesContextHelper#removeMessagesForImmediateComponents(UIComponent)}
	 */
	// Java 1.6+ @Override
	public void removeMessagesForImmediateComponents(FacesContext facesContext, UIComponent uiComponent) {
		FacesContextHelperUtil.removeMessagesForImmediateComponents(facesContext, uiComponent);
	}

	/**
	 * @see  {@link FacesContextHelper#removeParentFormFromComponentTree(UIComponent)}
	 */
	// Java 1.6+ @Override
	public void removeParentFormFromComponentTree(UIComponent uiComponent) {
		FacesContextHelperUtil.removeParentFormFromComponentTree(uiComponent);
	}

	/**
	 * @since  JSF 1.0
	 */
	// Java 1.6+ @Override
	public void renderResponse() {
		FacesContext.getCurrentInstance().renderResponse();
	}

	/**
	 * @see  {@link FacesContextHelper#resetView()}
	 */
	// Java 1.6+ @Override
	public void resetView() {
		FacesContextHelperUtil.resetView();
	}

	/**
	 * @see  {@link FacesContextHelper#resetView(boolean)}
	 */
	// Java 1.6+ @Override
	public void resetView(boolean renderResponse) {
		FacesContextHelperUtil.resetView();
	}

	/**
	 * @see  {@link FacesContextHelper#resetView()}
	 */
	// Java 1.6+ @Override
	public void resetView(FacesContext facesContext) {
		FacesContextHelperUtil.resetView(facesContext);
	}

	/**
	 * @see  {@link FacesContextHelper#resetView(boolean)}
	 */
	// Java 1.6+ @Override
	public void resetView(FacesContext facesContext, boolean renderResponse) {
		FacesContextHelperUtil.resetView(facesContext);
	}

	/**
	 * @see  {@link FacesContextHelper#resolveExpression(String)}
	 */
	// Java 1.6+ @Override
	public Object resolveExpression(String elExpression) {
		return FacesContextHelperUtil.resolveExpression(elExpression);
	}

	/**
	 * @see  {@link FacesContextHelper#resolveExpression(String)}
	 */
	// Java 1.6+ @Override
	public Object resolveExpression(FacesContext facesContext, String elExpression) {
		return FacesContextHelperUtil.resolveExpression(facesContext, elExpression);
	}

	/**
	 * @since  JSF 1.0
	 */
	// Java 1.6+ @Override
	public void responseComplete() {
		FacesContext.getCurrentInstance().responseComplete();
	}

	/**
	 * @see  {@link FacesContextHelper#setRequestAttribute(String, Object)}
	 */
	// Java 1.6+ @Override
	public void setRequestAttribute(String name, Object value) {
		FacesContextHelperUtil.setRequestAttribute(name, value);
	}

	/**
	 * @see  {@link FacesContextHelper#setRequestAttribute(String, Object)}
	 */
	// Java 1.6+ @Override
	public void setRequestAttribute(FacesContext facesContext, String name, Object value) {
		FacesContextHelperUtil.setRequestAttribute(facesContext, name, value);
	}

	/**
	 * @since  JSF 1.0
	 */
	// Java 1.6+ @Override
	public void setResponseStream(ResponseStream responseStream) {
		FacesContext.getCurrentInstance().setResponseStream(responseStream);
	}

	/**
	 * @since  JSF 1.0
	 */
	// Java 1.6+ @Override
	public void setResponseWriter(ResponseWriter responseWriter) {
		FacesContext.getCurrentInstance().setResponseWriter(responseWriter);
	}

	/**
	 * @see  {@link FacesContextHelper#setSessionAttribute(String, Object)}
	 */
	// Java 1.6+ @Override
	public void setSessionAttribute(String name, Object value) {
		FacesContextHelperUtil.setSessionAttribute(name, value);
	}

	/**
	 * @see  {@link FacesContextHelper#setSessionAttribute(String, Object)}
	 */
	// Java 1.6+ @Override
	public void setSessionAttribute(FacesContext facesContext, String name, Object value) {
		FacesContextHelperUtil.setSessionAttribute(facesContext, name, value);
	}

	/**
	 * @since  JSF 1.0
	 */
	// Java 1.6+ @Override
	public void setViewRoot(UIViewRoot viewRoot) {
		FacesContext.getCurrentInstance().setViewRoot(viewRoot);
	}
}
