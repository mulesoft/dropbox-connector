package org.mule.modules.dropbox.model.version2.request;

/**
 * Created by damianpelaez on 6/25/17.
 */
public class UploadStartHeader {

    /*
        {
            "close": false
        }
    */

    private boolean close = false;

    public boolean isClose() {
        return close;
    }

    public void setClose(boolean close) {
        this.close = close;
    }
}
