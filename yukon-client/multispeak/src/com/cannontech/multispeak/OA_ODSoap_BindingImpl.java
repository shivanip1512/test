/**
 * OA_ODSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

import com.cannontech.multispeak.client.MultispeakFuncs;

public class OA_ODSoap_BindingImpl implements com.cannontech.multispeak.OA_ODSoap_PortType{
	public static final String INTERFACE_NAME = "OA_OD";
		
    public com.cannontech.multispeak.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException {
    	init();
        return MultispeakFuncs.pingURL(INTERFACE_NAME);
    }

    public com.cannontech.multispeak.ArrayOfString getMethods() throws java.rmi.RemoteException {
		init();
		String [] methods = new String[]{"pingURL", "getMethods"};
        return MultispeakFuncs.getMethods(INTERFACE_NAME, methods);
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

    public com.cannontech.multispeak.OutageEventStatus getOutageEventStatus(java.lang.String outageEventID) throws java.rmi.RemoteException {
		init();
        return new OutageEventStatus();
    }

    public com.cannontech.multispeak.ArrayOfString getActiveOutages() throws java.rmi.RemoteException {
		init();
        return new ArrayOfString(new String[0]);
    }

    public com.cannontech.multispeak.OutageEventStatus getOutageEventStatusByOutageLocation(com.cannontech.multispeak.OutageLocation location) throws java.rmi.RemoteException {
		init();
        return new OutageEventStatus();
    }

    public com.cannontech.multispeak.ArrayOfErrorObject ODEventNotification(com.cannontech.multispeak.ArrayOfOutageDetectionEvent ODEvents) throws java.rmi.RemoteException {
		init();
        return new ArrayOfErrorObject(new ErrorObject[0]);
    }

    public com.cannontech.multispeak.ArrayOfErrorObject ODDeviceChangedNotification(com.cannontech.multispeak.ArrayOfOutageDetectionDevice ODDevices) throws java.rmi.RemoteException {
		init();
        return new ArrayOfErrorObject(new ErrorObject[0]);
    }

	private void init()
	{
		MultispeakFuncs.init();
	}
}
