/**
 * OD_OASoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public interface OD_OASoap_PortType extends java.rmi.Remote {

    /**
     * OA Pings URL of OD to see if it is alive. Returns errorObject(s)
     * as necessary to communicate application status.(Req)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException;

    /**
     * OA requests list of methods supported by OD. (Req)
     */
    public com.cannontech.multispeak.ArrayOfString getMethods() throws java.rmi.RemoteException;

    /**
     * The client requests from the server a list of names of domains
     * supported by the server.  This method is used, along with the GetDomainMembers
     * method to enable systems to exchange information about application-specific
     * or installation-specific lists of information, such as the lists of
     * counties for this installation or the list of serviceStatusCodes used
     * by the server. (Opt)
     */
    public com.cannontech.multispeak.ArrayOfString getDomainNames() throws java.rmi.RemoteException;

    /**
     * The client requests from the server the members of a specific
     * domain of information, identified by the domainName parameter, which
     * are supported by the server.  This method is used, along with the
     * GetDomainNames method to enable systems to exchange information about
     * application-specific or installation-specific lists of information,
     * such as the lists of counties for this installation or the list of
     * serviceStatusCodes used by the server. (Opt)
     */
    public com.cannontech.multispeak.ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException;

    /**
     * Returns all Outage Detection Devices. The calling parameter
     * lastReceived is included so that large sets of data can be returned
     * in manageable blocks.  lastReceived should carry an empty string the
     * first time in a session that this method is invoked.  When multiple
     * calls to this method are required to obtain all of the data, the lastReceived
     * should carry the objectID of the last data instance received in subsequent
     * calls.(Req)
     */
    public com.cannontech.multispeak.ArrayOfOutageDetectionDevice getAllOutageDetectionDevices(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns an Outage Detection Devices Associated with the Given
     * Meter Number.(Req)
     */
    public com.cannontech.multispeak.ArrayOfOutageDetectionDevice getOutageDetectionDevicesByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns all Outage Detection Devices with a given OutageDetectionDeviceStatus.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry the objectID of the
     * last data instance received in subsequent calls.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfOutageDetectionDevice getOutageDetectionDevicesByStatus(com.cannontech.multispeak.OutageDetectDeviceStatus oDDStatus, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all Outage Detection Devices of a given OutageDetectionDeviceType.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry the objectID of the
     * last data instance received in subsequent calls.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfOutageDetectionDevice getOutageDetectionDevicesByType(com.cannontech.multispeak.OutageDetectDeviceType oDDType, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the outageDetectionDevices that are currently experiencing
     * outage conditions.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfOutageDetectionDevice getOutagedODDevices() throws java.rmi.RemoteException;

    /**
     * OA requests OD to update the status of an outageDetectionDevice.
     * OD responds by publishing a revised outageDetectionEvent (using the
     * ODEventNotification method on OA-OD).  OD returns information about
     * failed transactions using an array of errorObjects.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject initiateOutageDetectionEventRequest(com.cannontech.multispeak.ArrayOfString meterNos, java.util.Calendar requestDate) throws java.rmi.RemoteException;

    /**
     * Allow OA to Modify OD data for aspecific Outage Detection Device
     * object.  If this transaction failes,OD returns information about the
     * failure using a SAOPFault.(Opt)
     */
    public void modifyODDataForOutageDetectionDevice(com.cannontech.multispeak.OutageDetectionDevice oDDevice) throws java.rmi.RemoteException;
}
