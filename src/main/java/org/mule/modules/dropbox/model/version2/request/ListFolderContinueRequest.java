package org.mule.modules.dropbox.model.version2.request;

/**
 * Created by damianpelaez on 6/25/17.
 */
public class ListFolderContinueRequest {

    /*
        {
            "cursor": "ZtkX9_EHj3x7PMkVuFIhwKYXEpwpLwyxp9vMKomUhllil9q7eWiAu"
        }
    */

    private String cursor;

    public ListFolderContinueRequest(String cursor) {
        this.cursor = cursor;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }
}
