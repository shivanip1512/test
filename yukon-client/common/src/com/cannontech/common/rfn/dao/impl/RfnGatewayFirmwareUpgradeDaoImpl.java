package com.cannontech.common.rfn.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.rfn.dao.RfnGatewayFirmwareUpgradeDao;
import com.cannontech.common.rfn.message.gateway.GatewayFirmwareUpdateRequestResult;
import com.cannontech.common.rfn.model.FirmwareUpdateServerInfo;
import com.cannontech.common.rfn.model.GatewayFirmwareUpdateStatus;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.model.RfnGatewayFirmwareUpdateResult;
import com.cannontech.common.rfn.model.RfnGatewayFirmwareUpdateSummary;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.RowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class RfnGatewayFirmwareUpgradeDaoImpl implements RfnGatewayFirmwareUpgradeDao {
    
    private static final int UPGRADE_TIMEOUT_MINUTES = 30;
    
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnGatewayDataCache gatewayDataCache;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private GatewayFirmwareUpdateSummaryRowMapper summaryRowMapper = new GatewayFirmwareUpdateSummaryRowMapper();
    private GatewayFirmwareUpdateResultRowMapper resultRowMapper = new GatewayFirmwareUpdateResultRowMapper();
    
    @Override
    @Transactional
    public int createUpgrade(Collection<RfnGateway> gateways) throws GatewayDataException {
        
        Map<Integer, FirmwareUpdateServerInfo> gatewayToUpdateServer = getAllFirmwareUpdateServerInfo(gateways);
        long updateServerCount = gatewayToUpdateServer.values().stream().distinct().count();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("GatewayFirmwareUpdate");
        
        int updateId = nextValueHelper.getNextValue("GatewayFirmwareUpdate");
        
        sink.addValue("UpdateId", updateId);
        sink.addValue("SendDate", Instant.now());
        sink.addValue("GatewayCount", gateways.size());
        sink.addValue("UpdateServers", updateServerCount);
        
        jdbcTemplate.update(sql);
        
        // Insert an entry for each gateway
        for (RfnGateway gateway : gateways) {
            sql = new SqlStatementBuilder();
            sink = sql.insertInto("GatewayFirmwareUpdateEntry");
            
            Integer paoId = gateway.getPaoIdentifier().getPaoId();
            FirmwareUpdateServerInfo updateServerInfo = gatewayToUpdateServer.get(paoId);
            int entryId = nextValueHelper.getNextValue("GatewayFirmwareUpdateEntry");
            
            sink.addValue("EntryId", entryId);
            sink.addValue("UpdateId", updateId);
            sink.addValue("GatewayId", paoId);
            sink.addValue("OriginalVersion", updateServerInfo.getReleaseVersion());
            sink.addValue("NewVersion", updateServerInfo.getAvailableVersion());
            sink.addValue("UpdateServerUrl", updateServerInfo.getUpdateServerUrl());
            sink.addValue("UpdateStatus", GatewayFirmwareUpdateStatus.STARTED);
            
            jdbcTemplate.update(sql);
        }
        
        return updateId;
    }
    
    /**
     * Checks for any upgrades that are older than the timeout period and marks any incomplete entries as timed out.
     */
    private void updateTimeouts() {
        Instant timeout = Instant.now().minus(Duration.standardMinutes(UPGRADE_TIMEOUT_MINUTES));
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UpdateId");
        sql.append("FROM GatewayFirmwareUpdate");
        sql.append("WHERE SendDate").gt(timeout);
        List<Integer> updateIds = jdbcTemplate.query(sql, RowMapper.INTEGER);
        
        if (!updateIds.isEmpty()) {
            sql = new SqlStatementBuilder();
            sql.append("UPDATE GatewayFirmwareUpdateEntry");
            sql.set("UpdateStatus", GatewayFirmwareUpdateStatus.TIMEOUT);
            sql.append("WHERE UpdateId").in(updateIds);
            sql.append("AND UpdateStatus").eq(GatewayFirmwareUpdateStatus.STARTED);
            jdbcTemplate.update(sql);
        }
    }

    @Override
    public void complete(int updateId, PaoIdentifier gatewayId, GatewayFirmwareUpdateRequestResult result) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE GatewayFirmwareUpdateEntry");
        sql.set("UpdateStatus", GatewayFirmwareUpdateStatus.of(result));
        sql.append("WHERE UpdateId").eq(updateId);
        sql.append("AND GatewayId").eq(gatewayId.getPaoId());
        
        jdbcTemplate.update(sql);
    }
    
    @Override
    @Transactional
    public List<RfnGatewayFirmwareUpdateSummary> getAllUpdateSummaries() {
        updateTimeouts();
        
        /*
         * The three inner selects retrieve a count of the started, succeeded and failed gateway entries associated
         * with the update. The CASEs ensure that 0 is returned for any count that would be null. These counts are then
         * combined into a single row with the info from GatewayFirmwareUpdate in the outer select.
         */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UpdateId, SendDate, GatewayCount, UpdateServerCount");
        sql.append("CASE");
        sql.append(  "WHEN TEMP.Pending IS NULL THEN 0");
        sql.append(  "ELSE TEMP.Pending");
        sql.append("END AS Pending,");
        sql.append("CASE");
        sql.append(  "WHEN TEMP.Successful IS NULL THEN 0");
        sql.append(  "ELSE TEMP.Successful");
        sql.append("END AS Successful");
        sql.append("CASE");
        sql.append(  "WHEN TEMP.Failed IS NULL THEN 0");
        sql.append(  "ELSE Temp.Failed");
        sql.append("END AS Failed");
        
        sql.append("FROM (");
        sql.append(  "SELECT UpdateId, SendDate, GatewayCount, UpdateServerCount");
        
        sql.append(  "(SELECT COUNT(EntryId)");
        sql.append(   "FROM GatewayFirmwareUpdateEntry gfue");
        sql.append(   "WHERE UpdateStatus").eq_k(GatewayFirmwareUpdateStatus.STARTED);
        sql.append(     "AND gfue.UpdateId = gfu.UpdateId");
        sql.append(   "GROUP BY UpdateId) AS Pending,");
        
        sql.append(  "(SELECT COUNT(EntryId)");
        sql.append(   "FROM GatewayFirmwareUpdateEntry gfue");
        sql.append(   "WHERE UpdateStatus").eq_k(GatewayFirmwareUpdateStatus.ACCEPTED);
        sql.append(     "AND gfue.UpdateId = gfu.UpdateId");
        sql.append(   "GROUP BY UpdateId) AS Successful,");
        
        sql.append(  "(SELECT COUNT(EntryId)");
        sql.append(   "FROM GatewayFimrwareUpdateEntry gfue");
        sql.append(   "WHERE UpdateStatus").in_k(GatewayFirmwareUpdateStatus.getFailedStates());
        sql.append(     "AND gfue.UpdateId = gfu.UpdateId");
        sql.append(   "GROUP BY UpdateId) AS Failed");
        
        sql.append(  "FROM GatewayFirmwareUpdate gfu");
        sql.append(") TEMP");
        
        List<RfnGatewayFirmwareUpdateSummary> summaries = jdbcTemplate.query(sql, summaryRowMapper);
        
        return summaries;
    }
    
    @Override
    @Transactional
    public List<RfnGatewayFirmwareUpdateResult> getUpdateResults(int updateId) {
        updateTimeouts();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UpdateId, GatewayId, OriginalVersion, NewVersion, UpdateServerUrl, UpdateStatus");
        sql.append("FROM GatewayFirmwareUpdateEntry");
        sql.append("WHERE UpdateId").eq(updateId);
        
        List<RfnGatewayFirmwareUpdateResult> results = jdbcTemplate.query(sql, resultRowMapper);
        
        return results;
    }
     
    @Override
    public Map<Integer, FirmwareUpdateServerInfo> getAllFirmwareUpdateServerInfo(Collection<RfnGateway> gateways) 
            throws GatewayDataException {
        
        //TODO get this from the service (YUK-14629)
        Map<String, String> serverUrlToAvailableFirmware = new HashMap<>();
        
        Map<Integer, FirmwareUpdateServerInfo> allUpdateServerInfo = new HashMap<>();
        for (RfnGateway gateway : gateways) {
            RfnGatewayData gatewayData;
            try {
                gatewayData = gatewayDataCache.get(gateway.getPaoIdentifier());
            } catch (NmCommunicationException e) {
                throw new GatewayDataException("Unable to load gateway data for gateway " + gateway.getName(), e);
            }
            String updateServerUrl = gatewayData.getUpdateServerUrl();
            String releaseVersion = gatewayData.getReleaseVersion();
            String availableVersion = serverUrlToAvailableFirmware.get(updateServerUrl);
            FirmwareUpdateServerInfo info = new FirmwareUpdateServerInfo(updateServerUrl, releaseVersion, availableVersion);
            allUpdateServerInfo.put(gateway.getPaoIdentifier().getPaoId(), info);
        }
        return allUpdateServerInfo;
    }
    
    private static final class GatewayFirmwareUpdateSummaryRowMapper implements YukonRowMapper<RfnGatewayFirmwareUpdateSummary> {
        @Override
        public RfnGatewayFirmwareUpdateSummary mapRow(YukonResultSet rs) throws SQLException {
            RfnGatewayFirmwareUpdateSummary summary = new RfnGatewayFirmwareUpdateSummary();
            summary.setUpdateId(rs.getInt("UpdateId"));
            summary.setSendDate(rs.getInstant("SendDate"));
            summary.setTotalGateways(rs.getInt("GatewayCount"));
            summary.setTotalUpdateServers(rs.getInt("UpdateServerCount"));
            summary.setGatewayUpdatesPending(rs.getInt("Pending"));
            summary.setGatewayUpdatesSuccessful(rs.getInt("Successful"));
            summary.setGatewayUpdatesFailed(rs.getInt("Failed"));
            
            return summary;
        }
    }
    
    private static final class GatewayFirmwareUpdateResultRowMapper implements YukonRowMapper<RfnGatewayFirmwareUpdateResult> {
        @Override
        public RfnGatewayFirmwareUpdateResult mapRow(YukonResultSet rs) throws SQLException {
            RfnGatewayFirmwareUpdateResult result = new RfnGatewayFirmwareUpdateResult();
            result.setUpdateId(rs.getInt("UpdateId"));
            result.setGatewayPaoId(rs.getInt("GatewayId"));
            result.setOriginalVersion(rs.getString("OriginalVersion"));
            result.setNewVersion(rs.getString("NewVersion"));
            result.setUpdateServerUrl(rs.getString("UpdateServerUrl"));
            result.setStatus(rs.getEnum("UpdateStatus", GatewayFirmwareUpdateStatus.class));
            
            return result;
        }
    }
}
