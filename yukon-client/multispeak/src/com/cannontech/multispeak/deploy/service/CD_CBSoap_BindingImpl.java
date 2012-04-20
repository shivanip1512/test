/**
 * CD_CBSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

import java.rmi.RemoteException;

import com.cannontech.spring.YukonSpringHook;

public class CD_CBSoap_BindingImpl implements com.cannontech.multispeak.deploy.service.CD_CBSoap_PortType{
    private CD_ServerSoap_PortType cd_server = YukonSpringHook.getBean("cd_server", CD_ServerSoap_PortType.class);

	public ErrorObject[] CDDeviceAddNotification(CDDevice[] addedCDDs)
			throws RemoteException {
		return cd_server.CDDeviceAddNotification(addedCDDs);
	}

	public ErrorObject[] CDDeviceChangedNotification(CDDevice[] changedCDDs)
			throws RemoteException {
		return cd_server.CDDeviceChangedNotification(changedCDDs);
	}

	public ErrorObject[] CDDeviceExchangeNotification(
			CDDeviceExchange[] CDDChangeout) throws RemoteException {
		return cd_server.CDDeviceExchangeNotification(CDDChangeout);
	}

	public ErrorObject[] CDDeviceRemoveNotification(CDDevice[] removedCDDs)
			throws RemoteException {
		return cd_server.CDDeviceRemoveNotification(removedCDDs);
	}

	public ErrorObject[] CDDeviceRetireNotification(CDDevice[] retiredCDDs)
			throws RemoteException {
		return cd_server.CDDeviceRetireNotification(retiredCDDs);
	}

	public ErrorObject[] customerChangedNotification(Customer[] changedCustomers)
			throws RemoteException {
		return cd_server.customerChangedNotification(changedCustomers);
	}

	public LoadActionCode getCDMeterState(String meterNo)
			throws RemoteException {
		return cd_server.getCDMeterState(meterNo);
	}

	public Meter[] getCDSupportedMeters(String lastReceived)
			throws RemoteException {
		return cd_server.getCDSupportedMeters(lastReceived);
	}

	public DomainMember[] getDomainMembers(String domainName)
			throws RemoteException {
		return cd_server.getDomainMembers(domainName);
	}

	public String[] getDomainNames() throws RemoteException {
		return cd_server.getDomainNames();
	}

	public String[] getMethods() throws RemoteException {
		return cd_server.getMethods();
	}

	public Meter[] getModifiedCDMeters(String previousSessionID,
			String lastReceived) throws RemoteException {
		return cd_server.getModifiedCDMeters(previousSessionID, lastReceived);
	}

	public ErrorObject[] initiateConnectDisconnect(
			ConnectDisconnectEvent[] cdEvents, String responseURL,
			String transactionID) throws RemoteException {
		return cd_server.initiateConnectDisconnect(cdEvents, responseURL,
				transactionID, Float.MIN_NORMAL);
	}

	public ErrorObject[] meterChangedNotification(Meter[] changedMeters)
			throws RemoteException {
		return cd_server.meterChangedNotification(changedMeters);
	}

	public ErrorObject[] pingURL() throws RemoteException {
		return cd_server.pingURL();
	}

	public ErrorObject[] serviceLocationChangedNotification(
			ServiceLocation[] changedServiceLocations) throws RemoteException {
		return cd_server
				.serviceLocationChangedNotification(changedServiceLocations);
	}
    
    
}
