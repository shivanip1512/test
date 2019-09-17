package com.cannontech.multispeak.deploy.service.impl.v5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.demandreset.service.DemandResetService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.commontypes.MeterID;
import com.cannontech.msp.beans.v5.commontypes.ObjectID;
import com.cannontech.msp.beans.v5.multispeak.FormattedBlock;
import com.cannontech.msp.beans.v5.multispeak.MeterGroup;
import com.cannontech.msp.beans.v5.multispeak.MeterIDs;
import com.cannontech.msp.beans.v5.multispeak.MeterReading;
import com.cannontech.msp.beans.v5.multispeak.Meters;
import com.cannontech.msp.beans.v5.multispeak.ReadingTypeCode;
import com.cannontech.multispeak.block.v5.Block;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.dao.v5.FormattedBlockProcessingService;
import com.cannontech.multispeak.dao.v5.MspObjectDao;
import com.cannontech.multispeak.dao.v5.MspRawPointHistoryDao;
import com.cannontech.multispeak.dao.v5.MspRawPointHistoryDao.ReadBy;
import com.cannontech.multispeak.data.v5.MspBlockReturnList;
import com.cannontech.multispeak.data.v5.MspMeterReadReturnList;
import com.cannontech.multispeak.data.v5.MspMeterReturnList;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v5.MR_Server;
import com.cannontech.multispeak.service.v5.MspValidationService;
import com.cannontech.multispeak.service.v5.MultispeakMeterService;
import com.cannontech.user.UserUtils;
import com.cannontech.yukon.BasicServerConnection;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


public class MR_ServerImpl implements MR_Server {

    @Autowired private DemandResetService demandResetService;
    @Autowired @Qualifier("mspMeterDaoV5") private MspMeterDao mspMeterDao;
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private MspRawPointHistoryDao mspRawPointHistoryDao;
    @Autowired private MspValidationService mspValidationService;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MultispeakMeterService multispeakMeterService;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private PaoDao paoDao;
    private BasicServerConnection porterConnection;

    private Map<String, FormattedBlockProcessingService<Block>> readingTypesMap;

    private final Logger log = YukonLogManager.getLogger(MR_ServerImpl.class);
    private final static String[] methods = new String[] {
        "PingURL",
        "GetMethods",
        "GetMeterReadingsByDate",
        "GetAMRSupportedMeters",
        "GetLatestMeterReadings",
        "GetMeterReadingsByMeterIDs",
        "GetLatestMeterReadingsByMeterIDs",
        "GetLatestMeterReadingsByMeterIDsAndReadingTypeCodes",
        "GetLatestMeterReadingsByReadingTypeCodes",
        "GetMeterReadingsByDateAndReadingTypeCodes", 
        "GetMeterReadingsByMeterIDsAndReadingTypeCodes",
        "InitiateMeterReadingsByMeterIDs",
        "InitiateMeterReadingsByReadingTypeCodes",
        "InitiateDemandReset",
        "IsAMRMeter",
        "InitiateEndDeviceEventMonitoring",
        "CancelEndDeviceEventMonitoring",
        "SetDisconnectedStatus",
        "ClearDisconnectedStatus",
        "DeleteMeterGroups",
        "CreateMeterGroups",
        "InsertMeterInMeterGroup",
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
        return multispeakFuncs.getMethods(MultispeakDefines.MR_Server_STR , Arrays.asList(methods));
    }
    
