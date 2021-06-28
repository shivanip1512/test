package com.cannontech.dr.honeywellWifi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.aspectj.bridge.IMessage;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.azure.core.amqp.AmqpTransportType;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusException;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import com.azure.messaging.servicebus.ServiceBusReceiverAsyncClient;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.dr.honeywellWifi.azure.event.HoneywellWifiData;
import com.cannontech.dr.honeywellWifi.azure.event.HoneywellWifiMessageWrapper;
import com.cannontech.dr.honeywellWifi.azure.event.UnknownEvent;
import com.cannontech.dr.honeywellWifi.azure.event.processing.HoneywellWifiDataProcessor;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteSource;

/**
 * Pulls messages from the Honeywell Azure service bus queue and processes them.
 */
public class HoneywellWifiDataListener {
    private static final Logger log = YukonLogManager.getLogger(HoneywellWifiDataListener.class);
    // Global settings
    private static String queueName;
    private static String connectionString;

    // Static configuration data
    private Map<HoneywellWifiDataType, HoneywellWifiDataProcessor> dataTypeToProcessingStrategy = new HashMap<>();

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Autowired dependencies
    @Autowired private List<HoneywellWifiDataProcessor> dataProcessingStrategies;
    @Autowired private GlobalSettingDao settingDao;

    // For monitoring how up-to-date message processing is
    private volatile DateTime lastEmptyQueueTime;
    private volatile DateTime lastProcessedMessageTime;

    private ServiceBusReceiverAsyncClient receiveClient;

    @PostConstruct
    public void init() throws Exception {
        queueName = settingDao.getString(GlobalSettingType.HONEYWELL_WIFI_SERVICE_BUS_QUEUE);
        connectionString = settingDao.getString(GlobalSettingType.HONEYWELL_WIFI_SERVICE_BUS_CONNECTION_STRING);
        log.info("Starting Honeywell wifi data listener.");
        // Only start the thread if Honeywell Azure service bus configuration parameters are present.
        if (StringUtils.isBlank(queueName) || StringUtils.isBlank(connectionString)) {
            log.info("Honeywell queue name or connection string is blank. Honeywell wifi data listener disabled.");
            return;
        }
        /* When queue is empty it prints info log i.e. "Eaton Dr queue is empty " on every 10 secs 
         which will fills log memory very fast, this scenario will come when devices are not
         connected to Internet and not pumping any data to queue. To prevent this situation 
         logging level of this class is changed to error.*/
        Configurator.setLevel("com.microsoft.azure.servicebus.primitives.CoreMessageReceiver", Level.ERROR);
        initProcessingStrategyMap();
        initQueueClient();
        // Using single thread executor as this thread can only process one message at a time
        if (receiveClient != null) {
            registerReceiver(receiveClient, executorService);
        }
    }

    public DateTime getLastEmptyQueueTime() {
        return lastEmptyQueueTime;
    }

    public DateTime getLastProcessedMessageTime() {
        return lastProcessedMessageTime;
    }

    @PreDestroy
    public void cleanup() throws InterruptedException {
        log.info("Honeywell wifi data listener is stopping.");
        executorService.shutdown();
        log.info("Honeywell wifi data listener has stopped.");
    }

    private void initProcessingStrategyMap() {
        dataProcessingStrategies.forEach(strategy -> dataTypeToProcessingStrategy.put(strategy.getSupportedType(), strategy));
    }

