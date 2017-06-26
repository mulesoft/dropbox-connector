package org.mule.modules.dropbox.utils;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.mule.modules.dropbox.jersey.json.GsonFactory;
import org.mule.modules.dropbox.model.Item;
import org.mule.modules.dropbox.model.version2.MetadataEntry;

import java.util.Map;

/**
 * Created by damianpelaez on 6/25/17.
 */
public class Utils {

    public static String adaptPath(String path) {
        path = StringUtils.trim(path);
        if(StringUtils.isBlank(path) || StringUtils.equals(path, "/"))
            return "";
        if(!StringUtils.startsWith(path, "/")) {
            return "/" + path;
        }
        return path;
    }

    public static Item getItemFromMetadataEntry(MetadataEntry metadataEntry) {
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

    public static Map<String, Object> jsonAsMap(String json) {
        return new Gson().fromJson(json, Map.class);
    }

    public static String toJson(Object object) {
        return GsonFactory.get().toJson(object);
    }

}
