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

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.security.encoding.annotations.EncodeEntity;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@EncodeEntity
@Path("/annotated/entity")
public class AnnotatedEntityEncodingResource {

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/post")
    public Response postEntity(@Context final HttpHeaders headers, final String name) {
        return Response.ok(String.format("<h1>Hello %s</h1>", name))
                .build();
    }
}
