package com.cannontech.dr.itron;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.core.Logger;
import org.joda.time.DateTime;

import com.cannontech.clientutils.YukonLogManager;
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
    CONFIGURATION_UPDATED_HASH(0x808A, BuiltInAttribute.CONFIGURATION_UPDATED_HASH, 0, 0, 1),
    
    //Events where the Relay Number for the Attribute is obtained from the payload.
    LOAD_ON(0x0014, null, 0, 1, 3),
    LOAD_OFF(0x0015, null, 0, 1, 2),
    SHED_START(0x0018, null, 0, 1, 1),
    SHED_END(0x0019, null, 0, 1, 0),
    CALL_FOR_COOL_ON(0x8098, null, 0, 1, 1),
    CALL_FOR_COOL_OFF(0x8099, null, 0, 1, 0),

    //Events where the values are obtained from the payload.
    AVERAGE_VOLTAGE(0x809D, BuiltInAttribute.AVERAGE_VOLTAGE, 0, 2, null),
    EVENT_SUPERSEDED(0x0012, BuiltInAttribute.EVENT_SUPERSEDED, 0, 4, null),
    FIRMWARE_UPDATE(0x0009, BuiltInAttribute.FIRMWARE_VERSION, 0, 2, null),
    RADIO_LINK_QUALITY(0x808B, BuiltInAttribute.RADIO_LINK_QUALITY, 0, 1, null),
    EVENT_RECEIVED(0x8097, BuiltInAttribute.EVENT_RECEIVED, 0, 4, null),

    //Events where the values increment current count
    POWER_FAIL(0x0000, BuiltInAttribute.BLINK_COUNT, 0, 1, null),
    LINE_UNDER_FREQUENCY(0x8083, BuiltInAttribute.TOTAL_LUF_COUNT, 0, 1, null),
    LINE_UNDER_VOLTAGE(0x8084, BuiltInAttribute.TOTAL_LUV_COUNT, 0, 1, null),
    
    //Events that rely on two events to complete point data
    MIN_VOLTAGE(0x809C, BuiltInAttribute.MINIMUM_VOLTAGE, 0, 2, null),
    MIN_VOLTAGE_TIME(0x809E, BuiltInAttribute.MINIMUM_VOLTAGE, 0, 4, null),
    MAX_VOLTAGE(0x809B, BuiltInAttribute.MAXIMUM_VOLTAGE, 0, 2, null),
    MAX_VOLTAGE_TIME(0x809F, BuiltInAttribute.MAXIMUM_VOLTAGE, 0, 4, null),
    ;
    
    private final Long eventIdHex;
    private final BuiltInAttribute attribute;
    private final int firstByteIndex;
    private final int numOfBytes;
    private final Integer value;
    
    private static final Logger log = YukonLogManager.getLogger(ItronDataEventType.class);
    private static final DateTime year2000 = new DateTime(2000, 1, 1, 0, 0, 0);
    
    private static final Cache<String, PointData> voltageCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();
    private static final ImmutableSet<ItronDataEventType> incrementalTypes;
    private static final ImmutableSet<ItronDataEventType> voltageTypes;
    private static final ImmutableSet<ItronDataEventType> controlEventTypes;
        
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
                                            EVENT_CANCELED,
                                            EVENT_SUPERSEDED);
    }
    
    private static Map<Long, ItronDataEventType> itronEventTypeFromHexMap = new HashMap<>();
    
    static {
        for (ItronDataEventType eventType : values()) {
            itronEventTypeFromHexMap.put(eventType.eventIdHex, eventType);
        }
    }
    
    ItronDataEventType(long eventIdHex, BuiltInAttribute attribute, int firstByteIndex, int numOfBytes, Integer value) {
        this.eventIdHex = eventIdHex;
        this.attribute = attribute;
        this.firstByteIndex = firstByteIndex;
        this.numOfBytes = numOfBytes;
        this.value = value; //Corresponds to the value in the point's state group
    }
    
    public static ItronDataEventType getFromHex(long hex) {
        return itronEventTypeFromHexMap.get(hex);
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
        // Relay number comes back 0-indexed, but relay-related attributes are 1-indexed, so increment.
        long relayNumber = decode(byteArray) + 1;
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
            log.debug("Handling data that is split across multiple entries");
            return getPointDataForMinMaxVoltage(byteArray, eventTime, lp);
        }
        
        PointData pointData = new PointData();
        pointData.setValue(getPointDataValue(byteArray, currentValue));
        pointData.setTime(new DateTime(eventTime).toDate());
        
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
    private Optional<PointData> getPointDataForMinMaxVoltage(byte[] byteArray, String dateTimeString, LitePoint point) {
        //Will eventually check to see if point exists for key: YYYY-MM-DD- + point ID + event type
        String keyPrefix = dateTimeString.substring(0, 11) + point.getLiteID();
        
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
     * @param pairedType The voltage data type (min or max, value or time) to search for in cache. For example, to 
     * process a "max voltage time", we would search cache for a matching "max voltage" value to pair with it.
     */
    private Optional<PointData> processVoltageEvent(boolean isValue, String keyPrefix, ItronDataEventType pairedType, byte[] byteArray) {
        String cacheKey = keyPrefix + pairedType.name();
        PointData pointData = voltageCache.getIfPresent(cacheKey);
        if (pointData == null) {
            log.debug("No cached entry found for {}", cacheKey);
            
            // make new, incomplete point data (with either timestamp or value) and put in cache
            pointData = new PointData();
            
            // Put either the time or the value into the pointdata
            insertValueOrTime(pointData, isValue, byteArray);
            
            String key = keyPrefix + name();
            log.debug("Caching incomplete data. Key: {}", key);
            // Put the incomplete point data into the cache with a key that combines date, point ID and event type
            voltageCache.put(key, pointData);
            return Optional.empty();
        }
        
        log.debug("Invalidating cache key: {}", cacheKey);
        voltageCache.invalidate(cacheKey);
        // Put the second part (time or value) into the pointdata
        insertValueOrTime(pointData, isValue, byteArray);
        log.debug("Completed point data - date: " + pointData.getPointDataTimeStamp() + ", value: " + pointData.getValue());
        return Optional.of(pointData);
    }
    
    /**
     * Insert a value or time into the pointData.
     */
    private void insertValueOrTime(PointData pointData, boolean isValue, byte[] byteArray) {
        long decodedData = decode(byteArray);
        if (isValue) {
            log.debug("Setting point data value: {}", decodedData);
            pointData.setValue(decodedData);
        } else {
            // Date comes in as seconds since epoch
            Date date = getLcrTimestamp(decodedData);
            log.debug("Setting point data date: {}", date.toString());
            pointData.setTime(date);
        }
    }
    
    /**
     * @return The Date representation of an LCR data timestamp, given the seconds since the start of the year 2000.
     */
    private static Date getLcrTimestamp(long secondsSinceYear2000) {
        return year2000.plus(secondsSinceYear2000 * 1000)
                       .toDate();
    }
}
