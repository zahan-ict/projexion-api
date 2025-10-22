/*
 * Copyright (c) 2025 Role. All rights reserved.
 */
package com.projexion.api.userRole;

import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;


@Data
@Entity
@Table(name = "user_roles")
@EqualsAndHashCode(callSuper = false)
public class RoleEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "role_description")
    private String roleDescription;

    // ✅ JSONB mapping — Hibernate + Jackson handle JSON automatically
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "role_permission", columnDefinition = "jsonb")
    private JsonNode rolePermission;

    @Column(name = "created_at", columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant updatedAt;
}