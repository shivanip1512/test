package com.cannontech.web.rfn.dataStreaming.service;

import java.util.Collection;

import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigRequest;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigResponse;
import com.cannontech.common.rfn.message.datastreaming.gateway.GatewayDataStreamingInfo;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.web.rfn.dataStreaming.DataStreamingConfigException;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfig;
import com.google.common.collect.Multimap;

/**
 * This service handles data streaming communications between Yukon web and Network Manager.
 */
public interface DataStreamingCommunicationService {
    
    /**
     * Build a data streaming configuration request intended to "test" a possible configuration. Network Manager will
     * approve the request if it has no significant errors and doesn't exceed gateway loading limits.
     * 
     * This type of request does not change the actual data streaming configurations. It is only a test. Once the
     * potential configuration has been verified, use <code>buildConfigRequest</code> to actually update the
     * configurations.
     */
    DeviceDataStreamingConfigRequest buildVerificationRequest(Multimap<DataStreamingConfig, Integer> configToDeviceIds);
    
    /**
     * Build a data streaming configuration request intended to update actual configurations. Network Manager will
     * approve the request if it has no significant errors and doesn't exceed gateway loading limits. A "verification"
     * request should be sent first to minimize the risk of rejection.
     */
    DeviceDataStreamingConfigRequest buildConfigRequest(Multimap<DataStreamingConfig, Integer> configToDeviceIds,
                                                        String correlationId);
    
    /**
     * Build a data streaming sync request intended to notify Network Manager of the device's response to data streaming
     * configuration.
     */
    DeviceDataStreamingConfigRequest buildSyncRequest(ReportedDataStreamingConfig reportedConfig, int deviceId,
                                                      String correlationId);
    
    /**
     * Sends a verification, config or sync request to Network Manager. For a verification request, a successful response
     * means that the config request can be sent. For a config request, a successful response means that Network
     * Manager has updated its records to reflect the data streaming configuration change, and Yukon must now send the 
     * corresponding changes to the devices (via Porter).
     * @throws DataStreamingConfigException if there was a connection problem sending the request.
     */
    DeviceDataStreamingConfigResponse sendConfigRequest(DeviceDataStreamingConfigRequest request) throws DataStreamingConfigException;
    
    /**
     * Sends a request for gateway data streaming information to Network Manager for the specified gateways.
     * @throws DataStreamingConfigException if there was a connection problem sending the request.
     */
    Collection<GatewayDataStreamingInfo> getGatewayInfo(Collection<RfnGateway> gateways) throws DataStreamingConfigException;

}
