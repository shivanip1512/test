package com.cannontech.stars.dr.event.dao.impl;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.stars.dr.event.dao.EventBaseDao;

public class EventBaseDaoImpl implements EventBaseDao, InitializingBean {

    private SimpleJdbcTemplate simpleJdbcTemplate;
    private ChunkingSqlTemplate<Integer> chunkyJdbcTemplate;

    /**
     * Method to delete events from EventBase table
     * @param eventIds
     */
    @Override
    public void deleteEvents(List<Integer> eventIds) {
        if (!eventIds.isEmpty()) {
            chunkyJdbcTemplate.update(new EventBaseDeleteSqlGenerator(),
                                      eventIds);
        }
    }

    /**
     * Sql generator for deleting events from LMCustomerEventBase, useful for
     * bulk deleteing with chunking sql template.
     */
    private class EventBaseDeleteSqlGenerator implements SqlGenerator<Integer> {

        @Override
        public String generate(List<Integer> eventIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM EventBase WHERE EventId IN (",
                       eventIds,
                       ")");
            return sql.toString();
        }
    }

    // Spring IOC
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        chunkyJdbcTemplate = new ChunkingSqlTemplate<Integer>(simpleJdbcTemplate);
    }
}
