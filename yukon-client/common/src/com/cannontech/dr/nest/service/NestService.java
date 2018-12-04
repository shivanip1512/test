package com.cannontech.dr.nest.service;

import java.util.Date;
import java.util.Optional;

import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.dr.nest.model.NestStopEventResult;
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
    Optional<String> dissolveAccountWithNest(LiteCustomer account, String accountNumber);

    /**
     * Sends control message to Nest
     * 
     * Currently adjustStopTime is set to false when one program is started at a time, as UI
     * validates that the end date is filled out and Nest will error out if duration exceeds maximum time
     * allowed, the error will be displayed to the user,
     * 
     * If control is started from control area or scenario the adjustStopTime is true, if duration between
     * stop and start time more then 4 hours or null the stopTime will be set to startTime + 4 hours.
     * 
     * @param programId
     * @param gearId
     * @param startTime - time to start control
     * @param stopTime - time to stop control, if adjustStopTime is true, stopTime will be modified as described below
     * @param adjustStopTime - if true checks that duration between stop and start time is 4 hours or less, if
     *        stop time is null or duration is greater then 4 hours adjusts the duration to be 4 hours
     * @return SchedulabilityError - error received from Nest
     * @throws NestException if more then 1 group is assigned to the program or if stopTime is null and adjustStopTime is false
     */
    Optional<SchedulabilityError> scheduleControl(int programId, int gearId, Date startTime, Date stopTime,
            boolean adjustStopTime);

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
     * @return NestStopEventResult
     *              isStopPossible is false - request was not sent to Nest because the control is finished or we already sent a request
     *              isSuccess is false - got an error from Nest
     *              nestResponse - failure response from Nest 
     */
    NestStopEventResult stopControlForProgram(int programId);
    
    /**
     * Sends message to Nest to stop or cancel control
     * 
     * @throws NestException if more then 1 group is assigned to the program
     * @return NestStopEventResult
     *              isStopPossible is false - request was not sent to Nest because the control is finished or we already sent a request
     *              isSuccess is false - got an error from Nest
     *              nestResponse - failure response from Nest 
     */
    NestStopEventResult stopControlForGroup(String groupName);
}
