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

package org.jboss.resteasy.plugins.cache.server;

import java.util.List;
import java.util.Map;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ServerCache {
    static boolean mayVary(Entry cached, MultivaluedMap<String, String> current) {
        boolean mayVary = false;
        for (Map.Entry<String, List<String>> entry : cached.getVaryHeaders().entrySet()) {
            String headerName = entry.getKey();
            mayVary |= !(current.containsKey(headerName) && current.get(headerName).containsAll(entry.getValue()));
        }
        return mayVary;
    }

    interface Entry {
        int getExpirationInSeconds();

        boolean isExpired();

        String getEtag();

        byte[] getCached();

        MultivaluedMap<String, Object> getHeaders();

        MultivaluedMap<String, String> getVaryHeaders();
    }

    Entry add(String uri, MediaType mediaType, CacheControl cc, MultivaluedMap<String, Object> headers, byte[] entity,
              String etag, MultivaluedMap<String, String> varyHeaders);

    Entry get(String uri, MediaType accept, MultivaluedMap<String, String> headers);

    void remove(String uri);

    void clear();
}
