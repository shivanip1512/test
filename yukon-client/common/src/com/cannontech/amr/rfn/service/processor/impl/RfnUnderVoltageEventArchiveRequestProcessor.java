package com.cannontech.amr.rfn.service.processor.impl;

import com.cannontech.amr.rfn.message.event.RfnConditionType;

public class RfnUnderVoltageEventArchiveRequestProcessor extends RfnVoltageEventArchiveRequestProcessor {

    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.UNDER_VOLTAGE;
    }
}