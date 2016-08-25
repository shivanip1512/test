package com.cannontech.common.rfn.simulation.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.dataStreaming.DataStreamingAttributeHelper;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigError;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigRequest;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigResponse;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigResponse.ConfigError;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigResponseType;
import com.cannontech.common.rfn.message.datastreaming.gateway.GatewayDataStreamingInfo;
import com.cannontech.common.rfn.message.datastreaming.gateway.GatewayDataStreamingInfoRequest;
import com.cannontech.common.rfn.message.datastreaming.gateway.GatewayDataStreamingInfoResponse;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.rfn.simulation.SimulatedDataStreamingSettings;
import com.cannontech.common.rfn.simulation.service.DataStreamingSimulatorService;
import com.google.common.collect.Lists;

public class DataStreamingSimulatorServiceImpl implements DataStreamingSimulatorService {
    private static final Logger log = YukonLogManager.getLogger(DataStreamingSimulatorServiceImpl.class);
    private static final int incomingMessageWaitMillis = 1000;
    private static final String requestQueue = "com.eaton.eas.yukon.networkmanager.dataStreaming.request";
    
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private DataStreamingAttributeHelper attributeHelper;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnGatewayService gatewayService;
    
    private JmsTemplate jmsTemplate;
    
    private volatile boolean isRunning;
    private volatile boolean isStopping;
    private volatile SimulatedDataStreamingSettings settings;
    
    @PostConstruct
    public void init() {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setReceiveTimeout(incomingMessageWaitMillis);
    }
    
    @Override
    public void setSettings(SimulatedDataStreamingSettings settings) {
        this.settings = settings;
    }
    
    @Override
    public boolean start() {
        
        if (isRunning) {
            return false;
        } else {
            Thread simulatorThread = getSimulatorThread();
            simulatorThread.start();
            return true;
        }
    }
    
    @Override
    public void stop() {
        isStopping = true;
    }
    
    @Override
    public SimulatedDataStreamingSettings getSettings() {
        return settings;
    }
    
    @Override
    public boolean isRunning() {
        return isRunning;
    }
    
    private Thread getSimulatorThread() {
        Thread simulatorThread = new Thread() {
            @Override
            public void run() {
                log.info("Data Streaming simulator thread starting up.");
                isRunning = true;
                while (!isStopping) {
                    try {
                        Object message = jmsTemplate.receive(requestQueue);
                        if (message != null && message instanceof ObjectMessage) {
                            log.info("Processing data streaming request.");
                            ObjectMessage objectMessage = (ObjectMessage) message;
                            Serializable requestMessage = objectMessage.getObject();
                            
                            Object response = null;
                            if (requestMessage instanceof DeviceDataStreamingConfigRequest) {
                                response = processConfigRequest((DeviceDataStreamingConfigRequest)objectMessage.getObject());
                            } else if (requestMessage instanceof GatewayDataStreamingInfoRequest) {
                                response = processGatewayInfoRequest((GatewayDataStreamingInfoRequest)objectMessage.getObject());
                            }
                            jmsTemplate.convertAndSend(objectMessage.getJMSReplyTo(), response);
                        }
                    } catch (Exception e) {
                        log.error("Error occurred in data streaming simulator.", e);
                    }
                }
                
                log.info("Data Streaming simulator thread shutting down.");
                isStopping = false;
                isRunning = false;
            }
        };
        return simulatorThread;
    }
    
    private GatewayDataStreamingInfoResponse processGatewayInfoRequest(GatewayDataStreamingInfoRequest request) {
        log.info("Processing gateway info request");
        
        Set<PaoType> dataStreamingTypes = attributeHelper.getAllSupportedPaoTypes();
        List<RfnDevice> dataStreamingDevices = rfnDeviceDao.getDevicesByPaoTypes(dataStreamingTypes);
        int numberOfDevices = dataStreamingDevices.size();
        int numberOfGateways = request.getGatewayRfnIdentifiers().size();
        int numberOfDevicesPerGateway = numberOfDevices / numberOfGateways;
        
        final List<List<RfnDevice>> deviceSubLists = new ArrayList<>();
        if (numberOfDevices > numberOfGateways) {
            deviceSubLists.addAll(Lists.partition(dataStreamingDevices, numberOfDevicesPerGateway));
        } else {
            dataStreamingDevices.forEach(device -> deviceSubLists.add(Lists.newArrayList(device)));
        }
        
        List<RfnIdentifier> gatewayRfnIds = Lists.newArrayList(request.getGatewayRfnIdentifiers());
        Map<RfnIdentifier, GatewayDataStreamingInfo> gatewayDataStreamingInfos = new HashMap<>();
        for (int i = 0; i < numberOfGateways; i++) {
            RfnIdentifier gatewayRfnId = gatewayRfnIds.get(i);
            GatewayDataStreamingInfo info = new GatewayDataStreamingInfo();
            info.setGatewayRfnIdentifier(gatewayRfnId);
            info.setCurrentLoading(1.0);
            info.setMaxCapacity(10.0);
            //info.setResultLoading(0.0);
            
            Map<RfnIdentifier, Double> deviceRfnIdentifiers = new HashMap<>();
            if (deviceSubLists.size() > i) {
                for (RfnDevice device : deviceSubLists.get(i)) {
                    deviceRfnIdentifiers.put(device.getRfnIdentifier(), 1.0);
                }
            }
            info.setDeviceRfnIdentifiers(deviceRfnIdentifiers);
            
            gatewayDataStreamingInfos.put(gatewayRfnId, info);
        }
        
        GatewayDataStreamingInfoResponse response = new GatewayDataStreamingInfoResponse();
        response.setGatewayDataStreamingInfos(gatewayDataStreamingInfos);
        return response;
    }
    
