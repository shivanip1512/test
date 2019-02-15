package com.cannontech.dr.itron.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.dr.itron.dao.ItronDao;

public class ItronDaoImpl implements ItronDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Override
    public int getGroup(int groupPaoId) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ItronGroupId");
        sql.append("FROM LMGroupItronMapping");
        sql.append("WHERE YukonGroupId").eq(groupPaoId);
        try {
            return jdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException ex){
            throw new NotFoundException("ItronGroupId with YukonGroupId: " + groupPaoId + " was not found.");
        }
    }

    @Override
    public int getProgram(int programPaoId) throws NotFoundException{
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ItronProgramId");
        sql.append("FROM LMProgramItronMapping");
        sql.append("WHERE YukonProgramId").eq(programPaoId);
        try {
            return jdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException ex){
            throw new NotFoundException("ItronProgramId with YukonProgramId: " + programPaoId + " was not found.");
        }    }

    @Override
    public void addProgram(long itronId, int programPaoId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO LMProgramItronMapping");
        sql.values(programPaoId, itronId);
        jdbcTemplate.update(sql);
    }

    @Override
    public void addGroup(long itronId, int groupPaoId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO LMGroupItronMapping");
        sql.values(groupPaoId, itronId);
        jdbcTemplate.update(sql);
    }

    @Override
    public Map<Integer, Long> getItronProgramIds(Collection<Integer> programPaoIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YukonProgramId, ItronProgramId");
        sql.append("FROM LMProgramItronMapping");
        sql.append("WHERE YukonProgramId").in(programPaoIds);
        
        Map<Integer, Long> result = new HashMap<>();
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                result.put(rs.getInt("YukonProgramId"), rs.getLong("ItronProgramId"));
            }
        });
        return result;
    }
    
    @Override
    public Map<Integer, Long> getItronGroupIds(Collection<Integer> groupPaoIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YukonGroupId, ItronGroupId");
        sql.append("FROM LMGroupItronMapping");
        sql.append("WHERE YukonGroupId").in(groupPaoIds);
        Map<Integer, Long> result = new HashMap<>();
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                result.put(rs.getInt("YukonGroupId"), rs.getLong("ItronGroupId"));
            }
        });
        return result;
    }
}
