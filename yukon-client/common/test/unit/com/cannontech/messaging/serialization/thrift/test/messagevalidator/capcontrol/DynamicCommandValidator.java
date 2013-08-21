package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol;

import com.cannontech.message.capcontrol.model.DynamicCommand;
import com.cannontech.message.capcontrol.model.DynamicCommand.DynamicCommandType;
import com.cannontech.message.capcontrol.model.DynamicCommand.Parameter;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class DynamicCommandValidator extends AutoInitializedClassValidator<DynamicCommand> {

    private static long DEFAULT_SEED = 211;
    
    
    public DynamicCommandValidator() {
        super(DynamicCommand.class, DEFAULT_SEED);        
    }

    @Override
    public void populateExpectedValue(DynamicCommand ctrlObj, RandomGenerator generator) {
        ctrlObj.setCommandType(generator.generateEnum(DynamicCommandType.class));

        ctrlObj.addParameter(Parameter.DEVICE_ID, generator.generateInt());
        ctrlObj.addParameter(Parameter.POINT_ID, generator.generateInt());
        ctrlObj.addParameter(Parameter.POINT_RESPONSE_DELTA, generator.generateInt());
        ctrlObj.addParameter(Parameter.POINT_RESPONSE_STATIC_DELTA, generator.generateInt());
        
        ctrlObj.addParameter(Parameter.DEVICE_ID, generator.generateDouble());
        ctrlObj.addParameter(Parameter.POINT_ID, generator.generateDouble());
        ctrlObj.addParameter(Parameter.POINT_RESPONSE_DELTA, generator.generateDouble());
        ctrlObj.addParameter(Parameter.POINT_RESPONSE_STATIC_DELTA, generator.generateDouble());
    }
    
    @Override
    protected DynamicCommand createObject() {    
        return new DynamicCommand(DynamicCommandType.UNDEFINED);
    }
}
