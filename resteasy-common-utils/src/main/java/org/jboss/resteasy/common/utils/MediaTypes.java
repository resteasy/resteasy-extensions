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

package org.jboss.resteasy.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

/**
 * A utility for media types.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class MediaTypes {

    /**
     * Determines the media types for the {@link Consumes} annotation. If the annotation is {@code null} the
     * {@link MediaType#WILDCARD_TYPE} media type is returned.
     *
     * @param consumes the annotation used to determine the supported media types
     *
     * @return the supported media types
     */
    public static Collection<MediaType> getMediaTypes(final Consumes consumes) {
        return consumes == null ? Collections.singleton(MediaType.WILDCARD_TYPE) : toMediaTypes(consumes.value());
    }

    /**
     * Resolves the encoding for the media type returning the default encoding if not found.
     *
     * @param mediaType the media type to check
     * @param dft       the default encoding
     *
     * @return the encoding from the media type or the default encoding
     */
    public static String resolveEncoding(final MediaType mediaType, final String dft) {
        if (mediaType == null) {
            return dft;
        }
        final String charset = mediaType.getParameters().get("charset");
        return charset == null ? dft : charset;
    }

    /**
     * Resolves the encoding for the media type returning &quot;utf-8&quot; if not found.
     *
     * @param mediaType the media type to check
     *
     * @return the encoding
     */
    public static String resolveEncoding(final MediaType mediaType) {
        return resolveEncoding(mediaType, "utf-8");
    }

    private static Collection<MediaType> toMediaTypes(final String[] values) {
        final Collection<MediaType> result = new ArrayList<>();
        for (String value : values) {
            result.add(MediaType.valueOf(value));
        }
        return result;
    }
}
