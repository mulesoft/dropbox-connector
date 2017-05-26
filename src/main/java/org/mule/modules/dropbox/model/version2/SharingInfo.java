package org.mule.modules.dropbox.model.version2;

/**
 * Created by marcosnunezcortes on 5/18/17.
 */
public class SharingInfo {

    /*
    {
        "read_only": true,
        "parent_shared_folder_id": "84528192421",
        "modified_by": "dbid:AAH4f99T0taONIb-OurWxbNQ6ywGRopQngc"
    }
     */

    private Boolean readOnly;
    private String parentSharedFolderId;
    private String modifiedBy;

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getParentSharedFolderId() {
        return parentSharedFolderId;
    }

    public void setParentSharedFolderId(String parentSharedFolderId) {
        this.parentSharedFolderId = parentSharedFolderId;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
