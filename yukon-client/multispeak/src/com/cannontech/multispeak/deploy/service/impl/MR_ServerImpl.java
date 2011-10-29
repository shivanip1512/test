/**
 * MR_ServerSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service.impl;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.FormattedBlockProcessingService;
import com.cannontech.multispeak.dao.MeterReadProcessingService;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.dao.MspRawPointHistoryDao;
import com.cannontech.multispeak.dao.MspRawPointHistoryDao.ReadBy;
import com.cannontech.multispeak.deploy.service.Customer;
import com.cannontech.multispeak.deploy.service.CustomersAffectedByOutage;
import com.cannontech.multispeak.deploy.service.DomainMember;
import com.cannontech.multispeak.deploy.service.EndDeviceShipment;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.EventCode;
import com.cannontech.multispeak.deploy.service.FormattedBlock;
import com.cannontech.multispeak.deploy.service.HistoryLog;
import com.cannontech.multispeak.deploy.service.InHomeDisplay;
import com.cannontech.multispeak.deploy.service.InHomeDisplayExchange;
import com.cannontech.multispeak.deploy.service.MR_ServerSoap_PortType;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.MeterConnectivity;
import com.cannontech.multispeak.deploy.service.MeterExchange;
import com.cannontech.multispeak.deploy.service.MeterGroup;
import com.cannontech.multispeak.deploy.service.MeterRead;
import com.cannontech.multispeak.deploy.service.MspObject;
import com.cannontech.multispeak.deploy.service.PhaseCd;
import com.cannontech.multispeak.deploy.service.ServiceLocation;
import com.cannontech.multispeak.service.MspValidationService;
import com.cannontech.multispeak.service.MultispeakMeterService;
import com.cannontech.yukon.BasicServerConnection;
import com.google.common.collect.Iterables;

public class MR_ServerImpl implements MR_ServerSoap_PortType{

    private MultispeakMeterService multispeakMeterService;
    private MspMeterDao mspMeterDao;
    private MultispeakFuncs multispeakFuncs;
    private MspRawPointHistoryDao mspRawPointHistoryDao;
    private BasicServerConnection porterConnection;
    private MspValidationService mspValidationService;
    public Map<String, FormattedBlockProcessingService<Block>> readingTypesMap;
    public MeterDao meterDao;
    public AttributeService attributeService;
    public MeterReadProcessingService meterReadProcessingService;
    public DynamicDataSource dynamicDataSource;

    private void init() throws RemoteException {
        multispeakFuncs.init();
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
                                         "initiateMeterReadByMeterNumber",
                                         "initiateMeterReadByMeterNoAndType",
                                         "isAMRMeter",
                                         "getReadingsByDate",
                                         "getAMRSupportedMeters",
                                         "getLatestReadingByMeterNo",
                                         "getLatestReadings",
                                         "getLatestReadingByMeterNoAndType",
                                         "getLatestReadingByType",
                                         "getReadingsByMeterNo",
                                         "getReadingsByDateAndType",
                                         "getReadingsByMeterNoAndType",
                                         "getSupportedReadingTypes",
                                         "meterAddNotification",
                                         "meterRemoveNotification",
                                         "meterChangedNotification",
                                         "initiateUsageMonitoring",
                                         "cancelUsageMonitoring",
                                         "initiateDisconnectedStatus",
                                         "cancelDisconnectedStatus",
                                         "serviceLocationChangedNotification",
                                         "deleteMeterGroup",
                                         "establishMeterGroup",
                                         "insertMeterInMeterGroup",
                                         "removeMetersFromMeterGroup"};
        return multispeakFuncs.getMethods(MultispeakDefines.MR_Server_STR , methods);
    }
    
    
    @Override
    public String[] getDomainNames() throws java.rmi.RemoteException {
        init();
        String [] strings = new String[]{"Method Not Supported"};
        multispeakFuncs.logStrings(MultispeakDefines.MR_Server_STR, "getDomainNames", strings);
        return strings;
    }
    
    
    @Override
    public DomainMember[] getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        init();
        return new DomainMember[0];
    }
    
    @Override
    public Meter[] getAMRSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        List<Meter> meterList = null;
        Date timerStart = new Date();
        try {
            meterList = mspMeterDao.getAMRSupportedMeters(lastReceived, vendor.getMaxReturnRecords());
        } catch(NotFoundException nfe) {
            //Not an error, it could happen that there are no more entries.
        }
        
        updateObjectsRemaining(meterList.size(), vendor.getMaxReturnRecords());
        try {
            Meter lastObject = Iterables.getLast(meterList);
            updateLastSent(lastObject);
        } catch (NoSuchElementException e) {
            // skip...we don't have any to report.
        }

        Meter[] meters = new Meter[meterList.size()];
        meterList.toArray(meters);
        CTILogger.info("Returning " + meters.length + " AMR Supported Meters. (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");             

        return meters;
    }
    
    @Override
    public Meter[] getModifiedAMRMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public boolean isAMRMeter(java.lang.String meterNo) throws java.rmi.RemoteException {
        init();

        try {
            mspValidationService.isYukonMeterNumber(meterNo);
        }catch (RemoteException e){
            return false;
        }
        return true;
    }
    
    @Override
    public MeterRead[] getReadingsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
    	init();
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        List<MeterRead> meterReads = mspRawPointHistoryDao.retrieveMeterReads(ReadBy.NONE, 
                                                                          null, 	//get all
                                                                          startDate.getTime(), 
                                                                          endDate.getTime(), 
                                                                          lastReceived,
                                                                          vendor.getMaxReturnRecords());

        updateObjectsRemaining(meterReads.size(), vendor.getMaxReturnRecords());
        try {
            MeterRead lastObject = Iterables.getLast(meterReads);
            updateLastSent(lastObject);
        } catch (NoSuchElementException e) {
            // skip...we don't have any to report.
        }

        MeterRead[] meterReadArray = new MeterRead[meterReads.size()];
        meterReads.toArray(meterReadArray);
        return meterReadArray;
    }

    @Override
    public MeterRead[] getReadingsByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
        init(); //init is already performed on the call to isAMRMeter()
        
        //Validate the meterNo is a Yukon meterNumber
        mspValidationService.isYukonMeterNumber(meterNo);
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        List<MeterRead> meterReads = mspRawPointHistoryDao.retrieveMeterReads(ReadBy.METER_NUMBER, 
                                                                          meterNo, 
                                                                          startDate.getTime(), 
                                                                          endDate.getTime(), 
                                                                          null,
                                                                          vendor.getMaxReturnRecords());
        
        MeterRead[] meterReadArray = new MeterRead[meterReads.size()];
        meterReads.toArray(meterReadArray);
        return meterReadArray;
    }

    @Override
    public MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
        init(); //init is already performed on the call to isAMRMeter()

        //Validate the meterNo is a Yukon meterNumber
        com.cannontech.amr.meter.model.Meter meter = mspValidationService.isYukonMeterNumber(meterNo);
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        
        //Custom hack put in only for SEDC.  Performs an actual meter read instead of simply replying from the database.
        if ( vendor.getCompanyName().equalsIgnoreCase("SEDC") ) {
        	return multispeakMeterService.getLatestReadingInterrogate(vendor, meter, null);
        } else	{ //THIS SHOULD BE WHERE EVERYONE ELSE GOES!!!
            try {
                MeterRead meterRead = meterReadProcessingService.createMeterRead(meter);
    	        
                EnumSet<BuiltInAttribute> attributesToLoad = EnumSet.of(BuiltInAttribute.USAGE, BuiltInAttribute.PEAK_DEMAND);
    
                for (BuiltInAttribute attribute : attributesToLoad) {
                    try {
                        LitePoint litePoint = attributeService.getPointForAttribute(meter, attribute);
                        PointValueQualityHolder pointValueQualityHolder = dynamicDataSource.getPointValue(litePoint.getPointID());
                        if( pointValueQualityHolder != null && 
                                pointValueQualityHolder.getPointQuality() != PointQuality.Uninitialized) {
                            meterReadProcessingService.updateMeterRead(meterRead, attribute, pointValueQualityHolder);
                        }
                    } catch (IllegalUseOfAttribute e) {
                        //it's okay...just skip
                    }
                }
    	        return meterRead;
            } catch (DynamicDataAccessException e) {
                String message = "Connection to dispatch is invalid";
                CTILogger.error(message);
                throw new RemoteException(message);
            }
        }
    }
    
    @Override
    public FormattedBlock[] getReadingsByBillingCycle(java.lang.String billingCycle, java.util.Calendar billingDate, int kWhLookBack, int kWLookBack, int kWLookForward, java.lang.String lastReceived) throws java.rmi.RemoteException {
        /* TODO
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        MeterRead[] meterReads = multispeakFuncs.getMspRawPointHistoryDao().retrieveMeterReads(ReadBy.BILL_GROUP, billingCycle, startDate.getTime(), endDate.getTime(), lastReceived);
        //TODO = need to get the true number of meters remaining
        int numRemaining = (meterReads.length < MultispeakDefines.MAX_RETURN_RECORDS ? 0:1); //at least one item remaining, bad assumption.
        multispeakFuncs.getResponseHeader().setObjectsRemaining(new BigInteger(String.valueOf(numRemaining)));

        return meterReads;
        */
        throw new RemoteException("Method getReadingsByBillingCycle(java.lang.String billingCycle, java.util.Calendar billingDate, int kWhLookBack, int kWLookBack, int kWLookForward, java.lang.String lastReceived) is NOT supported.");
    }
    
    @Override
    public HistoryLog[] getHistoryLogByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public HistoryLog[] getHistoryLogsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public HistoryLog[] getHistoryLogsByMeterNoAndEventCode(java.lang.String meterNo, EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public HistoryLog[] getHistoryLogsByDateAndEventCode(EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public ErrorObject[] initiatePlannedOutage(String[] meterNos, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public ErrorObject[] cancelPlannedOutage(String[] meterNos) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public ErrorObject[] initiateUsageMonitoring(String[] meterNos) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeakMeterService.initiateUsageMonitoringStatus(vendor, meterNos);
        return errorObject;
    }
    
    @Override
    public ErrorObject[] cancelUsageMonitoring(String[] meterNos) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeakMeterService.cancelUsageMonitoringStatus(vendor, meterNos);
        return errorObject;
    }
    
    @Override
    public ErrorObject[] initiateDisconnectedStatus(String[] meterNos) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeakMeterService.initiateDisconnectedStatus(vendor, meterNos);
        return errorObject;
    }
    
    @Override
    public ErrorObject[] cancelDisconnectedStatus(String[] meterNos) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeakMeterService.cancelDisconnectedStatus(vendor, meterNos);
        return errorObject;
    }

    //Perform an actual read of the meter and return a CB_MR readingChangedNotification message for each meterNo
    @Override
    public ErrorObject[] initiateMeterReadByMeterNumber(String[] meterNos,
            String responseURL, String transactionID) throws RemoteException {
        init();
        ErrorObject[] errorObjects = new ErrorObject[0];
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        if ( ! porterConnection.isValid() ) {
            String message = "Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.";
            CTILogger.error(message);
            throw new RemoteException(message);
        }

        errorObjects = multispeakMeterService.meterReadEvent(vendor, meterNos, transactionID);

        multispeakFuncs.logErrorObjects(MultispeakDefines.MR_Server_STR, "initiateMeterReadByMeterNumberRequest", errorObjects);
        return errorObjects;
    }
    
    @Override
    public ErrorObject[] customerChangedNotification(Customer[] changedCustomers) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public ErrorObject[] serviceLocationChangedNotification(ServiceLocation[] changedServiceLocations) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeakMeterService.updateServiceLocation(vendor, changedServiceLocations);
        return errorObject;
    }
    
    @Override
    public ErrorObject[] meterChangedNotification(Meter[] changedMeters) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeakMeterService.changeMeterObject(vendor, changedMeters);
        return errorObject;
    }
    
    @Override
    public ErrorObject[] meterRemoveNotification(Meter[] removedMeters) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeakMeterService.removeMeterObject(vendor, removedMeters);
        return errorObject;
    }
    
    @Override
    public ErrorObject[] meterAddNotification(Meter[] addedMeters) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeakMeterService.addMeterObject(vendor, addedMeters);
        return errorObject;
    }

    @Override
    public ErrorObject deleteMeterGroup(String meterGroupID)
            throws RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        return multispeakMeterService.deleteGroup(meterGroupID, vendor);
    }

    @Override
    public ErrorObject[] establishMeterGroup(MeterGroup meterGroup)
            throws RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeakMeterService.addMetersToGroup(meterGroup, vendor);
        return errorObject;
    }

    @Override
    public FormattedBlock getLatestMeterReadingsByMeterGroup(String meterGroupID)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] initiateGroupMeterRead(String meterGroupName,
            String responseURL, String transactionID) throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] insertMeterInMeterGroup(String[] meterNumbers,
            String meterGroupID) throws RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        MeterGroup meterGroup = new MeterGroup();
        meterGroup.setMeterList(meterNumbers);
        meterGroup.setGroupName(meterGroupID);
        ErrorObject[] errorObject = multispeakMeterService.addMetersToGroup(meterGroup, vendor);
        return errorObject;
    }

    @Override
    public ErrorObject[] meterExchangeNotification(
            MeterExchange[] meterChangeout) throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] meterRetireNotification(Meter[] retiredMeters)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] removeMetersFromMeterGroup(String[] meterNumbers,
            String meterGroupID) throws RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeakMeterService.removeMetersFromGroup(meterGroupID, meterNumbers, vendor);
        return errorObject;
    }

    @Override
    public ErrorObject[] scheduleGroupMeterRead(String meterGroupName,
            Calendar timeToRead, String responseURL, String transactionID)
            throws RemoteException {
        init();
        return null;
    }
    @Override
    public FormattedBlock[] getReadingByMeterNumberFormattedBlock(String meterNumber, Calendar billingDate,
            int whLookBack, int lookBack, int lookForward, String lastReceived) throws RemoteException {
        init();
        return null;
    }
    @Override
    public FormattedBlock[] getReadingsByDateFormattedBlock(Calendar billingDate, int whLookBack, int lookBack,
            int lookForward, String lastReceived) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] inHomeDisplayAddNotification(InHomeDisplay[] addedIHDs) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] inHomeDisplayChangedNotification(InHomeDisplay[] changedIHDs) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] inHomeDisplayExchangeNotification(InHomeDisplayExchange[] IHDChangeout) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] inHomeDisplayRemoveNotification(InHomeDisplay[] removedIHDs) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] inHomeDisplayRetireNotification(InHomeDisplay[] retiredIHDs) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] updateServiceLocationDisplays(String servLocID) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] customersAffectedByOutageNotification(
            CustomersAffectedByOutage[] newOutages) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] endDeviceShipmentNotification(
            EndDeviceShipment shipment) throws RemoteException {
        init();
        return null;
    }

    @Override
    public MeterRead[] getLatestReadings(String lastReceived)
            throws RemoteException {
        init();
        Date timerStart = new Date();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        List<MeterRead> meterReads = mspRawPointHistoryDao.retrieveLatestMeterReads(ReadBy.NONE, null, lastReceived, vendor.getMaxReturnRecords());

        updateObjectsRemaining(meterReads.size(), vendor.getMaxReturnRecords());
        try {
            MeterRead lastObject = Iterables.getLast(meterReads);
            updateLastSent(lastObject);
        } catch (NoSuchElementException e) {
            // skip...we don't have any to report.
        }

        CTILogger.info("Returning " + meterReads.size() + " MeterReads. (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");
        
        MeterRead[] meterReadArray = new MeterRead[meterReads.size()];
        meterReads.toArray(meterReadArray);
        return meterReadArray;
    }
    
    @Override
    public FormattedBlock getLatestReadingByMeterNoAndType(String meterNo, String readingType) throws RemoteException {
        init();
        com.cannontech.amr.meter.model.Meter meter = mspValidationService.isYukonMeterNumber(meterNo);
        
        FormattedBlockProcessingService<Block> formattedBlockProcessingService = 
            mspValidationService.isValidBlockReadingType(readingTypesMap, readingType);
        
        try {
            Block block = formattedBlockProcessingService.createBlock(meter);
            
            EnumSet<BuiltInAttribute> attributesToLoad = formattedBlockProcessingService.getAttributeSet();

            for (BuiltInAttribute attribute : attributesToLoad) {
                try {
                    LitePoint litePoint = attributeService.getPointForAttribute(meter, attribute);
                    PointValueQualityHolder pointValueQualityHolder = dynamicDataSource.getPointValue(litePoint.getPointID());
                    if( pointValueQualityHolder != null && 
                            pointValueQualityHolder.getPointQuality() != PointQuality.Uninitialized) {
                        formattedBlockProcessingService.updateFormattedBlock(block, attribute, pointValueQualityHolder);
                    }
                } catch (IllegalUseOfAttribute e) {
                    //it's okay...just skip
                }
            }
            FormattedBlock formattedBlock = formattedBlockProcessingService.createMspFormattedBlock(block);
            return formattedBlock;
            
        } catch (DynamicDataAccessException e) {
            String message = "Connection to dispatch is invalid";
            CTILogger.error(message);
            throw new RemoteException(message);
        }
    }
    
    @Override
    public FormattedBlock[] getLatestReadingByType(String readingType, String lastReceived) throws RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        FormattedBlockProcessingService<Block> formattedBlockProcessingService = 
            mspValidationService.isValidBlockReadingType(readingTypesMap, readingType);

        List<Block> blocks = mspRawPointHistoryDao.retrieveLatestBlock(formattedBlockProcessingService, 
                                                                       lastReceived, vendor.getMaxReturnRecords());
        
        FormattedBlock formattedBlock = formattedBlockProcessingService.createMspFormattedBlock(blocks);
        FormattedBlock[] formattedBlocks = new FormattedBlock[]{formattedBlock};

        // compare to 1 instead of maxRecords. We don't know how many paos are actually in blocks. This may result in 1 extra call from other system but that's the best we can do
        updateObjectsRemaining(blocks.size(), 1);
        try {
            Block lastObject = Iterables.getLast(blocks);
            updateLastSent(lastObject);
        } catch (NoSuchElementException e) {
            // skip...we don't have any to report.
        }
        return formattedBlocks;
    }
    
    @Override
    public FormattedBlock[] getReadingsByDateAndType(Calendar startDate, Calendar endDate, String readingType, String lastReceived) throws RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        FormattedBlockProcessingService<Block> formattedBlockProcessingService = 
            mspValidationService.isValidBlockReadingType(readingTypesMap, readingType);

        List<Block> blocks = mspRawPointHistoryDao.retrieveBlock(ReadBy.NONE, null,
                                                                 formattedBlockProcessingService,
                                                                 startDate.getTime(),
                                                                 endDate.getTime(),
                                                                 lastReceived,
                                                                 vendor.getMaxReturnRecords());

        FormattedBlock formattedBlock = formattedBlockProcessingService.createMspFormattedBlock(blocks);
        FormattedBlock[] formattedBlocks = new FormattedBlock[]{formattedBlock};

        // compare to 1 instead of maxRecords. We don't know how many paos are actually in blocks. This may result in 1 extra call from other system but that's t
        updateObjectsRemaining(blocks.size(), 1);
        try {
            Block lastObject = Iterables.getLast(blocks);
            updateLastSent(lastObject);
        } catch (NoSuchElementException e) {
            // skip...we don't have any to report.
        }
        return formattedBlocks;
    }

    @Override
    public FormattedBlock[] getReadingsByMeterNoAndType(String meterNo, Calendar startDate, Calendar endDate, String readingType, String lastReceived) throws RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        
        //Validate the meterNo is in Yukon
        mspValidationService.isYukonMeterNumber(meterNo); 

        FormattedBlockProcessingService<Block> formattedBlockProcessingService = 
            mspValidationService.isValidBlockReadingType(readingTypesMap, readingType);
        
        List<Block> blocks = mspRawPointHistoryDao.retrieveBlock(ReadBy.METER_NUMBER, meterNo,
                                                                 formattedBlockProcessingService,
                                                                 startDate.getTime(),
                                                                 endDate.getTime(),
                                                                 null,  //don't use lastReceived, we know there is only one
                                                                 vendor.getMaxReturnRecords());

        FormattedBlock formattedBlock = formattedBlockProcessingService.createMspFormattedBlock(blocks);
        FormattedBlock[] formattedBlocks = new FormattedBlock[]{formattedBlock};
     
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
        
        FormattedBlockProcessingService<Block> formattedBlockServ = 
            mspValidationService.isValidBlockReadingType(readingTypesMap, readingType);
        
        errorObjects = multispeakMeterService.blockMeterReadEvent(vendor, meterNo, formattedBlockServ);

        multispeakFuncs.logErrorObjects(MultispeakDefines.MR_Server_STR, "initiateMeterReadByMeterNumberRequest", errorObjects);
        return errorObjects;
    }
    @Override
    public MeterRead[] getReadingsByUOMAndDate(String uomData,
            Calendar startDate, Calendar endDate, String lastReceived)
            throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] initiateMeterReadByObject(String objectName,
            String nounType, PhaseCd phaseCode, String responseURL,
            String transactionID) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] meterConnectivityNotification(
            MeterConnectivity[] newConnectivity) throws RemoteException {
        init();
        return null;
    }

    /**
     * Helper method to update responseHeader.lastSent
     * @param block
     * @throws RemoteException
     */
	private void updateLastSent(Block block) throws RemoteException {
	    multispeakFuncs.getResponseHeader().setLastSent(block.getObjectId());
	}
 
    /**
     * Helper method to update responseHeader.lastSent
     * @param lastObject
     * @throws RemoteException
     */
    private void updateLastSent(MspObject lastObject) throws RemoteException{
        multispeakFuncs.getResponseHeader().setLastSent(lastObject.getObjectID());
    }
    
    /**
     * Helper method to update responseHeader.objectsRemaining
     * @param returnResultsSize
     * @param vendor
     * @return
     */
    private void updateObjectsRemaining(int returnResultsSize, int maxReturnRecords) throws RemoteException {
        int numberRemaining = (returnResultsSize < maxReturnRecords ? 0 : -1); //assuming at least one item remaining. -1 for unknown size
        multispeakFuncs.getResponseHeader().setObjectsRemaining(new BigInteger(String.valueOf(numberRemaining)));
    }
	
    @Autowired
    public void setMspMeterDao(MspMeterDao mspMeterDao) {
        this.mspMeterDao = mspMeterDao;
    }
    @Autowired
    public void setMultispeakMeterService(
			MultispeakMeterService multispeakMeterService) {
		this.multispeakMeterService = multispeakMeterService;
	}
    @Autowired
    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }
    @Autowired
    public void setMspRawPointHistoryDao(MspRawPointHistoryDao mspRawPointHistoryDao) {
        this.mspRawPointHistoryDao = mspRawPointHistoryDao;
    }
    @Required
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }
    @Autowired
    public void setMspValidationService(
            MspValidationService mspValidationService) {
        this.mspValidationService = mspValidationService;
    }
    @Required
    public void setReadingTypesMap(
            Map<String, FormattedBlockProcessingService<Block>> readingTypesMap) {
        this.readingTypesMap = readingTypesMap;
    }
    @Autowired
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    @Autowired
    public void setMeterReadProcessingService(MeterReadProcessingService meterReadProcessingService) {
        this.meterReadProcessingService = meterReadProcessingService;
    }
    @Autowired
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }
}