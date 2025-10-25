/*
 * Copyright (c) 2025 ProjeXion. All rights reserved.
 */
package com.projexion.api.Project;

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


@Path("projects")
@Produces("application/json")
@Consumes("application/json")
public class ProjectResource {
    private static final Logger LOGGER = Logger.getLogger(ProjectResource.class.getName());
    @Inject
    ProjectService service;

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
     * Find project name and id
     * @return
     */
    @GET
    @Path("/project-name")
    public Uni<Map<String, Long>> getProjectName() {
        return service.getProjectName();
    }



    /**
     * Add new project
     * @param project
     * @return
     */
    @POST
    public Uni<Response> createEntry(ProjectEntity project) {
        if (project == null || project.getId() != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }
        LOGGER.info("Creating a new project with title: " + project.getTitle());
        return service.createProject(project)
                .map(createdProject -> Response.ok(createdProject).status(201).build()); // Return 201 Created
    }

    /**
     * Update Project
     * @param key
     * @param project
     * @return
     */
    @PUT
    @Path("{key}")
    public Uni<Response> updateEntry(@PathParam("key") Long key, ProjectEntity project) {
        if (project == null || project.getTitle() == null) {
            throw new WebApplicationException("Project data is not set.", 422);
        }
        LOGGER.info("A project is updated with title: " + project.getTitle());
        return service.updateProject(key, project);
    }


    /**
     * Delete project
     * @param key
     * @return
     */
    @DELETE
    @Path("{key}")
    public Uni<Response> deleteEntry(@PathParam("key") Long key) {
        LOGGER.info("A roject is deleted with id: " + key);
        return service.deleteProject(key);
    }
}
