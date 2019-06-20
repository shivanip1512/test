package com.cannontech.amr.rfn.dao.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.common.stream.StreamUtils;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.db.device.RfnAddress;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

class RfnAddressCache implements DBChangeListener {

    static final Logger log = YukonLogManager.getLogger(RfnAddressCache.class);

    private static final YukonRowMapper<RfnAddress> rfnSerialRowMapper = rs -> {
        var rfnAddress = new RfnAddress();

        rfnAddress.setManufacturer(rs.getStringSafe("Manufacturer"));
        rfnAddress.setModel(rs.getStringSafe("Model"));
        rfnAddress.setSerialNumber(rs.getStringSafe("SerialNumber"));
        rfnAddress.setDeviceID(rs.getInt("DeviceId"));

        return rfnAddress;
    };

    private YukonJdbcTemplate jdbcTemplate;

    private final RfnAddressEnumCache enumCache = new RfnAddressEnumCache();
    private final RfnAddressStringCache stringCache = new RfnAddressStringCache();
    private final StampedLock cacheLock = new StampedLock();

    private Set<Integer> invalidatedPaoIds = Sets.newHashSet();

    public RfnAddressCache(YukonJdbcTemplate jdbcTemplate, AsyncDynamicDataSource dynamicDataSource) {
        this.jdbcTemplate = jdbcTemplate;

        dynamicDataSource.addDBChangeListener(this);

        List<RfnAddress> allAddresses = loadRfnAddresses();

        storeAddresses(allAddresses);
    }

    @Override
    public void dbChangeReceived(DBChangeMsg dbChange) {
        Predicate<DBChangeMsg> isDeviceChange = msg ->
                msg.getDatabase() == DBChangeMsg.CHANGE_PAO_DB 
                    && msg.getCategory().equalsIgnoreCase(PaoCategory.DEVICE.getDbString());

        switch (dbChange.getDbChangeType()) {
        case DELETE:
        case UPDATE:
            if (isDeviceChange.test(dbChange)) {
                //  Add the deleted/updated pao ID to the invalidated list to be reloaded when it is next accessed.
                withWriteLock(() -> invalidatedPaoIds.add(dbChange.getId()));
            }
            break;
        case ADD:
            if (isDeviceChange.test(dbChange)) {
                RfnAddress address = loadRfnAddress(dbChange.getId());
                if (address != null) {
                    withWriteLock(() -> storeAddress(address));
                } else {
                    log.warn("Could not load RfnAddress for paoId " + dbChange.getId());
                }
            }
            break;
        case NONE:
        default:
            break;
        }
    }

    /**
     * Acquires the cache write lock, executes the runnable, then releases the lock.
     * @param writeLockedMethod the runnable to execute
     */
    private void withWriteLock(Runnable writeLockedMethod) {
        log.trace("entering withWriteLock");
        long writeStamp = cacheLock.writeLock();
        log.trace("cache write lock acquired");
        try {
            writeLockedMethod.run();
            log.trace("runnable executed");
        } finally {
            cacheLock.unlockWrite(writeStamp);
            log.trace("cache write lock released");
        }
    }

    /**
     * Helper to upgrade a StampedLock from reader to writer, first by trying a
     * lock upgrade, then by unlocking and relocking as a writer.
     * @param lock The lock to convert to a write stamp
     * @param readStamp The read stamp currently held
     * @return The write stamp
-     */
    private static long convertToWriteLock(StampedLock lock, long readStamp) {
        log.trace("entering convertToWriteLock");
        long writeStamp = lock.tryConvertToWriteLock(readStamp);
        // Couldn't upgrade, so unlock the reader and relock as writer
        if (writeStamp == 0L) {
            log.trace("couldn't upgrade, unlocking and relocking");
            lock.unlock(readStamp);
            log.trace("cache read lock released");
            writeStamp = lock.writeLock();
            log.trace("cache write lock acquired");
        }
        log.trace("returning cache write stamp");
        return writeStamp;
    }

    /**
     * Queries cache for the rfnIdentifier's paoId, and reloads it if it has been invalidated.
     * @param rfnIdentifier the RfnAddress to look up
     * @return the paoId, or null if not found.
     */
    public Integer getPaoIdFor(RfnIdentifier rfnIdentifier) {
        log.trace("Entering getPaoIdFor " + rfnIdentifier);
        long stamp = cacheLock.readLock();
        try {
            Integer paoId = queryCacheFor(rfnIdentifier);
            //  Has the paoId been invalidated (updated or deleted)?
            if (paoId != null && invalidatedPaoIds.contains(paoId)) {
                stamp = convertToWriteLock(cacheLock, stamp);
                reloadInvalidatedPaoId(paoId, rfnIdentifier);
                paoId = queryCacheFor(rfnIdentifier);
            }
            log.trace("returning " + paoId + " for " + rfnIdentifier);
            return paoId;
        } finally {
            cacheLock.unlock(stamp);
        }
    }

