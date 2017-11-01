package com.cannontech.services.infrastructure.service.impl;

import java.util.List;

import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.smartNotification.model.InfrastructureWarningsParametersAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningSeverity;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.google.common.collect.ImmutableList;

public class InfrastructureWarningsServiceImplTest {
    private final InfrastructureWarningsServiceImpl service = new InfrastructureWarningsServiceImpl();
    
    @Test
    public void test_getNotificationEventsForNewWarnings() {
        InfrastructureWarning warning1 = new InfrastructureWarning(PaoIdentifier.of(1, PaoType.GWY800),
                                                                   InfrastructureWarningType.GATEWAY_COLOR,
                                                                   (short) 2);
        InfrastructureWarning warning2 = new InfrastructureWarning(PaoIdentifier.of(2, PaoType.GWY800),
                                                                   InfrastructureWarningType.GATEWAY_COLOR,
                                                                   (short) 2);
        InfrastructureWarning warning3 = new InfrastructureWarning(PaoIdentifier.of(3, PaoType.GWY800),
                                                                   InfrastructureWarningType.GATEWAY_FAILSAFE);
        InfrastructureWarning warning4 = new InfrastructureWarning(PaoIdentifier.of(4, PaoType.GWY800),
                                                                   InfrastructureWarningType.GATEWAY_COLOR,
                                                                   (short) 3);
        InfrastructureWarning warning5 = new InfrastructureWarning(PaoIdentifier.of(5, PaoType.GWY800),
                                                                   InfrastructureWarningType.GATEWAY_COLOR,
                                                                   (short) 3);
        
        List<InfrastructureWarning> oldWarnings = ImmutableList.of(warning1, warning2, warning3);
        List<InfrastructureWarning> newWarnings = ImmutableList.of(warning3, warning4, warning5);
        Instant now = Instant.now();
        
        List<SmartNotificationEvent> events = ReflectionTestUtils.invokeMethod(service, 
                "getNotificationEventsForNewWarnings", oldWarnings, newWarnings, now);
        
        //There should be 2 events for new Infrastructure Warnings
        Assert.assertEquals("Incorrect number of infrastructure warning events", 2, events.size());
        
        // Check that warning 4 is present with appropriate parameters
        SmartNotificationEvent device4event = new SmartNotificationEvent(now);
        device4event.addParameters(InfrastructureWarningsParametersAssembler.PAO_ID, 4);
        device4event.addParameters(InfrastructureWarningsParametersAssembler.WARNING_TYPE, InfrastructureWarningType.GATEWAY_COLOR);
        device4event.addParameters(InfrastructureWarningsParametersAssembler.WARNING_SEVERITY, InfrastructureWarningSeverity.LOW);
        device4event.addParameters(InfrastructureWarningsParametersAssembler.ARGUMENT_1, (short) 3);
        Assert.assertTrue("Event for device 4 is missing", events.contains(device4event));
        
        // Check that warning 4 is present with appropriate parameters
        SmartNotificationEvent device5event = new SmartNotificationEvent(now);
        device5event.addParameters(InfrastructureWarningsParametersAssembler.PAO_ID, 5);
        device5event.addParameters(InfrastructureWarningsParametersAssembler.WARNING_TYPE, InfrastructureWarningType.GATEWAY_COLOR);
        device5event.addParameters(InfrastructureWarningsParametersAssembler.WARNING_SEVERITY, InfrastructureWarningSeverity.LOW);
        device5event.addParameters(InfrastructureWarningsParametersAssembler.ARGUMENT_1, (short) 3);
        Assert.assertTrue("Event for device 5 is missing", events.contains(device5event));
    }
    
}
