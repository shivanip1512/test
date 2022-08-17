package com.cannontech.amr.rfn.dataStreaming.service.impl;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.amr.rfn.dao.RfnDeviceAttributeDao;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.dataStreaming.model.DataStreamingAttribute;
import com.cannontech.amr.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.amr.rfn.dataStreaming.model.DataStreamingConfigException;
import com.cannontech.amr.rfn.dataStreaming.service.DataStreamingCommunicationService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.dataStreaming.DataStreamingAttributeHelper;
import com.cannontech.common.rfn.dataStreaming.DataStreamingMetricStatus;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfig;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfig.MetricConfig;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigRequest;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigRequestType;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigResponse;
import com.cannontech.common.rfn.message.datastreaming.gateway.GatewayDataStreamingInfo;
import com.cannontech.common.rfn.message.datastreaming.gateway.GatewayDataStreamingInfoRequest;
import com.cannontech.common.rfn.message.datastreaming.gateway.GatewayDataStreamingInfoResponse;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class DataStreamingCommunicationServiceImpl implements DataStreamingCommunicationService {
    
    private static final Logger log = YukonLogManager.getLogger(DataStreamingCommunicationServiceImpl.class);
    private static final String configRequestCparm = "DATA_STREAMING_CONFIG_REQUEST";
    private static final String gatewayInfoRequestCparm = "DATA_STREAMING_GATEWAY_INFO_REQUEST";
    private static final String requestQueue = "com.eaton.eas.yukon.networkmanager.dataStreaming.request";
    
    @Autowired private ConfigurationSource configSource;
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private RfnDeviceAttributeDao rfnDeviceAttributeDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private DataStreamingAttributeHelper dataStreamingAttributeHelper;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    private RequestReplyTemplate<DeviceDataStreamingConfigResponse> configRequestTemplate;
    private RequestReplyTemplate<GatewayDataStreamingInfoResponse> gatewayInfoRequestTemplate;
    private boolean isDataStreamingEnabled;
    
    @PostConstruct
    public void init() {
        configRequestTemplate =
            new RequestReplyTemplateImpl<>(configRequestCparm, configSource, connectionFactory, requestQueue, false);
        gatewayInfoRequestTemplate = new RequestReplyTemplateImpl<>(gatewayInfoRequestCparm, configSource,
            connectionFactory, requestQueue, false);
        isDataStreamingEnabled = configSource.getBoolean(MasterConfigBoolean.RF_DATA_STREAMING_ENABLED, false);
    }
   
    @Override
    public DeviceDataStreamingConfigRequest buildVerificationRequest(Multimap<DeviceDataStreamingConfig, Integer> configToDeviceIds) {
        
        DeviceDataStreamingConfigRequest request = buildRequest(configToDeviceIds, 
                                                                DeviceDataStreamingConfigRequestType.ASSESS,
                                                                0);
        return request;
    }
    
    @Override
    public DeviceDataStreamingConfigRequest buildConfigRequest(Multimap<DeviceDataStreamingConfig, Integer> configToDeviceIds,
            DeviceDataStreamingConfigRequestType type, int requestSeqNumber) {

        DeviceDataStreamingConfigRequest request = buildRequest(configToDeviceIds, type, requestSeqNumber);
        return request;
    }
    
    @Override
    public DeviceDataStreamingConfigRequest buildSyncRequest(ReportedDataStreamingConfig reportedConfig, SimpleDevice device,
                                                             int requestSeqNumber) {
        
        DeviceDataStreamingConfig config = new DeviceDataStreamingConfig();
        config.setDataStreamingOn(reportedConfig.isStreamingEnabled());
        config.setSequenceNumber(reportedConfig.getSequence());
        
        Map<Integer, MetricConfig> metrics = 
        reportedConfig.getConfiguredMetrics().stream().collect(Collectors.toMap(
                          attribute -> {
                              BuiltInAttribute attr = BuiltInAttribute.valueOf(attribute.getAttribute());
                              return rfnDeviceAttributeDao.getMetricIdForAttribute(attr, device.getDeviceType());
                          },
                          attribute -> {
                              MetricConfig metricConfig = new MetricConfig();
                              metricConfig.setEnabled(attribute.isEnabled());
                              metricConfig.setInterval((short)attribute.getInterval());
                              metricConfig.setStatus(attribute.getStatus().asShort());
                              return metricConfig;
                          }));
        config.setMetrics(metrics);
        
        DeviceDataStreamingConfigRequest request = new DeviceDataStreamingConfigRequest();
        request.setConfigs(new DeviceDataStreamingConfig[]{config});
        
        RfnIdentifier rfnId = rfnDeviceDao.getDeviceForId(device.getDeviceId()).getRfnIdentifier();
        Map<RfnIdentifier, Integer> devices = new HashMap<>();
        devices.put(rfnId, 0); // Only one config to map to, at index 0
        request.setDevices(devices);
        
        request.setRequestExpiration(DateTimeConstants.MINUTES_PER_DAY);
        request.setRequestSeqNumber(requestSeqNumber);
        request.setRequestType(DeviceDataStreamingConfigRequestType.CONFIRM);
        
        return request;
    }
    
    @Override
    public DeviceDataStreamingConfigResponse sendConfigRequest(DeviceDataStreamingConfigRequest request) 
            throws DataStreamingConfigException {
        
        log.debug("Sending data streaming config request to Network Manager: " + request);
        
        //Send the request
        BlockingJmsReplyHandler<DeviceDataStreamingConfigResponse> replyHandler = 
                new BlockingJmsReplyHandler<>(DeviceDataStreamingConfigResponse.class);
        configRequestTemplate.send(request, replyHandler);
        
        //Wait for the response
        DeviceDataStreamingConfigResponse response;
        try {
            response = replyHandler.waitForCompletion();
            log.debug("Data Streaming Config response: " + response);
        } catch (ExecutionException e) {
            String errorMessage = "Unable to send request due to a communication error between Yukon and Network Manager";
            throw new DataStreamingConfigException(errorMessage, e, "commsError"); 
        }
        
        return response;
    }
    
    @Override
    public Collection<GatewayDataStreamingInfo> getGatewayInfo(Collection<RfnGateway> gateways, boolean shouldArchive)
            throws DataStreamingConfigException {

        Set<RfnIdentifier> gatewayIds = gateways.stream()
                                                .filter((gateway) -> gateway.isDataStreamingSupported())
                                                .map(gateway -> gateway.getRfnIdentifier()).collect(Collectors.toSet());

        GatewayDataStreamingInfoRequest request = new GatewayDataStreamingInfoRequest();
        request.setGatewayRfnIdentifiers(gatewayIds);

        log.debug("Sending gateway data streaming info request to Network Manager: " + request);

        // Send the request
        BlockingJmsReplyHandler<GatewayDataStreamingInfoResponse> replyHandler =
            new BlockingJmsReplyHandler<>(GatewayDataStreamingInfoResponse.class);
        gatewayInfoRequestTemplate.send(request, replyHandler);

        // Wait for the response
        GatewayDataStreamingInfoResponse response;
        try {
            response = replyHandler.waitForCompletion();
            log.debug("Gateway data streaming info response: " + response);

            for (GatewayDataStreamingInfo info : response.getGatewayDataStreamingInfos().values()) {
                RfnDevice gateway = rfnDeviceDao.getDeviceForExactIdentifier(info.getGatewayRfnIdentifier());
                Collection<Double> streamingRfnDevices = Optional.ofNullable(info.getDeviceRfnIdentifiers()).map(Map::values).orElse(Collections.emptyList());
                double streamingCount = streamingRfnDevices.stream().filter(load -> load > 0).count();
                double connectedCount = streamingRfnDevices.size();
                if (isDataStreamingEnabled) {
                    rfnGatewayService.generatePointData(gateway, BuiltInAttribute.DATA_STREAMING_LOAD,
                        info.getDataStreamingLoadingPercent(), shouldArchive);
                    rfnGatewayService.generatePointData(gateway, BuiltInAttribute.STREAMING_ACTIVE_DEVICE_COUNT,
                        streamingCount, shouldArchive);
                    rfnGatewayService.generatePointData(gateway, BuiltInAttribute.STREAMING_CAPABLE_DEVICE_COUNT,
                        connectedCount, shouldArchive);
                }
            }
        } catch (ExecutionException e) {
            String errorMessage =
                "Unable to send request due to a communication error between Yukon and Network Manager";
            throw new DataStreamingConfigException(errorMessage, e, "commsError");
        }

        return response.getGatewayDataStreamingInfos().values();
    }

    /**
     * Builds a DeviceDataStreamingConfigRequest with all the specified configs and devices.
     */
    private DeviceDataStreamingConfigRequest buildRequest(Multimap<DeviceDataStreamingConfig, Integer> configToDeviceIds, 
                                                          DeviceDataStreamingConfigRequestType requestType,
                                                          int requestSeqNumber) {
        
        log.debug("Building " + requestType + " data streaming config request");
        
        DeviceDataStreamingConfigRequest request = new DeviceDataStreamingConfigRequest();
        
        //These will hold the configs, and the mapping of devices to configs
        DeviceDataStreamingConfig[] configs = new DeviceDataStreamingConfig[configToDeviceIds.keySet().size()];
        Map<RfnIdentifier, Integer> deviceToConfigIdMap = new HashMap<>();
        
        int i = 0;
        for (Entry<DeviceDataStreamingConfig, Collection<Integer>> configAssignments : configToDeviceIds.asMap().entrySet()) {
            configs[i] = configAssignments.getKey();
            
            //Get the devices using this config
            Collection<Integer> deviceIds = configAssignments.getValue();
            List<RfnDevice> rfnDevices = rfnDeviceDao.getDevicesByPaoIds(deviceIds); //TODO performance
            
            //Create a map of the devices' RfnIdentifiers to the config's index in the config array
            int configIndex = i;
            Map<RfnIdentifier, Integer> partialDeviceToConfigIdMap = rfnDevices.stream()
                    .map(device -> device.getRfnIdentifier())
                    .collect(Collectors.toMap(rfnId -> rfnId, 
                                              rfnId -> configIndex));
            deviceToConfigIdMap.putAll(partialDeviceToConfigIdMap);
            
            i++;
        }
        
        request.setConfigs(configs);
        request.setDevices(deviceToConfigIdMap);
        request.setRequestType(requestType);
        if (requestType == DeviceDataStreamingConfigRequestType.UPDATE
            || requestType == DeviceDataStreamingConfigRequestType.UPDATE_WITH_FORCE) {
            request.setRequestExpiration(DateTimeConstants.MINUTES_PER_DAY);
            request.setRequestSeqNumber(requestSeqNumber);
        }

        return request;
    }
    
    @Override
    public DeviceDataStreamingConfig buildDeviceDataStreamingConfig(DataStreamingConfig config, PaoType type) {
        DeviceDataStreamingConfig nmConfig = new DeviceDataStreamingConfig();
        
        if (config == null || config.getAttributes().isEmpty()) {
            nmConfig.setDataStreamingOn(false);
        } else {
            nmConfig.setDataStreamingOn(true);
            
            //  Build a map of all the attributes in the config
            Map<BuiltInAttribute, DataStreamingAttribute> configAttributes = 
                    Maps.uniqueIndex(config.getAttributes(), DataStreamingAttribute::getAttribute);

            //  Get a list of all of the attributes supported by this type
            Collection<BuiltInAttribute> supportedAttributes = dataStreamingAttributeHelper.getSupportedAttributes(type);

            //  Get a default interval to use for disabled metrics 
            short defaultInterval = (short)config.getAttributes().get(0).getInterval();

            Map<Integer, MetricConfig> metrics = supportedAttributes.stream()
                .collect(Collectors.toMap(
                    attribute -> rfnDeviceAttributeDao.getMetricIdForAttribute(attribute, type),
                    attribute -> {
                        MetricConfig metricConfig = new MetricConfig();

                        Optional<DataStreamingAttribute> configAttribute = 
                                Optional.ofNullable(configAttributes.get(attribute));

                        metricConfig.setEnabled(
                            configAttribute.map(DataStreamingAttribute::getAttributeOn)
                                           .orElse(false));
                        metricConfig.setInterval((short)
                            configAttribute.map(DataStreamingAttribute::getInterval)
                                           .map(Integer::shortValue)
                                           .orElse(defaultInterval));

                        metricConfig.setStatus(DataStreamingMetricStatus.OK.asShort());  //  We're expecting it to be OK
                        return metricConfig;
                }));
            nmConfig.setMetrics(metrics);
        }

        return nmConfig;
    }
}
