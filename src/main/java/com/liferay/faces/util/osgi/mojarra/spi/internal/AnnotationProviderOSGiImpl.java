/**
 * Copyright (c) 2000-2017 Liferay, Inc. All rights reserved.
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
import java.net.URL;
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
import javax.servlet.annotation.HandlesTypes;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.osgi.FacesBundleUtil;

import com.sun.faces.config.FacesInitializer;
import com.sun.faces.spi.AnnotationProvider;


/**
 * @author  Kyle Stiemann
 */
public class AnnotationProviderOSGiImpl extends AnnotationProvider {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(AnnotationProviderOSGiImpl.class);

	// Private Constants
	private static final Set<Class<?>> ANNOTATIONS_HANDLED_BY_MOJARRA;

	static {

		final Set<Class<?>> annotationsHandledByMojarra = new HashSet<Class<?>>();

		try {

			Class<?> annotationScanningServletContainerInitializerClass = Class.forName(FacesInitializer.class
					.getName());
			HandlesTypes handledTypes = annotationScanningServletContainerInitializerClass.getAnnotation(
					HandlesTypes.class);
			Class[] annotationsHandledByMojarraArray = handledTypes.value();
			annotationsHandledByMojarra.addAll(Arrays.<Class<?>>asList(annotationsHandledByMojarraArray));

			// This list of classes was obtained from the AnnotationProvider JavaDoc.
			annotationsHandledByMojarra.addAll(Arrays.<Class<?>>asList(FacesComponent.class, FacesConverter.class,
					FacesRenderer.class, FacesValidator.class, ManagedBean.class, NamedEvent.class, FacesBehavior.class,
					FacesBehaviorRenderer.class));
		}
		catch (ClassNotFoundException e) {
			logger.error(e);
		}
		catch (NoClassDefFoundError e) {
			logger.error(e);
		}

		if (!annotationsHandledByMojarra.isEmpty()) {
			ANNOTATIONS_HANDLED_BY_MOJARRA = Collections.unmodifiableSet(annotationsHandledByMojarra);
		}
		else {
			ANNOTATIONS_HANDLED_BY_MOJARRA = Collections.emptySet();
		}
	}

	public AnnotationProviderOSGiImpl() {
	}

	@Override
	public Map<Class<? extends Annotation>, Set<Class<?>>> getAnnotatedClasses(Set<URI> set) {

		Map<Class<? extends Annotation>, Set<Class<?>>> annotatedClasses;

		// Annotation scanning works correctly in thick wabs and wars.
		if (FacesBundleUtil.isCurrentWarThinWab()) {

			Collection<Bundle> facesBundles = FacesBundleUtil.getFacesBundles(sc);
			annotatedClasses = new HashMap<Class<? extends Annotation>, Set<Class<?>>>();

			for (Class<?> annotation : ANNOTATIONS_HANDLED_BY_MOJARRA) {
				annotatedClasses.put((Class<? extends Annotation>) annotation, new HashSet<Class<?>>());
			}

			for (Bundle bundle : facesBundles) {

				BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
				Collection<String> classFilePaths = bundleWiring.listResources("/", "*.class",
						BundleWiring.LISTRESOURCES_RECURSE);

				for (String classFilePath : classFilePaths) {

					try {

						URL classResource = bundle.getResource(classFilePath);

						if (classResource == null) {
							continue;
						}

						String className = classFilePath.replaceAll("\\.class$", "").replace("/", ".");
						ClassLoader bundleClassLoader = bundleWiring.getClassLoader();
						Class<?> clazz = bundleClassLoader.loadClass(className);
						Annotation[] classAnnotations = clazz.getAnnotations();

						for (Annotation annotation : classAnnotations) {

							Class<? extends Annotation> annotationType = annotation.annotationType();

							if (ANNOTATIONS_HANDLED_BY_MOJARRA.contains(annotationType)) {
								annotatedClasses.get(annotationType).add(clazz);
							}
						}
					}
					catch (ClassNotFoundException e) {
						// no-op
					}
					catch (NoClassDefFoundError e) {
						// no-op
					}
				}
			}

			annotatedClasses = Collections.unmodifiableMap(annotatedClasses);
		}
		else {
			annotatedClasses = wrappedAnnotationProvider.getAnnotatedClasses(set);
		}

		return annotatedClasses;
	}
}
