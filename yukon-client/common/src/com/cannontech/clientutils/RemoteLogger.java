package com.cannontech.clientutils;

import org.apache.log4j.spi.LoggingEvent;

/**
 * Implemented by RemoteLoggingImpl for 
 * logging events from client to server
 * over http
 * @author tmack and dharrington
 *
 */
public interface RemoteLogger {
    public void doLog(String applicationName, String clientId, LoggingEvent event);
}
