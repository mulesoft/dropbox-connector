package org.mule.modules.dropbox.model.version2;

import java.util.Map;

public class SharingPolicies {

    /*
    {
        "shared_folder_member_policy": {
            ".tag": "team"
        },
        "shared_folder_join_policy": {
            ".tag": "from_anyone"
        },
        "shared_link_create_policy": {
            ".tag": "team_only"
        }
    }
     */

    private Map<String, String> sharedFolderMemberPolicy;
    private Map<String, String> sharedFolderJoinPolicy;
    private Map<String, String> sharedLinkCreatePolicy;

    public Map<String, String> getSharedFolderMemberPolicy() {
        return sharedFolderMemberPolicy;
    }

    public void setSharedFolderMemberPolicy(Map<String, String> sharedFolderMemberPolicy) {
        this.sharedFolderMemberPolicy = sharedFolderMemberPolicy;
    }

    public Map<String, String> getSharedFolderJoinPolicy() {
        return sharedFolderJoinPolicy;
    }

    public void setSharedFolderJoinPolicy(Map<String, String> sharedFolderJoinPolicy) {
        this.sharedFolderJoinPolicy = sharedFolderJoinPolicy;
    }

    public Map<String, String> getSharedLinkCreatePolicy() {
        return sharedLinkCreatePolicy;
    }

    public void setSharedLinkCreatePolicy(Map<String, String> sharedLinkCreatePolicy) {
        this.sharedLinkCreatePolicy = sharedLinkCreatePolicy;
    }
}
