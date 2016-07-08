package com.cannontech.web.rfn.dataStreaming.service;

import java.util.List;

import com.cannontech.common.device.streaming.model.VerificationInfo;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfiguration;

public interface DataStreamingService {
    
    /**
     * @return All data streaming configurations in use.
     */
    List<DataStreamingConfiguration> getAllDataStreamingConfigurations();
    
    /**
     * Requests Network Manager to simulate the effects of assigning the specified configuration to these devices.
     * @return VerificationInfo containing the results of the Network manager check.
     */
    VerificationInfo verifyConfiguration(DataStreamingConfiguration config, List<Integer> deviceIds);
    
    /**
     * Assign an existing data streaming configuration to the devices.
     * @param configId The behaviorId of the existing config.
     */
    void assignDataStreamingConfig(int configId, List<Integer> deviceIds, LiteYukonUser user);
    
    /**
     * Assign a new data streaming configuration to the devices.
     */
    void assignDataStreamingConfig(DataStreamingConfiguration config, List<Integer> deviceIds, LiteYukonUser user);
    
    /**
     * Unassign the data streaming configuration currently assigned to the specified devices. (They will no longer 
     * stream data.)
     */
    void unassignDataStreamingConfig(List<Integer> deviceIds);
    
    /**
     * Save the reported data streaming configuration that was reported to us from the device, via Porter.
     */
    void saveReportedConfig(DataStreamingConfiguration config, int deviceId);
}
