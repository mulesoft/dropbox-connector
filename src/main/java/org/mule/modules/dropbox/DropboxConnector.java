/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.modules.dropbox;

import com.google.common.collect.ImmutableMap;
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
import org.mule.modules.dropbox.model.version2.*;
import org.mule.modules.dropbox.model.version2.oauthparam.DropboxOAuthTokenAccessParamType;
import org.mule.modules.dropbox.model.version2.request.*;

import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mule.modules.dropbox.utils.Utils.*;

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
        refreshTokenRegex = "\"refresh_token\"[ ]*:[ ]*\"([^\\\"]*)\"",
		authorizationParameters = {
				@OAuthAuthorizationParameter(name = "token_access_type", defaultValue = "offline", type = DropboxOAuthTokenAccessParamType.class,
						description = "If this parameter is set to offline, then the access token payload returned by a successful /oauth2/token call " +
								"will contain a short-lived access_token and a long-lived refresh_token that can be used to request a new short-lived access token as long as a user's approval remains valid. " +
								"If set to online then only a short-lived access_token will be returned. If omitted, the response will default to returning a long-lived access_token if they are allowed in the app console. " +
								"If long-lived access tokens are disabled in the app console, this parameter defaults to online", optional = true)
		})
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

//		client.addFilter(new LoggingFilter(System.out));
//		contentClient.addFilter(new LoggingFilter(System.out));

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
		return getItemFromMetadataEntry(deleteV2(path));
	}

	/**
	 * Deletes a file or folder.
	 *
	 * {@sample.xml ../../../doc/Dropbox-connector.xml.sample dropbox:delete-v2}
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
	public MetadataEntry deleteV2(String path) {
		return this.jerseyUtil.post(
				this.apiResource
						.path("files").path("delete")
						.type(MediaType.APPLICATION_JSON_TYPE)
						.entity(new FileRequest(path)),
				MetadataEntry.class,
				200
		);
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
		InputStream response = downloadFileV2(path);
		if (delete)
			this.delete(path);

		return response;
	}

	/**
	 * Downloads a file from Dropbox
	 *
	 * {@sample.xml ../../../doc/Dropbox-connector.xml.sample dropbox:download-file-v2}
	 *
	 * @param path
	 *            path to the file
	 *
	 * @return Stream containing the downloaded file data
	 * @throws Exception
	 *             exception
	 */
    @Processor
    @OAuthProtected
    @OAuthInvalidateAccessTokenOn(exception = DropboxTokenExpiredException.class)
    public InputStream downloadFileV2(String path) {
		return this.jerseyUtilContent.post(
				this.contentResource
						.path("files").path("download")
						.header("Dropbox-API-Arg", toJson(new FileRequest(path))),
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
		return getItemFromMetadataEntry(getMetadataV2(path));
	}

	/**
	 * Lists the metadata of a file or directory
	 *
	 * {@sample.xml ../../../doc/Dropbox-connector.xml.sample dropbox:get-metadata-v2}
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
	public MetadataEntry getMetadataV2(String path) throws Exception {
		return this.jerseyUtil.post(
				this.apiResource
						.path("files").path("get_metadata")
						.type(MediaType.APPLICATION_JSON_TYPE)
						.entity(new MetadataRequest(path)),
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
		return getItemFromListFolderResult(listV2(path));
	}

	/**
	 * Lists the content of the remote directory
	 *
	 * {@sample.xml ../../../doc/Dropbox-connector.xml.sample dropbox:list-v2}
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
    public ListFolderResult listV2(String path) throws Exception {
		ListFolderResult finalResult = new ListFolderResult();

		ListFolderResult listFolderResult = this.jerseyUtil.post(
				this.apiResource
						.path("files").path("list_folder")
						.type(MediaType.APPLICATION_JSON_TYPE)
						.entity(new ListFolderRequest(path)),
				ListFolderResult.class,
				200
		);

		finalResult.setEntries(listFolderResult.getEntries());

		while(listFolderResult.getHasMore()) {
			listFolderResult = this.jerseyUtil.post(
					this.apiResource
							.path("files").path("list_folder").path("continue")
							.type(MediaType.APPLICATION_JSON_TYPE)
							.entity(new ListFolderContinueRequest(listFolderResult.getCursor())),
					ListFolderResult.class,
					200
			);
			finalResult.getEntries().addAll(listFolderResult.getEntries());
		}

		return finalResult;
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
		return getAccountInformationFromFullAccount(getAccountV2());
    }

	/**
	 * Requests the account's information.
	 *
	 * {@sample.xml ../../../doc/Dropbox-connector.xml.sample dropbox:get-account-v2}
	 *
	 * @return AccountInformation. A Dropbox account's information.
	 *
	 * @throws Exception exception
	 */
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
		return fileData.available() > 0 ? getItemFromMetadataEntry(uploadLongStreamV2(fileData, overwrite, path, filename)) : null;
    }

	/**
	 * Upload file to Dropbox. The payload is an InputStream containing bytes of
	 * the data to be uploaded.
	 *
	 * This version of the method supports streams of arbitrary length
	 *
	 * {@sample.xml ../../../doc/Dropbox-connector.xml.sample dropbox:upload-long-stream-v2}
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
							.header("Dropbox-API-Arg", toJson(new UploadStartHeader()))
							.type(MediaType.APPLICATION_OCTET_STREAM);
				} else {
					request = this
							.contentResource
							.path("files")
							.path("upload_session/append_v2")
							.header("Dropbox-API-Arg", toJson(new UploadAppendHeader(new Cursor(uploadId, readBytesAccum))));
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

		Commit commit = null;
		if (previousRev==null || !overwrite) {
			// Add
			commit = new Commit(path);
		} else {
			// Update
			commit = new Commit(path, ImmutableMap.of(".tag", "update", "update", previousRev), false);
		}
		WebResource.Builder request = this
				.contentResource
				.path("files")
				.path("upload_session/finish")
				.header("Dropbox-API-Arg", toJson(new UploadFinishHeader(new Cursor(uploadId, readBytesAccum), commit)))
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

}
