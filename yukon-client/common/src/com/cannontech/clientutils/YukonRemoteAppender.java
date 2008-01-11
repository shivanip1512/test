package com.cannontech.clientutils;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.remoting.httpinvoker.HttpInvokerClientInterceptor;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.spring.SimpleSessionHttpInvokerRequestExecutor;

/**
 * YukonRemoteAppender is a custom appender used for
 * appending messages from clients back to the server
 * through http. The spring frameworks HttpInvoker class
 * was used to abastract away the remoting details.
 * @authors tmack and dharrington
 *
 */
public class YukonRemoteAppender extends AppenderSkeleton {
    private static RemoteLogger remoteLogger;
    private static String hostName;
    private static String portNumber;
    private static String sessionId;
    private static String applicationName;
    private static String clientId;
    
    /**
     *  This appenders name
     */
    private String name;
    
    /**
     * @return name for this appender
     */
    public final String getAppenderName() {
        return this.name;
    }
    
    /**
     * @param name is the name of this appender
     */
    public final void setAppenderName(String name) {
        this.name = name;
    }

    /**
     * Default constructor
     */
    public YukonRemoteAppender() {
        super();
        remoteLogger = null;
    }
    
    /**
     * Sets up spring interface and sets the client's application name
     * and clientID (IP address)
     */
    public static void configureLogger() {
        HttpInvokerClientInterceptor interceptor = new HttpInvokerClientInterceptor();
        SimpleSessionHttpInvokerRequestExecutor requestExecutor = new SimpleSessionHttpInvokerRequestExecutor();
        requestExecutor.setSessionId(sessionId);
        interceptor.setHttpInvokerRequestExecutor(requestExecutor);
        interceptor.setServiceUrl("http://" + hostName + ":" + portNumber + "/remote/RemoteLogger");
        remoteLogger = (RemoteLogger) ProxyFactory.getProxy(RemoteLogger.class, interceptor);
        applicationName = CtiUtilities.getApplicationName();
        clientId = CtiUtilities.getIPAddress();
    } 

    @Override
    protected void append(LoggingEvent event) {
        if (remoteLogger == null) {
            return;
        }
        remoteLogger.doLog(applicationName, clientId, event);
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    @Override
    public void close() {
    }

    /**
     * @return the application name of the client
     */
    public static String getApplicationName() {
        return applicationName;
    }

    /**
     * @param applicationName The application name of the client
     */
    public static void setApplicationName(String applicationName) {
        YukonRemoteAppender.applicationName = applicationName;
    }

    /**
     * @return The clients IP address
     */
    public static String getClientId() {
        return clientId;
    }

    /**
     * @param clientId The client's IP address
     */
    public static void setClientId(String clientId) {
        YukonRemoteAppender.clientId = clientId;
    }

    /**
     * @return The name of the hosts computer
     */
    public static String getHostName() {
        return hostName;
    }

    /**
     * @param hostName The host name of the server
     */
    public static void setHostName(String hostName) {
        YukonRemoteAppender.hostName = hostName;
    }
    
    public static String getSessionId() {
		return sessionId;
	}
    
    public static void setSessionId(String sessionId) {
		YukonRemoteAppender.sessionId = sessionId;
	}

    /**
     * @return The port number the client connects to
     */
    public static String getPortNumber() {
        return portNumber;
    }

    /**
     * @param portNumber port numbe the client connects to
     */
    public static void setPortNumber(String portNumber) {
        YukonRemoteAppender.portNumber = portNumber;
    }
}
