package com.cannontech.clientutils;

import org.apache.log4j.Logger;

public class LogHelper {
    private final Logger log;

    private LogHelper(Logger log) {
        this.log = log;
        
    }
    
    public static LogHelper getInstance(Logger log) {
        return new LogHelper(log);
    }
    
    public static void debug(Logger log, String format, Object ... args) {
        if (log.isDebugEnabled()) {
            String output = String.format(format, args);
            log.debug(output);
        }
    }
    
    public void debug(String format, Object ... args) {
        debug(log, format, args);
    }

    public static void trace(Logger log, String format, Object ... args) {
        if (log.isTraceEnabled()) {
            String output = String.format(format, args);
            log.trace(output);
        }
    }

    public void trace(String format, Object ... args) {
        trace(log, format, args);
    }
    
}
