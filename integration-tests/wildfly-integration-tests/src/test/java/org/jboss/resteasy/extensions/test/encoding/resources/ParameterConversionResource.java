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

package org.jboss.resteasy.extensions.test.encoding.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.security.encoding.annotations.EncodeParameter;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Path("/greet")
public class ParameterConversionResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/param/{name}")
    public Response getParam(@PathParam("name") final String name) {
        return createResponse(name);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/query")
    public Response getQuery(@QueryParam("name") final String name) {
        return createResponse(name);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.TEXT_HTML)
    @Path("/html/{name}")
    public Response getHtml(@PathParam("name") final String name) {
        return createResponse(name);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/post/param/{name}")
    public Response postPathParam(@PathParam("name") final String name) {
        return createResponse(name);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/form/")
    public Response postFormParam(@FormParam("name") final String name) {
        return createResponse(name);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/form/html")
    public Response postFormHtml(@EncodeParameter(value = false) @FormParam("name") final String name) {
        return createResponse(name);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/matrix/param")
    public Response getMatrixParam(@MatrixParam("name") final String name) {
        return createResponse(name);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/full/{first}")
    public Response getFullName(@PathParam("first") final String first,
                                @MatrixParam("middle") final String middle, @QueryParam("last") final String last) {
        return createResponse(String.format("%s %s %s", first, middle, last));
    }

    private Response createResponse(final String name) {
        return Response.ok(String.format("<h1>Hello %s</h1>", name))
                .build();
    }
}
