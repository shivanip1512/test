package com.cannontech.services.infrastructure.service.impl;

import java.util.Date;
import java.util.Map;

import org.easymock.EasyMock;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;

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

public class GatewayConnectionStatusEvaluatorTest {
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
    public void testConnectedStatusWithinWarningDuration() {
        
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
        
        GatewayConnectionStatusEvaluator evaluator = new GatewayConnectionStatusEvaluator(null, null, mockRphDao);
        
        Map.Entry<PaoIdentifier, PointValueQualityHolder> gatewayConnectionStatusEntry = 
                Map.entry(gatewayPaoId, connectedPointValue);
        
        ConnectionStatusInfo connectionStatusInfo = 
                evaluator.buildConnectionStatusInfo(gatewayConnectionStatusEntry, baseTimestampInstant, sixtyMinutes);
        
        // Check for correct logic on whether to warn or not, based on point value timestamps
        
        Assert.assertFalse("ConnectionStatusInfo considers last connected timestamp warnable, "
                + "but timestamp isn't outside the warnable duration",
                connectionStatusInfo.isLastConnectedTimestampWarnable());
        
        Assert.assertFalse("ConnectionStatusInfo marked as warnable, "
                + "but CONNECTED and DISCONNECTED timestamps aren't outside the warnable duration",
                connectionStatusInfo.isWarnable());
    }
    
    @Test
    public void testConnectedStatusOutsideAndDisconnectedStatusInsideWarningDuration() {
        
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
        
        GatewayConnectionStatusEvaluator evaluator = new GatewayConnectionStatusEvaluator(null, null, mockRphDao);
        
        Map.Entry<PaoIdentifier, PointValueQualityHolder> gatewayConnectionStatusEntry = 
                Map.entry(gatewayPaoId, connectedPointValue);
        
        ConnectionStatusInfo connectionStatusInfo = 
                evaluator.buildConnectionStatusInfo(gatewayConnectionStatusEntry, baseTimestampInstant, sixtyMinutes);
        
        // Check for correct logic on whether to warn or not, based on point value timestamps
        
        Assert.assertTrue("ConnectionStatusInfo doesn't consider last connected timestamp warnable, "
                + "but timestamp is beyond the warnable duration",
                connectionStatusInfo.isLastConnectedTimestampWarnable());
        
        Assert.assertFalse("ConnectionStatusInfo marked as warnable, "
                + "but DISCONNECTED timestamp isn't outside the warnable duration",
                connectionStatusInfo.isWarnable());
    }
    
    @Test
    public void testConnectedAndDisconnectedStatusOutsideWarningDuration() {
        
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
        
        GatewayConnectionStatusEvaluator evaluator = new GatewayConnectionStatusEvaluator(null, null, mockRphDao);
        
        Map.Entry<PaoIdentifier, PointValueQualityHolder> gatewayConnectionStatusEntry = 
                Map.entry(gatewayPaoId, connectedPointValue);
        
        ConnectionStatusInfo connectionStatusInfo = 
                evaluator.buildConnectionStatusInfo(gatewayConnectionStatusEntry, baseTimestampInstant, sixtyMinutes);
        
        // Check for correct logic on whether to warn or not, based on point value timestamps
        
        Assert.assertTrue("ConnectionStatusInfo doesn't consider last connected timestamp warnable, "
                + "but timestamp is beyond the warnable duration",
                connectionStatusInfo.isLastConnectedTimestampWarnable());
        
        Assert.assertTrue("ConnectionStatusInfo not marked as warnable, "
                + "but CONNECTED and DISCONNECTED timestamps are beyond the warnable duration",
                connectionStatusInfo.isWarnable());
        
        Assert.assertEquals("Incorrect DISCONNECTED timestamp", 
                new Instant(sixtyOneMinutesAgo), 
                connectionStatusInfo.getNextDisconnectedTimestamp());
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
        
        GatewayConnectionStatusEvaluator evaluator = new GatewayConnectionStatusEvaluator(null, null, mockRphDao);
        
        Map.Entry<PaoIdentifier, PointValueQualityHolder> gatewayConnectionStatusEntry = 
                Map.entry(gatewayPaoId, connectedPointValue);
        
        ConnectionStatusInfo connectionStatusInfo = 
                evaluator.buildConnectionStatusInfo(gatewayConnectionStatusEntry, baseTimestampInstant, sixtyMinutes);
        
        InfrastructureWarning warning = GatewayConnectionStatusEvaluator.buildWarning(connectionStatusInfo);
        
        Assert.assertEquals("The infrastructure warning paoId does not match the original gateway ID.", 
                gatewayPaoId, 
                warning.getPaoIdentifier());
        
        Assert.assertEquals("The infrastructure warning type is incorrect", 
                InfrastructureWarningType.GATEWAY_CONNECTION_STATUS, 
                warning.getWarningType());
        
        Assert.assertEquals("The infrastructure warning severity is incorrect",
                InfrastructureWarningSeverity.LOW,
                warning.getSeverity());
        
        Object[] warningArgs = warning.getArguments();
        Assert.assertEquals("Incorrect number of arguments in infrastructure warning", 
                1, 
                warningArgs.length);
        
        String disconnectTimeString = (String) warningArgs[0];
        Assert.assertEquals("Incorrect formatted disconnect time string.",
                formattedDisconnectTimeString,
                disconnectTimeString);
    }
}
