package com.cannontech.web.rfn.dataStreaming.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.DataStreamingConfigCallbackResult;
import com.cannontech.common.device.commands.CommandRequestDevice;
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
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.rfn.dataStreaming.DataStreamingPorterConnection;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingAttribute;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.web.rfn.dataStreaming.model.VerificationInformation;
import com.cannontech.web.rfn.dataStreaming.service.DataStreamingService;
import com.cannontech.yukon.IDatabaseCache;

public class DataStreamingServiceImpl implements DataStreamingService {
    
    public final static String CHANNELS_STRING = "channels";
    public final static String ATTRIBUTE_STRING = ".attribute";
    public final static String INTERVAL_STRING = ".interval";
    
    @Autowired private DeviceBehaviorDao deviceBehaviorDao;
    @Autowired private DataStreamingPorterConnection porterConn;
    @Resource(name="recentResultsCache") private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache;
    @Autowired private IDatabaseCache serverDatabaseCache;
    
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
        //TODO
        //Build verification message for NM
        //Send verification message
          //Wait for response message
        //Build VerificationInfo from response message
        //Return VerificationInfo
        return null;
    }
    
    @Override
    public VerificationInformation verifyConfiguration(int configId, List<Integer> deviceIds) {
        //TODO
        //Build verification message for NM
        //Send verification message
          //Wait for response message
        //Build VerificationInfo from response message
        //Return VerificationInfo
        return null;
    }
    
    
    @Override
    public String assignDataStreamingConfig(int configId, List<Integer> deviceIds, LiteYukonUser user) {
        //TODO
        
        //Send notification message to NM for the pending configuration change
          //If NM rejects/errors, abort the operation
        
        //Update behavior assignment for devices based on id
        deviceBehaviorDao.assignBehavior(configId, BehaviorType.DATA_STREAMING, deviceIds);
        
        //Send config messages via porter
        List<SimpleDevice> devices = getDevicesByDeviceIds(deviceIds);
        DataStreamingConfigCallbackResult callbackResult = new DataStreamingConfigCallbackResult();
        String resultId = "";//TODO recentResultsCache.addResult(callbackResult);
        List<CommandRequestDevice> commands = porterConn.buildConfigurationCommandRequests(devices);
        porterConn.sendConfiguration(commands, callbackResult, user);
        
        return resultId;
    }
    
    private List<SimpleDevice> getDevicesByDeviceIds(Collection<Integer> deviceIds) {
        Map<Integer, LiteYukonPAObject> allPaosMap = serverDatabaseCache.getAllPaosMap();
        List<SimpleDevice> devices = new ArrayList<>();
        deviceIds.forEach(
            deviceId -> {
                LiteYukonPAObject pao = allPaosMap.get(deviceId);
                SimpleDevice device = new SimpleDevice(pao);
                devices.add(device);
            }
        );
        return devices;
    }
    
    @Override
    public String unassignDataStreamingConfig(List<Integer> deviceIds, LiteYukonUser user) {
        //TODO
        
        //Send notification message to NM for the pending configuration change
          //If NM rejects/errors, abort the operation

        //Update behavior assignment for devices
        deviceBehaviorDao.unassignBehavior(BehaviorType.DATA_STREAMING, deviceIds);
        
        //Send config messages via porter
        List<SimpleDevice> devices = getDevicesByDeviceIds(deviceIds);
        DataStreamingConfigCallbackResult callbackResult = new DataStreamingConfigCallbackResult();
        String resultId = "";//recentResultsCache.addResult(callbackResult);
        List<CommandRequestDevice> commands = porterConn.buildConfigurationCommandRequests(devices);
        porterConn.sendConfiguration(commands, callbackResult, user);
        
        return resultId;
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
}
