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

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.nest.dao.NestDao;
import com.cannontech.dr.nest.model.NestControlHistory;
import com.cannontech.dr.nest.model.NestSync;
import com.cannontech.dr.nest.model.NestSyncDetail;
import com.cannontech.dr.nest.model.NestSyncI18nKey;
import com.cannontech.dr.nest.model.NestSyncI18nValue;
import com.cannontech.dr.nest.model.NestSyncType;
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
    
    public void saveSyncValues(Map<Integer, Map<NestSyncI18nValue, String>> valueMap) {
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

    private void addSyncValues(Map<Integer, NestSyncDetail> details) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT SyncDetailId, SyncValueType, SyncValue");
        sql.append("FROM NestSyncValue");
        sql.append("WHERE SyncDetailId").in(details.keySet());
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                NestSyncDetail detail = details.get(rs.getInt("SyncDetailId"));
                detail.addValue(rs.getEnum("SyncValueType", NestSyncI18nValue.class), rs.getString("SyncValue"));
            }
        });
    }
    
    private int getNestSyncDetailCount(int syncId, List<NestSyncType> syncTypes) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(*)");
        sql.append("FROM NestSyncDetail");
        sql.append("WHERE SyncId").eq(syncId);
        sql.append("AND SyncType").in(syncTypes);
        return jdbcTemplate.queryForInt(sql);
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
    public void createControlHistory(NestControlHistory history) {
        //http://loutcsjira01.napa.ad.etn.com:8080/browse/YUK-18897
            
        /*CREATE TABLE LMNestControlHistory (

                NestHistoryId               NUMERIC             NOT NULL,
                NestGroup        VARCHAR(20)             NOT NULL,
                NestKey          VARCHAR(20)        NOT NULL,
                StartTime      datetime         NULL,
                StopTime      datetime         NULL,
                CancelRequestTime      datetime         NULL,
                CancelResponse     VARCHAR(200)     NULL

            );*/
    }
    
    @Override
    public void updateCancelRequestTime(int id) {
       // CancelRequestTime = now
    }
    
    @Override
    public void updateNestResponse(int id, String response) {
       // CancelResponse = response
    }
    
    @Override
    public NestControlHistory getRecentHistoryForGroup(String group) {
       /*
        * Select group with the most recent start time
        * Where 
        * Nest Group = group
        * 
        * StopTime > now (is still running)
        * CancelRequestTime != null (was not canceled)
        * 
        */
        return null;
    }
}
