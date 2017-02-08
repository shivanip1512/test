package com.cannontech.web.rfn.dataStreaming.service.impl;

import static java.util.stream.Collectors.toList;

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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.callbackResult.DataStreamingConfigCallback;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
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
import com.cannontech.common.device.streaming.dao.DeviceBehaviorDao.DiscrepancyInfo;
import com.cannontech.common.device.streaming.model.Behavior;
import com.cannontech.common.device.streaming.model.BehaviorReport;
import com.cannontech.common.device.streaming.model.BehaviorReportStatus;
import com.cannontech.common.device.streaming.model.BehaviorType;
import com.cannontech.common.events.loggers.DataStreamingEventLogService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.dataStreaming.DataStreamingAttributeHelper;
import com.cannontech.common.rfn.dataStreaming.DataStreamingMetricStatus;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingAttribute;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfig;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigError;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigRequest;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigRequestType;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigResponse;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigResponse.ConfigError;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigResponseType;
import com.cannontech.common.rfn.message.datastreaming.gateway.GatewayDataStreamingInfo;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.web.rfn.dataStreaming.DataStreamingConfigException;
import com.cannontech.web.rfn.dataStreaming.DataStreamingPorterConnection;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingAttribute;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfigResult;
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
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;

public class DataStreamingServiceImpl implements DataStreamingService {

    private static final Logger log = YukonLogManager.getLogger(DataStreamingServiceImpl.class);

    public final static BehaviorType TYPE = BehaviorType.DATA_STREAMING;

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
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private DataStreamingEventLogService logService;
    @Autowired private NextValueHelper nextValueHelper;
    
    boolean ignoreTimeCheck;

    @PostConstruct
    public void init() {
        ignoreTimeCheck =
            configurationSource.getBoolean(MasterConfigBoolean.SIMULATOR_DISCREPANCY_REPORT_IGNORE_TIME_CHECK, false);
    }

    @Override
    public DataStreamingConfigResult findDataStreamingResult(String resultKey) {
        return resultsCache.getResult(resultKey);
    }

