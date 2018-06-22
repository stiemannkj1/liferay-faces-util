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
package com.liferay.faces.util.osgi.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;


/**
 * @author  Kyle Stiemann
 */
public abstract class FacesBundlesHandlerBase<ReturnValueType> {

	public final ReturnValueType handleFacesBundles(Object context, Long... bundleKeysToSkipArray) {

		Map<Long, Bundle> facesBundles = null;

		if (context != null) {
			facesBundles = FacesBundleUtil.getFacesBundles(context);
		}

		return handleFacesBundles(facesBundles, bundleKeysToSkipArray);
	}

	public final ReturnValueType handleFacesBundles(Map<Long, Bundle> facesBundles, Long... bundleKeysToSkipArray) {

		ReturnValueType returnValueObject = getInitialReturnValueObject();
		ReturnValueReference<ReturnValueType> returnValueReference = new ReturnValueReference<ReturnValueType>(
				returnValueObject);

		if ((facesBundles != null) && !facesBundles.isEmpty()) {

			Set<Map.Entry<Long, Bundle>> entrySet = facesBundles.entrySet();
			List<Long> bundleKeysToSkip = Arrays.asList(bundleKeysToSkipArray);

			for (Map.Entry<Long, Bundle> entry : entrySet) {

				Long bundleKey = entry.getKey();

				if (bundleKeysToSkip.contains(bundleKey)) {
					continue;
				}

				Bundle bundle = entry.getValue();

				if (FacesBundleUtil.CURRENT_WAB_KEY.equals(bundleKey)) {
					handleCurrentFacesWab(bundleKey, bundle, returnValueReference);
				}
				else {
					handleFacesBundle(bundleKey, bundle, returnValueReference);
				}

				if (skipHandlingRemaingFacesBundles(returnValueReference)) {
					break;
				}
			}
		}

		return returnValueReference.get();
	}

	protected abstract ReturnValueType getInitialReturnValueObject();

	protected abstract void handleFacesBundle(Long bundleKey, Bundle bundle,
		ReturnValueReference<ReturnValueType> returnValueReference);

	protected void handleCurrentFacesWab(Long bundleKey, Bundle currentFacesWab,
		ReturnValueReference<ReturnValueType> returnValueReference) {
		handleFacesBundle(bundleKey, currentFacesWab, returnValueReference);
	}

	protected boolean skipHandlingRemaingFacesBundles(ReturnValueReference<ReturnValueType> returnValueReference) {
		return false;
	}

	protected static final class ReturnValueReference<ReturnValueType> {

		// Private Data Members
		private ReturnValueType returnValueObject;

		public ReturnValueReference(ReturnValueType returnValueObject) {
			this.returnValueObject = returnValueObject;
		}

		public ReturnValueType get() {
			return returnValueObject;
		}

		public boolean isEmpty() {
			return returnValueObject == null;
		}

		public void set(ReturnValueType returnValueObject) {
			this.returnValueObject = returnValueObject;
		}
	}
}
