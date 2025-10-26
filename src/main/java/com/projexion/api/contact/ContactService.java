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
import java.util.List;
import java.util.Map;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ContactService {
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger LOGGER = Logger.getLogger(ContactService.class);

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
     * @param entity
     * @return
     */
    @WithTransaction
    public Uni<ContactEntity> createContact(ContactEntity entity) {
        ContactEntity contact = new ContactEntity();
        contact.setFirstName(entity.getFirstName());
        contact.setName(entity.getName());
        contact.setAhvNumber(entity.getAhvNumber());
        contact.setNationality(entity.getNationality());
        contact.setWithPrefix(entity.getWithPrefix());
        contact.setTitels(entity.getTitels());
        contact.setPrefix(entity.getPrefix());
        contact.setPrivateAddressStreet(entity.getPrivateAddressStreet());
        contact.setPrivateAddressPostcode(entity.getPrivateAddressPostcode());
        contact.setPrivateAddressCity(entity.getPrivateAddressCity());
        contact.setPrivateAddressCountry(entity.getPrivateAddressCountry());
        contact.setCompanyPosition(entity.getCompanyPosition());
        contact.setPostcheckAccount(entity.getPostcheckAccount());
        contact.setBank(entity.getBank());
        contact.setContactNotes(entity.getContactNotes());
        contact.setPhone(entity.getPhone());
        contact.setProfession(entity.getProfession());
        contact.setBirthDate(entity.getBirthDate());
        contact.setBankAccount(entity.getBankAccount());
        contact.setPhoneCompany(entity.getPhoneCompany());
        contact.setPhoneCentral(entity.getPhoneCentral());
        contact.setFax(entity.getFax());
        contact.setEmail1(entity.getEmail1());
        contact.setEmail2(entity.getEmail2());
        contact.setCompanyIs(entity.getCompanyIs());
        try {
            if (entity.getProjects() != null) {
                contact.setProjectIs(mapper.writeValueAsString(entity.getProjects()));
            } else {
                contact.setProjectIs(null);
            }
            if (entity.getCompanies() != null) {
                contact.setCompanyIs(mapper.writeValueAsString(entity.getCompanies()));
            } else {
                contact.setProjectIs(null);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error converting project list to JSON", e);
        }
        return contact.persist().replaceWith(contact);
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

                            try {
                                if (updatedContact.getProjects() != null) {
                                    existing.setProjectIs(mapper.writeValueAsString(updatedContact.getProjects()));
                                } else {
                                    existing.setProjectIs(null);
                                }
                                if (updatedContact.getCompanies() != null) {
                                    existing.setCompanyIs(mapper.writeValueAsString(updatedContact.getCompanies()));
                                } else {
                                    existing.setProjectIs(null);
                                }
                            } catch (Exception e) {
                                throw new RuntimeException("Error converting project list to JSON", e);
                            }

                            // Persist and return response
                            return existing.persist().replaceWith(Response.ok(existing).build());
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

    /**
     * Search contact by partial name (case-insensitive)
     * @param query
     * @return
     */
    public Uni<List<ContactEntity>> searchContacts(String query) {
        if (query == null || query.trim().isEmpty()) {
            // Return empty list if query is blank
            return Uni.createFrom().item(List.of());
        }
        String searchTerm = "%" + query.toLowerCase() + "%";

        //  Search across multiple possible columns
        String jpql = """
                    FROM ContactEntity c
                    WHERE LOWER(c.name) LIKE ?1
                       OR LOWER(c.firstName) LIKE ?1
                       OR LOWER(c.email1) LIKE ?1
                    ORDER BY c.name ASC
                """;
        return ContactEntity.find(jpql, searchTerm).list();
    }
}
