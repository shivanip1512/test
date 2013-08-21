package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.message.capcontrol.model.ItemCommand;
import com.cannontech.message.capcontrol.model.VerifyBanks;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCItemCommand;
import com.cannontech.messaging.serialization.thrift.generated.CCVerifyBanks;

public class VerifyBanksSerializer extends
    ThriftInheritanceSerializer<VerifyBanks, ItemCommand, CCVerifyBanks, CCItemCommand> {

    public VerifyBanksSerializer(String messageType, ItemCommandSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<VerifyBanks> getTargetMessageClass() {
        return VerifyBanks.class;
    }

    @Override
    protected CCVerifyBanks createThriftEntityInstance(CCItemCommand entityParent) {
        CCVerifyBanks entity = new CCVerifyBanks();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCItemCommand getThriftEntityParent(CCVerifyBanks entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected VerifyBanks createMessageInstance() {
        return new VerifyBanks();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCVerifyBanks entity,
                                                   VerifyBanks msg) {
        msg.setDisableOvUv(entity.is_disableOvUv());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, VerifyBanks msg,
                                                   CCVerifyBanks entity) {
        entity.set_disableOvUv(msg.isDisableOvUv());
    }

}
