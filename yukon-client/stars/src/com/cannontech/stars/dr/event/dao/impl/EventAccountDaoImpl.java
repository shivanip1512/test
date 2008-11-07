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
import com.cannontech.stars.dr.event.dao.EventAccountDao;

public class EventAccountDaoImpl implements EventAccountDao {

    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    @Override
    public List<Integer> getAllEventsForAccount(Integer accountId) {
        String sql = "SELECT EventId FROM EventAccount WHERE AccountId = ?";
        List<Integer> eventIds = new ArrayList<Integer>();
        eventIds = simpleJdbcTemplate.query(sql, new ParameterizedRowMapper<Integer>(){

            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("EventId");
            }
        }, accountId);
        
        return eventIds;
    }
    
    @Override
    @Transactional
    public void deleteAllEventsForAccount(Integer accountId) {
        List<Integer> eventIds = getAllEventsForAccount(accountId);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM EventAccount WHERE EventId IN (", eventIds, ")");
        simpleJdbcTemplate.update(sql.toString());
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

}
