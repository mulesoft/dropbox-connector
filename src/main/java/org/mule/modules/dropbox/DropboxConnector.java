/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.modules.dropbox;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.impl.provider.entity.FormMultivaluedMapProvider;
import com.sun.jersey.core.impl.provider.entity.FormProvider;
import com.sun.jersey.core.impl.provider.entity.InputStreamProvider;
import com.sun.jersey.core.impl.provider.entity.MimeMultipartProvider;
import com.sun.jersey.multipart.impl.MultiPartWriter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.mule.RequestContext;
import org.mule.api.MuleException;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.lifecycle.Start;
import org.mule.api.annotations.oauth.*;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.api.annotations.param.Payload;
import org.mule.commons.jersey.JerseyUtil;
import org.mule.commons.jersey.provider.GsonProvider;
import org.mule.modules.dropbox.exception.DropboxException;
import org.mule.modules.dropbox.exception.DropboxTokenExpiredException;
import org.mule.modules.dropbox.jersey.AuthBuilderBehaviour;
import org.mule.modules.dropbox.jersey.DropboxResponseHandler;
import org.mule.modules.dropbox.jersey.MediaTypesBuilderBehaviour;
import org.mule.modules.dropbox.jersey.json.GsonFactory;
import org.mule.modules.dropbox.model.AccountInformation;
import org.mule.modules.dropbox.model.Item;
import org.mule.modules.dropbox.model.version2.MetadataEntry;
import org.mule.modules.dropbox.model.version2.FullAccount;
import org.mule.modules.dropbox.model.version2.ListFolderResult;
import org.mule.modules.dropbox.model.version2.UploadSession;

import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Dropbox Cloud Connector.
 * The Dropbox Connector will allow to use the Dropbox REST API. Almost every operation that can be done via the API can be done thru this connector.
 * 
 * @author MuleSoft, Inc.
 */
@Connector(name = "dropbox", schemaVersion = "3.3.0", friendlyName = "Dropbox", minMuleVersion = "3.4")
@OAuth2(authorizationUrl = "https://www.dropbox.com/oauth2/authorize",
		accessTokenUrl = "https://api.dropbox.com/oauth2/token",
        accessTokenRegex = "\"access_token\"[ ]*:[ ]*\"([^\\\"]*)\"",
        expirationRegex = "\"expires_in\"[ ]*:[ ]*([\\d]*)",
        refreshTokenRegex = "\"refresh_token\"[ ]*:[ ]*\"([^\\\"]*)\"")
public class DropboxConnector {
    private static final String ROOT_PARAM = "dropbox";

    private static final int MAX_UPLOAD_BUFFER_LEN = 4194304;
    public static final String DEFAULT_SERVER = "https://api.dropboxapi.com/2/";
    public static final String DEFAULT_CONTENT_SERVER = "https://content.dropboxapi.com/2/";

    private String accessTokenIdentifier;

	/**                        `
	 * URL of the Dropbox server API
	 */
	@Configurable
	@Optional
	@Default(DEFAULT_SERVER)
	private String server;

	/**
	 * URL of the Dropbox server content API
	 */
	@Configurable
	@Optional
	@Default(DEFAULT_CONTENT_SERVER)
	private String contentServer;

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

	@OAuthAccessToken
	private String accessToken;

    private JerseyUtil jerseyUtil;

    private JerseyUtil jerseyUtilContent;

    private WebResource apiResource;

    private WebResource contentResource;

    /**
     * This method initiates the dropbox client and the auth callback.
     * @throws MuleException
     */
    @Start
    public void init() throws MuleException {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        clientConfig.getClasses().add(MultiPartWriter.class);
        clientConfig.getClasses().add(MimeMultipartProvider.class);
        clientConfig.getClasses().add(InputStreamProvider.class);
        clientConfig.getClasses().add(FormProvider.class);
        clientConfig.getClasses().add(FormMultivaluedMapProvider.class);
        clientConfig.getSingletons().add(new GsonProvider(GsonFactory.get()));

        Client client = Client.create(clientConfig);
        Client contentClient = Client.create(clientConfig);
        contentClient.setChunkedEncodingSize(null);

		client.addFilter(new LoggingFilter(System.out));
		contentClient.addFilter(new LoggingFilter(System.out));

        this.initJerseyUtil();

        this.apiResource = client.resource(this.server);
        this.contentResource = contentClient.resource(this.contentServer);
    }

