package com.cannontech.messaging.serialization.thrift.serializer.dispatch;

import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.DBChange;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;

public class DBChangeSerializer
    extends
    ThriftInheritanceSerializer<DBChangeMsg, Message, DBChange, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public DBChangeSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<DBChangeMsg> getTargetMessageClass() {
        return DBChangeMsg.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message getThriftEntityParent(DBChange entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected DBChange
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        DBChange entity = new DBChange();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected DBChangeMsg createMessageInstance() {
        return new DBChangeMsg();
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, DBChangeMsg msg, DBChange entity) {
        entity.set_category(msg.getCategory());
        entity.set_database(msg.getDatabase());
        entity.set_id(msg.getId());
        entity.set_objecttype(msg.getObjectType());
        entity.set_typeofchange(msg.getDbChangeType().getTypeOfChange());
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, DBChange entity, DBChangeMsg msg) {
        msg.setCategory(entity.get_category());
        msg.setDatabase(entity.get_database());
        msg.setDbChangeType(DbChangeType.getForType(entity.get_typeofchange()));
        msg.setId(entity.get_id());
        msg.setObjectType(entity.get_objecttype());
    }

}
