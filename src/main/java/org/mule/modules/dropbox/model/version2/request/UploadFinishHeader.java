package org.mule.modules.dropbox.model.version2.request;

/**
 * Created by damianpelaez on 6/25/17.
 */
public class UploadFinishHeader {

    /*
        {
            "cursor": {
                "session_id": "1234faaf0678bcde",
                "offset": 0
            },
            "commit": {
                "path": "/Homework/math/Matrices.txt",
                "mode": {
                    ".tag": "update",
                    "update": "a1c10ce0dd78"
                },
                "autorename": false,
                "mute": false
            }
        }
    */

    private Cursor cursor;
    private Commit commit;

    public UploadFinishHeader(Cursor cursor, Commit commit) {
        this.cursor = cursor;
        this.commit = commit;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }
}
