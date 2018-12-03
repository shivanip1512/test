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
