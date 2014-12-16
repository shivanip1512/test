package com.cannontech.messaging.serialization.thrift.serializer.capcontrol.streamable;

import com.cannontech.message.capcontrol.streamable.SpecialArea;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
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
    protected CCSpecial createThriftEntityInstance(CCPao entityParent) {
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

        msg.setCcId(entity.get_baseMessage().get_paoId());
        msg.setDisableFlag(entity.get_baseMessage().is_disableFlag());
        msg.setPaoCategory(entity.get_baseMessage().get_paoCategory());
        msg.setPaoClass(entity.get_baseMessage().get_paoClass());
        msg.setPaoDescription(entity.get_baseMessage().get_paoDescription());
        msg.setCcName(entity.get_baseMessage().get_paoName());
        msg.setCcType(entity.get_baseMessage().get_paoType());

    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, SpecialArea msg, CCSpecial entity) {
        entity.set_substationIds(ConverterHelper.toList(msg.getCcSubIds()));
        entity.set_ovUvDisabledFlag(msg.getOvUvDisabledFlag());
        entity.set_pfDisplayValue(msg.getPowerFactorValue());
        entity.set_estPfDisplayValue(msg.getEstimatedPFValue());
        entity.set_voltReductionControlValue(msg.getVoltReductionFlag());

        entity.get_baseMessage().set_paoId(msg.getCcId());
        entity.get_baseMessage().set_disableFlag(msg.getDisableFlag());
        entity.get_baseMessage().set_paoCategory(msg.getPaoCategory());
        entity.get_baseMessage().set_paoClass(msg.getPaoClass());
        entity.get_baseMessage().set_paoDescription(msg.getPaoDescription());
        entity.get_baseMessage().set_paoName(msg.getCcName());
        entity.get_baseMessage().set_paoType(msg.getCcType());
    }
}
