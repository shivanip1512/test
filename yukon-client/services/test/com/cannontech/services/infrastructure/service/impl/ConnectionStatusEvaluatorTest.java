package com.cannontech.services.infrastructure.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.AbstractMap;
import java.util.Date;
import java.util.Map;

import org.easymock.EasyMock;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.point.stategroup.CommStatusState;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningSeverity;
import com.cannontech.infrastructure.model.InfrastructureWarningType;

public class ConnectionStatusEvaluatorTest {
    private static final Duration sixtyMinutes = Duration.standardMinutes(60);
    private static final String baseTimestamp = "2020-06-24T12:00:00";
    private static final Instant baseTimestampInstant = Instant.parse(baseTimestamp);
    private static final String formattedDisconnectTimeString = "06/24/2020 10:59:00"; //61m prior to base time
    
    private static Date minutesAgo(int minutes) {
        return Instant.parse(baseTimestamp)
               .minus(Duration.standardMinutes(minutes))
               .toDate();
    }
    
    private static RawPointHistoryDao setupMockRphDao(PointValueQualityHolder connectedPointValue, 
            PointValueQualityHolder nextDisconnectedPointValue) {
        
        RawPointHistoryDao mockRphDao = EasyMock.createNiceMock(RawPointHistoryDao.class);
        RawPointHistoryDao.AdjacentPointValues adjacentPointValues = 
                new RawPointHistoryDao.AdjacentPointValues(null, nextDisconnectedPointValue);
        EasyMock.expect(mockRphDao.getAdjacentPointValues(connectedPointValue))
                .andReturn(adjacentPointValues);
        EasyMock.replay(mockRphDao);
        
        return mockRphDao;
    }
    
    private static PointValueQualityHolder fakePointValue(Date timestamp, double value) {
        return PointValueBuilder.create()
                .withTimeStamp(timestamp)
                .withValue(value)
                .withPointId(1)
                .withPointQuality(PointQuality.Normal)
                .withType(PointType.Status)
                .build();
    }
    
    @Test
    public void testGatewayNoConnectedStatus() {
        
        // Set up mocks so there's no CONNECTED status
        
        PaoIdentifier gatewayPaoId = new PaoIdentifier(1, PaoType.GWY800);
        
        // Build the connection status info, with warning duration of 60 minutes and no CONNECTED value
        
        GatewayConnectionStatusEvaluator evaluator = new GatewayConnectionStatusEvaluator();
        
        Map.Entry<PaoIdentifier, PointValueQualityHolder> gatewayConnectionStatusEntry = 
                new AbstractMap.SimpleEntry<>(gatewayPaoId, null);
        
        ConnectionStatusInfo connectionStatusInfo = ReflectionTestUtils.invokeMethod(evaluator, "buildConnectionStatusInfo", gatewayConnectionStatusEntry, baseTimestampInstant, sixtyMinutes);
        
        // Check for correct logic on whether to warn or not, based on point value timestamps
        
        assertFalse(connectionStatusInfo.isLastConnectedTimestampWarnable(),
                "ConnectionStatusInfo should not have connected timestamp warnable, "
                        + "with no connected timestamp");

        assertFalse(connectionStatusInfo.isWarnable(), "ConnectionStatusInfo should not be warnable with no connected timestamp");
    }
    
    @Test
    public void testRelayNoConnectedStatus() {
        
        // Set up mocks so there's no CONNECTED status
        
        PaoIdentifier gatewayPaoId = new PaoIdentifier(1, PaoType.GWY800);
        
        // Build the connection status info, with warning duration of 60 minutes and no CONNECTED value
        
        CellularDeviceConnectionStatusEvaluator evaluator = new CellularDeviceConnectionStatusEvaluator();
        
        Map.Entry<PaoIdentifier, PointValueQualityHolder> relayConnectionStatusEntry = 
                new AbstractMap.SimpleEntry<>(gatewayPaoId, null);
        
        ConnectionStatusInfo connectionStatusInfo = ReflectionTestUtils.invokeMethod(evaluator, "buildConnectionStatusInfo", relayConnectionStatusEntry, baseTimestampInstant, sixtyMinutes);
        
        // Check for correct logic on whether to warn or not, based on point value timestamps
        
        assertFalse(connectionStatusInfo.isLastConnectedTimestampWarnable(),
                "ConnectionStatusInfo should not have connected timestamp warnable, "
                        + "with no connected timestamp");

        assertFalse(connectionStatusInfo.isWarnable(), "ConnectionStatusInfo should not be warnable with no connected timestamp");
    }
    
