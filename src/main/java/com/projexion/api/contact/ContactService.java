/*
 * Copyright (c) 2025 ProjeXion. All rights reserved.
 */
package com.projexion.api.contact;


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
public class ContactService {
    /**
     * Server side pagination on element list
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @WithSession
    protected Uni<Map<String, Object>> findAllByPage(int pageIndex, int pageSize) {
        return ContactEntity.count()                  // first get total count
                .flatMap(totalCount ->
                        ContactEntity.find("ORDER BY id DESC")            // then get paginated data
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
     * Find Contact
     *
     * @param id
     * @return
     */
    public Uni<ContactEntity> findItemById(Long id) {
        return ContactEntity.findById(id);
    }

    /**
     * Create Contact
     *
     * @param contact
     * @return
     */
    @WithTransaction
    public Uni<ContactEntity> createContact(ContactEntity contact) {
        return Panache.withTransaction(contact::persist).replaceWith(contact);
    }

    /**
     * Update Contact
     *
     * @param id
     * @param updatedContact
     * @return
     */
    public Uni<Response> updateContact(Long id, ContactEntity updatedContact) {
        return Panache.withTransaction(() ->
                ContactEntity.<ContactEntity>findById(id)
                        .onItem().ifNotNull().transformToUni(existing -> {
                            // Copy all updatable fields
                            existing.setAhvNumber(updatedContact.getAhvNumber());
                            existing.setNationality(updatedContact.getNationality());
                            existing.setWithPrefix(updatedContact.getWithPrefix());
                            existing.setTitels(updatedContact.getTitels());
                            existing.setPrefix(updatedContact.getPrefix());
                            existing.setName(updatedContact.getName());
                            existing.setFirstName(updatedContact.getFirstName());
                            existing.setPrivateAddressStreet(updatedContact.getPrivateAddressStreet());
                            existing.setPrivateAddressPostcode(updatedContact.getPrivateAddressPostcode());
                            existing.setPrivateAddressCity(updatedContact.getPrivateAddressCity());
                            existing.setPrivateAddressCountry(updatedContact.getPrivateAddressCountry());
                            existing.setCompanyPosition(updatedContact.getCompanyPosition());
                            existing.setPostcheckAccount(updatedContact.getPostcheckAccount());
                            existing.setBank(updatedContact.getBank());
                            existing.setContactNotes(updatedContact.getContactNotes());
                            existing.setPhone(updatedContact.getPhone());
                            existing.setProfession(updatedContact.getProfession());
                            existing.setBirthDate(updatedContact.getBirthDate());
                            existing.setBankAccount(updatedContact.getBankAccount());
                            existing.setPhoneCompany(updatedContact.getPhoneCompany());
                            existing.setPhoneCentral(updatedContact.getPhoneCentral());
                            existing.setFax(updatedContact.getFax());
                            existing.setEmail1(updatedContact.getEmail1());
                            existing.setEmail2(updatedContact.getEmail2());
                            existing.setCompanyIs(updatedContact.getCompanyIs());

                            // Set audit fields
                            existing.setUpdatedAt(Instant.now());

                            // Persist and return response
                            return existing.persist()
                                    .replaceWith(Response.ok(existing).build());
                        })
                        .onItem().ifNull().continueWith(Response.status(NOT_FOUND).build())
        );
    }

    /**
     * Delete Contact
     *
     * @param id
     * @return
     */
    public Uni<Response> deleteContact(Long id) {
        return Panache.withTransaction(() -> ContactEntity.deleteById(id))
                .map(deleted -> deleted ? Response.ok().status(NO_CONTENT).build() : Response.ok().status(NOT_FOUND).build());
    }

}