    private DeviceDataStreamingConfigResponse processConfigRequest(DeviceDataStreamingConfigRequest request) {
        log.info("Processing config request");
        
        switch(request.getRequestType()) {
        case ASSESS:
            return getVerificationResponse(request);
        case UPDATE:
            return getConfigResponse(request);
        case CONFIRM:
            return getSyncResponse(request);
        default:
            throw new IllegalArgumentException("Unsupported request type: " + request.getRequestType());
        }
    }
    
    private DeviceDataStreamingConfigResponse getVerificationResponse(DeviceDataStreamingConfigRequest request) {
        List<RfnGateway> gateways = new ArrayList<>(gatewayService.getAllGateways());
        List<RfnIdentifier> devices = new ArrayList<>(request.getDevices().keySet());
        
        int numberOfDevices = devices.size();
        int numberOfDevicesPerGateway = numberOfDevices / gateways.size();
        final List<List<RfnIdentifier>> deviceSubLists = new ArrayList<>();
        if (numberOfDevices > gateways.size()) {
            deviceSubLists.addAll(Lists.partition(devices, numberOfDevicesPerGateway));
        } else {
            devices.forEach(device -> deviceSubLists.add(Lists.newArrayList(device)));
        }
        
        Map<RfnIdentifier, GatewayDataStreamingInfo> affectedGateways = new HashMap<>();
        for (int i = 0;  i < gateways.size() && i < deviceSubLists.size(); i++) {
            RfnGateway gateway = gateways.get(i);
            
            GatewayDataStreamingInfo info = new GatewayDataStreamingInfo();
            info.setGatewayRfnIdentifier(gateway.getRfnIdentifier());
            info.setCurrentLoading(1.0);
            info.setMaxCapacity(10.0);
            if (settings.isOverloadGatewaysOnVerification()) {
                info.setResultLoading(11.5);
            } else {
                info.setResultLoading(2.0);
            }
            
            Map<RfnIdentifier, Double> deviceRfnIdentifiers = deviceSubLists.get(i).stream()
                    .collect(Collectors.toMap(d -> d, d -> 1.0));
            info.setDeviceRfnIdentifiers(deviceRfnIdentifiers);
            
            affectedGateways.put(gateway.getRfnIdentifier(), info);
        }
        
        
        DeviceDataStreamingConfigResponse response = new DeviceDataStreamingConfigResponse();
        response.setAffectedGateways(affectedGateways);
        response.setResponseMessage("Simulated Response!");
        
        DeviceDataStreamingConfigError deviceError = settings.getDeviceErrorOnVerification();
        if (deviceError != null) {
            Map<RfnIdentifier, ConfigError> errorMap = new HashMap<>();
            ConfigError error = new ConfigError();
            error.setErrorType(deviceError);
            error.setErrorMessage(deviceError.toString());
            error.setOverSubscribedGatewayRfnIdentifier(devices.get(0)); //TODO variable by error type
            errorMap.put(devices.get(0), error);
            response.setErrorConfigedDevices(errorMap);
        } else {
            response.setErrorConfigedDevices(new HashMap<>());
        }
        
        if (settings.isNetworkManagerFailOnVerification()) {
            response.setResponseType(DeviceDataStreamingConfigResponseType.NETWORK_MANAGER_FAILURE);
        } else if (settings.isOverloadGatewaysOnVerification() || settings.getDeviceErrorOnVerification() != null) {
            response.setResponseType(DeviceDataStreamingConfigResponseType.CONFIG_ERROR);
        } else {
            response.setResponseType(DeviceDataStreamingConfigResponseType.ACCEPTED);
        }
        
        return response;
    }
    
