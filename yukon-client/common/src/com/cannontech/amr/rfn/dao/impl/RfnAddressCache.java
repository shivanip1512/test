package com.cannontech.amr.rfn.dao.impl;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.StampedLock;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.db.device.RfnAddress;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

class RfnAddressCache implements DBChangeListener {

    private static final Logger log = YukonLogManager.getLogger(RfnAddressCache.class);

    private static final YukonRowMapper<RfnAddress> rfnAddressRowMapper = rs -> {
        var rfnAddress = new RfnAddress();

        rfnAddress.setManufacturer(rs.getStringSafe("Manufacturer"));
        rfnAddress.setModel(rs.getStringSafe("Model"));
        rfnAddress.setSerialNumber(rs.getStringSafe("SerialNumber"));
        rfnAddress.setDeviceID(rs.getInt("DeviceId"));

        return rfnAddress;
    };

    private YukonJdbcTemplate jdbcTemplate;

    private RfnAddressIdLookup lookup = new RfnAddressIdLookup();
    
    /**
     * Use a read-write lock to improve concurrency for multi-threaded use, such as the RfnMeterReadingArchiveRequest processors.
     */
    private final StampedLock cacheLock = new StampedLock();

    /**
     * A list of paoIds that have been modified and not reloaded yet.
     * Since this is a one-way mapping of rfnAddress to paoId, we don't know which rfnAddress was modified.
     */
    private SetMultimap<DbChangeType, Integer> dbChanges = Multimaps.newSetMultimap(new EnumMap<>(DbChangeType.class), TreeSet::new);

    public RfnAddressCache(YukonJdbcTemplate jdbcTemplate, AsyncDynamicDataSource dynamicDataSource) {
        this.jdbcTemplate = jdbcTemplate;

        dynamicDataSource.addDBChangeListener(this);

        List<RfnAddress> allAddresses = loadRfnAddresses();

        withWriteLock(() -> lookup.putAll(allAddresses));
    }

    @Override
    public void dbChangeReceived(DBChangeMsg dbChange) {
        switch (dbChange.getDbChangeType()) {
        case DELETE:
        case UPDATE:
        case ADD:
            if (dbChange.getDatabase() == DBChangeMsg.CHANGE_PAO_DB &&
                dbChange.getCategory().equalsIgnoreCase(PaoCategory.DEVICE.getDbString())) {
                withWriteLock(() -> 
                    dbChanges.put(dbChange.getDbChangeType(), dbChange.getId()));
            }
            break;
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
            if (!dbChanges.isEmpty()) {
                stamp = convertToWriteLock(cacheLock, stamp);
                handleDbChanges();
            }
            Integer paoId = lookup.get(rfnIdentifier);
            log.trace("returning " + paoId + " for " + rfnIdentifier);
            return paoId;
        } finally {
            cacheLock.unlock(stamp);
        }
    }

    /**
     * Queries cache for the rfnIdentifiers' paoIds, and reloads them if any have been invalidated.
     * @param rfnIdentifier the RfnAddress to look up
     * @return the paoId, or null if not found.
     */
    public Set<Integer> getPaoIdsFor(Iterable<RfnIdentifier> rfnIdentifiers) {
        long stamp = cacheLock.readLock();
        log.trace("Read lock acquired");
        try {
            if (!dbChanges.isEmpty()) {
                stamp = convertToWriteLock(cacheLock, stamp);
                handleDbChanges();
            }
            Set<Integer> paoIds = lookup.getAll(rfnIdentifiers);
            log.trace("returning " + paoIds.size() + " paoIds");
            return paoIds;
        } finally {
            cacheLock.unlock(stamp);
            log.trace("Lock released");
        }
    }

    /**
     * Gets an SqlStatementBuilder filled with an RfnAddress query with no WHERE clause.
     * @return The RfnAddress query
     */
    private SqlStatementBuilder getRfnAddressBaseSql() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceId, Manufacturer, Model, SerialNumber");
        sql.append("FROM RfnAddress rfn");
        return sql;
    }

    /**
     * Loads the RfnIdentifiers for a list of paoIds.
     * @return The list of RfnIdentifier-to-paoId mappings.
     */
    private List<RfnAddress> loadRfnAddresses(Iterable<Integer> paoIds) {

        var chunkingTemplate = new ChunkingSqlTemplate(jdbcTemplate);

        var addresses = chunkingTemplate.query(idChunk -> {
            var sql = getRfnAddressBaseSql();
            sql.append("where DeviceId").in(idChunk);
            return sql;
        }, paoIds, rfnAddressRowMapper);

        log.debug("Retrieved " + addresses.size() + " addresses");

        return addresses;
    }

    /**
     * Loads all RfnAddresses.
     * @return The list of RfnIdentifier-to-paoId mappings.
     */
    private List<RfnAddress> loadRfnAddresses() {

        SqlStatementBuilder sql = getRfnAddressBaseSql();

        try {
            List<RfnAddress> rfnAddresses = jdbcTemplate.query(sql, rfnAddressRowMapper);
            log.debug("Retrieved " + rfnAddresses.size() + " serials for query " + sql + sql.getArgumentList());
            return rfnAddresses;
        } catch (@SuppressWarnings("unused")
        EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    /**
     * Handles paoIds that have been deleted, updated, or added. Called lazily whenever
     * the next getPaoIdFor() or getPaoIdsFor() call is made.
     */
    private void handleDbChanges() {
        log.trace("Entering handleDbChanges");

        if (dbChanges.isEmpty()) {
            log.trace("dbChanges empty, returning early");
            return;
        }
        
        Set<Integer> deletes = dbChanges.get(DbChangeType.DELETE);
        Set<Integer> updates = dbChanges.get(DbChangeType.UPDATE);
        Set<Integer> inserts = dbChanges.get(DbChangeType.ADD);
        
        Set<Integer> removals = Sets.union(deletes, updates);
        
        lookup.remove(removals);

        Set<Integer> loads = Sets.union(updates, inserts);
        
        List<RfnAddress> addresses = loadRfnAddresses(loads);

        lookup.putAll(addresses);

        log.trace("Loaded " + addresses.size() + " addresses out of " + loads.size() + " paoIds");

        dbChanges.clear();

        log.trace("handleDbChanges complete");
    }
}
