package com.cannontech.web.rfn.dataStreaming.service.impl;

import java.util.List;

import com.cannontech.common.device.streaming.model.VerificationInfo;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.web.rfn.dataStreaming.service.DataStreamingService;

public class DataStreamingServiceImpl implements DataStreamingService {
    
    @Override
    public DataStreamingConfig findDataStreamingConfigurationForDevice(int deviceId) {
        //TODO
        //Retrieve behavior of type data streaming config for deviceId
        //Convert behavior into DataStreamingConfiguration
        //Return DataStreamingConfiguration
        return null;
    }
    
    @Override
    public List<DataStreamingConfig> getAllDataStreamingConfigurations() {
        //TODO
        //Retrieve all behaviors of type data streaming config
        //Convert behaviors into DataStreamingConfiguration
        //Return DataStreamingConfigurations
        return null;
    }
    
    @Override
    public VerificationInfo verifyConfiguration(DataStreamingConfig config, List<Integer> deviceIds) {
        //TODO
        //Build verification message for NM
        //Send verification message
          //Wait for response message
        //Build VerificationInfo from response message
        //Return VerificationInfo
        return null;
    }
    
    @Override
    public VerificationInfo verifyConfiguration(int configId, List<Integer> deviceIds) {
        //TODO
        //Build verification message for NM
        //Send verification message
          //Wait for response message
        //Build VerificationInfo from response message
        //Return VerificationInfo
        return null;
    }
    
    
    @Override
    public void assignDataStreamingConfig(int configId, List<Integer> deviceIds, LiteYukonUser user) {
        //TODO
        //Send notification message to NM for the pending configuration change
          //If NM rejects/errors, abort the operation
        //Update behavior assignment for devices based on id
        //Send configs via porter //DataStreamingPorterConnection.sendConfiguration(...)
        //return an identifier for getting results of this operation (sendConfiguration will return this id)
    }
    
    @Override
    public void assignDataStreamingConfig(DataStreamingConfig config, List<Integer> deviceIds, LiteYukonUser user) {
        //TODO
        //Send notification message to NM for the pending configuration change
          //If NM rejects/errors, abort the operation
        //Create behavior based on config
        //Check for existing identical behavior
          //otherwise insert new behavior
        //Update behavior assignment for devices
        //Send configs via porter //DataStreamingPorterConnection.sendConfiguration(...)
        //return an identifier for getting results of this operation (sendConfiguration will return this id)
    }
    
    @Override
    public void unassignDataStreamingConfig(List<Integer> deviceIds) {
        //TODO
        //dao call to remove data streaming behavior from specified ids
    }
    
    @Override
    public void saveReportedConfig(DataStreamingConfig config, int deviceId) {
        //TODO
        //Create BehaviorReport based on config
        //Update for device
    }
    
}
