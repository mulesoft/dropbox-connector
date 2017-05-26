package org.mule.modules.dropbox.model.version2;

import java.util.Map;

public class PropertyGroups {

    /*
    {
            "template_id": "ptid:1a5n2i6d3OYEAAAAAAAAAYa",
            "fields": [
                {
                    "name": "Security Policy",
                    "value": "Confidential"
                }
            ]
        }
     */

    private String templateId;
    private Map<String, String> fields;

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }
}
