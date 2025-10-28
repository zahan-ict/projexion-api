/*
 * Copyright (c) 2025 Nexinx. All rights reserved.
 */
package com.projexion.api.auth;

import com.projexion.api.common.ConfigService;
import com.projexion.api.common.PasswordUtils;
import com.projexion.api.user.UserEntity;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class AuthService {
    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());

    @Inject
    ConfigService configService;

    @Inject
    JWTParser jwtParser;


    @WithSession
    public Uni<Response> userLogin(String username, String password) {
        return UserEntity.find("userEmail", username.toLowerCase())
                .firstResult()
                .onItem().transformToUni(user -> {
                    if (user == null) {
                        return Uni.createFrom().item(
                                Response.ok(Map.of("status", 403, "error", "User not found. Access will not grant")).build());
                    }

                    UserEntity userEntity = (UserEntity) user;

                    try {
                        boolean valid = PasswordUtils.verifyPassword(
                                password,
                                userEntity.getUserPass(),
                                userEntity.getUserSalt()
                        );

                        if (!valid) {
                            // Return 200 OK, but with a body showing 403
                            return Uni.createFrom().item(
                                    Response.ok(Map.of("status", 403, "error", "Invalid credentials")).build());
                        }

                        // Generate JWTs
                        String accessToken = generateAccessToken(username, userEntity.getUserRoles());
                        String refreshToken = generateRefreshToken(username, userEntity.getUserRoles());

                        // Secure cookie
                        NewCookie refreshCookie = new NewCookie.Builder("refresh_token")
                                .value(refreshToken)
                                .path("/")
                                .maxAge(configService.getJwtRefreshTokenCookieExpire())
                                .secure(true)      // only via HTTPS
                                .httpOnly(true)    // not accessible by JS
                                .comment("Refresh Token")
                                .build();


                        return Uni.createFrom().item(
                                Response.ok(Map.of("accessToken", accessToken))
                                        .cookie(refreshCookie)
                                        .build()
                        );

                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        LOGGER.error("Error verifying credentials", e);
                        return Uni.createFrom().item(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                .entity(Map.of("error", "Server error during authentication")).build());
                    }
                });
    }

    public String generateAccessToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        return Jwt.issuer(configService.getJwtIssuer())
                .upn(email) // User Principal Name. it’s a standard JWT claim that represents the unique identity
                .groups(role) // role
                .expiresAt(Instant.now().plusSeconds(configService.getJwtTokenExpire())) // expires in 1 hour
                .sign();
    }

    public String generateRefreshToken(String email, String role) {
        return Jwt.issuer(configService.getJwtIssuer())
                .upn(email)
                .groups(role) // role
                .expiresAt(Instant.now().plusSeconds(configService.getJwtRefreshTokenExpire()))
                .claim("type", "refresh")
                .sign();
    }

    public boolean isRefreshToken(String token) {
        try {
            var jwt = jwtParser.parse(token);
            return "refresh".equals(jwt.getClaim("type"));
        } catch (ParseException e) {
            return false;
        }
    }

    public Uni<Response> refreshAccessToken(String refreshToken) {
        if (refreshToken == null || !isRefreshToken(refreshToken)) {
            return Uni.createFrom().item(
                    Response.ok(Map.of("status", 403, "error", "Invalid or missing refresh token")).build());
        }

        try {
            var jwt = jwtParser.parse(refreshToken);
            String email = jwt.getClaim("upn");
            Set<String> roles = jwt.getGroups(); // Assuming this returns a Set<String>
            String role = roles != null && !roles.isEmpty() ? roles.iterator().next() : "reader";
            String newAccessToken = generateAccessToken(email,role);
            return Uni.createFrom().item(Response.ok(Map.of("accessToken", newAccessToken)).build());

        } catch (Exception e) {
            LOGGER.error("Error refreshing token", e);
            return Uni.createFrom().item(Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Refresh failed")).build());
        }
    }

    public Uni<Response> userLogout() {
        NewCookie expiredRefresh = new NewCookie.Builder("refresh_token")
                .value("")
                .path("/")
                .maxAge(0)
                .secure(configService.isJwtRefreshTokeHttps())
                .httpOnly(true)
                .comment("Expired")
                .build();

//        // Add a temporary cookie to signal logout (readable by JS)
//        NewCookie logoutFlag = new NewCookie.Builder("loggedOut")
//                .value("true")
//                .path("/")
//                .maxAge(300) // expires in 5 minutes
//                .secure(true)
//                .httpOnly(false) // must be accessible to JS
//                .build();


        return Uni.createFrom().item(Response.ok()
                .cookie(expiredRefresh)
//                .cookie(logoutFlag)
                .entity(Map.of("message", "Logout successful"))
                .build());
    }
}