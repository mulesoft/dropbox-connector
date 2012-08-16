/**
 * Mule Dropbox Cloud Connector
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

/**
 * This file was automatically generated by the Mule Development Kit
 */
package com.mulesoft.module.dropbox;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.oauth.OAuth;
import org.mule.api.annotations.oauth.OAuthAccessToken;
import org.mule.api.annotations.oauth.OAuthAccessTokenSecret;
import org.mule.api.annotations.oauth.OAuthConsumerKey;
import org.mule.api.annotations.oauth.OAuthConsumerSecret;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.api.annotations.param.Payload;
import org.mule.util.StringUtils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.impl.MultiPartWriter;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.HMAC_SHA1;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;

/**
 * Dropbox Cloud Connector.
 * The Dropbox Connector will allow to use the Dropbox REST API. Almost every operation that can be done via the API can be done thru this connector.
 * 
 * @author MuleSoft, Inc.
 */
@Connector(name = "dropbox", schemaVersion = "3.2.2")
@OAuth(requestTokenUrl = "https://api.dropbox.com/1/oauth/request_token", accessTokenUrl = "https://api.dropbox.com/1/oauth/access_token", authorizationUrl = "https://www.dropbox.com/1/oauth/authorize", verifierRegex = "oauth_token=([^&]+)")
public class DropboxConnector {
	/**
	 * URL of the Dropbox server API
	 */
	@Configurable
	@Optional
	@Default("api.dropbox.com")
	private String server;

	/**
	 * URL of the Dropbox server content API
	 */

	@Configurable
	@Optional
	@Default("api-content.dropbox.com")
	private String contentServer;

	/**
	 * Dropbox server port
	 */
	@Configurable
	@Optional
	@Default("80")
	private int port;

	/**
	 * User email address
	 */
	@Configurable
	private String userEmail;

	/**
	 * Password
	 */
	@Configurable
	private String userPassword;

	/**
	 * Application key
	 */
	@Configurable
	@OAuthConsumerKey
	private String appKey;

	/**
	 * Application secret
	 */
	@Configurable
	@OAuthConsumerSecret
	private String appSecret;

	/**
	 * debug mode
	 */
	@Configurable
	@Optional
	@Default("false")
	private boolean debug;

	@OAuthAccessToken
	private String accessToken;

	@OAuthAccessTokenSecret
	private String accessTokenSecret;

	private Client client;

	/**
	 * Upload file to Dropbox. The payload is an InputStream containing bytes of
	 * the data to be uploaded.
	 * 
	 * {@sample.xml ../../../doc/Dropbox-connector.xml.sample
	 * dropbox:upload-file}
	 * 
	 * @param accessToken
	 *            accessToken
	 * @param accessTokenSecret
	 *            access token secret
	 * @param fileDataObj
	 *            file to be uploaded
	 * @param overwrite
	 * 				overwrite file in case it already exists           
	 * @param path
	 *            The destination path
	 * @param filename
	 *            The destination file name
	 * 
	 * @return http response
	 * @throws Exception
	 *             exception
	 */
	@Processor
	public String uploadFile(@Payload InputStream fileDataObj,
							@Optional @Default("true") Boolean overwrite,
							String path,
							String filename) throws Exception {
		
		final InputStream fileData = (InputStream) fileDataObj;
		final String apiUrl = getApiContentUrl(path);

		final FormDataBodyPart formDataBodyPart = new FormDataBodyPart(fileData, MediaType.APPLICATION_OCTET_STREAM_TYPE);
		MultiPart parts = new FormDataMultiPart().bodyPart(formDataBodyPart);

		formDataBodyPart.setContentDisposition(FormDataContentDisposition
				.name("file")
				.fileName(filename)
				.size(fileData.available())
				.modificationDate(new Date()).build());

		WebResource r = getClient().resource(apiUrl)
													.queryParam("file",	filename)
													.queryParam("overwrite", overwrite.toString());

		if (isDebug()) {
			r.addFilter(new LoggingFilter());
		}
		r.addFilter(getOAuthClientFilter(accessToken, accessTokenSecret));

		String response = r.type(MediaType.MULTIPART_FORM_DATA_TYPE).post(
				String.class, parts);

		return response;
	}

	/**
	 * Create new folder on Dropbox
	 * 
	 * {@sample.xml ../../../doc/Dropbox-connector.xml.sample
	 * dropbox:create-folder}
	 * 
	 * @param accessToken
	 *            accessToken
	 * @param accessTokenSecret
	 *            access token secret
	 * @param path
	 *            full path of the folder to be created
	 * 
	 * @return http response
	 * @throws Exception
	 *             exception
	 */
	@Processor
	public String createFolder(String path) throws Exception {
		final String apiUrl = getApiUrl("fileops/create_folder");

		WebResource r = getClient().resource(apiUrl);
		r.accept(MediaType.APPLICATION_JSON_TYPE).type(
				MediaType.APPLICATION_FORM_URLENCODED_TYPE);
		r = r.queryParam("root", "dropbox").queryParam("path", path);

		if (isDebug()) {
			r.addFilter(new LoggingFilter());
		}
		r.addFilter(getOAuthClientFilter(accessToken, accessTokenSecret));
		String response = r.post(String.class);

		return response;
	}

