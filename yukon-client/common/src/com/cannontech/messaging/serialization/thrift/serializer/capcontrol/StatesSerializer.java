package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.database.db.state.State;
import com.cannontech.messaging.message.capcontrol.CapControlMessage;
import com.cannontech.messaging.message.capcontrol.StatesMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCCapBankStates;
import com.cannontech.messaging.serialization.thrift.generated.CCMessage;
import com.cannontech.messaging.serialization.thrift.generated.CCState;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class StatesSerializer extends
    ThriftInheritanceSerializer<StatesMessage, CapControlMessage, CCCapBankStates, CCMessage> {

    public StatesSerializer(String messageType, CapControlSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<StatesMessage> getTargetMessageClass() {
        return StatesMessage.class;
    }

    @Override
    protected StatesMessage createMessageInstance() {
        return new StatesMessage();
    }

    @Override
    protected CCCapBankStates createThrifEntityInstance(CCMessage entityParent) {
        CCCapBankStates entity = new CCCapBankStates();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCMessage getThriftEntityParent(CCCapBankStates entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCCapBankStates entity, StatesMessage msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();
        
        msg.setStates(helper.convertToMessageVector(entity.get_ccCapBankStates(), State.class));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, StatesMessage msg, CCCapBankStates entity) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();
        
        entity.set_ccCapBankStates(helper.convertToEntityList(msg.getStates(), CCState.class));
    }
}
