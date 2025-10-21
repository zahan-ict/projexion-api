/*
 * Copyright (c) 2025 ProjeXion. All rights reserved.
 */
package com.projexion.api.company;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;
import java.util.Map;


@Path("companies")
@Produces("application/json")
@Consumes("application/json")
public class CompanyResource {
    private static final Logger LOGGER = Logger.getLogger(CompanyResource.class.getName());
    @Inject
    CompanyService service;

    /**
     * Server side paging
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GET
    @Path("/paging")
    public Uni<Map<String, Object>> getEntryByPage(@QueryParam("pageIndex") int pageIndex, @QueryParam("pageSize") int pageSize) {
        return service.findAllByPage(pageIndex, pageSize);
    }

    /**
     * Add new company
     * @param company
     * @return
     */
    @POST
    public Uni<Response> createEntry(CompanyEntity company) {
        if (company == null || company.getId() != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }
        LOGGER.info("Creating a new  company with name: " + company.getCompanyName());
        return service.createCompany(company)
                .map(createdCompany -> Response.ok(createdCompany).status(201).build()); // Return 201 Created
    }

    /**
     * Update company
     * @param key
     * @param company
     * @return
     */
    @PUT
    @Path("{key}")
    public Uni<Response> updateEntry(@PathParam("key") Long key, CompanyEntity company) {
        if (company == null || company.getCompanyName() == null) {
            throw new WebApplicationException("Company data is not set.", 422);
        }
        LOGGER.info("A Company is updated with name: " + company.getCompanyName());
        return service.updateCompany(key, company);
    }


    /**
     * Delete company
     * @param key
     * @return
     */
    @DELETE
    @Path("{key}")
    public Uni<Response> deleteEntry(@PathParam("key") Long key) {
        LOGGER.info("A company is deleted with id: " + key);
        return service.deleteCompany(key);
    }
}