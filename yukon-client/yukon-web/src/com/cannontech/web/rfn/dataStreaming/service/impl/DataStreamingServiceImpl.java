package com.cannontech.web.rfn.dataStreaming.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.callbackResult.DataStreamingConfigCallback;
import com.cannontech.common.bulk.callbackResult.DataStreamingConfigResult;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.streaming.dao.DeviceBehaviorDao;
import com.cannontech.common.device.streaming.model.Behavior;
import com.cannontech.common.device.streaming.model.BehaviorReport;
import com.cannontech.common.device.streaming.model.BehaviorReportStatus;
import com.cannontech.common.device.streaming.model.BehaviorType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingAttribute;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigError;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigRequest;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigResponse;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigResponse.ConfigError;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.rfn.dataStreaming.DataStreamingAttributeHelper;
import com.cannontech.web.rfn.dataStreaming.DataStreamingConfigException;
import com.cannontech.web.rfn.dataStreaming.DataStreamingPorterConnection;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingAttribute;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.web.rfn.dataStreaming.model.DeviceUnsupported;
import com.cannontech.web.rfn.dataStreaming.model.GatewayLoading;
import com.cannontech.web.rfn.dataStreaming.model.VerificationInformation;
import com.cannontech.web.rfn.dataStreaming.service.DataStreamingCommunicationService;
import com.cannontech.web.rfn.dataStreaming.service.DataStreamingService;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.conns.ConnPool;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class DataStreamingServiceImpl implements DataStreamingService {

    private static final Logger log = YukonLogManager.getLogger(DataStreamingServiceImpl.class);

    public final static String STREAMING_ENABLED_STRING = "streamingEnabled";
    public final static String CHANNELS_STRING = "channels";
    public final static String ATTRIBUTE_STRING = ".attribute";
    public final static String INTERVAL_STRING = ".interval";
    public final static String ENABLED_STRING = ".enabled";

    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private ConnPool connPool;
    @Autowired private DataStreamingCommunicationService dataStreamingCommService;
    @Autowired private DataStreamingAttributeHelper dataStreamingAttributeHelper;
    @Autowired private DataStreamingPorterConnection porterConn;
    @Autowired private DeviceBehaviorDao deviceBehaviorDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private IDatabaseCache serverDatabaseCache;
    @Resource(name = "recentResultsCache") private RecentResultsCache<DataStreamingConfigResult> resultsCache;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;

    @Override
    public DataStreamingConfigResult findDataStreamingResult(String resultKey) {
        return resultsCache.getResult(resultKey);
    }
    
    @Override
    public DataStreamingConfig findDataStreamingConfiguration(int configId) {
        Behavior behavior = deviceBehaviorDao.getBehaviorById(configId);
        return convertBehaviorToConfig(behavior);
    }

    @Override
    public DataStreamingConfig findDataStreamingConfigurationForDevice(int deviceId) {
        Behavior behavior = deviceBehaviorDao.getBehaviorByDeviceIdAndType(deviceId, BehaviorType.DATA_STREAMING);
        return convertBehaviorToConfig(behavior);
    }

    @Override
    public List<DataStreamingConfig> getAllDataStreamingConfigurations() {
        List<Behavior> behaviors = deviceBehaviorDao.getBehaviorsByType(BehaviorType.DATA_STREAMING);
        List<DataStreamingConfig> configs = new ArrayList<>();
        behaviors.forEach(behavior -> configs.add(convertBehaviorToConfig(behavior)));
        return configs;
    }

    @Override
    public int saveConfig(DataStreamingConfig config) {
        Behavior behavior = convertConfigToBehavior(config);
        return deviceBehaviorDao.saveBehavior(behavior);
    }

    @Override
    public VerificationInformation verifyConfiguration(DataStreamingConfig config, List<Integer> deviceIds) {
        
        DataStreamingConfigInfo configInfo = new DataStreamingConfigInfo();
        configInfo.originalConfig = config;
        
        //Remove devices from the list that don't support data streaming
        List<Integer> unsupportedDevices = removeDataStreamingUnsupportedDevices(deviceIds);
        List<BuiltInAttribute> allConfigAttributes = config.getAttributes().stream()
                                                           .map(dsAttribute -> dsAttribute.getAttribute())
                                                           .collect(Collectors.toList());
        configInfo.addDeviceUnsupported(allConfigAttributes, unsupportedDevices);
        if (deviceIds.size() == 0) {
            // None of the selected devices support data streaming
            configInfo.exceptions.add(new DataStreamingConfigException("No devices support data streaming", "noDevices"));
            VerificationInformation verificationInfo = buildVerificationInformation(configInfo);
            return verificationInfo;
        }
        
        //Create multiple configurations for different device types if necessary
        //(Some devices may only support some of the configured attributes)
        Multimap<DataStreamingConfig, Integer> configToDeviceIds = splitConfigForDevices(config, deviceIds, configInfo);
        
        // Calculate estimated gateway loading
        List<GatewayLoading> gatewayLoadings = getEstimatedGatewayLoading(configToDeviceIds);
        configInfo.gatewayLoading = gatewayLoadings;
        for (GatewayLoading gatewayLoading : gatewayLoadings) {
            if (gatewayLoading.getProposedPercent() > 100) {
                //Estimate shows overloaded gateways. Skip sending to Network Manager since we know it will fail.
                configInfo.exceptions.add(new DataStreamingConfigException("Gateways oversubscribed", "gatewaysOversubscribed"));
                VerificationInformation verificationInfo = buildVerificationInformation(configInfo);
                return verificationInfo;
            }
        }
        
        //Build verification message for NM
        DeviceDataStreamingConfigRequest verificationRequest = dataStreamingCommService.buildVerificationRequest(configToDeviceIds);
        
        //Send verification message (Block for response message)
        try {
            DeviceDataStreamingConfigResponse response = dataStreamingCommService.sendConfigRequest(verificationRequest);
            configInfo.processVerificationResponse(response);
        } catch (DataStreamingConfigException e) {
            configInfo.exceptions.add(e);
        }
        
        //Build VerificationInfo from response message
        VerificationInformation verificationInfo = buildVerificationInformation(configInfo);
        return verificationInfo;
    }
    
    private List<GatewayLoading> getEstimatedGatewayLoading(Multimap<DataStreamingConfig, Integer> configToDeviceIds) {
        //TODO replace with real data
        GatewayLoading loading = new GatewayLoading();
        loading.setGatewayName("Gateway 1");
        loading.setCurrentPercent(93.5);
        loading.setProposedPercent(85.5);
        //loading.setDetail("");
        return Lists.newArrayList(loading);
    }
    
    /**
     * Remove the devices that do not support data streaming from the list and return them as a separate list.
     */
    private List<Integer> removeDataStreamingUnsupportedDevices(List<Integer> deviceIds) {
        
        List<Integer> unsupportedDeviceIds = new ArrayList<>();
        
        for (Integer deviceId : deviceIds) {
            LiteYukonPAObject pao = serverDatabaseCache.getAllPaosMap().get(deviceId);
            PaoType paoType = pao.getPaoType();
            boolean supports = dataStreamingAttributeHelper.supportsDataStreaming(paoType);
            if (!supports) {
                unsupportedDeviceIds.add(deviceId);
            }
        }
        deviceIds.removeAll(unsupportedDeviceIds);
        
        return unsupportedDeviceIds;
    }
    
    private Multimap<DataStreamingConfig, Integer> splitConfigForDevices(DataStreamingConfig config, List<Integer> deviceIds, 
                                                                         DataStreamingConfigInfo configInfo) {
        //sort devices by type
        Multimap<PaoType, Integer> paoTypeToDeviceIds = getPaoTypeToDeviceIds(deviceIds);
        
        //build a map of paoTypes and the config attributes they don't support
        Multimap<Set<BuiltInAttribute>, PaoType> unsupportedAttributesToTypes = getUnsupportedAttributesToTypes(config, paoTypeToDeviceIds.keySet());
        
        Multimap<DataStreamingConfig, Integer> newConfigToDevices = ArrayListMultimap.create();
        
        //create a new config for each unique set of unsupported attributes
        for (Set<BuiltInAttribute> unsupportedAttributes : unsupportedAttributesToTypes.keySet()) {
            DataStreamingConfig newConfig = config.clone();
            removeUnsupportedAttributes(newConfig, unsupportedAttributes);
            
            //Get devices whose types support this new config
            List<Integer> deviceIdsForNewConfig = new ArrayList<>();
            for (PaoType type : unsupportedAttributesToTypes.get(unsupportedAttributes)) {
                deviceIdsForNewConfig.addAll(paoTypeToDeviceIds.get(type));
                deviceIds.removeAll(deviceIdsForNewConfig);
                configInfo.addDeviceUnsupported(Lists.newArrayList(unsupportedAttributes), deviceIdsForNewConfig);
            }
            
            newConfigToDevices.putAll(newConfig, deviceIdsForNewConfig);
        }
        
        //all the deviceIds that haven't been mapped to a new config can use the original config
        newConfigToDevices.putAll(config, deviceIds);
        
        return newConfigToDevices;
    }
    
    private Multimap<PaoType, Integer> getPaoTypeToDeviceIds(Collection<Integer> deviceIds) {
        Multimap<PaoType, Integer> paoTypeToDeviceIds = ArrayListMultimap.create();
        for (Integer deviceId : deviceIds) {
            PaoType type = serverDatabaseCache.getAllPaosMap().get(deviceId).getPaoType();
            paoTypeToDeviceIds.put(type, deviceId);
        }
        return paoTypeToDeviceIds;
    }
    
    private Multimap<Set<BuiltInAttribute>, PaoType> getUnsupportedAttributesToTypes(DataStreamingConfig config, Collection<PaoType> paoTypes) {
        Multimap<Set<BuiltInAttribute>, PaoType> unsupportedAttributesToTypes = ArrayListMultimap.create();
        for (PaoType paoType : paoTypes) {
            //get subset of supported config attributes
            Collection<BuiltInAttribute> supportedAttributes = dataStreamingAttributeHelper.getSupportedAttributes(paoType);
            //get any attributes in the config that are unsupported by the type
            Set<BuiltInAttribute> unsupportedAttributes = getUnsupportedAttributes(config, supportedAttributes);
            //check the map to see if we already have this exact list of unsupported attributes
            if (unsupportedAttributes.size() > 0) {
                unsupportedAttributesToTypes.put(unsupportedAttributes, paoType);
            }
        }
        return unsupportedAttributesToTypes;
    }
    
    private Set<BuiltInAttribute> getUnsupportedAttributes(DataStreamingConfig config, Collection<BuiltInAttribute> attributes) {
        Set<BuiltInAttribute> unsupportedAttributes = 
                config.getAttributes().stream()
                                      .map(dsAttribute -> dsAttribute.getAttribute())
                                      .filter(attribute -> !attributes.contains(attribute))
                                      .collect(Collectors.toSet());
        
        return unsupportedAttributes;
    }
    
    private void removeUnsupportedAttributes(DataStreamingConfig config, Set<BuiltInAttribute> unsupportedAttributes) {
        Iterator<DataStreamingAttribute> iterator = config.getAttributes().iterator();
        while (iterator.hasNext()) {
            DataStreamingAttribute dsAttribute = iterator.next();
            if (unsupportedAttributes.contains(dsAttribute.getAttribute())) {
                iterator.remove();
            }
        }
    }
    
    private VerificationInformation buildVerificationInformation(DataStreamingConfigInfo configInfo) {
        
        VerificationInformation verificationInfo = new VerificationInformation();
        verificationInfo.setConfiguration(configInfo.originalConfig);
        verificationInfo.setDeviceUnsupported(configInfo.deviceUnsupported);
        verificationInfo.setGatewayLoadingInfo(configInfo.gatewayLoading);
        verificationInfo.setVerificationPassed(configInfo.success);
        verificationInfo.setExceptions(configInfo.exceptions);
        
        return verificationInfo;
    }
    
    @Override
    public VerificationInformation verifyConfiguration(int configId, List<Integer> deviceIds) {
        Behavior behavior = deviceBehaviorDao.getBehaviorById(configId);
        DataStreamingConfig config = convertBehaviorToConfig(behavior);
        return verifyConfiguration(config, deviceIds);
    }
    
    @Override
    public DataStreamingConfigResult unassignDataStreamingConfig(DeviceCollection deviceCollection,
            LiteYukonUser user) {
        if (isValidPorterConnection()) {
            List<Integer> deviceIds =
                    getSupportedDevices(deviceCollection).stream().map(s -> s.getDeviceId()).collect(Collectors.toList());
            deviceIds.forEach(id -> {
                BehaviorReport report;
                try {
                    report = buildPendingReport(findDataStreamingConfigurationForDevice(id), id, false);
                } catch (NotFoundException e) {
                    report = buildPendingReport(id);
                }
                deviceBehaviorDao.saveBehaviorReport(report);
            });
            deviceBehaviorDao.unassignBehavior(BehaviorType.DATA_STREAMING, deviceIds);
            return sendConfiguration(user, deviceCollection);
        } else {
            return createNoPorterConnectionResult(deviceCollection);
        }
    }
    
    @Override
    public DataStreamingConfigResult assignDataStreamingConfig(int configId, DeviceCollection deviceCollection,
            LiteYukonUser user) {
        if (isValidPorterConnection()) {
            List<Integer> deviceIds =
                getSupportedDevices(deviceCollection).stream().map(s -> s.getDeviceId()).collect(Collectors.toList());
            if (!deviceIds.isEmpty()) {
                deviceBehaviorDao.assignBehavior(configId, BehaviorType.DATA_STREAMING, deviceIds);
                deviceIds.forEach(id -> {
                    BehaviorReport report = buildPendingReport(findDataStreamingConfigurationForDevice(id), id, true);
                    deviceBehaviorDao.saveBehaviorReport(report);
                });
            }
            return sendConfiguration(user, deviceCollection);
        } else {
            return createNoPorterConnectionResult(deviceCollection);
        }
    }

    private DataStreamingConfigResult sendConfiguration(LiteYukonUser user, DeviceCollection deviceCollection) {
        log.info("Attemting to send configuration command to "+deviceCollection.getDeviceCount()+" devices.");
        final DataStreamingConfigResult result = new DataStreamingConfigResult();
        String resultsId = resultsCache.addResult(result);
        result.setResultsId(resultsId);
        final CommandRequestExecution execution = commandRequestExecutionDao.createStartedExecution(
            CommandRequestType.DEVICE, DeviceRequestType.DATA_STREAMING_CONFIG, 0, user);
        result.setExecution(execution);

        final StoredDeviceGroup allDevicesGroup = tempDeviceGroupService.createTempGroup();
        final StoredDeviceGroup successGroup = tempDeviceGroupService.createTempGroup();
        final StoredDeviceGroup failedGroup = tempDeviceGroupService.createTempGroup();
        final StoredDeviceGroup canceledGroup = tempDeviceGroupService.createTempGroup();
        final StoredDeviceGroup unsupportedGroup = tempDeviceGroupService.createTempGroup();

        result.setAllDevicesCollection(deviceGroupCollectionHelper.buildDeviceCollection(allDevicesGroup));
        result.setSuccessDeviceCollection(deviceGroupCollectionHelper.buildDeviceCollection(successGroup));
        result.setFailureDeviceCollection(deviceGroupCollectionHelper.buildDeviceCollection(failedGroup));
        result.setCanceledDeviceCollection(deviceGroupCollectionHelper.buildDeviceCollection(canceledGroup));
        result.setUnsupportedCollection(deviceGroupCollectionHelper.buildDeviceCollection(unsupportedGroup));
        
        DataStreamingConfigCallback callback = new DataStreamingConfigCallbackImpl(result, successGroup, failedGroup, canceledGroup,
                                                                                   execution, deviceCollection.getDeviceList());
        
        result.setConfigCallback(callback);
        List<SimpleDevice> allDevices = deviceCollection.getDeviceList();
        deviceGroupMemberEditorDao.addDevices(allDevicesGroup, allDevices);
        List<SimpleDevice> unsupportedDevices = new ArrayList<>();
        List<SimpleDevice> supportedDevices = getSupportedDevices(deviceCollection);
        unsupportedDevices.addAll(allDevices);
        unsupportedDevices.removeAll(supportedDevices);
        if (!unsupportedDevices.isEmpty()) {
            log.info(unsupportedDevices.size()+" devices are unsupported.");
            deviceGroupMemberEditorDao.addDevices(unsupportedGroup, unsupportedDevices);
            commandRequestExecutionResultDao.saveUnsupported(new HashSet<>(unsupportedDevices), execution.getId(),
                CommandRequestUnsupportedType.UNSUPPORTED);
        }
        log.info("Sending configuration command to " + supportedDevices.size() + " devices.");
        if (supportedDevices.isEmpty()) {
            callback.complete();
        } else {
            List<CommandRequestDevice> commands = porterConn.buildConfigurationCommandRequests(supportedDevices);
            porterConn.sendConfiguration(commands, result, user);
        }
        updateRequestCount(execution, supportedDevices.size());
        return result;
    }
    
    private List<SimpleDevice> getSupportedDevices(DeviceCollection deviceCollection) {
        List<SimpleDevice> supportedDevices =
            deviceCollection.getDeviceList().stream().filter((x) -> x.getDeviceType().isRfMeter()).collect(
                Collectors.toList());
        return supportedDevices;
    }
    
    private void updateRequestCount(CommandRequestExecution execution, int count){
        execution.setRequestCount(count);
        if (log.isDebugEnabled()) {
            log.debug("Updating request count to " + count +".");
        }
        commandRequestExecutionDao.saveOrUpdate(execution);
    }
    
    private boolean isValidPorterConnection() {
        if (!connPool.getDefPorterConn().isValid()) {
            log.error("Porter connection is invalid.");
            return false;
        }
        return true;
    }
    
    private DataStreamingConfigResult createNoPorterConnectionResult(DeviceCollection deviceCollection){
        DataStreamingConfigResult result = new DataStreamingConfigResult();

        StoredDeviceGroup successGroup = tempDeviceGroupService.createTempGroup();
        StoredDeviceGroup failedGroup = tempDeviceGroupService.createTempGroup();
        StoredDeviceGroup canceledGroup = tempDeviceGroupService.createTempGroup();
        StoredDeviceGroup unsupportedGroup = tempDeviceGroupService.createTempGroup();

        result.setAllDevicesCollection(deviceCollection);
        result.setSuccessDeviceCollection(deviceGroupCollectionHelper.buildDeviceCollection(successGroup));
        result.setFailureDeviceCollection(deviceGroupCollectionHelper.buildDeviceCollection(failedGroup));
        result.setCanceledDeviceCollection(deviceGroupCollectionHelper.buildDeviceCollection(canceledGroup));
        result.setUnsupportedCollection(deviceGroupCollectionHelper.buildDeviceCollection(unsupportedGroup));
        result.setExceptionReason("Porter connection is invalid.");
        String resultsId = resultsCache.addResult(result);
        result.setResultsId(resultsId);
        result.complete();
        return result;
    }

    @Override
    public void cancel(String key, LiteYukonUser user) {
        DataStreamingConfigResult result = resultsCache.getResult(key);
        result.getCommandCompletionCallback().cancel();
        porterConn.cancel(result ,user);
    }

    /**
     * Converts behavior to config
     *
     * Behavior values example:
     * { "channels", "2" },
     * { "channels.0.attribute", "VOLTAGE" }
     * { "channels.0.interval", "3" },
     * { "channels.1.attribute", "DEMAND" }
     * { "channels.1.interval", "7"};
     * 
     * Data Streaming Config example:
     * VOLTAGE,3
     * DEMAND,7
     */
    private DataStreamingConfig convertBehaviorToConfig(Behavior behavior) {
        DataStreamingConfig config = new DataStreamingConfig();
        config.setId(behavior.getId());
        int i = 0;
        int channels = behavior.getIntValue(CHANNELS_STRING);
        for (i = 0; i < channels; i++) {
            String key = CHANNELS_STRING + "." + i;
            BuiltInAttribute attributeValue = behavior.getEnumValue(key + ATTRIBUTE_STRING, BuiltInAttribute.class);
            int intervalValue = behavior.getIntValue(key + INTERVAL_STRING);
            DataStreamingAttribute dataStreamingAttribute = new DataStreamingAttribute();
            dataStreamingAttribute.setAttribute(attributeValue);
            dataStreamingAttribute.setInterval(intervalValue);
            dataStreamingAttribute.setAttributeOn(Boolean.TRUE);
            config.addAttribute(dataStreamingAttribute);
        }
        return config;
    }

    /**
     * Converts config to behavior
     */
    private Behavior convertConfigToBehavior(DataStreamingConfig config) {
        Behavior behavior = new Behavior();
        behavior.setType(BehaviorType.DATA_STREAMING);
        // only enabled attributes are stored in the database
        List<DataStreamingAttribute> attributes =
            config.getAttributes().stream().filter(x -> x.getAttributeOn() == Boolean.TRUE).collect(
                Collectors.toList());
        behavior.addValue(CHANNELS_STRING, attributes.size());
        for (int i = 0; i < attributes.size(); i++) {
            BuiltInAttribute attribute = attributes.get(i).getAttribute();
            int interval = attributes.get(i).getInterval();
            String key = CHANNELS_STRING + "." + i;
            behavior.addValue(key + ATTRIBUTE_STRING, attribute);
            behavior.addValue(key + INTERVAL_STRING, interval);
        }
        return behavior;
    }
        
    private BehaviorReport buildConfirmedReport(ReportedDataStreamingConfig config, int deviceId) {
        BehaviorReport report = new BehaviorReport();
        report.setType(BehaviorType.DATA_STREAMING);
        report.setDeviceId(deviceId);
        report.setStatus(BehaviorReportStatus.CONFIRMED);
        report.setTimestamp(new Instant());
        List<ReportedDataStreamingAttribute> attributes = config.getAttributes();
        report.addValue(CHANNELS_STRING, attributes.size());
        report.addValue(STREAMING_ENABLED_STRING, config.isStreamingEnabled());
        for (int i = 0; i < attributes.size(); i++) {
            BuiltInAttribute attribute = BuiltInAttribute.valueOf(attributes.get(i).getAttribute());
            int interval = attributes.get(i).getInterval();
            String key = CHANNELS_STRING + "." + i;
            report.addValue(key + ATTRIBUTE_STRING, attribute);
            report.addValue(key + INTERVAL_STRING, interval);
            report.addValue(key + ENABLED_STRING, attributes.get(i).isEnabled());
        }
        return report;
    }

    private BehaviorReport buildPendingReport(DataStreamingConfig config, int deviceId, boolean enabled) {
        BehaviorReport report = new BehaviorReport();
        report.setType(BehaviorType.DATA_STREAMING);
        report.setDeviceId(deviceId);
        report.setStatus(BehaviorReportStatus.PENDING);
        report.setTimestamp(new Instant());
        List<DataStreamingAttribute> attributes = config.getAttributes();
        report.addValue(CHANNELS_STRING, attributes.size());
        report.addValue(STREAMING_ENABLED_STRING, enabled);
        for (int i = 0; i < attributes.size(); i++) {
            BuiltInAttribute attribute = attributes.get(i).getAttribute();
            int interval = attributes.get(i).getInterval();
            String key = CHANNELS_STRING + "." + i;
            report.addValue(key + ATTRIBUTE_STRING, attribute);
            report.addValue(key + INTERVAL_STRING, interval);
            report.addValue(key + ENABLED_STRING, true);
        }
        return report;
    }
    
    /**
     * The user is trying to remove data streaming from the device that does NOT have a data streaming assigned.
     * This method builds a pending report that has steaming enabled set to false and has no channels.
     */
    private BehaviorReport buildPendingReport(int deviceId) {
        BehaviorReport report = new BehaviorReport();
        report.setType(BehaviorType.DATA_STREAMING);
        report.setDeviceId(deviceId);
        report.setStatus(BehaviorReportStatus.PENDING);
        report.setTimestamp(new Instant());
        report.addValue(CHANNELS_STRING, 0);
        report.addValue(STREAMING_ENABLED_STRING, false);
        return report;
    }


    private void completeCommandRequestExecutionRecord(CommandRequestExecution cre,
            CommandRequestExecutionStatus status) {
        cre.setStopTime(new Date());
        cre.setCommandRequestExecutionStatus(status);
        commandRequestExecutionDao.saveOrUpdate(cre);
    }
    
    /**
     * Internal data class to consolidate info about a data streaming configuration attempt.
     */
    private final class DataStreamingConfigInfo {
        public DataStreamingConfig originalConfig;
        public List<DataStreamingConfigException> exceptions = new ArrayList<>();
        public List<DeviceUnsupported> deviceUnsupported = new ArrayList<>();
        public List<GatewayLoading> gatewayLoading = new ArrayList<>();
        public boolean success = true;
        
        public void addDeviceUnsupported(List<BuiltInAttribute> attributes, List<Integer> deviceIds) {
            DeviceUnsupported unsupported = new DeviceUnsupported();
            unsupported.setAttributes(attributes);
            unsupported.setDeviceIds(deviceIds);
            // TODO Can we add these in the controller?
            // unsupported.setDetail(detail);
            // unsupported.setDeviceCollection(deviceCollection);
            deviceUnsupported.add(unsupported);
        }
        
        public void processVerificationResponse(DeviceDataStreamingConfigResponse response) {
            switch (response.getResponseType()) {
                case ACCEPTED:
                    // No need to do anything, the response was success
                    break;
                case REJECTED:
                    success = false;
                    Map<RfnIdentifier, Double> oversubscribedGateways = response.getOverSubscribedGateways();
                    if (oversubscribedGateways.size() > 0) {
                        log.debug("Data streaming verification response - gateways oversubscribed.");
                        exceptions.add(new DataStreamingConfigException("Gateways oversubscribed", "gatewaysOversubscribed"));
                        for (Entry<RfnIdentifier, Double> entry : oversubscribedGateways.entrySet()) {
                            RfnDevice gateway = rfnDeviceDao.getDeviceForExactIdentifier(entry.getKey());
                            boolean gatewayLoadingFound = false;
                            for (GatewayLoading loading : gatewayLoading) {
                                if (loading.getGatewayName().equals(gateway.getName())) {
                                    loading.setProposedPercent(entry.getValue());
                                    gatewayLoadingFound = true;
                                    break;
                                }
                            }
                            if (!gatewayLoadingFound) {
                                //TODO replace with real data
                                GatewayLoading loading = new GatewayLoading();
                                loading.setGatewayName(gateway.getName());
                                loading.setCurrentPercent(12.3);
                                loading.setProposedPercent(entry.getValue());
                                gatewayLoading.add(loading);
                            }
                        }
                    }
                    
                    for (Entry<RfnIdentifier, ConfigError> errorDeviceEntry : response.getErrorConfigedDevices().entrySet()) {
                        DeviceDataStreamingConfigError errorType = errorDeviceEntry.getValue().getErrorType();
                        //Ignore Gateway overloaded errors, those are covered above
                        if (errorType != DeviceDataStreamingConfigError.GATEWAY_OVERLOADED) {
                            RfnDevice deviceName = rfnDeviceDao.getDeviceForExactIdentifier(errorDeviceEntry.getKey());
                            log.debug("Data streaming verification response - device error: " + deviceName + ", " + errorType.name());
                            String deviceError = "Device error for " + deviceName + ": " + errorType;
                            String i18nKey = "device." + errorType.name();
                            exceptions.add(new DataStreamingConfigException(deviceError , i18nKey, deviceName));
                        }
                    }
                    break;
                case NETWORK_MANAGER_FAILURE:
                    // Exception was thrown in NM processing and it couldn't complete the task
                    success = false;
                    String nmError = "Network Manager encountered an error during data streaming configuration verification. ";
                    log.error(nmError + response.getResponseMessage());
                    exceptions.add(new DataStreamingConfigException(nmError, "networkManagerFailure"));
                    break;
                case OTHER_ERROR:
                default:
                    //unknown error
                    success = false;
                    String unknownError = "Unknown error during data streaming configuration verification. ";
                    log.error(unknownError + response.getResponseMessage()); 
                    exceptions.add(new DataStreamingConfigException(unknownError, "unknownError"));
            }
        }
    }
    
    /**
     * Callback to handle data streaming config responses.
     */
    public class DataStreamingConfigCallbackImpl implements DataStreamingConfigCallback {
        private DataStreamingConfigResult result; 
        private StoredDeviceGroup successGroup;
        private StoredDeviceGroup failedGroup;
        private StoredDeviceGroup cancelGroup;
        private CommandRequestExecution execution;
        private List<SimpleDevice> deviceList;
        
        public DataStreamingConfigCallbackImpl(DataStreamingConfigResult result, StoredDeviceGroup successGroup,
                StoredDeviceGroup failedGroup, StoredDeviceGroup cancelGroup, CommandRequestExecution execution,
                List<SimpleDevice> deviceList) {
            this.result = result;
            this.successGroup = successGroup;
            this.failedGroup = failedGroup;
            this.execution = execution;
            this.deviceList = deviceList;
            this.cancelGroup = cancelGroup;
        }
        
        @Override
        public void receivedConfigReport(SimpleDevice device, ReportedDataStreamingConfig config) {
            deviceGroupMemberEditorDao.addDevices(successGroup, device);
            BehaviorReport report = buildConfirmedReport(config, device.getDeviceId());
            deviceBehaviorDao.saveBehaviorReport(report);
        }

        @Override
        public void receivedConfigError(SimpleDevice device, SpecificDeviceErrorDescription error) {
            log.debug("Recived a config error for device=" + device + " error=" + error.getDescription());
            deviceGroupMemberEditorDao.addDevices(failedGroup, device);
            deviceBehaviorDao.updateBehaviorReportStatus(BehaviorType.DATA_STREAMING, BehaviorReportStatus.FAILED,
                Arrays.asList(device.getDeviceId()));
        }

        @Override
        public void complete() {
            if (!result.isComplete()) {
                result.complete();
                log.info("Command Complete");
                completeCommandRequestExecutionRecord(execution, CommandRequestExecutionStatus.COMPLETE);
            }
        }
        
        @Override
        public boolean isComplete() {
            return result.isComplete();
        }

        @Override
        public void processingExceptionOccured(String reason) {
            result.complete();
            completeCommandRequestExecutionRecord(execution, CommandRequestExecutionStatus.FAILED);
            result.setExceptionReason(reason);
        }

        @Override
        public void cancel(LiteYukonUser yukonUser) {
            if (!result.isComplete()) {
                log.info("Command Canceled");
                result.complete();
                List<SimpleDevice> canceledDevices = new ArrayList<>();
                canceledDevices.addAll(deviceList);
                canceledDevices.removeAll(result.getSuccessDeviceCollection().getDeviceList());
                canceledDevices.removeAll(result.getFailureDeviceCollection().getDeviceList());
                canceledDevices.removeAll(result.getUnsupportedDeviceCollection().getDeviceList());
                completeCommandRequestExecutionRecord(execution, CommandRequestExecutionStatus.CANCELING);
                commandRequestExecutionResultDao.saveUnsupported(
                    new HashSet<>(result.getCanceledDeviceCollection().getDeviceList()), execution.getId(),
                    CommandRequestUnsupportedType.CANCELED);
                deviceBehaviorDao.updateBehaviorReportStatus(BehaviorType.DATA_STREAMING,
                    BehaviorReportStatus.CANCELED,
                    canceledDevices.stream().map(s -> s.getDeviceId()).collect(Collectors.toList()));
                deviceGroupMemberEditorDao.addDevices(cancelGroup, canceledDevices);
                updateRequestCount(execution, result.getFailureCount() + result.getSuccessCount());
            }
        }
    }
}