    private void initJerseyUtil() {
        JerseyUtil.Builder builder = JerseyUtil.builder().addRequestBehaviour(MediaTypesBuilderBehaviour.INSTANCE)
                .addRequestBehaviour(new AuthBuilderBehaviour(this)).setResponseHandler(DropboxResponseHandler.INSTANCE);

        JerseyUtil.Builder builderContent = JerseyUtil.builder()
                .addRequestBehaviour(new AuthBuilderBehaviour(this)).setResponseHandler(DropboxResponseHandler.INSTANCE);

        this.jerseyUtil = builder.build();
        this.jerseyUtilContent = builderContent.build();
    }

    @OAuthAccessTokenIdentifier
    public String getOAuthTokenAccessIdentifier() throws Exception {
        if (this.accessTokenIdentifier == null) {
            this.accessTokenIdentifier = this.getAccount().getUid();
        }

        return this.accessTokenIdentifier;
    }

    @OAuthPostAuthorization
    public void postAuthorize() throws Exception{
        //REMOVE THIS: this hack needs to be removed once the connector returns the remote user id
        // by itself in a right way after authorize
        RequestContext.getEvent().setFlowVariable("remoteUserId", this.getAccount().getUid());
    }

	/**
	 * Deletes a file or folder.
	 *
	 * {@sample.xml ../../../doc/Dropbox-connector.xml.sample dropbox:delete}
	 *
	 * @param path
	 *            full path to the file to be deleted
	 *
	 * @return Item with the metadata of the deleted object
	 * @throws Exception
	 *             exception
	 */
	@Processor
	@OAuthProtected
    @OAuthInvalidateAccessTokenOn(exception = DropboxTokenExpiredException.class)
	public Item delete(String path) throws Exception {
//        return this.jerseyUtil.post(
//                this.apiResource.path("fileops").path("delete").queryParam("root", ROOT_PARAM).queryParam("path", path), Item.class, 200);
		return getItemFromMetadataEntry(deleteV2(path));
	}

    @Processor
    @OAuthProtected
    @OAuthInvalidateAccessTokenOn(exception = DropboxTokenExpiredException.class)
	public MetadataEntry deleteV2(String path) {
		String jsonEntity = "{"+pathAsJson(path)+"}";
		return this.jerseyUtil.post(
				this.apiResource
						.path("files").path("delete")
						.type(MediaType.APPLICATION_JSON_TYPE)
						.entity(jsonAsMap(jsonEntity)),
				MetadataEntry.class,
				200
		);
	}

	private Map<String, Object> jsonAsMap(String json) {
	    return new Gson().fromJson(json, Map.class);
    }

	private String pathAsJson(String path) {
//        return "\"path\": \"/dropbox/" + adaptPath(path) + "\"";
        return "\"path\": \"" + adaptPath(path) + "\"";
	}

	/**
	 * Downloads a file from Dropbox
	 * 
	 * {@sample.xml ../../../doc/Dropbox-connector.xml.sample dropbox:download-file}
	 * 
	 * @param path
	 *            path to the file
	 * @param delete
	 *            delete the file on the Dropbox after download (ignored if
	 *            moveTo is set)
	 *
	 * @return Stream containing the downloaded file data
	 * @throws Exception
	 *             exception
	 */
	@Processor
	@OAuthProtected
    @OAuthInvalidateAccessTokenOn(exception = DropboxTokenExpiredException.class)
    public InputStream downloadFile(String path,
			@Optional @Default("false") boolean delete) throws Exception {

//        InputStream response = this.jerseyUtil.get(this.contentResource
//                                                            .path("files")
//                                                            .path(ROOT_PARAM)
//                                                            .path(adaptPath(path)), InputStream.class, 200);
		InputStream response = downloadFileV2(path);
		if (delete)
			this.delete(path);

		return response;
	}

