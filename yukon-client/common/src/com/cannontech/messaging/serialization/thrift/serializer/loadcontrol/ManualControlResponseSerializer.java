package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import com.cannontech.loadcontrol.messages.ConstraintViolation;
import com.cannontech.loadcontrol.messages.LMManualControlResponse;
import com.cannontech.loadcontrol.messages.LMMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class ManualControlResponseSerializer
    extends
    ThriftInheritanceSerializer<LMManualControlResponse, LMMessage, com.cannontech.messaging.serialization.thrift.generated.LMManualControlResponse, com.cannontech.messaging.serialization.thrift.generated.LMMessage> {

    public ManualControlResponseSerializer(String messageType, LmMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMManualControlResponse> getTargetMessageClass() {
        return LMManualControlResponse.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMManualControlResponse
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.LMMessage entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.LMManualControlResponse entity =
            new com.cannontech.messaging.serialization.thrift.generated.LMManualControlResponse();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMMessage
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.LMManualControlResponse entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMManualControlResponse createMessageInstance() {
        return new LMManualControlResponse();
    }

    @Override
    protected
        void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMManualControlResponse entity,
                                        LMManualControlResponse msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        msg.setProgramID(entity.get_paoId());
        msg.setConstraintViolations(helper.convertToMessageList(entity.get_constraintViolations(),
                                                                ConstraintViolation.class));
        msg.setBestFitAction(entity.get_bestFitAction());
    }

    @Override
    protected
        void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory,
                                        LMManualControlResponse msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMManualControlResponse entity) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        entity.set_paoId(msg.getProgramID());
        entity
            .set_constraintViolations(helper.convertToEntityList(msg.getConstraintViolations(),
                                                                 com.cannontech.messaging.serialization.thrift.generated.LMConstraintViolation.class));
        entity.set_bestFitAction(msg.getBestFitAction());
    }
}
