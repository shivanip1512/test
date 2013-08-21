package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.message.notif.NotifAlarmMsg;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.NotifAlarm;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class AlarmSerializer
    extends
    ThriftInheritanceSerializer<NotifAlarmMsg, Message, NotifAlarm, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public AlarmSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<NotifAlarmMsg> getTargetMessageClass() {
        return NotifAlarmMsg.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message getThriftEntityParent(NotifAlarm entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected NotifAlarm
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        NotifAlarm entity = new NotifAlarm();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected NotifAlarmMsg createMessageInstance() {
        return new NotifAlarmMsg();
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, NotifAlarmMsg msg, NotifAlarm entity) {
        entity.set_abnormal(msg.abnormal);
        entity.set_acknowledged(msg.acknowledged);
        entity.set_alarmTimestamp(ConverterHelper.dateToMillisec(msg.alarmTimestamp));
        entity.set_categoryId(msg.alarmCategoryId);
        entity.set_condition(msg.condition);
        entity.set_notifGroupIds(ConverterHelper.toList(msg.notifGroupIds));
        entity.set_pointId(msg.pointId);
        entity.set_value(msg.value);
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifAlarm entity, NotifAlarmMsg msg) {
        msg.abnormal = entity.is_abnormal();
        msg.acknowledged = entity.is_acknowledged();
        msg.alarmCategoryId = entity.get_categoryId();
        msg.alarmTimestamp = ConverterHelper.millisecToDate(entity.get_alarmTimestamp());
        msg.condition = entity.get_condition();
        msg.notifGroupIds = ConverterHelper.toIntArray(entity.get_notifGroupIds());
        msg.pointId = entity.get_pointId();
        msg.value = entity.get_value();
    }
}
