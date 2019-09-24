package com.cannontech.services.rfn.endpoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.easymock.Capture;
import org.easymock.CaptureType;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.amr.rfn.message.status.RfnStatusArchiveRequest;
import com.cannontech.amr.rfn.message.status.type.MeterDisconnectStatus;
import com.cannontech.amr.rfn.message.status.type.MeterInfo;
import com.cannontech.amr.rfn.message.status.type.MeterInfoStatus;
import com.cannontech.amr.rfn.message.status.type.RfnMeterDisconnectMeterMode;
import com.cannontech.amr.rfn.message.status.type.RfnMeterDisconnectStateType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;
import com.cannontech.message.dispatch.message.PointData;

@RunWith(Parameterized.class)
public class RfnStatusArchiveRequestListenerTest {
    private RfnStatusArchiveRequestListener statusListener;
    private Capture<PointData> pointdataCapture = new Capture<>(CaptureType.ALL);
    private static final long timestamp = 0;
    private static final RfnIdentifier rfnIdentifier = new RfnIdentifier("112358", "ITRN", "C2SX-SD");
    private static final LitePoint disconnectStatusPoint = new LitePoint(176827, "Disconnect status point", PointType.Status.getPointTypeId(), 71, 77, -18);
    
    private static final Map<RfnMeterDisconnectMeterMode, Map<RfnMeterDisconnectStateType, Optional<Integer>>> expectedModeStates = getExpectedModeStates();
    
    @Parameter(0)
    public RfnMeterDisconnectMeterMode mode;
    @Parameter(1)
    public RfnMeterDisconnectStateType type;
    
    @Parameters(name="mode: {0} {1}")
    public static Collection<Object[]> input() {
        Comparator<Enum<?>> sortByName = (a, b) -> a.name().compareTo(b.name());
        //  Get all combinations of DisconnectMeterMode and DisconnectStateType values
        return Arrays.stream(RfnMeterDisconnectMeterMode.values())
                     .sorted(sortByName)
                     .flatMap(mode -> Arrays.stream(RfnMeterDisconnectStateType.values())
                                            .sorted(sortByName)
                                            .map(type -> new Object[] {mode, type}))
                     .collect(Collectors.toList());
    }
    
    @Before
    public void init() {
        statusListener = new RfnStatusArchiveRequestListener();
        
        var jmsTemplate = EasyMock.createNiceMock(JmsTemplate.class);
        ReflectionTestUtils.setField(statusListener, "jmsTemplate", jmsTemplate, JmsTemplate.class);

        var rfnDeviceLookupService = EasyMock.createNiceMock(RfnDeviceLookupService.class);
        ReflectionTestUtils.setField(statusListener, "rfnDeviceLookupService", rfnDeviceLookupService, RfnDeviceLookupService.class);
        
        var attributeService = EasyMock.createStrictMock(AttributeService.class);
        EasyMock.expect(attributeService.createAndFindPointForAttribute(null, BuiltInAttribute.DISCONNECT_STATUS))
            .andReturn(disconnectStatusPoint);
        EasyMock.replay(attributeService);
        ReflectionTestUtils.setField(statusListener, "attributeService", attributeService, AttributeService.class);
        
        var asyncDynamicDataSource = EasyMock.createStrictMock(AsyncDynamicDataSource.class);
        asyncDynamicDataSource.putValue(EasyMock.capture(pointdataCapture));
        EasyMock.replay(asyncDynamicDataSource);
        ReflectionTestUtils.setField(statusListener, "asyncDynamicDataSource", asyncDynamicDataSource, AsyncDynamicDataSource.class);
    }
    
    @Test
    public void testRfnMeterDisconnectState() {
        
        var expectedStates = expectedModeStates.get(mode);
        
        assertNotNull("No expected states for " + mode, expectedStates);
        
        var expectedState = expectedStates.get(type);
        
        assertNotNull("Missing state for " + mode + " " + type, expectedState);
        
        // Build the RfnStatusArchiveRequest message with MeterDisconnectStatus inside
        var disconnectStatus = new MeterDisconnectStatus();
        disconnectStatus.setMeterMode(mode);
        disconnectStatus.setRelayStatus(type);

        var info = new MeterInfo();
        info.setMeterDisconnectStatus(disconnectStatus);
        
        var status = new MeterInfoStatus();
        status.setData(info);
        status.setTimeStamp(timestamp);
        status.setRfnIdentifier(rfnIdentifier);
        
        var request = new RfnStatusArchiveRequest();
        request.setStatus(status);
        
        // Process the request, capturing any pointdata into pointdataCapture
        statusListener.process(request, "jimmy");
        
        // Verify the pointdata value
        expectedState.ifPresentOrElse(value -> {
            assertFalse("No pointdata generated", pointdataCapture.getValues().isEmpty());
            assertEquals("Excess pointdata generated", 1, pointdataCapture.getValues().size());
            
            var pointData = pointdataCapture.getValue();

            assertEquals(value, pointData.getValue(), 0.0);
        }, () -> 
            assertTrue(pointdataCapture.getValues().isEmpty()));
    }

    /**
     * Helper class to hold tuples of mode/type/value
     */
    private static class ModeStateValue {
        public RfnMeterDisconnectMeterMode mode;
        public RfnMeterDisconnectStateType type;
        public Integer value;

