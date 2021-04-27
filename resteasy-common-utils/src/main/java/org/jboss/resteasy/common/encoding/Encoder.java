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

import org.jboss.resteasy.common.Priority;
import org.jboss.resteasy.common.PriorityServiceLoader;

/**
 * An API used to encode {@link CharSequence} values.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@FunctionalInterface
public interface Encoder extends Priority {

    /**
     * Looks up the encoder implementations.
     * <p>
     * If more than one implementation is found the encoders are processed by their {@linkplain Priority priority}. A
     * lower priority will be executed before a higher priority.
     * </p>
     * <p>
     * If no encoders are found a simple encoder which just returns the value is used.
     * </p>
     *
     * @return the encoder found, if more than one is found a composite encoder will be returned
     *
     * @throws SecurityException if a security manager is present and the caller does not have the
     *                           {@link RuntimePermission RuntimePermission("getClassLoader")}
     * @see Thread#getContextClassLoader()
     */
    static Encoder resolve() throws SecurityException {
        final PriorityServiceLoader<Encoder> loader = PriorityServiceLoader.load(Encoder.class);
        if (loader.count() == 0) {
            return DefaultEncoder.INSTANCE;
        }
        if (loader.count() == 1) {
            return loader.iterator().next();
        }
        return new CompositeEncoder(loader);
    }

    /**
     * Encodes the value.
     *
     * @param value the value to encode
     *
     * @return the encoded value
     */
    String encode(CharSequence value);

    @Override
    default int priority() {
        return 500;
    }
}
