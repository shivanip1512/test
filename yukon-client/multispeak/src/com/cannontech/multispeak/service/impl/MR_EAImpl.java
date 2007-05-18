package com.cannontech.multispeak.service.impl;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.service.ArrayOfDomainMember;
import com.cannontech.multispeak.service.ArrayOfErrorObject;
import com.cannontech.multispeak.service.ArrayOfHistoryLog;
import com.cannontech.multispeak.service.ArrayOfMeter;
import com.cannontech.multispeak.service.ArrayOfMeterRead;
import com.cannontech.multispeak.service.ArrayOfString;
import com.cannontech.multispeak.service.DomainMember;
import com.cannontech.multispeak.service.EventCode;
import com.cannontech.multispeak.service.MR_CBSoap_BindingImpl;
import com.cannontech.multispeak.service.MR_EASoap_BindingImpl;
import com.cannontech.multispeak.service.MeterRead;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

public class MR_EAImpl extends MR_EASoap_BindingImpl
{
    public MultispeakDao multispeakDao;
    
    /**
     * @param multispeakDao The multispeakDao to set.
     */
    public void setMultispeakDao(MultispeakDao multispeakDao)
    {
        this.multispeakDao = multispeakDao;
    }

    public ArrayOfErrorObject pingURL() throws java.rmi.RemoteException {
        init();
        return MultispeakFuncs.pingURL(MultispeakDefines.MR_EA_STR);
    }

    public ArrayOfString getMethods() throws java.rmi.RemoteException {
        init();
        String [] methods = new String[]{"pingURL", "getMethods", 
                                         "getAMRSupportedMeters", "getLatestReadingByMeterNo",
                                         "getReadingsByMeterNo", "getLatestReadings"};
        return MultispeakFuncs.getMethods(MultispeakDefines.MR_EA_STR, methods );
    }

    public ArrayOfString getDomainNames() throws java.rmi.RemoteException {
        init();
        String [] strings = new String[]{"Method Not Supported"};
        MultispeakFuncs.logArrayOfString(MultispeakDefines.MR_EA_STR, "getDomainNames", strings);
        return new ArrayOfString(strings);
    }   

    public ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        init();
        return new ArrayOfDomainMember(new DomainMember[0]);
    }

    /**
     * This method is a duplicate, refer to the MR_CB method so only one must be implemented  
     */
    public ArrayOfMeter getAMRSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
//      init(); //init() is performed in the MR_CB method, no need to call it again.
        MR_CBSoap_BindingImpl mr_cb = new MR_CBSoap_BindingImpl();
        return mr_cb.getAMRSupportedMeters(lastReceived);
    }

    public ArrayOfMeter getModifiedAMRMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return null;
    }

    public ArrayOfMeterRead getReadingsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return null;
    }

    /**
     * This method is a duplicate, refer to the MR_CB method so only one must be implemented  
     */
    public ArrayOfMeterRead getReadingsByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
//      init(); //init() is performed in the MR_CB method, no need to call it again.
        MR_CBSoap_BindingImpl mr_cb = new MR_CBSoap_BindingImpl();
        return mr_cb.getReadingsByMeterNo(meterNo, startDate, endDate);
    }

    public ArrayOfMeterRead getLatestReadings(java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = MultispeakFuncs.getMultispeakVendorFromHeader();
        String key = (vendor != null ? vendor.getUniqueKey(): "meternumber");
                
        int maxSize = 10000;    //Max number of meters we'll send
        int startIndex = 0;
        int endIndex = 0;   //less than this index
                
        MeterRead[] meterReads;
        int indexCount = 0;
        
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        List allMeters = cache.getAllDeviceMeterGroups();
                
        if( lastReceived != null && lastReceived.length() > 0)
        {
            for (int i = 0; i < allMeters.size(); i++)
            {
                LiteDeviceMeterNumber ldmn = (LiteDeviceMeterNumber)allMeters.get(i);
                String lastValue = "";
                if( key.toLowerCase().startsWith("device") || key.toLowerCase().startsWith("pao"))
                    lastValue = DaoFactory.getPaoDao().getYukonPAOName(ldmn.getDeviceID());
                else //if(key.toLowerCase().startsWith("meternum"))
                    lastValue = ldmn.getMeterNumber();
                            
                if( lastValue.compareTo(lastReceived) == 0)
                {
                    startIndex = i + 1;
                    endIndex = (startIndex+maxSize > allMeters.size()? allMeters.size(): startIndex+maxSize);   //index is the lesser of allMeters.size or startIndex+maxSize
                    break;
                }
            }
        }
        if( endIndex == 0)  //haven't set this value yet, so set it now.
            endIndex = (maxSize> allMeters.size()? allMeters.size(): maxSize);  //index is the lesser of allMeters.size or 1000


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

            List<LitePoint> litePoints = DaoFactory.getPointDao().getLitePointsByPaObjectId(lPao.getYukonID());
            for (LitePoint lp : litePoints) {
                if( lp.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT && lp.getPointOffset() == 1)   //kW
                {
                    DynamicDataSource dds = (DynamicDataSource) YukonSpringHook.getBean("dynamicDataSource");
                    PointValueHolder pointData = dds.getPointValue(lp.getPointID());
                    if( pointData != null)
                    {
                        mr.setKW(new Float(pointData.getValue()));
                        GregorianCalendar cal = new GregorianCalendar();
                        cal.setTime(pointData.getPointDataTimeStamp());
                        mr.setKWDateTime(cal);
                    }
                }
                else if ( lp.getPointType() == PointTypes.PULSE_ACCUMULATOR_POINT && lp.getPointOffset() == 1)  //kWh
                {
                    DynamicDataSource dds = (DynamicDataSource) YukonSpringHook.getBean("dynamicDataSource");
                    PointValueHolder pointData = dds.getPointValue(lp.getPointID());
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
            MultispeakFuncs.getResponseHeader().setObjectsRemaining(new BigInteger(String.valueOf(allMeters.size()-endIndex)));
                        
        return new ArrayOfMeterRead(meterReads);
    }

    public MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
//      init(); //init() is performed in the MR_CB method, no need to call it again.
        MR_CBSoap_BindingImpl mr_cb = new MR_CBSoap_BindingImpl();
        return mr_cb.getLatestReadingByMeterNo(meterNo);
    }

    public ArrayOfMeterRead getReadingsByUOMAndDate(java.lang.String uomData, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
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
