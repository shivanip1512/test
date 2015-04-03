package com.cannontech.multispeak.deploy.service.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.demandreset.service.DemandResetService;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.msp.beans.v3.Customer;
import com.cannontech.msp.beans.v3.CustomersAffectedByOutage;
import com.cannontech.msp.beans.v3.DomainMember;
import com.cannontech.msp.beans.v3.DomainNameChange;
import com.cannontech.msp.beans.v3.EndDeviceShipment;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.EventCode;
import com.cannontech.msp.beans.v3.FormattedBlock;
import com.cannontech.msp.beans.v3.FormattedBlockTemplate;
import com.cannontech.msp.beans.v3.HistoryLog;
import com.cannontech.msp.beans.v3.InHomeDisplay;
import com.cannontech.msp.beans.v3.InHomeDisplayExchange;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.MeterBase;
import com.cannontech.msp.beans.v3.MeterBaseExchange;
import com.cannontech.msp.beans.v3.MeterConnectivity;
import com.cannontech.msp.beans.v3.MeterExchange;
import com.cannontech.msp.beans.v3.MeterGroup;
import com.cannontech.msp.beans.v3.MeterIdentifier;
import com.cannontech.msp.beans.v3.MeterList;
import com.cannontech.msp.beans.v3.MeterRead;
import com.cannontech.msp.beans.v3.PhaseCd;
import com.cannontech.msp.beans.v3.ReadingSchedule;
import com.cannontech.msp.beans.v3.RegistrationInfo;
import com.cannontech.msp.beans.v3.Schedule;
import com.cannontech.msp.beans.v3.ServiceLocation;
import com.cannontech.msp.beans.v3.ServiceType;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.FormattedBlockProcessingService;
import com.cannontech.multispeak.dao.MeterReadProcessingService;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MspRawPointHistoryDao;
import com.cannontech.multispeak.dao.MspRawPointHistoryDao.ReadBy;
import com.cannontech.multispeak.data.MspBlockReturnList;
import com.cannontech.multispeak.data.MspMeterReadReturnList;
import com.cannontech.multispeak.data.MspMeterReturnList;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.MR_Server;
import com.cannontech.multispeak.service.MspValidationService;
import com.cannontech.multispeak.service.MultispeakMeterService;
import com.cannontech.user.UserUtils;
import com.cannontech.yukon.BasicServerConnection;
import com.google.common.base.Function;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


public class MR_ServerImpl implements MR_Server{

    @Autowired private AttributeService attributeService;
    @Autowired private DemandResetService demandResetService;
    @Autowired private DynamicDataSource dynamicDataSource;
    @Autowired private MeterReadProcessingService meterReadProcessingService;
    @Autowired private MspMeterDao mspMeterDao;
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private MspRawPointHistoryDao mspRawPointHistoryDao;
    @Autowired private MspValidationService mspValidationService;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MultispeakMeterService multispeakMeterService;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    private BasicServerConnection porterConnection;
    private Map<String, FormattedBlockProcessingService<Block>> readingTypesMap;

    private final Logger log = YukonLogManager.getLogger(MR_ServerImpl.class);
    private final static String[] methods = new String[] {
        "pingURL",
        "getMethods",
        "initiateMeterReadByMeterNumber",
        "initiateMeterReadByMeterNoAndType",
        "initiateDemandReset",
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
        "removeMetersFromMeterGroup" };

    private void init() throws MultispeakWebServiceException {
        multispeakFuncs.init();
    }

    @Override
    public void pingURL() throws MultispeakWebServiceException {
        init();
    }
    
    @Override
    public List<String> getMethods() throws MultispeakWebServiceException {
        init();
        return multispeakFuncs.getMethods(MultispeakDefines.MR_Server_STR , Arrays.asList(methods));
    }
    
    @Override
    public List<Meter> getAMRSupportedMeters(java.lang.String lastReceived) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getAMRSupportedMeters", vendor.getCompanyName());

