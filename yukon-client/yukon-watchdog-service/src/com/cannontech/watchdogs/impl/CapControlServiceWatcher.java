package com.cannontech.watchdogs.impl;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.message.capcontrol.model.CapControlCommand;
import com.cannontech.message.capcontrol.model.CommandType;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.util.ConnectionFactoryService;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdog.service.WatchdogWatcherService;
import com.cannontech.watchdogs.message.WatchdogMessageListener;
import com.cannontech.watchdogs.util.WatchdogCapControlClientConnection;

public class CapControlServiceWatcher extends ServiceStatusWatchdogImpl implements WatchdogMessageListener {

    private static final Logger log = YukonLogManager.getLogger(CapControlServiceWatcher.class);

    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    @Autowired private ConnectionFactoryService connectionFactorySvc;
    @Autowired private WatchdogWatcherService watcherService;

    private BlockingQueue<Message> queue = new ArrayBlockingQueue<>(1);
    private static WatchdogCapControlClientConnection clientConnection;

    @Override
    public void start() {
        executor.scheduleAtFixedRate(() -> {
            createConnection();
            sendRequestAllDataCommandtoServer();
        }, 0, 5, TimeUnit.MINUTES);
    }

    private void sendRequestAllDataCommandtoServer() {
        if (clientConnection.isValid()) {
            clientConnection.sendCommand(new CapControlCommand(CommandType.REQUEST_ALL_DATA.getCommandId()));
        } else {
            log.debug("Invalid connection with CapControl Service");
        }
        watchAndNotify();
    }

    public WatchdogCapControlClientConnection createConnection() {
        if (clientConnection == null) {
            clientConnection = new WatchdogCapControlClientConnection();
            clientConnection.addWatchdogMessageListener(this);
            clientConnection.setConnectionFactory(connectionFactorySvc.findConnectionFactory("CBC"));
            clientConnection.connectWithoutWait();
        } else {
            return clientConnection;
        }

        for (int retry = 0; retry < 5; retry++) {
            if (clientConnection.isValid()) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
        return clientConnection;
    }

    @Override
    public List<WatchdogWarnings> watch() {
        Message message = null;
        try {
            message = queue.poll(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            /* ignore */
        }
        if (message == null) {
            log.debug("Status of CapControl service " + ServiceStatus.STOPPED);
            return generateWarning(WatchdogWarningType.CAPCONTROL_SERVICE_STATUS, ServiceStatus.STOPPED);
        } else {
            log.debug("Status of CapControl service " + ServiceStatus.RUNNING);
            return generateWarning(WatchdogWarningType.CAPCONTROL_SERVICE_STATUS, ServiceStatus.RUNNING);
        }
    }

    @Override
    public void handleMessage(Message message) {
        log.debug("messageReceived: " + message.toString());
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            /* ignore */
        }
    }

    public YukonServices getServiceName() {
        return YukonServices.CAPCONTROL;
    }

    @Override
    public boolean shouldRun() {
        return watcherService.isServiceRequired(getServiceName());
    }
}
