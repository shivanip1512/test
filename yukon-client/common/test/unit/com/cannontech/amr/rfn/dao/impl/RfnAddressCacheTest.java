package com.cannontech.amr.rfn.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.common.mock.MockDataSource;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.core.dynamic.impl.MockAsyncDynamicDataSourceImpl;
import com.cannontech.database.TestResultSet;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.db.device.RfnAddress;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.collect.Iterables;

public class RfnAddressCacheTest {

    private static final RfnIdentifier rfnId1 = new RfnIdentifier("11235813", "ITRN", "C2SX"); 
    private static final RfnIdentifier rfnId2 = new RfnIdentifier("31415927", "ITRN", "C2SX"); 
    private static final RfnIdentifier rfnId2_updated = new RfnIdentifier("62831853", "ITRN", "C2SX"); 
    private static final RfnIdentifier rfnId3 = new RfnIdentifier("7800000024", "CPS", "RFGateway2");
    private static final RfnIdentifier rfnId4 = new RfnIdentifier("Jambalaya", "CPS", "RFGateway2"); 
    private static final RfnIdentifier nonexistent = new RfnIdentifier("apple", "banana", "cucumber"); 
    
    @Test
    public void test_emptyLookup() {
        var template = new RfnAddressJdbcTemplate(Collections.emptyList());
        
        var rfnAddressCache = new RfnAddressCache(template, new MockAsyncDynamicDataSourceImpl());

        assertNull("Nonexistent lookup", rfnAddressCache.getPaoIdFor(rfnId1));
        assertNull("Nonexistent lookup", rfnAddressCache.getPaoIdFor(rfnId2));
        assertNull("Nonexistent lookup", rfnAddressCache.getPaoIdFor(rfnId3));
        assertNull("Nonexistent lookup", rfnAddressCache.getPaoIdFor(rfnId4));
        assertNull("Nonexistent lookup", rfnAddressCache.getPaoIdFor(nonexistent));
    }

    @Test
    public void test_singleLookup() {
        var template = new RfnAddressJdbcTemplate(List.of(makeRfnAddress(17, rfnId1)));
        
        var rfnAddressCache = new RfnAddressCache(template, new MockAsyncDynamicDataSourceImpl());

        assertEquals("Numeric serial lookup", Integer.valueOf(17), rfnAddressCache.getPaoIdFor(rfnId1));
        assertNull("Nonexistent lookup", rfnAddressCache.getPaoIdFor(rfnId2));
        assertNull("Nonexistent lookup", rfnAddressCache.getPaoIdFor(rfnId3));
        assertNull("Nonexistent lookup", rfnAddressCache.getPaoIdFor(rfnId4));
        assertNull("Nonexistent lookup", rfnAddressCache.getPaoIdFor(nonexistent));
    }
    
    @Test
    public void test_multipleLookup() {
        var template = new RfnAddressJdbcTemplate(List.of(makeRfnAddress(22, rfnId1),
                                                          makeRfnAddress(23, rfnId2),
                                                          makeRfnAddress(24, rfnId3),
                                                          makeRfnAddress(25, rfnId4)));
        
        var rfnAddressCache = new RfnAddressCache(template, new MockAsyncDynamicDataSourceImpl());

        assertEquals("Numeric lookup", Integer.valueOf(22), rfnAddressCache.getPaoIdFor(rfnId1));
        assertEquals("Numeric lookup", Integer.valueOf(23), rfnAddressCache.getPaoIdFor(rfnId2));
        assertEquals("Gateway lookup", Integer.valueOf(24), rfnAddressCache.getPaoIdFor(rfnId3));
        assertEquals("Text lookup", Integer.valueOf(25), rfnAddressCache.getPaoIdFor(rfnId4));
        assertNull("Nonexistent lookup", rfnAddressCache.getPaoIdFor(nonexistent));
        
        Set<Integer> multiple = rfnAddressCache.getPaoIdsFor(List.of(rfnId1, rfnId2, rfnId3, rfnId4, nonexistent));
        
        assertEquals("Multiple lookup", Set.of(22, 23, 24, 25), multiple);
    }
    
    @Test
    public void test_singleDelete() {
        var template = new RfnAddressJdbcTemplate(List.of(makeRfnAddress(22, rfnId1),
                                                          makeRfnAddress(23, rfnId2),
                                                          makeRfnAddress(24, rfnId3),
                                                          makeRfnAddress(25, rfnId4)));
        
        var rfnAddressCache = new RfnAddressCache(template, new MockAsyncDynamicDataSourceImpl());

        var dbChange = new DBChangeMsg(24, DBChangeMsg.CHANGE_PAO_DB, PaoCategory.DEVICE.getDbString(), DbChangeType.DELETE);
        
        rfnAddressCache.dbChangeReceived(dbChange);
        
        Set<Integer> multiple = rfnAddressCache.getPaoIdsFor(List.of(rfnId1, rfnId2, rfnId3, rfnId4, nonexistent));
        
        assertEquals("Multiple lookup", Set.of(22, 23, 25), multiple);
    }
    