    @Test
    public void testGatewayConnectedStatusWithNoDisconnectedStatus() {
        
        // Set up mocks so that last CONNECTED status was 59 minutes ago, and no last DISCONNECTED status
        
        PaoIdentifier gatewayPaoId = new PaoIdentifier(1, PaoType.GWY800);
        
        Date fiftyNineMinutesAgo = minutesAgo(59);
        PointValueQualityHolder connectedPointValue = 
                fakePointValue(fiftyNineMinutesAgo, CommStatusState.CONNECTED.getRawState());
        
        RawPointHistoryDao mockRphDao = EasyMock.createNiceMock(RawPointHistoryDao.class);
        RawPointHistoryDao.AdjacentPointValues adjacentPointValues = 
                new RawPointHistoryDao.AdjacentPointValues(null, null);
        EasyMock.expect(mockRphDao.getAdjacentPointValues(connectedPointValue))
                .andReturn(adjacentPointValues);
        EasyMock.replay(mockRphDao);
        
        // Build the connection status info, with warning duration of 60 minutes
        
        GatewayConnectionStatusEvaluator evaluator = new GatewayConnectionStatusEvaluator();
        ReflectionTestUtils.setField(evaluator, "rphDao", mockRphDao);
        
        Map.Entry<PaoIdentifier, PointValueQualityHolder> gatewayConnectionStatusEntry = 
                Map.entry(gatewayPaoId, connectedPointValue);
        
        ConnectionStatusInfo connectionStatusInfo = ReflectionTestUtils.invokeMethod(evaluator, "buildConnectionStatusInfo", gatewayConnectionStatusEntry, baseTimestampInstant, sixtyMinutes);
        
        // Check for correct logic on whether to warn or not, based on point value timestamps
        
        assertFalse(connectionStatusInfo.isLastConnectedTimestampWarnable(),
                "ConnectionStatusInfo considers last connected timestamp warnable, "
                        + "but timestamp isn't outside the warnable duration");

        assertFalse(connectionStatusInfo.isWarnable(), "ConnectionStatusInfo marked as warnable, "
                + "but there is no DISCONNECTED status");
    }
    
    @Test
    public void testRelayConnectedStatusWithNoDisconnectedStatus() {
        
        // Set up mocks so that last CONNECTED status was 59 minutes ago, and no last DISCONNECTED status
        
        PaoIdentifier relayPaoId = new PaoIdentifier(1, PaoType.CRLY856);
        
        Date fiftyNineMinutesAgo = minutesAgo(59);
        PointValueQualityHolder connectedPointValue = 
                fakePointValue(fiftyNineMinutesAgo, CommStatusState.CONNECTED.getRawState());
        
        RawPointHistoryDao mockRphDao = EasyMock.createNiceMock(RawPointHistoryDao.class);
        RawPointHistoryDao.AdjacentPointValues adjacentPointValues = 
                new RawPointHistoryDao.AdjacentPointValues(null, null);
        EasyMock.expect(mockRphDao.getAdjacentPointValues(connectedPointValue))
                .andReturn(adjacentPointValues);
        EasyMock.replay(mockRphDao);
        
        // Build the connection status info, with warning duration of 60 minutes
        
        CellularDeviceConnectionStatusEvaluator evaluator = new CellularDeviceConnectionStatusEvaluator();
        ReflectionTestUtils.setField(evaluator, "rphDao", mockRphDao);
        
        Map.Entry<PaoIdentifier, PointValueQualityHolder> relayConnectionStatusEntry = 
                Map.entry(relayPaoId, connectedPointValue);
        
        ConnectionStatusInfo connectionStatusInfo = ReflectionTestUtils.invokeMethod(evaluator, "buildConnectionStatusInfo", relayConnectionStatusEntry, baseTimestampInstant, sixtyMinutes);
        
        // Check for correct logic on whether to warn or not, based on point value timestamps
        
        assertFalse(connectionStatusInfo.isLastConnectedTimestampWarnable(),
                "ConnectionStatusInfo considers last connected timestamp warnable, "
                        + "but timestamp isn't outside the warnable duration");

        assertFalse(connectionStatusInfo.isWarnable(), "ConnectionStatusInfo marked as warnable, "
                + "but there is no DISCONNECTED status");
    }
    
