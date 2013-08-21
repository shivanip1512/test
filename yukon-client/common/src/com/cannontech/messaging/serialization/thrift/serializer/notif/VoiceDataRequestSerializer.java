package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.message.util.Message;
import com.cannontech.message.notif.VoiceDataRequestMsg;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;

import com.cannontech.messaging.serialization.thrift.generated.NotifVoiceDataRequest;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;

public class VoiceDataRequestSerializer extends
    ThriftInheritanceSerializer<VoiceDataRequestMsg, Message, NotifVoiceDataRequest, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public VoiceDataRequestSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<VoiceDataRequestMsg> getTargetMessageClass() {
        return VoiceDataRequestMsg.class;
    }

    @Override
    protected NotifVoiceDataRequest createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        NotifVoiceDataRequest entity = new NotifVoiceDataRequest();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message getThriftEntityParent(NotifVoiceDataRequest entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected VoiceDataRequestMsg createMessageInstance() {
        return new VoiceDataRequestMsg();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifVoiceDataRequest entity, VoiceDataRequestMsg msg) {
        msg.callToken = entity.get_callToken();
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, VoiceDataRequestMsg msg, NotifVoiceDataRequest entity) {
        entity.set_callToken(msg.callToken);
    }
}