    @Test
    public void test_singleAdd() {
        var template = new RfnAddressJdbcTemplate(List.of(makeRfnAddress(22, rfnId1),
                                                          makeRfnAddress(23, rfnId2),
                                                          makeRfnAddress(24, rfnId3)));
        
        var rfnAddressCache = new RfnAddressCache(template, new MockAsyncDynamicDataSourceImpl());

        template.setData(List.of(makeRfnAddress(25, rfnId4)));
        
        var dbChange = new DBChangeMsg(25, DBChangeMsg.CHANGE_PAO_DB, PaoCategory.DEVICE.getDbString(), DbChangeType.ADD);
        
        rfnAddressCache.dbChangeReceived(dbChange);
        
        Set<Integer> multiple = rfnAddressCache.getPaoIdsFor(List.of(rfnId1, rfnId2, rfnId3, rfnId4, nonexistent));
        
        assertEquals("Multiple lookup", Set.of(22, 23, 24, 25), multiple);
    }
    
    @Test
    public void test_singleUpdate() {
        var template = new RfnAddressJdbcTemplate(List.of(makeRfnAddress(22, rfnId1),
                                                          makeRfnAddress(23, rfnId2),
                                                          makeRfnAddress(24, rfnId3),
                                                          makeRfnAddress(25, rfnId4)));
        
        var rfnAddressCache = new RfnAddressCache(template, new MockAsyncDynamicDataSourceImpl());

        template.setData(List.of(makeRfnAddress(23, rfnId2_updated)));
        
        assertEquals("Numeric lookup", Integer.valueOf(23), rfnAddressCache.getPaoIdFor(rfnId2));
        assertNull("Nonexistent lookup", rfnAddressCache.getPaoIdFor(rfnId2_updated));

        var dbChange = new DBChangeMsg(23, DBChangeMsg.CHANGE_PAO_DB, PaoCategory.DEVICE.getDbString(), DbChangeType.UPDATE);
        
        rfnAddressCache.dbChangeReceived(dbChange);
        
        assertNull("Nonexistent lookup", rfnAddressCache.getPaoIdFor(rfnId2));
        assertEquals("Numeric lookup", Integer.valueOf(23), rfnAddressCache.getPaoIdFor(rfnId2_updated));
    }
    
    @Test
    public void test_addThenUpdate() {
        var template = new RfnAddressJdbcTemplate(List.of(makeRfnAddress(22, rfnId1),
                                                          makeRfnAddress(23, rfnId2),
                                                          makeRfnAddress(25, rfnId4)));
        
        var rfnAddressCache = new RfnAddressCache(template, new MockAsyncDynamicDataSourceImpl());

        assertNull("Nonexistent lookup", rfnAddressCache.getPaoIdFor(rfnId3));

        template.setData(List.of(makeRfnAddress(24, rfnId3)));
        
        rfnAddressCache.dbChangeReceived(new DBChangeMsg(24, DBChangeMsg.CHANGE_PAO_DB, PaoCategory.DEVICE.getDbString(), DbChangeType.ADD));
        rfnAddressCache.dbChangeReceived(new DBChangeMsg(24, DBChangeMsg.CHANGE_PAO_DB, PaoCategory.DEVICE.getDbString(), DbChangeType.UPDATE));

        assertEquals("Numeric lookup", Integer.valueOf(24), rfnAddressCache.getPaoIdFor(rfnId3));
    }
    
    @Test
    public void test_addUpdateDelete() {
        var template = new RfnAddressJdbcTemplate(List.of(makeRfnAddress(22, rfnId1),
                                                          makeRfnAddress(23, rfnId2),
                                                          makeRfnAddress(25, rfnId4)));
        
        var rfnAddressCache = new RfnAddressCache(template, new MockAsyncDynamicDataSourceImpl());

        assertNull("Nonexistent lookup", rfnAddressCache.getPaoIdFor(rfnId3));

        template.setData(List.of(makeRfnAddress(24, rfnId3)));  //  Provide the data in case they try to load (which they shouldn't)
        
        rfnAddressCache.dbChangeReceived(new DBChangeMsg(24, DBChangeMsg.CHANGE_PAO_DB, PaoCategory.DEVICE.getDbString(), DbChangeType.ADD));
        rfnAddressCache.dbChangeReceived(new DBChangeMsg(24, DBChangeMsg.CHANGE_PAO_DB, PaoCategory.DEVICE.getDbString(), DbChangeType.UPDATE));
        rfnAddressCache.dbChangeReceived(new DBChangeMsg(24, DBChangeMsg.CHANGE_PAO_DB, PaoCategory.DEVICE.getDbString(), DbChangeType.DELETE));

        assertNull("Nonexistent lookup", rfnAddressCache.getPaoIdFor(rfnId3));
    }
    
