package org.mule.modules.dropbox.model.version2;


import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MetadataEntry {

    /*
    {
        ".tag": "file",
        "name": "Prime_Numbers.txt",
        "id": "id:a4ayc_80_OEAAAAAAAAAXw",
        "client_modified": "2015-05-12T15:50:38Z",
        "server_modified": "2015-05-12T15:50:38Z",
        "rev": "a1c10ce0dd78",
        "size": 7212,
        "path_lower": "/homework/math/prime_numbers.txt",
        "path_display": "/Homework/math/Prime_Numbers.txt",
        "sharing_info": {
            "read_only": true,
            "parent_shared_folder_id": "84528192421",
            "modified_by": "dbid:AAH4f99T0taONIb-OurWxbNQ6ywGRopQngc"
        },
        "property_groups": [
            {
                "template_id": "ptid:1a5n2i6d3OYEAAAAAAAAAYa",
                "fields": [
                    {
                        "name": "Security Policy",
                        "value": "Confidential"
                    }
                ]
            }
        ],
        "has_explicit_shared_members": false,
        "content_hash": "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
    }
    */

    @SerializedName(".tag")
    private String tag;
    private String name;
    private String id;
    private String clientModified;
    private String serverModified;
    private String rev;
    private Long   size;
    private String pathLower;
    private String pathDisplay;
    private SharingInfo sharingInfo;
    private List<PropertyGroups> propertyGroups;
    private Boolean hasExplicitSharedMembers;
    private String contentHash;

    public enum Type {
        file,
        folder,
        deleted,
        unknown
    };

    public Type getType() {
        try {
            return Type.valueOf(getTag());
        } catch (Exception e) {
            return Type.unknown;
        }
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientModified() {
        return clientModified;
    }

    public void setClientModified(String clientModified) {
        this.clientModified = clientModified;
    }

    public String getServerModified() {
        return serverModified;
    }

    public void setServerModified(String serverModified) {
        this.serverModified = serverModified;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getPathLower() {
        return pathLower;
    }

    public void setPathLower(String pathLower) {
        this.pathLower = pathLower;
    }

    public String getPathDisplay() {
        return pathDisplay;
    }

    public void setPathDisplay(String pathDisplay) {
        this.pathDisplay = pathDisplay;
    }

    public SharingInfo getSharingInfo() {
        return sharingInfo;
    }

    public void setSharingInfo(SharingInfo sharingInfo) {
        this.sharingInfo = sharingInfo;
    }

    public List<PropertyGroups> getPropertyGroups() {
        return propertyGroups;
    }

    public void setPropertyGroups(List<PropertyGroups> propertyGroups) {
        this.propertyGroups = propertyGroups;
    }

    public Boolean getHasExplicitSharedMembers() {
        return hasExplicitSharedMembers;
    }

    public void setHasExplicitSharedMembers(Boolean hasExplicitSharedMembers) {
        this.hasExplicitSharedMembers = hasExplicitSharedMembers;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }
}
