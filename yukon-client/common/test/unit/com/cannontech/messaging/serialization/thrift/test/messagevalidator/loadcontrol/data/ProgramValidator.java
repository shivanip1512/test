package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.common.pao.PaoType;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramControlWindow;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class ProgramValidator extends AutoInitializedClassValidator<LMProgramBase> {
    private static final PaoType[] poaType = { PaoType.MCT440_2131B, PaoType.MCT440_2132B, PaoType.MCT440_2133B };

    public ProgramValidator() {
        super(LMProgramBase.class);
    }

    @Override
    public void populateExpectedValue(LMProgramBase ctrlObj, RandomGenerator generator) {
        ctrlObj.setYukonID(generator.generateInt());
        generator.generateString();     // for _paocategory in C++ side. It does not exist anymore in the Java message (replaced by PaoType) 
        generator.generateString();     // for _paoclass  in C++ side. It does not exist anymore in the Java message (replaced by PaoType)
        ctrlObj.setYukonName(generator.generateString());
        ctrlObj.setYukonType(generator.generateChoice(poaType));
        ctrlObj.setYukonDescription(generator.generateString());
        ctrlObj.setDisableFlag(generator.generateBoolean());
        ctrlObj.setStartPriority(generator.generateInt());
        ctrlObj.setStopPriority(generator.generateInt());
        ctrlObj.setControlType(generator.generateString());
        ctrlObj.setAvailableWeekDays(generator.generateString());
        ctrlObj.setMaxHoursDaily(generator.generateInt());
        ctrlObj.setMaxHoursMonthly(generator.generateInt());
        ctrlObj.setMaxHoursSeasonal(generator.generateInt());
        ctrlObj.setMaxHoursAnnually(generator.generateInt());
        ctrlObj.setMinActivateTime(generator.generateInt());
        ctrlObj.setMinResponseTime(generator.generateInt());
        ctrlObj.setProgramStatusPointID(generator.generateInt());
        ctrlObj.setProgramStatus(generator.generateInt(0,9));
        ctrlObj.setReductionAnalogPointId(generator.generateInt());
        ctrlObj.setReductionTotal(generator.generateDouble());
        ctrlObj.setStartedControlling(generator.generateCalendar());
        ctrlObj.setLastControlSent(generator.generateCalendar());
        ctrlObj.setManualControlReceivedFlag(generator.generateBoolean());

        ctrlObj.setControlWindowVector(getDefaultObjectVectorFor(LMProgramControlWindow.class, generator));

        ignoreField("stoppedControlling");
    }
}
