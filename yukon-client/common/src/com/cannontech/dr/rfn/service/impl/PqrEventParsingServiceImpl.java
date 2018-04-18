package com.cannontech.dr.rfn.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.Instant;

import com.cannontech.common.util.ByteUtil;
import com.cannontech.dr.rfn.model.PqrEvent;
import com.cannontech.dr.rfn.model.PqrEventType;
import com.cannontech.dr.rfn.model.PqrResponseType;
import com.cannontech.dr.rfn.service.PqrEventParsingService;

public class PqrEventParsingServiceImpl implements PqrEventParsingService {

    @Override
    public List<PqrEvent> parseLogBlob(int inventoryId, byte[] pqrLogBlob) {
        List<byte[]> rawEntries = ByteUtil.divideByteArray(pqrLogBlob, 8);
        return rawEntries.stream()
                         .map(rawEntry -> {
                             // Event Type: byte 0
                             PqrEventType eventType = PqrEventType.of(rawEntry[0]);
                             // Timestamp (in seconds): bytes 1, 2, 3, 4
                             long timestampSeconds = ByteUtil.getInteger(Arrays.copyOfRange(rawEntry, 1, 5));
                             Instant timestamp = new Instant(timestampSeconds * 1000);
                             // Response Type: byte 5
                             PqrResponseType responseType = PqrResponseType.of(rawEntry[5]);
                             // Value: bytes 6, 7
                             double value = ByteUtil.getInteger(Arrays.copyOfRange(rawEntry, 6, 8));
                             // Adjust value based on multiplier (e.g. OV values are in 10ths of a volt)
                             value = value * responseType.getMultiplier();
                             return new PqrEvent(inventoryId, timestamp, eventType, responseType, value);
                         })
                         .collect(Collectors.toList());
    }

}
