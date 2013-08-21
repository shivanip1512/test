package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol;

import com.cannontech.message.capcontrol.model.BankMove;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class BankMoveValidator extends AutoInitializedClassValidator<BankMove> {
    private static long DEFAULT_SEED = 214;

    public BankMoveValidator() {
        super(BankMove.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(BankMove ctrlObj, RandomGenerator generator) {
        ctrlObj.setPermanentMove(generator.generateBoolean());
        ctrlObj.setOldFeederId(generator.generateInt());
        ctrlObj.setNewFeederId(generator.generateInt());
        ctrlObj.setDisplayOrder(generator.generateFloat());
        ctrlObj.setCloseOrder(generator.generateFloat());
        ctrlObj.setTripOrder(generator.generateFloat());        
    }
}
