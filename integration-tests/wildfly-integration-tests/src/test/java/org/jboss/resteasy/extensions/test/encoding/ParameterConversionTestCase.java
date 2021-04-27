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

import java.util.Collections;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.extensions.test.TestDeployment;
import org.jboss.resteasy.extensions.test.encoding.resources.AnnotatedParameterConversionResource;
import org.jboss.resteasy.extensions.test.encoding.resources.ParameterConversionResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
public class ParameterConversionTestCase extends AbstractEncoderTestCase {

    @Deployment
    public static Archive<?> createDeployment() {
        return TestDeployment.createWar("parameter-conversion.war", "org.jboss.resteasy:resteasy-server-security")
                .addClasses(AbstractEncoderTestCase.class, ParameterConversionResource.class, AnnotatedParameterConversionResource.class);
    }

    @Test
    public void pathParamGet() throws Exception {
        final String url = createUrl("greet", "param", "<script>console.log('attacked')</script>");
        final Response response = client.target(url)
                .request()
                .get();
        validateResponse(response);
    }

    @Test
    public void pathParamGetNoEncode() throws Exception {
        final String url = createUrl("annotated", "noencode", "param", "<script>console.log('attacked')</script>");
        final Response response = client.target(url)
                .request()
                .get();
        validateResponse(response, "<script>console.log('attacked')</script>");
    }

    @Test
    public void pathParamGetEncode() throws Exception {
        final String url = createUrl("annotated", "encode", "param", "<script>console.log('attacked')</script>");
        final Response response = client.target(url)
                .request()
                .get();
        validateResponse(response);
    }

    @Test
    public void queryParamGet() throws Exception {
        final String url = createUrl(Collections.singletonMap("name", "<script>console.log('attacked')</script>"), "greet", "query");
        final Response response = client.target(url)
                .request()
                .get();
        validateResponse(response);
    }

    @Test
    public void pathParamGetHtml() throws Exception {
        final String url = createUrl("greet", "html", "<script>console.log('attacked')</script>");
        final Response response = client.target(url)
                .request()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML)
                .get();
        validateResponse(response, "<script>console.log('attacked')</script>");
    }

    @Test
    public void pathParamPost() throws Exception {
        final String url = createUrl("greet", "post", "param", "<script>console.log('attacked')</script>");
        final Response response = client.target(url)
                .request()
                .post(null);
        validateResponse(response);
    }

    @Test
    public void postFormHtml() throws Exception {
        final String url = createUrl("greet", "form", "html");
        final Form form = new Form("name", "<script>console.log('attacked')</script>");
        final Response response = client.target(url)
                .request()
                .post(Entity.form(form));
        validateResponse(response, "<script>console.log('attacked')</script>");
    }

    @Test
    public void postMatrix() throws Exception {
        final StringBuilder url = new StringBuilder();
        createUrl(url, "greet", "full",
                "<script>alert('first')</script>")
                .append(";middle=<script>alert('middle')<%2fscript>?last=<script>alert('last')<%2fscript>");
        final Response response = client.target(url.toString())
                .request()
                .get();
        validateResponse(response, "&lt;script&gt;alert(&#39;first&#39;)&lt;/script&gt; &lt;script&gt;alert(&#39;middle&#39;)&lt;/script&gt; &lt;script&gt;alert(&#39;last&#39;)&lt;/script&gt;");
    }

    @Test
    public void postForm() throws Exception {
        final String url = createUrl("greet", "form");
        final Form form = new Form("name", "<script>console.log('attacked')</script>");
        final Response response = client.target(url)
                .request()
                .post(Entity.form(form));
        validateResponse(response);
    }

    private void validateResponse(final Response response) {
        validateResponse(response, "&lt;script&gt;console.log(&#39;attacked&#39;)&lt;/script&gt;");
    }

    private void validateResponse(final Response response, final String expectedText) {
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        final String asText = response.readEntity(String.class);

        Assertions.assertNotNull(asText);
        Assertions.assertEquals(String.format("<h1>Hello %s</h1>", expectedText), asText);
    }
}
