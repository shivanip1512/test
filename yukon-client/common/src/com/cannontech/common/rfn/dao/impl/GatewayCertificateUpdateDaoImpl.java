package com.cannontech.common.rfn.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.dao.GatewayCertificateUpdateDao;
import com.cannontech.common.rfn.model.GatewayCertificateUpdateInfo;
import com.cannontech.common.rfn.model.GatewayCertificateUpdateStatus;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class GatewayCertificateUpdateDaoImpl implements GatewayCertificateUpdateDao {
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    
    @Override
    public int createUpdate(String certificateId, String fileName) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("GatewayCertificateUpdate");
        int updateId = nextValueHelper.getNextValue("GatewayCertificateUpdate");
        sink.addValue("UpdateId", updateId);
        sink.addValue("CertificateId", certificateId);
        sink.addValue("FileName", fileName);
        sink.addValue("SendDate", Instant.now());
        
        jdbcTemplate.update(sql);
        
        return updateId;
    }
    
    @Override
    @Transactional
    public void createEntries(int updateId, GatewayCertificateUpdateStatus status, Collection<Integer> gatewayIds) {
        
        //If gatewayIds is empty, all gateways are being upgraded
        if (gatewayIds == null || gatewayIds.size() == 0) {
            gatewayIds = new ArrayList<>();
            List<RfnDevice> devices = rfnDeviceDao.getDevicesByPaoType(PaoType.RFN_GATEWAY);
            for (RfnDevice device : devices) {
                gatewayIds.add(device.getPaoIdentifier().getPaoId());
            }
        }
        
        for (int gatewayId : gatewayIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            SqlParameterSink sink = sql.insertInto("GatewayCertificateUpdateEntry");
            int entryId = nextValueHelper.getNextValue("GatewayCertificateUpdateEntry");
            sink.addValue("EntryId", entryId);
            sink.addValue("UpdateId", updateId);
            sink.addValue("GatewayId", gatewayId);
            sink.addValue("UpdateStatus", status);
            
            jdbcTemplate.update(sql);
        }
        
    }

    @Override
    public void updateEntry(int updateId, int gatewayId, GatewayCertificateUpdateStatus status) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE GatewayCertificateUpdateEntry");
        sql.set("UpdateStatus", status);
        sql.append("WHERE UpdateId").eq(updateId);
        sql.append("AND gatewayId").eq(gatewayId);
        
        jdbcTemplate.update(sql);
    }
    
    @Override
    public void timeoutUpdate(int updateId) {
        setStatusOnStartedEntries(updateId, GatewayCertificateUpdateStatus.TIMEOUT);
    }
    
    @Override
    public void failUnackedForUpdate(int updateId) {
        setStatusOnStartedEntries(updateId, GatewayCertificateUpdateStatus.NM_ERROR);
    }
    
    private void setStatusOnStartedEntries(int updateId, GatewayCertificateUpdateStatus status) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE GatewayCertificateUpdateEntry");
        sql.set("UpdateStatus", status);
        sql.append("WHERE UpdateId").eq(updateId);
        sql.append("AND UpdateStatus").eq_k(GatewayCertificateUpdateStatus.STARTED);
        
        jdbcTemplate.update(sql);
    }
    
    @Override
    @Transactional
    public GatewayCertificateUpdateInfo getUpdateInfo(int updateId) {
        timeoutIncompleteEntries();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UpdateId, CertificateId, SendDate, FileName"); 
        sql.append("FROM GatewayCertificateUpdate");
        sql.append("WHERE UpdateId").eq(updateId);
        
        GatewayCertificateUpdateInfo info = jdbcTemplate.queryForObject(sql, new UpdateRowMapper());
        
        sql = new SqlStatementBuilder();
        sql.append("SELECT GatewayId, UpdateStatus");
        sql.append("FROM GatewayCertificateUpdateEntry");
        sql.append("WHERE UpdateId").eq(updateId);
        
        UpdateEntryRowCallbackHandler handler = new UpdateEntryRowCallbackHandler(info);
        jdbcTemplate.query(sql, handler);
        
        return info;
    }

    @Override
    @Transactional
    public List<GatewayCertificateUpdateInfo> getAllUpdateInfo() {
        timeoutIncompleteEntries();
        
        final Map<Integer, GatewayCertificateUpdateInfo> updateMap = new HashMap<>();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT updates.UpdateId, CertificateId, SendDate, FileName, GatewayId, UpdateStatus");
        sql.append("FROM GatewayCertificateUpdateEntry entries");
        sql.append("JOIN GatewayCertificateUpdate updates ON updates.UpdateId = entries.UpdateId");
        sql.append("ORDER BY SendDate DESC");
        
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                int updateId = rs.getInt("UpdateId");
                GatewayCertificateUpdateInfo info = updateMap.get(updateId);
                if (info == null) {
                    String certificateId = rs.getString("CertificateId");
                    Instant sendDate = rs.getInstant("SendDate");
                    String fileName = rs.getString("FileName");
                    info = new GatewayCertificateUpdateInfo(updateId, certificateId, sendDate, fileName);
                    updateMap.put(updateId, info);
                }
                int gatewayId = rs.getInt("GatewayId");
                GatewayCertificateUpdateStatus status = rs.getEnum("UpdateStatus", GatewayCertificateUpdateStatus.class);
                info.addGatewayStatus(gatewayId, status);
            }
        });
        
        return new ArrayList<>(updateMap.values());
    }
    
    @Override
    @Transactional
    public int getLatestUpdateForCertificate(String certificateId) {
        timeoutIncompleteEntries();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UpdateId");
        sql.append("FROM GatewayCertificateUpdate");
        sql.append("WHERE SendDate = (");
        sql.append("  SELECT MAX(SendDate)");
        sql.append("  FROM GatewayCertificateUpdate");
        sql.append("  WHERE CertificateId").eq(certificateId);
        sql.append(")");
        
        return jdbcTemplate.queryForInt(sql);
    }
    
    /**
     * Sets the status to TIMEOUT on all entries older than 1 hour.
     */
    private void timeoutIncompleteEntries() {
        Instant oneHourPrevious = Instant.now().minus(Duration.standardHours(1));
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE GatewayCertificateUpdateEntry");
        sql.set("UpdateStatus", GatewayCertificateUpdateStatus.TIMEOUT);
        sql.append("WHERE (");
        sql.append("UpdateStatus").eq(GatewayCertificateUpdateStatus.STARTED);
        sql.append("OR UpdateStatus").eq(GatewayCertificateUpdateStatus.REQUEST_ACCEPTED);
        sql.append(")");
        sql.append("AND UpdateId IN (");
        sql.append(  "SELECT UpdateId");
        sql.append(  "FROM GatewayCertificateUpdate");
        sql.append(  "WHERE SendDate").lt(oneHourPrevious);
        sql.append(")");
        
        jdbcTemplate.update(sql);
    }
    
    private class UpdateRowMapper implements YukonRowMapper<GatewayCertificateUpdateInfo> {
        @Override
        public GatewayCertificateUpdateInfo mapRow(YukonResultSet rs) throws SQLException {
            int updateId = rs.getInt("UpdateId");
            String certificateId = rs.getString("CertificateId");
            Instant sendDate = rs.getInstant("SendDate");
            String fileName = rs.getString("FileName");
            
            return new GatewayCertificateUpdateInfo(updateId, certificateId, sendDate, fileName);
        }
    }
    
    private class UpdateEntryRowCallbackHandler implements YukonRowCallbackHandler {
        private GatewayCertificateUpdateInfo info;
        
        public UpdateEntryRowCallbackHandler(GatewayCertificateUpdateInfo info) {
            this.info = info;
        }
        
        @Override
        public void processRow(YukonResultSet rs) throws SQLException {
            Integer gatewayId = rs.getInt("GatewayId");
            GatewayCertificateUpdateStatus status = rs.getEnum("UpdateStatus", GatewayCertificateUpdateStatus.class);
            info.addGatewayStatus(gatewayId, status);
        }
    }
}
