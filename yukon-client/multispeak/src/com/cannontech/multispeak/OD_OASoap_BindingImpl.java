/**
 * OD_OASoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.multispeak.client.Multispeak;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;

public class OD_OASoap_BindingImpl implements com.cannontech.multispeak.OD_OASoap_PortType{

    public static final String INTERFACE_NAME = "OD_OA";

    public com.cannontech.multispeak.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException {
		init();
		return MultispeakFuncs.pingURL(INTERFACE_NAME);
    }

    public com.cannontech.multispeak.ArrayOfString getMethods() throws java.rmi.RemoteException {
		init();
		String [] methods = new String[]{"pingURL", "getMethods", "initiateOutageDetectionEventRequst"};
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

    public com.cannontech.multispeak.ArrayOfOutageDetectionDevice getAllOutageDetectionDevices(java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
		return new ArrayOfOutageDetectionDevice(new OutageDetectionDevice[0]);
    }

    public com.cannontech.multispeak.ArrayOfOutageDetectionDevice getOutageDetectionDevicesByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
        init();
		return new ArrayOfOutageDetectionDevice(new OutageDetectionDevice[0]);
    }

    public com.cannontech.multispeak.ArrayOfOutageDetectionDevice getOutageDetectionDevicesByStatus(com.cannontech.multispeak.OutageDetectDeviceStatus oDDStatus, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
		return new ArrayOfOutageDetectionDevice(new OutageDetectionDevice[0]);
    }

    public com.cannontech.multispeak.ArrayOfOutageDetectionDevice getOutageDetectionDevicesByType(com.cannontech.multispeak.OutageDetectDeviceType oDDType, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
		return new ArrayOfOutageDetectionDevice(new OutageDetectionDevice[0]);
    }

    public com.cannontech.multispeak.ArrayOfOutageDetectionDevice getOutagedODDevices() throws java.rmi.RemoteException {
        init();
		return new ArrayOfOutageDetectionDevice(new OutageDetectionDevice[0]);
    }

    public com.cannontech.multispeak.ArrayOfErrorObject initiateOutageDetectionEventRequest(com.cannontech.multispeak.ArrayOfString meterNos, java.util.Calendar requestDate) throws java.rmi.RemoteException {
        init();
		ErrorObject[] errorObjects = new ErrorObject[0];
		
		String companyName = MultispeakFuncs.getCompanyNameFromSOAPHeader();
		MultispeakVendor vendor = Multispeak.getInstance().getMultispeakVendor(companyName);

		String url = (vendor != null ? vendor.getUrl() : "(none)");
		if( url == null || url.equalsIgnoreCase(CtiUtilities.STRING_NONE))
		{
			throw new RemoteException("OMS vendor unknown.  Please contact Yukon administrator to set the Multispeak Vendor Role Property value in Yukon.");
/*			ErrorObject error = new ErrorObject();
			error.setErrorString("OMS vendor unknown.  Please contact Yukon administrator to set the Multispeak Vendor Role Property value in Yukon.");
			error.setEventTime(new GregorianCalendar());
			errorObjects = new ErrorObject[]{error};
*/
		}
		else if ( ! Multispeak.getInstance().getPilConn().isValid() )
		{
			throw new RemoteException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
/*			ErrorObject error = new ErrorObject();
			error.setErrorString("Connection to Yukon Porter is not valid.");
			error.setEventTime(new GregorianCalendar());
			errorObjects = new ErrorObject[]{error};
*/
		}
		else
		{
			errorObjects = Multispeak.getInstance().ODEvent(companyName, meterNos.getString());
		}
		MultispeakFuncs.logArrayOfErrorObjects(INTERFACE_NAME, "initiateOutageDetectionEventRequest", errorObjects);
        return new ArrayOfErrorObject(errorObjects);
    }

    public void modifyODDataForOutageDetectionDevice(com.cannontech.multispeak.OutageDetectionDevice oDDevice) throws java.rmi.RemoteException {
        init();
    }

	public ArrayOfErrorObject initiateODEventRequestByObject(ObjectRef objectRef, PhaseCd phaseCode, Calendar requestDate) throws RemoteException
	{
		init();
		return null;
	}

	public ArrayOfErrorObject initiateODMonitoringRequestByObject(ObjectRef objectRef, PhaseCd phaseCode, int periodicity, Calendar requestDate) throws RemoteException
	{
		init();
		return null;
	}

	public ArrayOfObjectRef displayODMonitoringRequests() throws RemoteException
	{
		init();
		return null;
	}

	public ArrayOfErrorObject cancelODMonitoringRequestByObject(ArrayOfObjectRef objectRef, Calendar requestDate) throws RemoteException
	{
		init();
		return null;
	}
	
	private void init()
	{
		MultispeakFuncs.init();
	}
}
