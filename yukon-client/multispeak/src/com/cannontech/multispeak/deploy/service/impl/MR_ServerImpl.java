package com.cannontech.multispeak.deploy.service.impl;

import java.util.Arrays;
import java.util.Calendar;
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
import com.cannontech.multispeak.deploy.service.CustomersAffectedByOutage;
import com.cannontech.multispeak.deploy.service.DomainMember;
import com.cannontech.multispeak.deploy.service.DomainNameChange;
import com.cannontech.multispeak.deploy.service.EndDeviceShipment;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.FormattedBlock;
import com.cannontech.multispeak.deploy.service.FormattedBlockTemplate;
import com.cannontech.multispeak.deploy.service.HistoryLog;
import com.cannontech.multispeak.deploy.service.InHomeDisplay;
import com.cannontech.multispeak.deploy.service.InHomeDisplayExchange;
import com.cannontech.multispeak.deploy.service.MR_Server;
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
import com.cannontech.multispeak.deploy.service.ServiceLocation;
import com.cannontech.multispeak.service.MspValidationService;
import com.cannontech.multispeak.service.MultispeakMeterService;
import com.cannontech.user.UserUtils;
import com.cannontech.yukon.BasicServerConnection;
import com.google.common.base.Function;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class MR_ServerImpl implements MR_Server {

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
    @Autowired private ObjectFactory objectFactory;
    private BasicServerConnection porterConnection;
    private Map<String, FormattedBlockProcessingService<Block>> readingTypesMap;

    private final Logger log = YukonLogManager.getLogger(MR_ServerImpl.class);
    private final static String[] methods = new String[] { "pingURL", "getMethods", "initiateMeterReadByMeterNumber",
        "initiateMeterReadByMeterNoAndType", "initiateDemandReset", "isAMRMeter", "getReadingsByDate",
        "getAMRSupportedMeters", "getLatestReadingByMeterNo", "getLatestReadings", "getLatestReadingByMeterNoAndType",
        "getLatestReadingByType", "getReadingsByMeterNo", "getReadingsByDateAndType", "getReadingsByMeterNoAndType",
        "getSupportedReadingTypes", "meterAddNotification", "meterRemoveNotification", "meterChangedNotification",
        "initiateUsageMonitoring", "cancelUsageMonitoring", "initiateDisconnectedStatus", "cancelDisconnectedStatus",
        "serviceLocationChangedNotification", "deleteMeterGroup", "establishMeterGroup", "insertMeterInMeterGroup",
        "removeMetersFromMeterGroup" };

    private void init() throws MultispeakWebServiceException {
        multispeakFuncs.init();
    }

    @Override
    public ErrorObject[] pingURL() throws MultispeakWebServiceException {
        init();
        return new ErrorObject[0];
    }

    @Override
    public DomainMember[] getDomainMembers(String domainNaString) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @Override
    public String[] getDomainNames() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public String[] getMethods() throws MultispeakWebServiceException {
        init();
        return multispeakFuncs.getMethods(MultispeakDefines.MR_Server_STR, methods);
    }

    @Override
    public Meter[] getAMRSupportedMeters(String lastReceived) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getAMRSupportedMeters", vendor.getCompanyName());

        MspMeterReturnList meterList = null;
        Date timerStart = new Date();
        meterList = mspMeterDao.getAMRSupportedMeters(lastReceived, vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(meterList);

        Meter[] meters = new Meter[meterList.getMeters().size()];
        meterList.getMeters().toArray(meters);
        log.info("Returning " + meters.length + " AMR Supported Meters. ("
            + (new Date().getTime() - timerStart.getTime()) * .001 + " secs)");
        multispeakEventLogService.returnObjects(meters.length, meterList.getObjectsRemaining(), "Meter",
            meterList.getLastSent(), "getAMRSupportedMeters", vendor.getCompanyName());

        return meters;
    }

    @Override
    public boolean isAMRMeter(String meterNo) throws MultispeakWebServiceException {
        init();
        // Commenting out for now, not sure if we want this logged or not, it
        // could be a lot...and doesn't
        // have much impact to the system
        // MultispeakVendor vendor =
        // multispeakFuncs.getMultispeakVendorFromHeader();
        // multispeakEventLogService.methodInvoked("isAMRMeter",
        // vendor.getCompanyName());

        boolean isAmrMeter = false;
        try {
            mspValidationService.isYukonMeterNumber(meterNo);
            isAmrMeter = true;
        } catch (MultispeakWebServiceException e) {
            isAmrMeter = false;
        }
        log.debug("isAMRMeter " + isAmrMeter + " for " + meterNo + ".");
        return isAmrMeter;
    }

    @Override
    public MeterRead[] getReadingsByDate(Calendar startDate, Calendar endDate,
            String lastReceived) throws MultispeakWebServiceException {
        init();

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getReadingsByDate", vendor.getCompanyName());

        MspMeterReadReturnList mspMeterReadReturnList =
            mspRawPointHistoryDao.retrieveMeterReads(ReadBy.NONE, null, startDate.getTime(), endDate.getTime(),
                lastReceived, vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(mspMeterReadReturnList);

        MeterRead[] meterReadArray = new MeterRead[mspMeterReadReturnList.getMeterReads().size()];
        mspMeterReadReturnList.getMeterReads().toArray(meterReadArray);
        log.info("Returning " + meterReadArray.length + " Readings By Date.");
        multispeakEventLogService.returnObjects(meterReadArray.length, mspMeterReadReturnList.getObjectsRemaining(),
            "MeterRead", mspMeterReadReturnList.getLastSent(), "getReadingsByDate", vendor.getCompanyName());

        return meterReadArray;
    }

    @Override
    public MeterRead[] getReadingsByMeterNo(String meterNo, Calendar startDate,
            Calendar endDate) throws MultispeakWebServiceException {
        init(); // init is already performed on the call to isAMRMeter()

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getReadingsByMeterNo", vendor.getCompanyName());

        // Validate the meterNo is a Yukon meterNumber
        mspValidationService.isYukonMeterNumber(meterNo);

        MspMeterReadReturnList mspMeterReadReturnList =
            mspRawPointHistoryDao.retrieveMeterReads(ReadBy.METER_NUMBER, meterNo, startDate.getTime(),
                endDate.getTime(), null, vendor.getMaxReturnRecords());

        // There is only one MeterNo in the response, so does it make sense to
        // update the header with
        // lastSent?
        // updateResponseHeader(mspMeterRead);

        MeterRead[] meterReadArray = new MeterRead[mspMeterReadReturnList.getMeterReads().size()];
        mspMeterReadReturnList.getMeterReads().toArray(meterReadArray);
        log.info("Returning " + meterReadArray.length + " Readings By MeterNo.");
        multispeakEventLogService.returnObjects(meterReadArray.length, mspMeterReadReturnList.getObjectsRemaining(),
            "MeterRead", mspMeterReadReturnList.getLastSent(), "getReadingsByMeterNo", vendor.getCompanyName());
        return meterReadArray;
    }

    @Override
    public MeterRead getLatestReadingByMeterNo(String meterNo) throws MultispeakWebServiceException {
        init(); // init is already performed on the call to isAMRMeter()

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getLatestReadingByMeterNo", vendor.getCompanyName());

        // Validate the meterNo is a Yukon meterNumber
        YukonMeter meter = mspValidationService.isYukonMeterNumber(meterNo);

        boolean canInitiatePorterRequest =
            paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.PORTER_COMMAND_REQUESTS);

        // Custom hack put in only for SEDC. Performs an actual meter read
        // instead of simply replying from the
        // database.
        if (vendor.getCompanyName().equalsIgnoreCase("SEDC") && canInitiatePorterRequest) {

            // Don't know the responseURL as it's not provided in this method
            // (by definition!) Using default
            // for SEDC.
            String responseUrl = multispeakFuncs.getResponseUrl(vendor, null, MultispeakDefines.CB_Server_STR);
            MeterRead meterRead = multispeakMeterService.getLatestReadingInterrogate(vendor, meter, responseUrl);
            multispeakEventLogService.returnObject("MeterRead", meterNo, "getLatestReadingByMeterNo",
                vendor.getCompanyName());
            return meterRead;
        } else { // THIS SHOULD BE WHERE EVERYONE ELSE GOES!!!
            try {
                MeterRead meterRead = meterReadProcessingService.createMeterRead(meter);

                EnumSet<BuiltInAttribute> attributesToLoad =
                    EnumSet.of(BuiltInAttribute.USAGE, BuiltInAttribute.PEAK_DEMAND);

                for (BuiltInAttribute attribute : attributesToLoad) {
                    try {
                        LitePoint litePoint = attributeService.getPointForAttribute(meter, attribute);
                        PointValueQualityHolder pointValueQualityHolder =
                            dynamicDataSource.getPointValue(litePoint.getPointID());
                        if (pointValueQualityHolder != null
                            && pointValueQualityHolder.getPointQuality() != PointQuality.Uninitialized) {
                            meterReadProcessingService.updateMeterRead(meterRead, attribute, pointValueQualityHolder);
                        }
                    } catch (IllegalUseOfAttribute e) {
                        // it's okay...just skip
                    }
                }
                multispeakEventLogService.returnObject("MeterRead", meterNo, "getLatestReadingByMeterNo",
                    vendor.getCompanyName());
                return meterRead;
            } catch (DynamicDataAccessException e) {
                String message = "Connection to dispatch is invalid";
                log.error(message);
                throw new MultispeakWebServiceException(message);
            }
        }
    }

    @Override
    public FormattedBlock[] getReadingsByBillingCycle(String billingCycle, Calendar billingDate, int kWhLookBack,
            int kWLookBack, int kWLookForward, String lastReceived, String formattedBlockTemplateName,
            String[] fieldName) throws MultispeakWebServiceException {
        /*
         * TODO init(); MultispeakVendor vendor =
         * multispeakFuncs.getMultispeakVendorFromHeader(); MeterRead[]
         * meterReads =
         * multispeakFuncs.getMspRawPointHistoryDao().retrieveMeterReads
         * (ReadBy.BILL_GROUP, billingCycle, startDate.getTime(),
         * endDate.getTime(), lastReceived); //TODO = need to get the true
         * number of meters remaining int numRemaining = (meterReads.length <
         * MultispeakDefines.MAX_RETURN_RECORDS ? 0:1); //at least one item
         * remaining, bad assumption.
         * multispeakFuncs.getResponseHeader().setObjectsRemaining(new
         * BigInteger(String.valueOf(numRemaining))); return meterReads;
         */
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] initiateUsageMonitoring(String[] meterNos) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("initiateUsageMonitoring", vendor.getCompanyName());
        ErrorObject[] errorObject = multispeakMeterService.initiateUsageMonitoring(vendor, meterNos);
        return errorObject;
    }

    @Override
    public ErrorObject[] cancelUsageMonitoring(String[] meterNos) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("cancelUsageMonitoring", vendor.getCompanyName());
        ErrorObject[] errorObject = multispeakMeterService.cancelUsageMonitoring(vendor, meterNos);
        return errorObject;
    }

    @Override
    public ErrorObject[] initiateDisconnectedStatus(String[] meterNos) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("initiateDisconnectedStatus", vendor.getCompanyName());
        ErrorObject[] errorObject = multispeakMeterService.initiateDisconnectedStatus(vendor, meterNos);
        return errorObject;
    }

    @Override
    public ErrorObject[] cancelDisconnectedStatus(String[] meterNos) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("cancelDisconnectedStatus", vendor.getCompanyName());
        ErrorObject[] errorObject = multispeakMeterService.cancelDisconnectedStatus(vendor, meterNos);
        return errorObject;
    }

    // Perform an actual read of the meter and return a CB_MR
    // readingChangedNotification message for each
    // meterNo
    @Override
    public ErrorObject[] initiateMeterReadByMeterNumber(String[] meterNos, String responseURL, String transactionID,
            Float expirationTime) throws MultispeakWebServiceException {
        init();
        ErrorObject[] errorObjects = new ErrorObject[0];

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("initiateMeterReadByMeterNumber", vendor.getCompanyName());

        String actualResponseUrl = multispeakFuncs.getResponseUrl(vendor, responseURL, MultispeakDefines.CB_Server_STR);

        if (!porterConnection.isValid()) {
            String message =
                "Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.";
            log.error(message);
            throw new MultispeakWebServiceException(message);
        }

        errorObjects = multispeakMeterService.meterReadEvent(vendor, meterNos, transactionID, actualResponseUrl);

        multispeakFuncs.logErrorObjects(MultispeakDefines.MR_Server_STR, "initiateMeterReadByMeterNumberRequest",
            errorObjects);
        return errorObjects;
    }

    @Override
    public ErrorObject[] serviceLocationChangedNotification(ServiceLocation[] changedServiceLocations)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("serviceLocationChangedNotification", vendor.getCompanyName());
        ErrorObject[] errorObject = multispeakMeterService.serviceLocationChanged(vendor, changedServiceLocations);
        return errorObject;
    }

    @Override
    public ErrorObject[] meterChangedNotification(Meter[] changedMeters) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("meterChangedNotification", vendor.getCompanyName());
        ErrorObject[] errorObject = multispeakMeterService.meterChanged(vendor, changedMeters);
        return errorObject;
    }

    @Override
    public ErrorObject[] meterRemoveNotification(Meter[] removedMeters) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("meterRemoveNotification", vendor.getCompanyName());
        ErrorObject[] errorObject = multispeakMeterService.meterRemove(vendor, removedMeters);
        return errorObject;
    }

    @Override
    public ErrorObject[] meterAddNotification(Meter[] addedMeters) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("meterAddNotification", vendor.getCompanyName());
        ErrorObject[] errorObject = multispeakMeterService.meterAdd(vendor, addedMeters);
        return errorObject;
    }

    @Override
    public ErrorObject deleteMeterGroup(String meterGroupID) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("deleteMeterGroup", vendor.getCompanyName());
        return multispeakMeterService.deleteGroup(meterGroupID, vendor);
    }

    @Override
    public ErrorObject[] establishMeterGroup(MeterGroup meterGroup) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("establishMeterGroup", vendor.getCompanyName());
        ErrorObject[] errorObject = multispeakMeterService.addMetersToGroup(meterGroup, "establishMeterGroup", vendor);
        return errorObject;
    }

    @Override
    public FormattedBlock getLatestMeterReadingsByMeterGroup(String meterGroupID, String formattedBlockTemplateName,
            String[] fieldName) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] initiateGroupMeterRead(String meterGroupName, String responseURL, String transactionID,
            float expirationTime) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] insertMeterInMeterGroup(String[] meterNumbers, String meterGroupID)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("insertMeterInMeterGroup", vendor.getCompanyName());
        MeterGroup meterGroup = objectFactory.createMeterGroup();
        MeterList meterList = objectFactory.createMeterList();
        List<String> meterID = meterList.getMeterID();
        for (String meterNumber : meterNumbers) {
            meterID.add(meterNumber);
        }
        meterGroup.setMeterList(meterList);
        meterGroup.setGroupName(meterGroupID);
        ErrorObject[] errorObject =
            multispeakMeterService.addMetersToGroup(meterGroup, "insertMeterInMeterGroup", vendor);
        return errorObject;
    }

    @Override
    public ErrorObject[] meterExchangeNotification(MeterExchange[] meterChangeout) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] meterRetireNotification(Meter[] retiredMeters) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] removeMetersFromMeterGroup(String[] meterNumbers, String meterGroupID)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("removeMetersFromMeterGroup", vendor.getCompanyName());

        ErrorObject[] errorObject = multispeakMeterService.removeMetersFromGroup(meterGroupID, meterNumbers, vendor);
        return errorObject;
    }

    @Override
    public ErrorObject[] scheduleGroupMeterRead(String meterGroupName, Calendar timeToRead, String responseURL,
            String transactionID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public FormattedBlock[] getReadingByMeterNumberFormattedBlock(String meterNumber, Calendar billingDate,
            int kWhLookBack, int kWLookBack, int kWLookForward, String lastReceived, String formattedBlockTemplateName,
            String[] fieldName) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public FormattedBlock[] getReadingsByDateFormattedBlock(Calendar billingDate, int kWhLookBack, int kWLookBack,
            int kWLookForward, String lastReceived, String formattedBlockTemplateName, String[] fieldName)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] inHomeDisplayAddNotification(InHomeDisplay[] addedIHDs) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] inHomeDisplayChangedNotification(InHomeDisplay[] changedIHDs)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] inHomeDisplayExchangeNotification(InHomeDisplayExchange[] IHDChangeout)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] inHomeDisplayRemoveNotification(InHomeDisplay[] removedIHDs)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] inHomeDisplayRetireNotification(InHomeDisplay[] retiredIHDs)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] updateServiceLocationDisplays(String servLocID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] customersAffectedByOutageNotification(CustomersAffectedByOutage[] newOutages)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] endDeviceShipmentNotification(EndDeviceShipment shipment) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public MeterRead[] getLatestReadings(String lastReceived) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getLatestReadings", vendor.getCompanyName());

        MspMeterReadReturnList mspMeterReadReturnList =
            mspRawPointHistoryDao.retrieveLatestMeterReads(ReadBy.NONE, null, lastReceived,
                vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(mspMeterReadReturnList);

        MeterRead[] meterReadArray = new MeterRead[mspMeterReadReturnList.getMeterReads().size()];
        mspMeterReadReturnList.getMeterReads().toArray(meterReadArray);

        log.info("Returning " + meterReadArray.length + " latest Readings.");
        multispeakEventLogService.returnObjects(meterReadArray.length, mspMeterReadReturnList.getObjectsRemaining(),
            "MeterRead", mspMeterReadReturnList.getLastSent(), "getLatestReadings", vendor.getCompanyName());

        return meterReadArray;
    }

    @Override
    public FormattedBlock getLatestReadingByMeterNoAndType(String meterNo, String readingType,
            String formattedBlockTemplateName, String[] fieldName) throws MultispeakWebServiceException {
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
                    PointValueQualityHolder pointValueQualityHolder =
                        dynamicDataSource.getPointValue(litePoint.getPointID());
                    if (pointValueQualityHolder != null
                        && pointValueQualityHolder.getPointQuality() != PointQuality.Uninitialized) {
                        formattedBlockProcessingService.updateFormattedBlock(block, attribute, pointValueQualityHolder);
                    }
                } catch (IllegalUseOfAttribute e) {
                    // it's okay...just skip
                }
            }
            FormattedBlock formattedBlock = formattedBlockProcessingService.createMspFormattedBlock(block);
            multispeakEventLogService.returnObject("FormattedBlock", meterNo, "getLatestReadingByMeterNoAndType",
                vendor.getCompanyName());
            return formattedBlock;

        } catch (DynamicDataAccessException e) {
            String message = "Connection to dispatch is invalid";
            log.error(message);
            throw new MultispeakWebServiceException(message);
        }
    }

    @Override
    public FormattedBlock[] getLatestReadingByType(String readingType, String lastReceived,
            String formattedBlockTemplateName, String[] fieldName) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getLatestReadingByType", vendor.getCompanyName());

        FormattedBlockProcessingService<Block> formattedBlockProcessingService =
            mspValidationService.getProcessingServiceByReadingType(readingTypesMap, readingType);

        MspBlockReturnList mspBlockReturnList =
            mspRawPointHistoryDao.retrieveLatestBlock(formattedBlockProcessingService, lastReceived,
                vendor.getMaxReturnRecords());
        multispeakFuncs.updateResponseHeader(mspBlockReturnList);

        FormattedBlock formattedBlock =
            formattedBlockProcessingService.createMspFormattedBlock(mspBlockReturnList.getBlocks());
        FormattedBlock[] formattedBlocks = new FormattedBlock[] { formattedBlock };

        multispeakEventLogService.returnObjects(formattedBlocks.length, mspBlockReturnList.getObjectsRemaining(),
            "FormattedBlock", mspBlockReturnList.getLastSent(), "getLatestReadingByType", vendor.getCompanyName());

        return formattedBlocks;
    }

    @Override
    public FormattedBlock[] getReadingsByDateAndType(Calendar startDate, Calendar endDate, String readingType,
            String lastReceived, String formattedBlockTemplateName, String[] fieldName)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getReadingsByDateAndType", vendor.getCompanyName());

        FormattedBlockProcessingService<Block> formattedBlockProcessingService =
            mspValidationService.getProcessingServiceByReadingType(readingTypesMap, readingType);

        MspBlockReturnList mspBlockReturnList =
            mspRawPointHistoryDao.retrieveBlock(ReadBy.NONE, null, formattedBlockProcessingService,
                startDate.getTime(), endDate.getTime(), lastReceived, vendor.getMaxReturnRecords());
        multispeakFuncs.updateResponseHeader(mspBlockReturnList);

        FormattedBlock formattedBlock =
            formattedBlockProcessingService.createMspFormattedBlock(mspBlockReturnList.getBlocks());
        FormattedBlock[] formattedBlocks = new FormattedBlock[] { formattedBlock };

        multispeakEventLogService.returnObjects(formattedBlocks.length, mspBlockReturnList.getObjectsRemaining(),
            "FormattedBlock", mspBlockReturnList.getLastSent(), "getReadingsByDateAndType", vendor.getCompanyName());

        return formattedBlocks;
    }

    @Override
    public FormattedBlock[] getReadingsByMeterNoAndType(String meterNo, Calendar startDate, Calendar endDate,
            String readingType, String lastReceived, String formattedBlockTemplateName, String[] fieldName)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getReadingsByMeterNoAndType", vendor.getCompanyName());

        // Validate the meterNo is in Yukon
        mspValidationService.isYukonMeterNumber(meterNo);

        FormattedBlockProcessingService<Block> formattedBlockProcessingService =
            mspValidationService.getProcessingServiceByReadingType(readingTypesMap, readingType);

        MspBlockReturnList mspBlockReturnList =
            mspRawPointHistoryDao.retrieveBlock(ReadBy.METER_NUMBER, meterNo, formattedBlockProcessingService,
                startDate.getTime(), endDate.getTime(), null, // don't use lastReceived, we
                                                              // know there is only
                                                              // one
                vendor.getMaxReturnRecords());

        // There is only one MeterNo in the response, so does it make sense to
        // update the header with
        // lastSent?
        // updateResponseHeader(mspBlockReturnList);

        FormattedBlock formattedBlock =
            formattedBlockProcessingService.createMspFormattedBlock(mspBlockReturnList.getBlocks());
        FormattedBlock[] formattedBlocks = new FormattedBlock[] { formattedBlock };

        multispeakEventLogService.returnObjects(formattedBlocks.length, mspBlockReturnList.getObjectsRemaining(),
            "FormattedBlock", mspBlockReturnList.getLastSent(), "getReadingsByMeterNoAndType", vendor.getCompanyName());

        return formattedBlocks;
    }

    @Override
    public String[] getSupportedReadingTypes() throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getSupportedReadingTypes", vendor.getCompanyName());

        Set<String> keys = readingTypesMap.keySet();
        String[] types = new String[keys.size()];
        keys.toArray(types);

        return types;
    }

    @Override
    public ErrorObject[] initiateMeterReadByMeterNoAndType(String meterNo, String responseURL, String readingType,
            String transactionID, Float expirationTime) throws MultispeakWebServiceException {
        init();
        ErrorObject[] errorObjects = new ErrorObject[0];

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("initiateMeterReadByMeterNoAndType", vendor.getCompanyName());

        String actualResponseUrl = multispeakFuncs.getResponseUrl(vendor, responseURL, MultispeakDefines.CB_Server_STR);
        if (!porterConnection.isValid()) {
            String message =
                "Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.";
            log.error(message);
            throw new MultispeakWebServiceException(message);
        }
        FormattedBlockProcessingService<Block> formattedBlockServ =
            mspValidationService.getProcessingServiceByReadingType(readingTypesMap, readingType);

        errorObjects =
            multispeakMeterService.blockMeterReadEvent(vendor, meterNo, formattedBlockServ, transactionID,
                actualResponseUrl);
        multispeakFuncs.logErrorObjects(MultispeakDefines.MR_Server_STR, "initiateMeterReadByMeterNoAndTypeRequest",
            errorObjects);
        return errorObjects;
    }

    @Override
    public MeterRead[] getReadingsByUOMAndDate(String uomData, Calendar startDate, Calendar endDate, String lastReceived)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] initiateMeterReadByObject(String objectName, String nounType, PhaseCd phaseCode,
            String responseURL, String transactionID, float expirationTime) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] meterConnectivityNotification(MeterConnectivity[] newConnectivity)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] meterBaseExchangeNotification(MeterBaseExchange[] MBChangeout)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public String requestRegistrationID() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] registerForService(RegistrationInfo registrationDetails) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] unregisterForService(String registrationID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public RegistrationInfo getRegistrationInfoByID(String registrationID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public String[] getPublishMethods() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] domainMembersChangedNotification(DomainMember[] changedDomainMembers)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] domainNamesChangedNotification(DomainNameChange[] changedDomainNames)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public Schedule[] getSchedules(String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public Schedule getScheduleByID(String scheduleID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ReadingSchedule[] getReadingSchedules(String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ReadingSchedule getReadingScheduleByID(String readingScheduleID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public FormattedBlock[] getLatestReadingsByMeterNoList(String[] meterNo, Calendar startDate, Calendar endDate,
            String readingType, String lastReceived, ServiceType serviceType, String formattedBlockTemplateName,
            String[] fieldName) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public FormattedBlockTemplate[] getFormattedBlockTemplates(String lastReceived)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public Meter[] getModifiedAMRMeters(String previousSessionID, String lastReceived)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public FormattedBlock[] getLatestReadingsByMeterNoListFormattedBlock(String[] meterNo, Calendar startDate,
            Calendar endDate, String formattedBlockTemplateName, String[] fieldName, String lastReceived,
            ServiceType serviceType) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] initiateDemandReset(MeterIdentifier[] meterIDs, String responseURL, String transactionId,
            Float expirationTime) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("initiateDemandReset", vendor.getCompanyName());

        List<ErrorObject> errors = Lists.newArrayList();
        boolean hasFatalErrors = false;

        String actualResponseUrl = multispeakFuncs.getResponseUrl(vendor, responseURL, MultispeakDefines.CB_Server_STR);

        // Do a basic URL check. This only validates that it's not empty.
        ErrorObject errorObject =
            mspValidationService.validateResponseURL(actualResponseUrl, "Meter", "InitiateDemandReset");
        if (errorObject != null) {
            errors.add(errorObject);
            hasFatalErrors = true;
        }

        List<MeterIdentifier> meterIdentifierList = Arrays.asList(meterIDs);
        Function<MeterIdentifier, String> meterNumberFromIdentifier = new Function<MeterIdentifier, String>() {
            @Override
            public String apply(MeterIdentifier meterIdentifier) {
                return meterIdentifier.getMeterNo();
            }
        };
        Set<String> meterNumbers = Sets.newHashSet(Lists.transform(meterIdentifierList, meterNumberFromIdentifier));

        Map<String, PaoIdentifier> paoIdsByMeterNumber = paoDao.findPaoIdentifiersByMeterNumber(meterNumbers);
        Map<PaoIdentifier, String> meterNumbersByPaoId = HashBiMap.create(paoIdsByMeterNumber).inverse();
        Set<String> invalidMeterNumbers = Sets.difference(meterNumbers, paoIdsByMeterNumber.keySet());
        for (String invalidMeterNumber : invalidMeterNumbers) {
            errors.add(mspObjectDao.getNotFoundErrorObject(invalidMeterNumber, "MeterNumber", "Meter",
                "initiateDemandReset", vendor.getCompanyName()));
        }

        Set<PaoIdentifier> meterIdentifiers = Sets.newHashSet(paoIdsByMeterNumber.values());
        Set<PaoIdentifier> validMeters = Sets.newHashSet(demandResetService.filterDevices(meterIdentifiers));
        Set<PaoIdentifier> unsupportedMeters = Sets.difference(meterIdentifiers, validMeters);
        for (PaoIdentifier unsupportedMeter : unsupportedMeters) {
            String errorMsg = unsupportedMeter.getPaoIdentifier().getPaoType() + " does not support demand reset";
            String meterNumber = meterNumbersByPaoId.get(unsupportedMeter);
            errors.add(mspObjectDao.getErrorObject(meterNumber, errorMsg, "Meter", "initiateDemandReset",
                vendor.getCompanyName()));
        }

        if (hasFatalErrors || validMeters.isEmpty()) {
            return errors.toArray(new ErrorObject[errors.size()]);
        }

        log.info("Received " + meterIDs.length + " Meter(s) for Demand Reset from " + vendor.getCompanyName());
        multispeakEventLogService.initiateDemandResetRequest(meterNumbers.size(), meterNumbersByPaoId.size(),
            invalidMeterNumbers.size(), unsupportedMeters.size(), "initiateConnectDisconnect", vendor.getCompanyName());

        MRServerDemandResetCallback callback =
            new MRServerDemandResetCallback(mspObjectDao, multispeakEventLogService, vendor, meterNumbersByPaoId,
                responseURL, transactionId);

        demandResetService.sendDemandResetAndVerify(validMeters, callback, UserUtils.getYukonUser());
        errors.addAll(callback.getErrors());
        return errors.toArray(new ErrorObject[errors.size()]);
    }

    @Override
    public ErrorObject[] insertMetersInConfigurationGroup(String[] meterNumbers, String meterGroupID,
            ServiceType serviceType) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] removeMetersFromConfigurationGroup(String[] meterNumbers, String meterGroupID,
            ServiceType serviceType) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] establishSchedules(Schedule[] schedules) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] deleteSchedule(String scheduleID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] establishReadingSchedules(ReadingSchedule[] readingSchedules)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] enableReadingSchedule(String readingScheduleID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] disableReadingSchedule(String readingScheduleID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] deleteReadingSchedule(String readingoScheduleID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] initiateMeterReadsByFieldName(String[] meterNumbers, String[] fieldNames, String responseURL,
            String transactionID, float expirationTime, String formattedBlockTemplateName)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] meterBaseChangedNotification(MeterBase[] changedMBs) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] meterBaseRemoveNotification(MeterBase[] removedMBs) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] meterBaseRetireNotification(MeterBase[] retiredMBs) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] meterBaseAddNotification(MeterBase[] addedMBs) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Required
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }

    @Required
    public void setReadingTypesMap(Map<String, FormattedBlockProcessingService<Block>> readingTypesMap) {
        this.readingTypesMap = readingTypesMap;
    }

    @Override
    public ErrorObject[] customerChangedNotification(Customer[] changedCustomers) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] cancelPlannedOutage(String[] meterNos) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public HistoryLog[] getHistoryLogByMeterNo(String meterNo, Calendar startDate, Calendar endDate)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public HistoryLog[] getHistoryLogsByDate(Calendar startDate, Calendar endDate, String lastReceived)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public HistoryLog[] getHistoryLogsByMeterNoAndEventCode(String meterNo, EventCode eventCode, Calendar startDate,
            Calendar endDate) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public HistoryLog[] getHistoryLogsByDateAndEventCode(EventCode eventCode, Calendar startDate, Calendar endDate,
            String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] initiatePlannedOutage(String[] meterNos, Calendar startDate, Calendar endDate)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
}
