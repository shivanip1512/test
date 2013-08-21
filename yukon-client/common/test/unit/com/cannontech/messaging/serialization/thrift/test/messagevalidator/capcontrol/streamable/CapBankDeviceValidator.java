package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol.streamable;

import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class CapBankDeviceValidator extends AutoInitializedClassValidator<CapBankDevice> {

    public CapBankDeviceValidator() {
        super(CapBankDevice.class);
    }

    @Override
    public void populateExpectedValue(CapBankDevice ctrlObj, RandomGenerator generator) {
        ctrlObj.setParentID(generator.generateInt());
        ctrlObj.setMaxDailyOperation(generator.generateInt());
        ctrlObj.setMaxOperationDisableFlag(generator.generateBoolean());
        ctrlObj.setAlarmInhibit(ConverterHelper.boolToInt(generator.generateBoolean()));
        ctrlObj.setControlInhibit(ConverterHelper.boolToInt(generator.generateBoolean()));
        ctrlObj.setOperationalState(generator.generateString());
        ctrlObj.setControllerType(generator.generateString());
        ctrlObj.setControlDeviceID(generator.generateInt());
        ctrlObj.setBankSize(generator.generateInt());
        ctrlObj.setTypeOfSwitch(generator.generateString());
        ctrlObj.setSwitchManufacture(generator.generateString());
        ctrlObj.setMapLocationID(generator.generateString());
        ctrlObj.setRecloseDelay(generator.generateInt());
        ctrlObj.setControlOrder(generator.generateFloat());
        ctrlObj.setStatusPointID(generator.generateInt());
        ctrlObj.setControlStatus(generator.generateInt());
        ctrlObj.setOperationAnalogPointID(generator.generateInt());
        ctrlObj.setTotalOperations(generator.generateInt());
        ctrlObj.setLastStatusChangeTime(generator.generateDate());
        ctrlObj.setTagControlStatus(generator.generateInt());
        ctrlObj.setOrigFeederID(generator.generateInt());   
        ctrlObj.setCurrentDailyOperations(generator.generateInt());
        ctrlObj.setIgnoreFlag(generator.generateBoolean());
        ctrlObj.setIgnoreReason(generator.generateInt());
        ctrlObj.setOvUVDisabled(generator.generateBoolean());
        ctrlObj.setTripOrder(generator.generateFloat());
        ctrlObj.setCloseOrder(generator.generateFloat());
        ctrlObj.setControlDeviceType(generator.generateString());
        ctrlObj.setBeforeVars(generator.generateString());
        ctrlObj.setAfterVars(generator.generateString());
        ctrlObj.setPercentChange(generator.generateString());
        ctrlObj.setMaxDailyOperationHitFlag(generator.generateBoolean());
        ctrlObj.setOvuvSituationFlag(generator.generateBoolean());
        ctrlObj.setControlStatusQuality(generator.generateInt());
        ctrlObj.setLocalControlFlag(generator.generateBoolean());
        ctrlObj.setPartialPhaseInfo(generator.generateString());
    }
}
