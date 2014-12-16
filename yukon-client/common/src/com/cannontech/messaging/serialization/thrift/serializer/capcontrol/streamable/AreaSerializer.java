package com.cannontech.messaging.serialization.thrift.serializer.capcontrol.streamable;

import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCArea;
import com.cannontech.messaging.serialization.thrift.generated.CCPao;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class AreaSerializer extends ThriftInheritanceSerializer<Area, StreamableCapObject, CCArea, CCPao> {

    public AreaSerializer(String messageType, StreamableSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<Area> getTargetMessageClass() {
        return Area.class;
    }

    @Override
    protected CCArea createThriftEntityInstance(CCPao entityParent) {
        CCArea entity = new CCArea();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCPao getThriftEntityParent(CCArea entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected Area createMessageInstance() {
        return new Area();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCArea entity, Area msg) {
        msg.setChildVoltReductionFlag(entity.is_childVoltReductionFlag());
        msg.setEstimatedPFValue(entity.get_estPfDisplayValue());
        msg.setOvUvDisabledFlag(entity.is_ovUvDisabledFlag());
        msg.setPowerFactorValue(entity.get_pfDisplayValue());
        msg.setStations(ConverterHelper.toIntArray(entity.get_substationIds()));
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
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, Area msg, CCArea entity) {
        entity.set_childVoltReductionFlag(msg.getChildVoltReductionFlag());
        entity.set_estPfDisplayValue(msg.getEstimatedPFValue());
        entity.set_ovUvDisabledFlag(msg.getOvUvDisabledFlag());
        entity.set_pfDisplayValue(msg.getPowerFactorValue());
        entity.set_substationIds(ConverterHelper.toList(msg.getStations()));
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
