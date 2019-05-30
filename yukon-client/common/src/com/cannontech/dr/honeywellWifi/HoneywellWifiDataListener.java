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
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.dr.honeywellWifi.azure.event.HoneywellWifiData;
import com.cannontech.dr.honeywellWifi.azure.event.HoneywellWifiMessageWrapper;
import com.cannontech.dr.honeywellWifi.azure.event.UnknownEvent;
import com.cannontech.dr.honeywellWifi.azure.event.processing.HoneywellWifiDataProcessor;
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
    private static boolean deleteMessageOnProcessingFailure = false;
    
    // Static configuration data
    private Map<HoneywellWifiDataType, HoneywellWifiDataProcessor> dataTypeToProcessingStrategy = new HashMap<>();
    private ReceiveMessageOptions receiveMessageOptions;
    private ServiceBusContract azureService;
    
    // Autowired dependencies
    @Autowired private List<HoneywellWifiDataProcessor> dataProcessingStrategies;
    @Autowired private GlobalSettingDao settingDao;
    
    // Thread control/monitoring
    private volatile boolean isStopping = false;
    private volatile boolean isStopped = false;
    
    // For monitoring how up-to-date message processing is
    private volatile DateTime lastEmptyQueueTime;
    private volatile DateTime lastProcessedMessageTime;
    
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
                        try {
                            processMessage(data);
                            lastProcessedMessageTime = data.getMessageWrapper().getDate().toDateTime();
                            removeMessageFromQueue(data);
                        } catch (Exception e) {
                            log.error("Error processing Honeywell wifi message of type: " + data.getType(), e);
                            log.debug(data);
                            if (deleteMessageOnProcessingFailure) {
                                log.info("Deleting bad message.");
                                removeMessageFromQueue(data);
                            } else {
                                log.info("Unlocking bad message.");
                                unlockMessage(data);
                            }
                        }
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
    
    public DateTime getLastEmptyQueueTime() {
        return lastEmptyQueueTime;
    }
    
    public DateTime getLastProcessedMessageTime() {
        return lastProcessedMessageTime;
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
        Configuration config = ServiceBusConfiguration.configureWithConnectionString(null, new Configuration(), connectionString);

        YukonHttpProxy.fromGlobalSetting(settingDao)              
                      .ifPresent(proxy -> {
                          log.debug("Set Azure service bus proxy: " + proxy.getHost() + ":" + proxy.getPortString());
                          config.setProperty(Configuration.PROPERTY_HTTP_PROXY_HOST, proxy.getHost());
                          config.setProperty(Configuration.PROPERTY_HTTP_PROXY_PORT, proxy.getPort());
                      });
        
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
            lastEmptyQueueTime = DateTime.now();
            return Optional.empty();
        }
        
        log.debug("Handling message, id=" + message.getMessageId());
        HoneywellWifiData data = buildHoneywellWifiData(message);
        return Optional.ofNullable(data);
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
        
        return getData(messageBody, message);
    }
    
    private HoneywellWifiData getData(String messageBody, BrokeredMessage message) {
        ObjectMapper jsonParser = new ObjectMapper();
        HoneywellWifiMessageWrapper messageWrapper = null;
        try {
            messageWrapper = jsonParser.readValue(messageBody, HoneywellWifiMessageWrapper.class);
        } catch (IOException e) {
            log.error("Unable to parse Honeywell Wifi Azure message wrapper.", e);
            log.debug("Message body: " + messageBody);
            UnknownEvent unknown = new UnknownEvent();
            unknown.setOriginalMessage(message);
            return unknown;
        }
        
        return getMessageData(messageWrapper, message);
    }
    
    /**
     * Extracts the json payload string from the message wrapper and converts it into a POJO for processing.
     */
    private HoneywellWifiData getMessageData(HoneywellWifiMessageWrapper messageWrapper, BrokeredMessage originalMessage) {
        ObjectMapper jsonParser = new ObjectMapper();
        String jsonPayload = StringEscapeUtils.unescapeJava(messageWrapper.getJson());
        try {
            HoneywellWifiData data = jsonParser.readValue(jsonPayload, messageWrapper.getType().getMessageClass());
            if (messageWrapper.getType().name() == "UNKNOWN") {
                log.info("Received new event of unknown data type, enable debugging for more information.");
                
                if (log.isDebugEnabled() && originalMessage.getBody() != null) {
                    String unknownEventMessage;
                    Scanner scanner = new Scanner(originalMessage.getBody());
                    //Scans the entire input in one token. See http://stackoverflow.com/a/5445161/299996
                    Scanner entireInputScanner = scanner.useDelimiter("\\A");
                    unknownEventMessage = entireInputScanner.next();
                    scanner.close();
                    log.debug("Unknown Event Message Body " + unknownEventMessage);
                }
            }
            data.setOriginalMessage(originalMessage);
            data.setMessageWrapper(messageWrapper);
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
        HoneywellWifiDataProcessor strategy = dataTypeToProcessingStrategy.get(data.getType());
        if (strategy != null) {
            strategy.updateAssetAvailability(data);
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
    
    /**
     * "Unlocks" the specified message on the Azure service bus queue, allowing it to be processed again. 
     */
    private void unlockMessage(HoneywellWifiData data) {
        BrokeredMessage originalMessage = data.getOriginalMessage();
        log.debug("Unlocking message, id=" + originalMessage.getMessageId());
        try {
            azureService.unlockMessage(originalMessage);
        } catch (ServiceException e) {
            log.error("Error unlocking message on queue.", e);
        }
    }
}
