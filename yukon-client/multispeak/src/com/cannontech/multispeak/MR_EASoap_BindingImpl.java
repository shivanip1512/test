/**
 * MR_EASoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import org.apache.axis.MessageContext;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.cache.PointChangeCache;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.multispeak.client.Multispeak;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;

public class MR_EASoap_BindingImpl implements com.cannontech.multispeak.MR_EASoap_PortType{
	public static final String INTERFACE_NAME = "MR_EA";

	public com.cannontech.multispeak.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException {
		init();
		return MultispeakFuncs.pingURL(INTERFACE_NAME);
	}

	public com.cannontech.multispeak.ArrayOfString getMethods() throws java.rmi.RemoteException {
		init();
		String [] methods = new String[]{"pingURL", "getMethods", 
										 "getAMRSupportedMeters", "getLatestReadingByMeterNo",
										 "getReadingsByMeterNo", "getLatestReadings"};
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

	/**
	 * This method is a duplicate, refer to the MR_CB method so only one must be implemented  
	 */
	public com.cannontech.multispeak.ArrayOfMeter getAMRSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
//		init();	//init() is performed in the MR_CB method, no need to call it again.
		MR_CBSoap_BindingImpl mr_cb = new MR_CBSoap_BindingImpl();
		return mr_cb.getAMRSupportedMeters(lastReceived);
    }

    public com.cannontech.multispeak.ArrayOfMeter getModifiedAMRMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
		init();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfMeterRead getReadingsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
		init();
        return null;
    }

	/**
	 * This method is a duplicate, refer to the MR_CB method so only one must be implemented  
	 */
    public com.cannontech.multispeak.ArrayOfMeterRead getReadingsByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
//		init();	//init() is performed in the MR_CB method, no need to call it again.
		MR_CBSoap_BindingImpl mr_cb = new MR_CBSoap_BindingImpl();
        return mr_cb.getReadingsByMeterNo(meterNo, startDate, endDate);
    }

    public com.cannontech.multispeak.ArrayOfMeterRead getLatestReadings(java.lang.String lastReceived) throws java.rmi.RemoteException {
		init();
		String companyName = MultispeakFuncs.getCompanyNameFromSOAPHeader();
		MultispeakVendor vendor = Multispeak.getInstance().getMultispeakVendor(companyName);
		String key = (vendor != null ? vendor.getUniqueKey(): "meternumber");
				
		int maxSize = 10000;	//Max number of meters we'll send
		int startIndex = 0;
		int endIndex = 0;	//less than this index
				
		MeterRead[] meterReads;
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


		Vector vectorOfMeterReads = new Vector();
		
		meterReads = new MeterRead[endIndex - startIndex];					

		CTILogger.info("Returning Readings for " + meterReads.length + " of " + allMeters.size() + " Meters (" + startIndex + " through " + endIndex + ")");				
		for (int i = startIndex; i < endIndex; i++)
		{
			LiteDeviceMeterNumber ldmn = (LiteDeviceMeterNumber)allMeters.get(i);
			LiteYukonPAObject lPao = (LiteYukonPAObject)cache.getAllPAOsMap().get(new Integer(ldmn.getDeviceID()));
			String meterID = ""; 
			if( key.toLowerCase().startsWith("device") || key.toLowerCase().startsWith("pao"))
				meterID = lPao.getPaoName();
			else //if(key.toLowerCase().startsWith("meternum"))
				meterID = ldmn.getMeterNumber();
			
			MeterRead mr = new MeterRead();
			mr.setDeviceID(meterID);
			mr.setMeterNo(meterID);
			mr.setObjectID(meterID);

			LitePoint[] litePoints = PAOFuncs.getLitePointsForPAObject(lPao.getYukonID());
			for (int j = 0; j < litePoints.length; j++)
			{
				LitePoint lp = litePoints[j];
				if( lp.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT && lp.getPointOffset() == 1)	//kW
				{
					PointData pointData = PointChangeCache.getPointChangeCache().getValue(lp.getPointID());
					if( pointData != null)
					{
						mr.setKW(new Float(pointData.getValue()));
						GregorianCalendar cal = new GregorianCalendar();
						cal.setTime(pointData.getPointDataTimeStamp());
						mr.setKWDateTime(cal);
					}
				}
				else if ( lp.getPointType() == PointTypes.PULSE_ACCUMULATOR_POINT && lp.getPointOffset() == 1)	//kWh
				{
					PointData pointData = PointChangeCache.getPointChangeCache().getValue(lp.getPointID());
					if( pointData != null)
					{
						mr.setPosKWh(new BigInteger(String.valueOf(new Double(pointData.getValue()).intValue())));
						GregorianCalendar cal = new GregorianCalendar();
						cal.setTime(pointData.getPointDataTimeStamp());
						mr.setReadingDate(cal);
					}
				}
			}

			meterReads[indexCount++] = mr;
		}
		if( endIndex != allMeters.size())
			((YukonMultispeakMsgHeader)MessageContext.getCurrentContext().getResponseMessage().getSOAPEnvelope().getHeaderByName("http://www.multispeak.org", "MultiSpeakMsgHeader").getObjectValue()).setObjectsRemaining(new BigInteger(String.valueOf(allMeters.size()-endIndex)));
				    	
		return new ArrayOfMeterRead(meterReads);
    }

    public com.cannontech.multispeak.MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
//		init();	//init() is performed in the MR_CB method, no need to call it again.
		MR_CBSoap_BindingImpl mr_cb = new MR_CBSoap_BindingImpl();
		return mr_cb.getLatestReadingByMeterNo(meterNo);
    }

    public com.cannontech.multispeak.ArrayOfMeterRead getReadingsByUOMAndDate(java.lang.String uomData, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
		init();
        return null;
    }

	public ArrayOfHistoryLog getHistoryLogByMeterNo(String meterNo, Calendar startDate, Calendar endDate) throws RemoteException
	{
		init();
		return null;
	}

	public ArrayOfHistoryLog getHistoryLogsByDate(Calendar startDate, Calendar endDate, String lastReceived) throws RemoteException
	{
		init();
		return null;
	}

	public ArrayOfHistoryLog getHistoryLogsByMeterNoAndEventCode(String meterNo, EventCode eventCode, Calendar startDate, Calendar endDate) throws RemoteException
	{
		init();
		return null;
	}

	public ArrayOfHistoryLog getHistoryLogsByDateAndEventCode(EventCode eventCode, Calendar startDate, Calendar endDate, String lastReceived) throws RemoteException
	{
		init();
		return null;
	}
	
	private void init()
	{
		MultispeakFuncs.init();
	}
}