        MspMeterReturnList meterList = null;
        Date timerStart = new Date();
        meterList = mspMeterDao.getAMRSupportedMeters(lastReceived, vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(meterList);

        log.info("Returning " + meterList.getMeters().size() + " AMR Supported Meters. (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");
        multispeakEventLogService.returnObjects(meterList.getMeters().size(), meterList.getObjectsRemaining(), "Meter", meterList.getLastSent(),
                                                "getAMRSupportedMeters", vendor.getCompanyName());
        
        return meterList.getMeters();
    }
    
    @Override
    public boolean isAMRMeter(java.lang.String meterNo) throws MultispeakWebServiceException {
        init();
//          Commenting out for now, not sure if we want this logged or not, it could be a lot...and doesn't have much impact to the system
//        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
//        multispeakEventLogService.methodInvoked("isAMRMeter", vendor.getCompanyName());

        boolean isAmrMeter = false;
        try {
            mspValidationService.isYukonMeterNumber(meterNo);
            isAmrMeter = true;
        } catch (MultispeakWebServiceException e){
            isAmrMeter = false;
        }
        log.debug("isAMRMeter " + isAmrMeter + " for " + meterNo + ".");
        return isAmrMeter;
    }
    
    @Override
    public List<MeterRead> getReadingsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws MultispeakWebServiceException {
    	init();
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getReadingsByDate", vendor.getCompanyName());
        
        MspMeterReadReturnList mspMeterReadReturnList = mspRawPointHistoryDao.retrieveMeterReads(ReadBy.NONE, 
                                                                          null, 	//get all
                                                                          startDate.getTime(), 
                                                                          endDate.getTime(), 
                                                                          lastReceived,
                                                                          vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(mspMeterReadReturnList);
        List<MeterRead> meterReads = mspMeterReadReturnList.getMeterReads();
        log.info("Returning " + meterReads.size() + " Readings By Date.");
        multispeakEventLogService.returnObjects(meterReads.size(), mspMeterReadReturnList.getObjectsRemaining(), "MeterRead", 
                                                mspMeterReadReturnList.getLastSent(), "getReadingsByDate", vendor.getCompanyName());

        return meterReads;
    }

    @Override
    public List<MeterRead> getReadingsByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws MultispeakWebServiceException {
        init(); //init is already performed on the call to isAMRMeter()
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getReadingsByMeterNo", vendor.getCompanyName());
        
        //Validate the meterNo is a Yukon meterNumber
        mspValidationService.isYukonMeterNumber(meterNo);
        
        MspMeterReadReturnList mspMeterReadReturnList = mspRawPointHistoryDao.retrieveMeterReads(ReadBy.METER_NUMBER, 
                                                                          meterNo, 
                                                                          startDate.getTime(), 
                                                                          endDate.getTime(), 
                                                                          null,
                                                                          vendor.getMaxReturnRecords());
        
        // There is only one MeterNo in the response, so does it make sense to update the header with lastSent?
        // updateResponseHeader(mspMeterRead);
        List<MeterRead> meterReads = mspMeterReadReturnList.getMeterReads();
        log.info("Returning " + meterReads.size() + " Readings By MeterNo.");
        multispeakEventLogService.returnObjects(meterReads.size(), mspMeterReadReturnList.getObjectsRemaining(), "MeterRead", 
                                                mspMeterReadReturnList.getLastSent(), "getReadingsByMeterNo", vendor.getCompanyName());
        return meterReads;
    }

    @Override
    public MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws MultispeakWebServiceException {
        init(); //init is already performed on the call to isAMRMeter()

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getLatestReadingByMeterNo", vendor.getCompanyName());
        
        //Validate the meterNo is a Yukon meterNumber
        YukonMeter meter = mspValidationService.isYukonMeterNumber(meterNo);
        
    	boolean canInitiatePorterRequest = paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.PORTER_COMMAND_REQUESTS);
    	
        //Custom hack put in only for SEDC.  Performs an actual meter read instead of simply replying from the database.
        if ( vendor.getCompanyName().equalsIgnoreCase("SEDC") && canInitiatePorterRequest) {
            
            // Don't know the responseURL as it's not provided in this method (by definition!) Using default for SEDC.
            String responseUrl = multispeakFuncs.getResponseUrl(vendor, null, MultispeakDefines.CB_Server_STR);
        	MeterRead meterRead = multispeakMeterService.getLatestReadingInterrogate(vendor, meter, responseUrl);
        	multispeakEventLogService.returnObject("MeterRead", meterNo, "getLatestReadingByMeterNo", vendor.getCompanyName());
        	return meterRead;
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
                multispeakEventLogService.returnObject("MeterRead", meterNo, "getLatestReadingByMeterNo", vendor.getCompanyName());
    	        return meterRead;
            } catch (DynamicDataAccessException e) {
                String message = "Connection to dispatch is invalid";
                log.error(message);
                throw new MultispeakWebServiceException(message);
            }
        }
    }
    
    @Override
    public List<FormattedBlock> getReadingsByBillingCycle(String billingCycle,
            Calendar billingDate, int kWhLookBack, int kWLookBack,
            int kWLookForward, String lastReceived,
            String formattedBlockTemplateName, List<String> fieldName)
            throws MultispeakWebServiceException {
        /* TODO
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        MeterRead[] meterReads = multispeakFuncs.getMspRawPointHistoryDao().retrieveMeterReads(ReadBy.BILL_GROUP, billingCycle, startDate.getTime(), endDate.getTime(), lastReceived);
        //TODO = need to get the true number of meters remaining
        int numRemaining = (meterReads.length < MultispeakDefines.MAX_RETURN_RECORDS ? 0:1); //at least one item remaining, bad assumption.
        multispeakFuncs.getResponseHeader().setObjectsRemaining(new BigInteger(String.valueOf(numRemaining)));

        return meterReads;
        */
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    
    @Override
    public List<ErrorObject> initiateUsageMonitoring(List<String> meterNos) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("initiateUsageMonitoring", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.initiateUsageMonitoring(vendor, meterNos);
        return errorObject;
    }
    
    @Override
    public List<ErrorObject> cancelUsageMonitoring(List<String> meterNos) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("cancelUsageMonitoring", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.cancelUsageMonitoring(vendor, meterNos);
        return errorObject;
    }
    
    @Override
    public List<ErrorObject> initiateDisconnectedStatus(List<String> meterNos) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("initiateDisconnectedStatus", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.initiateDisconnectedStatus(vendor, meterNos);
        return errorObject;
    }
    
    @Override
    public List<ErrorObject> cancelDisconnectedStatus(List<String> meterNos) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("cancelDisconnectedStatus", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.cancelDisconnectedStatus(vendor, meterNos);
        return errorObject;
    }

    //Perform an actual read of the meter and return a CB_MR readingChangedNotification message for each meterNo
    @Override
    public List<ErrorObject> initiateMeterReadByMeterNumber(List<String> meterNos,
            String responseURL, String transactionID, Float expirationTime)
            throws MultispeakWebServiceException {
        init();
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("initiateMeterReadByMeterNumber", vendor.getCompanyName());
        
        String actualResponseUrl = multispeakFuncs.getResponseUrl(vendor, responseURL, MultispeakDefines.CB_Server_STR);
        
        if ( ! porterConnection.isValid() ) {
            String message = "Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.";
            log.error(message);
            throw new MultispeakWebServiceException(message);
        }

        List<ErrorObject> errorObjects = multispeakMeterService.meterReadEvent(vendor, meterNos, transactionID, actualResponseUrl);

        multispeakFuncs.logErrorObjects(MultispeakDefines.MR_Server_STR, "initiateMeterReadByMeterNumberRequest", errorObjects);
        return errorObjects;
    }
    
    @Override
    public List<ErrorObject> serviceLocationChangedNotification(List<ServiceLocation> changedServiceLocations) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("serviceLocationChangedNotification", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.serviceLocationChanged(vendor, changedServiceLocations);
        return errorObject;
    }
    
    @Override
    public List<ErrorObject> meterChangedNotification(List<Meter> changedMeters) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("meterChangedNotification", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.meterChanged(vendor, changedMeters);
        return errorObject;
    }
    
    @Override
    public List<ErrorObject> meterRemoveNotification(List<Meter> removedMeters) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("meterRemoveNotification", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.meterRemove(vendor, removedMeters);
        return errorObject;
    }
    
    @Override
    public List<ErrorObject> meterAddNotification(List<Meter> addedMeters) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("meterAddNotification", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.meterAdd(vendor, addedMeters);
        return errorObject;
    }

    @Override
    public ErrorObject deleteMeterGroup(String meterGroupID)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("deleteMeterGroup", vendor.getCompanyName());
        return multispeakMeterService.deleteGroup(meterGroupID, vendor);
    }

    @Override
    public List<ErrorObject> establishMeterGroup(MeterGroup meterGroup)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("establishMeterGroup", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.addMetersToGroup(meterGroup, "establishMeterGroup", vendor);
        return errorObject;
    }

    @Override
    public List<ErrorObject> insertMeterInMeterGroup(List<String> meterNumbers,
            String meterGroupID) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("insertMeterInMeterGroup", vendor.getCompanyName());
        MeterGroup meterGroup = new MeterGroup();
        MeterList meterList = meterGroup.getMeterList();
        List<String> meterID = meterList.getMeterID();
        for (String meterNumber : meterNumbers) {
            meterID.add(meterNumber);
        }
        meterGroup.setMeterList(meterList);
        meterGroup.setGroupName(meterGroupID);
        List<ErrorObject> errorObject = multispeakMeterService.addMetersToGroup(meterGroup, "insertMeterInMeterGroup", vendor);
        return errorObject;
    }


    @Override
    public List<ErrorObject> removeMetersFromMeterGroup(List<String> meterNumbers,
            String meterGroupID) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("removeMetersFromMeterGroup", vendor.getCompanyName());
        
        List<ErrorObject> errorObject = multispeakMeterService.removeMetersFromGroup(meterGroupID, meterNumbers, vendor);
        return errorObject;
    }

    @Override
    public List<MeterRead> getLatestReadings(String lastReceived)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getLatestReadings", vendor.getCompanyName());
        
        MspMeterReadReturnList mspMeterReadReturnList = mspRawPointHistoryDao.retrieveLatestMeterReads(ReadBy.NONE, null, lastReceived, vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(mspMeterReadReturnList);

        List<MeterRead> meterReads = mspMeterReadReturnList.getMeterReads();
        log.info("Returning " + meterReads.size() + " latest Readings.");
        multispeakEventLogService.returnObjects(meterReads.size(), mspMeterReadReturnList.getObjectsRemaining(), "MeterRead", 
                                                mspMeterReadReturnList.getLastSent(), "getLatestReadings", vendor.getCompanyName());

        return meterReads;
    }
    
    @Override
    public FormattedBlock getLatestReadingByMeterNoAndType(String meterNo,
            String readingType, String formattedBlockTemplateName,
            List<String> fieldName) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getLatestReadingByMeterNoAndType", vendor.getCompanyName());
        
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
            multispeakEventLogService.returnObject("FormattedBlock", meterNo, "getLatestReadingByMeterNoAndType", vendor.getCompanyName());
            return formattedBlock;
            
        } catch (DynamicDataAccessException e) {
            String message = "Connection to dispatch is invalid";
            log.error(message);
            throw new MultispeakWebServiceException(message);
        }
    }
    
    @Override
    public List<FormattedBlock> getLatestReadingByType(String readingType,
            String lastReceived, String formattedBlockTemplateName,
            List<String> fieldName) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getLatestReadingByType", vendor.getCompanyName());

        FormattedBlockProcessingService<Block> formattedBlockProcessingService = 
            mspValidationService.getProcessingServiceByReadingType(readingTypesMap, readingType);

        MspBlockReturnList mspBlockReturnList = mspRawPointHistoryDao.retrieveLatestBlock(formattedBlockProcessingService, 
                                                                       lastReceived, vendor.getMaxReturnRecords());
        multispeakFuncs.updateResponseHeader(mspBlockReturnList);
        
        FormattedBlock formattedBlock = formattedBlockProcessingService.createMspFormattedBlock(mspBlockReturnList.getBlocks());
        
        multispeakEventLogService.returnObjects(1, mspBlockReturnList.getObjectsRemaining(), "FormattedBlock", 
                                                mspBlockReturnList.getLastSent(), "getLatestReadingByType", vendor.getCompanyName());

        return Collections.singletonList(formattedBlock);
    }
    
    @Override
    public List<FormattedBlock> getReadingsByDateAndType(Calendar startDate,
            Calendar endDate, String readingType, String lastReceived,
            String formattedBlockTemplateName, List<String> fieldName)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getReadingsByDateAndType", vendor.getCompanyName());
        
        FormattedBlockProcessingService<Block> formattedBlockProcessingService = 
            mspValidationService.getProcessingServiceByReadingType(readingTypesMap, readingType);

        MspBlockReturnList mspBlockReturnList = mspRawPointHistoryDao.retrieveBlock(ReadBy.NONE, null,
                                                                 formattedBlockProcessingService,
                                                                 startDate.getTime(),
                                                                 endDate.getTime(),
                                                                 lastReceived,
                                                                 vendor.getMaxReturnRecords());
        multispeakFuncs.updateResponseHeader(mspBlockReturnList);
        
        FormattedBlock formattedBlock = formattedBlockProcessingService.createMspFormattedBlock(mspBlockReturnList.getBlocks());
        
        multispeakEventLogService.returnObjects(1, mspBlockReturnList.getObjectsRemaining(), "FormattedBlock", 
                                                mspBlockReturnList.getLastSent(), "getReadingsByDateAndType", vendor.getCompanyName());

        return Collections.singletonList(formattedBlock);
    }

    @Override
    public List<FormattedBlock> getReadingsByMeterNoAndType(String meterNo,
            Calendar startDate, Calendar endDate, String readingType,
            String lastReceived, String formattedBlockTemplateName,
            List<String> fieldName) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getReadingsByMeterNoAndType", vendor.getCompanyName());
        
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

        // There is only one MeterNo in the response, so does it make sense to update the header with lastSent?
        // updateResponseHeader(mspBlockReturnList);

        FormattedBlock formattedBlock = formattedBlockProcessingService.createMspFormattedBlock(mspBlockReturnList.getBlocks());

        multispeakEventLogService.returnObjects(1, mspBlockReturnList.getObjectsRemaining(), "FormattedBlock", 
                                                mspBlockReturnList.getLastSent(), "getReadingsByMeterNoAndType", vendor.getCompanyName());

        return Collections.singletonList(formattedBlock);
    }
    
    @Override
    public Set<String> getSupportedReadingTypes() throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getSupportedReadingTypes", vendor.getCompanyName());
        
        Set<String> types = readingTypesMap.keySet();
        return types;
    }
    
    @Override
    public List<ErrorObject> initiateMeterReadByMeterNoAndType(String meterNo, String responseURL,
            String readingType, String transactionID,
            Float expirationTime) throws MultispeakWebServiceException {
        init();
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("initiateMeterReadByMeterNoAndType", vendor.getCompanyName());
        
        String actualResponseUrl = multispeakFuncs.getResponseUrl(vendor, responseURL, MultispeakDefines.CB_Server_STR);
        if ( ! porterConnection.isValid() ) {
            String message = "Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.";
            log.error(message);                
            throw new MultispeakWebServiceException(message);
        }
        
        FormattedBlockProcessingService<Block> formattedBlockServ = 
            mspValidationService.getProcessingServiceByReadingType(readingTypesMap, readingType);
        
        List<ErrorObject> errorObjects = multispeakMeterService.blockMeterReadEvent(vendor, meterNo, formattedBlockServ, transactionID, actualResponseUrl);

        multispeakFuncs.logErrorObjects(MultispeakDefines.MR_Server_STR, "initiateMeterReadByMeterNoAndTypeRequest", errorObjects);
        return errorObjects;
    }

    @Override
    public List<ErrorObject> initiateDemandReset(List<MeterIdentifier> meterIDs,
            String responseURL, String transactionId, Float expirationTime)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("initiateDemandReset", vendor.getCompanyName());
        
        List<ErrorObject> errors = Lists.newArrayList();
        boolean hasFatalErrors = false;
        
        String actualResponseUrl = multispeakFuncs.getResponseUrl(vendor, responseURL, MultispeakDefines.CB_Server_STR);
        
        // Do a basic URL check. This only validates that it's not empty.
        ErrorObject errorObject = mspValidationService.validateResponseURL(actualResponseUrl, "Meter", "InitiateDemandReset");
        if (errorObject != null) {
            errors.add(errorObject);
            hasFatalErrors = true;
        }

        Function<MeterIdentifier, String> meterNumberFromIdentifier =
            new Function<MeterIdentifier, String>() {
                @Override
                public String apply(MeterIdentifier meterIdentifier) {
                    return meterIdentifier.getMeterNo();
                }
            };
        Set<String> meterNumbers =
                Sets.newHashSet(Lists.transform(meterIDs, meterNumberFromIdentifier));
        
        Map<String, PaoIdentifier> paoIdsByMeterNumber =
                paoDao.findPaoIdentifiersByMeterNumber(meterNumbers);
        Map<PaoIdentifier, String> meterNumbersByPaoId =
                HashBiMap.create(paoIdsByMeterNumber).inverse();
        Set<String> invalidMeterNumbers = Sets.difference(meterNumbers, paoIdsByMeterNumber.keySet());
        for (String invalidMeterNumber : invalidMeterNumbers) {
            errors.add(mspObjectDao.getNotFoundErrorObject(invalidMeterNumber, "MeterNumber", "Meter",
                                                           "initiateDemandReset", vendor.getCompanyName()));
        }

        Set<PaoIdentifier> meterIdentifiers = Sets.newHashSet(paoIdsByMeterNumber.values());
        Set<PaoIdentifier> validMeters =
                Sets.newHashSet(demandResetService.filterDevices(meterIdentifiers));
        Set<PaoIdentifier> unsupportedMeters = Sets.difference(meterIdentifiers, validMeters);
        for (PaoIdentifier unsupportedMeter : unsupportedMeters) {
            String errorMsg = unsupportedMeter.getPaoIdentifier().getPaoType()
                    + " does not support demand reset";
            String meterNumber = meterNumbersByPaoId.get(unsupportedMeter);
            errors.add(mspObjectDao.getErrorObject(meterNumber, errorMsg, "Meter",
                                                   "initiateDemandReset", vendor.getCompanyName()));
        }

        if (hasFatalErrors || validMeters.isEmpty()) {
            return errors;
        }

        log.info("Received " + meterIDs.size() + " Meter(s) for Demand Reset from " + vendor.getCompanyName());
        multispeakEventLogService.initiateDemandResetRequest(meterNumbers.size(), meterNumbersByPaoId.size(), 
                                                             invalidMeterNumbers.size(), unsupportedMeters.size(),
                                                             "initiateConnectDisconnect", vendor.getCompanyName());

        MRServerDemandResetCallback callback =
                new MRServerDemandResetCallback(mspObjectDao, multispeakEventLogService, vendor, meterNumbersByPaoId, responseURL, transactionId);
        
        demandResetService.sendDemandResetAndVerify(validMeters, callback, UserUtils.getYukonUser());
        errors.addAll(callback.getErrors());
        return errors;
    }

    @Required
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }

    @Required
    public void setReadingTypesMap(
            Map<String, FormattedBlockProcessingService<Block>> readingTypesMap) {
        this.readingTypesMap = readingTypesMap;
    }

    @Override
    public List<ErrorObject> cancelPlannedOutage(List<String> meterNos) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> customerChangedNotification(List<Customer> changedCustomers) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> customersAffectedByOutageNotification(List<CustomersAffectedByOutage> newOutages)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> deleteReadingSchedule(String readingScheduleID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> deleteSchedule(String scheduleID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> disableReadingSchedule(String readingScheduleID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> domainMembersChangedNotification(List<DomainMember> changedDomainMembers)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> domainNamesChangedNotification(List<DomainNameChange> changedDomainNames)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> enableReadingSchedule(String readingScheduleID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> endDeviceShipmentNotification(EndDeviceShipment shipment) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> establishReadingSchedules(List<ReadingSchedule> readingSchedules)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> establishSchedules(List<Schedule> schedules) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<String> getDomainNames() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<DomainMember> getDomainMembers(String domainString) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<FormattedBlockTemplate> getFormattedBlockTemplates(String lastReceived)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<HistoryLog> getHistoryLogByMeterNo(String meterNo, Calendar startDate, Calendar endDate)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<HistoryLog>getHistoryLogsByDate(Calendar startDate, Calendar endDate, String lastReceived)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<HistoryLog>getHistoryLogsByMeterNoAndEventCode(String meterNo, EventCode eventCode, Calendar startDate,
            Calendar endDate) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<HistoryLog>getHistoryLogsByDateAndEventCode(EventCode eventCode, Calendar startDate, Calendar endDate,
            String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public FormattedBlock getLatestMeterReadingsByMeterGroup(String meterGroupID, String formattedBlockTemplateName,
            List<String> fieldName) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<FormattedBlock> getLatestReadingsByMeterNoList(List<String> meterNo, Calendar startDate, Calendar endDate,
            String readingType, String lastReceived, ServiceType serviceType, String formattedBlockTemplateName,
            List<String> fieldName) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<FormattedBlock> getLatestReadingsByMeterNoListFormattedBlock(List<String> meterNo, Calendar startDate,
            Calendar endDate, String formattedBlockTemplateName, List<String> fieldName, String lastReceived,
            ServiceType serviceType) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<Meter> getModifiedAMRMeters(String previousSessionID, String lastReceived)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<String> getPublishMethods() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<FormattedBlock> getReadingByMeterNumberFormattedBlock(String meterNumber, Calendar billingDate,
            int kWhLookBack, int kWLookBack, int kWLookForward, String lastReceived, String formattedBlockTemplateName,
            List<String> fieldName) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<FormattedBlock> getReadingsByDateFormattedBlock(Calendar billingDate, int kWhLookBack, int kWLookBack,
            int kWLookForward, String lastReceived, String formattedBlockTemplateName, List<String> fieldName)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<MeterRead> getReadingsByUOMAndDate(String uomData, Calendar startDate, Calendar endDate, String lastReceived)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ReadingSchedule getReadingScheduleByID(String readingScheduleID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ReadingSchedule> getReadingSchedules(String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public RegistrationInfo getRegistrationInfoByID(String registrationID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public Schedule getScheduleByID(String scheduleID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<Schedule> getSchedules(String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> inHomeDisplayAddNotification(List<InHomeDisplay> addedIHDs) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> inHomeDisplayChangedNotification(List<InHomeDisplay> changedIHDs)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> inHomeDisplayExchangeNotification(List<InHomeDisplayExchange> IHDChangeout)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> inHomeDisplayRemoveNotification(List<InHomeDisplay> removedIHDs)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> inHomeDisplayRetireNotification(List<InHomeDisplay> retiredIHDs)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> initiateGroupMeterRead(String meterGroupName, String responseURL, String transactionID,
            float expirationTime) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> initiateMeterReadByObject(String objectName, String nounType, PhaseCd phaseCode,
            String responseURL, String transactionID, float expirationTime) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> initiateMeterReadsByFieldName(List<String> meterNumbers, List<String> fieldNames, String responseURL,
            String transactionID, float expirationTime, String formattedBlockTemplateName)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> initiatePlannedOutage(List<String> meterNos, Calendar startDate, Calendar endDate)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> insertMetersInConfigurationGroup(List<String> meterNumbers, String meterGroupID,
            ServiceType serviceType) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> meterBaseAddNotification(List<MeterBase> addedMBs) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> meterBaseChangedNotification(List<MeterBase> changedMBs) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> meterBaseExchangeNotification(List<MeterBaseExchange> MBChangeout)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> meterBaseRemoveNotification(List<MeterBase> removedMBs) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> meterBaseRetireNotification(List<MeterBase> retiredMBs) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> meterConnectivityNotification(List<MeterConnectivity> newConnectivity)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> meterExchangeNotification(List<MeterExchange> meterChangeout) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> meterRetireNotification(List<Meter> retiredMeters) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> registerForService(RegistrationInfo registrationDetails) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> removeMetersFromConfigurationGroup(List<String> meterNumbers, String meterGroupID,
            ServiceType serviceType) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public String requestRegistrationID() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> scheduleGroupMeterRead(String meterGroupName, Calendar timeToRead, String responseURL,
            String transactionID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> unregisterForService(String registrationID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> updateServiceLocationDisplays(String servLocID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    } 
}
