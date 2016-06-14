package com.cannontech.amr.rfn.dao.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.amr.rfn.dao.RfnIdentifierCache;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.google.common.collect.Range;
import com.google.common.collect.Ranges;

class RfnIdentifierCacheImpl implements DBChangeListener, RfnIdentifierCache {

    private final static Logger log = Logger.getLogger(RfnIdentifierCacheImpl.class);

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private AsyncDynamicDataSource dynamicDataSource;
    
    //  This cache only includes entries that uniquely map to an RfnManufacturerModel.
    //    This currently excludes gateways, relays, Focus 410s, and a few Focus 420s.  See RfnManufacturerModel for details.
    //  If we need to reduce the memory use of these caches, Trove primitive collections might be a reasonable replacement. (https://bitbucket.org/trove4j/trove)
    private Map<RfnManufacturerModel, Map<Long, Integer>> rfnNumericSerials = new EnumMap<>(RfnManufacturerModel.class); 
    private Map<RfnManufacturerModel, PatriciaTrie<Integer>> rfnStringSerials = new EnumMap<>(RfnManufacturerModel.class);
    private final ReadWriteLock cacheLock = new ReentrantReadWriteLock(true);
    
    private Set<Integer> invalidatedPaoIds = new HashSet<Integer>();

    @PostConstruct
    private void listenForDbChanges() {
        dynamicDataSource.addDBChangeListener(this);
    }
    
    @Override
    public void dbChangeReceived(DBChangeMsg dbChange) {
        switch (dbChange.getDbChangeType()) {
        case DELETE:
        case UPDATE:
            if (dbChange.getDatabase() == DBChangeMsg.CHANGE_PAO_DB) {
                if (dbChange.getCategory().equalsIgnoreCase(PaoCategory.DEVICE.getDbString())) {
                    invalidatePaoId(dbChange.getId());
                }
            }
        default:
            break;
        }
    }

    @Override
    public Integer getPaoIdFor(RfnManufacturerModel mm, String serial) {
        Integer paoId = null;
        Long numericSerial = tryParseSerialAsLong(serial);
        if (numericSerial != null) {
            paoId = getPaoIdForSerial(mm, numericSerial, rfnNumericSerials);
        } else {
            paoId = getPaoIdForSerial(mm, serial, rfnStringSerials);
        }
        if (paoId == null) {
            //  If not found in cache, try to populate the cache with a chunk of similar devices
            List<RfnSerialPaoId> paoIdsToSerials = loadSerials(mm, serial);

            paoId = refreshSerialMap(mm, paoIdsToSerials, serial);
        }
        return paoId;
    }
    
    private Long tryParseSerialAsLong(String serial) {
        if (NumberUtils.isDigits(serial)) {
            try {
                return Long.parseUnsignedLong(serial);
            } catch (NumberFormatException ex) {
                log.debug("RFN serial is all digits, but cannot be parsed as a Long:" + serial);
            }
        }
        return null;
    }
    
    private <T, U extends Map<T, Integer>> Integer getPaoIdForSerial(RfnManufacturerModel mm, T serial, Map<RfnManufacturerModel, U> cache) {
        Lock readLock = cacheLock.readLock(); 
        try {
            readLock.lock();
            return Optional.ofNullable(cache.get(mm))
                    .map(serialPaoIdMap -> serialPaoIdMap.get(serial))
                    .filter(id -> ! invalidatedPaoIds.contains(id))
                    .orElse(null);
        } finally {
            readLock.unlock();
        }
    }
    
