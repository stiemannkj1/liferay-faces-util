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

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class ProductWebLogicImpl extends ProductBaseImpl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ProductWebLogicImpl.class);

	public ProductWebLogicImpl() {

		try {
			this.title = "WebLogic";

			Class<?> clazz = Class.forName("weblogic.deploy.api.shared.PlanConstants");
			init(clazz, "WebLogic");
		}
		catch (Exception e) {
			// Ignore -- WebLogic is likely not present.
		}
	}
}
