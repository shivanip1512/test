package com.cannontech.clientutils;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.cannontech.common.util.IterableUtils;

public class LogHelper {
    private final Logger log;

    private LogHelper(Logger log) {
        this.log = log;
        
    }
    
    public static LogHelper getInstance(Logger log) {
        return new LogHelper(log);
    }
    
    private static void log(Logger log, Level level, String format, Object... args) {
        if (log.isEnabledFor(level)) {
            String output = String.format(format, processArgs(args));
            log.log(level, output);
        }
    }
    
    public static void warn(Logger log, String format, Object ... args) {
        log(log, Level.WARN, format, args);
    }

    public void warn(String format, Object ... args) {
        warn(log, format, args);
    }
    
    public static void debug(Logger log, String format, Object ... args) {
        if (log.isDebugEnabled()) {
            String output = String.format(format, processArgs(args));
            log.debug(output);
        }
    }
    
    public void debug(String format, Object ... args) {
        debug(log, format, args);
    }

    public static void trace(Logger log, String format, Object ... args) {
        if (log.isTraceEnabled()) {
            String output = String.format(format, processArgs(args));
            log.trace(output);
        }
    }

    public void trace(String format, Object ... args) {
        trace(log, format, args);
    }
    
    private static Object[] processArgs(Object[] args) {
        Object[] result = new Object[args.length];
        for (int i = 0; i < result.length; i++) {
            Object value = args[i];
            if (value instanceof Iterable) {
                value = IterableUtils.toFormattable((Iterable<?>) value);
            }
            result[i] = value;
        }
        return result;
    }

}
