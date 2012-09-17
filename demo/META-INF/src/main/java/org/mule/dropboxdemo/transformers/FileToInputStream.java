/**
 * Mule Dropbox Cloud Connector
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.dropboxdemo.transformers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

public class FileToInputStream extends AbstractMessageTransformer {

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding)
			throws TransformerException {
		
		Map<String,String> payload = new HashMap<String,String>();
		
		payload = message.getPayload(payload.getClass());
		
		try {
			return new FileInputStream(new File( System.getProperty("user.dir") + "/src/main/resources/" + payload.get("inputFile")));
		} catch (FileNotFoundException e) {
			throw new TransformerException(null);
		}
		
	}

}
