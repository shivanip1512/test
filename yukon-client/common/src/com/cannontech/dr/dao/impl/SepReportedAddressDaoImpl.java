package com.cannontech.dr.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.dao.SepReportedAddress;
import com.cannontech.dr.dao.SepReportedAddressDao;
import com.google.common.collect.Sets;

public class SepReportedAddressDaoImpl implements SepReportedAddressDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    
    private SimpleTableAccessTemplate<SepReportedAddress> addressTemplate;
    
    private final static FieldMapper<SepReportedAddress> fieldMapper = new FieldMapper<SepReportedAddress>() {
        @Override
        public Number getPrimaryKey(SepReportedAddress address) {
            return address.getChangeId();
        }

        @Override
        public void setPrimaryKey(SepReportedAddress address, int changeId) {
            address.setChangeId(changeId);
        }

        @Override
        public void extractValues(MapSqlParameterSource parameterHolder, SepReportedAddress address) {
            parameterHolder.addValue("DeviceId", address.getDeviceId());
            parameterHolder.addValue("Timestamp", address.getTimestamp());
            parameterHolder.addValue("DeviceClass", address.getDeviceClass());
            parameterHolder.addValue("UtilityEnrollmentGroup", address.getUtilityEnrollmentGroup());
            parameterHolder.addValue("RandomStartTimeMinutes", address.getRandomStartTimeMinutes());
            parameterHolder.addValue("RandomStopTimeMinutes", address.getRandomStopTimeMinutes());
        }
    };
    
    @PostConstruct
    public void init() {
        addressTemplate = new SimpleTableAccessTemplate<SepReportedAddress>(yukonJdbcTemplate, nextValueHelper);
        addressTemplate.setTableName("ReportedAddressSep");
        addressTemplate.setFieldMapper(fieldMapper);
        addressTemplate.setPrimaryKeyField("ChangeId");
        addressTemplate.setPrimaryKeyValidOver(0);
    }
    
    YukonRowMapper<SepReportedAddress> addressRowMapper = new YukonRowMapper<SepReportedAddress>() {

        @Override
        public SepReportedAddress mapRow(YukonResultSet rs) throws SQLException {
            SepReportedAddress address = new SepReportedAddress();
            
            address.setChangeId(rs.getInt("ChangeId"));
            address.setDeviceId(rs.getInt("DeviceId"));
            address.setTimestamp(rs.getInstant("Timestamp"));
            address.setDeviceClass(rs.getInt("DeviceClass"));
            address.setUtilityEnrollmentGroup(rs.getInt("UtilityEnrollmentGroup"));
            address.setRandomStartTimeMinutes(rs.getInt("RandomStartTimeMinutes"));
            address.setRandomStopTimeMinutes(rs.getInt("RandomStopTimeMinutes"));
            
            return address;
        }
        
    };
    
    @Override
    @Transactional
    public boolean save(SepReportedAddress address) {
        try {
            SepReportedAddress current = getCurrentAddress(address.getDeviceId());
            
            if (!address.isEquivalent(current)) {
                insertAddress(address);
                return true;
            } else {
                return false;
            }
            
        } catch (NotFoundException e) {
            insertAddress(address);
            return true;
        }
    }
    
    private void insertAddress(SepReportedAddress address) {
        addressTemplate.insert(address);
    }
    
    @Override
    public List<SepReportedAddress> getAllRecordedAddresses(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ChangeId, DeviceId, Timestamp, DeviceClass, UtilityEnrollmentGroup, RandomStartTimeMinutes, RandomStopTimeMinutes");
        sql.append("FROM ReportedAddressSep");
        sql.append("WHERE DeviceId").eq(deviceId);
        
        List<SepReportedAddress> addresses = yukonJdbcTemplate.query(sql, addressRowMapper);
        
        return addresses;
    }
    
    @Override
    public SepReportedAddress getCurrentAddress(int deviceId) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ChangeId, RAS.DeviceId, Timestamp, DeviceClass, UtilityEnrollmentGroup, RandomStartTimeMinutes, RandomStopTimeMinutes");
        sql.append("FROM ReportedAddressSep lmra,");
        sql.append("(SELECT DeviceId, MAX(Timestamp) LatestTimestamp FROM ReportedAddressSep GROUP BY DeviceId) RAS");
        sql.append("WHERE lmra.ChangeId = (SELECT MAX(ChangeId) FROM ReportedAddressSep RAS2 WHERE RAS2.DeviceId = RAS.DeviceId  AND RAS.LatestTimestamp = RAS2.Timestamp)");
        sql.append("AND RAS.DeviceId").eq(deviceId);
        
        try {
            SepReportedAddress address = yukonJdbcTemplate.queryForObject(sql, addressRowMapper);
            return address;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("No address recorded for device with id: " + deviceId);
        }
    }
    
    @Override
    public Set<SepReportedAddress> getAllCurrentAddresses() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ChangeId, RAS.DeviceId, Timestamp, DeviceClass, UtilityEnrollmentGroup, RandomStartTimeMinutes, RandomStopTimeMinutes");
        sql.append("FROM ReportedAddressSep lmra,");
        sql.append("(SELECT DeviceId, MAX(Timestamp) LatestTimestamp FROM ReportedAddressSep GROUP BY DeviceId) RAS");
        sql.append("WHERE lmra.ChangeId = (SELECT MAX(ChangeId) FROM ReportedAddressSep RAS2 WHERE RAS2.DeviceId = RAS.DeviceId  AND RAS.LatestTimestamp = RAS2.Timestamp)");
        
        List<SepReportedAddress> addresses = yukonJdbcTemplate.query(sql, addressRowMapper);
        
        return Sets.newHashSet(addresses);
    }
    
}