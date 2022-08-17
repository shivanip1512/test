package com.cannontech.multispeak.deploy.service.impl.v3;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
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
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.FormattedBlock;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.MeterGroup;
import com.cannontech.msp.beans.v3.MeterIdentifier;
import com.cannontech.msp.beans.v3.MeterList;
import com.cannontech.msp.beans.v3.MeterRead;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.ServiceLocation;
import com.cannontech.multispeak.block.Block;
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
import com.cannontech.multispeak.service.v3.MR_Server;
import com.cannontech.multispeak.service.v3.MspValidationService;
import com.cannontech.multispeak.service.v3.MultispeakMeterService;
import com.cannontech.user.UserUtils;
import com.cannontech.yukon.BasicServerConnection;
import com.google.common.base.Function;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


public class MR_ServerImpl implements MR_Server{

    @Autowired private AttributeService attributeService;
    @Autowired private DemandResetService demandResetService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
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
    @Autowired private ObjectFactory objectFactory;
    private BasicServerConnection porterConnection;
    private Map<String, FormattedBlockProcessingService<Block>> readingTypesMap;

    private final Logger log = YukonLogManager.getLogger(MR_ServerImpl.class);
    private final static String[] methods = new String[] {
        "PingURL",
        "GetMethods",
        "InitiateMeterReadByMeterNumber",
        "InitiateMeterReadByMeterNoAndType",
        "InitiateDemandReset",
        "IsAMRMeter",
        "GetReadingsByDate",
        "GetAMRSupportedMeters",
        "GetLatestReadingByMeterNo",
        "GetLatestReadings",
        "GetLatestReadingByMeterNoAndType",
        "GetLatestReadingByType",
        "GetReadingsByMeterNo",
        "GetReadingsByDateAndType",
        "GetReadingsByMeterNoAndType",
        "GetSupportedReadingTypes",
        "MeterAddNotification",
        "MeterRemoveNotification",
        "MeterChangedNotification",
        "InitiateUsageMonitoring",
        "CancelUsageMonitoring",
        "InitiateDisconnectedStatus",
        "CancelDisconnectedStatus",
        "ServiceLocationChangedNotification",
        "DeleteMeterGroup",
        "EstablishMeterGroup",
        "InsertMeterInMeterGroup",
        "RemoveMetersFromMeterGroup" };

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
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetAMRSupportedMeters", vendor.getCompanyName());

        Date timerStart = new Date();
        MspMeterReturnList meterList = (MspMeterReturnList) mspMeterDao.getAMRSupportedMeters(lastReceived, vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(meterList);

        log.info("Returning " + meterList.getMeters().size() + " AMR Supported Meters. (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");
        multispeakEventLogService.returnObjects(meterList.getMeters().size(), meterList.getObjectsRemaining(), "Meter", meterList.getLastSent(),
                                                "GetAMRSupportedMeters", vendor.getCompanyName());
        
        return meterList.getMeters();
    }
    
    @Override
    public boolean isAMRMeter(java.lang.String meterNo) throws MultispeakWebServiceException {
        init();
        multispeakFuncs.getMultispeakVendorFromHeader();
//      Commenting out for now, not sure if we want this logged or not, it could be a lot...and doesn't have much impact to the system
//      multispeakEventLogService.methodInvoked("IsAMRMeter", vendor.getCompanyName());

        boolean isAmrMeter = false;
        try {
            mspValidationService.isYukonMeterNumber(meterNo);
            isAmrMeter = true;
        } catch (MultispeakWebServiceException e){
            isAmrMeter = false;
        }
        log.debug("IsAMRMeter " + isAmrMeter + " for " + meterNo + ".");
        return isAmrMeter;
    }
    
    @Override
    public List<MeterRead> getReadingsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws MultispeakWebServiceException {
    	init();
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetReadingsByDate", vendor.getCompanyName());
        
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
                                                mspMeterReadReturnList.getLastSent(), "GetReadingsByDate", vendor.getCompanyName());

