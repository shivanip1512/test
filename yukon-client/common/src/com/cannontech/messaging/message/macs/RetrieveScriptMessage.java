package com.cannontech.messaging.message.macs;

import com.cannontech.messaging.message.BaseMessage;

public class RetrieveScriptMessage extends BaseMessage {

    // The name of the script
    private String scriptName;

    public java.lang.String getScriptName() {
        return scriptName;
    }

    public void setScriptName(java.lang.String newName) {
        scriptName = newName;
    }
}
