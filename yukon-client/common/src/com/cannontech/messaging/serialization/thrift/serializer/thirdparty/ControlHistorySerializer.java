package com.cannontech.messaging.serialization.thrift.serializer.thirdparty;

import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMControlHistory;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;
import com.cannontech.thirdparty.messaging.ControlHistoryMessage;

public class ControlHistorySerializer
    extends
    ThriftInheritanceSerializer<ControlHistoryMessage, Message, LMControlHistory, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public ControlHistorySerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ControlHistoryMessage> getTargetMessageClass() {
        return ControlHistoryMessage.class;
    }

    @Override
    protected LMControlHistory
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        LMControlHistory entity = new LMControlHistory();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(LMControlHistory entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected ControlHistoryMessage createMessageInstance() {

        return new ControlHistoryMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory factory, LMControlHistory entity,
                                                   ControlHistoryMessage msg) {
        msg.setActiveRestore(entity.get_activeRestore());
        msg.setAssociationId(entity.get_associationKey());
        msg.setControlDuration(entity.get_controlDuration());
        msg.setControlPriority(entity.get_controlPriority());
        msg.setControlType(entity.get_controlType());
        msg.setPaoId(entity.get_paoId());
        msg.setPointId(entity.get_pointId());
        msg.setRawState(entity.get_rawState());
        msg.setReductionRatio(entity.get_reductionRatio());
        msg.setReductionValue(entity.get_reductionValue());
        msg.setStartTime(ConverterHelper.millisecToDate(entity.get_startDateTime()));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory factory, ControlHistoryMessage msg,
                                                   LMControlHistory entity) {
        entity.set_activeRestore(msg.getActiveRestore());
        entity.set_associationKey(msg.getAssociationId());
        entity.set_controlDuration(msg.getControlDuration());
        entity.set_controlPriority(msg.getControlPriority());
        entity.set_controlType(msg.getControlType());
        entity.set_paoId(msg.getPaoId());
        entity.set_pointId(msg.getPointId());
        entity.set_rawState(msg.getRawState());
        entity.set_reductionRatio(msg.getReductionRatio());
        entity.set_reductionValue(msg.getReductionValue());
        entity.set_startDateTime(ConverterHelper.dateToMillisec(msg.getStartTime()));
    }
}
