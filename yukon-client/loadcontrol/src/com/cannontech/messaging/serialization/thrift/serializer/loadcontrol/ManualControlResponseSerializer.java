package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import com.cannontech.messaging.message.loadcontrol.LmMessage;
import com.cannontech.messaging.message.loadcontrol.ManualControlResponseMessage;
import com.cannontech.messaging.message.loadcontrol.data.ConstraintViolation;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMManualControlResponse;
import com.cannontech.messaging.serialization.thrift.generated.LMMessage;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class ManualControlResponseSerializer extends
    ThriftInheritanceSerializer<ManualControlResponseMessage, LmMessage, LMManualControlResponse, LMMessage> {

    public ManualControlResponseSerializer(String messageType, LmMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ManualControlResponseMessage> getTargetMessageClass() {
        return ManualControlResponseMessage.class;
    }

    @Override
    protected LMManualControlResponse createThrifEntityInstance(LMMessage entityParent) {
        LMManualControlResponse entity = new LMManualControlResponse();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMMessage getThriftEntityParent(LMManualControlResponse entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected ManualControlResponseMessage createMessageInstance() {
        return new ManualControlResponseMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMManualControlResponse entity, ManualControlResponseMessage msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();
        
        msg.setProgramId(entity.get_paoId());
        msg.setConstraintViolations(helper.convertToMessageList(entity.get_constraintViolations(),
            ConstraintViolation.class));
        msg.setBestFitAction(entity.get_bestFitAction());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ManualControlResponseMessage msg, LMManualControlResponse entity) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();
        
        entity.set_paoId(msg.getProgramId());
        entity.set_constraintViolations(helper.convertToEntityList(msg.getConstraintViolations(),
            com.cannontech.messaging.serialization.thrift.generated.LMConstraintViolation.class));
        entity.set_bestFitAction(msg.getBestFitAction());
    }
}
