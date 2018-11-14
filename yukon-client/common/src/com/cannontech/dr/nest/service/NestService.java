package com.cannontech.dr.nest.service;

import java.util.Optional;

import org.joda.time.Instant;

import com.cannontech.dr.nest.model.v3.SchedulabilityError;
import com.cannontech.stars.dr.account.model.CustomerAccount;

public interface NestService {

    /**
     * Sends message to Nest to update group
     */
    Optional<String> updateGroup(int accountId, String accountNumber, String newGroup);

    /**
     * Sends message to Nest to remove account
     */
    Optional<String> dissolveAccountWithNest(CustomerAccount account);

    /**
     * Sends control message to Nest
     */
    Optional<SchedulabilityError> control(int programId, int gearId, Instant startTime, Instant stopTime);

    /**
     * Sends message to Nest to stop or cancel control
     */
    String stopControl(int programId);
}
