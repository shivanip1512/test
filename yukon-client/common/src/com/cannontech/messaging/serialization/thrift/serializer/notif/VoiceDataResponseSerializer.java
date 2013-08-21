package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.message.notif.VoiceDataResponseMsg;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.NotifVoiceDataResponse;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;

public class VoiceDataResponseSerializer
    extends
    ThriftInheritanceSerializer<VoiceDataResponseMsg, Message, NotifVoiceDataResponse, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public VoiceDataResponseSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<VoiceDataResponseMsg> getTargetMessageClass() {
        return VoiceDataResponseMsg.class;
    }

    @Override
    protected NotifVoiceDataResponse
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        NotifVoiceDataResponse entity = new NotifVoiceDataResponse();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(NotifVoiceDataResponse entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected VoiceDataResponseMsg createMessageInstance() {
        return new VoiceDataResponseMsg();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifVoiceDataResponse entity,
                                                   VoiceDataResponseMsg msg) {
        msg.callToken = entity.get_callToken();
        msg.contactId = entity.get_contactId();
        msg.xmlData = entity.get_xmlData();
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, VoiceDataResponseMsg msg,
                                                   NotifVoiceDataResponse entity) {
        entity.set_callToken(msg.callToken);
        entity.set_contactId(msg.contactId);
        entity.set_xmlData(msg.xmlData);
    }
}
