package com.cannontech.multispeak.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadCallback;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.deviceread.dao.WaitableDeviceAttributeReadCallback;
import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.meter.search.dao.MeterSearchDao;
import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.MeterSearchField;
import com.cannontech.amr.meter.search.model.MeterSearchOrderBy;
import com.cannontech.amr.meter.search.model.StandardFilterBy;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectCmdType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReplyType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectCallback;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.config.MasterConfigHelper;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.groups.DeviceGroupInUseException;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.exception.InsufficientMultiSpeakDataException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.location.Origin;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.roleproperties.MspPaoNameAliasEnum;
import com.cannontech.core.roleproperties.MultispeakManagePaoLocation;
import com.cannontech.core.roleproperties.MultispeakMeterLookupFieldEnum;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.point.stategroup.Disconnect410State;
import com.cannontech.database.db.point.stategroup.OutageStatus;
import com.cannontech.database.db.point.stategroup.PointStateHelper;
import com.cannontech.database.db.point.stategroup.RfnDisconnectStatusState;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.msp.beans.v3.ArrayOfMeterRead;
import com.cannontech.msp.beans.v3.ArrayOfOutageDetectionEvent;
import com.cannontech.msp.beans.v3.CDStateChangedNotification;
import com.cannontech.msp.beans.v3.ConnectDisconnectEvent;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.ExtensionsItem;
import com.cannontech.msp.beans.v3.ExtensionsList;
import com.cannontech.msp.beans.v3.FormattedBlock;
import com.cannontech.msp.beans.v3.FormattedBlockNotification;
import com.cannontech.msp.beans.v3.FormattedBlockNotificationResponse;
import com.cannontech.msp.beans.v3.LoadActionCode;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.MeterGroup;
import com.cannontech.msp.beans.v3.MeterRead;
import com.cannontech.msp.beans.v3.ODEventNotification;
import com.cannontech.msp.beans.v3.ODEventNotificationResponse;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.OutageDetectDeviceType;
import com.cannontech.msp.beans.v3.OutageDetectionEvent;
import com.cannontech.msp.beans.v3.OutageEventType;
import com.cannontech.msp.beans.v3.OutageLocation;
import com.cannontech.msp.beans.v3.ReadingChangedNotification;
import com.cannontech.msp.beans.v3.ReadingChangedNotificationResponse;
import com.cannontech.msp.beans.v3.ServiceLocation;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.CBClient;
import com.cannontech.multispeak.client.core.OAClient;
import com.cannontech.multispeak.dao.FormattedBlockProcessingService;
import com.cannontech.multispeak.dao.FormattedBlockUpdater;
import com.cannontech.multispeak.dao.FormattedBlockUpdaterChain;
import com.cannontech.multispeak.dao.MeterReadProcessingService;
import com.cannontech.multispeak.dao.MeterReadUpdater;
import com.cannontech.multispeak.dao.MeterReadUpdaterChain;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.data.MspErrorObjectException;
import com.cannontech.multispeak.data.MspLoadActionCode;
import com.cannontech.multispeak.event.MeterReadEvent;
import com.cannontech.multispeak.event.MultispeakEvent;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.MultispeakMeterServiceBase;
import com.cannontech.multispeak.service.v3.MultispeakMeterService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.BasicServerConnection;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

public class MultispeakMeterServiceImpl extends MultispeakMeterServiceBase implements MultispeakMeterService, MessageListener {

    private static final Logger log = YukonLogManager.getLogger(MultispeakMeterServiceImpl.class);

    private BasicServerConnection porterConnection;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private AttributeService attributeService;
    @Autowired private CommandExecutionService commandRequestService;
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    @Autowired private DeviceCreationService deviceCreationService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private MeterReadProcessingService meterReadProcessingService;
    @Autowired private MeterSearchDao meterSearchDao;
    @Autowired private MspMeterDao mspMeterDao;
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PointDao pointDao;
    @Autowired private RfnMeterDisconnectService rfnMeterDisconnectService;
    @Autowired private DeviceUpdateService deviceUpdateService;
    
    @Autowired private TransactionTemplate transactionTemplate;
    @Autowired private ObjectFactory objectFactory;
    @Autowired private OAClient oaClient;
    @Autowired private CBClient cbClient;
    @Autowired private YukonUserContextMessageSourceResolver resolver;

    /** Singleton incrementor for messageIDs to send to porter connection */
    private static long messageID = 1;

    /** A map of Long(userMessageID) to MultispeakEvent values */
    private static Map<Long, MultispeakEvent> eventsMap = Collections.synchronizedMap(new HashMap<Long, MultispeakEvent>());

    private ImmutableSet<OutageEventType> supportedEventTypes;
    private ImmutableSetMultimap<OutageEventType, Integer> outageConfig;

    private static final String EXTENSION_DEVICE_TEMPLATE_STRING = "AMRMeterType";

    // Strings to represent MultiSpeak method calls, generally used for logging.
    private static final String METER_REMOVE_STRING = "MeterRemoveNotification";
    private static final String METER_CHANGED_STRING = "MeterChangedNotification";
    private static final String METER_ADD_STRING = "MeterAddNotification";
    private static final String SERV_LOC_CHANGED_STRING = "ServiceLocationChangedNotification";

    
    /**
     * Get the static instance of Multispeak (this) object. Adds a message
     * listener to the pil connection instance.
     */
    @PostConstruct
    public void initialize() throws Exception {
        log.info("New MSP instance created");
        porterConnection.addMessageListener(this);

        ConfigurationSource configurationSource = MasterConfigHelper.getConfiguration();
        Builder<OutageEventType, Integer> builder = ImmutableMultimap.builder();
        
        // We are purposely not adding any RFN DeviceErros for OUTAGE, all should default to NO_RESPONSE
        // NM_TIMEOUT (aka RfnMeterReadingDataReplyType.NETWORK_TIMEOUT) is the only one that could be, (with some higher confidence), a real outage.
        builder.putAll(OutageEventType.OUTAGE, DeviceError.WORD_1_NACK_PADDED.getCode(),
                                               DeviceError.EWORD_RECEIVED.getCode(),
                                               DeviceError.DLC_READ_TIMEOUT.getCode());
        builder.putAll(OutageEventType.RESTORATION, DeviceError.ABNORMAL_RETURN.getCode(),
                                                    DeviceError.WORD_1_NACK.getCode(),
                                                    DeviceError.ROUTE_FAILED.getCode(),
                                                    DeviceError.SUCCESSFUL_READ.getCode());
        ImmutableMultimap<OutageEventType, Integer> systemDefault = builder.build();

        supportedEventTypes = ImmutableSet.of(OutageEventType.OUTAGE,
                                              OutageEventType.NO_RESPONSE,
                                              OutageEventType.RESTORATION,
                                              OutageEventType.POWER_OFF,
                                              OutageEventType.POWER_ON,
                                              OutageEventType.INSTANTANEOUS,
                                              OutageEventType.INFERRED);

        SetMultimap<OutageEventType, Integer> outageConfigTemp = HashMultimap.create(systemDefault);
        for (OutageEventType eventType : supportedEventTypes) {
            String valueStr = configurationSource.getString("MSP_OUTAGE_EVENT_TYPE_CONFIG_" + eventType.value().toUpperCase());
            if (valueStr != null) {
                int[] errorCodes = com.cannontech.common.util.StringUtils.parseIntStringAfterRemovingWhitespace(valueStr);
                List<Integer> errorCodeList = Arrays.asList(ArrayUtils.toObject(errorCodes));
                outageConfigTemp.values().removeAll(errorCodeList);
                outageConfigTemp.putAll(eventType, errorCodeList);
            }
        }

        outageConfig = ImmutableSetMultimap.copyOf(outageConfigTemp);
        log.info("outage event configuration: " + outageConfig);
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

                        log.info("Message Received [ID:"+ returnMsg.getUserMessageID() + 
                                     " DevID:" + returnMsg.getDeviceID() + 
                                     " Command:" + returnMsg.getCommandString() +
                                     " Result:" + returnMsg.getResultString() + 
                                     " Status:" + returnMsg.getStatus() +
                                     " More:" + returnMsg.getExpectMore()+"]");

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

    @Override
    public LoadActionCode CDMeterState(final MultispeakVendor mspVendor, final YukonMeter meter) throws MultispeakWebServiceException {

        log.info("Received " + meter.getMeterNumber() + " for CDMeterState from " + mspVendor.getCompanyName());
        if (!porterConnection.isValid()) {
            throw new MultispeakWebServiceException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
        }

        List<YukonMeter> allPaosToRead = Collections.singletonList(meter);
        final EnumSet<BuiltInAttribute> attributes = EnumSet.of(BuiltInAttribute.DISCONNECT_STATUS);

        LACWaitableDeviceAttributeReadCallback waitableCallback = new LACWaitableDeviceAttributeReadCallback(mspVendor.getRequestMessageTimeout()) {

            @Override
            public void complete() {
                super.complete();
                // do we need to do anything here once we received all of the data?
                log.debug("deviceAttributeReadCallback.complete for cdEvent");
            }

            @Override
            public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
                // the following is expensive but unavoidable until PointData is
                // changed
                PaoPointIdentifier paoPointIdentifier = pointDao.getPaoPointIdentifier(value.getId());
                Set<BuiltInAttribute> thisAttribute = attributeService.findAttributesForPoint(paoPointIdentifier.getPaoTypePointIdentifier(), attributes);
                if (thisAttribute.contains(BuiltInAttribute.DISCONNECT_STATUS)) {
                    setLoadActionCode(multispeakFuncs.getLoadActionCode(meter, value));
                } else {
                    return;
                }
            }

            @Override
            public void receivedLastValue(PaoIdentifier pao, String value) {
                log.debug("deviceAttributeReadCallback.receivedLastValue for cdEvent");
            }

            @Override
            public void receivedError(PaoIdentifier pao, SpecificDeviceErrorDescription error) {
                // do we need to send something to the foreign system here?
                log.warn("received error for " + pao + ": " + error);
            }

            @Override
            public void receivedException(SpecificDeviceErrorDescription error) {
                log.warn("received exception in meterReadEvent callback: " + error);
            }
            
        };
        multispeakEventLogService.initiateMeterRead(meter.getMeterNumber(), meter, "N/A", "GetCDMeterState", mspVendor.getCompanyName());
        deviceAttributeReadService.initiateRead(allPaosToRead, attributes, waitableCallback, DeviceRequestType.MULTISPEAK_METER_READ_EVENT, UserUtils.getYukonUser());
        try {
            waitableCallback.waitForCompletion();
        } catch (InterruptedException e) { /* Ignore */}
        return waitableCallback.getLoadActionCode();
    }

    /**
     * Wrapper class for holding attribute read response value.
     */
    private abstract class LACWaitableDeviceAttributeReadCallback extends WaitableDeviceAttributeReadCallback {
        private LoadActionCode loadActionCode = LoadActionCode.UNKNOWN;

        public LACWaitableDeviceAttributeReadCallback(long timeoutInMillis) {
            super(timeoutInMillis);
        }

        public void setLoadActionCode(LoadActionCode loadActionCode) {
            this.loadActionCode = loadActionCode;
        }

        public LoadActionCode getLoadActionCode() {
            return loadActionCode;
        }
    };

