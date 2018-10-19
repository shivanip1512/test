package com.cannontech.dr.nest.model;

import static com.cannontech.dr.nest.model.NestSyncI18nValue.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public enum NestSyncI18nKey {
    FOUND_GROUP_ONLY_IN_NEST(GROUP), 
    FOUND_GROUP_ONLY_IN_YUKON(GROUP), 
    FOUND_NON_NEST_GROUP_IN_YUKON_WITH_THE_NEST_GROUP_NAME(GROUP),
    AUTO_CREATED_GROUP_IN_YUKON(GROUP), 
    MANUALLY_DELETE_GROUP_FROM_YUKON(GROUP),
    MODIFY_YUKON_GROUP_NOT_TO_CONFLICT_WITH_NEST_GROUP(GROUP),
    NOT_FOUND_PROGRAM_FOR_NEST_GROUP(GROUP),
    NOT_FOUND_AREA_FOR_NEST_GROUP(GROUP),
    SETUP_PROGRAM_AND_AREA_CORRECTLY_FOR_NEST_GROUP(GROUP),
    NOT_FOUND_THERMOSTAT(SERIAL_NUMBER, ACCOUNT_NUMBER),
    AUTO_CREATED_THERMOSTAT(SERIAL_NUMBER, ACCOUNT_NUMBER),
    NOT_ENROLLED_THERMOSTAT(SERIAL_NUMBER, ACCOUNT_NUMBER),
    //Device {serialNumber} enrolled in program {programName} [{groupName}] for account {accountNumber}
    ENROLLED_THERMOSTAT(SERIAL_NUMBER, ACCOUNT_NUMBER, GROUP, PROGRAM);
    
    private List<NestSyncI18nValue> valueKeys = new ArrayList<>();
    
    private NestSyncI18nKey(NestSyncI18nValue... values) {
        valueKeys.addAll(Lists.newArrayList(values));
    }
    
    public List<NestSyncI18nValue> getValueKeys(){
        return Collections.unmodifiableList(valueKeys);
    }
}
