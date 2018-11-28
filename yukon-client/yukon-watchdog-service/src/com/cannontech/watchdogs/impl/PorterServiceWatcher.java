package com.cannontech.watchdogs.impl;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.message.util.Command;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.util.ConnectionFactoryService;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdogs.message.WatchdogMessageListener;
import com.cannontech.watchdogs.util.WatchdogPorterClientConnection;

/**
 * This class will validate Port Control Service Status in every 5 min and generate warning.
 */

@Service
public class PorterServiceWatcher extends ServiceStatusWatchdogImpl implements WatchdogMessageListener {

    public PorterServiceWatcher(ConfigurationSource configSource) {
        super(configSource);
    }

    private static final Logger log = YukonLogManager.getLogger(PorterServiceWatcher.class);
    @Autowired private ConnectionFactoryService connectionFactorySvc;

    private volatile Instant receivedLatestMessageTimeStamp;
    private Instant sendMessageTimeStamp;
    private static WatchdogPorterClientConnection porterClientConnection;
    
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    public void doWatchAction() {
        receivedLatestMessageTimeStamp = null;
        createConnection();
        sendLoopbackCommandToServer();
    }

    /**
     * Send LOOP_CLIENT command to Port Control Service and based on Status, generate warning.
     */

    private void sendLoopbackCommandToServer() {
        // Received timestamp from c++ with 0 nanoseconds and Instant.now() gives timestamp with
        // nanoseconds precision
        sendMessageTimeStamp =  OffsetDateTime.now().withNano(0).toInstant();
        if (porterClientConnection.isValid()) {
            Command cmd = new Command();
            cmd.setOperation(Command.LOOP_CLIENT);
            cmd.setPriority(14);
            cmd.setTimeStamp(new Date());
            porterClientConnection.write(cmd);
        } else {
            log.info("Invalid connection with Porter Service ");
        }
        watchAndNotify();
    }

    /**
     * Creating Port Control client connection.
     */

    private WatchdogPorterClientConnection createConnection() {
        if (porterClientConnection == null) {
            porterClientConnection = new WatchdogPorterClientConnection();
            porterClientConnection.addWatchdogMessageListener(this);
            porterClientConnection.setConnectionFactory(connectionFactorySvc.findConnectionFactory("Porter"));
            porterClientConnection.connectWithoutWait();
        } else {
            return porterClientConnection;
        }

        for (int retry = 0; retry < 3; retry++) {
            if (porterClientConnection.isValid()) {
                break;
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {}
        }
        return porterClientConnection;
    }

    @Override
    public List<WatchdogWarnings> watch() {
        try {
            countDownLatch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {}
        if (receivedLatestMessageTimeStamp == null || (receivedLatestMessageTimeStamp.isBefore(sendMessageTimeStamp)) || !porterClientConnection.isValid()) {
            log.info("Status of Porter service " + ServiceStatus.STOPPED);
            return generateWarning(WatchdogWarningType.YUKON_PORT_CONTROL_SERVICE, ServiceStatus.STOPPED);
        } else {
            log.info("Status of Porter service " + ServiceStatus.RUNNING);
            return generateWarning(WatchdogWarningType.YUKON_PORT_CONTROL_SERVICE, ServiceStatus.RUNNING);
        }
    }

    @Override
    public void handleMessage(Message message) {
        log.debug("messageReceived: " + message.toString());
        Instant timeStamp = message.getTimeStamp().toInstant();

        if (sendMessageTimeStamp != null) {
            Instant compareTimeStamp = Optional.ofNullable(receivedLatestMessageTimeStamp).orElse(sendMessageTimeStamp);
            if (timeStamp.compareTo(compareTimeStamp) >= 0) {
                receivedLatestMessageTimeStamp = timeStamp;
            }
        }
        
    }

    @Override
    public YukonServices getServiceName() {
        return YukonServices.PORTER;
    }

}
