/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.modules.dropbox.jersey;

import javax.ws.rs.core.MediaType;

import org.mule.commons.jersey.RequestBehaviour;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource.Builder;

/**
 * 
 * @author mariano.gonzalez@mulesoft.com
 *
 */
public class MediaTypesBuilderBehaviour implements RequestBehaviour {
	
	public static final MediaTypesBuilderBehaviour INSTANCE = new MediaTypesBuilderBehaviour();
	
	private MediaTypesBuilderBehaviour() {}
	
	@Override
	public <T> Builder behave(Builder builder, String method, Class<T> entityClass) {
        return builder.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON);
	}

	@Override
	public <T> Builder behave(Builder builder, String method, GenericType<T> type) {
        return builder.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON);
	}
}
