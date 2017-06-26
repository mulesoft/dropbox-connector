package org.mule.modules.dropbox.model.version2.request;

/**
 * Created by damianpelaez on 6/25/17.
 */
public class Cursor {

    private String sessionId;
    private Long offset = 0L;

    public Cursor(String sessionId, Long offset) {
        this.sessionId = sessionId;
        this.offset = offset;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
