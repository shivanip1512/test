package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.notif.CallStatus;
import com.cannontech.messaging.message.notif.VoiceCompletedMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.generated.NotifVoiceCompleted;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class VoiceCompletedSerializer extends
    ThriftInheritanceSerializer<VoiceCompletedMessage, BaseMessage, NotifVoiceCompleted, Message> {

    public VoiceCompletedSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<VoiceCompletedMessage> getTargetMessageClass() {
        return VoiceCompletedMessage.class;
    }

    @Override
    protected NotifVoiceCompleted createThrifEntityInstance(Message entityParent) {
        NotifVoiceCompleted entity = new NotifVoiceCompleted();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(NotifVoiceCompleted entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected VoiceCompletedMessage createMessageInstance() {
        return new VoiceCompletedMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifVoiceCompleted entity,
                                                   VoiceCompletedMessage msg) {
        msg.setCallStatus(CallStatus.values()[entity.get_callStatus()]);
        msg.setCallToken(entity.get_callToken());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, VoiceCompletedMessage msg,
                                                   NotifVoiceCompleted entity) {
        entity.set_callStatus(msg.getCallStatus().ordinal());
        entity.set_callToken(msg.getCallToken());
    }
}
