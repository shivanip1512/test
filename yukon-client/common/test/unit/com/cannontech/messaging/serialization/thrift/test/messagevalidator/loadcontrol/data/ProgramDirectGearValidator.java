package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class ProgramDirectGearValidator extends AutoInitializedClassValidator<LMProgramDirectGear> {

    static private GearControlMethod[] controlMethods = { GearControlMethod.TimeRefresh, GearControlMethod.SmartCycle,
                                                         GearControlMethod.SepCycle };

    public ProgramDirectGearValidator() {
        super(LMProgramDirectGear.class);
    }

    @Override
    public void populateExpectedValue(LMProgramDirectGear ctrlObj, RandomGenerator generator) {

        ctrlObj.setGearId(generator.generateInt());
        ctrlObj.setGearName(generator.generateString());
        ctrlObj.setGearNumber(generator.generateInt());
        ctrlObj.setControlMethod(generator.generateChoice(controlMethods));
        ctrlObj.setMethodRate(generator.generateInt());
        ctrlObj.setMethodPeriod(generator.generateInt());
        ctrlObj.setMethodRateCount(generator.generateInt());
        ctrlObj.setCycleRefreshRate(generator.generateInt());
        ctrlObj.setMethodStopType(generator.generateString());
        ctrlObj.setChangeCondition(generator.generateString());
        ctrlObj.setChangeDuration(generator.generateInt());
        ctrlObj.setChangePriority(generator.generateInt());
        ctrlObj.setChangeTriggerNumber(generator.generateInt());
        ctrlObj.setChangeTriggerOffset(generator.generateDouble());
        ctrlObj.setPercentReduction(generator.generateInt());
        ctrlObj.setGroupSelectionMethod(generator.generateString());
        ctrlObj.setMethodOptionType(generator.generateString());
        ctrlObj.setMethodOptionMax(generator.generateInt());
        ctrlObj.setRampInInterval(generator.generateInt());
        ctrlObj.setRampInPercent(generator.generateInt());
        ctrlObj.setRampOutInterval(generator.generateInt());
        ctrlObj.setRampOutPercent(generator.generateInt());
        ctrlObj.setKwReduction(generator.generateDouble());
    }
}
