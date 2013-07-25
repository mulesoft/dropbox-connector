/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.modules.dropbox.jersey;

import org.mule.modules.dropbox.exception.DropboxException;
import org.mule.modules.dropbox.exception.DropboxTokenExpiredException;
import org.mule.commons.jersey.DefaultResponseHandler;
import org.mule.modules.dropbox.exception.Error;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * 
 * @author mariano.gonzalez@mulesoft.com
 *
 */
public class DropboxResponseHandler extends DefaultResponseHandler {

	public static final DropboxResponseHandler INSTANCE = new DropboxResponseHandler();
	
	private DropboxResponseHandler() {}
	
	@Override
	public <T> T onFailure(ClientResponse response, int status, int[] expectedStatus) {
		if (status == Status.UNAUTHORIZED.getStatusCode()) {
			throw new DropboxTokenExpiredException();
		}

		Error error = response.getEntity(Error.class);
        throw new DropboxException(error);
	}
}
