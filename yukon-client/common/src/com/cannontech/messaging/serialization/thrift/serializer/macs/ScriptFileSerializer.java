package com.cannontech.messaging.serialization.thrift.serializer.macs;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.macs.ScriptFileMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.MCScript;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class ScriptFileSerializer extends
    ThriftInheritanceSerializer<ScriptFileMessage, BaseMessage, MCScript, Message> {

    public ScriptFileSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ScriptFileMessage> getTargetMessageClass() {
        return ScriptFileMessage.class;
    }

    @Override
    protected MCScript createThrifEntityInstance(Message entityParent) {
        MCScript entity = new MCScript();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(MCScript entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected ScriptFileMessage createMessageInstance() {
        return new ScriptFileMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, MCScript entity, ScriptFileMessage msg) {
        msg.setFileContents(entity.get_contents());
        msg.setFileName(entity.get_name());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ScriptFileMessage msg, MCScript entity) {
        entity.set_contents(msg.getFileContents());
        entity.set_name(msg.getFileName());
    }

}
