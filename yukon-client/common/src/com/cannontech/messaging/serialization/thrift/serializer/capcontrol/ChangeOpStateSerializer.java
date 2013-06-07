package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.capcontrol.BankOpState;
import com.cannontech.messaging.message.capcontrol.ChangeOpStateMessage;
import com.cannontech.messaging.message.capcontrol.ItemCommandMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCChangeOpState;
import com.cannontech.messaging.serialization.thrift.generated.CCItemCommand;

public class ChangeOpStateSerializer extends
    ThriftInheritanceSerializer<ChangeOpStateMessage, ItemCommandMessage, CCChangeOpState, CCItemCommand> {

    public ChangeOpStateSerializer(String messageType, ItemCommandSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ChangeOpStateMessage> getTargetMessageClass() {
        return ChangeOpStateMessage.class;
    }

    @Override
    protected CCChangeOpState createThrifEntityInstance(CCItemCommand entityParent) {
        CCChangeOpState entity = new CCChangeOpState();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCItemCommand getThriftEntityParent(CCChangeOpState entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected ChangeOpStateMessage createMessageInstance() {
        return new ChangeOpStateMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCChangeOpState entity, ChangeOpStateMessage msg) {
        msg.setState(BankOpState.getStateByName(entity.get_opStateName()));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ChangeOpStateMessage msg, CCChangeOpState entity) {
        entity.set_opStateName(msg.getState().name());
    }
}
