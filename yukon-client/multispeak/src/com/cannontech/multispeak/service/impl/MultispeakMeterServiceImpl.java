package com.cannontech.multispeak.service.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadCallback;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.deviceread.dao.WaitableDeviceAttributeReadCallback;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.meter.search.dao.MeterSearchDao;
import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.MeterSearchField;
import com.cannontech.amr.meter.search.model.MeterSearchOrderBy;
import com.cannontech.amr.meter.search.model.StandardFilterBy;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectStatusType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectCallback;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.service.ChangeDeviceTypeService;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.config.MasterConfigHelper;
import com.cannontech.common.config.MasterConfigStringKeysEnum;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.impl.CommandCallbackBase;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.exception.InsufficientMultiSpeakDataException;
import com.cannontech.common.model.Route;
import com.cannontech.common.model.Substation;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.roleproperties.MspPaoNameAliasEnum;
import com.cannontech.core.roleproperties.MultispeakMeterLookupFieldEnum;
import com.cannontech.core.substation.dao.SubstationDao;
import com.cannontech.core.substation.dao.SubstationToRouteMappingDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.point.stategroup.Disconnect410State;
import com.cannontech.database.db.point.stategroup.RfnDisconnectStatusState;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
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
import com.cannontech.multispeak.deploy.service.CB_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.CB_ServerSoap_PortType;
import com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.ExtensionsItem;
import com.cannontech.multispeak.deploy.service.FormattedBlock;
import com.cannontech.multispeak.deploy.service.LoadActionCode;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.MeterGroup;
import com.cannontech.multispeak.deploy.service.MeterRead;
import com.cannontech.multispeak.deploy.service.OutageEventType;
import com.cannontech.multispeak.deploy.service.ServiceLocation;
import com.cannontech.multispeak.deploy.service.impl.MultispeakPortFactory;
import com.cannontech.multispeak.event.MeterReadEvent;
import com.cannontech.multispeak.event.MultispeakEvent;
import com.cannontech.multispeak.event.ODEvent;
import com.cannontech.multispeak.service.MultispeakMeterService;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.BasicServerConnection;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.SetMultimap;