    /**
     * This method still does not support attributes. 
     * The issue is that SEDC does not support the ability to receive ReadingChangedNotification messages. 
     * Therefore, just for them, we initiate a real time read, wait, and return. 
     * This DOES need to be changed in future versions.
     */
    @Override
    public MeterRead getLatestReadingInterrogate(MultispeakVendor mspVendor, YukonMeter meter, String responseUrl) {
        long id = generateMessageID();
        MeterReadEvent event = new MeterReadEvent(mspVendor, id, meter, responseUrl);

        getEventsMap().put(new Long(id), event);
        
        String commandStr = "getvalue kwh";
        if (DeviceTypesFuncs.isMCT4XX(meter.getPaoIdentifier().getPaoType())) {
            commandStr = "getvalue peak"; // getvalue peak returns the peak kW and the total kWh
        }

        final String meterNumber = meter.getMeterNumber();
        log.info("Received " + meterNumber + " for LatestReadingInterrogate from " + mspVendor.getCompanyName());
        multispeakEventLogService.initiateMeterRead(meterNumber, meter, "N/A", "GetLatestReadingByMeterNo", mspVendor.getCompanyName());

        // Writes a request to pil for the meter and commandStr using the id for mspVendor.
        commandStr += " update noqueue";
        Request pilRequest = new Request(meter.getPaoIdentifier().getPaoId(), commandStr, id);
        pilRequest.setPriority(13);
        porterConnection.write(pilRequest);

        systemLog("GetLatestReadingByMeterNo", "(ID:" + meter.getPaoIdentifier().getPaoId() + ") MeterNumber (" + meterNumber + ") - " + commandStr, mspVendor);

        synchronized (event) {
            boolean timeout = !waitOnEvent(event, mspVendor.getRequestMessageTimeout());
            if (timeout) {
                mspObjectDao.logMSPActivity("GetLatestReadingByMeterNo",
                                            "MeterNumber (" + meterNumber + ") - Reading Timed out after " + 
                                            (mspVendor.getRequestMessageTimeout() / 1000) + " seconds.  No reading collected.",
                                            mspVendor.getCompanyName());
            }
        }

        return event.getDevice().getMeterRead();
    }

    @Override
    public ImmutableSet<OutageEventType> getSupportedEventTypes() {
        return supportedEventTypes;
    }

    @Override
    public ImmutableSetMultimap<OutageEventType, Integer> getOutageConfig() {
        return outageConfig;
    }

