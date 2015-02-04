package com.cannontech.multispeak.service;

import com.cannontech.msp.beans.v3.Customer;
import com.cannontech.msp.beans.v3.DomainMember;
import com.cannontech.msp.beans.v3.DomainNameChange;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.LMDeviceExchange;
import com.cannontech.msp.beans.v3.LoadManagementDevice;
import com.cannontech.msp.beans.v3.LoadManagementEvent;
import com.cannontech.msp.beans.v3.PowerFactorManagementEvent;
import com.cannontech.msp.beans.v3.RegistrationInfo;
import com.cannontech.msp.beans.v3.ScadaAnalog;
import com.cannontech.msp.beans.v3.ScadaPoint;
import com.cannontech.msp.beans.v3.ScadaStatus;
import com.cannontech.msp.beans.v3.ServiceLocation;
import com.cannontech.msp.beans.v3.SubstationLoadControlStatus;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;

public interface LM_Server {

    /**
     * ping URL.
     * 
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public ErrorObject[] pingURL() throws MultispeakWebServiceException;

    /**
     * get Methods.
     * 
     * @return the methods
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public String[] getMethods() throws MultispeakWebServiceException;

    /**
     * SCADA Analog Changed Notification.
     * 
     * @param scadaAnalogs the scada analogs
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public ErrorObject[] SCADAAnalogChangedNotification(ScadaAnalog[] scadaAnalogs)
            throws MultispeakWebServiceException;

    /**
     * get All Substation Load Control Statuses.
     * 
     * @return the all substation load control statuses
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public SubstationLoadControlStatus[] getAllSubstationLoadControlStatuses() throws MultispeakWebServiceException;

    /**
     * initiate Load Management Event.
     * 
     * @param theLMEvent the the lm event
     * @return the error object
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public ErrorObject initiateLoadManagementEvent(LoadManagementEvent theLMEvent) throws MultispeakWebServiceException;

    /**
     * initiate Load Management Events.
     * 
     * @param theLMEvents the the lm events
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public ErrorObject[] initiateLoadManagementEvents(LoadManagementEvent[] theLMEvents)
            throws MultispeakWebServiceException;

    /**
     * initiate Load Management Events.
     * 
     * @param scadaAnalog the scada analog
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    void SCADAAnalogChangedNotificationByPointID(ScadaAnalog scadaAnalog) throws MultispeakWebServiceException;

    /**
     * initiate Load Management Events.
     * 
     * @param scadaAnalogs the scada analogs
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    ErrorObject[] SCADAAnalogChangedNotificationForPower(ScadaAnalog[] scadaAnalogs)
            throws MultispeakWebServiceException;

    /**
     * initiate Load Management Events.
     * 
     * @param scadaAnalogs the scada analogs
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    ErrorObject[] SCADAAnalogChangedNotificationForVoltage(ScadaAnalog[] scadaAnalogs)
            throws MultispeakWebServiceException;

    /**
     * initiate Load Management Events.
     * 
     * @param scadaPoints the scada points
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    ErrorObject[] SCADAPointChangedNotification(ScadaPoint[] scadaPoints) throws MultispeakWebServiceException;

    /**
     * initiate Load Management Events.
     * 
     * @param scadaPoints the scada points
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    ErrorObject[] SCADAPointChangedNotificationForAnalog(ScadaPoint[] scadaPoints) throws MultispeakWebServiceException;

    /**
     * initiate Load Management Events.
     * 
     * @param scadaPoints the scada points
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    ErrorObject[] SCADAPointChangedNotificationForStatus(ScadaPoint[] scadaPoints) throws MultispeakWebServiceException;

    /**
     * initiate Load Management Events.
     * 
     * @param scadaStatuses the scada statuses
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    ErrorObject[] SCADAStatusChangedNotification(ScadaStatus[] scadaStatuses) throws MultispeakWebServiceException;

    /**
     * initiate Load Management Events.
     * 
     * @param scadaStatus the scada status
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    void SCADAStatusChangedNotificationByPointID(ScadaStatus scadaStatus) throws MultispeakWebServiceException;

    /**
     * initiate Load Management Events.
     * 
     * @param changedCustomers the changed customers
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    ErrorObject[] customerChangedNotification(Customer[] changedCustomers) throws MultispeakWebServiceException;

    /**
     * initiate Load Management Events.
     * 
     * @param lastReceived the last received
     * @return the all load management devices
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    LoadManagementDevice[] getAllLoadManagementDevices(String lastReceived) throws MultispeakWebServiceException;

    /**
     * initiate Load Management Events.
     * 
     * @return the domain names
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    String[] getDomainNames() throws MultispeakWebServiceException;

    /**
     * initiate Load Management Events.
     * 
     * @param domainName the domain name
     * @return the domain members
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    DomainMember[] getDomainMembers(String domainName) throws MultispeakWebServiceException;

    /**
     * initiate Load Management Events.
     * 
     * @param addedLMDs the added lm ds
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    ErrorObject[] LMDeviceAddNotification(LoadManagementDevice[] addedLMDs) throws MultispeakWebServiceException;

    /**
     * initiate Load Management Events.
     * 
     * @param changedLMDs the changed lm ds
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    ErrorObject[] LMDeviceChangedNotification(LoadManagementDevice[] changedLMDs) throws MultispeakWebServiceException;

    /**
     * initiate Load Management Events.
     * 
     * @param LMDChangeout the LMD changeout
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    ErrorObject[] LMDeviceExchangeNotification(LMDeviceExchange[] LMDChangeout) throws MultispeakWebServiceException;

    /**
     * initiate Load Management Events.
     * 
     * @param removedLMDs the removed lm ds
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    ErrorObject[] LMDeviceRemoveNotification(LoadManagementDevice[] removedLMDs) throws MultispeakWebServiceException;

    /**
     * LM device retire notification.
     * 
     * @param retiredLMDs the retired lm ds
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    ErrorObject[] LMDeviceRetireNotification(LoadManagementDevice[] retiredLMDs) throws MultispeakWebServiceException;

    /**
     * Gets the amount of controllable load.
     * 
     * @return the amount of controllable load
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    float getAmountOfControllableLoad() throws MultispeakWebServiceException;

    /**
     * Gets the amount of controlled load.
     * 
     * @return the amount of controlled load
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    float getAmountOfControlledLoad() throws MultispeakWebServiceException;

    /**
     * Gets the load management device by meter number.
     * 
     * @param meterNo the meter no
     * @return the load management device by meter number
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    LoadManagementDevice[] getLoadManagementDeviceByMeterNumber(String meterNo) throws MultispeakWebServiceException;

    /**
     * Gets the load management device by serv loc.
     * 
     * @param servLoc the serv loc
     * @return the load management device by serv loc
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    LoadManagementDevice[] getLoadManagementDeviceByServLoc(String servLoc) throws MultispeakWebServiceException;

    /**
     * Initiate power factor management event.
     * 
     * @param thePFMEvent the the pfm event
     * @return the error object
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    ErrorObject initiatePowerFactorManagementEvent(PowerFactorManagementEvent thePFMEvent)
            throws MultispeakWebServiceException;

    /**
     * Checks if is load management active.
     * 
     * @param servLoc the serv loc
     * @return true, if is load management active
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    boolean isLoadManagementActive(String servLoc) throws MultispeakWebServiceException;

    /**
     * Service location changed notification.
     * 
     * @param changedServiceLocations the changed service locations
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    ErrorObject[] serviceLocationChangedNotification(ServiceLocation[] changedServiceLocations)
            throws MultispeakWebServiceException;

    /**
     * Request registration id.
     * 
     * @return the string
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    String requestRegistrationID() throws MultispeakWebServiceException;

    /**
     * Register for service.
     * 
     * @param registrationDetails the registration details
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    ErrorObject[] registerForService(RegistrationInfo registrationDetails) throws MultispeakWebServiceException;

    /**
     * Unregister for service.
     * 
     * @param registrationID the registration id
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    ErrorObject[] unregisterForService(String registrationID) throws MultispeakWebServiceException;

    /**
     * Gets the registration info by id.
     * 
     * @param registrationID the registration id
     * @return the registration info by id
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    RegistrationInfo getRegistrationInfoByID(String registrationID) throws MultispeakWebServiceException;

    /**
     * Gets the publish methods.
     * 
     * @return the publish methods
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    String[] getPublishMethods() throws MultispeakWebServiceException;

    /**
     * Domain members changed notification.
     * 
     * @param changedDomainMembers the changed domain members
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    ErrorObject[] domainMembersChangedNotification(DomainMember[] changedDomainMembers)
            throws MultispeakWebServiceException;

    /**
     * Domain names changed notification.
     * 
     * @param changedDomainNames the changed domain names
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    ErrorObject[] domainNamesChangedNotification(DomainNameChange[] changedDomainNames)
            throws MultispeakWebServiceException;
}
