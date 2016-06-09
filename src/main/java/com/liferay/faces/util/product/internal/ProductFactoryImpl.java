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

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;
import com.liferay.faces.util.product.ProductId;


/**
 * @author  Kyle Stiemann
 */
public class ProductFactoryImpl extends ProductFactory {

	private static final Map<ProductId, Product> PRODUCTS;

	static {

		Map<ProductId, Product> productMap = new EnumMap<ProductId, Product>(ProductId.class);
		productMap.put(ProductId.ANGULARBEANS, new ProductAngularBeansImpl());
		productMap.put(ProductId.ANGULARFACES, new ProductAngularFacesImpl());
		productMap.put(ProductId.BOOTSFACES, new ProductBootsFacesImpl());
		productMap.put(ProductId.BUTTERFACES, new ProductButterFacesImpl());
		productMap.put(ProductId.DELTASPIKE, new ProductDeltaSpikeImpl());
		productMap.put(ProductId.ICEFACES, new ProductICEfacesImpl());
		productMap.put(ProductId.LIFERAY_FACES_ALLOY, new ProductLiferayFacesAlloyImpl());
		productMap.put(ProductId.LIFERAY_FACES_BRIDGE, new ProductLiferayFacesBridgeImpl());
		productMap.put(ProductId.LIFERAY_FACES_METAL, new ProductLiferayFacesMetalImpl());
		productMap.put(ProductId.LIFERAY_FACES_PORTAL, new ProductLiferayFacesPortalImpl());
		productMap.put(ProductId.LIFERAY_FACES_SHOWCASE, new ProductLiferayFacesShowcaseImpl());
		productMap.put(ProductId.LIFERAY_FACES_UTIL, new ProductLiferayFacesUtilImpl());
		productMap.put(ProductId.LIFERAY_PORTAL, new ProductLiferayPortalImpl());

		Product productMojarraImpl = new ProductMojarraImpl();
		productMap.put(ProductId.MOJARRA, productMojarraImpl);

		Product productMyfacesImpl = new ProductMyfacesImpl();
		productMap.put(ProductId.MYFACES, productMyfacesImpl);
		productMap.put(ProductId.JSF, new ProductSpecImpl("JSF", productMojarraImpl, productMyfacesImpl));
		productMap.put(ProductId.OMNIFACES, new ProductOmniFacesImpl());
		productMap.put(ProductId.PLUTO, new ProductPlutoImpl());
		productMap.put(ProductId.PRIMEFACES, new ProductPrimeFacesImpl());
		productMap.put(ProductId.PRIMEFACES_EXTENSIONS, new ProductPrimeFacesExtensionsImpl());
		productMap.put(ProductId.RESIN, new ProductResinImpl());
		productMap.put(ProductId.RICHFACES, new ProductRichFacesImpl());
		productMap.put(ProductId.SPRING_FRAMEWORK, new ProductSpringFrameworkImpl());

		Product productWeldImpl = new ProductWeldImpl();
		productMap.put(ProductId.WELD, productWeldImpl);

		Product productOpenWebBeansImpl = new ProductOpenWebBeansImpl();
		productMap.put(ProductId.OPEN_WEB_BEANS, productOpenWebBeansImpl);
		productMap.put(ProductId.CDI, new ProductSpecImpl("CDI", productWeldImpl, productOpenWebBeansImpl));
		productMap.put(ProductId.WILDFLY, new ProductWildFlyImpl());
		PRODUCTS = Collections.unmodifiableMap(productMap);
	}

	@Override
	public Product getProductImplementation(ProductId productId) {
		return PRODUCTS.get(productId);
	}
}
