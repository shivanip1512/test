package com.cannontech.clientutils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.remoting.httpinvoker.HttpInvokerClientInterceptor;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;

import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.common.util.CtiUtilities;

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
    private static String applicationName;
    private static String clientId;
    
    /**
     *  This appenders name
     */
    private String name;
    private static String userName;
    private static String password;
    
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
        try {
            HttpInvokerClientInterceptor interceptor = new HttpInvokerClientInterceptor();
            SimpleHttpInvokerRequestExecutor requestExecutor = new SimpleHttpInvokerRequestExecutor();
            interceptor.setHttpInvokerRequestExecutor(requestExecutor);
            interceptor.setServiceUrl(hostName + "/remote/RemoteLogger" + "?" + "USERNAME=" + URLEncoder.encode(userName, "UTF-8") + "&PASSWORD=" + URLEncoder.encode(password, "UTF-8") + "&noLoginRedirect=true");
            remoteLogger = (RemoteLogger) ProxyFactory.getProxy(RemoteLogger.class, interceptor);
            applicationName = BootstrapUtils.getApplicationName();
            clientId = CtiUtilities.getIPAddress();
        } catch (UnsupportedEncodingException e) {
            remoteLogger = null;
        }
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
    
    public static void setUserName(String userName) {
        YukonRemoteAppender.userName = userName;
    }

    public static void setPassword(String password) {
        YukonRemoteAppender.password = password;
    }
}
