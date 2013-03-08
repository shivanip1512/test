package com.cannontech.openadr.dao.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.openadr.dao.OpenAdrEventDao;
import com.cannontech.openadr.model.OffsetEvent;

public class OpenAdrEventDaoImpl implements OpenAdrEventDao {
    private static final Logger log = YukonLogManager.getLogger(OpenAdrEventDaoImpl.class);

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    private static final YukonRowMapper<OffsetEvent> mapper = new YukonRowMapper<OffsetEvent>() {
        @Override
        public OffsetEvent mapRow(YukonResultSet rs) throws SQLException {
            String eventXml = rs.getString("EventXml");
            int startOffset = rs.getInt("StartOffset");
            
            return new OffsetEvent(eventXml, startOffset);
        }
    };
    
    @Override
    public void insertEventAndPurgeExpired(String eventId, String eventXml, Date endDate, String requestId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink params = sql.insertInto("OpenAdrEvents");
        
        String hashedEventId;
        String hashedRequestId;
        try {
            hashedEventId = getHashedString(eventId);
            hashedRequestId = getHashedString(requestId);
        } catch (NoSuchAlgorithmException | RuntimeException e) {
            log.error("Error occurred hashing while hashing for event " + eventId + ". Unable to write the event." , e);
            return;
        }
        
        params.addValue("EventId", hashedEventId);
        params.addValue("EventXml", eventXml);
        params.addValue("EndDate", endDate);
        params.addValue("RequestId", hashedRequestId);
        
        jdbcTemplate.update(sql);
        
        int expiredEvents = purgeExpiredEvents();
        
        if (expiredEvents > 0) {
            log.debug("Purged " + expiredEvents + " expired OpenAdrEvents");
        }
    }
    
    /**
     * Returns an SHA-256 hashed representation of a string
     * @param toHash the string being hashed
     * @return a non-null, non-empty SHA-256 hashed representation of a string.
     * @throws NoSuchAlgorithmException if the hashing algorithm isn't valid
     * @throws RuntimeException if the hashed string is null or empty.
     */
    private String getHashedString(String toHash) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(toHash.getBytes());
        String hashedEventId = new String(messageDigest.digest());
        
        if (StringUtils.isBlank(hashedEventId)) {
            throw new RuntimeException("Hashed string was either null or empty!");
        }
        
        return hashedEventId;
    }
    
    @Override
    public void updateEventAndPurgeExpired(String eventId, String eventXml, Date endDate, String requestId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink params = sql.update("OpenAdrEvents");
        
        String hashedEventId;
        String hashedRequestId;
        try {
            hashedEventId = getHashedString(eventId);
            hashedRequestId = getHashedString(requestId);
        } catch (NoSuchAlgorithmException | RuntimeException e) {
            log.error("Error occurred hashing event id " + eventId + ". Unable to update the event." , e);
            return;
        }
        
        params.addValue("EventXml", eventXml);
        params.addValue("EndDate", endDate);
        params.addValue("RequestId", hashedRequestId);
        sql.append("WHERE EventId").eq(hashedEventId);
        
        int rowsAffected = jdbcTemplate.update(sql);
        
        if (rowsAffected == 0) {
            log.debug("No rows were changed in the attempt to update event with ID " + eventId);
        }
    }
    
    @Override
    public void deleteEvent(String eventId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM OpenAdrEvents");
        
        String hashedEventId;
        try {
            hashedEventId = getHashedString(eventId);
        } catch (NoSuchAlgorithmException | RuntimeException e) {
            log.error("Error occurred hashing event id " + eventId + ". Unable to delete the event." , e);
            return;
        }
        
        sql.append("WHERE EventId").eq(hashedEventId);
        
        int rowsAffected = jdbcTemplate.update(sql);
        
        if (rowsAffected == 0) {
            log.debug("No rows were deleted in the attempt to cancel event with ID " + eventId);
        }
    }
    
    private int purgeExpiredEvents() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM OpenAdrEvents");
        sql.append("WHERE EndDate").lt(new Instant());
        
        int rowsAffected = jdbcTemplate.update(sql);
        
        return rowsAffected;
    }

    @Override
    public OffsetEvent retrieveOffsetEvent(String eventId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EventXml, StartOffset");
        sql.append("FROM OpenAdrEvents");
        
        String hashedEventId;
        try {
            hashedEventId = getHashedString(eventId);
        } catch (NoSuchAlgorithmException | RuntimeException e) {
            log.error("Error occurred hashing event id " + eventId + ". Unable to delete the event." , e);
            return null;
        }
        
        sql.append("WHERE EventId").eq(hashedEventId);
        
        try {
            return jdbcTemplate.queryForObject(sql, mapper);
        } catch (DataAccessException e) {
            return null;
        }
    }
}