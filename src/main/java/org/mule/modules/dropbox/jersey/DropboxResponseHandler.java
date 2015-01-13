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
import com.sun.jersey.api.client.ClientHandlerException;
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
		//SFDC-2218 Received a Dropbox exception (ClientHandlerException because the response does not have an entity Error) 
		//that we can not reproduce, we need to change the connector in order to get the message. This is the way we can get
		//the possible cause of the error. 
		if(!response.hasEntity()){
		    throw new DropboxException(response);
		}
        //If getEntity fails throw a ClientHandlerException.
		try{
		    Error error = response.getEntity(Error.class);
            throw new DropboxException(error);
		}catch(ClientHandlerException e){
		    //Try to get the entity as a String class in order to get the cause of the problem
		    try{
	            String errorMessage = response.getEntity(String.class);
	            throw new DropboxException(errorMessage);
	        }catch(ClientHandlerException e1){
	            //If the getEntity fails we throw the exception hoping the response.toString() could help us to determine the error.
	            throw new DropboxException(response);
	        }
		}
	}
}
