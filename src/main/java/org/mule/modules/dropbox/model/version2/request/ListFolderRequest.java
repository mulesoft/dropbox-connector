package org.mule.modules.dropbox.model.version2.request;

import org.mule.modules.dropbox.utils.Utils;

/**
 * Created by damianpelaez on 6/25/17.
 */
public class ListFolderRequest {

    /*
        {
            "path": "/Homework/math",
            "recursive": false,
            "include_media_info": false,
            "include_deleted": false,
            "include_has_explicit_shared_members": false
        }
    */

    private String path;
    private boolean recursive = false;
    private boolean includeMediaInfo = false;
    private boolean includeDeleted = false;
    private boolean includeHasExplicitSharedMembers = false;

    public ListFolderRequest(String path) {
        this.path = Utils.adaptPath(path);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
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
