package com.cannontech.multispeak.deploy.service.impl.v4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.cannontech.msp.beans.v4.ArrayOfMeterID;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.ExpirationTime;
import com.cannontech.msp.beans.v4.FormattedBlock;
import com.cannontech.msp.beans.v4.MeterGroup;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.MeterReading;
import com.cannontech.msp.beans.v4.Meters;
import com.cannontech.msp.beans.v4.MspMeter;
import com.cannontech.msp.beans.v4.ObjectFactory;
import com.cannontech.msp.beans.v4.ServiceLocation;
import com.cannontech.multispeak.block.v4.Block;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.dao.v4.FormattedBlockProcessingService;
import com.cannontech.multispeak.dao.v4.MeterReadingProcessingService;
import com.cannontech.multispeak.dao.v4.MspObjectDao;
import com.cannontech.multispeak.dao.v4.MspRawPointHistoryDao;
import com.cannontech.multispeak.dao.v4.MspRawPointHistoryDao.ReadBy;
import com.cannontech.multispeak.data.v4.FieldNamesMspV4;
import com.cannontech.multispeak.data.v4.MspBlockReturnList;
import com.cannontech.multispeak.data.v4.MspMeterReadingReturnList;
import com.cannontech.multispeak.data.v4.MspMeterReturnList;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v4.MR_Server;
import com.cannontech.multispeak.service.v4.MspValidationService;
import com.cannontech.multispeak.service.v4.MultispeakMeterService;
import com.cannontech.user.UserUtils;
import com.cannontech.yukon.BasicServerConnection;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class MR_ServerImpl implements MR_Server {

    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MspRawPointHistoryDao mspRawPointHistoryDao;
    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private MeterReadingProcessingService meterReadingProcessingService;
    @Autowired private MspValidationService mspValidationService;
    @Autowired private MultispeakMeterService multispeakMeterService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PaoDao paoDao;
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private DemandResetService demandResetService;
    @Autowired private ObjectFactory objectFactory;
    @Autowired @Qualifier("mspMeterDaoV4") private MspMeterDao mspMeterDao;
    private Map<String, FormattedBlockProcessingService<Block>> formattedBlockMap;
    private BasicServerConnection porterConnection;

    private final Logger log = YukonLogManager.getLogger(MR_ServerImpl.class);

    private final static String[] methods = new String[] { "PingURL",
                                                           "GetMethods",
                                                           "GetReadingsByDate",
                                                           "GetReadingsByMeterID",
                                                           "GetLatestReadings",
                                                           "GetLatestReadingByMeterID",
                                                           "IsAMRMeter",
                                                           "InitiateUsageMonitoring",
                                                           "CancelUsageMonitoring",
                                                           "GetAMRSupportedMeters",
                                                           "GetSupportedFieldNames",
                                                           "GetReadingsByDateAndFieldName",
                                                           "GetReadingsByMeterIDAndFieldName",
                                                           "GetLatestReadingByFieldName",
                                                           "GetLatestReadingByMeterIDAndFieldName",
                                                           "ServiceLocationChangedNotification",
                                                           "MeterAddNotification",
                                                           "InitiateDemandReset",
                                                           "MeterRemoveNotification",
                                                           "EstablishMeterGroup",
                                                           "InsertMeterInMeterGroup",
                                                           "DeleteMeterGroup",
                                                           "RemoveMetersFromMeterGroup"
                                                           };

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
        return multispeakFuncs.getMethods(MultispeakDefines.MR_Server_STR, Arrays.asList(methods));
    }
    
    @Override
    public List<MeterReading> getReadingsByDate(Calendar startDate, Calendar endDate, String lastReceived)
            throws MultispeakWebServiceException {
        init();

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetReadingsByDate", vendor.getCompanyName());

        MspMeterReadingReturnList mspMeterReadingReturnList = mspRawPointHistoryDao.retrieveMeterReading(ReadBy.NONE,
                                                                                            null, // get all
                                                                                            startDate.getTime(),
                                                                                            endDate.getTime(),
                                                                                            lastReceived,
                                                                                            vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(mspMeterReadingReturnList);
        List<MeterReading> meterReading = mspMeterReadingReturnList.getMeterReading();
        log.info("Returning " + meterReading.size() + " Readings By Date.");
        multispeakEventLogService.returnObjects(meterReading.size(), mspMeterReadingReturnList.getObjectsRemaining(),
                "MeterReading",
                mspMeterReadingReturnList.getLastSent(), "GetReadingsByDate", vendor.getCompanyName());

        return meterReading;
    }

    @Override
    public List<MeterReading> getReadingsByMeterID(String meterNo, Calendar startDate, Calendar endDate)
            throws MultispeakWebServiceException {
        init(); // init is already performed on the call to isAMRMeter()

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetReadingsByMeterID", vendor.getCompanyName());

        // Validate the meterNo is a Yukon meterNumber
        mspValidationService.isYukonMeterNumber(meterNo);

        MspMeterReadingReturnList mspMeterReadingReturnList = mspRawPointHistoryDao.retrieveMeterReading(ReadBy.METER_NUMBER,
                                                                                                meterNo,
                                                                                                startDate.getTime(),
                                                                                                endDate.getTime(),
                                                                                                null,
                                                                                                vendor.getMaxReturnRecords());

        // There is only one MeterNo in the response, so does it make sense to update the header with lastSent?
        List<MeterReading> meterReading = mspMeterReadingReturnList.getMeterReading();
        log.info("Returning " + meterReading.size() + " Readings By MeterID.");
        multispeakEventLogService.returnObjects(meterReading.size(), mspMeterReadingReturnList.getObjectsRemaining(),
                "MeterReading",
                mspMeterReadingReturnList.getLastSent(), "GetReadingsByMeterID", vendor.getCompanyName());
    
        return meterReading;
    }

    @Override
    public List<MeterReading> getLatestReadings(String lastReceived)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetLatestReadings", vendor.getCompanyName());

        MspMeterReadingReturnList mspMeterReadingReturnList = mspRawPointHistoryDao.retrieveLatestMeterReading(ReadBy.NONE, null,
                lastReceived, vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(mspMeterReadingReturnList);

        List<MeterReading> meterReading = mspMeterReadingReturnList.getMeterReading();
        log.info("Returning " + meterReading.size() + " latest Readings.");
        multispeakEventLogService.returnObjects(meterReading.size(), mspMeterReadingReturnList.getObjectsRemaining(),
                "MeterReading",
                mspMeterReadingReturnList.getLastSent(), "GetLatestReadings", vendor.getCompanyName());

        return meterReading;
    }

    @Override
    public MeterReading getLatestReadingByMeterID(String meterNo) throws MultispeakWebServiceException {
        init(); //init is already performed on the call to isAMRMeter()

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetLatestReadingByMeterID", vendor.getCompanyName());
        
        //Validate the meterNo is a Yukon meterNumber
        YukonMeter meter = mspValidationService.isYukonMeterNumber(meterNo);
        
        boolean canInitiatePorterRequest = paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.PORTER_COMMAND_REQUESTS);
        
        //Custom hack put in only for SEDC.  Performs an actual meter read instead of simply replying from the database.
        if ( vendor.getCompanyName().equalsIgnoreCase("SEDC") && canInitiatePorterRequest) {
            
            // Don't know the responseURL as it's not provided in this method (by definition!) Using default for SEDC.
            String responseUrl = multispeakFuncs.getResponseUrl(vendor, null, MultispeakDefines.CB_Server_STR);
            MeterReading meterReading = multispeakMeterService.getLatestReadingInterrogate(vendor, meter, responseUrl);
            multispeakEventLogService.returnObject("MeterReading", meterNo, "GetLatestReadingByMeterID", vendor.getCompanyName());
            return meterReading;
        } else { //THIS SHOULD BE WHERE EVERYONE ELSE GOES!!!
            try {
                MeterReading meterReading = meterReadingProcessingService.createMeterReading(meter);
                
                EnumSet<BuiltInAttribute> attributesToLoad = EnumSet.of(BuiltInAttribute.USAGE, BuiltInAttribute.PEAK_DEMAND);
    
                for (BuiltInAttribute attribute : attributesToLoad) {
                    try {
                        LitePoint litePoint = attributeService.getPointForAttribute(meter, attribute);
                        PointValueQualityHolder pointValueQualityHolder = asyncDynamicDataSource.getPointValue(litePoint.getPointID());
                        if( pointValueQualityHolder != null && 
                                pointValueQualityHolder.getPointQuality() != PointQuality.Uninitialized) {
                            meterReadingProcessingService.updateMeterReading(meterReading, attribute, pointValueQualityHolder);
                        }
                    } catch (IllegalUseOfAttribute e) {
                        //it's okay...just skip
                    }
                }
                multispeakEventLogService.returnObject("MeterReading", meterNo, "GetLatestReadingByMeterID",
                        vendor.getCompanyName());
                return meterReading;
            } catch (DynamicDataAccessException e) {
                String message = "Connection to dispatch is invalid";
                log.error(message);
                throw new MultispeakWebServiceException(message);
            }
        }
    }
    
    @Override
    public List<FormattedBlock> getReadingsByDateAndFieldName(Calendar startDate,
            Calendar endDate, String lastReceived, String formattedBlockTemplateName) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("getReadingsByDateAndFieldName", vendor.getCompanyName());

        FormattedBlockProcessingService<Block> formattedBlockProcessingService = mspValidationService
                .getProcessingServiceByFormattedBlockTemplate(formattedBlockMap, formattedBlockTemplateName);

        MspBlockReturnList mspBlockReturnList = mspRawPointHistoryDao.retrieveBlock(ReadBy.NONE, null,
                                                                                    formattedBlockProcessingService,
                                                                                    startDate.getTime(),
                                                                                    endDate.getTime(),
                                                                                    lastReceived,
                                                                                    vendor.getMaxReturnRecords());
        multispeakFuncs.updateResponseHeader(mspBlockReturnList);

        FormattedBlock formattedBlock = formattedBlockProcessingService.createMspFormattedBlock(mspBlockReturnList.getBlocks());

        multispeakEventLogService.returnObjects(1, mspBlockReturnList.getObjectsRemaining(), "FormattedBlock",
                mspBlockReturnList.getLastSent(), "getReadingsByDateAndFieldName", vendor.getCompanyName());

        return Collections.singletonList(formattedBlock);
    }

    @Required
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }

    @Required
    public void setFormattedBlockMap(
            Map<String, FormattedBlockProcessingService<Block>> formattedBlockMap) {
        this.formattedBlockMap = formattedBlockMap;
    }
    
    @Override
    public List<FormattedBlock> getReadingsByMeterIDAndFieldName(String meterNo, Calendar startDate, Calendar endDate,
            String lastReceived, String formattedBlockTemplateName)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetReadingsByMeterIDAndFieldName", vendor.getCompanyName());

        // Validate the meterNo is in Yukon
        mspValidationService.isYukonMeterNumber(meterNo);

        FormattedBlockProcessingService<Block> formattedBlockProcessingService = mspValidationService
                .getProcessingServiceByFormattedBlockTemplate(formattedBlockMap, formattedBlockTemplateName);

        MspBlockReturnList mspBlockReturnList = mspRawPointHistoryDao.retrieveBlock(ReadBy.METER_NUMBER, meterNo,
                                                                                    formattedBlockProcessingService,
                                                                                    startDate.getTime(),
                                                                                    endDate.getTime(),
                                                                                    null, // don't use lastReceived, we know there is only one
                                                                                    vendor.getMaxReturnRecords());

        FormattedBlock formattedBlock = formattedBlockProcessingService.createMspFormattedBlock(mspBlockReturnList.getBlocks());

        multispeakEventLogService.returnObjects(1, mspBlockReturnList.getObjectsRemaining(), "FormattedBlock",
                mspBlockReturnList.getLastSent(), "GetReadingsByMeterIDAndFieldName", vendor.getCompanyName());

        return Collections.singletonList(formattedBlock);
    }

    @Override
    public FormattedBlock getLatestReadingByMeterIDAndFieldName(String meterNo,
            String formattedBlockTemplateName) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetLatestReadingByMeterIDAndFieldName", vendor.getCompanyName());

        YukonMeter meter = mspValidationService.isYukonMeterNumber(meterNo);

        FormattedBlockProcessingService<Block> formattedBlockProcessingService = mspValidationService
                .getProcessingServiceByFormattedBlockTemplate(formattedBlockMap, formattedBlockTemplateName);

        try {
            Block block = formattedBlockProcessingService.createBlock(meter);

            EnumSet<BuiltInAttribute> attributesToLoad = formattedBlockProcessingService.getAttributeSet();

            for (BuiltInAttribute attribute : attributesToLoad) {
                try {
                    LitePoint litePoint = attributeService.getPointForAttribute(meter, attribute);
                    PointValueQualityHolder pointValueQualityHolder = asyncDynamicDataSource
                            .getPointValue(litePoint.getPointID());
                    if (pointValueQualityHolder != null &&
                            pointValueQualityHolder.getPointQuality() != PointQuality.Uninitialized) {
                        formattedBlockProcessingService.updateFormattedBlock(block, attribute, pointValueQualityHolder);
                    }
                } catch (IllegalUseOfAttribute e) {
                    // it's okay...just skip
                }
            }
            FormattedBlock formattedBlock = formattedBlockProcessingService.createMspFormattedBlock(block);
            multispeakEventLogService.returnObject("FormattedBlock", meterNo, "GetLatestReadingByMeterIDAndFieldName",
                    vendor.getCompanyName());
            return formattedBlock;

        } catch (DynamicDataAccessException e) {
            String message = "Connection to dispatch is invalid";
            log.error(message);
            throw new MultispeakWebServiceException(message);
        }
    }

    @Override
    public List<FormattedBlock> getLatestReadingByFieldName(String lastReceived, String formattedBlockTemplateName)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetLatestReadingByFieldName", vendor.getCompanyName());

        FormattedBlockProcessingService<Block> formattedBlockProcessingService = mspValidationService
                .getProcessingServiceByFormattedBlockTemplate(formattedBlockMap, formattedBlockTemplateName);

        MspBlockReturnList mspBlockReturnList = mspRawPointHistoryDao.retrieveLatestBlock(formattedBlockProcessingService,
                lastReceived, vendor.getMaxReturnRecords());
        multispeakFuncs.updateResponseHeader(mspBlockReturnList);

        FormattedBlock formattedBlock = formattedBlockProcessingService.createMspFormattedBlock(mspBlockReturnList.getBlocks());

        multispeakEventLogService.returnObjects(1, mspBlockReturnList.getObjectsRemaining(), "FormattedBlock",
                mspBlockReturnList.getLastSent(), "GetLatestReadingByFieldName", vendor.getCompanyName());

        return Collections.singletonList(formattedBlock);
    }

    @Override
    public boolean isAMRMeter(String meterNo) throws MultispeakWebServiceException {
        init();
        multispeakFuncs.getMultispeakVendorFromHeader();
        // Commenting out for now, not sure if we want this logged or not, it
        // could be a lot...and doesn't have much impact to the system
        // multispeakEventLogService.methodInvoked("IsAMRMeter", vendor.getCompanyName());

        boolean isAmrMeter = false;
        try {
            mspValidationService.isYukonMeterNumber(meterNo);
            isAmrMeter = true;
        } catch (final MultispeakWebServiceException e) {
            isAmrMeter = false;
        }
        log.debug("IsAMRMeter " + isAmrMeter + " for " + meterNo + ".");
        return isAmrMeter;
    }

    @Override
    public List<ErrorObject> initiateUsageMonitoring(List<MeterID> meterIds) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("InitiateUsageMonitoring", vendor.getCompanyName());

        List<ErrorObject> errorObject = multispeakMeterService.initiateUsageMonitoring(vendor, meterIds);
        return errorObject;
    }

    @Override
    public List<ErrorObject> cancelUsageMonitoring(List<MeterID> meterIds) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("CancelUsageMonitoring", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.cancelUsageMonitoring(vendor, meterIds);
        return errorObject;
    }

    @Override
    public Meters getAMRSupportedMeters(String lastReceived) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetAMRSupportedMeters", vendor.getCompanyName());

        Date timerStart = new Date();
        MspMeterReturnList meterList = (MspMeterReturnList) mspMeterDao.getAMRSupportedMeters(lastReceived,
                vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(meterList);

        log.info("Returning " + meterList.getSize() + " AMR Supported Meters. ("
                + (new Date().getTime() - timerStart.getTime()) * .001 + " secs)");
        multispeakEventLogService.returnObjects(meterList.getSize(), meterList.getObjectsRemaining(), "MspMeter",
                meterList.getLastSent(), "GetAMRSupportedMeters", vendor.getCompanyName());

        return meterList.getMeters();
    }

    @Override
    public List<String> getSupportedFieldNames() throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetSupportedFieldNames", vendor.getCompanyName());

        Set<BuiltInAttribute> attributes = meterReadingProcessingService.getAttributes();

        List<String> fieldNames = attributes.stream()
                .map(attribute -> FieldNamesMspV4.getFieldNamesMspV4ByAttribute(attribute).getFieldName())
                .collect(Collectors.toList());

        return fieldNames;
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
    public List<ErrorObject> insertMeterInMeterGroup(List<MeterID> meterIds,
            String meterGroupId)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("InsertMeterInMeterGroup", vendor.getCompanyName());

        List<ErrorObject> errorObject = new ArrayList<ErrorObject>();
        if (meterIds != null && meterGroupId != null) {
            MeterGroup meterGroup = new MeterGroup();

            ArrayOfMeterID arrayOfMeterId = objectFactory.createArrayOfMeterID();
            List<MeterID> meterList = arrayOfMeterId.getMeterID();

            for (MeterID id : meterIds) {
                meterList.add(id);
            }

            meterGroup.setMeterList(arrayOfMeterId);
            meterGroup.setGroupName(meterGroupId);

            errorObject = multispeakMeterService.addMetersToGroup(meterGroup,
                    "InsertMeterInMeterGroup", vendor);
        }
        return errorObject;
    }

    @Override
    public ErrorObject deleteMeterGroup(String meterGroupId)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("DeleteMeterGroup", vendor.getCompanyName());
        return multispeakMeterService.deleteGroup(meterGroupId, vendor);
    }

    @Override
    public List<ErrorObject> removeMetersFromMeterGroup(List<MeterID> meterIds,
            String meterGroupId) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("RemoveMetersFromMeterGroup", vendor.getCompanyName());

        List<ErrorObject> errorObject = multispeakMeterService.removeMetersFromGroup(meterGroupId, meterIds, vendor);
        return errorObject;
    }

    public List<ErrorObject> serviceLocationChangedNotification(List<ServiceLocation> serviceLocations)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("ServiceLocationChangedNotification", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.serviceLocationChanged(vendor, serviceLocations);
        return errorObject;
    }
    
    @Override
    public List<ErrorObject> meterAddNotification(List<MspMeter> addedMeters) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("MeterAddNotification", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.meterAdd(vendor, addedMeters);
        return errorObject;
    }
    
    @Override
    public List<ErrorObject> initiateDemandReset(List<MeterID> meterIds, String responseURL, String transactionId,
            ExpirationTime expirationTime) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("InitiateDemandReset", vendor.getCompanyName());

        List<ErrorObject> errors = Lists.newArrayList();
        boolean hasFatalErrors = false;

        String actualResponseUrl = multispeakFuncs.getResponseUrl(vendor, responseURL, MultispeakDefines.CB_Server_STR);

        // Do a basic URL check. This only validates that it's not empty.
        ErrorObject errorObject = mspValidationService.validateResponseURL(actualResponseUrl, "ResponseURL", "InitiateDemandReset");
        if (errorObject != null) {
            errors.add(errorObject);
            hasFatalErrors = true;
        }

        Set<String> meterNumbers = meterIds.stream().map(meterId -> meterId.getMeterNo()).collect(Collectors.toSet());
        Map<String, PaoIdentifier> paoIdsByMeterNumber = paoDao.findPaoIdentifiersByMeterNumber(meterNumbers);
        Map<PaoIdentifier, String> meterNumbersByPaoId = HashBiMap.create(paoIdsByMeterNumber).inverse();
        Set<String> invalidMeterNumbers = Sets.difference(meterNumbers, paoIdsByMeterNumber.keySet());

        for (String invalidMeterNumber : invalidMeterNumbers) {
            errors.add(mspObjectDao.getNotFoundErrorObject(invalidMeterNumber,
                                                           "MeterID", 
                                                           "MeterNumber",
                                                           "InitiateDemandReset", 
                                                           vendor.getCompanyName()));
        }

        Set<PaoIdentifier> meterIdentifiers = Sets.newHashSet(paoIdsByMeterNumber.values());
        Set<PaoIdentifier> validMeters = Sets.newHashSet(demandResetService.filterDevices(meterIdentifiers));
        Set<PaoIdentifier> unsupportedMeters = Sets.difference(meterIdentifiers, validMeters);
        for (PaoIdentifier unsupportedMeter : unsupportedMeters) {
            String errorMsg = unsupportedMeter.getPaoIdentifier().getPaoType()
                    + " does not support demand reset";
            String meterNumber = meterNumbersByPaoId.get(unsupportedMeter);
            errors.add(mspObjectDao.getErrorObject(meterNumber, errorMsg, 
                                                   "MeterID", "InitiateDemandReset", 
                                                   vendor.getCompanyName()));
        }

        if (hasFatalErrors || validMeters.isEmpty()) {
            return errors;
        }

        log.info("Received " + meterIds.size() + " Meter(s) for Demand Reset from " + vendor.getCompanyName());
        multispeakEventLogService.initiateDemandResetRequest(meterNumbers.size(), meterNumbersByPaoId.size(),
                invalidMeterNumbers.size(), unsupportedMeters.size(),
                "InitiateConnectDisconnect", vendor.getCompanyName());

        MRServerDemandResetCallback callback = new MRServerDemandResetCallback(mspObjectDao, multispeakEventLogService,
                                                                               vendor, meterNumbersByPaoId,
                                                                               actualResponseUrl, transactionId,
                                                                               expirationTime);

        demandResetService.sendDemandResetAndVerify(validMeters, callback, UserUtils.getYukonUser());
        errors.addAll(callback.getErrors());

        return errors;
    }

    @Override
    public List<ErrorObject> meterRemoveNotification(List<MspMeter> removedMeters) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("MeterRemoveNotification", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.meterRemove(vendor, removedMeters);
        return errorObject;
    }
}
