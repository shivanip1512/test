package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.dr.controlarea.model.TriggerType;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class ControlAreaTriggerItemValidator extends AutoInitializedClassValidator<LMControlAreaTrigger> {

    public ControlAreaTriggerItemValidator() {
        super(LMControlAreaTrigger.class);
    }

    @Override
    public void populateExpectedValue(LMControlAreaTrigger ctrlObj, RandomGenerator generator) {
        ctrlObj.setYukonID(generator.generateInt());
        ctrlObj.setTriggerNumber(generator.generateInt());
        ctrlObj.setTriggerType(generator.generateEnum(TriggerType.class));
        ctrlObj.setPointId(generator.generateInt());
        ctrlObj.setPointValue(generator.generateDouble());
        ctrlObj.setLastPointValueTimeStamp(generator.generateDate());
        ctrlObj.setNormalState(generator.generateInt());
        ctrlObj.setThreshold(generator.generateDouble());
        ctrlObj.setProjectionType(generator.generateString());
        ctrlObj.setProjectionPoints(generator.generateInt());
        ctrlObj.setProjectAheadDuration(generator.generateInt());
        ctrlObj.setThresholdKickPercent(generator.generateInt());   
        ctrlObj.setMinRestoreOffset(generator.generateDouble());
        ctrlObj.setPeakPointId(generator.generateInt());
        ctrlObj.setPeakPointValue(generator.generateDouble());
        ctrlObj.setLastPeakPointValueTimeStamp(generator.generateDate());
        ctrlObj.setProjectedPointValue(generator.generateDouble());
    }
}
