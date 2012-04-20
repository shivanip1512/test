/**
 * MR_ServerSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service.impl;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.point.PointQuality;
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
import com.cannontech.multispeak.data.MspBlockReturnList;
import com.cannontech.multispeak.data.MspMeterReadReturnList;
import com.cannontech.multispeak.data.MspMeterReturnList;
import com.cannontech.multispeak.deploy.service.Customer;
import com.cannontech.multispeak.deploy.service.CustomersAffectedByOutage;
import com.cannontech.multispeak.deploy.service.DomainMember;
import com.cannontech.multispeak.deploy.service.DomainNameChange;
import com.cannontech.multispeak.deploy.service.EndDeviceShipment;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.EventCode;
import com.cannontech.multispeak.deploy.service.FormattedBlock;
import com.cannontech.multispeak.deploy.service.FormattedBlockTemplate;
import com.cannontech.multispeak.deploy.service.HistoryLog;
import com.cannontech.multispeak.deploy.service.InHomeDisplay;
import com.cannontech.multispeak.deploy.service.InHomeDisplayExchange;
import com.cannontech.multispeak.deploy.service.MR_ServerSoap_PortType;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.MeterBase;
import com.cannontech.multispeak.deploy.service.MeterBaseExchange;
import com.cannontech.multispeak.deploy.service.MeterConnectivity;
import com.cannontech.multispeak.deploy.service.MeterExchange;
import com.cannontech.multispeak.deploy.service.MeterGroup;
import com.cannontech.multispeak.deploy.service.MeterIdentifier;
import com.cannontech.multispeak.deploy.service.MeterRead;
import com.cannontech.multispeak.deploy.service.PhaseCd;
import com.cannontech.multispeak.deploy.service.ReadingSchedule;
import com.cannontech.multispeak.deploy.service.RegistrationInfo;
import com.cannontech.multispeak.deploy.service.Schedule;
import com.cannontech.multispeak.deploy.service.ServiceLocation;
import com.cannontech.multispeak.deploy.service.ServiceType;
import com.cannontech.multispeak.service.MspValidationService;
import com.cannontech.multispeak.service.MultispeakMeterService;
import com.cannontech.yukon.BasicServerConnection;

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
    public PaoDefinitionDao paoDefinitionDao;
    
    private final Logger log = YukonLogManager.getLogger(MR_ServerImpl.class);
    
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

        MspMeterReturnList meterList = null;
        Date timerStart = new Date();
        meterList = mspMeterDao.getAMRSupportedMeters(lastReceived, vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(meterList);

        Meter[] meters = new Meter[meterList.getMeters().size()];
        meterList.getMeters().toArray(meters);
        log.info("Returning " + meters.length + " AMR Supported Meters. (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");             

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
        MspMeterReadReturnList mspMeterReadReturnList = mspRawPointHistoryDao.retrieveMeterReads(ReadBy.NONE, 
                                                                          null, 	//get all
                                                                          startDate.getTime(), 
                                                                          endDate.getTime(), 
                                                                          lastReceived,
                                                                          vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(mspMeterReadReturnList);

        MeterRead[] meterReadArray = new MeterRead[mspMeterReadReturnList.getMeterReads().size()];
        mspMeterReadReturnList.getMeterReads().toArray(meterReadArray);
        return meterReadArray;
    }

    @Override
    public MeterRead[] getReadingsByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
        init(); //init is already performed on the call to isAMRMeter()
        
        //Validate the meterNo is a Yukon meterNumber
        mspValidationService.isYukonMeterNumber(meterNo);
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        MspMeterReadReturnList mspMeterReadReturnList = mspRawPointHistoryDao.retrieveMeterReads(ReadBy.METER_NUMBER, 
                                                                          meterNo, 
                                                                          startDate.getTime(), 
                                                                          endDate.getTime(), 
                                                                          null,
                                                                          vendor.getMaxReturnRecords());
        
        // There is only one MeterNo in the response, so does it make sense to update the header with lastSent?
        // updateResponseHeader(mspMeterRead);
        
        MeterRead[] meterReadArray = new MeterRead[mspMeterReadReturnList.getMeterReads().size()];
        mspMeterReadReturnList.getMeterReads().toArray(meterReadArray);
        return meterReadArray;
    }

    @Override
    public MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
        init(); //init is already performed on the call to isAMRMeter()

        //Validate the meterNo is a Yukon meterNumber
        YukonMeter meter = mspValidationService.isYukonMeterNumber(meterNo);
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        
    	boolean canInitiatePorterRequest = paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.PORTER_COMMAND_REQUESTS);
    	
        //Custom hack put in only for SEDC.  Performs an actual meter read instead of simply replying from the database.
        if ( vendor.getCompanyName().equalsIgnoreCase("SEDC") && canInitiatePorterRequest) {
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
                log.error(message);
                throw new RemoteException(message);
            }
        }
    }
    
    @Override
    public FormattedBlock[] getReadingsByBillingCycle(String billingCycle,
            Calendar billingDate, int kWhLookBack, int kWLookBack,
            int kWLookForward, String lastReceived,
            String formattedBlockTemplateName, String[] fieldName)
            throws RemoteException {
        /* TODO
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        MeterRead[] meterReads = multispeakFuncs.getMspRawPointHistoryDao().retrieveMeterReads(ReadBy.BILL_GROUP, billingCycle, startDate.getTime(), endDate.getTime(), lastReceived);
        //TODO = need to get the true number of meters remaining
        int numRemaining = (meterReads.length < MultispeakDefines.MAX_RETURN_RECORDS ? 0:1); //at least one item remaining, bad assumption.
        multispeakFuncs.getResponseHeader().setObjectsRemaining(new BigInteger(String.valueOf(numRemaining)));

        return meterReads;
        */
        throw new RemoteException("Method getReadingsByBillingCycle is NOT supported.");
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
            String responseURL, String transactionID, float expirationTime)
            throws RemoteException {
        init();
        ErrorObject[] errorObjects = new ErrorObject[0];
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        if ( ! porterConnection.isValid() ) {
            String message = "Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.";
            log.error(message);
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
    public FormattedBlock getLatestMeterReadingsByMeterGroup(
            String meterGroupID, String formattedBlockTemplateName,
            String[] fieldName) throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] initiateGroupMeterRead(String meterGroupName,
            String responseURL, String transactionID, float expirationTime)
            throws RemoteException {
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
    public FormattedBlock[] getReadingByMeterNumberFormattedBlock(
            String meterNumber, Calendar billingDate, int kWhLookBack,
            int kWLookBack, int kWLookForward, String lastReceived,
            String formattedBlockTemplateName, String[] fieldName)
            throws RemoteException {
        init();
        return null;
    }
    @Override
    public FormattedBlock[] getReadingsByDateFormattedBlock(
            Calendar billingDate, int kWhLookBack, int kWLookBack,
            int kWLookForward, String lastReceived,
            String formattedBlockTemplateName, String[] fieldName)
            throws RemoteException {
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
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        MspMeterReadReturnList mspMeterReadReturnList = mspRawPointHistoryDao.retrieveLatestMeterReads(ReadBy.NONE, null, lastReceived, vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(mspMeterReadReturnList);

        MeterRead[] meterReadArray = new MeterRead[mspMeterReadReturnList.getMeterReads().size()];
        mspMeterReadReturnList.getMeterReads().toArray(meterReadArray);
        return meterReadArray;
    }
    
    @Override
    public FormattedBlock getLatestReadingByMeterNoAndType(String meterNo,
            String readingType, String formattedBlockTemplateName,
            String[] fieldName) throws RemoteException {
        init();
        YukonMeter meter = mspValidationService.isYukonMeterNumber(meterNo);
        
        FormattedBlockProcessingService<Block> formattedBlockProcessingService = 
            mspValidationService.getProcessingServiceByReadingType(readingTypesMap, readingType);
        
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
            log.error(message);
            throw new RemoteException(message);
        }
    }
    
    @Override
    public FormattedBlock[] getLatestReadingByType(String readingType,
            String lastReceived, String formattedBlockTemplateName,
            String[] fieldName) throws RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        FormattedBlockProcessingService<Block> formattedBlockProcessingService = 
            mspValidationService.getProcessingServiceByReadingType(readingTypesMap, readingType);

        MspBlockReturnList mspBlockReturnList = mspRawPointHistoryDao.retrieveLatestBlock(formattedBlockProcessingService, 
                                                                       lastReceived, vendor.getMaxReturnRecords());
        
        FormattedBlock formattedBlock = formattedBlockProcessingService.createMspFormattedBlock(mspBlockReturnList.getBlocks());
        FormattedBlock[] formattedBlocks = new FormattedBlock[]{formattedBlock};
        multispeakFuncs.updateResponseHeader(mspBlockReturnList);
        return formattedBlocks;
    }
    
    @Override
    public FormattedBlock[] getReadingsByDateAndType(Calendar startDate,
            Calendar endDate, String readingType, String lastReceived,
            String formattedBlockTemplateName, String[] fieldName)
            throws RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        FormattedBlockProcessingService<Block> formattedBlockProcessingService = 
            mspValidationService.getProcessingServiceByReadingType(readingTypesMap, readingType);

        MspBlockReturnList mspBlockReturnList = mspRawPointHistoryDao.retrieveBlock(ReadBy.NONE, null,
                                                                 formattedBlockProcessingService,
                                                                 startDate.getTime(),
                                                                 endDate.getTime(),
                                                                 lastReceived,
                                                                 vendor.getMaxReturnRecords());

        FormattedBlock formattedBlock = formattedBlockProcessingService.createMspFormattedBlock(mspBlockReturnList.getBlocks());
        FormattedBlock[] formattedBlocks = new FormattedBlock[]{formattedBlock};
        multispeakFuncs.updateResponseHeader(mspBlockReturnList);
        return formattedBlocks;
    }

    @Override
    public FormattedBlock[] getReadingsByMeterNoAndType(String meterNo,
            Calendar startDate, Calendar endDate, String readingType,
            String lastReceived, String formattedBlockTemplateName,
            String[] fieldName) throws RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        
        //Validate the meterNo is in Yukon
        mspValidationService.isYukonMeterNumber(meterNo); 

        FormattedBlockProcessingService<Block> formattedBlockProcessingService = 
            mspValidationService.getProcessingServiceByReadingType(readingTypesMap, readingType);
        
        MspBlockReturnList mspBlockReturnList = mspRawPointHistoryDao.retrieveBlock(ReadBy.METER_NUMBER, meterNo,
                                                                 formattedBlockProcessingService,
                                                                 startDate.getTime(),
                                                                 endDate.getTime(),
                                                                 null,  //don't use lastReceived, we know there is only one
                                                                 vendor.getMaxReturnRecords());

        FormattedBlock formattedBlock = formattedBlockProcessingService.createMspFormattedBlock(mspBlockReturnList.getBlocks());
        FormattedBlock[] formattedBlocks = new FormattedBlock[]{formattedBlock};
     
        // There is only one MeterNo in the response, so does it make sense to update the header with lastSent?
        // updateResponseHeader(mspBlockReturnList);

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
            String readingType, String transactionID,
            float expirationTime) throws RemoteException {
        init();
        ErrorObject[] errorObjects = new ErrorObject[0];
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        if ( ! porterConnection.isValid() ) {
            String message = "Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.";
            log.error(message);                
            throw new RemoteException(message);
        }
        
        FormattedBlockProcessingService<Block> formattedBlockServ = 
            mspValidationService.getProcessingServiceByReadingType(readingTypesMap, readingType);
        
        errorObjects = multispeakMeterService.blockMeterReadEvent(vendor, meterNo, formattedBlockServ, transactionID);

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
            String transactionID, float expirationTime) throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] meterConnectivityNotification(
            MeterConnectivity[] newConnectivity) throws RemoteException {
        init();
        return null;
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
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
		this.paoDefinitionDao = paoDefinitionDao;
	}

    @Override
    public ErrorObject[] meterBaseExchangeNotification(
            MeterBaseExchange[] MBChangeout) throws RemoteException {
        init();
        return null;
    }

    @Override
    public String requestRegistrationID() throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] registerForService(RegistrationInfo registrationDetails)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] unregisterForService(String registrationID)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public RegistrationInfo getRegistrationInfoByID(String registrationID)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public String[] getPublishMethods() throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] domainMembersChangedNotification(
            DomainMember[] changedDomainMembers) throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] domainNamesChangedNotification(
            DomainNameChange[] changedDomainNames) throws RemoteException {
        init();
        return null;
    }

    @Override
    public Schedule[] getSchedules(String lastReceived) throws RemoteException {
        init();
        return null;
    }

    @Override
    public Schedule getScheduleByID(String scheduleID) throws RemoteException {
        init();
        return null;
    }

    @Override
    public ReadingSchedule[] getReadingSchedules(String lastReceived)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ReadingSchedule getReadingScheduleByID(String readingScheduleID)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public FormattedBlock[] getLatestReadingsByMeterNoList(String[] meterNo,
            Calendar startDate, Calendar endDate, String readingType,
            String lastReceived, ServiceType serviceType,
            String formattedBlockTemplateName, String[] fieldName)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public FormattedBlockTemplate[] getFormattedBlockTemplates(
            String lastReceived) throws RemoteException {
        init();
        return null;
    }

    @Override
    public FormattedBlock[] getLatestReadingsByMeterNoListFormattedBlock(
            String[] meterNo, Calendar startDate, Calendar endDate,
            String formattedBlockTemplateName, String[] fieldName,
            String lastReceived, ServiceType serviceType)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] initiateDemandReset(MeterIdentifier[] meterIDs,
            String responseURL, String transactionID, float expirationTime)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] insertMetersInConfigurationGroup(
            String[] meterNumbers, String meterGroupID, ServiceType serviceType)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] removeMetersFromConfigurationGroup(
            String[] meterNumbers, String meterGroupID, ServiceType serviceType)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] establishSchedules(Schedule[] schedules)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] deleteSchedule(String scheduleID)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] establishReadingSchedules(
            ReadingSchedule[] readingSchedules) throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] enableReadingSchedule(String readingScheduleID)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] disableReadingSchedule(String readingScheduleID)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] deleteReadingSchedule(String readingScheduleID)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] initiateMeterReadsByFieldName(String[] meterNumbers,
            String[] fieldNames, String responseURL, String transactionID,
            float expirationTime, String formattedBlockTemplateName)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] meterBaseChangedNotification(MeterBase[] changedMBs)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] meterBaseRemoveNotification(MeterBase[] removedMBs)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] meterBaseRetireNotification(MeterBase[] retiredMBs)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] meterBaseAddNotification(MeterBase[] addedMBs)
            throws RemoteException {
        init();
        return null;
    }
}