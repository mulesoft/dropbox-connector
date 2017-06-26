package org.mule.modules.dropbox.model.version2.request;

import org.mule.modules.dropbox.utils.Utils;

/**
 * Created by damianpelaez on 6/25/17.
 */
public class FileRequest {

    /*
        {
            "path": "/Homework/math/Prime_Numbers.txt"
        }
    */

    private String path;

    public FileRequest(String path) {
        this.path = Utils.adaptPath(path);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
