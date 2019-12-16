package com.cannontech.stars.dr.jms.notification.message;

import java.io.Serializable;
import java.util.List;

import com.cannontech.common.pao.definition.model.PaoPointIdentifier;

public class DRNotificationDataMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private PaoPointIdentifier paoPointIdentifier;
    private List<DRDataMessage> dataMessages;
    private DRNotificationMessageType messageType;
    

    public PaoPointIdentifier getPaoPointIdentifier() {
        return paoPointIdentifier;
    }

    public void setPaoPointIdentifier(PaoPointIdentifier paoPointIdentifier) {
        this.paoPointIdentifier = paoPointIdentifier;
    }

    public List<DRDataMessage> getDataMessages() {
        return dataMessages;
    }

    public void setDataMessages(List<DRDataMessage> dataMessages) {
        this.dataMessages = dataMessages;
    }

    public DRNotificationMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(DRNotificationMessageType messageType) {
        this.messageType = messageType;
    }

}
