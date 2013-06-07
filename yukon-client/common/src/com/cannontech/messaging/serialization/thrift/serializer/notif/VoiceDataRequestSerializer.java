package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.notif.VoiceDataRequestMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.generated.NotifVoiceDataRequest;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class VoiceDataRequestSerializer extends
    ThriftInheritanceSerializer<VoiceDataRequestMessage, BaseMessage, NotifVoiceDataRequest, Message> {

    public VoiceDataRequestSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<VoiceDataRequestMessage> getTargetMessageClass() {
        return VoiceDataRequestMessage.class;
    }

    @Override
    protected NotifVoiceDataRequest createThrifEntityInstance(Message entityParent) {
        NotifVoiceDataRequest entity = new NotifVoiceDataRequest();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(NotifVoiceDataRequest entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected VoiceDataRequestMessage createMessageInstance() {
        return new VoiceDataRequestMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifVoiceDataRequest entity, VoiceDataRequestMessage msg) {
        msg.setCallToken(entity.get_callToken());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, VoiceDataRequestMessage msg, NotifVoiceDataRequest entity) {
        entity.set_callToken(msg.getCallToken());
    }
}
