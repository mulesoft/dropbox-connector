package com.mulesoft.module.dropbox.model;

import java.io.Serializable;

/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

public class QuotaInformation implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * The user's used quota in shared folders (bytes).
     */
    private Long shared;

    /**
     * The user's total quota allocation (bytes).
     */
    private Long quota;

    /**
     * The user's used quota outside of shared folders (bytes).
     */
    private Long normal;

    public Long getShared() {
        return shared;
    }

    public void setShared(Long shared) {
        this.shared = shared;
    }

    public Long getQuota() {
        return quota;
    }

    public void setQuota(Long quota) {
        this.quota = quota;
    }

    public Long getNormal() {
        return normal;
    }

    public void setNormal(Long normal) {
        this.normal = normal;
    }
}