    @Test
    public void testGatewayConnectedStatusWithinWarningDuration() {
        
        // Set up mocks so that last CONNECTED status was 59 minutes ago, and last DISCONNECTED status was 1 minute ago.
        
        PaoIdentifier gatewayPaoId = new PaoIdentifier(1, PaoType.GWY800);
        
        Date fiftyNineMinutesAgo = minutesAgo(59);
        PointValueQualityHolder connectedPointValue = 
                fakePointValue(fiftyNineMinutesAgo, CommStatusState.CONNECTED.getRawState());
        
        Date oneMinuteAgo = minutesAgo(1);
        PointValueQualityHolder nextDisconnectedPointValue = 
                fakePointValue(oneMinuteAgo, CommStatusState.DISCONNECTED.getRawState());
        
        RawPointHistoryDao mockRphDao = setupMockRphDao(connectedPointValue, nextDisconnectedPointValue);
        
        // Build the connection status info, with warning duration of 60 minutes
        
        GatewayConnectionStatusEvaluator evaluator = new GatewayConnectionStatusEvaluator();
        ReflectionTestUtils.setField(evaluator, "rphDao", mockRphDao);
        
        Map.Entry<PaoIdentifier, PointValueQualityHolder> gatewayConnectionStatusEntry = 
                Map.entry(gatewayPaoId, connectedPointValue);
        
        ConnectionStatusInfo connectionStatusInfo = ReflectionTestUtils.invokeMethod(evaluator, "buildConnectionStatusInfo", gatewayConnectionStatusEntry, baseTimestampInstant, sixtyMinutes);
        
        // Check for correct logic on whether to warn or not, based on point value timestamps
        
        assertFalse(connectionStatusInfo.isLastConnectedTimestampWarnable(),
                "ConnectionStatusInfo considers last connected timestamp warnable, "
                        + "but timestamp isn't outside the warnable duration");

        assertFalse(connectionStatusInfo.isWarnable(), "ConnectionStatusInfo marked as warnable, "
                + "but CONNECTED and DISCONNECTED timestamps aren't outside the warnable duration");
    }
    
    @Test
    public void testRelayConnectedStatusWithinWarningDuration() {
        
        // Set up mocks so that last CONNECTED status was 59 minutes ago, and last DISCONNECTED status was 1 minute ago.
        
        PaoIdentifier relayPaoId = new PaoIdentifier(1, PaoType.CRLY856);
        
        Date fiftyNineMinutesAgo = minutesAgo(59);
        PointValueQualityHolder connectedPointValue = 
                fakePointValue(fiftyNineMinutesAgo, CommStatusState.CONNECTED.getRawState());
        
        Date oneMinuteAgo = minutesAgo(1);
        PointValueQualityHolder nextDisconnectedPointValue = 
                fakePointValue(oneMinuteAgo, CommStatusState.DISCONNECTED.getRawState());
        
        RawPointHistoryDao mockRphDao = setupMockRphDao(connectedPointValue, nextDisconnectedPointValue);
        
        // Build the connection status info, with warning duration of 60 minutes
        
        CellularDeviceConnectionStatusEvaluator evaluator = new CellularDeviceConnectionStatusEvaluator();
        ReflectionTestUtils.setField(evaluator, "rphDao", mockRphDao);
        
        Map.Entry<PaoIdentifier, PointValueQualityHolder> relayConnectionStatusEntry = 
                Map.entry(relayPaoId, connectedPointValue);
        
        ConnectionStatusInfo connectionStatusInfo = ReflectionTestUtils.invokeMethod(evaluator, "buildConnectionStatusInfo", relayConnectionStatusEntry, baseTimestampInstant, sixtyMinutes);
        
        // Check for correct logic on whether to warn or not, based on point value timestamps
        
        assertFalse(connectionStatusInfo.isLastConnectedTimestampWarnable(),
                "ConnectionStatusInfo considers last connected timestamp warnable, "
                        + "but timestamp isn't outside the warnable duration");

        assertFalse(connectionStatusInfo.isWarnable(), "ConnectionStatusInfo marked as warnable, "
                + "but CONNECTED and DISCONNECTED timestamps aren't outside the warnable duration");
    }
    
