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

package org.jboss.resteasy.security.encoding.spi;

import java.lang.annotation.Annotation;
import java.util.Collection;
import javax.ws.rs.FormParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.common.Priority;
import org.jboss.resteasy.common.PriorityServiceLoader;
import org.jboss.resteasy.common.encoding.Encoder;

/**
 * Describes the configuration for the {@link org.jboss.resteasy.security.encoding.EncoderFeature}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public interface EncoderConfiguration extends Priority {

    /**
     * Looks up the first configuration. If not found a default implementation is returned.
     *
     * @return the encoder configuration
     */
    static EncoderConfiguration create() {
        final PriorityServiceLoader<EncoderConfiguration> loader = PriorityServiceLoader.load(EncoderConfiguration.class);
        return loader.findFirst().orElse(DefaultEncoderConfiguration.INSTANCE);
    }

    /**
     * The media types to ignore encoding for. The {@link org.jboss.resteasy.security.encoding.EncoderFeature} uses the
     * types to determine of the {@linkplain org.jboss.resteasy.security.encoding.EncoderParamConverterProvider encoder provider}
     * should be registered.
     * <p>
     * By default the following types are ignored:
     * <ul>
     *     <li>application/atom+xml</li>
     *     <li>application/svg+xml</li>
     *     <li>application/xhtml+xml</li>
     *     <li>application/xml</li>
     *     <li>text/html</li>
     *     <li>text/xml</li>
     * </ul>
     * </p>
     *
     * @return a collection of media types to be ignored for encoding
     */
    Collection<MediaType> ignoredMediaTypes();

    /**
     * Returns the encoder to be used for encoding parameters and entities.
     *
     * @return the encoder
     */
    Encoder getEncoder();

    /**
     * Determines whether or not the annotation is supported for encoding a parameter.
     * <p>
     * By default the following annotations are said to be supported:
     * <ul>
     *     <li>{@link FormParam}</li>
     *     <li>{@link MatrixParam}</li>
     *     <li>{@link PathParam}</li>
     *     <li>{@link QueryParam}</li>
     * </ul>
     * </p>
     *
     * @param annotation the annotation to check
     *
     * @return {@code true} if the annotation is supported for encoding, otherwise {@link false}
     */
    default boolean isSupportedAnnotation(Annotation annotation) {
        if (annotation instanceof PathParam) {
            return true;
        }
        if (annotation instanceof QueryParam) {
            return true;
        }
        if (annotation instanceof MatrixParam) {
            return true;
        }
        return annotation instanceof FormParam;
    }

    /**
     * Indicates all registration of the {@link org.jboss.resteasy.security.encoding.EncoderFeature} should be disabled.
     *
     * @return {@code true} if the registration of the providers should be disabled
     */
    default boolean isDisabled() {
        return false;
    }
}
