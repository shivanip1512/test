/**
 * CB_CDSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

import com.cannontech.multispeak.client.MultispeakFuncs;

public class CB_CDSoap_BindingImpl implements com.cannontech.multispeak.CB_CDSoap_PortType{
	public static final String INTERFACE_NAME = "CB_CD";

 	public com.cannontech.multispeak.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException {
		init();
		return MultispeakFuncs.pingURL(INTERFACE_NAME);
	}

	public com.cannontech.multispeak.ArrayOfString getMethods() throws java.rmi.RemoteException {
		init();
		String [] methods = new String[]{"pingURL", "getMethods"};
		return MultispeakFuncs.getMethods(INTERFACE_NAME, methods );
 	}

 	public com.cannontech.multispeak.ArrayOfString getDomainNames() throws java.rmi.RemoteException {
	 	init();
	 	String [] strings = new String[]{"Method Not Supported"};
	 	MultispeakFuncs.logArrayOfString(INTERFACE_NAME, "getDomainNames", strings);
	 	return new ArrayOfString(strings);
 	}

 	public com.cannontech.multispeak.ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
		init();
	 	return new ArrayOfDomainMember(new DomainMember[0]);
 	}

    public com.cannontech.multispeak.ArrayOfCustomer getAllCustomers(java.lang.String lastReceived) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfCustomer getModifiedCustomers(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getModifiedServiceLocations(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.Customer getCustomerByCustId(java.lang.String custId) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.Customer getCustomerByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfCustomer getCustomerByName(java.lang.String firstName, java.lang.String lastName) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.Customer getCustomerByDBAName(java.lang.String dBAName) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getAllServiceLocations(java.lang.String lastReceived) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ServiceLocation getServiceLocationByServLoc(java.lang.String servLocId) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByCustId(java.lang.String custId) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ServiceLocation getServiceLocationByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByServiceStatus(java.lang.String servStatus, java.lang.String lastReceived) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByGridLocation(java.lang.String gridLocation) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfMeter getAllMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfMeter getModifiedMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.Meter getMeterByMeterId(java.lang.String meterID) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.Meter getMeterByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfMeter getMeterByServLoc(java.lang.String servLoc) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfMeter getMeterByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfMeter getMeterByCustID(java.lang.String custID) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject modifyCBDataForCustomer(com.cannontech.multispeak.ArrayOfCustomer customerData) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject modifyCBDataForServiceLocation(com.cannontech.multispeak.ArrayOfServiceLocation serviceLocationData) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject modifyCBDataForMeter(com.cannontech.multispeak.ArrayOfMeter meterData) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public void CDStateChangedNotification(java.lang.String meterNo, com.cannontech.multispeak.LoadActionCode stateChange) throws java.rmi.RemoteException {
		init();
    }
	private void init(){
		MultispeakFuncs.init();
	}
}
