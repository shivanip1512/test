package com.cannontech.common.rfn.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.rfn.dao.RfnGatewayFirmwareUpgradeDao;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.message.gateway.GatewayFirmwareUpdateRequestResult;
import com.cannontech.common.rfn.message.gateway.RfnUpdateServerAvailableVersionRequest;
import com.cannontech.common.rfn.message.gateway.RfnUpdateServerAvailableVersionResponse;
import com.cannontech.common.rfn.message.gateway.RfnUpdateServerAvailableVersionResult;
import com.cannontech.common.rfn.model.FirmwareUpdateServerInfo;
import com.cannontech.common.rfn.model.GatewayFirmwareUpdateStatus;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.model.RfnGatewayFirmwareUpdateResult;
import com.cannontech.common.rfn.model.RfnGatewayFirmwareUpdateSummary;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class RfnGatewayFirmwareUpgradeDaoImpl implements RfnGatewayFirmwareUpgradeDao {
    
    private static final Logger log = YukonLogManager.getLogger(RfnGatewayFirmwareUpgradeDaoImpl.class);
    private static final Duration upgradeTimeoutMinutes = Duration.standardMinutes(45);
    private static final String rfnUpdateServerAvailableVersionRequestCparm =
            "RFN_UPDATE_SERVER_AVAILABLE_VERSION_REQUEST";
    private static final String updateServerAvailableVersionRequestQueue =
            "yukon.qr.obj.common.rfn.UpdateServerAvailableVersionRequest";
    
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnGatewayDataCache gatewayDataCache;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private ConfigurationSource configSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    
    private GatewayFirmwareUpdateSummaryRowMapper summaryRowMapper = new GatewayFirmwareUpdateSummaryRowMapper();
    private GatewayFirmwareUpdateResultRowMapper resultRowMapper = new GatewayFirmwareUpdateResultRowMapper();
    private RequestReplyTemplate<RfnUpdateServerAvailableVersionResponse> rfnUpdateServerAvailableVersionTemplate;
    
    @PostConstruct
    public void init() {
        rfnUpdateServerAvailableVersionTemplate =
                new RequestReplyTemplateImpl<>(rfnUpdateServerAvailableVersionRequestCparm, configSource,
                    connectionFactory, updateServerAvailableVersionRequestQueue, false, true);
    }
    
    @Override
    @Transactional
    public int createUpgrade(Collection<RfnGateway> gateways) throws GatewayDataException {
        
        Map<Integer, FirmwareUpdateServerInfo> gatewayToUpdateServer = getAllFirmwareUpdateServerInfo(gateways);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("GatewayFirmwareUpdate");
        
        int updateId = nextValueHelper.getNextValue("GatewayFirmwareUpdate");
        
        sink.addValue("UpdateId", updateId);
        sink.addValue("SendDate", Instant.now());
        
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
        Instant timeoutMinutesBeforeNow = Instant.now().minus(upgradeTimeoutMinutes);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UpdateId");
        sql.append("FROM GatewayFirmwareUpdate");
        sql.append("WHERE SendDate").lt(timeoutMinutesBeforeNow);
        List<Integer> updateIds = jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
        
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
        sql.append("SELECT UpdateId, SendDate, GatewayCount, UpdateServerCount,");
        sql.append("CASE");
        sql.append(  "WHEN TEMP.Pending IS NULL THEN 0");
        sql.append(  "ELSE TEMP.Pending");
        sql.append("END AS Pending,");
        sql.append("CASE");
        sql.append(  "WHEN TEMP.Success IS NULL THEN 0");
        sql.append(  "ELSE TEMP.Success");
        sql.append("END AS Success,");
        sql.append("CASE");
        sql.append(  "WHEN TEMP.Failed IS NULL THEN 0");
        sql.append(  "ELSE Temp.Failed");
        sql.append("END AS Failed");
        
        sql.append("FROM (");
        sql.append(  "SELECT UpdateId, SendDate,");
        
        // Number of pending gateways
        sql.append(  "(SELECT COUNT(EntryId)");
        sql.append(   "FROM GatewayFirmwareUpdateEntry gfue");
        sql.append(   "WHERE UpdateStatus").eq_k(GatewayFirmwareUpdateStatus.STARTED);
        sql.append(     "AND gfue.UpdateId = gfu.UpdateId");
        sql.append(   "GROUP BY UpdateId) AS Pending,");
        
        // Number of successful gateways
        sql.append(  "(SELECT COUNT(EntryId)");
        sql.append(   "FROM GatewayFirmwareUpdateEntry gfue");
        sql.append(   "WHERE UpdateStatus").eq_k(GatewayFirmwareUpdateStatus.ACCEPTED);
        sql.append(     "AND gfue.UpdateId = gfu.UpdateId");
        sql.append(   "GROUP BY UpdateId) AS Success,");
        
        // Number of failed gateways
        sql.append(  "(SELECT COUNT(EntryId)");
        sql.append(   "FROM GatewayFirmwareUpdateEntry gfue");
        sql.append(   "WHERE UpdateStatus").in_k(GatewayFirmwareUpdateStatus.getFailedStates());
        sql.append(     "AND gfue.UpdateId = gfu.UpdateId");
        sql.append(   "GROUP BY UpdateId) AS Failed,");
        
        // Number of gateways total
        sql.append(  "(SELECT COUNT(GatewayId)");
        sql.append(   "FROM GatewayFirmwareUpdateEntry gfue");
        sql.append(   "WHERE gfue.UpdateId = gfu.UpdateId");
        sql.append(   "GROUP BY UpdateId) AS GatewayCount,");
        
        // Number of firmware server urls
        sql.append(  "(SELECT COUNT(DISTINCT UpdateServerUrl)");
        sql.append(   "FROM GatewayFirmwareUpdateEntry gfue");
        sql.append(   "WHERE gfue.UpdateId = gfu.UpdateId");
        sql.append(   "GROUP BY UpdateId) AS UpdateServerCount");
        
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
        
        Map<Integer, FirmwareUpdateServerInfo> allUpdateServerInfo = new HashMap<>();
        
        try {
            Map<String, String> serverUrlToAvailableFirmware = getFirmwareUpdateServerVersions(gateways);
            
            for (RfnGateway gateway : gateways) {
                RfnGatewayData gatewayData;
                
                    gatewayData = gatewayDataCache.get(gateway.getPaoIdentifier());
                
                String updateServerUrl = gatewayData.getUpdateServerUrl();
                if (StringUtils.isEmpty(updateServerUrl)) {
                    updateServerUrl = globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER);
                }
                String releaseVersion = gatewayData.getReleaseVersion();
                String availableVersion = serverUrlToAvailableFirmware.get(updateServerUrl);
                FirmwareUpdateServerInfo info = new FirmwareUpdateServerInfo(updateServerUrl, releaseVersion, availableVersion);
                allUpdateServerInfo.put(gateway.getPaoIdentifier().getPaoId(), info);
            }
        } catch (NmCommunicationException e) {
            throw new GatewayDataException("Unable to load gateway data for gateway.", e);
        }
        
        return allUpdateServerInfo;
    }
    
    @Override
    public Map<String, String> getFirmwareUpdateServerVersions(Collection<RfnGateway> gateways) throws NmCommunicationException {
        Map<String, String> updateServerAvailableVersionMap = new HashMap<String, String>();
        
        // Iterating over all rfnGateways to set the RfnUpdateData with available version for the update server
        for (RfnGateway rfnGateway : gateways) {
            RfnGatewayData rfnGatewayData = rfnGateway.getData();
            String updateServerUrl = rfnGatewayData.getUpdateServerUrl();
            Authentication updateServerLogin = rfnGatewayData.getUpdateServerLogin();
            
            if (rfnGatewayData != null && !updateServerAvailableVersionMap.containsKey(updateServerUrl)) {
                
                RfnUpdateServerAvailableVersionRequest rfnUpdateServerAvailableVersionRequest =
                    buildVersionRequest(updateServerUrl, updateServerLogin);
                
                RfnUpdateServerAvailableVersionResponse response =
                    sendUpdateServerVersionRequest(rfnUpdateServerAvailableVersionRequest);

                if (response != null && response.getResult() == RfnUpdateServerAvailableVersionResult.SUCCESS) {
                    updateServerAvailableVersionMap.put(updateServerUrl, response.getAvailableVersion());
                }
            }
        }
        
        // Send a request for the default url if not already retrieved
        String defaultUpdateServerUrl = globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER);
        if (!updateServerAvailableVersionMap.containsKey(defaultUpdateServerUrl)) {
            String defaultUser = globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER_USER);
            String defaultPass = globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER_PASSWORD);
            Authentication defaultUpdateServerLogin = new Authentication();
            defaultUpdateServerLogin.setUsername(defaultUser);
            defaultUpdateServerLogin.setPassword(defaultPass);
                    
            RfnUpdateServerAvailableVersionRequest rfnUpdateServerAvailableVersionRequest =
                    buildVersionRequest(defaultUpdateServerUrl, defaultUpdateServerLogin);
            RfnUpdateServerAvailableVersionResponse response =
                    sendUpdateServerVersionRequest(rfnUpdateServerAvailableVersionRequest);
            
            if (response != null && response.getResult() == RfnUpdateServerAvailableVersionResult.SUCCESS) {
                updateServerAvailableVersionMap.put(defaultUpdateServerUrl, response.getAvailableVersion());
            }
        }
        
        return updateServerAvailableVersionMap;
    }
    
    private RfnUpdateServerAvailableVersionRequest buildVersionRequest(String updateServerUrl, 
                                                                       Authentication updateServerLogin) 
                                                                           throws NmCommunicationException {
        
        RfnUpdateServerAvailableVersionRequest rfnUpdateServerAvailableVersionRequest =
                new RfnUpdateServerAvailableVersionRequest();
        
        rfnUpdateServerAvailableVersionRequest.setUpdateServerUrl(updateServerUrl);
        rfnUpdateServerAvailableVersionRequest.setUpdateServerLogin(updateServerLogin);
        
        return rfnUpdateServerAvailableVersionRequest;
    }

    /**
     * This method communicates to NM and fetches the available version for update servers.
     * @throws NmCommunicationException If there is an error communicating with Network Manager.
     */
    private RfnUpdateServerAvailableVersionResponse sendUpdateServerVersionRequest(RfnUpdateServerAvailableVersionRequest request) 
            throws NmCommunicationException {
        
        RfnUpdateServerAvailableVersionResponse response = null;
        BlockingJmsReplyHandler<RfnUpdateServerAvailableVersionResponse> replyHandler =
            new BlockingJmsReplyHandler<>(RfnUpdateServerAvailableVersionResponse.class);
        
        log.debug("Sending fetch request for firmware update server's available version");
        rfnUpdateServerAvailableVersionTemplate.send(request, replyHandler);
        
        try {
            response = replyHandler.waitForCompletion();
            log.debug("Response = " + response.getResult());
        } catch (ExecutionException e) {
            throw new NmCommunicationException("Fetching available version for firmware update servers failed due to a "
                    + "communication error with Network Manager.", e);
        }
        return response;
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
            summary.setGatewayUpdatesSuccessful(rs.getInt("Success"));
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
