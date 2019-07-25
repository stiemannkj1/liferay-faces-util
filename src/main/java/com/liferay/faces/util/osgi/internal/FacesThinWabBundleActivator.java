/**
 * Copyright (c) 2000-2019 Liferay, Inc. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

/**
 *
 * @author kylestiemann
 */
@Component(immediate = true)
public final class FacesThinWabBundleActivator {
    
    // Private Data Members
	@Reference
	private LogService logService;

	private static boolean isFacesWab(Bundle bundle) {

		Dictionary<String, String> headers = bundle.getHeaders();
		String importPackageHeader = headers.get("Import-Package");

		return FacesBundlesHandlerBase.isWab(bundle) && (importPackageHeader != null) && importPackageHeader.contains("javax.faces");
	}

	@Activate
	/* package-private */ synchronized void activate(BundleContext bundleContext) throws BundleException {

		// Refresh deployed Faces bundles to ensure that ServiceBuilder bytecode weaving occurs even on bundles deployed
        // before the weaver was activated.
        List<Bundle> facesWabs = new ArrayList<Bundle>();
		Bundle[] bundles = bundleContext.getBundles();

		for (Bundle bundle : bundles) {

			if (isFacesWab(bundle)) {
				facesWabs.add(bundle);
			}
		}

		if (!facesWabs.isEmpty()) {

			Iterator<Bundle> iterator = facesWabs.iterator();

			while (iterator.hasNext()) {

				Bundle bundle = iterator.next();
				int facesWabState = bundle.getState();

				if ((facesWabState == Bundle.STARTING) || (facesWabState == Bundle.ACTIVE)) {
                    
                    try {
                        
                        bundle.stop();
                        bundle.start();
                    }
                    catch (BundleException e) {
                        logService.log(LogService.LOG_ERROR,
                            "Failed to restart " + bundle.getSymbolicName() + " WAB due to the following error:", e);
                    }
				}
			}
		}
	}
}
