/**
 * EA_MRSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

import com.cannontech.multispeak.client.MultispeakFuncs;

public class EA_MRSoap_BindingImpl implements com.cannontech.multispeak.EA_MRSoap_PortType{
	public static final String INTERFACE_NAME = "EA_MR";

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
    public com.cannontech.multispeak.ArrayOfString getSubstationNames() throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfCircuitElement getDownlineCircuitElements(java.lang.String eaLoc, java.lang.String lastReceived) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfCircuitElement getUplineCircuitElements(java.lang.String eaLoc) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfCircuitElement getChildCircuitElements(java.lang.String eaLoc) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfCircuitElement getParentCircuitElements(java.lang.String eaLoc) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfCircuitElement getAllCircuitElements(java.lang.String lastReceived) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfCircuitElement getModifiedCircuitElements(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfMeterConnectivity getDownlineMeterConnectivity(java.lang.String meterNo) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfMeterConnectivity getUplineMeterConnectivity(java.lang.String meterNo) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfMeterConnectivity getSiblingMeterConnectivity(java.lang.String meterNo) throws java.rmi.RemoteException {
		init();    	
        return null;
    }
	private void init(){
		MultispeakFuncs.init();
	}
}
