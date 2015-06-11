package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol.streamable;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class FeederValidator extends AutoInitializedClassValidator<Feeder> {

    public FeederValidator() {
        super(Feeder.class);
    }

    @Override
    public void populateExpectedValue(Feeder ctrlObj, RandomGenerator generator) {

        ctrlObj.setParentID(generator.generateInt());

     

        ctrlObj.setCurrentVarLoadPointID(generator.generateInt());
        ctrlObj.setCurrentVarLoadPointValue(generator.generateDouble());
        ctrlObj.setCurrentWattLoadPointID(generator.generateInt());
        ctrlObj.setCurrentWattLoadPointValue(generator.generateDouble());
        ctrlObj.setMapLocationID(generator.generateString());
        ctrlObj.setDisplayOrder(generator.generateFloat());
        ctrlObj.setNewPointDataReceivedFlag(generator.generateBoolean());
        ctrlObj.setLastCurrentVarPointUpdateTime(generator.generateDate());
        ctrlObj.setEstimatedVarLoadPointID(generator.generateInt());
        ctrlObj.setEstimatedVarLoadPointValue(generator.generateDouble());
        ctrlObj.setDailyOperationsAnalogPointID(generator.generateInt());
        ctrlObj.setPowerFactorPointID(generator.generateInt());
        ctrlObj.setEstimatedPowerFactorPointID(generator.generateInt());
        ctrlObj.setCurrentDailyOperations(generator.generateInt());
        ctrlObj.setRecentlyControlledFlag(generator.generateBoolean());
        ctrlObj.setLastOperationTime(generator.generateDate());
        ctrlObj.setVarValueBeforeControl(generator.generateDouble());
        ctrlObj.setPowerFactorValue(generator.generateDouble());
        ctrlObj.setEstimatedPFValue(generator.generateDouble());
        ctrlObj.setCurrentVarPtQuality(generator.generateInt());
        ctrlObj.setWaiveControlFlag(generator.generateBoolean());
      

        ctrlObj.setDecimalPlaces(generator.generateInt());
        ctrlObj.setPeakTimeFlag(generator.generateBoolean());
        ctrlObj.setCurrentVoltLoadPointID(generator.generateInt());
        ctrlObj.setCurrentVoltLoadPointValue(generator.generateDouble());
        ctrlObj.setCurrentwattpointquality(generator.generateInt());
        ctrlObj.setCurrentvoltpointquality(generator.generateInt());
        ctrlObj.setTargetvarvalue(generator.generateDouble());
        ctrlObj.setSolution(generator.generateString());
        ctrlObj.setOvUvDisabledFlag(generator.generateBoolean());
      
        
        ctrlObj.setPhaseA(generator.generateDouble());
        ctrlObj.setPhaseB(generator.generateDouble());
        ctrlObj.setPhaseC(generator.generateDouble());
        ctrlObj.setLikeDayControlFlag(generator.generateBoolean());
        ctrlObj.setUsePhaseData(generator.generateBoolean());
        ctrlObj.setOriginalParentId(generator.generateInt());

        ctrlObj.setCcCapBanks(getDefaultObjectVectorFor(CapBankDevice.class, generator, 1));
        
        // Strategies
        ctrlObj.setMaxDailyOperation(generator.generateInt());
        ctrlObj.setMaxOperationDisableFlag(generator.generateBoolean());
        ctrlObj.setAlgorithm(generator.generateEnum(ControlAlgorithm.class));
        ctrlObj.setControlmethod(generator.generateEnum(ControlMethod.class));
        ctrlObj.setPeakLag(generator.generateDouble());
        ctrlObj.setOffPkLag(generator.generateDouble());
        ctrlObj.setPeakLead(generator.generateDouble());        
        ctrlObj.setOffPkLead(generator.generateDouble());
        ctrlObj.setPeakPFSetPoint(generator.generateDouble());
        ctrlObj.setOffpeakPFSetPoint(generator.generateDouble());
    }
}
