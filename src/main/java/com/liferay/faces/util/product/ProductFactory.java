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
package com.liferay.faces.util.product;

import java.util.Iterator;
import java.util.ServiceLoader;


/**
 * @author  Kyle Stiemann
 */
public abstract class ProductFactory {

	private static final ProductFactory productFactory;

	static {

		ServiceLoader<ProductFactory> serviceLoader = ServiceLoader.load(ProductFactory.class);

		if (serviceLoader != null) {

			Iterator<ProductFactory> iterator = serviceLoader.iterator();

			ProductFactory productFactoryImpl = null;

			while ((productFactoryImpl == null) && iterator.hasNext()) {
				productFactoryImpl = iterator.next();
			}

			if (productFactoryImpl == null) {
				throw new NullPointerException("Unable locate service for " + ProductFactory.class.getName());
			}

			productFactory = productFactoryImpl;
		}
		else {
			throw new NullPointerException("Unable to acquire ServiceLoader for " + ProductFactory.class.getName());
		}
	}

	/**
	 * Returns the product associated with the specified productId.
	 *
	 * @param   productId  The id of the product.
	 *
	 * @return  The product associated with the specified productId.
	 */
	public static final Product getProduct(ProductId productId) {
		return productFactory.getProductImplementation(productId);
	}

	public abstract Product getProductImplementation(ProductId product);
}
