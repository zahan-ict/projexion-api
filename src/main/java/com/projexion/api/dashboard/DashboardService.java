/*
 * Copyright (c) 2025 ProjeXion. All rights reserved.
 */
package com.projexion.api.dashboard;
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
                .map(userCount -> {
                    totals.put("total_users", userCount);
                    return totals;
                });
    }
}
