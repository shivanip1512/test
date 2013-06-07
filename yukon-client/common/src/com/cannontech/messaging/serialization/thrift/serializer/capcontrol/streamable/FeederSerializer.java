package com.cannontech.messaging.serialization.thrift.serializer.capcontrol.streamable;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.messaging.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.messaging.message.capcontrol.streamable.Feeder;
import com.cannontech.messaging.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCFeeder;
import com.cannontech.messaging.serialization.thrift.generated.CCPao;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class FeederSerializer extends ThriftInheritanceSerializer<Feeder, StreamableCapObject, CCFeeder, CCPao> {

    public FeederSerializer(String messageType, StreamableSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<Feeder> getTargetMessageClass() {
        return Feeder.class;
    }

    @Override
    protected CCFeeder createThrifEntityInstance(CCPao entityParent) {
        CCFeeder entity = new CCFeeder();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCPao getThriftEntityParent(CCFeeder entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected Feeder createMessageInstance() {
        return new Feeder();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCFeeder entity, Feeder msg) {
        // For legacy reasons, the parentId is defined at this level in the Thrift Entity.
        // It is therefore here that the parent field "parentId" is populated in the message object
        msg.setParentId(entity.get_parentId());

        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        msg.setCcCapBanks(helper.convertToMessageVector(entity.get_ccCapbanks(), CapBankDevice.class));
        msg.setControlmethod(ControlMethod.getForDisplayName(entity.get_strategy_controlMethod()));
        msg.setControlUnits(ControlAlgorithm.getDisplayName(entity.get_strategy_controlUnits()));
        msg.setCurrentDailyOperations(entity.get_currentDailyOperations());
        msg.setCurrentVarLoadPointId(entity.get_currentVarLoadPointId());
        msg.setCurrentVarLoadPointValue(entity.get_currentVarLoadPointValue());
        msg.setCurrentVarPtQuality(entity.get_currentVarPointQuality());
        msg.setCurrentVoltLoadPointId(entity.get_currentVarLoadPointId());
        msg.setCurrentVoltLoadPointValue(entity.get_currentVoltLoadPointValue());
        msg.setCurrentvoltpointquality(entity.get_currentVoltPointQuality());
        msg.setCurrentWattLoadPointId(entity.get_currentWattLoadPointId());
        msg.setCurrentWattLoadPointValue(entity.get_currentWattLoadPointValue());
        msg.setCurrentwattpointquality(entity.get_currentWattPointQuality());
        msg.setDailyOperationsAnalogPointId(entity.get_dailyOperationsAnalogPointId());
        msg.setDecimalPlaces(entity.get_decimalPlaces());
        msg.setDisplayOrder((float) entity.get_displayOrder());
        msg.setEstimatedPowerFactorPointId(entity.get_estimatedPowerFactorPointId());
        msg.setEstimatedPowerFactorValue(entity.get_estimatedPowerFactorValue());
        msg.setEstimatedVarLoadPointId(entity.get_estimatedVarLoadPointId());
        msg.setEstimatedVarLoadPointValue(entity.get_estimatedVarLoadPointValue());
        msg.setLastCurrentVarPointUpdateTime(ConverterHelper.millisecToDate(entity.get_lastCurrentVarPointUpdateTime()));
        msg.setLastOperationTime(ConverterHelper.millisecToDate(entity.get_lastOperationTime()));
        msg.setLikeDayControlFlag(entity.is_likeDayControlFlag());
        msg.setMapLocationId(entity.get_mapLocationId());
        msg.setMaxDailyOperation(entity.get_strategy_maxDailyOperation());
        msg.setMaxOperationDisableFlag(entity.is_strategy_maxOperationDisableFlag());
        msg.setNewPointDataReceivedFlag(entity.is_newPointDataReceivedFlag());
        msg.setOffpeakPFSetPoint(entity.get_strategy_offPeakPFSetPoint());
        msg.setOffPkLag(entity.get_strategy_peakLag());
        msg.setOffPkLead(entity.get_strategy_PeakLead());
        msg.setOriginalParentId(entity.get_originalParentId());
        msg.setOvUvDisabledFlag(entity.is_ovUvDisabledFlag());
        msg.setPeakLag(entity.get_strategy_peakLag());
        msg.setPeakLead(entity.get_strategy_PeakLead());
        msg.setPeakPFSetPoint(entity.get_strategy_peakPFSetPoint());
        msg.setPeakTimeFlag(entity.is_peakTimeFlag());
        msg.setPhaseA(entity.get_phaseAValue());
        msg.setPhaseB(entity.get_phaseBValue());
        msg.setPhaseC(entity.get_phaseCValue());
        msg.setPowerFactorPointId(entity.get_powerFactorPointId());
        msg.setPowerFactorValue(entity.get_powerFactorValue());
        msg.setRecentlyControlledFlag(entity.is_recentlyControlledFlag_or_performingVerificationFlag());
        msg.setSolution(entity.get_solution());
        msg.setTargetvarvalue(entity.get_targetVarValue());
        msg.setUsePhaseData(entity.is_usePhaseData());
        msg.setVarValueBeforeControl(entity.get_varValueBeforeControl());
        msg.setWaiveControlFlag(entity.is_waiveControlFlag());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, Feeder msg, CCFeeder entity) {
        throw new RuntimeException("This operation is not implemented");
    }

}
