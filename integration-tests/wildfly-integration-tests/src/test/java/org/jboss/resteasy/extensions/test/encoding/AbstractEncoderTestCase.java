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

package org.jboss.resteasy.extensions.test.encoding;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
abstract class AbstractEncoderTestCase {
    @ArquillianResource
    URL baseUrl;

    Client client;

    @BeforeEach
    public void createClient() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void closeClient() {
        if (client != null) client.close();
    }

    String createUrl(final String... paths) throws UnsupportedEncodingException {
        return createUrl(new StringBuilder(), paths).toString();
    }

    String createUrl(final Map<String, String> queryParams, final String... paths) throws UnsupportedEncodingException {
        final StringBuilder result = new StringBuilder();
        createUrl(result, paths)
                .append('?');
        final Iterator<Map.Entry<String, String>> iter = queryParams.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<String, String> entry = iter.next();
            result.append(encode(entry.getKey()))
                    .append('=')
                    .append(encode(entry.getValue()));
            if (iter.hasNext()) {
                result.append('&');
            }
        }
        return result.toString();
    }

    StringBuilder createUrl(final StringBuilder sb, final String... paths) throws UnsupportedEncodingException {
        sb.append(baseUrl.toString());
        for (String path : paths) {
            if (sb.charAt(sb.length() - 1) != '/') {
                sb.append('/');
            }
            sb.append(encode(path));
        }
        return sb;
    }

    static String encode(final String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "utf-8");
    }
}
