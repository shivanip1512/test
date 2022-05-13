package com.cannontech.multispeak.service.impl.v4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
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
import com.cannontech.amr.rfn.model.RfnMeter;
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
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.exception.InsufficientMultiSpeakDataException;
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
import com.cannontech.database.db.point.stategroup.OutageStatus;
import com.cannontech.database.db.point.stategroup.PointStateHelper;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.msp.beans.v4.ArrayOfExtensionsItem;
import com.cannontech.msp.beans.v4.ArrayOfModule;
import com.cannontech.msp.beans.v4.ArrayOfOutageDetectionEvent;
import com.cannontech.msp.beans.v4.ElectricMeter;
import com.cannontech.msp.beans.v4.ElectricService;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.ExtensionsItem;
import com.cannontech.msp.beans.v4.GasMeter;
import com.cannontech.msp.beans.v4.GasService;
import com.cannontech.msp.beans.v4.MeterBase;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.MeterReading;
import com.cannontech.msp.beans.v4.Module;
import com.cannontech.msp.beans.v4.MspMeter;
import com.cannontech.msp.beans.v4.MspObject;
import com.cannontech.msp.beans.v4.ODEventNotification;
import com.cannontech.msp.beans.v4.ODEventNotificationResponse;
import com.cannontech.msp.beans.v4.ObjectFactory;
import com.cannontech.msp.beans.v4.OutageDetectDeviceType;
import com.cannontech.msp.beans.v4.OutageDetectionEvent;
import com.cannontech.msp.beans.v4.OutageEventType;
import com.cannontech.msp.beans.v4.OutageLocation;
import com.cannontech.msp.beans.v4.RCDState;
import com.cannontech.msp.beans.v4.ServiceLocation;
import com.cannontech.msp.beans.v4.WaterMeter;
import com.cannontech.msp.beans.v4.WaterService;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.v4.OAClient;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.dao.v4.MspObjectDao;
import com.cannontech.multispeak.data.v4.MspErrorObjectException;
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
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMultimap.Builder;

public class MultispeakMeterServiceImpl extends MultispeakMeterServiceBase implements MultispeakMeterService,MessageListener {

    private static final Logger log = YukonLogManager.getLogger(MultispeakMeterServiceImpl.class);

    @Autowired private @Qualifier("porter") BasicServerConnection porterConnection;

    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private ObjectFactory objectFactory;
    @Autowired private DeviceUpdateService deviceUpdateService;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private TransactionTemplate transactionTemplate;
    @Autowired private MeterSearchDao meterSearchDao;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private DeviceCreationService deviceCreationService;
    @Autowired private PointDao pointDao;
    @Autowired private AttributeService attributeService;
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    @Autowired private CommandExecutionService commandRequestService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private OAClient oaClient;
    
