package com.cannontech.stars.dr.event.dao.impl;

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
import com.cannontech.stars.dr.event.dao.EventBaseDao;
import com.cannontech.stars.dr.event.dao.EventInventoryDao;

public class EventInventoryDaoImpl implements EventInventoryDao,
        InitializingBean {

    private SimpleJdbcTemplate simpleJdbcTemplate;
    private ChunkingSqlTemplate chunkingJdbcTemplate;
    private EventBaseDao eventBaseDao;

    @Override
    @Transactional
    public void deleteInventoryEvents(Integer inventoryId) {
        List<Integer> eventIds = getInventoryEventIds(inventoryId);
        chunkingJdbcTemplate.update(new EventInventoryDeleteSqlGenerator(),
                                  eventIds);
        eventBaseDao.deleteEvents(eventIds);
    }

    private List<Integer> getInventoryEventIds(Integer inventoryId) {
        List<Integer> eventIds = new ArrayList<Integer>();
        String sql = "SELECT EB.EventId FROM EventInventory EI, EventBase EB WHERE EB.EventId = EI.EventId AND EI.InventoryId = ?";
        eventIds = simpleJdbcTemplate.query(sql,
                                            new IntegerRowMapper(),
                                            inventoryId);

        return eventIds;
    }

    /**
     * Sql generator for deleting Inventory events, useful for bulk deleting
     * with chunking sql template.
     */
    private class EventInventoryDeleteSqlGenerator implements
            SqlGenerator<Integer> {

        @Override
        public String generate(List<Integer> eventIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM EventInventory WHERE EventId IN (",
                       eventIds,
                       ")");
            return sql.toString();
        }
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    @Autowired    
    public void setEventBaseDao(EventBaseDao eventBaseDao) {
        this.eventBaseDao = eventBaseDao;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        chunkingJdbcTemplate = new ChunkingSqlTemplate(simpleJdbcTemplate);
    }

}
