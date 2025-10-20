/*
 * Copyright (c) 2024 nexinx. All rights reserved.
 */
package com.projexion.api.organization;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import io.smallrye.mutiny.Uni;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.logging.Logger;

@Path("organizations")
@Produces("application/json")
@Consumes("application/json")
public class OrganizationResource {
    private static final Logger LOGGER = Logger.getLogger(OrganizationResource.class.getName());
    @Inject
    OrganizationService service;

    @Operation(operationId = "AllOrganizationElement", summary = "show all element")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Show all element."
                    )
            }
    )
    @GET
    public Uni<List<OrganizationEntity>> getAllEntry() {
        return service.getAllOrganization();
    }


    @Operation(operationId = "SingleOrganizationElement", summary = "show single element")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Show single element."
                    )
            }
    )
    @GET
    @Path("{id}")
    public Uni<OrganizationEntity> getSingleEntry(@PathParam("id") Long id) {
        return service.findItemById(id);
    }

    @Operation(operationId = "OrganizationElementShowByPage", summary = "show element by page")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "209",
                            description = "Show element by page."
                    )
            }
    )
    @GET
    @Path("/{limit}/{offset}")
    // limit= item per page, offset = page number. if 6 element and per page 3 than total 2 page. so limit = 3, page =1 but url param=0 and page 2 but url param= 3
    public Uni<List<OrganizationEntity>> getEntryByPage(int limit, int offset) {
        return service.findAllByPage(limit, offset);
    }

    @Operation(operationId = "CreateOrganization", summary = "create entry")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "201",
                            description = "Entry created."
                    ),
                    @APIResponse(
                            responseCode = "406",
                            description = "Entry not created."
                    ),
                    @APIResponse(
                            responseCode = "401",
                            description = "Unauthorized to create entry."
                    )
            }
    )
    @POST
    public Uni<Response> createEntry(OrganizationEntity organization) {
        if (organization == null || organization.getId() != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }
        return service.createOrganization(organization);
    }

    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "204",
                            description = "Entry updated."
                    ),
                    @APIResponse(
                            responseCode = "401",
                            description = "Unauthorized to update entry."
                    )
            }
    )
    @PUT
    @Path("{key}")
    public Uni<Response> updateEntry(@PathParam("key") Long key, OrganizationEntity organization) {
        if (organization == null || organization.getOrganizationName() == null) {
            throw new WebApplicationException("Organization name was not set on request.", 422);
        }
        return service.updateOrganization(key, organization);
    }

    @DELETE
    @Path("{key}")
    @Operation(summary = "delete entry via its key")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "201",
                            description = "Entry deleted."
                    ),
                    @APIResponse(
                            responseCode = "406",
                            description = "Entry not deleted."
                    ),
                    @APIResponse(
                            responseCode = "401",
                            description = "Unauthorized to delete entry for plant"
                    )
            }
    )
    public Uni<Response> deleteEntry(@PathParam("key") Long key) {
        return service.deleteOrganization(key);
    }
}
