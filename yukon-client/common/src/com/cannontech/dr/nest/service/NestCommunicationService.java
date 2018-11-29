package com.cannontech.dr.nest.service;

import java.util.List;
import java.util.Optional;

import com.cannontech.dr.nest.model.NestControlEvent;
import com.cannontech.dr.nest.model.NestURL;
import com.cannontech.dr.nest.model.v3.ControlEvent;
import com.cannontech.dr.nest.model.v3.CustomerEnrollment;
import com.cannontech.dr.nest.model.v3.CustomerInfo;
import com.cannontech.dr.nest.model.v3.EnrollmentState;
import com.cannontech.dr.nest.model.v3.RushHourEventType;
import com.cannontech.dr.nest.model.v3.SchedulabilityError;

public interface NestCommunicationService{

    /**
     * Sends message to Nest to cancel event
     */
    Optional<String> cancelEvent(NestControlEvent history);

    /**
     * Sends message to Nest to stop event
     */
    Optional<String> stopEvent(NestControlEvent history);
    
    /**
     * Sends message to Nest to start event, used by simulator to show result on the screen without starting control 
     */
    String getNestResponse(String url, ControlEvent event, RushHourEventType type);

    // this method will be removed from interface 
    boolean useProxy(String stringUrl);

    // this method will be removed from interface 
    String encodeAuthorization();

    /**
     * Retrieves all customers from Nest for the state provided
     */
    List<CustomerInfo> retrieveCustomers(EnrollmentState state);

    /**
     * Updates enrollment: dissolves account of changes group 
     */
    Optional<String> updateEnrollment(CustomerEnrollment enrollment);

    /**
     * Contracts Nest url for a specific version
     */
    String constructNestUrl(int version, NestURL url);

    /**
     * Contracts Nest url for a specific version
     */
    String constructNestUrl(NestURL url);

    /**
     * Sends message to Nest to start control
     */
    Optional<SchedulabilityError> sendEvent(ControlEvent event, RushHourEventType type);
}