    private DataStreamingConfig findDataStreamingConfiguration(int configId) {
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
    private Map<RfnIdentifier, RfnGateway> getGatewaysForLoadingRange(SummarySearchCriteria criteria)
            throws DataStreamingConfigException {
        List<RfnGateway> gateways = new ArrayList<>();

        try {
            if (criteria.isGatewaySelected()) {
                gateways.addAll(rfnGatewayService.getGatewaysByPaoIdsWithData(criteria.getSelectedGatewayIds()));
            } else {
                gateways.addAll(rfnGatewayService.getGatewaysWithData(PaoType.getRfGatewayTypes()));
            }
        } catch (NmCommunicationException e) {
            throw new DataStreamingConfigException("Communication error requesting gateway data from Network Manager.",
                e, "commsError");
        }

        Double min = criteria.getMinLoadPercent() == null ? Double.MIN_VALUE : criteria.getMinLoadPercent();
        Double max = criteria.getMaxLoadPercent() == null ? Double.MAX_VALUE : criteria.getMaxLoadPercent();
        Range<Double> range = Range.closed(min, max);
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
    public List<RfnGateway> getOverloadedGateways() throws DataStreamingConfigException {
        List<RfnGateway> overloadedGateways = new ArrayList<>();

        List<RfnGateway> allGateways = new ArrayList<>();
        try {
            allGateways.addAll(rfnGatewayService.getGatewaysWithData(PaoType.getRfGatewayTypes()));
            allGateways.forEach(gateway -> {
                if (gateway.getData().getDataStreamingLoadingPercent() > 100) {
                    overloadedGateways.add(gateway);
                }
            });
        } catch (NmCommunicationException e) {
            throw new DataStreamingConfigException("Communication error requesting gateway data from Network Manager.",
                e, "commsError");
        }

        return overloadedGateways;
    }

    @Override
    public List<SummarySearchResult> search(SummarySearchCriteria criteria) throws DataStreamingConfigException {
        log.info("Searching by criteria=" + criteria);
        List<SummarySearchResult> results = new ArrayList<>();
        Map<Integer, DeviceInfo> deviceIdToDeviceInfo = new HashMap<>();
        Map<RfnIdentifier, RfnGateway> gatewaysForLoadingRange = getGatewaysForLoadingRange(criteria);

        Collection<GatewayDataStreamingInfo> gatewayInfos =
            dataStreamingCommService.getGatewayInfo(rfnGatewayService.getAllGateways(), false);

        for (GatewayDataStreamingInfo gatewayInfo : gatewayInfos) {
            Map<RfnIdentifier, Double> rfnIdentifiers = gatewayInfo.getDeviceRfnIdentifiers();
            if (rfnIdentifiers != null) {
                for (RfnIdentifier identifier : rfnIdentifiers.keySet()) {
                    DeviceInfo deviceToGateway = new DeviceInfo();
                    // Identifiers are cached
                    try {
                        deviceToGateway.device = rfnDeviceDao.getDeviceForExactIdentifier(identifier);
                        deviceToGateway.gatewayRfnIdentifier = gatewayInfo.getGatewayRfnIdentifier();
                        deviceIdToDeviceInfo.put(deviceToGateway.device.getPaoIdentifier().getPaoId(), deviceToGateway);
                    } 
                    catch (NotFoundException ex) {
                        //  NM included a node in the gateway device list that Yukon doesn't know about, so ignore it
                        log.warn(ex);
                    }
                }
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
        log.debug("Found results=" + results.size());
        return results;
    }

    private static class DeviceInfo {
        RfnIdentifier gatewayRfnIdentifier;
        RfnDevice device;
    }

    @Override
    public List<DiscrepancyResult> findDiscrepancies() {
        return findDiscrepancies(null);
    }

    @Override
    public DiscrepancyResult findDiscrepancy(int deviceId) {
        return Iterables.getOnlyElement(findDiscrepancies(deviceId), null);
    }

    private List<DiscrepancyResult> findDiscrepancies(Integer deviceId) {
        List<DiscrepancyResult> results = new ArrayList<>();
        Iterable<DiscrepancyInfo> discrepancies = deviceBehaviorDao.findDiscrepancies(TYPE, deviceId);
        for (DiscrepancyInfo discrepancy : discrepancies) {
            if (checkPending(discrepancy.getBehaviorReport())) {
                DiscrepancyResult result = new DiscrepancyResult();
                // behavior
                DataStreamingConfig expectedConfig = convertBehaviorToConfig(discrepancy.getBehavior());
                log.debug("expectedConfig=" + expectedConfig);
                // behavior report
                DataStreamingConfig reportedConfig = convertBehaviorToConfig(discrepancy.getBehaviorReport());
                log.debug("reportedConfig=" + reportedConfig);
                result.setActual(reportedConfig);
                result.setExpected(expectedConfig);
                result.setStatus(discrepancy.getBehaviorReport().getStatus());
                result.setLastCommunicated(discrepancy.getLastCommunicated());
                result.setPaoName(serverDatabaseCache.getAllPaosMap().get(discrepancy.getDeviceId()).getPaoName());
                result.setDeviceId(discrepancy.getDeviceId());
                DateTime oneWeekAgo = new DateTime().minusWeeks(1);
                boolean displayRemove = result.getLastCommunicated() == null || ignoreTimeCheck
                    || result.getLastCommunicated().isBefore(oneWeekAgo);
                result.setDisplayRemove(displayRemove);
                results.add(result);
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
        if (!ignoreTimeCheck && report.getStatus() == BehaviorReportStatus.PENDING) {
            DateTime reportedTimePlus24Hours =  new DateTime(report.getTimestamp()).plusHours(24);
            DateTime now = new DateTime();
            if(now.isBefore(reportedTimePlus24Hours)){
                //24 hours didn't pass yet, do not display the report.
                return false;
            }
        }
        return true;
    }

    private int saveConfig(DataStreamingConfig config) {
        DataStreamingConfig savedConfig = findConfig(getAllDataStreamingConfigurations(), config);
        if(savedConfig == null){
            Behavior behavior = convertConfigToBehavior(config);
            return deviceBehaviorDao.saveBehavior(behavior);  
        }
       
        return savedConfig.getId();
    }

    private class DeviceTypeList {
        DeviceTypeList(PaoType type, Collection<Integer> deviceIds) {
            this.type = type;
            this.deviceIds = deviceIds;
        }
        PaoType type;
        Collection<Integer> deviceIds;
    }

    private static Multimap<PaoType, Integer> asTypesToDeviceIds(List<SimpleDevice> devices) {
        return Multimaps.transformValues(
                Multimaps.index(devices, SimpleDevice::getDeviceType),
                SimpleDevice::getDeviceId);        
    }

    private static List<Integer> asDeviceIds(Collection<SimpleDevice> devices) {
        return devices.stream()
                    .map(SimpleDevice::getDeviceId)
                    .collect(toList());
    }

    @Override
    public VerificationInformation verifyConfiguration(DataStreamingConfig config, List<SimpleDevice> devices) {

        DataStreamingConfigInfo configInfo = new DataStreamingConfigInfo();

        if (!config.isNewConfiguration()) {
            config = findDataStreamingConfiguration(config.getSelectedConfiguration());
        }

        configInfo.originalConfig = config;
        
        // Remove devices from the list that don't support data streaming
        List<Integer> unsupportedDevices = removeDataStreamingUnsupportedDevices(devices);
        List<BuiltInAttribute> allConfigAttributes =
            config.getAttributes().stream().map(DataStreamingAttribute::getAttribute).collect(toList());
        configInfo.addDeviceUnsupported(allConfigAttributes, unsupportedDevices, true);
        if (devices.isEmpty()) {
            // None of the selected devices support data streaming
            configInfo.success = false;
            configInfo.exceptions.add(
                new DataStreamingConfigException("No devices support data streaming", "noDevices"));
            VerificationInformation verificationInfo = buildVerificationInformation(configInfo);
            return verificationInfo;
        }

        Multimap<PaoType, Integer> devicesByType = asTypesToDeviceIds(devices); 
        
        Multimap<Set<BuiltInAttribute>, PaoType> unsupportedAttributesPerType = 
                getUnsupportedAttributesToTypes(config, devicesByType.keySet());
        
        long attCount = config.getAttributes().stream().filter(DataStreamingAttribute::getAttributeOn).count();

        unsupportedAttributesPerType.asMap().forEach((unsupportedAttributes, unsupportedTypes) -> {
            configInfo.addDeviceUnsupported(
                Lists.newArrayList(unsupportedAttributes),  //  Ideally, this would be a set.
                unsupportedTypes.stream()
                    .flatMap(type -> devicesByType.get(type).stream())
                    .collect(toList()),
                unsupportedAttributes.size() == attCount);
        });

        Multimap<DataStreamingConfig, DeviceTypeList> configAssignmentsByType = 
                splitConfigForDevices(config, devicesByType, unsupportedAttributesPerType);

        // Create multiple configurations for different device types if necessary
        // (Some devices may only support some of the configured attributes)
        Multimap<DeviceDataStreamingConfig, Integer> configToDeviceIds = createNmConfigs(configAssignmentsByType);

        // Build verification message for NM
        DeviceDataStreamingConfigRequest verificationRequest =
            dataStreamingCommService.buildVerificationRequest(configToDeviceIds);

        // Send verification message (Block for response message)
        try {
            DeviceDataStreamingConfigResponse response =
                dataStreamingCommService.sendConfigRequest(verificationRequest);
            configInfo.processVerificationResponse(response);
        } catch (DataStreamingConfigException e) {
            configInfo.exceptions.add(e);
            configInfo.success = false;
        }

        // Build VerificationInfo from response message
        VerificationInformation verificationInfo = buildVerificationInformation(configInfo);
        return verificationInfo;
    }

    private ConfigResponseResult processConfigResponse(DeviceDataStreamingConfigResponse response) throws DataStreamingConfigException {
        ConfigResponseResult result = new ConfigResponseResult();
        result.type = response.getResponseType();
        log.debug("Data streaming config response-"+response.getResponseType());
        switch (response.getResponseType()) {
        case ACCEPTED:
            // NM accepted the configuration. It can now be sent to the devices.
            break;
        case ACCEPTED_WITH_ERROR:
            checkForDeviceErrors(response, result);
            break;
        case CONFIG_ERROR:
            boolean gatewaysOverloaded = 
                    Optional.ofNullable(response.getAffectedGateways())
                            .map(Map::values)
                            .map(Collection::stream)
                            .map(s -> s.filter(info -> (info.getResultLoading() / info.getMaxCapacity()) > 1))
                            .map(Stream::findAny)
                            .isPresent();

            if (gatewaysOverloaded) {
                log.debug("Data streaming config response - gateways oversubscribed.");
                throw new DataStreamingConfigException("Gateways oversubscribed", "gatewaysOversubscribed");
            }
            checkForDeviceErrors(response, result);
            if(result.hasDeviceErrors){
                String error = result.deviceIds.size()
                    + " devices failed Network Manager verification and will not be configured. Check Yukon logs for details";
                throw new DataStreamingConfigException(error, "failed", result.deviceIds.size());
            }
            break;
        case INVALID_REQUEST_TYPE:
        case INVALID_REQUEST_SEQUENCE_NUMBER:
        case NO_CONFIGS:
        case NO_DEVICES:
            // The request contained unacceptable data
            String requestError = "Network Manager rejected the request as malformed: " + response.getResponseType();
            log.error(requestError + ". " + response.getResponseMessage());
            throw new DataStreamingConfigException(requestError, "badRequest", response.getResponseType());
        case NETWORK_MANAGER_SERVER_FAILURE:
            // Exception was thrown in NM processing and it couldn't complete the task
            String nmError = "Network Manager encountered a processing error during data streaming configuration. ";
            log.error(nmError + response.getResponseMessage());
            throw new DataStreamingConfigException(nmError, "networkManagerFailure");
        case NETWORK_MANAGER_DATABASE_FAILURE:
            // Exception was thrown in NM processing and it couldn't complete the task
            String nmDbError = "Network Manager encountered a databse error during data streaming configuration. ";
            log.error(nmDbError + response.getResponseMessage());
            throw new DataStreamingConfigException(nmDbError, "networkManagerFailureDb");
        case OTHER_ERROR:
        default:
            // unknown error
            String unknownError = "Unknown error during data streaming configuration. ";
            log.error(unknownError + response.getResponseMessage());
            throw new DataStreamingConfigException(unknownError, "unknownError");
        }
        return result;
    }
    
    class ConfigResponseResult{
        DeviceDataStreamingConfigResponseType type;
        List<Integer> deviceIds = new ArrayList<>();
        boolean hasDeviceErrors = false;
    }
    
    private void checkForDeviceErrors(DeviceDataStreamingConfigResponse response, ConfigResponseResult result)
            throws DataStreamingConfigException {
        for (Entry<RfnIdentifier, ConfigError> errorDeviceEntry : response.getErrorConfigedDevices().entrySet()) {
            DeviceDataStreamingConfigError errorType = errorDeviceEntry.getValue().getErrorType();
            // Ignore Gateway overloaded errors, those are covered above
            if (errorType != DeviceDataStreamingConfigError.GATEWAY_OVERLOADED) {
                RfnDevice device = rfnDeviceDao.getDeviceForExactIdentifier(errorDeviceEntry.getKey());
                log.error(" Data streaming verification response - device error: " + device + ", " + errorType.name());
                result.deviceIds.add(device.getPaoIdentifier().getPaoId());
                result.hasDeviceErrors = true;
            }
        }
    }

    /**
     * Remove the devices that do not support data streaming from the list and return them as a separate list.
     */
    private List<Integer> removeDataStreamingUnsupportedDevices(List<SimpleDevice> devices) {

        List<SimpleDevice> unsupportedDevices = devices.stream()
                .filter(d -> ! dataStreamingAttributeHelper.supportsDataStreaming(d.getDeviceType()))
                .collect(toList());

        devices.removeAll(unsupportedDevices);

        return asDeviceIds(unsupportedDevices);
    }

    private Multimap<DataStreamingConfig, DeviceTypeList> splitConfigForDevices(DataStreamingConfig config, 
            Multimap<PaoType, Integer> devicesByType, Multimap<Set<BuiltInAttribute>, PaoType> unsupportedAttributesToTypes) {
        // build a map of paoTypes and the config attributes they don't support
        Multimap<DataStreamingConfig, DeviceTypeList> configToTypes = ArrayListMultimap.create();

        // create a new config for each paoType that has unsupported attributes
        unsupportedAttributesToTypes.asMap().forEach((unsupportedAttributes, paoTypes) -> {
            DataStreamingConfig newConfig = config.clone();

            removeUnsupportedAttributes(newConfig, unsupportedAttributes);

            configToTypes.putAll(
                newConfig,             
                paoTypes.stream()
                    .map(type -> new DeviceTypeList(
                        type, 
                        devicesByType.get(type)))
                    .collect(toList()));
        });
        
        Set<PaoType> partialTypes = unsupportedAttributesToTypes.values().stream().collect(Collectors.toSet());
        
        Multimap<PaoType, Integer> fullySupported = 
                Multimaps.filterKeys(devicesByType, type -> !partialTypes.contains(type));

        configToTypes.putAll(
            config,
            fullySupported.asMap().entrySet().stream()
                .map(e -> new DeviceTypeList(e.getKey(), e.getValue()))
                .collect(toList()));
        
        return configToTypes;
    }
    
    private Multimap<DeviceDataStreamingConfig, Integer> createNmConfigs(Multimap<DataStreamingConfig, DeviceTypeList> configsPerType)
    {
        Multimap<DeviceDataStreamingConfig, Integer> configsPerDevice = ArrayListMultimap.create();
        
        configsPerType.entries().forEach(entry -> {
            configsPerDevice.putAll(
                dataStreamingCommService.buildDeviceDataStreamingConfig(entry.getKey(), entry.getValue().type),
                entry.getValue().deviceIds);
        });

        return configsPerDevice;
    }

    private Multimap<Set<BuiltInAttribute>, PaoType> getUnsupportedAttributesToTypes(DataStreamingConfig config,
            Collection<PaoType> paoTypes) {
        Multimap<Set<BuiltInAttribute>, PaoType> unsupportedAttributesToTypes = ArrayListMultimap.create();
        for (PaoType paoType : paoTypes) {
            // get subset of supported config attributes
            Collection<BuiltInAttribute> supportedAttributes =
                dataStreamingAttributeHelper.getSupportedAttributes(paoType);
            // get any attributes in the config that are unsupported by the type
            Set<BuiltInAttribute> unsupportedAttributes = getUnsupportedAttributes(config, supportedAttributes);
            // check the map to see if we already have this exact list of unsupported attributes
            if (unsupportedAttributes.size() > 0) {
                unsupportedAttributesToTypes.put(unsupportedAttributes, paoType);
            }
        }
        return unsupportedAttributesToTypes;
    }

    private Set<BuiltInAttribute> getUnsupportedAttributes(DataStreamingConfig config,
            Collection<BuiltInAttribute> attributes) {
        Set<BuiltInAttribute> unsupportedAttributes =
            config.getAttributes().stream().map(dsAttribute -> dsAttribute.getAttribute()).filter(
                attribute -> !attributes.contains(attribute)).collect(Collectors.toSet());

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
    public DataStreamingConfigResult resend(List<Integer> deviceIds, LiteYukonUser user) throws DataStreamingConfigException {
        DataStreamingConfigResult result = createUncompletedResult();
        return resend(deviceIds, result, user);
    }
    
    public DataStreamingConfigResult resend(List<Integer> deviceIds, DataStreamingConfigResult result,
            LiteYukonUser user) throws DataStreamingConfigException {

        log.info("Re-sending configuration for devices=" + deviceIds);

        logService.resendAttempted(user, result.getResultsId(), deviceIds.size());

        if (isValidPorterConnection()) {
            Map<Integer, BehaviorReport> deviceIdToBehaviorReport = initPendingReports(deviceIds);
            Multimap<DeviceDataStreamingConfig, Integer> configToDeviceIds = HashMultimap.create();
            for (Entry<Integer, BehaviorReport> deviceReport : deviceIdToBehaviorReport.entrySet()) {
                Integer deviceId = deviceReport.getKey();
                BehaviorReport report = deviceReport.getValue();
                PaoType paoType = serverDatabaseCache.getAllPaosMap().get(deviceId).getPaoType();
                DataStreamingConfig config = convertBehaviorToConfig(report);
                DeviceDataStreamingConfig ddsConfig = dataStreamingCommService.buildDeviceDataStreamingConfig(config, paoType);
                configToDeviceIds.put(ddsConfig, deviceId);
            }
            int requestSeqNumber = nextValueHelper.getNextValue("DataStreaming");
            ConfigResponseResult configResponseResult = sendNmConfiguration(configToDeviceIds, requestSeqNumber,
                DeviceDataStreamingConfigRequestType.UPDATE_WITH_FORCE);

            if (configResponseResult.hasDeviceErrors) {
                List<Integer> devicesToResend = new ArrayList<>();
                devicesToResend.addAll(deviceIds);
                devicesToResend.removeAll(configResponseResult.deviceIds);

                if (devicesToResend.isEmpty()) {
                    String error =
                        deviceIds.size() + " devices failed Network Manager verification and will not be configured.";
                    log.error(error);
                    throw new DataStreamingConfigException(error, "failed", deviceIds.size() + "");
                } else {
                    log.info(configResponseResult.deviceIds.size()
                        + " devices failed Network Manager verification and will not be configured. Attempting to resend configuration to "
                        + devicesToResend.size() + " devices.");
                    result.complete();
                    result = createUncompletedResult();
                    result.setAcceptedWithErrorFailedDeviceCount(configResponseResult.deviceIds.size());
                    return resend(devicesToResend, result, user);
                }
            }
            if (configResponseResult.type == DeviceDataStreamingConfigResponseType.ACCEPTED_WITH_ERROR) {
                result.setAcceptedWithError(true);
            }
            saveBehaviorReports(deviceIdToBehaviorReport);
            DeviceCollection deviceCollection = createDeviceCollectionForIds(deviceIds);
            sendConfiguration(user, deviceCollection, requestSeqNumber, result);
            return result;
        } else {
            return createEmptyResult(createDeviceCollectionForIds(deviceIds), "Porter connection is invalid.");
        }
    }

    private Map<Integer, BehaviorReport> initPendingReports(List<Integer> deviceIds) {
        Map<Integer, BehaviorReport> deviceIdToBehaviorReport =
            deviceBehaviorDao.getBehaviorReportsByTypeAndDeviceIds(TYPE, deviceIds);

        deviceIds.forEach(id -> {
            BehaviorReport report = deviceIdToBehaviorReport.get(id);
            if (report == null) {
                report = buildPendingReport(id);
            } else {
                report.setStatus(BehaviorReportStatus.PENDING);
                report.setTimestamp(new Instant());
            }
        });
        return deviceIdToBehaviorReport;
    }

    private void saveBehaviorReports(Map<Integer, BehaviorReport> deviceIdToBehaviorReport) {
        deviceIdToBehaviorReport.keySet().forEach(id -> {
            BehaviorReport report = deviceIdToBehaviorReport.get(id);
            deviceBehaviorDao.saveBehaviorReport(report);
        });
    }

    @Override
    public DataStreamingConfigResult unassignDataStreamingConfig(DeviceCollection deviceCollection, LiteYukonUser user)
            throws DataStreamingConfigException {

        if (isValidPorterConnection()) {

            DataStreamingConfigResult result= createUncompletedResult();
            logService.unassignAttempted(user, result.getResultsId(), deviceCollection.getDeviceCount());
            
            List<Integer> deviceIds = asDeviceIds(getSupportedDevices(deviceCollection));

            log.info("Unassign data streaming config from devices=" + deviceIds);

            Map<Integer, Behavior> deviceIdToBehavior =
                deviceBehaviorDao.getBehaviorsByTypeAndDeviceIds(TYPE, deviceIds);

            // filter out devices that do not have behaviors
            List<Integer> devicesIdsWithoutBehaviors = new ArrayList<>();
            devicesIdsWithoutBehaviors.addAll(deviceIds);
            devicesIdsWithoutBehaviors.removeAll(deviceIdToBehavior.keySet());

            if (deviceIds.size() == devicesIdsWithoutBehaviors.size()) {
                // none of the devices have behavior assigned
                List<SimpleDevice> allDevices = deviceCollection.getDeviceList();
                deviceGroupMemberEditorDao.addDevices(result.getAllDevicesGroup(), allDevices);
                // mark devices as "success"
                addDevicesToGroup(devicesIdsWithoutBehaviors, result.getSuccessGroup());
                List<Integer> unsupportedDeviceIds = removeDataStreamingUnsupportedDevices(
                    deviceCollection.getDeviceList());
               // mark devices as "unsupported"
                addDevicesToGroup(unsupportedDeviceIds, result.getUnsupportedGroup());
                result.complete();
                
                logService.completedResults(result.getResultsId(), deviceCollection.getDeviceCount(),
                    result.getSuccessCount(), 0, result.getUnsupportedCount());
                
                return result;
            }
            
            int requestSeqNumber = nextValueHelper.getNextValue("DataStreaming");

            // only unassign devices that have behavior assigned
            deviceIds.remove(devicesIdsWithoutBehaviors);

            sendNmConfigurationRemove(deviceIds, requestSeqNumber);
            Map<Integer, BehaviorReport> reports =
                deviceBehaviorDao.getBehaviorReportsByTypeAndDeviceIds(TYPE, deviceIds);
            deviceIds.forEach(id -> {
                BehaviorReport report = reports.get(id);
                if (report == null) {
                    report = buildPendingReport(id);
                } else {
                    report.setStatus(BehaviorReportStatus.PENDING);
                    report.setTimestamp(new Instant());
                }
                deviceBehaviorDao.saveBehaviorReport(report);
            });
            deviceBehaviorDao.unassignBehavior(TYPE, deviceIds);
            sendConfiguration(user, deviceCollection, requestSeqNumber, result);
            // mark devices as "success"
            addDevicesToGroup(devicesIdsWithoutBehaviors, result.getSuccessGroup());
            return result;
        } else {
            return createEmptyResult(deviceCollection, "Porter connection is invalid.");
        }
    }

    @Override
    public DataStreamingConfigResult deleteDataStreamingReportAndUnassignConfig(int deviceId, LiteYukonUser user) {
        
        log.info("Deleting report and unassigning config for "+deviceId);
        
        List<Integer> devicesIds = Arrays.asList(deviceId);
        DeviceCollection deviceCollection = createDeviceCollectionForIds(devicesIds);
        // creating a result
        DataStreamingConfigResult result = createEmptyResult(deviceCollection, null);
        
        logService.removeAttempted(user, result.getResultsId(), deviceCollection.getDeviceCount());
        
        // mark devices as "success"
        addDevicesToGroup(devicesIds, result.getSuccessGroup());

        deviceBehaviorDao.unassignBehavior(TYPE, devicesIds);
        Map<Integer, BehaviorReport> reports = deviceBehaviorDao.getBehaviorReportsByTypeAndDeviceIds(TYPE, devicesIds);
        deviceBehaviorDao.deleteBehaviorReport(reports.get(deviceId).getId());
        
        logService.removeCompleted(result.getResultsId(), deviceCollection.getDeviceCount());

        return result;
    }

    private void addDevicesToGroup(List<Integer> deviceIds, StoredDeviceGroup group) {
        if (!deviceIds.isEmpty()) {
            Collection<LiteYukonPAObject> paos =
                deviceIds.stream().map(id -> serverDatabaseCache.getAllPaosMap().get(id)).collect(toList());
            deviceGroupMemberEditorDao.addDevices(group, paos);
        }
    }

    @Override
    public DataStreamingConfigResult assignDataStreamingConfig(DataStreamingConfig config,
            DeviceCollection deviceCollection, LiteYukonUser user) throws DataStreamingConfigException {

        if (isValidPorterConnection()) {

            if (config.getId() != 0) {
                config = findDataStreamingConfiguration(config.getId());
            }
            
            DataStreamingConfigResult result = createUncompletedResult();
                
            logService.assignAttempted(user, result.getResultsId(), config.getName(),
                deviceCollection.getDeviceCount());

            List<SimpleDevice> devices = getSupportedDevices(deviceCollection);
            Multimap<PaoType, Integer> devicesByType = asTypesToDeviceIds(devices);
            int requestSeqNumber = nextValueHelper.getNextValue("DataStreaming");

            log.info("Assigning data streaming config " + config + " devices=" + devicesByType.values());
            
            Multimap<Set<BuiltInAttribute>, PaoType> unsupportedAttributesPerType = 
                    getUnsupportedAttributesToTypes(config, devicesByType.keySet());
            
            Multimap<DataStreamingConfig, DeviceTypeList> configAssignmentsByType = 
                    splitConfigForDevices(config, devicesByType, unsupportedAttributesPerType);

            // Create multiple configurations for different device types if necessary
            // (Some devices may only support some of the configured attributes)
            Multimap<DeviceDataStreamingConfig, Integer> configToDeviceIds = createNmConfigs(configAssignmentsByType);
            
            sendNmConfiguration(configToDeviceIds, requestSeqNumber, DeviceDataStreamingConfigRequestType.UPDATE);

            //  Save the config per type
            configAssignmentsByType.asMap().forEach((typeConfig, typeLists) -> {
                List<Integer> configDeviceIds = typeLists.stream().flatMap(tl -> tl.deviceIds.stream()).collect(toList());
                int behaviorId = saveConfig(typeConfig);
                deviceBehaviorDao.assignBehavior(behaviorId, TYPE, configDeviceIds, true);
                configDeviceIds.stream()
                    .map(this::buildPendingReport)
                    .forEach(deviceBehaviorDao::saveBehaviorReport);
            });
            
            result.setConfig(config);

            sendConfiguration(user, deviceCollection, requestSeqNumber, result);
            return result;
        } else {
            return createEmptyResult(deviceCollection, "Porter connection is invalid.");
        }
    }

    @Override
    public DataStreamingConfigResult accept(List<Integer> allDeviceIds, LiteYukonUser user){

        DeviceCollection deviceCollection = createDeviceCollectionForIds(allDeviceIds);
        if (isValidPorterConnection()) {
            
            log.info("Attempting to accept reported config for devices=" + allDeviceIds);

            DataStreamingConfigResult result = createEmptyResult(deviceCollection, null);            
            logService.acceptAttempted(user, result.getResultsId(), allDeviceIds.size());
            
            // deviceId, report
            Map<Integer, BehaviorReport> deviceIdToReport =
                deviceBehaviorDao.getBehaviorReportsByTypeAndDeviceIds(TYPE, allDeviceIds);

            //mark devices as success
            addDevicesToGroup(new ArrayList<>(deviceIdToReport.keySet()), result.getSuccessGroup());

            List<Integer> devicesIdsToUnassign = new ArrayList<>();
            List<DataStreamingConfig> allConfigs = getAllDataStreamingConfigurations();
            Multimap<Integer, Integer> configIdsToDeviceIds = ArrayListMultimap.create();

            for (int deviceId : allDeviceIds) {

                BehaviorReport report = deviceIdToReport.get(deviceId);
                DataStreamingConfig reportedConfig = convertBehaviorToConfig(report);

                if (report == null || !reportedConfig.isEnabled() || reportedConfig.getAttributes().isEmpty()) {
                    devicesIdsToUnassign.add(deviceId);
                    continue;
                }
   
                // Look for an existing config matching the behavior report
                DataStreamingConfig config = findConfig(allConfigs, reportedConfig);

                if (config == null) {
                    // If no existing config, create a new one from the behavior report
                    Behavior behavior = convertConfigToBehavior(reportedConfig);
                    // create behavior
                    int behaviorId = deviceBehaviorDao.saveBehavior(behavior);
                    configIdsToDeviceIds.put(behaviorId, deviceId);
                    log.debug("Match not found, created new behavior with id=" + behaviorId);
                } else {
                    log.debug("Match found, behavior id=" + config.getId());
                    configIdsToDeviceIds.put(config.getId(), deviceId);
                }
            }
            
            log.debug("Accepting devices=" + allDeviceIds);
            log.debug("Assigning devices=" + configIdsToDeviceIds.values());
            log.debug("Unassigning devices=" + devicesIdsToUnassign);
            // assign behaviors
            for (int configId : configIdsToDeviceIds.keys()) {
                deviceBehaviorDao.assignBehavior(configId, TYPE,
                    new ArrayList<>(configIdsToDeviceIds.get(configId)), false);
            }
            deviceBehaviorDao.deleteUnusedBehaviors();
            
            if(!devicesIdsToUnassign.isEmpty()){
                deviceBehaviorDao.unassignBehavior(TYPE, devicesIdsToUnassign);
            }
            
            logService.acceptCompleted(result.getResultsId(), allDeviceIds.size(), configIdsToDeviceIds.size(),
                devicesIdsToUnassign.size());
            
            return result;
        } else {
            return createEmptyResult(deviceCollection, "Porter connection is invalid.");
        }
    }
    
    /**
     * Searches allConfigs list for behavior with the same attributes and interval as reportedConfig.
     * 
     * @return null if the config is not found
     */
    private DataStreamingConfig findConfig(List<DataStreamingConfig> allConfigs, DataStreamingConfig reportedConfig) {
        // search for behavior with the same attributes and interval
        for (DataStreamingConfig savedConfig : allConfigs) {
            if (compareByAttributesAndInterval(savedConfig, reportedConfig)) {
                return savedConfig;
            }
        }
        
        return null;
    }

    /**
     * Compares 2 configs by attributes and interval
     * @return true if configs are equal
     */
    private boolean compareByAttributesAndInterval(DataStreamingConfig expectedConfig, DataStreamingConfig actualConfig){
        Set<BuiltInAttribute> expected =
            expectedConfig.getOnAttributes().stream().map(x -> x.getAttribute()).collect(Collectors.toSet());
        Set<BuiltInAttribute> actual =
                actualConfig.getOnAttributes().stream().map(x -> x.getAttribute()).collect(Collectors.toSet());
        //The returned set contains all elements that are contained in either set1 or set2 but not in both
        Set<BuiltInAttribute> differences = Sets.symmetricDifference(expected,actual);
        boolean isEqualInterval = expectedConfig.getSelectedInterval() == actualConfig.getSelectedInterval();
        return differences.isEmpty() && isEqualInterval;
    }

    @Override
    public void cancel(String resultId, LiteYukonUser user) {
        DataStreamingConfigResult result = resultsCache.getResult(resultId);
        
        logService.cancelAttempted(user, result.getResultsId());
        
        result.getConfigCallback().cancel(user);
        porterConn.cancel(result, user);
    }

    private ConfigResponseResult sendNmConfiguration(Multimap<DeviceDataStreamingConfig, Integer> configToDeviceIds, int requestSeqNumber,
            DeviceDataStreamingConfigRequestType type) throws DataStreamingConfigException {
        // If only called on "re-send", we can assume that all devices support data streaming. If called
        // from
        // elsewhere, we may need to check.

        // Build config message for NM
        DeviceDataStreamingConfigRequest configRequest =
            dataStreamingCommService.buildConfigRequest(configToDeviceIds, type, requestSeqNumber);

        // Send message (Block for response message)
        DeviceDataStreamingConfigResponse response = dataStreamingCommService.sendConfigRequest(configRequest);

        return processConfigResponse(response);
    }

    private void sendNmConfigurationRemove(List<Integer> deviceIds, int requestSeqNumber)
            throws DataStreamingConfigException {
        // Unsupported devices were already removed by the getSupportedDevices() call in unassignDataStreamingConfig()
        //removeDataStreamingUnsupportedDevices(deviceIds);

        DeviceDataStreamingConfig config = new DeviceDataStreamingConfig();
        config.setDataStreamingOn(false);
        config.setMetrics(new HashMap<>());

        List<RfnDevice> rfnDevices = rfnDeviceDao.getDevicesByPaoIds(deviceIds);
        Map<RfnIdentifier, Integer> deviceToConfigId =
            rfnDevices.stream().map(device -> device.getRfnIdentifier()).collect(
                Collectors.toMap(rfnId -> rfnId, rfnId -> 0));

        DeviceDataStreamingConfigRequest configRequest = new DeviceDataStreamingConfigRequest();
        configRequest.setRequestType(DeviceDataStreamingConfigRequestType.UPDATE_WITH_FORCE);
        configRequest.setRequestExpiration(DateTimeConstants.MINUTES_PER_DAY);
        configRequest.setConfigs(new DeviceDataStreamingConfig[] { config });
        configRequest.setDevices(deviceToConfigId);
        configRequest.setRequestSeqNumber(requestSeqNumber);

        // Send verification message (Block for response message)
        DeviceDataStreamingConfigResponse response = dataStreamingCommService.sendConfigRequest(configRequest);

        processConfigResponse(response);
    }

    private void sendConfiguration(LiteYukonUser user, DeviceCollection deviceCollection,
            int requestSeqNumber, DataStreamingConfigResult result) {
        log.info("Attempting to send configuration command to " + deviceCollection.getDeviceCount() + " devices.");

        CommandRequestExecution execution = commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE,
            DeviceRequestType.DATA_STREAMING_CONFIG, 0, user);
        result.setExecution(execution);

        List<SimpleDevice> allDevices = deviceCollection.getDeviceList();
        deviceGroupMemberEditorDao.addDevices(result.getAllDevicesGroup(), allDevices);
        DataStreamingConfigCallback callback =
            new DataStreamingConfigCallbackImpl(result, execution, deviceCollection.getDeviceList(), requestSeqNumber);

        result.setConfigCallback(callback);
        List<SimpleDevice> unsupportedDevices = new ArrayList<>();
        List<SimpleDevice> supportedDevices = getSupportedDevices(deviceCollection);
        unsupportedDevices.addAll(allDevices);
        unsupportedDevices.removeAll(supportedDevices);
        if (!unsupportedDevices.isEmpty()) {
            log.info(unsupportedDevices.size() + " devices are unsupported.");
            deviceGroupMemberEditorDao.addDevices(result.getUnsupportedGroup(), unsupportedDevices);
            commandRequestExecutionResultDao.saveUnsupported(new HashSet<>(unsupportedDevices), execution.getId(),
                CommandRequestUnsupportedType.UNSUPPORTED);
        }
        if (supportedDevices.isEmpty()) {
            callback.complete();
        } else {
            List<CommandRequestDevice> commands = porterConn.buildConfigurationCommandRequests(supportedDevices);
            porterConn.sendConfiguration(commands, result, user);
        }
        updateRequestCount(execution, supportedDevices.size());
    }
    
    /**
     * Creates uncompleted result and adds it to cache
     */
    private DataStreamingConfigResult createUncompletedResult() {
        DataStreamingConfigResult result =
            new DataStreamingConfigResult(tempDeviceGroupService, deviceGroupCollectionHelper);
        String resultsId = resultsCache.addResult(result);
        result.setResultsId(resultsId);
        return result;
    }

    private List<SimpleDevice> getSupportedDevices(DeviceCollection deviceCollection) {

        List<RfnDevice> rfnDevices = rfnDeviceDao.getDevicesByPaoIds(
            asDeviceIds(deviceCollection.getDeviceList()));
        Set<Integer> deviceIds =
            rfnDevices.stream().map(rfnDevice -> rfnDevice.getPaoIdentifier().getPaoId()).collect(Collectors.toSet());

        List<SimpleDevice> supportedDevices =
            deviceCollection.getDeviceList().stream().filter(device -> deviceIds.contains(device.getDeviceId())
                && dataStreamingAttributeHelper.supportsDataStreaming(device.getDeviceType())).collect(toList());

        return supportedDevices;
    }

    private void updateRequestCount(CommandRequestExecution execution, int count) {
        execution.setRequestCount(count);
        if (log.isDebugEnabled()) {
            log.debug("Updating Command Request Execution request count to " + count + ".");
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

    private DataStreamingConfigResult createEmptyResult(DeviceCollection deviceCollection, String reason) {
        DataStreamingConfigResult result =
            new DataStreamingConfigResult(tempDeviceGroupService, deviceGroupCollectionHelper);
        List<SimpleDevice> allDevices = deviceCollection.getDeviceList();
        deviceGroupMemberEditorDao.addDevices(result.getAllDevicesGroup(), allDevices);
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
        // there is no channels if porter return a failure
        if (!behavior.getValuesMap().isEmpty()) {
            int channels = behavior.getIntValue(CHANNELS_STRING);
            String enabled = behavior.getValue(STREAMING_ENABLED_STRING);
            if (!StringUtils.isEmpty(enabled)) {
                // behavior report can be enabled or disabled
                config.setEnabled(Boolean.parseBoolean(enabled));
            }

            if (config.isEnabled()) {
                for (i = 0; i < channels; i++) {
                    String key = CHANNELS_STRING + "." + i;
                    boolean attributeEnabled = true;
                    String attributeEnabledValue = behavior.getValue(key + ENABLED_STRING);
                    if (!StringUtils.isEmpty(attributeEnabledValue)
                        && Boolean.parseBoolean(attributeEnabledValue) == false) {
                        attributeEnabled = false;
                    }
                    /**
                     * This method converts behavior and behavior report to config.
                     * Behavior report contains disabled attributes.
                     * Behavior contains only enabled attributes.
                     * Add only enabled attributed to config
                     */
                    if (attributeEnabled) {
                        BuiltInAttribute attributeValue =
                            behavior.getEnumValue(key + ATTRIBUTE_STRING, BuiltInAttribute.class);
                        intervalValue = behavior.getIntValue(key + INTERVAL_STRING);
                        String status = behavior.getValue(key + STATUS_STRING);
                        DataStreamingAttribute dataStreamingAttribute = new DataStreamingAttribute();
                        dataStreamingAttribute.setAttribute(attributeValue);
                        dataStreamingAttribute.setInterval(intervalValue);
                        dataStreamingAttribute.setAttributeOn(Boolean.TRUE);
                        if (!StringUtils.isEmpty(status)) {
                            dataStreamingAttribute.setStatus(DataStreamingMetricStatus.valueOf(status));
                        }
                        config.addAttribute(dataStreamingAttribute);
                    }
                }
            }
            config.setSelectedInterval(intervalValue);
        }
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
            config.getAttributes().stream().filter(DataStreamingAttribute::getAttributeOn).collect(toList());
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

    private BehaviorReport buildBehaviorReport(ReportedDataStreamingConfig config, int deviceId, BehaviorReportStatus status) {
        BehaviorReport report = new BehaviorReport();
        report.setType(TYPE);
        report.setDeviceId(deviceId);
        report.setStatus(status);
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
            report.addValue(key + STATUS_STRING, attributes.get(i).getStatus());
        }
        return report;
    }

    /**
     * Builds pending report.
     * 
     * @param deviceId
     * @param false - the user is trying to unassign a device that was not assigned to a config
     */
    private BehaviorReport buildPendingReport(int deviceId) {
        BehaviorReport report = new BehaviorReport();
        report.setType(TYPE);
        report.setDeviceId(deviceId);
        report.setStatus(BehaviorReportStatus.PENDING);
        report.setTimestamp(new Instant());
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

        public void addDeviceUnsupported(List<BuiltInAttribute> attributes, List<Integer> deviceIds, boolean allAttributes) {
            DeviceUnsupported unsupported = new DeviceUnsupported();
            unsupported.setAttributes(attributes);
            unsupported.setDeviceIds(deviceIds);
            unsupported.setAllAttributes(allAttributes);
            deviceUnsupported.add(unsupported);
        }

        public void processVerificationResponse(DeviceDataStreamingConfigResponse response) {
            switch (response.getResponseType()) {
            case ACCEPTED:
            case ACCEPTED_WITH_ERROR:
                setGatewayLoading(response);
                break;
            case CONFIG_ERROR:
                handleRejectedResponse(response);
                break;
            case INVALID_REQUEST_TYPE:
            case INVALID_REQUEST_SEQUENCE_NUMBER:
            case NO_CONFIGS:
            case NO_DEVICES:
                handleBadRequestResponse(response);
                break;
            case NETWORK_MANAGER_SERVER_FAILURE:
            case NETWORK_MANAGER_DATABASE_FAILURE:
                // Exception was thrown in NM processing and it couldn't complete the task
                handleNmErrorResponse(response);
                break;
            case OTHER_ERROR:
            default:
                // unknown error
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
                // Ignore Gateway overloaded errors, those are covered above
                if (errorType != DeviceDataStreamingConfigError.GATEWAY_OVERLOADED) {
                    RfnDevice device = rfnDeviceDao.getDeviceForExactIdentifier(errorDeviceEntry.getKey());
                    log.debug(
                        "Data streaming verification response - device error: " + device + ", " + errorType.name());
                    String deviceError = "Device error for " + device + ": " + errorType;
                    String i18nKey = "device." + errorType.name();
                    exceptions.add(new DataStreamingConfigException(deviceError, i18nKey, device.getName()));
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

            if (response.getResponseType() == DeviceDataStreamingConfigResponseType.NETWORK_MANAGER_DATABASE_FAILURE) {
                String nmError =
                    "Network Manager encountered a database error during data streaming configuration verification. ";
                log.error(nmError + response.getResponseMessage());
                exceptions.add(new DataStreamingConfigException(nmError, "networkManagerFailureDb"));
            } else {
                String nmError =
                    "Network Manager encountered a processing error during data streaming configuration verification. ";
                log.error(nmError + response.getResponseMessage());
                exceptions.add(new DataStreamingConfigException(nmError, "networkManagerFailure"));
            }
        }

        private void handleUnknownErrorResponse(DeviceDataStreamingConfigResponse response) {
            success = false;
            String unknownError = "Unknown error during data streaming configuration verification. ";
            log.error(unknownError + response.getResponseMessage());
            exceptions.add(new DataStreamingConfigException(unknownError, "unknownError"));
        }

        /**
         * Add GatewayLoading objects to this object's list for each affected gateway in the response message.
         * 
         * @return True if the response estimates that gateways will be overloaded.
         */
        private boolean setGatewayLoading(DeviceDataStreamingConfigResponse response) {
            boolean gatewaysOversubscribed = false;
            Map<RfnIdentifier, GatewayDataStreamingInfo> affectedGateways = response.getAffectedGateways();
            if (affectedGateways != null) {
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
            }
            return gatewaysOversubscribed;
        }
    }

    /**
     * Creates devices collection
     * 
     * @param deviceIds - to add to the collection
     */
    private DeviceCollection createDeviceCollectionForIds(Collection<Integer> deviceIds) {
        Collection<SimpleMeter> meters = deviceIds.stream().map(
            id -> new SimpleMeter(serverDatabaseCache.getAllPaosMap().get(id).getPaoIdentifier(), "")).collect(toList());
        DeviceCollection deviceCollection =
            deviceGroupCollectionHelper.createDeviceGroupCollection(meters.iterator(), "");
        return deviceCollection;
    }

    /**
     * Callback to handle data streaming config responses.
     */
    public class DataStreamingConfigCallbackImpl implements DataStreamingConfigCallback {
        private int requestSeqNumber;
        private DataStreamingConfigResult result;
        private StoredDeviceGroup successGroup;
        private StoredDeviceGroup failedGroup;
        private StoredDeviceGroup cancelGroup;
        private CommandRequestExecution execution;
        private List<SimpleDevice> deviceList;
        Map<Integer, Behavior> deviceIdToBehavior; 

        public DataStreamingConfigCallbackImpl(DataStreamingConfigResult result, CommandRequestExecution execution,
                List<SimpleDevice> deviceList, int requestSeqNumber) {
            this.result = result;
            successGroup = result.getSuccessGroup();
            failedGroup = result.getFailedGroup();
            this.execution = execution;
            this.deviceList = deviceList;
            cancelGroup = result.getCanceledGroup();
            this.requestSeqNumber = requestSeqNumber;
            deviceIdToBehavior = deviceBehaviorDao.getBehaviorsByTypeAndDeviceIds(TYPE, asDeviceIds(deviceList));
        }

        @Override
        public void receivedConfigReport(SimpleDevice device, ReportedDataStreamingConfig config) {
            
            BehaviorReport reportedBehavior = buildBehaviorReport(config, device.getDeviceId(), BehaviorReportStatus.CONFIRMED);
                                
            if (!isComplete()) {
                // Add to result
                // mark device as success
                deviceGroupMemberEditorDao.addDevices(successGroup, device);
            }
            
            handleReportedDataStreamingConfig(device, config, reportedBehavior);
        }

        private void handleReportedDataStreamingConfig(SimpleDevice device, ReportedDataStreamingConfig config, BehaviorReport reportedBehavior) {
            // Update DB
            deviceBehaviorDao.saveBehaviorReport(reportedBehavior);

            // Send sync to NM
            DeviceDataStreamingConfigRequest request =
                dataStreamingCommService.buildSyncRequest(config, device, requestSeqNumber);
            try {
                dataStreamingCommService.sendConfigRequest(request);
            } catch (DataStreamingConfigException e) {
                log.error("Error sending data streaming sync to Network Manager.", e);
            }
        }

        @Override
        public void receivedConfigError(SimpleDevice device, SpecificDeviceErrorDescription error, ReportedDataStreamingConfig config) {
            log.debug("Recieved a config error for device=" + device + " error=" + error.getDescription() + " config=" + config);
            
            if (!isComplete()) {
                deviceGroupMemberEditorDao.addDevices(failedGroup, device);
            }

            if (config != null) {
                BehaviorReport report = buildBehaviorReport(config, device.getDeviceId(), BehaviorReportStatus.FAILED);
                
                DataStreamingConfig reportedConfig = convertBehaviorToConfig(report);
                for (DataStreamingAttribute attribute : reportedConfig.getAttributes()) {
                    if (attribute.getStatus() != DataStreamingMetricStatus.OK) {
                        report.setStatus(BehaviorReportStatus.CONFIGURATION_ERROR);
                        break;
                    }
                }
                handleReportedDataStreamingConfig(device, config, report);
            } else {
                deviceBehaviorDao.updateBehaviorReportStatus(TYPE, BehaviorReportStatus.FAILED,
                    Arrays.asList(device.getDeviceId()));
            }
        }

        @Override
        public void complete() {
            if (!result.isComplete()) {
                result.complete();
                log.info("Command Complete");
                completeCommandRequestExecutionRecord(execution, CommandRequestExecutionStatus.COMPLETE);
                
                logService.completedResults(result.getResultsId(), result.getTotalItems(), result.getSuccessCount(),
                    result.getFailureCount(), result.getUnsupportedCount());
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
        public void cancel(LiteYukonUser user) {
            if (!result.isComplete()) {
                log.info("Command Canceled");
                Set<SimpleDevice> canceledDevices = new HashSet<>();
                canceledDevices.addAll(deviceList);
                canceledDevices.removeAll(result.getSuccessDeviceCollection().getDeviceList());
                canceledDevices.removeAll(result.getFailureDeviceCollection().getDeviceList());
                canceledDevices.removeAll(result.getUnsupportedDeviceCollection().getDeviceList());
                
                result.complete();
                result.getCommandCompletionCallback().cancel();

                completeCommandRequestExecutionRecord(execution, CommandRequestExecutionStatus.CANCELING);
                commandRequestExecutionResultDao.saveUnsupported(canceledDevices, execution.getId(),
                    CommandRequestUnsupportedType.CANCELED);
                deviceBehaviorDao.updateBehaviorReportStatus(TYPE, BehaviorReportStatus.CANCELED, 
                    asDeviceIds(canceledDevices));
                deviceGroupMemberEditorDao.addDevices(cancelGroup, canceledDevices);
                updateRequestCount(execution, result.getFailureCount() + result.getSuccessCount());
                
                logService.cancelCompleted(result.getResultsId(), result.getTotalItems(), result.getSuccessCount(),
                    result.getFailureCount(), result.getUnsupportedCount(), String.valueOf(result.getCanceledCount()));
            }
        }
    }
}
