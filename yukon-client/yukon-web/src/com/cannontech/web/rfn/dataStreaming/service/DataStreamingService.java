package com.cannontech.web.rfn.dataStreaming.service;

import java.util.List;
import java.util.Map;

import com.cannontech.common.bulk.callbackResult.DataStreamingConfigResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.rfn.dataStreaming.DataStreamingConfigException;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.web.rfn.dataStreaming.model.SummarySearchCriteria;
import com.cannontech.web.rfn.dataStreaming.model.SummarySearchResult;
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
     * Requests Network Manager to simulate the effects of assigning the specified configuration to these devices.
     * @return VerificationInfo containing the results of the Network manager check.
     */
    VerificationInformation verifyConfiguration(int configId, List<Integer> deviceIds);
    
    /**
     * Informs Network Manager of a configuration that Yukon plans to send.
     * @throws DataStreamingConfigException If the configuration is disallowed or there is an error.
     */
    void sendNmConfiguration(DataStreamingConfig config, List<Integer> deviceIds) throws DataStreamingConfigException;
    
    /**
     * Informs Network Manager that Yukon plans to disable data streaming for the specified devices.
     * @throws DataStreamingConfigException If the configuration is disallowed or there is an error.
     */
    void sendNmConfigurationRemove(List<Integer> deviceIds) throws DataStreamingConfigException;
    
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

    int saveConfig(DataStreamingConfig config);

    DataStreamingConfig findDataStreamingConfiguration(int configId);

    void cancel(String key, LiteYukonUser user);

    DataStreamingConfigResult findDataStreamingResult(String resultKey);

    /**
     * Returns map of configurations to device collection that contains all assigned devices.
     */
    Map<DataStreamingConfig, DeviceCollection> getAllDataStreamingConfigurationsAndDevices();

    /**
     * Attempts to find data streaming summary based on the search criteria selected by the user.
     * 
     * @return search result.
     */
    List<SummarySearchResult> search(SummarySearchCriteria criteria);

}
