package org.mule.modules.dropbox.model.version2.request;

/**
 * Created by damianpelaez on 6/25/17.
 */
public class UploadAppendHeader {

    /*
        {
            "cursor": {
                "session_id": "1234faaf0678bcde",
                "offset": 0
            },
            "close": false
        }
    */

    private Cursor cursor;
    private boolean close = false;

    public UploadAppendHeader(Cursor cursor) {
        this.cursor = cursor;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public boolean isClose() {
        return close;
    }

    public void setClose(boolean close) {
        this.close = close;
    }
}
