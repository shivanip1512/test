package com.cannontech.messaging.serialization.thrift.serializer.capcontrol.streamable;

import com.cannontech.messaging.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.messaging.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCCapBank;
import com.cannontech.messaging.serialization.thrift.generated.CCPao;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class CapBankDeviceSerializer extends
    ThriftInheritanceSerializer<CapBankDevice, StreamableCapObject, CCCapBank, CCPao> {

    public CapBankDeviceSerializer(String messageType, StreamableSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<CapBankDevice> getTargetMessageClass() {
        return CapBankDevice.class;
    }

    @Override
    protected CCCapBank createThrifEntityInstance(CCPao entityParent) {
        CCCapBank entity = new CCCapBank();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCPao getThriftEntityParent(CCCapBank entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected CapBankDevice createMessageInstance() {
        return new CapBankDevice();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCCapBank entity, CapBankDevice msg) {
        // For legacy reasons, the parentId is defined at this level in the Thrift Entity.
        // It is therefore here that the parent field "parentId" is populated in this object
        msg.setParentId(entity.get_parentId());

        msg.setMaxDailyOperation(entity.get_maxDailyOps());
        msg.setMaxOperationDisableFlag(entity.is_maxOpsDisableFlag());
        msg.setAlarmInhibit(entity.is_alarmInhibitFlag());
        msg.setControlInhibit(entity.is_controlInhibitFlag());
        msg.setOperationalState(entity.get_operationalState());
        msg.setControllerType(entity.get_controllerType());
        msg.setControlDeviceId(entity.get_controlDeviceId());
        msg.setBankSize(entity.get_bankSize());
        msg.setTypeOfSwitch(entity.get_typeOfSwitch());
        msg.setSwitchManufacture(entity.get_switchManufacture());
        msg.setMapLocationId(entity.get_mapLocationId());
        msg.setRecloseDelay(entity.get_recloseDelay());
        msg.setControlOrder((float) entity.get_controlOrder());
        msg.setStatusPointId(entity.get_statusPointId());
        msg.setControlStatus(entity.get_controlStatus());
        msg.setOperationAnalogPointId(entity.get_operationAnalogPointId());
        msg.setTotalOperations(entity.get_totalOperations());
        msg.setLastStatusChangeTime(ConverterHelper.millisecToDate(entity.get_lastStatusChangeTime()));
        msg.setTagControlStatus(entity.get_tagsControlStatus());
        msg.setOrigFeederId(entity.get_originalParentId());
        msg.setCurrentDailyOperations(entity.get_currentDailyOperations());
        msg.setIgnoreFlag(entity.is_ignoreFlag());
        msg.setIgnoreReason(entity.get_ignoreReason());
        msg.setOvUVDisabled(entity.is_ovUvDisabledFlag());
        msg.setTripOrder((float) entity.get_tripOrder());
        msg.setCloseOrder((float) entity.get_closeOrder());
        msg.setControlDeviceType(entity.get_controlDeviceType());
        msg.setBeforeVars(entity.get_sBeforeVars());
        msg.setAfterVars(entity.get_sAfterVars());
        msg.setPercentChange(entity.get_sPercentChange());
        msg.setMaxDailyOperationHitFlag(entity.is_maxDailyOpsHitFlag());
        msg.setOvuvSituationFlag(entity.is_ovUvSituationFlag());
        msg.setControlStatusQuality(entity.get_controlStatusQuality());
        msg.setLocalControlFlag(entity.is_localControlFlag());
        msg.setPartialPhaseInfo(entity.get_partialPhaseInfo());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, CapBankDevice msg, CCCapBank entity) {
        // For legacy reasons, the parentId is defined at this level in the Thrift Entity.
        // It is therefore here that the parent field "parentId" is populated in this object
        entity.set_parentId(msg.getParentId());

        entity.set_maxDailyOps(msg.getMaxDailyOperation());
        entity.set_maxOpsDisableFlag(msg.getMaxOperationDisableFlag());
        entity.set_alarmInhibitFlag(msg.isAlarmInhibit());
        entity.set_controlInhibitFlag(msg.isControlInhibit());
        entity.set_operationalState(msg.getOperationalState());
        entity.set_controllerType(msg.getControllerType());
        entity.set_controlDeviceId(msg.getControlDeviceId());
        entity.set_bankSize(msg.getBankSize());
        entity.set_typeOfSwitch(msg.getTypeOfSwitch());
        entity.set_switchManufacture(msg.getSwitchManufacture());
        entity.set_mapLocationId(msg.getMapLocationId());
        entity.set_recloseDelay(msg.getRecloseDelay());
        entity.set_controlOrder((float) msg.getControlOrder());
        entity.set_statusPointId(msg.getStatusPointId());
        entity.set_controlStatus(msg.getControlStatus());
        entity.set_operationAnalogPointId(msg.getOperationAnalogPointId());
        entity.set_totalOperations(msg.getTotalOperations());
        entity.set_lastStatusChangeTime(ConverterHelper.dateToMillisec(msg.getLastStatusChangeTime()));
        entity.set_tagsControlStatus(msg.getTagControlStatus());
        entity.set_originalParentId(msg.getOrigFeederId());
        entity.set_currentDailyOperations(msg.getCurrentDailyOperations());
        entity.set_ignoreFlag(msg.isIgnoreFlag());
        entity.set_ignoreReason(msg.getIgnoreReason());
        entity.set_ovUvDisabledFlag(msg.getOvUVDisabled());
        entity.set_tripOrder((float) msg.getTripOrder());
        entity.set_closeOrder((float) msg.getCloseOrder());
        entity.set_controlDeviceType(msg.getControlDeviceType());
        entity.set_sBeforeVars(msg.getBeforeVars());
        entity.set_sAfterVars(msg.getAfterVars());
        entity.set_sPercentChange(msg.getPercentChange());
        entity.set_maxDailyOpsHitFlag(msg.getMaxDailyOperationHitFlag());
        entity.set_ovUvSituationFlag(msg.getOvuvSituationFlag());
        entity.set_controlStatusQuality(msg.getControlStatusQuality());
        entity.set_localControlFlag(msg.getLocalControlFlag());
        entity.set_partialPhaseInfo(msg.getPartialPhaseInfo());
    }
}
