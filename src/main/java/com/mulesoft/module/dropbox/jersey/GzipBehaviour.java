/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package com.mulesoft.module.dropbox.jersey;

import org.mule.commons.jersey.RequestBehaviour;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

/**
 * 
 * @author mariano.gonzalez@mulesoft.com
 *
 */
public class GzipBehaviour implements RequestBehaviour {
	
	public static final GzipBehaviour INSTANCE = new GzipBehaviour();
	
	private GzipBehaviour(){}
	
	public <T> WebResource.Builder behave(WebResource.Builder builder, String method, Class<T> entityClass) {
		return builder.header("Accept-Encoding", "gzip, deflate");
	}

	@Override
	public <T> Builder behave(Builder builder, String method, GenericType<T> type) {
		return builder.header("Accept-Encoding", "gzip, deflate");
	};

}
