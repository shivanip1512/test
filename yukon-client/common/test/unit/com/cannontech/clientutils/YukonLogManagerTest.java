package com.cannontech.clientutils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.log.model.CustomizedSystemLogger;
import com.cannontech.common.log.model.SystemLogger;

public class YukonLogManagerTest {

    @Test
    public void test_shouldPopulateSystemLoggers_true() {
        List<SystemLogger> dbLoggers = new ArrayList<SystemLogger>();
        dbLoggers.add(SystemLogger.API_LOGGER);
        assertTrue((boolean) ReflectionTestUtils.invokeMethod(YukonLogManager.class, "shouldPopulateSystemLoggers", dbLoggers),
                "Should return true");
    }

    @Test
    public void test_shouldPopulateSystemLoggers_false() {
        List<SystemLogger> dbLoggers = new ArrayList<SystemLogger>();
        dbLoggers.addAll(Arrays.asList(SystemLogger.values()));
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(YukonLogManager.class, "shouldPopulateSystemLoggers", dbLoggers),
                "Should return false");
    }

    @Test
    public void test_getNewSystemLoggers() {
        List<SystemLogger> dbLoggers = new ArrayList<SystemLogger>();
        dbLoggers.addAll(Arrays.asList(SystemLogger.values()));
        List<SystemLogger> newLoggers = (List<SystemLogger>) ReflectionTestUtils.invokeMethod(YukonLogManager.class,
                "getNewSystemLoggers", dbLoggers);
        assertTrue(newLoggers.size() == 0, "Size must be zero");

        dbLoggers.remove(SystemLogger.API_LOGGER);
        dbLoggers.remove(SystemLogger.COMMS_LOGGER);
        newLoggers = (List<SystemLogger>) ReflectionTestUtils.invokeMethod(YukonLogManager.class, "getNewSystemLoggers",
                dbLoggers);
        assertTrue(newLoggers.size() == 2, "Size must be 2");
        assertTrue(newLoggers.contains(SystemLogger.API_LOGGER) && newLoggers.contains(SystemLogger.COMMS_LOGGER),
                "Must be API_LOGGER & COMMS_LOGGER");
    }

    @Test
    public void test_getAppenderRef() {
        assertTrue(YukonLogManager.getAppenderRef("apiLogger").equals("yukonApiRollingFile"),
                "Appender ref must be yukonApiRollingFile");
        assertTrue(YukonLogManager.getAppenderRef("commsLogger").equals("commsRollingFile"),
                "Appender ref must be commsRollingFile");
        assertTrue(YukonLogManager.getAppenderRef("rfnCommsLogger").equals("yukonRfnRollingFile"),
                "Appender ref must be yukonRfnRollingFile");
        assertTrue(YukonLogManager.getAppenderRef("smartNotifLogger").equals("smartNotifRollingFile"),
                "Appender ref must be smartNotifRollingFile");
        assertTrue(YukonLogManager.getAppenderRef("com.cannontech.clientutils").equals("yukonRollingFileAppender"),
                "Appender ref must be yukonRollingFileAppender");
    }

    @Test
    public void test_isCustomizedAppenderLogger() {

        assertTrue(CustomizedSystemLogger.isCustomizedAppenderLogger(
                "com.cannontech.web.filter.TokenAuthenticationAndLoggingFilter"), "Must be true");
        assertTrue(CustomizedSystemLogger.isCustomizedAppenderLogger(
                "com.cannontech.services.smartNotification"), "Must be true");
        assertTrue(CustomizedSystemLogger.isCustomizedAppenderLogger("com.cannontech.common.smartNotification"), "Must be true");
        assertTrue(CustomizedSystemLogger.isCustomizedAppenderLogger("com.cannontech.dr.rfn"), "Must be true");
        assertTrue(CustomizedSystemLogger.isCustomizedAppenderLogger("com.cannontech.services.rfn"), "Must be true");
        assertTrue(CustomizedSystemLogger.isCustomizedAppenderLogger(
                "com.cannontech.services.smartNotification.service.impl.SmartNotificationDeciderServiceImpl"),
                "Must be true");
        assertFalse(CustomizedSystemLogger.isCustomizedAppenderLogger("com.cannontech.clientutils"), "Must be alse");
    }

    @Test
    public void test_getCustomizedAppenderRef() {
        assertTrue(YukonLogManager.getCustomizedAppenderRef("com.cannontech.web.filter.TokenAuthenticationAndLoggingFilter")
                .equals("yukonApiRollingFile"), "Appender ref must be yukonApiRollingFile");
        assertTrue(YukonLogManager.getCustomizedAppenderRef(
                "com.cannontech.services.smartNotification").equals("commsRollingFile"), "Appender ref must be commsRollingFile");
        assertTrue(YukonLogManager.getCustomizedAppenderRef("com.cannontech.common.smartNotification").equals("commsRollingFile"),
                "Appender ref must be commsRollingFile");
        assertTrue(YukonLogManager.getCustomizedAppenderRef("com.cannontech.dr.rfn").equals("yukonRfnRollingFile"),
                "Appender ref must be yukonRfnRollingFile");
        assertTrue(YukonLogManager.getCustomizedAppenderRef("com.cannontech.services.rfn").equals("yukonRfnRollingFile"),
                "Appender ref must be yukonRfnRollingFile");
        assertTrue(YukonLogManager.getCustomizedAppenderRef(
                "com.cannontech.services.smartNotification.service.impl.SmartNotificationDeciderServiceImpl")
                .equals("smartNotifRollingFile"), "Appender ref must be smartNotifRollingFile");
    }

    @Test
    public void test_getLevel() {

        Map<String, Level> loggers = new HashMap<String, Level>();
        loggers.put("apiLogger", Level.INFO);
        loggers.put("commsLogger", Level.DEBUG);
        loggers.put("rfnCommsLogger", Level.ERROR);
        loggers.put("smartNotifLogger", Level.FATAL);

        Level level = ReflectionTestUtils.invokeMethod(YukonLogManager.class, "getLevel", loggers,
                "com.cannontech.web.filter.TokenAuthenticationAndLoggingFilter");
        assertTrue(level == Level.INFO, "Must be true");

        level = ReflectionTestUtils.invokeMethod(YukonLogManager.class, "getLevel", loggers,
                "com.cannontech.services.smartNotification");
        assertTrue(level == Level.DEBUG, "Must be true");

        level = ReflectionTestUtils.invokeMethod(YukonLogManager.class, "getLevel", loggers,
                "com.cannontech.services.smartNotification.a.b.c");
        assertTrue(level == Level.DEBUG, "Must be true");

        level = ReflectionTestUtils.invokeMethod(YukonLogManager.class, "getLevel", loggers,
                "com.cannontech.common.smartNotification");
        assertTrue(level == Level.DEBUG, "Must be true");

        level = ReflectionTestUtils.invokeMethod(YukonLogManager.class, "getLevel", loggers,
                "com.cannontech.common.smartNotification.a.b.c");
        assertTrue(level == Level.DEBUG, "Must be true");

        level = ReflectionTestUtils.invokeMethod(YukonLogManager.class, "getLevel", loggers, "com.cannontech.dr.rfn");
        assertTrue(level == Level.ERROR, "Must be true");

        level = ReflectionTestUtils.invokeMethod(YukonLogManager.class, "getLevel", loggers, "com.cannontech.dr.rfn.a.b.c");
        assertTrue(level == Level.ERROR, "Must be true");

        level = ReflectionTestUtils.invokeMethod(YukonLogManager.class, "getLevel", loggers, "com.cannontech.services.rfn");
        assertTrue(level == Level.ERROR, "Must be true");

        level = ReflectionTestUtils.invokeMethod(YukonLogManager.class, "getLevel", loggers, "com.cannontech.services.rfn.a.b.c");
        assertTrue(level == Level.ERROR, "Must be true");

        level = ReflectionTestUtils.invokeMethod(YukonLogManager.class, "getLevel", loggers,
                "com.cannontech.services.smartNotification.service.impl");
        assertTrue(level == Level.FATAL, "Must be true");

        level = ReflectionTestUtils.invokeMethod(YukonLogManager.class, "getLevel", loggers,
                "com.cannontech.services.smartNotification.service.impl.rfn.a.b.c");
        assertTrue(level == Level.FATAL, "Must be true");
    }
}
