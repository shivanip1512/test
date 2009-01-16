package com.cannontech.sensus;

import com.sms.messages.rx.DataMessage;


public interface SensusMessageObjectHandler {
    public void processMessageObject(DataMessage message);
}
