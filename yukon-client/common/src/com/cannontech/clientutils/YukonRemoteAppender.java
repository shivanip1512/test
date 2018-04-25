package com.cannontech.clientutils;

import java.io.File;
import java.io.Serializable;
import java.util.zip.Deflater;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;
import org.apache.logging.log4j.core.appender.rolling.RolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.cannontech.common.config.RemoteLoginSession;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.common.util.CtiUtilities;

/**
 * YukonRemoteAppender is a custom appender used for
 * appending messages from clients back to the server
 * through http. The spring frameworks HttpInvoker class
 * was used to abstract away the remoting details.*
 */

// TODO: This file has to be changed based on Remote Logging Appender
@Plugin(name = "YukonRemoteRollingFile", category = "Core", elementType = "appender", printObject = true)
public class YukonRemoteAppender extends AbstractOutputStreamAppender<RollingFileManager> {
    // TODO: Have to check while implementing Remote Appender
    public YukonRemoteAppender(final String name, final Filter filter, final Layout<? extends Serializable> layout,
            String fileName, String pattern, TriggeringPolicy policy, RolloverStrategy strategy, String applicationName,
            RollingFileManager manager) {
        super(name, layout, filter, true, true, manager);
    }

    private static volatile YukonRemoteAppender instance;

    public final static int LOG_RETENTION_DAYS = 90;

    protected final static String filenameDateFormat = "yyyyMMdd";
    private static RemoteLogger remoteLogger;
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

    @PluginFactory
    public static YukonRemoteAppender createAppender(@PluginAttribute("name") String name,
            @PluginAttribute("pattern") String pattern,
            @PluginElement("Layout") Layout<? extends Serializable> layout, 
            @PluginElement("Filters") Filter filter,
            @PluginElement("Policy") TriggeringPolicy policy,
            @PluginElement("Strategy") RolloverStrategy strategy) {

        String applicationName = BootstrapUtils.getApplicationName();
        String fileName = applicationName + ".log";
        
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        if (pattern == null) {
            pattern = applicationName + "_" + "%d{" + filenameDateFormat + "}.log";
        }

        if (strategy == null) {
            DefaultRolloverStrategy.newBuilder().withCompressionLevelStr(String.valueOf(Deflater.DEFAULT_COMPRESSION))
                                                .withConfig(((Logger) LogManager.getLogger(
                                                    YukonRemoteAppender.class)).getContext().getConfiguration()).build();
        }
        final RollingFileManager manager = RollingFileManager.getFileManager(fileName, pattern, true, false, policy,
            strategy, new File(fileName).toURI().toString(), layout, 8192, false, true, "wr", null, null,
            ((Logger) LogManager.getLogger(YukonRemoteAppender.class)).getContext().getConfiguration());

        instance = new YukonRemoteAppender(name, filter, layout, fileName, pattern, policy, strategy, applicationName, manager);
        
        if (manager != null) {
            manager.initialize();
        }
        
        return instance;

    }
    /**
     * Default constructor
     */
    // TODO: Have to check while implementing Remote Appender
    /*    public YukonRemoteAppender() {
        super();
        remoteLogger = null;
    }*/
    
    /**
     * Sets up spring interface and sets the client's application name
     * and clientID (IP address)
     */
    public static void configureLogger(RemoteLoginSession remoteLoginSession) {
        remoteLogger = remoteLoginSession.getReconnectingInteceptorProxy(RemoteLogger.class, "/remote/RemoteLogger");
        applicationName = BootstrapUtils.getApplicationName();
        clientId = CtiUtilities.getIPAddress(); 
    } 

    @Override
    public void append(LogEvent event) {
        if (remoteLogger == null) {
            return;
        }
        // TODO: Updtae this after Remote logger impl
        remoteLogger.doLog(applicationName, clientId, event);
    }
     // TODO: Have to check while implementing Remote Appender
/*    @Override
    public boolean requiresLayout() {
        return false;
    }

    @Override
    public void close() {
    }*/

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
}
