package com.cannontech.web.rfn.dataStreaming.service;

import java.util.List;

import com.cannontech.common.bulk.callbackResult.DataStreamingConfigResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.web.rfn.dataStreaming.model.VerificationInformation;

public interface DataStreamingService {
    
    /**
     * @return The DataStreamingConfiguration assigned to the device, or null if no configuration is assigned.
     */
    DataStreamingConfig findDataStreamingConfigurationForDevice(int deviceId);
    
    /**
     * @return All data streaming configurations in use.
     */
    List<DataStreamingConfig> getAllDataStreamingConfigurations();
    
    /**
     * Requests Network Manager to simulate the effects of assigning the specified configuration to these devices.
     * @return VerificationInfo containing the results of the Network manager check.
     */
    VerificationInformation verifyConfiguration(DataStreamingConfig config, List<Integer> deviceIds);
    
    /**
     * Assign an existing data streaming configuration to the devices.
     * @param configId The behaviorId of the existing config.
     * @return The resultId to look up the results in recentResultsCache.
     */
    DataStreamingConfigResult assignDataStreamingConfig(int configId, DeviceCollection deviceCollection, LiteYukonUser user);
    
    /**
     * Unassign the data streaming configuration currently assigned to the specified devices. (They will no longer 
     * stream data.)
     * @return The resultId to look up the results in recentResultsCache.
     */
    DataStreamingConfigResult unassignDataStreamingConfig(DeviceCollection deviceCollection, LiteYukonUser user);
    
    /**
     * Save the reported data streaming configuration that was reported to us from the device, via Porter.
     */
    void saveReportedConfig(ReportedDataStreamingConfig config, int deviceId);

    VerificationInformation verifyConfiguration(int configId, List<Integer> deviceIds);

    int saveConfig(DataStreamingConfig config);

    DataStreamingConfig findDataStreamingConfiguration(int configId);

    void cancel(String key, LiteYukonUser user);

    DataStreamingConfigResult findDataStreamingResult(String resultKey);

    //TODO: retrieve all mismatches (reported config and stored config don't match)
}
