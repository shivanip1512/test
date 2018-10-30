package com.cannontech.dr.nest.model;

import static com.cannontech.dr.nest.model.NestSyncI18nValue.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public enum NestSyncI18nKey {
    /**NestSyncI18nValues must be in the order in which they should display in the user message in demandResponse.xml**/
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
    ENROLLED_THERMOSTAT(SERIAL_NUMBER, ACCOUNT_NUMBER, GROUP, PROGRAM),
    NOT_NEST_THERMOSTAT(SERIAL_NUMBER),
    MANUALLY_DELETE_THERMOSTAT_FROM_YUKON(SERIAL_NUMBER),
    NOT_FOUND_ACCOUNT(ACCOUNT_NUMBER),
    AUTO_CREATED_ACCOUNT(ACCOUNT_NUMBER),
    NOT_FOUND_ADDRESS(ACCOUNT_NUMBER),
    AUTO_CREATED_ADDRESS(ACCOUNT_NUMBER),
    AUTO_CHANGE_GROUP(SERIAL_NUMBER, ACCOUNT_NUMBER, GROUP, PROGRAM, GROUP_FROM),
    THERMOSTAT_IN_WRONG_ACCOUNT(SERIAL_NUMBER, ACCOUNT_NUMBER),
    DELETE_THERMOSTAT(SERIAL_NUMBER, ACCOUNT_NUMBER),
    ;
    
    private List<NestSyncI18nValue> valueKeys = new ArrayList<>();
    
    private NestSyncI18nKey(NestSyncI18nValue... values) {
        valueKeys.addAll(Lists.newArrayList(values));
    }
    
    public List<NestSyncI18nValue> getValueKeys(){
        return Collections.unmodifiableList(valueKeys);
    }
}
