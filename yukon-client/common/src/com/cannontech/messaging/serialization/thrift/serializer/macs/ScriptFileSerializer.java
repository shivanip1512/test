package com.cannontech.messaging.serialization.thrift.serializer.macs;

import com.cannontech.message.macs.message.ScriptFile;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.MCScript;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;

public class ScriptFileSerializer
    extends
    ThriftInheritanceSerializer<ScriptFile, Message, MCScript, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public ScriptFileSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ScriptFile> getTargetMessageClass() {
        return ScriptFile.class;
    }

    @Override
    protected MCScript
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        MCScript entity = new MCScript();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message getThriftEntityParent(MCScript entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected ScriptFile createMessageInstance() {
        return new ScriptFile();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, MCScript entity, ScriptFile msg) {
        msg.setFileContents(entity.get_contents());
        msg.setFileName(entity.get_name());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ScriptFile msg, MCScript entity) {
        entity.set_contents(msg.getFileContents());
        entity.set_name(msg.getFileName());
    }

}
