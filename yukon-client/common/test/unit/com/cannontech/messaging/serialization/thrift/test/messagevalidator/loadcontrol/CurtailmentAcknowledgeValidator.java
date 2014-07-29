package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol;

import com.cannontech.loadcontrol.messages.LMCurtailmentAcknowledgeMsg;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class CurtailmentAcknowledgeValidator extends AutoInitializedClassValidator<LMCurtailmentAcknowledgeMsg> {

    private static long DEFAULT_SEED = 307;

    public CurtailmentAcknowledgeValidator() {
        super(LMCurtailmentAcknowledgeMsg.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(LMCurtailmentAcknowledgeMsg ctrlObj, RandomGenerator generator) {
        ctrlObj.setYukonID(generator.generateInt());
        ctrlObj.setCurtailReferenceID(generator.generateInt());
        ctrlObj.setAcknowledgeStatus(generator.generateString());
        ctrlObj.setIpAddressOfAckUser(generator.generateString());
        ctrlObj.setUserIdName(generator.generateString());
        ctrlObj.setNameOfAckPerson(generator.generateString());
        ctrlObj.setCurtailmentNotes(generator.generateString());               
    }
}
