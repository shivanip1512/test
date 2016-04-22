package com.cannontech.stars.dr.event.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.event.dao.LMCustomerEventBaseDao;
import com.cannontech.stars.dr.event.dao.LMProgramEventDao;

public class LMProgramEventDaoImpl implements LMProgramEventDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private LMCustomerEventBaseDao lmCustomerEventBaseDao;
    private ChunkingSqlTemplate chunkyJdbcTemplate;
    
    @Override
    @Transactional
    public void deleteProgramEventsForAccount(int accountId) {
        List<Integer> eventIds = getProgramEventIdsForAccount(accountId);
        if(!eventIds.isEmpty()) {
            chunkyJdbcTemplate.update(new LMProgramEventDeleteSqlGenerator(), eventIds);
            ecMappingDao.deleteECToCustomerEventMapping(eventIds);
            lmCustomerEventBaseDao.deleteCustomerEvents(eventIds);
        }
    }
    
    /**
     * Sql generator for deleting events from LMProgramEvent, useful for bulk deleteing
     * with chunking sql template.
     */
    private class LMProgramEventDeleteSqlGenerator implements SqlGenerator<Integer> {

        @Override
        public String generate(List<Integer> eventIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM LMProgramEvent WHERE EventId IN (", eventIds, ")");
            return sql.toString();
        }
    }
    
    @Override
    public List<Integer> getProgramEventIdsForAccount(int accountId){
        List<Integer> eventIds = new ArrayList<Integer>();
        String sql = "SELECT EventId FROM LMProgramEvent WHERE AccountId = ?";
        eventIds = jdbcTemplate.query(sql, new RowMapper<Integer>(){

            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("EventId");
            }}, accountId);
        
        return eventIds;
    }

    @PostConstruct
    public void init() throws Exception {
        chunkyJdbcTemplate= new ChunkingSqlTemplate(jdbcTemplate);
    }
}
