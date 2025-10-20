/*
 * Copyright (c) 2024 nexinx. All rights reserved.
 */
package com.projexion.api.auth;

import io.smallrye.jwt.auth.principal.JWTCallerPrincipal;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.time.Instant;

@Provider
public class SecurityFilter implements ContainerRequestFilter {
    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());

    /**
     * Add security features
     *
     * @param requestContext
     * @throws IOException
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        isTokenExpire(requestContext);
    }

    /**
     * Check weather or not token is expiring
     *
     * @param requestContext
     */
    private void isTokenExpire(ContainerRequestContext requestContext) {
        String token = requestContext.getHeaderString("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            try {
                // Parse the token and check its expiration
                JWTCallerPrincipal principal = (JWTCallerPrincipal) requestContext.getSecurityContext().getUserPrincipal();
                Instant expiration = Instant.ofEpochSecond(principal.getExpirationTime());
                if (expiration != null && expiration.isBefore(Instant.now())) {
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                }
            } catch (Exception e) {
                LOGGER.info("Token parsing failed: " + e.getMessage());
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            }
        }
    }
}