package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol.streamable;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class SubBusValidator extends AutoInitializedClassValidator<SubBus> {

    public SubBusValidator() {
        super(SubBus.class);
    }

    @Override
    public void populateExpectedValue(SubBus ctrlObj, RandomGenerator generator) {
        ctrlObj.setParentID(generator.generateInt());
        ctrlObj.setCurrentVarLoadPointID(generator.generateInt());
        ctrlObj.setCurrentVarLoadPointValue(generator.generateDouble());
        ctrlObj.setCurrentWattLoadPointID(generator.generateInt());
        ctrlObj.setCurrentWattLoadPointValue(generator.generateDouble());
        ctrlObj.setMapLocationID(generator.generateString());
        ctrlObj.setDecimalPlaces(generator.generateInt());
        ctrlObj.setNewPointDataReceivedFlag(generator.generateBoolean());
        ctrlObj.setBusUpdateFlag(generator.generateBoolean());
        ctrlObj.setLastCurrentVarPointUpdateTime(generator.generateDate());
        ctrlObj.setEstimatedVarLoadPointID(generator.generateInt());
        ctrlObj.setEstimatedVarLoadPointValue(generator.generateDouble());
        ctrlObj.setDailyOperationsAnalogPointId(generator.generateInt());
        ctrlObj.setPowerFactorPointId(generator.generateInt());
        ctrlObj.setEstimatedPowerFactorPointId(generator.generateInt());
        ctrlObj.setCurrentDailyOperations(generator.generateInt());
        ctrlObj.setPeakTimeFlag(generator.generateBoolean());
        ctrlObj.setRecentlyControlledFlag(generator.generateBoolean());
        ctrlObj.setLastOperationTime(generator.generateDate());
        ctrlObj.setVarValueBeforeControl(generator.generateDouble());
        ctrlObj.setPowerFactorValue(generator.generateDouble());
        ctrlObj.setEstimatedPFValue(generator.generateDouble());
        ctrlObj.setCurrentVarPtQuality(generator.generateInt());
        ctrlObj.setWaiveControlFlag(generator.generateBoolean());
        ctrlObj.setCurrentVoltLoadPointID(generator.generateInt());
        ctrlObj.setCurrentVoltLoadPointValue(generator.generateDouble());
        ctrlObj.setVerificationFlag(generator.generateBoolean());
        ctrlObj.setSwitchOverStatus(generator.generateBoolean());
        ctrlObj.setCurrentwattpointquality(generator.generateInt());
        ctrlObj.setCurrentvoltpointquality(generator.generateInt());
        ctrlObj.setTargetvarvalue(generator.generateDouble());
        ctrlObj.setSolution(generator.generateString());
        ctrlObj.setOvUvDisabledFlag(generator.generateBoolean());
        ctrlObj.setPhaseA(generator.generateDouble());
        ctrlObj.setPhaseB(generator.generateDouble());
        ctrlObj.setPhaseC(generator.generateDouble());
        ctrlObj.setLikeDayControlFlag(generator.generateBoolean());
        ctrlObj.setDisplayOrder(generator.generateInt());
        ctrlObj.setVoltReductionFlag(generator.generateBoolean());
        ctrlObj.setUsePhaseData(generator.generateBoolean());        
        ctrlObj.setAlternateBusId(generator.generateInt());
        ctrlObj.setDualBusEnabled(generator.generateBoolean());

        ctrlObj.setCcFeeders(getDefaultObjectVectorFor(Feeder.class, generator));

        ctrlObj.setStrategyId(0);   // Hardcoded on C++ side to be able to generate strategies fields
        
        // Strategies
        ctrlObj.setMaxDailyOperation(generator.generateInt());
        ctrlObj.setMaxOperationDisableFlag(generator.generateBoolean());
        ctrlObj.setAlgorithm(generator.generateEnum(ControlAlgorithm.class));
        ctrlObj.setControlMethod(generator.generateEnum(ControlMethod.class));
        ctrlObj.setPeakLag(generator.generateDouble());
        ctrlObj.setOffPkLag(generator.generateDouble());
        ctrlObj.setPeakLead(generator.generateDouble());
        ctrlObj.setOffPkLead(generator.generateDouble());
        ctrlObj.setPeakPFSetPoint(generator.generateDouble());
        ctrlObj.setOffpeakPFSetPoint(generator.generateDouble());
        
        ignoreField("primaryBusFlag");

    }
}
