package com.cannontech.messaging.serialization.thrift.serializer.capcontrol.streamable;

import com.cannontech.messaging.message.capcontrol.streamable.Area;
import com.cannontech.messaging.message.capcontrol.streamable.StreamableCapObject;
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
    protected CCArea createThrifEntityInstance(CCPao entityParent) {
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
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, Area msg, CCArea entity) {
        entity.set_childVoltReductionFlag(msg.getChildVoltReductionFlag());
        entity.set_estPfDisplayValue(msg.getEstimatedPFValue());
        entity.set_ovUvDisabledFlag(msg.getOvUvDisabledFlag());
        entity.set_pfDisplayValue(msg.getPowerFactorValue());
        entity.set_substationIds(ConverterHelper.toList(msg.getStations()));
        entity.set_voltReductionControlValue(msg.getVoltReductionFlag());
    }
}
