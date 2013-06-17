package com.cannontech.notif.server;

import java.io.IOException;
import java.net.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.notif.outputs.OutputHandlerHelper;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.util.MBeanUtil;

/**
 * The server used for accepting and creating notification messages.
 * 
 */
public class NotificationServer implements Runnable, NotificationServerMBean
{
    private static final Logger log = YukonLogManager.getLogger(NotificationServer.class);

    // The port the web server listens on
    private int port = 1515;

    // The interface to bind to
    private InetAddress bindAddress;

    // The serverSocket listen queue depth
    private int backlog = 50;

    // The servers listening socket
    private ServerSocket server = null;

    // The thread accept any incoming connections
    private Thread acceptThread = null;

    @Autowired private NotificationMessageHandler messageHandler;
    @Autowired private OutputHandlerHelper outputHelper;
    @Autowired private GlobalSettingDao globalSettingDao;

    /**
     * Start the notification server.
     * If this fails with an exception, no threads will have been started.
     * 
     * @throws IOException
     */
    public void start() {
        try {

            String bindAddress = globalSettingDao.getString(GlobalSettingType.NOTIFICATION_HOST);
            int port = globalSettingDao.getInteger(GlobalSettingType.NOTIFICATION_PORT);

            setBindAddress(bindAddress);
            setPort(port);

            server = new ServerSocket(getPort(), getBacklog(), null);

            // start output handlers
            outputHelper.startup();

            acceptThread = new Thread(this, "NotifListener");
            acceptThread.start();

            log.info("Started Notification server: " + server);
        } catch (Exception e) {
            try {
                if (server != null) {
                    server.close();
                }
            } catch (IOException e1) {
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
                ServerSocket temp = server;
                server = null;
                temp.close();
            }

            // shutdown voice handler
            outputHelper.shutdown();
        } catch (Exception e) {}

        log.info("Stopped Notification server: " + server);

    }

    public boolean isRunning() {
        return server != null;
    }

    /**
     * Listen threads entry point. Here we accept a client connection
     */
    public void run() {
        for (;;)
        {
            // Return if the server has been stopped
            if (server == null) {
                return;
            }

            // Accept a connection
            Socket socket = null;
            try {
                socket = server.accept();
            } catch (IOException e) {
                // If the server is not null meaning we were not stopped report the error
                if (server != null) {
                    log.error("Failed to accept connection", e);
                }

                server = null;
                return;
            }

            // we have a connection, pass it off to another thread to process it
            try {
                // start handling the message here
                NotifServerConnection conn = new NotifServerConnection(socket, messageHandler);

                conn.connectWithoutWait(); // passes control to another thread
            } catch (Exception ex) {
                log.error("error handling socket connection", ex);
            }
        }
    }

    public static void main(String[] argsv) {
        try {
            System.setProperty("cti.app.name", "Notification-Server");
            log.info("Starting notification server from main method");
            YukonSpringHook.setDefaultContext(YukonSpringHook.NOTIFICATION_BEAN_FACTORY_KEY);

            NotificationServer ns = (NotificationServer) YukonSpringHook.getBean("notificationServer");

            MBeanUtil.tryRegisterMBean("type=notificationserver", ns);

            ns.start();
        } catch (Throwable t) {
            log.error("There was an error starting up the Notification Server", t);
        }
    }

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
        } catch (UnknownHostException e) {
            String msg = "Invalid host address specified: " + host;
            log.error(msg, e);
        }
    }

}