    /**
     * Initializes the Queue Client that will receive messages from Azure Service bus.
     */
    private void initQueueClient() {

        // ProxySelector set up for an HTTP proxy
        final ProxySelector systemDefaultSelector = ProxySelector.getDefault();
        ProxySelector.setDefault(new ProxySelector() {
            @Override
            public List<Proxy> select(URI uri) {

                List<Proxy> proxies = new LinkedList<>();

                YukonHttpProxy.fromGlobalSetting(settingDao).ifPresentOrElse((proxy -> {
                    log.debug("Set Azure service bus proxy: " + proxy.getHost() + ":" + proxy.getPortString());
                    proxies.add(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getHost(), proxy.getPort())));
                }), (() -> proxies.add(Proxy.NO_PROXY)));

                return proxies;
            }

            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                if (uri == null || sa == null || ioe == null) {
                    log.error("Arguments can't be null. " + ioe);
                    throw new IllegalArgumentException("Arguments can't be null." + ioe);
                }
                systemDefaultSelector.connectFailed(uri, sa, ioe);
            }
        });

        try {
            receiveClient = new ServiceBusClientBuilder()
                           .connectionString(connectionString)
                           .transportType(AmqpTransportType.AMQP_WEB_SOCKETS)
                           .receiver()
                           .queueName(queueName)
                           .receiveMode(ServiceBusReceiveMode.PEEK_LOCK)
                           .buildAsyncClient();
            log.info("Connection is established with Azure service bus");
        } catch (ServiceBusException e) {
            log.error("Error making connection to Azure service bus to EndPoint URI " + receiveClient.getFullyQualifiedNamespace()
            + " with Queue Name: " + receiveClient.getEntityPath() + " with exception: " + e);
        }
    }

    void registerReceiver(ServiceBusReceiverAsyncClient queueClient, ExecutorService executorService) throws Exception {
        Consumer<ServiceBusReceivedMessageContext> processMessage = messageContext -> {
            HoneywellWifiData data = buildHoneywellWifiData(messageContext.getMessage());
            try {
                processMessage(data);
                lastProcessedMessageTime = data.getMessageWrapper().getDate().toDateTime();
            } catch (Exception e) {
                log.error("Error processing Honeywell wifi message of type: " + data.getType(), e);
                log.debug(data);
            }
            // other message processing code
        };
        Consumer<ServiceBusErrorContext> processError = errorContext -> {
            log.error("Error occured in handling the wifi message with ExceptionPhase type : " + errorContext.getErrorSource()
                    + "-" + errorContext.getException());
        };
        ServiceBusProcessorClient processorClient = new ServiceBusClientBuilder()
                                                       .connectionString(connectionString)
                                                       .processor()
                                                       .queueName(queueName)
                                                       .processMessage(processMessage)
                                                       .processError(processError)
                                                       .buildProcessorClient();
        processorClient.start();
    }

    /**
     * Extracts binary message from an Azure IMessage.
     */
    private byte[] extractBinaryMessage(ServiceBusReceivedMessage message) {

        // No more messages on the queue
        if (message == null || message.getBody() == null || message.getBody().toBytes() == null) {
            log.debug("No messages available. (Message or messageId null)");
            lastEmptyQueueTime = DateTime.now();
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        log.debug("Handling message, id=" + message.getMessageId());
        return message.getBody().toBytes();
    }

    /**
     * Builds a HoneywellWifiData message from an Azure IMessage.
     */
    private HoneywellWifiData buildHoneywellWifiData(ServiceBusReceivedMessage message) {
        String messageBody = null;
        byte[] binaryData = extractBinaryMessage(message);
        try (Scanner scanner = new Scanner(ByteSource.wrap(binaryData).openStream());
                // Scans the entire input in one token. See http://stackoverflow.com/a/5445161/299996
                Scanner entireInputScanner = scanner.useDelimiter("\\A")) {

            messageBody = entireInputScanner.next();
            log.trace("Message body: " + messageBody);
        } catch (IOException e) {
            log.error("Error occured while processing mesasge" + e);
        }

        if (log.isTraceEnabled()) {
            for (Map.Entry<String, Object> entry : message.getApplicationProperties().entrySet()) {
                log.trace("Message property: " + entry.getKey() + ", value: " + entry.getValue());
            }
        }

        return getData(messageBody, message);
    }

    private HoneywellWifiData getData(String messageBody, ServiceBusReceivedMessage message) {
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
    private HoneywellWifiData getMessageData(HoneywellWifiMessageWrapper messageWrapper, ServiceBusReceivedMessage originalMessage) {
        ObjectMapper jsonParser = new ObjectMapper();
        String jsonPayload = StringEscapeUtils.unescapeJava(messageWrapper.getJson());
        try {
            HoneywellWifiData data = jsonParser.readValue(jsonPayload, messageWrapper.getType().getMessageClass());
            if (messageWrapper.getType().name() == "UNKNOWN") {
                log.info("Received new event of unknown data type, enable debugging for more information.");

                if (log.isDebugEnabled() && originalMessage.getBody() != null) {
                    String unknownEventMessage;
                    byte[] binaryData = extractBinaryMessage(originalMessage);
                    Scanner scanner = new Scanner(ByteSource.wrap(binaryData).openStream());
                    // Scans the entire input in one token. See http://stackoverflow.com/a/5445161/299996
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
}
