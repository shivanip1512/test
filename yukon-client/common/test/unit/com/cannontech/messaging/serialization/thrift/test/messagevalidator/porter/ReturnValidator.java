package com.cannontech.messaging.serialization.thrift.test.messagevalidator.porter;

import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;
import com.cannontech.messaging.serialization.util.ConverterHelper;
import com.google.common.collect.Lists;

public class ReturnValidator extends AutoInitializedClassValidator<Return> {
    private static long DEFAULT_SEED = 11;

    public ReturnValidator() {
        super(Return.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(Return ctrlObj, RandomGenerator generator) {

        // Because on the CPP side a Return message is derived from Multi, not from Message
        // We have to generate on to stay in sync (for the random generator) and we need to get the message Vector back
        Multi<Message> multiMsg = new Multi<>();
        getAutoValidatorFor(Multi.class).populateExpectedValue(multiMsg, generator);
        ctrlObj.setMessages(Lists.newArrayList(multiMsg.getVector()));

        ctrlObj.setDeviceID(generator.generateInt());
        ctrlObj.setCommandString(generator.generateString());
        ctrlObj.setResultString(generator.generateString());
        ctrlObj.setStatus(generator.generateInt());
        ctrlObj.setRouteOffset(generator.generateInt());
        ctrlObj.setMacroOffset(
            generator.generateBoolean()
                ? generator.generateUInt() + 1
                : 0);
        ctrlObj.setAttemptNum(generator.generateInt());
        ctrlObj.setExpectMore(ConverterHelper.boolToInt(generator.generateBoolean()));
        ctrlObj.setGroupMessageID(generator.generateInt());
        ctrlObj.setUserMessageID(generator.generateInt());
    }
}
