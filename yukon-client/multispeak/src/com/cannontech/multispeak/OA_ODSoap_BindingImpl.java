/**
 * OA_ODSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class OA_ODSoap_BindingImpl implements com.cannontech.multispeak.OA_ODSoap_PortType{
    public com.cannontech.multispeak.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException {
    	System.out.println("HERE");
        return new ArrayOfErrorObject(new ErrorObject[]{new ErrorObject()});
    }

    public com.cannontech.multispeak.ArrayOfString getMethods() throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfString getDomainNames() throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.OutageEventStatus getOutageEventStatus(java.lang.String outageEventID) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfString getActiveOutages() throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.OutageEventStatus getOutageEventStatusByOutageLocation(com.cannontech.multispeak.OutageLocation location) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject ODEventNotification(com.cannontech.multispeak.ArrayOfOutageDetectionEvent ODEvents) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject ODDeviceChangedNotification(com.cannontech.multispeak.ArrayOfOutageDetectionDevice ODDevices) throws java.rmi.RemoteException {
        return null;
    }

}
