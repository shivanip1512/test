package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.message.notif.NotifCallEvent;
import com.cannontech.message.notif.NotifCompletedMsg;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.NotifVoiceCompleted;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;

public class VoiceCompletedSerializer
    extends
    ThriftInheritanceSerializer<NotifCompletedMsg, Message, NotifVoiceCompleted, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public VoiceCompletedSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<NotifCompletedMsg> getTargetMessageClass() {
        return NotifCompletedMsg.class;
    }

    @Override
    protected NotifVoiceCompleted
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        NotifVoiceCompleted entity = new NotifVoiceCompleted();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(NotifVoiceCompleted entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected NotifCompletedMsg createMessageInstance() {
        return new NotifCompletedMsg();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifVoiceCompleted entity,
                                                   NotifCompletedMsg msg) {
        msg.status = NotifCallEvent.values()[entity.get_callStatus()];
        msg.token = entity.get_callToken();
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, NotifCompletedMsg msg,
                                                   NotifVoiceCompleted entity) {
        entity.set_callStatus(msg.status.ordinal());
        entity.set_callToken(msg.token);
    }
}
