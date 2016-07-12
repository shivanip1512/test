package com.cannontech.web.rfn.dataStreaming.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.streaming.dao.DeviceBehaviorDao;
import com.cannontech.common.device.streaming.model.Behavior;
import com.cannontech.common.device.streaming.model.BehaviorType;
import com.cannontech.common.device.streaming.model.BehaviorValue;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingAttribute;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.web.rfn.dataStreaming.model.VerificationInformation;
import com.cannontech.web.rfn.dataStreaming.service.DataStreamingService;

public class DataStreamingServiceImpl implements DataStreamingService {
    
    private final static String channelsStr= "channels";
    private final static String attributeStr = ".attribute";
    private final static String intervalStr = ".interval";
    
    @Autowired private DeviceBehaviorDao deviceBehaviorDao;
    
    @Override
    public DataStreamingConfig findDataStreamingConfiguration(int configId) {
        Behavior behavior = deviceBehaviorDao.getBehaviorById(configId);
        return convertBehaviorToConfig(behavior);
    }
    
    @Override
    public DataStreamingConfig findDataStreamingConfigurationForDevice(int deviceId) {
       return null;
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
    public void assignDataStreamingConfig(int configId, List<Integer> deviceIds) {
        //TODO
        //Send notification message to NM for the pending configuration change
          //If NM rejects/errors, abort the operation
        //Update behavior assignment for devices based on id
        deviceBehaviorDao.assignBehavior(configId, BehaviorType.DATA_STREAMING, deviceIds);
        //Send configs via porter //DataStreamingPorterConnection.sendConfiguration(...)
        //return an identifier for getting results of this operation (sendConfiguration will return this id)
    }
    
    @Override
    public void unassignDataStreamingConfig(int configId, List<Integer> deviceIds) {
        //TODO
        //Send notification message to NM for the pending configuration change
          //If NM rejects/errors, abort the operation
        //Create behavior based on config
        //Check for existing identical behavior
          //otherwise insert new behavior
        //Update behavior assignment for devices
        deviceBehaviorDao.unassignBehavior(configId, BehaviorType.DATA_STREAMING, deviceIds);
        //Send configs via porter //DataStreamingPorterConnection.sendConfiguration(...)
        //return an identifier for getting results of this operation (sendConfiguration will return this id)
    }
    
    @Override
    public void saveReportedConfig(DataStreamingConfig config, int deviceId) {
        //TODO
        //Create BehaviorReport based on config
        //Update for device
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
        BehaviorValue channelValue = findBehaviorValueByName(behavior, channelsStr);
        int channels = Integer.parseInt(channelValue.getValue());
        while (i < channels) {
            String key = channelsStr + "." + i;
            BehaviorValue attributeValue = findBehaviorValueByName(behavior, key + attributeStr);
            BehaviorValue intervalValue = findBehaviorValueByName(behavior, key + intervalStr);
            DataStreamingAttribute dataStreamingAttribute = new DataStreamingAttribute();
            dataStreamingAttribute.setAttribute(BuiltInAttribute.valueOf(attributeValue.getValue()));
            dataStreamingAttribute.setInterval(Integer.parseInt(intervalValue.getValue()));
            dataStreamingAttribute.setAttributeOn(Boolean.TRUE);
            config.addAttribute(dataStreamingAttribute);
            i++;
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
        BehaviorValue value = new BehaviorValue(channelsStr, String.valueOf(attributes.size()));
        behavior.getValues().add(value);
        for (int i = 0; i < attributes.size(); i++) {
            BuiltInAttribute attribute = attributes.get(i).getAttribute();
            int interval = attributes.get(i).getInterval();
            String key = channelsStr + "." + i;
            BehaviorValue valueAttribute = new BehaviorValue(key + attributeStr, attribute.getKey());
            behavior.getValues().add(valueAttribute);
            BehaviorValue intervalAttribute = new BehaviorValue(key + intervalStr, String.valueOf(interval));
            behavior.getValues().add(intervalAttribute);
        }
        return behavior;
    }

    private BehaviorValue findBehaviorValueByName(Behavior behavior, String name) {
        return behavior.getValues().stream().filter(v -> v.getName().equals(name)).findFirst().get();
    }
}
