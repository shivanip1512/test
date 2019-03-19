package com.cannontech.dr.itron.service;

import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.Test;

import com.cannontech.dr.itron.ItronDataEventType;

public class ItronDeviceDataParserTest {
    
    @Test
    public void validateItronDataEventType() {
        HashMap<Long, Integer> hexValueCounter = new HashMap<>();
        String duplicates = "";
        boolean failed = false;
        for(ItronDataEventType type : ItronDataEventType.values()) {
            if (hexValueCounter.containsKey(type.getEventIdHex())) {
                failed = true;
                duplicates += String.format("0x%08X", type.getEventIdHex()) + ", ";
            } else {
                hexValueCounter.put(type.getEventIdHex(), 1);
            }
        }
        if (failed) {
            fail("duplicate hex value found: " + duplicates);
        }

    }

}
