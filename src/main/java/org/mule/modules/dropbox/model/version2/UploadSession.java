package org.mule.modules.dropbox.model.version2;

/**
 * Created by damianpelaez on 6/19/17.
 */
public class UploadSession {

    /*
    {
        "session_id": "1234faaf0678bcde"
    }
    */

    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
