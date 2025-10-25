/*
 * Copyright (c) 2025 ProjeXion. All rights reserved.
 */
package com.projexion.api.contact;

import jakarta.inject.Inject;
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
import org.jboss.logging.Logger;
import java.util.Map;


@Path("contacts")
@Produces("application/json")
@Consumes("application/json")
public class ContactResource {
    private static final Logger LOGGER = Logger.getLogger(ContactResource.class.getName());
    @Inject
    ContactService service;

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
     * Add new contact
     * @param contact
     * @return
     */
    @POST
    public Uni<Response> createEntry(ContactEntity contact)  {
        if (contact == null || contact.getId() != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }
        LOGGER.info("Creating a new  contact: " + contact.getId());
        return service.createContact(contact)
                .map(createdContact -> Response.ok(createdContact).status(201).build()); // Return 201 Created
    }

    /**
     * Update Contact
     * @param key
     * @param contact
     * @return
     */
    @PUT
    @Path("{key}")
    public Uni<Response> updateEntry(@PathParam("key") Long key, ContactEntity contact) {
        try {
        } catch (Exception e) {
            LOGGER.warn("Failed to log contact JSON: " + e.getMessage());
        }
        // Optional sanity check
        if (contact == null) {
            throw new WebApplicationException("Contact data is missing", 400);
        }
        LOGGER.info("Updating contact with ID: " + key + " (payload ID: " + contact.getId() + ")");
        return service.updateContact(key, contact);
    }


    /**
     * Delete contact
     * @param key
     * @return
     */
    @DELETE
    @Path("{key}")
    public Uni<Response> deleteEntry(@PathParam("key") Long key) {
        LOGGER.info("A contact is deleted with id: " + key);
        return service.deleteContact(key);
    }
}
