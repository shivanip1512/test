package com.cannontech.dr.nest.service;

import com.cannontech.dr.nest.model.NestSyncTimeInfo;

public interface NestSyncService {

    /**
     * Attempts to sync Yukon and Nest
     * 
     * 
     * Sync Load Groups
       If group exists in Nest file but not in Yukon:
         Create a Load Group in Yukon with the same name as in Nest.
         
       If Nest group exists in Yukon but not in Nest:
          Flag the group as “Manual” fix, notify the user that he should clean up the Groups by either setting up the same group in Nest or deleting the group from Yukon.
       
       If lm group that is not of a type "Nest" exists in Yukon with the same name as a group in Nest:
          Flag the group as “Manual” fix, notify the user to change the group name 
           
       If Load Programs/Areas are not setup correctly 
          Flag the group as “Manual” fix, notify the user to set up programs  
       
       
       
       
       
       //todo
       Sync Accounts
            If the account is in the Nest file but not in Yukon, create account in Yukon.
            If the address on the Yukon account is missing, and the account was found in Nest file, update the Yukon account with the Nest address.
            Un-enroll and delete all the Nest devices from Yukon accounts if account number is not in the Nest file.
       Sync Thermostats
            From the Nest file parse the thermostats serial numbers from WINTER_RHR and SUMMER_RHR
            If account is assigned to the group and the thermostat with the same serial number doesn’t exist in Yukon:
            -   If the enrollment is possible (Program/Control Area is setup correctly), create the Nest thermostat and enroll it in the program.
            -   Otherwise, Flag the  group as “discrepancy” to notify the user that he should set up Program/Control Area etc.
       Sync Enrollment
            Group in Nest file is different than the group in Yukon
            -   If the enrollment is possible (Program/Control Area is setup correctly), enroll thermostat in the program with the Load Group from the Nest File.
            -   Otherwise, Flag the group as “Manual fix” to notify the user that he should set up Program/Control Area etc.
     * 
     * The results of the sync are stored in the table marked at "AUTO" or "MANUAL" fix.
     * 
     * If forceSync is true ignores the 15 minute delay between syncs and runs the sync right away.
     * ForceSync = true should be only used for testing.
     */
    void sync(boolean forceSync);
    /**
     * Returns the sync time information
     */
    NestSyncTimeInfo getSyncTimeInfo();
}
