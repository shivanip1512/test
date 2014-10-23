package com.cannontech.common.rfn.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.rfn.dao.GatewayCertificateUpdateDao;
import com.cannontech.common.rfn.model.GatewayCertificateUpdateInfo;
import com.cannontech.common.rfn.model.GatewayCertificateUpdateStatus;
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
    public void createEntries(int updateId, GatewayCertificateUpdateStatus status, int... gatewayIds) {
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
        sql.append("update GatewayCertificateUpdateEntry");
        sql.set("UpdateStatus", status);
        sql.append("where UpdateId").eq(updateId);
        sql.append("and gatewayId").eq(gatewayId);
        
        jdbcTemplate.update(sql);
    }
    
    @Override
    public GatewayCertificateUpdateInfo getUpdateInfo(int updateId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select UpdateId, CertificateId, SendDate, FileName"); 
        sql.append("from GatewayCertificateUpdate");
        sql.append("where UpdateId").eq(updateId);
        
        GatewayCertificateUpdateInfo info = jdbcTemplate.queryForObject(sql, new UpdateRowMapper());
        
        sql = new SqlStatementBuilder();
        sql.append("select GatewayId, UpdateStatus");
        sql.append("from GatewayCertificateUpdateEntry");
        sql.append("where UpdateId").eq(updateId);
        
        UpdateEntryRowCallbackHandler handler = new UpdateEntryRowCallbackHandler(info);
        jdbcTemplate.query(sql, handler);
        
        return info;
    }

    @Override
    public List<GatewayCertificateUpdateInfo> getAllUpdateInfo() {
        final Map<Integer, GatewayCertificateUpdateInfo> updateMap = new HashMap<>();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select updates.UpdateId, CertificateId, SendDate, FileName, GatewayId, UpdateStatus");
        sql.append("from GatewayCertificateUpdateEntry entries");
        sql.append("join GatewayCertificateUpdate updates on updates.UpdateId = entries.UpdateId");
        sql.append("order by SendDate desc");
        
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
    public int getLatestUpdateForCertificate(String certificateId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select UpdateId");
        sql.append("from GatewayCertificateUpdate");
        sql.append("where SendDate = (");
        sql.append("  select MAX(SendDate)");
        sql.append("  from GatewayCertificateUpdate");
        sql.append("  where CertificateId").eq(certificateId);
        sql.append(")");
        
        return jdbcTemplate.queryForInt(sql);
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
