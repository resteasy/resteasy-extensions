/*
 * Copyright (c) 2023 Red Hat, Inc.
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

package org.jboss.tracing.api;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.tracing.api.RESTEasyTracingInfo;
import org.jboss.resteasy.tracing.api.RESTEasyTracingInfoFormat;
import org.jboss.resteasy.tracing.api.RESTEasyTracingMessage;
import org.jboss.resteasy.tracing.api.providers.TextBasedRESTEasyTracingInfo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kohsuke.MetaInfServices;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class TextTracingTest {

    private static SeBootstrap.Instance INSTANCE;

    @BeforeAll
    public static void start() throws Exception {
        INSTANCE = SeBootstrap.start(TestApplication.class)
                .toCompletableFuture().get();
    }

    @AfterAll
    public static void stop() throws Exception {
        if (INSTANCE != null) {
            INSTANCE.stop().toCompletableFuture().get();
        }
    }

    @Test
    public void checkGet() throws Exception {
        try (
                Client client = ClientBuilder.newClient();
                Response response = client.target(INSTANCE.configuration().baseUriBuilder().path("trace"))
                        .request()
                        .get()) {
            checkStatus(response);
            final Collection<String> headerNames = getFilteredHeaderNames(response);
            // Iterate the headers, we should see at least one X-RESTEasy-Tracing-
            Assertions.assertFalse(headerNames.isEmpty(),
                    () -> String.format("Did not find any headers matching X-RESTEasy-Tracing-: %s", headerNames));
        }
    }

    @Test
    public void multipleRequests() {
        try (
                Client client = ClientBuilder.newClient();
                Response r1 = getRequest(client)) {
            checkStatus(r1);
            final Collection<String> firstRequestHeaders = getFilteredHeaderNames(r1);
            Assertions.assertFalse(firstRequestHeaders.isEmpty(),
                    () -> String.format("First request did not find any headers matching X-RESTEasy-Tracing-: %s",
                            firstRequestHeaders));

            // Make a second request and get the request headers
            try (Response r2 = getRequest(client)) {
                checkStatus(r2);
                final Collection<String> secondRequestHeaders = getFilteredHeaderNames(r2);
                Assertions.assertFalse(secondRequestHeaders.isEmpty(),
                        () -> String.format("Second request did not find any headers matching X-RESTEasy-Tracing-: %s",
                                secondRequestHeaders));

                // We at least the same number of messages, but not more. In some cases it will be less on the second
                // request.
                Assertions.assertTrue(secondRequestHeaders.size() <= firstRequestHeaders.size(),
                        () -> String.format("Headers do not match:%nFirst Request: %s%nSecond Request: %s",
                                toString(r1.getStringHeaders()),
                                toString(r2.getStringHeaders())));
            }
        }
    }

    @Test
    public void legacyMultipleRequests() {
        final Map<String, String> headers = Map.of("X-RESTEasy-Tracing-Accept-Format", "JSON");
        try (
                Client client = ClientBuilder.newClient();
                Response r1 = getRequest(client, headers)) {
            checkStatus(r1);
            // The first header should be the legacy tracing info
            Assertions.assertEquals(LegacyTracingInfo.class.getName(), r1.getHeaderString("X-RESTEasy-Tracing-000"));
            final Collection<String> firstRequestHeaders = getFilteredHeaderNames(r1);
            Assertions.assertFalse(firstRequestHeaders.isEmpty(),
                    () -> String.format("First request did not find any headers matching X-RESTEasy-Tracing-: %s",
                            firstRequestHeaders));

            // Make a second request and get the request headers
            try (Response r2 = getRequest(client, headers)) {
                checkStatus(r2);
                // The first header should be the legacy tracing info
                Assertions.assertEquals(LegacyTracingInfo.class.getName(), r2.getHeaderString("X-RESTEasy-Tracing-000"));
                final Collection<String> secondRequestHeaders = getFilteredHeaderNames(r2);
                Assertions.assertFalse(secondRequestHeaders.isEmpty(),
                        () -> String.format("Second request did not find any headers matching X-RESTEasy-Tracing-: %s",
                                secondRequestHeaders));

                // We at least the same number of messages, but not more. In some cases it will be less on the second
                // request.
                Assertions.assertTrue(secondRequestHeaders.size() <= firstRequestHeaders.size(),
                        () -> String.format("Headers do not match:%nFirst Request: %s%nSecond Request: %s",
                                toString(r1.getStringHeaders()),
                                toString(r2.getStringHeaders())));
            }
        }
    }

    private static Response getRequest(final Client client) {
        return client.target(INSTANCE.configuration().baseUriBuilder().path("trace"))
                .request()
                .get();
    }

    private static Response getRequest(final Client client, final Map<String, String> headers) {
        return client.target(INSTANCE.configuration().baseUriBuilder().path("trace"))
                .request()
                .headers(new MultivaluedHashMap<>(headers))
                .get();
    }

    private static void checkStatus(final Response response) {
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo(),
                () -> String.format("Invalid status: %s - %s", response.getStatus(), response.readEntity(String.class)));
    }

    private static Collection<String> getFilteredHeaderNames(final Response response) {
        final MultivaluedMap<String, String> headers = response.getStringHeaders();
        Assertions.assertFalse(headers.isEmpty(),
                () -> String.format("Headers should not be empty: %s", response.readEntity(String.class)));
        return filterTracingHeaders(headers);
    }

    private static Collection<String> filterTracingHeaders(final MultivaluedMap<String, String> headers) {
        return headers.keySet()
                .stream()
                .filter(header -> header.startsWith("X-RESTEasy-Tracing-"))
                .collect(Collectors.toList());
    }

    private static CharSequence toString(final MultivaluedMap<String, String> headers) {
        final StringBuilder builder = new StringBuilder();
        headers.forEach((name, value) -> builder.append(name)
                .append(": ")
                .append(value)
                .append(System.lineSeparator()));
        return builder;
    }

    @ApplicationPath("/")
    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(TraceResource.class);
        }

        @Override
        public Map<String, Object> getProperties() {
            return Map.ofEntries(
                    Map.entry("resteasy.server.tracing.threshold", "VERBOSE"),
                    Map.entry("resteasy.server.tracing.type", "ALL"));
        }
    }

    @Path("/trace")
    public static class TraceResource {
        @GET
        public String get() {
            return "get";
        }
    }

    @MetaInfServices(RESTEasyTracingInfo.class)
    @SuppressWarnings("removal")
    public static class LegacyTracingInfo extends TextBasedRESTEasyTracingInfo {
        @Override
        public boolean supports(final RESTEasyTracingInfoFormat format) {
            return format.equals(RESTEasyTracingInfoFormat.JSON);
        }

        @Override
        public String[] getMessages() {
            // We're not going to return real JSON data here, this is for legacy messageList testing
            final String[] messages = new String[messageList.size() + 1];
            messages[0] = LegacyTracingInfo.class.getName();
            int i = 1;
            for (RESTEasyTracingMessage message : messageList) {
                // requestId
                final String text = message.getRequestId() + ' ' +
                // event
                        String.format("%-11s ", message.getEvent().category()) +
                        // duration
                        '[' +
                        formatDuration(message.getDuration()) +
                        " / " +
                        formatDuration(0, message.getTimestamp()) +
                        " ms |" +
                        formatPercent(message.getDuration(), 0) +
                        " %] " +
                        // text
                        message;
                messages[i++] = text;
            }
            return messages;
        }
    }
}
