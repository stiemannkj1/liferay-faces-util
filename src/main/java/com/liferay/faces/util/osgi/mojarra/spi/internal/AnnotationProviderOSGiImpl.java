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
package com.liferay.faces.util.osgi.mojarra.spi.internal;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.component.FacesComponent;
import javax.faces.component.behavior.FacesBehavior;
import javax.faces.convert.FacesConverter;
import javax.faces.event.NamedEvent;
import javax.faces.render.FacesBehaviorRenderer;
import javax.faces.render.FacesRenderer;
import javax.faces.validator.FacesValidator;
import javax.servlet.ServletContext;
import javax.servlet.annotation.HandlesTypes;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import com.liferay.faces.util.internal.TCCLUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.osgi.OSGiClassLoaderUtil;
import com.liferay.faces.util.osgi.internal.FacesBundleUtil;
import com.liferay.faces.util.osgi.internal.FacesBundlesHandlerBase;

import com.sun.faces.config.FacesInitializer;
import com.sun.faces.spi.AnnotationProvider;


/**
 * @author  Kyle Stiemann
 */
public class AnnotationProviderOSGiImpl extends AnnotationProvider {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(AnnotationProviderOSGiImpl.class);

	@Override
	public Map<Class<? extends Annotation>, Set<Class<?>>> getAnnotatedClasses(Set<URI> set) {

		Map<Class<? extends Annotation>, Set<Class<?>>> annotatedClasses;

		// Annotation scanning works correctly in thick wabs and wars.
		if (FacesBundleUtil.isCurrentWarThinWab()) {

			FacesBundlesHandlerBase<Map<Class<? extends Annotation>, Set<Class<?>>>> facesBundlesHandler =
				new FacesBundlesHandlerAnnotationProviderOSGiImpl(sc);
			annotatedClasses = Collections.unmodifiableMap(facesBundlesHandler.handleFacesBundles(sc));
		}
		else {
			annotatedClasses = wrappedAnnotationProvider.getAnnotatedClasses(set);
		}

		return annotatedClasses;
	}

	private static final class FacesBundlesHandlerAnnotationProviderOSGiImpl
		extends FacesBundlesHandlerBase<Map<Class<? extends Annotation>, Set<Class<?>>>> {

		// Private Final Data Members
		private final Set<Class<?>> annotationsHandledByMojarra;

		private FacesBundlesHandlerAnnotationProviderOSGiImpl(ServletContext servletContext) {

			final Set<Class<?>> annotationsHandledByMojarra = new HashSet<Class<?>>();
			Class<? extends FacesBundlesHandlerAnnotationProviderOSGiImpl> clazz = getClass();
			ClassLoader classLoader = TCCLUtil.getThreadContextClassLoaderOrDefault(clazz);

			try {

				Class<?> annotationScanningServletContainerInitializerClass = OSGiClassLoaderUtil.classForName(
						FacesInitializer.class.getName(), true, servletContext, classLoader);
				HandlesTypes handledTypes = annotationScanningServletContainerInitializerClass.getAnnotation(
						HandlesTypes.class);
				Class[] annotationsHandledByMojarraArray = handledTypes.value();
				annotationsHandledByMojarra.addAll(Arrays.<Class<?>>asList(annotationsHandledByMojarraArray));

				// This list of classes was obtained from the AnnotationProvider JavaDoc.
				annotationsHandledByMojarra.addAll(Arrays.<Class<?>>asList(FacesComponent.class, FacesConverter.class,
						FacesRenderer.class, FacesValidator.class, ManagedBean.class, NamedEvent.class,
						FacesBehavior.class, FacesBehaviorRenderer.class));
			}
			catch (ClassNotFoundException e) {
				logger.error(e);
			}
			catch (NoClassDefFoundError e) {
				logger.error(e);
			}

			if (!annotationsHandledByMojarra.isEmpty()) {
				this.annotationsHandledByMojarra = Collections.unmodifiableSet(annotationsHandledByMojarra);
			}
			else {
				this.annotationsHandledByMojarra = Collections.emptySet();
			}
		}

		private static Class<?> loadBundleClass(Bundle bundle, String className) {

			Class<?> clazz = null;

			try {
				clazz = bundle.loadClass(className);
			}
			catch (ClassNotFoundException e) {
				// no-op
			}
			catch (LinkageError e) {
				// no-op
			}

			return clazz;
		}

		@Override
		protected Map<Class<? extends Annotation>, Set<Class<?>>> getInitialReturnValueObject() {

			Map<Class<? extends Annotation>, Set<Class<?>>> annotatedClasses =
				new HashMap<Class<? extends Annotation>, Set<Class<?>>>();

			for (Class<?> annotation : annotationsHandledByMojarra) {
				annotatedClasses.put((Class<? extends Annotation>) annotation, new HashSet<Class<?>>());
			}

			return annotatedClasses;
		}

		@Override
		protected void handleFacesBundle(Long bundleKey, Bundle bundle,
			ReturnValueReference<Map<Class<? extends Annotation>, Set<Class<?>>>> returnValueReference) {

			BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

			if (bundleWiring != null) {

				Collection<String> classFilePaths = bundleWiring.listResources("/", "*.class",
						BundleWiring.LISTRESOURCES_RECURSE);

				for (String classFilePath : classFilePaths) {

					if (!FacesBundleUtil.shouldLoadClassWithBundle(classFilePath, bundleKey, bundle)) {
						continue;
					}

					String className = classFilePath.replaceAll("\\.class$", "").replace("/", ".");
					Class<?> clazz = loadBundleClass(bundle, className);

					if (clazz == null) {
						continue;
					}

					Annotation[] classAnnotations = clazz.getAnnotations();

					for (Annotation annotation : classAnnotations) {

						Class<? extends Annotation> annotationType = annotation.annotationType();

						if (annotationsHandledByMojarra.contains(annotationType)) {
							returnValueReference.get().get(annotationType).add(clazz);
						}
					}
				}
			}
		}
	}
}
