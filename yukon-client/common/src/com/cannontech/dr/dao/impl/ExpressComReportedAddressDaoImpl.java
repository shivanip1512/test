package com.cannontech.dr.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.dao.ExpressComReportedAddress;
import com.cannontech.dr.dao.ExpressComReportedAddressDao;
import com.cannontech.dr.dao.ExpressComReportedAddressRelay;
import com.google.common.collect.Sets;

public class ExpressComReportedAddressDaoImpl implements ExpressComReportedAddressDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    
    private SimpleTableAccessTemplate<ExpressComReportedAddress> addressTemplate;
    
    private final static FieldMapper<ExpressComReportedAddress> fieldMapper = new FieldMapper<ExpressComReportedAddress>() {
        @Override
        public Number getPrimaryKey(ExpressComReportedAddress address) {
            return address.getChangeId();
        }

        @Override
        public void setPrimaryKey(ExpressComReportedAddress address, int changeId) {
            address.setChangeId(changeId);
        }

        @Override
        public void extractValues(MapSqlParameterSource parameterHolder, ExpressComReportedAddress address) {
            parameterHolder.addValue("DeviceId", address.getDeviceId());
            parameterHolder.addValue("Timestamp", address.getTimestamp());
            parameterHolder.addValue("SPID", address.getSpid());
            parameterHolder.addValue("GEO", address.getGeo());
            parameterHolder.addValue("Substation", address.getSubstation());
            parameterHolder.addValue("Feeder", address.getFeeder());
            parameterHolder.addValue("ZIP", address.getZip());
            parameterHolder.addValue("UDA", address.getUda());
            parameterHolder.addValue("Required", address.getRequired());
        }
    };
    
    @PostConstruct
    public void init() {
        addressTemplate = new SimpleTableAccessTemplate<ExpressComReportedAddress>(yukonJdbcTemplate, nextValueHelper);
        addressTemplate.setTableName("ReportedAddressExpressCom");
        addressTemplate.setFieldMapper(fieldMapper);
        addressTemplate.setPrimaryKeyField("ChangeId");
        addressTemplate.setPrimaryKeyValidOver(0);
    }
    
    YukonRowMapper<ExpressComReportedAddress> addressRowMapper = new YukonRowMapper<ExpressComReportedAddress>() {

        @Override
        public ExpressComReportedAddress mapRow(YukonResultSet rs) throws SQLException {
            ExpressComReportedAddress address = new ExpressComReportedAddress();
            
            address.setChangeId(rs.getInt("ChangeId"));
            address.setDeviceId(rs.getInt("DeviceId"));
            address.setTimestamp(rs.getInstant("Timestamp"));
            address.setSpid(rs.getInt("SPID"));
            address.setGeo(rs.getInt("GEO"));
            address.setSubstation(rs.getInt("Substation"));
            address.setFeeder(rs.getInt("Feeder"));
            address.setZip(rs.getInt("ZIP"));
            address.setUda(rs.getInt("UDA"));
            address.setRequired(rs.getInt("Required"));
            
            return address;
        }
        
    };
    
    YukonRowMapper<ExpressComReportedAddressRelay> relayMapper = new YukonRowMapper<ExpressComReportedAddressRelay>() {

        @Override
        public ExpressComReportedAddressRelay mapRow(YukonResultSet rs) throws SQLException {
            var relay = new ExpressComReportedAddressRelay(rs.getInt("RelayNumber"));
            
            relay.setProgram(rs.getInt("Program"));
            relay.setSplinter(rs.getInt("Splinter"));
            
            return relay;
        }
        
    };
    
    @Override
    @Transactional
    public boolean save(ExpressComReportedAddress address, ExpressComReportedAddress currentaddress) {

        if (!address.isEquivalent(currentaddress)) {
            insertAddress(address);
            return true;
        } else {
            if (!address.getTimestamp().equals(currentaddress.getTimestamp())) {
                updateReportedTimeStamp(address);
                return true;
            }
            return false;
        }
    }

    @Override
    public void insertAddress(ExpressComReportedAddress address) {
        addressTemplate.insert(address);
        
        for (ExpressComReportedAddressRelay relay : address.getRelays()) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("INSERT INTO ReportedAddressRelayExpressCom");
            sql.values(address.getChangeId(), relay.getRelayNumber(), relay.getProgram(), relay.getSplinter());
            
            yukonJdbcTemplate.update(sql);
        }
    }

    private void updateReportedTimeStamp(ExpressComReportedAddress address) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE ReportedAddressExpressCom");
        sql.append("SET ").append("Timestamp").eq(address.getTimestamp());
        sql.append("WHERE ChangeId = (");
        sql.append("    SELECT MAX(ChangeId) FROM ReportedAddressExpressCom");
        sql.append("    WHERE DeviceId").eq(address.getDeviceId());
        sql.append("    )");
        sql.append("  AND DeviceId").eq(address.getDeviceId());
    }

    @Override
    public List<ExpressComReportedAddress> getAllRecordedAddresses(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ChangeId, DeviceId, Timestamp, SPID, GEO, Substation, Feeder, ZIP, UDA, Required");
        sql.append("FROM ReportedAddressExpressCom");
        sql.append("WHERE DeviceId").eq(deviceId);
        
        List<ExpressComReportedAddress> addresses = yukonJdbcTemplate.query(sql, addressRowMapper);
        
        for (ExpressComReportedAddress address : addresses) {
            address.setRelays(getRelays(address.getChangeId()));
        }
        
        return addresses;
    }

    private Set<ExpressComReportedAddressRelay> getRelays(int changeId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ChangeId, RelayNumber, Program, Splinter");
        sql.append("FROM ReportedAddressRelayExpressCom");
        sql.append("WHERE ChangeId").eq(changeId);
        
        return Sets.newHashSet(yukonJdbcTemplate.query(sql, relayMapper));
    }

    @Override
    public ExpressComReportedAddress findCurrentAddress(int deviceId) {
        try {
            return getCurrentAddress(deviceId);
        } catch (NotFoundException e) {
            return null;
        }
    }

    @Override
    public ExpressComReportedAddress getCurrentAddress(int deviceId) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ChangeId, DeviceId, Timestamp, SPID, GEO, Substation, Feeder, ZIP, UDA, Required");
        sql.append("FROM ReportedAddressExpressCom");
        sql.append("WHERE ChangeId = (");
        sql.append("    SELECT MAX(ChangeId) FROM ReportedAddressExpressCom");
        sql.append("    WHERE DeviceId").eq(deviceId);
        sql.append("    )");
        sql.append("  AND DeviceId").eq(deviceId);
        
        try {
            ExpressComReportedAddress address = yukonJdbcTemplate.queryForObject(sql, addressRowMapper);
            address.setRelays(getRelays(address.getChangeId()));
            
            return address;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("No address recorded for device with id: " + deviceId);
        }
    }

    @Override
    public Set<ExpressComReportedAddress> getAllCurrentAddresses() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ChangeId, RAEC.DeviceId, Timestamp, SPID, GEO, Substation, Feeder, ZIP, UDA, Required");
        sql.append("FROM ReportedAddressExpressCom lmra,");
        sql.append("(SELECT DeviceId, MAX(Timestamp) LatestTimestamp FROM ReportedAddressExpressCom GROUP BY DeviceId) RAEC");
        sql.append("WHERE lmra.ChangeId = (SELECT MAX(ChangeId) FROM ReportedAddressExpressCom RAEC2 WHERE RAEC2.DeviceId = RAEC.DeviceId  AND RAEC.LatestTimestamp = RAEC2.Timestamp)");
        
        List<ExpressComReportedAddress> addresses = yukonJdbcTemplate.query(sql, addressRowMapper);
        for (ExpressComReportedAddress address : addresses) {
            address.setRelays(getRelays(address.getChangeId()));
        }
        
        return Sets.newHashSet(addresses);
    }
    
    @Override
    public List<ExpressComReportedAddress> getCurrentAddresses(List<Integer> deviceId) {
        final ChunkingSqlTemplate chunkingTemplate = new ChunkingSqlTemplate(yukonJdbcTemplate);
        
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ChangeId, RAEC.DeviceId, Timestamp, SPID, GEO, Substation, Feeder, ZIP, UDA, Required");
                sql.append("FROM ReportedAddressExpressCom lmra,");
                sql.append("(SELECT DeviceId, MAX(Timestamp) LatestTimestamp FROM ReportedAddressExpressCom GROUP BY DeviceId) RAEC");
                sql.append("WHERE lmra.ChangeId = ");
                sql.append("    (SELECT MAX(ChangeId) ");
                sql.append("    FROM ReportedAddressExpressCom RAEC2"); 
                sql.append("    WHERE RAEC2.DeviceId = RAEC.DeviceId  AND RAEC.LatestTimestamp = RAEC2.Timestamp)");
                sql.append("AND RAEC.DeviceId").in(subList);
                return sql;
            }
        };
        List<ExpressComReportedAddress> addresses = chunkingTemplate.query(sqlGenerator, deviceId, addressRowMapper);

        for (ExpressComReportedAddress address : addresses) {
            address.setRelays(getRelays(address.getChangeId()));
        }
        return addresses;
    }
    
}