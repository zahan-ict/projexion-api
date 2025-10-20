/*
 * Copyright (c) 2025 Nexinx All rights reserved.
 */
package com.projexion.api.common;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ext.Provider;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Provider
public class ConfigService {

    /**
     * Jwt url issuer validation
     */
    @Getter
    @ConfigProperty(name = "mp.jwt.verify.issuer")
    protected String jwtIssuer;


    @Getter
    @ConfigProperty(name = "jwt.token.expire")
    protected Long jwtTokenExpire;

    @Getter
    @ConfigProperty(name = "jwt.refresh.token.expire")
    protected Long jwtRefreshTokenExpire;

    @Getter
    @ConfigProperty(name = "jwt.refresh.token.cookie.expire")
    protected int jwtRefreshTokenCookieExpire;
}
