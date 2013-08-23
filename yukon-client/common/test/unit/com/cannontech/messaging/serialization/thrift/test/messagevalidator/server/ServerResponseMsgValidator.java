package com.cannontech.messaging.serialization.thrift.test.messagevalidator.server;

import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.Command;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class ServerResponseMsgValidator extends AutoInitializedClassValidator<ServerResponseMsg> {
    private static long DEFAULT_SEED = 18;

    public ServerResponseMsgValidator() {
        super(ServerResponseMsg.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(ServerResponseMsg ctrlObj, RandomGenerator generator) {
        ctrlObj.setId(generator.generateInt());
        ctrlObj.setStatus(generator.generateInt());
        ctrlObj.setMessage(generator.generateString());

        ctrlObj.setPayload(getDefaultObjectFor(Command.class, generator));
    }
}
