package com.cannontech.azure.service.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.azure.model.AzureServices;
import com.cannontech.azure.model.IOTDataType;
import com.cannontech.azure.service.AzureCloudService;
import com.cannontech.data.provider.DataProvider;
import com.cannontech.message.model.ConfigurationSettings;
import com.cannontech.message.model.SystemData;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;
import com.microsoft.azure.sdk.iot.device.Message;
import com.microsoft.azure.sdk.iot.device.ProxySettings;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.Device;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.Property;

/**
 * This service connects to IOT hub and push data on it.
 * It will be pushing telemetry and property data to show on IOT dashboard.
 */
@Service
public class IOTHubService extends AzureCloudService {
    private static Logger log = (Logger) LogManager.getLogger(IOTHubService.class);
    private DeviceClient client;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT_WS;
    // TODO This will be removed after YUK-21163
    private static String CONNECTION_STRING = "HostName=iotc-43ed731f-bc8f-4e3c-aaa2-759ba862fa54.azure-devices.net;DeviceId=5ccfaf0c-c5ff-41e3-bf40-dd09f09e9500;SharedAccessKey=0HCpK4QOV58sUZnHdoV7KWwcHYVK7b/IXJv2BF/B6kU=";

    @Autowired DataProvider dataProviderService;

    @Override
    public AzureServices getName() {
        return AzureServices.IOT_HUB_SERVICE;
    }

    @Override
    public ConfigurationSettings getConfigurationSetting() {
        // TODO This will be replaced with actual ConfigurationSettings call from dataProviderService (YUK-21163).
        ConfigurationSettings config = dataProviderService.getConfigurationSetting(getName());
        return getConfigurationSettings();
    }

    @Override
    public void createConnection() throws IllegalArgumentException, URISyntaxException, IOException {
        ConfigurationSettings config = getConfigurationSetting();
        // Parse hostName and port from ProxySetting.
        String hostName ;
        Integer port ;
        String[] hostPortArray = config.getProxySetting().split(":");
        if(hostPortArray.length != 2) {
            throw new IllegalArgumentException("Invalid proxy value: " + hostPortArray + ".Unable to setup proxy.");
        } else {
            hostName = hostPortArray[0];
            try {
                port = Integer.parseInt(hostPortArray[1]);
            } catch(NumberFormatException e) {
                throw new IllegalArgumentException("Invalid proxy value: " + hostPortArray + ".Unable to setup proxy.");
            }
        }

        if (client == null) {
            // Create connection to IOT Hub
            client = new DeviceClient(config.getConnectionString(), protocol);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostName, port));
            ProxySettings proxySettings = new ProxySettings(proxy);
            client.setProxySettings(proxySettings);
            client.open();
        }
    }

    @Override
    public boolean shouldStart() {
        return true;
    }

    @Override
    public void start() {
        log.info("Starting IOT Hub service");
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                createConnection();
                prepareAndPushData();
            } catch (IllegalArgumentException | UnsupportedOperationException | IOException | URISyntaxException e) {
                log.info("Unable to start IOT Hub service. " + e);
            }
        }, 0, getConfigurationSetting().getFrequency(), TimeUnit.HOURS);
    }

    private void prepareAndPushData() throws IllegalArgumentException, UnsupportedOperationException, IOException {
        List<String> telemetryData = new ArrayList<>();
        Set<Property> propertyData = new HashSet<>();
        
        // Prepare telemetry and property data.
        prepareData(telemetryData , propertyData);

        // Push telemetry and property data to IOT hub.
        pushTelemetryData(telemetryData);
        pushPropertyData(propertyData);
    }

    private void prepareData(List<String> telemetryData, Set<Property> propertyData) {
        Map<String, SystemData> data = dataProviderService.getSystemInformation();
        for (Map.Entry<String, SystemData> entry : data.entrySet()) {
            IOTDataType dataType = entry.getValue().getIotDataType();
            String fieldName = entry.getValue().getFieldName();
            String fieldValue = entry.getValue().getFieldValue();
            if (dataType == IOTDataType.TELEMETRY || dataType == IOTDataType.BOTH) {
                telemetryData.add("{\"" + fieldName + "\":" + fieldValue + "}");
            }
            if (dataType == IOTDataType.PROPERTY || dataType == IOTDataType.BOTH) {
                propertyData.add(new Property(fieldName, fieldValue));
            }
        }
    }

    private void pushTelemetryData(List<String> telemetryData) {
        Message message = new Message(telemetryData.toString());
        client.sendEventAsync(message, new EventCallback(), new Object());
    }

    private void pushPropertyData(Set<Property> propertyData)
            throws IllegalArgumentException, UnsupportedOperationException, IOException {
        Device dataCollector = new Device() {
            @Override
            public void PropertyCall(String propertyKey, Object propertyValue, Object context) {
                log.info(propertyKey + " changed to " + propertyValue);
            }
        };
        client.startDeviceTwin(new DeviceTwinStatusCallBack(), null, dataCollector, null);

        for (Property p : propertyData) {
            dataCollector.setReportedProp(new Property(p.getKey(), p.getValue()));
        }
        client.sendReportedProperties(dataCollector.getReportedProp());
    }

    private class EventCallback implements IotHubEventCallback {
        public void execute(IotHubStatusCode status, Object context) {
            log.info("IoT Hub responded to message with status: " + status.name());
        }
    }
    
    private class DeviceTwinStatusCallBack implements IotHubEventCallback {
        @Override
        public void execute(IotHubStatusCode status, Object context) {
            log.info("IoT Hub responded to device twin operation with status " + status.name());
        }
    }

    // TODO Remove this after actual ConfigurationSettings implementation (YUK-21163).
    private ConfigurationSettings getConfigurationSettings() {
        ConfigurationSettings configurationSettings = new ConfigurationSettings();
        configurationSettings.setConnectionString(CONNECTION_STRING);
        configurationSettings.setProxySetting("proxy.etn.com:8080");
        configurationSettings.setFrequency(5);
        return configurationSettings;
    }
}
