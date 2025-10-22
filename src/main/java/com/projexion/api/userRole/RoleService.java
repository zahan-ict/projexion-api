/*
 * Copyright (c) 2025 Nexinx. All rights reserved.
 */
package com.projexion.api.userRole;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

@ApplicationScoped
public class RoleService {


    /**
     * Server side pagination on element list
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @WithSession
    protected Uni<Map<String, Object>> findAllByPage(int pageIndex, int pageSize) {
        return RoleEntity.count()                  // first get total count
                .flatMap(totalCount ->
                        RoleEntity.find("ORDER BY id DESC")            // then get paginated data
                                .page(pageIndex, pageSize)
                                .list()
                                .map(list -> {
                                    Map<String, Object> response = new HashMap<>();
                                    response.put("data", list);
                                    response.put("totalCount", totalCount);
                                    return response;
                                })
                );
    }

    public Uni<RoleEntity> getAdministratorRole() {
        // Query by roleName = 'Administrator'
        return Panache.withTransaction(() ->
                RoleEntity.find("roleName", "Administrator").firstResult());
    }


    @WithTransaction
    public Uni<RoleEntity> findItemById(Long id) {
        return RoleEntity.findById(id);
    }


    public Uni<Map<String, Long>> getRoleNames() {
        Map<String, Long> roleNameIndex = new HashMap<>();
        return RoleEntity.<RoleEntity>listAll()
                .onItem().transformToUni(roleEntities -> {
                    roleEntities.stream()
                            .peek(entity -> roleNameIndex.put(entity.getRoleName(), entity.getId()))
                            .map(RoleEntity::getRoleName)
                            .collect(Collectors.toList());
                    return Uni.createFrom().item(roleNameIndex);
                });
    }


    public Uni<Response> createRole(RoleEntity role) {
        Instant now = Instant.now();
        role.setCreatedAt(now);
        role.setUpdatedAt(now); // always update updatedAt
        return Panache.withTransaction(role::persist)
                .replaceWith(() -> Response.ok(role).status(CREATED).build());
    }

    public Uni<Response> updateRole(Long id, RoleEntity updateRole) {
        return Panache.withTransaction(() ->
                updateRole.<RoleEntity>findById(id)
                        .onItem().ifNotNull().transform(entity -> {
                            entity.setRoleName(updateRole.getRoleName());
                            entity.setRoleDescription(updateRole.getRoleDescription());
                            entity.setRolePermission(updateRole.getRolePermission());
                            entity.setCreatedAt(updateRole.getCreatedAt());
                            entity.setUpdatedAt(Instant.now());
                            entity.persist();
                            return Response.ok(entity).build();
                        }).onItem().ifNull().continueWith(Response.ok().status(NOT_FOUND)::build)
        );
    }

    public Uni<Response> deleteRole(Long id) {
        return Panache.withTransaction(() -> RoleEntity.deleteById(id))
                .map(deleted -> deleted ? Response.ok().status(NO_CONTENT).build() : Response.ok().status(NOT_FOUND).build());
    }
}
