package com.cannontech.dr.nest.service;

import java.util.Date;
import java.util.Optional;

import com.cannontech.dr.nest.model.v3.SchedulabilityError;
import com.cannontech.stars.dr.account.model.CustomerAccount;

public interface NestService {

    /**
     * Sends message to Nest to update group
     */
    Optional<String> updateGroup(int customerId, String accountNumber, String newGroup);

    /**
     * Sends message to Nest to remove account
     */
    Optional<String> dissolveAccountWithNest(CustomerAccount account);

    /**
     * Sends control message to Nest
     * 
     * @throws NestException if more then 1 group is assigned to the program
     */
    Optional<SchedulabilityError> scheduleControl(int programId, int gearId, Date startTime, Date stopTime);

    /**
     * Returns true if the programId is of the Nest program, the program and the group are enabled
     * 
     * @throws NestException if more then 1 group is assigned to the program
     */
    boolean isEnabledNestProgramWithEnabledGroup(int programId);

    /**
     * Sends message to Nest to stop or cancel control
     * 
     * @throws NestException if more then 1 group is assigned to the program
     */
    String stopControlForProgram(int programId);
    
    /**
     * Sends message to Nest to stop or cancel control
     * 
     * @throws NestException if more then 1 group is assigned to the program
     */
    String stopControlForGroup(String groupName);
}
