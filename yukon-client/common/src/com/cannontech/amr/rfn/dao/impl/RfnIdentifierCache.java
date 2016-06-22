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
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.message.dispatch.message.DBChangeMsg;

class RfnIdentifierCache implements DBChangeListener {

    private final static Logger log = Logger.getLogger(RfnIdentifierCache.class);

    private final static int SERIAL_DIGITS = 5;
    private final static int SERIALS_PER_SELECT = 250;

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
    
    private YukonJdbcTemplate jdbcTemplate;
    
    //  This cache only includes entries that uniquely map to an RfnManufacturerModel.
    //    This currently excludes gateways, relays, Focus 410s, and a few Focus 420s.  See RfnManufacturerModel for details.
    //  If we need to reduce the memory use of these caches, Trove primitive collections might be a reasonable replacement. (https://bitbucket.org/trove4j/trove)
    private Map<RfnManufacturerModel, Map<Long, Integer>> rfnNumericSerials = new EnumMap<>(RfnManufacturerModel.class); 
    private Map<RfnManufacturerModel, PatriciaTrie<Integer>> rfnStringSerials = new EnumMap<>(RfnManufacturerModel.class);
    private final ReadWriteLock cacheLock = new ReentrantReadWriteLock(true);
    
    private Set<Integer> invalidatedPaoIds = new HashSet<Integer>();

    public RfnIdentifierCache(YukonJdbcTemplate jdbcTemplate, AsyncDynamicDataSource dynamicDataSource) {
        this.jdbcTemplate = jdbcTemplate;
        
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

    /**
     * Adds the paoId to the list of PAOs requiring reload.  If the PAO is deleted, it will remain in this list until restart.
     * This assumes a relatively small number of deletions occur per runtime. 
     */
    private void invalidatePaoId(int paoId) {
        Lock writeLock = cacheLock.writeLock();
        try {
            writeLock.lock();
            invalidatedPaoIds.add(paoId);
        } finally {
            writeLock.unlock();
        }
    }

    /** 
     * Attempts to load and cache the paoId for the specified RfnManufacturerModel and serial.
     * @return the associated paoId, or null if not found.
     */
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
    
    /**
     * Attempts to parse the serial string as a Long.
     * @param serial The serial string to parse.
     * @return The serial as a Long, or null if it is not digits.
     * @throws NumberFormatException
     */
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
    
    /**
     * Generics shim to allow String or Long access to the serial-to-paoId maps.
     * @return the paoId, or null if not found or invalidated.
     */
    private <T, U extends Map<T, Integer>> Integer getPaoIdForSerial(RfnManufacturerModel mm, T serial, Map<RfnManufacturerModel, U> cache) {
        Lock readLock = cacheLock.readLock(); 
        try {
            readLock.lock();
            return Optional.ofNullable(cache.get(mm))
                    .map(serialPaoIdMap -> serialPaoIdMap.get(serial))
                    .filter(id -> !invalidatedPaoIds.contains(id))
                    .orElse(null);
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Refreshes the caches with the list of serial-to-pao mappings, and scans for the original serial's pao ID.
     * @return the paoId of the original serial requested, or null if not found.
     */
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

    /**
     * Loads a list of serial numbers similar to the serial passed in.
     * @param mm The Manufacturer and Model to look up.
     * @param serial The serial number to base the load on.
     * @return The list of serial-to-paoId mappings.
     */
    private List<RfnSerialPaoId> loadSerials(RfnManufacturerModel mm, String serial) {
        
        List<String> similarSerials = buildSerialRange(serial);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select DeviceId, SerialNumber");
        sql.append("from RfnAddress rfn");
        sql.append("where Manufacturer").eq(mm.getManufacturer());
        sql.append("and Model").eq(mm.getModel());
        if (!similarSerials.isEmpty()) {
            sql.append("and SerialNumber").in(similarSerials);
            log.debug("Ranged load [" + similarSerials.get(0) + "-" + similarSerials.get(similarSerials.size() - 1) + "] for " + mm + serial);
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

    /**
     * Builds a list of 250 similar serials if an RFN serial number has a numeric suffix of 5 digits or more.<br>
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
     * <ul><li><tt>B02984 &rarr; [B02750, B02751, ..., B02999]</tt></li>
     * <li><tt>B99994 &rarr; [B99750, B99751, ..., B99999]</tt></li>
     * <li><tt>31415927 &rarr; [31415750, 31415751, ..., 31415999]</tt></li>
     * <li><tt>314159 &rarr; [314000, 314001, ..., 314249]</tt></li>
     * <li><tt>3141 &rarr; []</tt></li>
     * <li><tt>banana &rarr; []</tt></li></ul>
     *  
     * @param sensorSerialNumber
     * @return a list of similar serials, or an empty list if the serial did not have a numeric suffix.
     */
    private static List<String> buildSerialRange(String sensorSerialNumber) {
        if (sensorSerialNumber.length() >= SERIAL_DIGITS) {
            String suffix = sensorSerialNumber.substring(sensorSerialNumber.length() - SERIAL_DIGITS);
            String prefix = sensorSerialNumber.substring(0, sensorSerialNumber.length() - SERIAL_DIGITS);
            if (NumberUtils.isDigits(suffix)) {
                try {
                    Long longSuffix = Long.parseUnsignedLong(suffix);
                    longSuffix -= longSuffix % SERIALS_PER_SELECT;
                    return LongStream
                            .range(longSuffix, longSuffix + SERIALS_PER_SELECT)
                            .mapToObj(Long::toString)
                            .map(longStr -> StringUtils.leftPad(longStr, SERIAL_DIGITS, '0'))
                            .map(paddedStr -> prefix + paddedStr)
                            .collect(Collectors.toList());
                } catch (NumberFormatException e) {
                    log.warn("Serial suffix was digits, but could not parse as Long: " + sensorSerialNumber);
                }
            } else {
                log.trace("Serial suffix was not digits: " + sensorSerialNumber);
            }
        } else {
            log.debug("Serial was shorter than " + SERIAL_DIGITS + " characters: " + sensorSerialNumber);
        }
        
        return Collections.emptyList();
    }
}
