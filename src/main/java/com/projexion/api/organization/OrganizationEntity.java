/*
 * Copyright (c) 2024 nexinx. All rights reserved.
 */
package com.projexion.api.organization;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "organization")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class OrganizationEntity extends PanacheEntityBase {
    @Id
    @SequenceGenerator(name = "OrganizationSeq", sequenceName = "organization_id_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(generator = "OrganizationSeq")
    private Long id;

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "organization_details")
    private String organizationDetails;

    @Column(name = "organization_address")
    private String organizationAddress;

    @Column(name = "organization_phone")
    private String organizationPhone;

    @Column(name = "organization_email")
    private String organizationEmail;

    @Column(name = "tax_id")
    private String taxId;

    @Column(name = "registration_id")
    private String registrationId;

    @Column(name = "created_at", columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant updatedAt;
}
