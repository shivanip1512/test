package com.cannontech.messaging.serialization.thrift.serializer.capcontrol.streamable;

import com.cannontech.messaging.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.messaging.message.capcontrol.streamable.SubStation;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCPao;
import com.cannontech.messaging.serialization.thrift.generated.CCSubstationItem;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class SubStationSerializer extends
    ThriftInheritanceSerializer<SubStation, StreamableCapObject, CCSubstationItem, CCPao> {

    public SubStationSerializer(String messageType, StreamableSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<SubStation> getTargetMessageClass() {
        return SubStation.class;
    }

    @Override
    protected CCSubstationItem createThrifEntityInstance(CCPao entityParent) {
        CCSubstationItem entity = new CCSubstationItem();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCPao getThriftEntityParent(CCSubstationItem entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected SubStation createMessageInstance() {
        return new SubStation();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCSubstationItem entity, SubStation msg) {
        // For legacy reasons, the parentId is defined at this level in the Thrift Entity.
        // It is therefore here that the parent field "parentId" is populated in this object
        msg.setParentId(entity.get_parentId());

        msg.setOvuvDisableFlag(entity.is_ovUvDisabledFlag());
        msg.setSubBusIds(ConverterHelper.toIntArray(entity.get_subBusIds()));
        msg.setPowerFactorValue(entity.get_pfDisplayValue());
        msg.setEstimatedPFValue(entity.get_estPfDisplayValue());
        msg.setSpecialAreaEnabled(entity.is_saEnabledFlag());
        msg.setSpecialAreaId(entity.get_saEnabledId());
        msg.setVoltReductionFlag(entity.is_voltReductionFlag());
        msg.setRecentlyControlledFlag(entity.is_recentlyControlledFlag());
        msg.setChildVoltReductionFlag(entity.is_childVoltReductionFlag());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, SubStation msg, CCSubstationItem entity) {
        // For legacy reasons, the parentId is defined at this level in the Thrift Entity.
        // It is therefore here that the parent field "parentId" is populated in this object
        entity.set_parentId(msg.getParentId());

        entity.set_ovUvDisabledFlag(msg.getOvuvDisableFlag());
        entity.set_subBusIds(ConverterHelper.toList(msg.getSubBusIds()));
        entity.set_pfDisplayValue(msg.getPowerFactorValue());
        entity.set_estPfDisplayValue(msg.getEstimatedPFValue());
        entity.set_saEnabledFlag(msg.getSpecialAreaEnabled());
        entity.set_saEnabledId(msg.getSpecialAreaId());
        entity.set_voltReductionFlag(msg.getVoltReductionFlag());
        entity.set_recentlyControlledFlag(msg.getRecentlyControlledFlag());
        entity.set_childVoltReductionFlag(msg.getChildVoltReductionFlag());
    }

}
