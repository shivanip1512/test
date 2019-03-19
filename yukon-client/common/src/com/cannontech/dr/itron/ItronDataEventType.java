package com.cannontech.dr.itron;

import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.ByteUtil;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;

public enum ItronDataEventType {
    //Event where the value and attribute are known and defined. decode() is not used for these enums.
    EVENT_STARTED(0x0E, BuiltInAttribute.CONTROL_STATUS, 0, 0, 1),
    EVENT_STOPPED(0x0F, BuiltInAttribute.CONTROL_STATUS, 0, 0, 0),
    EVENT_CANCELED(0x10, BuiltInAttribute.CONTROL_STATUS, 0, 0, 0),
    MEMORY_MAP_LOST(0x0A, BuiltInAttribute.MEMORY_MAP_LOST, 0, 0, 1),
    MAX_CONTROL_EXCEEDED(0x85, BuiltInAttribute.MAX_CONTROL_EXCEEDED, 0, 0, 1),
    NETWORK_TIMEOUT_CANCEL(0x86, BuiltInAttribute.NETWORK_TIMEOUT_CANCEL, 0, 0, 1),
    CONFIGURATION_UPDATED_HASH(0x8A, BuiltInAttribute.CONFIGURATION_UPDATED_HASH, 0, 0, 1),
    TLS_FAIL(0x1F, BuiltInAttribute.TLS_FAIL, 0, 0, 1),
    BAD_HDLC(0x20, BuiltInAttribute.BAD_HDLC, 0, 0, 1),
    
    //Events where the Relay Number for the Attribute is obtained from the payload.
    LOAD_ON(0x14, null, 0, 1, 3),
    LOAD_OFF(0x15, null, 0, 1, 2),
    SHED_START(0x18, null, 0, 1, 1),
    SHED_END(0x19, null, 0, 1, 0),
    CALL_FOR_COOL_ON(0x98, null, 0, 1, 1),
    CALL_FOR_COOL_OFF(0x99, null, 0, 1, 0),

    //Events where the values are obtained from the payload.
    AVERAGE_VOLTAGE(0x9D, BuiltInAttribute.AVERAGE_VOLTAGE, 0, 2, null),
    EVENT_SUPERSEDED(0x12, BuiltInAttribute.EVENT_SUPERSEDED, 0, 4, null),
    FIRMWARE_UPDATE(0x09, BuiltInAttribute.FIRMWARE_VERSION, 0, 2, null),
    TIME_SYNC(0x81, BuiltInAttribute.TIME_SYNC, 0, 4, null),
    COLD_START(0x01, BuiltInAttribute.COLD_START, 0, 1, null),
    CONFIGURATION_PROCESSED(0x87, BuiltInAttribute.CONFIGURATION_PROCESSED, 0, 4, null),
    TIME_LOST(0x88, BuiltInAttribute.TIME_LOST, 0, 4, null),
    SELF_CHECK_FAIL(0x02, BuiltInAttribute.SELF_CHECK_FAIL, 0, 4, null),
    INACTIVE_APPLIANCE(0x17, BuiltInAttribute.INACTIVE_APPLIANCE, 0, 1, null),
    RADIO_LINK_QUALITY(0x8B, BuiltInAttribute.RADIO_LINK_QUALITY, 0, 1, null),
    INCORRECT_TLS_IDENTITY(0x8F, BuiltInAttribute.INCORRECT_TLS_IDENTITY, 0, 2, null),
    KEY_UPDATE(0x1C, BuiltInAttribute.KEY_UPDATE, 0, 2, null),
    KEY_UPDATE_FAIL(0x1D, BuiltInAttribute.KEY_UPDATE_FAIL, 0, 2, null),
    TLS_ALERT(0x21, BuiltInAttribute.TLS_ALERT, 0, 1, null),
    OPTIMIZE_INTELLIGENT_CONTROL(0x94, BuiltInAttribute.OPTIMIZE_INTELLIGENT_CONTROL, 0, 2, null),
    SNAP_TO_GOLD(0x96, BuiltInAttribute.SNAP_TO_GOLD, 0, 4, null),
    EVENT_RECEIVED(0x97, BuiltInAttribute.EVENT_RECEIVED, 0, 4, null),
    LOAD_STATUS(0x9A, BuiltInAttribute.LOAD_STATUS, 0, 1, null),

    //Events where the values increment current count
    POWER_FAIL(0x00, BuiltInAttribute.BLINK_COUNT, 0, 1, null),
    LINE_UNDER_FREQUENCY(0x83, BuiltInAttribute.TOTAL_LUF_COUNT, 0, 1, null),
    LINE_UNDER_VOLTAGE(0x84, BuiltInAttribute.TOTAL_LUV_COUNT, 0, 1, null),
    
    //Events that rely on two events to complete point data
    MIN_VOLTAGE(0x9C, BuiltInAttribute.MINIMUM_VOLTAGE, 0, 2, null),
    MIN_VOLTAGE_TIME(0x9E, BuiltInAttribute.MINIMUM_VOLTAGE, 0, 4, null),
    MAX_VOLTAGE(0x9B, BuiltInAttribute.MAXIMUM_VOLTAGE, 0, 2, null),
    MAX_VOLTAGE_TIME(0x9F, BuiltInAttribute.MAXIMUM_VOLTAGE, 0, 4, null),
    ;
    
    private final static ImmutableSet<ItronDataEventType> incrementalTypes;
    private final static ImmutableSet<ItronDataEventType> voltageTypes;
    
    static {
        incrementalTypes = ImmutableSet.of(
            POWER_FAIL,
            LINE_UNDER_FREQUENCY,
            LINE_UNDER_VOLTAGE
            );
        voltageTypes = ImmutableSet.of(
           MIN_VOLTAGE,
           MIN_VOLTAGE_TIME,
           MAX_VOLTAGE,
           MAX_VOLTAGE_TIME
           );
    }
    
