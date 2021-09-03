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

package org.jboss.resteasy.test.cache;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.plugins.cache.server.ServerCacheFeature;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServerCacheTest {
    private static NettyJaxrsServer server;
    private static ResteasyDeployment deployment;
    private static int count = 0;
    private static int plainCount = 0;
    private static int htmlCount = 0;
    private static Client client;


    @BeforeAll
    public static void beforeClass() {
        server = new NettyJaxrsServer();
        server.setPort(TestPortProvider.getPort());
        server.setRootResourcePath("/");
        deployment = server.getDeployment();
        server.start();
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void afterClass() {
        server.stop();
        server = null;
        deployment = null;
        client.close();
    }

    public ResteasyProviderFactory getProviderFactory() {
        return deployment.getProviderFactory();
    }

    public static void addPerRequestResource(Class<?> resource) {
        deployment.getRegistry().addPerRequestResource(resource);
    }

    @Path("/cache")
    public static class MyService {
        @GET
        @Produces("text/plain")
        @Cache(maxAge = 2)
        public String get() {
            count++;
            return "hello world" + count;
        }

        @PUT
        @Consumes("text/plain")
        public void put(String val) {
        }

        @GET
        @Produces("text/plain")
        @Path("accepts")
        @Cache(maxAge = 2)
        public String getPlain() {
            plainCount++;
            return "plain" + plainCount;
        }

        @GET
        @Produces("text/html")
        @Path("accepts")
        @Cache(maxAge = 2)
        public String getHtml() {
            htmlCount++;
            return "html" + htmlCount;
        }

        @GET
        @Produces("text/plain")
        @Path("stuff")
        @Cache(maxAge = 2)
        public String getStuff() {
            count++;
            return "stuff";
        }

        @GET
        @Produces("text/plain")
        @Path("vary")
        @Cache(maxAge = 2)
        public Response getVary(@HeaderParam("X-Test-Vary") @DefaultValue("default") String testVary) {
            count++;
            return Response.ok(testVary).header(HttpHeaders.VARY, "X-Test-Vary").header("X-Count", count).build();
        }
    }

    @Path("/cache")
    public interface MyProxy {
        @GET
        @Produces("text/plain")
        String get();

    }

    @BeforeEach
    public void setUp() {
        getProviderFactory().register(ServerCacheFeature.class);
        addPerRequestResource(MyService.class);
    }

    @Test
    public void testNoCacheHitValidation() throws Exception {
        // test that after a cache expiration NOT MODIFIED is still returned if matching etags

        count = 0;
        String etag;
        {
            Builder request = client.target(generateURL("/cache/stuff")).request();
            Response response = request.get();
            Assertions.assertEquals(200, response.getStatus());
            String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
            Assertions.assertNotNull(cc);
            etag = response.getHeaderString(HttpHeaders.ETAG);
            Assertions.assertNotNull(etag);
            Assertions.assertEquals(response.readEntity(String.class), "stuff");
        }


        Thread.sleep(2000);

        {
            Builder request = client.target(generateURL("/cache/stuff")).request();
            request.header(HttpHeaders.IF_NONE_MATCH, etag);
            Response response = request.get();
            Assertions.assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(), response.getStatus());
            Assertions.assertEquals(2, count);
            response.close();
        }
    }


    @Test
    public void testCache() throws Exception {
        count = 0;
        String etag;
        {
            Builder request = client.target(generateURL("/cache")).request();
            Response response = request.get();
            Assertions.assertEquals(200, response.getStatus());
            String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
            Assertions.assertNotNull(cc);
            etag = response.getHeaderString(HttpHeaders.ETAG);
            Assertions.assertNotNull(etag);
            Assertions.assertEquals(response.readEntity(String.class), "hello world" + 1);
        }


        {
            Builder request = client.target(generateURL("/cache")).request();
            Response response = request.get();
            Assertions.assertEquals(200, response.getStatus());
            String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
            Assertions.assertNotNull(cc);
            etag = response.getHeaderString(HttpHeaders.ETAG);
            Assertions.assertNotNull(etag);
            Assertions.assertEquals(response.readEntity(String.class), "hello world" + 1);
        }
        // test if-not-match
        {
            Builder request = client.target(generateURL("/cache")).request();
            request.header(HttpHeaders.IF_NONE_MATCH, etag);
            Response response = request.get();
            Assertions.assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(), response.getStatus());
            response.close();
        }


        Thread.sleep(2000);

        {
            Builder request = client.target(generateURL("/cache")).request();
            Response response = request.get();
            Assertions.assertEquals(200, response.getStatus());
            String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
            Assertions.assertNotNull(cc);
            etag = response.getHeaderString(HttpHeaders.ETAG);
            Assertions.assertNotNull(etag);
            Assertions.assertEquals(response.readEntity(String.class), "hello world" + 2);
        }

        {
            Builder request = client.target(generateURL("/cache")).request();
            Response response = request.get();
            Assertions.assertEquals(200, response.getStatus());
            String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
            Assertions.assertNotNull(cc);
            etag = response.getHeaderString(HttpHeaders.ETAG);
            Assertions.assertNotNull(etag);
            Assertions.assertEquals(response.readEntity(String.class), "hello world" + 2);
        }

        {
            Builder request = client.target(generateURL("/cache")).request();
            Response response = request.put(Entity.entity("yo", "text/plain"));
            Assertions.assertEquals(204, response.getStatus());
            response.close();
        }
        {
            Builder request = client.target(generateURL("/cache")).request();
            Response response = request.get();
            Assertions.assertEquals(200, response.getStatus());
            String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
            Assertions.assertNotNull(cc);
            etag = response.getHeaderString(HttpHeaders.ETAG);
            Assertions.assertNotNull(etag);
            Assertions.assertEquals(response.readEntity(String.class), "hello world" + 3);
        }
    }


    @Test
    public void testAccepts() {
        count = 0;
        plainCount = 0;
        htmlCount = 0;
        String etag;
        {
            Builder request = client.target(generateURL("/cache/accepts")).request();
            Response response = request.accept("text/plain").get();
            Assertions.assertEquals(200, response.getStatus());
            String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
            Assertions.assertNotNull(cc);
            etag = response.getHeaderString(HttpHeaders.ETAG);
            Assertions.assertNotNull(etag);
            Assertions.assertEquals(response.readEntity(String.class), "plain" + 1);
        }

        {
            Builder request = client.target(generateURL("/cache/accepts")).request();
            Response response = request.accept("text/plain").get();
            Assertions.assertEquals(200, response.getStatus());
            String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
            Assertions.assertNotNull(cc);
            etag = response.getHeaderString(HttpHeaders.ETAG);
            Assertions.assertNotNull(etag);
            Assertions.assertEquals(response.readEntity(String.class), "plain" + 1);
        }

        {
            Builder request = client.target(generateURL("/cache/accepts")).request();
            Response response = request.accept("text/html").get();
            Assertions.assertEquals(200, response.getStatus());
            String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
            Assertions.assertNotNull(cc);
            etag = response.getHeaderString(HttpHeaders.ETAG);
            Assertions.assertNotNull(etag);
            Assertions.assertEquals(response.readEntity(String.class), "html" + 1);
        }
        {
            Builder request = client.target(generateURL("/cache/accepts")).request();
            Response response = request.accept("text/html").get();
            Assertions.assertEquals(200, response.getStatus());
            String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
            Assertions.assertNotNull(cc);
            etag = response.getHeaderString(HttpHeaders.ETAG);
            Assertions.assertNotNull(etag);
            Assertions.assertEquals(response.readEntity(String.class), "html" + 1);
        }
    }

    @Test
    public void testPreferredAccepts() {
        count = 0;
        plainCount = 0;
        htmlCount = 0;
        String etag;
        {
            Builder request = client.target(generateURL("/cache/accepts")).request();
            Response response = request.accept("text/plain").get();
            Assertions.assertEquals(200, response.getStatus());
            String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
            Assertions.assertNotNull(cc);
            etag = response.getHeaderString(HttpHeaders.ETAG);
            Assertions.assertNotNull(etag);
            Assertions.assertEquals(response.readEntity(String.class), "plain" + 1);
        }

        {
            Builder request = client.target(generateURL("/cache/accepts")).request();
            Response response = request.accept("text/html").get();
            Assertions.assertEquals(200, response.getStatus());
            String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
            Assertions.assertNotNull(cc);
            etag = response.getHeaderString(HttpHeaders.ETAG);
            Assertions.assertNotNull(etag);
            Assertions.assertEquals(response.readEntity(String.class), "html" + 1);
        }

        {
            Builder request = client.target(generateURL("/cache/accepts")).request();
            request.header(HttpHeaders.ACCEPT, "text/html;q=0.5, text/plain");
            Response response = request.get();
            Assertions.assertEquals(200, response.getStatus());
            String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
            Assertions.assertNotNull(cc);
            etag = response.getHeaderString(HttpHeaders.ETAG);
            Assertions.assertNotNull(etag);
            Assertions.assertEquals(response.readEntity(String.class), "plain" + 1);
        }
        {
            Builder request = client.target(generateURL("/cache/accepts")).request();
            request.header(HttpHeaders.ACCEPT, "text/plain;q=0.5, text/html");
            Response response = request.get();
            Assertions.assertEquals(200, response.getStatus());
            String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
            Assertions.assertNotNull(cc);
            etag = response.getHeaderString(HttpHeaders.ETAG);
            Assertions.assertNotNull(etag);
            Assertions.assertEquals(response.readEntity(String.class), "html" + 1);
        }
    }

    @Test
    public void testPreferredButNotCachedAccepts() {
        count = 0;
        plainCount = 0;
        htmlCount = 0;
        String etag;
        {
            Builder request = client.target(generateURL("/cache/accepts")).request();
            Response response = request.accept("text/plain").get();
            Assertions.assertEquals(200, response.getStatus());
            String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
            Assertions.assertNotNull(cc);
            etag = response.getHeaderString(HttpHeaders.ETAG);
            Assertions.assertNotNull(etag);
            Assertions.assertEquals(response.readEntity(String.class), "plain" + 1);
        }

        // we test that the preferred can be handled
        {
            Builder request = client.target(generateURL("/cache/accepts")).request();
            request.header(HttpHeaders.ACCEPT, "text/plain;q=0.5, text/html");
            Response response = request.get();
            Assertions.assertEquals(200, response.getStatus());
            String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
            Assertions.assertNotNull(cc);
            etag = response.getHeaderString(HttpHeaders.ETAG);
            Assertions.assertNotNull(etag);
            Assertions.assertEquals(response.readEntity(String.class), "html" + 1);
        }
    }

    @Test
    public void testVary() {
        int cachedCount;
        {
            Builder request = client.target(generateURL("/cache/vary")).request();
            Response foo = request.accept("text/plain").header("X-Test-Vary", "foo").get();
            Assertions.assertEquals("foo", foo.readEntity(String.class));
            cachedCount = Integer.parseInt(foo.getHeaderString("X-Count"));
        }
        {
            Builder request = client.target(generateURL("/cache/vary")).request();
            Response bar = request.accept("text/plain").header("X-Test-Vary", "bar").get();
            Assertions.assertEquals("bar", bar.readEntity(String.class));
        }
        {
            Builder request = client.target(generateURL("/cache/vary")).request();
            Response foo = request.accept("text/plain").header("X-Test-Vary", "foo").get();
            Assertions.assertEquals("foo", foo.readEntity(String.class));
            int currentCount = Integer.parseInt(foo.getHeaderString("X-Count"));
            Assertions.assertEquals(cachedCount, currentCount);
        }
    }

}
