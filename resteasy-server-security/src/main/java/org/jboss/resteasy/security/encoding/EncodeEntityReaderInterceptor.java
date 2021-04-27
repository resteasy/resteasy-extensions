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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

import org.jboss.resteasy.common.encoding.Encoder;
import org.jboss.resteasy.common.utils.MediaTypes;
import org.jboss.resteasy.security.encoding.annotations.EncodeEntity;

/**
 * Encodes an entity if annotated with {@link EncodeEntity @EncodeEntity}.
 * <p>
 * Reads the {@linkplain ReaderInterceptorContext#getInputStream() entity} into a string, then encodes the string. The
 * entity is then replaced with a {@link ByteArrayInputStream}.
 * </p>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class EncodeEntityReaderInterceptor implements ReaderInterceptor {
    private final Encoder encoder;
    private final EncodeEntity encodeEntity;

    public EncodeEntityReaderInterceptor(final Encoder encoder, final EncodeEntity encodeEntity) {
        this.encoder = encoder;
        this.encodeEntity = encodeEntity;
    }

    @Override
    public Object aroundReadFrom(final ReaderInterceptorContext context) throws IOException, WebApplicationException {
        if (context.getInputStream() != null) {
            final String encoding = resolveCharset(context.getMediaType());
            try (InputStreamReader reader = new InputStreamReader(context.getInputStream(), encoding)) {
                final StringBuilder text = new StringBuilder();
                final char[] buffer = new char[1024];
                int len;
                while ((len = reader.read(buffer)) != -1) {
                    text.append(buffer, 0, len);
                }
                context.setInputStream(new ByteArrayInputStream(encoder.encode(text).getBytes(encoding)));
            }
        }
        return context.proceed();
    }

    private String resolveCharset(final MediaType mediaType) {
        if (encodeEntity.charset().isEmpty()) {
            return MediaTypes.resolveEncoding(mediaType);
        }
        return encodeEntity.charset();
    }
}
