package com.cannontech.openadr.dao.impl;

import java.util.Date;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.openadr.dao.OpenAdrEventDao;


public class OpenAdrEventDaoImpl implements OpenAdrEventDao {
    private static final Logger log = YukonLogManager.getLogger(OpenAdrEventDaoImpl.class);

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Override
    public void insertEventAndPurgeExpired(String eventId, String eventXml, Date endDate) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink params = sql.insertInto("OadrEvents");
        params.addValue("EventId", eventId);
        params.addValue("EventXml", eventXml);
        params.addValue("EndDate", endDate);
        
        jdbcTemplate.update(sql);
        
        int expiredEvents = purgeExpiredEvents();
        
        if (expiredEvents > 0) {
            log.debug("Purged " + expiredEvents + " expired OadrEvents");
        }
    }
    
    @Override
    public void updateEventAndPurgeExpired(String eventId, String eventXml, Date endDate) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink params = sql.update("OadrEvents");
        params.addValue("EventXml", eventXml);
        params.addValue("EndDate", endDate);
        sql.append("WHERE EventId").eq(eventId);
        
        int rowsAffected = jdbcTemplate.update(sql);
        
        if (rowsAffected == 0) {
            log.debug("No rows were changed in the attempt to update event with ID " + eventId);
        }
    }
    
    @Override
    public void deleteEvent(String eventId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM OadrEvents");
        sql.append("WHERE EventId").eq(eventId);
        
        int rowsAffected = jdbcTemplate.update(sql);
        
        if (rowsAffected == 0) {
            log.debug("No rows were deleted in the attempt to cancel event with ID " + eventId);
        }
    }
    
    private int purgeExpiredEvents() {
        DateTime now = new DateTime();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM OadrEvents");
        sql.append("WHERE EndDate < ").appendArgument(now.toDate());
        
        int rowsAffected = jdbcTemplate.update(sql);
        
        return rowsAffected;
    }

    @Override
    public String retrieveEventXml(String eventId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EventXml");
        sql.append("FROM OadrEvents");
        sql.append("WHERE EventId").eq(eventId);
        
        return jdbcTemplate.queryForString(sql);
    }
}