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

import org.jboss.resteasy.security.encoding.EncodeEntityReaderInterceptor;

/**
 * An annotation used to suggest an entity should be encoded.
 * <p>
 * If placed on a type then all entities will either be encoded or skipped based on the {@link #encode()} value unless
 * a method is annotated in the type is annotated.
 * </p>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 * @see EncodeEntityReaderInterceptor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface EncodeEntity {

    /**
     * Whether or not the entity should be encoded.
     *
     * @return {@code true} if the entity should be encoded
     */
    boolean encode() default true;

    /**
     * The {@linkplain java.nio.charset.Charset charset} to override for the encoding.
     * <p>
     * By default the charset will attempt to be discovered from the {@link javax.ws.rs.core.MediaType}.
     * </p>
     *
     * @return the charset
     */
    String charset() default "";
}
