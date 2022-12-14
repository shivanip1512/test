package com.cannontech.services.infrastructure.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.context.NoSuchMessageException;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.events.loggers.InfrastructureEventLogService;
import com.cannontech.common.i18n.Displayable;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.smartNotification.model.InfrastructureWarningsEventAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.services.mock.EventLogMockServiceFactory;
import com.cannontech.services.mock.StubServerDatabaseCache;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableList;

public class InfrastructureWarningsServiceImplTest {
    private final InfrastructureWarningsServiceImpl service = new InfrastructureWarningsServiceImpl();
    private final InfrastructureEventLogService warningsLogService = 
            EventLogMockServiceFactory.getEventLogMockService(InfrastructureEventLogService.class);
    
    @Test
    public void test_getNotificationEventsForNewWarnings() {
        final LiteYukonPAObject pao1 = new LiteYukonPAObject(1, "pao1", 
                                                             PaoCategory.DEVICE, 
                                                             PaoClass.RFMESH, 
                                                             PaoType.GWY800, 
                                                             "", "");
        final LiteYukonPAObject pao2 = new LiteYukonPAObject(2, "pao2", 
                                                             PaoCategory.DEVICE, 
                                                             PaoClass.RFMESH, 
                                                             PaoType.GWY800,
                                                             "", "");
        final LiteYukonPAObject pao3 = new LiteYukonPAObject(3, "pao3", 
                                                             PaoCategory.DEVICE, 
                                                             PaoClass.RFMESH, 
                                                             PaoType.GWY800,
                                                             "", "");
        final LiteYukonPAObject pao4 = new LiteYukonPAObject(4, "pao4", 
                                                             PaoCategory.DEVICE, 
                                                             PaoClass.RFMESH, 
                                                             PaoType.GWY800,
                                                             "", "");
        final LiteYukonPAObject pao5 = new LiteYukonPAObject(5, "pao5", 
                                                             PaoCategory.DEVICE, 
                                                             PaoClass.RFMESH, 
                                                             PaoType.GWY800,
                                                             "", "");
        final LiteYukonPAObject pao6 = new LiteYukonPAObject(6, "pao6",
                                                             PaoCategory.DEVICE,
                                                             PaoClass.RFMESH,
                                                             PaoType.VIRTUAL_GATEWAY,
                                                             "", "");
        final LiteYukonPAObject pao7 = new LiteYukonPAObject(7, "pao7",
                                                             PaoCategory.DEVICE,
                                                             PaoClass.RFMESH,
                                                             PaoType.VIRTUAL_GATEWAY,
                                                             "", "");
        final LiteYukonPAObject pao8 = new LiteYukonPAObject(8, "pao8",
                                                             PaoCategory.DEVICE,
                                                             PaoClass.RFMESH,
                                                             PaoType.VIRTUAL_GATEWAY,
                                                             "", "");
        final LiteYukonPAObject pao9 = new LiteYukonPAObject(9, "pao9",
                                                             PaoCategory.DEVICE,
                                                             PaoClass.RFMESH,
                                                             PaoType.VIRTUAL_GATEWAY,
                                                             "", "");
        final LiteYukonPAObject pao10 = new LiteYukonPAObject(10, "pao10",
                                                             PaoCategory.DEVICE,
                                                             PaoClass.RFMESH,
                                                             PaoType.VIRTUAL_GATEWAY,
                                                             "", "");
        
        Instant now = Instant.now();
        InfrastructureWarning warning1 = new InfrastructureWarning(pao1.getPaoIdentifier(),
                                                                   InfrastructureWarningType.GATEWAY_COLOR,
                                                                   now,
                                                                   (short) 2);
        InfrastructureWarning warning2 = new InfrastructureWarning(pao2.getPaoIdentifier(),
                                                                   InfrastructureWarningType.GATEWAY_COLOR,
                                                                   now,
                                                                   (short) 2);
        InfrastructureWarning warning3 = new InfrastructureWarning(pao3.getPaoIdentifier(),
                                                                   InfrastructureWarningType.GATEWAY_FAILSAFE,
                                                                   now);
        InfrastructureWarning warning4 = new InfrastructureWarning(pao4.getPaoIdentifier(),
                                                                   InfrastructureWarningType.GATEWAY_COLOR,
                                                                   now,
                                                                   (short) 3);
        InfrastructureWarning warning5 = new InfrastructureWarning(pao5.getPaoIdentifier(),
                                                                   InfrastructureWarningType.GATEWAY_COLOR,
                                                                   now,
                                                                   (short) 3);
        InfrastructureWarning warning6 = new InfrastructureWarning(pao2.getPaoIdentifier(),
                                                                   InfrastructureWarningType.GATEWAY_COLOR,
                                                                   new DateTime(now).plusMinutes(1).toInstant(),
                                                                   (short) 4);
        
        InfrastructureWarning warning7 = new InfrastructureWarning(pao6.getPaoIdentifier(),
                                                                   InfrastructureWarningType.GATEWAY_COLOR,
                                                                   now,
                                                                   (short) 2);
        InfrastructureWarning warning8 = new InfrastructureWarning(pao7.getPaoIdentifier(),
                                                                   InfrastructureWarningType.GATEWAY_COLOR,
                                                                   now,
                                                                   (short) 2);
        InfrastructureWarning warning9 = new InfrastructureWarning(pao8.getPaoIdentifier(),
                                                                   InfrastructureWarningType.GATEWAY_FAILSAFE,
                                                                   now);
        InfrastructureWarning warning10 = new InfrastructureWarning(pao9.getPaoIdentifier(),
                                                                   InfrastructureWarningType.GATEWAY_COLOR,
                                                                   now,
                                                                   (short) 3);
        InfrastructureWarning warning11 = new InfrastructureWarning(pao10.getPaoIdentifier(),
                                                                   InfrastructureWarningType.GATEWAY_COLOR,
                                                                   now,
                                                                   (short) 3);
        InfrastructureWarning warning12 = new InfrastructureWarning(pao7.getPaoIdentifier(),
                                                                   InfrastructureWarningType.GATEWAY_COLOR,
                                                                   new DateTime(now).plusMinutes(1).toInstant(),
                                                                   (short) 4);

        List<InfrastructureWarning> oldWarnings = ImmutableList.of(warning1, warning2, warning3, warning7, warning8, warning9);
        List<InfrastructureWarning> newWarnings = ImmutableList.of(warning3, warning4, warning5, warning6, warning9, warning10, warning11 ,warning12);
        
        final IDatabaseCache databaseCache = new StubServerDatabaseCache() {
            @Override
            public Map<Integer, LiteYukonPAObject> getAllPaosMap() {
                Map<Integer, LiteYukonPAObject> allPaos = new HashMap<>();
                
                allPaos.put(1, pao1);
                allPaos.put(2, pao2);
                allPaos.put(3, pao3);
                allPaos.put(4, pao4);
                allPaos.put(5, pao5);
                allPaos.put(6, pao1);
                allPaos.put(7, pao2);
                allPaos.put(8, pao3);
                allPaos.put(9, pao4);
                allPaos.put(10, pao5);
                return allPaos;
            }
        };
        final MessageSourceAccessor systemMessageSourceAccessor = new MessageSourceAccessor(null, null) {
            @Override
            public String getMessage(Displayable displayable) throws NoSuchMessageException {
                return "";
            };
        };
        ReflectionTestUtils.setField(service, "infrastructureEventLogService", warningsLogService);
        ReflectionTestUtils.setField(service, "systemMessageSourceAccessor", systemMessageSourceAccessor);
        ReflectionTestUtils.setField(service, "serverDatabaseCache", databaseCache);
        ReflectionTestUtils.setField(service, "infrastructureEventLogService", warningsLogService);

        // Test Infrastructure Warnings event log logic
        List<InfrastructureWarning> eventLogWarnings = ReflectionTestUtils.invokeMethod(service,
                "getAndLogEventLogWarnings", oldWarnings, newWarnings);

        assertEquals(6, eventLogWarnings.size(), "Incorrect number of infrastructure warning event log entries");
        assertTrue(eventLogWarnings.contains(warning4), "Event log warning for warning 4 is missing");
        assertTrue(eventLogWarnings.contains(warning5), "Event log warning for warning 5 is missing");
        assertTrue(eventLogWarnings.contains(warning6), "Event log warning for warning 6 is missing");
        assertTrue(eventLogWarnings.contains(warning10), "Event log warning for warning 10 is missing");
        assertTrue(eventLogWarnings.contains(warning11), "Event log warning for warning 11 is missing");
        assertTrue(eventLogWarnings.contains(warning12), "Event log warning for warning 12 is missing");

        // Test warnings smart notification logic
        List<InfrastructureWarning> smartNotificationWarnings = ReflectionTestUtils.invokeMethod(service,
                "getSmartNotifications", oldWarnings, newWarnings);

        // Test warnings timestamp logic
        List<InfrastructureWarning> infrastructureWarnings = ReflectionTestUtils.invokeMethod(service,
                "getInfrastructureWarnings", oldWarnings, smartNotificationWarnings);

        assertEquals(infrastructureWarnings.stream()
                .filter(warning -> warning.getPaoIdentifier().getPaoId() == 4)
                .findFirst()
                .get()
                .getTimestamp(), now, "Timestamp should not be updated for repeated infrastructure warnings");

        List<SmartNotificationEvent> events = ReflectionTestUtils.invokeMethod(service,
                "getNotificationEventsForNewWarnings", smartNotificationWarnings, now);

        // There should be 2 events for new Infrastructure Warnings
        assertEquals(4, events.size(), "Incorrect number of infrastructure warning smart notification events");

        // Check that warning 4 is present with appropriate parameters
        SmartNotificationEvent device4event = InfrastructureWarningsEventAssembler.assemble(now, warning4);
        assertTrue(events.contains(device4event), "Event for device 4 is missing");

        // Check that warning 5 is present with appropriate parameters
        SmartNotificationEvent device5event = InfrastructureWarningsEventAssembler.assemble(now, warning5);
        assertTrue(events.contains(device5event), "Event for device 5 is missing");

        // Check that warning 9 is present with appropriate parameters
        SmartNotificationEvent device9event = InfrastructureWarningsEventAssembler.assemble(now, warning10);
        assertTrue(events.contains(device9event), "Event for device 9 is missing");

        // Check that warning 10 is present with appropriate parameters
        SmartNotificationEvent device10event = InfrastructureWarningsEventAssembler.assemble(now, warning11);
        assertTrue(events.contains(device10event), "Event for device 10 is missing");
        
    }

}
