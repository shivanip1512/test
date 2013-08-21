package com.cannontech.messaging.serialization.thrift.serializer.macs;

import com.cannontech.message.util.Message;
import com.cannontech.message.macs.message.RetrieveScript;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.MCRetrieveScript;

import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;

public class RetrieveScriptSerializer extends
    ThriftInheritanceSerializer<RetrieveScript, Message, MCRetrieveScript, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public RetrieveScriptSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<RetrieveScript> getTargetMessageClass() {
        return RetrieveScript.class;
    }

    @Override
    protected MCRetrieveScript createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        MCRetrieveScript entity = new MCRetrieveScript();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message getThriftEntityParent(MCRetrieveScript entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected RetrieveScript createMessageInstance() {
        return new RetrieveScript();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, MCRetrieveScript entity, RetrieveScript msg) {
        msg.setScriptName(entity.get_name());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, RetrieveScript msg, MCRetrieveScript entity) {
        entity.set_name(msg.getScriptName());
    }

}
