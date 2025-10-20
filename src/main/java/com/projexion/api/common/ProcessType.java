/*
 * Copyright (c) 2024 nexinx. All rights reserved.
 */
package com.projexion.api.common;

public enum ProcessType {
    INACTIVE,  // if e-paper is not connected
    ACTIVE,   // if at least one e-paper is connected
    QUEUED,  // if at least one e-paper is connected and product is just updated
    SUCCESS, // if process is successfully
    FAILED  // if process is failed
}
