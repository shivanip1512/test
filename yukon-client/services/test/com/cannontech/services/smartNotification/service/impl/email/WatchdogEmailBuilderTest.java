package com.cannontech.services.smartNotification.service.impl.email;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationVerbosity;
import com.cannontech.services.smartNotification.service.WatchdogEmailFormatHandler;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.Watchdogs;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class WatchdogEmailBuilderTest {

    private WatchdogEmailBuilder watchdogEmailBuilder = null;
    private Map<Watchdogs, WatchdogEmailFormatHandler> emailBuilderHandler = Maps.newHashMap();


    @Before
    public void setUp() throws Exception {
        ServiceStatusWatchdogEmailFormatHandler handler = new ServiceStatusWatchdogEmailFormatHandler();
        List<WatchdogEmailFormatHandler> watchTypeEmailHandlers = Lists.newArrayList();
        watchTypeEmailHandlers.add(handler);
        watchdogEmailBuilder = new WatchdogEmailBuilder(watchTypeEmailHandlers);
        emailBuilderHandler.put(Watchdogs.SERVICE_STATUS, new ServiceStatusWatchdogEmailFormatHandler());
        ReflectionTestUtils.setField(watchdogEmailBuilder, "emailBuilderHandler", emailBuilderHandler);
    }

    @Test
    public void test_getSubjectArguments_count() {
        SmartNotificationEvent smartNotificationEvent = new SmartNotificationEvent(12, Instant.now());
        Map<String, Object> parameter = Maps.newHashMap();
        parameter.put("WarningType", "YUKON_WEB_APPLICATION_SERVICE");
        parameter.put("Status", "STOPPED");
        smartNotificationEvent.setParameters(parameter);
        List<SmartNotificationEvent> event = new ArrayList<>();
        event.add(smartNotificationEvent);
        Object[] subjectArguments = ReflectionTestUtils.invokeMethod(watchdogEmailBuilder, "getSubjectArguments",
            event, SmartNotificationVerbosity.SUMMARY);
        assertThat(subjectArguments.length, equalTo(1));
    }

    @Test
    public void test_getSubjectArguments_subject_text() {
        SmartNotificationEvent smartNotificationEvent = new SmartNotificationEvent(123, Instant.now());
        Map<String, Object> parameter = Maps.newHashMap();
        parameter.put("WarningType", "YUKON_LOAD_MANAGEMENT_SERVICE");
        parameter.put("Status", "STOPPED");
        smartNotificationEvent.setParameters(parameter);
        List<SmartNotificationEvent> event = new ArrayList<>();
        event.add(smartNotificationEvent);
        Object[] subjectArguments = ReflectionTestUtils.invokeMethod(watchdogEmailBuilder, "getSubjectArguments",
            event, SmartNotificationVerbosity.SUMMARY);
        assertThat(subjectArguments[0], equalTo("Yukon Load Management Service"));
    }

    @Test
    public void test_getSubjectArgumentsMultipleEvents() {
        SmartNotificationEvent smartNotificationEvent1 = new SmartNotificationEvent(124, Instant.now());
        Map<String, Object> parameter1 = Maps.newHashMap();
        parameter1.put("WarningType", "YUKON_WEB_APPLICATION_SERVICE");
        parameter1.put("Status", "STOPPED");
        smartNotificationEvent1.setParameters(parameter1);
        SmartNotificationEvent smartNotificationEvent2 = new SmartNotificationEvent(125, Instant.now());
        Map<String, Object> parameter2 = Maps.newHashMap();
        parameter2.put("WarningType", "YUKON_CAP_CONTROL_SERVICE");
        parameter2.put("Status", "STOPPED");
        smartNotificationEvent2.setParameters(parameter2);
        List<SmartNotificationEvent> events = new ArrayList<>();
        events.add(smartNotificationEvent1);
        events.add(smartNotificationEvent2);
        Object[] subjectArguments = ReflectionTestUtils.invokeMethod(watchdogEmailBuilder, "getSubjectArguments",
            events, SmartNotificationVerbosity.SUMMARY);
        assertThat(subjectArguments[0], equalTo(2));
    }

    @Test
    public void test_getWatchdogEmailFormatHandler() {
        WatchdogEmailFormatHandler handler = ReflectionTestUtils.invokeMethod(watchdogEmailBuilder,
            "getWatchdogEmailFormatHandler", WatchdogWarningType.YUKON_WEB_APPLICATION_SERVICE);
        assertNotNull(handler);
        assertTrue(handler.getClass() == ServiceStatusWatchdogEmailFormatHandler.class);
    }

}