    /**
     * Finds the paoId in cache for the given rfnIdentifier, if it exists. Caller must hold the cacheLock.
     */
    private Integer queryCacheFor(RfnIdentifier rfnIdentifier) {
        RfnManufacturerModel mm = RfnManufacturerModel.of(rfnIdentifier);
        if (mm != null) {
            return enumCache.query(mm, rfnIdentifier.getSensorSerialNumber());
        }
        return stringCache.query(rfnIdentifier);
    }

    /**
     * Queries cache for the rfnIdentifiers' paoIds, and reloads them if any have been invalidated.
     * @param rfnIdentifier the RfnAddress to look up
     * @return the paoId, or null if not found.
     */
    public Set<Integer> getPaoIdsFor(Iterable<RfnIdentifier> rfnIdentifiers) {
        Map<RfnManufacturerModel, List<RfnIdentifier>> manufacturerModelGrouping =
                StreamUtils.stream(rfnIdentifiers)
                    .collect(Collectors.groupingBy(RfnManufacturerModel::of));
        
        List<RfnIdentifier> stringIdentifiers = manufacturerModelGrouping.remove(null);
        
        Map<RfnManufacturerModel, List<String>> enumIdentifiers = 
                Maps.transformValues(manufacturerModelGrouping, l -> Lists.transform(l, RfnIdentifier::getSensorSerialNumber));
        
        long stamp = cacheLock.readLock();
        log.trace("Read lock acquired");
        try {
            Set<Integer> paoIds = queryCacheFor(enumIdentifiers, stringIdentifiers);
            //  Have any of the returned paoIds been updated or deleted?
            Set<Integer> toReload = Sets.intersection(paoIds, invalidatedPaoIds);
            if (!toReload.isEmpty()) {
                stamp = convertToWriteLock(cacheLock, stamp);
                Map<String, Set<String>> stringManufacturerModels = 
                        stringIdentifiers.stream()
                            .collect(Collectors.groupingBy(RfnIdentifier::getSensorManufacturer,
                                     Collectors.mapping(RfnIdentifier::getSensorModel,
                                     Collectors.toSet())));

                reloadInvalidatedPaoIds(toReload, enumIdentifiers.keySet(), stringManufacturerModels);
                paoIds = queryCacheFor(enumIdentifiers, stringIdentifiers);
            }
            log.trace("returning " + paoIds.size() + " paoIds");
            return paoIds;
        } finally {
            cacheLock.unlock(stamp);
            log.trace("Lock released");
        }
    }

    /**
     * Queries cache for the enum and string identifiers' paoIds.
     */
    private Set<Integer> queryCacheFor(Map<RfnManufacturerModel, List<String>> enumIdentifiers,
            List<RfnIdentifier> stringIdentifiers) {
        return Sets.union(stringCache.query(stringIdentifiers),
                          enumCache.query(enumIdentifiers));
    }

