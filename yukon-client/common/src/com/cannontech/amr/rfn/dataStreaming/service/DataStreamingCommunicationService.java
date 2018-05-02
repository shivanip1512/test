package com.cannontech.amr.rfn.dataStreaming.service;

import java.util.Collection;

import com.cannontech.amr.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.amr.rfn.dataStreaming.model.DataStreamingConfigException;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfig;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigRequest;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigRequestType;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigResponse;
import com.cannontech.common.rfn.message.datastreaming.gateway.GatewayDataStreamingInfo;
import com.cannontech.common.rfn.model.RfnGateway;
import com.google.common.collect.Multimap;

/**
 * This service handles data streaming communications between Yukon web and Network Manager.
 */
public interface DataStreamingCommunicationService {
    
    /**
     * Build a DeviceDataStreamingConfig (the NM messaging object) from a DataStreamingConfig (the Yukon model object).
     */
    public DeviceDataStreamingConfig buildDeviceDataStreamingConfig(DataStreamingConfig config, PaoType type);
    
    /**
     * Build a data streaming configuration request intended to "test" a possible configuration. Network Manager will
     * approve the request if it has no significant errors and doesn't exceed gateway loading limits.
     * 
     * This type of request does not change the actual data streaming configurations. It is only a test. Once the
     * potential configuration has been verified, use <code>buildConfigRequest</code> to actually update the
     * configurations.
     */
    DeviceDataStreamingConfigRequest buildVerificationRequest(Multimap<DeviceDataStreamingConfig, Integer> configToDeviceIds);
    
    /**
     * Build a data streaming configuration request intended to update actual configurations. Network Manager will
     * approve the request if it has no significant errors and doesn't exceed gateway loading limits. A "verification"
     * request should be sent first to minimize the risk of rejection.
     */
    DeviceDataStreamingConfigRequest buildConfigRequest(Multimap<DeviceDataStreamingConfig, Integer> configToDeviceIds,
            DeviceDataStreamingConfigRequestType type, int requestSeqNumber);
    
    /**
     * Build a data streaming sync request intended to notify Network Manager of the device's response to data streaming
     * configuration.
     */
    DeviceDataStreamingConfigRequest buildSyncRequest(ReportedDataStreamingConfig reportedConfig, SimpleDevice device,
            int requestSeqNumber);
    
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
     * When response is received, generates point data for
     * DATA_STREAMING_LOAD,STREAMING_ACTIVE_DEVICE_COUNT,STREAMING_CAPABLE_DEVICE_COUNT and sends it to dispatch.
     * 
     * @param shouldArchive - sets Pointdata.setTagsPointMustArchive to true or false.
     * @throws DataStreamingConfigException - if there was a connection problem sending the request.
     */
    Collection<GatewayDataStreamingInfo> getGatewayInfo(Collection<RfnGateway> gateways, boolean shouldArchive)
            throws DataStreamingConfigException;
}
