package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.common.pao.PaoType;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class DirectGroupBaseValidator extends AutoInitializedClassValidator<LMDirectGroupBase> {
    private static final PaoType[] poaType = { PaoType.MCT440_2131B, PaoType.MCT440_2132B, PaoType.MCT440_2133B };

    public DirectGroupBaseValidator() {
        super(LMDirectGroupBase.class);
    }

    @Override
    public void populateExpectedValue(LMDirectGroupBase ctrlObj, RandomGenerator generator) {
        ctrlObj.setYukonID(generator.generateInt());
        generator.generateString(); // paocategory
        generator.generateString(); // paoclass
        ctrlObj.setYukonName(generator.generateString());
        ctrlObj.setYukonType(generator.generateChoice(poaType));
        ctrlObj.setYukonDescription(generator.generateString());
        ctrlObj.setDisableFlag(generator.generateBoolean());
        ctrlObj.setGroupOrder(generator.generateInt());
        ctrlObj.setKwCapacity(generator.generateDouble());
        ctrlObj.setChildOrder(generator.generateInt());
        ctrlObj.setAlarmInhibit(generator.generateBoolean());
        ctrlObj.setControlInhibit(generator.generateBoolean());
        ctrlObj.setGroupControlState(generator.generateInt());
        ctrlObj.setCurrentHoursDaily(generator.generateInt());
        ctrlObj.setCurrentHoursMonthly(generator.generateInt());
        ctrlObj.setCurrentHoursSeasonal(generator.generateInt());
        ctrlObj.setCurrentHoursAnnually(generator.generateInt());
        ctrlObj.setLastControlSent(generator.generateCalendar());
        ctrlObj.setControlStartTime(generator.generateDate());
        ctrlObj.setControlCompleteTime(generator.generateDate());
        ctrlObj.setNextControlTime(generator.generateDate());
        ctrlObj.setInternalState(generator.generateUInt());
        ctrlObj.setDailyOps(generator.generateInt());
        ctrlObj.setLastStopTimeSent(generator.generateDate());
    }
}
