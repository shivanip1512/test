package com.cannontech.messaging.serialization.thrift.serializer.capcontrol.streamable;

import com.cannontech.messaging.message.capcontrol.streamable.SpecialArea;
import com.cannontech.messaging.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCPao;
import com.cannontech.messaging.serialization.thrift.generated.CCSpecial;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class SpecialAreaSerializer extends
    ThriftInheritanceSerializer<SpecialArea, StreamableCapObject, CCSpecial, CCPao> {

    public SpecialAreaSerializer(String messageType, StreamableSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<SpecialArea> getTargetMessageClass() {
        return SpecialArea.class;
    }

    @Override
    protected CCSpecial createThrifEntityInstance(CCPao entityParent) {
        CCSpecial entity = new CCSpecial();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCPao getThriftEntityParent(CCSpecial entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected SpecialArea createMessageInstance() {
        return new SpecialArea();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCSpecial entity, SpecialArea msg) {
        msg.setCcSubIds(ConverterHelper.toIntArray(entity.get_substationIds()));
        msg.setOvUvDisabledFlag(entity.is_ovUvDisabledFlag());
        msg.setPowerFactorValue(entity.get_pfDisplayValue());
        msg.setEstimatedPFValue(entity.get_estPfDisplayValue());
        msg.setVoltReductionFlag(entity.is_voltReductionControlValue());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, SpecialArea msg, CCSpecial entity) {
        entity.set_substationIds(ConverterHelper.toList(msg.getCcSubIds()));
        entity.set_ovUvDisabledFlag(entity.is_ovUvDisabledFlag());
        entity.set_pfDisplayValue(msg.getPowerFactorValue());
        entity.set_estPfDisplayValue(msg.getEstimatedPFValue());
        entity.set_voltReductionControlValue(entity.is_voltReductionControlValue());
    }
}
