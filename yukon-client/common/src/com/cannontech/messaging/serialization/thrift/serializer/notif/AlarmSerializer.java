package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.notif.AlarmMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.generated.NotifAlarm;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class AlarmSerializer extends ThriftInheritanceSerializer<AlarmMessage, BaseMessage, NotifAlarm, Message> {

    public AlarmSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<AlarmMessage> getTargetMessageClass() {
        return AlarmMessage.class;
    }

    @Override
    protected Message getThriftEntityParent(NotifAlarm entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected NotifAlarm createThrifEntityInstance(Message entityParent) {
        NotifAlarm entity = new NotifAlarm();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected AlarmMessage createMessageInstance() {
        return new AlarmMessage();
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, AlarmMessage msg, NotifAlarm entity) {
        entity.set_abnormal(msg.isAbnormal());
        entity.set_acknowledged(msg.isAcknowledged());
        entity.set_alarmTimestamp(ConverterHelper.dateToMillisec(msg.getAlarmTimestamp()));
        entity.set_categoryId(msg.getAlarmCategoryId());
        entity.set_condition(msg.getCondition());
        entity.set_notifGroupIds(ConverterHelper.toList(msg.getNotifGroupIds()));
        entity.set_pointId(msg.getPointId());
        entity.set_value(msg.getValue());
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifAlarm entity, AlarmMessage msg) {
        msg.setAbnormal(entity.is_abnormal());
        msg.setAcknowledged(entity.is_acknowledged());
        msg.setAlarmCategoryId(entity.get_categoryId());
        msg.setAlarmTimestamp(ConverterHelper.millisecToDate(entity.get_alarmTimestamp()));
        msg.setCondition(entity.get_condition());
        msg.setNotifGroupIds(ConverterHelper.toIntArray(entity.get_notifGroupIds()));
        msg.setPointId(entity.get_pointId());
        msg.setValue(entity.get_value());
    }
}
