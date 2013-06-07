package com.cannontech.messaging.message.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage;

public interface ProgramMessageCreation {

    ManualControlRequestMessage createScheduledStartMsg(java.util.Date start, java.util.Date stop, int gearNumber,
                                                        java.util.Date notifyTime, String additionalInfo,
                                                        int constraintFlag);

    ManualControlRequestMessage createScheduledStopMsg(java.util.Date start, java.util.Date stop, int gearNumber,
                                                       String additionalInfo);

    ManualControlRequestMessage createStartStopNowMsg(java.util.Date stopTime, int gearNumber, String additionalInfo,
                                                      boolean isStart, int constraintFlag);
}
