package com.cannontech.stars.dr.event.dao.impl;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.stars.dr.event.dao.EventBaseDao;

public class EventBaseDaoImpl implements EventBaseDao, InitializingBean {

    private SimpleJdbcTemplate simpleJdbcTemplate;
    private ChunkingSqlTemplate<Integer> chunkingJdbcTemplate;

    @Override
    @Transactional
    public void deleteEvents(List<Integer> eventIds) {
        chunkingJdbcTemplate.update(new EventBaseDeleteSqlGenerator(), eventIds);
    }

    /**
     * Sql generator for deleting Inventory events, useful for
     * bulk deleting with chunking sql template.
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

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        chunkingJdbcTemplate = new ChunkingSqlTemplate<Integer>(simpleJdbcTemplate);
    }
}
