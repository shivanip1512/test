package com.cannontech.common.bulk.collection.device.dao.impl;

import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.CANCELED;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.CONFIRMED;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.FAILURE;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.SUCCESS;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.UNCONFIRMED;
import static com.cannontech.common.device.commands.CommandRequestExecutionStatus.COMPLETE;
import static com.cannontech.common.device.commands.CommandRequestExecutionStatus.FAILED;
import static com.cannontech.common.device.commands.CommandRequestExecutionStatus.STARTED;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.dao.CollectionActionDao;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionFilter;
import com.cannontech.common.bulk.collection.device.model.CollectionActionFilteredResult;
import com.cannontech.common.bulk.collection.device.model.CollectionActionProcess;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.service.CollectionActionLogDetailService;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class CollectionActionDaoImpl implements CollectionActionDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandRequestExecutionResultDao crerDao;
    @Autowired private TemporaryDeviceGroupService tempGroupService;
    @Autowired private DeviceGroupMemberEditorDao editorDao;
    @Autowired private DeviceGroupCollectionHelper groupHelper;
    @Autowired private CollectionActionLogDetailService logService;
   
    private static final Logger log = YukonLogManager.getLogger(CollectionActionDaoImpl.class);
    
    private final YukonRowMapper<CollectionActionResult> resultMapper = new YukonRowMapper<CollectionActionResult>() {
        @Override
        public CollectionActionResult mapRow(YukonResultSet rs) throws SQLException {
            int key = rs.getInt("CollectionActionId");
            
            CollectionActionResult result = null;
            CollectionAction action = rs.getEnum("Action", CollectionAction.class);
            if (action.getProcess() == CollectionActionProcess.CRE) {
                result = buildCreResult(action, key);
            } else if (action.getProcess() == CollectionActionProcess.DB) {
                result = buildDbResult(action, key);
            }
            result.setLoadedFromDatabase(true);
            result.setCacheKey(key);
            result.setStatus(rs.getEnum("Status", CommandRequestExecutionStatus.class));
            result.setStartTime(rs.getInstant("StartTime"));
            result.setStopTime(rs.getInstant("StopTime"));
            result.setLogger(log);
            log.debug("--------Loaded result:");
            result.log();
            return result;      
        }
    };
    
    @Override
    @Transactional
    public void createCollectionAction(CollectionActionResult result, LiteYukonUser user) {
        final int caId;
        if (result.getExecution() != null) {
            caId = createAction(result.getAction(), result.getStartTime(),
                result.getExecution().getCommandRequestExecutionStatus(), user);
            SqlStatementBuilder sql = new SqlStatementBuilder();
            SqlParameterSink sink = sql.insertInto("CollectionActionCommandRequest");
            sink.addValue("CollectionActionId", caId);
            sink.addValue("CommandRequestExecId", result.getExecution().getId());
            jdbcTemplate.update(sql);
        } else {
            caId = createAction(result.getAction(), result.getStartTime(), STARTED, user);
            SqlStatementBuilder sql = new SqlStatementBuilder();
            List<List<Object>> values = result.getInputs().getCollection().getDeviceList().stream().map(d -> {
                int collectionActionRequestId = nextValueHelper.getNextValue("CollectionActionRequest");
                List<Object> row = Lists.newArrayList(collectionActionRequestId, caId, d.getDeviceId(), STARTED);
                return row;
            }).collect(Collectors.toList());
            sql.batchInsertInto("CollectionActionRequest").columns("CollectionActionRequestId", "CollectionActionId",
                "PAObjectID", "Result").values(values);
            jdbcTemplate.yukonBatchUpdate(sql);
        }
        if(result.getInputs().getInputs() != null) {
            saveInputs(caId, result.getInputs().getInputs());
        }
        result.setCacheKey(caId);
    }
    
    @Override
    public List<CollectionAction> getHistoricalActions() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT Action");
        sql.append("FROM CollectionAction");
        return jdbcTemplate.query(sql, new YukonRowMapper<CollectionAction>() {
            @Override
            public CollectionAction mapRow(YukonResultSet rs) throws SQLException {
                return rs.getEnum("Action", CollectionAction.class);
            }
        });
    }
    
    @Override
    public SearchResults<CollectionActionFilteredResult> getCollectionActionFilteredResults(
            CollectionActionFilter filter, PagingParameters paging, SortBy sortBy, Direction direction, YukonUserContext userContext) {

        SqlStatementBuilder allRowsSql = buildResultSelect(filter, sortBy, direction, userContext);
        SqlStatementBuilder countSql = buildResultSelect(filter, null, null, userContext);

        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();

        PagingResultSetExtractor<CollectionActionFilteredResult> rse =
            new PagingResultSetExtractor<>(start, count, new YukonRowMapper<CollectionActionFilteredResult>() {
                @Override
                public CollectionActionFilteredResult mapRow(YukonResultSet rs) throws SQLException {
                    CollectionActionFilteredResult result = new CollectionActionFilteredResult();
                    result.setCacheKey(rs.getInt("CollectionActionId"));
                    result.setAction(rs.getEnum("Action", CollectionAction.class));
                    result.setStartTime(rs.getInstant("StartTime"));
                    result.setStopTime(rs.getInstant("StopTime"));
                    result.setStatus(rs.getEnum("Status", CommandRequestExecutionStatus.class));
                    result.setNotAttemptedCount(rs.getInt("NotAttemptedCount"));
                    result.setFailureCount(rs.getInt("FailedCount"));
                    result.setSuccessCount(rs.getInt("SuccessCount"));
                    result.setUserName(rs.getString("UserName"));
                    return result;
                }
                
            });
        jdbcTemplate.query(allRowsSql, rse);

        SearchResults<CollectionActionFilteredResult> searchResult = new SearchResults<>();
        int totalCount = jdbcTemplate.queryForInt(countSql);
        searchResult.setBounds(paging.getStartIndex(), paging.getItemsPerPage(), totalCount);
        searchResult.setResultList(rse.getResultList());

        return searchResult;
    }
    
    /**
     * If sortBy is not returns count sql, otherwise returns all the fields
     */
    private SqlStatementBuilder buildResultSelect(CollectionActionFilter filter, SortBy sortBy, Direction direction, YukonUserContext userContext) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("WITH Successes AS");
        sql.append("(");
        sql.append("    SELECT COUNT(1) AS SuccessCount, CollectionActionId");
        sql.append("    FROM CollectionActionRequest WHERE Result").eq_k(COMPLETE);
        sql.append("    GROUP BY CollectionActionId ");
        sql.append("UNION");
        sql.append("    SELECT COUNT(1) AS SuccessCount, CA.CollectionActionId");
        sql.append("    FROM CollectionAction CA");
        sql.append("    JOIN CollectionActionCommandRequest CACR ON CA.CollectionActionId = CACR.CollectionActionId");
        sql.append("    JOIN CommandRequestExecResult CRER ON CACR.CommandRequestExecId = CRER.CommandRequestExecId");
        sql.append("    WHERE CRER.ErrorCode = 0");
        sql.append("    GROUP BY CA.CollectionActionId ");
        sql.append(")");
        
        sql.append(", Failures AS");
        sql.append("(");
        sql.append("    SELECT COUNT(1) AS FailureCount, CollectionActionId");
        sql.append("    FROM CollectionActionRequest WHERE Result").eq_k(FAILED);
        sql.append("    GROUP BY CollectionActionId ");
        sql.append("UNION");
        sql.append("    SELECT COUNT(1) AS FailureCount, CA.CollectionActionId");
        sql.append("    FROM CollectionAction CA");
        sql.append("    JOIN CollectionActionCommandRequest CACR ON CA.CollectionActionId = CACR.CollectionActionId");
        sql.append("    JOIN CommandRequestExecResult CRER ON CACR.CommandRequestExecId = CRER.CommandRequestExecId");
        sql.append("    WHERE CRER.ErrorCode > 0");
        sql.append("    GROUP BY CA.CollectionActionId ");
        sql.append(")");
        
        sql.append(", NotAttempted AS");
        sql.append("(");
        sql.append("    SELECT COUNT(1) AS NotAttemptedCount, CA.CollectionActionId");
        sql.append("    FROM CollectionAction CA");
        sql.append("    JOIN CollectionActionCommandRequest CACR ON CA.CollectionActionId = CACR.CollectionActionId");
        sql.append("    JOIN CommandRequestUnsupported CRU ON CACR.CommandRequestExecId = CRU.CommandRequestExecId");
        sql.append("    GROUP BY CA.CollectionActionId ");
        sql.append(")");

        if (sortBy == null) {
            sql.append( "SELECT count(ca.CollectionActionId)");
        } else {
            sql.append( "SELECT CA.CollectionActionId, CA.Action, CA.StartTime, CA.StopTime, CA.Status, COALESCE( F.FailureCount, 0 ) AS FailedCount, COALESCE( S.SuccessCount, 0 ) AS SuccessCount, COALESCE( N.NotAttemptedCount, 0 ) AS NotAttemptedCount, CA.UserName");
        }
        sql.append("FROM CollectionAction CA");
        sql.append("LEFT JOIN Successes S ON CA.CollectionActionId = S.CollectionActionId");
        sql.append("LEFT JOIN Failures F ON CA.CollectionActionId = F.CollectionActionId");
        sql.append("LEFT JOIN NotAttempted N ON CA.CollectionActionId = N.CollectionActionId");
        
        if (filter.getActions() == null || filter.getActions().isEmpty()) {
            sql.append("WHERE ca.Action").in_k(Arrays.asList(CollectionAction.values()));
        } else {
            sql.append("WHERE ca.Action").in_k(filter.getActions());
        }
        
        if (filter.getStatuses() != null) {
            if (filter.getStatuses().contains(CommandRequestExecutionStatus.CANCELLED)) {
                filter.getStatuses().add(CommandRequestExecutionStatus.CANCELING);
            }
            sql.append("AND ca.Status").in_k(filter.getStatuses());
            filter.getStatuses().remove(CommandRequestExecutionStatus.CANCELING);
        }
        if (filter.getUserNames() != null && !filter.getUserNames().isEmpty()) {
            sql.append("AND ca.UserName").in(filter.getUserNames());
        }
        DateTime start = new DateTime(filter.getStartDate()).withTimeAtStartOfDay().withZone(userContext.getJodaTimeZone());
        DateTime end = new DateTime(filter.getEndDate()).plusDays(1).withTimeAtStartOfDay().withZone(userContext.getJodaTimeZone());
        Range<Instant> dateRange = Range.inclusive(new Instant(start), new Instant(end));
        Instant startDate = dateRange == null ? null : dateRange.getMin();
        if (startDate != null) {
            if (dateRange.isIncludesMinValue()) {
                sql.append("AND ca.StartTime").gte(startDate);
            } else {
                sql.append("AND ca.StartTime").gt(startDate);
            }
        }
        Instant stopDate = dateRange == null ? null : dateRange.getMax();
        if (stopDate != null) {
            if (dateRange.isIncludesMaxValue()) {
                sql.append("AND ca.StartTime").lte(stopDate);
            } else {
                sql.append("AND ca.StartTime").lt(stopDate);
            }
        }
       
        if (sortBy != null) {
            sql.append("ORDER BY");
            sql.append(sortBy.getDbString());
            sql.append(direction);
        }
        return sql;
    }
    
    @Override
    public CollectionActionResult loadResultFromDb(int key) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CollectionActionId, Action, StartTime, StopTime, Status");
        sql.append("FROM CollectionAction");
        sql.append("WHERE CollectionActionId").eq(key);
        return jdbcTemplate.queryForObject(sql, resultMapper);
    }
    
    @Override
    public List<CollectionActionResult> loadIncompeteResultsFromDb() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CollectionActionId, Action, StartTime, StopTime, Status");
        sql.append("FROM CollectionAction");
        sql.append("WHERE StopTime is NULL");
        sql.append("AND Action").in_k(CollectionAction.getActionsWithCre());
        return jdbcTemplate.query(sql, resultMapper);
    }
    
    @Override
    public void updateDbRequestStatus(int collectionActionId, int deviceId, CommandRequestExecutionStatus newStatus) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.update("CollectionActionRequest");
        sink.addValue("Result", newStatus);
        sql.append("WHERE CollectionActionId").eq(collectionActionId);
        sql.append("AND PAObjectID").eq(deviceId);
        jdbcTemplate.update(sql);
    }
    
    @Override
    public void updateCollectionActionStatus(int collectionActionId, CommandRequestExecutionStatus newStatus,
            Date stopTime) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.update("CollectionAction");
        sink.addValue("Status", newStatus);
        if (stopTime != null) {
            sink.addValue("StopTime", stopTime);
        }
        sql.append("WHERE CollectionActionId").eq(collectionActionId);
        jdbcTemplate.update(sql);
    }
    
    private CollectionActionResult buildDbResult(CollectionAction action, int key) {
        List<YukonPao> failed = new ArrayList<>();
        List<YukonPao> succeeded = new ArrayList<>();
        List<YukonPao> allDevices = new ArrayList<>();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YPO.PAObjectID, YPO.Type, Result");
        sql.append("FROM CollectionActionRequest CAR");
        sql.append("  JOIN YukonPAObject YPO on CAR.PAObjectID = YPO.PAObjectID");
        sql.append("WHERE CAR.CollectionActionId").eq(key);
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                CommandRequestExecutionStatus status = rs.getEnum("Result", CommandRequestExecutionStatus.class);
                PaoIdentifier device = rs.getPaoIdentifier("PAObjectID", "Type");
                if (status == COMPLETE) {
                    succeeded.add(device);
                } else if (status == FAILED) {
                    failed.add(device);
                }
                allDevices.add(device);
            }
        });
        CollectionActionResult result = new CollectionActionResult(action, allDevices, loadInputs(key),
            null, editorDao, tempGroupService, groupHelper, null, logService, YukonUserContext.system);
        //failure bucket
        result.addDevicesToGroup(FAILURE, failed, null);
        //success bucket
        result.addDevicesToGroup(SUCCESS, succeeded, null);
        return result;
    }
    
    private CollectionActionResult buildCreResult(CollectionAction action, int key) {
        CommandRequestExecution exec = commandRequestExecutionDao.getById(getCreId(key));
        Set<PaoIdentifier> allDevices = new HashSet<>(crerDao.getRequestedDeviceIds(exec.getId()));
        Map<CommandRequestUnsupportedType, List<PaoIdentifier>> unsupported =
            action.getCreUnsupportedTypes().stream().collect(Collectors.toMap(type -> type,
                type -> crerDao.getUnsupportedDeviceIdsByExecutionId(exec.getId(), type)));
        unsupported.values().forEach(values -> allDevices.addAll(values));
        CollectionActionResult result = new CollectionActionResult(action, Lists.newArrayList(allDevices),
            loadInputs(key), exec, editorDao, tempGroupService, groupHelper, null, logService, YukonUserContext.system);
        
        List<PaoIdentifier> succeeded = crerDao.getSucessDeviceIdsByExecutionId(exec.getId());
        List<PaoIdentifier> failed = crerDao.getFailDeviceIdsByExecutionId(exec.getId());
        
        if (action == CollectionAction.LOCATE_ROUTE) {
            // If device failed and succeeded, do not show device as failed.
            failed.removeAll(succeeded);
        }
        
        //failure bucket
        result.addDevicesToGroup(FAILURE, failed, null);
        //unsupported buckets
        unsupported.forEach((k, v) -> result.addDevicesToGroup(action.getDetail(k), new ArrayList<>(v), null));
        if (action == CollectionAction.DEMAND_RESET) {
            List<CommandRequestExecution> execs = commandRequestExecutionDao.getCresByContextId(exec.getContextId());
            Optional<CommandRequestExecution> optional = execs.stream().filter(
                e -> e.getCommandRequestExecutionType() == DeviceRequestType.DEMAND_RESET_COMMAND_VERIFY).findFirst();
            if (optional.isPresent()) {
                CommandRequestExecution verifExec = optional.get();
                result.setVerificationExecution(verifExec);
                List<PaoIdentifier> verified = crerDao.getSucessDeviceIdsByExecutionId(verifExec.getId());
                //verified confirmed bucket
                result.addDevicesToGroup(CONFIRMED, verified, null);
                succeeded.removeAll(verified);
                
                List<PaoIdentifier> verifFailed = crerDao.getFailDeviceIdsByExecutionId(verifExec.getId());
                result.addDevicesToGroup(FAILURE, verifFailed, null);
                succeeded.removeAll(verifFailed);
                List<PaoIdentifier> canceled =
                        crerDao.getUnsupportedDeviceIdsByExecutionId(verifExec.getId(), CommandRequestUnsupportedType.CANCELED);
                result.addDevicesToGroup(CANCELED, canceled, null);
                succeeded.removeAll(canceled);
            }
            result.addDevicesToGroup(UNCONFIRMED, succeeded, null);
        } else {
            //success bucket
            result.addDevicesToGroup(CollectionActionDetail.getSuccessDetail(action), succeeded, null);
        }
        return result;
    }
        
    private LinkedHashMap<String, String> loadInputs(int key) {
        LinkedHashMap<String, String> inputs = new LinkedHashMap<>();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Description, Value");
        sql.append("FROM CollectionActionInput");
        sql.append("WHERE CollectionActionId").eq(key);
        sql.append("Order By InputOrder");
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                inputs.put(rs.getString("Description"), rs.getString("Value"));
            }
        });
        return inputs;
    }
    
    private int getCreId(int key) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CommandRequestExecId");
        sql.append("FROM CollectionActionCommandRequest");
        sql.append("WHERE CollectionActionId").eq(key);
        return jdbcTemplate.queryForInt(sql);
    }
        
    @Override
    public Integer findCollectionActionIdFromCreId(int creId) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT CollectionActionId");
            sql.append("FROM CollectionActionCommandRequest");
            sql.append("WHERE CommandRequestExecId").eq(creId);
            return jdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException e) {
            log.debug("No CollectionActionId corresponds to CommandRequestExecutionId", e);
            return null;
        }
    }
    
    private int createAction(CollectionAction action, Instant startTime, CommandRequestExecutionStatus status, LiteYukonUser user) {
        int collectionActionId = nextValueHelper.getNextValue("CollectionAction");
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("CollectionAction");
        sink.addValue("CollectionActionId", collectionActionId);
        sink.addValue("Action", action);
        sink.addValue("StartTime", startTime);
        sink.addValue("Status", status);
        sink.addValue("UserName", user.getUsername());
        jdbcTemplate.update(sql);
        return collectionActionId;
    }
    
    private void saveInputs(int actionId, LinkedHashMap<String, String> inputs) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        int i = 1;
        List<List<Object>> values = new ArrayList<>();
        for (Map.Entry<String, String> entry : inputs.entrySet()) {
            List<Object> row = Lists.newArrayList(actionId, i++, entry.getKey(), entry.getValue());
            values.add(row);
        }
        sql.batchInsertInto("CollectionActionInput").columns("CollectionActionId", "InputOrder", "Description", "Value").values(values);
        jdbcTemplate.yukonBatchUpdate(sql);
    }
    
    @Override
    @Transactional
    public void updateCollectionActionRequest(int cacheKey, int deviceId, CommandRequestExecutionStatus status) {
        SqlStatementBuilder updateSql = new SqlStatementBuilder();
        SqlParameterSink params = updateSql.update("CollectionActionRequest");
        params.addValue("Result", status);
        updateSql.append("WHERE CollectionActionId").eq(cacheKey);
        updateSql.append("AND PAObjectId").eq(deviceId);
        jdbcTemplate.update(updateSql);
    }
    
    @Override
    public List<Integer> getAllOldCollectionActionIds(DateTime retentionDate) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CollectionActionId");
        sql.append("FROM CollectionAction");
        sql.append("WHERE StopTime").lte(retentionDate);
        return jdbcTemplate.query(sql, new YukonRowMapper<Integer>() {
            @Override
            public Integer mapRow(YukonResultSet rs) throws SQLException {
                return rs.getInt("CollectionActionId");
            }
        });
    }
}