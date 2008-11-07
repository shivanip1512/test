package com.cannontech.stars.dr.event.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.event.dao.LMCustomerEventBaseDao;
import com.cannontech.stars.dr.event.dao.LMProgramEventDao;

public class LMProgramEventDaoImpl implements LMProgramEventDao {

    SimpleJdbcTemplate simpleJdbcTemplate;
    ECMappingDao ecMappingDao;
    LMCustomerEventBaseDao lmCustomerEventBaseDao;
    
    @Override
    @Transactional
    public void deleteProgramEventsForAccount(int accountId) {
        List<Integer> eventIds = getProgramEventIdsForAccount(accountId);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM LMProgramEvent WHERE EventId IN (", eventIds, ")");
        simpleJdbcTemplate.update(sql.toString());
        ecMappingDao.deleteECToCustomerEventMapping(eventIds);
        lmCustomerEventBaseDao.deleteCustomerEvents(eventIds);
    }
    
    @Override
    public List<Integer> getProgramEventIdsForAccount(int accountId){
        List<Integer> eventIds = new ArrayList<Integer>();
        String sql = "SELECT EventId FROM LMProgramEvent WHERE AccountId = ?";
        eventIds = simpleJdbcTemplate.query(sql, new ParameterizedRowMapper<Integer>(){

            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("EventId");
            }}, accountId);
        
        return eventIds;
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    @Autowired
    public void setECMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
    @Autowired
    public void setLMCustomerEventBaseDao(LMCustomerEventBaseDao lmCustomerEventBaseDao) {
        this.lmCustomerEventBaseDao = lmCustomerEventBaseDao;
    }

}
