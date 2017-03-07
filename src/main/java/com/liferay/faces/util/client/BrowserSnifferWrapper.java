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
package com.liferay.faces.util.client;

import com.liferay.faces.util.helper.Wrapper;


/**
 * @author  Neil Griffin
 */
public abstract class BrowserSnifferWrapper implements BrowserSniffer, Wrapper<BrowserSniffer> {

	// Java 1.6+ @Override
	public abstract BrowserSniffer getWrapped();

	// Java 1.6+ @Override
	public boolean acceptsGzip() {
		return getWrapped().acceptsGzip();
	}

	// Java 1.6+ @Override
	public String getBrowserId() {
		return getWrapped().getBrowserId();
	}

	// Java 1.6+ @Override
	public float getMajorVersion() {
		return getWrapped().getMajorVersion();
	}

	// Java 1.6+ @Override
	public String getRevision() {
		return getWrapped().getRevision();
	}

	// Java 1.6+ @Override
	public String getVersion() {
		return getWrapped().getVersion();
	}

	// Java 1.6+ @Override
	public boolean isAir() {
		return getWrapped().isAir();
	}

	// Java 1.6+ @Override
	public boolean isAndroid() {
		return getWrapped().isAndroid();
	}

	// Java 1.6+ @Override
	public boolean isChrome() {
		return getWrapped().isChrome();
	}

	// Java 1.6+ @Override
	public boolean isFirefox() {
		return getWrapped().isFirefox();
	}

	// Java 1.6+ @Override
	public boolean isGecko() {
		return getWrapped().isGecko();
	}

	// Java 1.6+ @Override
	public boolean isIe() {
		return getWrapped().isIe();
	}

	// Java 1.6+ @Override
	public boolean isIeOnWin32() {
		return getWrapped().isIeOnWin32();
	}

	// Java 1.6+ @Override
	public boolean isIeOnWin64() {
		return getWrapped().isIeOnWin64();
	}

	// Java 1.6+ @Override
	public boolean isIphone() {
		return getWrapped().isIphone();
	}

	// Java 1.6+ @Override
	public boolean isLinux() {
		return getWrapped().isLinux();
	}

	// Java 1.6+ @Override
	public boolean isMac() {
		return getWrapped().isMac();
	}

	// Java 1.6+ @Override
	public boolean isMobile() {
		return getWrapped().isMobile();
	}

	// Java 1.6+ @Override
	public boolean isMozilla() {
		return getWrapped().isMozilla();
	}

	// Java 1.6+ @Override
	public boolean isOpera() {
		return getWrapped().isOpera();
	}

	// Java 1.6+ @Override
	public boolean isRtf() {
		return getWrapped().isRtf();
	}

	// Java 1.6+ @Override
	public boolean isSafari() {
		return getWrapped().isSafari();
	}

	// Java 1.6+ @Override
	public boolean isSun() {
		return getWrapped().isSun();
	}

	// Java 1.6+ @Override
	public boolean isWebKit() {
		return getWrapped().isWebKit();
	}

	// Java 1.6+ @Override
	public boolean isWindows() {
		return getWrapped().isWindows();
	}
}
