package com.mulesoft.module.dropbox.exception;

import java.io.Serializable;

/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */
public class Error implements Serializable {
    private static final long serialVersionUID = 7971386151015364242L;

    private String error;

    public String getError(){
        return error;
    }

    public void setError(String error){
        this.error = error;
    }

}
