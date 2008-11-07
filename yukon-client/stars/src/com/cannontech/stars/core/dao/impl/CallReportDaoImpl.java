package com.cannontech.stars.core.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.stars.core.dao.CallReportDao;
import com.cannontech.stars.core.dao.ECMappingDao;

public class CallReportDaoImpl implements CallReportDao {

    private SimpleJdbcTemplate simpleJdbcTemplate;
    private ECMappingDao ecMappingDao;
    
    @Override
    @Transactional
    public void deleteByAccount(int accountId) {
        List<Integer> callReportIds = getByAccount(accountId);
        ecMappingDao.deleteECToCallReportMapping(callReportIds);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM CallReportBase WHERE CallId IN (", callReportIds, ")");
        simpleJdbcTemplate.update(sql.toString());
    }
    
    @Override
    public List<Integer> getByAccount(int accountId){
        String sql = "SELECT CallId FROM CallReportBase WHERE AccountId = ?";
        List<Integer> workOrderIds = simpleJdbcTemplate.query(sql, new IntegerRowMapper(), accountId);
        return workOrderIds;
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    @Autowired
    public void setECMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }

}
