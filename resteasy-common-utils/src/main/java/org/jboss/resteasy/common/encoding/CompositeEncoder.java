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

package org.jboss.resteasy.common.encoding;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class CompositeEncoder implements Encoder {
    private final Iterable<Encoder> encoders;

    CompositeEncoder(final Iterable<Encoder> encoders) {
        this.encoders = encoders;
    }

    @Override
    public String encode(final CharSequence value) {
        if (value == null) {
            return null;
        }
        String result = String.valueOf(value);
        for (Encoder encoder : encoders) {
            result = encoder.encode(result);
        }
        return result;
    }
}
