/*
 * Copyright (c) 2024 nexinx. All rights reserved.
 */
package com.projexion.api.organization;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.util.List;

import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;

@ApplicationScoped
public class OrganizationService {

    public Uni<List<OrganizationEntity>> getAllOrganization() {
        return OrganizationEntity.listAll();
    }

    public Uni<OrganizationEntity> findItemById(Long id) {
        return OrganizationEntity.findById(id);
    }

    public Uni<List<OrganizationEntity>> findAllByPage(int limit, int offset) {
        return OrganizationEntity.findAll().page(limit, offset).list();
    }

    public Uni<Response> createOrganization(OrganizationEntity organization) {
        return Panache.withTransaction(() ->
                organization.persist().replaceWith(() -> Response.ok(organization).status(CREATED).build()));
    }

    public Uni<Response> updateOrganization(Long id, OrganizationEntity organization) {
        return Panache.withTransaction(() ->
                organization.<OrganizationEntity>findById(id)
                        .onItem().ifNotNull().transform(entity -> {
                            entity.setOrganizationName(organization.getOrganizationName());
                            entity.setOrganizationDetails(organization.getOrganizationDetails());
                            entity.setOrganizationAddress(organization.getOrganizationAddress());
                            entity.setOrganizationPhone(organization.getOrganizationPhone());
                            entity.setOrganizationEmail(organization.getOrganizationEmail());
                            entity.setTaxId(organization.getTaxId());
                            entity.setRegistrationId(organization.getRegistrationId());
                            entity.setUpdatedAt(Instant.now());
                            entity.persist();
                            return Response.ok(entity).build();
                        }).onItem().ifNull().continueWith(Response.ok().status(NOT_FOUND)::build)
        );
    }


    public Uni<Response> deleteOrganization(Long id) {
        return Panache.withTransaction(() -> OrganizationEntity.deleteById(id))
                .map(deleted -> deleted ? Response.ok().status(NO_CONTENT).build() : Response.ok().status(NOT_FOUND).build());
    }
}
