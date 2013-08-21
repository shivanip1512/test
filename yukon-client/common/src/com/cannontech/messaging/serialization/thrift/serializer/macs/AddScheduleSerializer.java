package com.cannontech.messaging.serialization.thrift.serializer.macs;

import com.cannontech.message.macs.message.AddSchedule;
import com.cannontech.message.macs.message.Schedule;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.MCAddSchedule;
import com.cannontech.messaging.serialization.thrift.generated.MCSchedule;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class AddScheduleSerializer
    extends
    ThriftInheritanceSerializer<AddSchedule, Message, MCAddSchedule, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public AddScheduleSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<AddSchedule> getTargetMessageClass() {
        return AddSchedule.class;
    }

    @Override
    protected MCAddSchedule
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        MCAddSchedule entity = new MCAddSchedule();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(MCAddSchedule entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected AddSchedule createMessageInstance() {
        return new AddSchedule();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, MCAddSchedule entity,
                                                   AddSchedule msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        msg.setSchedule(helper.convertToMessage(entity.get_schedule(), Schedule.class));
        msg.setScript(entity.get_script());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, AddSchedule msg,
                                                   MCAddSchedule entity) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        entity.set_schedule(helper.convertToEntity(msg.getSchedule(), MCSchedule.class));
        entity.set_script(msg.getScript());
    }
}
