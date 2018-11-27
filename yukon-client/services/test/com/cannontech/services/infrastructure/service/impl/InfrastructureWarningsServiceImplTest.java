package com.cannontech.services.infrastructure.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
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
import com.cannontech.infrastructure.model.InfrastructureWarningSeverity;
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
        
        List<InfrastructureWarning> oldWarnings = ImmutableList.of(warning1, warning2, warning3);
        List<InfrastructureWarning> newWarnings = ImmutableList.of(warning3, warning4, warning5, warning6);
        
        final IDatabaseCache databaseCache = new StubServerDatabaseCache() {
            @Override
            public Map<Integer, LiteYukonPAObject> getAllPaosMap() {
                Map<Integer, LiteYukonPAObject> allPaos = new HashMap<>();
                
                allPaos.put(1, pao1);
                allPaos.put(2, pao2);
                allPaos.put(3, pao3);
                allPaos.put(4, pao4);
                allPaos.put(5, pao5);
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
        
        Assert.assertEquals("Incorrect number of infrastructure warning event log entries", 3, eventLogWarnings.size());
        Assert.assertTrue("Event log warning for warning 4 is missing", eventLogWarnings.contains(warning4));
        Assert.assertTrue("Event log warning for warning 5 is missing", eventLogWarnings.contains(warning5));
        Assert.assertTrue("Event log warning for warning 6 is missing", eventLogWarnings.contains(warning6));
        
        // Test warnings smart notification logic
        List<InfrastructureWarning> smartNotificationWarnings = ReflectionTestUtils.invokeMethod(service, 
                "getSmartNotificationWarnings", oldWarnings, newWarnings);
        
        // Test warnings timestamp logic
        List<InfrastructureWarning> infrastructureWarnings =  ReflectionTestUtils.invokeMethod(service, 
                "getInfrastructureWarnings", oldWarnings, smartNotificationWarnings);
        Assert.assertEquals("Timestamp should not be updated for repeated infrastructure warnings", 
                            infrastructureWarnings.stream()
                                                  .filter(warning -> warning.getPaoIdentifier().getPaoId() == 2)
                                                  .findFirst()
                                                  .get()
                                                  .getTimestamp(),
                            now);
        
        List<SmartNotificationEvent> events = ReflectionTestUtils.invokeMethod(service, 
                "getNotificationEventsForNewWarnings", smartNotificationWarnings, now);
        
        // There should be 2 events for new Infrastructure Warnings
        Assert.assertEquals("Incorrect number of infrastructure warning smart notification events", 2, events.size());
        
        // Check that warning 4 is present with appropriate parameters
        SmartNotificationEvent device4event = new SmartNotificationEvent(now);
        device4event.addParameters(InfrastructureWarningsEventAssembler.PAO_ID, 4);
        device4event.addParameters(InfrastructureWarningsEventAssembler.WARNING_TYPE, InfrastructureWarningType.GATEWAY_COLOR);
        device4event.addParameters(InfrastructureWarningsEventAssembler.WARNING_SEVERITY, InfrastructureWarningSeverity.LOW);
        device4event.addParameters(InfrastructureWarningsEventAssembler.ARGUMENT_1, (short) 3);
        Assert.assertTrue("Event for device 4 is missing", events.contains(device4event));
        
        // Check that warning 5 is present with appropriate parameters
        SmartNotificationEvent device5event = new SmartNotificationEvent(now);
        device5event.addParameters(InfrastructureWarningsEventAssembler.PAO_ID, 5);
        device5event.addParameters(InfrastructureWarningsEventAssembler.WARNING_TYPE, InfrastructureWarningType.GATEWAY_COLOR);
        device5event.addParameters(InfrastructureWarningsEventAssembler.WARNING_SEVERITY, InfrastructureWarningSeverity.LOW);
        device5event.addParameters(InfrastructureWarningsEventAssembler.ARGUMENT_1, (short) 3);
        Assert.assertTrue("Event for device 5 is missing", events.contains(device5event));
    }

}
