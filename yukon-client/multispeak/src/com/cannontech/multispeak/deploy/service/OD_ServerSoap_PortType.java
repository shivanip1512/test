/**
 * OD_ServerSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public interface OD_ServerSoap_PortType extends java.rmi.Remote {

    /**
     * Requester pings URL of OD to see if it is alive. Returns errorObject(s)
     * as necessary to communicate application status.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] pingURL() throws java.rmi.RemoteException;

    /**
     * Requester requests list of methods supported by OD.
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
     * Returns all outage detection devices. The calling parameter
     * lastReceived is included so that large sets of data can be returned
     * in manageable blocks.  lastReceived should carry an empty string the
     * first time in a session that this method is invoked.  When multiple
     * calls to this method are required to obtain all of the data, the lastReceived
     * should carry the objectID of the last data instance received in subsequent
     * calls. If the sessionID parameter is set in the message header, then
     * the server shall respond as if it were being asked for a GetModifiedXXX
     * since that sessionID; if the sessionID is not included in the method
     * call, then all instances of the object shall be returned in response
     * to the call.
     */
    public com.cannontech.multispeak.deploy.service.OutageDetectionDevice[] getAllOutageDetectionDevices(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns outage detection devices associated with the given
     * meter number.
     */
    public com.cannontech.multispeak.deploy.service.OutageDetectionDevice[] getOutageDetectionDevicesByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns all outage detection devices with a given OutageDetectionDeviceStatus.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry the objectID of the
     * last data instance received in subsequent calls.
     */
    public com.cannontech.multispeak.deploy.service.OutageDetectionDevice[] getOutageDetectionDevicesByStatus(com.cannontech.multispeak.deploy.service.OutageDetectDeviceStatus oDDStatus, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all outage detection devices of a given OutageDetectionDeviceType.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry the objectID of the
     * last data instance received in subsequent calls.
     */
    public com.cannontech.multispeak.deploy.service.OutageDetectionDevice[] getOutageDetectionDevicesByType(com.cannontech.multispeak.deploy.service.OutageDetectDeviceType oDDType, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the outageDetectionDevices that are currently experiencing
     * outage conditions.
     */
    public com.cannontech.multispeak.deploy.service.OutageDetectionDevice[] getOutagedODDevices() throws java.rmi.RemoteException;

    /**
     * Client requests server to update the status of an outageDetectionDevice.
     * OD responds by publishing a revised outageDetectionEvent (using the
     * ODEventNotification method on OA-OD) to the URL specified in the responseURL
     * parameter.  OD returns information about failed transactions using
     * an array of errorObjects. The transactionID calling parameter is included
     * to link a returned ODEventNotification with this request.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateOutageDetectionEventRequest(java.lang.String[] meterNos, java.util.Calendar requestDate, java.lang.String responseURL, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Client requests server to return only outage detection events
     * that are known to be of type Outage or Inferred on service locations
     * downline from a circuit element supplied using the calling parameters
     * objectName and nounType and containing the phasing supplied in the
     * calling parameter phaseCode. OD responds by publishing a revised outageDetectionEvent
     * (using the ODEventNotification method on OA-OD)to the URL specified
     * in the responseURL parameter.  OD returns information about failed
     * transactions using an array of errorObjects.The transactionID calling
     * parameter is included to link a returned ODEventNotification with
     * this request.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateODEventRequestByObject(java.lang.String objectName, java.lang.String nounType, com.cannontech.multispeak.deploy.service.PhaseCd phaseCode, java.util.Calendar requestDate, java.lang.String responseURL, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Client requests server to return outage detection events that
     * are known to be of type Outage or Inferred on an array of service
     * locations. Server responds by publishing a revised outageDetectionEvent
     * (using the ODEventNotification method on OA_Server)to the URL specified
     * in the responseURL parameter.  Server returns information about failed
     * transactions using an array of errorObjects.  The transactionID calling
     * parameter is included to link a returned ODEventNotification with
     * this request.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateODEventRequestByServiceLocation(java.lang.String[] servLoc, java.util.Calendar requestDate, java.lang.String responseURL, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Client requests server to return only outage detection events
     * that are known to be of type Outage or Inferred on service locations
     * downline from a circuit element supplied using the calling parameters
     * objectName and nounType. OD creates a monitoring event for the specified
     * circuit element.  Monitoring shall be performed at the time interval
     * given in the periodicity parameter (expressed in minutes).  OD responds
     * by publishing a revised outageDetectionEvent (using the ODEventNotification
     * method on OA-OD)to the URL specified in the responseURL parameter.
     * OD returns information about failed transactions using an array of
     * errorObjects.The transactionID calling parameter is included to link
     * a returned ODEventNotification with this request.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateODMonitoringRequestByObject(java.lang.String objectName, java.lang.String nounType, com.cannontech.multispeak.deploy.service.PhaseCd phaseCode, int periodicity, java.util.Calendar requestDate, java.lang.String responseURL, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Requester requests server to return a list of circuit elements
     * (in the form of objectRefs) that are currently being monitored.  OD
     * returns an array of objectRefs.
     */
    public com.cannontech.multispeak.deploy.service.ObjectRef[] displayODMonitoringRequests() throws java.rmi.RemoteException;

    /**
     * Requester requests server to cancel outage detection monitoring
     * on the list of supplied circuit elements (called out by objectRef).
     * OD returns information about failed transactions using an array of
     * errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] cancelODMonitoringRequestByObject(com.cannontech.multispeak.deploy.service.ObjectRef[] objectRef, java.util.Calendar requestDate) throws java.rmi.RemoteException;

    /**
     * Client notifies server of a change in the Customer object by
     * sending one or more changed customer object(s).  OD returns information
     * about failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] customerChangedNotification(com.cannontech.multispeak.deploy.service.Customer[] changedCustomers) throws java.rmi.RemoteException;

    /**
     * Client notifies server of a change in the Service Location
     * object by sending one or more changed serviceLocation object(s). 
     * OD returns information about failed transactions using an array of
     * errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] serviceLocationChangedNotification(com.cannontech.multispeak.deploy.service.ServiceLocation[] changedServiceLocations) throws java.rmi.RemoteException;

    /**
     * Client notifies server of a change in the Meter object by sending
     * one or more changed meter object(s).  OD returns information about
     * failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterChangedNotification(com.cannontech.multispeak.deploy.service.Meter[] changedMeters) throws java.rmi.RemoteException;

    /**
     * Allow requester to modify server data for a specific outage
     * detection device object.  If this transaction failes,OD returns information
     * about the failure using a SAOPFault.
     */
    public void modifyODDataForOutageDetectionDevice(com.cannontech.multispeak.deploy.service.OutageDetectionDevice oDDevice) throws java.rmi.RemoteException;
}
