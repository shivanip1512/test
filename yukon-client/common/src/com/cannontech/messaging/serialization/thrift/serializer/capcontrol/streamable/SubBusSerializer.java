package com.cannontech.messaging.serialization.thrift.serializer.capcontrol.streamable;

import org.apache.commons.lang3.EnumUtils;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCPao;
import com.cannontech.messaging.serialization.thrift.generated.CCSubstationBusItem;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class SubBusSerializer extends
    ThriftInheritanceSerializer<SubBus, StreamableCapObject, CCSubstationBusItem, CCPao> {

    public SubBusSerializer(String messageType, StreamableSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<SubBus> getTargetMessageClass() {
        return SubBus.class;
    }

    @Override
    protected CCSubstationBusItem createThriftEntityInstance(CCPao entityParent) {
        CCSubstationBusItem entity = new CCSubstationBusItem();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCPao getThriftEntityParent(CCSubstationBusItem entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected SubBus createMessageInstance() {
        return new SubBus();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCSubstationBusItem entity,
                                                   SubBus msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        // For legacy reasons, the parentId is defined at this level in the Thrift Entity.
        // It is therefore here that the parent field "parentId" is set populated in this object
        msg.setParentID(entity.get_parentId());

        msg.setMaxDailyOperation(entity.get_strategy_maxDailyOperation());
        msg.setMaxOperationDisableFlag(entity.is_strategy_maxOperationDisableFlag());
        msg.setCurrentVarLoadPointID(entity.get_currentVarLoadPointId());
        msg.setCurrentVarLoadPointValue(entity.get_varValue());
        msg.setCurrentWattLoadPointID(entity.get_currentWattLoadPointId());
        msg.setCurrentWattLoadPointValue(entity.get_wattValue());
        msg.setMapLocationID(entity.get_mapLocationId());
        msg.setAlgorithm(ControlAlgorithm.valueOf(entity.get_strategy_controlUnits()));
        msg.setDecimalPlaces(entity.get_decimalPlaces());
        msg.setNewPointDataReceivedFlag(entity.is_newPointDataReceivedFlag());
        msg.setBusUpdateFlag(entity.is_busUpdatedflag());
        msg.setLastCurrentVarPointUpdateTime(ConverterHelper.millisecToDate(entity.get_lastCurrentVarPointUpdateTime()));
        msg.setEstimatedVarLoadPointID(entity.get_estimatedVarLoadPointId());
        msg.setEstimatedVarLoadPointValue(entity.get_estimatedVarLoadPointValue());
        msg.setDailyOperationsAnalogPointId(entity.get_dailyOperationsAnalogPointId());
        msg.setPowerFactorPointId(entity.get_powerFactorPointId());
        msg.setEstimatedPowerFactorPointId(entity.get_estimatedPowerFactorPointId());
        msg.setCurrentDailyOperations(entity.get_currentDailyOperations());
        msg.setPeakTimeFlag(entity.is_peakTimeFlag());
        msg.setRecentlyControlledFlag(entity.is_recentlyControlledFlag());
        msg.setLastOperationTime(ConverterHelper.millisecToDate(entity.get_lastOperationTime()));
        msg.setVarValueBeforeControl(entity.get_varValueBeforeControl());
        msg.setPowerFactorValue(entity.get_powerFactorValue());
        msg.setEstimatedPFValue(entity.get_estimatedPowerFactorValue());
        msg.setCurrentVarPtQuality(entity.get_currentVarPointQuality());
        msg.setWaiveControlFlag(entity.is_waiveControlFlag());
        msg.setPeakLag(entity.get_strategy_peakLag());
        msg.setOffPkLag(entity.get_strategy_offPeakLag());
        msg.setPeakLead(entity.get_strategy_peakLead());
        msg.setOffPkLead(entity.get_strategy_offPeakLead());
        msg.setCurrentVoltLoadPointID(entity.get_currentVoltLoadPointId());
        msg.setCurrentVoltLoadPointValue(entity.get_voltValue());
        msg.setVerificationFlag(entity.is_verificationFlag());
        msg.setSwitchOverStatus(entity.is_switchOverStatus());
        msg.setCurrentwattpointquality(entity.get_currentWattPointQuality());
        msg.setCurrentvoltpointquality(entity.get_currentVoltPointQuality());
        msg.setTargetvarvalue(entity.get_targetVarValue());
        msg.setSolution(entity.get_solution());
        msg.setOvUvDisabledFlag(entity.is_ovUvDisabledFlag());
        msg.setPeakPFSetPoint(entity.get_strategy_peakPFSetPoint());
        msg.setOffpeakPFSetPoint(entity.get_strategy_offPeakPFSetPoint());
        
        ControlMethod controlMethod = EnumUtils.getEnum(ControlMethod.class, entity.get_strategy_controlMethod());
        msg.setControlMethod(controlMethod);
        msg.setPhaseA(entity.get_phaseAValue());
        msg.setPhaseB(entity.get_phaseBValue());
        msg.setPhaseC(entity.get_phaseCValue());
        msg.setLikeDayControlFlag(entity.is_likeDayControlFlag());
        msg.setDisplayOrder(entity.get_displayOrder());
        msg.setVoltReductionFlag(entity.is_voltReductionFlag());
        msg.setUsePhaseData(entity.is_usePhaseData());
        msg.setPrimaryBusFlag(entity.is_primaryBusFlag());
        msg.setAlternateBusId(entity.get_altSubId());
        msg.setDualBusEnabled(entity.is_dualBusEnabled());
        msg.setStrategyId(entity.get_strategyId());
        msg.setCcFeeders(helper.convertToMessageVector(entity.get_ccFeeders(), Feeder.class));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, SubBus msg,
                                                   CCSubstationBusItem entity) {
        throw new UnsupportedOperationException("Message serialization not supported");
    }

}
