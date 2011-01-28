package com.cannontech.cc.daojdbc;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cc.dao.CustomerStubDao;
import com.cannontech.cc.dao.EconomicEventDao;
import com.cannontech.cc.dao.EconomicEventNotifDao;
import com.cannontech.cc.dao.EconomicEventParticipantDao;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventNotif;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.cc.model.EconomicEventParticipantSelection;
import com.cannontech.cc.model.EconomicEventParticipantSelectionWindow;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.cc.model.EconomicEventPricingWindow;
import com.cannontech.cc.model.EconomicEventParticipantSelection.SelectionState;
import com.cannontech.common.util.CachingDaoWrapper;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.support.IdentifiableUtils;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.YukonRowMapperAdapter;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class EconomicEventParticipantDaoImpl implements EconomicEventParticipantDao, InitializingBean {

    private YukonJdbcTemplate yukonJdbcTemplate;
    private EconomicEventNotifDao economicEventNotifDao;
    private CustomerStubDao customerStubDao;
    private EconomicEventDao economicEventDao;
    private SimpleTableAccessTemplate<EconomicEventParticipant> participantTemplate;
    private SimpleTableAccessTemplate<EconomicEventParticipantSelection> selectionTemplate;
    private SimpleTableAccessTemplate<EconomicEventParticipantSelectionWindow> windowTemplate;
    private NextValueHelper nextValueHelper;
    
    @Override
    public EconomicEventParticipant getForCustomerAndEvent(CICustomerStub customer, EconomicEvent event) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtEEParticipant");
        sql.append("where CustomerID").eq(customer.getId());
        sql.append(  "and CCurtEconomicEventID").eq(event.getId());
        
        List<EconomicEventParticipant> result = yukonJdbcTemplate.query(sql, new EconomicEventParticipantRowMapper(customer));
        if(result.size() == 0) {
            return null;
        }
        EconomicEventParticipant participant = Iterables.getOnlyElement(result);
        
        participant.setEvent(event);
        
        setSelectionsForParticipants(Collections.singletonList(participant));
        return participant;
    }

    @Override
    public List<EconomicEventParticipant> getForEvent(EconomicEvent event) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtEEParticipant");
        sql.append("where CCurtEconomicEventID").eq(event.getId());
        
        List<EconomicEventParticipant> result = yukonJdbcTemplate.query(sql, new EconomicEventParticipantRowMapper());
        for (EconomicEventParticipant participant : result) {
            participant.setEvent(event);
        }
        
        setSelectionsForParticipants(result);
        return result;
    }
    
    @Override
    public List<EconomicEventParticipant> getForCustomer(CICustomerStub customer) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtEEParticipant");
        sql.append("where CustomerID").eq(customer.getId());
        
        List<EconomicEventParticipant> result = yukonJdbcTemplate.query(sql, new EconomicEventParticipantRowMapper(customer));
        if(!result.isEmpty()) {
            List<EconomicEvent> eventList = economicEventDao.getAllForParticipants(result);
            setEventsForParticipants(result, eventList);
            setSelectionsForParticipants(result);
        }
        return result;
    }

    public List<EconomicEventParticipant> getForNotifs(List<EconomicEventNotif> notifList) {
        HashSet<Object> participantIdSet = Sets.newHashSet();
        for (EconomicEventNotif notif : notifList) {
            participantIdSet.add(notif.getParticipant().getId());
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtEEParticipant");
        sql.append("where CCurtEEParticipantID").in(participantIdSet);
        
        List<EconomicEventParticipant> result = yukonJdbcTemplate.query(sql, new EconomicEventParticipantRowMapper());
        if(!result.isEmpty()) {
            List<EconomicEvent> eventList = economicEventDao.getAllForParticipants(result);
            setEventsForParticipants(result, eventList);
            setSelectionsForParticipants(result);
        }
        return result;
    }
    
    @Override
    @Transactional
    public void save(EconomicEventParticipant participant) {
        participantTemplate.save(participant);
        for (EconomicEventParticipantSelection selection : participant.getSelections()) {
            selectionTemplate.save(selection);
            for (EconomicEventParticipantSelectionWindow window : selection.getSelectionWindows()) {
                windowTemplate.save(window);
            }
        }
    }

    @Override
    @Transactional
    public void deleteForEvent(EconomicEvent event) {
        economicEventNotifDao.deleteForEvent(event);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete");
        sql.append("from CCurtEEparticipant");
        sql.append("where CCurtEconomicEventID").eq(event.getId());
        
        yukonJdbcTemplate.update(sql);
    }
    
    private FieldMapper<EconomicEventParticipant> economicEventParticipantFieldMapper = 
        new FieldMapper<EconomicEventParticipant>() {
        public void extractValues(MapSqlParameterSource p, EconomicEventParticipant participant) {
            p.addValue("NotifAttribs", participant.getNotifAttribs());
            p.addValue("CustomerID", participant.getCustomer().getId());
            p.addValue("CCurtEconomicEventID", participant.getEvent().getId());
        }

        public Number getPrimaryKey(EconomicEventParticipant participant) {
            return participant.getId();
        }

        public void setPrimaryKey(EconomicEventParticipant participant, int value) {
            participant.setId(value);
        }
    };
    
    private FieldMapper<EconomicEventParticipantSelection> economicEventParticipantSelectionFieldMapper = 
        new FieldMapper<EconomicEventParticipantSelection>() {
        public void extractValues(MapSqlParameterSource p, EconomicEventParticipantSelection selection) {
            p.addValue("ConnectionAudit", selection.getConnectionAudit());
            p.addValue("SubmitTime", selection.getSubmitTime());
            p.addValue("State", selection.getState());
            p.addValue("CCurtEEParticipantID", selection.getParticipant().getId());
            p.addValue("CCurtEEPricingID", selection.getPricingRevision().getId());
        }

        public Number getPrimaryKey(EconomicEventParticipantSelection selection) {
            return selection.getId();
        }

        public void setPrimaryKey(EconomicEventParticipantSelection selection, int value) {
            selection.setId(value);
        }
    };
    
    private FieldMapper<EconomicEventParticipantSelectionWindow> economicEventParticipantSelectionWindowFieldMapper = 
        new FieldMapper<EconomicEventParticipantSelectionWindow>() {
        public void extractValues(MapSqlParameterSource p, EconomicEventParticipantSelectionWindow window) {
            p.addValue("EnergyToBuy", window.getEnergyToBuy());
            p.addValue("CCurtEEPricingWindowID", window.getWindow().getId());
            p.addValue("CCurtEEParticipantSelectionID", window.getSelection().getId());
        }

        public Number getPrimaryKey(EconomicEventParticipantSelectionWindow window) {
            return window.getId();
        }

        public void setPrimaryKey(EconomicEventParticipantSelectionWindow window, int value) {
            window.setId(value);
        }
    };
    
    @Override
    public void afterPropertiesSet() throws Exception {
        participantTemplate = new SimpleTableAccessTemplate<EconomicEventParticipant>(yukonJdbcTemplate, nextValueHelper);
        participantTemplate.setTableName("CCurtEEParticipant");
        participantTemplate.setPrimaryKeyField("CCurtEEParticipantID");
        participantTemplate.setFieldMapper(economicEventParticipantFieldMapper);

        selectionTemplate = new SimpleTableAccessTemplate<EconomicEventParticipantSelection>(yukonJdbcTemplate, nextValueHelper);
        selectionTemplate.setTableName("CCurtEEParticipantSelection");
        selectionTemplate.setPrimaryKeyField("CCurtEEParticipantSelectionID");
        selectionTemplate.setFieldMapper(economicEventParticipantSelectionFieldMapper);

        windowTemplate = new SimpleTableAccessTemplate<EconomicEventParticipantSelectionWindow>(yukonJdbcTemplate, nextValueHelper);
        windowTemplate.setTableName("CCurtEEParticipantWindow");
        windowTemplate.setPrimaryKeyField("CCurtEEParticipantWindowID");
        windowTemplate.setFieldMapper(economicEventParticipantSelectionWindowFieldMapper);
    }

    private void setEventsForParticipants(List<EconomicEventParticipant> participantList, Iterable<EconomicEvent> eventList) {
        Map<Integer, EconomicEvent> map = IdentifiableUtils.getMap(eventList);
        
        for (EconomicEventParticipant participant : participantList) {
            EconomicEvent event = map.get(participant.getEvent().getId());
            participant.setEvent(event);
        }
    }

    private void setSelectionsForParticipants(Collection<EconomicEventParticipant> participantCol) {
        ChunkingMappedSqlTemplate mappedSqlTemplate = new ChunkingMappedSqlTemplate(yukonJdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("select * from CCurtEEParticipantSelection");
                sql.append("where CCurtEEParticipantID").in(subList);
                return sql;
            }
        };

        YukonRowMapper<Map.Entry<Integer, EconomicEventParticipantSelection>> participantSelectionRowMapper = new YukonRowMapper<Map.Entry<Integer, EconomicEventParticipantSelection>>() {
            public Map.Entry<Integer, EconomicEventParticipantSelection> mapRow(YukonResultSet rs) throws SQLException {
                EconomicEventParticipantSelection selection = new EconomicEventParticipantSelection();
                selection.setId(rs.getInt("CCurtEEParticipantSelectionID"));
                selection.setConnectionAudit(rs.getString("ConnectionAudit"));
                Date submitTime = rs.getDate("SubmitTime");
                selection.setSubmitTime(submitTime);
                SelectionState state = SelectionState.valueOf(rs.getString("State"));
                selection.setState(state);
                EconomicEventPricing pricingRevision = new EconomicEventPricing();
                pricingRevision.setId(rs.getInt("CCurtEEPricingID"));
                selection.setPricingRevision(pricingRevision);
                Integer participantId = rs.getInt("CCurtEEParticipantID");
                return Maps.immutableEntry(participantId, selection);
            }
        };

        Function<EconomicEventParticipant, Integer> func = new Function<EconomicEventParticipant, Integer>() {
            public Integer apply(EconomicEventParticipant participant) {
                return participant.getId();
            }
        };

        Multimap<EconomicEventParticipant, EconomicEventParticipantSelection> pointLookup = 
            mappedSqlTemplate.multimappedQuery(sqlGenerator, participantCol, new YukonRowMapperAdapter<Map.Entry<Integer, EconomicEventParticipantSelection>>(participantSelectionRowMapper), func);

        Collection<EconomicEventParticipantSelection> allSelections = new LinkedList<EconomicEventParticipantSelection>();
        for (EconomicEventParticipant participant : participantCol) {
            Collection<EconomicEventParticipantSelection> selectionCol = pointLookup.get(participant);
            participant.setSelections(selectionCol);
            allSelections.addAll(selectionCol);
            
            Collection<EconomicEventPricing> pricingCol = participant.getEvent().getRevisions().values();
            ImmutableMap<Integer, EconomicEventPricing> pricingLookup = IdentifiableUtils.getMap(pricingCol);
            for (EconomicEventParticipantSelection selection : selectionCol) {
                selection.setParticipant(participant);
                
                EconomicEventPricing pricing = pricingLookup.get(selection.getPricingRevision().getId());
                selection.setPricingRevision(pricing);
            }
        }
        setSelectionWindowsForSelections(allSelections);
    }

    private void setSelectionWindowsForSelections(Collection<EconomicEventParticipantSelection> selectionCol) {
        ChunkingMappedSqlTemplate mappedSqlTemplate = new ChunkingMappedSqlTemplate(yukonJdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("select * from CCurtEEParticipantWindow");
                sql.append("where CCurtEEParticipantSelectionID").in(subList);
                return sql;
            }
        };

        YukonRowMapper<Map.Entry<Integer, EconomicEventParticipantSelectionWindow>> selectionWindowRowMapper = new YukonRowMapper<Map.Entry<Integer, EconomicEventParticipantSelectionWindow>>() {
            public Map.Entry<Integer, EconomicEventParticipantSelectionWindow> mapRow(YukonResultSet rs) throws SQLException {
                EconomicEventParticipantSelectionWindow selectionWindow = new EconomicEventParticipantSelectionWindow();
                selectionWindow.setId(rs.getInt("CCurtEEParticipantWindowID"));
                selectionWindow.setEnergyToBuy(rs.getBigDecimal("EnergyToBuy"));
                EconomicEventPricingWindow pricingWindow = new EconomicEventPricingWindow();
                pricingWindow.setId(rs.getInt("CCurtEEPricingWindowID"));
                selectionWindow.setWindow(pricingWindow);
                Integer selectionId = rs.getInt("CCurtEEParticipantSelectionID");
                return Maps.immutableEntry(selectionId, selectionWindow);
            }
        };

        Function<EconomicEventParticipantSelection, Integer> func = new Function<EconomicEventParticipantSelection, Integer>() {
            public Integer apply(EconomicEventParticipantSelection selection) {
                return selection.getId();
            }
        };

        Multimap<EconomicEventParticipantSelection, EconomicEventParticipantSelectionWindow> pointLookup = 
            mappedSqlTemplate.multimappedQuery(sqlGenerator, selectionCol, new YukonRowMapperAdapter<Map.Entry<Integer, EconomicEventParticipantSelectionWindow>>(selectionWindowRowMapper), func);

        for (EconomicEventParticipantSelection selection : selectionCol) {
            Collection<EconomicEventParticipantSelectionWindow> selectionWindowCol = pointLookup.get(selection);

            Collection<EconomicEventPricingWindow> pricingWindowCol = selection.getPricingRevision().getWindows().values();
            ImmutableMap<Integer,EconomicEventPricingWindow> pricingWindowLookup = IdentifiableUtils.getMap(pricingWindowCol);
            for (EconomicEventParticipantSelectionWindow window : selectionWindowCol) {
                window.setSelection(selection);
                
                EconomicEventPricingWindow pricingWindow = pricingWindowLookup.get(window.getWindow().getId());
                window.setWindow(pricingWindow);

            }
            selection.setSelectionWindows(selectionWindowCol);
        }
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setCustomerStubDao(CustomerStubDao customerStubDao) {
        this.customerStubDao = customerStubDao;
    }
    
    @Autowired
    public void setEconomicEventDao(EconomicEventDao economicEventDao) {
        this.economicEventDao = economicEventDao;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    public void setEconomicEventNotifDao(EconomicEventNotifDao economicEventNotifDao) {
        this.economicEventNotifDao = economicEventNotifDao;
    }

    private class EconomicEventParticipantRowMapper implements YukonRowMapper<EconomicEventParticipant> {
        CachingDaoWrapper<CICustomerStub> cachingCustomerStubDao;
        
        public EconomicEventParticipantRowMapper(CICustomerStub... initialItems) {
            cachingCustomerStubDao = new CachingDaoWrapper<CICustomerStub>(customerStubDao, initialItems);
        }
        
        public EconomicEventParticipant mapRow(YukonResultSet rs) throws SQLException {
            EconomicEventParticipant participant = new EconomicEventParticipant();
            participant.setId(rs.getInt("CCurtEEParticipantID"));
            participant.setNotifAttribs(rs.getString("NotifAttribs"));
            CICustomerStub customer = cachingCustomerStubDao.getForId(rs.getInt("CustomerID"));
            participant.setCustomer(customer);
            EconomicEvent event = new EconomicEvent();
            event.setId(rs.getInt("CCurtEconomicEventID"));
            participant.setEvent(event);
            return participant;
        }
    }
}
