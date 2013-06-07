package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.messaging.message.capcontrol.VerifyBanksMessage;
import com.cannontech.messaging.message.capcontrol.VerifySelectedBankMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCVerifyBanks;
import com.cannontech.messaging.serialization.thrift.generated.CCVerifySelectedBank;

public class VerifySelectedBankSerializer extends
    ThriftInheritanceSerializer<VerifySelectedBankMessage, VerifyBanksMessage, CCVerifySelectedBank, CCVerifyBanks> {

    public VerifySelectedBankSerializer(String messageType, VerifyBanksSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<VerifySelectedBankMessage> getTargetMessageClass() {
        return VerifySelectedBankMessage.class;
    }

    @Override
    protected CCVerifySelectedBank createThrifEntityInstance(CCVerifyBanks entityParent) {
        CCVerifySelectedBank entity = new CCVerifySelectedBank();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCVerifyBanks getThriftEntityParent(CCVerifySelectedBank entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected VerifySelectedBankMessage createMessageInstance() {
        return new VerifySelectedBankMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCVerifySelectedBank entity, VerifySelectedBankMessage msg) {
        msg.setBankId(entity.get_bankId());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, VerifySelectedBankMessage msg, CCVerifySelectedBank entity) {
        entity.set_bankId((int) msg.getBankId());
    }
}
