package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.message.capcontrol.model.VerifyBanks;
import com.cannontech.message.capcontrol.model.VerifyInactiveBanks;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCVerifyBanks;
import com.cannontech.messaging.serialization.thrift.generated.CCVerifyInactiveBanks;

public class VerifyInactiveBanksSerializer extends
    ThriftInheritanceSerializer<VerifyInactiveBanks, VerifyBanks, CCVerifyInactiveBanks, CCVerifyBanks> {

    public VerifyInactiveBanksSerializer(String messageType, VerifyBanksSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<VerifyInactiveBanks> getTargetMessageClass() {
        return VerifyInactiveBanks.class;
    }

    @Override
    protected CCVerifyInactiveBanks createThriftEntityInstance(CCVerifyBanks entityParent) {
        CCVerifyInactiveBanks entity = new CCVerifyInactiveBanks();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCVerifyBanks getThriftEntityParent(CCVerifyInactiveBanks entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected VerifyInactiveBanks createMessageInstance() {
        return new VerifyInactiveBanks();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCVerifyInactiveBanks entity,
                                                   VerifyInactiveBanks msg) {
        msg.setCbInactivityTime(entity.get_bankInactiveTime());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, VerifyInactiveBanks msg,
                                                   CCVerifyInactiveBanks entity) {
        entity.set_bankInactiveTime((int) msg.getCbInactivityTime());
    }
}
