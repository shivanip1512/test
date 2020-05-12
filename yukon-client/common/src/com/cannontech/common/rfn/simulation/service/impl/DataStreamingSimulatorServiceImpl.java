package com.cannontech.common.rfn.simulation.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.dataStreaming.DataStreamingAttributeHelper;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigError;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigRequest;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigRequestType;
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
import com.cannontech.common.rfn.simulation.SimulatedGatewayDataSettings;
import com.cannontech.common.rfn.simulation.service.DataStreamingSimulatorService;
import com.cannontech.common.rfn.simulation.service.RfnGatewaySimulatorService;
import com.cannontech.common.stream.StreamUtils;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApi;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.common.util.jms.api.JmsApiDirectoryHelper;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.google.common.collect.Lists;

public class DataStreamingSimulatorServiceImpl implements DataStreamingSimulatorService {
    private static final Logger log = YukonLogManager.getLogger(DataStreamingSimulatorServiceImpl.class);
    @Autowired private DataStreamingAttributeHelper attributeHelper;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnGatewayService gatewayService;
    @Autowired private RfnGatewaySimulatorService gatewaySimulatorService;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    
    private YukonJmsTemplate jmsTemplate;
    private volatile boolean isRunning;
    private volatile boolean isStopping;
    private volatile SimulatedDataStreamingSettings settings;
    public static final Duration incomingMessageWait = Duration.standardSeconds(1);

