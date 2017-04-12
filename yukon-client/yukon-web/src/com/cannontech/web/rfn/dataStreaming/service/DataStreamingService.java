package com.cannontech.web.rfn.dataStreaming.service;

import java.util.List;
import java.util.Map;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.streaming.dao.DeviceBehaviorStrings;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.rfn.dataStreaming.DataStreamingConfigException;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfigResult;
import com.cannontech.web.rfn.dataStreaming.model.DiscrepancyResult;
import com.cannontech.web.rfn.dataStreaming.model.SummarySearchCriteria;
import com.cannontech.web.rfn.dataStreaming.model.SummarySearchResult;
import com.cannontech.web.rfn.dataStreaming.model.VerificationInformation;

public interface DataStreamingService extends DeviceBehaviorStrings{
    
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
    VerificationInformation verifyConfiguration(DataStreamingConfig config, List<SimpleDevice> devices);
    
    /**
     * Assign a data streaming configuration to the devices.
     * @param config The configuration to assign to the devices.  If the deviceCollection contains multiple device types, 
     *     the config will be split into multiple configs containing the subset of attributes supported by each device type.
     * @param failedVerificationDevices A list of ids for devices that failed NM verification.
     * @param user The user who initiated this request.
     * @return The resultId to look up the results in recentResultsCache.
     * @throws DataStreamingConfigException If the configuration is disallowed or there is an error.
     */
    public DataStreamingConfigResult assignDataStreamingConfig(DataStreamingConfig config, DeviceCollection deviceCollection, 
                                                               List<Integer> failedVerificationDevices, LiteYukonUser user) 
                                                               throws DataStreamingConfigException;
    
    /**
     * Unassign the data streaming configuration currently assigned to the specified devices. (They will no longer 
     * stream data.)
     * @return The resultId to look up the results in recentResultsCache.
     * @throws DataStreamingConfigException when there is an error communicating to Network Manager, or Network Manager
     * rejects the configuration request.
     */
    DataStreamingConfigResult unassignDataStreamingConfig(DeviceCollection deviceCollection, LiteYukonUser user) throws DataStreamingConfigException;
    
    /**
     * Cancels a configuration operation that is in progress.
     * @param resultId The id of the operation to cancel.
     */
    void cancel(String resultId, LiteYukonUser user);
    
    /**
     * Gets the result of a configuration operation.
     * @param result of the operation.
     */
    DataStreamingConfigResult findDataStreamingResult(String resultId);
    
    /**
     * Resends config
     * 
     * @param deviceIds - to re-send config to
     */
    DataStreamingConfigResult resend(List<Integer> deviceIds, LiteYukonUser user) throws DataStreamingConfigException;

    /**
     * Returns map of configurations to device collection that contains all assigned devices.
     */
    Map<DataStreamingConfig, DeviceCollection> getAllDataStreamingConfigurationsAndDevices();

    /**
     * Attempts to find data streaming summary based on the search criteria selected by the user.
     * 
     * @return search result.
     * @throws DataStreamingConfigException if there is a communication error requesting information from Network Manager.
     */
    List<SummarySearchResult> search(SummarySearchCriteria criteria) throws DataStreamingConfigException;

    /**
     * Attempts to find discrepancies by comparing behavior to behavior report .
     * 
     * @return the list of discrepancies.
     * @throws DataStreamingConfigException if there is a communication error requesting information from Network
     *         Manager.
     */
    List<DiscrepancyResult> findDiscrepancies();

    /**
     * Attempts to find discrepancies by comparing behavior to behavior report for a single device.
     * 
     * @return null is returned if there is no discrepancy
     * @throws DataStreamingConfigException if there is a communication error requesting information from Network
     *         Manager.
     */
    DiscrepancyResult findDiscrepancy(int deviceId);
    
    /**
     * 1. For each device finds behavior report
     * 2. Attempts to figure if behavior exists matching the the behavior report.
     *    a. If behavior doesn't exist creates a new behavior
     * 3. Assigns device to behavior that was found/created
     * 4. Re-sends config if behavior report has a not OK metric status
     * @throws DataStreamingConfigException 
     * 
     */
    DataStreamingConfigResult accept(List<Integer> deviceIds, LiteYukonUser user) throws DataStreamingConfigException;

    /**
     * Gets any overloaded gateways
     * 
     * @return the list of overloaded gateways
     * @throws DataStreamingConfigException if there is a communication error requesting information from Network
     *         Manager.
     */
    List<RfnGateway> getOverloadedGateways() throws DataStreamingConfigException;

    DataStreamingConfigResult deleteDataStreamingReportAndUnassignConfig(int deviceId, LiteYukonUser user);

    DataStreamingConfigResult read(int deviceId, LiteYukonUser user) throws DataStreamingConfigException;
}