    @Processor
    @OAuthProtected
    @OAuthInvalidateAccessTokenOn(exception = DropboxTokenExpiredException.class)
    public InputStream downloadFileV2(String path) {
		String jsonEntity = "{"+pathAsJson(path)+"}";
		return this.jerseyUtilContent.post(
				this.contentResource
						.path("files").path("download")
						.header("Dropbox-API-Arg", jsonEntity),
				InputStream.class,
				200
		);
	}


	/**
	 * Lists the metadata of a file or directory
	 *
	 * {@sample.xml ../../../doc/Dropbox-connector.xml.sample dropbox:get-metadata}
	 *
	 * @param path
	 *            path to the file or directory
	 *
	 * @return Metadata of file or directory
	 * @throws Exception
	 *             exception
	 */
	@Processor
	@OAuthProtected
	@OAuthInvalidateAccessTokenOn(exception = DropboxTokenExpiredException.class)
	public Item getMetadata(String path) throws Exception {
//		final String apiPath = adaptPath(path);
//        return this.jerseyUtil.get(
//                this.apiResource.path("metadata").path("dropbox").path(apiPath), Item.class, 200);
		return getItemFromMetadataEntry(getMetadataV2(path));
	}

	@Processor
	@OAuthProtected
	@OAuthInvalidateAccessTokenOn(exception = DropboxTokenExpiredException.class)
	public MetadataEntry getMetadataV2(String path) throws Exception {
		String jsonEntity =
						"{" +
						"    " +pathAsJson(path)+ "," +
						"    \"include_media_info\": false," +
						"    \"include_deleted\": false," +
						"    \"include_has_explicit_shared_members\": false" +
						"}";
		return this.jerseyUtil.post(
				this.apiResource
						.path("files").path("get_metadata")
						.type(MediaType.APPLICATION_JSON_TYPE)
						.entity(jsonAsMap(jsonEntity)),
				MetadataEntry.class,
				200
		);
	}

	/**
	 * Lists the content of the remote directory
	 * 
	 * {@sample.xml ../../../doc/Dropbox-connector.xml.sample dropbox:list}
	 * 
	 * @param path
	 *            path to the remote directory
	 * 
	 * @return List of files and/or folders
	 * @throws Exception
	 *             exception
	 */
	@Processor
	@OAuthProtected
    @OAuthInvalidateAccessTokenOn(exception = DropboxTokenExpiredException.class)
	public Item list(String path) throws Exception {
//		final String apiPath = adaptPath(path);
//        return this.jerseyUtil.get(
//                this.apiResource.path("metadata").path("dropbox").path(apiPath), Item.class, 200);
		return getItemFromListFolderResult(listV2(path));
	}

	private Item getItemFromListFolderResult(ListFolderResult listFolderResult) {
		List<Item> items = new LinkedList<Item>();
		for(MetadataEntry metadataEntry : listFolderResult.getEntries()) {
			items.add(getItemFromMetadataEntry(metadataEntry));
		}
		Item item = new Item();
		item.setContents(items);
		return item;
	}

