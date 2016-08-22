package com.cannontech.web.rfn.dataStreaming.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTimeConstants;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.model.SimpleMeter;
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
import com.cannontech.common.rfn.dataStreaming.DataStreamingAttributeHelper;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingAttribute;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfig;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigError;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigRequest;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigRequestType;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigResponse;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigResponse.ConfigError;
import com.cannontech.common.rfn.message.datastreaming.gateway.GatewayDataStreamingInfo;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.rfn.dataStreaming.DataStreamingConfigException;
import com.cannontech.web.rfn.dataStreaming.DataStreamingPorterConnection;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingAttribute;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.web.rfn.dataStreaming.model.DeviceUnsupported;
import com.cannontech.web.rfn.dataStreaming.model.DiscrepancyResult;
import com.cannontech.web.rfn.dataStreaming.model.GatewayLoading;
import com.cannontech.web.rfn.dataStreaming.model.SummarySearchCriteria;
import com.cannontech.web.rfn.dataStreaming.model.SummarySearchResult;
import com.cannontech.web.rfn.dataStreaming.model.VerificationInformation;
import com.cannontech.web.rfn.dataStreaming.service.DataStreamingCommunicationService;
import com.cannontech.web.rfn.dataStreaming.service.DataStreamingService;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.conns.ConnPool;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import com.google.common.collect.Ranges;
import com.google.common.collect.Sets;

public class DataStreamingServiceImpl implements DataStreamingService {

    private static final Logger log = YukonLogManager.getLogger(DataStreamingServiceImpl.class);