    @Test
    public void test_swapIdentifiers() {
        var template = new RfnAddressJdbcTemplate(List.of(makeRfnAddress(22, rfnId1),
                                                          makeRfnAddress(23, rfnId2),
                                                          makeRfnAddress(24, rfnId3),
                                                          makeRfnAddress(25, rfnId4)));
        
        var rfnAddressCache = new RfnAddressCache(template, new MockAsyncDynamicDataSourceImpl());

        assertEquals("Numeric lookup", Integer.valueOf(23), rfnAddressCache.getPaoIdFor(rfnId2));
        assertEquals("Numeric lookup", Integer.valueOf(24), rfnAddressCache.getPaoIdFor(rfnId3));

        //  A true swap requires three DBChanges due to the uniqueness 
        //    constraints on RfnAddress - changing A to tmp, B to A, then tmp to B.
        //  We just show the end result - two DB rows, three DBChanges.
        template.setData(List.of(makeRfnAddress(23, rfnId3),
                                 makeRfnAddress(24, rfnId2)));
        
        rfnAddressCache.dbChangeReceived(new DBChangeMsg(23, DBChangeMsg.CHANGE_PAO_DB, PaoCategory.DEVICE.getDbString(), DbChangeType.UPDATE));
        rfnAddressCache.dbChangeReceived(new DBChangeMsg(24, DBChangeMsg.CHANGE_PAO_DB, PaoCategory.DEVICE.getDbString(), DbChangeType.UPDATE));
        rfnAddressCache.dbChangeReceived(new DBChangeMsg(23, DBChangeMsg.CHANGE_PAO_DB, PaoCategory.DEVICE.getDbString(), DbChangeType.UPDATE));
        
        assertEquals("Numeric lookup", Integer.valueOf(24), rfnAddressCache.getPaoIdFor(rfnId2));
        assertEquals("Numeric lookup", Integer.valueOf(23), rfnAddressCache.getPaoIdFor(rfnId3));
    }
    
    private static RfnAddress makeRfnAddress(int paoId, RfnIdentifier rfnIdentifier) {
        var rfnAddress = new RfnAddress();
        rfnAddress.setManufacturer(rfnIdentifier.getSensorManufacturer());
        rfnAddress.setModel(rfnIdentifier.getSensorModel());
        rfnAddress.setSerialNumber(rfnIdentifier.getSensorSerialNumber());
        rfnAddress.setDeviceID(paoId);
        return rfnAddress;
    }

    private static class RfnAddressJdbcTemplate extends YukonJdbcTemplate {
        private List<RfnAddress> data;
        
        public RfnAddressJdbcTemplate(List<RfnAddress> data) {
            super(new MockDataSource());
            setData(data);
        }
        
        public void setData(List<RfnAddress> data) {
            this.data = data;
        }

        private List<RfnAddress> getDataOnce() {
            var queryData = data;
            setData(Collections.emptyList());
            return queryData;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T queryForObject(SqlFragmentSource sql, RowMapper<T> rm) {
            return (T) Iterables.getFirst(getDataOnce(), null);
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <T> List<T> query(String sql, RowMapper<T> rm) {
            return (List<T>) getDataOnce();
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <T> List<T> query(SqlFragmentSource sql, YukonRowMapper<T> rm) {
            return (List<T>) getDataOnce();
        }
        
        @SuppressWarnings("serial")
        @Override
        public void query(String sql, Object[] args, RowCallbackHandler rch) {
            try {
                for (RfnAddress address : getDataOnce()) {
                    rch.processRow(createResultSet(address));
                }
            } catch (SQLException e) {
                throw new DataAccessException("Broken", e) {};
            }
        }
        
        private static ResultSet createResultSet(RfnAddress rfnAddress) {
            return new TestResultSet() {
                @Override
                public String getString(String column) {
                    switch (column.toLowerCase()) {
                    case "manufacturer":  
                        return rfnAddress.getManufacturer();
                    case "model":
                        return rfnAddress.getModel();
                    case "serialnumber":
                        return rfnAddress.getSerialNumber();
                    default:
                        return null;
                    }
                }
                @Override
                public int getInt(String column) {
                    if (column.equalsIgnoreCase("deviceid")) {
                        return rfnAddress.getDeviceID();
                    }
                    return -999;
                }
            };
        }
    }
}