    @PostConstruct
    public void init() {
        JmsApi<?, ?, ?> requestQueue = JmsApiDirectoryHelper.requireMatchingQueueNames(JmsApiDirectory.DATA_STREAMING_CONFIG,
                JmsApiDirectory.GATEWAY_DATA_STREAMING_INFO);
        jmsTemplate = jmsTemplateFactory.createTemplate(requestQueue, incomingMessageWait);
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
        if (settings == null) {
          SimulatedDataStreamingSettings simulatedDataStreamingSettings = new SimulatedDataStreamingSettings();
          //verification
          simulatedDataStreamingSettings.setDeviceErrorOnVerification(DeviceDataStreamingConfigError.valueOf((String) YukonSimulatorSettingsKey.DATA_STREAMING_SIMULATOR_DEV_ERR_VER.getDefaultValue()));
          simulatedDataStreamingSettings.setDeviceErrorOnVerificationEnabled((boolean) YukonSimulatorSettingsKey.DATA_STREAMING_SIMULATOR_DEV_ERR_VER_ENABLED.getDefaultValue());
          simulatedDataStreamingSettings.setOverloadGatewaysOnVerification((boolean) YukonSimulatorSettingsKey.DATA_STREAMING_SIMULATOR_OVERLOAD_VER.getDefaultValue());
          simulatedDataStreamingSettings.setNetworkManagerFailOnVerification((boolean) YukonSimulatorSettingsKey.DATA_STREAMING_SIMULATOR_FAIL_VER.getDefaultValue());
          simulatedDataStreamingSettings.setNumberOfDevicesToErrorOnVerification((int) YukonSimulatorSettingsKey.DATA_STREAMING_SIMULATOR_NUM_DEV_ERR_VER.getDefaultValue());
          //config
          simulatedDataStreamingSettings.setNetworkManagerFailOnConfig((boolean) YukonSimulatorSettingsKey.DATA_STREAMING_SIMULATOR_FAIL_CON.getDefaultValue());
          simulatedDataStreamingSettings.setOverloadGatewaysOnConfig((boolean) YukonSimulatorSettingsKey.DATA_STREAMING_SIMULATOR_OVERLOAD_CON.getDefaultValue());
          simulatedDataStreamingSettings.setDeviceErrorOnConfig(DeviceDataStreamingConfigError.valueOf((String) YukonSimulatorSettingsKey.DATA_STREAMING_SIMULATOR_DEV_ERR_CON.getDefaultValue()));
          simulatedDataStreamingSettings.setDeviceErrorOnConfigEnabled((boolean) YukonSimulatorSettingsKey.DATA_STREAMING_SIMULATOR_DEV_ERR_CON_ENABLED.getDefaultValue());
          simulatedDataStreamingSettings.setNumberOfDevicesToErrorOnConfig((int) YukonSimulatorSettingsKey.DATA_STREAMING_SIMULATOR_NUM_DEV_ERR_CON.getDefaultValue());
          simulatedDataStreamingSettings.setAcceptedWithError((boolean) YukonSimulatorSettingsKey.DATA_STREAMING_SIMULATOR_ACCEPTED_ERR.getDefaultValue());
          settings = simulatedDataStreamingSettings;
        }
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
                        Object message = jmsTemplate.receive();
                        if (message != null && message instanceof ObjectMessage) {
                           // log.info("Processing data streaming request.");
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
            SimulatedGatewayDataSettings simulatedGatewayDataSettings = gatewaySimulatorService.getGatewayDataSettings();
            info.setCurrentLoading(simulatedGatewayDataSettings.getCurrentDataStreamingLoading());
            info.setMaxCapacity(DefaultGatewaySimulatorData.maxDataStreamingLoading);
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
        //log.info("Processing config request");
        
        switch(request.getRequestType()) {
        case ASSESS:
            return getResponse(request.getRequestType(),
                               new ArrayList<>(request.getDevices().keySet()),
                               settings.isOverloadGatewaysOnVerification(),
                               settings.isNetworkManagerFailOnVerification(),
                               settings.getDeviceErrorOnVerification(), 
                               settings.getNumberOfDevicesToErrorOnVerification(),
                               false, request.getRequestSeqNumber());
        case UPDATE:
            return getResponse(request.getRequestType(),
                               new ArrayList<>(request.getDevices().keySet()),
                               settings.isOverloadGatewaysOnConfig(),
                               settings.isNetworkManagerFailOnConfig(),
                               settings.getDeviceErrorOnConfig(), 
                               settings.getNumberOfDevicesToErrorOnConfig(),
                               false, request.getRequestSeqNumber());
        case UPDATE_WITH_FORCE:
            return getResponse(request.getRequestType(),
                               new ArrayList<>(request.getDevices().keySet()),
                               settings.isOverloadGatewaysOnConfig(),
                               settings.isNetworkManagerFailOnConfig(),
                               settings.getDeviceErrorOnConfig(),
                               settings.getNumberOfDevicesToErrorOnConfig(),
                               settings.isAcceptedWithError(),
                               request.getRequestSeqNumber());
        case CONFIRM:
            return getResponse(request.getRequestType(), new ArrayList<>(request.getDevices().keySet()), false, false, 
                               null, 0, false, request.getRequestSeqNumber());
        default:
            throw new IllegalArgumentException("Unsupported request type: " + request.getRequestType());
        }
    }
    
    private DeviceDataStreamingConfigResponse getResponse(DeviceDataStreamingConfigRequestType requestType,
                                                          List<RfnIdentifier> devices,
                                                          boolean isOverloadGateways,
                                                          boolean isNetworkManagerFail,
                                                          DeviceDataStreamingConfigError deviceError,
                                                          int numberOfDevicesToError,
                                                          boolean isAcceptedWithError,
                                                          long requestSeqNumber) {
        
        List<RfnGateway> gateways = new ArrayList<>(gatewayService.getAllGateways());
        
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
            SimulatedGatewayDataSettings simulatedGatewayDataSettings = gatewaySimulatorService.getGatewayDataSettings();
            info.setCurrentLoading(simulatedGatewayDataSettings.getCurrentDataStreamingLoading());
            info.setMaxCapacity(DefaultGatewaySimulatorData.maxDataStreamingLoading);
            if (isOverloadGateways) {
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
        response.setRequestSeqNumber(requestSeqNumber);
        response.setAffectedGateways(affectedGateways);
        response.setResponseMessage("Simulated Response!");

        if (deviceError != null) {
            Map<RfnIdentifier, ConfigError> deviceErrors = 
                    devices.stream()
                           .limit(numberOfDevicesToError == 0 ? Integer.MAX_VALUE : numberOfDevicesToError)
                           .collect(StreamUtils.mapSelfTo(rfnId -> {
                               ConfigError error = new ConfigError();
                               error.setErrorType(deviceError);
                               error.setErrorMessage(deviceError.toString());
                               if (isOverloadGateways) {
                                   error.setOverSubscribedGatewayRfnIdentifier(rfnId);
                               }
                               return error;
                           }));
            response.setErrorConfigedDevices(deviceErrors);
        } 
        if (isAcceptedWithError) {
            response.setResponseType(DeviceDataStreamingConfigResponseType.ACCEPTED_WITH_ERROR);
            List<DeviceDataStreamingConfigError> configErrors =
                Arrays.stream(DeviceDataStreamingConfigError.values())
                      .filter( e -> e != DeviceDataStreamingConfigError.GATEWAY_OVERLOADED)
                      .collect(Collectors.toList());
            Map<RfnIdentifier, ConfigError> errorMap = new HashMap<>();
            for(int i = 0; i < devices.size(); i++){
                if(i < 5){
                   continue;
                }
                Collections.shuffle(configErrors);
                ConfigError error = new ConfigError();
                DeviceDataStreamingConfigError configError = configErrors.stream().findFirst().get();
                error.setErrorType(configError);
                error.setErrorMessage(configError.toString());
                error.setOverSubscribedGatewayRfnIdentifier(devices.get(i)); 
                errorMap.put(devices.get(i), error);
            }
            if (!errorMap.isEmpty()) {
                response.setErrorConfigedDevices(errorMap);
            }
        } else if (isNetworkManagerFail) {
            response.setResponseType(DeviceDataStreamingConfigResponseType.NETWORK_MANAGER_SERVER_FAILURE);
        } else if (isOverloadGateways || deviceError != null) {
            response.setResponseType(DeviceDataStreamingConfigResponseType.CONFIG_ERROR);
        } else {
            response.setResponseType(DeviceDataStreamingConfigResponseType.ACCEPTED);
        }
        
        return response;
    }

    @Override
    public void startSimulatorWithCurrentSettings() {
        settings = getSettings();
        start();
    }
    
}
