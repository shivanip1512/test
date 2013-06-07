package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.notif.VoiceDataResponseMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.generated.NotifVoiceDataResponse;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class VoiceDataResponseSerializer extends
    ThriftInheritanceSerializer<VoiceDataResponseMessage, BaseMessage, NotifVoiceDataResponse, Message> {

    public VoiceDataResponseSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<VoiceDataResponseMessage> getTargetMessageClass() {
        return VoiceDataResponseMessage.class;
    }

    @Override
    protected NotifVoiceDataResponse createThrifEntityInstance(Message entityParent) {
        NotifVoiceDataResponse entity = new NotifVoiceDataResponse();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(NotifVoiceDataResponse entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected VoiceDataResponseMessage createMessageInstance() {
        return new VoiceDataResponseMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifVoiceDataResponse entity, VoiceDataResponseMessage msg) {
        msg.setCallToken(entity.get_callToken());
        msg.setContactId(entity.get_contactId());
        msg.setXmlData(entity.get_xmlData());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, VoiceDataResponseMessage msg, NotifVoiceDataResponse entity) {
        entity.set_callToken(msg.getCallToken());
        entity.set_contactId(msg.getContactId());
        entity.set_xmlData(msg.getXmlData());
    }
}
