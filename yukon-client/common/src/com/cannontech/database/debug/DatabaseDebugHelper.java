package com.cannontech.database.debug;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;

public class DatabaseDebugHelper {
    public static void outputStackTrace(Logger log) {
        if (!log.isDebugEnabled()) {
            return;
        }
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // probably should tweak to remove this method...
        String stackStr = StringUtils.join(stackTrace, "\n    ");
        log.debug("\n    " + stackStr);
    }
    
    public static Logger getMainLogger() {
        return YukonLogManager.getLogger("databasedebug");
    }
    public static Logger getStackTraceLogger() {
        return YukonLogManager.getLogger("databasedebug.stack");
    }

}
