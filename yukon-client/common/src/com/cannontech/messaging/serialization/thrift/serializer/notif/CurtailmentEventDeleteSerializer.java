package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.message.notif.CurtailmentEventDeleteMsg;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.NotifCurtailmentEventDelete;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;

public class CurtailmentEventDeleteSerializer
    extends
    ThriftInheritanceSerializer<CurtailmentEventDeleteMsg, Message, NotifCurtailmentEventDelete, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public CurtailmentEventDeleteSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<CurtailmentEventDeleteMsg> getTargetMessageClass() {
        return CurtailmentEventDeleteMsg.class;
    }

    @Override
    protected NotifCurtailmentEventDelete
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        NotifCurtailmentEventDelete entity = new NotifCurtailmentEventDelete();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(NotifCurtailmentEventDelete entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected CurtailmentEventDeleteMsg createMessageInstance() {
        return new CurtailmentEventDeleteMsg();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifCurtailmentEventDelete entity,
                                                   CurtailmentEventDeleteMsg msg) {
        msg.curtailmentEventId = entity.get_curtailmentEventId();
        msg.deleteStart = entity.is_deleteStart();
        msg.deleteStop = entity.is_deleteStop();
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, CurtailmentEventDeleteMsg msg,
                                                   NotifCurtailmentEventDelete entity) {
        entity.set_curtailmentEventId(msg.curtailmentEventId);
        entity.set_deleteStart(msg.deleteStart);
        entity.set_deleteStop(msg.deleteStop);
    }

}
