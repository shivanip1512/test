package com.cannontech.messaging.serialization.thrift.serializer.macs;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.macs.ScheduleMessage;
import com.cannontech.messaging.message.macs.UpdateScheduleMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.MCSchedule;
import com.cannontech.messaging.serialization.thrift.generated.MCUpdateSchedule;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class UpdateScheduleSerializer extends
    ThriftInheritanceSerializer<UpdateScheduleMessage, BaseMessage, MCUpdateSchedule, Message> {

    public UpdateScheduleSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<UpdateScheduleMessage> getTargetMessageClass() {
        return UpdateScheduleMessage.class;
    }

    @Override
    protected MCUpdateSchedule createThrifEntityInstance(Message entityParent) {
        MCUpdateSchedule entity = new MCUpdateSchedule();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(MCUpdateSchedule entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected UpdateScheduleMessage createMessageInstance() {
        return new UpdateScheduleMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, MCUpdateSchedule entity, UpdateScheduleMessage msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();
        
        msg.setSchedule(helper.convertToMessage(entity.get_schedule(), ScheduleMessage.class));
        msg.setScript(entity.get_script());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, UpdateScheduleMessage msg, MCUpdateSchedule entity) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();
        
        entity.set_schedule(helper.convertToEntity(msg.getSchedule(), MCSchedule.class));
        entity.set_script(msg.getScript());
    }
}
