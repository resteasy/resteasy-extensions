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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

import org.jboss.resteasy.security.encoding.annotations.EncodeParameter;
import org.jboss.resteasy.security.encoding.spi.EncoderConfiguration;

/**
 * A provider which determines if a {@link EncoderParamConverter} should be used.
 * <p>
 * For a {@link EncoderParamConverter} the parameter must be a {@link String} and have a valid annotation. The valid
 * annotation comes from the {@link EncoderConfiguration#isSupportedAnnotation(Annotation)}.
 * </p>
 * <p>
 * If the provider is created in strict mode the parameter must be annotated {@link EncodeParameter}. The check for the
 * {@linkplain EncoderConfiguration#isSupportedAnnotation(Annotation) supported annotations} is skipped.
 * </p>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 * @see EncoderParamConverter
 */
public class EncoderParamConverterProvider implements ParamConverterProvider {
    private final EncoderConfiguration config;
    private final boolean strict;

    /**
     * Creates a new provider.
     *
     * @param config the configuration to use
     * @param strict {@code true} if the {@link EncodeParameter} must be present on the parameter in order to be
     *               encoded
     */
    public EncoderParamConverterProvider(final EncoderConfiguration config, final boolean strict) {
        this.config = config;
        this.strict = strict;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ParamConverter<T> getConverter(final Class<T> rawType, final Type genericType, final Annotation[] annotations) {
        if (rawType.isAssignableFrom(String.class) && hasSupportedAnnotation(annotations)) {
            return (ParamConverter<T>) new EncoderParamConverter(config.getEncoder());
        }
        return null;
    }

    private boolean hasSupportedAnnotation(final Annotation[] annotations) {
        boolean result = false;
        for (Annotation annotation : annotations) {
            if (annotation instanceof EncodeParameter) {
                return ((EncodeParameter) annotation).value();
            }
            if (!strict && config.isSupportedAnnotation(annotation)) {
                result = true;
            }
        }
        return result;
    }
}
