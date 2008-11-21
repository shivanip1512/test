/**
 * LM_ServerSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public interface LM_ServerSoap_PortType extends java.rmi.Remote {

    /**
     * Requester pings URL of LM to see if it is alive.  Returns errorObject(s)
     * as necessary to communicate application status.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] pingURL() throws java.rmi.RemoteException;

    /**
     * Requester requests list of methods supported by LM.
     */
    public java.lang.String[] getMethods() throws java.rmi.RemoteException;

    /**
     * The client requests from the server a list of names of domains
     * supported by the server.  This method is used, along with the GetDomainMembers
     * method to enable systems to exchange information about application-specific
     * or installation-specific lists of information, such as the lists of
     * counties for this installation or the list of serviceStatusCodes used
     * by the server.
     */
    public java.lang.String[] getDomainNames() throws java.rmi.RemoteException;

    /**
     * The client requests from the server the members of a specific
     * domain of information, identified by the domainName parameter, which
     * are supported by the server.  This method is used, along with the
     * GetDomainNames method to enable systems to exchange information about
     * application-specific or installation-specific lists of information,
     * such as the lists of counties for this installation or the list of
     * serviceStatusCodes used by the server.
     */
    public com.cannontech.multispeak.deploy.service.DomainMember[] getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException;

    /**
     * Returns all required data for all loadManagementDevices.  The
     * calling parameter lastReceived is included so that large sets of data
     * can be returned in manageable blocks.  lastReceived should carry an
     * empty string the first time in a session that this method is invoked.
     * When multiple calls to this method are required to obtain all of the
     * data, the lastReceived should carry in subsequent calls the objectID
     * of the data instance noted by the server as being the lastSent. If
     * the sessionID parameter is set in the message header, then the server
     * shall respond as if it were being asked for a GetModifiedXXX since
     * that sessionID; if the sessionID is not included in the method call,
     * then all instances of the object shall be returned in response to
     * the call.
     */
    public com.cannontech.multispeak.deploy.service.LoadManagementDevice[] getAllLoadManagementDevices(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested loadManagementDevice(s), chosen by meter
     * number.
     */
    public com.cannontech.multispeak.deploy.service.LoadManagementDevice[] getLoadManagementDeviceByMeterNumber(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the requested loadManagementDevice(s), chosen by the
     * objectID of the service location (servLoc).
     */
    public com.cannontech.multispeak.deploy.service.LoadManagementDevice[] getLoadManagementDeviceByServLoc(java.lang.String servLoc) throws java.rmi.RemoteException;

    /**
     * Returns true if load management is in effect for a given serviceLocation
     * (chosen by objectID of serviceLocation (servLoc), otherwise false.
     */
    public boolean isLoadManagementActive(java.lang.String servLoc) throws java.rmi.RemoteException;

    /**
     * Returns the theoretical total amount of load that can be controlled,
     * expressed in kW.
     */
    public float getAmountOfControllableLoad() throws java.rmi.RemoteException;

    /**
     * Returns the amount of load that is currently under control,
     * expressed in kW.
     */
    public float getAmountOfControlledLoad() throws java.rmi.RemoteException;

    /**
     * Returns all of the substation load control statuses
     */
    public com.cannontech.multispeak.deploy.service.SubstationLoadControlStatus[] getAllSubstationLoadControlStatuses() throws java.rmi.RemoteException;

    /**
     * Publisher calls this LM service to initiate a load management
     * event via the loadManagementEvent object.  If substation and feeder
     * information is included in the loadManagementEvent, then the LM should
     * attempt to control the requested amount of load in that substation/feeder
     * area.  Otherwise, the controlled load should be spread over the entire
     * system. If this transaction should fail, LM returns information about
     * the failure using a SOAPFault.
     */
    public void intiateLoadManagementEvent(com.cannontech.multispeak.deploy.service.LoadManagementEvent theLMEvent) throws java.rmi.RemoteException;

    /**
     * Publisher calls this LM service to initiate an array of load
     * management events via multiple loadManagementEvent objects.  If substation
     * and feeder information is included in the loadManagementEvent, then
     * the LM should attempt to control the requested amount of load in that
     * substation/feeder area.  Otherwise, the controlled load should be
     * spread over the entire system. If this transaction should fail, LM
     * returns information about the failure using a SOAPFault.
     */
    public void intiateLoadManagementEvents(com.cannontech.multispeak.deploy.service.LoadManagementEvent[] theLMEvents) throws java.rmi.RemoteException;

    /**
     * Publisher calls this LM service to initiate a power factor
     * management event via the powerFactorManagementEvent object.  If this
     * transaction should fail, LM returns information about the failure
     * using a SOAPFault.
     */
    public void intiatePowerFactorManagementEvent(com.cannontech.multispeak.deploy.service.PowerFactorManagementEvent thePFMEvent) throws java.rmi.RemoteException;

    /**
     * Publisher notifies LM of a change in the Customer object(s)
     * by sending the changed customer object(s).  LM returns information
     * about failed transactions by returning an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] customerChangedNotification(com.cannontech.multispeak.deploy.service.Customer[] changedCustomers) throws java.rmi.RemoteException;

    /**
     * Publisher Notifies LM of a change in the Service Location object(s)
     * by sending the changed serviceLocation object(s).  LM returns information
     * about failed transactions by returning an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] serviceLocationChangedNotification(com.cannontech.multispeak.deploy.service.ServiceLocation[] changedServiceLocations) throws java.rmi.RemoteException;

    /**
     * Publisher notifies LM to add the associated load mangement
     * device(s). LM returns information about failed transactions using
     * an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceAddNotification(com.cannontech.multispeak.deploy.service.LoadManagementDevice[] addedLMDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies LM of a change in load mangement device(s)by
     * sending the changed loadManagementDevice object(s).  LM returns information
     * about failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceChangedNotification(com.cannontech.multispeak.deploy.service.LoadManagementDevice[] changedLMDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies LM that load mangement device(s) have been
     * deployed or exchanged.  LM returns information about failed transactions
     * in an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceExchangeNotification(com.cannontech.multispeak.deploy.service.LMDeviceExchange[] LMDChangeout) throws java.rmi.RemoteException;

    /**
     * Publisher notifies LM to remove the associated load mangement
     * device(s).  LM returns information about failed transactions using
     * an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceRemoveNotification(com.cannontech.multispeak.deploy.service.LoadManagementDevice[] removedLMDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies LM that the associated load mangement devices(s)have
     * been retired from the system.  LM returns information about failed
     * transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceRetireNotification(com.cannontech.multispeak.deploy.service.LoadManagementDevice[] retiredLMDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies LM of changes in analog values by sending
     * an array of changed scadaAnalog objects.  LM returns failed transactions
     * using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotification(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies LM of changes in point status by sending
     * an array of changed scadaStatus objects.  LM returns failed transactions
     * using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAStatusChangedNotification(com.cannontech.multispeak.deploy.service.ScadaStatus[] scadaStatuses) throws java.rmi.RemoteException;

    /**
     * Publisher notifies LM of changes in SCADA point definitions
     * by sending an array of changed scadaPoint objects.  LM returns failed
     * transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotification(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException;

    /**
     * Publisher notifies LM of changes in SCADA point definitions,
     * limited to Analog points, by sending an array of changed scadaPoint
     * objects.  LM returns failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotificationForAnalog(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException;

    /**
     * Publisher notifies LM of changes in SCADA point definitions,
     * limited to Status points, by sending an array of changed scadaPoint
     * objects.  LM returns failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotificationForStatus(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException;

    /**
     * Publisher notifies LM of changes in a specific analog value,
     * chosen by scadaPointID, by sending a changed scadaAnalog object. 
     * If this transaction fails, LM returns information about the failure
     * in a SOAPFault.
     */
    public void SCADAAnalogChangedNotificationByPointID(com.cannontech.multispeak.deploy.service.ScadaAnalog scadaAnalog) throws java.rmi.RemoteException;

    /**
     * Publisher notifies LM of changes in a specific analog value,
     * limited to power analogs, by sending an arrray of changed scadaAnalog
     * objects.  LM returns failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotificationForPower(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies LM of changed analog values, limited to
     * voltage analogs, by sending an array of changed scadaAnalog objects.
     * LM returns failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotificationForVoltage(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies LM of changes in the status of a specific
     * point, chosen by PointID, by sending a changed scadaStatus object.
     * If this transaction fails, LM returns information about the failure
     * in a SOAPFault.
     */
    public void SCADAStatusChangedNotificationByPointID(com.cannontech.multispeak.deploy.service.ScadaStatus scadaStatus) throws java.rmi.RemoteException;
}
