package com.cannontech.multispeak.deploy.service.impl;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.block.FormattedBlockService;
import com.cannontech.multispeak.client.Multispeak;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspMeterReadDao;
import com.cannontech.multispeak.dao.MspRawPointHistoryDao;
import com.cannontech.multispeak.deploy.service.DomainMember;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.EventCode;
import com.cannontech.multispeak.deploy.service.FormattedBlock;
import com.cannontech.multispeak.deploy.service.HistoryLog;
import com.cannontech.multispeak.deploy.service.MR_CBSoap_PortType;
import com.cannontech.multispeak.deploy.service.MR_EASoap_PortType;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.MeterRead;
import com.cannontech.multispeak.deploy.service.PhaseCd;
import com.cannontech.multispeak.service.MspValidationService;
import com.cannontech.yukon.BasicServerConnection;

public class MR_EAImpl implements MR_EASoap_PortType
{
    public MultispeakFuncs multispeakFuncs;
    public MspMeterReadDao mspMeterReadDao;
    public MR_CBSoap_PortType mr_cb;
    public Map<String, FormattedBlockService> readingTypesMap;
    private BasicServerConnection porterConnection;
    public Multispeak multispeak;
    public MspRawPointHistoryDao mspRawPointHistoryDao;
    public MspValidationService mspValidationService;
    public MeterDao meterDao;    
    
    @Required
    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }
    @Required
    public void setMspMeterReadDao(MspMeterReadDao mspMeterReadDao) {
        this.mspMeterReadDao = mspMeterReadDao;
    }
    @Required
    public void setMr_cb(MR_CBSoap_PortType mr_cb) {
        this.mr_cb = mr_cb;
    }
    @Required
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }
    @Required
    public void setMultispeak(Multispeak multispeak) {
        this.multispeak = multispeak;
    }
    @Required
    public void setMspRawPointHistoryDao(
            MspRawPointHistoryDao mspRawPointHistoryDao) {
        this.mspRawPointHistoryDao = mspRawPointHistoryDao;
    }
    @Required
    public void setMspValidationService(
            MspValidationService mspValidationService) {
        this.mspValidationService = mspValidationService;
    }
    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    
    private void init() throws RemoteException {
        multispeakFuncs.init();
    }

    public void setReadingTypesMap(
            Map<String, FormattedBlockService> readingTypesMap) {
        this.readingTypesMap = readingTypesMap;
    }
    
    @Override
    public ErrorObject[] pingURL() throws java.rmi.RemoteException {
        init();
        return new ErrorObject[0];
    }
    
    @Override
    public String[] getMethods() throws java.rmi.RemoteException {
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
    
    @Override
    public String[] getDomainNames() throws java.rmi.RemoteException {
        init();
        String [] strings = new String[]{"Method Not Supported"};
        multispeakFuncs.logStrings(MultispeakDefines.MR_EA_STR, "getDomainNames", strings);
        return strings;
    }   
    
    @Override
    public DomainMember[] getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        init();
        return new DomainMember[0];
    }

    
    /**
     * This method is a duplicate, refer to the MR_CB method so only one must be implemented  
     */
    @Override
    public Meter[] getAMRSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
//      init(); //init() is performed in the MR_CB method, no need to call it again.
        return mr_cb.getAMRSupportedMeters(lastReceived);
    }
    
    @Override
    public Meter[] getModifiedAMRMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public MeterRead[] getReadingsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return null;
    }

    /**
     * This method is a duplicate, refer to the MR_CB method so only one must be implemented  
     */    
    @Override
    public MeterRead[] getReadingsByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
//      init(); //init() is performed in the MR_CB method, no need to call it again.
        return mr_cb.getReadingsByMeterNo(meterNo, startDate, endDate);
    }
    
    @Override
    public MeterRead[] getLatestReadings(java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        Date timerStart = new Date();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
                
        List<com.cannontech.amr.meter.model.Meter> meters = meterDao.getMetersByMeterNumber(lastReceived, 
                                                             vendor.getMaxReturnRecords());
        
        MeterRead[] meterReads = mspMeterReadDao.getMeterRead(meters);

        int numRemaining = (meterReads.length < vendor.getMaxReturnRecords() ? 0:1); //at least one item remaining, bad assumption.
        multispeakFuncs.getResponseHeader().setObjectsRemaining(new BigInteger(String.valueOf(numRemaining)));
        
        CTILogger.info("Returning " + meters.size() + " MeterReads. (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");
        return meterReads;
    }
    
    @Override
    public MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
