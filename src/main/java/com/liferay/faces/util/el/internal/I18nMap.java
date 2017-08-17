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
package com.liferay.faces.util.el.internal;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

import com.liferay.faces.util.cache.Cache;
import com.liferay.faces.util.cache.CacheFactory;
import com.liferay.faces.util.config.ApplicationConfig;
import com.liferay.faces.util.i18n.I18n;
import com.liferay.faces.util.i18n.I18nFactory;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class I18nMap extends I18nMapCompat implements SystemEventListener {

	// serialVersionUID
	private static final long serialVersionUID = 5549598732411060854L;

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(I18nMap.class);

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object get(Object key) {

		String message = null;

		if (key != null) {

			FacesContext facesContext = FacesContext.getCurrentInstance();
			UIViewRoot viewRoot = facesContext.getViewRoot();
			Locale locale = viewRoot.getLocale();

			if (locale == null) {
				Application application = facesContext.getApplication();
				locale = application.getDefaultLocale();
			}

			ExternalContext externalContext = facesContext.getExternalContext();
			I18n i18n = I18nFactory.getI18nInstance(externalContext);

			String keyAsString = key.toString();

			if (cacheEnabled) {

				String messageKey = keyAsString;

				if (locale != null) {
					messageKey = locale.toString().concat(keyAsString);
				}

				Map<String, Object> applicationMap = externalContext.getApplicationMap();
				Cache<String, String> messageCache = (Cache<String, String>) applicationMap.get(I18nMap.class
						.getName());

				if (messageCache != null) {
					message = messageCache.get(messageKey);
				}

				if (message == null) {
					message = i18n.getMessage(facesContext, locale, keyAsString);

					if ((message != null) && (messageCache != null)) {
						message = messageCache.put(messageKey, message);
					}
				}
			}
			else {
				message = i18n.getMessage(facesContext, locale, keyAsString);
			}
		}

		return message;
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isListenerForSource(Object source) {
		return (source instanceof ApplicationConfig);
	}

	@Override
	public Set<String> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void processEvent(SystemEvent systemEvent) throws AbortProcessingException {

		FacesContext startupFacesContext = FacesContext.getCurrentInstance();

		// Store the i18n message cache in the application map (as a Servlet Context attribute).
		if (startupFacesContext != null) {

			ExternalContext externalContext = startupFacesContext.getExternalContext();
			Map<String, Object> applicationMap = externalContext.getApplicationMap();
			Cache<String, String> facesResourceBundleCache = CacheFactory.<String, String>getConcurrentCacheInstance(
					externalContext, "com.liferay.faces.util.el.i18n.maxCacheSize");
			applicationMap.put(I18nMap.class.getName(), facesResourceBundleCache);
		}
		else {
			logger.error("Unable to store the i18n message cache in the application map");
		}
	}

	@Override
	public Object put(String key, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends String, ?> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object remove(Object key) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the message associated with the specified messageId according to the locale in the current FacesContext.
	 * This method is primarily meant to be called via EL, providing the implementation supports passing parameters
	 * (like JBoss EL).
	 *
	 * @param   messageId  The message key.
	 * @param   arg1       The first argument, assuming that the messageId has a {0} token.
	 *
	 * @return  The internationalized message.
	 */
	public String replace(String messageId, String arg1) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		UIViewRoot viewRoot = facesContext.getViewRoot();
		Locale locale = viewRoot.getLocale();

		if (locale == null) {
			Application application = facesContext.getApplication();
			locale = application.getDefaultLocale();
		}

		ExternalContext externalContext = facesContext.getExternalContext();
		I18n i18n = I18nFactory.getI18nInstance(externalContext);

		return i18n.getMessage(facesContext, locale, messageId, arg1);
	}

	/**
	 * Gets the message associated with the specified messageId according to the locale in the current FacesContext.
	 * This method is primarily meant to be called via EL, providing the implementation supports passing parameters
	 * (like JBoss EL).
	 *
	 * @param   messageId  The message key.
	 * @param   arg1       The first argument, assuming that the messageId has a {0} token.
	 * @param   arg2       The second argument, assuming that the messageId has a {1} token.
	 *
	 * @return  The internationalized message.
	 */
	public String replace(String messageId, String arg1, String arg2) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		UIViewRoot viewRoot = facesContext.getViewRoot();
		Locale locale = viewRoot.getLocale();

		if (locale == null) {
			Application application = facesContext.getApplication();
			locale = application.getDefaultLocale();
		}

		ExternalContext externalContext = facesContext.getExternalContext();
		I18n i18n = I18nFactory.getI18nInstance(externalContext);

		return i18n.getMessage(facesContext, locale, messageId, arg1, arg2);
	}

	/**
	 * Gets the message associated with the specified messageId according to the locale in the current FacesContext.
	 * This method is primarily meant to be called via EL, providing the implementation supports passing parameters
	 * (like JBoss EL).
	 *
	 * @param   messageId  The message key.
	 * @param   arg1       The first argument, assuming that the messageId has a {0} token.
	 * @param   arg2       The second argument, assuming that the messageId has a {1} token.
	 * @param   arg3       The third argument, assuming that the messageId has a {2} token.
	 *
	 * @return  The internationalized message.
	 */
	public String replace(String messageId, String arg1, String arg2, String arg3) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		UIViewRoot viewRoot = facesContext.getViewRoot();
		Locale locale = viewRoot.getLocale();

		if (locale == null) {
			Application application = facesContext.getApplication();
			locale = application.getDefaultLocale();
		}

		ExternalContext externalContext = facesContext.getExternalContext();
		I18n i18n = I18nFactory.getI18nInstance(externalContext);

		return i18n.getMessage(facesContext, locale, messageId, arg1, arg2, arg3);
	}

	/**
	 * Gets the message associated with the specified messageId according to the locale in the current FacesContext.
	 * This method is primarily meant to be called via EL, providing the implementation supports passing parameters
	 * (like JBoss EL).
	 *
	 * @param   messageId  The message key.
	 * @param   arg1       The first argument, assuming that the messageId has a {0} token.
	 * @param   arg2       The second argument, assuming that the messageId has a {1} token.
	 * @param   arg3       The third argument, assuming that the messageId has a {2} token.
	 * @param   arg4       The fourth argument, assuming that the messageId has a {3} token.
	 *
	 * @return  The internationalized message.
	 */
	public String replace(String messageId, String arg1, String arg2, String arg3, String arg4) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		UIViewRoot viewRoot = facesContext.getViewRoot();
		Locale locale = viewRoot.getLocale();

		if (locale == null) {
			Application application = facesContext.getApplication();
			locale = application.getDefaultLocale();
		}

		ExternalContext externalContext = facesContext.getExternalContext();
		I18n i18n = I18nFactory.getI18nInstance(externalContext);

		return i18n.getMessage(facesContext, locale, messageId, arg1, arg2, arg3, arg4);
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Object> values() {
		throw new UnsupportedOperationException();
	}
}
