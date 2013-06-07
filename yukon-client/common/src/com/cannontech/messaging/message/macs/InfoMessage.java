package com.cannontech.messaging.message.macs;

import com.cannontech.messaging.message.BaseMessage;

public class InfoMessage extends BaseMessage {

    private long id;
    private String info;

    public long getId() {
        return id;
    }

    public String getInfo() {
        return info;
    }

    public void setId(long newId) {
        id = newId;
    }

    public void setInfo(String newInfo) {
        info = newInfo;
    }
}
