/*
 * Copyright (c) 2025 Nexinx. All rights reserved.
 */
package com.projexion.api.userRole;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import io.smallrye.mutiny.Uni;

import java.util.Map;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

@Path("roles")
@Produces("application/json")
@Consumes("application/json")
public class RoleResource {
    private static final Logger LOGGER = Logger.getLogger(RoleResource.class.getName());
    @Inject
    RoleService service;


    @GET
    @Path("/paging")
    public Uni<Map<String, Object>> getEntryByPage(@QueryParam("pageIndex") int pageIndex, @QueryParam("pageSize") int pageSize) {
        return service.findAllByPage(pageIndex, pageSize);
    }

    @GET
    @Path("/admin-role") // Endpoint path with a parameter
    public Uni<RoleEntity>  getAdmin() {
        return service.getAdministratorRole();
    }

    @GET
    @Path("/role-name") // Endpoint path with a parameter
    public Uni<Map<String, Long>> getRoleName() {
        return service.getRoleNames();
    }

    @GET
    @Path("{id}")
    public Uni<Response> getSingleEntry(@PathParam("id") Long id) {
        return service.findItemById(id)
                .onItem().ifNotNull().transform(role -> Response.ok(role).build())
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
    }



    @POST
    public Uni<Response> createEntry(RoleEntity role) {
        if (role == null || role.getId() != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }
        return service.createRole(role);
    }

    @PUT
    @Path("{key}")
    public Uni<Response> updateEntry(@PathParam("key") Long key, RoleEntity role) {
        if (role == null) {
            throw new WebApplicationException("User name was not set on request.", 422);
        }
        return service.updateRole(key, role);
    }

    @DELETE
    @Path("{key}")
    public Uni<Response> deleteEntry(@PathParam("key") Long key) {
        return service.deleteRole(key);
    }
}
