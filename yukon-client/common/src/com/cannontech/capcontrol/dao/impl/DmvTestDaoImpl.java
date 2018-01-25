package com.cannontech.capcontrol.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.capcontrol.dao.DmvTestDao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.db.capcontrol.DmvTest;
import com.cannontech.database.incrementer.NextValueHelper;

public class DmvTestDaoImpl implements DmvTestDao {
    private final RowMapper<DmvTest> rowMapper = new dmvTestRowMapper();
    
    private class dmvTestRowMapper implements RowMapper<DmvTest> {

        @Override
        public DmvTest mapRow(ResultSet rs, int rowNum) throws SQLException {
            DmvTest dmvTest = new DmvTest();
            dmvTest.setName(rs.getString("DmvTestName"));
            dmvTest.setDmvTestId(rs.getInt("DmvTestId"));
            dmvTest.setPollingInterval(rs.getInt("PollingInterval"));
            dmvTest.setDataGatheringDuration(rs.getInt("DataGatheringDuration"));
            dmvTest.setStepSize(rs.getDouble("StepSize"));
            dmvTest.setCommSuccPercentage(rs.getInt("CommSuccessPercentage"));
            return dmvTest;
        }

    }
    
    
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    
    @Override
    public int createDmvTest(DmvTest dmvTest) throws DuplicateException {
        
        if (!isUniqueDmvTestName(dmvTest.getName())) {
            throw new DuplicateException("Demand Verification and Mangement Test with name" + dmvTest.getName() + " already exist.");
        }
        
        int newId = dmvTest.getDmvTestId();
        if (newId == 0) {
            newId = nextValueHelper.getNextValue("DmvTest");
        }
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink parameterSink = sql.insertInto("DmvTest");
        parameterSink.addValue("DmvTestId", newId);
        parameterSink.addValue("DmvTestName", dmvTest.getName());
        parameterSink.addValue("PollingInterval", dmvTest.getPollingInterval());
        parameterSink.addValue("DataGatheringDuration", dmvTest.getDataGatheringDuration());
        parameterSink.addValue("StepSize", dmvTest.getStepSize());
        parameterSink.addValue("CommSuccessPercentage", dmvTest.getCommSuccPercentage());
        jdbcTemplate.update(sql);
        return newId;
    }
    
    @Override
    public List<DmvTest> getAllDmvTest() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DmvTestId, DmvTestName, PollingInterval, DataGatheringDuration, StepSize, CommSuccessPercentage");
        sql.append("FROM DmvTest");
        sql.append("ORDER By DmvTestName");
        
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public DmvTest getDmvTestById(int id) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DmvTestId, DmvTestName, PollingInterval, DataGatheringDuration, StepSize, CommSuccessPercentage");
        sql.append("FROM DmvTest");
        sql.append("WHERE DmvTestId").eq(id);
        return jdbcTemplate.queryForObject(sql, rowMapper);
    }
    
    @Override
    public boolean isUniqueDmvTestName(String name) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM DmvTest");
        sql.append("WHERE DmvTestName").eq(name);

        int duplicateNames =  jdbcTemplate.queryForInt(sql);
        return duplicateNames != 0;
    }
    
    
}