    @Override
    public synchronized List<ErrorObject> odEvent(final MultispeakVendor mspVendor, List<String> meterNumbers,
            final String transactionId, final String responseUrl) throws MultispeakWebServiceException {

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
        
        ListMultimap<String, YukonMeter> meterNumberToMeterMap = meterDao.getMetersMapForMeterNumbers(Lists.newArrayList(meterNumbers));
        boolean excludeDisabled = globalSettingDao.getBoolean(GlobalSettingType.MSP_EXCLUDE_DISABLED_METERS);

        for (String meterNumber : meterNumbers) {
            List<YukonMeter> meters = meterNumberToMeterMap.get(meterNumber);    // this will most likely be size 1
            if (CollectionUtils.isEmpty(meters)) {
                ErrorObject err =
                    mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "ODEvent",
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
                    multispeakEventLogService.initiateODEvent(meterNumber, meter, transactionId, "InitiateOutageDetectionEventRequest", mspVendor.getCompanyName());
                    continue;
                }
                // Assume plc if we made it this far, validate meter can receive porter command requests and command string exists, then perform action
                boolean supportsPing = paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.PORTER_COMMAND_REQUESTS);
                if (supportsPing) { // build up a list of plc command requests (to be sent later)
                    CommandRequestDevice request = new CommandRequestDevice("ping", SimpleDevice.of(meter.getPaoIdentifier()));

                    plcCommandRequests.add(request);
                    multispeakEventLogService.initiateODEvent(meterNumber, meter, transactionId, "InitiateOutageDetectionEventRequest", mspVendor.getCompanyName());
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
                
                Date now = new Date();  // may need to get this from the porter Return Message, but for now "now" will do.
                OutageEventType outageEventType = getForStatusCode(0);
                OutageDetectionEvent outageDetectionEvent = buildOutageDetectionEvent(yukonMeter, outageEventType, now, value);

                sendODEventNotification(yukonMeter, mspVendor, transactionId, responseUrl, outageDetectionEvent);
            }

            @Override
            public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                log.warn("receivedLastError for odEvent " + error.getDescription());
                SimpleMeter yukonMeter = meterDao.getSimpleMeterForId(command.getDevice().getDeviceId());
                
                Date now = new Date();  // may need to get this from the porter Return Message, but for now "now" will do.
                OutageEventType outageEventType = getForStatusCode(error.getErrorCode());
                OutageDetectionEvent outageDetectionEvent = buildOutageDetectionEvent(yukonMeter, outageEventType, now, error.getPorter());

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
            commandRequestService.execute(plcCommandRequests, callback, DeviceRequestType.MULTISPEAK_OUTAGE_DETECTION_PING_COMMAND, yukonUserContext.getYukonUser());
        }
    }

    /**
     * Performs the RFN meter outage analysis.
     * If MSP_RFN_PING_FORCE_CHANNEL_READ setting is set to
     *   true = perform a real time attribute read using Outage_Status attribute.
     *          Callback will initiate a ODEventNotification on receivedLastValue or receivedError.
     *  false = use the last known value of Outage_Status to determine odEvent state.
     * Returns immediately, does not wait for a response. 
     */
    private void doRfnOutagePing(final List<YukonMeter> meters, final MultispeakVendor mspVendor, final String transactionId, final String responseUrl) {

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
                            OutageEventType outageEventType = getForStatusCode(DeviceError.SUCCESSFUL_READ.getCode());  // assume if we got one value, then the meter must be talking successfully
                            OutageDetectionEvent outageDetectionEvent = buildOutageDetectionEvent(yukonMeter, outageEventType, value.getPointDataTimeStamp(), "");
                            sendODEventNotification(yukonMeter, mspVendor, transactionId, responseUrl, outageDetectionEvent);
                        }
                    }
                }
    
                @Override
                public void receivedLastValue(PaoIdentifier pao, String value) {    //success - not guaranteed!!!!
                    log.debug("deviceAttributeReadCallback.receivedLastValue for odEvent");
                    
                    YukonMeter yukonMeter = meterDao.getForId(pao.getPaoId());  // can we get this from meters?
                    if (meters.contains(yukonMeter)) {
                        meters.remove(yukonMeter);
                        Date now = new Date();  // may need to get this from the callback, but for now "now" will do.
                        OutageEventType outageEventType = getForStatusCode(DeviceError.TIMEOUT.getCode());  // unknown status if we didn't hit receivedValue at least once
                        OutageDetectionEvent outageDetectionEvent = buildOutageDetectionEvent(yukonMeter, outageEventType, now, "");
                        sendODEventNotification(yukonMeter, mspVendor, transactionId, responseUrl, outageDetectionEvent);
                    }
                }
    
                @Override
                public void receivedError(PaoIdentifier pao, SpecificDeviceErrorDescription error) {  //failure
                    log.warn("deviceAttributeReadCallback.receivedError for odEvent: " + pao + ": " + error);
                    
                    YukonMeter yukonMeter = meterDao.getForId(pao.getPaoId());  // can we get this from meters?
                    if (meters.contains(yukonMeter)) {
                        meters.remove(yukonMeter);
                        Date now = new Date();  // may need to get this from the callback, but for now "now" will do.
                        OutageEventType outageEventType = getForStatusCode(error.getErrorCode());
                        OutageDetectionEvent outageDetectionEvent = buildOutageDetectionEvent(yukonMeter, outageEventType, now, error.toString());
                        sendODEventNotification(yukonMeter, mspVendor, transactionId, responseUrl, outageDetectionEvent);
                    }
                }
    
                @Override
                public void receivedException(SpecificDeviceErrorDescription error) {
                    log.warn("deviceAttributeReadCallback.receivedException in odEvent callback: " + error);
                    // TODO there is still a potential bug here, because meters is left populated with the pao, even though an exception
                    //   has occurred. This means we can still get receivedLastValue and process it as a "success" instead of "unknown" or failure.
                }
    
            };
            if (CollectionUtils.isNotEmpty(meters)) {
                deviceAttributeReadService.initiateRead(meters, Sets.newHashSet(BuiltInAttribute.OUTAGE_STATUS), callback, DeviceRequestType.MULTISPEAK_OUTAGE_DETECTION_PING_COMMAND, UserUtils.getYukonUser());
            }
        } else {    //save network expense by just returning latest known value. 

            BiMap<LitePoint, PaoIdentifier> pointsToPaos = attributeService.getPoints(meters,  BuiltInAttribute.OUTAGE_STATUS).inverse();
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
                
                OutageDetectionEvent outageDetectionEvent = buildOutageDetectionEvent(yukonMeter, outageEventType, pointValue.getPointDataTimeStamp(), "Last Known Status");
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

            ODEventNotificationResponse odEventNotificationResponse =
                oaClient.odEventNotification(mspVendor, responseUrl, odEventNotification);
            List<ErrorObject> errObjects = null;
            if (odEventNotificationResponse != null && odEventNotificationResponse.getODEventNotificationResult() != null) {
                List<ErrorObject> responseErrorObjects = odEventNotificationResponse.getODEventNotificationResult().getErrorObject();
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
        outageDetectionEvent.setOutageDetectDeviceID(meterNumber);
        outageDetectionEvent.setOutageDetectDeviceType(OutageDetectDeviceType.METER);

        OutageLocation outageLocation = new OutageLocation();
        outageLocation.setObjectID(meterNumber);
        outageLocation.setMeterNo(meterNumber);
        outageDetectionEvent.setOutageLocation(outageLocation);

        // set defaults, may be overwritten below
        outageDetectionEvent.setComments(resultString);
        outageDetectionEvent.setOutageEventType(outageEventType);
        outageDetectionEvent.setErrorString(outageEventType.value() + ": " + resultString);

        return outageDetectionEvent;
    }


    @Override
    public synchronized List<ErrorObject> meterReadEvent(final MultispeakVendor mspVendor, List<String> meterNumbers,
            final String transactionID, final String responseUrl) {
        
        ArrayList<ErrorObject> errorObjects = new ArrayList<>();

        log.info("Received " + meterNumbers.size() + " Meter(s) for MeterReading from " + mspVendor.getCompanyName());
        multispeakEventLogService.initiateMeterReadRequest(meterNumbers.size(), "InitiateMeterReadByMeterNumber", mspVendor.getCompanyName());
        List<YukonMeter> allPaosToRead = Lists.newArrayListWithCapacity(meterNumbers.size());
        ListMultimap<String, YukonMeter> meterNumberToMeterMap = meterDao.getMetersMapForMeterNumbers(Lists.newArrayList(meterNumbers));
        boolean excludeDisabled = globalSettingDao.getBoolean(GlobalSettingType.MSP_EXCLUDE_DISABLED_METERS);
        
        for (String meterNumber : meterNumbers) {
            List<YukonMeter> meters = meterNumberToMeterMap.get(meterNumber);    // this will most likely be size 1
            if (CollectionUtils.isNotEmpty(meters)) {
                for (YukonMeter meter : meters) {
                    if (excludeDisabled && meter.isDisabled()) {
                        log.debug("Meter " + meter.getMeterNumber() + " is disabled, skipping.");
                        continue;
                    }
                    allPaosToRead.add(meter);
                    multispeakEventLogService.initiateMeterRead(meterNumber, meter, transactionID, "InitiateMeterReadByMeterNumber", mspVendor.getCompanyName());
                }
            } else {
                multispeakEventLogService.meterNotFound(meterNumber, "InitiateMeterReadByMeterNumber",
                    mspVendor.getCompanyName());
                ErrorObject err =
                    mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "MeterReadEvent",
                        mspVendor.getCompanyName());
                errorObjects.add(err);
                continue;
            }
        }


        final ImmutableMap<PaoIdentifier, YukonMeter> meterLookup = PaoUtils.indexYukonPaos(allPaosToRead);

        final EnumSet<BuiltInAttribute> attributes = EnumSet.of(BuiltInAttribute.USAGE, BuiltInAttribute.PEAK_DEMAND);

        final ConcurrentMap<PaoIdentifier, MeterReadUpdater> updaterMap = new MapMaker().concurrencyLevel(2).initialCapacity(meterNumbers.size()).makeMap();

        DeviceAttributeReadCallback callback = new DeviceAttributeReadCallback() {

            @Override
            public void complete() {
                // do we need to do anything here once we received all of the data?
                log.debug("deviceAttributeReadCallback.complete for meterReadEvent");
            }

            @Override
            public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
                // the following is expensive but unavoidable until PointData is changed
                PaoPointIdentifier paoPointIdentifier = pointDao.getPaoPointIdentifier(value.getId());
                Set<BuiltInAttribute> thisAttribute = attributeService.findAttributesForPoint(paoPointIdentifier.getPaoTypePointIdentifier(), attributes);

                if (log.isDebugEnabled()) {
                    log.debug("deviceAttributeReadCallback.receivedValue for meterReadEvent: " + paoPointIdentifier.toString() + " - " + value.getPointDataTimeStamp() + " - " + value.getValue());
                }

                if (thisAttribute.isEmpty()) {
                    log.debug("data received but no attribute found for point");
                    return;
                }
                for (BuiltInAttribute attribute : thisAttribute) {
                    log.debug("data received for some attribute: " + thisAttribute.toString());

                    // Get a new updater object for the current value
                    MeterReadUpdater meterReadUpdater = meterReadProcessingService.buildMeterReadUpdater(attribute,
                                                                                                         value);
                    // if the map is empty, place the updater into it
                    MeterReadUpdater oldValue = updaterMap.putIfAbsent(pao, meterReadUpdater);
                    while (oldValue != null) {
                     // looks like the map was not empty, combine the existing updater with the
                        // new one and then place it back in the map, but we must be careful
                        // that someone hasn't changed the map out from under us (thus the while loop)
                        MeterReadUpdaterChain chain = new MeterReadUpdaterChain(oldValue, meterReadUpdater);
                        boolean success = updaterMap.replace(pao, oldValue, chain);
                        if (success) {
                            break;
                        }
                        oldValue = updaterMap.putIfAbsent(pao, meterReadUpdater);
                    }
                }
            }

            /**
             * The unfortunate part is that this method is going to fire off a readingChangeNotification for each 
             * set of attributes that happened to be able to be collected using the same command 
             * (as derived by MeterReadCommandGenerationService.getMinimalCommandSet(...))
             */
            @Override
            public void receivedLastValue(PaoIdentifier pao, String value) {
                YukonMeter meter = meterLookup.get(pao);
                MeterRead meterRead = meterReadProcessingService.createMeterRead(meter);
                // because we were so careful about putting updater or updater chains into the
                // map, we know we can safely remove it and generate a MeterRead from it
                // whenever we want; but this happens to be a perfect time
                MeterReadUpdater updater = updaterMap.remove(pao);
                if (updater != null) {
                    updater.update(meterRead);
                    try {
                        log.info("Sending ReadingChangedNotification (" + responseUrl + "): Meter Number " + meterRead.getObjectID());
                        final ReadingChangedNotification readingChangedNotification = objectFactory.createReadingChangedNotification();
                        ArrayOfMeterRead meterReads = objectFactory.createArrayOfMeterRead();
                        meterReads.getMeterRead().add(meterRead);
                        readingChangedNotification.setChangedMeterReads(meterReads);
                        
                        readingChangedNotification.setTransactionID(transactionID);
                        log.info("Sending ReadingChangedNotification (" + responseUrl + "): Meter Number " + meterRead.getObjectID());
                        ReadingChangedNotificationResponse readingChangedNotificationResponse = cbClient.readingChangedNotification(mspVendor,
                                                                                                                                    responseUrl,
                                                                                                                                    readingChangedNotification);
                        List<ErrorObject> errObjects = null;
                        if (readingChangedNotificationResponse != null && readingChangedNotificationResponse.getReadingChangedNotificationResult() != null) {
                            List<ErrorObject> responseErrorObjects = readingChangedNotificationResponse.getReadingChangedNotificationResult().getErrorObject();
                            errObjects = responseErrorObjects;
                        }
                        multispeakEventLogService.notificationResponse("ReadingChangedNotification", transactionID, meterRead.getObjectID(), 
                                                                       meterRead.getPosKWh().toString(), CollectionUtils.size(errObjects), responseUrl); //just picked PoskWh to log...because nothing better
                        if (CollectionUtils.isNotEmpty(errObjects)) {
                            multispeakFuncs.logErrorObjects(responseUrl, "ReadingChangedNotification", errObjects);
                        }
                    } catch (MultispeakWebServiceClientException e) {
                        log.warn("caught exception in receivedValue of meterReadEvent", e);
                    }
                } else {
                    log.info("No matching attribute to point mappings identified. No notification message triggered.");
                }
            }

            @Override
            public void receivedError(PaoIdentifier pao, SpecificDeviceErrorDescription error) {
                // do we need to send something to the foreign system here?
                log.warn("received error for " + pao + ": " + error);
            }

            @Override
            public void receivedException(SpecificDeviceErrorDescription error) {
                log.warn("received exception in meterReadEvent callback: " + error);
            }
        };
        if (CollectionUtils.isNotEmpty(allPaosToRead)) {
            deviceAttributeReadService.initiateRead(allPaosToRead, attributes, callback, DeviceRequestType.MULTISPEAK_METER_READ_EVENT, UserUtils.getYukonUser());
        }
        return errorObjects;
    }

    @Override
    public synchronized List<ErrorObject> blockMeterReadEvent(final MultispeakVendor mspVendor, String meterNumber,
            final FormattedBlockProcessingService<Block> blockProcessingService, final String transactionId,
            final String responseUrl) {
        
        ArrayList<ErrorObject> errorObjects = new ArrayList<>();

        log.info("Received " + meterNumber + " for BlockMeterReading from " + mspVendor.getCompanyName());
        multispeakEventLogService.initiateMeterReadRequest(1, "InitiateMeterReadByMeterNoAndType", mspVendor.getCompanyName());
        


        final YukonMeter paoToRead;
        try {
            paoToRead = mspMeterDao.getMeterForMeterNumber(meterNumber);
            multispeakEventLogService.initiateMeterRead(meterNumber, paoToRead, transactionId, "InitiateMeterReadByMeterNoAndType", mspVendor.getCompanyName());
        } catch (NotFoundException e) {
            multispeakEventLogService.meterNotFound(meterNumber, "InitiateMeterReadByMeterNoAndType", mspVendor.getCompanyName());
            ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "BlockMeterReadEvent", mspVendor.getCompanyName());
            errorObjects.add(err);
            log.error(e);
            return errorObjects;
        }

        final EnumSet<BuiltInAttribute> attributes = blockProcessingService.getAttributeSet();

        // retain only those that ARE readable.
        // profile attributes are not readable right now, which means that the LoadBlock specifically, will not get many new values.
        // this is a change from before where we built up a command to retrieve at least the last 6 profile reads. Oh well...
        // a solution could be implemented a new command for collected the "latest" profile reads available.
        attributes.retainAll(attributeService.getReadableAttributes());

        final ConcurrentMap<PaoIdentifier, FormattedBlockUpdater<Block>> updaterMap = new MapMaker().concurrencyLevel(2).initialCapacity(1).makeMap();

        DeviceAttributeReadCallback callback = new DeviceAttributeReadCallback() {

            /**
             * Because we only have one meterNumber that is being processed, we will wait until all reads are
             * returned to fire off the formattedBlockNotification. 
             * This allows us to group them all together into one Block, which is desired.
             * This is different than the meterReadEvent which may have multiple meterNumbers it is processing and 
             * therefore fires notifications for each read that comes in (basically using this same layout of code, only
             * implemented in receivedLastValue instead)
             */
            @Override
            public void complete() {

                Block block = blockProcessingService.createBlock(paoToRead);

             // because we were so careful about putting updater or updater chains into the
                // map, we know we can safely remove it and generate a MeterRead from it
                // whenever we want; but this happens to be a perfect time
                FormattedBlockUpdater<Block> updater = updaterMap.remove(paoToRead.getPaoIdentifier());
                if (updater != null) {
                    updater.update(block);
                } else {
                    log.warn("no data updates for meter. notification will contain no readings");
                }
                final FormattedBlockNotification formattedBlockNotification = objectFactory.createFormattedBlockNotification();
                FormattedBlock formattedBlock = blockProcessingService.createMspFormattedBlock(block);
                formattedBlockNotification.setTransactionID(transactionId);
                formattedBlockNotification.setChangedMeterReads(formattedBlock);
                formattedBlockNotification.setErrorString("errorString?");
                try {
                    FormattedBlockNotificationResponse formattedBlockNotificationResponse = cbClient.formattedBlockNotification(mspVendor,
                                                                                                                                responseUrl,
                                                                                                                                formattedBlockNotification);
                    List<ErrorObject> errObjects = null;
                    if (formattedBlockNotificationResponse != null && formattedBlockNotificationResponse.getFormattedBlockNotificationResult() != null) {
                        List<ErrorObject> responseErrorObjects = formattedBlockNotificationResponse.getFormattedBlockNotificationResult().getErrorObject();
                        errObjects = responseErrorObjects;
                    }
                    multispeakEventLogService.notificationResponse("FormattedBlockNotification", transactionId, block.getObjectId(), 
                                                                   "", CollectionUtils.size(errObjects), responseUrl);
                    if (CollectionUtils.isNotEmpty(errObjects)) {
                        multispeakFuncs.logErrorObjects(responseUrl, "FormattedBlockNotification", errObjects);
                    }
                } catch (MultispeakWebServiceClientException e) {
                    log.warn("caught exception in receivedValue of FormattedBlockEvent", e);
                }
            }

            @Override
            public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
                // the following is expensive but unavoidable until PointData is changed
                PaoPointIdentifier paoPointIdentifier = pointDao.getPaoPointIdentifier(value.getId());
                Set<BuiltInAttribute> thisAttribute = attributeService.findAttributesForPoint(paoPointIdentifier.getPaoTypePointIdentifier(), attributes);
                if (thisAttribute.isEmpty()) {
                    return;
                }

                // Get a new updater object for the current value
                for (BuiltInAttribute attribute : thisAttribute) {
                    FormattedBlockUpdater<Block> formattedBlockUpdater = blockProcessingService.buildFormattedBlockUpdater(attribute, value);

                    // if the map is empty, place the updater into it
                    FormattedBlockUpdater<Block> oldValue = updaterMap.putIfAbsent(pao, formattedBlockUpdater);

                    while (oldValue != null) {
                     // looks like the map was not empty, combine the existing updater with the
                        // new one and then place it back in the map, but we must be careful
                        // that someone hasn't changed the map out from under us (thus the while loop)
                        FormattedBlockUpdaterChain<Block> chain = new FormattedBlockUpdaterChain<>(oldValue, formattedBlockUpdater);
                        boolean success = updaterMap.replace(pao, oldValue, chain);
                        if (success) {
                            break;
                        }
                        oldValue = updaterMap.putIfAbsent(pao, formattedBlockUpdater);
                    }
                }
            }

            @Override
            public void receivedLastValue(PaoIdentifier pao, String value) {
                log.debug("deviceAttributeReadCallback.receivedLastValue for formattedBlockEvent");
            }

            @Override
            public void receivedError(PaoIdentifier pao, SpecificDeviceErrorDescription error) {
                // do we need to send something to the foreign system here?
                log.warn("received error for " + pao + ": " + error);
            }

            @Override
            public void receivedException(SpecificDeviceErrorDescription error) {
                log.warn("received exception in FormattedBlockEvent callback: " + error);
            }
        };
        
        multispeakEventLogService.initiateMeterRead(meterNumber, paoToRead, transactionId, "InitiateMeterReadByMeterNoAndType", mspVendor.getCompanyName());
        deviceAttributeReadService.initiateRead(Collections.singleton(paoToRead), attributes, callback, DeviceRequestType.MULTISPEAK_FORMATTED_BLOCK_READ_EVENT, UserUtils.getYukonUser());
        return errorObjects;
    }

    @Override
    public synchronized List<ErrorObject> cdEvent(final MultispeakVendor mspVendor,
            List<ConnectDisconnectEvent> cdEvents, final String transactionId, final String responseUrl)
            throws MultispeakWebServiceException {

        if (!porterConnection.isValid()) {
            throw new MultispeakWebServiceException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
        }

        ArrayList<ErrorObject> errorObjects = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(cdEvents)) {
            log.info("Received " + cdEvents.size() + " Meter(s) for Connect/Disconnect from "
                + mspVendor.getCompanyName());
            multispeakEventLogService.initiateCDRequest(cdEvents.size(), "InitiateConnectDisconnect",
                mspVendor.getCompanyName());

            List<CommandRequestDevice> plcCommandRequests = Lists.newArrayList();

            for (ConnectDisconnectEvent cdEvent : cdEvents) {
                final String meterNumber = getMeterNumberFromCDEvent(cdEvent);

                try {
                    YukonMeter meter = mspMeterDao.getMeterForMeterNumber(meterNumber);
                    if (cdEvent.getLoadActionCode() == null) {
                        ErrorObject err =
                            mspObjectDao.getErrorObject(meterNumber, "MeterNumber (" + meterNumber
                                + ") - Cannot InitiateConnectDisconnect as no load action code exists.", "Meter",
                                "CDEvent", mspVendor.getCompanyName());
                        errorObjects.add(err);
                        continue;
                    }

                    MspLoadActionCode mspLoadActionCode =
                        MspLoadActionCode.getForLoadActionCode(cdEvent.getLoadActionCode());

                    // validate is CD supported meter
                    if (!mspMeterDao.isCDSupportedMeter(meterNumber)) {
                        ErrorObject err =
                            mspObjectDao.getErrorObject(meterNumber, "MeterNumber (" + meterNumber
                                + ") - Invalid Yukon Connect/Disconnect Meter.", "Meter", "CDEvent",
                                mspVendor.getCompanyName());
                        errorObjects.add(err);
                        continue;
                    }

                    // check for rf disconnect meter type and perform action
                    boolean isRfnDisconnect =
                        paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.DISCONNECT_RFN);
                    if (isRfnDisconnect) {
                        RfnMeter rfnMeter = (RfnMeter) meter;
                        multispeakEventLogService.initiateCD(meter.getMeterNumber(), meter,
                            mspLoadActionCode.toString(), transactionId, "InitiateConnectDisconnect",
                            mspVendor.getCompanyName());
                        doRfnConnectDisconnect(rfnMeter,
                            mspLoadActionCode.getRfnMeterDisconnectCmdType(configurationSource), mspVendor,
                            transactionId, responseUrl);
                        continue;
                    }

                    // Assume plc if we made it this far, validate meter can receive porter command requests
                    // and command string exists, then perform action
                    boolean canInitiatePorterRequest =
                        paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(),
                            PaoTag.PORTER_COMMAND_REQUESTS);
                    if (!canInitiatePorterRequest || StringUtils.isBlank(mspLoadActionCode.getPlcCommandString())) {
                        ErrorObject err =
                            mspObjectDao.getErrorObject(meterNumber, "MeterNumber (" + meterNumber
                                + ") - Meter cannot receive requests from porter or no control command exists. "
                                + "LoadActionCode=" + cdEvent.getLoadActionCode(), "Meter", "CDEvent",
                                mspVendor.getCompanyName());
                        errorObjects.add(err);
                    } else { // build up a list of plc command requests (to be sent later)
                        CommandRequestDevice request = new CommandRequestDevice(mspLoadActionCode.getPlcCommandString(), new SimpleDevice(meter));
                        plcCommandRequests.add(request);
                        multispeakEventLogService.initiateCD(meter.getMeterNumber(), meter,
                            mspLoadActionCode.toString(), (cdEvent.getReasonCode() != null
                                ? cdEvent.getReasonCode().value() : "unknown"), "InitiateConnectDisconnect",
                            mspVendor.getCompanyName());
                    }
                } catch (NotFoundException e) {
                    multispeakEventLogService.meterNotFound(meterNumber, "InitiateConnectDisconnect",
                        mspVendor.getCompanyName());
                    ErrorObject err =
                        mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "CDEvent",
                            mspVendor.getCompanyName());
                    errorObjects.add(err);
                    log.error(e);
                }

            }

            // perform plc action on list of commandRequests
            doPlcConnectDisconnect(plcCommandRequests, mspVendor, transactionId, responseUrl);
        }
        return errorObjects;
    }

    /**
     * Performs the PLC meter disconnect. 
     * Returns immediately, does not wait for a response. 
     * Callback will initiate a cdEventNotification on receivedValue.
     */
    private void doPlcConnectDisconnect(List<CommandRequestDevice> plcCommandRequests, final MultispeakVendor mspVendor,
            final String transactionId, final String responseUrl) {
        
        YukonUserContext yukonUserContext = YukonUserContext.system;

        CommandCompletionCallback<CommandRequestDevice> callback = new CommandCompletionCallback<CommandRequestDevice>() {

            @Override
            public void receivedIntermediateError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                log.warn("receivedIntermediateError for cdEvent " + error.getDescription());
            }

            @Override
            public void receivedIntermediateResultString(CommandRequestDevice command, String value) {
                log.debug("receivedIntermediateResultString for cdEvent " + value);
            }

            @Override
            public void receivedValue(CommandRequestDevice command, PointValueHolder value) {
                log.debug("receivedValue for cdEvent" + value);
                Disconnect410State state = Disconnect410State.getByRawState(new Double(value.getValue()).intValue());
                MspLoadActionCode mspLoadActionCode = MspLoadActionCode.getForPlcState(state);
                SimpleMeter yukonMeter = meterDao.getSimpleMeterForId(command.getDevice().getDeviceId());
                sendCDEventNotification(yukonMeter, mspLoadActionCode.getLoadActionCode(), mspVendor, transactionId, responseUrl);
            }

            @Override
            public void receivedLastResultString(CommandRequestDevice command, String value) {
                log.debug("receivedLastResultString for cdEvent " + value);
            }

            @Override
            public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                log.warn("receivedLastError for cdEvent " + error.getDescription());
            }

            @Override
            public void complete() {
                log.debug("complete for cdEvent");
            }

            @Override
            public void processingExceptionOccurred(String reason) {
                log.warn("processingExceptionOccurred for cdEvent " + reason);
            }
        };
        
        if (CollectionUtils.isNotEmpty(plcCommandRequests)) {
            commandRequestService.execute(plcCommandRequests, callback, DeviceRequestType.MULTISPEAK_CONNECT_DISCONNECT, yukonUserContext.getYukonUser());
        }
    }

    /**
     * Performs the RFN meter disconnect. 
     * Returns immediately, does not wait for a response. 
     * Callback will initiate a cdEventNotification on success or error.
     */
    private void doRfnConnectDisconnect(final RfnMeter meter, RfnMeterDisconnectCmdType action,
            final MultispeakVendor mspVendor, final String transactionId, final String responseUrl) {

        RfnMeterDisconnectCallback rfnCallback = new RfnMeterDisconnectCallback() {

            @Override
            public void receivedSuccess(RfnDisconnectStatusState state, PointValueQualityHolder pointData) {
                log.debug("rfn " + meter + " receivedSuccess for cdEvent " + state);
                MspLoadActionCode mspLoadActionCode = MspLoadActionCode.getForRfnState(state);
                sendCDEventNotification(meter, mspLoadActionCode.getLoadActionCode(), mspVendor, transactionId, responseUrl);
            }

            @Override
            public void receivedError(MessageSourceResolvable message, RfnDisconnectStatusState state, RfnMeterDisconnectConfirmationReplyType replyType) {
                log.warn("rfn " + meter + " receivedError for cdEvent " + getMessageText(message));
                // MspLoadActionCode mspLoadActionCode = MspLoadActionCode.getForRfnState(RfnDisconnectStatusState.getForNmState(state));
                // should we actually be returning msopLoadActionCode.getLoadActionCode instead of UNKNOWN ?
                sendCDEventNotification(meter, LoadActionCode.UNKNOWN, mspVendor, transactionId, responseUrl);
            }

            @Override
            public void processingExceptionOccurred(MessageSourceResolvable message) {
                log.warn("rfn " + meter + " processingExceptionOccurred for cdEvent " + getMessageText(message));
            }

            @Override
            public void complete() {
                log.debug("rfn " + meter + " complete for cdEvent");
            }

        };

        rfnMeterDisconnectService.send(meter, action, rfnCallback);
    }
    
    /**
     * Returns message text.
     */
    private String getMessageText(MessageSourceResolvable message){
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(YukonUserContext.system);
        return accessor.getMessage(message);
    }

    /**
     * Returns meterNumber from ConnectDisconnectEvent object. Tries to load
     * from CDEvent's meterNo, then meterId (SEDC specific), then objectId.
     * @param cdEvent
     * @return
     */
    private String getMeterNumberFromCDEvent(ConnectDisconnectEvent cdEvent) {
        String meterNumber = cdEvent.getMeterNo();

        // Try to load MeterNumber from another element
        if (StringUtils.isBlank(meterNumber)) {
            meterNumber = cdEvent.getMeterID(); // SEDC
            if (StringUtils.isBlank(meterNumber)) { // this is only necessary for old integrations; moving forward, objectId will be a unique identifier of the cdevent.
                meterNumber = cdEvent.getObjectID();
            }
        }
        if (meterNumber != null) {
            meterNumber = meterNumber.trim();
        }
        return meterNumber;
    }

    /**
     * Initiates a CDStateChangedNotifaction message to vendor.
     * @param yukonMeter - meter
     * @param loadActionCode - loadActionCode
     * @param mspVendor - msp vendor that made the inital request
     * @param transactionId - the token provided from the initial request
     */
    private void sendCDEventNotification(SimpleMeter yukonMeter, LoadActionCode loadActionCode,
            MultispeakVendor mspVendor, String transactionId, String responseUrl) {

        log.info("Sending CDStateChangedNotification (" + responseUrl + "): Meter Number " + yukonMeter.getMeterNumber() + " Code:" + loadActionCode);

        try {
            CDStateChangedNotification cdStateChangedNotification = objectFactory.createCDStateChangedNotification();
            cdStateChangedNotification.setMeterNo(yukonMeter.getMeterNumber());
            cdStateChangedNotification.setStateChange(loadActionCode);
            cdStateChangedNotification.setTransactionID(transactionId);
            cbClient.cdStateChangedNotification(mspVendor, responseUrl, cdStateChangedNotification);
            multispeakEventLogService.notificationResponse("CDStateChangedNotification", transactionId, yukonMeter.getMeterNumber(),
                                                               loadActionCode.value(), -1, responseUrl);
            
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + responseUrl + " - InitiateConnectDisconnect (" + mspVendor.getCompanyName() + ")");
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
        }
    }

    /**
     * Returns true if event processes without timing out, false if event times
     * out.
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

    @Override
    public List<ErrorObject> initiateDisconnectedStatus(MultispeakVendor mspVendor, List<String> meterNos) {
        boolean disable = globalSettingDao.getBoolean(GlobalSettingType.MSP_DISABLE_DISCONNECT_STATUS);
        return addToGroupAndDisable(meterNos, SystemGroupEnum.DISCONNECTED_STATUS, "InitiateDisconnectedStatus", mspVendor, disable);
    }

    @Override
    public List<ErrorObject> initiateUsageMonitoring(MultispeakVendor mspVendor, List<String> meterNos) {
        return addMetersToGroup(meterNos, SystemGroupEnum.USAGE_MONITORING, "InitiateUsageMonitoring", mspVendor);
    }

    @Override
    public List<ErrorObject> cancelDisconnectedStatus(MultispeakVendor mspVendor, List<String> meterNos) {
        // For the cancel method, the MSP_DISABLE_DISCONNECT_STATUS setting shall be reversed to "undo" the disable.
        boolean enable = globalSettingDao.getBoolean(GlobalSettingType.MSP_DISABLE_DISCONNECT_STATUS);
        return removeMetersFromGroupAndEnable(meterNos, SystemGroupEnum.DISCONNECTED_STATUS, "CancelDisconnectedStatus", mspVendor, enable);
    }

    @Override
    public List<ErrorObject> cancelUsageMonitoring(MultispeakVendor mspVendor, List<String> meterNos) {
        return removeMetersFromGroup(meterNos, SystemGroupEnum.USAGE_MONITORING, "CancelUsageMonitoring", mspVendor);
    }

    @Override
    public List<ErrorObject> meterAdd(final MultispeakVendor mspVendor, List<Meter> addMeters) throws MultispeakWebServiceException {
        final List<ErrorObject> errorObjects = new ArrayList<>();

        for (final Meter mspMeter : addMeters) {
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {

                        validateMspMeter(mspMeter, mspVendor, METER_ADD_STRING);

                        YukonMeter newMeter;
                        try {
                            newMeter = checkForExistingMeterAndUpdate(mspMeter, mspVendor);
                        } catch (NotFoundException e) { // and NEW meter
                            newMeter = addNewMeter(mspMeter, mspVendor);
                        }

                        updatePaoLocation(mspMeter, newMeter, METER_ADD_STRING);
                        removeFromGroup(newMeter, SystemGroupEnum.INVENTORY, METER_ADD_STRING, mspVendor);

                        updateCISDeviceClassGroup(mspMeter.getMeterNo(), mspMeter.getDeviceClass(), newMeter, METER_ADD_STRING, mspVendor);

                        // TODO Consider moving this stuff out of this transaction....requesting serviceLocaiton could fail and shouldn't roll back the rest of this.
                        ServiceLocation mspServiceLocation = mspObjectDao.getMspServiceLocation(mspMeter.getMeterNo(), mspVendor);

                        // update the billing group from CIS billingCyle
                        if (mspServiceLocation != null) {
                            String billingCycle = mspServiceLocation.getBillingCycle();
                            updateBillingCyle(billingCycle, newMeter.getMeterNumber(), newMeter, METER_ADD_STRING, mspVendor);
                            updateAltGroup(mspServiceLocation, newMeter.getMeterNumber(), newMeter, METER_ADD_STRING, mspVendor);
                        } else {
                            multispeakEventLogService.objectNotFoundByVendor(mspMeter.getMeterNo(), "GetServiceLocationByMeterNo", METER_ADD_STRING, mspVendor.getCompanyName());
                        }
                        
                        // Must complete route locate after meter is enabled
                        verifyAndUpdateSubstationGroupAndRoute(newMeter, mspVendor, mspMeter, mspServiceLocation, METER_ADD_STRING);
                    };
                });
            } catch (MspErrorObjectException e) {
                errorObjects.add(e.getErrorObject());
                multispeakEventLogService.errorObject(e.getErrorObject().getErrorString(), METER_ADD_STRING, mspVendor.getCompanyName());
                log.error(e);
            } catch (RuntimeException ex) {
                // Transactional code threw application exception -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getMeterNo(),
                                                              "X Exception: (MeterNo:" + mspMeter.getMeterNo() + ")-" + ex.getMessage(),
                                                              "Meter",
                                                              METER_ADD_STRING,
                                                              mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.errorObject(err.getErrorString(), METER_ADD_STRING, mspVendor.getCompanyName());
                log.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getMeterNo(),
                                                              "X Error: (MeterNo:" + mspMeter.getMeterNo() + ")-" + ex.getMessage(),
                                                              "Meter",
                                                              METER_ADD_STRING,
                                                              mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.errorObject(err.getErrorString(), METER_ADD_STRING, mspVendor.getCompanyName());
                log.error(ex);
            }
        }// end for

        return errorObjects;
    }

    /**
     * Add a new meter to Yukon.
     * @throws MspErrorObjectException when templateName is not a valid YukonPaobject Name.
     */
    private YukonMeter addNewMeter(Meter mspMeterToAdd, MultispeakVendor mspVendor) throws MspErrorObjectException {

        YukonMeter templateMeter = getYukonMeterForTemplate(mspMeterToAdd, mspVendor, true);

        String newPaoName = getPaoNameFromMspMeter(mspMeterToAdd, mspVendor);
        // If PaoName already exists, a uniqueness value will be added.
        newPaoName = getNewUniquePaoName(newPaoName);

        SimpleDevice newDevice;

        // Create PLC or RFN Device object with defaults
        try {
            if (templateMeter.getPaoType().isRfn()) {

                String serialNumber = mspMeterToAdd.getNameplate().getTransponderID().trim();
                RfnIdentifier newMeterRfnIdentifier = buildNewMeterRfnIdentifier((RfnMeter)templateMeter, serialNumber);

                // Use Model and Manufacturer from template
                newDevice = deviceCreationService.createRfnDeviceByTemplate(templateMeter.getName(),
                                                                            newPaoName,
                                                                            newMeterRfnIdentifier,
                                                                            true);

            } else if (templateMeter.getPaoType().isPlc()) {
                // CREATE DEVICE - new device is automatically added to template's device groups
                // TODO create new method here that takes a loaded template...since we already have one!
                newDevice = deviceCreationService.createDeviceByTemplate(templateMeter.getName(), newPaoName, true);
            } else {
                // return errorObject for any other type.
                ErrorObject errorObject = mspObjectDao.getErrorObject(mspMeterToAdd.getObjectID(),
                                                                      "Error: Invalid template type [" + templateMeter.getPaoType() + "].",
                                                                      "Meter",
                                                                      METER_ADD_STRING,
                                                                      mspVendor.getCompanyName());
                throw new MspErrorObjectException(errorObject);
            }
        } catch (DeviceCreationException | BadConfigurationException e) {
            log.error(e);
            ErrorObject errorObject = mspObjectDao.getErrorObject(mspMeterToAdd.getObjectID(),
                                                                  "Error: " + e.getMessage(),
                                                                  "Meter",
                                                                  METER_ADD_STRING,
                                                                  mspVendor.getCompanyName());
            throw new MspErrorObjectException(errorObject);
        }

        YukonMeter newMeter = meterDao.getForId(newDevice.getDeviceId());
        systemLog(METER_ADD_STRING, "New Meter created: " + newMeter.toString(), mspVendor);
        multispeakEventLogService.meterCreated(newMeter.getMeterNumber(), newMeter, METER_ADD_STRING, mspVendor.getCompanyName());

        // update default values of newMeter
        newMeter = updateExistingMeter(mspMeterToAdd, newMeter, templateMeter, METER_ADD_STRING, mspVendor, true);
        return newMeter;
    }
    
    /**
     * Check for existing meter in system and update if found.
     * @throws NotFoundException if existing meter is not found in system.
     * @throws MspErrorObjectException when templateName is not a valid YukonPaobject Name. Or if changeType cannot be processed.
     */
    private YukonMeter checkForExistingMeterAndUpdate(Meter mspMeter, MultispeakVendor mspVendor)
            throws NotFoundException, MspErrorObjectException {

        YukonMeter meter = null;
        // Get meter address from mspMeter.communicationsAddress.value
        String mspAddress =
            (mspMeter.getNameplate() != null && mspMeter.getNameplate().getTransponderID() != null)
                ? mspMeter.getNameplate().getTransponderID().trim() : null;
        MultispeakMeterLookupFieldEnum multispeakMeterLookupFieldEnum = multispeakFuncs.getMeterLookupField();

        switch (multispeakMeterLookupFieldEnum) {
        case METER_NUMBER:
            meter = getMeterByMeterNumber(mspMeter.getMeterNo().trim());
            break;
        case ADDRESS:
            meter = getMeterBySerialNumberOrAddress(mspAddress);
            break;
        case DEVICE_NAME:
            meter = getMeterByPaoName(mspMeter, mspVendor);
            break;
        case AUTO_METER_NUMBER_FIRST:
            try { // Lookup by MeterNo
                meter = getMeterByMeterNumber(mspMeter.getMeterNo().trim());
            } catch (NotFoundException e) { // Doesn't exist by MeterNumber
                try { // Lookup by Address
                    meter = getMeterBySerialNumberOrAddress(mspAddress);
                } catch (NotFoundException e2) { // Doesn't exist by Address
                    meter = getMeterByPaoName(mspMeter, mspVendor);
                    // Not Found Exception thrown in the end if never found
                }
            }
            break;
        case AUTO_DEVICE_NAME_FIRST:
        default:
            try { // Lookup by Device Name
                meter = getMeterByPaoName(mspMeter, mspVendor);
            } catch (NotFoundException e) { // Doesn't exist by Device name
                try { // Lookup by Meter Number
                    meter = getMeterByMeterNumber(mspMeter.getMeterNo().trim());
                } catch (NotFoundException e2) {
                    // Lookup by Address
                    meter = getMeterBySerialNumberOrAddress(mspAddress);
                    // Not Found Exception thrown in the end if never found
                }
            }
            break;
        }

        multispeakEventLogService.meterFound(meter.getMeterNumber(), meter, METER_ADD_STRING, mspVendor.getCompanyName());
        
        // load (and validate) template exists
        YukonMeter templateMeter = getYukonMeterForTemplate(mspMeter, mspVendor, false); // throws MspErrorObjectException
        if (templateMeter == null) {
            // If no template found, just use this meter as the template (meaning, same meter, no type changes).
            templateMeter = meter;
        }
        
        meter = updateExistingMeter(mspMeter, meter, templateMeter, METER_ADD_STRING, mspVendor, true);
        return meter;
    }

    /**
     * Helper method to update an existing meter with data associated with mspMeter.
     * Updates the following (if different):
     *  - If existingMeter is enabled, we just warn...do not give up (anymore! 201406 change)
     *  - Attempt to change the deviceType, throws MspErrorObjectException if cannot be completed.
     *  - Attempt to change the Meter Number.
     *  - Attempt to change the Serial Number or (Carrier) Address.
     *  - Attempt to change the PaoName. -
     *  - Loads MspServiceLocation from meterNumber.
     *      - Updates BillingCycle 
     *      - Updates Alt Group (DEMCO special)
     *      - Removes from /Meters/Flags/Inventory/
     *  - Enables meter 
     *  - Attempt to update CIS Substation Group and Routing information
     * @return Returns the updated existingMeter object (in case of major paoType change)
     */
    private YukonMeter updateExistingMeter(Meter mspMeter, YukonMeter existingMeter, YukonMeter templateMeter, String mspMethod,
            MultispeakVendor mspVendor, boolean enable) throws MspErrorObjectException {

        YukonMeter originalCopy = existingMeter;
        String newSerialOrAddress = mspMeter.getNameplate().getTransponderID().trim(); // this should be the sensorSerialNumber        
        
        existingMeter = verifyAndUpdateType(templateMeter, existingMeter, newSerialOrAddress, mspMethod, mspVendor);

        String newMeterNumber = mspMeter.getMeterNo().trim();
        verifyAndUpdateMeterNumber(newMeterNumber, existingMeter, mspMethod, mspVendor);

        verifyAndUpdateAddressOrSerial(newSerialOrAddress, templateMeter, existingMeter, mspMethod, mspVendor);

        String newPaoName = getPaoNameFromMspMeter(mspMeter, mspVendor);
        verifyAndUpdatePaoName(newPaoName, existingMeter, mspMethod, mspVendor);

        // Enable Meter and update applicable fields.
        if (enable) {
            if (existingMeter.isDisabled()) {
                deviceDao.enableDevice(existingMeter);
                existingMeter.setDisabled(false); // update local object with new status
                multispeakEventLogService.enableDevice(existingMeter.getMeterNumber(), existingMeter, mspMethod, mspVendor.getCompanyName());
            } else {
                log.info("Meter (" + existingMeter.toString() + ") - currently enabled, continuing with processing...");
            }
        }
        
        systemLog(mspMethod, "Original:"+ originalCopy.toString() + " New:"+ existingMeter.toString(), mspVendor);
        // TODO Perform DBChange for Pao here instead? Need to make sure the above methods no longer push the db change msg too...
        return existingMeter;
    }

    /**
     * Check if the deviceType of meter is different than the deviceType of the template meter 
     * If different types of meters, then the deviceType will be changed for meter.
     * If the types are not compatible, a MspErrorObjectException will be thrown.
     * @param templateMeter - the meter to compare to, this is the type of meter the calling system thinks we should have
     * @param existingMeter - the meter to update
     * @param mspVendor
     * @throws MspErrorObjectException when error changing the type
     * @return Returns the updated existingMeter object (in case of major paoType change) 
     */
    private YukonMeter verifyAndUpdateType(YukonMeter templateMeter, YukonMeter existingMeter, String serialOrAddress,
            String mspMethod, MultispeakVendor mspVendor) throws MspErrorObjectException {
        PaoType originalType = existingMeter.getPaoType();
        if (templateMeter.getPaoType() != originalType) {
            // PROBLEM, types do not match!
            // Attempt to change type
            try {
                existingMeter = updateDeviceType(templateMeter, existingMeter, serialOrAddress, mspMethod, mspVendor);
            } catch (ProcessingException | NumberFormatException e) {
                ErrorObject errorObject =
                    mspObjectDao.getErrorObject(existingMeter.getMeterNumber(), "Error: " + e.getMessage(), "Meter",
                        "ChangeDeviceType", mspVendor.getCompanyName());
                // return errorObject; couldn't save the change type
                throw new MspErrorObjectException(errorObject);
            }
        }
        return existingMeter;
    }

    /**
     * Update the (CIS) Substation Group.
     * If changed, update route (perform route locate).
     * If substationName is blank, do nothing.
     * @param meter - the meter to modify
     * @param mspVendor
     * @param mspMeter - the multispeak meter to process (if null, most likely will not change substation and routing info)
     *                  - See {@link #getSubstationNameFromMspObjects(Meter, ServiceLocation, MultispeakVendor)}
     * @param mspServiceLocation - the multispeak serviceLocation to process (will be lazy loaded if needed)
     *                  - See {@link #getSubstationNameFromMspObjects(Meter, ServiceLocation, MultispeakVendor)}
     */
    private void verifyAndUpdateSubstationGroupAndRoute(YukonMeter meterToUpdate, MultispeakVendor mspVendor,
            Meter mspMeter, ServiceLocation mspServiceLocation, String mspMethod) {

        String meterNumber = meterToUpdate.getMeterNumber();

        // Get substation name
        String substationName = getSubstationNameFromMspObjects(mspMeter, mspServiceLocation, mspVendor);
        // Validate, verify and update substationName
        checkAndUpdateSubstationName(substationName, meterNumber, mspMethod, mspVendor, meterToUpdate);
    }

    /**
     * Helper method to return a Meter object for PaoName PaoName is looked up
     * based on PaoNameAlias
     */
    private YukonMeter getMeterByPaoName(Meter mspMeter, MultispeakVendor mspVendor) {
        String paoName = getPaoNameFromMspMeter(mspMeter, mspVendor);
        return meterDao.getForPaoName(paoName);
    }

    @Override
    public List<ErrorObject> meterRemove(MultispeakVendor mspVendor, List<Meter> removeMeters) {
        ArrayList<ErrorObject> errorObjects = new ArrayList<>();

        for (Meter mspMeter : removeMeters) {
            if (mspMeter.getMeterNo() != null) {
                // Lookup meter in Yukon by msp meter number
                YukonMeter meter;
                try {
                    meter = getMeterByMeterNumber(mspMeter.getMeterNo().trim());
                    removeDeviceNameExtension(meter, METER_REMOVE_STRING, mspVendor);
                    removeDeviceFromCISGroups(meter, METER_REMOVE_STRING, mspVendor);
                    // Added meter to Inventory
                    addMeterToGroup(meter, SystemGroupEnum.INVENTORY, METER_REMOVE_STRING, mspVendor);
                    if (!meter.isDisabled()) {// enabled
                        meter.setDisabled(true); // update local object reference
                        deviceUpdateService.disableDevice(meter);
                        multispeakEventLogService.disableDevice(meter.getMeterNumber(), meter, METER_REMOVE_STRING,
                            mspVendor.getCompanyName());
                    }

                } catch (NotFoundException e) {
                    multispeakEventLogService.meterNotFound(mspMeter.getMeterNo(), METER_REMOVE_STRING,
                        mspVendor.getCompanyName());
                    ErrorObject err =
                        mspObjectDao.getNotFoundErrorObject(mspMeter.getMeterNo().trim(), "MeterNumber", "Meter",
                            METER_REMOVE_STRING, mspVendor.getCompanyName());
                    errorObjects.add(err);
                    multispeakEventLogService.errorObject(err.getErrorString(), METER_REMOVE_STRING,
                        mspVendor.getCompanyName());
                    log.error(e);
                }
            }
        }

        return errorObjects;
    }

    @Override
    public List<ErrorObject> serviceLocationChanged(final MultispeakVendor mspVendor, List<ServiceLocation> serviceLocations) {
        final ArrayList<ErrorObject> errorObjects = new ArrayList<>();
        final MspPaoNameAliasEnum paoAlias = multispeakFuncs.getPaoNameAlias();

        for (final ServiceLocation mspServiceLocation : serviceLocations) {
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {

                        if (paoAlias == MspPaoNameAliasEnum.SERVICE_LOCATION) {

                            String serviceLocationStr = mspServiceLocation.getObjectID();
                            List<YukonMeter> meters = searchForMetersByPaoName(serviceLocationStr);

                            if (meters.isEmpty()) {
                                multispeakEventLogService.meterNotFoundByPaoName(serviceLocationStr, SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                                ErrorObject err = mspObjectDao.getNotFoundErrorObject(serviceLocationStr, "ServiceLocation", "ServiceLocation",
                                                                                      SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                                errorObjects.add(err);
                                multispeakEventLogService.errorObject(err.getErrorString(), SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                            } else {
                                for (YukonMeter meter : meters) {
                                    if (!meter.isDisabled()) {  //check if disabled here, since we are searching Yukon for potential service location matches (which _could_ be disabled?)
                                        // update the billing group from CIS billingCyle
                                        String billingCycle = mspServiceLocation.getBillingCycle();
                                        updateBillingCyle(billingCycle, meter.getMeterNumber(), meter, SERV_LOC_CHANGED_STRING, mspVendor);
                                        updateAltGroup(mspServiceLocation, meter.getMeterNumber(), meter, SERV_LOC_CHANGED_STRING, mspVendor);
                                        
                                        updatePaoLocation(mspServiceLocation, meter, SERV_LOC_CHANGED_STRING);
                                        // using null for mspMeter. See comments in getSubstationNameFromMspMeter(...)
                                        verifyAndUpdateSubstationGroupAndRoute(meter, mspVendor, null, mspServiceLocation, SERV_LOC_CHANGED_STRING);
                                    } else {
                                        // TODO log something about meter not updated because disabled? Never did before in 6.0 either
                                    }
                                }
                            }
                        } else {
                            // Must get meters from MSP CB call to process.
                            List<Meter> mspMeters = mspObjectDao.getMspMetersByServiceLocation(mspServiceLocation,
                                                                                               mspVendor);

                            if (!mspMeters.isEmpty()) {

                                for (Meter mspMeter : mspMeters) {
                                    try {
                                        YukonMeter meter = getMeterByMeterNumber(mspMeter.getMeterNo().trim());
                                        
                                        // MeterNumber should not have changed, nor transponderId...only paoName possibly
                                        String newPaoName = getPaoNameFromMspMeter(mspMeter, mspVendor);
                                        verifyAndUpdatePaoName(newPaoName, meter, SERV_LOC_CHANGED_STRING, mspVendor);

                                        updateCISDeviceClassGroup(mspMeter.getMeterNo(), mspMeter.getDeviceClass(), meter, SERV_LOC_CHANGED_STRING, mspVendor);
                                        String billingCycle = mspServiceLocation.getBillingCycle();
                                        updateBillingCyle(billingCycle, meter.getMeterNumber(), meter, SERV_LOC_CHANGED_STRING, mspVendor);
                                        updateAltGroup(mspServiceLocation, meter.getMeterNumber(), meter, SERV_LOC_CHANGED_STRING, mspVendor);

                                        updatePaoLocation(mspServiceLocation, meter, SERV_LOC_CHANGED_STRING);
                                        verifyAndUpdateSubstationGroupAndRoute(meter, mspVendor, mspMeter, mspServiceLocation, METER_ADD_STRING);
                                    } catch (NotFoundException e) {
                                        multispeakEventLogService.meterNotFound(mspMeter.getMeterNo(), SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                                        ErrorObject err = mspObjectDao.getNotFoundErrorObject(mspMeter.getMeterNo(), "MeterNumber", "Meter", SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                                        errorObjects.add(err);
                                        multispeakEventLogService.errorObject(err.getErrorString(), SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                                        log.error(e);
                                    }
                                }
                            } else {
                                multispeakEventLogService.objectNotFoundByVendor(mspServiceLocation.getObjectID(), "GetMeterByServLoc", SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                                ErrorObject err = mspObjectDao.getErrorObject(mspServiceLocation.getObjectID(),
                                                                              paoAlias.getDisplayName() + " ServiceLocation(" + mspServiceLocation.getObjectID() + ") - No meters returned from vendor for location.",
                                                                              "ServiceLocation", SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                                errorObjects.add(err);
                                multispeakEventLogService.errorObject(err.getErrorString(), SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                            }
                        }
                    };
                });
            } catch (MspErrorObjectException e) {
                errorObjects.add(e.getErrorObject());
                multispeakEventLogService.errorObject(e.getErrorObject().getErrorString(), SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                log.error(e);
            } catch (RuntimeException ex) {
                // Transactional code threw application exception -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspServiceLocation.getObjectID(),
                                                              "X Exception: (ServLoc:" + mspServiceLocation.getObjectID() + ")-" + ex.getMessage(),
                                                              "ServiceLocation",
                                                              SERV_LOC_CHANGED_STRING,
                                                              mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.errorObject(err.getErrorString(), SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                log.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspServiceLocation.getObjectID(),
                                                              "X Error: (ServLoc:" + mspServiceLocation.getObjectID() + ")-" + ex.getMessage(),
                                                              "ServiceLocation",
                                                              SERV_LOC_CHANGED_STRING,
                                                              mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.errorObject(err.getErrorString(), SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                log.error(ex);
            }
        }
        return errorObjects;
    }

    @Override
    public List<ErrorObject> meterChanged(final MultispeakVendor mspVendor, List<Meter> changedMeters) throws MultispeakWebServiceException{
        final List<ErrorObject> errorObjects = new ArrayList<>();

        for (final Meter mspMeter : changedMeters) {
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {

                        validateMspMeter(mspMeter, mspVendor, METER_CHANGED_STRING);

                        try {
                            YukonMeter meterToChange = getMeterByMeterNumber(mspMeter.getMeterNo().trim());
                            YukonMeter templateMeter = getYukonMeterForTemplate(mspMeter, mspVendor, false); // throws MspErrorObjectException
                            if (templateMeter == null) {
                                // If no template found, just use this meter as the template (meaning, same meter, no type changes).
                                templateMeter = meterToChange;
                            }
                            meterToChange = updateExistingMeter(mspMeter, meterToChange, templateMeter, METER_CHANGED_STRING, mspVendor, false);

                            updatePaoLocation(mspMeter, meterToChange, METER_CHANGED_STRING);
                            updateCISDeviceClassGroup(mspMeter.getMeterNo(), mspMeter.getDeviceClass(), meterToChange, METER_CHANGED_STRING, mspVendor);

                            // using null for mspServiceLocation. See comments in getSubstationNameFromMspMeter(...)
                            verifyAndUpdateSubstationGroupAndRoute(meterToChange, mspVendor, mspMeter, null, SERV_LOC_CHANGED_STRING);
                        } catch (NotFoundException e) {
                            multispeakEventLogService.meterNotFound(mspMeter.getMeterNo(), METER_CHANGED_STRING, mspVendor.getCompanyName());
                            ErrorObject err = mspObjectDao.getNotFoundErrorObject(mspMeter.getObjectID(),
                                                                                  "MeterNumber: " + mspMeter.getMeterNo(),
                                                                                  "Meter", METER_CHANGED_STRING, mspVendor.getCompanyName());
                            errorObjects.add(err);
                            multispeakEventLogService.errorObject(err.getErrorString(), METER_CHANGED_STRING, mspVendor.getCompanyName());
                            log.error(e);
                        }
                    };
                });
            } catch (MspErrorObjectException e) {
                errorObjects.add(e.getErrorObject());
                multispeakEventLogService.errorObject(e.getErrorObject().getErrorString(), METER_CHANGED_STRING, mspVendor.getCompanyName());
                log.error(e);
            } catch (RuntimeException ex) {
                // Transactional code threw application exception -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getMeterNo(),
                                                              "X Exception: (MeterNo:" + mspMeter.getMeterNo() + ")-" + ex.getMessage(),
                                                              "Meter", METER_CHANGED_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.errorObject(err.getErrorString(), METER_CHANGED_STRING, mspVendor.getCompanyName());
                log.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getMeterNo(),
                                                              "X Error: (MeterNo:" + mspMeter.getMeterNo() + ")-" + ex.getMessage(),
                                                              "Meter", METER_CHANGED_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.errorObject(err.getErrorString(), METER_CHANGED_STRING, mspVendor.getCompanyName());
                log.error(ex);
            }
        }// end for

        return errorObjects;
    }

    @Override
    public List<ErrorObject> addMetersToGroup(MeterGroup meterGroup, String mspMethod, MultispeakVendor mspVendor) {

        List<ErrorObject> errorObjects = new ArrayList<>();
        if (meterGroup != null && meterGroup.getGroupName() != null && meterGroup.getMeterList() != null) {
            // Convert MeterNumbers to YukonDevices
            List<SimpleDevice> yukonDevices = new ArrayList<>();
            for (String meterNumber : CollectionUtils.emptyIfNull(meterGroup.getMeterList().getMeterID())) {
                try {
                    SimpleDevice yukonDevice = deviceDao.getYukonDeviceObjectByMeterNumber(meterNumber);
                    yukonDevices.add(yukonDevice);
                } catch (EmptyResultDataAccessException e) {
                    String exceptionMessage = "Unknown meter number " + meterNumber;
                    ErrorObject errorObject =
                        mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "addMetersToGroup",
                            mspVendor.getCompanyName(), exceptionMessage);
                    errorObjects.add(errorObject);
                    log.error(e);
                } catch (IncorrectResultSizeDataAccessException e) {
                    String exceptionMessage = "Duplicate meters were found for this meter number  " + meterNumber;
                    ErrorObject errorObject =
                        mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "addMetersToGroup",
                            mspVendor.getCompanyName(), exceptionMessage);
                    errorObjects.add(errorObject);
                    log.error(e);
                }
            }

            String groupName = meterGroup.getGroupName();
            StoredDeviceGroup storedGroup = deviceGroupEditorDao.getStoredGroup(groupName, true);
            deviceGroupMemberEditorDao.addDevices(storedGroup, yukonDevices);
            multispeakEventLogService.addMetersToGroup(yukonDevices.size(), storedGroup.getFullName(), mspMethod,
                mspVendor.getCompanyName());
        }
        return errorObjects;
    }

    @Override
    public List<ErrorObject> removeMetersFromGroup(String groupName, List<String> meterNumbers, MultispeakVendor mspVendor) {
        List<ErrorObject> errorObjects = new ArrayList<>();
        List<SimpleDevice> yukonDevices = new ArrayList<>();
        
        if (groupName!=null){
        try {
            StoredDeviceGroup storedGroup = deviceGroupEditorDao.getStoredGroup(groupName, false);
            for (String meterNumber : meterNumbers) {
                try {
                    SimpleDevice yukonDevice = deviceDao.getYukonDeviceObjectByMeterNumber(meterNumber);
                    yukonDevices.add(yukonDevice);
                } catch (EmptyResultDataAccessException e) {
                    String exceptionMessage = "Unknown meter number " + meterNumber;
                    ErrorObject errorObject =
                        mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter",
                            "removeMetersFromGroup", mspVendor.getCompanyName(), exceptionMessage);
                    errorObjects.add(errorObject);
                    log.error(e);
                } catch (IncorrectResultSizeDataAccessException e) {
                    String exceptionMessage = "Duplicate meters were found for this meter number  " + meterNumber;
                    ErrorObject errorObject =
                        mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter",
                            "removeMetersFromGroup", mspVendor.getCompanyName(), exceptionMessage);
                    errorObjects.add(errorObject);
                    log.error(e);
                }
            }

            deviceGroupMemberEditorDao.removeDevices(storedGroup, yukonDevices);
            multispeakEventLogService.removeMetersFromGroup(yukonDevices.size(), storedGroup.getFullName(),
                "RemoveMetersFromMeterGroup", mspVendor.getCompanyName());
        } catch (NotFoundException e) {
            ErrorObject errorObject =
                mspObjectDao.getNotFoundErrorObject(groupName, "GroupName", "MeterGroup", "removeMetersFromGroup",
                    mspVendor.getCompanyName());
            errorObjects.add(errorObject);
            log.error(e);
        }
        }
        return errorObjects;
    }

    @Override
    public ErrorObject deleteGroup(String groupName, MultispeakVendor mspVendor) {
        if (groupName != null) {
            try {
                StoredDeviceGroup storedGroup = deviceGroupEditorDao.getStoredGroup(groupName, false);
                deviceGroupEditorDao.removeGroup(storedGroup);
            } catch (NotFoundException e) {
                return mspObjectDao.getNotFoundErrorObject(groupName, "meterGroupId", "MeterGroup", "deleteGroup", mspVendor.getCompanyName());
            } catch (DeviceGroupInUseException e) {
                return mspObjectDao.getErrorObject(groupName, "Cannot delete group, it is currently in use.", "MeterGroup", "deleteGroup", mspVendor.getCompanyName());
            }
        }
        return new ErrorObject();
    }

    /**
     * Helper method to load extension value from extensionItems for
     * extensionName
     */
    private String getExtensionValue(ExtensionsList extensionsList, String extensionName, String defaultValue) {
        log.debug("Attempting to load extension value for key:" + extensionName);
        
        if (extensionsList != null) {
            for (ExtensionsItem eItem : extensionsList.getExtensionsItem()) {
                String extName = eItem.getExtName();
                if (extName.equalsIgnoreCase(extensionName)) {
                    return eItem.getExtValue();
                }
            }
        }
        log.warn("Extension " + extensionName + " key was not found. Returning default value: " + defaultValue);
        return defaultValue;
    }

    /**
     * Returns a Yukon PaoName (template) to model new meters after. If no value
     * is provided in the mspMeter object, then the defaultTemplateName is
     * returned.
     */
    private String getMeterTemplate(Meter mspMeter, String defaultTemplateName) {

        if (StringUtils.isNotBlank(mspMeter.getAMRDeviceType())) {
            return mspMeter.getAMRDeviceType();
        }

        return getExtensionValue(mspMeter.getExtensionsList(), EXTENSION_DEVICE_TEMPLATE_STRING, defaultTemplateName);
    }

    /**
     * Returns the value of the paoName alias extension field from Meter object.
     * If no value is provided in the Meter object, then null is returned. NOTE:
     * meterno - this extension will return mspMeter.meterNo directly.
     */
    private String getExtensionValue(Meter mspMeter) {

        boolean usesExtension = multispeakFuncs.usesPaoNameAliasExtension();

        if (usesExtension) {
            String extensionName = multispeakFuncs.getPaoNameAliasExtension();
            if (extensionName.equalsIgnoreCase("meterno")) { // specific field
                return mspMeter.getMeterNo();
            } else if (extensionName.equalsIgnoreCase("deviceclass")) { // specific field (WHE custom)
                return mspMeter.getDeviceClass();
            } else { // use extensions
                return getExtensionValue(mspMeter.getExtensionsList(), extensionName, null);
            }
        }
        return null;
    }

    /**
    * Updates an alternate device grouping.
    * The exact parent group to update is configured by MSP_ALTGROUP_EXTENSION.
    * This functionality was added specifically for DEMCO.
    */
    @Override
    public boolean updateAltGroup(ServiceLocation mspServiceLocation, String meterNumber, YukonDevice yukonDevice,
            String mspMethod, MultispeakVendor mspVendor) {
        boolean updateAltGroup = configurationSource.getBoolean(MasterConfigBoolean.MSP_ENABLE_ALTGROUP_EXTENSION);
        if (updateAltGroup) {
            String extensionName = configurationSource.getString(MasterConfigString.MSP_ALTGROUP_EXTENSION, "altGroup");
            String altGroup = getExtensionValue(mspServiceLocation.getExtensionsList(), extensionName, null);
            if (!StringUtils.isBlank(altGroup)) {

                // Remove from all alt group membership groups
                DeviceGroup altGroupDeviceGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.ALTERNATE);
                StoredDeviceGroup deviceGroupParent = deviceGroupEditorDao.getStoredGroup(altGroupDeviceGroup);
                return updatePrefixGroup(altGroup, meterNumber, yukonDevice, mspMethod, mspVendor, deviceGroupParent);
            }
        }
        return false;
    }

    /**
     * Update the paolocation coordinates based on METER object.
     */
    private void updatePaoLocation(Meter mspMeter, YukonMeter meterToUpdate, String mspMethod) {
        MultispeakManagePaoLocation managePaoLocation = globalSettingDao.getEnum(GlobalSettingType.MSP_MANAGE_PAO_LOCATION, MultispeakManagePaoLocation.class);
        if (managePaoLocation == MultispeakManagePaoLocation.METER) {
            if (mspMeter.getUtilityInfo() != null && mspMeter.getUtilityInfo().getMapLocation() != null &&
                mspMeter.getUtilityInfo().getMapLocation().getCoord() != null) {
                BigDecimal longitude = mspMeter.getUtilityInfo().getMapLocation().getCoord().getX();
                BigDecimal latitude = mspMeter.getUtilityInfo().getMapLocation().getCoord().getY();
                if (longitude != null && latitude != null) {
                    PaoLocation paoLocation = new PaoLocation(meterToUpdate.getPaoIdentifier(), latitude.doubleValue(), longitude.doubleValue(),
                                                              Origin.MULTISPEAK, Instant.now());
                    updatePaoLocation(meterToUpdate.getMeterNumber(), meterToUpdate.getName(), paoLocation);
                }
            }
        }
    }
    
    /**
     * Update the paolocation coordinates based on SERVICELOCATION object.
     */
    private void updatePaoLocation(ServiceLocation mspServiceLocation, YukonMeter meterToUpdate, String mspMethod) {
        MultispeakManagePaoLocation managePaoLocation = globalSettingDao.getEnum(GlobalSettingType.MSP_MANAGE_PAO_LOCATION, MultispeakManagePaoLocation.class);
        if (managePaoLocation == MultispeakManagePaoLocation.SERVICE_LOCATION) {
            if (mspServiceLocation.getGPS() != null) {
                Double longitude = mspServiceLocation.getGPS().getLongitude();
                Double latitude = mspServiceLocation.getGPS().getLatitude();
                if (longitude != null && latitude != null) {
                    PaoLocation paoLocation = new PaoLocation(meterToUpdate.getPaoIdentifier(), latitude.doubleValue(), longitude.doubleValue(),
                                                              Origin.MULTISPEAK, Instant.now());
                    updatePaoLocation(meterToUpdate.getMeterNumber(), meterToUpdate.getName(), paoLocation);
                    return;
                }
            }
            if (mspServiceLocation.getMapLocation() != null &&
                mspServiceLocation.getMapLocation().getCoord() != null) {
                BigDecimal longitude = mspServiceLocation.getMapLocation().getCoord().getX();
                BigDecimal latitude = mspServiceLocation.getMapLocation().getCoord().getY();
                if (longitude != null && latitude != null) {
                    PaoLocation paoLocation = new PaoLocation(meterToUpdate.getPaoIdentifier(), latitude.doubleValue(), longitude.doubleValue(),
                                                              Origin.MULTISPEAK, Instant.now());
                    updatePaoLocation(meterToUpdate.getMeterNumber(), meterToUpdate.getName(), paoLocation);
                }
            }
        }
    }
    /**
     * Helper method to add meterNos to systemGroup
     */
    private List<ErrorObject> addMetersToGroup(List<String> meterNos, SystemGroupEnum systemGroup,
            String mspMethod, MultispeakVendor mspVendor) {
        return addToGroupAndDisable(meterNos, systemGroup, mspMethod, mspVendor, false);
    }

    /**
     * Helper method to add meterNos to systemGroup
     * @param disable - when true, the meter will be disabled. Else no change.
     */
    private List<ErrorObject> addToGroupAndDisable(List<String> meterNos, SystemGroupEnum systemGroup, String mspMethod,
            MultispeakVendor mspVendor, boolean disable) {

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
                ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber,
                                                                      "MeterNumber",
                                                                      "Meter",
                                                                      "addToGroup",
                                                                      mspVendor.getCompanyName(),
                                                                      e.getMessage());
                errorObjects.add(err);
                log.error(e);
            }
        }
        return errorObjects;
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
                                                                      "Meter",
                                                                      "removeFromGroup",
                                                                      mspVendor.getCompanyName());
                errorObjects.add(err);
                log.error(e);
            }
        }

        return errorObjects;
    }

    /**
     * Returns the PaoName alias value for the MultiSpeak mspMeter object. 
     * @throws InsufficientMultiSpeakDataException - when paoName not found (null)
     */
    private String getPaoNameFromMspMeter(Meter mspMeter, MultispeakVendor mspVendor) {

        String paoName = null;
        final MspPaoNameAliasEnum paoAlias = multispeakFuncs.getPaoNameAlias();
        if (paoAlias == MspPaoNameAliasEnum.ACCOUNT_NUMBER) {
            if (mspMeter.getUtilityInfo() != null && StringUtils.isNotBlank(mspMeter.getUtilityInfo().getAccountNumber())) {
                paoName = mspMeter.getUtilityInfo().getAccountNumber();
            }
        } else if (paoAlias == MspPaoNameAliasEnum.SERVICE_LOCATION) {
            if (mspMeter.getUtilityInfo() != null && StringUtils.isNotBlank(mspMeter.getUtilityInfo().getServLoc())) {
                paoName = mspMeter.getUtilityInfo().getServLoc();
            }
        } else if (paoAlias == MspPaoNameAliasEnum.CUSTOMER_ID) {
            if (mspMeter.getUtilityInfo() != null && StringUtils.isNotBlank(mspMeter.getUtilityInfo().getCustID())) {
                paoName = mspMeter.getUtilityInfo().getCustID();
            }
        } else if (paoAlias == MspPaoNameAliasEnum.EA_LOCATION) {
            if (mspMeter.getUtilityInfo() != null && mspMeter.getUtilityInfo().getEaLoc() != null && 
                    StringUtils.isNotBlank(mspMeter.getUtilityInfo().getEaLoc().getName())) {
                paoName = mspMeter.getUtilityInfo().getEaLoc().getName();
            }
        } else if (paoAlias == MspPaoNameAliasEnum.GRID_LOCATION) {
            if (StringUtils.isNotBlank(mspMeter.getFacilityID())) {
                paoName = mspMeter.getFacilityID();
            }
        } else if (paoAlias == MspPaoNameAliasEnum.METER_NUMBER) {
            if (StringUtils.isNotBlank(mspMeter.getMeterNo())) {
                paoName = mspMeter.getMeterNo();
            }
        } else if (paoAlias == MspPaoNameAliasEnum.POLE_NUMBER) {
            if (StringUtils.isNotBlank(mspMeter.getMeterNo())) {
                ServiceLocation mspServiceLocation = mspObjectDao.getMspServiceLocation(mspMeter.getMeterNo(), mspVendor);
                if (mspServiceLocation.getNetwork() != null && 
                        StringUtils.isNotBlank(mspServiceLocation.getNetwork().getPoleNo())) {
                    paoName = mspServiceLocation.getNetwork().getPoleNo();
                }
            }
        }

        if (paoName == null) {
            throw new InsufficientMultiSpeakDataException("Message does not contain sufficient data for Yukon Device Name.");
        }

        String extensionValue = getExtensionValue(mspMeter);
        return multispeakFuncs.buildAliasWithQuantifier(paoName, extensionValue, mspVendor);

    }

    /**
     * Return the substation name of a Meter. If meter is null, return empty
     * string. If MSP_ENABLE_SUBSTATIONNAME_EXTENSION cparm is set, then attempt
     * to return from mspMeter extensions. If mspMeter returns nothing, then
     * call mspVendor to load the related ServiceLocation. Attempt to return
     * from mspServiceLocation extensions. If ServiceLocation extensions return
     * nothing, then return null. If MSP_ENABLE_SUBSTATIONNAME_EXTENSION cparm
     * is NOT set, use normal loading from Meter object. Normal loading: If the
     * Meter does not contain a substation name in its utility info, return
     * null.
     * @param ServiceLocation mspServiceLocation - will be lazy loaded (if
     *            needed) and null passed in.
     * @return String substationName
     */
    private String getSubstationNameFromMspObjects(Meter mspMeter, ServiceLocation mspServiceLocation,
            MultispeakVendor mspVendor) {

        boolean useExtension = configurationSource.getBoolean(MasterConfigBoolean.MSP_ENABLE_SUBSTATIONNAME_EXTENSION);
        if (useExtension) { // custom for DEMCO/SEDC integration
            String extensionName = configurationSource.getString(MasterConfigString.MSP_SUBSTATIONNAME_EXTENSION, "readPath");
            String extensionValue;

            if (mspMeter != null) {
                log.debug("Attempting to load extension value for substation name from multispeak _meter_.");
                extensionValue = getExtensionValue(mspMeter.getExtensionsList(), extensionName, null);
                if (StringUtils.isNotBlank(extensionValue)) {
                    return extensionValue;
                }

                log.debug("Not found in meter. Attempting to load extension value for substation name from multispeak _serviceLocation_.");
                if (mspServiceLocation == null) {
                    log.debug("Calling CB to load ServiceLocation for Meter.");
                    mspServiceLocation = mspObjectDao.getMspServiceLocation(mspMeter.getMeterNo(), mspVendor);
                }
            }

            if (mspServiceLocation != null) {
                extensionValue = getExtensionValue(mspServiceLocation.getExtensionsList(), extensionName, null);
                if (StringUtils.isNotBlank(extensionValue)) {
                    return extensionValue;
                }
            }

            log.debug("Extension value for substation name NOT found for meter or service location, returning empty substationName.");
            return "";

        } else { // Normal loading
            if (mspMeter == null || mspMeter.getUtilityInfo() == null || StringUtils.isBlank(mspMeter.getUtilityInfo().getSubstationName())) {
                return null;
            } else {
                return mspMeter.getUtilityInfo().getSubstationName();
            }
        }
    }

    /**
     * Helper method to search devices by PaoName for filterValue. Performs
     * (starts with) search on PaoName. If MSP_EXACT_SEARCH_PAONAME is set, then
     * an exact lookup of paoName for fitlerValue is performed.
     */
    private List<YukonMeter> searchForMetersByPaoName(String filterValue) {

        boolean exactSearch = configurationSource.getBoolean(MasterConfigBoolean.MSP_EXACT_SEARCH_PAONAME);
        List<YukonMeter> meters = Lists.newArrayList();
        if (exactSearch) {
            YukonMeter meter = meterDao.findForPaoName(filterValue);
            if (meter != null) {
                meters.add(meter);
            }
        } else {
            List<FilterBy> searchFilter = new ArrayList<>(1);
            FilterBy filterBy = new StandardFilterBy("deviceName", MeterSearchField.PAONAME);
            filterBy.setFilterValue(filterValue);
            searchFilter.add(filterBy);
            MeterSearchOrderBy orderBy = new MeterSearchOrderBy(MeterSearchField.PAONAME.toString(), true);
            SearchResults<YukonMeter> result = meterSearchDao.search(searchFilter, orderBy, 0, 25);
            meters.addAll(result.getResultList());
        }
        return meters;
    }

    /**
     * Returns a YukonMeter object, looked up by a templateName provided by mspMeter.
     * If templateName not found on mspMeter, then the vendor's default templateName will be used.
     * If useDefault is true, then the default template name will be used if AMRDeviceType not provided, otherwise return null.
     * @throws MspErrorObjectException when meter not found in Yukon by templateName provided
     */
    private YukonMeter getYukonMeterForTemplate(Meter mspMeter, MultispeakVendor mspVendor, boolean useDefault)
            throws MspErrorObjectException {

        String defaultTemplateName = useDefault ? mspVendor.getTemplateNameDefault() : null;
        String templateName = getMeterTemplate(mspMeter, defaultTemplateName);
        if (templateName != null) {
            YukonMeter templateMeter;
            try {
                templateMeter = meterDao.getForPaoName(templateName);
            } catch (NotFoundException e) {
                // template not found...now what? ERROR?
                ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getObjectID(),
                                                              "Error: Meter (" + mspMeter.getMeterNo() + ") - does not contain a valid template meter: Template[" + templateName + "]. Processing could not be completed, returning ErrorObject to calling vendor for processing.",
                                                              "Meter", METER_ADD_STRING, mspVendor.getCompanyName());
                log.error(e);
                throw new MspErrorObjectException(err);
            }
            return templateMeter;
        }
        return null;
    }

    /**
     * Returns ErrorObject when mspMeter is not valid. Returns null when mspMeter is valid. 
     * Validates 
     *  1) Meter.MeterNo field is not blank. 
     *  2) Meter.Nameplate is not null AND Meter.Nameplate.TransponderId is not blank
     */
    private void validateMspMeter(Meter mspMeter, MultispeakVendor mspVendor, String method)
            throws MspErrorObjectException {

        // Check for valid MeterNo
        if (StringUtils.isBlank(mspMeter.getMeterNo())) {

            ErrorObject errorObject = mspObjectDao.getErrorObject(mspMeter.getMeterNo(),
                                                                  "Error: MeterNo is invalid (empty or null).  No updates were made.",
                                                                  "Meter",
                                                                  method,
                                                                  mspVendor.getCompanyName());
            throw new MspErrorObjectException(errorObject);
        }

        // Check for valid TransponderID (PLC meters)
        if (mspMeter.getNameplate() == null || StringUtils.isBlank(mspMeter.getNameplate().getTransponderID())) {

            ErrorObject errorObject = mspObjectDao.getErrorObject(mspMeter.getMeterNo(),
                                                                  "Error: MeterNumber(" + mspMeter.getMeterNo() + ") - SerialNumber nor TransponderId are valid.  No updates were made.",
                                                                  "Meter",
                                                                  method,
                                                                  mspVendor.getCompanyName());
            throw new MspErrorObjectException(errorObject);
        }
    }

    /**
     * Extra SystemLog logging that will be completely removed upon completion of MultiSpeak EventLogs.
     * Only use this method if you intend for the logging to be removed with EventLogs completion.
     */
    @Deprecated
    private void systemLog(String mspMethod, String description, MultispeakVendor mspVendor) {
        mspObjectDao.logMSPActivity(mspMethod, description, mspVendor.getCompanyName());
    }

    @Required
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }
}