package org.mule.modules.dropbox.model.version2.request;

import org.mule.modules.dropbox.utils.Utils;

/**
 * Created by damianpelaez on 6/25/17.
 */
public class Commit {

    private String path;
    private boolean autorename = true;
    private boolean mute = false;
    private Object mode = "add";

    public Commit(String path, Object mode, boolean autorename) {
        this.path = Utils.adaptPath(path);
        this.mode = mode;
        this.autorename = autorename;
    }

    public Commit(String path) {
        this.path = Utils.adaptPath(path);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isAutorename() {
        return autorename;
    }

    public void setAutorename(boolean autorename) {
        this.autorename = autorename;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public Object getMode() {
        return mode;
    }

    public void setMode(Object mode) {
        this.mode = mode;
    }
}