	private Item getItemFromMetadataEntry(MetadataEntry metadataEntry) {
/*
            "size": "225.4KB",
            "rev": "35e97029684fe",
            "hash": "35e97029684fe",
            "thumb_exists": false,
            "bytes": 230783,
            "modified": "Tue, 19 Jul 2011 21:55:38 +0000",
            "client_mtime": "Mon, 18 Jul 2011 18:04:35 +0000",
            "path": "/Getting_Started.pdf",
            "is_dir": false,
            "is_deleted": false,
            "icon": "page_white_acrobat",
            "root": "dropbox",
            "mime_type": "application/pdf",
            "revision": 220823
 */
		Item item = new Item();
		item.setSize(String.valueOf(metadataEntry.getSize()));
		item.setRev(metadataEntry.getRev());
		item.setHash(metadataEntry.getContentHash());
		item.setBytes(metadataEntry.getSize());
		item.setModified(metadataEntry.getServerModified());
		item.setClientMtime(metadataEntry.getClientModified());
		item.setPath(metadataEntry.getPathLower());
		item.setDir(metadataEntry.getType()== MetadataEntry.Type.folder);
		item.setDeleted(metadataEntry.getType()== MetadataEntry.Type.deleted);
		return item;
	}

    @Processor
    @OAuthProtected
    @OAuthInvalidateAccessTokenOn(exception = DropboxTokenExpiredException.class)
    public ListFolderResult listV2(String path) throws Exception {
		String jsonEntity =
				"{" +
				"    " +pathAsJson(path)+ "," +
				"    \"recursive\": false," +
				"    \"include_media_info\": false," +
				"    \"include_deleted\": false," +
				"    \"include_has_explicit_shared_members\": false" +
				"}";
        return this.jerseyUtil.post(
				this.apiResource
						.path("files").path("list_folder")
						.type(MediaType.APPLICATION_JSON_TYPE)
						.entity(jsonAsMap(jsonEntity)),
				ListFolderResult.class,
				200
		);
	}


    /**
     * Requests the account's information.
     *
     * {@sample.xml ../../../doc/Dropbox-connector.xml.sample dropbox:get-account}
     *
     * @return AccountInformation. A Dropbox account's information.
     *
     * @throws Exception exception
     */
    @Processor
    @OAuthProtected
    @OAuthInvalidateAccessTokenOn(exception = DropboxTokenExpiredException.class)
    public AccountInformation getAccount() throws Exception {
//        return this.jerseyUtil.get(
//                this.apiResource.path("account").path("info"), AccountInformation.class, 200);
		return getAccountInformationFromFullAccount(getAccountV2());
    }

    private AccountInformation getAccountInformationFromFullAccount(FullAccount fullAccount) {
    	AccountInformation accountInformation = new AccountInformation();
    	accountInformation.setUid(fullAccount.getAccountId());
    	accountInformation.setCountry(fullAccount.getCountry());
    	accountInformation.setDisplayName(fullAccount.getName().getDisplayName());
    	accountInformation.setEmail(fullAccount.getEmail());
    	accountInformation.setReferalLink(fullAccount.getReferralLink());
    	return accountInformation;
	}

    @Processor
    @OAuthProtected
    @OAuthInvalidateAccessTokenOn(exception = DropboxTokenExpiredException.class)
    public FullAccount getAccountV2() {
		return this.jerseyUtilContent.post(
				this.apiResource.path("users").path("get_current_account"), FullAccount.class, 200);
	}


    /**
     * Upload file to Dropbox. The payload is an InputStream containing bytes of
     * the data to be uploaded.
     *
     * This version of the method supports streams of arbitrary length
     *
     * {@sample.xml ../../../doc/Dropbox-connector.xml.sample dropbox:upload-long-stream}
     *
     * @param fileData
     *            file to be uploaded
     * @param overwrite
     * 				overwrite file in case it already exists
     * @param path
     *            The destination path
     * @param filename
     *            The destination file name
     *
     * @return Item with the metadata of the uploaded object
     * @throws Exception
     *             exception
     */
    @SuppressWarnings("resource")
    @Processor
    @OAuthProtected
    @OAuthInvalidateAccessTokenOn(exception = DropboxTokenExpiredException.class)
    public Item uploadLongStream(@Payload InputStream fileData,
                                 @Optional @Default("true") Boolean overwrite,
                                 String path,
                                 String filename) throws Exception {
        return getItemFromMetadataEntry(uploadLongStreamV2(fileData, overwrite, path, filename));
    }

