package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.messaging.message.capcontrol.VerifyBanksMessage;
import com.cannontech.messaging.message.capcontrol.VerifyInactiveBanksMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCVerifyBanks;
import com.cannontech.messaging.serialization.thrift.generated.CCVerifyInactiveBanks;

public class VerifyInactiveBanksSerializer extends
    ThriftInheritanceSerializer<VerifyInactiveBanksMessage, VerifyBanksMessage, CCVerifyInactiveBanks, CCVerifyBanks> {

    public VerifyInactiveBanksSerializer(String messageType, VerifyBanksSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<VerifyInactiveBanksMessage> getTargetMessageClass() {
        return VerifyInactiveBanksMessage.class;
    }

    @Override
    protected CCVerifyInactiveBanks createThrifEntityInstance(CCVerifyBanks entityParent) {
        CCVerifyInactiveBanks entity = new CCVerifyInactiveBanks();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCVerifyBanks getThriftEntityParent(CCVerifyInactiveBanks entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected VerifyInactiveBanksMessage createMessageInstance() {
        return new VerifyInactiveBanksMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCVerifyInactiveBanks entity, VerifyInactiveBanksMessage msg) {
        msg.setCbInactivityTime(entity.get_bankInactiveTime());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, VerifyInactiveBanksMessage msg, CCVerifyInactiveBanks entity) {
        entity.set_bankInactiveTime((int) msg.getCbInactivityTime());
    }
}
