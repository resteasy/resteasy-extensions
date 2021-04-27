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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.common.encoding.Encoder;

/**
 * A simple default encoder configuration.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class DefaultEncoderConfiguration implements EncoderConfiguration {
    static final DefaultEncoderConfiguration INSTANCE = new DefaultEncoderConfiguration();

    private static final List<MediaType> DEFAULT_IGNORED_MEDIA_TYPES = Arrays.asList(
            MediaType.APPLICATION_ATOM_XML_TYPE,
            MediaType.APPLICATION_SVG_XML_TYPE,
            MediaType.APPLICATION_XHTML_XML_TYPE,
            MediaType.APPLICATION_XML_TYPE,
            MediaType.TEXT_HTML_TYPE,
            MediaType.TEXT_XML_TYPE);

    @Override
    public Collection<MediaType> ignoredMediaTypes() {
        return DEFAULT_IGNORED_MEDIA_TYPES;
    }

    @Override
    public Encoder getEncoder() {
        return Holder.ENCODER;
    }

    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }

    private static class Holder {
        // Lazily initialize the encoder
        static final Encoder ENCODER = Encoder.resolve();
    }
}