    @Test
    public void testGatewayConnectedStatusOutsideAndDisconnectedStatusInsideWarningDuration() {
        
        // Set up mocks so that last CONNECTED status was 61 minutes ago, and last DISCONNECTED status was 59 minute ago.
        
        PaoIdentifier gatewayPaoId = new PaoIdentifier(1, PaoType.GWY800);
        
        Date sixtyOneMinutesAgo = minutesAgo(61);
        PointValueQualityHolder connectedPointValue = 
                fakePointValue(sixtyOneMinutesAgo, CommStatusState.CONNECTED.getRawState());
        
        Date fiftyNineMinutesAgo = minutesAgo(59);
        PointValueQualityHolder nextDisconnectedPointValue = 
                fakePointValue(fiftyNineMinutesAgo, CommStatusState.DISCONNECTED.getRawState());
        
        RawPointHistoryDao mockRphDao = setupMockRphDao(connectedPointValue, nextDisconnectedPointValue);
        
        // Build the connection status info, with warning duration of 60 minutes
        
        GatewayConnectionStatusEvaluator evaluator = new GatewayConnectionStatusEvaluator();
        ReflectionTestUtils.setField(evaluator, "rphDao", mockRphDao);
        
        Map.Entry<PaoIdentifier, PointValueQualityHolder> gatewayConnectionStatusEntry = 
                Map.entry(gatewayPaoId, connectedPointValue);
        
        ConnectionStatusInfo connectionStatusInfo = ReflectionTestUtils.invokeMethod(evaluator, "buildConnectionStatusInfo", gatewayConnectionStatusEntry, baseTimestampInstant, sixtyMinutes);
        
        // Check for correct logic on whether to warn or not, based on point value timestamps
        
        assertTrue(connectionStatusInfo.isLastConnectedTimestampWarnable(),
                "ConnectionStatusInfo doesn't consider last connected timestamp warnable, "
                        + "but timestamp is beyond the warnable duration");

        assertFalse(connectionStatusInfo.isWarnable(), "ConnectionStatusInfo marked as warnable, "
                + "but DISCONNECTED timestamp isn't outside the warnable duration");
    }
    
    @Test
    public void testRelayConnectedStatusOutsideAndDisconnectedStatusInsideWarningDuration() {
        
        // Set up mocks so that last CONNECTED status was 61 minutes ago, and last DISCONNECTED status was 59 minute ago.
        
        PaoIdentifier relayPaoId = new PaoIdentifier(1, PaoType.CRLY856);
        
        Date sixtyOneMinutesAgo = minutesAgo(61);
        PointValueQualityHolder connectedPointValue = 
                fakePointValue(sixtyOneMinutesAgo, CommStatusState.CONNECTED.getRawState());
        
        Date fiftyNineMinutesAgo = minutesAgo(59);
        PointValueQualityHolder nextDisconnectedPointValue = 
                fakePointValue(fiftyNineMinutesAgo, CommStatusState.DISCONNECTED.getRawState());
        
        RawPointHistoryDao mockRphDao = setupMockRphDao(connectedPointValue, nextDisconnectedPointValue);
        
        // Build the connection status info, with warning duration of 60 minutes
        
        CellularDeviceConnectionStatusEvaluator evaluator = new CellularDeviceConnectionStatusEvaluator();
        ReflectionTestUtils.setField(evaluator, "rphDao", mockRphDao);
        
        Map.Entry<PaoIdentifier, PointValueQualityHolder> relayConnectionStatusEntry = 
                Map.entry(relayPaoId, connectedPointValue);
        
        ConnectionStatusInfo connectionStatusInfo = ReflectionTestUtils.invokeMethod(evaluator, "buildConnectionStatusInfo", relayConnectionStatusEntry, baseTimestampInstant, sixtyMinutes);
        
        // Check for correct logic on whether to warn or not, based on point value timestamps
        
        assertTrue(connectionStatusInfo.isLastConnectedTimestampWarnable(),
                "ConnectionStatusInfo doesn't consider last connected timestamp warnable, "
                        + "but timestamp is beyond the warnable duration");

        assertFalse(connectionStatusInfo.isWarnable(), "ConnectionStatusInfo marked as warnable, "
                + "but DISCONNECTED timestamp isn't outside the warnable duration");
    }
    
