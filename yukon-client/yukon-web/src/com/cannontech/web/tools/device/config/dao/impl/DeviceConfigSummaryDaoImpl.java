package com.cannontech.web.tools.device.config.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.web.tools.device.config.dao.DeviceConfigSummaryDao;
import com.cannontech.web.tools.device.config.model.DeviceConfigActionHistoryDetail;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryDetail;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter.InSync;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter.LastAction;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter.LastActionStatus;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

public class DeviceConfigSummaryDaoImpl implements DeviceConfigSummaryDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private static final Logger log = YukonLogManager.getLogger(DeviceConfigSummaryDaoImpl.class);
    private static List<DeviceRequestType> deviceConfigExecTypes = ImmutableList.of(DeviceRequestType.GROUP_DEVICE_CONFIG_VERIFY,
        DeviceRequestType.GROUP_DEVICE_CONFIG_SEND, DeviceRequestType.GROUP_DEVICE_CONFIG_READ);
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private DeviceGroupService deviceGroupService;

    
    private final class DetailRowMapper implements YukonRowMapper<DeviceConfigSummaryDetail> { 
        
        @Override
        public DeviceConfigSummaryDetail mapRow(YukonResultSet rs) throws SQLException {
            DeviceConfigSummaryDetail detail = new DeviceConfigSummaryDetail();
            try {
                detail.setAction(LastAction.getByRequestType(rs.getEnum("ExecType", DeviceRequestType.class)));
            } catch (Exception e) {
                // device doesn't have an entry in cre
            }
            detail.setActionEnd(rs.getInstant("StopTime"));
            detail.setActionStart(rs.getInstant("StartTime"));
            detail.setDevice(new DisplayableDevice(rs.getPaoIdentifier("DeviceId", "DeviceType"), rs.getString("DeviceName")));
            Integer configId = rs.getNullableInt("ConfigId");
            if (configId != null) {
                detail.setDeviceConfig(new LightDeviceConfiguration(configId, rs.getString("ConfigName"), null));
            }
            detail.setInSync(rs.getEnum("InSync", InSync.class));
            detail.setStatus(rs.getEnum("ActionStatus", LastActionStatus.class));
            detail.setErrorCode(rs.getNullableInt("ErrorCode"));
            return detail;
        }
    }
    
    private final class HistoryRowMapper implements YukonRowMapper<DeviceConfigActionHistoryDetail> { 
        
        @Override
        public DeviceConfigActionHistoryDetail mapRow(YukonResultSet rs) throws SQLException {
            DeviceConfigActionHistoryDetail detail = new DeviceConfigActionHistoryDetail();
            detail.setAction(LastAction.getByRequestType(rs.getEnum("ExecType", DeviceRequestType.class)));
            detail.setActionEnd(rs.getInstant("StopTime"));
            detail.setActionStart(rs.getInstant("StartTime"));
            detail.setStatus(rs.getEnum("ActionStatus", LastActionStatus.class));
            return detail;
        }
    }
    
    /*
     *  State       Last action         Success?    Prior verify?       In sync?
        Unassigned  n/a                 n/a         n/a                     n/a
                        
        Assigned    None                n/a         n/a                 Unverified
                        
        Assigned    Send                Fail        None                Unverified
        Assigned    Send                Fail        Success             In sync
        Assigned    Send                Fail        Fail                Out of sync
        Assigned    Send                Success     None                Unverified
        Assigned    Send                Success     Success             In sync
        Assigned    Send                Success     Fail                Unverified
                        
        Assigned    Send in progress    n/a         None                Unverified
        Assigned    Send in progress    n/a         Success             In sync
        Assigned    Send in progress    n/a         Fail                Out of sync
                        
        Assigned    Read                Fail        None                Unverified
        Assigned    Read                Fail        Success             In sync
        Assigned    Read                Fail        Fail                Out of sync
        Assigned    Read                Success     None                Unverified
        Assigned    Read                Success     Success             In sync
        Assigned    Read                Success     Fail                Unverified
                        
        Assigned    Read in progress    n/a         None                Unverified
        Assigned    Read in progress    n/a         Success             In sync
        Assigned    Read in progress    n/a         Fail                Out of sync
                        
        Assigned    Verify              Yes         n/a                 In sync
        Assigned    Verify              No          n/a                 Out of sync
     */
    
    @Override
    public SearchResults<DeviceConfigSummaryDetail> getSummary(DeviceConfigSummaryFilter filter,
            PagingParameters paging, SortBy sortBy, Direction direction) {
        log.debug("Getting summary for filter=" + filter);
        SqlStatementBuilder allRowsSql = buildDetailSelect(filter, sortBy, direction, false);
        log.debug(allRowsSql.getDebugSql());
        SqlStatementBuilder countSql = buildDetailSelect(filter, null, null, true);
        int totalCount = jdbcTemplate.queryForInt(countSql);

        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();

        PagingResultSetExtractor<DeviceConfigSummaryDetail> rse =
            new PagingResultSetExtractor<>(start, count, new DetailRowMapper());
        jdbcTemplate.query(allRowsSql, rse);

        SearchResults<DeviceConfigSummaryDetail> searchResult = new SearchResults<>();
        searchResult.setBounds(paging.getStartIndex(), paging.getItemsPerPage(), totalCount);
        searchResult.setResultList(rse.getResultList());

        return searchResult;
    }
    
    private class AddUnion {
        boolean addUnionBeforeNextSelect = false;
        void add(SqlStatementBuilder sql){
            if(addUnionBeforeNextSelect){
                sql.append("UNION");
                addUnionBeforeNextSelect = false;
            }
        }
    }
    private SqlStatementBuilder buildDetailSelect(DeviceConfigSummaryFilter filter, SortBy sortBy,
            Direction direction, boolean selectCount) {
        AddUnion addUnion = new AddUnion();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        buildWithClause(sql, filter);
        if (selectCount) {
            sql.append("SELECT count(DeviceId)");
        } else {
            sql.append("SELECT DeviceId, DeviceName, DeviceType, ExecId, ExecType, ErrorCode, ActionStatus, StartTime, StopTime, InSync, ConfigName, ConfigId");
        }
        sql.append("FROM (");  
        if (filter.isDisplayUnassigned() && filter.getConfigurationIds().isEmpty()) {
            addUnion.add(sql);
            buildUnassignedSelect(sql, filter);
        } else {
            if (filter.contains(LastActionStatus.FAILURE) || filter.contains(LastActionStatus.SUCCESS)
                || filter.contains(LastActionStatus.IN_PROGRESS)) {
                addUnion.add(sql);
                buildActionSelect(sql, filter);
                addUnion.addUnionBeforeNextSelect = true;
            }
            if (filter.contains(InSync.UNVERIFIED)) {
                addUnion.add(sql);
                buildNoExecutionsSelect(sql, filter);
                addUnion.addUnionBeforeNextSelect = true;
            }
            if (filter.isDisplayUnassigned()) {
                addUnion.add(sql);
                buildUnassignedSelect(sql, filter);
            }
        }
        sql.append(") results");
        if (sortBy != null) {
            sql.append("ORDER BY");
            sql.append(sortBy.getDbString());
            sql.append(direction);
        }
        return sql;
    }

    //doesn't have device config
    private void buildUnassignedSelect(SqlStatementBuilder sql, DeviceConfigSummaryFilter filter) {
        sql.append("SELECT");
        sql.append("    ypo.PAObjectID as DeviceId,");
        sql.append("    ypo.paoName as DeviceName,");
        sql.append("    ypo.Type as DeviceType,");
        sql.append("    null as ExecId,");
        sql.append("    null as ExecType,");
        sql.append("    null as ErrorCode,");
        sql.append("     ").appendArgument_k(LastActionStatus.NA).append("as ActionStatus,");
        sql.append("    null as StartTime,");
        sql.append("    null as StopTime,");
        sql.append("     ").appendArgument_k(InSync.NA).append("as InSync,");
        sql.append("    null as ConfigName,");
        sql.append("    null as ConfigId");
               
        sql.append("FROM YukonPAObject ypo");
        sql.append("WHERE ypo.type").in_k(getSupportedPaoTypes());
        sql.append("AND ypo.PAObjectID NOT IN (select DeviceID from DeviceConfigurationDeviceMap)");
        if (!CollectionUtils.isEmpty(filter.getGroups())) {
            sql.append("AND").appendFragment(
                deviceGroupService.getDeviceGroupSqlWhereClause(filter.getGroups(), "ypo.PAObjectID"));
        }
    }

    //has device config but no executions
    private void buildNoExecutionsSelect(SqlStatementBuilder sql, DeviceConfigSummaryFilter filter) {
        sql.append("SELECT");
        sql.append("    ypo.PAObjectID as DeviceId,");
        sql.append("    paoName as DeviceName,");
        sql.append("    ypo.Type as DeviceType,");
        sql.append("    null as ExecId,");
        sql.append("    null as ExecType,");
        sql.append("    null as ErrorCode,");
        sql.append("     ").appendArgument_k(LastActionStatus.NA).append("as ActionStatus,");
        sql.append("    null as StartTime,");
        sql.append("    null as StopTime,");
        sql.append("     ").appendArgument_k(InSync.UNVERIFIED).append("as InSync,");
        sql.append("    dc.Name as ConfigName,");
        sql.append("    dc.DeviceConfigurationId as ConfigId");

        sql.append("FROM YukonPAObject ypo");
        sql.append("JOIN DeviceConfigurationDeviceMap scdm ON scdm.DeviceID = ypo.PAObjectID");
        sql.append("JOIN DeviceConfiguration dc ON dc.DeviceConfigurationID = scdm.DeviceConfigurationId");
        sql.append("WHERE ypo.type").in_k(getSupportedPaoTypes());
        if (!CollectionUtils.isEmpty(filter.getGroups())) {
            sql.append("AND").appendFragment(
                deviceGroupService.getDeviceGroupSqlWhereClause(filter.getGroups(), "ypo.PAObjectID"));
        }
        if (!CollectionUtils.isEmpty(filter.getConfigurationIds())) {
            sql.append("AND").append("dc.DeviceConfigurationID").in(filter.getConfigurationIds());
        }
        sql.append("AND PAObjectID NOT IN(");
        sql.append("SELECT DeviceId");
        sql.append("FROM CommandRequestExecResult result");
        sql.append("JOIN CommandRequestExec cre ON cre.CommandRequestExecId = result.CommandRequestExecId");
        sql.append("WHERE CommandRequestExecType").in_k(deviceConfigExecTypes);
        sql.append("UNION");
        sql.append("SELECT DeviceId");
        sql.append("FROM CommandRequestExecRequest request JOIN CommandRequestExec cre ON cre.CommandRequestExecId = request.CommandRequestExecId ");
        sql.append("WHERE CommandRequestExecType").in_k(deviceConfigExecTypes);
        sql.append(")");
    }

    //read/send/verify
    private void buildActionSelect(SqlStatementBuilder sql, DeviceConfigSummaryFilter filter) {
        sql.append("SELECT");
        sql.append("    ypo.PAObjectID as DeviceId,");
        sql.append("    paoName as DeviceName,");
        sql.append("    ypo.Type as DeviceType,");
        sql.append("    t.ExecId,");
        sql.append("    t.ExecType,");
        sql.append("    t.ErrorCode,");
        sql.append("    CASE");
        sql.append("        WHEN t.ErrorCode IS NOT NULL");
        sql.append("        THEN");
        sql.append("            CASE");
        sql.append("                WHEN t.ErrorCode <> 0");
        sql.append("                THEN").appendArgument_k(LastActionStatus.FAILURE);
        sql.append("                ELSE").appendArgument_k(LastActionStatus.SUCCESS);
        sql.append("            END");
        sql.append("        ELSE").appendArgument_k(LastActionStatus.IN_PROGRESS);
        sql.append("    END as ActionStatus,");
        sql.append("    t.StartTime,");
        sql.append("    t.StopTime,");
        sql.append("    InSync,");
        sql.append("    t.ConfigName,");
        sql.append("    t.ConfigId");

        sql.append("FROM YukonPAObject ypo");
        sql.append("JOIN CONFIGTable t ON  ypo.PAObjectId = t.DeviceId");
        sql.append("JOIN SYNCTable st ON ypo.PAObjectId = st.DeviceId");
        sql.append("WHERE InSync").in_k(filter.getInSync());
    }
    
    private void addStatusSelect(SqlStatementBuilder sql, DeviceConfigSummaryFilter filter) {
        List<String> statuses = new ArrayList<>();
        if (filter.contains(LastActionStatus.FAILURE)) {
            statuses.add("res.ErrorCode <> 0");
        }
        if (filter.contains(LastActionStatus.SUCCESS)) {
            statuses.add("res.ErrorCode = 0");
        }
        if (filter.contains(LastActionStatus.IN_PROGRESS)) {
            statuses.add("res.ErrorCode IS NULL");
        }
        sql.append("AND (").append(Joiner.on(" OR ").join(statuses)).append(")");
    
    }
          
    private void buildWithClause(SqlStatementBuilder sql, DeviceConfigSummaryFilter filter) {
        sql.append("WITH");
        sql.append("CONFIGTable AS (");
        sql.append("SELECT");
        sql.append("    ypo.PAObjectID as DeviceId,");
        sql.append("    ypo.Type as DeviceType,");
        sql.append("    t.CommandRequestExecId as ExecId,");
        sql.append("    t.CommandRequestExecType as ExecType,");
        sql.append("    t.ErrorCode,");
        sql.append("    t.StartTime,");
        sql.append("    t.StopTime,");
        sql.append("    dc.Name as ConfigName,");
        sql.append("    dc.DeviceConfigurationId as ConfigId");
        sql.append("FROM (");
        sql.append("SELECT cre.CommandRequestExecId, req.DeviceID, cre.CommandRequestExecType, res.ErrorCode, cre.StartTime, res.CompleteTime as StopTime");
        sql.append("FROM CommandRequestExec cre");
        sql.append("LEFT JOIN CommandRequestExecRequest req ON cre.CommandRequestExecId = req.CommandRequestExecId");
        sql.append("LEFT JOIN CommandRequestExecResult res ON req.CommandRequestExecId = res.CommandRequestExecId AND req.DeviceId = res.DeviceId");
        sql.append("WHERE CommandRequestExecType").in_k(filter.getRequestTypes());
        addStatusSelect(sql, filter);
        sql.append("AND cre.CommandRequestExecId =");
        sql.append("    (");
        sql.append("        SELECT MAX(cre2.CommandRequestExecId)");
        sql.append("        FROM CommandRequestExec cre2");
        sql.append("        JOIN CommandRequestExecRequest req2 ON cre2.CommandRequestExecId = req2.CommandRequestExecId");
        sql.append("        WHERE req.DeviceId = req2.DeviceId");
        sql.append("        AND cre2.CommandRequestExecType").in_k(deviceConfigExecTypes);
        sql.append("    )");
        sql.append(") t ");
        sql.append("JOIN YukonPAObject ypo ON t.DeviceId = ypo.PAObjectID");
        sql.append("JOIN DeviceConfigurationDeviceMap scdm ON scdm.DeviceID = ypo.PAObjectID");
        sql.append("JOIN DeviceConfiguration dc ON dc.DeviceConfigurationID = scdm.DeviceConfigurationId");
        sql.append("WHERE ypo.type").in_k(getSupportedPaoTypes());
        if(!CollectionUtils.isEmpty(filter.getConfigurationIds())){
            sql.append("AND").append("dc.DeviceConfigurationID").in(filter.getConfigurationIds());
        }
        if(!CollectionUtils.isEmpty(filter.getGroups())){
            sql.append("AND").appendFragment(deviceGroupService.getDeviceGroupSqlWhereClause(filter.getGroups(), "ypo.PAObjectID"));
        }
        sql.append(")");
        sql.append(", SYNCTable AS (");
        sql.append("SELECT");
        sql.append("    cfg.DeviceId,");
        sql.append("    CASE");
        sql.append("        WHEN t.ErrorCode IS NOT NULL");
        sql.append("        THEN");
        sql.append("            CASE");
        sql.append("                WHEN t.ErrorCode = 0");
        sql.append("                THEN").appendArgument_k(InSync.IN_SYNC);
        sql.append("                ELSE");
        sql.append("                    CASE");
        sql.append("                        WHEN");
        sql.append("                            (SELECT COUNT(*)");
        sql.append("                            FROM CONFIGTable InnerConfig");
        sql.append("                            WHERE t.DeviceId = InnerConfig.DeviceId");
        sql.append("                            AND InnerConfig.ExecType").neq_k(DeviceRequestType.GROUP_DEVICE_CONFIG_VERIFY);
        sql.append("                            AND InnerConfig.ErrorCode = 0) > 0");
        sql.append("                        THEN").appendArgument_k(InSync.UNVERIFIED);
        sql.append("                        ELSE").appendArgument_k(InSync.OUT_OF_SYNC);
        sql.append("                    END");
        sql.append("            END");
        sql.append("        ELSE").appendArgument_k(InSync.UNVERIFIED);
        sql.append("    END as InSync");
        sql.append("FROM CONFIGTable cfg");
        sql.append("     LEFT JOIN");
        sql.append("     (");
        sql.append("        SELECT");
        sql.append("            req.DeviceId,");
        sql.append("            res.ErrorCode");
        sql.append("            FROM");
        sql.append("            CommandRequestExec cre");
        sql.append("            LEFT JOIN CommandRequestExecRequest req ON cre.CommandRequestExecId = req.CommandRequestExecId");
        sql.append("            LEFT JOIN CommandRequestExecResult res ON req.CommandRequestExecId = res.CommandRequestExecId  AND req.DeviceId = res.DeviceId");
        sql.append("            WHERE");
        sql.append("            (");
        sql.append("                cre.CommandRequestExecType").eq_k(DeviceRequestType.GROUP_DEVICE_CONFIG_VERIFY);
        sql.append("                AND cre.CommandRequestExecId =");
        sql.append("                (");
        sql.append("                    SELECT");
        sql.append("                        MAX(cre2.CommandRequestExecId)");
        sql.append("                        FROM CommandRequestExec cre2");
        sql.append("                        JOIN CommandRequestExecRequest req2 ON cre2.CommandRequestExecId = req2.CommandRequestExecId");
        sql.append("                        WHERE CommandRequestExecType").eq_k(DeviceRequestType.GROUP_DEVICE_CONFIG_VERIFY);
        sql.append("                        AND req2.DeviceID = res.DeviceId"); 
        sql.append("                )");
        sql.append("            )");
        sql.append("            OR");
        sql.append("            (");
        sql.append("                req.DeviceId IS NOT NULL");
        sql.append("                AND res.DeviceId IS NULL");
        sql.append("                AND CommandRequestExecType").eq_k(DeviceRequestType.GROUP_DEVICE_CONFIG_VERIFY);        
        sql.append("            )");
        sql.append("    ) t ON cfg.DeviceId = t.DeviceId");
        sql.append(")");
    }
   
    @Override
    public List<DeviceConfigActionHistoryDetail> getDeviceConfigActionHistory(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT");
        sql.append("CommandRequestExecType as ExecType,");
        sql.append("cre.StartTime,");
        sql.append("result.CompleteTime as StopTime,");
        sql.append("CASE");
        sql.append("    WHEN result.ErrorCode <> 0 THEN").appendArgument_k(LastActionStatus.FAILURE);
        sql.append("    WHEN result.ErrorCode = 0 THEN").appendArgument_k(LastActionStatus.SUCCESS);
        sql.append("    ELSE").appendArgument_k(LastActionStatus.IN_PROGRESS);
        sql.append("END as ActionStatus, request.DeviceId");
        sql.append("FROM CommandRequestExec cre");
        sql.append("LEFT JOIN CommandRequestExecRequest request ON cre.CommandRequestExecId = request.CommandRequestExecId");
        sql.append("LEFT JOIN CommandRequestExecResult result ON cre.CommandRequestExecId = result.CommandRequestExecId AND request.DeviceId = result.DeviceId");
        sql.append("WHERE cre.CommandRequestExecType").in_k(deviceConfigExecTypes);
        sql.append("AND (result.DeviceId").eq(deviceId);
        sql.append("    OR request.DeviceId").eq(deviceId).append(")");
        sql.append("ORDER BY cre.StartTime DESC");
        log.debug(sql);
        return jdbcTemplate.query(sql, new HistoryRowMapper());
    }
    
    private List<PaoType> getSupportedPaoTypes() {
        return paoDefinitionDao.getPaosThatSupportTag(PaoTag.DEVICE_CONFIGURATION).stream().map(
            t -> t.getType()).collect(Collectors.toList());
    }
}
