package com.cannontech.multispeak.service.impl;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.block.YukonFormattedBlock;
import com.cannontech.multispeak.client.Multispeak;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspMeterReadDao;
import com.cannontech.multispeak.dao.MspRawPointHistoryDao;
import com.cannontech.multispeak.service.ArrayOfDomainMember;
import com.cannontech.multispeak.service.ArrayOfErrorObject;
import com.cannontech.multispeak.service.ArrayOfFormattedBlock;
import com.cannontech.multispeak.service.ArrayOfHistoryLog;
import com.cannontech.multispeak.service.ArrayOfMeter;
import com.cannontech.multispeak.service.ArrayOfMeterRead;
import com.cannontech.multispeak.service.ArrayOfString;
import com.cannontech.multispeak.service.DomainMember;
import com.cannontech.multispeak.service.ErrorObject;
import com.cannontech.multispeak.service.EventCode;
import com.cannontech.multispeak.service.FormattedBlock;
import com.cannontech.multispeak.service.MR_CBSoap_PortType;
import com.cannontech.multispeak.service.MR_EASoap_PortType;
import com.cannontech.multispeak.service.MeterRead;
import com.cannontech.yukon.BasicServerConnection;

public class MR_EAImpl implements MR_EASoap_PortType
{
    public MultispeakFuncs multispeakFuncs;
    public MspMeterReadDao mspMeterReadDao;
    public MR_CBSoap_PortType mr_cb;
    public Map<String, YukonFormattedBlock> readingTypesMap;
    private BasicServerConnection porterConnection;
    public Multispeak multispeak;
    public MspRawPointHistoryDao mspRawPointHistoryDao;
    
    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }

    public void setMspMeterReadDao(MspMeterReadDao mspMeterReadDao) {
        this.mspMeterReadDao = mspMeterReadDao;
    }
    
    public void setMr_cb(MR_CBSoap_PortType mr_cb) {
        this.mr_cb = mr_cb;
    }
    
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }
    
    public void setMultispeak(Multispeak multispeak) {
        this.multispeak = multispeak;
    }
    
    public void setMspRawPointHistoryDao(
            MspRawPointHistoryDao mspRawPointHistoryDao) {
        this.mspRawPointHistoryDao = mspRawPointHistoryDao;
    }
    
    private void init() {
        multispeakFuncs.init();
    }

    public void setReadingTypesMap(
            Map<String, YukonFormattedBlock> readingTypesMap) {
        this.readingTypesMap = readingTypesMap;
    }
    
    public ArrayOfErrorObject pingURL() throws java.rmi.RemoteException {
        init();
        return new ArrayOfErrorObject(new ErrorObject[0]);
    }

    public ArrayOfString getMethods() throws java.rmi.RemoteException {
        init();
        String [] methods = new String[]{"pingURL", "getMethods", 
                                         "getAMRSupportedMeters", 
                                         "getLatestReadingByMeterNo",
                                         "getReadingsByMeterNo", 
                                         "getLatestReadings",
                                         "getLatestReadingByMeterNoAndType",
                                         "getLatestReadingByType",
                                         "getReadingsByDateAndType",
                                         "getReadingsByMeterNoAndType",
                                         "getSupportedReadingTypes",
                                         "initiateMeterReadByMeterNoAndType"};
        
        return multispeakFuncs.getMethods(MultispeakDefines.MR_EA_STR, methods );
    }

    public ArrayOfString getDomainNames() throws java.rmi.RemoteException {
        init();
        String [] strings = new String[]{"Method Not Supported"};
        multispeakFuncs.logArrayOfString(MultispeakDefines.MR_EA_STR, "getDomainNames", strings);
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
        return mr_cb.getReadingsByMeterNo(meterNo, startDate, endDate);
    }

    public ArrayOfMeterRead getLatestReadings(java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        Date timerStart = new Date();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
                
        List<com.cannontech.amr.meter.model.Meter> meters = multispeakFuncs.getMeters(vendor.getUniqueKey(), 
                                                                                      lastReceived,
                                                                                      vendor.getMaxReturnRecords());
        MeterRead[] meterReads = mspMeterReadDao.getMeterRead(meters, vendor.getUniqueKey());

        int numRemaining = (meterReads.length < vendor.getMaxReturnRecords() ? 0:1); //at least one item remaining, bad assumption.
        multispeakFuncs.getResponseHeader().setObjectsRemaining(new BigInteger(String.valueOf(numRemaining)));
        
        CTILogger.info("Returning " + meters.size() + " MeterReads. (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");
        return new ArrayOfMeterRead(meterReads);
    }

    public MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