    @Override
    public List<MeterReading> getMeterReadingsByDate(Calendar startDate, Calendar endDate, String lastReceived)
            throws MultispeakWebServiceException {
        init();

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetMeterReadingsByDate", vendor.getCompanyName());

        MspMeterReadReturnList mspMeterReadReturnList = mspRawPointHistoryDao.retrieveMeterReads(ReadBy.NONE, null,
            startDate.getTime(), endDate.getTime(), lastReceived, vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(mspMeterReadReturnList);
        List<MeterReading> meterReads = mspMeterReadReturnList.getMeterReads();
        log.info("Returning " + meterReads.size() + " Readings By Date.");
        multispeakEventLogService.returnObjects(meterReads.size(), mspMeterReadReturnList.getObjectsRemaining(),
            "MeterRead", mspMeterReadReturnList.getLastSent(), "GetMeterReadingsByDate", vendor.getCompanyName());

        return meterReads;
    }

    @Override
    public List<MeterReading> getMeterReadingsByMeterIDs(List<MeterID> meterIDs, Calendar startDate, Calendar endDate)
            throws MultispeakWebServiceException {
        init(); // init is already performed on the call to isAMRMeter()
        List<MeterReading> meterReads = new ArrayList<MeterReading>();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        MspMeterReadReturnList mspMeterReadReturnList = null;
        multispeakEventLogService.methodInvoked("GetMeterReadingsByMeterIDs", vendor.getCompanyName());
        
        if (mspValidationService.isValidMeterCount(meterIDs, vendor.getMaxReturnRecords())) {

            List<String> meterNbrList =
                meterIDs.stream().map(meterID -> meterID.getMeterName()).collect(Collectors.toList());

            // Retrieve meter reads for all meters.
            mspMeterReadReturnList =
                mspRawPointHistoryDao.retrieveMeterReads(ReadBy.METER_NUMBERS, meterNbrList, startDate.getTime(),
                    endDate.getTime(), null, vendor.getMaxReturnRecords());
            meterReads.addAll(mspMeterReadReturnList.getMeterReads());
            log.info("Returning " + mspMeterReadReturnList.getMeterReads().size() + "Readings By Meter Number/s"
                + meterNbrList);

            multispeakEventLogService.returnObject("MeterRead", meterNbrList.size(), "GetMeterReadingsByMeterIDs",
                vendor.getCompanyName());
        }
       
        return meterReads;
    }
    
    @Override
    public Meters getAMRSupportedMeters(String lastReceived) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetAMRSupportedMeters", vendor.getCompanyName());

        Date timerStart = new Date();
        MspMeterReturnList meterList = (MspMeterReturnList) mspMeterDao.getAMRSupportedMeters(lastReceived, vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(meterList);

        log.info("Returning " + meterList.getSize() + " AMR Supported Meters. ("
            + (new Date().getTime() - timerStart.getTime()) * .001 + " secs)");
        multispeakEventLogService.returnObjects(meterList.getSize(), meterList.getObjectsRemaining(), "Meter",
            meterList.getLastSent(), "GetAMRSupportedMeters", vendor.getCompanyName());

        return meterList.getMeters();
    }

    @Override
    public List<MeterReading> getLatestMeterReadings(String lastReceived) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetLatestMeterReadings", vendor.getCompanyName());

        MspMeterReadReturnList mspMeterReadReturnList =
            mspRawPointHistoryDao.retrieveLatestMeterReads(ReadBy.NONE, null, lastReceived,
                vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(mspMeterReadReturnList);

        List<MeterReading> meterReads = mspMeterReadReturnList.getMeterReads();
        log.info("Returning " + meterReads.size() + " latest Readings.");
        multispeakEventLogService.returnObjects(meterReads.size(), mspMeterReadReturnList.getObjectsRemaining(),
            "MeterRead", mspMeterReadReturnList.getLastSent(), "GetLatestMeterReadings", vendor.getCompanyName());

        return meterReads;
    }

    @Override
    public List<MeterReading> getLatestMeterReadingsByMeterIDs(List<MeterID> meterIDs)
            throws MultispeakWebServiceException {
        init(); // init is already performed on the call to isAMRMeter()
        List<MeterReading> meterReadings = new ArrayList<MeterReading>();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        MspMeterReadReturnList mspMeterReadReturnList = null;
        multispeakEventLogService.methodInvoked("GetLatestMeterReadingsByMeterIDs", vendor.getCompanyName());
        
        if (mspValidationService.isValidMeterCount(meterIDs, vendor.getMaxReturnRecords())) {

            List<String> meterNbrList =
                meterIDs.stream().map(meterID -> meterID.getMeterName()).collect(Collectors.toList());

            mspMeterReadReturnList =
                    mspRawPointHistoryDao.retrieveLatestMeterReads(ReadBy.METER_NUMBERS, meterNbrList, null,
                        vendor.getMaxReturnRecords());

            meterReadings = mspMeterReadReturnList.getMeterReads();
            multispeakEventLogService.returnObject("MeterRead", meterNbrList.size(), "GetLatestMeterReadingsByMeterIDs",
                vendor.getCompanyName());
        }
        
        return meterReadings;
    }
    
    @Override
    public List<FormattedBlock> getMeterReadingsByDateAndReadingTypeCodes(Calendar startDate, Calendar endDate,
            List<ReadingTypeCode> readingTypeCodes, String lastReceived, ObjectID formattedBlockTemplateID)
            throws MultispeakWebServiceException {
        String blockTemplateName = null;
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetMeterReadingsByDateAndReadingTypeCodes", vendor.getCompanyName());

        // TODO template name can come from objectID or primaryIdentifier value (objectGuid is mandatory)
        if (formattedBlockTemplateID!= null && formattedBlockTemplateID.getObjectGUID() != null) {
            blockTemplateName = formattedBlockTemplateID.getObjectGUID();

            if (!readingTypesMap.containsKey(blockTemplateName)) {

                if (formattedBlockTemplateID.getPrimaryIdentifier() != null) {
                    blockTemplateName = formattedBlockTemplateID.getPrimaryIdentifier().getValue();
                } else {

                    String errorMessage = "formattedBlockTemplate value is not present in request";
                    log.error(errorMessage);
                    throw new MultispeakWebServiceException(errorMessage);
                }
            }
        } else {
            String errorMessage = "formattedBlockTemplateID is not present in request";
            log.error(errorMessage);
            throw new MultispeakWebServiceException(errorMessage);
        }

        FormattedBlockProcessingService<Block> formattedBlockProcessingService =
            mspValidationService.getProcessingServiceByReadingType(readingTypesMap, blockTemplateName);

        MspBlockReturnList mspBlockReturnList =
            mspRawPointHistoryDao.retrieveBlock(ReadBy.NONE, null, formattedBlockProcessingService,
                startDate.getTime(), endDate.getTime(), lastReceived, vendor.getMaxReturnRecords());
        multispeakFuncs.updateResponseHeader(mspBlockReturnList);

        FormattedBlock formattedBlock =
            formattedBlockProcessingService.createMspFormattedBlock(mspBlockReturnList.getBlocks());

        multispeakEventLogService.returnObjects(1, mspBlockReturnList.getObjectsRemaining(), "FormattedBlock",
            mspBlockReturnList.getLastSent(), "GetMeterReadingsByDateAndReadingTypeCodes", vendor.getCompanyName());
        return Collections.singletonList(formattedBlock);
    }

    @Override
    public FormattedBlock getLatestMeterReadingsByMeterIDsAndReadingTypeCodes(List<MeterID> meterIDs,
            List<ReadingTypeCode> readingTypeCodes, ObjectID formattedBlockTemplateID)
            throws MultispeakWebServiceException {
        String blockTemplateName;
        FormattedBlock formattedBlock = null;
        
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetLatestMeterReadingsByMeterIDsAndReadingTypeCodes",
            vendor.getCompanyName());

        boolean isValidMeterCount = mspValidationService.isValidMeterCount(meterIDs, vendor.getMaxReturnRecords());
        // TODO template name can come from objectID or primaryIdentifier value (objectGuid is mandatory)
        if (isValidMeterCount) {

            List<String> mspMeters = meterIDs.stream().map(new Function<MeterID, String>() {

                @Override
                public String apply(MeterID meterID) {
                    return meterID.getMeterName();
                }
            }).collect(Collectors.toList());

            if (formattedBlockTemplateID != null && formattedBlockTemplateID.getObjectGUID() != null) {
                blockTemplateName = formattedBlockTemplateID.getObjectGUID();

                if (!readingTypesMap.containsKey(blockTemplateName)) {

                    if (formattedBlockTemplateID.getPrimaryIdentifier() != null) {
                        blockTemplateName = formattedBlockTemplateID.getPrimaryIdentifier().getValue();
                    } else {
                        String errorMessage = "formattedBlockTemplate value is not present in request";
                        log.error(errorMessage);
                        throw new MultispeakWebServiceException(errorMessage);
                    }
                }
            } else {
                String errorMessage = "formattedBlockTemplateID is not present in request";
                log.error(errorMessage);
                throw new MultispeakWebServiceException(errorMessage);
            }

            FormattedBlockProcessingService<Block> formattedBlockProcessingService =
                mspValidationService.getProcessingServiceByReadingType(readingTypesMap, blockTemplateName);

            MspBlockReturnList mspBlockReturnList =
                mspRawPointHistoryDao.retrieveLatestBlock(ReadBy.METER_NUMBERS, formattedBlockProcessingService,
                    mspMeters, null, vendor.getMaxReturnRecords());

            formattedBlock = formattedBlockProcessingService.createMspFormattedBlock(mspBlockReturnList.getBlocks());

            multispeakEventLogService.returnObject("FormattedBlock", mspMeters.size(),
                "GetLatestMeterReadingsByMeterIDsAndReadingTypeCodes", vendor.getCompanyName());

        }

        return formattedBlock;
    }

