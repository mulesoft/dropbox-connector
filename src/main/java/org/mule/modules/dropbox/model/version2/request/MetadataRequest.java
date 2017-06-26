package org.mule.modules.dropbox.model.version2.request;

import org.mule.modules.dropbox.utils.Utils;

/**
 * Created by damianpelaez on 6/25/17.
 */
public class MetadataRequest {

    /*
        {
            "path": "/Homework/math",
            "include_media_info": false,
            "include_deleted": false,
            "include_has_explicit_shared_members": false
        }
    */

    private String path;
    private boolean includeMediaInfo = false;
    private boolean includeDeleted = false;
    private boolean includeHasExplicitSharedMembers = false;

    public MetadataRequest(String path) {
        this.path = Utils.adaptPath(path);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isIncludeMediaInfo() {
        return includeMediaInfo;
    }

    public void setIncludeMediaInfo(boolean includeMediaInfo) {
        this.includeMediaInfo = includeMediaInfo;
    }

    public boolean isIncludeDeleted() {
        return includeDeleted;
    }

    public void setIncludeDeleted(boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
    }

    public boolean isIncludeHasExplicitSharedMembers() {
        return includeHasExplicitSharedMembers;
    }

    public void setIncludeHasExplicitSharedMembers(boolean includeHasExplicitSharedMembers) {
        this.includeHasExplicitSharedMembers = includeHasExplicitSharedMembers;
    }
}
