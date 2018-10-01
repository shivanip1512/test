package com.cannontech.dr.nest.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.nest.dao.NestDao;
import com.cannontech.dr.nest.model.NestControlHistory;
import com.cannontech.dr.nest.model.NestSync;
import com.cannontech.dr.nest.model.NestSyncDetail;
import com.google.common.collect.Lists;

public class NestDaoImpl implements NestDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    
    @Override
    public void saveSyncDetails(List<NestSyncDetail> details) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        List<List<Object>> values = details.stream().map(detail -> {
            List<Object> row =
                Lists.newArrayList(nextValueHelper.getNextValue("NestSyncDetail"), detail.getSyncId(), detail.getType(),
                    detail.getReasonKey(), detail.getReasonValue(), detail.getActionKey(), detail.getActionValue());
            return row;
        }).collect(Collectors.toList());

        sql.batchInsertInto("NestSyncDetail").columns("SyncDetailId", "SyncId", "SyncType", "SyncReasonKey",
            "SyncReasonValue", "SyncActionKey", "SyncActionValue").values(values);
        jdbcTemplate.yukonBatchUpdate(sql);
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
