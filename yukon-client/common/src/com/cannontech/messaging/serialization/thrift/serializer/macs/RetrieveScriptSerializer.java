package com.cannontech.messaging.serialization.thrift.serializer.macs;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.macs.RetrieveScriptMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.MCRetrieveScript;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class RetrieveScriptSerializer extends
    ThriftInheritanceSerializer<RetrieveScriptMessage, BaseMessage, MCRetrieveScript, Message> {

    public RetrieveScriptSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<RetrieveScriptMessage> getTargetMessageClass() {
        return RetrieveScriptMessage.class;
    }

    @Override
    protected MCRetrieveScript createThrifEntityInstance(Message entityParent) {
        MCRetrieveScript entity = new MCRetrieveScript();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(MCRetrieveScript entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected RetrieveScriptMessage createMessageInstance() {
        return new RetrieveScriptMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, MCRetrieveScript entity, RetrieveScriptMessage msg) {
        msg.setScriptName(entity.get_name());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, RetrieveScriptMessage msg, MCRetrieveScript entity) {
        entity.set_name(msg.getScriptName());
    }

}