    @Test
    public void testGatewayConnectedAndDisconnectedStatusOutsideWarningDuration() {
        
        // Set up mocks so that last CONNECTED status was 62 minutes ago, and last DISCONNECTED status was 61 minutes ago.
        
        PaoIdentifier gatewayPaoId = new PaoIdentifier(1, PaoType.GWY800);
        
        Date sixtyTwoMinutesAgo = minutesAgo(62);
        PointValueQualityHolder connectedPointValue = 
                fakePointValue(sixtyTwoMinutesAgo, CommStatusState.CONNECTED.getRawState());
        
        Date sixtyOneMinutesAgo = minutesAgo(61);
        PointValueQualityHolder nextDisconnectedPointValue = 
                fakePointValue(sixtyOneMinutesAgo, CommStatusState.DISCONNECTED.getRawState());
        
        RawPointHistoryDao mockRphDao = setupMockRphDao(connectedPointValue, nextDisconnectedPointValue);
        
        // Build the connection status info, with warning duration of 60 minutes
        
        GatewayConnectionStatusEvaluator evaluator = new GatewayConnectionStatusEvaluator();
        ReflectionTestUtils.setField(evaluator, "rphDao", mockRphDao);
        
        Map.Entry<PaoIdentifier, PointValueQualityHolder> gatewayConnectionStatusEntry = 
                Map.entry(gatewayPaoId, connectedPointValue);
        
        ConnectionStatusInfo connectionStatusInfo = ReflectionTestUtils.invokeMethod(evaluator, "buildConnectionStatusInfo", gatewayConnectionStatusEntry, baseTimestampInstant, sixtyMinutes);
        
        // Check for correct logic on whether to warn or not, based on point value timestamps

        assertTrue(connectionStatusInfo.isLastConnectedTimestampWarnable(),
                "ConnectionStatusInfo doesn't consider last connected timestamp warnable, "
                        + "but timestamp is beyond the warnable duration");

        assertTrue(connectionStatusInfo.isWarnable(), "ConnectionStatusInfo not marked as warnable, "
                + "but CONNECTED and DISCONNECTED timestamps are beyond the warnable duration");

        assertEquals(new Instant(sixtyOneMinutesAgo), connectionStatusInfo.getNextDisconnectedTimestamp(),
                "Incorrect DISCONNECTED timestamp");
    }
    