    @SuppressWarnings("resource")
    @Processor
    @OAuthProtected
    @OAuthInvalidateAccessTokenOn(exception = DropboxTokenExpiredException.class)
    public MetadataEntry uploadLongStreamV2(@Payload InputStream fileData,
											@Optional @Default("true") Boolean overwrite,
											String path,
											String filename) throws Exception {
		path = StringUtils.join( new String[] {adaptPath(path), filename } , "/");
        byte[] buffer = new byte[MAX_UPLOAD_BUFFER_LEN];
        Long readBytesAccum = 0L;
        int readBytes = 0;
        String uploadId = null;

        while(readBytes >= 0) {
            readBytes = fileData.read(buffer);

            ByteArrayInputStream chunk = new ByteArrayInputStream(ArrayUtils.subarray(buffer,0, readBytes));

            if (readBytes > 0) {
				WebResource.Builder request;
            	if (uploadId==null) {
					request = this
							.contentResource
							.path("files")
							.path("upload_session/start")
							.header("Dropbox-API-Arg", "{\"close\": false}")
							.type(MediaType.APPLICATION_OCTET_STREAM);
				} else {
					String jsonEntity =
							"{" +
							"    \"cursor\": {" +
							"        \"session_id\": \"" + uploadId + "\"," +
							"        \"offset\": " + readBytesAccum.toString() +
							"    }," +
							"    \"close\": false" +
							"}";
					request = this
							.contentResource
							.path("files")
							.path("upload_session/append_v2")
							.header("Dropbox-API-Arg", jsonEntity);
				}
				request
						.entity(chunk)
						.type(MediaType.APPLICATION_OCTET_STREAM)
						.accept(MediaType.APPLICATION_JSON);

                UploadSession uploadSession = this.jerseyUtilContent.post(request, UploadSession.class, 200);

                // Set the uploadId after the first successful upload
                if (uploadId == null && uploadSession != null) {
					uploadId = uploadSession.getSessionId();
				}

                readBytesAccum += readBytes;
            }
        }

        String previousRev = null;
		try {
			Item file = this.getMetadata(path);
			if (file != null) {
				previousRev = file.getRev();
			}
		} catch (DropboxException e) {
			// file was not found
		}

		String jsonEntity = null;
		if (previousRev==null || !overwrite) {
			// Add
			jsonEntity =
					"{" +
					"    \"cursor\": {" +
					"        \"session_id\": \"" + uploadId + "\"," +
					"        \"offset\": 0" +
					"    }," +
					"    \"commit\": {" +
					"        " + pathAsJson(path) + "," +
					"        \"mode\": \"add\"," +
					"        \"autorename\": true," +
					"        \"mute\": false" +
					"    }" +
					"}";
		} else {
			// Update
			jsonEntity =
					"{" +
					"    \"cursor\": {" +
					"        \"session_id\": \"" + uploadId + "\"," +
					"        \"offset\": 0" +
					"    }," +
					"    \"commit\": {" +
					"        " + pathAsJson(path) + "," +
					"        \"mode\": {" +
					"            \".tag\": \"update\"," +
					"            \"update\": \"" + previousRev + "\"" +
					"        }," +
					"        \"autorename\": false," +
					"        \"mute\": false" +
					"    }" +
					"}";
		}
		WebResource.Builder request = this
				.contentResource
				.path("files")
				.path("upload_session/finish")
				.header("Dropbox-API-Arg", jsonEntity)
				.type(MediaType.APPLICATION_OCTET_STREAM)
				.accept(MediaType.APPLICATION_JSON);

        return jerseyUtilContent.post(request, MetadataEntry.class, 200);
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

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	private String adaptPath(String path) {
		path = StringUtils.trim(path);
		if(StringUtils.isBlank(path) || StringUtils.equals(path, "/"))
			return "";
		if(!StringUtils.startsWith(path, "/")) {
			return "/" + path;
		}
		return path;
	}
}
