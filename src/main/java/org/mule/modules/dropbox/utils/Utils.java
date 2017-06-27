package org.mule.modules.dropbox.utils;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.mule.modules.dropbox.jersey.json.GsonFactory;
import org.mule.modules.dropbox.model.AccountInformation;
import org.mule.modules.dropbox.model.Item;
import org.mule.modules.dropbox.model.version2.FullAccount;
import org.mule.modules.dropbox.model.version2.ListFolderResult;
import org.mule.modules.dropbox.model.version2.MetadataEntry;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by damianpelaez on 6/25/17.
 */
public class Utils {

    public static Item getItemFromMetadataEntry(MetadataEntry metadataEntry) {
        Item item = new Item();
        item.setSize(String.valueOf(metadataEntry.getSize()));
        item.setRev(metadataEntry.getRev());
        item.setHash(metadataEntry.getContentHash());
        item.setBytes(metadataEntry.getSize());
        item.setModified(metadataEntry.getServerModified());
        item.setClientMtime(metadataEntry.getClientModified());
        item.setPath(metadataEntry.getPathDisplay());
        item.setDir(metadataEntry.getType()== MetadataEntry.Type.folder);
        item.setDeleted(metadataEntry.getType()== MetadataEntry.Type.deleted);
        return item;
    }

    public static AccountInformation getAccountInformationFromFullAccount(FullAccount fullAccount) {
        AccountInformation accountInformation = new AccountInformation();
        accountInformation.setUid(fullAccount.getAccountId());
        accountInformation.setCountry(fullAccount.getCountry());
        accountInformation.setDisplayName(fullAccount.getName().getDisplayName());
        accountInformation.setEmail(fullAccount.getEmail());
        accountInformation.setReferalLink(fullAccount.getReferralLink());
        return accountInformation;
    }

    public static Item getItemFromListFolderResult(ListFolderResult listFolderResult) {
        List<Item> items = new LinkedList<Item>();
        for(MetadataEntry metadataEntry : listFolderResult.getEntries()) {
            items.add(getItemFromMetadataEntry(metadataEntry));
        }
        Item item = new Item();
        item.setContents(items);
        return item;
    }

    public static Map<String, Object> jsonAsMap(String json) {
        return new Gson().fromJson(json, Map.class);
    }

    public static String toJson(Object object) {
        return GsonFactory.get().toJson(object);
    }

    public static String adaptPath(String path) {
        path = StringUtils.trim(path);
        if(StringUtils.isBlank(path) || StringUtils.equals(path, "/"))
            return "";
        if(!StringUtils.startsWith(path, "/")) {
            return "/" + path;
        }
        return path;
    }

}
