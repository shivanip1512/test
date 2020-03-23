package com.cannontech.amr.rfn.service.processor.impl;

import com.cannontech.amr.rfn.message.event.RfnConditionType;

public class RfnRemoteMeterConfigurationFailureEventArchiveRequestProcessor extends RfnRemoteMeterConfigurationEventProcessorHelper {
    
    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.REMOTE_METER_CONFIGURATION_FAILURE;
    }
}