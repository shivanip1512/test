/**
 * OD_OASoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

import java.util.GregorianCalendar;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.multispeak.client.Multispeak;
import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;
import com.cannontech.roles.yukon.MultispeakRole;

public class OD_OASoap_BindingImpl implements com.cannontech.multispeak.OD_OASoap_PortType{


    public com.cannontech.multispeak.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException {
		loadResponseHeader();
    	if (Multispeak.getInstance() != null)
        	return new ArrayOfErrorObject(new ErrorObject[0]);
       	ErrorObject err = new ErrorObject();
       	err.setErrorString("Yukon Multispeak WebServices are down.");
       	err.setEventTime(new GregorianCalendar());
		return new ArrayOfErrorObject(new ErrorObject[]{err});
    }

    public com.cannontech.multispeak.ArrayOfString getMethods() throws java.rmi.RemoteException {
		loadResponseHeader();
    	String [] methods = new String[]{"PingURL", "GetMethods", "InitiateOutageDetectionEventRequst"};
        return new ArrayOfString(methods);
    }

    public com.cannontech.multispeak.ArrayOfString getDomainNames() throws java.rmi.RemoteException {
		loadResponseHeader();
        return new ArrayOfString(new String[]{"Method Not Supported"});
    }

    public com.cannontech.multispeak.ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
		loadResponseHeader();
		return new ArrayOfDomainMember(new DomainMember[0]);
    }

    public com.cannontech.multispeak.ArrayOfOutageDetectionDevice getAllOutageDetectionDevices(java.lang.String lastReceived) throws java.rmi.RemoteException {
		loadResponseHeader();
		return new ArrayOfOutageDetectionDevice(new OutageDetectionDevice[0]);
    }

    public com.cannontech.multispeak.ArrayOfOutageDetectionDevice getOutageDetectionDevicesByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
		loadResponseHeader();
		return new ArrayOfOutageDetectionDevice(new OutageDetectionDevice[0]);
    }

    public com.cannontech.multispeak.ArrayOfOutageDetectionDevice getOutageDetectionDevicesByStatus(com.cannontech.multispeak.OutageDetectDeviceStatus oDDStatus, java.lang.String lastReceived) throws java.rmi.RemoteException {
		loadResponseHeader();
		return new ArrayOfOutageDetectionDevice(new OutageDetectionDevice[0]);
    }

    public com.cannontech.multispeak.ArrayOfOutageDetectionDevice getOutageDetectionDevicesByType(com.cannontech.multispeak.OutageDetectDeviceType oDDType, java.lang.String lastReceived) throws java.rmi.RemoteException {
		loadResponseHeader();
		return new ArrayOfOutageDetectionDevice(new OutageDetectionDevice[0]);
    }

    public com.cannontech.multispeak.ArrayOfOutageDetectionDevice getOutagedODDevices() throws java.rmi.RemoteException {
		loadResponseHeader();
		return new ArrayOfOutageDetectionDevice(new OutageDetectionDevice[0]);
    }

    public com.cannontech.multispeak.ArrayOfErrorObject initiateOutageDetectionEventRequest(com.cannontech.multispeak.ArrayOfString meterNos, java.util.Calendar requestDate) throws java.rmi.RemoteException {
    	loadResponseHeader();
		ErrorObject[] errorObjects = new ErrorObject[0];

		String url = RoleFuncs.getGlobalPropertyValue(MultispeakRole.OMS_WEBSERVICE_URL);
		if( url == null || url.equalsIgnoreCase(CtiUtilities.STRING_NONE))
		{
			ErrorObject error = new ErrorObject();
			error.setErrorString("OMS vendor unknown.  Please contact Yukon administrator to set the OMS_WEBSERVICES_URL Role Property value in Yukon.");
			error.setEventTime(new GregorianCalendar());
			errorObjects = new ErrorObject[]{error};
		}
		else if ( ! Multispeak.getInstance().getPilConn().isValid() )
		{
			ErrorObject error = new ErrorObject();
			error.setErrorString("Connection to Yukon Porter is not valid.");
			error.setEventTime(new GregorianCalendar());
			errorObjects = new ErrorObject[]{error};
		}
		else
		{
			errorObjects = Multispeak.getInstance().ODEvent(meterNos.getString());
		}
        return new ArrayOfErrorObject(errorObjects);
    }

    public void modifyODDataForOutageDetectionDevice(com.cannontech.multispeak.OutageDetectionDevice oDDevice) throws java.rmi.RemoteException {
    }
    
    private void loadResponseHeader() 
    {
		try {
			//Get current message context
			MessageContext ctx = MessageContext.getCurrentContext();

			// Get SOAP envelope of response
			SOAPEnvelope env = ctx.getResponseMessage().getSOAPEnvelope();
			
			//Create SOAP header object } } } 
			SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org", "MultiSpeakMsgHeader", new YukonMultispeakMsgHeader());

			// Set Header
			env.addHeader(header);
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
