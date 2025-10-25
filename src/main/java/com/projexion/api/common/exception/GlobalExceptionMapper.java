/*
 * Copyright (c) 2025 Nexinx All rights reserved.
 */
package com.projexion.api.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.security.UnauthorizedException;
import io.smallrye.mutiny.CompositeException;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.time.Instant;

/**
 * Global exception handler for REST endpoints.
 * Catches any Throwable and returns a structured JSON response.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Inject
    ObjectMapper objectMapper;

    @Inject
    UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable throwable) {

        // Handle Mutiny CompositeException (unwrap)
        if (throwable instanceof CompositeException composite) {
            throwable = composite.getCause();
        }

        int statusCode = resolveStatusCode(throwable);
        String message = resolveMessage(throwable);

        // Attempt to map status code to JAX-RS enum
        Response.Status statusEnum = Response.Status.fromStatusCode(statusCode);

        // Build JSON response safely
        ObjectNode errorJson = objectMapper.createObjectNode()
                .put("timestamp", Instant.now().toString())
                .put("status", statusCode)
                .put(
                        "error",
                        statusEnum != null
                                ? statusEnum.getReasonPhrase()
                                : "Unknown HTTP Status (" + statusCode + ")"
                )
                .put("message", message)
                .put("exception", throwable.getClass().getSimpleName())
                .put("path", uriInfo != null ? uriInfo.getPath() : "N/A");

        if (throwable.getCause() != null) {
            errorJson.put("cause", throwable.getCause().getClass().getSimpleName());
        }

        // Logging rules
        if (statusCode >= 500) {
            LOGGER.errorf(throwable,
                    "Unhandled server error at %s: %s",
                    uriInfo != null ? uriInfo.getPath() : "unknown",
                    message
            );
        } else if (statusCode != 404) {
            LOGGER.warnf(
                    "Client error [%d] at %s: %s",
                    statusCode,
                    uriInfo != null ? uriInfo.getPath() : "unknown",
                    message
            );
        }

        return Response.status(statusCode)
                .entity(errorJson)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    // ------------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------------

    private int resolveStatusCode(Throwable t) {
        if (t instanceof WebApplicationException webEx) {
            return webEx.getResponse().getStatus();
        } else if (t instanceof NotFoundException) {
            return Response.Status.NOT_FOUND.getStatusCode();
        } else if (t instanceof BadRequestException) {
            return Response.Status.BAD_REQUEST.getStatusCode();
        } else if (t instanceof ForbiddenException) {
            return Response.Status.FORBIDDEN.getStatusCode();
        } else if (t instanceof UnauthorizedException) {
            return Response.Status.UNAUTHORIZED.getStatusCode();
        } else if (t instanceof InternalServerErrorException) {
            return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        } else if (t instanceof ServiceUnavailableException) {
            return Response.Status.SERVICE_UNAVAILABLE.getStatusCode();
        } else {
            return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        }
    }

    private String resolveMessage(Throwable t) {
        if (t.getMessage() != null && !t.getMessage().isBlank()) {
            return t.getMessage();
        }

        if (t instanceof NotFoundException) {
            return "Resource not found";
        } else if (t instanceof BadRequestException) {
            return "Invalid request";
        } else if (t instanceof ForbiddenException) {
            return "Access denied";
        } else if (t instanceof UnauthorizedException) {
            return "Unauthorized access";
        } else if (t instanceof ServiceUnavailableException) {
            return "Service temporarily unavailable";
        }
        return "An unexpected error occurred";
    }
}
