package com.cannontech.dr.rfn.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.junit.jupiter.api.Test;

import com.cannontech.common.util.ByteUtil;
import com.cannontech.dr.rfn.model.PqrEvent;
import com.cannontech.dr.rfn.model.PqrEventType;
import com.cannontech.dr.rfn.model.PqrResponseType;
import com.cannontech.dr.rfn.service.impl.PqrEventParsingServiceImpl;

public class PqrEventParsingServiceTest {
    private PqrEventParsingService service = new PqrEventParsingServiceImpl();
    private static final int INV_ID = 1234567;
    private static final double DOUBLE_DELTA = 0.0001;
    
    /**
     * Build a log blob array containing a variety of entries, parse it via the service, and assert that the event
     * objects that come out have values that match the original values put into the log blob byte array.
     */
    @Test
    public void test_parseLogBlob() {
        Instant timestamp = DateTime.now().withMillisOfSecond(0).toInstant();
        List<byte[]> entries = generateEntries(timestamp);
        byte[] logBlob = buildLogBlob(entries);
        List<PqrEvent> events = service.parseLogBlob(INV_ID, logBlob);
        assertOnEvents(events, timestamp);
    }
    
    /**
     * Check that each event in the list has values that match the parameters specified here. These parameters should
     * match the parameters used to generate the bytes in the original log blob, in the <code>generateEntries</code>
     * method.
     */
    private void assertOnEvents(List<PqrEvent> events, Instant timestamp) {
        assertOnEvent(events.get(0), 0, PqrEventType.EVENT_COMPLETE, PqrResponseType.OVER_FREQUENCY, timestamp, 0);
        assertOnEvent(events.get(1), 1, PqrEventType.EVENT_DETECTED, PqrResponseType.OVER_VOLTAGE, timestamp, 1);
        assertOnEvent(events.get(2), 2, PqrEventType.EVENT_RELEASE, PqrResponseType.UNDER_FREQUENCY, timestamp, 10);
        assertOnEvent(events.get(3), 3, PqrEventType.EVENT_RELEASE_MAX, PqrResponseType.UNDER_VOLTAGE, timestamp, 100);
        assertOnEvent(events.get(4), 4, PqrEventType.EVENT_RELEASE_MIN, PqrResponseType.OVER_FREQUENCY, timestamp, 1000);
        assertOnEvent(events.get(5), 5, PqrEventType.EVENT_SUSTAINED, PqrResponseType.OVER_VOLTAGE, timestamp, 10000);
        assertOnEvent(events.get(6), 6, PqrEventType.NO_DR_EVENT, PqrResponseType.OVER_FREQUENCY, timestamp, 115);
        assertOnEvent(events.get(7), 7, PqrEventType.NOT_ACTIVATED, PqrResponseType.UNDER_FREQUENCY, timestamp, 200);
        assertOnEvent(events.get(8), 8, PqrEventType.RESPONSE_DISABLED, PqrResponseType.UNDER_VOLTAGE, timestamp, 200);
        assertOnEvent(events.get(9), 9, PqrEventType.RESTORE_DETECTED, PqrResponseType.OVER_FREQUENCY, timestamp, 200);
    }
    
    /**
     * Given a PQR event object and a set of event parameters, assert that the the values in the event object match the
     * specified event parameters.
     */
    private void assertOnEvent(PqrEvent event, int index, PqrEventType eventType, PqrResponseType responseType, Instant timestamp, double value) {
        assertEquals(INV_ID, event.getInventoryId(), "Mismatched inventoryId for event " + index);
        assertEquals(eventType, event.getEventType(), "Mismatched event type for event " + index);
        assertEquals(responseType, event.getResponseType(), "Mismatched response type for event " + index);
        assertEquals(timestamp, event.getTimestamp(), "Mismatched timestamp for event " + index);
        
        // Expect that the value in the parsed event has the multiplier applied
        double originalValueMultiplied = BigDecimal.valueOf(value)
                                                   .multiply(BigDecimal.valueOf(responseType.getMultiplier()))
                                                   .doubleValue();
        assertEquals(originalValueMultiplied, event.getValue(), DOUBLE_DELTA, "Mismatched value for event " + index);
    }
    
    /**
     * Generate a set of log entries (in byte array format) that use every type of PQR event, every type of PQR
     * response, and a variety of values.
     */
    private List<byte[]> generateEntries(Instant timestamp) {
        List<byte[]> entries = new ArrayList<>();
        entries.add(buildEntry(PqrEventType.EVENT_COMPLETE, PqrResponseType.OVER_FREQUENCY, timestamp, (short)0));      //0
        entries.add(buildEntry(PqrEventType.EVENT_DETECTED, PqrResponseType.OVER_VOLTAGE, timestamp, (short)1));        //1
        entries.add(buildEntry(PqrEventType.EVENT_RELEASE, PqrResponseType.UNDER_FREQUENCY, timestamp, (short)10));     //2
        entries.add(buildEntry(PqrEventType.EVENT_RELEASE_MAX, PqrResponseType.UNDER_VOLTAGE, timestamp, (short)100));  //3
        entries.add(buildEntry(PqrEventType.EVENT_RELEASE_MIN, PqrResponseType.OVER_FREQUENCY, timestamp, (short)1000));//4
        entries.add(buildEntry(PqrEventType.EVENT_SUSTAINED, PqrResponseType.OVER_VOLTAGE, timestamp, (short)10000));   //5
        entries.add(buildEntry(PqrEventType.NO_DR_EVENT, PqrResponseType.OVER_FREQUENCY, timestamp, (short)115));       //6
        entries.add(buildEntry(PqrEventType.NOT_ACTIVATED, PqrResponseType.UNDER_FREQUENCY, timestamp, (short)200));    //7
        entries.add(buildEntry(PqrEventType.RESPONSE_DISABLED, PqrResponseType.UNDER_VOLTAGE, timestamp, (short)200));  //8
        entries.add(buildEntry(PqrEventType.RESTORE_DETECTED, PqrResponseType.OVER_FREQUENCY, timestamp, (short)200));  //9
        return entries;
    }
    
    /**
     * Given all the parameters of a log entry, build up the appropriate byte array.
     */
    private byte[] buildEntry(PqrEventType eventType, PqrResponseType responseType, Instant timestamp, short value) {
        return ByteBuffer.allocate(8)
                         .put(eventType.getValue())
                         .put(ByteUtil.getTimestampBytes(timestamp))
                         .put(responseType.getValue())
                         .putShort(value)
                         .array();
    }
    
    /**
     * Concatenates a list of byte arrays into a single byte array. Used to combine a bunch of individual log entries
     * into the log blob.
     */
    private byte[] buildLogBlob(List<byte[]> entries) {
        ByteBuffer buffer = ByteBuffer.allocate(entries.size() * 8);
        for(byte[] entry : entries) {
            buffer.put(entry);
        }
        return buffer.array();
    }
}
