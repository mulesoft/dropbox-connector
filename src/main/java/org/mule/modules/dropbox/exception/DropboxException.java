/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.modules.dropbox.exception;

import com.sun.jersey.api.client.ClientResponse;

public class DropboxException extends RuntimeException {

    private String message;

    public DropboxException(String message){
        this.message = message;
    }

    public DropboxException(Error error) {
        this.message = error.getError();
    }
    
    public DropboxException(ClientResponse response) {
        this.message = response.toString();
    }

    public String getMessage(){
        return message;
    }

}
