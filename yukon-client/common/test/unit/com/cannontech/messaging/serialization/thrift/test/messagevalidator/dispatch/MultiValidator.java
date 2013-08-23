package com.cannontech.messaging.serialization.thrift.test.messagevalidator.dispatch;

import java.util.Vector;

import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.server.ServerRequestMsg;
import com.cannontech.message.util.Command;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

@SuppressWarnings("rawtypes")
public class MultiValidator extends AutoInitializedClassValidator<Multi> {
    private static final long DEFAULT_SEED = 5;

    public MultiValidator() {
        super(Multi.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(Multi ctrlObj, RandomGenerator generator) {
        Vector<Message> vect = new Vector<Message>();

        vect.add(getDefaultObjectFor(Command.class, generator));
        vect.add(getDefaultObjectFor(ServerRequestMsg.class, generator));

        ctrlObj.setVector(vect);
    }

}
