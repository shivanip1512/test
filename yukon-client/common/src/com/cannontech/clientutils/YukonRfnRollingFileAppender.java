package com.cannontech.clientutils;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.DirectWriteRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;
import org.apache.logging.log4j.core.appender.rolling.RolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.Configuration;
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
        String creationDate = new SimpleDateFormat(filenameDateFormat).format(new Date());
        // Create RfnComms log for each application separately. Example : ServiceManager_RfnComms.log and Webserver_RfnComms.log
        String applicationName = BootstrapUtils.getApplicationName() + "_" + "RfnComms";
        String fileName = directory + applicationName + "_" + creationDate +".log";
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        if (pattern == null) {
            pattern = directory + applicationName + "_" + "%d{" + filenameDateFormat + "}.log.zip";
        }
        Configuration config = ((Logger) LogManager.getLogger(YukonRollingFileAppender.class)).getContext().getConfiguration();
        // Override the strategy with DirectWriteRolloverStrategy if set DefaultRolloverStrategy from yukonLoggign.xml.
        if (strategy == null || strategy instanceof DefaultRolloverStrategy) {
            // maxFile : The maximum number of files to allow in the time period matching the file pattern.
            // compressionLevel : Sets the compression level, 0-9, where 0 = none and 9 = best compression.
            strategy = DirectWriteRolloverStrategy.newBuilder()
                                                  .withMaxFiles("1")
                                                  .withCompressionLevelStr("9")
                                                  .withConfig(config)
                                                  .build();
        }
        // Passing fileName as null (1st param) so that manager can generate it dynamically while rolling with the help of pattern.
        final RollingFileManager manager = RollingFileManager.getFileManager(null, pattern, true, false, policy,
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
