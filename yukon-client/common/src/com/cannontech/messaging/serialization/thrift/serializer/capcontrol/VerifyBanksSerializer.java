package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.messaging.message.capcontrol.ItemCommandMessage;
import com.cannontech.messaging.message.capcontrol.VerifyBanksMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCItemCommand;
import com.cannontech.messaging.serialization.thrift.generated.CCVerifyBanks;

public class VerifyBanksSerializer extends
    ThriftInheritanceSerializer<VerifyBanksMessage, ItemCommandMessage, CCVerifyBanks, CCItemCommand> {

    public VerifyBanksSerializer(String messageType, ItemCommandSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<VerifyBanksMessage> getTargetMessageClass() {
        return VerifyBanksMessage.class;
    }

    @Override
    protected CCVerifyBanks createThrifEntityInstance(CCItemCommand entityParent) {
        CCVerifyBanks entity = new CCVerifyBanks();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCItemCommand getThriftEntityParent(CCVerifyBanks entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected VerifyBanksMessage createMessageInstance() {
        return new VerifyBanksMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCVerifyBanks entity, VerifyBanksMessage msg) {
        msg.setDisableOvUv(entity.is_disableOvUv());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, VerifyBanksMessage msg, CCVerifyBanks entity) {
        entity.set_disableOvUv(msg.isDisableOvUv());
    }

}
