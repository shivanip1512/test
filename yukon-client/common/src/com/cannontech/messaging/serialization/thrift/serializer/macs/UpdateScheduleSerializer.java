package com.cannontech.messaging.serialization.thrift.serializer.macs;

import com.cannontech.message.macs.message.Schedule;
import com.cannontech.message.macs.message.UpdateSchedule;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.MCSchedule;
import com.cannontech.messaging.serialization.thrift.generated.MCUpdateSchedule;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class UpdateScheduleSerializer
    extends
    ThriftInheritanceSerializer<UpdateSchedule, Message, MCUpdateSchedule, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public UpdateScheduleSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<UpdateSchedule> getTargetMessageClass() {
        return UpdateSchedule.class;
    }

    @Override
    protected MCUpdateSchedule
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        MCUpdateSchedule entity = new MCUpdateSchedule();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(MCUpdateSchedule entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected UpdateSchedule createMessageInstance() {
        return new UpdateSchedule();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, MCUpdateSchedule entity,
                                                   UpdateSchedule msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        msg.setSchedule(helper.convertToMessage(entity.get_schedule(), Schedule.class));
        msg.setScript(entity.get_script());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, UpdateSchedule msg,
                                                   MCUpdateSchedule entity) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        entity.set_schedule(helper.convertToEntity(msg.getSchedule(), MCSchedule.class));
        entity.set_script(msg.getScript());
    }
}
