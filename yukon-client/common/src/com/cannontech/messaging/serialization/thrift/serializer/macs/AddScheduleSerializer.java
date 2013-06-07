package com.cannontech.messaging.serialization.thrift.serializer.macs;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.macs.AddScheduleMessage;
import com.cannontech.messaging.message.macs.ScheduleMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.MCAddSchedule;
import com.cannontech.messaging.serialization.thrift.generated.MCSchedule;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class AddScheduleSerializer extends
    ThriftInheritanceSerializer<AddScheduleMessage, BaseMessage, MCAddSchedule, Message> {

    public AddScheduleSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<AddScheduleMessage> getTargetMessageClass() {
        return AddScheduleMessage.class;
    }

    @Override
    protected MCAddSchedule createThrifEntityInstance(Message entityParent) {
        MCAddSchedule entity = new MCAddSchedule();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(MCAddSchedule entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected AddScheduleMessage createMessageInstance() {
        return new AddScheduleMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, MCAddSchedule entity,
                                                   AddScheduleMessage msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        msg.setSchedule(helper.convertToMessage(entity.get_schedule(), ScheduleMessage.class));
        msg.setScript(entity.get_script());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, AddScheduleMessage msg,
                                                   MCAddSchedule entity) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        entity.set_schedule(helper.convertToEntity(msg.getSchedule(), MCSchedule.class));
        entity.set_script(msg.getScript());
    }
}
