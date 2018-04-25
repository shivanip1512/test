package com.cannontech.clientutils;

import java.io.File;
import java.io.Serializable;
import java.util.zip.Deflater;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;
import org.apache.logging.log4j.core.appender.rolling.RolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.cannontech.common.util.BootstrapUtils;

/**
 * This customize appender serves logging for the rfnCommsLogger.
 * YukonRfnRollingFileAppender does set the pattern required for 
 * rolling of log file and determines the log directory.
 * Actual appending is delegated to a YukonRollingFileAppender
 * that handles actual appending, roll over, and timing issues.
 */

@Plugin(name = "YukonRfnRollingFile", category = "Core", elementType = "appender", printObject = true)
public class YukonRfnRollingFileAppender extends YukonRollingFileAppender {

    private static volatile YukonRfnRollingFileAppender instance;

    public YukonRfnRollingFileAppender(String name, Filter filter, Layout<? extends Serializable> layout, String fileName,
            String pattern, TriggeringPolicy policy, RolloverStrategy strategy, String applicationName,
            String directory, RollingFileManager manager) {
        super(name, filter, layout, fileName, pattern, policy, strategy, applicationName, directory, manager);
        
    }

    @PluginFactory
    public static YukonRfnRollingFileAppender createAppender(@PluginAttribute("name") String name,
            @PluginAttribute("pattern") String pattern,
            @PluginElement("Layout") Layout<? extends Serializable> layout, 
            @PluginElement("Filters") Filter filter,
            @PluginElement("Policy") TriggeringPolicy policy,
            @PluginElement("Strategy") RolloverStrategy strategy) {

        String directory = BootstrapUtils.getServerLogDir();
        String applicationName = "RfnCommsLog";
        String fileName = directory + applicationName + ".log";

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        if (pattern == null) {
            pattern = directory + applicationName + "_" + "%d{" + filenameDateFormat + "}.log";
        }

        if (strategy == null) {
            DefaultRolloverStrategy.newBuilder().withCompressionLevelStr(String.valueOf(Deflater.DEFAULT_COMPRESSION))
                                                .withConfig(((Logger) LogManager.getLogger(
                                                            YukonRollingFileAppender.class)).getContext().getConfiguration()).build();
        }
        final RollingFileManager manager = RollingFileManager.getFileManager(fileName, pattern, true, false, policy,
            strategy, new File(fileName).toURI().toString(), layout, 8192, false, true, "wr", null, null,
            ((Logger) LogManager.getLogger(YukonRollingFileAppender.class)).getContext().getConfiguration());

        instance = new YukonRfnRollingFileAppender(name, filter, layout, fileName, pattern, policy, strategy, applicationName, directory,manager);
        
        if (manager != null) {
            manager.initialize();
        }
        
        return instance;
    }
    
    @Override
    public void append(final LogEvent event) {
        // Call YukonRollingFileAppender for actual append.
        super.append(event);
    }
    
}
