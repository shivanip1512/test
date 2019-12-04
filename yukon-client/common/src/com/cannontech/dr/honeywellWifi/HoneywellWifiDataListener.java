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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.ArrayUtils;
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
import com.google.common.io.ByteSource;
import com.microsoft.azure.servicebus.ExceptionPhase;
import com.microsoft.azure.servicebus.IMessage;
import com.microsoft.azure.servicebus.IMessageHandler;
import com.microsoft.azure.servicebus.MessageBody;
import com.microsoft.azure.servicebus.QueueClient;
import com.microsoft.azure.servicebus.ReceiveMode;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import com.microsoft.azure.servicebus.primitives.TransportType;

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

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Autowired dependencies
    @Autowired
    private List<HoneywellWifiDataProcessor> dataProcessingStrategies;
    @Autowired
    private GlobalSettingDao settingDao;

    // For monitoring how up-to-date message processing is
    private volatile DateTime lastEmptyQueueTime;
    private volatile DateTime lastProcessedMessageTime;

    QueueClient receiveClient;

    @PostConstruct
    public void init() throws Exception {
        queueName = settingDao.getString(GlobalSettingType.HONEYWELL_WIFI_SERVICE_BUS_QUEUE);
        connectionString = settingDao.getString(GlobalSettingType.HONEYWELL_WIFI_SERVICE_BUS_CONNECTION_STRING);
        log.info("Starting Honeywell wifi data listener.");
        // Only start the thread if HoneyWell Azure service bus configuration parameters are present.
        if (StringUtils.isBlank(queueName) || StringUtils.isBlank(connectionString)) {
            log.info("Honeywell queue name or connection string is blank. Honeywell wifi data listener disabled.");
            return;
        }
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
                    log.error("Arguments can't be null. "+ ioe);
                    throw new IllegalArgumentException("Arguments can't be null."+ ioe);
                }
                systemDefaultSelector.connectFailed(uri, sa, ioe);
            }
        });

        ConnectionStringBuilder connStrBuilder = new ConnectionStringBuilder(connectionString, queueName);
        connStrBuilder.setTransportType(TransportType.AMQP_WEB_SOCKETS);
        try {
            // Create a QueueClient instance for receiving using the connection string builder
            // We set the receive mode to "PeekLock", meaning the message is delivered
            // under a lock and must be acknowledged ("completed") to be removed from the queue
            receiveClient = new QueueClient(connStrBuilder, ReceiveMode.PEEKLOCK);
            log.info("Connection is established with Azure service bus");
        } catch (InterruptedException e) {
            log.error("Interrupted in  making connection to Azure service bus with exception: " + e);
        } catch (ServiceBusException e) {
            log.error("Error making connection to Azure service bus to EndPoint URI " + connStrBuilder.getEndpoint() + 
                      " with Queue Name: " + connStrBuilder.getEntityPath() + " with exception: " + e);
        }
    }

    void registerReceiver(QueueClient queueClient, ExecutorService executorService) throws Exception {

        // register the RegisterMessageHandler callback with executor service
        queueClient.registerMessageHandler(new IMessageHandler() {
            // callback invoked when the message handler loop has obtained a message
            public CompletableFuture<Void> onMessageAsync(IMessage message) {
                // receives message is passed to callback
                {
                    HoneywellWifiData data = buildHoneywellWifiData(message);
                    try {
                        processMessage(data);
                    } catch (Exception e) {
                        log.error("Error processing Honeywell wifi message of type: " + data.getType(), e);
                        log.debug(data);
                    }
                }
                return CompletableFuture.completedFuture(null);
            }

            // callback invoked when the message handler has an exception to report
            public void notifyException(Throwable throwable, ExceptionPhase exceptionPhase) {
                log.error("Error occured in handling the wifi message with ExceptionPhase type : " + exceptionPhase + "-" + throwable.getMessage());
            }
        }, executorService);

    }

    /**
     * Extracts binary message from an Azure IMessage.
     */
    private byte[] extractBinaryMessage(IMessage message) {

        MessageBody messageBody = message.getMessageBody();
        List<byte[]> binaryData = messageBody.getBinaryData();
        // No more messages on the queue
        if (message == null || message.getMessageBody() == null || binaryData.get(0) == null) {
            log.debug("No messages available. (Message or messageId null)");
            lastEmptyQueueTime = DateTime.now();
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        log.debug("Handling message, id=" + message.getMessageId());
        return binaryData.get(0);
    }

    /**
     * Builds a HoneywellWifiData message from an Azure IMessage.
     */
    private HoneywellWifiData buildHoneywellWifiData(IMessage message) {
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
            for (Map.Entry<String, Object> entry : message.getProperties().entrySet()) {
                log.trace("Message property: " + entry.getKey() + ", value: " + entry.getValue());
            }
        }

        return getData(messageBody, message);
    }

    private HoneywellWifiData getData(String messageBody, IMessage message) {
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
    private HoneywellWifiData getMessageData(HoneywellWifiMessageWrapper messageWrapper, IMessage originalMessage) {
        ObjectMapper jsonParser = new ObjectMapper();
        String jsonPayload = StringEscapeUtils.unescapeJava(messageWrapper.getJson());
        try {
            HoneywellWifiData data = jsonParser.readValue(jsonPayload, messageWrapper.getType().getMessageClass());
            if (messageWrapper.getType().name() == "UNKNOWN") {
                log.info("Received new event of unknown data type, enable debugging for more information.");

                if (log.isDebugEnabled() && originalMessage.getMessageBody() != null) {
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