    private Integer refreshSerialMap(RfnManufacturerModel mm, List<RfnSerialPaoId> serialIds, String originalSerial) {
        if (serialIds.isEmpty()) {
            return null;
        }

        Map<String, Integer> stringSerials = new HashMap<>();
        Map<Long, Integer> numericSerials = new HashMap<>();
        Set<Integer> paoIds = new HashSet<>();
        Integer originalPaoId = null;
        
        for (RfnSerialPaoId serialId : serialIds) {
            if (serialId.serial.equals(originalSerial)) {
                originalPaoId = serialId.paoId;
            }
            Long numericSerial = tryParseSerialAsLong(serialId.serial);
            if (numericSerial != null) {
                numericSerials.put(numericSerial, serialId.paoId);
            } else {
                stringSerials.put(serialId.serial, serialId.paoId);
            }
            paoIds.add(serialId.paoId);
        }
        
        Lock writeLock = cacheLock.writeLock();
        try {
            writeLock.lock();
            getNumericSerialMapFor(mm).putAll(numericSerials);
            getStringSerialMapFor(mm).putAll(stringSerials);
            invalidatedPaoIds.removeAll(paoIds);
        } finally {
            writeLock.unlock();
        }
        return originalPaoId;
    }
    
    private Map<Long, Integer> getNumericSerialMapFor(RfnManufacturerModel mm) {
        return rfnNumericSerials.computeIfAbsent(mm, unused -> new HashMap<>());
    }

    private Map<String, Integer> getStringSerialMapFor(RfnManufacturerModel mm) {
        return rfnStringSerials.computeIfAbsent(mm, unused -> new PatriciaTrie<Integer>());
    }

