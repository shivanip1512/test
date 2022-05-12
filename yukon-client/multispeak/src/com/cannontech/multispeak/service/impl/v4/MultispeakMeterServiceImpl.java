package com.cannontech.multispeak.service.impl.v4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadCallback;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.point.stategroup.OutageStatus;
import com.cannontech.database.db.point.stategroup.PointStateHelper;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.msp.beans.v4.ArrayOfOutageDetectionEvent;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.MeterReading;
import com.cannontech.msp.beans.v4.ODEventNotification;
import com.cannontech.msp.beans.v4.ODEventNotificationResponse;
import com.cannontech.msp.beans.v4.ObjectFactory;
import com.cannontech.msp.beans.v4.OutageDetectDeviceType;
import com.cannontech.msp.beans.v4.OutageDetectionEvent;
import com.cannontech.msp.beans.v4.OutageEventType;
import com.cannontech.msp.beans.v4.OutageLocation;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.v4.OAClient;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.dao.v4.MspObjectDao;
import com.cannontech.multispeak.event.v4.MeterReadEvent;
import com.cannontech.multispeak.event.v4.MultispeakEvent;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.MultispeakMeterServiceBase;
import com.cannontech.multispeak.service.v4.MultispeakMeterService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.BasicServerConnection;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class MultispeakMeterServiceImpl extends MultispeakMeterServiceBase implements MultispeakMeterService,MessageListener {

    private static final Logger log = YukonLogManager.getLogger(MultispeakMeterServiceImpl.class);

    private BasicServerConnection porterConnection;

    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private ObjectFactory objectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private DeviceUpdateService deviceUpdateService;
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    @Autowired private CommandExecutionService commandRequestService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private AttributeService attributeService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private OAClient oaClient;

    /** Singleton incrementor for messageIDs to send to porter connection */
    private static long messageID = 1;

    /** A map of Long(userMessageID) to MultispeakEvent values */
    private static Map<Long, MultispeakEvent> eventsMap = Collections.synchronizedMap(new HashMap<Long, MultispeakEvent>());
    
    private ImmutableSetMultimap<OutageEventType, Integer> outageConfig;
    
    private ImmutableSet<OutageEventType> supportedEventTypes;
    
    @PostConstruct
    public void initialize() throws Exception {
        log.info("New MSP instance created");
        porterConnection.addMessageListener(this);
    }
    /**
     * generate a unique messageId, don't let it be negative
     */
    private synchronized long generateMessageID() {
        if (++messageID == Long.MAX_VALUE) {
            messageID = 1;
        }
        return messageID;
    }

    /**
     * Returns true if event processes without timing out, false if event times
     * out.
     * 
     * @param event
     * @return
     */
    private boolean waitOnEvent(MultispeakEvent event, long timeout) {

        long millisTimeOut = 0; //
        while (!event.isPopulated() && millisTimeOut < timeout) // quit after
                                                                // timeout
        {
            try {
                Thread.sleep(10);
                millisTimeOut += 10;
            } catch (InterruptedException e) {
                log.error(e);
            }
        }
        if (millisTimeOut >= timeout) {// this broke the loop, more than likely,
                                       // have to kill it sometime
            return false;
        }
        return true;
    }

    private static Map<Long, MultispeakEvent> getEventsMap() {
        return eventsMap;
    }

    /**
     * This method still does not support attributes.
     * The issue is that SEDC does not support the ability to receive ReadingChangedNotification messages.
     * Therefore, just for them, we initiate a real time read, wait, and return.
     * This DOES need to be changed in future versions.
     */
    @Override
    public MeterReading getLatestReadingInterrogate(MultispeakVendor mspVendor, YukonMeter meter, String responseUrl) {
        long id = generateMessageID();
        MeterReadEvent event = new MeterReadEvent(mspVendor, id, meter, responseUrl);

        getEventsMap().put(new Long(id), event);

        String commandStr = "getvalue kwh";
        if (DeviceTypesFuncs.isMCT4XX(meter.getPaoIdentifier().getPaoType())) {
            commandStr = "getvalue peak"; // getvalue peak returns the peak kW and the total kWh
        }

        final String meterNumber = meter.getMeterNumber();
        log.info("Received " + meterNumber + " for LatestReadingInterrogate from " + mspVendor.getCompanyName());
        multispeakEventLogService.initiateMeterRead(meterNumber, meter, "N/A", "GetLatestReadingByMeterID",
                mspVendor.getCompanyName());

        // Writes a request to pil for the meter and commandStr using the id for mspVendor.
        commandStr += " update noqueue";
        Request pilRequest = new Request(meter.getPaoIdentifier().getPaoId(), commandStr, id);
        pilRequest.setPriority(13);
        porterConnection.write(pilRequest);

        systemLog("GetLatestReadingByMeterID",
                "(ID:" + meter.getPaoIdentifier().getPaoId() + ") MeterNumber (" + meterNumber + ") - " + commandStr, mspVendor);

        synchronized (event) {
            boolean timeout = !waitOnEvent(event, mspVendor.getRequestMessageTimeout());
            if (timeout) {
                mspObjectDao.logMSPActivity("GetLatestReadingByMeterID",
                        "MeterNumber (" + meterNumber + ") - Reading Timed out after " +
                                (mspVendor.getRequestMessageTimeout() / 1000) + " seconds.  No reading collected.",
                        mspVendor.getCompanyName());
            }
        }

        return event.getDevice().getMeterReading();
    }

    /**
     * Extra SystemLog logging that will be completely removed upon completion of MultiSpeak EventLogs.
     * Only use this method if you intend for the logging to be removed with EventLogs completion.
     */
    @Deprecated
    private void systemLog(String mspMethod, String description, MultispeakVendor mspVendor) {
        mspObjectDao.logMSPActivity(mspMethod, description, mspVendor.getCompanyName());
    }

    @Override
    public List<ErrorObject> initiateUsageMonitoring(MultispeakVendor mspVendor, List<MeterID> meterIDs) {

        List<String> mspMeters = meterIDs.stream().map(meterID -> meterID.getMeterNo()).collect(Collectors.toList());

        return addMetersToGroup(mspMeters, SystemGroupEnum.USAGE_MONITORING, "InitiateUsageMonitoring", mspVendor);
    }

    /**
     * Helper method to add meterNos to systemGroup
     */
    private List<ErrorObject> addMetersToGroup(List<String> meterNos, SystemGroupEnum systemGroup, String mspMethod,
            MultispeakVendor mspVendor) {
        return addToGroupAndDisable(meterNos, systemGroup, mspMethod, mspVendor, false);
    }

    /**
     * Helper method to add meterNos to systemGroup
     * 
     * @param disable - when true, the meter will be disabled. Else no change.
     */
    private List<ErrorObject> addToGroupAndDisable(List<String> meterNos, SystemGroupEnum systemGroup,
            String mspMethod, MultispeakVendor mspVendor, boolean disable) {

        ArrayList<ErrorObject> errorObjects = new ArrayList<>();

        for (String meterNumber : meterNos) {
            try {
                YukonMeter meter = meterDao.getForMeterNumber(meterNumber);
                if (disable && !meter.isDisabled()) {
                    deviceUpdateService.disableDevice(meter);
                }
                addMeterToGroup(meter, systemGroup, mspMethod, mspVendor);
            } catch (NotFoundException e) {
                multispeakEventLogService.meterNotFound(meterNumber, mspMethod, mspVendor.getCompanyName());
                ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "MeterID", "addToGroup",
                        mspVendor.getCompanyName(), e.getMessage());
                errorObjects.add(err);
                log.error(e);
            }
        }
        return errorObjects;
    }
    
    @Override
    public List<ErrorObject> cancelUsageMonitoring(MultispeakVendor mspVendor, List<MeterID> meterIDs) {
        
        List<String> mspMeters = meterIDs.stream().map(meterID -> meterID.getMeterNo()).collect(Collectors.toList());
        
        return removeMetersFromGroup(mspMeters, SystemGroupEnum.USAGE_MONITORING, "CancelUsageMonitoring", mspVendor);
    }
    
    /**
     * Helper method to remove meterNos from systemGroup
     */
    private List<ErrorObject> removeMetersFromGroup(List<String> meterNos, SystemGroupEnum systemGroup, String mspMethod,
            MultispeakVendor mspVendor) {
        return removeMetersFromGroupAndEnable(meterNos, systemGroup, mspMethod, mspVendor, false);
    }
    
    /**
     * Helper method to remove meterNos from systemGroup
     * @param disable - when true, the meter will be enabled. Else no change.
     */
    private List<ErrorObject> removeMetersFromGroupAndEnable(List<String> meterNos, SystemGroupEnum systemGroup, String mspMethod,
            MultispeakVendor mspVendor, boolean enable) {

        ArrayList<ErrorObject> errorObjects = new ArrayList<>();

        for (String meterNumber : meterNos) {
            try {
                YukonMeter meter = meterDao.getForMeterNumber(meterNumber);
                if (enable && meter.isDisabled()) {
                    deviceDao.enableDevice(meter);
                }
                removeFromGroup(meter, systemGroup, mspMethod, mspVendor);
            } catch (NotFoundException e) {
                multispeakEventLogService.meterNotFound(meterNumber, mspMethod, mspVendor.getCompanyName());
                ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber,
                                                                      "MeterNumber",
                                                                      "MeterID",
                                                                      "removeFromGroup",
                                                                      mspVendor.getCompanyName());
                errorObjects.add(err);
                log.error(e);
            }
        }

        return errorObjects;
    }
    
    /**
     * Performs the PLC meter outage ping.
     * Returns immediately, does not wait for a response.
     * Callback will initiate a ODEventNotification on receivedLastResultString.
     */
    private void doPlcOutagePing(List<CommandRequestDevice> plcCommandRequests, final MultispeakVendor mspVendor,
            final String transactionId, final String responseUrl) {

        YukonUserContext yukonUserContext = YukonUserContext.system;
        CommandCompletionCallback<CommandRequestDevice> callback = new CommandCompletionCallback<CommandRequestDevice>() {
            @Override
            public void receivedLastResultString(CommandRequestDevice command, String value) {
                log.debug("receivedLastResultString for odEvent " + value);
                SimpleMeter yukonMeter = meterDao.getSimpleMeterForId(command.getDevice().getDeviceId());

                Date now = new Date(); // may need to get this from the porter Return Message, but for now "now" will do.
                OutageEventType outageEventType = getForStatusCode(0);
                OutageDetectionEvent outageDetectionEvent = buildOutageDetectionEvent(yukonMeter, outageEventType, now, value);

                sendODEventNotification(yukonMeter, mspVendor, transactionId, responseUrl, outageDetectionEvent);
            }

            @Override
            public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                log.warn("receivedLastError for odEvent " + error.getDescription());
                SimpleMeter yukonMeter = meterDao.getSimpleMeterForId(command.getDevice().getDeviceId());

                Date now = new Date(); // may need to get this from the porter Return Message, but for now "now" will do.
                OutageEventType outageEventType = getForStatusCode(error.getErrorCode());
                OutageDetectionEvent outageDetectionEvent = buildOutageDetectionEvent(yukonMeter, outageEventType, now,
                        error.getPorter());

                sendODEventNotification(yukonMeter, mspVendor, transactionId, responseUrl, outageDetectionEvent);
            }

            @Override
            public void complete() {
                log.debug("complete for odEvent");
            }

            @Override
            public void processingExceptionOccurred(String reason) {
                log.warn("processingExceptionOccurred for odEvent " + reason);
            }
        };

        if (CollectionUtils.isNotEmpty(plcCommandRequests)) {
            commandRequestService.execute(plcCommandRequests, callback,
                    DeviceRequestType.MULTISPEAK_OUTAGE_DETECTION_PING_COMMAND, yukonUserContext.getYukonUser());
        }
    }

    /**
     * Performs the RFN meter outage analysis.
     * If MSP_RFN_PING_FORCE_CHANNEL_READ setting is set to
     * true = perform a real time attribute read using Outage_Status attribute.
     * Callback will initiate a ODEventNotification on receivedLastValue or receivedError.
     * false = use the last known value of Outage_Status to determine odEvent state.
     * Returns immediately, does not wait for a response.
     */
    private void doRfnOutagePing(final List<YukonMeter> meters, final MultispeakVendor mspVendor, final String transactionId,
            final String responseUrl) {

        boolean isChannelReadForPing = globalSettingDao.getBoolean(GlobalSettingType.MSP_RFN_PING_FORCE_CHANNEL_READ);

        if (isChannelReadForPing) {
            DeviceAttributeReadCallback callback = new DeviceAttributeReadCallback() {

                @Override
                public void complete() {
                    log.debug("deviceAttributeReadCallback.complete for odEvent");
                }

                @Override
                public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
                    log.debug("deviceAttributeReadCallback.receivedLastValue for odEvent");
                    if (value != null) {
                        YukonMeter yukonMeter = meterDao.getForId(pao.getPaoId());
                        if (meters.contains(yukonMeter)) {
                            meters.remove(yukonMeter);
                            OutageEventType outageEventType = getForStatusCode(DeviceError.SUCCESS.getCode()); // assume if we got
                                                                                                               // one value, then
                                                                                                               // the meter must
                                                                                                               // be talking
                                                                                                               // successfully
                            OutageDetectionEvent outageDetectionEvent = buildOutageDetectionEvent(yukonMeter, outageEventType,
                                    value.getPointDataTimeStamp(), "");
                            sendODEventNotification(yukonMeter, mspVendor, transactionId, responseUrl, outageDetectionEvent);
                        }
                    }
                }

                @Override
                public void receivedLastValue(PaoIdentifier pao, String value) { // success - not guaranteed!!!!
                    log.debug("deviceAttributeReadCallback.receivedLastValue for odEvent");

                    YukonMeter yukonMeter = meterDao.getForId(pao.getPaoId()); // can we get this from meters?
                    if (meters.contains(yukonMeter)) {
                        meters.remove(yukonMeter);
                        Date now = new Date(); // may need to get this from the callback, but for now "now" will do.
                        OutageEventType outageEventType = getForStatusCode(DeviceError.TIMEOUT.getCode()); // unknown status if we
                                                                                                           // didn't hit
                                                                                                           // receivedValue at
                                                                                                           // least once
                        OutageDetectionEvent outageDetectionEvent = buildOutageDetectionEvent(yukonMeter, outageEventType, now,
                                "");
                        sendODEventNotification(yukonMeter, mspVendor, transactionId, responseUrl, outageDetectionEvent);
                    }
                }

                @Override
                public void receivedError(PaoIdentifier pao, SpecificDeviceErrorDescription error) { // failure
                    log.warn("deviceAttributeReadCallback.receivedError for odEvent: " + pao + ": " + error);

                    YukonMeter yukonMeter = meterDao.getForId(pao.getPaoId()); // can we get this from meters?
                    if (meters.contains(yukonMeter)) {
                        meters.remove(yukonMeter);
                        Date now = new Date(); // may need to get this from the callback, but for now "now" will do.
                        OutageEventType outageEventType = getForStatusCode(error.getErrorCode());
                        OutageDetectionEvent outageDetectionEvent = buildOutageDetectionEvent(yukonMeter, outageEventType, now,
                                error.toString());
                        sendODEventNotification(yukonMeter, mspVendor, transactionId, responseUrl, outageDetectionEvent);
                    }
                }

                @Override
                public void receivedException(SpecificDeviceErrorDescription error) {
                    log.warn("deviceAttributeReadCallback.receivedException in odEvent callback: " + error);
                    // TODO there is still a potential bug here, because meters is left populated with the pao, even though an
                    // exception
                    // has occurred. This means we can still get receivedLastValue and process it as a "success" instead of
                    // "unknown" or failure.
                }

            };
            if (CollectionUtils.isNotEmpty(meters)) {
                deviceAttributeReadService.initiateRead(meters, Sets.newHashSet(BuiltInAttribute.OUTAGE_STATUS), callback,
                        DeviceRequestType.MULTISPEAK_OUTAGE_DETECTION_PING_COMMAND, UserUtils.getYukonUser());
            }
        } else { // save network expense by just returning latest known value.

            BiMap<LitePoint, PaoIdentifier> pointsToPaos = attributeService.getPoints(meters, BuiltInAttribute.OUTAGE_STATUS)
                    .inverse();
            Set<Integer> pointIds = Sets.newHashSet(Iterables.transform(pointsToPaos.keySet(), LitePoint.ID_FUNCTION));
            Set<? extends PointValueQualityHolder> pointValues = asyncDynamicDataSource.getPointValues(pointIds);

            final ImmutableMap<Integer, LitePoint> pointLookup = Maps.uniqueIndex(pointsToPaos.keySet(), LitePoint.ID_FUNCTION);
            final ImmutableMap<PaoIdentifier, YukonMeter> meterLookup = PaoUtils.indexYukonPaos(meters);
            // need to send unkonwn or something if we don't have a point value.
            for (PointValueQualityHolder pointValue : pointValues) {
                Integer pointId = pointValue.getId();
                LitePoint litePoint = pointLookup.get(pointId);
                PaoIdentifier paoIdentifier = pointsToPaos.get(litePoint);
                YukonMeter yukonMeter = meterLookup.get(paoIdentifier);

                OutageStatus outageStatus = PointStateHelper.decodeRawState(OutageStatus.class, pointValue.getValue());
                OutageEventType outageEventType;

                switch (outageStatus) {
                case GOOD:
                    outageEventType = OutageEventType.RESTORATION;
                    break;
                case BAD:
                    outageEventType = OutageEventType.OUTAGE;
                    break;
                default:
                    outageEventType = OutageEventType.NO_RESPONSE;
                    break;
                }

                OutageDetectionEvent outageDetectionEvent = buildOutageDetectionEvent(yukonMeter, outageEventType,
                        pointValue.getPointDataTimeStamp(), "Last Known Status");
                sendODEventNotification(yukonMeter, mspVendor, transactionId, responseUrl, outageDetectionEvent);
            }
        }
    }

    private OutageEventType getForStatusCode(int statusCode) {
        for (OutageEventType eventType : supportedEventTypes) {
            if (outageConfig.get(eventType).contains(statusCode)) {
                return eventType;
            }
        }
        return OutageEventType.NO_RESPONSE;
    }

    private void sendODEventNotification(SimpleMeter meter, MultispeakVendor mspVendor, String transactionId,
            String responseUrl, OutageDetectionEvent outageDetectionEvent) {
        try {

            ODEventNotification odEventNotification = objectFactory.createODEventNotification();
            ArrayOfOutageDetectionEvent events = objectFactory.createArrayOfOutageDetectionEvent();
            events.getOutageDetectionEvent().add(outageDetectionEvent);
            odEventNotification.setODEvents(events);
            odEventNotification.setTransactionID(transactionId);

            log.info("Sending ODEventNotification (" + responseUrl + "): Meter Number " + meter.toString()
                    + " OutageEventType: " + outageDetectionEvent.getOutageEventType());
            // TODO - Do we want an EventLog when we send out notification messages?

            ODEventNotificationResponse odEventNotificationResponse = oaClient.odEventNotification(mspVendor, responseUrl,
                    odEventNotification);
            List<ErrorObject> errObjects = null;
            if (odEventNotificationResponse != null && odEventNotificationResponse.getODEventNotificationResult() != null) {
                List<ErrorObject> responseErrorObjects = odEventNotificationResponse.getODEventNotificationResult()
                        .getErrorObject();
                errObjects = responseErrorObjects;
            }

            multispeakEventLogService.notificationResponse("ODEventNotification", transactionId,
                    outageDetectionEvent.getObjectID(), outageDetectionEvent.getOutageEventType().toString(),
                    CollectionUtils.size(errObjects), responseUrl);
            if (CollectionUtils.isNotEmpty(errObjects)) {
                multispeakFuncs.logErrorObjects(responseUrl, "ODEventNotification", errObjects);
            }
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + responseUrl + " - initiateOutageDetection (" + mspVendor.getCompanyName()
                    + ")");
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
        }
    }

    private OutageDetectionEvent buildOutageDetectionEvent(SimpleMeter yukonMeter, OutageEventType outageEventType,
            Date timestamp, String resultString) {
        OutageDetectionEvent outageDetectionEvent = null;

        outageDetectionEvent = new OutageDetectionEvent();
        String meterNumber = yukonMeter.getMeterNumber();

        outageDetectionEvent.setEventTime(MultispeakFuncs.toXMLGregorianCalendar(timestamp));

        System.out.println("EventTime: " + timestamp.getTime());
        outageDetectionEvent.setObjectID(meterNumber);
        outageDetectionEvent.setOutageDetectionDeviceID(meterNumber);
        outageDetectionEvent.setOutageDetectionDeviceType(OutageDetectDeviceType.METER);

        OutageLocation outageLocation = new OutageLocation();
        outageLocation.setObjectID(meterNumber);
        MeterID meterID = new MeterID();
        meterID.setMeterNo(meterNumber);
        outageLocation.setMeterID(meterID);
        outageDetectionEvent.setOutageLocation(outageLocation);

        // set defaults, may be overwritten below
        outageDetectionEvent.setComments(resultString);
        outageDetectionEvent.setOutageEventType(outageEventType);
        outageDetectionEvent.setErrorString(outageEventType.value() + ": " + resultString);

        return outageDetectionEvent;
    }

    @Override
    public synchronized List<ErrorObject> odEvent(final MultispeakVendor mspVendor, List<MeterID> meterIDs,
            final String transactionId, final String responseUrl) throws MultispeakWebServiceException {

        List<String> meterNumbers = meterIDs.stream().map(meterID -> meterID.getMeterNo()).collect(Collectors.toList());

        if (StringUtils.isBlank(responseUrl)) { // no need to go through all the work if we have no one to respond to.
            throw new MultispeakWebServiceException("OMS vendor unknown.  Please contact Yukon administrator" +
                    " to set the Multispeak Vendor Role Property value in Yukon.");
        }

        if (!porterConnection.isValid()) {
            throw new MultispeakWebServiceException("Connection to 'Yukon Port Control Service' " +
                    "is not valid.  Please contact your Yukon Administrator.");
        }

        log.info("Received " + meterNumbers.size() + " Meter(s) for Outage Verification Testing from "
                + mspVendor.getCompanyName());
        multispeakEventLogService.initiateODEventRequest(meterNumbers.size(), "InitiateOutageDetectionEventRequest",
                mspVendor.getCompanyName());

        ArrayList<ErrorObject> errorObjects = new ArrayList<>();
        List<YukonMeter> rfnPaosToPing = Lists.newArrayList();
        List<CommandRequestDevice> plcCommandRequests = Lists.newArrayList();

        ListMultimap<String, YukonMeter> meterNumberToMeterMap = meterDao
                .getMetersMapForMeterNumbers(Lists.newArrayList(meterNumbers));
        boolean excludeDisabled = globalSettingDao.getBoolean(GlobalSettingType.MSP_EXCLUDE_DISABLED_METERS);

        for (String meterNumber : meterNumbers) {
            List<YukonMeter> meters = meterNumberToMeterMap.get(meterNumber); // this will most likely be size 1
            if (CollectionUtils.isEmpty(meters)) {
                ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "ODEvent",
                        mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.meterNotFound(meterNumber, "InitiateOutageDetectionEventRequest",
                        mspVendor.getCompanyName());
            }

            for (YukonMeter meter : meters) {
                if (excludeDisabled && meter.isDisabled()) {
                    log.debug("Meter " + meter.getMeterNumber() + " is disabled, skipping.");
                    continue;
                }
                // TODO validate is OD supported meter ???
                if (meter instanceof RfnMeter) {
                    rfnPaosToPing.add(meter);
                    multispeakEventLogService.initiateODEvent(meterNumber, meter, transactionId,
                            "InitiateOutageDetectionEventRequest", mspVendor.getCompanyName());
                    continue;
                }
                // Assume plc if we made it this far, validate meter can receive porter command requests and command string
                // exists, then perform action
                boolean supportsPing = paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(),
                        PaoTag.PORTER_COMMAND_REQUESTS);
                if (supportsPing) { // build up a list of plc command requests (to be sent later)
                    CommandRequestDevice request = new CommandRequestDevice("ping", SimpleDevice.of(meter.getPaoIdentifier()));

                    plcCommandRequests.add(request);
                    multispeakEventLogService.initiateODEvent(meterNumber, meter, transactionId,
                            "InitiateOutageDetectionEventRequest", mspVendor.getCompanyName());
                } else {
                    ErrorObject err = mspObjectDao.getErrorObject(meterNumber,
                            "MeterNumber (" + meterNumber + ") - Meter cannot receive requests from porter. ",
                            "Meter", "ODEvent", mspVendor.getCompanyName());
                    errorObjects.add(err);
                }
            }
        }

        // perform read attribute(?) on list of meters
        doRfnOutagePing(rfnPaosToPing, mspVendor, transactionId, responseUrl);
        // perform plc action on list of commandRequests
        doPlcOutagePing(plcCommandRequests, mspVendor, transactionId, responseUrl);
        return errorObjects;
    }

    @Required
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }

    @Override
    public void messageReceived(MessageEvent e) {

        Message in = e.getMessage();
        if (in instanceof Return) {
            final Return returnMsg = (Return) in;
            final MultispeakEvent event = getEventsMap().get(new Long(returnMsg.getUserMessageID()));

            if (event != null) {

                Runnable eventRunner = new Runnable() {

                    @Override
                    public void run() {

                        log.info("Message Received [ID:" + returnMsg.getUserMessageID() +
                                " DevID:" + returnMsg.getDeviceID() +
                                " Command:" + returnMsg.getCommandString() +
                                " Result:" + returnMsg.getResultString() +
                                " Status:" + returnMsg.getStatus() +
                                " More:" + returnMsg.getExpectMore() + "]");

                        if (returnMsg.getExpectMore() == 0) {

                            log.info("Received Message From ID:" + returnMsg.getDeviceID() + " - " + returnMsg.getResultString());

                            boolean doneProcessing = event.messageReceived(returnMsg);
                            if (doneProcessing) {
                                getEventsMap().remove(new Long(event.getPilMessageID()));
                            }
                        }
                    }
                };

                eventRunner.run();
            }
        }

    }
}