    public final static BehaviorType TYPE = BehaviorType.DATA_STREAMING;
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
    @Autowired private RfnGatewayService rfnGatewayService;
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
        Behavior behavior = deviceBehaviorDao.findBehaviorByDeviceIdAndType(deviceId, BehaviorType.DATA_STREAMING);
        return convertBehaviorToConfig(behavior);
    }

    @Override
    public List<DataStreamingConfig> getAllDataStreamingConfigurations() {
        List<Behavior> behaviors = deviceBehaviorDao.getAllBehaviorsByType(TYPE);
        List<DataStreamingConfig> configs = new ArrayList<>();
        behaviors.forEach(behavior -> configs.add(convertBehaviorToConfig(behavior)));
        return configs;
    }
    
    @Override
    public Map<DataStreamingConfig, DeviceCollection> getAllDataStreamingConfigurationsAndDevices() {
        Map<DataStreamingConfig, DeviceCollection> configToDeviceCollection = new HashMap<>();
        Map<Integer, Behavior> deviceIdToBehavior = deviceBehaviorDao.getBehaviorsByType(TYPE);
        Multimap<DataStreamingConfig, Integer> configsToDeviceIds = HashMultimap.create();
        for (Entry<Integer, Behavior> entry : deviceIdToBehavior.entrySet()) {
            configsToDeviceIds.put(convertBehaviorToConfig(entry.getValue()), entry.getKey());
        }
        configsToDeviceIds.keySet().forEach(config -> {
            configToDeviceCollection.put(config, createDeviceCollectionForIds(configsToDeviceIds.get(config)));
        });
        return configToDeviceCollection;
    }
       
    /**
     * Finds gateways based on the user selected criteria (User can select 1 or more gateways).
     * Filters the gateways based on the loading percent selected by the user.
     * 
     * @return a map of RfnIdentifier to RFNGateway
     * @throws DataStreamingConfigException 
     */
    private Map<RfnIdentifier, RfnGateway> getGatewaysForLoadingRange(SummarySearchCriteria criteria) throws DataStreamingConfigException{
        List<RfnGateway> gateways = new ArrayList<>();
        
        try {
            if (criteria.isGatewaySelected()) {
                gateways.addAll(rfnGatewayService.getGatewaysByPaoIdsWithData(criteria.getSelectedGatewayIds()));
            } else {
                gateways.addAll(rfnGatewayService.getAllGatewaysWithData());
            }
        } catch (NmCommunicationException e) {
            throw new DataStreamingConfigException("Communication error requesting gateway data from Network Manager.",
                e, "commsError");
        }
        
        Double min = criteria.getMinLoadPercent() == null ? Double.MIN_VALUE : criteria.getMinLoadPercent();
        Double max = criteria.getMaxLoadPercent() == null ? Double.MAX_VALUE : criteria.getMaxLoadPercent();
        Range<Double> range = Ranges.closed(min, max);
        Iterator<RfnGateway> it = gateways.iterator();
        while (it.hasNext()) {
            RfnGateway gateway = it.next();
            double loadingPercent = gateway.getData().getDataStreamingLoadingPercent();
            if (!range.contains(loadingPercent)) {
                it.remove();
            }
        }
        return Maps.uniqueIndex(gateways, c -> c.getRfnIdentifier());
    }
    
    @Override
    public List<SummarySearchResult> search(SummarySearchCriteria criteria) throws DataStreamingConfigException {
        log.debug(criteria);
        List<SummarySearchResult> results = new ArrayList<>();
        Map<Integer, DeviceInfo> deviceIdToDeviceInfo = new HashMap<>();
        Map<RfnIdentifier, RfnGateway> gatewaysForLoadingRange = getGatewaysForLoadingRange(criteria);

        Collection<GatewayDataStreamingInfo> gatewayInfos =
            dataStreamingCommService.getGatewayInfo(rfnGatewayService.getAllGateways());
        
        for (GatewayDataStreamingInfo gatewayInfo : gatewayInfos) {
            for (RfnIdentifier identifier : gatewayInfo.getDeviceRfnIdentifiers().keySet()) {
                DeviceInfo deviceToGateway = new DeviceInfo();
                // Identifiers are cached
                deviceToGateway.device = rfnDeviceDao.getDeviceForExactIdentifier(identifier);
                deviceToGateway.gatewayRfnIdentifier = gatewayInfo.getGatewayRfnIdentifier();
                deviceIdToDeviceInfo.put(deviceToGateway.device.getPaoIdentifier().getPaoId(), deviceToGateway);
            }
        }
        Integer selectedConfigId = criteria.isConfigSelected() ? criteria.getSelectedConfiguration() : null;
        List<BuiltInAttribute> attributes = criteria.getBuiltInAttributes();
        Integer interval = criteria.isConfigIntervalSelected() ? criteria.getSelectedInterval() : null;
        Map<Integer, Integer> deviceIdsToBehaviorIds =
            deviceBehaviorDao.getDeviceIdsToBehaviorIdMap(TYPE, attributes, interval, selectedConfigId);

        Map<Integer, DataStreamingConfig> configs = deviceIdsToBehaviorIds.values().stream().distinct().collect(
            Collectors.toMap(id -> id, id -> findDataStreamingConfiguration(id)));

        for (Map.Entry<Integer, Integer> entry : deviceIdsToBehaviorIds.entrySet()) {
            int deviceId = entry.getKey();
            int configId = entry.getValue();
            DeviceInfo deviceInfo = deviceIdToDeviceInfo.get(deviceId);
            SummarySearchResult result = new SummarySearchResult();
            DataStreamingConfig config = configs.get(configId);
            result.setConfig(config);
            if (deviceInfo == null) {
                result.setMeter(rfnDeviceDao.getDeviceForId(deviceId));
                log.error("GatewayDataStreamingInfo didn't include device=" + result.getMeter());
                results.add(result);
            } else {
                RfnGateway gateway = gatewaysForLoadingRange.get(deviceInfo.gatewayRfnIdentifier);
                if (gateway != null) {
                    // gateway is in range selected by the user
                    result.setMeter(deviceInfo.device);
                    result.setGateway(gateway);
                    results.add(result);
                }
            }
        }
        return results;
    }
    
    private static class DeviceInfo{
        RfnIdentifier gatewayRfnIdentifier;
        RfnDevice device;
    }
    
    @Override
    public List<DiscrepancyResult> findDiscrepancies() {
        List<DiscrepancyResult> results = new ArrayList<>();
        
        // deviceId, config
        Map<Integer, Behavior> deviceIdToBehavior = deviceBehaviorDao.getBehaviorsByType(TYPE);

        // deviceId, report
        Map<Integer, BehaviorReport> deviceIdToReport = deviceBehaviorDao.getBehaviorReportsByType(TYPE);

        Set<Integer> deviceIds = new HashSet<>();
        deviceIds.addAll(deviceIdToBehavior.keySet());
        deviceIds.addAll(deviceIdToReport.keySet());

        for (int deviceId : deviceIds) {
            /*
             * Assume that each device assigned to a behavior has an entry in behavior report table.
             * There might be an entry in a behavior report table but device might not be assigned to behavior.
             * Example: device was unassigned, there will be an entry only in behavior report table but not in behavior.
             * Pending entries should only show up after 24 hours.
             */
            BehaviorReport report = deviceIdToReport.get(deviceId);
            if (report != null && checkPending(report)) {
                Behavior behavior = deviceIdToBehavior.get(deviceId);

                // behavior
                DataStreamingConfig expectedConfig = convertBehaviorToConfig(behavior);
                // behavior report
                DataStreamingConfig actualConfig = convertBehaviorToConfig(report);

                if (expectedConfig == null || hasDiscrepancy(expectedConfig.getAttributes(), actualConfig.getAttributes())) {
                    DiscrepancyResult result = new DiscrepancyResult();
                    result.setActual(actualConfig);
                    result.setExpected(expectedConfig);
                    result.setStatus(report.getStatus());
                    result.setLastCommunicated(new Instant());
                    result.setPaoName(serverDatabaseCache.getAllPaosMap().get(deviceId).getPaoName());
                    result.setDeviceId(deviceId);
                    results.add(result);
                }
            }
        }

        return results;
    }
    
 
    /**
     * Pending entries should only show up after 24 hours.
     * 
     * @return true - if the behavior can be shown
     */
    private boolean checkPending(BehaviorReport report) {
        if (report.getStatus() == BehaviorReportStatus.PENDING) {
            return false;
        }
        return true;
    }

    /**
     * Compares expected and actual attributes, returns true if attributes are not equal.
     */
    private boolean hasDiscrepancy(List<DataStreamingAttribute> expected, List<DataStreamingAttribute> actual) {
        // actual can be null if the behavior was assigned and porter didn't update behavior report table.
        if (actual == null) {
            return true;
        }
        return !Sets.difference(Sets.newHashSet(expected), Sets.newHashSet(actual)).isEmpty();
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
            configInfo.success = false;
            configInfo.exceptions.add(new DataStreamingConfigException("No devices support data streaming", "noDevices"));
            VerificationInformation verificationInfo = buildVerificationInformation(configInfo);
            return verificationInfo;
        }
        
        //Create multiple configurations for different device types if necessary
        //(Some devices may only support some of the configured attributes)
        Multimap<DataStreamingConfig, Integer> configToDeviceIds = splitConfigForDevices(config, deviceIds, configInfo);
        
        //Build verification message for NM
        DeviceDataStreamingConfigRequest verificationRequest = dataStreamingCommService.buildVerificationRequest(configToDeviceIds);
        
        //Send verification message (Block for response message)
        try {
            DeviceDataStreamingConfigResponse response = dataStreamingCommService.sendConfigRequest(verificationRequest);
            configInfo.processVerificationResponse(response);
        } catch (DataStreamingConfigException e) {
            configInfo.exceptions.add(e);
            configInfo.success = false;
        }
        
        //Build VerificationInfo from response message
        VerificationInformation verificationInfo = buildVerificationInformation(configInfo);
        return verificationInfo;
    }
    
    private void processConfigResponse(DeviceDataStreamingConfigResponse response) throws DataStreamingConfigException {
        switch (response.getResponseType()) {
        case ACCEPTED:
            // NM accepted the configuration. It can now be sent to the devices.
            break;
        case CONFIG_ERROR:
            boolean gatewaysOverloaded = false;
            Map<RfnIdentifier, GatewayDataStreamingInfo> affectedGateways = response.getAffectedGateways();
            for (RfnIdentifier rfnId : affectedGateways.keySet()) {
                GatewayDataStreamingInfo info = affectedGateways.get(rfnId);
                if ((info.getResultLoading() / info.getMaxCapacity()) > 1) {
                    gatewaysOverloaded = true;
                    break;
                }
            }
            
            if (gatewaysOverloaded) {
                log.debug("Data streaming config response - gateways oversubscribed.");
                throw new DataStreamingConfigException("Gateways oversubscribed", "gatewaysOversubscribed");
            }
            
            for (Entry<RfnIdentifier, ConfigError> errorDeviceEntry : response.getErrorConfigedDevices().entrySet()) {
                DeviceDataStreamingConfigError errorType = errorDeviceEntry.getValue().getErrorType();
                //Ignore Gateway overloaded errors, those are covered above
                if (errorType != DeviceDataStreamingConfigError.GATEWAY_OVERLOADED) {
                    RfnDevice deviceName = rfnDeviceDao.getDeviceForExactIdentifier(errorDeviceEntry.getKey());
                    log.debug("Data streaming verification response - device error: " + deviceName + ", " + errorType.name());
                    String deviceError = "Device error for " + deviceName + ": " + errorType;
                    String i18nKey = "device." + errorType.name();
                    throw new DataStreamingConfigException(deviceError , i18nKey, deviceName);
                }
            }
            break;
        case INVALID_REQUEST_TYPE:
        case INVALID_REQUEST_ID:
        case NO_CONFIGS:
        case NO_DEVICES:
            // The request contained unacceptable data
            String requestError = "Network Manager rejected the request as malformed: " + response.getResponseType();
            log.error(requestError + ". " + response.getResponseMessage());
            throw new DataStreamingConfigException(requestError, "badRequest", response.getResponseType());
        case NETWORK_MANAGER_FAILURE:
            // Exception was thrown in NM processing and it couldn't complete the task
            String nmError = "Network Manager encountered an error during data streaming configuration. ";
            log.error(nmError + response.getResponseMessage());
            throw new DataStreamingConfigException(nmError, "networkManagerFailure");
        case OTHER_ERROR:
        default:
            //unknown error
            String unknownError = "Unknown error during data streaming configuration. ";
            log.error(unknownError + response.getResponseMessage()); 
            throw new DataStreamingConfigException(unknownError, "unknownError");
        }
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
                if (configInfo != null) {
                    configInfo.addDeviceUnsupported(Lists.newArrayList(unsupportedAttributes), deviceIdsForNewConfig);
                }
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
    public DataStreamingConfigResult resend(List<Integer> deviceIds, LiteYukonUser user)
            throws DataStreamingConfigException {
        
        DeviceCollection deviceCollection = createDeviceCollectionForIds(deviceIds);
        if (isValidPorterConnection()) {
            String correlationId = UUID.randomUUID().toString();

            Map<Integer, Behavior> deviceIdToBehavior =
                deviceBehaviorDao.getBehaviorsByTypeAndDeviceIds(TYPE, deviceIds);
            Multimap<DataStreamingConfig, Integer> configsToDeviceIds = HashMultimap.create();
            for (Entry<Integer, Behavior> entry : deviceIdToBehavior.entrySet()) {
                configsToDeviceIds.put(convertBehaviorToConfig(entry.getValue()), entry.getKey());
            }
            for (DataStreamingConfig config : configsToDeviceIds.keys()) {
                sendNmConfiguration(config, Lists.newArrayList(configsToDeviceIds.get(config)), correlationId);
            }
            Map<Integer, BehaviorReport> reports =
                deviceBehaviorDao.getBehaviorReportsByTypeAndDeviceIds(TYPE, deviceIds);
            deviceIds.forEach(id -> {
                BehaviorReport report = reports.get(id);
                if (report == null) {
                    // shouldn't happen
                    report = buildPendingReport(id, true);
                } else {
                    report.setStatus(BehaviorReportStatus.PENDING);
                    report.setTimestamp(new Instant());
                }
                deviceBehaviorDao.saveBehaviorReport(report);
            });
            return sendConfiguration(user, deviceCollection, correlationId);
        } else {
            return createEmptyConnectionResult(deviceCollection, "Porter connection is invalid.");
        }
    }

    @Override
    public DataStreamingConfigResult unassignDataStreamingConfig(DeviceCollection deviceCollection, LiteYukonUser user)
            throws DataStreamingConfigException {

        if (isValidPorterConnection()) {
            List<Integer> deviceIds =
                getSupportedDevices(deviceCollection).stream().map(s -> s.getDeviceId()).collect(Collectors.toList());
            String correlationId = UUID.randomUUID().toString();

            sendNmConfigurationRemove(deviceIds, correlationId);
            Map<Integer, BehaviorReport> reports =
                deviceBehaviorDao.getBehaviorReportsByTypeAndDeviceIds(TYPE, deviceIds);
            deviceIds.forEach(id -> {
                BehaviorReport report = reports.get(id);
                if (report == null) {
                    report = buildPendingReport(id, false);
                } else {
                    report.setStatus(BehaviorReportStatus.PENDING);
                    report.setTimestamp(new Instant());
                    report.addValue(STREAMING_ENABLED_STRING, false);
                }
                deviceBehaviorDao.saveBehaviorReport(report);
            });
            deviceBehaviorDao.unassignBehavior(TYPE, deviceIds);
            return sendConfiguration(user, deviceCollection, correlationId);
        } else {
            return createEmptyConnectionResult(deviceCollection, "Porter connection is invalid.");
        }
    }
    
    @Override
    public DataStreamingConfigResult unassignDataStreamingConfig(List<Integer> deviceIds, LiteYukonUser user)
            throws DataStreamingConfigException {
        DeviceCollection deviceCollection = createDeviceCollectionForIds(deviceIds);
        return unassignDataStreamingConfig(deviceCollection, user);
    }

    @Override
    public DataStreamingConfigResult assignDataStreamingConfig(DataStreamingConfig config,
            DeviceCollection deviceCollection, LiteYukonUser user) throws DataStreamingConfigException {
        
        if (isValidPorterConnection()) {
            List<Integer> deviceIds =
                getSupportedDevices(deviceCollection).stream().map(s -> s.getDeviceId()).collect(Collectors.toList());
            String correlationId = UUID.randomUUID().toString();
        
            sendNmConfiguration(config, deviceIds, correlationId);
         
            if (!deviceIds.isEmpty()) {
                deviceBehaviorDao.assignBehavior(config.getId(), TYPE, deviceIds);
                deviceIds.forEach(id -> {
                    BehaviorReport report = buildPendingReport(id, true);
                    deviceBehaviorDao.saveBehaviorReport(report);
                });
            }
            return sendConfiguration(user, deviceCollection, correlationId);
        } else {
            return createEmptyConnectionResult(deviceCollection, "Porter connection is invalid.");
        }
    }
    
    @Override
    public DataStreamingConfigResult accept(List<Integer> allDeviceIds, LiteYukonUser user)
            throws DataStreamingConfigException {
        
        DeviceCollection deviceCollection = createDeviceCollectionForIds(allDeviceIds);
        if (isValidPorterConnection()) {
            
            // deviceId, config
            Map<Integer, Behavior> deviceIdToBehavior =
                deviceBehaviorDao.getBehaviorsByTypeAndDeviceIds(TYPE, allDeviceIds);

            // deviceId, report
            Map<Integer, BehaviorReport> deviceIdToReport =
                deviceBehaviorDao.getBehaviorReportsByTypeAndDeviceIds(TYPE, allDeviceIds);
            
            Set<Integer> deviceIds = new HashSet<>();
            deviceIds.addAll(deviceIdToBehavior.keySet());
            deviceIds.addAll(deviceIdToReport.keySet());
            
            String correlationId = UUID.randomUUID().toString();

            return createEmptyConnectionResult(deviceCollection, "...");
        } else {
            return createEmptyConnectionResult(deviceCollection, "Porter connection is invalid.");
        }
    }

    @Override
    public void cancel(String resultId, LiteYukonUser user) {
        DataStreamingConfigResult result = resultsCache.getResult(resultId);
        result.getCommandCompletionCallback().cancel();
        porterConn.cancel(result ,user);
    }
    
    private void sendNmConfiguration(Multimap<DataStreamingConfig, Integer> configToDeviceIds, String correlationId) throws DataStreamingConfigException {
        //If only called from the discrepancies page, we can assume that all devices support data streaming. If called from
        //elsewhere, we may need to check.
        
        //Build config message for NM
        DeviceDataStreamingConfigRequest configRequest = dataStreamingCommService.buildConfigRequest(configToDeviceIds, correlationId);
        
        //Send message (Block for response message)
        DeviceDataStreamingConfigResponse response = dataStreamingCommService.sendConfigRequest(configRequest);
        
        processConfigResponse(response);
    }
    
    private void sendNmConfiguration(DataStreamingConfig config, List<Integer> deviceIds, String correlationId) throws DataStreamingConfigException {
        //Remove devices from the list that don't support data streaming
        removeDataStreamingUnsupportedDevices(deviceIds);
        
        //Create multiple configurations for different device types if necessary
        //(Some devices may only support some of the configured attributes)
        Multimap<DataStreamingConfig, Integer> configToDeviceIds = splitConfigForDevices(config, deviceIds, null);
        
        sendNmConfiguration(configToDeviceIds, correlationId);
    }
    
    private void sendNmConfigurationRemove(List<Integer> deviceIds, String correlationId) throws DataStreamingConfigException {
        //Remove devices from the list that don't support data streaming
        removeDataStreamingUnsupportedDevices(deviceIds);
        
        DeviceDataStreamingConfig config = new DeviceDataStreamingConfig();
        config.setDataStreamingOn(false);
        config.setMetrics(new HashMap<>());
        
        List<RfnDevice> rfnDevices = rfnDeviceDao.getDevicesByPaoIds(deviceIds);
        Map<RfnIdentifier, Integer> deviceToConfigId = rfnDevices.stream()
                                                                 .map(device -> device.getRfnIdentifier())
                                                                 .collect(Collectors.toMap(rfnId -> rfnId, 
                                                                                           rfnId -> 0));
        
        DeviceDataStreamingConfigRequest configRequest = new DeviceDataStreamingConfigRequest();
        configRequest.setRequestType(DeviceDataStreamingConfigRequestType.UPDATE);
        configRequest.setRequestExpiration(DateTimeConstants.MINUTES_PER_DAY);
        configRequest.setConfigs(new DeviceDataStreamingConfig[]{config});
        configRequest.setDevices(deviceToConfigId);
        configRequest.setRequestId(correlationId);
        
        //Send verification message (Block for response message)
        DeviceDataStreamingConfigResponse response = dataStreamingCommService.sendConfigRequest(configRequest);
        
        processConfigResponse(response);
    }
    
    private DataStreamingConfigResult sendConfiguration(LiteYukonUser user, DeviceCollection deviceCollection,
            String correlationId) {
        log.info("Attempting to send configuration command to " + deviceCollection.getDeviceCount() + " devices.");
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

        DataStreamingConfigCallback callback = new DataStreamingConfigCallbackImpl(result, successGroup, failedGroup,
            canceledGroup, execution, deviceCollection.getDeviceList(), correlationId);

        result.setConfigCallback(callback);
        List<SimpleDevice> allDevices = deviceCollection.getDeviceList();
        deviceGroupMemberEditorDao.addDevices(allDevicesGroup, allDevices);
        List<SimpleDevice> unsupportedDevices = new ArrayList<>();
        List<SimpleDevice> supportedDevices = getSupportedDevices(deviceCollection);
        unsupportedDevices.addAll(allDevices);
        unsupportedDevices.removeAll(supportedDevices);
        if (!unsupportedDevices.isEmpty()) {
            log.info(unsupportedDevices.size() + " devices are unsupported.");
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

        List<RfnDevice> rfnDevices = rfnDeviceDao.getDevicesByPaoIds(
            deviceCollection.getDeviceList().stream().map(rfnDevice -> rfnDevice.getDeviceId()).collect(
                Collectors.toList()));
        Set<Integer> deviceIds =
            rfnDevices.stream().map(rfnDevice -> rfnDevice.getPaoIdentifier().getPaoId()).collect(Collectors.toSet());

        List<SimpleDevice> supportedDevices =
            deviceCollection.getDeviceList().stream().filter(device -> deviceIds.contains(device.getDeviceId())
                && dataStreamingAttributeHelper.supportsDataStreaming(device.getDeviceType())).collect(
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
    
    private DataStreamingConfigResult createEmptyConnectionResult(DeviceCollection deviceCollection, String reason){
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
        result.setExceptionReason(reason);
        String resultsId = resultsCache.addResult(result);
        result.setResultsId(resultsId);
        result.complete();
        return result;
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
        if (behavior == null) {
            return null;
        }
        DataStreamingConfig config = new DataStreamingConfig();
        config.setId(behavior.getId());
        int i = 0;
        int intervalValue = 0;
        int channels = behavior.getIntValue(CHANNELS_STRING);
        for (i = 0; i < channels; i++) {
            String key = CHANNELS_STRING + "." + i;
            boolean attributeEnabled = true;
            String attributeEnabledValue = behavior.getValue(key + ENABLED_STRING);
            if (!StringUtils.isEmpty(attributeEnabledValue) && Boolean.parseBoolean(attributeEnabledValue) == false) {
                attributeEnabled = false;
            }
            /**
             * This method converts behavior and behavior report to config.
             * Behavior report contains disabled attributes.
             * Behavior contains only enabled attributes.
             * Add only enabled attributed to config
             */
            if (attributeEnabled) {
                BuiltInAttribute attributeValue = behavior.getEnumValue(key + ATTRIBUTE_STRING, BuiltInAttribute.class);
                intervalValue = behavior.getIntValue(key + INTERVAL_STRING);
                DataStreamingAttribute dataStreamingAttribute = new DataStreamingAttribute();
                dataStreamingAttribute.setAttribute(attributeValue);
                dataStreamingAttribute.setInterval(intervalValue);
                dataStreamingAttribute.setAttributeOn(Boolean.TRUE);
                config.addAttribute(dataStreamingAttribute);
            }
        }
        config.setSelectedInterval(intervalValue);
        return config;
    }

    /**
     * Converts config to behavior
     */
    private Behavior convertConfigToBehavior(DataStreamingConfig config) {
        Behavior behavior = new Behavior();
        behavior.setType(TYPE);
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
        report.setType(TYPE);
        report.setDeviceId(deviceId);
        report.setStatus(BehaviorReportStatus.CONFIRMED);
        report.setTimestamp(new Instant());
        List<ReportedDataStreamingAttribute> attributes = config.getConfiguredMetrics();
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

    /**
     * Pending report doesn't have channels leaving as is for now until this is tested with Porter
        private BehaviorReport buildPendingReport(DataStreamingConfig config, int deviceId, boolean enabled) {
        BehaviorReport report = new BehaviorReport();
        report.setType(TYPE);
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
    }*/
    
    /**
     * Builds pending report.
     * 
     * @param deviceId
     * @param false - the user is trying to unassign a device that was not assigned to a config
     */
    private BehaviorReport buildPendingReport(int deviceId, boolean enabled) {
        BehaviorReport report = new BehaviorReport();
        report.setType(TYPE);
        report.setDeviceId(deviceId);
        report.setStatus(BehaviorReportStatus.PENDING);
        report.setTimestamp(new Instant());
        report.addValue(CHANNELS_STRING, 0);
        report.addValue(STREAMING_ENABLED_STRING, enabled);
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
            deviceUnsupported.add(unsupported);
        }
        
        public void processVerificationResponse(DeviceDataStreamingConfigResponse response) {
            switch (response.getResponseType()) {
                case ACCEPTED:
                    setGatewayLoading(response);
                    break;
                case CONFIG_ERROR:
                    handleRejectedResponse(response);
                    break;
                case INVALID_REQUEST_TYPE:
                case INVALID_REQUEST_ID:
                case NO_CONFIGS:
                case NO_DEVICES:
                    handleBadRequestResponse(response);
                    break;
                case NETWORK_MANAGER_FAILURE:
                    // Exception was thrown in NM processing and it couldn't complete the task
                    handleNmErrorResponse(response);
                    break;
                case OTHER_ERROR:
                default:
                    //unknown error
                    handleUnknownErrorResponse(response);
            }
        }
        
        private void handleRejectedResponse(DeviceDataStreamingConfigResponse response) {
            success = false;
            boolean gatewaysOverloaded = setGatewayLoading(response);
            
            if (gatewaysOverloaded) {
                log.debug("Data streaming verification response - gateways oversubscribed.");
                exceptions.add(new DataStreamingConfigException("Gateways oversubscribed", "gatewaysOversubscribed"));
            }
            
            for (Entry<RfnIdentifier, ConfigError> errorDeviceEntry : response.getErrorConfigedDevices().entrySet()) {
                DeviceDataStreamingConfigError errorType = errorDeviceEntry.getValue().getErrorType();
                //Ignore Gateway overloaded errors, those are covered above
                if (errorType != DeviceDataStreamingConfigError.GATEWAY_OVERLOADED) {
                    RfnDevice device = rfnDeviceDao.getDeviceForExactIdentifier(errorDeviceEntry.getKey());
                    log.debug("Data streaming verification response - device error: " + device + ", " + errorType.name());
                    String deviceError = "Device error for " + device + ": " + errorType;
                    String i18nKey = "device." + errorType.name();
                    exceptions.add(new DataStreamingConfigException(deviceError , i18nKey, device.getName()));
                }
            }
        }
        
        private void handleBadRequestResponse(DeviceDataStreamingConfigResponse response) {
            success = false;
            String requestError = "Network Manager rejected the request as malformed: " + response.getResponseType(); 
            log.error(requestError + ". " + response.getResponseMessage());
            exceptions.add(new DataStreamingConfigException(requestError, "badRequest", response.getResponseType()));
        }
        
        private void handleNmErrorResponse(DeviceDataStreamingConfigResponse response) {
            success = false;
            String nmError = "Network Manager encountered an error during data streaming configuration verification. ";
            log.error(nmError + response.getResponseMessage());
            exceptions.add(new DataStreamingConfigException(nmError, "networkManagerFailure"));
        }
        
        private void handleUnknownErrorResponse(DeviceDataStreamingConfigResponse response) {
            success = false;
            String unknownError = "Unknown error during data streaming configuration verification. ";
            log.error(unknownError + response.getResponseMessage()); 
            exceptions.add(new DataStreamingConfigException(unknownError, "unknownError"));
        }
        
        /**
         * Add GatewayLoading objects to this object's list for each affected gateway in the response message.
         * @return True if the response estimates that gateways will be overloaded.
         */
        private boolean setGatewayLoading(DeviceDataStreamingConfigResponse response) {
            boolean gatewaysOversubscribed = false;
            Map<RfnIdentifier, GatewayDataStreamingInfo> affectedGateways = response.getAffectedGateways();
            for (RfnIdentifier rfnId : affectedGateways.keySet()) {
                RfnDevice gateway = rfnDeviceDao.getDeviceForExactIdentifier(rfnId);
                GatewayDataStreamingInfo info = affectedGateways.get(rfnId);
                double currentPercent = (info.getCurrentLoading() / info.getMaxCapacity()) * 100;
                double proposedPercent = (info.getResultLoading() / info.getMaxCapacity()) * 100;
                
                GatewayLoading loading = new GatewayLoading();
                loading.setGatewayName(gateway.getName());
                loading.setCurrentPercent(currentPercent);
                loading.setProposedPercent(proposedPercent);
                gatewayLoading.add(loading);
                
                if (proposedPercent > 100) {
                    gatewaysOversubscribed = true;
                }
            }
            return gatewaysOversubscribed;
        }
    }
    
    /**
     * Creates devices collection
     * @param deviceIds - to add to the collection
     */
    private DeviceCollection createDeviceCollectionForIds(Collection<Integer> deviceIds) {
        Collection<SimpleMeter> meters = deviceIds.stream().map(
            id -> new SimpleMeter(serverDatabaseCache.getAllPaosMap().get(id).getPaoIdentifier(), "")).collect(
                Collectors.toList());
        DeviceCollection deviceCollection =
            deviceGroupCollectionHelper.createDeviceGroupCollection(meters.iterator(), "");
        return deviceCollection;
    }
    
    /**
     * Callback to handle data streaming config responses.
     */
    public class DataStreamingConfigCallbackImpl implements DataStreamingConfigCallback {
        private String correlationId;
        private DataStreamingConfigResult result; 
        private StoredDeviceGroup successGroup;
        private StoredDeviceGroup failedGroup;
        private StoredDeviceGroup cancelGroup;
        private CommandRequestExecution execution;
        private List<SimpleDevice> deviceList;
        
        public DataStreamingConfigCallbackImpl(DataStreamingConfigResult result, StoredDeviceGroup successGroup,
                StoredDeviceGroup failedGroup, StoredDeviceGroup cancelGroup, CommandRequestExecution execution,
                List<SimpleDevice> deviceList, String correlationId) {
            this.result = result;
            this.successGroup = successGroup;
            this.failedGroup = failedGroup;
            this.execution = execution;
            this.deviceList = deviceList;
            this.cancelGroup = cancelGroup;
            this.correlationId = correlationId;
        }
        
        @Override
        public void receivedConfigReport(SimpleDevice device, ReportedDataStreamingConfig config) {
            // Add to result
            deviceGroupMemberEditorDao.addDevices(successGroup, device);
            
            // Update DB
            BehaviorReport report = buildConfirmedReport(config, device.getDeviceId());
            deviceBehaviorDao.saveBehaviorReport(report);
            
            // Send sync to NM
            DeviceDataStreamingConfigRequest request = dataStreamingCommService.buildSyncRequest(config, 
                                                                                                 device.getDeviceId(), 
                                                                                                 correlationId);
            try {
                dataStreamingCommService.sendConfigRequest(request);
            } catch (DataStreamingConfigException e) {
                log.error("Error sending data streaming sync to Network Manager.", e);
            }
        }

        @Override
        public void receivedConfigError(SimpleDevice device, SpecificDeviceErrorDescription error) {
            log.debug("Recieved a config error for device=" + device + " error=" + error.getDescription());
            deviceGroupMemberEditorDao.addDevices(failedGroup, device);
            deviceBehaviorDao.updateBehaviorReportStatus(TYPE, BehaviorReportStatus.FAILED,
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
                deviceBehaviorDao.updateBehaviorReportStatus(TYPE,
                    BehaviorReportStatus.CANCELED,
                    canceledDevices.stream().map(s -> s.getDeviceId()).collect(Collectors.toList()));
                deviceGroupMemberEditorDao.addDevices(cancelGroup, canceledDevices);
                updateRequestCount(execution, result.getFailureCount() + result.getSuccessCount());
            }
        }
    }
}
