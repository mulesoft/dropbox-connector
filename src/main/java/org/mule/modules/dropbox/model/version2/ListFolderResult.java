package org.mule.modules.dropbox.model.version2;

import java.util.List;

public class ListFolderResult {

    /*
        {
            "entries": [
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
                },
                {
                    ".tag": "folder",
                    "name": "math",
                    "id": "id:a4ayc_80_OEAAAAAAAAAXz",
                    "path_lower": "/homework/math",
                    "path_display": "/Homework/math",
                    "sharing_info": {
                        "read_only": false,
                        "parent_shared_folder_id": "84528192421",
                        "traverse_only": false,
                        "no_access": false
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
                    ]
                }
            ],
            "cursor": "ZtkX9_EHj3x7PMkVuFIhwKYXEpwpLwyxp9vMKomUhllil9q7eWiAu",
            "has_more": false
        }
     */

    private String cursor;
    private boolean hasMore;
    private List<MetadataEntry> entries;

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public List<MetadataEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<MetadataEntry> entries) {
        this.entries = entries;
    }
}
