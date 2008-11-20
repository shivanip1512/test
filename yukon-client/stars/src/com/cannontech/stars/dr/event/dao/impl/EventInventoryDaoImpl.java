package com.cannontech.stars.dr.event.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
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
    private ChunkingSqlTemplate<Integer> chunkyJdbcTemplate;
    private EventBaseDao eventBaseDao;

    /**
     * Delete events for the Inventory from EventInventory table
     * @param inventoryId
     */
    @Override
    @Transactional
    public void deleteInventoryEvents(Integer inventoryId) {
        List<Integer> eventIds = getInventoryEventIds(inventoryId);
        if (!eventIds.isEmpty()) {
            chunkyJdbcTemplate.update(new EventInventoryDeleteSqlGenerator(),
                                      eventIds);
            eventBaseDao.deleteEvents(eventIds);
        }
    }

    private List<Integer> getInventoryEventIds(Integer inventoryId) {
        List<Integer> eventIds = new ArrayList<Integer>();
        String sql = "SELECT EventId FROM EventInventory EI, EventBase EB WHERE EB.EventId = EI.EventId AND EI.InventoryId = ? ORDER BY EventID";
        eventIds = simpleJdbcTemplate.query(sql,
                                            new IntegerRowMapper(),
                                            inventoryId);

        return eventIds;
    }

    /**
     * Sql generator for deleting in LMHardwareEvent, useful for bulk deleting
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

    // Spring IOC
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    // Spring IOC    
    public void setEventBaseDao(EventBaseDao eventBaseDao) {
        this.eventBaseDao = eventBaseDao;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        chunkyJdbcTemplate = new ChunkingSqlTemplate<Integer>(simpleJdbcTemplate);
    }

}