//      init(); //init() is performed in the MR_CB method, no need to call it again.
        return mr_cb.getLatestReadingByMeterNo(meterNo);
    }
    
    @Override
    public MeterRead[] getReadingsByUOMAndDate(java.lang.String uomData, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public HistoryLog[] getHistoryLogByMeterNo(String meterNo, Calendar startDate, Calendar endDate) throws RemoteException {
        init();
        return null;
    }
    
    @Override
    public HistoryLog[] getHistoryLogsByDate(Calendar startDate, Calendar endDate, String lastReceived) throws RemoteException {
        init();
        return null;
    }
    
    @Override
    public HistoryLog[] getHistoryLogsByMeterNoAndEventCode(String meterNo, EventCode eventCode, Calendar startDate, Calendar endDate) throws RemoteException {
        init();
        return null;
    }
    
    @Override
    public HistoryLog[] getHistoryLogsByDateAndEventCode(EventCode eventCode, Calendar startDate, Calendar endDate, String lastReceived) throws RemoteException {
        init();
        return null;
    }
    
    @Override
    public FormattedBlock getLatestReadingByMeterNoAndType(String meterNo, String readingType) throws RemoteException {
        init();
        com.cannontech.amr.meter.model.Meter meter = mspValidationService.isYukonMeterNumber(meterNo);
        return readingTypesMap.get(readingType).getFormattedBlock(meter);
    }
    
    @Override
    public FormattedBlock[] getLatestReadingByType(String readingType, String lastReceived) throws RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        List<com.cannontech.amr.meter.model.Meter> meters = meterDao.getMetersByMeterNumber(lastReceived, 
                                                                                            vendor.getMaxReturnRecords());
        
        FormattedBlock formattedBlock = readingTypesMap.get(readingType).getFormattedBlock(meters);
        FormattedBlock[] formattedBlockArray = new FormattedBlock[]{formattedBlock};
        return formattedBlockArray;
    }
    
    @Override
    public FormattedBlock[] getReadingsByDateAndType(Calendar startDate, Calendar endDate, String readingType, String lastReceived) throws RemoteException {
        init();
        FormattedBlockService<Block> formattedBlock = 
            mspValidationService.isValidBlockReadingType(readingTypesMap, readingType);
        
        FormattedBlock mspBlock = mspRawPointHistoryDao.retrieveBlock(formattedBlock, startDate.getTime(), endDate.getTime(), lastReceived);
        FormattedBlock[] formattedBlocks = new FormattedBlock[]{mspBlock};
     
        return formattedBlocks;
    }

    @Override
    public FormattedBlock[] getReadingsByMeterNoAndType(String meterNo, Calendar startDate, Calendar endDate, String readingType, String lastReceived) throws RemoteException {
        init();
        //Validate the meterNo is in Yukon
        mspValidationService.isYukonMeterNumber(meterNo); 

        FormattedBlockService<Block> formattedBlock = 
            mspValidationService.isValidBlockReadingType(readingTypesMap, readingType);
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        FormattedBlock mspBlock = mspRawPointHistoryDao.retrieveBlockByMeterNo(formattedBlock, 
                                                                               startDate.getTime(), 
                                                                               endDate.getTime(),
                                                                               meterNo,
                                                                               vendor.getMaxReturnRecords());
        FormattedBlock[] formattedBlocks = new FormattedBlock[]{mspBlock};
     
        return formattedBlocks;
    }
    
    @Override
    public String[] getSupportedReadingTypes() throws RemoteException {
        init();
        Set<String> keys = readingTypesMap.keySet();
        String[] types = new String[keys.size()];
        keys.toArray(types);

        return types;
    }
    
    @Override
    public ErrorObject[] initiateMeterReadByMeterNoAndType(String meterNo, String responseURL, 
            String readingType, String transactionID) throws RemoteException {
        init();
        ErrorObject[] errorObjects = new ErrorObject[0];
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        if ( ! porterConnection.isValid() ) {
            String message = "Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.";
            CTILogger.error(message);                
            throw new RemoteException(message);
        }
        
        FormattedBlockService<Block> formattedBlock = 
            mspValidationService.isValidBlockReadingType(readingTypesMap, readingType);
        errorObjects = multispeak.BlockMeterReadEvent(vendor, meterNo, 
                                                      formattedBlock, transactionID);

        multispeakFuncs.logErrorObjects(MultispeakDefines.MR_CB_STR, "initiateMeterReadByMeterNumberRequest", errorObjects);
        return errorObjects;
    }

    @Override
    public ErrorObject[] initiateMeterReadByObject(String objectName,
            String nounType, PhaseCd phaseCode, String responseURL,
            String transactionID) throws RemoteException {
        init();
        return null;
    }
}