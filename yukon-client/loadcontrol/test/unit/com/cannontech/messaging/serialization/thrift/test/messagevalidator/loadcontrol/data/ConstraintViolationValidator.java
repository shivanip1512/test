package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;


import java.util.Date;

import com.cannontech.loadcontrol.messages.ConstraintError;
import com.cannontech.loadcontrol.messages.ConstraintViolation;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class ConstraintViolationValidator extends AutoInitializedClassValidator<ConstraintViolation> {

    public ConstraintViolationValidator() {
        super(ConstraintViolation.class);
    }

    @Override
    public void populateExpectedValue(ConstraintViolation ctrlObj, RandomGenerator generator) {
        ctrlObj.setErrorCode(generator.generateEnum(ConstraintError.class));
        ctrlObj.setDoubleParams(generator.generateList(Double.class));
        ctrlObj.setIntegerParams(generator.generateList(Integer.class));
        ctrlObj.setStringParams(generator.generateList(String.class)); 
        ctrlObj.setDateTimeParams(generator.generateList(Date.class));
    }
}
