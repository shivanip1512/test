package com.cannontech.dr.itron;

import java.nio.ByteBuffer;
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

/**
 * This enum maps events to the event IDs that will display in the data log, the attribute each event is associated
 * with, and some organizational and data parsing information.
 * 
 * Event IDs here are taken from the firmware documents: 
 * "Flash Logging Library 6.3.2 - SSN Log - Entry Types" and
 * "Load Control Switch Feature Specification 11.1 - Logging - Event IDs"
 * 
 * The event IDs in the 0x0000 - 0x009F range are common data that Itron knows how to decode. These match the event IDs
 * in "Flash Logging Library".
 * 
 * The event IDs in the 0x8000+ range are vendor extensions that Itron does not understand. So "Flash Logging Library"
 * will list these event IDs in the lower range, but we expect many of them to be returned from Itron in the 0x8000+ 
 * range. "Load Control Switch Feature Spec" lists the correct event ID for these.
 */
public enum ItronDataEventType {
    //Event where the value and attribute are known and defined. decode() is not used for these enums.
    EVENT_STARTED(0x000E, BuiltInAttribute.CONTROL_STATUS, 0, 4, 1),
    EVENT_STOPPED(0x000F, BuiltInAttribute.CONTROL_STATUS, 0, 4, 0),
    EVENT_CANCELED(0x0010, BuiltInAttribute.CONTROL_STATUS, 0, 4, 0),
    MEMORY_MAP_LOST(0x8081, BuiltInAttribute.MEMORY_MAP_LOST, 0, 0, 1),
    
    //Events where the Relay Number for the Attribute is obtained from the payload.
    LOAD_ON(0x0014, null, 0, 1, 3),
    LOAD_OFF(0x0015, null, 0, 1, 2),
    SHED_START(0x0018, null, 0, 1, 1),
    SHED_END(0x0019, null, 0, 1, 0),
    CALL_FOR_COOL_ON(0x8098, null, 0, 1, 1),
    CALL_FOR_COOL_OFF(0x8099, null, 0, 1, 0),

    //Events where the values are obtained from the payload.
    AVERAGE_VOLTAGE(0x809D, BuiltInAttribute.AVERAGE_VOLTAGE, 0, 2, null),
    FIRMWARE_UPDATE(0x0009, BuiltInAttribute.FIRMWARE_VERSION, 0, 2, null),
    RADIO_LINK_QUALITY(0x808B, BuiltInAttribute.RADIO_LINK_QUALITY, 0, 1, null),
    EVENT_RECEIVED(0x8097, BuiltInAttribute.EVENT_RECEIVED, 0, 4, null),

    //Events where the values increment current count
    POWER_FAIL(0x8000, BuiltInAttribute.BLINK_COUNT, 0, 1, null),
    LINE_UNDER_FREQUENCY(0x8083, BuiltInAttribute.TOTAL_LUF_COUNT, 0, 1, null),
    LINE_UNDER_VOLTAGE(0x8084, BuiltInAttribute.TOTAL_LUV_COUNT, 0, 1, null),
    
    //Events that rely on two events to complete point data
    MIN_VOLTAGE(0x809C, BuiltInAttribute.MINIMUM_VOLTAGE, 0, 2, null),
    MIN_VOLTAGE_TIME(0x809E, BuiltInAttribute.MINIMUM_VOLTAGE, 0, 4, null),
    MAX_VOLTAGE(0x809B, BuiltInAttribute.MAXIMUM_VOLTAGE, 0, 2, null),
    MAX_VOLTAGE_TIME(0x809F, BuiltInAttribute.MAXIMUM_VOLTAGE, 0, 4, null),
    ;
    
    private final static ImmutableSet<ItronDataEventType> incrementalTypes;
    private final static ImmutableSet<ItronDataEventType> voltageTypes;
    private final static ImmutableSet<ItronDataEventType> controlEventTypes;
        
    static {
        incrementalTypes = ImmutableSet.of(
                                           POWER_FAIL,
                                           LINE_UNDER_FREQUENCY,
                                           LINE_UNDER_VOLTAGE);
        voltageTypes = ImmutableSet.of(
                                       MIN_VOLTAGE,
                                       MIN_VOLTAGE_TIME,
                                       MAX_VOLTAGE,
                                       MAX_VOLTAGE_TIME);
        controlEventTypes = ImmutableSet.of(
                                            EVENT_STARTED,
                                            EVENT_STOPPED,
                                            EVENT_CANCELED);
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
        this.value = value; //Corresponds to the value in the point's state group
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
    
    public boolean isControlEventType() {
        return controlEventTypes.contains(this);
    }
    /**
     * 
     * @param byteArray - this is a byte array that has been converted from a 5 byte hex string.
     * @return will return the value needed based off the flash logging library document.
     */
    public long decode(byte[] byteArray) {
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
     * @throws IllegalArgumentException if no appropriate attribute can be found.
     */
    public BuiltInAttribute getAttribute(byte[] byteArray) throws IllegalArgumentException {
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
        if (isVoltageType()) {
            return getPointDataForMinMaxVoltage(byteArray, eventTime, lp);
        }
        
        PointData pointData = new PointData();
        pointData.setValue(getPointDataValue(byteArray, currentValue));
        pointData.setTimeStamp(new DateTime(eventTime).toDate());
        
        return Optional.of(pointData);
    }
    
    private double getPointDataValue(byte[] byteArray, double currentValue) {
        if (value != null) { 
            // Event translates straight to a status point value
            return value;
        } else if (isIncrementalType()) { 
            // Event increments a count
            return currentValue + 1;
        } else {
            // Event has to be decoded
            return decode(byteArray);
        }
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
        String keyPrefix = dt.substring(0, 11) + lp.getLiteID();
        
        switch (this) {
            case MIN_VOLTAGE:
                return processVoltageEvent(true, keyPrefix, MIN_VOLTAGE_TIME, byteArray);
            case MIN_VOLTAGE_TIME:
                return processVoltageEvent(false, keyPrefix, MIN_VOLTAGE, byteArray);
            case MAX_VOLTAGE:
                return processVoltageEvent(true, keyPrefix, MAX_VOLTAGE_TIME, byteArray);
            case MAX_VOLTAGE_TIME:
                return processVoltageEvent(false, keyPrefix, MAX_VOLTAGE, byteArray);
            default:
                break;
        }
        
        return Optional.empty();
    }
    
    /**
     * If date/value is found in cache, grab partial point data, clear from cache, generate and return point data.
     * Otherwise cache a partial point and return empty optional.
     * @param isValue true if the event is a voltage value, false if it is a voltage date.
     * @param type The voltage data type (min or max) to search for in cache.
     */
    private Optional<PointData> processVoltageEvent(boolean isValue, String keyPrefix, ItronDataEventType type, byte[] byteArray) {
        PointData pointData = voltageCache.getIfPresent(keyPrefix + type.name());
        if (pointData == null) {
            // make new, partial point data and put in cache
            pointData = new PointData();
            insertValueOrTime(pointData, isValue, byteArray);
            voltageCache.put(keyPrefix + name(), pointData);
            return Optional.empty();
        }
        
        voltageCache.invalidate(keyPrefix + type.name());
        insertValueOrTime(pointData, isValue, byteArray);
        return Optional.of(pointData);
    }
    
    /**
     * Insert a value or time into the pointData.
     */
    private void insertValueOrTime(PointData pointData, boolean isValue, byte[] byteArray) {
        if (isValue) {
            pointData.setValue(decode(byteArray));
        } else {
            pointData.setTime(new Date(decode(byteArray)));
        }
    }
}
