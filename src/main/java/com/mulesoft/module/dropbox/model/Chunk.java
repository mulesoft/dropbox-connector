package com.mulesoft.module.dropbox.model;

import java.io.Serializable;

/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */
public class Chunk implements Serializable {
    private static final long serialVersionUID = -1L;

    /*
    {
        "upload_id": "v0k84B0AT9fYkfMUp0sBTA",
        "offset": 31337,
        "expires": "Tue, 19 Jul 2011 21:55:38 +0000"
    }
    */

    /***
     * The unique ID of the in-progress upload on the server. If left blank, the server will create a new upload session.
     */
    private String uploadId;

    /***
     *  The byte offset of this chunk, relative to the beginning of the full file. The server will verify that this matches the offset it expects. If it does not, the server will return an error with the expected offset.
     */
    private Long offset;

    /**
     * The expiration date for this upload session.
     */
    private String expires;

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }
}
