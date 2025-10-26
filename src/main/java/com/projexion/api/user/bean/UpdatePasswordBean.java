/*
 * Copyright (c) 2025 Nexinx. All rights reserved.
 */
package com.projexion.api.user.bean;

import lombok.Data;

@Data
public class UpdatePasswordBean {
    private String email;
    private String currentPassword;
    private String newPassword;
}
