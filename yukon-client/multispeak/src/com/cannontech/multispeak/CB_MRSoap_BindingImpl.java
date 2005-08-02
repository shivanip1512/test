/**
 * CB_MRSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class CB_MRSoap_BindingImpl implements com.cannontech.multispeak.CB_MRSoap_PortType{
    public com.cannontech.multispeak.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfString getMethods() throws java.rmi.RemoteException {
    	ArrayOfString arrayOfString = new ArrayOfString(new String[]{"PingUrl", "GetMethods"});
        return arrayOfString;
    }

    public com.cannontech.multispeak.ArrayOfString getDomainNames() throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfCustomer getAllCustomers(java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfCustomer getModifiedCustomers(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getModifiedServiceLocations(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.Customer getCustomerByCustId(java.lang.String custId) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.Customer getCustomerByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfCustomer getCustomerByName(java.lang.String firstName, java.lang.String lastName) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.Customer getCustomerByDBAName(java.lang.String dBAName) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getAllServiceLocations(java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByServiceStatus(java.lang.String servStatus, java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ServiceLocation getServiceLocationByServLoc(java.lang.String servLocId) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByCustId(java.lang.String custId) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByGridLocation(java.lang.String gridLocation) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByPhaseCode(com.cannontech.multispeak.PhaseCd phaseCode, java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByLoadGroup(java.lang.String loadGroup, java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByServiceType(java.lang.String serviceType, java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByShutOffDate(java.util.Calendar shutOffDate) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfMeter getAllMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfMeter getModifiedMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.Meter getMeterByMeterId(java.lang.String meterID) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.Meter getMeterByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfMeter getMeterByServLoc(java.lang.String servLoc) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfMeter getMeterByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfMeter getMeterByCustID(java.lang.String custID) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfMeter getMeterByAMRType(java.lang.String aMRType, java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public void modifyCBDataForCustomer(com.cannontech.multispeak.Customer customerData) throws java.rmi.RemoteException {
    }

    public void modifyCBDataForServiceLocation(com.cannontech.multispeak.ServiceLocation serviceLocationData) throws java.rmi.RemoteException {
    }

    public void modifyCBDataForMeter(com.cannontech.multispeak.Meter meterData) throws java.rmi.RemoteException {
    }

    public com.cannontech.multispeak.ArrayOfErrorObject readingChangedNotification(com.cannontech.multispeak.ArrayOfMeterRead changedMeterReads) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject historyLogChangedNotification(com.cannontech.multispeak.ArrayOfHistoryLog changedHistoryLogs) throws java.rmi.RemoteException {
        return null;
    }

}
