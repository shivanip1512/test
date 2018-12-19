package com.cannontech.dr.nest.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.nest.dao.NestDao;
import com.cannontech.dr.nest.model.NestControlEvent;
import com.cannontech.dr.nest.model.NestSync;
import com.cannontech.dr.nest.model.NestSyncDetail;
import com.cannontech.dr.nest.model.NestSyncI18nKey;
import com.cannontech.dr.nest.model.NestSyncI18nValue;
import com.cannontech.dr.nest.model.NestSyncType;
import com.cannontech.dr.nest.model.v3.LoadShapingOptions;
import com.cannontech.dr.nest.model.v3.PeakLoadShape;
import com.cannontech.dr.nest.model.v3.PostLoadShape;
import com.cannontech.dr.nest.model.v3.PrepLoadShape;
import com.google.common.collect.Lists;

public class NestDaoImpl implements NestDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    
    private YukonRowMapper<NestSyncDetail> nestSyncDetailRowMapper = new YukonRowMapper<NestSyncDetail>() {
        @Override
        public NestSyncDetail mapRow(YukonResultSet rs) throws SQLException {
            NestSyncDetail row = new NestSyncDetail();
            row.setId(rs.getInt("SyncDetailId"));
            row.setSyncId(rs.getInt("SyncId"));
            row.setType(rs.getEnum("SyncType", NestSyncType.class));
            row.setReasonKey(rs.getEnum("SyncReasonKey", NestSyncI18nKey.class));
            row.setActionKey(rs.getEnum("SyncActionKey", NestSyncI18nKey.class));
            return row;
        }
    };
    
    private YukonRowMapper<NestSync> nestSyncRowMapper = new YukonRowMapper<NestSync>() {
        @Override
        public NestSync mapRow(YukonResultSet rs) throws SQLException {
            NestSync row = new NestSync();
            row.setId(rs.getInt("SyncId"));
            row.setStartTime(new Instant(rs.getDate("SyncStartTime")));
            row.setStopTime(new Instant(rs.getDate("SyncStopTime")));
            return row;
        }
    };
    
    @Override
    public void saveSyncDetails(List<NestSyncDetail> details) {
        Map<Integer, Map<NestSyncI18nValue, String>> allValues = new HashMap<>();
        if(!details.isEmpty()) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            List<List<Object>> values = details.stream().map(detail -> {
                int pk = nextValueHelper.getNextValue("NestSyncDetail");
                allValues.put(pk, detail.getValues());
                List<Object> row = Lists.newArrayList(pk, detail.getSyncId(), detail.getType(), detail.getReasonKey(),
                    detail.getActionKey());
                return row;
            }).collect(Collectors.toList());
    
            sql.batchInsertInto("NestSyncDetail").columns("SyncDetailId", "SyncId", "SyncType", "SyncReasonKey",
                "SyncActionKey").values(values);
            jdbcTemplate.yukonBatchUpdate(sql);
            saveSyncValues(allValues);
        }
    }
    
    private void saveSyncValues(Map<Integer, Map<NestSyncI18nValue, String>> valueMap) {
        if (!valueMap.isEmpty()) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            List<List<Object>> values = new ArrayList<>();
            valueMap.forEach((detailPk, actionValues) -> {
                valueMap.get(detailPk).forEach((valueKey, value) -> {
                    values.add(Lists.newArrayList(nextValueHelper.getNextValue("NestSyncValue"), detailPk, valueKey, value));
                });
            });
            sql.batchInsertInto("NestSyncValue").columns("SyncValueId", "SyncDetailId", "SyncValueType", "SyncValue").values(
                values);
            jdbcTemplate.yukonBatchUpdate(sql);
        }
    }

    @Override
    public SearchResults<NestSyncDetail> getNestSyncDetail(int syncId, PagingParameters paging, SortBy sortBy, Direction direction, List<NestSyncType> syncTypes) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        sql.append("SELECT SyncDetailId, SyncId, SyncType, SyncReasonKey, SyncActionKey");
        sql.append("FROM NestSyncDetail");
        sql.append("WHERE SyncId").eq(syncId);
        sql.append("AND SyncType").in(syncTypes);
        sql.append("ORDER BY").append(sortBy).append(direction);
        
        PagingResultSetExtractor<NestSyncDetail> rse = new PagingResultSetExtractor<>(start, count, nestSyncDetailRowMapper);
        jdbcTemplate.query(sql, rse);
        
        Map<Integer, NestSyncDetail> details = rse.getResultList().stream()
                .collect(Collectors.toMap(detail -> detail.getId(), detail -> detail));
     
        addSyncValues(details);

        SearchResults<NestSyncDetail> searchResults = new SearchResults<>();
        searchResults.setBounds(start, count, getNestSyncDetailCount(syncId, syncTypes));
        searchResults.setResultList(rse.getResultList());
        
        return searchResults;
    }

    @Override
    public int getNestSyncDetailCount(int syncId, List<NestSyncType> syncTypes) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(*)");
        sql.append("FROM NestSyncDetail");
        sql.append("WHERE SyncId").eq(syncId);
        sql.append("AND SyncType").in(syncTypes);
        return jdbcTemplate.queryForInt(sql);

    }
    
    private void addSyncValues(Map<Integer, NestSyncDetail> details) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT SyncDetailId, SyncValueType, SyncValue");
        sql.append("FROM NestSyncValue");
        sql.append("WHERE SyncDetailId").in(details.keySet());
        jdbcTemplate.query(sql, (YukonResultSet rs) -> {
            NestSyncDetail detail = details.get(rs.getInt("SyncDetailId"));
            detail.addValue(rs.getEnum("SyncValueType", NestSyncI18nValue.class), rs.getString("SyncValue"));
        });
    }
    
    @Override
    public List<NestSync> getNestSyncs() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT SyncId, SyncStartTime, SyncStopTime");
        sql.append("FROM NestSync");
        sql.append("ORDER BY SyncId Desc");
        return jdbcTemplate.query(sql, nestSyncRowMapper);
    }
    
    @Override
    public NestSync getNestSyncById(int syncId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT SyncId, SyncStartTime, SyncStopTime");
        sql.append("FROM NestSync");
        sql.append("WHERE SyncId").eq(syncId);
        return jdbcTemplate.queryForObject(sql, nestSyncRowMapper);
    }
    
    @Override
    public void saveSyncInfo(NestSync sync) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT SyncId");
        sql.append("FROM NestSync");
        sql.append("WHERE SyncId").eq(sync.getId());

        SqlStatementBuilder updateCreateSql = new SqlStatementBuilder();
        try {
            jdbcTemplate.queryForInt(sql);
            SqlParameterSink params = updateCreateSql.update("NestSync");
            params.addValue("SyncStopTime", sync.getStopTime());
            updateCreateSql.append("WHERE SyncId").eq(sync.getId());
        } catch (EmptyResultDataAccessException e) {
            SqlParameterSink params = updateCreateSql.insertInto("NestSync");
            sync.setId(nextValueHelper.getNextValue("NestSync"));
            params.addValue("SyncStartTime", sync.getStartTime());
            params.addValue("SyncId", sync.getId());
        }
        jdbcTemplate.update(updateCreateSql);
    }
    
    @Override
    public void saveControlEvent(NestControlEvent event) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT NestControlEventId");
        sql.append("FROM LMNestControlEvent");
        sql.append("WHERE NestControlEventId").eq(event.getId());

        SqlStatementBuilder updateCreateSql = new SqlStatementBuilder();
        try {
            jdbcTemplate.queryForInt(sql);
            SqlParameterSink params = updateCreateSql.update("LMNestControlEvent");
            params.addValue("CancelRequestTime", event.getCancelRequestTime());
            params.addValue("CancelResponse", event.getCancelResponse());
            params.addValue("CancelOrStop", event.getCancelOrStop());
            params.addValue("Success", event.getSuccess());
            updateCreateSql.append("WHERE NestControlEventId").eq(event.getId());
        } catch (EmptyResultDataAccessException e) {
            SqlParameterSink params = updateCreateSql.insertInto("LMNestControlEvent");
            event.setId(nextValueHelper.getNextValue("LMNestControlEvent"));
            params.addValue("NestControlEventId", event.getId());
            params.addValue("NestGroup", event.getGroup());
            params.addValue("NestKey", event.getKey());
            params.addValue("StartTime", event.getStartTime());
            params.addValue("StopTime", event.getStopTime());
        }
        jdbcTemplate.update(updateCreateSql);
    }
    
    @Override
    public NestControlEvent getCancelableEvent(String group) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT NestControlEventId, NestGroup, NestKey, StartTime, StopTime, CancelOrStop, Success, CancelResponse, CancelRequestTime");
        sql.append("FROM LMNestControlEvent");
        sql.append("WHERE NestGroup").eq(group);
        //still scheduled or running
        sql.append("AND StopTime").gt(Instant.now());
        //cancellation didn't succeed or was not attempted
        sql.append("AND (Success IS NULL OR Success").eq_k(YNBoolean.NO).append(")");
        sql.append("AND NestControlEventId = (SELECT MAX(NestControlEventId) FROM LMNestControlEvent");
        sql.append("        WHERE NestGroup").eq(group);
        sql.append("        AND StopTime").gt(Instant.now());
        sql.append("        AND (Success IS NULL OR Success").eq_k(YNBoolean.NO);
        sql.append("        ))");
        try {
            return jdbcTemplate.queryForObject(sql, controlEventRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    private YukonRowMapper<NestControlEvent> controlEventRowMapper = new YukonRowMapper<NestControlEvent>() {
        @Override
        public NestControlEvent mapRow(YukonResultSet rs) throws SQLException {
            NestControlEvent row = new NestControlEvent();
            row.setId(rs.getInt("NestControlEventId"));
            row.setGroup(rs.getString("NestGroup"));
            row.setCancelOrStop(rs.getString("CancelOrStop"));
            row.setCancelRequestTime(rs.getInstant("CancelRequestTime"));
            row.setStartTime(rs.getInstant("StartTime"));
            row.setStopTime(rs.getInstant("StopTime"));
            row.setKey(rs.getString("NestKey"));
            row.setCancelResponse(rs.getString("CancelResponse"));
            row.setSuccess(rs.getEnum("Success", YNBoolean.class));
            return row;
        }
    };
    
    
    @Override
    public LoadShapingOptions findNestLoadShapingOptions(int gearId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PreparationOption, PeakOption, PostPeakOption");
        sql.append("FROM LMNestLoadShapingGear nestGear JOIN LMProgramDirectGear gear ON nestGear.GearId = gear.GearId");
        sql.append("WHERE nestGear.GearId").eq(gearId);
        sql.append("AND ControlMethod").eq_k(GearControlMethod.NestStandardCycle);

        try {
            return jdbcTemplate.queryForObject(sql, new YukonRowMapper<LoadShapingOptions>() {
                @Override
                public LoadShapingOptions mapRow(YukonResultSet rs) throws SQLException {
                    
                    PrepLoadShape prepLoadShape = rs.getEnum("PreparationOption", PrepLoadShape.class);
                    PeakLoadShape peakLoadShape = rs.getEnum("PeakOption", PeakLoadShape.class);
                    PostLoadShape postLoadShape = rs.getEnum("PostPeakOption", PostLoadShape.class);
                    
                    return new LoadShapingOptions(prepLoadShape, peakLoadShape, postLoadShape);
                }
            });
        } catch (IncorrectResultSizeDataAccessException e) {
            // gearId is NOT Nest Standard Cycle Gear
            return null;
        }
    }

}
