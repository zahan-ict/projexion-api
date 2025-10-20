/*
 * Copyright (c) 2024 nexinx. All rights reserved.
 */
package com.projexion.api.dashboard;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;

@Path("/element-sum")
public class DashboardResource {
    @Inject
    DashboardService dashboardService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Map<String, Long>> getTotals() {
        return dashboardService.getTotals();
    }
}
