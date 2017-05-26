package com.mulesoft.module.dropbox;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.impl.provider.entity.FormMultivaluedMapProvider;
import com.sun.jersey.core.impl.provider.entity.FormProvider;
import com.sun.jersey.core.impl.provider.entity.InputStreamProvider;
import com.sun.jersey.core.impl.provider.entity.MimeMultipartProvider;
import com.sun.jersey.multipart.impl.MultiPartWriter;
import org.junit.Test;
import org.mule.api.MuleException;
import org.mule.commons.jersey.provider.GsonProvider;
import org.mule.modules.dropbox.DropboxConnector;
import org.mule.modules.dropbox.jersey.json.GsonFactory;
import org.mule.modules.dropbox.model.Item;
import org.mule.modules.dropbox.model.version2.ListFolderResult;

import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

public class DropbocConnectorApiV2Test {

    // App: Mule-DBX-Connector-Test-MNC

    private static final String APP_NAME     = "Mule-DBX-Connector-Test-MNC";
    private static final String APP_KEY      = "cbmsa3s2ocuty8n";
    private static final String APP_SECRET   = "oow5jelrnmxj39j";
    private static final String ACCESS_TOKEN = "oP_Zd14jzqUAAAAAAAAAwGx_ORNJpHK3FMbIkantrddOlGheWcMQ2SqksEPbAG74";

    @Test
    public void listFiles() throws Exception {
        /*
        curl -X POST https://api.dropboxapi.com/2/files/list_folder \
        --header "Authorization: Bearer oP_Zd14jzqUAAAAAAAAAwGx_ORNJpHK3FMbIkantrddOlGheWcMQ2SqksEPbAG74" \
        --header "Content-Type: application/json" \
        --data "{\"path\": \"/dropbox/Nardoz\", \"recursive\": true, \"include_media_info\": false,  \"include_deleted\": false,  \"include_has_explicit_shared_members\": false}"

         */

        DropboxConnector dropboxConnector = new DropboxConnector();
        dropboxConnector.setAppKey(APP_KEY);
        dropboxConnector.setAppSecret(APP_SECRET);
        dropboxConnector.setAccessToken(ACCESS_TOKEN);
        dropboxConnector.setServer(DropboxConnector.DEFAULT_SERVER);
        dropboxConnector.setContentServer(DropboxConnector.DEFAULT_CONTENT_SERVER);
        dropboxConnector.init();
        ListFolderResult listFolderResult = dropboxConnector.listV2("Nardoz");
        System.out.println(listFolderResult.getEntries().get(0).getTag());

        Item item = dropboxConnector.list("Nardoz");
        System.out.println(item.getContents().size());
        System.out.println(item.getContents().get(0).getRev());
    }

    @Test
    public void google() throws Exception {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        clientConfig.getClasses().add(MultiPartWriter.class);
        clientConfig.getClasses().add(MimeMultipartProvider.class);
        clientConfig.getClasses().add(InputStreamProvider.class);
        clientConfig.getClasses().add(FormProvider.class);
        clientConfig.getClasses().add(FormMultivaluedMapProvider.class);
        clientConfig.getSingletons().add(new GsonProvider(GsonFactory.get()));

//        Client client = Client.create(clientConfig);
//        WebResource webResource = client.resource("http://www.google.com");
//        ClientResponse response = webResource.getRequestBuilder().method("GET", ClientResponse.class);

//        Client client = Client.create();
//        String jsonData = "{\"path\": \"/Nardoz\", \"recursive\": true, \"include_media_info\": false,  \"include_deleted\": false,  \"include_has_explicit_shared_members\": false}";
//        WebResource.Builder builder = client
//                .resource("https://api.dropboxapi.com/2/files/list_folder")
//                .header("Authorization", "Bearer oP_Zd14jzqUAAAAAAAAAwGx_ORNJpHK3FMbIkantrddOlGheWcMQ2SqksEPbAG74")
//                .type(MediaType.APPLICATION_JSON_TYPE);
//        ClientResponse response = builder.method("POST", ClientResponse.class, jsonData);

        Client client = Client.create(clientConfig);
        String jsonData = "{\"path\": \"/Nardoz\", \"recursive\": true, \"include_media_info\": false,  \"include_deleted\": false,  \"include_has_explicit_shared_members\": false}";
        Map<String, Object> jsonDataMap = new Gson().fromJson(jsonData, Map.class);
        System.out.println(jsonDataMap);
        WebResource.Builder builder = client
                .resource("https://api.dropboxapi.com/2/files/list_folder")
                .header("Authorization", "Bearer oP_Zd14jzqUAAAAAAAAAwGx_ORNJpHK3FMbIkantrddOlGheWcMQ2SqksEPbAG74")
                .type(MediaType.APPLICATION_JSON_TYPE);
        ClientResponse response = builder.method("POST", ClientResponse.class, jsonDataMap);

        System.out.println(response.getStatus());
        System.out.println(response.getEntity(String.class));
    }

}
