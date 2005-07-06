package com.cannontech.notif.server;

import java.io.IOException;

public interface NotificationServerMBean {

    public int getBacklog() throws IOException;

    public int getPort() throws IOException;

    public void setBacklog(int i) throws IOException;

    public void setPort(int i) throws IOException;

    public String getBindAddress() throws IOException;

    public void setBindAddress(String host) throws IOException;

    /**
     * Start the notification server.
     * If this fails with an exception, no threads will have been started.
     * @throws IOException
     */
    public void start() throws IOException;

    /**
     * Shutdown the notification server
     */
    public void stop() throws IOException;

    public boolean isRunning() throws IOException;

}