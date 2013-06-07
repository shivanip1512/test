package com.cannontech.messaging.message.notif;

import com.cannontech.messaging.message.BaseMessage;

public class VoiceDataResponseMessage extends BaseMessage {

    private String callToken = "";
    private String xmlData = "";
    private int contactId = 0;

    public String getCallToken() {
        return callToken;
    }

    public void setCallToken(String callToken) {
        this.callToken = callToken;
    }

    public String getXmlData() {
        return xmlData;
    }

    public void setXmlData(String xmlData) {
        this.xmlData = xmlData;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

}