    private DeviceDataStreamingConfigResponse getConfigResponse(DeviceDataStreamingConfigRequest request) {
        List<RfnGateway> gateways = new ArrayList<>(gatewayService.getAllGateways());
        List<RfnIdentifier> devices = new ArrayList<>(request.getDevices().keySet());
        
        int numberOfDevices = devices.size();
        int numberOfDevicesPerGateway = numberOfDevices / gateways.size();
        final List<List<RfnIdentifier>> deviceSubLists = new ArrayList<>();
        
        if (numberOfDevices > gateways.size()) {
            deviceSubLists.addAll(Lists.partition(devices, numberOfDevicesPerGateway));
        } else {
            devices.forEach(device -> deviceSubLists.add(Lists.newArrayList(device)));
        }
        
        log.debug("Gateways size: " + gateways.size());
        log.debug("Device sub list size: " + deviceSubLists.size());
        
        Map<RfnIdentifier, GatewayDataStreamingInfo> affectedGateways = new HashMap<>();
        for (int i = 0; i < gateways.size() && i < deviceSubLists.size(); i++) {
            RfnGateway gateway = gateways.get(i);
            
            GatewayDataStreamingInfo info = new GatewayDataStreamingInfo();
            info.setGatewayRfnIdentifier(gateway.getRfnIdentifier());
            info.setCurrentLoading(1.0);
            info.setMaxCapacity(10.0);
            if (settings.isOverloadGatewaysOnConfig()) {
                info.setResultLoading(11.5);
            } else {
                info.setResultLoading(2.0);
            }
            
            Map<RfnIdentifier, Double> deviceRfnIdentifiers = deviceSubLists.get(i).stream()
                    .collect(Collectors.toMap(d -> d, d -> 1.0));
            info.setDeviceRfnIdentifiers(deviceRfnIdentifiers);
            
            affectedGateways.put(gateway.getRfnIdentifier(), info);
        }
        
        DeviceDataStreamingConfigResponse response = new DeviceDataStreamingConfigResponse();
        response.setAffectedGateways(affectedGateways);
        response.setResponseMessage("Simulated Response!");
        
        DeviceDataStreamingConfigError deviceError = settings.getDeviceErrorOnConfig();
        if (deviceError != null) {
            Map<RfnIdentifier, ConfigError> errorMap = new HashMap<>();
            ConfigError error = new ConfigError();
            error.setErrorType(deviceError);
            error.setErrorMessage(deviceError.toString());
            error.setOverSubscribedGatewayRfnIdentifier(devices.get(0)); //TODO variable by error type
            errorMap.put(devices.get(0), error);
            response.setErrorConfigedDevices(errorMap);
        } else {
            response.setErrorConfigedDevices(new HashMap<>());
        }
        
        if (settings.isNetworkManagerFailOnConfig()) {
            response.setResponseType(DeviceDataStreamingConfigResponseType.NETWORK_MANAGER_FAILURE);
        } else if (settings.isOverloadGatewaysOnConfig() || settings.getDeviceErrorOnConfig() != null) {
            response.setResponseType(DeviceDataStreamingConfigResponseType.CONFIG_ERROR);
        } else {
            response.setResponseType(DeviceDataStreamingConfigResponseType.ACCEPTED);
        }
        
        return response;
    }
    
    private DeviceDataStreamingConfigResponse getSyncResponse(DeviceDataStreamingConfigRequest request) {
        //TODO additional settings/adjustments?
        List<RfnGateway> gateways = new ArrayList<>(gatewayService.getAllGateways());
        List<RfnIdentifier> devices = new ArrayList<>(request.getDevices().keySet());
        
        int numberOfDevices = devices.size();
        int numberOfDevicesPerGateway = numberOfDevices / gateways.size();
        final List<List<RfnIdentifier>> deviceSubLists = new ArrayList<>();
        if (numberOfDevices > gateways.size()) {
            deviceSubLists.addAll(Lists.partition(devices, numberOfDevicesPerGateway));
        } else {
            devices.forEach(device -> deviceSubLists.add(Lists.newArrayList(device)));
        }
        
        Map<RfnIdentifier, GatewayDataStreamingInfo> affectedGateways = new HashMap<>();
        for (int i = 0; i < gateways.size() && i < deviceSubLists.size(); i++) {
            RfnGateway gateway = gateways.get(i);
            
            GatewayDataStreamingInfo info = new GatewayDataStreamingInfo();
            info.setGatewayRfnIdentifier(gateway.getRfnIdentifier());
            info.setCurrentLoading(1.0);
            info.setMaxCapacity(10.0);
            info.setResultLoading(2.0);
            
            Map<RfnIdentifier, Double> deviceRfnIdentifiers = deviceSubLists.get(i).stream()
                    .collect(Collectors.toMap(d -> d, d -> 1.0));
            info.setDeviceRfnIdentifiers(deviceRfnIdentifiers);
            
            affectedGateways.put(gateway.getRfnIdentifier(), info);
        }
        
        
        DeviceDataStreamingConfigResponse response = new DeviceDataStreamingConfigResponse();
        response.setAffectedGateways(affectedGateways);
        response.setResponseMessage("Simulated Response!");
        response.setErrorConfigedDevices(new HashMap<>());
        response.setResponseType(DeviceDataStreamingConfigResponseType.ACCEPTED);
        
        return response;
    }
}
