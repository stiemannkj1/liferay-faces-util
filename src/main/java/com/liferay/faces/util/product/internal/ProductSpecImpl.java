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

import com.liferay.faces.util.product.Product;


/**
 * @author  Kyle Stiemann
 */
public class ProductSpecImpl implements Product {

	// Private Members
	private String specTitle;
	private Product wrappedProduct;

	public ProductSpecImpl(String specTitle, Product... specImplProducts) {

		for (int i = 0; i < specImplProducts.length; i++) {

			Product specImplProduct = specImplProducts[i];

			if (specImplProduct.isDetected() || (i == (specImplProducts.length - 1))) {

				this.wrappedProduct = specImplProduct;
				this.specTitle = specImplProduct.getTitle();
				break;
			}
		}

		if (this.wrappedProduct == null) {

			this.wrappedProduct = new ProductBaseImpl();
			this.specTitle = specTitle;
		}
	}

	// Java 1.6+ @Override
	public int getBuildId() {
		return wrappedProduct.getBuildId();
	}

	// Java 1.6+ @Override
	public int getMajorVersion() {
		return wrappedProduct.getMajorVersion();
	}

	// Java 1.6+ @Override
	public int getMinorVersion() {
		return wrappedProduct.getMinorVersion();
	}

	// Java 1.6+ @Override
	public int getPatchVersion() {
		return wrappedProduct.getPatchVersion();
	}

	// Java 1.6+ @Override
	public String getTitle() {
		return specTitle;
	}

	// Java 1.6+ @Override
	public String getVersion() {
		return wrappedProduct.getVersion();
	}

	// Java 1.6+ @Override
	public boolean isDetected() {
		return wrappedProduct.isDetected();
	}

	// Java 1.6+ @Override
	public String toString() {
		return wrappedProduct.toString();
	}
}
