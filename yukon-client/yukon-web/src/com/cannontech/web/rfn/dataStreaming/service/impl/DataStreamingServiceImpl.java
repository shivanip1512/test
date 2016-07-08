package com.cannontech.web.rfn.dataStreaming.service.impl;

import java.util.List;

import com.cannontech.common.device.streaming.model.VerificationInfo;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfiguration;
import com.cannontech.web.rfn.dataStreaming.service.DataStreamingService;

public class DataStreamingServiceImpl implements DataStreamingService {
    
    @Override
    public List<DataStreamingConfiguration> getAllDataStreamingConfigurations() {
        //TODO
        //Retrieve all behaviors of type data streaming config
        //Convert behaviors into DataStreamingConfiguration
        //Return DataStreamingConfigurations
        return null;
    }
    
    @Override
    public VerificationInfo verifyConfiguration(DataStreamingConfiguration config, List<Integer> deviceIds) {
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
    public void assignDataStreamingConfig(DataStreamingConfiguration config, List<Integer> deviceIds, LiteYukonUser user) {
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
    public void saveReportedConfig(DataStreamingConfiguration config, int deviceId) {
        //TODO
        //Create BehaviorReport based on config
        //Update for device
    }
    
}
