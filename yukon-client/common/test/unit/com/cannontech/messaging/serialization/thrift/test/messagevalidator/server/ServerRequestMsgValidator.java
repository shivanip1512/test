package com.cannontech.messaging.serialization.thrift.test.messagevalidator.server;

import com.cannontech.message.porter.message.RequestCancel;
import com.cannontech.message.server.ServerRequestMsg;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class ServerRequestMsgValidator extends AutoInitializedClassValidator<ServerRequestMsg> {
    private static long DEFAULT_SEED = 17;

    public ServerRequestMsgValidator() {
        super(ServerRequestMsg.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(ServerRequestMsg ctrlObj, RandomGenerator generator) {
        ctrlObj.setId(generator.generateInt());

        ctrlObj.setPayload(getDefaultObjectFor(RequestCancel.class, generator));
    }
}
