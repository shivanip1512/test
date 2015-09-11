package com.cannontech.amr.rfn.service.processor.impl;

import com.cannontech.amr.rfn.message.event.RfnConditionType;

public class RfnOverVoltageEventArchiveRequestProcessor extends RfnVoltageEventArchiveRequestProcessor {

    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.OVER_VOLTAGE;
    }
}