        public static ModeStateValue of(RfnMeterDisconnectMeterMode mode, RfnMeterDisconnectStateType type,  Integer value) {
            var msv = new ModeStateValue();
            msv.mode = mode;
            msv.type = type;
            msv.value = value;
            return msv;
        }
    }
    
    private static Map<RfnMeterDisconnectMeterMode, Map<RfnMeterDisconnectStateType, Optional<Integer>>> getExpectedModeStates() {
        return Stream.of(
              ModeStateValue.of(RfnMeterDisconnectMeterMode.ARM, RfnMeterDisconnectStateType.ARMED, 3),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.ARM, RfnMeterDisconnectStateType.RESUMED, null),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.ARM, RfnMeterDisconnectStateType.TERMINATED, null),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.ARM, RfnMeterDisconnectStateType.UNKNOWN, null),
              
              ModeStateValue.of(RfnMeterDisconnectMeterMode.CYCLING_ACTIVATE, RfnMeterDisconnectStateType.ARMED, null),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.CYCLING_ACTIVATE, RfnMeterDisconnectStateType.RESUMED, 7),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.CYCLING_ACTIVATE, RfnMeterDisconnectStateType.TERMINATED, 6),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.CYCLING_ACTIVATE, RfnMeterDisconnectStateType.UNKNOWN, null),
              
              ModeStateValue.of(RfnMeterDisconnectMeterMode.CYCLING_CONFIGURATION, RfnMeterDisconnectStateType.ARMED, null),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.CYCLING_CONFIGURATION, RfnMeterDisconnectStateType.RESUMED, null),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.CYCLING_CONFIGURATION, RfnMeterDisconnectStateType.TERMINATED, null),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.CYCLING_CONFIGURATION, RfnMeterDisconnectStateType.UNKNOWN, null),
              
              ModeStateValue.of(RfnMeterDisconnectMeterMode.CYCLING_DEACTIVATE, RfnMeterDisconnectStateType.ARMED, 1),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.CYCLING_DEACTIVATE, RfnMeterDisconnectStateType.RESUMED, 1),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.CYCLING_DEACTIVATE, RfnMeterDisconnectStateType.TERMINATED, 1),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.CYCLING_DEACTIVATE, RfnMeterDisconnectStateType.UNKNOWN, 1),
              
              ModeStateValue.of(RfnMeterDisconnectMeterMode.DEMAND_THRESHOLD_ACTIVATE, RfnMeterDisconnectStateType.ARMED, 4),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.DEMAND_THRESHOLD_ACTIVATE, RfnMeterDisconnectStateType.RESUMED, 5),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.DEMAND_THRESHOLD_ACTIVATE, RfnMeterDisconnectStateType.TERMINATED, 4),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.DEMAND_THRESHOLD_ACTIVATE, RfnMeterDisconnectStateType.UNKNOWN, null),
              
              ModeStateValue.of(RfnMeterDisconnectMeterMode.DEMAND_THRESHOLD_CONFIGURATION, RfnMeterDisconnectStateType.ARMED, null),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.DEMAND_THRESHOLD_CONFIGURATION, RfnMeterDisconnectStateType.RESUMED, null),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.DEMAND_THRESHOLD_CONFIGURATION, RfnMeterDisconnectStateType.TERMINATED, null),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.DEMAND_THRESHOLD_CONFIGURATION, RfnMeterDisconnectStateType.UNKNOWN, null),
              
              ModeStateValue.of(RfnMeterDisconnectMeterMode.DEMAND_THRESHOLD_DEACTIVATE, RfnMeterDisconnectStateType.ARMED, 1),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.DEMAND_THRESHOLD_DEACTIVATE, RfnMeterDisconnectStateType.RESUMED, 1),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.DEMAND_THRESHOLD_DEACTIVATE, RfnMeterDisconnectStateType.TERMINATED, 1),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.DEMAND_THRESHOLD_DEACTIVATE, RfnMeterDisconnectStateType.UNKNOWN, 1),
              
              ModeStateValue.of(RfnMeterDisconnectMeterMode.ON_DEMAND_CONFIGURATION, RfnMeterDisconnectStateType.ARMED, 3),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.ON_DEMAND_CONFIGURATION, RfnMeterDisconnectStateType.RESUMED, 1),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.ON_DEMAND_CONFIGURATION, RfnMeterDisconnectStateType.TERMINATED, 2),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.ON_DEMAND_CONFIGURATION, RfnMeterDisconnectStateType.UNKNOWN, null),
              
              ModeStateValue.of(RfnMeterDisconnectMeterMode.RESUME, RfnMeterDisconnectStateType.ARMED, null),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.RESUME, RfnMeterDisconnectStateType.RESUMED, 1),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.RESUME, RfnMeterDisconnectStateType.TERMINATED, null),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.RESUME, RfnMeterDisconnectStateType.UNKNOWN, null),
              
              ModeStateValue.of(RfnMeterDisconnectMeterMode.TERMINATE, RfnMeterDisconnectStateType.ARMED, null),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.TERMINATE, RfnMeterDisconnectStateType.RESUMED, null),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.TERMINATE, RfnMeterDisconnectStateType.TERMINATED, 2),
              ModeStateValue.of(RfnMeterDisconnectMeterMode.TERMINATE, RfnMeterDisconnectStateType.UNKNOWN, null))
                 .collect(Collectors.groupingBy(msv -> msv.mode, 
                          Collectors.toMap(msv -> msv.type, 
                                           msv -> Optional.ofNullable(msv.value))));
    }
}