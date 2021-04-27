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

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.extensions.test.TestDeployment;
import org.jboss.resteasy.extensions.test.encoding.resources.AnnotatedEntityEncodingResource;
import org.jboss.resteasy.extensions.test.encoding.resources.AnnotatedNoEncodeEntityEncodingResource;
import org.jboss.resteasy.extensions.test.encoding.resources.EntityEncodingResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
public class EntityEncoderTestCase extends AbstractEncoderTestCase {

    @Deployment
    public static Archive<?> createDeployment() {
        return TestDeployment.createWar("parameter-conversion.war", "org.jboss.resteasy:resteasy-server-security")
                .addClasses(AbstractEncoderTestCase.class, EntityEncodingResource.class,
                        AnnotatedEntityEncodingResource.class, AnnotatedNoEncodeEntityEncodingResource.class);
    }

    @Test
    public void postEntityMethodAnnotated() throws Exception {
        final String url = createUrl("entity", "post");
        final Response response = client.target(url)
                .request()
                .post(Entity.entity("<script>console.log('attacked')</script>", MediaType.TEXT_PLAIN_TYPE));
        validateResponse(response);
    }


    @Test
    public void postEntityTypeAnnotated() throws Exception {
        final String url = createUrl("annotated", "entity", "post");
        final Response response = client.target(url)
                .request()
                .post(Entity.entity("<script>console.log('attacked')</script>", MediaType.TEXT_HTML_TYPE));
        validateResponse(response);
    }

    @Test
    public void postEntityNoEncodeTypeAnnotated() throws Exception {
        final String url = createUrl("annotated", "noencode", "entity", "post");
        final Response response = client.target(url)
                .request()
                .post(Entity.entity("<script>console.log('attacked')</script>", MediaType.TEXT_PLAIN_TYPE));
        validateResponse(response, "<script>console.log('attacked')</script>");
    }

    @Test
    public void postEntityEncodeMethodAnnotated() throws Exception {
        final String url = createUrl("annotated", "noencode", "entity", "encode");
        final Response response = client.target(url)
                .request()
                .post(Entity.entity("<script>console.log('attacked')</script>", MediaType.TEXT_PLAIN_TYPE));
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
