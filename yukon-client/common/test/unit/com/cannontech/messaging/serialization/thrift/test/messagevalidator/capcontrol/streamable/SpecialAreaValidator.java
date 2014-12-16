package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol.streamable;

import com.cannontech.message.capcontrol.streamable.SpecialArea;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class SpecialAreaValidator extends AutoInitializedClassValidator<SpecialArea> {
    public SpecialAreaValidator() {
        super(SpecialArea.class);
    }

    @Override
    public void populateExpectedValue(SpecialArea ctrlObj, RandomGenerator generator) {
        ctrlObj.setCcSubIds(ConverterHelper.toIntArray(generator.generateList(int.class)));
        ctrlObj.setOvUvDisabledFlag(generator.generateBoolean());
        ctrlObj.setPowerFactorValue(generator.generateDouble());
        ctrlObj.setEstimatedPFValue(generator.generateDouble());
        ctrlObj.setVoltReductionFlag(generator.generateBoolean());
        
        ctrlObj.setCcId(ctrlObj.getCcId());
        ctrlObj.setDisableFlag(ctrlObj.getCcDisableFlag());
        ctrlObj.setPaoCategory(ctrlObj.getCcCategory());
        ctrlObj.setPaoClass(ctrlObj.getCcClass());
        ctrlObj.setPaoDescription(ctrlObj.getCcArea());
        ctrlObj.setCcName(ctrlObj.getCcName());
        ctrlObj.setCcType(ctrlObj.getCcType());
    }
}
