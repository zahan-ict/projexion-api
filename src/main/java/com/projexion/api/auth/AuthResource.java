/*
 * Copyright (c) 2025 Nexinx All rights reserved.
 */
package com.projexion.api.auth;

import com.projexion.api.user.UserEntity;
import com.projexion.api.user.UserService;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@ApplicationScoped
public class AuthResource {

    @Inject
    AuthService authService;

    @Inject
    UserService userService;


    @POST
    @Path("/register")
    @Consumes("application/json")
    public Uni<Response> createEntry(UserEntity user) {
        if (user == null || user.getId() != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }
        return userService.createUser(user);
    }

    @POST
    @Path("/login")
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Uni<Response> login(@FormParam("username") String username, @FormParam("password") String password) {
        return authService.userLogin(username, password);
    }

    @POST
    @Path("/refresh")
    @PermitAll
    public Uni<Response> refresh(@CookieParam("refresh_token") String refreshToken) {
        return authService.refreshAccessToken(refreshToken);
    }


    @POST
    @Path("/logout")
    @PermitAll
    public Uni<Response> logout() {
        return authService.userLogout();
    }
}
