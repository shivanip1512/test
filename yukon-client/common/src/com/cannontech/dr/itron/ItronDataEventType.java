package com.cannontech.dr.itron;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.message.dispatch.message.PointData;



public enum ItronDataEventType {
    EVENT_STARTED(0x0E, BuiltInAttribute.CONTROL_STATUS, 0, 4),
    EVENT_STOPPED(0x0F, BuiltInAttribute.CONTROL_STATUS, 0, 4),
    EVENT_CANCELED(0x10, BuiltInAttribute.CONTROL_STATUS, 0, 4),
    LOAD_ON(0x14, null, 0, 1),
    LOAD_OFF(0x15, null, 0, 1),
    ;
      
    private static Map<Long, ItronDataEventType> ItronEventTypeFromHexMap = new HashMap<>();
    static {
        for (ItronDataEventType eventType : values()) {
            ItronEventTypeFromHexMap.put(eventType.eventIdHex, eventType);
        }
    }
    
    Long eventIdHex;
    BuiltInAttribute attribute;
    int firstByteIndex;
    int numOfBytes;
    
    ItronDataEventType(long eventIdHex) {
        this.eventIdHex = eventIdHex;
    }

    ItronDataEventType(long eventIdHex, BuiltInAttribute attribute) {
        this.eventIdHex = eventIdHex;
        this.attribute = attribute;
    }
    
    ItronDataEventType(long eventIdHex, BuiltInAttribute attribute, int firstByteIndex, int numOfBytes) {
        this.eventIdHex = eventIdHex;
        this.attribute = attribute;
        this.firstByteIndex = firstByteIndex;
        this.numOfBytes = numOfBytes;
    }
    
    public static ItronDataEventType getFromHex(long hex) {
        return ItronEventTypeFromHexMap.get(hex);
    }
    
    public int getFirstByteIndex() {
        return firstByteIndex;
    }
    
    private int decode(ByteBuffer buffer) {
        if (numOfBytes == 1) {
            return buffer.get(firstByteIndex);
        } else if (numOfBytes == 2) {
            return buffer.getShort(firstByteIndex);
        } else {
            return buffer.getInt(firstByteIndex);
        }
    }
    
    public PointData getPointData(ByteBuffer buffer) {
        int value = decode(buffer);
        PointData pointData = new PointData();
        switch (this) {
            case EVENT_STARTED:
                pointData.setValue(1);
                
                break;
            case EVENT_STOPPED:
            case EVENT_CANCELED:
                pointData.setValue(0);
                break;
            case LOAD_ON:
                this.attribute = BuiltInAttribute.valueOf("RELAY_" + value + "_RELAY_STATE");
                pointData.setValue(1);
                break;
            case LOAD_OFF:
                this.attribute = BuiltInAttribute.valueOf("RELAY_" + value + "_RELAY_STATE");
                pointData.setValue(0);
                break;
        }
        
        return pointData;
    }
}
