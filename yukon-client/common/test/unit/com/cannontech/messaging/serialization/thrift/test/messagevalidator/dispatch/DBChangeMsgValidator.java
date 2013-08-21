package com.cannontech.messaging.serialization.thrift.test.messagevalidator.dispatch;

import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class DBChangeMsgValidator extends AutoInitializedClassValidator<DBChangeMsg> {

    private static final long DEFAULT_SEED = 3;

    public DBChangeMsgValidator() {
        super(DBChangeMsg.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(DBChangeMsg ctrlObj, RandomGenerator generator) {
        ctrlObj.setId(generator.generateInt());
        ctrlObj.setDatabase(generator.generateInt());
        ctrlObj.setCategory(generator.generateString());
        ctrlObj.setObjectType(generator.generateString());
        ctrlObj.setDbChangeType(generator.generateEnum(DbChangeType.class));
    }
}
