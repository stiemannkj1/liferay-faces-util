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
package com.liferay.faces.util.client.internal;

import javax.servlet.http.HttpServletRequest;

import com.liferay.faces.util.client.BrowserSniffer;


/**
 * @author  Neil Griffin
 */
public class BrowserSnifferImpl extends LiferayPortalBrowserSnifferImpl implements BrowserSniffer {

	// Private Data Members
	private HttpServletRequest httpServletRequest;

	public BrowserSnifferImpl(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;
	}

	// Java 1.6+ @Override
	public boolean acceptsGzip() {
		return acceptsGzip(httpServletRequest);
	}

	// Java 1.6+ @Override
	public String getBrowserId() {
		return getBrowserId(httpServletRequest);
	}

	// Java 1.6+ @Override
	public float getMajorVersion() {
		return getMajorVersion(httpServletRequest);
	}

	// Java 1.6+ @Override
	public String getRevision() {
		return getRevision(httpServletRequest);
	}

	// Java 1.6+ @Override
	public String getVersion() {
		return getVersion(httpServletRequest);
	}

	// Java 1.6+ @Override
	public boolean isAir() {
		return isAir(httpServletRequest);
	}

	// Java 1.6+ @Override
	public boolean isAndroid() {
		return isAndroid(httpServletRequest);
	}

	// Java 1.6+ @Override
	public boolean isChrome() {
		return isChrome(httpServletRequest);
	}

	// Java 1.6+ @Override
	public boolean isFirefox() {
		return isFirefox(httpServletRequest);
	}

	// Java 1.6+ @Override
	public boolean isGecko() {
		return isGecko(httpServletRequest);
	}

	// Java 1.6+ @Override
	public boolean isIe() {
		return isIe(httpServletRequest);
	}

	// Java 1.6+ @Override
	public boolean isIeOnWin32() {
		return isIeOnWin32(httpServletRequest);
	}

	// Java 1.6+ @Override
	public boolean isIeOnWin64() {
		return isIeOnWin64(httpServletRequest);
	}

	// Java 1.6+ @Override
	public boolean isIpad() {
		String userAgent = getUserAgent(httpServletRequest);

		if (userAgent.contains("ipad")) {
			return true;
		}

		return false;
	}

	// Java 1.6+ @Override
	public boolean isIphone() {
		return isIphone(httpServletRequest);
	}

	// Java 1.6+ @Override
	public boolean isLinux() {
		return isLinux(httpServletRequest);
	}

	// Java 1.6+ @Override
	public boolean isMac() {
		return isMac(httpServletRequest);
	}

	// Java 1.6+ @Override
	public boolean isMobile() {
		return isMobile(httpServletRequest);
	}

	// Java 1.6+ @Override
	public boolean isMozilla() {
		return isMozilla(httpServletRequest);
	}

	// Java 1.6+ @Override
	public boolean isOpera() {
		return isOpera(httpServletRequest);
	}

	// Java 1.6+ @Override
	public boolean isRtf() {
		return isRtf(httpServletRequest);
	}

	// Java 1.6+ @Override
	public boolean isSafari() {
		return isSafari(httpServletRequest);
	}

	// Java 1.6+ @Override
	public boolean isSun() {
		return isSun(httpServletRequest);
	}

	// Java 1.6+ @Override
	public boolean isWebKit() {
		return isWebKit(httpServletRequest);
	}

	// Java 1.6+ @Override
	public boolean isWindows() {
		return isWindows(httpServletRequest);
	}
}
