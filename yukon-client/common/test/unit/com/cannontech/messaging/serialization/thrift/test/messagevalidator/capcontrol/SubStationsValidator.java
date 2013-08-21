package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol;

import com.cannontech.message.capcontrol.model.SubStations;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class SubStationsValidator  extends AutoInitializedClassValidator<SubStations> {

    private static long DEFAULT_SEED = 207;
    
    
    public SubStationsValidator() {
        super(SubStations.class, DEFAULT_SEED);        
    }

    @Override
    public void populateExpectedValue(SubStations ctrlObj, RandomGenerator generator) {
        ctrlObj.setMsgInfoBitMask(generator.generateUInt());
        
        ctrlObj.setSubStations(getDefaultObjectVectorFor(SubStation.class, generator));
    }
}