    @Override
    public List<FormattedBlock> getMeterReadingsByMeterIDsAndReadingTypeCodes(List<MeterID> meterIDs,
            Calendar startDate, Calendar endDate, List<ReadingTypeCode> readingTypeCodes,
            ObjectID formattedBlockTemplateID) throws MultispeakWebServiceException {
        String blockTemplateName;
        FormattedBlock formattedBlock = null;

        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetMeterReadingsByMeterIDsAndReadingTypeCodes",
            vendor.getCompanyName());

        boolean isValidMeterCount = mspValidationService.isValidMeterCount(meterIDs, vendor.getMaxReturnRecords());

        if (isValidMeterCount) {

            List<String> mspMeters =
                meterIDs.stream().map(meterID -> meterID.getMeterName()).collect(Collectors.toList());

            // TODO template name can come from objectID or primaryIdentifier value (objectGuid is mandatory)
            if (formattedBlockTemplateID != null && formattedBlockTemplateID.getObjectGUID() != null) {
                blockTemplateName = formattedBlockTemplateID.getObjectGUID();

                if (!readingTypesMap.containsKey(blockTemplateName)) {

                    if (formattedBlockTemplateID.getPrimaryIdentifier() != null) {
                        blockTemplateName = formattedBlockTemplateID.getPrimaryIdentifier().getValue();
                    } else {

                        String errorMessage = "formattedBlockTemplate value is not present in request";
                        log.error(errorMessage);
                        throw new MultispeakWebServiceException(errorMessage);
                    }
                }
            } else {
                String errorMessage = "formattedBlockTemplateID is not present in request";
                log.error(errorMessage);
                throw new MultispeakWebServiceException(errorMessage);
            }

            FormattedBlockProcessingService<Block> formattedBlockProcessingService =
                mspValidationService.getProcessingServiceByReadingType(readingTypesMap, blockTemplateName);

            MspBlockReturnList mspBlockReturnList =
                mspRawPointHistoryDao.retrieveBlock(ReadBy.METER_NUMBERS, mspMeters, formattedBlockProcessingService,
                    startDate.getTime(), endDate.getTime(), null, vendor.getMaxReturnRecords());

            formattedBlock = formattedBlockProcessingService.createMspFormattedBlock(mspBlockReturnList.getBlocks());

            multispeakEventLogService.returnObject("FormattedBlock", mspMeters.size(),
                "GetMeterReadingsByMeterIDsAndReadingTypeCodes", vendor.getCompanyName());
        }
        return Collections.singletonList(formattedBlock);

    }

    @Override
    public List<FormattedBlock> getLatestMeterReadingsByReadingTypeCodes(List<ReadingTypeCode> readingTypeCodes,
            String lastReceived, ObjectID formattedBlockTemplateID) throws MultispeakWebServiceException {
        String blockTemplateName = null;
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetLatestMeterReadingsByReadingTypeCodes", vendor.getCompanyName());
        // TODO template name can come from objectID or primaryIdentifier value (objectGuid is mandatory)
        if (formattedBlockTemplateID != null && formattedBlockTemplateID.getObjectGUID() != null) {
            blockTemplateName = formattedBlockTemplateID.getObjectGUID();

            if (!readingTypesMap.containsKey(blockTemplateName)) {

                if (formattedBlockTemplateID.getPrimaryIdentifier() != null) {
                    blockTemplateName = formattedBlockTemplateID.getPrimaryIdentifier().getValue();
                } else {

                    String errorMessage = "formattedBlockTemplate value is not present in request";
                    log.error(errorMessage);
                    throw new MultispeakWebServiceException(errorMessage);
                }
            }
        } else {
            String errorMessage = "formattedBlockTemplateID is not present in request";
            log.error(errorMessage);
            throw new MultispeakWebServiceException(errorMessage);
        }

        FormattedBlockProcessingService<Block> formattedBlockProcessingService =
            mspValidationService.getProcessingServiceByReadingType(readingTypesMap, blockTemplateName);

        MspBlockReturnList mspBlockReturnList =
            mspRawPointHistoryDao.retrieveLatestBlock(ReadBy.NONE, formattedBlockProcessingService, null, lastReceived,
                vendor.getMaxReturnRecords());
        multispeakFuncs.updateResponseHeader(mspBlockReturnList);

        FormattedBlock formattedBlock =
            formattedBlockProcessingService.createMspFormattedBlock(mspBlockReturnList.getBlocks());

        multispeakEventLogService.returnObjects(1, mspBlockReturnList.getObjectsRemaining(), "FormattedBlock",
            mspBlockReturnList.getLastSent(), "GetLatestMeterReadingsByReadingTypeCodes", vendor.getCompanyName());

        return Collections.singletonList(formattedBlock);
    }
    
    @Override
    public List<ErrorObject> initiateMeterReadingsByMeterIDs(List<MeterID> meterIds,
            String responseURL, String transactionID, XMLGregorianCalendar expirationTime)
            throws MultispeakWebServiceException {
        init();

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("InitiateMeterReadingsByMeterIDs",
                                                vendor.getCompanyName());

        List<ErrorObject> errorObjects = new ArrayList<ErrorObject>();
        
        if (meterIds != null) {
        String actualResponseUrl = multispeakFuncs.getResponseUrl(vendor,
                                     responseURL, MultispeakDefines.NOT_Server_STR);

        if (!porterConnection.isValid()) {
            String message = "Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.";
            log.error(message);
            throw new MultispeakWebServiceException(message);
        }

        errorObjects = multispeakMeterService.meterReadEvent(vendor,
                                            meterIds, transactionID, actualResponseUrl);

        }
        multispeakFuncs.logErrorObjects(MultispeakDefines.MR_Server_STR,
                                        "InitiateMeterReadingsByMeterIDs",
                                        errorObjects);
        return errorObjects;
    }
    
    @Override
    public List<ErrorObject> initiateMeterReadingsByReadingTypeCodes(List<MeterID> meterIDs,
            String responseURL, List<ReadingTypeCode> readingType, String transactionID,
            XMLGregorianCalendar expirationTime, ObjectID formattedBlockTemplateID)
            throws MultispeakWebServiceException {
        init();

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("InitiateMeterReadingsByReadingTypeCodes",
                                                vendor.getCompanyName());

        String blockTemplateName = null;
        String actualResponseUrl = multispeakFuncs.getResponseUrl(vendor,
                                                  responseURL, MultispeakDefines.NOT_Server_STR);
        if (!porterConnection.isValid()) {
            String message = "Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.";
            log.error(message);
            throw new MultispeakWebServiceException(message);
        }

        if (formattedBlockTemplateID != null && formattedBlockTemplateID.getObjectGUID() != null) {
            blockTemplateName = formattedBlockTemplateID.getObjectGUID();

            if (!readingTypesMap.containsKey(blockTemplateName)) {

                if (formattedBlockTemplateID.getPrimaryIdentifier() != null) {
                    blockTemplateName = formattedBlockTemplateID.getPrimaryIdentifier().getValue();
                } else {
                    String errorMessage = "formattedBlockTemplate value is not present in request";
                    log.error(errorMessage);
                    throw new MultispeakWebServiceException(errorMessage);
                }
            }
        } else {
            String errorMessage = "formattedBlockTemplateID is not present in request";
            log.error(errorMessage);
            throw new MultispeakWebServiceException(errorMessage);
        }

        FormattedBlockProcessingService<Block> formattedBlockServ = mspValidationService.getProcessingServiceByReadingType(readingTypesMap,
                                                                                         blockTemplateName);

        List<ErrorObject> errorObjects = multispeakMeterService.blockMeterReadEvent(vendor,
                                                               meterIDs, formattedBlockServ,
                                                               transactionID, actualResponseUrl);

        multispeakFuncs.logErrorObjects(MultispeakDefines.MR_Server_STR,
                                        "InitiateMeterReadingsByReadingTypeCodes",
                                        errorObjects);
        return errorObjects;
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
        } catch (MultispeakWebServiceException e) {
            isAmrMeter = false;
        }
        log.debug("IsAMRMeter " + isAmrMeter + " for " + meterNo + ".");
        return isAmrMeter;
    }

    @Override
    public List<ErrorObject> initiateEndDeviceEventMonitoring(List<MeterID> meterNos) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("InitiateEndDeviceEventMonitoring", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.initiateEndDeviceEventMonitoring(vendor, meterNos);
        return errorObject;
    }
    
    @Override
    public List<ErrorObject> cancelEndDeviceEventMonitoring(String meterID)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("CancelEndDeviceEventMonitoring", vendor.getCompanyName());
        
        List<ErrorObject> errorObject = new ArrayList<ErrorObject>();
        if (meterID != null) {
            errorObject = multispeakMeterService.cancelEndDeviceEventMonitoring(vendor, meterID);
        }
        return errorObject;
    }
    
    @Override
    public List<ErrorObject> setDisconnectedStatus(List<MeterID> meterIDs)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("SetDisconnectedStatus", vendor.getCompanyName());
        List<ErrorObject> errorObject = new ArrayList<ErrorObject>();
        if (meterIDs != null) {
            errorObject = multispeakMeterService.setDisconnectedStatus(vendor, meterIDs);
        }
        return errorObject;
    }

    @Override
    public List<ErrorObject> clearDisconnectedStatus(List<MeterID> meterIDs)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("ClearDisconnectedStatus", vendor.getCompanyName());
        List<ErrorObject> errorObject = new ArrayList<ErrorObject>();
        if (meterIDs != null) {
            errorObject = multispeakMeterService.clearDisconnectedStatus(vendor, meterIDs);
        }
        return errorObject;
    }

    @Override
    public List<ErrorObject> deleteMeterGroups(List<ObjectID> objectIDs)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("DeleteMeterGroups", vendor.getCompanyName());

        List<ErrorObject> errorObject = new ArrayList<ErrorObject>();

        if (objectIDs != null) {

            List<String> groupIDs =
                objectIDs.stream().map(ObjectID -> ObjectID.getObjectGUID()).collect(Collectors.toList());

            for (String groupID : groupIDs) {
                ErrorObject errorObj = multispeakMeterService.deleteMeterGroups(groupID, vendor);
                if(errorObj != null) {
                    errorObject.add(errorObj);
                }
            }
        }
        return errorObject;
    }

    @Override
    public List<ErrorObject> removeMetersFromMeterGroup(List<MeterID> meterIDs, String meterGroupID)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("RemoveMetersFromMeterGroup",
                                                vendor.getCompanyName());

        List<ErrorObject> errorObject = multispeakMeterService.removeMetersFromGroup(meterGroupID,
                                                               meterIDs, vendor);
        return errorObject;
    }

    @Override
    public List<ErrorObject> createMeterGroups(List<MeterGroup> meterGroups)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("CreateMeterGroups", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.addMetersToGroup(meterGroups,
                                                              "CreateMeterGroups", vendor);
        return errorObject;
    }

    @Override
    public List<ErrorObject> insertMeterInMeterGroup(List<MeterID> meterID, String meterGroupID)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("InsertMeterInMeterGroup", vendor.getCompanyName());

        List<ErrorObject> errorObject = new ArrayList<ErrorObject>();
        if (meterID != null && meterGroupID != null) {
            MeterGroup meterGroup = new MeterGroup();
            MeterIDs meterIDs = new MeterIDs();
            List<MeterID> meterIDsList = meterIDs.getMeterID();
            
            for (MeterID id : meterID) {
                meterIDsList.add(id);
            }
            meterGroup.setMeterIDs(meterIDs);
            meterGroup.setGroupName(meterGroupID);
            List<MeterGroup> meterGroups = new ArrayList<MeterGroup>();
            meterGroups.add(meterGroup);

            errorObject = multispeakMeterService.addMetersToGroup(meterGroups,
                                                "InsertMeterInMeterGroup", vendor);
        }
        return errorObject;
    }
    
    @Override
    public List<ErrorObject> initiateDemandReset(List<MeterID> meterIDs,
            String responseURL, String transactionId, XMLGregorianCalendar expirationTime)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("InitiateDemandReset", vendor.getCompanyName());
        
        List<ErrorObject> errors = Lists.newArrayList();
        boolean hasFatalErrors = false;
        
        String actualResponseUrl = multispeakFuncs.getResponseUrl(vendor, responseURL, MultispeakDefines.NOT_Server_STR);
        
        // Do a basic URL check. This only validates that it's not empty.
        ErrorObject errorObject = mspValidationService.validateResponseURL(actualResponseUrl, "Meter", "InitiateDemandReset");
        if (errorObject != null) {
            errors.add(errorObject);
            hasFatalErrors = true;
        }

        Set<String> meterNumbers = meterIDs.stream().map(meterID -> meterID.getMeterName()).collect(Collectors.toSet());

        Map<String, PaoIdentifier> paoIdsByMeterNumber =
                paoDao.findPaoIdentifiersByMeterNumber(meterNumbers);
        Map<PaoIdentifier, String> meterNumbersByPaoId =
                HashBiMap.create(paoIdsByMeterNumber).inverse();
        Set<String> invalidMeterNumbers = Sets.difference(meterNumbers, paoIdsByMeterNumber.keySet());
        for (String invalidMeterNumber : invalidMeterNumbers) {
            errors.add(mspObjectDao.getNotFoundErrorObject(invalidMeterNumber, "MeterID", "Meter",
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
            errors.add(mspObjectDao.getErrorObject(meterNumber, errorMsg, "MeterID",
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
            new MRServerDemandResetCallback(mspObjectDao, multispeakEventLogService, vendor, meterNumbersByPaoId,
                actualResponseUrl, transactionId);
        
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
