package com.cannontech.stars.dr.hardware.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.event.dao.LMCustomerEventBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMThermostatDao;

public class LMThermostatDaoImpl implements LMThermostatDao {
    
    SimpleJdbcTemplate simpleJdbcTemplate;
    private ECMappingDao ecMappingDao;
    private LMCustomerEventBaseDao lmCustomerEventBaseDao;
    
    @Override
    @Transactional
    public void deleteSchedulesForInventory(Integer inventoryId) {
        String sql = "SELECT ScheduleId FROM LMThermostatSchedule WHERE InventoryId = ?";
        
        Integer scheduleId = simpleJdbcTemplate.queryForInt(sql, inventoryId);
        
        List<Integer> seasonsOfSchedule = getSeasonsForSchedule(scheduleId);
        deleteSeasons(seasonsOfSchedule);
        
        String delete = "DELETE FROM LMThermostatSchedule WHERE InventoryId = ?";
        simpleJdbcTemplate.update(delete);
        
    }
    
    @Override
    public List<Integer> getSchedulesForAccount(Integer accountId){
        String sql = "SELECT ScheduleId FROM LMThermostatSchedule WHERE AccountId = ?";
        List<Integer> scheduleIds = new ArrayList<Integer>();
        scheduleIds = simpleJdbcTemplate.query(sql, new IntegerRowMapper(), accountId);
        
        return scheduleIds;
    }
    
    @Override
    @Transactional
    public void deleteSchedulesForAccount(Integer accountId) {
        String delete = "DELETE FROM LMThermostatSchedule WHERE AccountId = ?";
        List<Integer> scheduleIds = getSchedulesForAccount(accountId);
        for(Integer scheduleId : scheduleIds) {
            List<Integer> seasonIds = getSeasonsForSchedule(scheduleId);
            deleteSeasonEntrys(seasonIds);
            deleteSeasons(seasonIds);
        }
        simpleJdbcTemplate.update(delete, accountId);
    }
    
    @Override
    public void deleteSeasons(List<Integer> seasonIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM LMThermostatSeason WHERE seasonId IN (", seasonIds, ")");
        deleteSeasonEntrys(seasonIds);
        simpleJdbcTemplate.update(sql.toString());
    }
    
    @Override
    public void deleteSeasonEntrys(List<Integer> seasonIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM LMThermostatSeasonEntry WHERE seasonId IN (", seasonIds, ")");
        simpleJdbcTemplate.update(sql.toString());
    }

    @Override
    public List<Integer> getSeasonsForSchedule(Integer scheduleId) {
        List<Integer> seasonIds = new ArrayList<Integer>();
        
        String sql = "SELECT SeasonId FROM LMThermostatSeason WHERE ScheduleId = ?";
        seasonIds = simpleJdbcTemplate.query(sql, new IntegerRowMapper(), scheduleId);
        
        return seasonIds;
    }
    
    @Override
    public List<Integer> getAllManualEvents(Integer inventoryId) {
        List<Integer> eventIds = new ArrayList<Integer>();
        
        String sql = "SELECT EventId FROM LMThermostatManualEvent WHERE InventoryId = ? ORDER BY EventID";
        eventIds = simpleJdbcTemplate.query(sql, new IntegerRowMapper(), inventoryId);
        
        return eventIds;
    }
    
    @Override
    @Transactional
    public void deleteManualEvents(Integer inventoryId) {
        List<Integer> eventIds = getAllManualEvents(inventoryId);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM LMThermostatManualEvent WHERE EventId IN (", eventIds, ")");
        simpleJdbcTemplate.update(sql.toString());
        ecMappingDao.deleteECToCustomerEventMapping(eventIds);
        lmCustomerEventBaseDao.deleteCustomerEvents(eventIds);
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
