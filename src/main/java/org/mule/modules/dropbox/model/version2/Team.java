package org.mule.modules.dropbox.model.version2;

import java.util.Map;

public class Team {

    /*
    {
        "id": "dbtid:AAFdgehTzw7WlXhZJsbGCLePe8RvQGYDr-I",
        "name": "Acme, Inc.",
        "sharing_policies": {
            "shared_folder_member_policy": {
                ".tag": "team"
            },
            "shared_folder_join_policy": {
                ".tag": "from_anyone"
            },
            "shared_link_create_policy": {
                ".tag": "team_only"
            }
        },
        "office_addin_policy": {
            ".tag": "disabled"
        }
    }
     */

    private String id;
    private String name;
    private SharingPolicies sharingPolicies;
    private Map<String, String> officeAddinPolicy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SharingPolicies getSharingPolicies() {
        return sharingPolicies;
    }

    public void setSharingPolicies(SharingPolicies sharingPolicies) {
        this.sharingPolicies = sharingPolicies;
    }

    public Map<String, String> getOfficeAddinPolicy() {
        return officeAddinPolicy;
    }

    public void setOfficeAddinPolicy(Map<String, String> officeAddinPolicy) {
        this.officeAddinPolicy = officeAddinPolicy;
    }
}
