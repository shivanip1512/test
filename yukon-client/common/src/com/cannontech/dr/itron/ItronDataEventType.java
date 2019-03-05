package com.cannontech.dr.itron;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.message.PointData;



public enum ItronDataEventType {
    EVENT_STARTED(0x0E, BuiltInAttribute.CONTROL_STATUS, 0, 4, 1),
    EVENT_STOPPED(0x0F, BuiltInAttribute.CONTROL_STATUS, 0, 4, 0),
    EVENT_CANCELED(0x10, BuiltInAttribute.CONTROL_STATUS, 0, 4, 0),
    EVENT_SUPERSEDED(0x12, null, 0, 4, null),
    LOAD_ON(0x14, null, 0, 1, 1),
    LOAD_OFF(0x15, null, 0, 1, 0),
    SHED_START(0x18, null, 0, 1, 1),
    SHED_END(0x19, null, 0, 1, 0),
    POWER_FAIL(0x00, BuiltInAttribute.BLINK_COUNT, 0, 1, null),
    LINE_UNDER_FREQUENCY(0x83, BuiltInAttribute.TOTAL_LUF_COUNT, 0, 1, null),
    LINE_UNDER_VOLTAGE(0x84, BuiltInAttribute.TOTAL_LUV_COUNT, 0, 1, null),
    AVERAGE_VOLTAGE(0x9D, BuiltInAttribute.AVERAGE_VOLTAGE, 0, 2, null),
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
    Integer value;
    
    ItronDataEventType(long eventIdHex) {
        this.eventIdHex = eventIdHex;
    }

    ItronDataEventType(long eventIdHex, BuiltInAttribute attribute) {
        this.eventIdHex = eventIdHex;
        this.attribute = attribute;
    }
    
    ItronDataEventType(long eventIdHex, BuiltInAttribute attribute, int firstByteIndex, int numOfBytes, Integer value) {
        this.eventIdHex = eventIdHex;
        this.attribute = attribute;
        this.firstByteIndex = firstByteIndex;
        this.numOfBytes = numOfBytes;
        this.value = value;
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
    
    public BuiltInAttribute getAttribute(ByteBuffer buffer) {
        int value = decode(buffer);
        switch (this) {
        case EVENT_STARTED:            
            break;
        case EVENT_STOPPED:
        case EVENT_CANCELED:
            break;
        case EVENT_SUPERSEDED:
            break;
        case LOAD_ON:
            return BuiltInAttribute.valueOf("RELAY_" + value + "_RELAY_STATE");
        case LOAD_OFF:
            return BuiltInAttribute.valueOf("RELAY_" + value + "_RELAY_STATE");
        case SHED_START:
            return BuiltInAttribute.valueOf("RELAY_" + value + "_SHED_STATUS");
        case SHED_END:
            return BuiltInAttribute.valueOf("RELAY_" + value + "_SHED_STATUS");
        case POWER_FAIL:
            break;
        case LINE_UNDER_FREQUENCY:
            break;
        case LINE_UNDER_VOLTAGE:
            break;
        default:
            return attribute;
        }
        return attribute;
    }
    
    public PointData getPointData(ByteBuffer buffer, LitePoint lp) {
        int payloadValue = decode(buffer);
        PointData pointData = new PointData();
        pointData.setValue(this.value);
        //this here is hot garbage.
        switch (this) {
            case EVENT_STARTED:
                pointData.setValue(1);
                break;
            case EVENT_STOPPED:
            case EVENT_CANCELED:
                pointData.setValue(0);
                break;
            case EVENT_SUPERSEDED:
                pointData.setValue(payloadValue);
                break;
            case LOAD_ON:
                pointData.setValue(1);
                break;
            case LOAD_OFF:
                pointData.setValue(0);
                break;
            case SHED_START:
                pointData.setValue(1);
                break;
            case SHED_END:
                pointData.setValue(0);
                break;
            case POWER_FAIL:
                pointData.setValue(0);
                break;
            case LINE_UNDER_FREQUENCY:
                pointData.setValue(0);
                break;
            case LINE_UNDER_VOLTAGE:
                pointData.setValue(0);
                break;
            case AVERAGE_VOLTAGE:
                pointData.setValue(payloadValue);
                break;
        }
        pointData.setId(lp.getLiteID());
        pointData.setType(lp.getPointType());
        pointData.setPointQuality(PointQuality.Normal);
        pointData.setTagsPointMustArchive(true);
        return pointData;
    }
}
