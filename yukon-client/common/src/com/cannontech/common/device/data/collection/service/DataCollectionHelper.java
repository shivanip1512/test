package com.cannontech.common.device.data.collection.service;

import static org.joda.time.Instant.now;

import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.data.collection.dao.RecentPointValueDao.RangeType;
import com.cannontech.common.util.Range;

/**
 * Helper class to provide data related to data collection
 */
public class DataCollectionHelper {
    private static final Logger log = YukonLogManager.getLogger(DataCollectionHelper.class);
    private static final Duration DAYS_7 = Duration.standardDays(7);
    private static final Duration DAYS_14 = Duration.standardDays(14);

    /**
     * Creates time ranges for each range type.
     */
    public static Map<RangeType, Range<Instant>> getRanges(Duration windowInDays) {
        // Duration days = Duration.standardDays(globalSettingDao.getInteger(GlobalSettingType.DATA_AVAILABILITY_WINDOW_IN_DAYS));
        Map<RangeType, Range<Instant>> ranges = new TreeMap<>();
        Instant startOfTheDay = new Instant(new DateTime().withTimeAtStartOfDay());
        Range<Instant> currentRange = buildRange(RangeType.AVAILABLE, ranges, startOfTheDay.minus(windowInDays), now());
        currentRange = buildRange(RangeType.EXPECTED, ranges, startOfTheDay.minus(DAYS_7), currentRange.getMin());
        currentRange = buildRange(RangeType.OUTDATED, ranges, startOfTheDay.minus(DAYS_14), currentRange.getMin());
        buildRange(RangeType.UNAVAILABLE, ranges, null, currentRange.getMin());
        return ranges;
    }

    /**
     * Constructs range, adds it list of ranges and logs the range information.
     */
    private static Range<Instant> buildRange(RangeType type, Map<RangeType, Range<Instant>> ranges, Instant min, Instant max) {
        Range<Instant> range = new Range<>(min, false, max, true);
        ranges.put(type, range);
        log.debug(getLogString(type, range));
        return range;
    }

    /**
     * Builds a string message that describes the range. Used for logging.
     */
    public static String getLogString(RangeType type, Range<Instant> range) {
        final DateTimeFormatter df = DateTimeFormat.forPattern("MMM dd YYYY HH:mm:ss");
        String min = range.getMin() == null ? "" : range.getMin().toString(df.withZone(DateTimeZone.getDefault()));
        String max = range.getMax() == null ? "" : range.getMax().toString(df.withZone(DateTimeZone.getDefault()));
        String includesMin = " [exclusive] ";
        String includesMax = " [exclusive] ";
        if (range.isIncludesMinValue()) {
            includesMin = " [inclusive] ";
        }
        if (range.isIncludesMaxValue()) {
            includesMax = " [inclusive] ";
        }
        return type + " : " + includesMin + min + " - " + includesMax + max + " ";
    }
}
