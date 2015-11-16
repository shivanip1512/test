package com.cannontech.web.common.resources.data;

/** ResourceType
*   Defines the ResourceBundle encoding and description type. 
*/
public enum ResourceType {
    javascript("text/javascript", ".js", "UTF-8"), css("text/css", ".css", "UTF-8");

    public String getContentType() {
        
        return contentType;
    }

    public String getExtension() {

        return extension;
    }

    public String getEncoding() {

        return encoding;
    }

    private String contentType;
    private String extension;
    private String encoding;

    private ResourceType(String contentType, String extension, String encoding) {
        this.contentType = contentType;
        this.extension = extension;
        this.encoding = encoding;
    }

}