        return meterReads;
    }

    @Override
    public List<MeterRead> getReadingsByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws MultispeakWebServiceException {
        init(); //init is already performed on the call to isAMRMeter()
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetReadingsByMeterNo", vendor.getCompanyName());
        
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
                                                mspMeterReadReturnList.getLastSent(), "GetReadingsByMeterNo", vendor.getCompanyName());
        return meterReads;
    }

    @Override
    public MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws MultispeakWebServiceException {
        init(); //init is already performed on the call to isAMRMeter()

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetLatestReadingByMeterNo", vendor.getCompanyName());
        
        //Validate the meterNo is a Yukon meterNumber
        YukonMeter meter = mspValidationService.isYukonMeterNumber(meterNo);
        
        boolean canInitiatePorterRequest = paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.PORTER_COMMAND_REQUESTS);
        
        //Custom hack put in only for SEDC.  Performs an actual meter read instead of simply replying from the database.
        if ( vendor.getCompanyName().equalsIgnoreCase("SEDC") && canInitiatePorterRequest) {
            
            // Don't know the responseURL as it's not provided in this method (by definition!) Using default for SEDC.
            String responseUrl = multispeakFuncs.getResponseUrl(vendor, null, MultispeakDefines.CB_Server_STR);
            MeterRead meterRead = multispeakMeterService.getLatestReadingInterrogate(vendor, meter, responseUrl);
            multispeakEventLogService.returnObject("MeterRead", meterNo, "GetLatestReadingByMeterNo", vendor.getCompanyName());
            return meterRead;
        } else { //THIS SHOULD BE WHERE EVERYONE ELSE GOES!!!
            try {
                MeterRead meterRead = meterReadProcessingService.createMeterRead(meter);
                
                EnumSet<BuiltInAttribute> attributesToLoad = EnumSet.of(BuiltInAttribute.USAGE, BuiltInAttribute.PEAK_DEMAND);
    
                for (BuiltInAttribute attribute : attributesToLoad) {
                    try {
                        LitePoint litePoint = attributeService.getPointForAttribute(meter, attribute);
                        PointValueQualityHolder pointValueQualityHolder = asyncDynamicDataSource.getPointValue(litePoint.getPointID());
                        if( pointValueQualityHolder != null && 
                                pointValueQualityHolder.getPointQuality() != PointQuality.Uninitialized) {
                            meterReadProcessingService.updateMeterRead(meterRead, attribute, pointValueQualityHolder);
                        }
                    } catch (IllegalUseOfAttribute e) {
                        //it's okay...just skip
                    }
                }
                multispeakEventLogService.returnObject("MeterRead", meterNo, "GetLatestReadingByMeterNo", vendor.getCompanyName());
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
            String formattedBlockTemplateName)
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
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("InitiateUsageMonitoring", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.initiateUsageMonitoring(vendor, meterNos);
        return errorObject;
    }
    
    @Override
    public List<ErrorObject> cancelUsageMonitoring(List<String> meterNos) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("CancelUsageMonitoring", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.cancelUsageMonitoring(vendor, meterNos);
        return errorObject;
    }
    
    @Override
    public List<ErrorObject> initiateDisconnectedStatus(List<String> meterNos) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("InitiateDisconnectedStatus", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.initiateDisconnectedStatus(vendor, meterNos);
        return errorObject;
    }
    
    @Override
    public List<ErrorObject> cancelDisconnectedStatus(List<String> meterNos) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("CancelDisconnectedStatus", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.cancelDisconnectedStatus(vendor, meterNos);
        return errorObject;
    }

    //Perform an actual read of the meter and return a CB_MR readingChangedNotification message for each meterNo
    @Override
    public List<ErrorObject> initiateMeterReadByMeterNumber(List<String> meterNos,
            String responseURL, String transactionID, Float expirationTime)
            throws MultispeakWebServiceException {
        init();
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("InitiateMeterReadByMeterNumber", vendor.getCompanyName());
        
        String actualResponseUrl = multispeakFuncs.getResponseUrl(vendor, responseURL, MultispeakDefines.CB_Server_STR);
        
        if ( ! porterConnection.isValid() ) {
            String message = "Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.";
            log.error(message);
            throw new MultispeakWebServiceException(message);
        }

        List<ErrorObject> errorObjects = multispeakMeterService.meterReadEvent(vendor, meterNos, transactionID, actualResponseUrl);

        multispeakFuncs.logErrorObjects(MultispeakDefines.MR_Server_STR, "InitiateMeterReadByMeterNumberRequest", errorObjects);
        return errorObjects;
    }
    
    @Override
    public List<ErrorObject> serviceLocationChangedNotification(List<ServiceLocation> changedServiceLocations) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("ServiceLocationChangedNotification", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.serviceLocationChanged(vendor, changedServiceLocations);
        return errorObject;
    }
    
    @Override
    public List<ErrorObject> meterChangedNotification(List<Meter> changedMeters) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("MeterChangedNotification", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.meterChanged(vendor, changedMeters);
        return errorObject;
    }
    
    @Override
    public List<ErrorObject> meterRemoveNotification(List<Meter> removedMeters) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("MeterRemoveNotification", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.meterRemove(vendor, removedMeters);
        return errorObject;
    }
    
    @Override
    public List<ErrorObject> meterAddNotification(List<Meter> addedMeters) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("MeterAddNotification", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.meterAdd(vendor, addedMeters);
        return errorObject;
    }

    @Override
    public ErrorObject deleteMeterGroup(String meterGroupID)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("DeleteMeterGroup", vendor.getCompanyName());
        return multispeakMeterService.deleteGroup(meterGroupID, vendor);
    }

    @Override
    public List<ErrorObject> establishMeterGroup(MeterGroup meterGroup)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("EstablishMeterGroup", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.addMetersToGroup(meterGroup, "EstablishMeterGroup", vendor);
        return errorObject;
    }

    @Override
    public List<ErrorObject> insertMeterInMeterGroup(List<String> meterNumbers,
            String meterGroupID) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("InsertMeterInMeterGroup", vendor.getCompanyName());
        MeterGroup meterGroup = objectFactory.createMeterGroup();
        MeterList meterList = objectFactory.createMeterList();
        List<String> meterID = meterList.getMeterID();
        for (String meterNumber : meterNumbers) {
            meterID.add(meterNumber);
        }
        meterGroup.setMeterList(meterList);
        meterGroup.setGroupName(meterGroupID);
        List<ErrorObject> errorObject = multispeakMeterService.addMetersToGroup(meterGroup, "InsertMeterInMeterGroup", vendor);
        return errorObject;
    }


    @Override
    public List<ErrorObject> removeMetersFromMeterGroup(List<String> meterNumbers,
            String meterGroupID) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("RemoveMetersFromMeterGroup", vendor.getCompanyName());
        
        List<ErrorObject> errorObject = multispeakMeterService.removeMetersFromGroup(meterGroupID, meterNumbers, vendor);
        return errorObject;
    }

    @Override
    public List<MeterRead> getLatestReadings(String lastReceived)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetLatestReadings", vendor.getCompanyName());
        
        MspMeterReadReturnList mspMeterReadReturnList = mspRawPointHistoryDao.retrieveLatestMeterReads(ReadBy.NONE, null, lastReceived, vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(mspMeterReadReturnList);

        List<MeterRead> meterReads = mspMeterReadReturnList.getMeterReads();
        log.info("Returning " + meterReads.size() + " latest Readings.");
        multispeakEventLogService.returnObjects(meterReads.size(), mspMeterReadReturnList.getObjectsRemaining(), "MeterRead", 
                                                mspMeterReadReturnList.getLastSent(), "GetLatestReadings", vendor.getCompanyName());

        return meterReads;
    }
    
    @Override
    public FormattedBlock getLatestReadingByMeterNoAndType(String meterNo,
            String readingType, String formattedBlockTemplateName) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetLatestReadingByMeterNoAndType", vendor.getCompanyName());
        
        YukonMeter meter = mspValidationService.isYukonMeterNumber(meterNo);
        
        FormattedBlockProcessingService<Block> formattedBlockProcessingService = 
            mspValidationService.getProcessingServiceByReadingType(readingTypesMap, readingType);
        
        try {
            Block block = formattedBlockProcessingService.createBlock(meter);
            
            EnumSet<BuiltInAttribute> attributesToLoad = formattedBlockProcessingService.getAttributeSet();

            for (BuiltInAttribute attribute : attributesToLoad) {
                try {
                    LitePoint litePoint = attributeService.getPointForAttribute(meter, attribute);
                    PointValueQualityHolder pointValueQualityHolder = asyncDynamicDataSource.getPointValue(litePoint.getPointID());
                    if( pointValueQualityHolder != null && 
                            pointValueQualityHolder.getPointQuality() != PointQuality.Uninitialized) {
                        formattedBlockProcessingService.updateFormattedBlock(block, attribute, pointValueQualityHolder);
                    }
                } catch (IllegalUseOfAttribute e) {
                    //it's okay...just skip
                }
            }
            FormattedBlock formattedBlock = formattedBlockProcessingService.createMspFormattedBlock(block);
            multispeakEventLogService.returnObject("FormattedBlock", meterNo, "GetLatestReadingByMeterNoAndType", vendor.getCompanyName());
            return formattedBlock;
            
        } catch (DynamicDataAccessException e) {
            String message = "Connection to dispatch is invalid";
            log.error(message);
            throw new MultispeakWebServiceException(message);
        }
    }
    
    @Override
    public List<FormattedBlock> getLatestReadingByType(String readingType,
            String lastReceived, String formattedBlockTemplateName) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetLatestReadingByType", vendor.getCompanyName());

        FormattedBlockProcessingService<Block> formattedBlockProcessingService = 
            mspValidationService.getProcessingServiceByReadingType(readingTypesMap, readingType);

        MspBlockReturnList mspBlockReturnList = mspRawPointHistoryDao.retrieveLatestBlock(formattedBlockProcessingService, 
                                                                       lastReceived, vendor.getMaxReturnRecords());
        multispeakFuncs.updateResponseHeader(mspBlockReturnList);
        
        FormattedBlock formattedBlock = formattedBlockProcessingService.createMspFormattedBlock(mspBlockReturnList.getBlocks());
        
        multispeakEventLogService.returnObjects(1, mspBlockReturnList.getObjectsRemaining(), "FormattedBlock", 
                                                mspBlockReturnList.getLastSent(), "GetLatestReadingByType", vendor.getCompanyName());

        return Collections.singletonList(formattedBlock);
    }
    
    @Override
    public List<FormattedBlock> getReadingsByDateAndType(Calendar startDate,
            Calendar endDate, String readingType, String lastReceived,
            String formattedBlockTemplateName)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetReadingsByDateAndType", vendor.getCompanyName());
        
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
                                                mspBlockReturnList.getLastSent(), "GetReadingsByDateAndType", vendor.getCompanyName());

        return Collections.singletonList(formattedBlock);
    }

    @Override
    public List<FormattedBlock> getReadingsByMeterNoAndType(String meterNo,
            Calendar startDate, Calendar endDate, String readingType,
            String lastReceived, String formattedBlockTemplateName) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetReadingsByMeterNoAndType", vendor.getCompanyName());
        
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
                                                mspBlockReturnList.getLastSent(), "GetReadingsByMeterNoAndType", vendor.getCompanyName());

        return Collections.singletonList(formattedBlock);
    }
    
    @Override
    public Set<String> getSupportedReadingTypes() throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetSupportedReadingTypes", vendor.getCompanyName());
        
        Set<String> types = readingTypesMap.keySet();
        return types;
    }
    
    @Override
    public List<ErrorObject> initiateMeterReadByMeterNoAndType(String meterNo, String responseURL,
            String readingType, String transactionID,
            Float expirationTime) throws MultispeakWebServiceException {
        init();
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("InitiateMeterReadByMeterNoAndType", vendor.getCompanyName());
        
        String actualResponseUrl = multispeakFuncs.getResponseUrl(vendor, responseURL, MultispeakDefines.CB_Server_STR);
        if ( ! porterConnection.isValid() ) {
            String message = "Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.";
            log.error(message);                
            throw new MultispeakWebServiceException(message);
        }
        
        FormattedBlockProcessingService<Block> formattedBlockServ = 
            mspValidationService.getProcessingServiceByReadingType(readingTypesMap, readingType);
        
        List<ErrorObject> errorObjects = multispeakMeterService.blockMeterReadEvent(vendor, meterNo, formattedBlockServ, transactionID, actualResponseUrl);

        multispeakFuncs.logErrorObjects(MultispeakDefines.MR_Server_STR, "InitiateMeterReadByMeterNoAndTypeRequest", errorObjects);
        return errorObjects;
    }

    @Override
    public List<ErrorObject> initiateDemandReset(List<MeterIdentifier> meterIDs,
            String responseURL, String transactionId, Float expirationTime)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("InitiateDemandReset", vendor.getCompanyName());
        
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
                                                           "InitiateDemandReset", vendor.getCompanyName()));
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
                                                   "InitiateDemandReset", vendor.getCompanyName()));
        }

        if (hasFatalErrors || validMeters.isEmpty()) {
            return errors;
        }

        log.info("Received " + meterIDs.size() + " Meter(s) for Demand Reset from " + vendor.getCompanyName());
        multispeakEventLogService.initiateDemandResetRequest(meterNumbers.size(), meterNumbersByPaoId.size(), 
                                                             invalidMeterNumbers.size(), unsupportedMeters.size(),
                                                             "InitiateConnectDisconnect", vendor.getCompanyName());

        MRServerDemandResetCallback callback =
                new MRServerDemandResetCallback(mspObjectDao, multispeakEventLogService, vendor, meterNumbersByPaoId, actualResponseUrl, transactionId);
        
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
}