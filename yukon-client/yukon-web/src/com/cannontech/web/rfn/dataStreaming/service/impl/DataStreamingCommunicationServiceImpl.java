package com.cannontech.web.rfn.dataStreaming.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceAttributeDao;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfig;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfig.MetricConfig;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigRequest;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigRequestType;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigResponse;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.web.rfn.dataStreaming.DataStreamingConfigException;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.web.rfn.dataStreaming.service.DataStreamingCommunicationService;
import com.google.common.collect.Multimap;

public class DataStreamingCommunicationServiceImpl implements DataStreamingCommunicationService {
    
    private static final Logger log = YukonLogManager.getLogger(DataStreamingCommunicationServiceImpl.class);
    private static final String configRequestCparm = "DATA_STREAMING_REQUEST";
    private static final String requestQueue = "com.eaton.eas.yukon.networkmanager.dataStreaming.request";
    
    @Autowired private ConfigurationSource configSource;
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private RfnDeviceAttributeDao rfnDeviceAttributeDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnGatewayService rfnGatewayService;
    private RequestReplyTemplate<DeviceDataStreamingConfigResponse> configRequestTemplate;
    
    @PostConstruct
    public void init() {
        configRequestTemplate = new RequestReplyTemplateImpl<>(configRequestCparm, 
                configSource, connectionFactory, requestQueue, false);
    }
    
    @Override
    public DeviceDataStreamingConfigRequest buildVerificationRequest(Multimap<DataStreamingConfig, Integer> configToDeviceIds) {
        
        DeviceDataStreamingConfigRequest request = buildRequest(configToDeviceIds, 
                                                                DeviceDataStreamingConfigRequestType.TEST_ONLY,
                                                                null);
        return request;
    }
    
    @Override
    public DeviceDataStreamingConfigRequest buildConfigRequest(Multimap<DataStreamingConfig, Integer> configToDeviceIds,
                                                               String correlationId) {
        
        DeviceDataStreamingConfigRequest request = buildRequest(configToDeviceIds, 
                                                                DeviceDataStreamingConfigRequestType.TEST_ONLY,
                                                                correlationId);
        return request;
    }
    
    @Override
    public DeviceDataStreamingConfigRequest buildSyncRequest(ReportedDataStreamingConfig reportedConfig, int deviceId,
                                                             String correlationId) {
        
        DeviceDataStreamingConfig config = new DeviceDataStreamingConfig();
        config.setDataStreamingOn(reportedConfig.isStreamingEnabled());
        config.setSequenceNumber(reportedConfig.getSequence());
        
        Map<Integer, MetricConfig> metrics = 
        reportedConfig.getConfiguredMetrics().stream().collect(Collectors.toMap(
                          attribute -> {
                              BuiltInAttribute attr = BuiltInAttribute.valueOf(attribute.getAttribute());
                              return rfnDeviceAttributeDao.getMetricIdForAttribute(attr);
                          },
                          attribute -> {
                              MetricConfig metricConfig = new MetricConfig();
                              metricConfig.setEnabled(attribute.isEnabled());
                              metricConfig.setInterval((short)attribute.getInterval());
                              return metricConfig;
                          }));
        config.setMetrics(metrics);
        
        DeviceDataStreamingConfigRequest request = new DeviceDataStreamingConfigRequest();
        request.setConfigs(new DeviceDataStreamingConfig[]{config});
        
        RfnIdentifier rfnId = rfnDeviceDao.getDeviceForId(deviceId).getRfnIdentifier();
        Map<RfnIdentifier, Integer> devices = new HashMap<>();
        devices.put(rfnId, 0); // Only one config to map to, at index 0
        request.setDevices(devices);
        
        request.setExpiration(DateTimeConstants.MINUTES_PER_DAY);
        request.setRequestId(correlationId);
        request.setRequestType(DeviceDataStreamingConfigRequestType.SYNC);
        
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
            String errorMessage = "Unable to send config due to a communication error between Yukon and Network Manager";
            throw new DataStreamingConfigException(errorMessage, e, "commsError"); 
        }
        
        return response;
    }
    
    /**
     * Builds a DeviceDataStreamingConfigRequest with all the specified configs and devices.
     */
    private DeviceDataStreamingConfigRequest buildRequest(Multimap<DataStreamingConfig, Integer> configToDeviceIds, 
                                                          DeviceDataStreamingConfigRequestType requestType,
                                                          String correlationId) {
        
        log.debug("Building " + requestType + " data streaming config request");
        
        DeviceDataStreamingConfigRequest request = new DeviceDataStreamingConfigRequest();
        
        //These will hold the configs, and the mapping of devices to configs
        DeviceDataStreamingConfig[] configs = new DeviceDataStreamingConfig[configToDeviceIds.keySet().size()];
        Map<RfnIdentifier, Integer> deviceToConfigIdMap = new HashMap<>();
        
        int i = 0;
        for (DataStreamingConfig config: configToDeviceIds.keySet()) {
            //Convert the config into the NM config request object and add to the config array
            configs[i] = buildDeviceDataStreamingConfig(config);
            
            //Get the devices using this config
            Collection<Integer> deviceIds = configToDeviceIds.get(config);
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
        if (requestType == DeviceDataStreamingConfigRequestType.CONFIG) {
            request.setExpiration(DateTimeConstants.MINUTES_PER_DAY);
            request.setRequestId(correlationId);
        }
        
        return request;
    }
    
    /**
     * Build a DeviceDataStreamingConfig (the NM messaging object) from a DataStreamingConfig (the Yukon model object).
     */
    private DeviceDataStreamingConfig buildDeviceDataStreamingConfig(DataStreamingConfig config) {
        DeviceDataStreamingConfig nmConfig = new DeviceDataStreamingConfig();
        
        if (config == null) {
            nmConfig.setDataStreamingOn(false);
        } else {
            nmConfig.setDataStreamingOn(true);
            
            Map<Integer, MetricConfig> metrics = config.getAttributes().stream()
                .collect(Collectors.toMap(
                    dsAttribute -> rfnDeviceAttributeDao.getMetricIdForAttribute(dsAttribute.getAttribute()),
                    dsAttribute -> {
                        MetricConfig metricConfig = new MetricConfig();
                        metricConfig.setEnabled(dsAttribute.getAttributeOn());
                        metricConfig.setInterval((short)dsAttribute.getInterval());
                        return metricConfig;
                }));
            nmConfig.setMetrics(metrics);
        }

        return nmConfig;
    }

}
