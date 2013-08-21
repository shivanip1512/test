package com.cannontech.notif.server;

import java.io.IOException;

public interface NotificationServerMBean {  
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