    private static Map<Long, ItronDataEventType> ItronEventTypeFromHexMap = new HashMap<>();
    static {
        for (ItronDataEventType eventType : values()) {
            ItronEventTypeFromHexMap.put(eventType.eventIdHex, eventType);
        }
    }
    
    private static Cache<String, PointData> voltageCache = CacheBuilder.newBuilder().expireAfterWrite(59, TimeUnit.MINUTES).build();
    
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
    
    public Integer getValue() {
        return value;
    }
    
    public Long getEventIdHex() {
        return eventIdHex;
    }
    
    public boolean isIncrementalType() {
        return incrementalTypes.contains(this);
    }
    
    public boolean isVoltageType() {
        return voltageTypes.contains(this);
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
    /**
     * This is currently used to get relay numbered attributes.
     */
    public BuiltInAttribute getAttribute(byte[] byteArray) {
        long relayNumber = decode(byteArray);
        switch (this) {
        case LOAD_ON:
        case LOAD_OFF:
            return BuiltInAttribute.valueOf("RELAY_" + relayNumber + "_RELAY_STATE");
        case SHED_START:
        case SHED_END:
            return BuiltInAttribute.valueOf("RELAY_" + relayNumber + "_SHED_STATUS");
        case CALL_FOR_COOL_ON:
        case CALL_FOR_COOL_OFF:
            return BuiltInAttribute.valueOf("RELAY_" + relayNumber + "_CALL_FOR_COOL");
        default:
            return attribute;
        }
    }
    
    public Optional<PointData> getPointData(byte[] byteArray, double currentValue, String eventTime, LitePoint lp) {
        if (this.isVoltageType()) {
            return getPointDataForMinMaxVoltage(byteArray, eventTime, lp);
        }
        PointData pointData = new PointData();
        
        pointData.setValue(value != null ? value : this.isIncrementalType() ? currentValue + 1 : decode(byteArray));
        pointData.setTimeStamp(new DateTime(eventTime).toDate());
        
        return Optional.of(pointData);
    }
    /**
     * 
     * @param byteArray is utilized to decode the payload values.
     * @param dt is utilize to making the key for the cache of partial points.
     * @return returns pointData for Min and Max Voltage. Since these two points need two events to be completed,
     *  a partial point is created when only one of the two required events are there
     */
    private Optional<PointData> getPointDataForMinMaxVoltage(byte[] byteArray, String dt, LitePoint lp) {
        //Check to see if point exist for key: YYYY-MM-DD- + lp + other Partial Point
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); // Quoted "Z" to indicate UTC, no timezone offset
        String keyPrefix = df.format(new DateTime(dt)) + lp.getLiteID();
        PointData pointData;
        switch (this) {
        case MIN_VOLTAGE://if min time is found in cache grab point, clear from cache, generate and return point otherwise create partial point and return empty optional
            pointData = voltageCache.getIfPresent(keyPrefix + MIN_VOLTAGE_TIME.name());
            if (pointData == null) {
                //make new point data and put in cache as nowAsISO + MIN_VOLTAGE;
                pointData = new PointData();
                pointData.setValue(decode(byteArray));
                voltageCache.put(keyPrefix + this.name(), pointData);
                break;
            } else {
                voltageCache.invalidate(keyPrefix + MIN_VOLTAGE_TIME.name());
                pointData.setValue(decode(byteArray));
                return Optional.of(pointData);
            }
        case MIN_VOLTAGE_TIME://if min time is found in cache grab point, clear from cache, generate and return point otherwise create partial point and return empty optional
            pointData = voltageCache.getIfPresent(keyPrefix + MIN_VOLTAGE.name());
            if (pointData == null) {
                //make new point data and put in cache as nowAsISO + MIN_VOLTAGE;
                pointData = new PointData();
                pointData.setTime(new Date(decode(byteArray)));
                voltageCache.put(keyPrefix + this.name(), pointData);
                break;
            } else {
                voltageCache.invalidate(keyPrefix + MIN_VOLTAGE.name());
                pointData.setTime(new Date(decode(byteArray)));
                return Optional.of(pointData);
            }
        case MAX_VOLTAGE://if min time is found in cache grab point, clear from cache, generate and return point otherwise create partial point and return empty optional
            pointData = voltageCache.getIfPresent(keyPrefix + MAX_VOLTAGE_TIME.name());
            if (pointData == null) {
                //make new point data and put in cache as nowAsISO + MAX_VOLTAGE;
                pointData = new PointData();
                pointData.setValue(decode(byteArray));
                voltageCache.put(keyPrefix + this.name(), pointData);
                break;
            } else {
                voltageCache.invalidate(keyPrefix + MAX_VOLTAGE_TIME.name());
                pointData.setValue(decode(byteArray));
                return Optional.of(pointData);
            }
        case MAX_VOLTAGE_TIME://if min time is found in cache grab point, clear from cache, generate and return point otherwise create partial point and return empty optional
            pointData = voltageCache.getIfPresent(keyPrefix + MAX_VOLTAGE.name());
            if (pointData == null) {
                //make new point data and put in cache as nowAsISO + MAX_VOLTAGE_TIME;
                pointData = new PointData();
                pointData.setTime(new Date(decode(byteArray)));
                voltageCache.put(keyPrefix + this.name(), pointData);
                break;
            } else {
                voltageCache.invalidate(keyPrefix + MAX_VOLTAGE.name());
                pointData.setTime(new Date(decode(byteArray)));
                return Optional.of(pointData);
            }
        default:
            break;
        }
        return Optional.empty();
    }
}
