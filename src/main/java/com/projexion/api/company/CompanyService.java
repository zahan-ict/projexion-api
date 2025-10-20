/*
 * Copyright (c) 2025 ProjeXion. All rights reserved.
 */
package com.projexion.api.company;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;

@ApplicationScoped
public class CompanyService {
    /**
     * Server side pagination on element list
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @WithSession
    protected Uni<Map<String, Object>> findAllByPage(int pageIndex, int pageSize) {
        return CompanyEntity.count()                  // first get total count
                .flatMap(totalCount ->
                        CompanyEntity.find("ORDER BY id DESC")            // then get paginated data
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

    /**
     * Find company
     *
     * @param id
     * @return
     */
    public Uni<CompanyEntity> findItemById(Long id) {
        return CompanyEntity.findById(id);
    }

    /**
     * Create company
     *
     * @param company
     * @return
     */
    @WithTransaction
    public Uni<CompanyEntity> createCompany(CompanyEntity company) {
        return Panache.withTransaction(company::persist).replaceWith(company);
    }

    /**
     * Update company
     *
     * @param id
     * @param updatedCompany
     * @return
     */
    public Uni<Response> updateCompany(Long id, CompanyEntity updatedCompany) {
        return Panache.withTransaction(() ->
                CompanyEntity.<CompanyEntity>findById(id)
                        .onItem().ifNotNull().transformToUni(existing -> {
                            // Copy fields from updatedCompany → existing
                            existing.setCompanyName(updatedCompany.getCompanyName());
                            existing.setCompanyMail(updatedCompany.getCompanyMail());
                            existing.setCompanyPhone(updatedCompany.getCompanyPhone());
                            existing.setCompanyPhone2(updatedCompany.getCompanyPhone2());
                            existing.setCompanyPhone3(updatedCompany.getCompanyPhone3());
                            existing.setCompanyPhone4(updatedCompany.getCompanyPhone4());
                            existing.setCompanyPhone5(updatedCompany.getCompanyPhone5());
                            existing.setCompanyStreet(updatedCompany.getCompanyStreet());
                            existing.setCompanyCity(updatedCompany.getCompanyCity());
                            existing.setCompanyPostcode(updatedCompany.getCompanyPostcode());
                            existing.setCompanyState(updatedCompany.getCompanyState());
                            existing.setCompanyPostbox(updatedCompany.getCompanyPostbox());
                            existing.setCompanyCountry(updatedCompany.getCompanyCountry());
                            existing.setCompanyWebsite(updatedCompany.getCompanyWebsite());
                            existing.setCompanyFacebook(updatedCompany.getCompanyFacebook());
                            existing.setCompanyFax(updatedCompany.getCompanyFax());
                            existing.setCompanyDurchwahl(updatedCompany.getCompanyDurchwahl());
                            existing.setCompanyInstagram(updatedCompany.getCompanyInstagram());
                            existing.setCompanyTwitter(updatedCompany.getCompanyTwitter());
                            existing.setFirmenAdressCat(updatedCompany.getFirmenAdressCat());
                            existing.setCompanyNotes(updatedCompany.getCompanyNotes());

                            // Update audit fields
                            existing.setUpdatedAt(Instant.now());

                            // Persist and return 200 OK
                            return existing.persist()
                                    .replaceWith(Response.ok(existing).build());
                        })
                        // If not found, return 404
                        .onItem().ifNull().continueWith(Response.status(NOT_FOUND).build())
        );
    }

    /**
     * Delete company
     *
     * @param id
     * @return
     */
    public Uni<Response> deleteCompany(Long id) {
        return Panache.withTransaction(() -> CompanyEntity.deleteById(id))
                .map(deleted -> deleted ? Response.ok().status(NO_CONTENT).build() : Response.ok().status(NOT_FOUND).build());
    }

}
