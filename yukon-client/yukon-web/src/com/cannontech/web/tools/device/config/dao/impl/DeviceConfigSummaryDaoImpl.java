package com.cannontech.web.tools.device.config.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastAction;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastActionStatus;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.web.tools.device.config.dao.DeviceConfigSummaryDao;
import com.cannontech.web.tools.device.config.model.DeviceConfigActionHistoryDetail;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryDetail;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter;
import com.google.common.collect.ImmutableList;

public class DeviceConfigSummaryDaoImpl implements DeviceConfigSummaryDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private static final Logger log = YukonLogManager.getLogger(DeviceConfigSummaryDaoImpl.class);
    private static List<DeviceRequestType> deviceConfigExecTypes = ImmutableList.of(DeviceRequestType.GROUP_DEVICE_CONFIG_SEND, 
            DeviceRequestType.GROUP_DEVICE_CONFIG_READ);
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired ConfigurationSource configurationSource;
    
    private String rfTemplatePrefix;
    
    @PostConstruct
    public void initialize() {
         rfTemplatePrefix = configurationSource.getString(MasterConfigString.RFN_METER_TEMPLATE_PREFIX, "*RfnTemplate_");
    }

    private final YukonRowMapper<DeviceConfigSummaryDetail> detailRowMapper = rs -> {
        DeviceConfigSummaryDetail detail = new DeviceConfigSummaryDetail();
        detail.setAction(rs.getEnum("LastAction", LastAction.class));
        detail.setActionStart(rs.getInstant("LastActionStart"));
        detail.setActionEnd(rs.getInstant("LastActionEnd"));
        detail.setDevice(new DisplayableDevice(rs.getPaoIdentifier("PAObjectID", "Type"), rs.getString("PaoName")));
        Integer configId = rs.getNullableInt("DeviceConfigurationId");
        if (configId != null) {
            detail.setDeviceConfig(new LightDeviceConfiguration(configId, rs.getString("Name"), null));
        }
        detail.setState(rs.getEnum("CurrentState", ConfigState.class));
        detail.setStatus(rs.getEnum("LastActionStatus", LastActionStatus.class));
        detail.setErrorCode(rs.getNullableInt("ErrorCode"));
        log.debug(detail);
        return detail;
    };
    
    private final YukonRowMapper<DeviceConfigActionHistoryDetail> historyRowMapper = rs -> {
        DeviceConfigActionHistoryDetail detail = new DeviceConfigActionHistoryDetail();
        detail.setAction(LastAction.getByRequestType(rs.getEnum("ExecType", DeviceRequestType.class)));
        detail.setActionEnd(rs.getInstant("StopTime"));
        detail.setActionStart(rs.getInstant("StartTime"));
        detail.setStatus(rs.getEnum("ActionStatus", LastActionStatus.class));
        return detail;
    };
    
    @Override
    public SearchResults<DeviceConfigSummaryDetail> getSummary(DeviceConfigSummaryFilter filter, PagingParameters paging,
            SortBy sortBy, Direction direction) {
        log.debug("filter:{}", filter);
        SqlStatementBuilder allRowsSql = buildSummarySelect(filter, sortBy, direction, false);
        SqlStatementBuilder countSql = buildSummarySelect(filter, null, null, true);
        int totalCount = jdbcTemplate.queryForInt(countSql);

        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();

        PagingResultSetExtractor<DeviceConfigSummaryDetail> rse = new PagingResultSetExtractor<>(start, count, detailRowMapper);
        jdbcTemplate.query(allRowsSql, rse);

        SearchResults<DeviceConfigSummaryDetail> searchResult = new SearchResults<>();
        searchResult.setBounds(paging.getStartIndex(), paging.getItemsPerPage(), totalCount);
        searchResult.setResultList(rse.getResultList());

        return searchResult;
    }
    
    @Override
    public DeviceConfigSummaryDetail getSummaryForDevice(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        buildCommonStateSelect(sql, false);
        sql.append("WHERE ypo.PAObjectID").eq(deviceId);
        return jdbcTemplate.queryForObject(sql, detailRowMapper);
    }
    
    private SqlStatementBuilder buildUnassignSelect(DeviceConfigSummaryFilter filter, boolean selectCount) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        if (selectCount) {
            sql.append("SELECT ypo.PAObjectID");
        } else {
            sql.append("SELECT");
            sql.append("ypo.PAObjectID,");
            sql.append("ypo.paoName,");
            sql.append("ypo.Type,");
            sql.append("null as LastAction,");
            sql.append("null as LastActionStatus,");
            sql.append("null as LastActionStart,");
            sql.append("null as LastActionEnd,");
            sql.append("null as CurrentState,");
            sql.append("null as ErrorCode,");
            sql.append("null as Name,");
            sql.append("null as DeviceConfigurationId");
        }
        sql.append("FROM YukonPAObject ypo");
        sql.append("WHERE ypo.type").in_k(getSupportedPaoTypes());
        sql.append("AND ypo.PAObjectID NOT IN (select DeviceID from DeviceConfigurationDeviceMap)");
        sql.append("AND ypo.PAOName NOT").startsWith(rfTemplatePrefix);
        return sql;
    }

    private SqlStatementBuilder buildStateSelect(DeviceConfigSummaryFilter filter, boolean selectCount) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        buildCommonStateSelect(sql, selectCount);
        sql.append("WHERE ypo.type").in_k(getSupportedPaoTypes());
        if(!filter.isDisplayAll()) {
            sql.append("AND scdm.DeviceConfigurationId").in(filter.getConfigurationIds());
        }
        if (filter.getStateSelection() == StateSelection.ALL) {
            sql.append("AND (CurrentState").in_k(filter.getStateSelection().getStates());
            sql.append("OR LastActionStatus").eq_k(LastActionStatus.IN_PROGRESS).append(")");
        } else if (filter.getStateSelection() == StateSelection.IN_PROGRESS) {
            sql.append("AND LastActionStatus").eq_k(LastActionStatus.IN_PROGRESS);
        } else {
            sql.append("AND CurrentState").in_k(filter.getStateSelection().getStates());
        }
        sql.append("AND ypo.PAOName NOT").startsWith(rfTemplatePrefix);
        return sql;
    }
    
    private void buildCommonStateSelect(SqlStatementBuilder sql, boolean selectCount) {
        if (selectCount) {
            sql.append("SELECT ypo.PAObjectID");
        } else {
            sql.append("SELECT");
            sql.append("ypo.PAObjectID,");
            sql.append("ypo.paoName,");
            sql.append("ypo.Type,");
            sql.append("dcs.LastAction,");
            sql.append("dcs.LastActionStatus,");
            sql.append("dcs.LastActionStart,");
            sql.append("dcs.LastActionEnd,");
            sql.append("dcs.CurrentState,");
            sql.append("crer.ErrorCode,");
            sql.append("dc.Name,");
            sql.append("dc.DeviceConfigurationId");
        }
        sql.append("FROM DeviceConfigState dcs");
        sql.append("JOIN YukonPAObject ypo ON ypo.PAObjectID = dcs.PAObjectID");
        sql.append("JOIN DeviceConfigurationDeviceMap scdm ON dcs.PAObjectID = scdm.DeviceId");
        sql.append("JOIN DeviceConfiguration dc ON dc.DeviceConfigurationID = scdm.DeviceConfigurationId");
        if (!selectCount) {
            sql.append("LEFT JOIN CommandRequestExecResult crer ON crer.CommandRequestExecId = dcs.CommandRequestExecId AND dcs.PAObjectID=crer.DeviceId");
        }
    }

    private SqlStatementBuilder buildSummarySelect(DeviceConfigSummaryFilter filter, SortBy sortBy,
            Direction direction, boolean selectCount) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        if (selectCount) {
            sql.append("SELECT count(PaObjectId)");
            sql.append("FROM (");
        }
        if (filter.isDisplayAssigned() || filter.isDisplayAll()) {
            sql.append(buildStateSelect(filter, selectCount));
            if (filter.isDisplayUnassigned() || filter.isDisplayAll()) {
                if (!CollectionUtils.isEmpty(filter.getGroups())) {
                    sql.append("AND").appendFragment(
                            deviceGroupService.getDeviceGroupSqlWhereClause(filter.getGroups(), "ypo.PAObjectID"));
                }
                sql.append("UNION");
            }
        }
        if (filter.isDisplayUnassigned() || filter.isDisplayAll()) {
            sql.append(buildUnassignSelect(filter, selectCount));
        }
        addGroupsAndOrderBy(filter, sortBy, direction, sql);
        if (selectCount) {
            sql.append(") T");
        }
        return sql;
    }

    private void addGroupsAndOrderBy(DeviceConfigSummaryFilter filter, SortBy sortBy, Direction direction,
            SqlStatementBuilder sql) {
        if (!CollectionUtils.isEmpty(filter.getGroups())) {
            sql.append("AND").appendFragment(
                    deviceGroupService.getDeviceGroupSqlWhereClause(filter.getGroups(), "ypo.PAObjectID"));
        }

        if (sortBy != null) {
            sql.append("ORDER BY");
            sql.append(sortBy.getDbString());
            sql.append(direction);
        }
    }

    @Override
    public List<DeviceConfigActionHistoryDetail> getDeviceConfigActionHistory(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ExecType, StartTime, StopTime, ActionStatus, DeviceId");
        sql.append("FROM (");
        
        //Getting config send and reads history from Command Request execution tables
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
        //GROUP_DEVICE_CONFIG_VERIFY is excluded because it doesn't always mean ASSIGN_CONFIG or UNASSIGN_CONFIG, clicking "Needs Upload" also does verify
        sql.append("WHERE cre.CommandRequestExecType").in_k(deviceConfigExecTypes);
        sql.append("AND (result.DeviceId").eq(deviceId);
        sql.append("    OR request.DeviceId").eq(deviceId).append(")");
        
        sql.append("UNION");
        
        //Getting assign and unassign history from collection action tables 
        sql.append("SELECT");
        sql.appendArgument_k(DeviceRequestType.GROUP_DEVICE_CONFIG_VERIFY).append(" as ExecType,");
        sql.append("ca.StartTime,");
        sql.append("ca.StopTime,");
        sql.append("CASE");
        sql.append("    WHEN request.Result=").appendArgument_k(CommandRequestExecutionStatus.COMPLETE).append("THEN").appendArgument_k(LastActionStatus.SUCCESS);
        sql.append("    WHEN request.Result=").appendArgument_k(CommandRequestExecutionStatus.FAILED).append("THEN").appendArgument_k(LastActionStatus.FAILURE);
        sql.append("    ELSE").appendArgument_k(LastActionStatus.IN_PROGRESS);
        sql.append("END as ActionStatus, request.PAObjectID as DeviceId");
        sql.append("FROM CollectionAction ca");
        sql.append("JOIN CollectionActionRequest request ON ca.CollectionActionId = request.CollectionActionId");
        sql.append("WHERE Action").in_k(List.of(CollectionAction.ASSIGN_CONFIG, CollectionAction.UNASSIGN_CONFIG));
        sql.append("AND request.PAObjectID").eq(deviceId);

        sql.append(") results");
        sql.append("ORDER BY StartTime DESC");
        log.debug(sql.getDebugSql());
        return jdbcTemplate.query(sql, historyRowMapper);
    }
    
    private List<PaoType> getSupportedPaoTypes() {
        return paoDefinitionDao.getPaosThatSupportTag(PaoTag.DEVICE_CONFIGURATION).stream()
                .map(PaoDefinition::getType)
                .filter(PaoType::isMeter)
                .collect(Collectors.toList());
    }
}
