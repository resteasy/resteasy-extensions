/*
 * Copyright (c) 2021 Red Hat, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.jboss.resteasy.security.encoding;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.Consumes;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.common.utils.MediaTypes;
import org.jboss.resteasy.security.encoding.annotations.EncodeEntity;
import org.jboss.resteasy.security.encoding.annotations.EncodeParameter;
import org.jboss.resteasy.security.encoding.spi.EncoderConfiguration;

/**
 * A provider used to dynamically register encoders for parameters and entities.
 * <p>
 * By default all {@linkplain String string} parameters will be encoded if the {@linkplain Consumes consuming)
 * {@linkplain MediaType media type} is not in the {@linkplain EncoderConfiguration#ignoredMediaTypes() ignored} media
 * types.
 * </p>
 * <p>
 * Encoding can be disabled for a type, method or parameter with the {@link EncodeParameter} annotation by setting the
 * value to {@code false}.
 * </p>
 * <p>
 * If a type or method is found annotated with the {@link EncodeEntity} parameter and the
 * {@linkplain EncodeEntity#encode() encode} value is set to {@code true} the entity will be encoded. See the
 * {@link EncodeEntityReaderInterceptor} for details on how the entity is encoded.
 * </p>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 * @see EncoderParamConverterProvider
 * @see EncoderParamConverter
 * @see EncodeEntityReaderInterceptor
 */
@Provider
@ConstrainedTo(RuntimeType.SERVER)
public class EncoderFeature implements DynamicFeature {

    private final EncoderConfiguration config;

    /**
     * Creates the new dynamic feature.
     */
    public EncoderFeature() {
        config = EncoderConfiguration.create();
    }

    @Override
    public void configure(final ResourceInfo resourceInfo, final FeatureContext context) {
        if (config.isDisabled()) {
            return;
        }

        final Method method = resourceInfo.getResourceMethod();
        final Class<?> type = resourceInfo.getResourceClass();

        // Register the entity encoder if annotated
        final EncodeEntity encodeEntity;
        if (method.isAnnotationPresent(EncodeEntity.class)) {
            encodeEntity = method.getAnnotation(EncodeEntity.class);
        } else {
            encodeEntity = type.getAnnotation(EncodeEntity.class);
        }

        if (encodeEntity != null && encodeEntity.encode()) {
            context.register(new EncodeEntityReaderInterceptor(config.getEncoder(), encodeEntity));
        }

        final EncodeParameter encodeParameter;
        if (method.isAnnotationPresent(EncodeParameter.class)) {
            encodeParameter = method.getAnnotation(EncodeParameter.class);
        } else {
            encodeParameter = type.getAnnotation(EncodeParameter.class);
        }

        boolean strict = false;
        boolean register = true;
        if (encodeParameter != null) {
            if (!encodeParameter.value()) {
                // The type or method were annotated with @EncodeParameter(false)
                register = false;
                // Check the parameters for the annotation
                for (Parameter parameter : method.getParameters()) {
                    if (parameter.isAnnotationPresent(EncodeParameter.class)) {
                        // If the annotation is set to true we need to register the provider in strict mode
                        if (parameter.getAnnotation(EncodeParameter.class).value()) {
                            register = true;
                            strict = true;
                            break;
                        }
                    }
                }
            }
        } else {
            // No @EncoderParameter annotation was found, resolve the media types for the resource and check against
            // the ignored media types from the config
            final Collection<MediaType> mediaTypes = getMediaTypes(resourceInfo);
            final Collection<MediaType> ignoredMediaTypes = config.ignoredMediaTypes();
            for (MediaType mediaType : mediaTypes) {
                if (ignoredMediaTypes.contains(mediaType)) {
                    register = false;
                    break;
                }
            }
        }
        if (register) {
            context.register(new EncoderParamConverterProvider(config, strict));
        }
    }

    private static Collection<MediaType> getMediaTypes(final ResourceInfo resourceInfo) {
        // First attempt to get the media types from the method
        final Method method = resourceInfo.getResourceMethod();
        Consumes consumes = method.getAnnotation(Consumes.class);
        if (consumes == null) {
            consumes = resourceInfo.getResourceClass().getAnnotation(Consumes.class);
        }
        return MediaTypes.getMediaTypes(consumes);
    }
}