    private static final String EXTENSION_DEVICE_TEMPLATE_STRING = "AMRMeterType";
    private static final String SERV_LOC_CHANGED_STRING = "ServiceLocationChangedNotification";

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
                                                    DeviceError.SUCCESS.getCode());
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

    @Override
    public List<ErrorObject> serviceLocationChanged(MultispeakVendor mspVendor, List<ServiceLocation> serviceLocations) {
        final ArrayList<ErrorObject> errorObjects = new ArrayList<>();

        final MspPaoNameAliasEnum paoAlias = multispeakFuncs.getPaoNameAlias();
        for (final ServiceLocation serviceLocation : serviceLocations) {
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        boolean isMeterFound = false;
                        String companyName = mspVendor.getCompanyName();
                        if (paoAlias == MspPaoNameAliasEnum.SERVICE_LOCATION) {

                            List<MspObject> mspObjects = new ArrayList<>();
                            if (serviceLocation.getElectricServiceList() != null) {
                                mspObjects.addAll(serviceLocation.getElectricServiceList().getElectricService());
                            }
                            if (serviceLocation.getWaterServiceList() != null) {
                                mspObjects.addAll(serviceLocation.getWaterServiceList().getWaterService());
                            }
                            if (serviceLocation.getGasServiceList() != null) {
                                mspObjects.addAll(serviceLocation.getGasServiceList().getGasService());
                            }

                            for (MspObject mspObject : mspObjects) {
                                
                                String meterNo = null;
                                MspMeter mspMeter = null;
                                String billingCycle = null;
                                
                                if (mspObject instanceof ElectricService) {
                                    ElectricService electricService = (ElectricService) mspObject;
                                    meterNo = electricService.getElectricMeterID();
                                    MeterBase meterBase = electricService.getMeterBase();
                                    mspMeter = null != meterBase ? meterBase.getElectricMeter() : null;
                                    billingCycle = electricService.getBillingCycle();
                                }
                                else if (mspObject instanceof WaterService) {
                                    WaterService waterService = (WaterService) mspObject;
                                    meterNo = waterService.getWaterMeterID();
                                    mspMeter = waterService.getWaterMeter();
                                    billingCycle = waterService.getBillingCycle();
                                }
                                else if (mspObject instanceof GasService) {
                                    GasService gasService = (GasService) mspObject;
                                    meterNo = gasService.getGasMeterID();
                                    mspMeter = gasService.getGasMeter();
                                    billingCycle = gasService.getBillingCycle();
                                }

                                // if above both optional fields (meterID/mspMeter ) are not present in ServiceLocation then should we need to send any error message.
                                if (meterNo == null && mspMeter == null) {
                                    ErrorObject err = mspObjectDao.getNotFoundErrorObject("MeterID and MspMeter", "Meter",
                                                                                           "ServiceLocation", SERV_LOC_CHANGED_STRING,
                                                                                            companyName,
                                                                                           "not present in ServiceLocation");
                                    errorObjects.add(err);
                                } 
                                else
                                {
                                    YukonMeter meter = null;
                                    
                                    if (meterNo != null) {
                                        try {
                                            meter = getMeterByMeterNumber(meterNo);
                                            isMeterFound = true;
                                        } catch (NotFoundException e) {
                                            multispeakEventLogService.meterNotFound(meterNo, SERV_LOC_CHANGED_STRING, companyName);
                                            ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNo,
                                                                                                  "MeterNumber",
                                                                                                  "ServiceLocation",
                                                                                                   SERV_LOC_CHANGED_STRING,
                                                                                                   companyName);
                                            errorObjects.add(err);
                                            multispeakEventLogService.errorObject(err.getErrorString(),
                                                                                  SERV_LOC_CHANGED_STRING,
                                                                                  companyName);
                                            log.error(e);
                                        }
                                    }

                                    if (!isMeterFound && mspMeter != null) {
                                        try {
                                            meter = getMeterByMeterNumber(mspMeter.getMeterNo());
                                        } catch (NotFoundException e) {
                                            multispeakEventLogService.meterNotFound(mspMeter.getMeterNo(),
                                                                                    SERV_LOC_CHANGED_STRING,
                                                                                    companyName);
                                            ErrorObject err = mspObjectDao.getNotFoundErrorObject(mspMeter.getMeterNo(),
                                                                                                  "MeterNumber",
                                                                                                  "ServiceLocation",
                                                                                                  SERV_LOC_CHANGED_STRING,
                                                                                                  companyName);
                                            errorObjects.add(err);
                                            multispeakEventLogService.errorObject(err.getErrorString(),
                                                                                  SERV_LOC_CHANGED_STRING,
                                                                                  companyName);
                                            log.error(e);
                                        }
                                    }

                                    if (meter != null) {
                                        // update the billing group from request
                                        updateBillingCyle(billingCycle, meter.getMeterNumber(), meter, SERV_LOC_CHANGED_STRING, mspVendor);
                                        
                                        updatePaoLocation(serviceLocation, meter, SERV_LOC_CHANGED_STRING);
                                        
                                        verifyAndUpdateSubstationGroupAndRoute(meter, mspVendor, null, mspObject, SERV_LOC_CHANGED_STRING);
                                    }
                                }
                            }
                        }
                        else {
                            // Must get meters from MSP CB call to process.
                            List<MspMeter> mspMeters = mspObjectDao.getMspMetersByServiceLocation(serviceLocation, mspVendor);

                            if (!mspMeters.isEmpty()) {

                                for (MspMeter mspMeter : mspMeters) {
                                    try 
                                    {
                                        YukonMeter meter = getMeterByMeterNumber(mspMeter.getMeterNo());
                                        String newPaoName = getPaoNameFromMspMeter(mspMeter, mspVendor);

                                        verifyAndUpdatePaoName(newPaoName, meter, SERV_LOC_CHANGED_STRING, mspVendor);

                                        String mspMeterDeviceClass = (String) getModuleListFieldsForMspMeter(mspMeter).get("deviceClass");
                                        updateCISDeviceClassGroup(mspMeter.getMeterNo(), mspMeterDeviceClass, meter,
                                                                  SERV_LOC_CHANGED_STRING, mspVendor);

                                        String billingCycle = mspMeter.getBillingCycle();
                                        
                                        updateBillingCyle(billingCycle, meter.getMeterNumber(), meter, SERV_LOC_CHANGED_STRING, mspVendor);
                                        updatePaoLocation(serviceLocation, meter, SERV_LOC_CHANGED_STRING);
                                        verifyAndUpdateSubstationGroupAndRoute(meter, mspVendor, serviceLocation, mspMeter, SERV_LOC_CHANGED_STRING);
                                    
                                    } 
                                    catch (NotFoundException e) 
                                    {
                                        multispeakEventLogService.meterNotFound(mspMeter.getMeterNo(), SERV_LOC_CHANGED_STRING, companyName);
                                        ErrorObject err = mspObjectDao.getNotFoundErrorObject(mspMeter.getMeterNo(),
                                                                                              "MeterNumber",
                                                                                              "MspMeter",
                                                                                              SERV_LOC_CHANGED_STRING,
                                                                                              companyName);
                                        errorObjects.add(err);
                                        multispeakEventLogService.errorObject(err.getErrorString(), SERV_LOC_CHANGED_STRING, companyName);
                                        log.error(e);
                                    }
                                }
                            } 
                            else 
                            {
                                multispeakEventLogService.objectNotFoundByVendor(serviceLocation.getObjectID(),
                                                                                 "GetMetersByServiceLocationIDs",
                                                                                  SERV_LOC_CHANGED_STRING,
                                                                                  companyName);
                                
                                ErrorObject err = mspObjectDao.getErrorObject(serviceLocation.getObjectID(),
                                                                              paoAlias.getDisplayName() + 
                                                                              " ServiceLocation(" + 
                                                                              serviceLocation.getObjectID() + 
                                                                              ") - No meters returned from vendor for location.",
                                                                              "ServiceLocation",
                                                                              SERV_LOC_CHANGED_STRING,
                                                                              companyName);
                                errorObjects.add(err);
                                
                                multispeakEventLogService.errorObject(err.getErrorString(), SERV_LOC_CHANGED_STRING, companyName);
                            }
                        }
                    }
                });
            } catch (MspErrorObjectException e) {
                errorObjects.add(e.getErrorObject());
                multispeakEventLogService.errorObject(e.getErrorObject().getErrorString(),
                                                      SERV_LOC_CHANGED_STRING, 
                                                      mspVendor.getCompanyName());
                log.error(e);
            } catch (RuntimeException ex) {
                // Transactional code threw application exception -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(serviceLocation.getObjectID(),
                                                              "X Exception: (ServLoc:" + serviceLocation.getObjectID() + ")-" + ex.getMessage(),
                                                              "ServiceLocation",
                                                               SERV_LOC_CHANGED_STRING,
                                                               mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.errorObject(err.getErrorString(), SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                log.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(serviceLocation.getObjectID(),
                                                              "X Error: (ServLoc:" + serviceLocation.getObjectID() + ")-" + ex.getMessage(),
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
    public List<ErrorObject> meterAdd(final MultispeakVendor mspVendor, List<MspMeter> addMeters)
            throws MultispeakWebServiceException {
        final List<ErrorObject> errorObjects = new ArrayList<>();
        final String METER_ADD_STRING = "MeterAddNotification";
        for (final MspMeter mspMeter : addMeters) {
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {

                        validateMspMeter(mspMeter, mspVendor, METER_ADD_STRING);

                        YukonMeter newMeter;
                        try {
                            newMeter = checkForExistingMeterAndUpdate(mspMeter, mspVendor, METER_ADD_STRING);
                        } catch (NotFoundException e) { // and NEW meter
                            newMeter = addNewMeter(mspMeter, mspVendor, METER_ADD_STRING);
                        }

                        updatePaoLocation(mspMeter, newMeter, METER_ADD_STRING);
                        removeFromGroup(newMeter, SystemGroupEnum.INVENTORY, METER_ADD_STRING, mspVendor);

                        String mspMeterDeviceClass =(String) getModuleListFieldsForMspMeter(mspMeter).get("deviceClass");
                        updateCISDeviceClassGroup(mspMeter.getMeterNo(), mspMeterDeviceClass, newMeter, METER_ADD_STRING,
                                mspVendor);

                        String billingCycle = mspMeter.getBillingCycle();
                        updateBillingCyle(billingCycle, newMeter.getMeterNumber(), newMeter, METER_ADD_STRING, mspVendor);
                        updateAltGroup(mspMeter, newMeter.getMeterNumber(), newMeter, METER_ADD_STRING, mspVendor);
                        
                        // Must complete route locate after meter is enabled
                        verifyAndUpdateSubstationGroupAndRoute(newMeter, mspVendor, null, mspMeter, METER_ADD_STRING);
                    };
                });
            } catch (MspErrorObjectException e) {
                errorObjects.add(e.getErrorObject());
                multispeakEventLogService.errorObject(e.getErrorObject().getErrorString(), METER_ADD_STRING,
                                                      mspVendor.getCompanyName());
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
        }
        return errorObjects;
    }

    /**
     * Check for existing meter in system and update if found.
     * 
     * @throws NotFoundException       if existing meter is not found in system.
     * @throws MspErrorObjectException when templateName is not a valid YukonPaobject Name. Or if changeType cannot be processed.
     */
    private YukonMeter checkForExistingMeterAndUpdate(MspMeter mspMeter, MultispeakVendor mspVendor, String mspMethod )
            throws NotFoundException, MspErrorObjectException {

        YukonMeter meter = null;
       
        String mspAddress = getMeterCommAddress(mspMeter);
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

        multispeakEventLogService.meterFound(meter.getMeterNumber(), meter, mspMethod, mspVendor.getCompanyName());

        // load (and validate) template exists
        YukonMeter templateMeter = getYukonMeterForTemplate(mspMeter, mspVendor, false, mspMethod); // throws MspErrorObjectException
        if (templateMeter == null) {
            // If no template found, just use this meter as the template (meaning, same meter, no type changes).
            templateMeter = meter;
        }

        meter = updateExistingMeter(mspMeter, meter, templateMeter, mspMethod, mspVendor, true);
        return meter;
    }

    /**
     * Add a new meter to Yukon.
     * 
     * @throws MspErrorObjectException when templateName is not a valid YukonPaobject Name.
     */
    private YukonMeter addNewMeter(MspMeter mspMeter, MultispeakVendor mspVendor, String mspMethod) throws MspErrorObjectException {

        YukonMeter templateMeter = getYukonMeterForTemplate(mspMeter, mspVendor, true, mspMethod );

        String newPaoName = getPaoNameFromMspMeter(mspMeter, mspVendor);
        // If PaoName already exists, a uniqueness value will be added.
        newPaoName = getNewUniquePaoName(newPaoName);

        SimpleDevice newDevice;

        // Create PLC or RFN Device object with defaults
        try {
            if (templateMeter.getPaoType().isRfn()) {

                String serialNumber = getMeterCommAddress(mspMeter);
                RfnIdentifier newMeterRfnIdentifier = buildNewMeterRfnIdentifier((RfnMeter) templateMeter, serialNumber);

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
                ErrorObject errorObject = mspObjectDao.getErrorObject(mspMeter.getObjectID(),
                                                                      "Error: Invalid template type [" + templateMeter.getPaoType() + "].",
                                                                      "Meter",
                                                                      mspMethod,
                                                                      mspVendor.getCompanyName());
                throw new MspErrorObjectException(errorObject);
            }
        } catch (DeviceCreationException | BadConfigurationException e) {
            log.error(e);
            ErrorObject errorObject = mspObjectDao.getErrorObject(mspMeter.getObjectID(),
                                                                  "Error: " + e.getMessage(),
                                                                  "Meter",
                                                                  mspMethod,
                                                                  mspVendor.getCompanyName());
            throw new MspErrorObjectException(errorObject);
        }

        YukonMeter newMeter = meterDao.getForId(newDevice.getDeviceId());
        systemLog(mspMethod, "New Meter created: " + newMeter.toString(), mspVendor);
        multispeakEventLogService.meterCreated(newMeter.getMeterNumber(), newMeter, mspMethod, mspVendor.getCompanyName());

        // update default values of newMeter
        newMeter = updateExistingMeter(mspMeter, newMeter, templateMeter, mspMethod, mspVendor, true);
        return newMeter;
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
    private YukonMeter updateExistingMeter(MspMeter mspMeter, YukonMeter existingMeter, YukonMeter templateMeter,
            String mspMethod,
            MultispeakVendor mspVendor, boolean enable) throws MspErrorObjectException {

        YukonMeter originalCopy = existingMeter;
        String newSerialOrAddress = getMeterCommAddress(mspMeter);

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
                multispeakEventLogService.enableDevice(existingMeter.getMeterNumber(), existingMeter, mspMethod,
                        mspVendor.getCompanyName());
            } else {
                log.info("Meter (" + existingMeter.toString() + ") - currently enabled, continuing with processing...");
            }
        }

        systemLog(mspMethod, "Original:" + originalCopy.toString() + " New:" + existingMeter.toString(), mspVendor);
        // TODO Perform DBChange for Pao here instead? Need to make sure the above methods no longer push the db change msg too...
        return existingMeter;
    }

    /**
     * Check if the deviceType of meter is different than the deviceType of the template meter
     * If different types of meters, then the deviceType will be changed for meter.
     * If the types are not compatible, a MspErrorObjectException will be thrown.
     * 
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
                ErrorObject errorObject = mspObjectDao.getErrorObject(existingMeter.getMeterNumber(), "Error: " + e.getMessage(),
                                                                      "Meter",
                                                                      "ChangeDeviceType", mspVendor.getCompanyName());
                // return errorObject; couldn't save the change type
                throw new MspErrorObjectException(errorObject);
            }
        }
        return existingMeter;
    }

    /**
     * Returns a YukonMeter object, looked up by a templateName provided by mspMeter.
     * If templateName not found on mspMeter, then the vendor's default templateName will be used.
     * If useDefault is true, then the default template name will be used if AMRDeviceType not provided, otherwise return null.
     * 
     * @throws MspErrorObjectException when meter not found in Yukon by templateName provided
     */
    private YukonMeter getYukonMeterForTemplate(MspMeter mspMeter, MultispeakVendor mspVendor, boolean useDefault, String mspMethod)
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
                                                              "Error: Meter (" + mspMeter.getMeterNo() + ") - does not contain a valid template meter: Template["
                                                              + templateName + "]. Processing could not be completed, returning ErrorObject to calling vendor for processing.",
                                                              "Meter", mspMethod, mspVendor.getCompanyName());
                log.error(e);
                throw new MspErrorObjectException(err);
            }
            return templateMeter;
        }
        return null;
    }

    /**
     * Returns a Yukon PaoName (template) to model new meters after. If no value
     * is provided in the mspMeter object, then the defaultTemplateName is
     * returned.
     */
    private String getMeterTemplate(MspMeter mspMeter, String defaultTemplateName) {

        if (StringUtils.isNotBlank(mspMeter.getAMRDeviceType())) {
            return mspMeter.getAMRDeviceType();
        }

        return getExtensionValue(mspMeter.getExtensionsList(), EXTENSION_DEVICE_TEMPLATE_STRING, defaultTemplateName);
    }

    /**
     * Returns ErrorObject when mspMeter is not valid. Returns null when mspMeter is valid.
     * Validates
     * 1) Meter.MeterNo field is not blank.
     * 2) Meter.Nameplate is not null AND Meter.Nameplate.TransponderId is not blank
     */
    private void validateMspMeter(MspMeter mspMeter, MultispeakVendor mspVendor, String method)
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
        if (getMeterCommAddress(mspMeter) == null) {

            ErrorObject errorObject = mspObjectDao.getErrorObject(mspMeter.getMeterNo(),
                                                                  "Error: MeterNumber(" + mspMeter.getMeterNo()
                                                                        + ") - SerialNumber nor TransponderId are valid.  No updates were made.",
                                                                  "Meter",
                                                                  method,
                                                                  mspVendor.getCompanyName());
            throw new MspErrorObjectException(errorObject);
        }
    }

    /**
     * Retrieve meter comm address based on mspMeter
     */
    private String getMeterCommAddress(MspMeter mspMeter) {
        if (mspMeter instanceof WaterMeter) {
            return ((WaterMeter) mspMeter).getMeterCommAddress();
        } else if (mspMeter instanceof GasMeter) {
            return ((GasMeter) mspMeter).getMeterCommAddress();
        } else if (mspMeter instanceof ElectricMeter) {
            return ((ElectricMeter) mspMeter).getMeterCommAddress();
        }
        return null;
    }

    /**
     * Updates an alternate device grouping.
     * The exact parent group to update is configured by MSP_ALTGROUP_EXTENSION.
     * This functionality was added specifically for DEMCO.
     */
    @Override
    public boolean updateAltGroup(MspMeter mspMeter, String meterNumber, YukonDevice yukonDevice,
            String mspMethod, MultispeakVendor mspVendor) {
        boolean updateAltGroup = configurationSource.getBoolean(MasterConfigBoolean.MSP_ENABLE_ALTGROUP_EXTENSION);
        if (updateAltGroup) {
            String extensionName = configurationSource.getString(MasterConfigString.MSP_ALTGROUP_EXTENSION, "altGroup");
            String altGroup = getExtensionValue(mspMeter.getExtensionsList(), extensionName, null);
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
    private void updatePaoLocation(MspMeter mspMeter, YukonMeter meterToUpdate, String mspMethod) {
        MultispeakManagePaoLocation managePaoLocation = globalSettingDao.getEnum(GlobalSettingType.MSP_MANAGE_PAO_LOCATION,
                MultispeakManagePaoLocation.class);
        if (managePaoLocation == MultispeakManagePaoLocation.METER) {
            if (mspMeter.getUtilityInfo() != null && mspMeter.getUtilityInfo().getGpsPoint() != null) {
                Double longitude = mspMeter.getUtilityInfo().getGpsPoint().getLongitude();
                Double latitude = mspMeter.getUtilityInfo().getGpsPoint().getLatitude();
                if (longitude != null && latitude != null) {
                    PaoLocation paoLocation = new PaoLocation(meterToUpdate.getPaoIdentifier(), latitude, longitude,
                            Origin.MULTISPEAK, Instant.now());
                    updatePaoLocation(meterToUpdate.getMeterNumber(), meterToUpdate.getName(), paoLocation);
                }
            }
        }
    }

    /**
     * Helper method to return a Meter object for PaoName PaoName is looked up
     * based on PaoNameAlias
     */
    private YukonMeter getMeterByPaoName(MspMeter mspMeter, MultispeakVendor mspVendor) {
        String paoName = getPaoNameFromMspMeter(mspMeter, mspVendor);
        return meterDao.getForPaoName(paoName);
    }

    /**
     * @param mspMeter - multispeak meter
     * @return returns module list fields for particular multispeak meter
     */
    private Map<String, Object> getModuleListFieldsForMspMeter(MspMeter mspMeter) {

        Map<String, Object> moduleListFields = Maps.newHashMap();
        ArrayOfModule moduleList = mspMeter.getModuleList();

        if (moduleList != null) {
            List<Module> ListOfModule = moduleList.getModule();
            if (CollectionUtils.isNotEmpty(ListOfModule)) {
                Module module = ListOfModule.get(0);
                if (module != null) {
                    moduleListFields.put("deviceClass", module.getDeviceClass());
                    moduleListFields.put("facilityID", module.getFacilityID());
                }
            }
        }
        return moduleListFields;
    }

    /**
     * Update the (CIS) Substation Group.
     * If changed, update route (perform route locate).
     * If substationName is blank, do nothing.
     * 
     * @param meter           - the meter to modify
     * @param mspVendor
     * @param serviceLocation
     * @param mspObject       - the multispeak meter to process (if null, most likely will not change substation and
     *                        routing info)
     *                        - See {@link #getSubstationNameFromMspObjects(MspMeter, ServiceLocation, MultispeakVendor)}
     */
    private void verifyAndUpdateSubstationGroupAndRoute(YukonMeter meterToUpdate, MultispeakVendor mspVendor,
            ServiceLocation serviceLocation, MspObject mspObject, String mspMethod) {
        
        String meterNumber = meterToUpdate.getMeterNumber();

        // Verify substation name
        String substationName = getSubstationNameFromMspObjects(mspObject, mspVendor, serviceLocation);
        // Validate, verify and update substationName
        checkAndUpdateSubstationName(substationName, meterNumber, mspMethod, mspVendor, meterToUpdate);
    }

    /**
     * Return the substation name of a Meter. If the
     * Meter does not contain a substation name in its utility info, return
     * null.
     * 
     * @param mspServiceLocation
     * 
     * @return String substationName
     */
    private String getSubstationNameFromMspObjects(MspObject mspObject, MultispeakVendor mspVendor,
            ServiceLocation mspServiceLocation) {
        
        boolean useExtension = configurationSource.getBoolean(MasterConfigBoolean.MSP_ENABLE_SUBSTATIONNAME_EXTENSION);
        
        if (useExtension) {
            // custom for DEMCO/SEDC integration
            String extensionName = configurationSource.getString(MasterConfigString.MSP_SUBSTATIONNAME_EXTENSION, "readPath");
            String extensionValue;

            if (mspObject != null) {
                log.debug("Attempting to load extension value for substation name from multispeak _meter_.");
                extensionValue = getExtensionValue(mspObject.getExtensionsList(), extensionName, null);
                if (StringUtils.isNotBlank(extensionValue)) {
                    return extensionValue;
                }

                log.debug("Not found in meter. Attempting to load extension value for substation name from multispeak _serviceLocation_.");
                if (mspServiceLocation == null) {
                    log.debug("Calling CB to load ServiceLocation for Meter.");
                    mspServiceLocation = mspObjectDao.getMspServiceLocation(mspObject, mspVendor);
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

        } else {
            if (mspObject instanceof ElectricMeter) {
                ElectricMeter electricMeter = (ElectricMeter) mspObject;
                
                if (electricMeter.getElectricLocationFields() != null && 
                        electricMeter.getElectricLocationFields().getSubstationName() != null && 
                        !StringUtils.isBlank(electricMeter.getElectricLocationFields().getSubstationName())) 
                {
                    return electricMeter.getElectricLocationFields().getSubstationName();
                } 
            }
        }
        return null;
    }

    /**
     * Update the paolocation coordinates based on SERVICELOCATION object.
     */
    private void updatePaoLocation(ServiceLocation mspServiceLocation, YukonMeter meterToUpdate, String mspMethod) {
        MultispeakManagePaoLocation managePaoLocation = globalSettingDao.getEnum(GlobalSettingType.MSP_MANAGE_PAO_LOCATION,
                MultispeakManagePaoLocation.class);
        if (managePaoLocation == MultispeakManagePaoLocation.SERVICE_LOCATION) {
            if (mspServiceLocation.getGPSLocation() != null) {
                double longitude = mspServiceLocation.getGPSLocation().getLongitude();
                double latitude = mspServiceLocation.getGPSLocation().getLatitude();
                PaoLocation paoLocation = new PaoLocation(meterToUpdate.getPaoIdentifier(), latitude, longitude,
                                                          Origin.MULTISPEAK, Instant.now());
                updatePaoLocation(meterToUpdate.getMeterNumber(), meterToUpdate.getName(), paoLocation);
            }
        }
    }

    /**
     * Returns the PaoName alias value for the MultiSpeak mspMeter object.
     * 
     * @throws InsufficientMultiSpeakDataException - when paoName not found (null)
     */
    private String getPaoNameFromMspMeter(MspMeter mspMeter, MultispeakVendor mspVendor) {

        String paoName = null;
        final MspPaoNameAliasEnum paoAlias = multispeakFuncs.getPaoNameAlias();
        if (paoAlias == MspPaoNameAliasEnum.ACCOUNT_NUMBER) {
            if (mspMeter.getUtilityInfo() != null && StringUtils.isNotBlank(mspMeter.getUtilityInfo().getAccountNumber())) {
                paoName = mspMeter.getUtilityInfo().getAccountNumber();
            }
        } else if (paoAlias == MspPaoNameAliasEnum.SERVICE_LOCATION) {
            if (mspMeter.getUtilityInfo() != null && StringUtils.isNotBlank(mspMeter.getUtilityInfo().getServiceLocationID())) {
                paoName = mspMeter.getUtilityInfo().getServiceLocationID();
            }
        } else if (paoAlias == MspPaoNameAliasEnum.CUSTOMER_ID) {
            if (mspMeter.getUtilityInfo() != null && StringUtils.isNotBlank(mspMeter.getUtilityInfo().getCustomerID())) {
                paoName = mspMeter.getUtilityInfo().getCustomerID();
            }
        } else if (paoAlias == MspPaoNameAliasEnum.EA_LOCATION) {
            // updating paoName for EA_LOCATION in Electric meter
            if (mspMeter instanceof ElectricMeter) {
                ElectricMeter electricMeter = (ElectricMeter) mspMeter;
                if (electricMeter.getElectricLocationFields() != null
                        && electricMeter.getElectricLocationFields().getEaLoc() != null
                        && StringUtils.isNotBlank(electricMeter.getElectricLocationFields().getEaLoc().getValue())) {
                    paoName = electricMeter.getElectricLocationFields().getEaLoc().getValue();
                }
            }
        } else if (paoAlias == MspPaoNameAliasEnum.GRID_LOCATION) {
            paoName = (String) getModuleListFieldsForMspMeter(mspMeter).get("facilityID");

        } else if (paoAlias == MspPaoNameAliasEnum.METER_NUMBER) {
            if (StringUtils.isNotBlank(mspMeter.getMeterNo())) {
                paoName = mspMeter.getMeterNo();
            }
        } else if (paoAlias == MspPaoNameAliasEnum.POLE_NUMBER) {
            // updating paoName for pole number in Electric meter
            if (mspMeter instanceof ElectricMeter) {
                ElectricMeter electricMeter = (ElectricMeter) mspMeter;
                if (electricMeter.getElectricLocationFields() != null && 
                        StringUtils.isNotBlank(electricMeter.getElectricLocationFields().getPoleNo())) {
                    paoName = electricMeter.getElectricLocationFields().getPoleNo();
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
     * Returns the value of the paoName alias extension field from Meter object.
     * If no value is provided in the Meter object, then null is returned. NOTE:
     * meterno - this extension will return mspMeter.primaryIdentifier directly.
     */
    private String getExtensionValue(MspMeter mspMeter) {

        boolean usesExtension = multispeakFuncs.usesPaoNameAliasExtension();

        if (usesExtension) {
            String extensionName = multispeakFuncs.getPaoNameAliasExtension();
            if (extensionName.equalsIgnoreCase("meterno")) { 
                // specific field
                return mspMeter.getMeterNo();
            } else if (extensionName.equalsIgnoreCase("deviceclass")) { 
                // specific field (WHE custom)
                return (String) getModuleListFieldsForMspMeter(mspMeter).get("deviceClass");
            } else { 
                // use extensions
                return getExtensionValue(mspMeter.getExtensionsList(), extensionName, null);
            }
        }
        return null;
    }

    /**
     * Helper method to load extension value from extensionItems for
     * extensionName
     */
    private String getExtensionValue(ArrayOfExtensionsItem extensionsArr, String extensionName, String defaultValue) {
        log.debug("Attempting to load extension value for key:" + extensionName);
        List<ExtensionsItem> extensionsItem = extensionsArr != null ? extensionsArr.getExtensionsItem() : null;
        for (ExtensionsItem eItem : extensionsItem) {
            String extName = eItem.getExtName();
            if (extName.equalsIgnoreCase(extensionName)) {
                return eItem.getExtValue().getValue();
            }
        }
        log.warn("Extension " + extensionName + " key was not found. Returning default value: " + defaultValue);
        return defaultValue;
    }

    public RCDState cdMeterState(final MultispeakVendor mspVendor, final YukonMeter meter) throws MultispeakWebServiceException {

        log.info("Received " + meter.getMeterNumber() + " for CDMeterState from " + mspVendor.getCompanyName());
        if (!porterConnection.isValid()) {
            throw new MultispeakWebServiceException(
                    "Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
        }

        List<YukonMeter> allPaosToRead = Collections.singletonList(meter);
        final EnumSet<BuiltInAttribute> attributes = EnumSet.of(BuiltInAttribute.DISCONNECT_STATUS);

        LACWaitableDeviceAttributeReadCallback waitableCallback = new LACWaitableDeviceAttributeReadCallback(
                mspVendor.getRequestMessageTimeout()) {

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
                Set<BuiltInAttribute> thisAttribute = attributeService
                        .findAttributesForPoint(paoPointIdentifier.getPaoTypePointIdentifier(), attributes);
                if (thisAttribute.contains(BuiltInAttribute.DISCONNECT_STATUS)) {
                    setRCDState(multispeakFuncs.getRCDState(meter, value));
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
        multispeakEventLogService.initiateMeterRead(meter.getMeterNumber(), meter, "N/A", "GetCDMeterState",
                mspVendor.getCompanyName());
        deviceAttributeReadService.initiateRead(allPaosToRead, attributes, waitableCallback,
                DeviceRequestType.MULTISPEAK_METER_READ_EVENT, UserUtils.getYukonUser());
        try {
            waitableCallback.waitForCompletion();
        } catch (InterruptedException e) {
            /* Ignore */}
        return waitableCallback.getRCDState();
    }

    /**
     * Wrapper class for holding attribute read response value.
     */
    private abstract class LACWaitableDeviceAttributeReadCallback extends WaitableDeviceAttributeReadCallback {
        private RCDState rCDState = RCDState.UNKNOWN;

        public LACWaitableDeviceAttributeReadCallback(long timeoutInMillis) {
            super(timeoutInMillis);
        }

        public void setRCDState(RCDState rCDState) {
            this.rCDState = rCDState;
        }

        public RCDState getRCDState() {
            return rCDState;
        }
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
                            OutageEventType outageEventType = getForStatusCode(DeviceError.SUCCESS.getCode()); // assume if we got one value, then the meter must be talking successfully
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
                        OutageEventType outageEventType = getForStatusCode(DeviceError.TIMEOUT.getCode()); // unknown status if we didn't hit receivedValue at least once
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
                    // TODO there is still a potential bug here, because meters is left populated with the pao, even though an exception
                    //   has occurred. This means we can still get receivedLastValue and process it as a "success" instead of "unknown" or failure.
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