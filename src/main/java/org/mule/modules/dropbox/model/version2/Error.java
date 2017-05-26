package org.mule.modules.dropbox.model.version2;

import java.util.Map;

public class Error {

    private String ErrorSummary;
    private Map<String, Object> error;

    public String getErrorSummary() {
        return ErrorSummary;
    }

    public void setErrorSummary(String errorSummary) {
        ErrorSummary = errorSummary;
    }

    public Map<String, Object> getError() {
        return error;
    }

    public void setError(Map<String, Object> error) {
        this.error = error;
    }
}
