package com.cannontech.common.bulk.collection.device.dao.impl;

import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.FAILURE;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.SUCCESS;
import static com.cannontech.common.device.commands.CommandRequestExecutionStatus.COMPLETE;
import static com.cannontech.common.device.commands.CommandRequestExecutionStatus.FAILED;
import static com.cannontech.common.device.commands.CommandRequestExecutionStatus.STARTED;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.dao.CollectionActionDao;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionFilter;
import com.cannontech.common.bulk.collection.device.model.CollectionActionFilteredResult;
import com.cannontech.common.bulk.collection.device.model.CollectionActionProcess;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
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
import com.google.common.collect.Lists;

public class CollectionActionDaoImpl implements CollectionActionDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandRequestExecutionResultDao crerDao;
    @Autowired private TemporaryDeviceGroupService tempGroupService;
    @Autowired private DeviceGroupMemberEditorDao editorDao;
    @Autowired private DeviceGroupCollectionHelper groupHelper;
    
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
        saveInputs(caId, result.getInputs().getInputs());
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
            CollectionActionFilter filter, PagingParameters paging, SortBy sortBy, Direction direction) {

        SqlStatementBuilder allRowsSql = buildResultSelect(filter, sortBy, direction);
        SqlStatementBuilder countSql = buildResultSelect(filter, null, null);

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
    private SqlStatementBuilder buildResultSelect(CollectionActionFilter filter, SortBy sortBy, Direction direction) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("WITH Counts AS(");
        sql.append("SELECT CollectionActionId,");
        sql.append("    SUM(case when Result = '" + FAILED + "' then 1 else 0 end) FailedCount,");
        sql.append("    SUM(case when Result = '" + COMPLETE + "' then 1 else 0 end) SuccessCount,");
        sql.append("    0 as NotAttemptedCount");
        sql.append("FROM CollectionActionRequest");
        sql.append("GROUP BY CollectionActionId");
        sql.append(")");

        if (sortBy == null) {
            sql.append( "SELECT count(ca.CollectionActionId)");
        } else {
            sql.append( "SELECT ca.CollectionActionId, ca.Action, ca.StartTime, ca.StopTime, ca.Status, c.FailedCount, c.SuccessCount, c.NotAttemptedCount");
        }
        sql.append("FROM CollectionAction ca");
        sql.append("JOIN Counts AS c ON ca.CollectionActionId = c.CollectionActionId");
        sql.append("WHERE ca.Action").in_k(filter.getActions());
        if(filter.getStatuses().contains(CommandRequestExecutionStatus.CANCELLED)) {
            filter.getStatuses().add(CommandRequestExecutionStatus.CANCELING);
        }
        sql.append("AND ca.Status").in_k(filter.getStatuses());
        
        Range<Instant> dateRange = filter.getRange();
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
        return jdbcTemplate.queryForObject(sql, new YukonRowMapper<CollectionActionResult>() {
            @Override
            public CollectionActionResult mapRow(YukonResultSet rs) throws SQLException {
                CollectionActionResult result = null;
                CollectionAction action = rs.getEnum("Action", CollectionAction.class);
                if (action.getProcess() == CollectionActionProcess.CRE) {
                    if(true) {
                        throw new RuntimeException("Loading from CRE tables is not implemented");
                    }
                    result = buildCreResult(action, key);
                } else if (action.getProcess() == CollectionActionProcess.DB) {
                    result = buildDbResult(action, key);
                }
                result.setCacheKey(key);
                result.setStatus(rs.getEnum("Status", CommandRequestExecutionStatus.class));
                result.setStartTime(rs.getInstant("StartTime"));
                result.setStopTime(rs.getInstant("StopTime"));
                return result;      
            }
        });
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
            Instant stopTime) {
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
        Set<YukonPao> failed = new HashSet<>();
        Set<YukonPao> succeeded = new HashSet<>();
        Set<YukonPao> allDevices = new HashSet<>();
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
            null, editorDao, tempGroupService, groupHelper);
        //failure bucket
        result.addDevicesToGroup(FAILURE, failed);
        //success bucket
        result.addDevicesToGroup(SUCCESS, succeeded);
        return result;
    }
    
    private CollectionActionResult buildCreResult(CollectionAction action, int key) {
        CommandRequestExecution exec = commandRequestExecutionDao.getById(getCreId(key));
        Set<YukonPao> allDevices = new HashSet<>(crerDao.getRequestedDeviceIds(exec.getId()));
        CollectionActionResult result = new CollectionActionResult(action, allDevices, loadInputs(key),
            exec, editorDao, tempGroupService, groupHelper);
        
        Set<YukonPao> failed = new HashSet<>(crerDao.getFailDeviceIdsByExecutionId(exec.getId()));
        Set<YukonPao> succeeded = new HashSet<>(crerDao.getSucessDeviceIdsByExecutionId(exec.getId()));
        
        Map<CommandRequestUnsupportedType, List<PaoIdentifier>> unsupported = action.getCreUnsupportedTypes()
                .stream().collect(Collectors.toMap(type -> type, type -> crerDao.getUnsupportedDeviceIdsByExecutionId(exec.getId(), type)));
        //failure bucket
        result.addDevicesToGroup(FAILURE, failed);
        //unsupported buckets
        unsupported.forEach((k, v) -> result.addDevicesToGroup(action.getDetail(k), new HashSet<>(v)));
        //success bucket
        if (action == CollectionAction.DEMAND_RESET) {
            throw new RuntimeException("Not implemented");
        } else {
            result.addDevicesToGroup(action.getSuccessDetail(), succeeded);
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
        List<List<Object>> values = inputs.entrySet().stream().map(e -> {
            int i = 1;
            List<Object> row = Lists.newArrayList(actionId, i++, e.getKey(), e.getValue());
            return row;
        }).collect(Collectors.toList());

        sql.batchInsertInto("CollectionActionInput").columns("CollectionActionId", "InputOrder", "Description", "Value").values(values);
        jdbcTemplate.yukonBatchUpdate(sql);
    }
}