    @Test
    public void testRelayConnectedAndDisconnectedStatusOutsideWarningDuration() {
        
        // Set up mocks so that last CONNECTED status was 62 minutes ago, and last DISCONNECTED status was 61 minutes ago.
        
        PaoIdentifier relayPaoId = new PaoIdentifier(1, PaoType.GWY800);
        
        Date sixtyTwoMinutesAgo = minutesAgo(62);
        PointValueQualityHolder connectedPointValue = 
                fakePointValue(sixtyTwoMinutesAgo, CommStatusState.CONNECTED.getRawState());
        
        Date sixtyOneMinutesAgo = minutesAgo(61);
        PointValueQualityHolder nextDisconnectedPointValue = 
                fakePointValue(sixtyOneMinutesAgo, CommStatusState.DISCONNECTED.getRawState());
        
        RawPointHistoryDao mockRphDao = setupMockRphDao(connectedPointValue, nextDisconnectedPointValue);
        
        // Build the connection status info, with warning duration of 60 minutes
        
        CellularDeviceConnectionStatusEvaluator evaluator = new CellularDeviceConnectionStatusEvaluator();
        ReflectionTestUtils.setField(evaluator, "rphDao", mockRphDao);
        
        Map.Entry<PaoIdentifier, PointValueQualityHolder> relayConnectionStatusEntry = 
                Map.entry(relayPaoId, connectedPointValue);
        
        ConnectionStatusInfo connectionStatusInfo = ReflectionTestUtils.invokeMethod(evaluator, "buildConnectionStatusInfo", relayConnectionStatusEntry, baseTimestampInstant, sixtyMinutes);
        
        // Check for correct logic on whether to warn or not, based on point value timestamps

        assertTrue(connectionStatusInfo.isLastConnectedTimestampWarnable(),
                "ConnectionStatusInfo doesn't consider last connected timestamp warnable, "
                        + "but timestamp is beyond the warnable duration");

        assertTrue(connectionStatusInfo.isWarnable(), "ConnectionStatusInfo not marked as warnable, "
                + "but CONNECTED and DISCONNECTED timestamps are beyond the warnable duration");

        assertEquals(new Instant(sixtyOneMinutesAgo), connectionStatusInfo.getNextDisconnectedTimestamp(),
                "Incorrect DISCONNECTED timestamp");
    }
    
    @Test
    public void testBuildWarning() {
        
        // Set up mocks so that last CONNECTED status was 62 minutes ago, and last DISCONNECTED status was 61 minutes ago.
        
        PaoIdentifier gatewayPaoId = new PaoIdentifier(1, PaoType.GWY800);
        
        Date sixtyTwoMinutesAgo = minutesAgo(62);
        PointValueQualityHolder connectedPointValue = 
                fakePointValue(sixtyTwoMinutesAgo, CommStatusState.CONNECTED.getRawState());
        
        Date sixtyOneMinutesAgo = minutesAgo(61);
        PointValueQualityHolder nextDisconnectedPointValue = 
                fakePointValue(sixtyOneMinutesAgo, CommStatusState.DISCONNECTED.getRawState());
        
        RawPointHistoryDao mockRphDao = setupMockRphDao(connectedPointValue, nextDisconnectedPointValue);
        
        // Build the connection status info, with warning duration of 60 minutes
        
        GatewayConnectionStatusEvaluator evaluator = new GatewayConnectionStatusEvaluator();
        ReflectionTestUtils.setField(evaluator, "rphDao", mockRphDao);
        
        Map.Entry<PaoIdentifier, PointValueQualityHolder> gatewayConnectionStatusEntry = 
                Map.entry(gatewayPaoId, connectedPointValue);
        
        ConnectionStatusInfo connectionStatusInfo = ReflectionTestUtils.invokeMethod(evaluator, "buildConnectionStatusInfo", gatewayConnectionStatusEntry, baseTimestampInstant, sixtyMinutes);
        
        InfrastructureWarning warning = ReflectionTestUtils.invokeMethod(evaluator, "buildWarning", connectionStatusInfo);
        
        assertEquals(gatewayPaoId,
                warning.getPaoIdentifier(), "The infrastructure warning paoId does not match the original gateway ID.");

        assertEquals(InfrastructureWarningType.GATEWAY_CONNECTION_STATUS, warning.getWarningType(),
                "The infrastructure warning type is incorrect");

        assertEquals(InfrastructureWarningSeverity.LOW, warning.getSeverity(),
                "The infrastructure warning severity is incorrect");
        
        Object[] warningArgs = warning.getArguments();
        assertEquals(1,
                warningArgs.length, "Incorrect number of arguments in infrastructure warning");
        
        String disconnectTimeString = (String) warningArgs[0];
        assertEquals(formattedDisconnectTimeString, disconnectTimeString, "Incorrect formatted disconnect time string.");
    }
}
