package com.cannontech.notif.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.messaging.connection.Connection;
import com.cannontech.messaging.connection.ListenerConnection;
import com.cannontech.messaging.connection.Connection.ConnectionState;
import com.cannontech.messaging.connection.event.ConnectionEventHandler;
import com.cannontech.messaging.connection.event.InboundConnectionEventHandler;
import com.cannontech.messaging.util.*;
import com.cannontech.notif.outputs.OutputHandlerHelper;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.util.MBeanUtil;

/**
 * The server used for accepting and creating notification messages.
 */
public class NotificationServer implements NotificationServerMBean {
    private static final Logger log = YukonLogManager.getLogger(NotificationServer.class);

    // The port the web server listens on
    private int port = 1515;

    // The interface to bind to
    private InetAddress bindAddress;

    // The serverSocket listen queue depth
    private int backlog = 50;

    // The servers listening socket
    // private ServerSocket server = null;
    ListenerConnection server;

    // The thread accept any incoming connections
    // private Thread acceptThread = null;

    @Autowired
    private NotificationMessageHandler messageHandler;
    @Autowired
    private OutputHandlerHelper outputHelper;
    @Autowired
    private GlobalSettingDao globalSettingDao;

    /**
     * Start the notification server. If this fails with an exception, no threads will have been started.
     * @throws IOException
     */
    public void start() {
        try {

            String bindAddress = globalSettingDao.getString(GlobalSettingType.NOTIFICATION_HOST);
            int port = globalSettingDao.getInteger(GlobalSettingType.NOTIFICATION_PORT);

            setBindAddress(bindAddress);
            setPort(port);

            ListenerConnectionFactory notifListenerFactory =
                ConnectionFactoryService.getInstance().findListenerConnectionFactory("NotifListener");

            server = notifListenerFactory.createListenerConnection(getPort());
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

    public boolean isRunning() {
        return server != null;
    }

    public static void main(String[] argsv) {
        try {
            System.setProperty("cti.app.name", "Notification-Server");
            log.info("Starting notification server from main method");
            YukonSpringHook.setDefaultContext(YukonSpringHook.NOTIFICATION_BEAN_FACTORY_KEY);

            NotificationServer ns = (NotificationServer) YukonSpringHook.getBean("notificationServer");

            MBeanUtil.tryRegisterMBean("type=notificationserver", ns);

            ns.start();
        }
        catch (Throwable t) {
            log.error("There was an error starting up the Notification Server", t);
        }
    }

    @Override
    public final String toString() {
        return "ListenerConnection[name=" + "NotifListener" + ", host=" + server != null ? server.getName()
            : "unknown" + ", port=" + getPort() + "]";
    }

    /**************************************************************************
     * NotificationServerMBean interface implementation *
     **************************************************************************/

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int i) {
        if (i <= 0) {
            i = 50;
        }
        backlog = i;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int i) {
        port = i;
    }

    public String getBindAddress() {
        String address = null;
        if (bindAddress != null) {
            address = bindAddress.getHostAddress();
        }

        return address;
    }

    public void setBindAddress(String host) {
        try {
            if (host != null) {
                bindAddress = InetAddress.getByName(host);
            }
        }
        catch (UnknownHostException e) {
            String msg = "Invalid host address specified: " + host;
            log.error(msg, e);
        }
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
                        log.error("Failed to accept connection");
                    }
                    server = null;
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
