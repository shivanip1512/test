package com.cannontech.stars.dr.thermostat.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.event.dao.LMCustomerEventBaseDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;

/**
 * Implementation class for ThermostatScheduleDao
 */
public class ThermostatScheduleDaoImpl implements ThermostatScheduleDao, InitializingBean {

    private SimpleJdbcTemplate simpleJdbcTemplate;
    private ChunkingSqlTemplate chunkyJdbcTemplate;
    private ECMappingDao ecMappingDao;
    private LMCustomerEventBaseDao lmCustomerEventBaseDao;

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        chunkyJdbcTemplate= new ChunkingSqlTemplate(simpleJdbcTemplate);
    }

    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
    @Autowired
    public void setLMCustomerEventBaseDao(LMCustomerEventBaseDao lmCustomerEventBaseDao) {
        this.lmCustomerEventBaseDao = lmCustomerEventBaseDao;
    }

    @Override
    public List<Integer> getAllManualEventIds(Integer inventoryId) {
        List<Integer> eventIds = new ArrayList<Integer>();
        
        String sql = "SELECT EventId FROM LMThermostatManualEvent WHERE InventoryId = ? ORDER BY EventID";
        eventIds = simpleJdbcTemplate.query(sql, new IntegerRowMapper(), inventoryId);
        
        return eventIds;
    }
    
    @Override
    @Transactional
    public void deleteManualEvents(Integer inventoryId) {
        List<Integer> eventIds = getAllManualEventIds(inventoryId);
        if(!eventIds.isEmpty()) {
            chunkyJdbcTemplate.update(new LMThermostatManualEventDeleteSqlGenerator(), eventIds);
            ecMappingDao.deleteECToCustomerEventMapping(eventIds);
            lmCustomerEventBaseDao.deleteCustomerEvents(eventIds);
        }
    }
    
    /**
     * Sql generator for deleting in LMThermostatManualEvent, useful for bulk deleteing
     * with chunking sql template.
     */
    private class LMThermostatManualEventDeleteSqlGenerator implements SqlGenerator<Integer> {

        @Override
        public String generate(List<Integer> eventIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM LMThermostatManualEvent WHERE EventId IN (", eventIds, ")");
            return sql.toString();
        }
    }
}
