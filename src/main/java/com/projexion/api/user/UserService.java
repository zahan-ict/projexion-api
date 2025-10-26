/*
 * Copyright (c) 2025 Nexinx. All rights reserved.
 */
package com.projexion.api.user;

import com.projexion.api.common.PasswordUtils;
import com.projexion.api.user.bean.UpdatePasswordBean;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;
import static org.jboss.resteasy.reactive.RestResponse.Status.INTERNAL_SERVER_ERROR;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.CONFLICT;

@ApplicationScoped
public class UserService {

    /**
     * Server side pagination on element list
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @WithSession
    protected Uni<Map<String, Object>> findAllByPage(int pageIndex, int pageSize) {
        return UserEntity.count()                  // first get total count
                .flatMap(totalCount ->
                        UserEntity.find("ORDER BY id DESC")            // then get paginated data
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
     * Get user by id
     *
     * @param id
     * @return
     */
    public Uni<UserEntity> findItemById(Long id) {
        return UserEntity.findById(id);
    }


    /**
     * Get user by email
     *
     * @param email
     * @return
     */
    public Uni<UserEntity> findUserByEmail(String email) {
        return UserEntity.findByEmail(email);
    }


    /**
     * Create User
     *
     * @param user
     * @return
     */
    public Uni<Response> createUser(UserEntity user) {
        return UserEntity.findByEmail(user.getUserEmail())
                .onItem().ifNotNull().transform(existingUser -> {
                    return Response.ok(CONFLICT).entity(409).build();
                })
                .onItem().ifNull().switchTo(() -> {
                    // Email does not exist, proceed with user creation
                    try {
                        Instant now = Instant.now();
                        byte[] salt = PasswordUtils.generateSalt();
                        String encryptedPassword = PasswordUtils.hashPassword(user.getUserPass(), salt);
                        user.setUserSalt(Base64.getEncoder().encodeToString(salt));
                        user.setUserPass(encryptedPassword);
                        user.setIsActive(true);
                        user.setCreatedAt(now);
                        user.setUpdatedAt(now);
                        user.setValidUntil(now.plus(365, ChronoUnit.DAYS));// Set validity to 1 year from now
                        user.setLastLogin(now);

                    } catch (Exception e) {
                        return Uni.createFrom().item(() -> Response.status(INTERNAL_SERVER_ERROR).entity("Error encrypting password").build());
                    }
                    return Panache.withTransaction(user::persist)
                            .replaceWith(() -> Response.ok(user).status(CREATED).build());
                });
    }

    /**
     * Update user
     *
     * @param id
     * @param updatedUser
     * @return
     */
    public Uni<Response> updateUser(Long id, UserEntity updatedUser) {
        return Panache.withTransaction(() ->
                UserEntity.<UserEntity>findById(id)
                        .onItem().ifNotNull().transform(entity -> {
                            // Update only if values are provided (avoid overwriting existing data)
                            if (updatedUser.getUserFirstName() != null)
                                entity.setUserFirstName(updatedUser.getUserFirstName());
                            if (updatedUser.getUserLastName() != null)
                                entity.setUserLastName(updatedUser.getUserLastName());
                            if (updatedUser.getUserEmail() != null)
                                entity.setUserEmail(updatedUser.getUserEmail());
                            if (updatedUser.getUserPass() != null)
                                entity.setUserPass(updatedUser.getUserPass());
                            if (updatedUser.getUserSalt() != null)
                                entity.setUserSalt(updatedUser.getUserSalt());
                            if (updatedUser.getUserMobile() != null)
                                entity.setUserMobile(updatedUser.getUserMobile());
                            if (updatedUser.getUserRoles() != null)
                                entity.setUserRoles(updatedUser.getUserRoles());
                            if (updatedUser.getIsActive() != null)
                                entity.setIsActive(updatedUser.getIsActive());
                            if (updatedUser.getIsVerified() != null)
                                entity.setIsVerified(updatedUser.getIsVerified());
                            if (updatedUser.getValidUntil() != null)
                                entity.setValidUntil(updatedUser.getValidUntil());
                            if (updatedUser.getLastLogin() != null)
                                entity.setLastLogin(updatedUser.getLastLogin());
                            if (updatedUser.getPasswordRequestedAt() != null)
                                entity.setPasswordRequestedAt(updatedUser.getPasswordRequestedAt());
                            if (updatedUser.getCreatedAt() != null)
                                entity.setCreatedAt(updatedUser.getCreatedAt());

                            // Always update 'updatedAt' timestamp
                            entity.setUpdatedAt(Instant.now());

                            entity.persist();
                            return Response.ok(entity).build();
                        })
                        .onItem().ifNull().continueWith(Response.ok().status(Response.Status.NOT_FOUND)::build)
        );
    }


    /**
     * Delete user
     *
     * @param id
     * @return
     */
    public Uni<Response> deleteUser(Long id) {
        return Panache.withTransaction(() -> UserEntity.deleteById(id))
                .map(deleted -> deleted ? Response.ok().status(NO_CONTENT).build() : Response.ok().status(NOT_FOUND).build());
    }

    /**
     * Search User by partial name (case-insensitive)
     *
     * @param query
     * @return
     */
    public Uni<List<UserEntity>> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            // Return empty list if query is blank
            return Uni.createFrom().item(List.of());
        }
        String searchTerm = "%" + query.toLowerCase() + "%";

        //  Search across multiple possible columns
        String jpql = """
                    FROM UserEntity c
                    WHERE LOWER(c.userFirstName) LIKE ?1
                       OR LOWER(c.userEmail) LIKE ?1
                    ORDER BY c.userFirstName ASC
                """;
        return UserEntity.find(jpql, searchTerm).list();
    }

    /**
     * Update Password
     * @param request
     * @return
     */
    public Uni<Response> updatePassword(UpdatePasswordBean request) {
        if (request.getEmail() == null || request.getCurrentPassword() == null || request.getNewPassword() == null) {
            return Uni.createFrom().item(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("Missing required fields")
                            .build()
            );
        }

        String email = request.getEmail().toLowerCase();

        return UserEntity.findByEmail(email)
                .onItem().ifNull().failWith(() ->
                        new WebApplicationException("User not found", Response.Status.NOT_FOUND)
                )
                .flatMap(user -> {
                    try {
                        // 1 Verify old password
                        boolean valid = PasswordUtils.verifyPassword(
                                request.getCurrentPassword(),
                                user.getUserPass(),
                                user.getUserSalt()
                        );

                        if (!valid) {
                            return Uni.createFrom().item(
                                    Response.status(Response.Status.UNAUTHORIZED)
                                            .entity("Incorrect current password")
                                            .build()
                            );
                        }

                        // 2 Generate new hash + salt
                        byte[] newSalt = PasswordUtils.generateSalt();
                        String encodedSalt = Base64.getEncoder().encodeToString(newSalt);
                        String newHashedPassword = PasswordUtils.hashPassword(request.getNewPassword(), newSalt);

                        // 3 Update entity fields
                        user.setUserSalt(encodedSalt);
                        user.setUserPass(newHashedPassword);
                        user.setUpdatedAt(Instant.now());

                        // 4 Save changes
                        return user.flush()
                                .replaceWith(Response.ok("Password updated successfully").build());

                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        e.printStackTrace();
                        return Uni.createFrom().item(
                                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity("Error updating password")
                                        .build()
                        );
                    }
                });
    }
}
