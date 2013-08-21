package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import com.cannontech.loadcontrol.messages.LMCurtailmentAcknowledgeMsg;
import com.cannontech.loadcontrol.messages.LMMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMCurtailmentAcknowledge;

public class CurtailmentAcknowledgeSerializer
    extends
    ThriftInheritanceSerializer<LMCurtailmentAcknowledgeMsg, LMMessage, LMCurtailmentAcknowledge, com.cannontech.messaging.serialization.thrift.generated.LMMessage> {

    public CurtailmentAcknowledgeSerializer(String messageType, LmMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMCurtailmentAcknowledgeMsg> getTargetMessageClass() {
        return LMCurtailmentAcknowledgeMsg.class;
    }

    @Override
    protected LMCurtailmentAcknowledge
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.LMMessage entityParent) {
        LMCurtailmentAcknowledge entity = new LMCurtailmentAcknowledge();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMMessage
        getThriftEntityParent(LMCurtailmentAcknowledge entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMCurtailmentAcknowledgeMsg createMessageInstance() {
        return new LMCurtailmentAcknowledgeMsg();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMCurtailmentAcknowledge entity,
                                                   LMCurtailmentAcknowledgeMsg msg) {
        msg.setYukonID(entity.get_paoId());
        msg.setCurtailReferenceID(entity.get_curtailReferenceId());
        msg.setAcknowledgeStatus(entity.get_acknowledgeStatus());
        msg.setIpAddressOfAckUser(entity.get_ipAddressOfAckUser());
        msg.setUserIdName(entity.get_userIdName());
        msg.setNameOfAckPerson(entity.get_nameOfAckPerson());
        msg.setCurtailmentNotes(entity.get_curtailmentNotes());

    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMCurtailmentAcknowledgeMsg msg,
                                                   LMCurtailmentAcknowledge entity) {
        entity.set_paoId(msg.getYukonID());
        entity.set_curtailReferenceId(msg.getCurtailReferenceID());
        entity.set_acknowledgeStatus(msg.getAcknowledgeStatus());
        entity.set_ipAddressOfAckUser(msg.getIpAddressOfAckUser());
        entity.set_userIdName(msg.getUserIdName());
        entity.set_nameOfAckPerson(msg.getNameOfAckPerson());
        entity.set_curtailmentNotes(msg.getCurtailmentNotes());
    }
}
