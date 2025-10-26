/*
 * Copyright (c) 2025 ProjeXion. All rights reserved.
 */
package com.projexion.api.dashboard;
import com.projexion.api.Project.ProjectEntity;
import com.projexion.api.company.CompanyEntity;
import com.projexion.api.contact.ContactEntity;
import com.projexion.api.user.UserEntity;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class DashboardService {

    @WithSession
    public Uni<Map<String, Long>> getTotals() {
        Map<String, Long> totals = new HashMap<>();

        return UserEntity.count()
                .flatMap(userCount -> {
                    totals.put("total_users", userCount);
                    return ContactEntity.count();
                })
                .flatMap(contactCount -> {
                    totals.put("total_contacts", contactCount);
                    return ProjectEntity.count();
                })
                .flatMap(projectCount -> {
                    totals.put("total_projects", projectCount);
                    return CompanyEntity.count();
                })
                .map(companyCount -> {
                    totals.put("total_company", companyCount);
                    return totals;
                });
    }
}