/**
 * @author stacey To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MultispeakMeterServiceImpl implements MultispeakMeterService, MessageListener {

    private static final Logger log = YukonLogManager.getLogger(MultispeakMeterServiceImpl.class);

    private BasicServerConnection porterConnection;
    @Autowired private AttributeService attributeService;
    @Autowired private ChangeDeviceTypeService changeDeviceTypeService;
    @Autowired private CommandRequestDeviceExecutor commandRequestDeviceExecutor;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    @Autowired private DeviceCreationService deviceCreationService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceUpdateService deviceUpdateService;
    @Autowired private MeterDao meterDao;
    @Autowired private MeterReadProcessingService meterReadProcessingService;
    @Autowired private MeterSearchDao meterSearchDao;
    @Autowired private MspMeterDao mspMeterDao;
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PointDao pointDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnMeterDisconnectService rfnMeterDisconnectService;
    @Autowired private SubstationDao substationDao;
    @Autowired private SubstationToRouteMappingDao substationToRouteMappingDao;
    @Autowired private TransactionTemplate transactionTemplate;

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
        builder.putAll(OutageEventType.Outage, 20, 57, 72);
        builder.putAll(OutageEventType.Restoration, 1, 17, 74, 0);
        ImmutableMultimap<OutageEventType, Integer> systemDefault = builder.build();

        supportedEventTypes = ImmutableSet.of(OutageEventType.Outage,
                                              OutageEventType.NoResponse,
                                              OutageEventType.Restoration,
                                              OutageEventType.PowerOff,
                                              OutageEventType.PowerOn,
                                              OutageEventType.Instantaneous,
                                              OutageEventType.Inferred);

        SetMultimap<OutageEventType, Integer> outageConfigTemp = HashMultimap.create(systemDefault);
        for (OutageEventType eventType : supportedEventTypes) {
            String valueStr = configurationSource.getString("MSP_OUTAGE_EVENT_TYPE_CONFIG_" + eventType.getValue().toUpperCase());
            if (valueStr != null) {
                int[] errorCodes = com.cannontech.common.util.StringUtils.parseIntStringAfterRemovingWhitespace(valueStr);
                List<Integer> errorCodeList = Arrays.asList(ArrayUtils.toObject(errorCodes));
                outageConfigTemp.values().removeAll(errorCodeList);
                outageConfigTemp.putAll(eventType, errorCodeList);
            }
        }

        outageConfig = ImmutableSetMultimap.copyOf(outageConfigTemp);
        log.info("outage event configuation: " + outageConfig);
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
    public LoadActionCode CDMeterState(final MultispeakVendor mspVendor, final YukonMeter meter) throws RemoteException {

        log.info("Received " + meter.getMeterNumber() + " for CDMeterState from " + mspVendor.getCompanyName());
        if (!porterConnection.isValid()) {
            throw new RemoteException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
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
            public void receivedLastValue(PaoIdentifier pao) {
                log.debug("deviceAttributeReadCallback.receivedLastValue for cdEvent");
            }

            @Override
            public void receivedError(PaoIdentifier pao, DeviceAttributeReadError error) {
                // do we need to send something to the foreign system here?
                log.warn("received error for " + pao + ": " + error);
            }

            @Override
            public void receivedException(DeviceAttributeReadError error) {
                log.warn("received exception in meterReadEvent callback: " + error);
            }

        };
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
        private LoadActionCode loadActionCode = LoadActionCode.Unknown;

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
    public MeterRead getLatestReadingInterrogate(MultispeakVendor mspVendor, YukonMeter meter, String transactionID,
            String responseUrl) {
        long id = generateMessageID();
        MeterReadEvent event = new MeterReadEvent(mspVendor, id, meter, transactionID, responseUrl);

        getEventsMap().put(new Long(id), event);
        
        String commandStr = "getvalue kwh";
        if (DeviceTypesFuncs.isMCT4XX(meter.getPaoIdentifier().getPaoType())) {
            commandStr = "getvalue peak"; // getvalue peak returns the peak kW and the total kWh
        }

        final String meterNumber = meter.getMeterNumber();
        log.info("Received " + meterNumber + " for LatestReadingInterrogate from " + mspVendor.getCompanyName());
        writePilRequest(meter, commandStr, id, 13);
        mspObjectDao.logMSPActivity("getLatestReadingByMeterNo",
                                    "(ID:" + meter.getPaoIdentifier().getPaoId() + ") MeterNumber (" + meterNumber + ") - " + commandStr,
                                    mspVendor.getCompanyName());

        synchronized (event) {
            boolean timeout = !waitOnEvent(event, mspVendor.getRequestMessageTimeout());
            if (timeout) {
                mspObjectDao.logMSPActivity("getLatestReadingByMeterNo",
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
    public synchronized ErrorObject[] odEvent(MultispeakVendor vendor, String[] meterNumbers, String transactionID,
            String responseUrl) throws RemoteException {
        
        if (vendor.getUrl() == null || vendor.getUrl().equalsIgnoreCase(CtiUtilities.STRING_NONE)) {
            throw new RemoteException("OMS vendor unknown.  Please contact Yukon administrator to set the Multispeak Vendor Role Property value in Yukon.");
        }
        if (!porterConnection.isValid()) {
            throw new RemoteException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
        }

        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        log.info("Received " + meterNumbers.length + " Meter(s) for Outage Verification Testing from " + vendor.getCompanyName());

        for (String meterNumber : meterNumbers) {

            try {
                YukonMeter meter = mspMeterDao.getMeterForMeterNumber(meterNumber);

                // Validate meter can receive porter command requests and command string exists, then perform action
                // This is limiting us to PLC support only for this request.
                boolean canInitiatePorterRequest = paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.PORTER_COMMAND_REQUESTS);
                if (!canInitiatePorterRequest) {
                    ErrorObject err = mspObjectDao.getErrorObject(meterNumber,
                                                                  "MeterNumber (" + meterNumber + ") - Meter cannot receive requests from porter.",
                                                                  "Meter",
                                                                  "ODEvent",
                                                                  vendor.getCompanyName());
                    errorObjects.add(err);
                } else { // do archaic way
                    long id = generateMessageID();
                    ODEvent event = new ODEvent(vendor, id, transactionID, responseUrl);
                    getEventsMap().put(new Long(id), event);
                    writePilRequest(meter, "ping", id, 13);
                }

            } catch (NotFoundException e) {
                ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "ODEvent", vendor.getCompanyName());
                errorObjects.add(err);
            }
        }

        return mspObjectDao.toErrorObject(errorObjects);
    }

    @Override
    public synchronized ErrorObject[] meterReadEvent(final MultispeakVendor vendor, String[] meterNumbers,
            final String transactionID, final String responseUrl) {
        
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();

        log.info("Received " + meterNumbers.length + " Meter(s) for MeterReading from " + vendor.getCompanyName());

        final CB_ServerSoap_PortType port = MultispeakPortFactory.getCB_ServerPort(vendor, responseUrl);
        if (port == null) {
            log.error("Port not found for CB_MR (" + vendor.getCompanyName() + ")");
            return new ErrorObject[0]; // return without any processing, since we have no one to send to when we're done anyways.
        }

        List<YukonMeter> allPaosToRead = Lists.newArrayListWithCapacity(meterNumbers.length);
        for (String meterNumber : meterNumbers) {

            try {
                YukonMeter meter = mspMeterDao.getMeterForMeterNumber(meterNumber); // TODO probably need a better load method
                allPaosToRead.add(meter);
            } catch (NotFoundException e) {
                ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "MeterReadEvent", vendor.getCompanyName());
                errorObjects.add(err);
            }
        }

        final ImmutableMap<PaoIdentifier, YukonMeter> meterLookup = PaoUtils.indexYukonPaos(allPaosToRead);

        final EnumSet<BuiltInAttribute> attributes = EnumSet.of(BuiltInAttribute.USAGE, BuiltInAttribute.PEAK_DEMAND);

        final ConcurrentMap<PaoIdentifier, MeterReadUpdater> updaterMap = new MapMaker().concurrencyLevel(2).initialCapacity(meterNumbers.length).makeMap();

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
                        if (success) break;
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
            public void receivedLastValue(PaoIdentifier pao) {
                YukonMeter meter = meterLookup.get(pao);
                MeterRead meterRead = meterReadProcessingService.createMeterRead(meter);

                // because we were so careful about putting updater or updater chains into the
                // map, we know we can safely remove it and generate a MeterRead from it
                // whenever we want; but this happens to be a perfect time
                MeterReadUpdater updater = updaterMap.remove(pao);
                if (updater != null) {
                    updater.update(meterRead);

                    MeterRead[] meterReads = new MeterRead[] { meterRead };
                    try {
                        log.info("Sending ReadingChangedNotification (" + responseUrl + "): Meter Number " + meterRead.getObjectID());
                        ErrorObject[] errObjects = port.readingChangedNotification(meterReads, transactionID);
                        if (!ArrayUtils.isEmpty(errObjects)) {
                            multispeakFuncs.logErrorObjects(responseUrl, "ReadingChangedNotification", errObjects);
                        }
                    } catch (RemoteException e) {
                        log.warn("caught exception in receivedValue of meterReadEvent", e);
                    }
                } else {
                    log.info("No matching attribute to point mappings identified. No notification message triggered.");
                }
            }

            @Override
            public void receivedError(PaoIdentifier pao, DeviceAttributeReadError error) {
                // do we need to send something to the foreign system here?
                log.warn("received error for " + pao + ": " + error);
            }

            @Override
            public void receivedException(DeviceAttributeReadError error) {
                log.warn("received exception in meterReadEvent callback: " + error);
            }
        };
        deviceAttributeReadService.initiateRead(allPaosToRead, attributes, callback, DeviceRequestType.MULTISPEAK_METER_READ_EVENT, UserUtils.getYukonUser());

        return mspObjectDao.toErrorObject(errorObjects);
    }

    @Override
    public synchronized ErrorObject[] blockMeterReadEvent(final MultispeakVendor vendor, String meterNumber,
            final FormattedBlockProcessingService<Block> blockProcessingService, final String transactionId,
            final String responseUrl) {
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();

        log.info("Received " + meterNumber + " for BlockMeterReading from " + vendor.getCompanyName());

        final CB_ServerSoap_PortType port = MultispeakPortFactory.getCB_ServerPort(vendor, responseUrl);
        if (port == null) {
            log.error("Port not found for CB_Server (" + vendor.getCompanyName() + ")");
            return new ErrorObject[0]; // return without any processing, since we have no one to send to when we're done anyways.
        }

        final YukonMeter paoToRead;
        try {
            paoToRead = mspMeterDao.getMeterForMeterNumber(meterNumber);
        } catch (NotFoundException e) {
            ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "MeterReadEvent", vendor.getCompanyName());
            errorObjects.add(err);
            return mspObjectDao.toErrorObject(errorObjects);
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

                FormattedBlock formattedBlock = blockProcessingService.createMspFormattedBlock(block);

                try {
                    ErrorObject[] errObjects = port.formattedBlockNotification(formattedBlock, transactionId, "errorString?");
                    if (!ArrayUtils.isEmpty(errObjects)) {
                        multispeakFuncs.logErrorObjects(responseUrl, "FormattedBlockNotification", errObjects);
                    }
                } catch (RemoteException e) {
                    log.warn("caught exception in receivedValue of formattedBlockEvent", e);
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
                        FormattedBlockUpdaterChain<Block> chain = new FormattedBlockUpdaterChain<Block>(oldValue, formattedBlockUpdater);
                        boolean success = updaterMap.replace(pao, oldValue, chain);
                        if (success) break;
                        oldValue = updaterMap.putIfAbsent(pao, formattedBlockUpdater);
                    }
                }
            }

            @Override
            public void receivedLastValue(PaoIdentifier pao) {
                log.debug("deviceAttributeReadCallback.receivedLastValue for formattedBlockEvent");
            }

            @Override
            public void receivedError(PaoIdentifier pao, DeviceAttributeReadError error) {
                // do we need to send something to the foreign system here?
                log.warn("received error for " + pao + ": " + error);
            }

            @Override
            public void receivedException(DeviceAttributeReadError error) {
                log.warn("received exception in FormattedBlockEvent callback: " + error);
            }
        };
        deviceAttributeReadService.initiateRead(Collections.singleton(paoToRead), attributes, callback, DeviceRequestType.MULTISPEAK_FORMATTED_BLOCK_READ_EVENT, UserUtils.getYukonUser());
        return mspObjectDao.toErrorObject(errorObjects);
    }

    @Override
    public synchronized ErrorObject[] cdEvent(final MultispeakVendor vendor,
            ConnectDisconnectEvent[] cdEvents, final String transactionId, final String responseUrl)
            throws RemoteException {

        if (!porterConnection.isValid()) {
            throw new RemoteException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
        }

        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        log.info("Received " + cdEvents.length + " Meter(s) for Connect/Disconnect from " + vendor.getCompanyName());
        List<CommandRequestDevice> plcCommandRequests = Lists.newArrayList();

        for (ConnectDisconnectEvent cdEvent : cdEvents) {
            final String meterNumber = getMeterNumberFromCDEvent(cdEvent);

            try {
                YukonMeter meter = mspMeterDao.getMeterForMeterNumber(meterNumber);
                MspLoadActionCode mspLoadActionCode = MspLoadActionCode.getForLoadActionCode(cdEvent.getLoadActionCode());

                // validate is CD supported meter
                if (!mspMeterDao.isCDSupportedMeter(meterNumber)) {
                    ErrorObject err = mspObjectDao.getErrorObject(meterNumber,
                                                                  "MeterNumber (" + meterNumber + ") - Invalid Yukon Connect/Disconnect Meter.",
                                                                  "Meter", "CDEvent", vendor.getCompanyName());
                    errorObjects.add(err);
                    continue;
                }

                // check for rf disconnect meter type and perform action
                boolean isRfnDisconnect = paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.DISCONNECT_RFN);
                if (isRfnDisconnect) {
                    RfnMeter rfnMeter = (RfnMeter) meter;
                    doRfnConnectDisconnect(rfnMeter, mspLoadActionCode.getRfnState().getType(), vendor, transactionId, responseUrl);
                    continue;
                }

                // Assume plc if we made it this far, validate meter can receive porter command requests and command string exists, then perform action
                boolean canInitiatePorterRequest = paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.PORTER_COMMAND_REQUESTS);
                if (!canInitiatePorterRequest || StringUtils.isBlank(mspLoadActionCode.getPlcCommandString())) {
                    ErrorObject err = mspObjectDao.getErrorObject(meterNumber,
                                                                  "MeterNumber (" + meterNumber + ") - Meter cannot receive requests from porter or no control command exists. " + 
                                                                          "LoadActionCode=" + cdEvent.getLoadActionCode(),
                                                                  "Meter", "CDEvent", vendor.getCompanyName());
                    errorObjects.add(err);
                } else { // build up a list of plc command requests (to be sent later)
                    CommandRequestDevice request = new CommandRequestDevice();
                    request.setDevice(new SimpleDevice(meter));
                    request.setCommandCallback(new CommandCallbackBase(mspLoadActionCode.getPlcCommandString()));
                    plcCommandRequests.add(request);
                    mspObjectDao.logMSPActivity("initiateConnectDisconnect",
                                                "(ID:" + meter.getPaoIdentifier().getPaoId() + ") MeterNumber (" + meterNumber + ") - " + 
                                                        mspLoadActionCode.getPlcCommandString() + 
                                                        ((cdEvent.getReasonCode() != null) ? " sent for ReasonCode: " + cdEvent.getReasonCode().getValue() : ""),
                                                        vendor.getCompanyName());
                }
            } catch (NotFoundException e) {
                ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "CDEvent", vendor.getCompanyName());
                errorObjects.add(err);
            }

        }

        // perform plc action on list of commandRequests
        doPlcConnectDisconnect(plcCommandRequests, vendor, transactionId, responseUrl);

        return mspObjectDao.toErrorObject(errorObjects);
    }

    /**
     * Performs the PLC meter disconnect. 
     * Returns immediately, does not wait for a response. 
     * Callback will initiate a cdEventNotification on receivedValue.
     */
    private void doPlcConnectDisconnect(List<CommandRequestDevice> plcCommandRequests, final MultispeakVendor vendor,
            final String transactionId, final String responseUrl) {
        
        YukonUserContext yukonUserContext = YukonUserContext.system;

        CommandCompletionCallbackAdapter<CommandRequestDevice> callback = new CommandCompletionCallbackAdapter<CommandRequestDevice>() {

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
                sendCDEventNotification(yukonMeter, mspLoadActionCode.getLoadActionCode(), vendor, transactionId, responseUrl);
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
            public void processingExceptionOccured(String reason) {
                log.warn("processingExceptionOccured for cdEvent " + reason);
            }
        };

        commandRequestDeviceExecutor.execute(plcCommandRequests, callback, DeviceRequestType.GROUP_CONNECT_DISCONNECT, yukonUserContext.getYukonUser());
    }

    /**
     * Performs the RFN meter disconnect. 
     * Returns immediately, does not wait for a response. 
     * Callback will initiate a cdEventNotification on success or error.
     */
    private void doRfnConnectDisconnect(final RfnMeter meter, RfnMeterDisconnectStatusType action,
            final MultispeakVendor vendor, final String transactionId, final String responseUrl) {

        RfnMeterDisconnectCallback rfnCallback = new RfnMeterDisconnectCallback() {

            @Override
            public void receivedSuccess(RfnMeterDisconnectState state, PointValueQualityHolder pointData) {
                log.debug("rfn receivedSuccess for cdEvent " + state);
                MspLoadActionCode mspLoadActionCode = MspLoadActionCode.getForRfnState(RfnDisconnectStatusState.getForNmState(state));
                sendCDEventNotification(meter, mspLoadActionCode.getLoadActionCode(), vendor, transactionId, responseUrl);
            }

            @Override
            public void receivedError(MessageSourceResolvable message, RfnMeterDisconnectState state) {
                log.warn("rfn receivedError for cdEvent " + message);
                sendCDEventNotification(meter, LoadActionCode.Unknown, vendor, transactionId, responseUrl);
            }

            @Override
            public void processingExceptionOccured(MessageSourceResolvable message) {
                log.warn("rfn processingExceptionOccured for cdEvent " + message);
            }

            @Override
            public void complete() {
                log.debug("rfn complete for cdEvent");
            }
        };

        rfnMeterDisconnectService.send(meter, action, rfnCallback);
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
     * Initiates a CB_CD.CDStateChangedNotifaction message to vendor.
     * @param yukonMeter - meter
     * @param loadActionCode - loadActionCode
     * @param vendor - msp vendor that made the inital request
     * @param transactionId - the token provided from the initial request
     */
    private void sendCDEventNotification(SimpleMeter yukonMeter, LoadActionCode loadActionCode,
            MultispeakVendor vendor, String transactionId, String responseUrl) {

        log.info("Sending CDStateChangedNotification (" + responseUrl + "): Meter Number " + yukonMeter.getMeterNumber() + " Code:" + loadActionCode);

        try {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_CDPort(vendor, responseUrl);
            if (port != null) {
                port.CDStateChangedNotification(yukonMeter.getMeterNumber(), loadActionCode, transactionId, "");
            } else {
                log.error("Port not found for CB_Server (" + vendor.getCompanyName() + ")");
            }
        } catch (RemoteException e) {
            log.error("TargetService: " + responseUrl + " - initiateConnectDisconnect (" + vendor.getCompanyName() + ")");
            log.error("RemoteExceptionDetail: " + e.getMessage());
        }
    }

    /**
     * Writes a request to pil for the meter and commandStr using the id for
     * mspVendor. log a message for the method name.
     */
    private void writePilRequest(YukonMeter meter, String commandStr, long id, int priority) {

        Request pilRequest = setupPilRequest(meter, commandStr, id, priority);
        doWritePilRequest(pilRequest);
    }

    private Request setupPilRequest(YukonMeter meter, String commandStr, long id, int priority) {

        Request pilRequest = null;
        commandStr += " update";
        commandStr += " noqueue";
        pilRequest = new Request(meter.getPaoIdentifier().getPaoId(), commandStr, id);
        pilRequest.setPriority(priority);

        return pilRequest;
    }

    private void doWritePilRequest(Request pilRequest) {
        porterConnection.write(pilRequest);
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
    public ErrorObject[] initiateDisconnectedStatus(MultispeakVendor mspVendor, String[] meterNos) {

        return addToGroup(meterNos, SystemGroupEnum.DISCONNECTED_STATUS, "initiateDisconnectedStatus", mspVendor);
    }

    @Override
    public ErrorObject[] initiateUsageMonitoring(MultispeakVendor mspVendor, String[] meterNos) {

        return addToGroup(meterNos, SystemGroupEnum.USAGE_MONITORING, "initiateUsageMonitoring", mspVendor);
    }

    @Override
    public ErrorObject[] cancelDisconnectedStatus(MultispeakVendor mspVendor, String[] meterNos) {

        return removeFromGroup(meterNos, SystemGroupEnum.DISCONNECTED_STATUS, "cancelDisconnectedStatus", mspVendor);
    }

    @Override
    public ErrorObject[] cancelUsageMonitoring(MultispeakVendor mspVendor, String[] meterNos) {

        return removeFromGroup(meterNos, SystemGroupEnum.USAGE_MONITORING, "cancelUsageMonitoring", mspVendor);
    }

    @Override
    public ErrorObject[] meterAdd(final MultispeakVendor mspVendor, Meter[] addMeters) throws RemoteException {
        final List<ErrorObject> errorObjects = new ArrayList<ErrorObject>();

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

                        removeFromGroup(newMeter, SystemGroupEnum.INVENTORY, METER_ADD_STRING, mspVendor);

                        // TODO Consider moving this stuff out of this transaction....requesting serviceLocaiton could fail and shouldn't roll back the rest of this.
                        ServiceLocation mspServiceLocation = mspObjectDao.getMspServiceLocation(mspMeter.getMeterNo(), mspVendor);

                        // update the billing group from CIS billingCyle
                        if (mspServiceLocation != null) {
                            String billingCycle = mspServiceLocation.getBillingCycle();
                            updateBillingCyle(billingCycle, newMeter.getMeterNumber(), newMeter, METER_ADD_STRING, mspVendor);
                            updateAltGroup(mspServiceLocation, newMeter.getMeterNumber(), newMeter, METER_ADD_STRING, mspVendor);
                        }
                        // Must complete route locate after meter is enabled
                        verifyAndUpdateSubstationGroupAndRoute(newMeter, mspVendor, mspMeter, mspServiceLocation, METER_ADD_STRING, true);
                    };
                });
            } catch (MspErrorObjectException e) {
                errorObjects.add(e.getErrorObject());
                log.error(e);
            } catch (RuntimeException ex) {
                // Transactional code threw application exception -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getMeterNo(),
                                                              "X Exception: (MeterNo:" + mspMeter.getMeterNo() + ")-" + ex.getMessage(),
                                                              "Meter",
                                                              METER_ADD_STRING,
                                                              mspVendor.getCompanyName());
                errorObjects.add(err);
                log.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getMeterNo(),
                                                              "X Error: (MeterNo:" + mspMeter.getMeterNo() + ")-" + ex.getMessage(),
                                                              "Meter",
                                                              METER_ADD_STRING,
                                                              mspVendor.getCompanyName());
                errorObjects.add(err);
                log.error(ex);
            }
        }// end for

        return mspObjectDao.toErrorObject(errorObjects);
    }

    /**
     * Add a new meter to Yukon.
     * @throws MspErrorObjectException when templateName is not a valid YukonPaobject Name.
     */
    private YukonMeter addNewMeter(Meter mspMeterToAdd, MultispeakVendor mspVendor) throws MspErrorObjectException {

        YukonMeter templateMeter = getYukonMeterForTemplate(mspMeterToAdd, mspVendor);

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
                                                                            newMeterRfnIdentifier.getSensorModel(),
                                                                            newMeterRfnIdentifier.getSensorManufacturer(),
                                                                            newMeterRfnIdentifier.getSensorSerialNumber(),
                                                                            true);

            } else if (templateMeter.getPaoType().isPlc()) {
                // CREATE DEVICE - new device is automatically added to
                // template's device groups
                // TODO create new method here that takes a loaded
                // template...since we already have one!
                newDevice = deviceCreationService.createDeviceByTemplate(templateMeter.getName(), newPaoName, true);
            } else {
                // return errorObject for any other type.
                ErrorObject errorObject = mspObjectDao.getErrorObject(mspMeterToAdd.getObjectID(),
                                                                      "Error: Invalid template type [" + templateMeter.getPaoType() + "].",
                                                                      "Meter",
                                                                      "MeterAddNotification",
                                                                      mspVendor.getCompanyName());
                throw new MspErrorObjectException(errorObject);
            }
        } catch (DeviceCreationException | BadConfigurationException e) {
            log.error(e);// TODO create errorObject out of e
            ErrorObject errorObject = mspObjectDao.getErrorObject(mspMeterToAdd.getObjectID(),
                                                                  "Error: " + e.getMessage(),
                                                                  "Meter",
                                                                  "MeterAddNotification",
                                                                  mspVendor.getCompanyName());
            throw new MspErrorObjectException(errorObject);
        }

        YukonMeter newMeter = meterDao.getForId(newDevice.getDeviceId());
        mspObjectDao.logMSPActivity(METER_ADD_STRING,
                                    "DeviceName(" + newPaoName + ") - Meter created. " + newMeter.toString() + ").",
                                    mspVendor.getCompanyName());

        // update default values of newMeter
        updateExistingMeter(mspMeterToAdd, newMeter, templateMeter, mspVendor, true);
        return newMeter;
    }
    
    /**
     * Returns an rfnIdentifier representing the serialNumber and manufacturer/model from templateMeter.
     * If templateMeter has blank values, then we will attempt to parse the manufacturer/model values from the template name.
     *  This is useful if the templateName matches the "standard" *RfnTemplate_manufacturer_model naming convention
     * @param templateMeter
     * @param serialNumber
     * @return
     */
    private RfnIdentifier buildNewMeterRfnIdentifier(RfnMeter templateMeter, String serialNumber) {

        String manufacturer = templateMeter.getRfnIdentifier().getSensorManufacturer();
        String model = templateMeter.getRfnIdentifier().getSensorModel();
        
        if (StringUtils.isEmpty(manufacturer) || StringUtils.isEmpty(model)) {
            // if either is empty, attempt to parse from the templateMeter.paoName
            String templatePrefix = configurationSource.getString("RFN_METER_TEMPLATE_PREFIX", "*RfnTemplate_");
            
            // Format is *RfnTemplate_manufacturer_model
            String nameToStripRfnIdentifierFrom = templateMeter.getName();
            String rfnIdentifierPart = StringUtils.removeStart(nameToStripRfnIdentifierFrom, templatePrefix);
            String[] manufacturerModel = StringUtils.split(rfnIdentifierPart, "_");
            
            if (manufacturerModel.length == 2) {
                manufacturer= manufacturerModel[0];
                model = manufacturerModel[1];
            } else {
                // TODO - make better error object
                throw new MspErrorObjectException(new ErrorObject());
            }
        }
        
        return new RfnIdentifier(serialNumber, manufacturer, model);
    }
    
    /**
     * Check for existing meter in system and update if found.
     * @throws NotFoundException if existing meter is not found in system.
     * @throws MspErrorObjectException when templateName is not a valid YukonPaobject Name. Or if changeType cannot be processed.
     */
    private YukonMeter checkForExistingMeterAndUpdate(Meter mspMeter, MultispeakVendor mspVendor)
            throws NotFoundException, MspErrorObjectException {

        YukonMeter meter = null;
        MultispeakMeterLookupFieldEnum multispeakMeterLookupFieldEnum = multispeakFuncs.getMeterLookupField();

        switch (multispeakMeterLookupFieldEnum) {
        case METER_NUMBER:
            meter = getMeterByMeterNumber(mspMeter);
            break;
        case ADDRESS:
            meter = getMeterBySerialNumberOrAddress(mspMeter);
            break;
        case DEVICE_NAME:
            meter = getMeterByPaoName(mspMeter, mspVendor);
            break;
        case AUTO_METER_NUMBER_FIRST:
            try { // Lookup by MeterNo
                meter = getMeterByMeterNumber(mspMeter);
            } catch (NotFoundException e) { // Doesn't exist by MeterNumber
                try { // Lookup by Address
                    meter = getMeterBySerialNumberOrAddress(mspMeter);
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
                    meter = getMeterByMeterNumber(mspMeter);
                } catch (NotFoundException e2) {
                    // Lookup by Address
                    meter = getMeterBySerialNumberOrAddress(mspMeter);
                    // Not Found Exception thrown in the end if never found
                }
            }
            break;
        }

        YukonMeter templateMeter = getYukonMeterForTemplate(mspMeter, mspVendor); // throws
                                                                                  // MspErrorObjectException
        updateExistingMeter(mspMeter, meter, templateMeter, mspVendor, true);
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
     */
    private void updateExistingMeter(Meter mspMeter, YukonMeter existingMeter, YukonMeter templateMeter,
            MultispeakVendor mspVendor, boolean enable) throws MspErrorObjectException {

        verifyAndUpdateType(templateMeter, existingMeter, mspVendor);

        String newMeterNumber = mspMeter.getMeterNo().trim();
        verifyAndUpdateMeterNumber(newMeterNumber, existingMeter);

        String newSerialOrAddress = mspMeter.getNameplate().getTransponderID().trim(); // this should be the sensorSerialNumber
        verifyAndUpdateAddressOrSerial(newSerialOrAddress, templateMeter, existingMeter);

        String newPaoName = getPaoNameFromMspMeter(mspMeter, mspVendor);
        verifyAndUpdatePaoName(newPaoName, existingMeter);

        // Enable Meter and update applicable fields.
        if (enable) {
            if (existingMeter.isDisabled()) {
                deviceDao.enableDevice(existingMeter);
            } else {
                log.info("Meter (" + existingMeter.toString() + ") - currently enabled, continuing with processing...");
            }
        }

        // Perform DBChange for Pao here? Need to make sure the above methods no
        // longer push the db change msg too...
    }

    /**
     * Check if the deviceType of meter is different than the deviceType of the template meter 
     * If different types of meters, then the deviceType will be changed for meter.
     * If the types are not compatible, a MspErrorObjectException will be thrown.
     * @param templateMeter - the meter to compare to, this is the type of meter the calling system thinks we should have
     * @param existingMeter - the meter to update
     * @param mspVendor
     * @throws MspErrorObjectException when error changing the type
     */
    private void verifyAndUpdateType(YukonMeter templateMeter, YukonMeter existingMeter, MultispeakVendor mspVendor)
            throws MspErrorObjectException {
        if (templateMeter.getPaoType() != existingMeter.getPaoType()) {
            // PROBLEM, types do not match!
            // Attempt to change type
            try {
                changeDeviceTypeService.changeDeviceType(new SimpleDevice(existingMeter), templateMeter.getPaoType());
            } catch (ProcessingException e) {
                ErrorObject errorObject = mspObjectDao.getErrorObject(existingMeter.getMeterNumber(), "Error: " + e.getMessage(),
                                                                      "Meter", "ChangeDeviceType", mspVendor.getCompanyName());
                log.error(e);
                // return errorObject; couldn't save the change type
                throw new MspErrorObjectException(errorObject);
            }
            // ADD LOGGING
        }
    }

    /**
     * Check if paoName of meter is different than new paoName. 
     * If different, paoName is updated.
     */
    private void verifyAndUpdatePaoName(String newPaoName, YukonMeter existingMeter) {
        if (!existingMeter.getName().equals(newPaoName)) {
            // UPDATE PAONAME
            // Shouldn't fail, if PaoName already exists, a uniqueness value will be added.
            newPaoName = getNewUniquePaoName(newPaoName);
            deviceDao.changeName(existingMeter, newPaoName);
            // ADD LOGGING
        }
    }

    /**
     * Check if meterNumber of meter is different than new meterNumber.
     * If different, meterNumber is updated.
     */
    private void verifyAndUpdateMeterNumber(String newMeterNumber, YukonMeter existingMeter) {
        if (!existingMeter.getMeterNumber().equals(newMeterNumber)) {
            // UPDATE METER NUMBER
            // Shouldn't fail, if Meter Number already exists, we end up with duplicates
            deviceDao.changeMeterNumber(existingMeter, newMeterNumber);
            // ADD LOGGING
        }
    }

    /**
     * For Rfn, check if rfnIdentifier of meter is different than new serialNumber (and model/manufacturer of templateMeter).
     *  If different, rfnIdentifier is updated. 
     * For Plc, check if address of meter is different than new address. 
     *  If different, address is updated.
     */
    private void verifyAndUpdateAddressOrSerial(String newSerialOrAddress, YukonMeter templateMeter,
            YukonMeter existingMeter) {
        if (existingMeter.getPaoType().isRfn()) {
            RfnIdentifier newMeterRfnIdentifier = new RfnIdentifier(newSerialOrAddress,
                                                                    ((RfnMeter) templateMeter).getRfnIdentifier().getSensorManufacturer(),
                                                                    ((RfnMeter) templateMeter).getRfnIdentifier().getSensorModel());

            // Check if different first, then update only if change needed
            if (!((RfnMeter) existingMeter).getRfnIdentifier().equals(newMeterRfnIdentifier)) {
                RfnDevice deviceToUpdate = new RfnDevice(existingMeter, newMeterRfnIdentifier);
                rfnDeviceDao.updateDevice(deviceToUpdate);
                // UPDATE SERIAL NUMBER (model, manufacturer)
                // MAY FAIL IF RFNIdentifier ALREADY EXISTS FOR ANOTHER METER
            }

        } else if (templateMeter.getPaoType().isMct()) {
            // Check if different first, then update only if change needed
            if (!existingMeter.getSerialOrAddress().equals(newSerialOrAddress)) {
                // UPDATE CARRIER ADDRESS
                // WILL NOT FAIL IF ADDRESS IS ALREADY IN USE BY ANOTHER DEVICE!
                // Should we warn/error?
                deviceUpdateService.changeAddress(existingMeter, Integer.valueOf(newSerialOrAddress));
                // log
            }
        }
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
     * @param forceRouteUpdate - when true, route locate will occur; else, it will only occur if changed.
     */
    private void verifyAndUpdateSubstationGroupAndRoute(YukonMeter meterToUpdate, MultispeakVendor mspVendor,
            Meter mspMeter, ServiceLocation mspServiceLocation, String logActionStr, boolean forceRouteUpdate) {

        String meterNumber = meterToUpdate.getMeterNumber();

        // Verify substation name
        String substationName = getSubstationNameFromMspObjects(mspMeter, mspServiceLocation, mspVendor);
        if (StringUtils.isBlank(substationName)) {
            // change logging here.
            // No route updates made (if PLC). No substation group updates.
            mspObjectDao.logMSPActivity(METER_ADD_STRING,
                                        "MeterNumber(" + meterNumber + ") - substation name not provided, route locate and substation assignement not completed.",
                                        mspVendor.getCompanyName());
        } else {

            // update the substation group
            boolean addedToGroup = updateSubstationGroup(substationName, meterNumber, meterToUpdate, logActionStr, mspVendor);

            if (meterToUpdate.getPaoType().isPlc()) {
                if (forceRouteUpdate || addedToGroup) {
                    // If the substation changed, we should attempt to update the route info too.
                    // Update route (_after_ meter is enabled).
                    verifyAndUpdateRoute(meterToUpdate, mspVendor, substationName, meterNumber);
                }
            }
        }
    }

    /**
     * Checks if substationName is in Yukon.
     * If not found, or if no routes associated with substation, then return with no processing.
     * If found, and routes exist, assigns (first) route to meter.
     * Then initiates route locate.
     * If only one route found, assigns to meter but locate is skipped.
     * Returns with no processing for non PLC types.
     * @param meterDevice - the meter to update routing for
     * @param mspVendor
     * @param substationName - the substationName to lookup routeMappings for
     * @param meterNumber - for logging
     */
    private void verifyAndUpdateRoute(YukonDevice meterDevice, MultispeakVendor mspVendor, String substationName, String meterNumber) {

        // not valid for RFN meter types
        if (!meterDevice.getPaoIdentifier().getPaoType().isPlc()) {
            return;
        }

        try {
            // get routes
            Substation substation = substationDao.getByName(substationName);
            List<Route> routes = substationToRouteMappingDao.getRoutesBySubstationId(substation.getId());

            if (routes.isEmpty()) {
                mspObjectDao.logMSPActivity(METER_ADD_STRING,
                                            "MeterNumber(" + meterNumber + ") - No Routes for substation (" + substationName + "), using route from exisiting device.",
                                            mspVendor.getCompanyName());
            } else { // routes exist

                ImmutableList<Integer> routeIds = PaoUtils.asPaoIdList(routes);
                ImmutableList<String> routeNames = PaoUtils.asPaoNames(routes);

                // initally set route to first sub mapping
                deviceUpdateService.changeRoute(meterDevice, routes.get(0).getId());
                mspObjectDao.logMSPActivity(METER_ADD_STRING,
                                            "MeterNumber(" + meterNumber + ") - Route initially set to " + routes.get(0).getName() + ", will run route discovery.",
                                            mspVendor.getCompanyName());

                if (routes.size() == 1) { // no need to run route discovery if
                                          // we only have one route.
                    mspObjectDao.logMSPActivity(METER_ADD_STRING,
                                                "MeterNumber(" + meterNumber + ") - Only one route associated with substation, assigned route and skipping route discovery.",
                                                mspVendor.getCompanyName());
                } else { // run route discovery
                    LiteYukonUser liteYukonUser = UserUtils.getYukonUser();
                    deviceUpdateService.routeDiscovery(meterDevice, routeIds, liteYukonUser);
                    mspObjectDao.logMSPActivity(METER_ADD_STRING,
                                                "MeterNumber(" + meterNumber + ") - Route discovery started on: " + StringUtils.join(routeNames,
                                                                                                                                     ",") + ".",
                                                mspVendor.getCompanyName());
                }
            }

        } catch (NotFoundException e) { // bad sub name
            // TODO - need good logging here because this may be important
            // configuration information.
            mspObjectDao.logMSPActivity(METER_ADD_STRING,
                                        "MeterNumber(" + meterNumber + ") - substation name " + substationName + " not found in Yukon, no route changes will occur.",
                                        mspVendor.getCompanyName());
            log.warn(e);
        }
    }
    
    /**
     * Helper method to return a Meter object for PaoName PaoName is looked up
     * based on PaoNameAlias
     */
    private YukonMeter getMeterByPaoName(Meter mspMeter, MultispeakVendor mspVendor) {
        String paoName = getPaoNameFromMspMeter(mspMeter, mspVendor);
        return meterDao.getForPaoName(paoName);
    }

    /**
     * Helper method to return a PLC Meter object for Address
     * Address to look up is from mspMeter.Nameplate.TransponderID
     */
    private YukonMeter getMeterBySerialNumberOrAddress(Meter mspMeter) {
        String mspAddress = mspMeter.getNameplate().getTransponderID().trim();
        return mspMeterDao.getForSerialNumberOrAddress(mspAddress);
    }

    /**
     * Helper method to return a Meter object for Meter Number
     */
    private YukonMeter getMeterByMeterNumber(Meter mspMeter) throws NotFoundException {
        String mspMeterNo = mspMeter.getMeterNo().trim();
        return meterDao.getForMeterNumber(mspMeterNo);
    }

    @Override
    public ErrorObject[] meterRemove(MultispeakVendor mspVendor, Meter[] removeMeters) {
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();

        for (Meter mspMeter : removeMeters) {
            // Lookup meter in Yukon by msp meter number
            YukonMeter meter;
            try {
                meter = getMeterByMeterNumber(mspMeter);

                // Added meter to Inventory
                addToGroup(meter, SystemGroupEnum.INVENTORY, METER_REMOVE_STRING, mspVendor);
                if (!meter.isDisabled()) {// enabled
                    deviceDao.disableDevice(meter);
                }

                mspObjectDao.logMSPActivity(METER_REMOVE_STRING,
                                            "MeterNumber(" + meter.getMeterNumber() + ") - Meter Disabled.",
                                            mspVendor.getCompanyName());
            } catch (NotFoundException e) {

                ErrorObject err = mspObjectDao.getNotFoundErrorObject(mspMeter.getMeterNo().trim(), "MeterNumber", "Meter", 
                                                                      METER_REMOVE_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
            }
        }

        return mspObjectDao.toErrorObject(errorObjects);
    }

    @Override
    public ErrorObject[] serviceLocationChanged(final MultispeakVendor mspVendor, ServiceLocation[] serviceLocations) {
        final Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
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
                                ErrorObject err = mspObjectDao.getNotFoundErrorObject(serviceLocationStr, "ServiceLocation", "ServiceLocation",
                                                                                      SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                                errorObjects.add(err);
                            } else {
                                for (YukonMeter meter : meters) {
                                    // update the billing group from CIS billingCyle
                                    String billingCycle = mspServiceLocation.getBillingCycle();
                                    updateBillingCyle(billingCycle, meter.getMeterNumber(), meter, SERV_LOC_CHANGED_STRING, mspVendor);
                                    updateAltGroup(mspServiceLocation, meter.getMeterNumber(), meter, SERV_LOC_CHANGED_STRING, mspVendor);

                                    // using null for mspMeter. See comments in getSubstationNameFromMspMeter(...)
                                    verifyAndUpdateSubstationGroupAndRoute(meter, mspVendor, null, mspServiceLocation, SERV_LOC_CHANGED_STRING, true);
                                }
                            }
                        } else {
                            // Must get meters from MSP CB call to process.
                            List<Meter> mspMeters = mspObjectDao.getMspMetersByServiceLocation(mspServiceLocation,
                                                                                               mspVendor);

                            if (!mspMeters.isEmpty()) {

                                for (Meter mspMeter : mspMeters) {
                                    try {
                                        YukonMeter meter = getMeterByMeterNumber(mspMeter);

                                        // MeterNumber should not have changed, nor transponderId...only paoName possibly
                                        String newPaoName = getPaoNameFromMspMeter(mspMeter, mspVendor);
                                        verifyAndUpdatePaoName(newPaoName, meter);

                                        String billingCycle = mspServiceLocation.getBillingCycle();
                                        updateBillingCyle(billingCycle, meter.getMeterNumber(), meter, SERV_LOC_CHANGED_STRING, mspVendor);
                                        updateAltGroup(mspServiceLocation, meter.getMeterNumber(), meter, SERV_LOC_CHANGED_STRING, mspVendor);

                                        verifyAndUpdateSubstationGroupAndRoute(meter, mspVendor, mspMeter, mspServiceLocation, METER_ADD_STRING, true);
                                    } catch (NotFoundException e) {
                                        ErrorObject err = mspObjectDao.getNotFoundErrorObject(mspMeter.getMeterNo(), "MeterNumber", "Meter", SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                                        errorObjects.add(err);
                                        log.error(e);
                                    }
                                }
                            } else {
                                ErrorObject err = mspObjectDao.getErrorObject(mspServiceLocation.getObjectID(),
                                                                              paoAlias.getDisplayName() + "ServiceLocation(" + mspServiceLocation.getObjectID() + ") - No meters returned from vendor for location.",
                                                                              "ServiceLocation", SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                                errorObjects.add(err);
                            }
                        }
                    };
                });
            } catch (MspErrorObjectException e) {
                errorObjects.add(e.getErrorObject());
                log.error(e);
            } catch (RuntimeException ex) {
                // Transactional code threw application exception -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspServiceLocation.getObjectID(),
                                                              "X Exception: (ServLoc:" + mspServiceLocation.getObjectID() + ")-" + ex.getMessage(),
                                                              "ServiceLocation",
                                                              SERV_LOC_CHANGED_STRING,
                                                              mspVendor.getCompanyName());
                errorObjects.add(err);
                log.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspServiceLocation.getObjectID(),
                                                              "X Error: (ServLoc:" + mspServiceLocation.getObjectID() + ")-" + ex.getMessage(),
                                                              "ServiceLocation",
                                                              SERV_LOC_CHANGED_STRING,
                                                              mspVendor.getCompanyName());
                errorObjects.add(err);
                log.error(ex);
            }
        }
        return mspObjectDao.toErrorObject(errorObjects);
    }

    @Override
    public ErrorObject[] meterChanged(final MultispeakVendor mspVendor, Meter[] changedMeters) throws RemoteException {
        final List<ErrorObject> errorObjects = new ArrayList<ErrorObject>();

        for (final Meter mspMeter : changedMeters) {
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {

                        validateMspMeter(mspMeter, mspVendor, METER_CHANGED_STRING);

                        try {
                            YukonMeter meterToChange = getMeterByMeterNumber(mspMeter);
                            YukonMeter templateMeter = getYukonMeterForTemplate(mspMeter, mspVendor); // throws MspErrorObjectException
                            updateExistingMeter(mspMeter, meterToChange, templateMeter, mspVendor, true);

                            // using null for mspServiceLocation. See comments in getSubstationNameFromMspMeter(...)
                            verifyAndUpdateSubstationGroupAndRoute(meterToChange, mspVendor, mspMeter, null, SERV_LOC_CHANGED_STRING, false);
                        } catch (NotFoundException e) {
                            ErrorObject err = mspObjectDao.getNotFoundErrorObject(mspMeter.getObjectID(),
                                                                                  "MeterNumber: " + mspMeter.getMeterNo(),
                                                                                  "Meter", METER_CHANGED_STRING, mspVendor.getCompanyName());
                            errorObjects.add(err);
                            log.error(e);
                        }
                    };
                });
            } catch (MspErrorObjectException e) {
                errorObjects.add(e.getErrorObject());
                log.error(e);
            } catch (RuntimeException ex) {
                // Transactional code threw application exception -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getMeterNo(),
                                                              "X Exception: (MeterNo:" + mspMeter.getMeterNo() + ")-" + ex.getMessage(),
                                                              "Meter", METER_CHANGED_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                log.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getMeterNo(),
                                                              "X Error: (MeterNo:" + mspMeter.getMeterNo() + ")-" + ex.getMessage(),
                                                              "Meter", METER_CHANGED_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                log.error(ex);
            }
        }// end for

        return mspObjectDao.toErrorObject(errorObjects);
    }

    @Override
    public ErrorObject[] addMetersToGroup(MeterGroup meterGroup, MultispeakVendor mspVendor) {

        List<ErrorObject> errorObjects = new ArrayList<ErrorObject>();

        // Convert MeterNumbers to YukonDevices
        List<SimpleDevice> yukonDevices = new ArrayList<SimpleDevice>();
        if (meterGroup.getMeterList() != null) {
            for (String meterNumber : meterGroup.getMeterList()) {
                try {
                    SimpleDevice yukonDevice = deviceDao.getYukonDeviceObjectByMeterNumber(meterNumber);
                    yukonDevices.add(yukonDevice);
                } catch (EmptyResultDataAccessException e) {
                    ErrorObject errorObject = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "addMetersToGroup", mspVendor.getCompanyName());
                    errorObjects.add(errorObject);
                }
            }
        }

        String groupName = meterGroup.getGroupName();
        StoredDeviceGroup storedGroup = deviceGroupEditorDao.getStoredGroup(groupName, true);
        deviceGroupMemberEditorDao.addDevices(storedGroup, yukonDevices);
        mspObjectDao.logMSPActivity("addMetersToGroup",
                                    "Added " + yukonDevices.size() + " Meters to group: " + storedGroup.getFullName() + ".",
                                    mspVendor.getCompanyName());
        return mspObjectDao.toErrorObject(errorObjects);
    }

    @Override
    public ErrorObject[] removeMetersFromGroup(String groupName, String[] meterNumbers, MultispeakVendor mspVendor) {
        List<ErrorObject> errorObjects = new ArrayList<ErrorObject>();
        List<SimpleDevice> yukonDevices = new ArrayList<SimpleDevice>();

        try {
            StoredDeviceGroup storedGroup = deviceGroupEditorDao.getStoredGroup(groupName, false);
            if (meterNumbers != null) {
                for (String meterNumber : meterNumbers) {
                    try {
                        SimpleDevice yukonDevice = deviceDao.getYukonDeviceObjectByMeterNumber(meterNumber);
                        yukonDevices.add(yukonDevice);
                    } catch (EmptyResultDataAccessException e) {
                        ErrorObject errorObject = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "removeMetersFromGroup", mspVendor.getCompanyName());
                        errorObjects.add(errorObject);
                    }
                }
            }

            deviceGroupMemberEditorDao.removeDevices(storedGroup, yukonDevices);
            mspObjectDao.logMSPActivity("removeMetersFromGroup",
                                        "Removed " + yukonDevices.size() + " Meters from group: " + storedGroup.getFullName() + ".",
                                        mspVendor.getCompanyName());
        } catch (NotFoundException e) {
            ErrorObject errorObject = mspObjectDao.getNotFoundErrorObject(groupName, "GroupName", "MeterGroup", "removeMetersFromGroup", mspVendor.getCompanyName());
            errorObjects.add(errorObject);
        }
        return mspObjectDao.toErrorObject(errorObjects);
    }

    @Override
    public ErrorObject deleteGroup(String groupName, MultispeakVendor mspVendor) {
        try {
            StoredDeviceGroup storedGroup = deviceGroupEditorDao.getStoredGroup(groupName, false);
            deviceGroupEditorDao.removeGroup(storedGroup);
        } catch (NotFoundException e) {
            ErrorObject errorObject = mspObjectDao.getNotFoundErrorObject(groupName, "meterGroupId", "MeterGroup", "deleteGroup", mspVendor.getCompanyName());
            return errorObject;
        }
        return null;
    }

    /**
     * Helper method to load extension value from extensionItems for
     * extensionName
     */
    private String getExtensionValue(ExtensionsItem[] extensionItems, String extensionName, String defaultValue) {
        log.debug("Attempting to load extension value for key:" + extensionName);
        if (extensionItems != null) {
            for (ExtensionsItem eItem : extensionItems) {
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

        if (mspMeter.getAMRDeviceType() != null) {
            return mspMeter.getAMRDeviceType();
        }

        ExtensionsItem[] extensionItems = mspMeter.getExtensionsList();
        return getExtensionValue(extensionItems, EXTENSION_DEVICE_TEMPLATE_STRING, defaultTemplateName);
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
            } else { // use extensions

                ExtensionsItem[] extensionItems = mspMeter.getExtensionsList();
                return getExtensionValue(extensionItems, extensionName, null);
            }
        }
        return null;
    }

    /**
     * Updates the billingCycle device group.
     * The exact parent group to update is configured in MultiSpeak global settings.
     */
    @Override
    public boolean updateBillingCyle(String newBilling, String meterNumber, YukonDevice yukonDevice,
            String logActionStr, MultispeakVendor mspVendor) {

        if (!StringUtils.isBlank(newBilling)) {

            // Remove from all billing membership groups
            DeviceGroup billingCycledeviceGroup = multispeakFuncs.getBillingCycleDeviceGroup();
            StoredDeviceGroup deviceGroupParent = deviceGroupEditorDao.getStoredGroup(billingCycledeviceGroup);
            return updatePrefixGroup(newBilling, meterNumber, yukonDevice, logActionStr, mspVendor, deviceGroupParent);
        }

        return false;
    }

    /**
    * Updates an alternate device grouping.
    * The exact parent group to update is configured by MSP_ALTGROUP_EXTENSION.
    * This functionality was added specifically for DEMCO.
    */
    @Override
    public boolean updateAltGroup(ServiceLocation mspServiceLocation, String meterNumber, YukonDevice yukonDevice,
            String logActionStr, MultispeakVendor mspVendor) {
        boolean updateAltGroup = configurationSource.getBoolean(MasterConfigBooleanKeysEnum.MSP_ENABLE_ALTGROUP_EXTENSION);
        if (updateAltGroup) {
            ExtensionsItem[] extensionItems = mspServiceLocation.getExtensionsList();
            String extensionName = configurationSource.getString(MasterConfigStringKeysEnum.MSP_ALTGROUP_EXTENSION, "altGroup");
            String altGroup = getExtensionValue(extensionItems, extensionName, null);
            if (!StringUtils.isBlank(altGroup)) {

                // Remove from all alt group membership groups
                DeviceGroup altGroupDeviceGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.ALTERNATE);
                StoredDeviceGroup deviceGroupParent = deviceGroupEditorDao.getStoredGroup(altGroupDeviceGroup);
                return updatePrefixGroup(altGroup, meterNumber, yukonDevice, logActionStr, mspVendor, deviceGroupParent);
            }
        }
        return false;
    }

    /**
     * Updates the CIS Substation device group.
     * This group (should be) completely managed by MultiSpeak processing.
     */
    @Override
    public boolean updateSubstationGroup(String substationName, String meterNumber, YukonDevice yukonDevice,
            String logActionStr, MultispeakVendor mspVendor) {

        if (!StringUtils.isBlank(substationName)) {

            // Remove from all substation membership groups
            DeviceGroup substationNameDeviceGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.CIS_SUBSTATION);
            StoredDeviceGroup deviceGroupParent = deviceGroupEditorDao.getStoredGroup(substationNameDeviceGroup);
            return updatePrefixGroup(substationName, meterNumber, yukonDevice, logActionStr, mspVendor, deviceGroupParent);
        }
        return false;
    }

    /**
     * Removes meter from all immediate descendants of deviceGroupParent. Adds
     * meter to a subgroup of deviceGroupParent called groupName. If groupName
     * does not exist, a new group will be created.
     * @return true if added to new prefix group.
     */
    private boolean updatePrefixGroup(String groupName, String meterNumber, YukonDevice yukonDevice,
            String logActionStr, MultispeakVendor mspVendor, StoredDeviceGroup deviceGroupParent) {
        boolean alreadyInGroup = false;

        Set<StoredDeviceGroup> deviceGroups = deviceGroupMemberEditorDao.getGroupMembership(deviceGroupParent, yukonDevice);
        for (StoredDeviceGroup deviceGroup : deviceGroups) {
            if (deviceGroup.getName().equalsIgnoreCase(groupName)) {
                log.debug("MeterNumber(" + meterNumber + ") - Already in group:  " + groupName);
                alreadyInGroup = true;
            } else {
                deviceGroupMemberEditorDao.removeDevices(deviceGroup, yukonDevice);
                mspObjectDao.logMSPActivity(logActionStr,
                                            "MeterNumber(" + meterNumber + ") - Removed from Group: " + deviceGroup.getFullName() + ".",
                                            mspVendor.getCompanyName());
            }
        }

        if (!alreadyInGroup) {
            StoredDeviceGroup deviceGroup = deviceGroupEditorDao.getGroupByName(deviceGroupParent, groupName, true);
            deviceGroupMemberEditorDao.addDevices(deviceGroup, yukonDevice);
            mspObjectDao.logMSPActivity(logActionStr,
                                        "MeterNumber(" + meterNumber + ") - Added to Group: " + deviceGroup.getFullName() + ".",
                                        mspVendor.getCompanyName());
        }
        return !alreadyInGroup;
    }

    /**
     * Helper method to remove meter from systemGroup
     */
    private void removeFromGroup(YukonMeter meter, SystemGroupEnum systemGroup, String logActionStr,
            MultispeakVendor mspVendor) {

        DeviceGroup deviceGroup = deviceGroupEditorDao.getSystemGroup(systemGroup);
        deviceGroupMemberEditorDao.removeDevices((StoredDeviceGroup) deviceGroup, Collections.singletonList(meter));
        String basePath = deviceGroupEditorDao.getFullPath(systemGroup);
        mspObjectDao.logMSPActivity(logActionStr,
                                    "MeterNumber(" + meter.getMeterNumber() + ") - Removed from " + basePath,
                                    mspVendor.getCompanyName());
    }

    /**
     * Helper method to add meter to systemGroup
     */
    private void addToGroup(YukonMeter meter, SystemGroupEnum systemGroup, String logActionStr,
            MultispeakVendor mspVendor) {

        DeviceGroup deviceGroup = deviceGroupEditorDao.getSystemGroup(systemGroup);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) deviceGroup, Collections.singletonList(meter));
        String basePath = deviceGroupEditorDao.getFullPath(systemGroup);
        mspObjectDao.logMSPActivity(logActionStr,
                                    "MeterNumber(" + meter.getMeterNumber() + ") - Added to " + basePath,
                                    mspVendor.getCompanyName());
    }

    /**
     * Helper method to add meterNos to systemGroup
     */
    private ErrorObject[] addToGroup(String[] meterNos, SystemGroupEnum systemGroup, String logActionStr,
            MultispeakVendor mspVendor) {
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();

        for (String meterNumber : meterNos) {
            try {
                YukonMeter meter = meterDao.getForMeterNumber(meterNumber);
                addToGroup(meter, systemGroup, logActionStr, mspVendor);
            } catch (NotFoundException e) {
                ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber,
                                                                      "MeterNumber",
                                                                      "Meter",
                                                                      "addToGroup",
                                                                      mspVendor.getCompanyName());
                errorObjects.add(err);
            }
        }
        return mspObjectDao.toErrorObject(errorObjects);
    }

    /**
     * Helper method to remove meterNos from systemGroup
     */
    private ErrorObject[] removeFromGroup(String[] meterNos, SystemGroupEnum systemGroup, String logActionStr,
            MultispeakVendor mspVendor) {

        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();

        for (String meterNumber : meterNos) {
            try {
                YukonMeter meter = meterDao.getForMeterNumber(meterNumber);
                removeFromGroup(meter, systemGroup, logActionStr, mspVendor);
            } catch (NotFoundException e) {
                ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber,
                                                                      "MeterNumber",
                                                                      "Meter",
                                                                      "removeFromGroup",
                                                                      mspVendor.getCompanyName());
                errorObjects.add(err);
            }
        }

        return mspObjectDao.toErrorObject(errorObjects);
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

        boolean useExtension = configurationSource.getBoolean(MasterConfigBooleanKeysEnum.MSP_ENABLE_SUBSTATIONNAME_EXTENSION);
        if (useExtension) { // custom for DEMCO/SEDC integration
            String extensionName = configurationSource.getString(MasterConfigStringKeysEnum.MSP_SUBSTATIONNAME_EXTENSION, "readPath");
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

        boolean exactSearch = configurationSource.getBoolean(MasterConfigBooleanKeysEnum.MSP_EXACT_SEARCH_PAONAME);
        List<YukonMeter> meters = Lists.newArrayList();
        if (exactSearch) {
            YukonMeter meter = meterDao.findForPaoName(filterValue);
            if (meter != null) {
                meters.add(meter);
            }
        } else {
            List<FilterBy> searchFilter = new ArrayList<FilterBy>(1);
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
     * @throws MspErrorObjectException when meter not found in Yukon by templateName provided
     */
    private YukonMeter getYukonMeterForTemplate(Meter mspMeter, MultispeakVendor mspVendor)
            throws MspErrorObjectException {

        String templateName = getMeterTemplate(mspMeter, mspVendor.getTemplateNameDefault());
        YukonMeter templateMeter;
        try {
            templateMeter = meterDao.getForPaoName(templateName);
        } catch (NotFoundException e) {
            // template not found...now what? ERROR?
            ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getObjectID(),
                                                          "Error: Meter (" + mspMeter.getMeterNo() + ") - does not contain a valid template meter. Template[" + templateName + "] " + "MeterAddNotification could not be completed, returning ErrorObject to calling vendor for processing.",
                                                          "Meter", METER_ADD_STRING, mspVendor.getCompanyName());
            log.error(e);
            throw new MspErrorObjectException(err);
        }
        return templateMeter;
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
     * Returns an "unused" paoName for newPaoName. 
     * If a meter already exists with newPaoName, then a numeric incremented value will be appended to the newPaoName.
     */
    private String getNewUniquePaoName(String newPaoName) {

        int retryCount = 0;
        String tempPaoName = newPaoName;
        boolean found = false;
        do {
            try {
                if (retryCount > 0) {
                    tempPaoName = newPaoName + " (" + retryCount + ")";
                }
                // Try to find if a meter already exists with this paoName
                meterDao.getForPaoName(tempPaoName);
                retryCount++;
            } catch (NotFoundException e) {
                // this is good!
                found = true;
            }
        } while (!found);

        return tempPaoName;
    }

    @Required
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }
}