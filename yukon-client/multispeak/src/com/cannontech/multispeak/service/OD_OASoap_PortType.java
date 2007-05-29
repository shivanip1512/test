/**
 * OD_OASoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public interface OD_OASoap_PortType extends java.rmi.Remote {

    /**
     * OA Pings URL of OD to see if it is alive. Returns errorObject(s)
     * as necessary to communicate application status.(Required)
     */
    public com.cannontech.multispeak.service.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException;

    /**
     * OA requests list of methods supported by OD. (Required)
     */
    public com.cannontech.multispeak.service.ArrayOfString getMethods() throws java.rmi.RemoteException;

    /**
     * The client requests from the server a list of names of domains
     * supported by the server.  This method is used, along with the GetDomainMembers
     * method to enable systems to exchange information about application-specific
     * or installation-specific lists of information, such as the lists of
     * counties for this installation or the list of serviceStatusCodes used
     * by the server. (Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfString getDomainNames() throws java.rmi.RemoteException;

    /**
     * The client requests from the server the members of a specific
     * domain of information, identified by the domainName parameter, which
     * are supported by the server.  This method is used, along with the
     * GetDomainNames method to enable systems to exchange information about
     * application-specific or installation-specific lists of information,
     * such as the lists of counties for this installation or the list of
     * serviceStatusCodes used by the server. (Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException;

    /**
     * Returns all Outage Detection Devices. The calling parameter
     * lastReceived is included so that large sets of data can be returned
     * in manageable blocks.  lastReceived should carry an empty string the
     * first time in a session that this method is invoked.  When multiple
     * calls to this method are required to obtain all of the data, the lastReceived
     * should carry the objectID of the last data instance received in subsequent
     * calls.(Recommended)
     */
    public com.cannontech.multispeak.service.ArrayOfOutageDetectionDevice getAllOutageDetectionDevices(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns an Outage Detection Devices Associated with the Given
     * Meter Number.(Recommended)
     */
    public com.cannontech.multispeak.service.ArrayOfOutageDetectionDevice getOutageDetectionDevicesByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns all Outage Detection Devices with a given OutageDetectionDeviceStatus.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry the objectID of the
     * last data instance received in subsequent calls.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfOutageDetectionDevice getOutageDetectionDevicesByStatus(com.cannontech.multispeak.service.OutageDetectDeviceStatus oDDStatus, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all Outage Detection Devices of a given OutageDetectionDeviceType.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry the objectID of the
     * last data instance received in subsequent calls.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfOutageDetectionDevice getOutageDetectionDevicesByType(com.cannontech.multispeak.service.OutageDetectDeviceType oDDType, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the outageDetectionDevices that are currently experiencing
     * outage conditions.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfOutageDetectionDevice getOutagedODDevices() throws java.rmi.RemoteException;

    /**
     * OA requests OD to update the status of an outageDetectionDevice.
     * OD responds by publishing a revised outageDetectionEvent (using the
     * ODEventNotification method on OA-OD) to the URL specified in the responseURL
     * parameter.  OD returns information about failed transactions using
     * an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfErrorObject initiateOutageDetectionEventRequest(com.cannontech.multispeak.service.ArrayOfString meterNos, java.util.Calendar requestDate, java.lang.String responseURL) throws java.rmi.RemoteException;

    /**
     * OA requests OD to return only outage detection events that
     * are known to be of type Outage or Inferred on service locations downline
     * from a circuit element supplied using the calling parameters objectName
     * and nounType and containing the phasing supplied in the calling parameter
     * phaseCode. OD responds by publishing a revised outageDetectionEvent
     * (using the ODEventNotification method on OA-OD)to the URL specified
     * in the responseURL parameter.  OD returns information about failed
     * transactions using an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfErrorObject initiateODEventRequestByObject(java.lang.String objectName, java.lang.String nounType, com.cannontech.multispeak.service.PhaseCd phaseCode, java.util.Calendar requestDate, java.lang.String responseURL) throws java.rmi.RemoteException;

    /**
     * OA requests OD to return only outage detection events that
     * are known to be of type Outage or Inferred on service locations downline
     * from a circuit element supplied using the calling parameters objectName
     * and nounType. OD creates a monitoring event for the specified circuit
     * element.  Monitoring shall be performed at the time interval given
     * in the periodicity parameter (expressed in minutes).  OD responds
     * by publishing a revised outageDetectionEvent (using the ODEventNotification
     * method on OA-OD)to the URL specified in the responseURL parameter.
     * OD returns information about failed transactions using an array of
     * errorObjects.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfErrorObject initiateODMonitoringRequestByObject(java.lang.String objectName, java.lang.String nounType, com.cannontech.multispeak.service.PhaseCd phaseCode, int periodicity, java.util.Calendar requestDate, java.lang.String responseURL) throws java.rmi.RemoteException;

    /**
     * OA requests OD to return a list of circuit elements (in the
     * form of objectRefs) that are currently being monitored.  OD returns
     * an array of objectRefs.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfObjectRef displayODMonitoringRequests() throws java.rmi.RemoteException;

    /**
     * OA requests OD to cancel outage detection monitoring on the
     * list of supplied circuit elements (called out by objectRef).  OD returns
     * information about failed transactions using an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfErrorObject cancelODMonitoringRequestByObject(com.cannontech.multispeak.service.ArrayOfObjectRef objectRef, java.util.Calendar requestDate) throws java.rmi.RemoteException;

    /**
     * Allow OA to Modify OD data for aspecific Outage Detection Device
     * object.  If this transaction failes,OD returns information about the
     * failure using a SAOPFault.(Optional)
     */
    public void modifyODDataForOutageDetectionDevice(com.cannontech.multispeak.service.OutageDetectionDevice oDDevice) throws java.rmi.RemoteException;
}
