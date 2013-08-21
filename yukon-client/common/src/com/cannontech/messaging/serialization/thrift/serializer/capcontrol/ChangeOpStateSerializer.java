package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.capcontrol.BankOpState;
import com.cannontech.message.capcontrol.model.ChangeOpState;
import com.cannontech.message.capcontrol.model.ItemCommand;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCChangeOpState;
import com.cannontech.messaging.serialization.thrift.generated.CCItemCommand;

public class ChangeOpStateSerializer extends
    ThriftInheritanceSerializer<ChangeOpState, ItemCommand, CCChangeOpState, CCItemCommand> {

    public ChangeOpStateSerializer(String messageType, ItemCommandSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ChangeOpState> getTargetMessageClass() {
        return ChangeOpState.class;
    }

    @Override
    protected CCChangeOpState createThriftEntityInstance(CCItemCommand entityParent) {
        CCChangeOpState entity = new CCChangeOpState();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCItemCommand getThriftEntityParent(CCChangeOpState entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected ChangeOpState createMessageInstance() {
        return new ChangeOpState();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCChangeOpState entity, ChangeOpState msg) {
        msg.setState(BankOpState.getStateByName(entity.get_opStateName()));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ChangeOpState msg, CCChangeOpState entity) {
        entity.set_opStateName(msg.getState().getDbString());
    }
}
