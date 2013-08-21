package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.database.db.state.State;
import com.cannontech.message.capcontrol.model.CapControlMessage;
import com.cannontech.message.capcontrol.model.States;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCCapBankStates;
import com.cannontech.messaging.serialization.thrift.generated.CCMessage;
import com.cannontech.messaging.serialization.thrift.generated.CCState;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class StatesSerializer extends
    ThriftInheritanceSerializer<States, CapControlMessage, CCCapBankStates, CCMessage> {

    public StatesSerializer(String messageType, CapControlSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<States> getTargetMessageClass() {
        return States.class;
    }

    @Override
    protected States createMessageInstance() {
        return new States();
    }

    @Override
    protected CCCapBankStates createThriftEntityInstance(CCMessage entityParent) {
        CCCapBankStates entity = new CCCapBankStates();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCMessage getThriftEntityParent(CCCapBankStates entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCCapBankStates entity, States msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        msg.setStates(helper.convertToMessageVector(entity.get_ccCapBankStates(), State.class));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, States msg, CCCapBankStates entity) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        entity.set_ccCapBankStates(helper.convertToEntityList(msg.getStates(), CCState.class));
    }
}
