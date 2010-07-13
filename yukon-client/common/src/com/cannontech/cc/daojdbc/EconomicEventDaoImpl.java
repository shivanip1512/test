package com.cannontech.cc.daojdbc;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
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
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class EconomicEventDaoImpl implements InitializingBean, EconomicEventDao {

    YukonJdbcTemplate yukonJdbcTemplate;
    SimpleTableAccessTemplate<EconomicEvent> eventTemplate;
    SimpleTableAccessTemplate<EconomicEventPricing> pricingTemplate;
    SimpleTableAccessTemplate<EconomicEventPricingWindow> windowTemplate;
    NextValueHelper nextValueHelper;
    ProgramDao programDao;
    EconomicEventParticipantDao economicEventParticipantDao;

    @Override
    public EconomicEvent getChildEvent(EconomicEvent event) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtEconomicEvent");
        sql.append("where InitialEventID").eq(event.getId());

        List<EconomicEvent> result = yukonJdbcTemplate.query(sql, rowMapper);
        if(result.size() == 0) {
            return null;
        }
        setRevisionsForEvents(result);
        return result.get(0);
    }

    @Override
    public EconomicEvent getForId(Integer id) {
        if (id == null) {
            return null;
        }
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtEconomicEvent");
        sql.append("where CCurtEconomicEventID").eq(id);

        List<EconomicEvent> result = yukonJdbcTemplate.query(sql, rowMapper);
        if(result.size() == 0) {
            return null;
        }
        setRevisionsForEvents(result);
        return result.get(0);
    }

    @Override
    public List<EconomicEvent> getAllForProgram(Program program) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtEconomicEvent");
        sql.append("where CCurtProgramID").eq(program.getId());

        List<EconomicEvent> result = yukonJdbcTemplate.query(sql, rowMapper);
        setRevisionsForEvents(result);
        return result;
    }
    
    @Override
    public List<EconomicEvent> getAllForCustomer(CICustomerStub customer) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select distinct ee.* from CCurtEconomicEvent ee, CCurtEEParticipant eep");
        sql.append("where eep.CustomerID").eq(customer.getId());

        List<EconomicEvent> eventList = yukonJdbcTemplate.query(sql, rowMapper);
        setRevisionsForEvents(eventList);
        return eventList;
    }

    @Override
    public List<EconomicEvent> getAllForEnergyCompany(LiteEnergyCompany energyCompany) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select distinct ee.* from CCurtEconomicEvent ee, CCurtEEParticipant eep");
        sql.append("where eep.CustomerID in (");
        sql.append(energyCompany.getCiCustumerIDs().toArray());
        sql.append(")");
//        for (Integer id : energyCompany.getCiCustumerIDs().toArray()) {
//            sql.appendArgument(id);
//        }

        List<EconomicEvent> result = yukonJdbcTemplate.query(sql, rowMapper);
        setRevisionsForEvents(result);
        return result;
    }

    public List<EconomicEvent> getAllForParticipants(List<EconomicEventParticipant> participantList) {
        List<Integer> eventIdList = new LinkedList<Integer>();
        for (EconomicEventParticipant participant : participantList) {
            eventIdList.add(participant.getEvent().getId());
        }

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtEconomicEvent");
        sql.append("where CCurtEconomicEventID").in(eventIdList);
        
        List<EconomicEvent> result = yukonJdbcTemplate.query(sql, rowMapper);
        setRevisionsForEvents(result);
        return result;
    }
    
    @Override
    @Transactional(propagation=Propagation.MANDATORY)
    public void save(EconomicEvent event) {
        //Find out if new saved lists can be smaller.
        //removing orphan objects will require an extra query or two.
        //might be able to get all the information in one big join.
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
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from CCurtEconomicEvent");
        sql.append("where CCurtEconomicEventID").eq(event.getId());

        economicEventParticipantDao.deleteForEvent(event);
        yukonJdbcTemplate.update(sql);
    }

    private YukonRowMapper<EconomicEvent> rowMapper = new YukonRowMapper<EconomicEvent>() {
        public EconomicEvent mapRow(YukonResultSet rs) throws SQLException {
            EconomicEvent event = new EconomicEvent();
            event.setId(rs.getInt("CCurtEconomicEventID"));
            Date notificationTime = rs.getTimestamp("NotificationTime");
            event.setNotificationTime(notificationTime);
            event.setWindowLengthMinutes(rs.getInt("WindowLengthMinutes"));
            EconomicEventState state = EconomicEventState.valueOf(rs.getString("State"));
            event.setState(state);
            Date startTime = rs.getTimestamp("StartTime");
            event.setStartTime(startTime);
            Program program = programDao.getForId(rs.getInt("CCurtProgramID"));
            event.setProgram(program);
            EconomicEvent initialEvent = getForId(rs.getInt("InitialEventID"));
            event.setInitialEvent(initialEvent);
            event.setIdentifier(rs.getInt("Identifier"));
            // revisions is left unset
            return event;
        }
    };

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
        eventTemplate.withTableName("CCurtEconomicEvent");
        eventTemplate.withPrimaryKeyField("CCurtEconomicEventID");
        eventTemplate.withFieldMapper(economicEventFieldMapper);

        pricingTemplate = new SimpleTableAccessTemplate<EconomicEventPricing>(yukonJdbcTemplate, nextValueHelper);
        pricingTemplate.withTableName("CCurtEEPricing");
        pricingTemplate.withPrimaryKeyField("CCurtEEPricingID");
        pricingTemplate.withFieldMapper(economicEventPricingFieldMapper);

        windowTemplate = new SimpleTableAccessTemplate<EconomicEventPricingWindow>(yukonJdbcTemplate, nextValueHelper);
        windowTemplate.withTableName("CCurtEEPricingWindow");
        windowTemplate.withPrimaryKeyField("CCurtEEPricingWindowID");
        windowTemplate.withFieldMapper(economicEventPricingWindowFieldMapper);
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
                Date creationTime = rs.getTimestamp("CreationTime");
                value.setCreationTime(creationTime);
                // economicEvent is set later
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

        Collection<EconomicEventPricing> allRevisions = new LinkedList<EconomicEventPricing>();
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
                // eventPricing is set later
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
}
