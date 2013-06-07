package com.cannontech.messaging.message.macs;

import com.cannontech.messaging.message.BaseMessage;

public class ScriptFileMessage extends BaseMessage {

    private String fileName = "";
    private String fileContents = "";

    public String getFileContents() {
        return fileContents;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileContents(String newFileContents) {
        fileContents = newFileContents;
    }

    public void setFileName(String newFileName) {
        fileName = newFileName;
    }
}
