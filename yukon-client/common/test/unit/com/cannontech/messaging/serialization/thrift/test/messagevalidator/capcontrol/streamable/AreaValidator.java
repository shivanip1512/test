package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol.streamable;

import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class AreaValidator extends AutoInitializedClassValidator<Area> {

    public AreaValidator() {
        super(Area.class);
        
    }

    @Override
    public void populateExpectedValue(Area ctrlObj, RandomGenerator generator) {
        ctrlObj.setStations(ConverterHelper.toIntArray(generator.generateList(int.class)));
        ctrlObj.setOvUvDisabledFlag(generator.generateBoolean());
        ctrlObj.setPowerFactorValue(generator.generateDouble());
        ctrlObj.setEstimatedPFValue(generator.generateDouble());
        ctrlObj.setVoltReductionFlag(generator.generateBoolean());
        ctrlObj.setChildVoltReductionFlag(generator.generateBoolean());

        ctrlObj.setCcId(ctrlObj.getCcId());
        ctrlObj.setDisableFlag(ctrlObj.getCcDisableFlag());
        ctrlObj.setPaoCategory(ctrlObj.getCcCategory());
        ctrlObj.setPaoClass(ctrlObj.getCcClass());
        ctrlObj.setPaoDescription(ctrlObj.getCcArea());
        ctrlObj.setCcName(ctrlObj.getCcName());
        ctrlObj.setCcType(ctrlObj.getCcType());
    }
}