    @Override
    public void invalidatePaoId(int paoId) {
        Lock writeLock = cacheLock.writeLock();
        try {
            writeLock.lock();
            invalidatedPaoIds.add(paoId);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void updatePaoId(int paoId, RfnIdentifier rfnIdentifier) {
        RfnManufacturerModel mm = RfnManufacturerModel.of(rfnIdentifier);
        if (mm != null) {
            String serial = rfnIdentifier.getSensorSerialNumber();
            Long numericSerial = tryParseSerialAsLong(serial);
            //  This acquires the write lock and will contend with other cache access.
            //    This is currently only called from RfnDeviceDaoImpl.updateDevice(), so it should be rare.
            //    If it becomes a bottleneck, the updates could be buffered into a separate ConcurrentMap<RfnIdentifier, Integer>.
            Lock writeLock = cacheLock.writeLock();
            try {
                writeLock.lock();
                if (numericSerial != null) {
                    getNumericSerialMapFor(mm).put(numericSerial, paoId);
                } else {
                    getStringSerialMapFor(mm).put(serial, paoId);
                }
                //invalidatedPaoIds.remove(paoId);
            } finally {
                writeLock.unlock();
            }
        }
    }

    private final static class RfnSerialPaoId {
        RfnSerialPaoId(String serial, int paoId) {
            this.serial = serial;
            this.paoId = paoId;
        }
        public final String serial;
        public final int paoId;
    }
    
    private final static YukonRowMapper<RfnSerialPaoId> rfnSerialRowMapper = new YukonRowMapper<RfnSerialPaoId>() {
        @Override
        public RfnSerialPaoId mapRow(YukonResultSet rs) throws SQLException {
            
            String serial = rs.getStringSafe("SerialNumber");
            int paoId = rs.getInt("DeviceId");
            
            return new RfnSerialPaoId(serial, paoId);
        }
    };
    
    private List<RfnSerialPaoId> loadSerials(RfnManufacturerModel mm, String serial) {
        
        Range<String> similarSerials = buildSerialRange(serial);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select DeviceId, SerialNumber");
        sql.append("from RfnAddress rfn");
        sql.append("where Manufacturer").eq(mm.getManufacturer());
        sql.append("and Model").eq(mm.getModel());
        if (similarSerials != null) {
            //  This check has an execution plan that is almost as fast as the single .eq() check.
            //    It is 2-3 times faster than a LIKE clause or an IN() list with 10 entries.
            sql.append("and SerialNumber").gte(similarSerials.lowerEndpoint());
            sql.append("and SerialNumber").lt(similarSerials.upperEndpoint());
            sql.append("and LEN(SerialNumber)").eq(serial.length());
            log.debug("Ranged load " + similarSerials + " for " + mm + serial);
        } else {
            sql.append("and SerialNumber").eq(serial);
            log.debug("Directed load for " + mm + serial);
        }

        try {
            List<RfnSerialPaoId> serialIds = jdbcTemplate.query(sql, rfnSerialRowMapper);
            log.debug("Retrieved " + serialIds.size() + " serials for query " + sql + sql.getArgumentList());
            return serialIds;
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    final static int SERIAL_DIGITS = 5;
    final static int TRUNCATED_DIGITS = 3;
    final static int TRUNCATION_FACTOR = 1000;
    final static int OVERFLOW_LIMIT = 99000;

    /**
     * Builds a range of 1000 devices if an RFN serial number has a numeric suffix of 5 digits or more.<br>Does not attempt to handle rollover past 5 digits, so serials in the range &hellip;99000-&hellip;99999 will be loaded individually.
     * <p>Most non-trivial serial numbers in the RFN firmware and QA test labs are 8-10 characters long, such as:
     * <ul><li><tt>88638088</tt> (8 chars, ITRN C2SX-SD)</li>
     * <li><tt>133058796</tt> (9 chars, LGYR FocusAXD-SD-500)</li>
     * <li><tt>B61667274</tt> (9 chars, Eka water_sensor)</li>
     * <li><tt>660000134</tt> (9 chars, CPS 1077 aka LCR-6200)</li>
     * <li><tt>710330779</tt> (9 chars, CPS 1077 aka LCR-6200)</li>
     * <li><tt>720002053</tt> (9 chars, CPS 1082 aka LCR-6600)</li>
     * <li><tt>999620001</tt> (9 chars, CPS 1077 aka LCR-6200)</li>
     * <li><tt>7800000033</tt> (10 chars, CPS RFGateway2).</li></ul>
     * <p>Example inputs and outputs:
     * <ul><li><tt>B02984 &rarr; [B02, B03)</tt></li>
     * <li><tt>31415927 &rarr; [31415, 31416)</tt></li>
     * <li><tt>3141 &rarr; null</tt></li>
     * <li><tt>B99994 &rarr; null</tt></li>
     * <li><tt>banana &rarr; null</tt></li></ul>
     *  
     * @param sensorSerialNumber
     * @return a Range representing the bounds of the serial, or <tt>null</tt> if none could be created.
     */
    private static Range<String> buildSerialRange(String sensorSerialNumber) {
        if (sensorSerialNumber.length() >= SERIAL_DIGITS) {
            String suffix = sensorSerialNumber.substring(sensorSerialNumber.length() - SERIAL_DIGITS);
            String prefix = sensorSerialNumber.substring(0, sensorSerialNumber.length() - SERIAL_DIGITS);
            if (NumberUtils.isDigits(suffix)) {
                try {
                    Long longSuffix = Long.parseUnsignedLong(suffix);
                    //  Make sure we won't roll over to the 6th digit
                    if (longSuffix < OVERFLOW_LIMIT) {
                        longSuffix = longSuffix / TRUNCATION_FACTOR + 1;
                        
                        return Ranges.openClosed(
                                sensorSerialNumber.substring(0, sensorSerialNumber.length() - TRUNCATED_DIGITS),
                                //  Note that 02 (the padding) is SERIAL_DIGITS - TRUNCATED_DIGITS, or 5 - 3
                                prefix + String.format("%1$02d", longSuffix));
                    }
                } catch (NumberFormatException e) {
                    log.warn("Serial suffix was digits, but could not parse as Long: " + sensorSerialNumber);
                }
            } else {
                log.trace("Serial suffix was not digits: " + sensorSerialNumber);
            }
        } else {
            log.debug("Serial was shorter than " + SERIAL_DIGITS + " characters: " + sensorSerialNumber);
        }
        
        return null;
    }
}
