package com.cannontech.dr.itron.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.dr.itron.dao.ItronDao;

public class ItronDaoImpl implements ItronDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Override
    public int getGroup(int groupPaoId) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ItronGroupId");
        sql.append("FROM LMGroupItronMapping");
        sql.append("WHERE YukonGroupId").eq(groupPaoId);
        return jdbcTemplate.queryForInt(sql);
    }

    @Override
    public int getProgram(int programPaoId) throws NotFoundException{
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ItronProgramId");
        sql.append("FROM LMProgramItronMapping");
        sql.append("WHERE YukonProgramId").eq(programPaoId);
        return jdbcTemplate.queryForInt(sql);
    }

    @Override
    public void addProgram(long itronId, int programPaoId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO LMProgramItronMapping");
        sql.values(itronId, programPaoId);
        jdbcTemplate.update(sql);
    }

    @Override
    public void addGroup(long itronId, int groupPaoId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO LMGroupItronMapping");
        sql.values(itronId, groupPaoId);
        jdbcTemplate.update(sql);
    }

    @Override
    public Map<Integer, Long> getItronProgramIds(Collection<Integer> programPaoIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YukonProgramId, ItronProgramId");
        sql.append("FROM LMProgramItronMapping");
        sql.append("WHERE YukonProgramId").in(programPaoIds);
        return jdbcTemplate.queryForObject(sql, new YukonRowMapper<Map<Integer, Long>>() {
            @Override
            public Map<Integer,Long> mapRow(YukonResultSet rs) throws SQLException {
                HashMap<Integer, Long> row = new HashMap<>();
                row.put(rs.getInt("YukonProgramId"), rs.getLong("YukonProgramId"));
                return row;
            }
        });
    }
    
    @Override
    public Map<Integer, Long> getItronGroupIds(Collection<Integer> groupPaoIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YukonGroupId, ItronGroupId");
        sql.append("FROM LMGroupItronMapping");
        sql.append("WHERE YukonGroupId").in(groupPaoIds);
        return jdbcTemplate.queryForObject(sql, new YukonRowMapper<Map<Integer, Long>>() {
            @Override
            public Map<Integer,Long> mapRow(YukonResultSet rs) throws SQLException {
                HashMap<Integer, Long> row = new HashMap<>();
                row.put(rs.getInt("YukonGroupId"), rs.getLong("YukonGroupId"));
                return row;
            }
        });
    }
}
