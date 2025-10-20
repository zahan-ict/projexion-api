/*
 * Copyright (c) 2025 Nexinx. All rights reserved.
 */
package com.projexion.api.user;
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

@Path("users")
@Produces("application/json")
@Consumes("application/json")
public class UserResource {
    private static final Logger LOGGER = Logger.getLogger(UserResource.class.getName());

    @Inject
    UserService service;


    @GET
    @Path("/paging")
    public Uni<Map<String, Object>> getEntryByPage(@QueryParam("pageIndex") int pageIndex, @QueryParam("pageSize") int pageSize) {
        return service.findAllByPage(pageIndex, pageSize);
    }

    @GET
    @Path("{id}")
    public Uni<UserEntity> getSingleEntry(@PathParam("id") Long id) {
        return service.findItemById(id);
    }

    @GET
    public Uni<UserEntity> getUserByEmail(@QueryParam("email") String email) {
        return service.findUserByEmail(email);
    }

    @POST
    public Uni<Response> createEntry(UserEntity user) {
        if (user == null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }
        return service.createUser(user);
    }

    @PUT
    @Path("{key}")
    public Uni<Response> updateEntry(@PathParam("key") Long key, UserEntity user) {
        return service.updateUser(key, user);
    }

    @DELETE
    @Path("{key}")
    public Uni<Response> deleteEntry(@PathParam("key") Long key) {
        return service.deleteUser(key);
    }
}
