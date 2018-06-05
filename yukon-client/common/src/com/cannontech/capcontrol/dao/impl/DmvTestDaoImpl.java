package com.cannontech.capcontrol.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.DmvTestDao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.db.capcontrol.DmvTest;
import com.cannontech.database.incrementer.NextValueHelper;

public class DmvTestDaoImpl implements DmvTestDao {
    
    private static final YukonRowMapper<DmvTest> rowMapper = (rs) -> {
        DmvTest dmvTest = new DmvTest();
        dmvTest.setName(rs.getString("DmvTestName"));
        dmvTest.setDmvTestId(rs.getInt("DmvTestId"));
        dmvTest.setDataArchivingInterval(rs.getInt("DataArchivingInterval"));
        dmvTest.setIntervalDataGatheringDuration(rs.getInt("IntervalDataGatheringDuration"));
        dmvTest.setStepSize(rs.getDouble("StepSize"));
        dmvTest.setCommSuccPercentage(rs.getInt("CommSuccessPercentage"));
        return dmvTest;
    };
    
    
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    
    @Override
    public int updateDmvTest(DmvTest dmvTest) throws DuplicateException {
        
        if (!isUniqueDmvTestName(dmvTest.getName(), dmvTest.getDmvTestId())) {
            throw new DuplicateException("Demand Verification and Mangement Test with name" + dmvTest.getName() + " already exist.");
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink parameterSink;
        int id = dmvTest.getDmvTestId();
        if (id == 0) {
            parameterSink = sql.insertInto("DmvTest");
            id = nextValueHelper.getNextValue("DmvTest");
            parameterSink.addValue("DmvTestId", id);
            updateParameterSink(parameterSink, dmvTest);
        }
        else {
            parameterSink = sql.update("DmvTest");
            updateParameterSink(parameterSink, dmvTest);
            sql.append("Where DmvTestId").eq(id);
        }
        jdbcTemplate.update(sql);
        return id;
    }
    
    private void updateParameterSink(SqlParameterSink parameterSink, DmvTest dmvTest) {
        parameterSink.addValue("DmvTestName", dmvTest.getName());
        parameterSink.addValue("DataArchivingInterval", dmvTest.getDataArchivingInterval());
        parameterSink.addValue("IntervalDataGatheringDuration", dmvTest.getIntervalDataGatheringDuration());
        parameterSink.addValue("StepSize", dmvTest.getStepSize());
        parameterSink.addValue("CommSuccessPercentage", dmvTest.getCommSuccPercentage());
    }
    
    @Override
    public List<DmvTest> getAllDmvTest() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DmvTestId, DmvTestName, DataArchivingInterval, IntervalDataGatheringDuration, StepSize, CommSuccessPercentage");
        sql.append("FROM DmvTest");
        sql.append("ORDER By DmvTestName");
        
        return jdbcTemplate.query(sql, rowMapper);
    }
    
    @Override
    public List<DmvTest> getDmvTestByTestNames(List<String> testNames) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DmvTestId, DmvTestName, DataArchivingInterval, IntervalDataGatheringDuration, StepSize, CommSuccessPercentage");
        sql.append("FROM DmvTest");
        sql.append("WHERE DmvTestName").in(testNames);
        sql.append("ORDER By DmvTestName");

        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public DmvTest getDmvTestById(int id) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DmvTestId, DmvTestName, DataArchivingInterval, IntervalDataGatheringDuration, StepSize, CommSuccessPercentage");
        sql.append("FROM DmvTest");
        sql.append("WHERE DmvTestId").eq(id);
        return jdbcTemplate.queryForObject(sql, rowMapper);
    }
    
    @Override
    public boolean isUniqueDmvTestName(String name, int id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM DmvTest");
        sql.append("WHERE DmvTestName").eq(name);
        sql.append("AND DMVTestId").neq(id);
        int duplicateNames =  jdbcTemplate.queryForInt(sql);
        return duplicateNames == 0;
    }
    
    @Override
    public boolean delete(int id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM DMVTEST");
        sql.append("WHERE DmvTestId").eq(id);
        int rowsAffected = jdbcTemplate.update(sql);
        return rowsAffected == 1;
    }
    
    
}
