package com.cannontech.dr.meterDisconnect;

import static org.easymock.EasyMock.*;

import javax.jms.JMSException;
import javax.jms.StreamMessage;

import org.easymock.EasyMock;
import org.joda.time.Instant;
import org.junit.Test;
import org.junit.Assert;

public class MeterDisconnectMessageListenerTest {
    
    @Test
    public void test_MeterDisconnectControlMessage() throws JMSException {
        // Generate random groupId in the range 1 - 1,000,000
        int groupId = (int)(1 + (Math.random() * 999999));
        // Use the current time as the start time
        long startTime =  (Instant.now().getMillis() / 1000);
        // End time = Start + 12 hours (43200s)
        long endTime = startTime + 43200;
        
        // Mock the incoming message
        StreamMessage message = createNiceMock(javax.jms.StreamMessage.class);
        expect(message.readInt())
        .andReturn((int) groupId);
        expect(message.readLong())
        .andReturn((long) startTime)
        .andReturn((long) endTime);
        
        EasyMock.replay(message);
        
        MeterDisconnectControlMessage meterDisconnectControlMessage = createMockBuilder(MeterDisconnectControlMessage.class)
                .withConstructor(message)
                .createMock();
        
        Assert.assertEquals(meterDisconnectControlMessage.getGroupId(), groupId);
        Assert.assertEquals(meterDisconnectControlMessage.getStartTime(), new Instant(startTime * 1000));
        Assert.assertEquals(meterDisconnectControlMessage.getEndTime(), new Instant(endTime * 1000));

    }

    @Test
    public void test_MeterDisconnectRestoreMessage() throws JMSException {
        // Generate random groupId in the range 1 - 1,000,000
        int groupId = (int)(1 + (Math.random() * 999999));
        // Use the current time as the restore time
        long restoreTime =  (Instant.now().getMillis() / 1000);
        
        // Mock the incoming message
        StreamMessage message = createNiceMock(javax.jms.StreamMessage.class);
        expect(message.readInt())
        .andReturn((int) groupId);
        expect(message.readLong())
        .andReturn((long) restoreTime);
        
        EasyMock.replay(message);
        
        MeterDisconnectRestoreMessage meterDisconnectRestoreMessage = createMockBuilder(MeterDisconnectRestoreMessage.class)
                .withConstructor(message)
                .createMock();
        
        Assert.assertEquals(meterDisconnectRestoreMessage.getGroupId(), groupId);
         Assert.assertEquals(meterDisconnectRestoreMessage.getRestoreTime(), new Instant(restoreTime * 1000));
    }
}
