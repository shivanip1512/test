package com.cannontech.dr.honeywellWifi;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMessageOptions;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMode;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveQueueMessageResult;

/**
 * Pulls messages from the Honeywell Azure service bus queue and processes them.
 */
public class HoneywellWifiDataListener {
    private static final Logger log = YukonLogManager.getLogger(HoneywellWifiDataListener.class);
    
    // Global settings
    private static String queueName;
    private static String connectionString;
    
    // Static configuration data
    private Map<HoneywellWifiDataType, HoneywellWifiDataProcessingStrategy> dataTypeToProcessingStrategy = new HashMap<>();
    private ReceiveMessageOptions receiveMessageOptions;
    private ServiceBusContract azureService;
    
    // Autowired dependencies
    @Autowired private List<HoneywellWifiDataProcessingStrategy> dataProcessingStrategies;
    @Autowired private GlobalSettingDao settingDao;
    
    // Thread control/monitoring
    private volatile boolean isStopping = false;
    private volatile boolean isStopped = false;
    
    @PostConstruct
    public void init() {
        
        queueName = settingDao.getString(GlobalSettingType.HONEYWELL_WIFI_SERVICE_BUS_QUEUE);
        connectionString = settingDao.getString(GlobalSettingType.HONEYWELL_WIFI_SERVICE_BUS_CONNECTION_STRING);
        
        //Only start the thread if Honeywell Azure service bus configuration parameters are present.
        if (StringUtils.isBlank(queueName) || StringUtils.isBlank(connectionString)) {
            log.info("Honeywell queue name or connection string is blank. Honeywell wifi data listener disabled.");
            return;
        }
        
        initProcessingStrategyMap();
        initAzureService();
        initReceiveMessageOptions();
        
        // Create a thread that checks the queue for messages and sends them to processors
        Runnable thread = () -> {
            while (!isStopping) {
                try {
                    Optional<HoneywellWifiData> message = receiveMessage();
                    message.ifPresent(data -> {
                        processMessage(data);
                        removeMessageFromQueue(data);
                    });
                } catch (Exception e) {
                    log.error("Unhandled exception in Honeywell wifi data listener.", e);
                }
            }
            isStopped = true;
        };
        
        log.info("Starting Honeywell wifi data listener.");
        new Thread(thread).start();
    }
    
    @PreDestroy
    public void cleanup() throws InterruptedException {
        isStopping = true;
        log.info("Honeywell wifi data listener is stopping.");
        while (!isStopped) {
            Thread.sleep(0);
        }
        log.info("Honeywell wifi data listener has stopped.");
    }
    
    private void initProcessingStrategyMap() {
        dataProcessingStrategies.forEach(strategy -> dataTypeToProcessingStrategy.put(strategy.getSupportedType(), strategy));
    }
    
    private void initAzureService() {
        Configuration config = new Configuration();
        config = ServiceBusConfiguration.configureWithConnectionString(null, config, connectionString);
        //TODO: These settings are only available in 1.0+. Is there another way to set these?
//        config.setProperty(Configuration.PROPERTY_HTTP_PROXY_HOST, "proxy.etn.com");
//        config.setProperty(Configuration.PROPERTY_HTTP_PROXY_PORT, 8080);
//        config.setProperty("http.proxyHost", "proxy.etn.com");
//        config.setProperty("http.proxyPort", 8080);
        azureService = ServiceBusService.create(config);
    }
    
    private void initReceiveMessageOptions() {
        receiveMessageOptions = ReceiveMessageOptions.DEFAULT;
        // PEEK LOCK keeps the message on the queue until explicitly deleted
        receiveMessageOptions.setReceiveMode(ReceiveMode.PEEK_LOCK);
    }
    
    private Optional<HoneywellWifiData> receiveMessage(){
        
        BrokeredMessage message;
        try {
            ReceiveQueueMessageResult resultQM = azureService.receiveQueueMessage(queueName, receiveMessageOptions);
            message = resultQM.getValue();
        } catch (ServiceException e) {
            log.error("Error retrieving messages.", e);
            return Optional.empty();
        }
        
        // No more messages on the queue
        if (message == null || message.getMessageId() == null) {
            log.debug("No messages available. (Message or messageId null)");
            return Optional.empty();
        }
        
        log.debug("Handling message, id=" + message.getMessageId());
        HoneywellWifiData data = buildHoneywellWifiData(message);
        return Optional.of(data);
    }
    
    /**
     * Builds a HoneywellWifiData message from an Azure BrokeredMessage.
     */
    private HoneywellWifiData buildHoneywellWifiData(BrokeredMessage message) {
        String messageBody;
        try (Scanner scanner = new Scanner(message.getBody());
            //Scans the entire input in one token. See http://stackoverflow.com/a/5445161/299996
            Scanner entireInputScanner = scanner.useDelimiter("\\A")) {
            
            messageBody = entireInputScanner.next();
            log.trace("Message body: " + messageBody);
        }
        
        if (log.isTraceEnabled()) {
            for(Map.Entry<String, Object> entry : message.getProperties().entrySet()) {
                log.trace("Message property: " + entry.getKey() + ", value: " + entry.getValue());
            }
        }
        
        ObjectMapper jsonParser = new ObjectMapper();
        HoneywellWifiMessageWrapper messageWrapper = null;
        try {
            messageWrapper = jsonParser.readValue(messageBody, HoneywellWifiMessageWrapper.class);
        } catch (IOException e) {
            log.error("Unable to parse Honeywell Wifi Azure message wrapper.", e);
            return null;
        }
        
        HoneywellWifiData data = getMessageData(messageWrapper, message);
        return data;
    }
    
    /**
     * Extracts the json payload string from the message wrapper and converts it into a POJO for processing.
     */
    private HoneywellWifiData getMessageData(HoneywellWifiMessageWrapper messageWrapper, BrokeredMessage originalMessage) {
        ObjectMapper jsonParser = new ObjectMapper();
        String jsonPayload = StringEscapeUtils.unescapeJava(messageWrapper.getJson());
        try {
            HoneywellWifiData data = jsonParser.readValue(jsonPayload, messageWrapper.getType().getMessageClass());
            data.setOriginalMessage(originalMessage);
            return data;
        } catch (IOException e) {
            log.error("Unable to parse json payload in message data", e);
            log.debug("Payload: " + jsonPayload);
            return null;
        }
    }
    
    /**
     * Selects a processor that can handle this message and processes it.
     * @throws IllegalStateException if no processor is found that can handle the message.
     */
    private void processMessage(HoneywellWifiData data) {
        HoneywellWifiDataProcessingStrategy strategy = dataTypeToProcessingStrategy.get(data.getType());
        if (strategy != null) {
            strategy.processData(data);
        } else {
            throw new IllegalStateException("No strategy found for message with type " + data.getType());
        }
    }
    
    /**
     * Removes the specified message from the Azure service bus queue. This should only be done after the message has
     * been fully processed, to ensure that no messages can be lost due to a crash in the middle of processing a message.
     */
    private void removeMessageFromQueue(HoneywellWifiData data) {
        BrokeredMessage originalMessage = data.getOriginalMessage();
        log.debug("Deleting message, id=" + originalMessage.getMessageId());
        try {
            azureService.deleteMessage(originalMessage);
        } catch (ServiceException e) {
            log.error("Error removing message from queue.", e);
        }
    }
}
