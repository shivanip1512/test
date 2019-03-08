package com.cannontech.dr.itron;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.ByteUtil;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.message.PointData;



public enum ItronDataEventType {
    EVENT_STARTED(0x0E, BuiltInAttribute.CONTROL_STATUS, 0, 4, 1),
    EVENT_STOPPED(0x0F, BuiltInAttribute.CONTROL_STATUS, 0, 4, 0),
    EVENT_CANCELED(0x10, BuiltInAttribute.CONTROL_STATUS, 0, 4, 0),
    EVENT_SUPERSEDED(0x12, BuiltInAttribute.EVENT_SUPERSEDED, 0, 4, null),
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
    
    private final Long eventIdHex;
    private final BuiltInAttribute attribute;
    private final int firstByteIndex;
    private final int numOfBytes;
    private final Integer value;
    
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
    /**
     * 
     * @param byteArray - this is a byte array that has been converted from a 5 byte hex string.
     * @return will return the value needed based off the flash logging library document.
     */
    protected long decode(byte[] byteArray) {
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        if (numOfBytes == 1) {
            return ByteUtil.getInteger(byteArray[firstByteIndex]);
        } else if (numOfBytes == 2) {
            return buffer.getShort(firstByteIndex);
        } else if (numOfBytes == 4) {
            return buffer.getInt(firstByteIndex);
        } else {
            byte[] paddedArray = new byte[8];
            System.arraycopy(byteArray, 0, paddedArray, 3, 5);
            buffer = ByteBuffer.wrap(paddedArray);
            return buffer.getLong(firstByteIndex);
        }
    }
    
    public BuiltInAttribute getAttribute(byte[] byteArray) {
        long relayNumber = decode(byteArray);
        switch (this) {
        case LOAD_ON:
            return BuiltInAttribute.valueOf("RELAY_" + relayNumber + "_RELAY_STATE");
        case LOAD_OFF:
            return BuiltInAttribute.valueOf("RELAY_" + relayNumber + "_RELAY_STATE");
        case SHED_START:
            return BuiltInAttribute.valueOf("RELAY_" + relayNumber + "_SHED_STATUS");
        case SHED_END:
            return BuiltInAttribute.valueOf("RELAY_" + relayNumber + "_SHED_STATUS");
        case EVENT_STARTED:            
        case EVENT_STOPPED:
        case EVENT_CANCELED:
        case EVENT_SUPERSEDED:
        case POWER_FAIL:
        case LINE_UNDER_FREQUENCY:
        case LINE_UNDER_VOLTAGE:
        default:
            return attribute;
        }
    }
    
    public PointData getPointData(byte[] byteArray, LitePoint lp) {
        PointData pointData = new PointData();
        if (this.value != null) {
            pointData.setValue(this.value);
        } else {
            long payloadValue = decode(byteArray);
            pointData.setValue(payloadValue);
        }
        pointData.setId(lp.getLiteID());
        pointData.setType(lp.getPointType());
        pointData.setPointQuality(PointQuality.Normal);
        pointData.setTagsPointMustArchive(true);
        return pointData;
    }
}
