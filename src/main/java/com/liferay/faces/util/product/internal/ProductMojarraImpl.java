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
package com.liferay.faces.util.product.internal;

/**
 * @author  Neil Griffin
 * @author  Kyle Stiemann
 */
public class ProductMojarraImpl extends ProductBaseImpl {

	// Private Data Members
	private String toStringValue;

	public ProductMojarraImpl() {

		try {
			this.title = "Mojarra";

			Class<?> jsfImplClass = Class.forName("com.sun.faces.RIConstants");
			init(jsfImplClass, "Mojarra");

			// If running on WebLogic 12c (12.1.x), then the version typically looks like "1.0.0.0_2-1-20" or
			// "2.0.0.0_2-1-20"
			String version = getVersion();

			if ((version != null) && (version.startsWith("1.0.0.0_") || version.startsWith("2.0.0.0_"))) {
				version = version.substring("x.0.0.0_".length()).replaceAll("[-]", ".");
				initVersionInfo(version);
			}
		}
		catch (Exception e) {
			// Ignore -- JSF implementation is likely not present.
		}
	}

	// Java 1.6+ @Override
	public String toString() {

		if (toStringValue == null) {
			toStringValue = super.toString();

			// Some versions of Mojarra are mislabeled "-SNAPSHOT" (i.e.: "1.2_15-20100816-SNAPSHOT")
			if (toStringValue != null) {
				int pos = toStringValue.indexOf("-SNAPSHOT");

				if (pos > 0) {
					toStringValue = toStringValue.substring(0, pos);
				}
			}
		}

		return toStringValue;

	}
}
