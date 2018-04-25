package com.cannontech.notif.server;

import java.io.IOException;

import javax.annotation.PreDestroy;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.messaging.connection.Connection;
import com.cannontech.messaging.connection.Connection.ConnectionState;
import com.cannontech.messaging.connection.ListenerConnection;
import com.cannontech.messaging.connection.event.ConnectionEventHandler;
import com.cannontech.messaging.connection.event.InboundConnectionEventHandler;
import com.cannontech.messaging.util.ConnectionFactoryService;
import com.cannontech.messaging.util.ListenerConnectionFactory;
import com.cannontech.notif.outputs.OutputHandlerHelper;
import com.cannontech.spring.YukonSpringHook;

/**
 * The server used for accepting and creating notification messages.
 */
public class NotificationServer {
    private static class LogHolder {
        static final Logger log = YukonLogManager.getLogger(NotificationServer.class);
    }

    // The servers listening connection
    private ListenerConnection server;

    @Autowired private NotificationMessageHandler messageHandler;
    @Autowired private OutputHandlerHelper outputHelper;
    @Autowired private ConnectionFactoryService connFactorySvc;

    public static void main(String[] argsv) {
        try {
            CtiUtilities.setCtiAppName(ApplicationId.NOTIFICATION);
            getLogger().info("Starting notification server from main method");
            YukonSpringHook.setDefaultContext(YukonSpringHook.NOTIFICATION_BEAN_FACTORY_KEY);

            NotificationServer ns = YukonSpringHook.getBean("notificationServer", NotificationServer.class);

            ns.start();

            synchronized (ns) {
                while (ns.isRunning()) {
                   ns.wait();
                }
            }
        }
        catch (Throwable t) {
            getLogger().error("There was an error starting up the Notification Server", t);
            System.exit(1);
        }
    }

    private static Logger getLogger() {
        return LogHolder.log;
    }

    @Override
    public final String toString() {
        return "ListenerConnection[name=" + "NotifListener" + ", host=" + server != null ? server.getName() : "unknown"
                                                                                                              + "]";
    }

    /**
     * Start the notification server. If this fails with an exception, no threads will have been started.
     * @throws IOException
     */
    private synchronized void start() {
        ListenerConnectionFactory notifListenerFactory = connFactorySvc.findListenerConnectionFactory("NotifListener");

        server = notifListenerFactory.createListenerConnection();
        server.setName("NotifListener");
        server.getInboundConnectionEvent().registerHandler(eventHandler);
        server.getConnectionEvent().registerHandler(eventHandler);

        server.start();

        // start output handlers
        outputHelper.startup();

        getLogger().info("Started notification server: " + server);
    }

    /**
     * Shutdown the notification server
     */
    @PreDestroy
    public synchronized void shutdown() {
        String zServer = (server != null) ? server.toString() : "not started";

        try {
            if (server != null) {
                server.close();
                server = null;
            }

            // shutdown output handler
            outputHelper.shutdown();

            notify(); // notify main thread

            getLogger().info("Stopped notification server: " + zServer);
        }
        catch (Exception e) {
            getLogger().error("Error while stopping notification server: "+ zServer, e);
            System.exit(1);
        }
    }

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
            getLogger().info("Connection state changed to <" + state + "> for " + NotificationServer.this);

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
                        getLogger().error("Failed to connect to the broker.");
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
                getLogger().error("error handling incomming connection", ex);
            }
        }
    }
}
