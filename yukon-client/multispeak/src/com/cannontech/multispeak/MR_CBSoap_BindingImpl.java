/**
 * MR_CBSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.message.SOAPHeader;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.multispeak.client.Multispeak;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;

public class MR_CBSoap_BindingImpl implements com.cannontech.multispeak.MR_CBSoap_PortType{

	public static final String INTERFACE_NAME = "MC_CB";

	public com.cannontech.multispeak.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException {
		init();
		return MultispeakFuncs.pingURL(INTERFACE_NAME);
	}

	public com.cannontech.multispeak.ArrayOfString getMethods() throws java.rmi.RemoteException {
		init();
		String [] methods = new String[]{"pingURL", "getMethods", "getAMRSupportedMeters", "isAMRMeter"};		
		return MultispeakFuncs.getMethods(INTERFACE_NAME , methods);
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

    public com.cannontech.multispeak.ArrayOfMeter getAMRSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
    	init();
		String companyName = MultispeakFuncs.getCompanyNameFromSOAPHeader();
		MultispeakVendor vendor = Multispeak.getInstance().getMultispeakVendor(companyName);
		String key = (vendor != null ? vendor.getUniqueKey(): "meternumber");
				
		int maxSize = 10000;	//Max number of meters we'll send
		int startIndex = 0;
		int endIndex = 0;	//less than this index
				
		Meter[] meters;
		int indexCount = 0;
		
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		List allMeters = cache.getAllDeviceMeterGroups();
				
		if( lastReceived != null && lastReceived.length() > 0)
		{
			for (int i = 0; i < allMeters.size(); i++)
			{
				LiteDeviceMeterNumber ldmn = (LiteDeviceMeterNumber)allMeters.get(i);
				String lastValue = "";
				if( key.toLowerCase().startsWith("device") || key.toLowerCase().startsWith("pao"))
					lastValue = PAOFuncs.getYukonPAOName(ldmn.getDeviceID());
				else //if(key.toLowerCase().startsWith("meternum"))
					lastValue = ldmn.getMeterNumber();
							
				if( lastValue.compareTo(lastReceived) == 0)
				{
					startIndex = i + 1;
					endIndex = (startIndex+maxSize > allMeters.size()? allMeters.size(): startIndex+maxSize);	//index is the lesser of allMeters.size or startIndex+maxSize
					break;
				}
			}
		}
		if( endIndex == 0)	//haven't set this value yet, so set it now.
			endIndex = (maxSize> allMeters.size()? allMeters.size(): maxSize);	//index is the lesser of allMeters.size or 1000
					
		meters = new Meter[endIndex - startIndex];
		CTILogger.info("Returning " + meters.length + " of " + allMeters.size() +" Meters");				
		for (int i = startIndex; i < endIndex; i++)
		{
			LiteDeviceMeterNumber ldmn = (LiteDeviceMeterNumber)allMeters.get(i);
			LiteYukonPAObject lPao = (LiteYukonPAObject)cache.getAllPAOsMap().get(new Integer(ldmn.getDeviceID()));
			String meterID = ""; 
			if( key.toLowerCase().startsWith("device") || key.toLowerCase().startsWith("pao"))
				meterID = lPao.getPaoName();
			else //if(key.toLowerCase().startsWith("meternum"))
				meterID = ldmn.getMeterNumber();
									
			Meter m = new Meter(meterID, (lPao == null ? "none" : String.valueOf(lPao.getAddress())), "meter", "Cannon", null, MultispeakFuncs.AMR_TYPE, null, null);
			meters[indexCount++] = m;
		}
		if( endIndex != allMeters.size())
			((YukonMultispeakMsgHeader)MessageContext.getCurrentContext().getResponseMessage().getSOAPEnvelope().getHeaderByName("http://www.multispeak.org", "MultiSpeakMsgHeader").getObjectValue()).setObjectsRemaining(new BigInteger(String.valueOf(allMeters.size()-endIndex)));
				    	
		return new ArrayOfMeter(meters);
    }

    public com.cannontech.multispeak.ArrayOfMeter getModifiedAMRMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
    	init();
        return null;
    }

    public boolean isAMRMeter(java.lang.String meterNo) throws java.rmi.RemoteException {
		init();
		String key = "meternumber";//MultispeakFuncs.getUniqueKey();
		
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		List allMeters = cache.getAllDeviceMeterGroups();

		if( meterNo != null && meterNo.length() > 0)
		{
			for (int i = 0; i < allMeters.size(); i++)
			{
				LiteDeviceMeterNumber ldmn = (LiteDeviceMeterNumber)allMeters.get(i);
				String lastValue = "";
				if( key.toLowerCase().startsWith("device") || key.toLowerCase().startsWith("pao"))
					lastValue = PAOFuncs.getYukonPAOName(ldmn.getDeviceID());
				else //if(key.toLowerCase().startsWith("meternum"))
					lastValue = ldmn.getMeterNumber();
			
				if( lastValue.compareTo(meterNo) == 0)
				{
					CTILogger.info("MSP: MeterNumber: " + meterNo + " isAMRMeter(), returning true." );
					return true;
				}					
			}
		}		
		CTILogger.info("MSP: MeterNumber: " + meterNo + " isAMRMeter() NOT found, returning false." );
        return false;
    }

    public com.cannontech.multispeak.ArrayOfMeterRead getReadingsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfMeterRead getReadingsByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfMeterRead getReadingsByBillingCycle(java.lang.String billingCycle, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogsByMeterNoAndEventCode(java.lang.String meterNo, com.cannontech.multispeak.EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogsByDateAndEventCode(com.cannontech.multispeak.EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject initiatePlannedOutage(com.cannontech.multispeak.ArrayOfString meterNos, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject cancelPlannedOutage(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject initiateUsageMonitoring(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject cancelUsageMonitoring(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject initiateDisconnectedStatus(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject cancelDisconnectedStatus(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject initiateMeterReadByMeterNumber(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject customerChangedNotification(com.cannontech.multispeak.ArrayOfCustomer changedCustomers) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject serviceLocationChangedNotification(com.cannontech.multispeak.ArrayOfServiceLocation changedServiceLocations) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject meterChangedNotification(com.cannontech.multispeak.ArrayOfMeter changedMeters) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject meterRemoveNotification(com.cannontech.multispeak.ArrayOfMeter removedMeters) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject meterAddNotification(com.cannontech.multispeak.ArrayOfMeter addedMeters) throws java.rmi.RemoteException {
		init();
        return null;
    }
	private void init()
	{
		MultispeakFuncs.init();
	}
}
