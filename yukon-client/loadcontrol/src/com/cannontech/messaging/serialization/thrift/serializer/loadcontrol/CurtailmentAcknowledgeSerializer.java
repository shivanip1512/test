package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import com.cannontech.messaging.message.loadcontrol.CurtailmentAcknowledgeMessage;
import com.cannontech.messaging.message.loadcontrol.LmMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.LMCurtailmentAcknowledge;
import com.cannontech.messaging.serialization.thrift.generated.LMMessage;

public class CurtailmentAcknowledgeSerializer extends
    ThriftInheritanceSerializer<CurtailmentAcknowledgeMessage, LmMessage, LMCurtailmentAcknowledge, LMMessage> {

    public CurtailmentAcknowledgeSerializer(String messageType,
        ThriftSerializer<LmMessage, LMMessage> parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<CurtailmentAcknowledgeMessage> getTargetMessageClass() {
        return CurtailmentAcknowledgeMessage.class;
    }

    @Override
    protected LMCurtailmentAcknowledge createThrifEntityInstance(LMMessage entityParent) {
        LMCurtailmentAcknowledge entity = new LMCurtailmentAcknowledge();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMMessage getThriftEntityParent(LMCurtailmentAcknowledge entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected CurtailmentAcknowledgeMessage createMessageInstance() {
        return new CurtailmentAcknowledgeMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMCurtailmentAcknowledge entity, CurtailmentAcknowledgeMessage msg) {
        msg.setYukonId(entity.get_paoId());
        msg.setCurtailReferenceId(entity.get_curtailReferenceId());
        msg.setAcknowledgeStatus(entity.get_acknowledgeStatus());
        msg.setIpAddressOfAckUser(entity.get_ipAddressOfAckUser());
        msg.setUserIdName(entity.get_userIdName());
        msg.setNameOfAckPerson(entity.get_nameOfAckPerson());
        msg.setCurtailmentNotes(entity.get_curtailmentNotes());

    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, CurtailmentAcknowledgeMessage msg, LMCurtailmentAcknowledge entity) {
        entity.set_paoId(msg.getYukonId());
        entity.set_curtailReferenceId(msg.getCurtailReferenceId());
        entity.set_acknowledgeStatus(msg.getAcknowledgeStatus());
        entity.set_ipAddressOfAckUser(msg.getIpAddressOfAckUser());
        entity.set_userIdName(msg.getUserIdName());
        entity.set_nameOfAckPerson(msg.getNameOfAckPerson());
        entity.set_curtailmentNotes(msg.getCurtailmentNotes());
    }
}
