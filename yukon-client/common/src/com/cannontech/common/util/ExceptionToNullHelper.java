package com.cannontech.common.util;

import java.util.function.Supplier;

import org.apache.logging.log4j.core.Logger;

import com.cannontech.clientutils.YukonLogManager;

/**
 * A helper that EATS EXCEPTIONS and returns null instead. Use carefully!
 * The exception is logged, but not the stack trace.
 */
public class ExceptionToNullHelper {
    private static Logger log = YukonLogManager.getLogger(ExceptionToNullHelper.class);
    
    private ExceptionToNullHelper() {
        //Do not instantiate
    }
    
    public static <T> T nullifyExceptions(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            log.info("Expected exception converted to null: {}", e.toString());
            return null;
        }
    }
}