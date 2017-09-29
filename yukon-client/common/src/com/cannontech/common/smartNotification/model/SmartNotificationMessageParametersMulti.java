package com.cannontech.common.smartNotification.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SmartNotificationMessageParametersMulti implements Serializable {
    private final static long serialVersionUID = 1L;
    private boolean sendAllInOneEmail;
    private final List<SmartNotificationMessageParameters> messageParameters;

    public SmartNotificationMessageParametersMulti(List<SmartNotificationMessageParameters> messageParameters,
            boolean sendAllInOneEmail) {
        this.messageParameters = new ArrayList<>(messageParameters);
        this.sendAllInOneEmail = sendAllInOneEmail;
    }

    public boolean isSendAllInOneEmail() {
        return sendAllInOneEmail;
    }

    public List<SmartNotificationMessageParameters> getMessageParameters() {
        return messageParameters;
    }

    @Override
    public String toString() {
        return "SmartNotificationMessageParametersMulti[sendAllInOneEmail=" + sendAllInOneEmail + " event total="
            + messageParameters.size() + "]";
    }
}
