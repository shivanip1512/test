package com.cannontech.stars.dr.event.dao.impl;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.event.dao.EventBaseDao;
import com.cannontech.stars.dr.event.model.EventBase;

public class EventBaseDaoImpl implements EventBaseDao, InitializingBean {

    private ChunkingSqlTemplate chunkingJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private YukonJdbcTemplate yukonJdbcTemplate;

    private SimpleTableAccessTemplate<EventBase> eventBaseTemplate;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void add(EventBase eventBase) {
        int eventId = nextValueHelper.getNextValue("EventBase");
        eventBase.setEventId(eventId);
        
        eventBaseTemplate.insert(eventBase);
    }
    
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

    @Override
    public void afterPropertiesSet() throws Exception {
        chunkingJdbcTemplate = new ChunkingSqlTemplate(yukonJdbcTemplate);
        
        eventBaseTemplate = new SimpleTableAccessTemplate<EventBase>(yukonJdbcTemplate, nextValueHelper);
        eventBaseTemplate.setTableName("EventBase");
        eventBaseTemplate.setPrimaryKeyField("EventId");
        eventBaseTemplate.setFieldMapper(eventBaseFieldMapper);
        eventBaseTemplate.setPrimaryKeyValidOver(0);
    }

    private FieldMapper<EventBase> eventBaseFieldMapper = new FieldMapper<EventBase>() {

        @Override
        public void extractValues(MapSqlParameterSource p, EventBase eventBase) {
            p.addValue("UserId", eventBase.getUserId());
            p.addValue("SystemCategoryId", eventBase.getSystemCategoryId());
            p.addValue("ActionId", eventBase.getActionId());        
            p.addValue("EventTimestamp", eventBase.getEventTimestamp());
        }

        @Override
        public Number getPrimaryKey(EventBase eventBase) {
            return eventBase.getEventId();
        }

        @Override
        public void setPrimaryKey(EventBase eventBase, int eventId) {
            eventBase.setEventId(eventId);
        }
    };

    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(com.cannontech.database.YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
