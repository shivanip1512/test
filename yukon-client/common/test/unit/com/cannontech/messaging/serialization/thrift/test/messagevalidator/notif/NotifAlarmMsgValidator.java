package com.cannontech.messaging.serialization.thrift.test.messagevalidator.notif;

import com.cannontech.message.notif.NotifAlarmMsg;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class NotifAlarmMsgValidator extends AutoInitializedClassValidator<NotifAlarmMsg> {
    private static long DEFAULT_SEED = 6;

    public NotifAlarmMsgValidator() {
        super(NotifAlarmMsg.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(NotifAlarmMsg ctrlObj, RandomGenerator generator) {

        ctrlObj.notifGroupIds = ConverterHelper.toIntArray(generator.generateList(Integer.class));
        ctrlObj.alarmCategoryId = generator.generateInt();
        ctrlObj.pointId= generator.generateInt();
        ctrlObj.condition= generator.generateInt();
        ctrlObj.value= generator.generateDouble();
        ctrlObj.alarmTimestamp= generator.generateDate();
        ctrlObj.acknowledged= generator.generateBoolean();
        ctrlObj.abnormal=generator.generateBoolean();
    }
}