//      init(); //init() is performed in the MR_CB method, no need to call it again.
        return mr_cb.getLatestReadingByMeterNo(meterNo);
    }

    public ArrayOfMeterRead getReadingsByUOMAndDate(java.lang.String uomData, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return null;
    }

    public ArrayOfHistoryLog getHistoryLogByMeterNo(String meterNo, Calendar startDate, Calendar endDate) throws RemoteException {
        init();
        return null;
    }

    public ArrayOfHistoryLog getHistoryLogsByDate(Calendar startDate, Calendar endDate, String lastReceived) throws RemoteException {
        init();
        return null;
    }

    public ArrayOfHistoryLog getHistoryLogsByMeterNoAndEventCode(String meterNo, EventCode eventCode, Calendar startDate, Calendar endDate) throws RemoteException {
        init();
        return null;
    }

    public ArrayOfHistoryLog getHistoryLogsByDateAndEventCode(EventCode eventCode, Calendar startDate, Calendar endDate, String lastReceived) throws RemoteException {
        init();
        return null;
    }

    public FormattedBlock getLatestReadingByMeterNoAndType(String meterNo, String readingType) throws RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        Meter meter;
        try {
            meter = multispeakFuncs.getMeter(vendor.getUniqueKey(), meterNo);
        } catch (NotFoundException e) {
            throw new RemoteException( "Meter Number (" + meterNo + "): NOT Found.");
        }

        return readingTypesMap.get(readingType).getFormattedBlock(meter);
    }

    public ArrayOfFormattedBlock getLatestReadingByType(String readingType, String lastReceived) throws RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        List<com.cannontech.amr.meter.model.Meter> meters = multispeakFuncs.getMeters(vendor.getUniqueKey(), 
                                                                                      lastReceived,
                                                                                      vendor.getMaxReturnRecords());
        
        FormattedBlock formattedBlock = readingTypesMap.get(readingType).getFormattedBlock(meters);
        FormattedBlock[] formattedBlockArray = new FormattedBlock[]{formattedBlock};
        return new ArrayOfFormattedBlock(formattedBlockArray);
    }

    public ArrayOfFormattedBlock getReadingsByDateAndType(Calendar startDate, Calendar endDate, String readingType, String lastReceived) throws RemoteException {
        init();
        YukonFormattedBlock<Block> formattedBlock = readingTypesMap.get(readingType);
        if( formattedBlock == null)
            throw new RemoteException(readingType + " is NOT a supported ReadingType.");
        
        FormattedBlock mspBlock = mspRawPointHistoryDao.retrieveBlock(formattedBlock, startDate.getTime(), endDate.getTime(), lastReceived);
        ArrayOfFormattedBlock arrayOfFormattedBlock = new ArrayOfFormattedBlock(new FormattedBlock[]{mspBlock});
     
        return arrayOfFormattedBlock;
    }

    public ArrayOfFormattedBlock getReadingsByMeterNoAndType(String meterNo, Calendar startDate, Calendar endDate, String readingType, String lastReceived) throws RemoteException {
//        init();   //already handled in isAMRMeter
        if( ! mr_cb.isAMRMeter(meterNo))
            throw new RemoteException( "Meter Number (" + meterNo + "): NOT Found.");
        
        YukonFormattedBlock<Block> formattedBlock = readingTypesMap.get(readingType);
        if( formattedBlock == null)
            throw new RemoteException(readingType + " is NOT a supported ReadingType.");
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        FormattedBlock mspBlock = mspRawPointHistoryDao.retrieveBlockByMeterNo(formattedBlock, 
                                                                               startDate.getTime(), 
                                                                               endDate.getTime(),
                                                                               meterNo,
                                                                               vendor.getMaxReturnRecords());
        ArrayOfFormattedBlock arrayOfFormattedBlock = new ArrayOfFormattedBlock(new FormattedBlock[]{mspBlock});
     
        return arrayOfFormattedBlock;
    }
    
    public ArrayOfString getSupportedReadingTypes() throws RemoteException {
        init();
        Set<String> keys = readingTypesMap.keySet();
        String[] types = new String[keys.size()];
        keys.toArray(types);

        return new ArrayOfString(types);
    }

    public ArrayOfErrorObject initiateMeterReadByMeterNoAndType(ArrayOfString meterNos, String responseURL, String readingType) throws RemoteException {
        init();
        ErrorObject[] errorObjects = new ErrorObject[0];
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        if ( ! porterConnection.isValid() ) {
            throw new RemoteException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
        }
        
        if( meterNos.getString().length >= vendor.getMaxInitiateRequestObjects()){
            throw new RemoteException("Maximum number of MeterNos initiated exceeds limit.  Initiate request cancelled.  Limit set to " + vendor.getMaxInitiateRequestObjects());
        }
        
        YukonFormattedBlock<Block> formattedBlock = readingTypesMap.get(readingType);
        if( formattedBlock == null)
            throw new RemoteException(readingType + " is NOT a supported ReadingType.");
        errorObjects = multispeak.BlockMeterReadEvent(vendor, meterNos.getString(), formattedBlock);

        multispeakFuncs.logArrayOfErrorObjects(MultispeakDefines.MR_CB_STR, "initiateMeterReadByMeterNumberRequest", errorObjects);
        return new ArrayOfErrorObject(errorObjects);
    }
}