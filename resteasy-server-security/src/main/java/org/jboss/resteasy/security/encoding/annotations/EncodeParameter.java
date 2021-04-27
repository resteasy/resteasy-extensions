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

package org.jboss.resteasy.security.encoding.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to suggest a parameter should be encoded or have the encoding skipped on.
 * <p>
 * If a type is annotated all {@linkplain String string} parameters for all methods will be encoded unless specifically
 * annotated to be skipped.
 * </p>
 * <p>
 * If a method is annotated all {@linkplain String string} parameters will be encoded unless specifically annotated to
 * be skipped.
 * </p>
 * <p>
 * If a parameter is annotated and the parameter type is a {@linkplain String string} the parameter will be encoded
 * unless explicitly told not to.
 * </p>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 * @see org.jboss.resteasy.security.encoding.EncoderParamConverter
 * @see org.jboss.resteasy.security.encoding.EncoderParamConverterProvider
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
public @interface EncodeParameter {

    /**
     * Indicates whether or not the parameter or parameters should or should not be encoded.
     *
     * @return {@code true} to encode the parameter or parameters, otherwise {@code false} to disable encoding
     */
    boolean value() default true;
}
