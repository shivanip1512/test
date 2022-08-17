package com.cannontech.services.rf.alarms.service;

import javax.jms.JMSException;

import com.cannontech.common.rfn.message.alarm.AlarmArchiveRequest;
import com.cannontech.common.rfn.message.alarm.AlarmArchiveResponse;

public interface NmAlarmService {
    
    AlarmArchiveResponse handle(AlarmArchiveRequest request) throws JMSException;

}
