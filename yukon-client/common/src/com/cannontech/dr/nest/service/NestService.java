package com.cannontech.dr.nest.service;

import java.util.Optional;

import org.joda.time.Instant;

import com.cannontech.dr.nest.model.NestError;
import com.cannontech.dr.nest.model.NestUploadInfo;

public interface NestService {

    /**
     * 1. Downloads Nest existing file
     * 2. Finds the account number
     * 3. Replaces the group with a new group
     * 4. Uploads file
     * @return information about successes and errors
     */
    NestUploadInfo updateGroup(String accountNumber, String newGroup);

    /**
     * 1. Downloads Nest existing file
     * 2. Finds the account number
     * 3. Marks the account as "Dissolved" (account will be removed from the Nest file after it is uploaded)
     * 4. Uploads file
     * @return information about successes and errors
     */
    NestUploadInfo dissolveAccountWithNest(String accountNumber);

    /**
     * 1. Finds all Nest groups for the program
     * 2. Sends control request
     * @return optional error we got from Nest
     */
    Optional<NestError> control(int programId, int gearId, Instant startTime, Instant stopTime);

    /**
     * 1. Finds all Nest groups for the program
     * 2. Sends stop control request for each group
     */
    void stopControl(int programId);
}
