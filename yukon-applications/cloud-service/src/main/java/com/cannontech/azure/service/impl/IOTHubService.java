package com.cannontech.azure.service.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
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
import com.google.gson.Gson;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;
import com.microsoft.azure.sdk.iot.device.Message;
import com.microsoft.azure.sdk.iot.device.ProxySettings;

/**
 * This service connects to IOT hub and push data on it.
 * It will be pushing telemetry data to show on IOT dashboard.
 */
@Service
public class IOTHubService extends AzureCloudService {
    private static Logger log = (Logger) LogManager.getLogger(IOTHubService.class);
    private DeviceClient client;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT_WS;

    @Autowired DataProvider dataProviderService;

    @Override
    public AzureServices getName() {
        return AzureServices.IOT_HUB_SERVICE;
    }

    @Override
    public ConfigurationSettings getConfigurationSetting() {
        ConfigurationSettings config = dataProviderService.getConfigurationSetting(getName());
        return config;
    }

    @Override
    public boolean createConnection() {
        ConfigurationSettings config = getConfigurationSetting();

        if (client == null) {
            try {
                // Create connection to IOT Hub
                client = new DeviceClient(config.getConnectionString(), protocol);
                String proxySetting = config.getProxySetting();
                if (StringUtils.isNoneEmpty(proxySetting)) {
                    // Parse hostName and port from Proxy configuration and build ProxySetting
                    ProxySettings proxySettings = buildProxySettings(config.getProxySetting());
                    client.setProxySettings(proxySettings);
                } else {
                    log.info("Proxy settings are blank. Connecting without proxy");
                }
                client.open();
            } catch (IllegalArgumentException | URISyntaxException | IOException e) {
                log.error("Unable to create connection with IOT hub. " + e);
                client = null;
                return false;
            }
        }
        log.info("Successfully connected to IOT hub");
        return true;
    }

    @Override
    public boolean shouldStart() {
        return true;
    }

    @Override
    public void start() {
        log.info("Starting IOT Hub service");
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (createConnection()) {
                try {
                    prepareAndPushData();
                } catch (IllegalArgumentException | IOException e) {
                    log.error("Unable to push data to IOT hub. " + e);
                } catch (UnsupportedOperationException ex) {
                    log.warn("Device twin is already started " + ex);
                }
            }
        }, 2, getConfigurationSetting().getFrequency(), TimeUnit.MINUTES);
    }

    private void prepareAndPushData() throws IllegalArgumentException, UnsupportedOperationException, IOException {
        ConcurrentHashMap<String, Object> telemetryData = new ConcurrentHashMap<>();

        // Prepare telemetry data.
        prepareData(telemetryData);

        // Push telemetry data to IOT hub.
        pushTelemetryData(telemetryData);
    }

    private void prepareData(Map<String, Object> telemetryData) {
        Map<String, SystemData> data = dataProviderService.getSystemInformation();
        for (Map.Entry<String, SystemData> entry : data.entrySet()) {
            IOTDataType dataType = entry.getValue().getIotDataType();
            String fieldName = entry.getValue().getFieldName();
            Object fieldValue = entry.getValue().getFieldValue();
            if (dataType == IOTDataType.TELEMETRY) {
                telemetryData.put(fieldName, fieldValue);
            }
        }
    }

    /**
     * Build and push telemetry data in json format.
     */
    private void pushTelemetryData(Map<String, Object> telemetryData) {
        Gson gson = new Gson();
        Message message = new Message(gson.toJson(telemetryData));
        message.setContentEncoding("utf-8");
        message.setContentTypeFinal("application/json");
        client.sendEventAsync(message, new EventCallback(), message);
    }


    private class EventCallback implements IotHubEventCallback {
        public void execute(IotHubStatusCode status, Object context) {
            log.info("IoT Hub responded to message with status: " + status.name());
        }
    }

    private ProxySettings buildProxySettings(String proxyConfigString) {
        String hostName ;
        Integer port ;
        String[] hostPortArray = proxyConfigString.split(":");
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
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostName, port));
        return new ProxySettings(proxy);
    }
}
