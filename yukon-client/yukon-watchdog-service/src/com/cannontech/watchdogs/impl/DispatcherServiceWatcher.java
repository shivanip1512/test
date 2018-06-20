package com.cannontech.watchdogs.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.message.util.Command;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.util.ConnectionFactoryService;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdogs.message.WatchdogMessageListener;
import com.cannontech.watchdogs.util.WatchdogDispatchClientConnection;

public class DispatcherServiceWatcher extends ServiceStatusWatchdogImpl implements WatchdogMessageListener {

    private static final Logger log = YukonLogManager.getLogger(DispatcherServiceWatcher.class);

    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    @Autowired private ConnectionFactoryService connectionFactorySvc;

    private BlockingQueue<Message> queue = new ArrayBlockingQueue<>(1);
    private static WatchdogDispatchClientConnection dispatchConnection;

    @Override
    public void start() {
        executor.scheduleAtFixedRate(() -> {
            createConnection();
            sendLoopBackCommandtoServer();
        }, 0, 5, TimeUnit.MINUTES);
    }

    private void sendLoopBackCommandtoServer() {
        if (dispatchConnection.isValid()) {
            Command cmd = new Command();
            cmd.setOperation(Command.LOOP_CLIENT);
            cmd.setPriority(14);
            cmd.setTimeStamp(new Date());
            dispatchConnection.write(cmd);
        } else {
            log.debug("Invalid connection with Dispatcher Service");
        }
        watchAndNotify();
    }

    private WatchdogDispatchClientConnection createConnection() {
        if (dispatchConnection == null) {
            dispatchConnection = new WatchdogDispatchClientConnection();
            dispatchConnection.addWatchdogMessageListener(this);
            dispatchConnection.setConnectionFactory(connectionFactorySvc.findConnectionFactory("Dispatch"));
            dispatchConnection.connectWithoutWait();
        } else {
            return dispatchConnection;
        }

        for (int retry = 0; retry < 5; retry++) {
            if (dispatchConnection.isValid()) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
        return dispatchConnection;
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
            log.debug("Status of Dispatcher service " + ServiceStatus.STOPPED);
            return generateWarning(WatchdogWarningType.DISPATCH_SERVICE_STATUS, ServiceStatus.STOPPED);
        } else {
            log.debug("Status of Dispatcher service " + ServiceStatus.RUNNING);
            return generateWarning(WatchdogWarningType.DISPATCH_SERVICE_STATUS, ServiceStatus.RUNNING);
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
        return YukonServices.DISPATCH;
    }
}
