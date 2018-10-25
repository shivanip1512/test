package com.cannontech.watchdogs.impl;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.message.util.Command;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.util.ConnectionFactoryService;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdogs.message.WatchdogMessageListener;
import com.cannontech.watchdogs.util.WatchdogDispatchClientConnection;

/**
 * This class will validate Dispatch Service Status in every 5 min and generate warning.
 */

@Service
public class DispatchServiceWatcher extends ServiceStatusWatchdogImpl implements WatchdogMessageListener {

    private static final Logger log = YukonLogManager.getLogger(DispatchServiceWatcher.class);

    @Autowired private ConnectionFactoryService connectionFactorySvc;

    private volatile Instant receivedLatestMessageTimeStamp;
    private AtomicBoolean isDispatchRunning = new AtomicBoolean(false);
    private Instant sendMessageTimeStamp;
    private static WatchdogDispatchClientConnection dispatchConnection;
    
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    public void doWatchAction() {
        receivedLatestMessageTimeStamp = null;
        createConnection();
        sendLoopbackCommandToServer();
    }

    /**
     * Send LOOP_CLIENT command to Dispatch Service and based on Status, generate warning.
     */

    private void sendLoopbackCommandToServer() {
        // Received timestamp from c++ with 0 nanoseconds and Instant.now() gives timestamp with
        // nanoseconds precision
        sendMessageTimeStamp =  OffsetDateTime.now().withNano(0).toInstant();
        if (dispatchConnection.isValid()) {
            Command cmd = new Command();
            cmd.setOperation(Command.LOOP_CLIENT);
            cmd.setPriority(14);
            cmd.setTimeStamp(new Date());
            dispatchConnection.write(cmd);
        } else {
            log.info("Invalid connection with Dispatch Service");
        }
        watchAndNotify();
    }

    /**
     * Creating Dispatch client connection.
     */

    private WatchdogDispatchClientConnection createConnection() {
        if (dispatchConnection == null) {
            dispatchConnection = new WatchdogDispatchClientConnection();
            dispatchConnection.addWatchdogMessageListener(this);
            dispatchConnection.setConnectionFactory(connectionFactorySvc.findConnectionFactory("Dispatch"));
            dispatchConnection.connectWithoutWait();
        } else {
            return dispatchConnection;
        }

        for (int retry = 0; retry < 3; retry++) {
            if (dispatchConnection.isValid()) {
                break;
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {}
        }
        return dispatchConnection;
    }

    @Override
    public List<WatchdogWarnings> watch() {

        try {
            countDownLatch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {}

        if (receivedLatestMessageTimeStamp == null || !dispatchConnection.isValid()) {
            log.info("Status of Dispatch service " + ServiceStatus.STOPPED);
            isDispatchRunning.set(false);
            return generateWarning(WatchdogWarningType.YUKON_DISPATCH_SERVICE, ServiceStatus.STOPPED);
        } else {
            log.info("Status of Dispatch service " + ServiceStatus.RUNNING);
            isDispatchRunning.set(true);
            return generateWarning(WatchdogWarningType.YUKON_DISPATCH_SERVICE, ServiceStatus.RUNNING);
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
        return YukonServices.DISPATCH;
    }

    @Override
    public boolean isServiceRunning() {
        return isDispatchRunning.get();
    }

}
