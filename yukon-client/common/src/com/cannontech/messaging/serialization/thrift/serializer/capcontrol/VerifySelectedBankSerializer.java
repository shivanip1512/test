package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.message.capcontrol.model.VerifyBanks;
import com.cannontech.message.capcontrol.model.VerifySelectedBank;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCVerifyBanks;
import com.cannontech.messaging.serialization.thrift.generated.CCVerifySelectedBank;

public class VerifySelectedBankSerializer extends
    ThriftInheritanceSerializer<VerifySelectedBank, VerifyBanks, CCVerifySelectedBank, CCVerifyBanks> {

    public VerifySelectedBankSerializer(String messageType, VerifyBanksSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<VerifySelectedBank> getTargetMessageClass() {
        return VerifySelectedBank.class;
    }

    @Override
    protected CCVerifySelectedBank createThriftEntityInstance(CCVerifyBanks entityParent) {
        CCVerifySelectedBank entity = new CCVerifySelectedBank();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCVerifyBanks getThriftEntityParent(CCVerifySelectedBank entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected VerifySelectedBank createMessageInstance() {
        return new VerifySelectedBank();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCVerifySelectedBank entity,
                                                   VerifySelectedBank msg) {
        msg.setBankId(entity.get_bankId());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, VerifySelectedBank msg,
                                                   CCVerifySelectedBank entity) {
        entity.set_bankId((int) msg.getBankId());
    }
}
