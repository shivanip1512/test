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
import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.util.ConnectionFactoryService;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdog.service.WatchdogWatcherService;
import com.cannontech.watchdogs.message.WatchdogMessageListener;
import com.cannontech.watchdogs.util.WatchdogLoadManagementClientConnection;

public class LoadManagementServiceWatcher extends ServiceStatusWatchdogImpl implements WatchdogMessageListener {
    private static final Logger log = YukonLogManager.getLogger(LoadManagementServiceWatcher.class);

    @Autowired private ConnectionFactoryService connectionFactorySvc;
    @Autowired private WatchdogWatcherService watcherService;
    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;

    private BlockingQueue<Message> queue = new ArrayBlockingQueue<>(1);
    private static WatchdogLoadManagementClientConnection clientConnection;

    @Override
    public void start() {
        executor.scheduleAtFixedRate(() -> {
            createConnection();
            retrieveAllControlAreasFromServer();
        }, 0, 5, TimeUnit.MINUTES);

    }

    private void retrieveAllControlAreasFromServer() {
        if (clientConnection.isValid()) {
            LMCommand cmd = new LMCommand();
            cmd.setPriority(14);
            cmd.setCommand(LMCommand.RETRIEVE_ALL_CONTROL_AREAS);
            clientConnection.write(cmd);
        } else {
            log.debug("Invalid connection with LoadManagement Service ");
        }
        watchAndNotify();
    }

    private WatchdogLoadManagementClientConnection createConnection() {
        if (clientConnection == null) {
            clientConnection = new WatchdogLoadManagementClientConnection();
            clientConnection.addWatchdogMessageListener(this);
            clientConnection.setConnectionFactory(connectionFactorySvc.findConnectionFactory("LC"));
            clientConnection.connectWithoutWait();
        }else {
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
            log.debug("Status of LoadManagement service " + ServiceStatus.STOPPED);
            return generateWarning(WatchdogWarningType.LOADMANAGEMENT_SERVICE_STATUS, ServiceStatus.STOPPED);
        } else {
            log.debug("Status of LoadManagement service " + ServiceStatus.RUNNING);
            return generateWarning(WatchdogWarningType.LOADMANAGEMENT_SERVICE_STATUS, ServiceStatus.RUNNING);
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

    @Override
    public YukonServices getServiceName() {
        return YukonServices.LOADMANAGEMENT;
    }
    
    @Override
    public boolean shouldRun() {
        return watcherService.isServiceRequired(getServiceName());
    }

}
