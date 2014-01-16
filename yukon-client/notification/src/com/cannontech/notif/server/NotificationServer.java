package com.cannontech.notif.server;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.messaging.connection.*;
import com.cannontech.messaging.connection.Connection.ConnectionState;
import com.cannontech.messaging.connection.event.ConnectionEventHandler;
import com.cannontech.messaging.connection.event.InboundConnectionEventHandler;
import com.cannontech.messaging.util.ConnectionFactoryService;
import com.cannontech.messaging.util.ListenerConnectionFactory;
import com.cannontech.notif.outputs.OutputHandlerHelper;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.dao.GlobalSettingDao;

/**
 * The server used for accepting and creating notification messages.
 */
public class NotificationServer implements NotificationServerMBean {
    private static final Logger log = YukonLogManager.getLogger(NotificationServer.class);

    // The servers listening connection
    private ListenerConnection server;

    @Autowired private NotificationMessageHandler messageHandler;
    @Autowired private OutputHandlerHelper outputHelper;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired public ConnectionFactoryService connFactorySvc;

    public static void main(String[] argsv) {
        try {
            System.setProperty("cti.app.name", "Notification-Server");
            log.info("Starting notification server from main method");
            YukonSpringHook.setDefaultContext(YukonSpringHook.NOTIFICATION_BEAN_FACTORY_KEY);

            NotificationServer ns = YukonSpringHook.getBean("notificationServer", NotificationServer.class);

            ns.start();
        }
        catch (Throwable t) {
            log.error("There was an error starting up the Notification Server", t);
        }
    }

    @Override
    public final String toString() {
        return "ListenerConnection[name=" + "NotifListener" + ", host=" + server != null ? server.getName() : "unknown"
                                                                                                              + "]";
    }

    /**************************************************************************
     * NotificationServerMBean interface implementation *
     **************************************************************************/
    /**
     * Start the notification server. If this fails with an exception, no threads will have been started.
     * @throws IOException
     */
    @Override
    public void start() {
        try {
            ListenerConnectionFactory notifListenerFactory = connFactorySvc.findListenerConnectionFactory("NotifListener");

            server = notifListenerFactory.createListenerConnection();
            server.setName("NotifListener");
            server.getInboundConnectionEvent().registerHandler(eventHandler);
            server.getConnectionEvent().registerHandler(eventHandler);

            server.start();

            // server = new ServerSocket(getPort(), getBacklog(), null);

            // start output handlers
            outputHelper.startup();

            log.info("Started Notification server: " + server);
        }
        catch (Exception e) {
            try {
                if (server != null) {
                    server.close();
                    server = null;
                }
            }
            catch (Throwable e1) {
                // No op
            }

            throw new RuntimeException(e);
        }
    }

    /**
     * Shutdown the notification server
     */
    @Override
    public void stop() {
        try {

            if (server != null) {
                server.close();
                server = null;
            }

            // shutdown voice handler
            outputHelper.shutdown();
        }
        catch (Exception e) {}

        log.info("Stopped Notification server: " + server);

    }

    @Override
    public boolean isRunning() {
        return server != null;
    }

    /**************************************************************************
     * ConnectionEventHandler inner class
     **************************************************************************/
    private final ClientConnectionEventHandler eventHandler = new ClientConnectionEventHandler();

    private class ClientConnectionEventHandler implements InboundConnectionEventHandler, ConnectionEventHandler {

        @Override
        public void onConnectionEvent(Connection source, ConnectionState state) {
            log.info("Connection state changed to <" + state + "> for " + NotificationServer.this);

            switch (state) {
                case New:
                case Connecting:
                case Connected:
                    break;

                // case Closed:
                // case Disconnected:
                // case Error:
                default:
                    // If the server is not null meaning we were not stopped report the error
                    if (server != null) {
                        log.error("Failed to connect to the broker.");
                    }
                    break;
            }
        }

        @Override
        public void onInboundConnectionEvent(ListenerConnection source, Connection newServerConnection) {
            // we have a connection, pass it off to another thread to process it
            try {
                // start handling the message here
                NotifServerConnection conn = new NotifServerConnection(newServerConnection, messageHandler);

                conn.connectWithoutWait(); // passes control to another thread
            }
            catch (Exception ex) {
                log.error("error handling incomming connection", ex);
            }
        }
    }
}
