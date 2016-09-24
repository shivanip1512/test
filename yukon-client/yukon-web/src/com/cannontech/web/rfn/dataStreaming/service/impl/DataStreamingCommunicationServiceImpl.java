package com.cannontech.web.rfn.dataStreaming.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.amr.rfn.dao.RfnDeviceAttributeDao;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.point.PointQuality;
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
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.web.rfn.dataStreaming.DataStreamingConfigException;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.web.rfn.dataStreaming.service.DataStreamingCommunicationService;
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
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private AttributeService attributeService;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    @Autowired private AsyncDynamicDataSource dataSource;
    @Autowired private DbChangeManager dbChangeManager;
    private RequestReplyTemplate<DeviceDataStreamingConfigResponse> configRequestTemplate;
    private RequestReplyTemplate<GatewayDataStreamingInfoResponse> gatewayInfoRequestTemplate;
    
    @PostConstruct
    public void init() {
        configRequestTemplate = new RequestReplyTemplateImpl<>(configRequestCparm, configSource, connectionFactory, 
                requestQueue, false);
        gatewayInfoRequestTemplate = new RequestReplyTemplateImpl<>(gatewayInfoRequestCparm, configSource,
                connectionFactory, requestQueue, false);
        
        collectGatewayStatistics();
    }
   
    @Override
    public DeviceDataStreamingConfigRequest buildVerificationRequest(Multimap<DataStreamingConfig, Integer> configToDeviceIds) {
        
        DeviceDataStreamingConfigRequest request = buildRequest(configToDeviceIds, 
                                                                DeviceDataStreamingConfigRequestType.ASSESS,
                                                                null);
        return request;
    }
    
    @Override
    public DeviceDataStreamingConfigRequest buildConfigRequest(Multimap<DataStreamingConfig, Integer> configToDeviceIds,
                                                               String correlationId) {
        
        DeviceDataStreamingConfigRequest request = buildRequest(configToDeviceIds, 
                                                                DeviceDataStreamingConfigRequestType.UPDATE,
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
        
        request.setRequestExpiration(DateTimeConstants.MINUTES_PER_DAY);
        request.setRequestId(correlationId);
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
    public Collection<GatewayDataStreamingInfo> getGatewayInfo(Collection<RfnGateway> gateways) throws DataStreamingConfigException {
        
        Set<RfnIdentifier> gatewayIds = gateways.stream()
                                                .map(gateway -> gateway.getRfnIdentifier())
                                                .collect(Collectors.toSet());
        
        GatewayDataStreamingInfoRequest request = new GatewayDataStreamingInfoRequest();
        request.setGatewayRfnIdentifiers(gatewayIds);
        
        log.debug("Sending gateway data streaming info request to Network Manager: " + request);
        
        //Send the request
        BlockingJmsReplyHandler<GatewayDataStreamingInfoResponse> replyHandler = 
                new BlockingJmsReplyHandler<>(GatewayDataStreamingInfoResponse.class);
        gatewayInfoRequestTemplate.send(request, replyHandler);
        
        //Wait for the response
        GatewayDataStreamingInfoResponse response;
        try {
            response = replyHandler.waitForCompletion();
            log.debug("Gateway data streaming info response: " + response);
        } catch (ExecutionException e) {
            String errorMessage = "Unable to send request due to a communication error between Yukon and Network Manager";
            throw new DataStreamingConfigException(errorMessage, e, "commsError");
        }
        
        return response.getGatewayDataStreamingInfos().values();
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
        if (requestType == DeviceDataStreamingConfigRequestType.UPDATE) {
            request.setRequestExpiration(DateTimeConstants.MINUTES_PER_DAY);
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
    
    /**
     * If data streaming is enabled, schedules gateway statistics collection to run ever hour.
     */
    private void collectGatewayStatistics() {
        boolean isDataStreamingEnabled =
            configurationSource.getBoolean(MasterConfigBoolean.RF_DATA_STREAMING_ENABLED, false);
        if (isDataStreamingEnabled) {
            Runnable gatewayStatistics = new Runnable() {
                @Override
                public void run() {
                    log.debug("Starting gateway statistics collecton.");
                    List<PointData> datas = new ArrayList<>();
                    try {
                        Collection<GatewayDataStreamingInfo> infos = getGatewayInfo(rfnGatewayService.getAllGateways());
                        for (GatewayDataStreamingInfo info : infos) {
                            RfnDevice gateway = rfnDeviceDao.getDeviceForExactIdentifier(info.getGatewayRfnIdentifier());

                            LitePoint loadPoint = findPoint(gateway, BuiltInAttribute.DATA_STREAMING_LOAD);
                            datas.add(generatePointData(info.getDataStreamingLoadingPercent(), loadPoint));

                            LitePoint streamingDeviceCountPoint = findPoint(gateway, BuiltInAttribute.STREAMING_DEVICE_COUNT);
                            datas.add(generatePointData(info.getDeviceRfnIdentifiers().size(), streamingDeviceCountPoint));

                            LitePoint connectedDeviceCountPoint =findPoint(gateway, BuiltInAttribute.CONNECTED_DEVICE_COUNT);
                            datas.add(generatePointData(info.getPrimaryGatewayDeviceCount(), connectedDeviceCountPoint));
                        }
                        if (!datas.isEmpty()) {
                            dataSource.putValues(datas);
                        }
                    } catch (Exception e) {
                        log.error("Error accured during gateway statistics collection", e);
                    }
                }
            };
            log.info("Scheduling gateway statistics collecton to run every hour.");
            scheduledExecutor.scheduleAtFixedRate(gatewayStatistics, 0, 1, TimeUnit.HOURS);
        }
    }
    
    /**
     * Attempts to lookup a point if the point doesn't exists creates a point.
     */
    private LitePoint findPoint(RfnDevice gateway, BuiltInAttribute attribute){
        LitePoint point = null;
        try{
            point = attributeService.getPointForAttribute(gateway, attribute);
        }catch(IllegalUseOfAttribute e){
            log.info("Created point for "+attribute+" device:"+gateway.getRfnIdentifier());
            attributeService.createPointForAttribute(gateway, attribute);
            point = attributeService.getPointForAttribute(gateway, attribute);
            dbChangeManager.processPaoDbChange(gateway, DbChangeType.UPDATE);
        }
        return point;
    }
    
    /**
     * Creates point data to be send to dispatch.
     */
    private PointData generatePointData(double value, LitePoint point) {
        
        PointData pointData = new PointData();
        pointData = new PointData();
        pointData.setId(point.getLiteID());
        pointData.setPointQuality(PointQuality.Normal);
        pointData.setValue(value);
        pointData.setTime(new Date());
        pointData.setType(point.getPointType());
        pointData.setTagsPointMustArchive(true);

        return pointData;
    }
}