	/**
	 * Deletes a file or folder.
	 * 
	 * {@sample.xml ../../../doc/Dropbox-connector.xml.sample dropbox:delete}
	 * 
	 * @param accessToken
	 *            accessToken
	 * @param accessTokenSecret
	 *            access token secret
	 * @param path
	 *            full path to the file to be deleted
	 * 
	 * @return http response
	 * @throws Exception
	 *             exception
	 */
	@Processor
	public String delete(String path) throws Exception {
		final String apiUrl = getApiUrl("fileops/delete");

		WebResource r = getClient().resource(apiUrl);
		r.accept(MediaType.APPLICATION_JSON_TYPE).type(
				MediaType.APPLICATION_FORM_URLENCODED_TYPE);
		r = r.queryParam("root", "dropbox").queryParam("path", path);

		if (isDebug()) {
			r.addFilter(new LoggingFilter());
		}
		r.addFilter(getOAuthClientFilter(accessToken, accessTokenSecret));

		String response = r.post(String.class);

		return response;
	}

	/**
	 * Downloads a file from Dropbox
	 * 
	 * {@sample.xml ../../../doc/Dropbox-connector.xml.sample
	 * dropbox:download-file}
	 * 
	 * @param accessToken
	 *            accessToken
	 * @param accessTokenSecret
	 *            access token secret
	 * @param path
	 *            path to the file
	 * @param delete
	 *            delete the file on the Dropbox after download (ignored if
	 *            moveTo is set)
	 * @param moveTo
	 *            Specifies the destination path, including the new name for the
	 *            file or folder, relative to root.
	 * 
	 * @return Stream containing the downloaded file data
	 * @throws Exception
	 *             exception
	 */
	@Processor
	public InputStream downloadFile(String path,
			@Optional @Default("false") boolean delete,
			@Optional @Default("") String moveTo) throws Exception {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		final String apiUrl = getApiContentUrl(path);

		WebResource r = getClient().resource(apiUrl);

		if (isDebug()) {
			r.addFilter(new LoggingFilter());
		}
		r.addFilter(getOAuthClientFilter(accessToken, accessTokenSecret));

		InputStream response = r.get(InputStream.class);

		if (!StringUtils.isEmpty(moveTo))
			move(path, moveTo);
		else if (delete)
			delete(path);
		return response;
	}

	/**
	 * Lists the content of the remote directory
	 * 
	 * {@sample.xml ../../../doc/Dropbox-connector.xml.sample dropbox:list}
	 * 
	 * @param accessToken
	 *            accessToken
	 * @param accessTokenSecret
	 *            access token secret
	 * @param path
	 *            path to the remote directory
	 * 
	 * @return List of files and/or folders
	 * @throws Exception
	 *             exception
	 */
	@Processor
	public List<String> list(String path) throws Exception {
		final String apiUrl = getApiUrl("metadata/dropbox");

		WebResource r = getClient().resource(apiUrl).path(path);

		if (isDebug()) {
			r.addFilter(new LoggingFilter());
		}
		r.addFilter(getOAuthClientFilter(accessToken, accessTokenSecret));

		String response = r.get(String.class);
		final JSONObject root = (JSONObject) JSONValue.parse(response);
		final JSONArray files = (JSONArray) root.get("contents");
		List<String> paths = new ArrayList<String>(files.size());
		for (int i = 0; i < files.size(); i++) {
			JSONObject file = (JSONObject) files.get(i);
			paths.add(file.get("path").toString());
		}
		return paths;
	}

	/**
	 * Moves a file or folder to a new location.
	 * 
	 * {@sample.xml ../../../doc/Dropbox-connector.xml.sample dropbox:move}
	 * 
	 * @param accessToken
	 *            accessToken
	 * @param accessTokenSecret
	 *            access token secret
	 * @param from
	 *            Specifies the file or folder to be moved from, relative to
	 *            root.
	 * @param to
	 *            Specifies the destination path, including the new name for the
	 *            file or folder, relative to root.
	 * 
	 * @return http response
	 * @throws Exception
	 *             exception
	 */
	@Processor
	public String move(String from, String to) throws Exception {
		if (from.startsWith("/")) {
			from = from.substring(1);
		}
		if (to.startsWith("/")) {
			to = to.substring(1);
		}
		final String apiUrl = getApiUrl("fileops/move");

		WebResource r = client.resource(apiUrl).queryParam("root", "dropbox")
				.queryParam("from_path", from).queryParam("to_path", to);

		if (isDebug()) {
			r.addFilter(new LoggingFilter());
		}
		r.addFilter(getOAuthClientFilter(accessToken, accessTokenSecret));

		String response = r.post(String.class);

		return response;
	}

	// --------------------------------------

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getContentServer() {
		return contentServer;
	}

	public void setContentServer(String contentServer) {
		this.contentServer = contentServer;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	// --------------------------------------------------
	/**
	 * @param path
	 *            path without leading /
	 */
	protected String getApiUrl(String path) {
		return String.format("%s://%s/1/%s", "https", getServer(), path);
	}

	/**
	 * @param path
	 *            path without leading /
	 */
	protected String getApiContentUrl(String path) {
		return String.format("%s://%s/1/files/dropbox/%s", "https",
				getContentServer(), path);
	}

	protected Client getClient() {
		if (client == null) {
			ClientConfig cc = new DefaultClientConfig();
			cc.getClasses().add(MultiPartWriter.class);
			client = Client.create(cc);
		}
		return client;
	}
	
	protected OAuthClientFilter getOAuthClientFilter(String accessToken, String accessTokenSecret) {
        OAuthParameters params = new OAuthParameters()
                .signatureMethod(HMAC_SHA1.NAME)
                .consumerKey(getAppKey())
                .token(accessToken).version();

        OAuthSecrets secrets = new OAuthSecrets()
                .consumerSecret(getAppSecret())
                .tokenSecret(accessTokenSecret);

        OAuthClientFilter filter = new OAuthClientFilter(
                getClient().getProviders(),
                params,
                secrets
        );        
        
        return filter;
    }

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}

	public void setAccessTokenSecret(String accessTokenSecret) {
		this.accessTokenSecret = accessTokenSecret;
	}
}
