package com.cannontech.messaging.serialization.thrift.serializer.capcontrol.streamable;

import org.apache.commons.lang3.EnumUtils;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
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
    protected CCFeeder createThriftEntityInstance(CCPao entityParent) {
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
        msg.setParentID(entity.get_parentId());

        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        msg.setCcCapBanks(helper.convertToMessageVector(entity.get_ccCapbanks(), CapBankDevice.class));
        
        ControlMethod controlMethod = EnumUtils.getEnum(ControlMethod.class, entity.get_strategy_controlMethod());
        msg.setControlmethod(controlMethod);
        msg.setAlgorithm(ControlAlgorithm.valueOf(entity.get_strategy_controlUnits()));
        msg.setCurrentDailyOperations(entity.get_currentDailyOperations());
        msg.setCurrentVarLoadPointID(entity.get_currentVarLoadPointId());
        msg.setCurrentVarLoadPointValue(entity.get_currentVarLoadPointValue());
        msg.setCurrentVarPtQuality(entity.get_currentVarPointQuality());
        msg.setCurrentVoltLoadPointID(entity.get_currentVoltLoadPointId());
        msg.setCurrentVoltLoadPointValue(entity.get_currentVoltLoadPointValue());
        msg.setCurrentvoltpointquality(entity.get_currentVoltPointQuality());
        msg.setCurrentWattLoadPointID(entity.get_currentWattLoadPointId());
        msg.setCurrentWattLoadPointValue(entity.get_currentWattLoadPointValue());
        msg.setCurrentwattpointquality(entity.get_currentWattPointQuality());
        msg.setDailyOperationsAnalogPointID(entity.get_dailyOperationsAnalogPointId());
        msg.setDecimalPlaces(entity.get_decimalPlaces());
        msg.setDisplayOrder((float) entity.get_displayOrder());
        msg.setEstimatedPowerFactorPointID(entity.get_estimatedPowerFactorPointId());
        msg.setEstimatedPFValue(entity.get_estimatedPowerFactorValue());
        msg.setEstimatedVarLoadPointID(entity.get_estimatedVarLoadPointId());
        msg.setEstimatedVarLoadPointValue(entity.get_estimatedVarLoadPointValue());
        msg.setLastCurrentVarPointUpdateTime(ConverterHelper.millisecToDate(entity.get_lastCurrentVarPointUpdateTime()));
        msg.setLastOperationTime(ConverterHelper.millisecToDate(entity.get_lastOperationTime()));
        msg.setLikeDayControlFlag(entity.is_likeDayControlFlag());
        msg.setMapLocationID(entity.get_mapLocationId());
        msg.setMaxDailyOperation(entity.get_strategy_maxDailyOperation());
        msg.setMaxOperationDisableFlag(entity.is_strategy_maxOperationDisableFlag());
        msg.setNewPointDataReceivedFlag(entity.is_newPointDataReceivedFlag());
        msg.setOffpeakPFSetPoint(entity.get_strategy_offPeakPFSetPoint());
        msg.setOffPkLag(entity.get_strategy_offPeakLag());
        msg.setOffPkLead(entity.get_strategy_OffPeakLead());
        msg.setOriginalParentId(entity.get_originalParentId());
        msg.setOvUvDisabledFlag(entity.is_ovUvDisabledFlag());
        msg.setPeakLag(entity.get_strategy_peakLag());
        msg.setPeakLead(entity.get_strategy_PeakLead());
        msg.setPeakPFSetPoint(entity.get_strategy_peakPFSetPoint());
        msg.setPeakTimeFlag(entity.is_peakTimeFlag());
        msg.setPhaseA(entity.get_phaseAValue());
        msg.setPhaseB(entity.get_phaseBValue());
        msg.setPhaseC(entity.get_phaseCValue());
        msg.setPowerFactorPointID(entity.get_powerFactorPointId());
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
        throw new UnsupportedOperationException("Message serialization not supported");
    }

}
