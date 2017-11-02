package com.cannontech.web.tools.device.config.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.impl.DeviceDaoImpl;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.web.tools.device.config.dao.DeviceConfigSummaryDao;
import com.cannontech.web.tools.device.config.model.DeviceConfigActionHistory;
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
            detail.setDisplayRead(true);
            detail.setDisplaySend(true);
            detail.setDisplayVerify(true);
            detail.setInSync(rs.getEnum("InSync", InSync.class));
            detail.setStatus(rs.getEnum("ActionStatus", LastActionStatus.class));
            return detail;
        }
    }
    
    /*
     *  State    Last action Success?    Prior verify?   In sync?
        Unassigned  n/a n/a n/a n/a
                        
        Assigned    None    n/a n/a Unverified
                        
        Assigned    Send    Fail    None    Unverified
        Assigned    Send    Fail    Success In sync
        Assigned    Send    Fail    Fail    Out of sync
        Assigned    Send    Success None    Unverified
        Assigned    Send    Success Success In sync
        Assigned    Send    Success Fail    Unverified
                        
        Assigned    Send in progress    n/a None    Unverified
        Assigned    Send in progress    n/a Success In sync
        Assigned    Send in progress    n/a Fail    Out of sync
                        
        Assigned    Read    Fail    None    Unverified
        Assigned    Read    Fail    Success In sync
        Assigned    Read    Fail    Fail    Out of sync
        Assigned    Read    Success None    Unverified
        Assigned    Read    Success Success In sync
        Assigned    Read    Success Fail    Unverified
                        
        Assigned    Read in progress    n/a None    Unverified
        Assigned    Read in progress    n/a Success In sync
        Assigned    Read in progress    n/a Fail    Out of sync
                        
        Assigned    Verify  Yes n/a In sync
        Assigned    Verify  No  n/a Out of sync

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
            sql.append("SELECT DeviceId, DeviceName, DeviceType, ExecId, ExecType, ActionStatus, StartTime, StopTime, InSync, ConfigName, ConfigId");
        }
        sql.append("FROM (");  
        if (filter.contains(LastActionStatus.FAILURE) || filter.contains(LastActionStatus.SUCCESS)) {
            filter.getActions().forEach(action -> {
                addUnion.add(sql);
                buildActionSelect(sql, filter, action);
                addUnion.addUnionBeforeNextSelect = true;
            });
        }
        if (filter.contains(LastActionStatus.IN_PROGRESS)) {
            addUnion.add(sql);
            buildInProgressSelect(sql, filter);
            addUnion.addUnionBeforeNextSelect = true;
        }
        if (filter.contains(InSync.UNVERIFIED)) {
            addUnion.add(sql);
            buildNoActionSelect(sql, filter);
            addUnion.addUnionBeforeNextSelect = true;
        }
        if (filter.isDisplayUnassigned()) {
            addUnion.add(sql);
            buildUnassignedSelect(sql, filter);
        }
        sql.append(") results");
        if (sortBy != null) {
            sql.append("ORDER BY");
            sql.append(sortBy.getDbString());
            sql.append(direction);
        }
        return sql;
    }

    private void buildUnassignedSelect(SqlStatementBuilder sql, DeviceConfigSummaryFilter filter) {
        sql.append("SELECT");
        sql.append("    ypo.PAObjectID as DeviceId,");
        sql.append("    ypo.paoName as DeviceName,");
        sql.append("    ypo.Type as DeviceType,");
        sql.append("    null as ExecId,");
        sql.append("    null as ExecType,");
        sql.append("    '" + LastActionStatus.NA + "' as ActionStatus,");
        sql.append("    null as StartTime,");
        sql.append("    null as StopTime,");
        sql.append("    '" + InSync.NA + "' as InSync,");
        sql.append("    null as ConfigName,");
        sql.append("    null as ConfigId");
               
        sql.append("FROM YukonPAObject ypo");
        sql.append("WHERE ypo.type").in(getSupportedPaoTypes());
        sql.append("AND ypo.PAObjectID NOT IN (select DeviceID from DeviceConfigurationDeviceMap)");
        if (!CollectionUtils.isEmpty(filter.getGroups())) {
            sql.append("AND").appendFragment(
                deviceGroupService.getDeviceGroupSqlWhereClause(filter.getGroups(), "ypo.PAObjectID"));
        }
    }

    private boolean buildNoActionSelect(SqlStatementBuilder sql, DeviceConfigSummaryFilter filter) {
        sql.append("SELECT");
        sql.append("    ypo.PAObjectID as DeviceId,");
        sql.append("    paoName as DeviceName,");
        sql.append("    ypo.Type as DeviceType,");
        sql.append("    null as ExecId,");
        sql.append("    null as ExecType,");
        sql.append("    '" + LastActionStatus.NA + "' as ActionStatus,");
        sql.append("    null as StartTime,");
        sql.append("    null as StopTime,");
        sql.append("    '" + InSync.UNVERIFIED + "' as InSync,");
        sql.append("    dc.Name as ConfigName,");
        sql.append("    dc.DeviceConfigurationId as ConfigId");

        sql.append("FROM YukonPAObject ypo");
        sql.append("JOIN DeviceConfigurationDeviceMap scdm ON scdm.DeviceID = ypo.PAObjectID");
        sql.append("JOIN DeviceConfiguration dc ON dc.DeviceConfigurationID = scdm.DeviceConfigurationId");
        sql.append("WHERE ypo.type").in(getSupportedPaoTypes());
        if (!CollectionUtils.isEmpty(filter.getGroups())) {
            sql.append("AND").appendFragment(
                deviceGroupService.getDeviceGroupSqlWhereClause(filter.getGroups(), "ypo.PAObjectID"));
        }
        sql.append("AND");
        sql.append("PAObjectID NOT IN (");
        sql.append("    SELECT DeviceId from CommandRequestExecResult result");
        sql.append("    JOIN CommandRequestExec cre ON cre.CommandRequestExecId = result.CommandRequestExecId");
        sql.append("    WHERE cre.CommandRequestExecType").in_k(deviceConfigExecTypes).append(")");
        sql.append("AND");
        sql.append("PAObjectID NOT IN (");
        sql.append("    SELECT DeviceId from CommandRequestExecRequest request");
        sql.append("    JOIN CommandRequestExec cre ON cre.CommandRequestExecId = request.CommandRequestExecId");
        sql.append("    WHERE cre.CommandRequestExecType").in_k(deviceConfigExecTypes).append(")");
        return true;
    }

    private void buildActionSelect(SqlStatementBuilder sql, DeviceConfigSummaryFilter filter, LastAction action) {
        sql.append("SELECT");
        sql.append("    ypo.PAObjectID as DeviceId,");
        sql.append("    paoName as DeviceName,");
        sql.append("    ypo.Type as DeviceType,");
        sql.append("    t.ExecId,");
        sql.append("    t.ExecType,");
        sql.append("    CASE");
        sql.append("        WHEN t.ErrorCode > 0 THEN '" + LastActionStatus.FAILURE + "'");
        sql.append("        WHEN t.ErrorCode = 0 THEN '" + LastActionStatus.SUCCESS + "'");
        sql.append("    END as ActionStatus,");
        sql.append("    t.StartTime,");
        sql.append("    t.StopTime,");
        sql.append("    CASE");
        sql.append("        WHEN vt.ErrorCode IS NULL THEN '" + InSync.UNVERIFIED + "'");
        sql.append("        WHEN vt.ErrorCode > 0 THEN '" + InSync.OUT_OF_SYNC + "'");
        sql.append("        WHEN vt.ErrorCode = 0 THEN '" + InSync.IN_SYNC + "'");
        sql.append("    END as InSync,");
        sql.append("    dc.Name as ConfigName,");
        sql.append("    dc.DeviceConfigurationId as ConfigId");

        sql.append("FROM YukonPAObject ypo");
        sql.append("JOIN DeviceConfigurationDeviceMap scdm ON scdm.DeviceID = ypo.PAObjectID");
        sql.append("JOIN DeviceConfiguration dc ON dc.DeviceConfigurationID = scdm.DeviceConfigurationId");
        sql.append("JOIN " + action + "Table t ON  ypo.PAObjectId = t.DeviceId");
        sql.append("LEFT JOIN " + LastAction.VERIFY + "Table vt ON  ypo.PAObjectId = vt.DeviceId");
        sql.append("WHERE ypo.type").in(getSupportedPaoTypes());
        addInSyncSelect(sql, filter);
        List<String> statuses = new ArrayList<>();
        if (filter.contains(LastActionStatus.FAILURE)) {
            statuses.add("t.ErrorCode > 0");
        }
        if (filter.contains(LastActionStatus.SUCCESS)) {
            statuses.add("t.ErrorCode = 0");
        }
        sql.append("AND (").append(Joiner.on(" OR ").join(statuses)).append(")");
    }
    
    private void buildInProgressSelect(SqlStatementBuilder sql, DeviceConfigSummaryFilter filter) {
        sql.append("SELECT");
        sql.append("    ypo.PAObjectID as DeviceId,");
        sql.append("    paoName as DeviceName,");
        sql.append("    ypo.Type as DeviceType,");
        sql.append("    cre.CommandRequestExecId as ExecId,");
        sql.append("    cre.CommandRequestExecType as ExecType,");
        sql.append("    '"+LastActionStatus.IN_PROGRESS+"' as ActionStatus,");
        sql.append("    cre.StartTime,");
        sql.append("    null as StopTime,");
        sql.append("    CASE");
        sql.append("        WHEN vt.ErrorCode IS NULL THEN '" + InSync.UNVERIFIED + "'");
        sql.append("        WHEN vt.ErrorCode > 0 THEN '" + InSync.OUT_OF_SYNC + "'");
        sql.append("        WHEN vt.ErrorCode = 0 THEN '" + InSync.IN_SYNC + "'");
        sql.append("    END as InSync,");
        sql.append("    dc.Name as ConfigName,");
        sql.append("    dc.DeviceConfigurationId as ConfigId");

        sql.append("FROM YukonPAObject ypo");
        sql.append("JOIN DeviceConfigurationDeviceMap scdm ON scdm.DeviceID = ypo.PAObjectID");
        sql.append("JOIN DeviceConfiguration dc ON dc.DeviceConfigurationID = scdm.DeviceConfigurationId");
        sql.append("JOIN CommandRequestExecRequest request ON ypo.PAObjectId = request.DeviceId");
        sql.append("JOIN CommandRequestExec cre ON request.CommandRequestExecId = cre.CommandRequestExecId");
        sql.append("LEFT JOIN VerifyTable vt ON  ypo.PAObjectId = vt.DeviceId");
        sql.append("WHERE ypo.type").in(getSupportedPaoTypes());
        sql.append("AND CommandRequestExecType").in(
            filter.getActions().stream().map(action -> action.getRequestType()).collect(Collectors.toList()));
        sql.append("AND PAObjectID NOT IN (SELECT deviceid from CommandRequestExecResult result WHERE request.CommandRequestExecId = result.CommandRequestExecId AND request.DeviceId = result.DeviceId)");
        addInSyncSelect(sql, filter);
    }
    
    private void addInSyncSelect(SqlStatementBuilder sql, DeviceConfigSummaryFilter filter){
        List<String> inSync = new ArrayList<>();
        if (filter.contains(InSync.IN_SYNC)) {
            inSync.add("vt.ErrorCode = 0");
        }
        if (filter.contains(InSync.OUT_OF_SYNC)) {
            inSync.add("vt.ErrorCode > 0");
        }
        if (filter.contains(InSync.UNVERIFIED)) {
            inSync.add("vt.ErrorCode IS NULL");
        }
        sql.append("AND (").append(Joiner.on(" OR ").join(inSync)).append(")");
    }
      
    private void buildWithClause(SqlStatementBuilder sql, DeviceConfigSummaryFilter filter) {
        sql.append("WITH");
        addTable(sql, filter.getGroups(), filter.getConfigurationIds(), LastAction.VERIFY, true);
        addTable(sql, filter.getGroups(), filter.getConfigurationIds(), LastAction.SEND, true);
        addTable(sql, filter.getGroups(), filter.getConfigurationIds(), LastAction.READ, false);
    }
    
    private void addTable(SqlStatementBuilder sql, List<DeviceGroup> groups, List<Integer> configurationIds, LastAction action, boolean addComma){
        sql.append(action + "Table AS (");
        sql.append("SELECT");
        sql.append("    ypo.PAObjectID as DeviceId,");
        sql.append("    ypo.paoName as DeviceName,");
        sql.append("    ypo.Type as DeviceType,");
        sql.append("    CommandRequestExecId as ExecId,");
        sql.append("    CommandRequestExecType as ExecType,");
        sql.append("    ErrorCode as ErrorCode,");
        sql.append("    StartTime,");
        sql.append("    StopTime,");
        sql.append("    dc.Name as ConfigName,");
        sql.append("    dc.DeviceConfigurationId as ConfigId");
        sql.append("FROM (");
        sql.append("SELECT cre.CommandRequestExecId, DeviceId, CommandRequestExecType, crer.ErrorCode, cre.StartTime, crer.CompleteTime as StopTime");
        sql.append("FROM CommandRequestExec cre");
        sql.append("JOIN CommandRequestExecResult crer ON crer.CommandRequestExecId = cre.CommandRequestExecId");
        sql.append("WHERE CommandRequestExecType").eq(action.getRequestType());
        sql.append("AND cre.CommandRequestExecId =");
        sql.append("    (");
        sql.append("        SELECT MAX(cre2.CommandRequestExecId)");
        sql.append("        FROM CommandRequestExec cre2");
        sql.append("        JOIN CommandRequestExecResult crer2 ON crer2.CommandRequestExecId = cre2.CommandRequestExecId");
        sql.append("        WHERE CommandRequestExecType").eq(action.getRequestType());
        sql.append("        AND crer2.DeviceID = crer.DeviceId");
        sql.append("    )");
        sql.append(") AS t ");
        sql.append("JOIN YukonPAObject ypo ON t.DeviceId = ypo.PAObjectID");
        sql.append("JOIN DeviceConfigurationDeviceMap scdm ON scdm.DeviceID = ypo.PAObjectID");
        sql.append("JOIN DeviceConfiguration dc ON dc.DeviceConfigurationID = scdm.DeviceConfigurationId");

        if(!CollectionUtils.isEmpty(configurationIds)){
            sql.append("AND").append("dc.DeviceConfigurationID").in(configurationIds);
        }
        if(!CollectionUtils.isEmpty(groups)){
            sql.append("AND").appendFragment(deviceGroupService.getDeviceGroupSqlWhereClause(groups, "ypo.PAObjectID"));
        }
        sql.append(")");
        if(addComma){
            sql.append(",");
        }
    }
   
    @Override
    public DeviceConfigActionHistory getDeviceConfigActionHistory(int deviceId) {
        return new DeviceConfigActionHistory();
    }
    
    private List<PaoType> getSupportedPaoTypes() {
        return paoDefinitionDao.getPaosThatSupportTag(PaoTag.DEVICE_CONFIGURATION).stream().map(
            t -> t.getType()).collect(Collectors.toList());
    }
    
    @Override
    public List<SimpleDevice> getDevicesToVerify(){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT paobjectId, Type");
        sql.append("FROM (");
        sql.append("    SELECT cre.CommandRequestExecId, DeviceId, CommandRequestExecType");
        sql.append("        FROM CommandRequestExec cre");
        sql.append("        JOIN CommandRequestExecResult crer ON crer.CommandRequestExecId = cre.CommandRequestExecId");
        sql.append("        WHERE CommandRequestExecType").in_k(deviceConfigExecTypes);
        sql.append("        AND cre.CommandRequestExecId =");
        sql.append("        (");
        sql.append("            SELECT MAX(cre2.CommandRequestExecId)");
        sql.append("            FROM CommandRequestExec cre2");
        sql.append("            JOIN CommandRequestExecResult crer2");
        sql.append("                ON crer2.CommandRequestExecId = cre2.CommandRequestExecId");
        sql.append("                WHERE CommandRequestExecType").in_k(deviceConfigExecTypes);
        sql.append("                AND crer2.DeviceID = crer.DeviceId");
        sql.append("        )");
        sql.append(") AS t");
        sql.append("JOIN YukonPAObject ypo");
        sql.append("    ON t.DeviceId = ypo.PAObjectID");
        sql.append("WHERE t.CommandRequestExecType").neq_k(DeviceRequestType.GROUP_DEVICE_CONFIG_VERIFY);
        return jdbcTemplate.query(sql, DeviceDaoImpl.SIMPLE_DEVICE_MAPPER);
    }
}
