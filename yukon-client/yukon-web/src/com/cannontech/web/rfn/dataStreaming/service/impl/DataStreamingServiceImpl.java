package com.cannontech.web.rfn.dataStreaming.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.callbackResult.DataStreamingConfigCallback;
import com.cannontech.common.bulk.callbackResult.DataStreamingConfigResult;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
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
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingAttribute;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.rfn.dataStreaming.DataStreamingPorterConnection;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingAttribute;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.web.rfn.dataStreaming.model.VerificationInformation;
import com.cannontech.web.rfn.dataStreaming.service.DataStreamingService;

public class DataStreamingServiceImpl implements DataStreamingService {

    private static final Logger log = YukonLogManager.getLogger(DataStreamingServiceImpl.class);

    public final static String CHANNELS_STRING = "channels";
    public final static String ATTRIBUTE_STRING = ".attribute";
    public final static String INTERVAL_STRING = ".interval";

    @Autowired private DeviceBehaviorDao deviceBehaviorDao;
    @Autowired private DataStreamingPorterConnection porterConn;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Resource(name = "recentResultsCache") private RecentResultsCache<DataStreamingConfigResult> resultsCache;

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
        // TODO
        // Build verification message for NM
        // Send verification message
        // Wait for response message
        // Build VerificationInfo from response message
        // Return VerificationInfo
        return null;
    }

    @Override
    public VerificationInformation verifyConfiguration(int configId, List<Integer> deviceIds) {
        // TODO
        // Build verification message for NM
        // Send verification message
        // Wait for response message
        // Build VerificationInfo from response message
        // Return VerificationInfo
        return null;
    }

    @Override
    public DataStreamingConfigResult assignDataStreamingConfig(int configId, DeviceCollection deviceCollection,
            LiteYukonUser user) {
        List<Integer> deviceIds = new ArrayList<>();
        deviceCollection.getDeviceList().forEach(device -> deviceIds.add(device.getDeviceId()));
        deviceBehaviorDao.assignBehavior(configId, BehaviorType.DATA_STREAMING, deviceIds);
        return sendConfiguration(user, deviceCollection);
    }
    
    private DataStreamingConfigResult sendConfiguration(LiteYukonUser user, DeviceCollection deviceCollection) {
        log.info("Attemting to send configuration command to "+deviceCollection.getDeviceCount()+" devices.");
        final DataStreamingConfigResult result = new DataStreamingConfigResult();
        String resultsId = resultsCache.addResult(result);
        result.setResultsId(resultsId);
        final CommandRequestExecution execution = commandRequestExecutionDao.createStartedExecution(
            CommandRequestType.DEVICE, DeviceRequestType.DATA_STREAMING_CONFIG, 0, user);

        final StoredDeviceGroup allDevicesGroup = tempDeviceGroupService.createTempGroup();
        final StoredDeviceGroup successGroup = tempDeviceGroupService.createTempGroup();
        final StoredDeviceGroup failedGroup = tempDeviceGroupService.createTempGroup();
        final StoredDeviceGroup canceledGroup = tempDeviceGroupService.createTempGroup();
        final StoredDeviceGroup unsupportedGroup = tempDeviceGroupService.createTempGroup();

        result.setAllDevicesCollection(deviceCollection);
        result.setSuccessDeviceCollection(deviceGroupCollectionHelper.buildDeviceCollection(successGroup));
        result.setFailureDeviceCollection(deviceGroupCollectionHelper.buildDeviceCollection(failedGroup));
        result.setCanceledDeviceCollection(deviceGroupCollectionHelper.buildDeviceCollection(canceledGroup));
        result.setUnsupportedCollection(deviceGroupCollectionHelper.buildDeviceCollection(unsupportedGroup));

        DataStreamingConfigCallback callback = new DataStreamingConfigCallback() {
            @Override
            public void receivedConfigReport(SimpleDevice device, ReportedDataStreamingConfig config) {
                deviceGroupMemberEditorDao.addDevices(successGroup, device);
                result.addTimestamp(device, new Instant());
            }

            @Override
            public void receivedConfigError(SimpleDevice device, SpecificDeviceErrorDescription error) {
                log.debug("Recived a config error for device=" + device + " error=" + error.getDescription());
                deviceGroupMemberEditorDao.addDevices(failedGroup, device);
                result.addError(device, error);
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
                    canceledDevices.addAll(deviceCollection.getDeviceList());
                    canceledDevices.removeAll(result.getSuccessDeviceCollection().getDeviceList());
                    canceledDevices.removeAll(result.getFailureDeviceCollection().getDeviceList());
                    canceledDevices.removeAll(result.getUnsupportedCollection().getDeviceList());
                    completeCommandRequestExecutionRecord(execution, CommandRequestExecutionStatus.CANCELING);
                    commandRequestExecutionResultDao.saveUnsupported(
                        new HashSet<>(result.getCanceledDeviceCollection().getDeviceList()), execution.getId(),
                        CommandRequestUnsupportedType.CANCELED);
                    updateRequestCount(execution, result.getFailureCount() + result.getSuccessCount());
                }
            }
        };
        result.setConfigCallback(callback);
        List<SimpleDevice> allDevices = deviceCollection.getDeviceList();
        deviceGroupMemberEditorDao.addDevices(allDevicesGroup, allDevices);
        List<SimpleDevice> unsupportedDevices =
            deviceCollection.getDeviceList().stream().filter((x) -> !x.getDeviceType().isRfMeter()).collect(
                Collectors.toList());
        List<SimpleDevice> supportedDevices = new ArrayList<>();
        supportedDevices.addAll(allDevices);
        supportedDevices.removeAll(unsupportedDevices);
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
            CommandCompletionCallback<CommandRequestDevice> completionCallback =
                porterConn.sendConfiguration(commands, callback, user);
            result.setCommandCompletionCallback(completionCallback);
        }
        updateRequestCount(execution, supportedDevices.size());
        return result;
    }
    
    private void updateRequestCount(CommandRequestExecution execution, int count){
        execution.setRequestCount(count);
        if (log.isDebugEnabled()) {
            log.debug("Updating request count to " + count +".");
        }
        commandRequestExecutionDao.saveOrUpdate(execution);
    }

    @Override
    public void cancel(String key, LiteYukonUser user) {
        DataStreamingConfigResult result = resultsCache.getResult(key);
        result.getCommandCompletionCallback().cancel();
        porterConn.cancel(result.getCommandCompletionCallback() ,user);
    }

    @Override
    public DataStreamingConfigResult unassignDataStreamingConfig(DeviceCollection deviceCollection, LiteYukonUser user) {
        List<Integer> deviceIds = new ArrayList<>();
        deviceCollection.getDeviceList().forEach(device -> deviceIds.add(device.getDeviceId()));
        deviceBehaviorDao.unassignBehavior(BehaviorType.DATA_STREAMING, deviceIds);
        return sendConfiguration(user, deviceCollection);
    }

    @Override
    public void saveReportedConfig(ReportedDataStreamingConfig config, int deviceId) {
        BehaviorReport report = new BehaviorReport();
        report.setType(BehaviorType.DATA_STREAMING);
        report.setStatus(BehaviorReportStatus.PENDING);
        report.setTimestamp(Instant.now());
        report.setDeviceId(deviceId);
        report.setValues(generateBehaviorReportValues(config));

        deviceBehaviorDao.saveBehaviorReport(report);
    }

    private Map<String, String> generateBehaviorReportValues(ReportedDataStreamingConfig config) {
        Map<String, String> values = new HashMap<>();

        addReportValue("enabled", config.isStreamingEnabled(), values);
        addReportValue("channels", config.getConfiguredMetrics().size(), values);

        for (int i = 0; i < config.getConfiguredMetrics().size(); i++) {
            ReportedDataStreamingAttribute metric = config.getConfiguredMetrics().get(i);
            addReportValue("channels." + i + ".attribute", metric.getAttribute(), values);
            addReportValue("channels." + i + ".interval", metric.getInterval(), values);
            addReportValue("channels." + i + ".enabled", metric.isEnabled(), values);
        }

        return values;
    }

    private void addReportValue(String name, Object value, Map<String, String> values) {
        values.put(name, String.valueOf(value));
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
        behavior.setId(behavior.getId());
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

    private void completeCommandRequestExecutionRecord(CommandRequestExecution cre,
            CommandRequestExecutionStatus status) {
        cre.setStopTime(new Date());
        cre.setCommandRequestExecutionStatus(status);
        commandRequestExecutionDao.saveOrUpdate(cre);
    }
}
