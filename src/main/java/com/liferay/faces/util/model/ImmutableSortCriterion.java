/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.util.model;

import org.osgi.annotation.versioning.ConsumerType;


/**
 * @author  Neil Griffin
 */
@ConsumerType
public final class ImmutableSortCriterion extends SortCriterion {

	public ImmutableSortCriterion(String columnId, Order order) {
		super(columnId, order);
	}

	/**
	 * Throws {@link UnsupportedOperationException}.
	 *
	 * @param  columnId
	 */
	@Override
	public void setColumnId(String columnId) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Throws {@link UnsupportedOperationException}.
	 *
	 * @param  order
	 */
	@Override
	public void setOrder(Order order) {
		throw new UnsupportedOperationException();
	}
}
