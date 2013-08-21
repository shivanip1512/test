package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol.streamable;

import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class SubStationValidator extends AutoInitializedClassValidator<SubStation> {

    public SubStationValidator() {
        super(SubStation.class);
    }

    @Override
    public void populateExpectedValue(SubStation ctrlObj, RandomGenerator generator) {
        ctrlObj.setParentID(generator.generateInt());
        ctrlObj.setOvuvDisableFlag(generator.generateBoolean());
        ctrlObj.setSubBusIds(ConverterHelper.toIntArray(generator.generateList(int.class)));
        ctrlObj.setPowerFactorValue(generator.generateDouble());
        ctrlObj.setEstimatedPFValue(generator.generateDouble());
        ctrlObj.setSpecialAreaEnabled(generator.generateBoolean());
        ctrlObj.setSpecialAreaId(generator.generateInt());
        ctrlObj.setVoltReductionFlag(generator.generateBoolean());
        ctrlObj.setRecentlyControlledFlag(generator.generateBoolean());
        ctrlObj.setChildVoltReductionFlag(generator.generateBoolean());
    }
}
