package com.cannontech.messaging.serialization.thrift.serializer.dispatch;

import com.cannontech.dispatch.DbChangeType;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.dispatch.DBChangeMessage;
import com.cannontech.messaging.serialization.thrift.*;
import com.cannontech.messaging.serialization.thrift.generated.DBChange;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class DBChangeSerializer extends ThriftInheritanceSerializer<DBChangeMessage, BaseMessage, DBChange, Message> {

    public DBChangeSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<DBChangeMessage> getTargetMessageClass() {
        return DBChangeMessage.class;
    }

    @Override
    protected Message getThriftEntityParent(DBChange entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected DBChange createThrifEntityInstance(Message entityParent) {
        DBChange entity = new DBChange();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected DBChangeMessage createMessageInstance() {
        return new DBChangeMessage();
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, DBChangeMessage msg, DBChange entity) {
        entity.set_category(msg.getCategory());
        entity.set_database(msg.getDatabase());
        entity.set_id(msg.getId());
        entity.set_objecttype(msg.getObjectType());
        entity.set_typeofchange(msg.getDbChangeType().ordinal());
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, DBChange entity, DBChangeMessage msg) {
        msg.setCategory(entity.get_category());
        msg.setDatabase(entity.get_database());
        msg.setDbChangeType(DbChangeType.getForType(entity.get_typeofchange()));
        msg.setId(entity.get_id());
        msg.setObjectType(entity.get_objecttype());
    }

}
