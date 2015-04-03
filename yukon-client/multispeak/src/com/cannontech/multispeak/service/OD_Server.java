package com.cannontech.multispeak.service;

import java.util.Calendar;
import java.util.List;

import com.cannontech.msp.beans.v3.Customer;
import com.cannontech.msp.beans.v3.DomainMember;
import com.cannontech.msp.beans.v3.DomainNameChange;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.ObjectRef;
import com.cannontech.msp.beans.v3.OutageDetectDeviceStatus;
import com.cannontech.msp.beans.v3.OutageDetectDeviceType;
import com.cannontech.msp.beans.v3.OutageDetectionDevice;
import com.cannontech.msp.beans.v3.OutageEvent;
import com.cannontech.msp.beans.v3.PhaseCd;
import com.cannontech.msp.beans.v3.RegistrationInfo;
import com.cannontech.msp.beans.v3.ServiceLocation;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;

public interface OD_Server {

    /**
     * Requester pings URL of OD to see if it is alive. Returns errorObject(s)
     * as necessary to communicate application status.
     * @throws MultispeakWebServiceException 
     */
    public void pingURL() throws MultispeakWebServiceException;

    /**
     * Requester requests list of methods supported by OD.
     */
    public List<String> getMethods() throws MultispeakWebServiceException;

    /**
     * Client requests server to update the status of an outageDetectionDevice.
     * OD responds by publishing a revised outageDetectionEvent (using the
     * ODEventNotification method on OA-OD) to the URL specified in the responseURL
     * parameter. OD returns information about failed transactions using
     * an array of errorObjects. The transactionID calling parameter is included
     * to link a returned ODEventNotification with this request.The expiration
     * time parameter indicates the amount of time for which the publisher
     * should try to obtain and publish the data; if the publisher has been
     * unsuccessful in publishing the data after the expiration time (specified
     * in seconds), then the publisher will discard the request and the requestor
     * should not expect a response.
     */
    
    public List<ErrorObject> initiateOutageDetectionEventRequest(List<String> meterNos, Calendar requestDate,
            String responseURL, String transactionID, Float expirationTime) throws MultispeakWebServiceException;
    
    /**
     * get domain names
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<String> getDomainNames() throws MultispeakWebServiceException;

    /**
     * get domain members
     * @param domainName
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<DomainMember> getDomainMembers(String domainName) throws MultispeakWebServiceException;

    /**
     * get all outage detection devices
     * @param lastReceived
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<OutageDetectionDevice> getAllOutageDetectionDevices(String lastReceived)
            throws MultispeakWebServiceException;

    /**
     * get outage detection devices by meterNo
     * @param meterNo
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<OutageDetectionDevice> getOutageDetectionDevicesByMeterNo(String meterNo)
            throws MultispeakWebServiceException;

    /**
     * get outage detection devices by status  
     * @param oDDStatus
     * @param lastReceived
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<OutageDetectionDevice> getOutageDetectionDevicesByStatus(OutageDetectDeviceStatus oDDStatus,
            String lastReceived) throws MultispeakWebServiceException;

    /**
     * get outage detection devices by type
     * @param oDDType
     * @param lastReceived
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<OutageDetectionDevice> getOutageDetectionDevicesByType(OutageDetectDeviceType oDDType,
            String lastReceived) throws MultispeakWebServiceException;

    /**
     * get outaged OD devices
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<OutageDetectionDevice> getOutagedODDevices() throws MultispeakWebServiceException;

    /**
     * modify OD data for outage detection device
     * @param oDDevice
     * @throws MultispeakWebServiceException
     */
    public void modifyODDataForOutageDetectionDevice(OutageDetectionDevice oDDevice)
            throws MultispeakWebServiceException;

    /**
     * display OD monitoring requests
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<ObjectRef> displayODMonitoringRequests() throws MultispeakWebServiceException;

    /**
     * cancel OD monitoring request by object
     * @param objectRef
     * @param requestDate
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<ErrorObject> cancelODMonitoringRequestByObject(List<ObjectRef> objectRef, Calendar requestDate)
            throws MultispeakWebServiceException;

    /**
     * initiate OD event request by object
     * @param objectName
     * @param nounType
     * @param phaseCode
     * @param requestDate
     * @param responseURL
     * @param transactionID
     * @param expirationTime
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<ErrorObject> initiateODEventRequestByObject(String objectName, String nounType, PhaseCd phaseCode,
            Calendar requestDate, String responseURL, String transactionID, float expirationTime)
            throws MultispeakWebServiceException;

    /**
     * initiate OD event request By serviceLocation
     * @param servLoc
     * @param requestDate
     * @param responseURL
     * @param transactionID
     * @param expirationTime
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<ErrorObject> initiateODEventRequestByServiceLocation(List<String> servLoc, Calendar requestDate,
            String responseURL, String transactionID, float expirationTime) throws MultispeakWebServiceException;

    /**
     * initiate OD monitoring request by object
     * @param objectName
     * @param nounType
     * @param phaseCode
     * @param periodicity
     * @param requestDate
     * @param responseURL
     * @param transactionID
     * @param expirationTime
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<ErrorObject> initiateODMonitoringRequestByObject(String objectName, String nounType, PhaseCd phaseCode,
            int periodicity, Calendar requestDate, String responseURL, String transactionID, float expirationTime)
            throws MultispeakWebServiceException;

    /**
     * customer changed notification
     * @param changedCustomers
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<ErrorObject> customerChangedNotification(List<Customer> changedCustomers) throws MultispeakWebServiceException;

    /**
     * meter changed notification
     * @param changedMeters
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<ErrorObject> meterChangedNotification(List<Meter> changedMeters) throws MultispeakWebServiceException;

    /**
     * service location changed notification
     * @param changedServiceLocations
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<ErrorObject> serviceLocationChangedNotification(List<ServiceLocation> changedServiceLocations)
            throws MultispeakWebServiceException;

    /**
     * request registration ID
     * @return
     * @throws MultispeakWebServiceException
     */
    public String requestRegistrationID() throws MultispeakWebServiceException;

    /**
     * register for service
     * @param registrationDetails
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<ErrorObject> registerForService(RegistrationInfo registrationDetails) throws MultispeakWebServiceException;

    /**
     * un register for service
     * @param registrationID
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<ErrorObject> unregisterForService(String registrationID) throws MultispeakWebServiceException;

    /**
     * get registration info by ID
     * @param registrationID
     * @return
     * @throws MultispeakWebServiceException
     */
    public RegistrationInfo getRegistrationInfoByID(String registrationID) throws MultispeakWebServiceException;

    /**
     * get publish methods
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<String> getPublishMethods() throws MultispeakWebServiceException;

    /**
     * domain members changed notification
     * @param changedDomainMembers
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<ErrorObject> domainMembersChangedNotification(List<DomainMember> changedDomainMembers)
            throws MultispeakWebServiceException;

    /**
     * domain names changed notification
     * @param changedDomainNames
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<ErrorObject> domainNamesChangedNotification(List<DomainNameChange> changedDomainNames)
            throws MultispeakWebServiceException;

    /**
     * outage event changed notification
     * @param oEvents
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<ErrorObject> outageEventChangedNotification(List<OutageEvent> oEvents) throws MultispeakWebServiceException;
}
