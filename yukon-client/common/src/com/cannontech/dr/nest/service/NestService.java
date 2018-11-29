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
     */
    Optional<SchedulabilityError> scheduleControl(int programId, int gearId, Date startTime, Date stopTime);

    /**
     * Sends message to Nest to stop or cancel control
     */
    String stopControl(int programId);

    /**
     * Returns true if the programId is of the Nest program
     */
    boolean isNestProgram(int programId);
}
