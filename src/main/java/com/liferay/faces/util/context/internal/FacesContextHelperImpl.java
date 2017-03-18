/**
 * Copyright (c) 2000-2017 Liferay, Inc. All rights reserved.
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.component.ActionSource;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.http.HttpServletRequest;

import com.liferay.faces.util.component.ComponentUtil;
import com.liferay.faces.util.context.FacesContextHelper;
import com.liferay.faces.util.helper.BooleanHelper;
import com.liferay.faces.util.helper.IntegerHelper;
import com.liferay.faces.util.helper.LongHelper;
import com.liferay.faces.util.i18n.I18n;
import com.liferay.faces.util.i18n.I18nFactory;


/**
 * @author  Neil Griffin
 */
public class FacesContextHelperImpl implements FacesContextHelper, Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 5363868926989717581L;

	// Private Constants
	private static final String UNEXPECTED_ERROR_MSG_ID = "an-unexpected-error-occurred";
	private static final String SUCCESS_INFO_MSG_ID = "your-request-processed-successfully";

	// Java 1.6+ @Override
	public void addComponentErrorMessage(String clientId, String messageId) {
		addComponentErrorMessage(FacesContext.getCurrentInstance(), clientId, messageId);
	}

	// Java 1.6+ @Override
	public void addComponentErrorMessage(FacesContext facesContext, String clientId, String messageId) {
		addMessage(facesContext, clientId, FacesMessage.SEVERITY_ERROR, messageId);
	}

	// Java 1.6+ @Override
	public void addComponentErrorMessage(String clientId, String messageId, Object... arguments) {
		addComponentErrorMessage(FacesContext.getCurrentInstance(), clientId, messageId, arguments);
	}

	// Java 1.6+ @Override
	public void addComponentErrorMessage(FacesContext facesContext, String clientId, String messageId,
		Object... arguments) {
		addMessage(facesContext, clientId, FacesMessage.SEVERITY_ERROR, messageId, arguments);
	}

	// Java 1.6+ @Override
	public void addComponentInfoMessage(String clientId, String messageId) {
		addComponentInfoMessage(FacesContext.getCurrentInstance(), clientId, messageId);
	}

	// Java 1.6+ @Override
	public void addComponentInfoMessage(FacesContext facesContext, String clientId, String messageId) {
		addMessage(facesContext, clientId, FacesMessage.SEVERITY_INFO, messageId);
	}

	// Java 1.6+ @Override
	public void addComponentInfoMessage(String clientId, String messageId, Object... arguments) {
		addComponentInfoMessage(FacesContext.getCurrentInstance(), clientId, messageId, arguments);
	}

	// Java 1.6+ @Override
	public void addComponentInfoMessage(FacesContext facesContext, String clientId, String messageId,
		Object... arguments) {
		addMessage(facesContext, clientId, FacesMessage.SEVERITY_INFO, messageId, arguments);
	}

	// Java 1.6+ @Override
	public void addGlobalErrorMessage(String messageId) {
		addGlobalErrorMessage(FacesContext.getCurrentInstance(), messageId);
	}

	// Java 1.6+ @Override
	public void addGlobalErrorMessage(FacesContext facesContext, String messageId) {
		addComponentErrorMessage(facesContext, null, messageId);
	}

	// Java 1.6+ @Override
	public void addGlobalErrorMessage(String messageId, Object... arguments) {
		addGlobalErrorMessage(FacesContext.getCurrentInstance(), messageId, arguments);
	}

	// Java 1.6+ @Override
	public void addGlobalErrorMessage(FacesContext facesContext, String messageId, Object... arguments) {
		addComponentErrorMessage(facesContext, null, messageId, arguments);
	}

	// Java 1.6+ @Override
	public void addGlobalInfoMessage(String messageId) {
		addGlobalInfoMessage(FacesContext.getCurrentInstance(), messageId);
	}

	// Java 1.6+ @Override
	public void addGlobalInfoMessage(FacesContext facesContext, String messageId) {
		addComponentInfoMessage(facesContext, null, messageId);
	}

	// Java 1.6+ @Override
	public void addGlobalInfoMessage(String messageId, Object... arguments) {
		addGlobalInfoMessage(FacesContext.getCurrentInstance(), messageId, arguments);
	}

	// Java 1.6+ @Override
	public void addGlobalInfoMessage(FacesContext facesContext, String messageId, Object... arguments) {
		addComponentInfoMessage(facesContext, null, messageId, arguments);
	}

	/**
	 * @see  FacesContextHelper#addGlobalSuccessInfoMessage()
	 */
	// Java 1.6+ @Override
	public void addGlobalSuccessInfoMessage() {
		addGlobalSuccessInfoMessage(FacesContext.getCurrentInstance());
	}

	/**
	 * @see  FacesContextHelper#addGlobalSuccessInfoMessage()
	 */
	// Java 1.6+ @Override
	public void addGlobalSuccessInfoMessage(FacesContext facesContext) {
		addGlobalInfoMessage(facesContext, SUCCESS_INFO_MSG_ID);
	}

	/**
	 * @see  FacesContextHelper#addGlobalUnexpectedErrorMessage()
	 */
	// Java 1.6+ @Override
	public void addGlobalUnexpectedErrorMessage() {
		addGlobalUnexpectedErrorMessage(FacesContext.getCurrentInstance());
	}

	/**
	 * @see  FacesContextHelper#addGlobalUnexpectedErrorMessage()
	 */
	// Java 1.6+ @Override
	public void addGlobalUnexpectedErrorMessage(FacesContext facesContext) {
		addGlobalErrorMessage(facesContext, UNEXPECTED_ERROR_MSG_ID);
	}

	// Java 1.6+ @Override
	public void addMessage(String clientId, Severity severity, String messageId) {
		addMessage(FacesContext.getCurrentInstance(), clientId, severity, messageId);
	}

	// Java 1.6+ @Override
	public void addMessage(FacesContext facesContext, String clientId, Severity severity, String messageId) {

		Locale locale = getLocale(facesContext);
		I18n i18n = getI18n(facesContext);
		FacesMessage facesMessage = i18n.getFacesMessage(facesContext, locale, severity, messageId);
		facesContext.addMessage(clientId, facesMessage);
	}

	// Java 1.6+ @Override
	public void addMessage(String clientId, Severity severity, String messageId, Object... arguments) {
		addMessage(FacesContext.getCurrentInstance(), clientId, severity, messageId, arguments);
	}

	// Java 1.6+ @Override
	public void addMessage(FacesContext facesContext, String clientId, Severity severity, String messageId,
		Object... arguments) {

		Locale locale = getLocale(facesContext);
		I18n i18n = getI18n(facesContext);
		FacesMessage facesMessage = i18n.getFacesMessage(facesContext, locale, severity, messageId, arguments);
		facesContext.addMessage(clientId, facesMessage);
	}

	// Java 1.6+ @Override
	public FacesContext getFacesContext() {
		return getFacesContext(FacesContext.getCurrentInstance());
	}

	public FacesContext getFacesContext(FacesContext facesContext) {
		return facesContext;
	}

	// Java 1.6+ @Override
	public Locale getLocale() {
		return getLocale(FacesContext.getCurrentInstance());
	}

	// Java 1.6+ @Override
	public Locale getLocale(FacesContext facesContext) {

		UIViewRoot viewRoot = facesContext.getViewRoot();
		Locale locale = viewRoot.getLocale();

		// If the JSF ViewRoot didn't return a locale, then try and get it from the JSF Application.
		if (locale == null) {
			Application application = facesContext.getApplication();
			locale = application.getDefaultLocale();
		}

		// Otherwise, if we couldn't determine the locale, just use the server's default value.
		if (locale == null) {
			locale = Locale.getDefault();
		}

		return locale;
	}

	// Java 1.6+ @Override
	public String getMessage(String messageId) {
		return getMessage(FacesContext.getCurrentInstance(), messageId);
	}

	// Java 1.6+ @Override
	public String getMessage(FacesContext facesContext, String messageId) {
		return getMessage(facesContext, getLocale(facesContext), messageId);
	}

	// Java 1.6+ @Override
	public String getMessage(String messageId, Object... arguments) {
		return getMessage(FacesContext.getCurrentInstance(), messageId, arguments);
	}

	// Java 1.6+ @Override
	public String getMessage(Locale locale, String messageId) {
		return getMessage(FacesContext.getCurrentInstance(), locale, messageId);
	}

	// Java 1.6+ @Override
	public String getMessage(FacesContext facesContext, String messageId, Object... arguments) {

		I18n i18n = getI18n(facesContext);

		return i18n.getMessage(facesContext, getLocale(facesContext), messageId, arguments);
	}

	// Java 1.6+ @Override
	public String getMessage(FacesContext facesContext, Locale locale, String messageId) {

		I18n i18n = getI18n(facesContext);

		return i18n.getMessage(facesContext, locale, messageId);
	}

	// Java 1.6+ @Override
	public String getMessage(Locale locale, String messageId, Object... arguments) {
		return getMessage(FacesContext.getCurrentInstance(), locale, messageId, arguments);
	}

	// Java 1.6+ @Override
	public String getMessage(FacesContext facesContext, Locale locale, String messageId, Object... arguments) {

		I18n i18n = getI18n(facesContext);

		return i18n.getMessage(facesContext, locale, messageId, arguments);
	}

	// Java 1.6+ @Override
	public String getNamespace() {
		return getNamespace(FacesContext.getCurrentInstance());
	}

	// Java 1.6+ @Override
	public String getNamespace(FacesContext facesContext) {
		return facesContext.getExternalContext().encodeNamespace("");
	}

	// Java 1.6+ @Override
	public UIForm getParentForm(final UIComponent uiComponent) {

		UIComponent parent = uiComponent;

		while ((parent != null) && !(parent instanceof UIForm)) {
			parent = parent.getParent();
		}

		return (UIForm) parent;
	}

	// Java 1.6+ @Override
	public Object getRequestAttribute(String name) {
		return getRequestAttribute(FacesContext.getCurrentInstance(), name);
	}

	// Java 1.6+ @Override
	public Object getRequestAttribute(FacesContext facesContext, String name) {

		ExternalContext externalContext = facesContext.getExternalContext();
		HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();

		return httpServletRequest.getAttribute(name);
	}

	// Java 1.6+ @Override
	public String getRequestContextPath() {
		return getRequestContextPath(FacesContext.getCurrentInstance());
	}

	// Java 1.6+ @Override
	public String getRequestContextPath(FacesContext facesContext) {

		ExternalContext externalContext = facesContext.getExternalContext();

		return externalContext.getRequestContextPath();
	}

	// Java 1.6+ @Override
	public String getRequestParameter(String name) {
		return getRequestParameter(FacesContext.getCurrentInstance(), name);
	}

	// Java 1.6+ @Override
	public String getRequestParameter(FacesContext facesContext, String name) {

		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();

		return requestParameterMap.get(name);
	}

	// Java 1.6+ @Override
	public boolean getRequestParameterAsBool(String name, boolean defaultValue) {
		return getRequestParameterAsBool(FacesContext.getCurrentInstance(), name, defaultValue);
	}

	// Java 1.6+ @Override
	public boolean getRequestParameterAsBool(FacesContext facesContext, String name, boolean defaultValue) {
		return BooleanHelper.toBoolean(getRequestParameter(facesContext, name), defaultValue);
	}

	// Java 1.6+ @Override
	public int getRequestParameterAsInt(String name, int defaultValue) {
		return getRequestParameterAsInt(FacesContext.getCurrentInstance(), name, defaultValue);
	}

	// Java 1.6+ @Override
	public int getRequestParameterAsInt(FacesContext facesContext, String name, int defaultValue) {
		return IntegerHelper.toInteger(getRequestParameter(facesContext, name), defaultValue);
	}

	// Java 1.6+ @Override
	public long getRequestParameterAsLong(String name, long defaultValue) {
		return getRequestParameterAsLong(FacesContext.getCurrentInstance(), name, defaultValue);
	}

	// Java 1.6+ @Override
	public long getRequestParameterAsLong(FacesContext facesContext, String name, long defaultValue) {
		return LongHelper.toLong(getRequestParameter(facesContext, name), defaultValue);
	}

	// Java 1.6+ @Override
	public String getRequestParameterFromMap(String name) {
		return getRequestParameterFromMap(FacesContext.getCurrentInstance(), name);
	}

	// Java 1.6+ @Override
	public String getRequestParameterFromMap(FacesContext facesContext, String name) {

		ExternalContext externalContext = facesContext.getExternalContext();

		return externalContext.getRequestParameterMap().get(name);
	}

	// Java 1.6+ @Override
	public Map<String, String> getRequestParameterMap() {
		return getRequestParameterMap(FacesContext.getCurrentInstance());
	}

	// Java 1.6+ @Override
	public Map<String, String> getRequestParameterMap(FacesContext facesContext) {

		ExternalContext externalContext = facesContext.getExternalContext();

		return externalContext.getRequestParameterMap();
	}

	// Java 1.6+ @Override
	public String getRequestQueryString() {
		return getRequestQueryString(FacesContext.getCurrentInstance());
	}

	// Java 1.6+ @Override
	public String getRequestQueryString(FacesContext facesContext) {
		return (String) getRequestAttribute(facesContext, "javax.servlet.forward.query_string");
	}

	// Java 1.6+ @Override
	public String getRequestQueryStringParameter(String name) {
		return getRequestQueryStringParameter(FacesContext.getCurrentInstance(), name);
	}

	// Java 1.6+ @Override
	public String getRequestQueryStringParameter(FacesContext facesContext, String name) {

		String value = null;
		String queryString = getRequestQueryString(facesContext);

		if (queryString != null) {
			String[] queryStringTokens = queryString.split("&");
			boolean found = false;

			for (int i = 0; (!found && (i < queryStringTokens.length)); i++) {
				String nameValuePair = queryStringTokens[i];
				String[] nameValuePairArray = nameValuePair.split("=");
				found = nameValuePairArray[0].equals(name);

				if (found && (nameValuePairArray.length > 1)) {
					value = nameValuePairArray[1];
				}
			}
		}

		return value;
	}

	// Java 1.6+ @Override
	public Object getSession(boolean create) {
		return getSession(FacesContext.getCurrentInstance(), create);
	}

	// Java 1.6+ @Override
	public Object getSession(FacesContext facesContext, boolean create) {

		ExternalContext externalContext = facesContext.getExternalContext();

		return externalContext.getSession(create);
	}

	// Java 1.6+ @Override
	public Object getSessionAttribute(String name) {
		return getSessionAttribute(FacesContext.getCurrentInstance(), name);
	}

	// Java 1.6+ @Override
	public Object getSessionAttribute(FacesContext facesContext, String name) {

		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();

		return sessionMap.get(name);
	}

	// Java 1.6+ @Override
	public UIComponent matchComponentInHierarchy(UIComponent parent, String partialClientId) {
		return matchComponentInHierarchy(FacesContext.getCurrentInstance(), parent, partialClientId);
	}

	// Java 1.6+ @Override
	public UIComponent matchComponentInHierarchy(FacesContext facesContext, UIComponent parent,
		String partialClientId) {
		return ComponentUtil.matchComponentInHierarchy(facesContext, parent, partialClientId);
	}

	// Java 1.6+ @Override
	public UIComponent matchComponentInViewRoot(String partialClientId) {
		return matchComponentInViewRoot(FacesContext.getCurrentInstance(), partialClientId);
	}

	// Java 1.6+ @Override
	public UIComponent matchComponentInViewRoot(FacesContext facesContext, String partialClientId) {

		UIViewRoot viewRoot = facesContext.getViewRoot();

		return matchComponentInHierarchy(facesContext, viewRoot, partialClientId);
	}

	// Java 1.6+ @Override
	public void navigate(String fromAction, String outcome) {
		navigate(FacesContext.getCurrentInstance(), fromAction, outcome);
	}

	// Java 1.6+ @Override
	public void navigate(FacesContext facesContext, String fromAction, String outcome) {

		Application application = facesContext.getApplication();
		NavigationHandler navigationHandler = application.getNavigationHandler();
		navigationHandler.handleNavigation(facesContext, fromAction, outcome);
	}

	// Java 1.6+ @Override
	public void navigateTo(String outcome) {
		navigateTo(FacesContext.getCurrentInstance(), outcome);
	}

	// Java 1.6+ @Override
	public void navigateTo(FacesContext facesContext, String outcome) {
		navigate(facesContext, null, outcome);
	}

	// Java 1.6+ @Override
	public void recreateComponentTree() {
		recreateComponentTree(FacesContext.getCurrentInstance());
	}

	// Java 1.6+ @Override
	public void recreateComponentTree(FacesContext facesContext) {

		Application application = facesContext.getApplication();
		ViewHandler viewHandler = application.getViewHandler();
		UIViewRoot oldViewRoot = facesContext.getViewRoot();
		UIViewRoot viewRoot = viewHandler.createView(facesContext, oldViewRoot.getViewId());
		facesContext.setViewRoot(viewRoot);
		facesContext.renderResponse();
	}

	// Java 1.6+ @Override
	public void registerPhaseListener(PhaseListener phaseListener) throws IllegalStateException {

		LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(
				FactoryFinder.LIFECYCLE_FACTORY);

		for (Iterator<String> lifecycleIds = lifecycleFactory.getLifecycleIds(); lifecycleIds.hasNext();) {
			String lifecycleId = lifecycleIds.next();
			lifecycleFactory.getLifecycle(lifecycleId).addPhaseListener(phaseListener);
		}
	}

	// Java 1.6+ @Override
	public void removeChildrenFromComponentTree(String clientId) {
		removeChildrenFromComponentTree(FacesContext.getCurrentInstance(), clientId);
	}

	// Java 1.6+ @Override
	public void removeChildrenFromComponentTree(FacesContext facesContext, String clientId) {

		UIComponent uiComponent = facesContext.getViewRoot().findComponent(clientId);

		if (uiComponent != null) {
			uiComponent.getChildren().clear();
			uiComponent.getFacets().clear();
		}
	}

	// Java 1.6+ @Override
	public void removeMessages(String clientId) {
		removeMessages(FacesContext.getCurrentInstance(), clientId);
	}

	// Java 1.6+ @Override
	public void removeMessages(FacesContext facesContext, String clientId) {

		Iterator<FacesMessage> facesMessages = facesContext.getMessages(clientId);

		while (facesMessages.hasNext()) {
			facesMessages.next();
			facesMessages.remove();
		}
	}

	// Java 1.6+ @Override
	public void removeMessagesForImmediateComponents() {
		removeMessagesForImmediateComponents(FacesContext.getCurrentInstance());
	}

	// Java 1.6+ @Override
	public void removeMessagesForImmediateComponents(FacesContext facesContext) {
		removeMessagesForImmediateComponents(facesContext, facesContext.getViewRoot());
	}

	// Java 1.6+ @Override
	public void removeMessagesForImmediateComponents(UIComponent uiComponent) {
		removeMessagesForImmediateComponents(FacesContext.getCurrentInstance(), uiComponent);
	}

	// Java 1.6+ @Override
	public void removeMessagesForImmediateComponents(FacesContext facesContext, UIComponent uiComponent) {

		if (uiComponent instanceof ActionSource) {

			ActionSource actionSource = (ActionSource) uiComponent;

			if (actionSource.isImmediate()) {
				removeMessages(facesContext, uiComponent.getClientId(facesContext));
			}
		}
		else if (uiComponent instanceof EditableValueHolder) {

			EditableValueHolder editableValueHolder = (EditableValueHolder) uiComponent;

			if (editableValueHolder.isImmediate()) {
				removeMessages(facesContext, uiComponent.getClientId(facesContext));
			}
		}

		List<UIComponent> childComponents = uiComponent.getChildren();

		for (UIComponent childComponent : childComponents) {
			removeMessagesForImmediateComponents(facesContext, childComponent);
		}
	}

	// Java 1.6+ @Override
	public void removeParentFormFromComponentTree(final UIComponent uiComponent) {

		UIComponent form = getParentForm(uiComponent);

		if (form != null) {
			form.getChildren().clear();
			form.getFacets().clear();
		}
	}

	// Java 1.6+ @Override
	public void resetView() {
		resetView(FacesContext.getCurrentInstance());
	}

	// Java 1.6+ @Override
	public void resetView(FacesContext facesContext) {
		resetView(facesContext, true);
	}

	// Java 1.6+ @Override
	public void resetView(boolean renderResponse) {
		resetView(FacesContext.getCurrentInstance(), renderResponse);
	}

	// Java 1.6+ @Override
	public void resetView(FacesContext facesContext, boolean renderResponse) {

		Application application = facesContext.getApplication();
		ViewHandler viewHandler = application.getViewHandler();
		UIViewRoot viewRoot = facesContext.getViewRoot();
		UIViewRoot emptyView = viewHandler.createView(facesContext, viewRoot.getViewId());
		facesContext.setViewRoot(emptyView);

		if (renderResponse) {
			facesContext.renderResponse();
		}
	}

	// Java 1.6+ @Override
	public Object resolveExpression(String elExpression) {
		return resolveExpression(FacesContext.getCurrentInstance(), elExpression);
	}

	// Java 1.6+ @Override
	public Object resolveExpression(FacesContext facesContext, String elExpression) {

		Application application = facesContext.getApplication();
		ELResolver elResolver = application.getELResolver();
		ELContext elContext = facesContext.getELContext();

		return elResolver.getValue(elContext, null, elExpression);
	}

	// Java 1.6+ @Override
	public void setRequestAttribute(String name, Object value) {
		setRequestAttribute(FacesContext.getCurrentInstance(), name, value);
	}

	// Java 1.6+ @Override
	public void setRequestAttribute(FacesContext facesContext, String name, Object value) {

		ExternalContext externalContext = facesContext.getExternalContext();
		HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();
		httpServletRequest.setAttribute(name, value);
	}

	// Java 1.6+ @Override
	public void setSessionAttribute(String name, Object value) {
		setSessionAttribute(FacesContext.getCurrentInstance(), name, value);
	}

	// Java 1.6+ @Override
	public void setSessionAttribute(FacesContext facesContext, String name, Object value) {

		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		sessionMap.put(name, value);
	}

	private I18n getI18n(FacesContext facesContext) {

		ExternalContext externalContext = facesContext.getExternalContext();

		return I18nFactory.getI18nInstance(externalContext);
	}
}
