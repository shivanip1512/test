package com.cannontech.dr.nest.service;

import com.cannontech.dr.nest.model.NestSyncTimeInfo;

public interface NestSyncService {

    /**
     * Attempts to sync Yukon and Nest
     *  Test Action                                                                                         Reason                                                  Action
        
        No Nest Type in device list     Exception in WS log, sync doesn't run   
                    
        Group 'A' only in Yukon                                                                     MANUAL  FOUND_GROUP_ONLY_IN_YUKON                               MANUALLY_DELETE_GROUP_FROM_YUKON
        Group 'B' in Nest and in Yukon. In Yukon this is an Ecobee Group.                           MANUAL  FOUND_NON_NEST_GROUP_IN_YUKON_WITH_THE_NEST_GROUP_NAME  MODIFY_YUKON_GROUP_NOT_TO_CONFLICT_WITH_NEST_GROUP
        Group 'C' in Nest but not in Yukon                                                          AUTO    FOUND_GROUP_ONLY_IN_NEST                                AUTO_CREATED_GROUP_IN_YUKON
        Group 'D' is in sync but program is not setup                                               MANUAL  NOT_FOUND_PROGRAM_FOR_NEST_GROUP                        SETUP_PROGRAM_AND_AREA_CORRECTLY_FOR_NEST_GROUP
        Group 'E' is in sync but area is not setup                                                  MANUAL  NOT_FOUND_AREA_FOR_NEST_GROUP                           SETUP_PROGRAM_AND_AREA_CORRECTLY_FOR_NEST_GROUP
        Thermostat '101' is Nest and in Yukon, but in Yukon it is an Ecobee Thermostat              MANUAL  NOT_NEST_THERMOSTAT                                     MANUALLY_DELETE_THERMOSTAT_FROM_YUKON
        Thermostat '102' is a Nest Thermostat which belongs to account that is not In the Nest      AUTO    THERMOSTAT_IN_ACCOUNT_WHICH_IS_NOT_IN_NEST              AUTO_DELETED_THERMOSTAT
        Account with serialNumber '1' is in Nest but not in Yukon                                   AUTO    NOT_FOUND_ACCOUNT                                       AUTO_CREATED_ACCOUNT
        Account with serialNumber '2' is in sync but Yukon doesn't have address                     AUTO    NOT_FOUND_ADDRESS                                       AUTO_CREATED_ADDRESS
        Thermostat '101' is in Nest under accountNumber '1' and not in Yukon                        AUTO    NOT_FOUND_THERMOSTAT                                    AUTO_CREATED_THERMOSTAT
        Thermostat '102' is in Nest under accountNumber '1' and  in Yukon under accountNumber '2'   AUTO    THERMOSTAT_IN_WRONG_ACCOUNT                             AUTO_DELETED_THERMOSTAT
                                                                                                    AUTO    NOT_FOUND_THERMOSTAT                                    AUTO_CREATED_THERMOSTAT
        Thermostat '101' is not enrolled, group 'F' in Nest                                         AUTO    NOT_ENROLLED_THERMOSTAT                                 AUTO_ENROLLED_THERMOSTAT
        Thermostat '102' is enrolled in group 'F' in Yukon, group 'G' in Nest                       AUTO    CHANGE_GROUP                                            AUTO_ENROLLED_THERMOSTAT
     */
    void sync(boolean forceSync);
    /**
     * Returns the sync time information
     */
    NestSyncTimeInfo getSyncTimeInfo();
}