    /**
     * Gets an SqlStatementBuilder filled with an RfnAddress query with no WHERE clause.
     * @return The RfnAddress query
     */
    private SqlStatementBuilder getRfnAddressSql() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select DeviceId, Manufacturer, Model, SerialNumber");
        sql.append("from RfnAddress rfn");
        return sql;
    }

    /**
     * Loads the RfnIdentifier for a specific paoId.
     * @return The RfnIdentifier-to-paoId mapping, or null if none.
     */
    private RfnAddress loadRfnAddress(Integer paoId) {

        SqlStatementBuilder sql = getRfnAddressSql();
        sql.append("where DeviceId").eq(paoId);

        try {
            RfnAddress rfnAddress = jdbcTemplate.queryForObject(sql, rfnSerialRowMapper);
            log.debug("Retrieved " + rfnAddress + " for query " + sql + sql.getArgumentList());
            return rfnAddress;
        } catch (@SuppressWarnings("unused")
        EmptyResultDataAccessException e) {
            log.warn("No results returned for pao ID " + paoId);
            return null;
        }
    }

    /**
     * Loads the RfnIdentifiers for a list of paoIds.
     * @return The list of RfnIdentifier-to-paoId mappings.
     */
    private List<RfnAddress> loadRfnAddresses(Iterable<Integer> paoIds) {

        var chunkingTemplate = new ChunkingSqlTemplate(jdbcTemplate);

        var addresses = chunkingTemplate.query(idChunk -> {
            var sql = getRfnAddressSql();
            sql.append("where DeviceId").in(idChunk);
            return sql;
        }, paoIds, rfnSerialRowMapper);

        log.debug("Retrieved " + addresses.size() + " addresses");

        return addresses;
    }

    /**
     * Loads all RfnAddresses.
     * @return The list of RfnIdentifier-to-paoId mappings.
     */
    private List<RfnAddress> loadRfnAddresses() {

        SqlStatementBuilder sql = getRfnAddressSql();

        try {
            List<RfnAddress> rfnAddresses = jdbcTemplate.query(sql, rfnSerialRowMapper);
            log.debug("Retrieved " + rfnAddresses.size() + " serials for query " + sql + sql.getArgumentList());
            return rfnAddresses;
        } catch (@SuppressWarnings("unused")
        EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    private void storeAddresses(List<RfnAddress> allAddresses) {
        Map<Optional<RfnManufacturerModel>, List<RfnAddress>> enumIdentifiers = 
            allAddresses.stream()
                        .collect(Collectors.groupingBy(rfnAddress -> Optional.ofNullable(RfnManufacturerModel.of(rfnAddress))));

        List<RfnAddress> stringIdentifiers = enumIdentifiers.remove(Optional.empty());

        Map<RfnManufacturerModel, Map<String, Integer>> enumSerialIds = 
                enumIdentifiers.entrySet().stream()
                    .collect(Collectors.toMap(e -> e.getKey().get(), 
                                              e -> e.getValue().stream()
                                                    .collect(Collectors.toMap(RfnAddress::getSerialNumber, 
                                                                              RfnAddress::getDeviceID))));
        
        withWriteLock(() -> {
            stringCache.putAll(stringIdentifiers);
            enumSerialIds.forEach(enumCache::putAll);
        });
    }
    
    private void storeAddress(RfnAddress address) {
        var rfnManufacturerModel = RfnManufacturerModel.of(address);
        withWriteLock(() -> {
            Optional.ofNullable(rfnManufacturerModel)
                .ifPresentOrElse(rmm -> enumCache.put(rmm, address.getSerialNumber(), address.getDeviceID()),
                                 () -> stringCache.put(address));
        });
    }

    /**
     * Reloads a paoId that has been deleted or updated. Called lazily whenever
     * a modified paoId is returned by a getPaoId request.
     */
    private void reloadInvalidatedPaoId(Integer paoId, RfnIdentifier rfnIdentifier) {
        log.trace("Entering reloadInvalidatedPaoId");
        var rmm = RfnManufacturerModel.of(rfnIdentifier);
        if (rmm != null) {
            enumCache.removeId(paoId, rmm);
        } else {
            stringCache.removeId(paoId, rfnIdentifier);
        }
        
        Optional.ofNullable(loadRfnAddress(paoId))
            .ifPresentOrElse(this::storeAddress,
                             () -> log.trace("Could not load RfnAddress for paoId " + paoId + ", likely deleted"));
        
        invalidatedPaoIds.remove(paoId);
        
        log.trace("ReloadInvalidatedPaoId complete");
    }

    /**
     * Reloads paoIds that have been deleted or updated. Called lazily whenever
     * a modified paoId is returned by a getPaoId request.
     * @param toReload 
     * @param stringManufacturerModels 
     * @param  
     */
    private void reloadInvalidatedPaoIds(Set<Integer> toReload, 
            Set<RfnManufacturerModel> enumManufacturerModels, Map<String, Set<String>> stringManufacturerModels) {
        log.trace("Entering reloadInvalidatedPaoIds");

        enumCache.removeIds(toReload, enumManufacturerModels);
        stringCache.removeIds(toReload, stringManufacturerModels);

        List<RfnAddress> reloadedAddresses = loadRfnAddresses(toReload);
        if (!reloadedAddresses.isEmpty()) {
            withWriteLock(() -> storeAddresses(reloadedAddresses));
        }
        log.trace("Loaded " + reloadedAddresses.size() + " addresses out of " + toReload.size() + " paoIds");
        
        invalidatedPaoIds.removeAll(toReload);
        
        log.trace("ReloadInvalidatedPaoIds complete");
    }
}
