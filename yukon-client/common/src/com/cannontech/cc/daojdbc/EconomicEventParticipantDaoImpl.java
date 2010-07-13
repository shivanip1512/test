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
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class EconomicEventParticipantDaoImpl implements
        EconomicEventParticipantDao, InitializingBean {

    YukonJdbcTemplate yukonJdbcTemplate;
    EconomicEventNotifDao economicEventNotifDao;
    CustomerStubDao customerStubDao;
    EconomicEventDao economicEventDao;
    SimpleTableAccessTemplate<EconomicEventParticipant> participantTemplate;
    SimpleTableAccessTemplate<EconomicEventParticipantSelection> selectionTemplate;
    SimpleTableAccessTemplate<EconomicEventParticipantSelectionWindow> windowTemplate;
    NextValueHelper nextValueHelper;
    
    @Override
    public EconomicEventParticipant getForId(Integer id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtEEParticipant");
        sql.append("where CCurtEEParticipantID").eq(id);
        
        List<EconomicEventParticipant> result = yukonJdbcTemplate.query(sql, rowMapper);
        if(result.size() == 0) {
            return null;
        }
        EconomicEvent event = economicEventDao.getForId(result.get(0).getId());
        result.get(0).setEvent(event);
        
        setSelectionsForParticipants(result);
        return result.get(0);
    }

    @Override
    public EconomicEventParticipant getForCustomerAndEvent(CICustomerStub customer, EconomicEvent event) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtEEParticipant");
        sql.append("where CustomerID").eq(customer.getId());
        sql.append(  "and CCurtEconomicEventID").eq(event.getId());
        
        List<EconomicEventParticipant> result = yukonJdbcTemplate.query(sql, rowMapper);
        if(result.size() == 0) {
            return null;
        }
        result.get(0).setEvent(event);
        
        setSelectionsForParticipants(result);
        return result.get(0);
    }

    @Override
    public List<EconomicEventParticipant> getForEvent(EconomicEvent event) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtEEParticipant");
        sql.append("where CCurtEconomicEventID").eq(event.getId());
        
        List<EconomicEventParticipant> result = yukonJdbcTemplate.query(sql, rowMapper);
        for (EconomicEventParticipant participant : result) {
            participant.setEvent(event);
        }
        
        setSelectionsForParticipants(result);
        return result;
    }
    
    @Override
    public List<EconomicEventParticipant> getForCustomer(CICustomerStub customer) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtEEParticipant");
        sql.append("where CustomerID").eq(customer.getId());
        
        List<EconomicEventParticipant> result = yukonJdbcTemplate.query(sql, rowMapper);
        if(!result.isEmpty()) {
            List<EconomicEvent> eventList = economicEventDao.getAllForParticipants(result);
            setEventsForParticipants(result, eventList);
            setSelectionsForParticipants(result);
        }
        return result;
    }

    public List<EconomicEventParticipant> getForNotifs(List<EconomicEventNotif> notifList) {
        List<Integer> participantIdList = new LinkedList<Integer>();
        for (EconomicEventNotif notif : notifList) {
            participantIdList.add(notif.getParticipant().getId());
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtEEParticipant");
        sql.append("where CCurtEEParticipantID").in(participantIdList);
        
        List<EconomicEventParticipant> result = yukonJdbcTemplate.query(sql, rowMapper);
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
    public void delete(EconomicEventParticipant participant) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from CCurtEEParticipant");
        sql.append("where CCurtEEParticipantID").eq(participant.getId());
        
        economicEventNotifDao.deleteForParticipant(participant);
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    @Transactional
    public void deleteForEvent(EconomicEvent event) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from CCurtEEparticipant");
        sql.append("where CCurtEconomicEventID").eq(event.getId());
        
        economicEventNotifDao.deleteForEvent(event);
        yukonJdbcTemplate.update(sql);
    }

    private YukonRowMapper<EconomicEventParticipant> rowMapper = 
        new YukonRowMapper<EconomicEventParticipant>() {
        public EconomicEventParticipant mapRow(YukonResultSet rs) throws SQLException {
            EconomicEventParticipant participant = new EconomicEventParticipant();
            participant.setId(rs.getInt("CCurtEEParticipantID"));
            participant.setNotifAttribs(rs.getString("NotifAttribs"));
            CICustomerStub customer = customerStubDao.getForId(rs.getInt("CustomerID"));
            participant.setCustomer(customer);
            //set dummy event
            EconomicEvent event = new EconomicEvent();
            event.setId(rs.getInt("CCurtEconomicEventID"));
            participant.setEvent(event);
                //EconomicEvent event = economicEventDao.getForId(rs.getInt("CCurtEconomicEventID"));
                //participant.setEvent(event);
            //selections left unset
            return participant;
        }
    };
    
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
        participantTemplate.withTableName("CCurtEEParticipant");
        participantTemplate.withPrimaryKeyField("CCurtEEParticipantID");
        participantTemplate.withFieldMapper(economicEventParticipantFieldMapper);

        selectionTemplate = new SimpleTableAccessTemplate<EconomicEventParticipantSelection>(yukonJdbcTemplate, nextValueHelper);
        selectionTemplate.withTableName("CCurtEEParticipantSelection");
        selectionTemplate.withPrimaryKeyField("CCurtEEParticipantSelectionID");
        selectionTemplate.withFieldMapper(economicEventParticipantSelectionFieldMapper);

        windowTemplate = new SimpleTableAccessTemplate<EconomicEventParticipantSelectionWindow>(yukonJdbcTemplate, nextValueHelper);
        windowTemplate.withTableName("CCurtEEParticipantWindow");
        windowTemplate.withPrimaryKeyField("CCurtEEParticipantWindowID");
        windowTemplate.withFieldMapper(economicEventParticipantSelectionWindowFieldMapper);
    }

    private void setEventsForParticipants(List<EconomicEventParticipant> participantList, List<EconomicEvent> eventList) {
        for (EconomicEventParticipant participant : participantList) {
            for (EconomicEvent event : eventList) {
                if(event.getId().intValue() == participant.getEvent().getId().intValue()) {
                    participant.setEvent(event);
                } 
            }
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
                Date submitTime = rs.getTimestamp("SubmitTime");
                selection.setSubmitTime(submitTime);
                SelectionState state = SelectionState.valueOf(rs.getString("State"));
                selection.setState(state);
                // dummy eventPricing
                EconomicEventPricing pricingRevision = new EconomicEventPricing();
                pricingRevision.setId(rs.getInt("CCurtEEPricingID"));
                selection.setPricingRevision(pricingRevision);
                // selectionWindow left unset
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
            //set the proper list of EEParticipantSelections to each EEParticipant
            Collection<EconomicEventParticipantSelection> selectionCol = pointLookup.get(participant);
            participant.setSelections(selectionCol);
            allSelections.addAll(selectionCol);
            
            Collection<EconomicEventPricing> pricingCol = participant.getEvent().getRevisions().values();
            for (EconomicEventParticipantSelection selection : selectionCol) {
                //map the proper EEParticipant to each EEParticipantSelection
                selection.setParticipant(participant);
            
                for (EconomicEventPricing pricing : pricingCol) {
                    //map the proper EEPricing to each EEParticipantSelection
                    if(pricing.getId().intValue() == selection.getPricingRevision().getId().intValue()){
                        selection.setPricingRevision(pricing);
                        continue;
                    }
                }
            }
        }
        
        //find all EEParticipantSelectionWindows for each EEParticipantSelection we just handled
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
                // eventParticipantSelection left unset
                // dummy eventPricingWindow
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
            //map the proper list of EEParticipantSelectionWindows to each EEParticipantSelection
            Collection<EconomicEventParticipantSelectionWindow> selectionWindowCol = pointLookup.get(selection);

            Collection<EconomicEventPricingWindow> pricingWindowCol = selection.getPricingRevision().getWindows().values();
            for (EconomicEventParticipantSelectionWindow window : selectionWindowCol) {
                //map the proper EEParticipantSelection to each EEParticipantSelectionWindow
                window.setSelection(selection);
                
                for (EconomicEventPricingWindow pricingWindow : pricingWindowCol) {
                    //map the proper EEPricingWindow to each EEParticipantSelectionWindow 
                    if(pricingWindow.getId().intValue() == window.getWindow().getId().intValue()) {
                        window.setWindow(pricingWindow);
                        continue;
                    }
                }
            }
            //must map selectionWindows before adding them to selection
            //otherwise set will read the nulls and declare them equal.
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
}
