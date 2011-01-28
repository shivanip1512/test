package com.cannontech.cc.daojdbc;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cc.dao.EconomicEventDao;
import com.cannontech.cc.dao.EconomicEventParticipantDao;
import com.cannontech.cc.dao.ProgramDao;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.cc.model.EconomicEventPricingWindow;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.service.enums.EconomicEventState;
import com.cannontech.common.util.CachingDaoWrapper;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.YukonRowMapperAdapter;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class EconomicEventDaoImpl implements InitializingBean, EconomicEventDao {

    private YukonJdbcTemplate yukonJdbcTemplate;
    private SimpleTableAccessTemplate<EconomicEvent> eventTemplate;
    private SimpleTableAccessTemplate<EconomicEventPricing> pricingTemplate;
    private SimpleTableAccessTemplate<EconomicEventPricingWindow> windowTemplate;
    private NextValueHelper nextValueHelper;
    private ProgramDao programDao;
    private EconomicEventParticipantDao economicEventParticipantDao;

    @Override
    public EconomicEvent getChildEvent(EconomicEvent event) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtEconomicEvent");
        sql.append("where InitialEventID").eq(event.getId());

        List<EconomicEvent> result = yukonJdbcTemplate.query(sql, new EconomicEventRowMapper(event.getProgram()));
        if(result.size() == 0) {
            return null;
        }
        EconomicEvent childEvent = Iterables.getOnlyElement(result);
        
        setRevisionsForEvents(Collections.singletonList(childEvent));
        return childEvent;
    }

    @Override
    public EconomicEvent getForId(Integer id) {
        if (id == null) {
            return null;
        }
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtEconomicEvent");
        sql.append("where CCurtEconomicEventID").eq(id);

        List<EconomicEvent> result = yukonJdbcTemplate.query(sql, new EconomicEventRowMapper());
        if(result.size() == 0) {
            return null;
        }
        EconomicEvent event = Iterables.getOnlyElement(result);
        
        setRevisionsForEvents(Collections.singletonList(event));
        return event;
    }

    @Override
    public List<EconomicEvent> getAllForProgram(Program program) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtEconomicEvent");
        sql.append("where CCurtProgramID").eq(program.getId());

        List<EconomicEvent> result = yukonJdbcTemplate.query(sql, new EconomicEventRowMapper(program));
        setRevisionsForEvents(result);
        return result;
    }
    
    @Override
    public List<EconomicEvent> getAllForCustomer(CICustomerStub customer) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select distinct ee.*");
        sql.append("from CCurtEconomicEvent ee");
        sql.append(  "join CCurtEEParticipant eep on ee.CCurtEconomicEventID = eep.CCurtEconomicEventID");
        sql.append("where eep.CustomerID").eq(customer.getId());

        List<EconomicEvent> eventList = yukonJdbcTemplate.query(sql, new EconomicEventRowMapper());
        setRevisionsForEvents(eventList);
        return eventList;
    }

    @Override
    public List<EconomicEvent> getAllForEnergyCompany(LiteEnergyCompany energyCompany) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select distinct ee.*");
        sql.append("from CCurtEconomicEvent ee");
        sql.append(  "join CCurtEEParticipant eep on ee.CCurtEconomicEventID = eep.CCurtEconomicEventID");
        sql.append("where eep.CustomerID in (");
        sql.append(energyCompany.getCiCustumerIDs().toArray());
        sql.append(")");

        List<EconomicEvent> result = yukonJdbcTemplate.query(sql, new EconomicEventRowMapper());
        setRevisionsForEvents(result);
        return result;
    }

    public List<EconomicEvent> getAllForParticipants(List<EconomicEventParticipant> participantList) {
        List<Integer> eventIdList = Lists.newArrayListWithCapacity(participantList.size());
        for (EconomicEventParticipant participant : participantList) {
            eventIdList.add(participant.getEvent().getId());
        }

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtEconomicEvent");
        sql.append("where CCurtEconomicEventID").in(eventIdList);
        
        List<EconomicEvent> result = yukonJdbcTemplate.query(sql, new EconomicEventRowMapper());
        setRevisionsForEvents(result);
        return result;
    }
    
    @Override
    @Transactional(propagation=Propagation.MANDATORY)
    public void save(EconomicEvent event) {
        eventTemplate.save(event);
        for (EconomicEventPricing pricing : event.getRevisions().values()) {
            pricingTemplate.save(pricing);
            for (EconomicEventPricingWindow window : pricing.getWindows().values()) {
                windowTemplate.save(window);
            }
        }
    }
    
    @Override
    @Transactional(propagation=Propagation.MANDATORY)
    public void delete(EconomicEvent event) {
        economicEventParticipantDao.deleteForEvent(event);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete");
        sql.append("from CCurtEconomicEvent");
        sql.append("where CCurtEconomicEventID").eq(event.getId());

        yukonJdbcTemplate.update(sql);
    }

    private FieldMapper<EconomicEvent> economicEventFieldMapper = new FieldMapper<EconomicEvent>() {
        public void extractValues(MapSqlParameterSource p, EconomicEvent event) {
            p.addValue("NotificationTime", event.getNotificationTime());
            p.addValue("WindowLengthMinutes", event.getWindowLengthMinutes());
            p.addValue("State", event.getState());
            p.addValue("StartTime", event.getStartTime());
            p.addValue("CCurtProgramID", event.getProgram().getId());
            Integer initialEventId;
            if (event.isEventExtension()) {
                initialEventId = event.getInitialEvent().getId();
            } else {
                initialEventId = null;
            }
            p.addValue("InitialEventID", initialEventId);
            p.addValue("Identifier", event.getIdentifier());
        }

        public Number getPrimaryKey(EconomicEvent event) {
            return event.getId();
        }

        public void setPrimaryKey(EconomicEvent event, int value) {
            event.setId(value);
        }
    };

    private FieldMapper<EconomicEventPricing> economicEventPricingFieldMapper = new FieldMapper<EconomicEventPricing>() {
        public void extractValues(MapSqlParameterSource p, EconomicEventPricing pricing) {
            p.addValue("Revision", pricing.getRevision());
            p.addValue("CreationTime", pricing.getCreationTime());
            p.addValue("CCurtEconomicEventID", pricing.getEvent().getId());
        }

        @Override
        public Number getPrimaryKey(EconomicEventPricing pricing) {
            return pricing.getId();
        }

        @Override
        public void setPrimaryKey(EconomicEventPricing pricing, int value) {
            pricing.setId(value);
        }
    };
    
    private FieldMapper<EconomicEventPricingWindow> economicEventPricingWindowFieldMapper = new FieldMapper<EconomicEventPricingWindow>() {
        public void extractValues(MapSqlParameterSource p, EconomicEventPricingWindow window) {
            p.addValue("EnergyPrice", window.getEnergyPrice());
            p.addValue("Offset", window.getOffset());
            p.addValue("CCurtEEPricingID", window.getPricingRevision().getId());
        }

        @Override
        public Number getPrimaryKey(EconomicEventPricingWindow window) {
            return window.getId();
        }

        @Override
        public void setPrimaryKey(EconomicEventPricingWindow window, int value) {
            window.setId(value);
        }
    };

    @Override
    public void afterPropertiesSet() throws Exception {
        eventTemplate = new SimpleTableAccessTemplate<EconomicEvent>(yukonJdbcTemplate, nextValueHelper);
        eventTemplate.setTableName("CCurtEconomicEvent");
        eventTemplate.setPrimaryKeyField("CCurtEconomicEventID");
        eventTemplate.setFieldMapper(economicEventFieldMapper);

        pricingTemplate = new SimpleTableAccessTemplate<EconomicEventPricing>(yukonJdbcTemplate, nextValueHelper);
        pricingTemplate.setTableName("CCurtEEPricing");
        pricingTemplate.setPrimaryKeyField("CCurtEEPricingID");
        pricingTemplate.setFieldMapper(economicEventPricingFieldMapper);

        windowTemplate = new SimpleTableAccessTemplate<EconomicEventPricingWindow>(yukonJdbcTemplate, nextValueHelper);
        windowTemplate.setTableName("CCurtEEPricingWindow");
        windowTemplate.setPrimaryKeyField("CCurtEEPricingWindowID");
        windowTemplate.setFieldMapper(economicEventPricingWindowFieldMapper);
    }

    private void setRevisionsForEvents(Collection<EconomicEvent> eventCol) {
        ChunkingMappedSqlTemplate mappedSqlTemplate = new ChunkingMappedSqlTemplate(yukonJdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("select * from CCurtEEPricing");
                sql.append("where CCurtEconomicEventID").in(subList);
                return sql;
            }
        };

        YukonRowMapper<Map.Entry<Integer, EconomicEventPricing>> pricingRowMapper = new YukonRowMapper<Map.Entry<Integer, EconomicEventPricing>>() {
            public Map.Entry<Integer, EconomicEventPricing> mapRow(YukonResultSet rs) throws SQLException {
                EconomicEventPricing value = new EconomicEventPricing();
                value.setId(rs.getInt("CCurtEEPricingID"));
                value.setRevision(rs.getInt("Revision"));
                Date creationTime = rs.getDate("CreationTime");
                value.setCreationTime(creationTime);
                Integer eventId = rs.getInt("CCurtEconomicEventID");
                return Maps.immutableEntry(eventId, value);
            }
        };

        Function<EconomicEvent, Integer> func = new Function<EconomicEvent, Integer>() {
            public Integer apply(EconomicEvent event) {
                return event.getId();
            }
        };

        Multimap<EconomicEvent, EconomicEventPricing> pointLookup = 
            mappedSqlTemplate.multimappedQuery(sqlGenerator, eventCol, new YukonRowMapperAdapter<Map.Entry<Integer, EconomicEventPricing>>(pricingRowMapper), func);

        Collection<EconomicEventPricing> allRevisions = Lists.newArrayListWithCapacity(eventCol.size());
        for (EconomicEvent event : eventCol) {
            Collection<EconomicEventPricing> pricingCollection = pointLookup.get(event);
            event.setRevisions(pricingCollection);
            allRevisions.addAll(pricingCollection);
            for (EconomicEventPricing pricing : pricingCollection) {
                pricing.setEvent(event);
            }
        }
        setPricingWindowsForPricings(allRevisions);
    }

    private void setPricingWindowsForPricings(Collection<EconomicEventPricing> pricingCol) {
        ChunkingMappedSqlTemplate mappedSqlTemplate = new ChunkingMappedSqlTemplate(yukonJdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("select * from CCurtEEPricingWindow");
                sql.append("where CCurtEEPricingID").in(subList);
                return sql;
            }
        };

        YukonRowMapper<Map.Entry<Integer, EconomicEventPricingWindow>> pricingWindowRowMapper = new YukonRowMapper<Map.Entry<Integer, EconomicEventPricingWindow>>() {
            public Map.Entry<Integer, EconomicEventPricingWindow> mapRow(YukonResultSet rs) throws SQLException {
                EconomicEventPricingWindow window = new EconomicEventPricingWindow();
                window.setId(rs.getInt("CCurtEEPricingWindowID"));
                window.setEnergyPrice(rs.getBigDecimal("EnergyPrice"));
                window.setOffset(rs.getInt("Offset"));
                Integer pricingId = rs.getInt("CCurtEEPricingID");
                return Maps.immutableEntry(pricingId, window);
            }
        };

        Function<EconomicEventPricing, Integer> func = new Function<EconomicEventPricing, Integer>() {
            public Integer apply(EconomicEventPricing pricing) {
                return pricing.getId();
            }
        };

        Multimap<EconomicEventPricing, EconomicEventPricingWindow> pointLookup = 
            mappedSqlTemplate.multimappedQuery(sqlGenerator, 
                                               pricingCol, 
                                               new YukonRowMapperAdapter<Map.Entry<Integer, EconomicEventPricingWindow>>(pricingWindowRowMapper), 
                                               func);

        for (EconomicEventPricing pricing : pricingCol) {
            Collection<EconomicEventPricingWindow> collection = pointLookup.get(pricing);
            pricing.setWindows(collection);
            for (EconomicEventPricingWindow window : collection) {
                window.setPricingRevision(pricing);
            }
        }
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    @Autowired
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }

    public void setEconomicEventParticipantDao(EconomicEventParticipantDao economicEventParticipantDao) {
        this.economicEventParticipantDao = economicEventParticipantDao;
    }

    private class EconomicEventRowMapper implements YukonRowMapper<EconomicEvent> {
        private CachingDaoWrapper<Program> cachingProgramDao;
        
        public EconomicEventRowMapper(Program... initialItems) {
            cachingProgramDao = new CachingDaoWrapper<Program>(programDao, initialItems);
        }
        
        public EconomicEvent mapRow(YukonResultSet rs) throws SQLException {
            EconomicEvent event = new EconomicEvent();
            event.setId(rs.getInt("CCurtEconomicEventID"));
            Date notificationTime = rs.getDate("NotificationTime");
            event.setNotificationTime(notificationTime);
            event.setWindowLengthMinutes(rs.getInt("WindowLengthMinutes"));
            EconomicEventState state = rs.getEnum("State", EconomicEventState.class);
            event.setState(state);
            Date startTime = rs.getDate("StartTime");
            event.setStartTime(startTime);
            Program program = cachingProgramDao.getForId(rs.getInt("CCurtProgramID"));
            event.setProgram(program);
            EconomicEvent initialEvent = getForId(rs.getInt("InitialEventID"));
            event.setInitialEvent(initialEvent);
            event.setIdentifier(rs.getInt("Identifier"));
            return event;
        }
    }
}
