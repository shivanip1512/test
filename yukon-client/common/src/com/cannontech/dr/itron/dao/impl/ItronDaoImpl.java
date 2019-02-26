package com.cannontech.dr.itron.dao.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.dr.itron.dao.ItronDao;

public class ItronDaoImpl implements ItronDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Override
    public int getItronGroupId(int yukonLmGroupId) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ItronGroupId");
        sql.append("FROM LMGroupItronMapping");
        sql.append("WHERE YukonGroupId").eq(yukonLmGroupId);
        try {
            return jdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException ex){
            throw new NotFoundException("ItronGroupId with YukonGroupId: " + yukonLmGroupId + " was not found.");
        }
    }

    @Override
    public int getItronProgramId(int yukonLmProgramId) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ItronProgramId");
        sql.append("FROM LMProgramItronMapping");
        sql.append("WHERE YukonProgramId").eq(yukonLmProgramId);
        try {
            return jdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("ItronProgramId with YukonProgramId: " + yukonLmProgramId + " was not found.");
        }
    }
    
    @Override
    public int getVirtualRelayId(int yukonLmGroupId) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT VirtualRelayId");
        sql.append("FROM LMGroupItronMapping");
        sql.append("WHERE YukonGroupId").eq(yukonLmGroupId);
        try {
            return jdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException ex){
            throw new NotFoundException("VirtualRelayId with YukonGroupId: " + yukonLmGroupId + " was not found.");
        }
    }

    @Override
    public void addProgramMapping(long itronProgramId, int yukonLmProgramId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink params = sql.insertInto("LMProgramItronMapping");
        params.addValue("YukonProgramId", yukonLmProgramId);
        params.addValue("ItronProgramId", itronProgramId);
        jdbcTemplate.update(sql);
    }

    @Override
    public void addGroupMapping(int yukonLmGroupId, int virtualRelayId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink params = sql.insertInto("LMGroupItronMapping");
        params.addValue("YukonGroupId", yukonLmGroupId);
        params.addValue("VirtualRelayId", virtualRelayId);
        jdbcTemplate.update(sql);
    }
    
    @Override
    public void updateGroupMapping(int yukonLmGroupId, long itronGroupId) {
        SqlStatementBuilder updateSql = new SqlStatementBuilder();
        SqlParameterSink params = updateSql.update("LMGroupItronMapping");
        params.addValue("ItronGroupId", itronGroupId);
        updateSql.append("WHERE YukonGroupId").eq(yukonLmGroupId);
        jdbcTemplate.update(updateSql);
    }
    
    @Override
    public Map<Integer, Long> getItronProgramIds(Collection<Integer> lmProgramIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YukonProgramId, ItronProgramId");
        sql.append("FROM LMProgramItronMapping");
        sql.append("WHERE YukonProgramId").in(lmProgramIds);
        
        Map<Integer, Long> result = new HashMap<>();
        jdbcTemplate.query(sql, (YukonResultSet rs) -> {
            result.put(rs.getInt("YukonProgramId"), rs.getLong("ItronProgramId"));
        });
        return result;
    }
    
    @Override
    public Map<Integer, Long> getItronGroupIds(Collection<Integer> lmGroupIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YukonGroupId, ItronGroupId");
        sql.append("FROM LMGroupItronMapping");
        sql.append("WHERE YukonGroupId").in(lmGroupIds);
        Map<Integer, Long> result = new HashMap<>();
        jdbcTemplate.query(sql, (YukonResultSet rs) -> {
            result.put(rs.getInt("YukonGroupId"), rs.getLong("ItronGroupId"));
        });
        return result;
    }
    
    @Override
    public List<Integer> getLmGroupsWithoutItronGroup(Collection<Integer> lmGroupIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YukonGroupID");
        sql.append("FROM LMGroupItronMapping");
        sql.append("WHERE YukonGroupId").in(lmGroupIds);
        sql.append("AND ItronGroupId IS NULL");
        return jdbcTemplate.query(sql, (YukonResultSet rs) -> {
            return rs.getInt("YukonGroupId");
        });
    }
}
