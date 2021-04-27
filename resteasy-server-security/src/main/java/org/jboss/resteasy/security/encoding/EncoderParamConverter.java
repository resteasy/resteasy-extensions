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

import javax.ws.rs.ext.ParamConverter;

import org.jboss.resteasy.common.encoding.Encoder;

/**
 * Encodes a parameter with the given encoder.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class EncoderParamConverter implements ParamConverter<String> {
    private final Encoder encoder;

    /**
     * Create a new parameter encoder.
     *
     * @param encoder the encoder to use
     */
    public EncoderParamConverter(final Encoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public String fromString(final String value) {
        return encoder.encode(value);
    }

    @Override
    public String toString(final String value) {
        return encoder.encode(value);
    }
}
