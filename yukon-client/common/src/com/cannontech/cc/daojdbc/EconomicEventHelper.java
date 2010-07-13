//package com.cannontech.cc.daojdbc;
//
//import java.sql.SQLException;
//import java.util.Collection;
//import java.util.Date;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import com.cannontech.cc.dao.CustomerStubDao;
//import com.cannontech.cc.dao.EconomicEventDao;
//import com.cannontech.cc.dao.EconomicEventParticipantDao;
//import com.cannontech.cc.dao.ProgramDao;
//import com.cannontech.cc.model.CICustomerStub;
//import com.cannontech.cc.model.EconomicEvent;
//import com.cannontech.cc.model.EconomicEventParticipant;
//import com.cannontech.cc.model.EconomicEventParticipantSelection;
//import com.cannontech.cc.model.EconomicEventParticipantSelectionWindow;
//import com.cannontech.cc.model.EconomicEventPricing;
//import com.cannontech.cc.model.EconomicEventPricingWindow;
//import com.cannontech.cc.model.Program;
//import com.cannontech.cc.model.EconomicEventParticipantSelection.SelectionState;
//import com.cannontech.cc.service.enums.EconomicEventState;
//import com.cannontech.common.util.ChunkingMappedSqlTemplate;
//import com.cannontech.common.util.SqlFragmentGenerator;
//import com.cannontech.common.util.SqlFragmentSource;
//import com.cannontech.common.util.SqlStatementBuilder;
//import com.cannontech.database.YukonJdbcTemplate;
//import com.cannontech.database.YukonResultSet;
//import com.cannontech.database.YukonRowMapper;
//import com.cannontech.database.YukonRowMapperAdapter;
//import com.google.common.base.Function;
//import com.google.common.collect.Maps;
//import com.google.common.collect.Multimap;
//
//public class EconomicEventHelper {
//
//    EconomicEventDao economicEventDao;
//    EconomicEventParticipantDao economicEventParticipantDao;
//
//    ProgramDao programDao;
//    CustomerStubDao customerStubDao;
//
//    Multimap<EconomicEvent, EconomicEventPricing> mm1;
//    Multimap<EconomicEventPricing, EconomicEventPricingWindow> mm2;
//    Multimap<EconomicEventParticipant, EconomicEventParticipantSelection> mm3;
//    Multimap<EconomicEventParticipantSelection, EconomicEventParticipantSelectionWindow> mm4;
//
//    Collection<EconomicEvent> eventCol;
//    Collection<EconomicEventParticipant> participantCol;
//    Collection<EconomicEventPricing> allPricings;
//    Collection<EconomicEventParticipantSelection> allSelections;
//    Collection<EconomicEventPricingWindow> pricingWindowCol;
//    Collection<EconomicEventParticipantSelectionWindow> selectionWindowCol;
//
//    YukonJdbcTemplate yukonJdbcTemplate;
//
//    private YukonRowMapper<EconomicEvent> economicEventRowMapper = new YukonRowMapper<EconomicEvent>() {
//        public EconomicEvent mapRow(YukonResultSet rs) throws SQLException {
//            EconomicEvent event = new EconomicEvent();
//            event.setId(rs.getInt("CCurtEconomicEventID"));
//            Date notificationTime = rs.getTimestamp("NotificationTime");
//            event.setNotificationTime(notificationTime);
//            event.setWindowLengthMinutes(rs.getInt("WindowLengthMinutes"));
//            EconomicEventState state = EconomicEventState.valueOf(rs.getString("State"));
//            event.setState(state);
//            Date startTime = rs.getTimestamp("StartTime");
//            event.setStartTime(startTime);
//            Program program = programDao.getForId(rs.getInt("CCurtProgramID"));
//            event.setProgram(program);
//            event.setIdentifier(rs.getInt("Identifier"));
//            EconomicEvent initialEvent = economicEventDao.getForId(rs.getInt("InitialEventID"));
//            event.setInitialEvent(initialEvent);
//            // revisions left unset
//
//            return event;
//        }
//    };
//
//    private YukonRowMapper<EconomicEventParticipant> economicEventParticipantRowMapper = new YukonRowMapper<EconomicEventParticipant>() {
//        public EconomicEventParticipant mapRow(YukonResultSet rs) throws SQLException {
//            EconomicEventParticipant participant = new EconomicEventParticipant();
//            participant.setId(rs.getInt("CCurtEEParticipantID"));
//            participant.setNotifAttribs(rs.getString("NotifAttribs"));
//            CICustomerStub customer = customerStubDao.getForId(rs.getInt("CustomerID"));
//            participant.setCustomer(customer);
//            // EconomicEvent event =
//            // economicEventDao.getForId(rs.getInt("CCurtEconomicEventID"));
//            // participant.setEvent(event);
//            // dummy event
//            EconomicEvent event = new EconomicEvent();
//            event.setId(rs.getInt("CCurtEconomicEventID"));
//            participant.setEvent(event);
//            // selections left unset
//
//            return participant;
//        }
//    };
//
//    private YukonRowMapper<EconomicEventPricing> economicEventPricingRowMapper = new YukonRowMapper<EconomicEventPricing>() {
//        public EconomicEventPricing mapRow(YukonResultSet rs) throws SQLException {
//            EconomicEventPricing pricing = new EconomicEventPricing();
//            pricing.setId(rs.getInt("CCurtEEPricingID"));
//            pricing.setRevision(rs.getInt("Revision"));
//            Date creationTime = rs.getTimestamp("CreationTime");
//            pricing.setCreationTime(creationTime);
//            // dummy event
//            EconomicEvent event = new EconomicEvent();
//            event.setId(rs.getInt("CCurtEconomicEventID"));
//            pricing.setEvent(event);
//
//            return pricing;
//        }
//    };
//
//    private YukonRowMapper<EconomicEventPricingWindow> economicEventPricingWindowRowMapper = new YukonRowMapper<EconomicEventPricingWindow>() {
//        public EconomicEventPricingWindow mapRow(YukonResultSet rs) throws SQLException {
//            EconomicEventPricingWindow pricingWindow = new EconomicEventPricingWindow();
//            pricingWindow.setId(rs.getInt("CCurtEEPricingWindowID"));
//            pricingWindow.setEnergyPrice(rs.getBigDecimal("EnergyPrice"));
//            pricingWindow.setOffset(rs.getInt("Offset"));
//            // dummy eventPricing
//            EconomicEventPricing pricingRevision = new EconomicEventPricing();
//            pricingRevision.setId(rs.getInt("CCurtEEPricingID"));
//            pricingWindow.setPricingRevision(pricingRevision);
//
//            return pricingWindow;
//        }
//    };
//
//    private YukonRowMapper<EconomicEventParticipantSelection> economicEventParticipantSelectionRowMapper = new YukonRowMapper<EconomicEventParticipantSelection>() {
//        public EconomicEventParticipantSelection mapRow(YukonResultSet rs) throws SQLException {
//            EconomicEventParticipantSelection selection = new EconomicEventParticipantSelection();
//            selection.setId(rs.getInt("CCurtEEParticipantSelectionID"));
//            selection.setConnectionAudit(rs.getString("ConnectionAudit"));
//            Date submitTime = rs.getTimestamp("SubmitTime");
//            selection.setSubmitTime(submitTime);
//            SelectionState state = SelectionState.valueOf(rs.getString("State"));
//            selection.setState(state);
//            // dummy eventParticipant
//            EconomicEventParticipant participant = new EconomicEventParticipant();
//            participant.setId(rs.getInt("CCurtEEParticipantID"));
//            selection.setParticipant(participant);
//            // dummy eventPricing
//            EconomicEventPricing pricingRevision = new EconomicEventPricing();
//            pricingRevision.setId(rs.getInt("CCurtEEPricingID"));
//            selection.setPricingRevision(pricingRevision);
//            // selectionWindow left unset
//
//            return selection;
//        }
//    };
//
//    private YukonRowMapper<EconomicEventParticipantSelectionWindow> economicEventParticipantSelectionWindowRowMapper = new YukonRowMapper<EconomicEventParticipantSelectionWindow>() {
//        public EconomicEventParticipantSelectionWindow mapRow(YukonResultSet rs) throws SQLException {
//            EconomicEventParticipantSelectionWindow selectionWindow = new EconomicEventParticipantSelectionWindow();
//            selectionWindow.setId(rs.getInt("CCurtEEParticipantWindowID"));
//            selectionWindow.setEnergyToBuy(rs.getBigDecimal("EnergyToBuy"));
//            // dummy eventParticipantSelection
//            EconomicEventParticipantSelection selection = new EconomicEventParticipantSelection();
//            selection.setId(rs.getInt("CCurtEEParticipantSelectionID"));
//            selectionWindow.setSelection(selection);
//            // eventPricingWindow left unset
//            EconomicEventPricingWindow pricingWindow = new EconomicEventPricingWindow();
//            pricingWindow.setId(rs.getInt("CCurtEEPricingWindowID"));
//            selectionWindow.setWindow(pricingWindow);
//            return selectionWindow;
//        }
//    };
//
//    public void mapEventToParticipant() {
//        for (EconomicEventParticipant participant : participantCol) {
//            for (EconomicEvent event : eventCol) {
//                if (event.getId() == participant.getEvent().getId()) {
//                    participant.setEvent(event);
//                    break;
//                }
//            }
//        }
//    }
//
//    public void mapPricingToSelection() {
//        for (EconomicEventParticipantSelection selection : allSelections) {
//            for (EconomicEventPricing pricingRevision : allPricings) {
//                if (pricingRevision.getId() == selection.getPricingRevision().getId()) {
//                    selection.setPricingRevision(pricingRevision);
//                    break;
//                }
//            }
//        }
//    }
//
//    public void mapSelectionWindowToPricingWindow() {
//        for (EconomicEventParticipantSelectionWindow selectionWindow : selectionWindowCol) {
//            for (EconomicEventPricingWindow pricingWindow : pricingWindowCol) {
//                if (pricingWindow.getId() == selectionWindow.getWindow().getId()) {
//                    selectionWindow.setWindow(pricingWindow);
//                    break;
//                }
//            }
//        }
//    }
//
//    // public void mapEventToPricing() {
//    // for (EconomicEventPricing pricing : pricingList) {
//    // for (EconomicEvent event : eventList) {
//    // if(event.getId() == pricing.getEvent().getId()) {
//    // pricing.setEvent(event);
//    // break;
//    // }
//    // }
//    // }
//    // }
//    //    
//    // public void mapPricingToPricingWindow() {
//    // for (EconomicEventPricingWindow pricingWindow : pricingWindowList) {
//    // for (EconomicEventPricing pricing : pricingList) {
//    // if(pricing.getId() == pricingWindow.getPricingRevision().getId()) {
//    // pricingWindow.setPricingRevision(pricing);
//    // break;
//    // }
//    // }
//    // }
//    // }
//    //    
//    // public void mapParticipantToSelection() {
//    // for (EconomicEventParticipantSelection selection : selectionList) {
//    // for (EconomicEventParticipant participant : participantList) {
//    // if(participant.getId() == selection.getParticipant().getId()) {
//    // selection.setParticipant(participant);
//    // break;
//    // }
//    // }
//    // }
//    // }
//    //
//    // public void mapSelectionToSelectionWindow() {
//    // for (EconomicEventParticipantSelectionWindow selectionWindow :
//    // selectionWindowList) {
//    // for (EconomicEventParticipantSelection selection : selectionList) {
//    // if(selection.getId() == selectionWindow.getSelection().getId()) {
//    // selectionWindow.setSelection(selection);
//    // break;
//    // }
//    // }
//    // }
//    // }
//    //    
//    public void setRevisionsForEvents(Collection<EconomicEvent> eventCol) {
//        ChunkingMappedSqlTemplate mappedSqlTemplate = new ChunkingMappedSqlTemplate(yukonJdbcTemplate);
//        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
//            public SqlFragmentSource generate(List<Integer> subList) {
//                SqlStatementBuilder sql = new SqlStatementBuilder();
//                sql.append("select * from CCurtEEPricing");
//                sql.append("where CCurtEconomicEventID in (").appendArgumentList(subList).append(")");
//                return sql;
//            }
//        };
//
//        YukonRowMapper<Map.Entry<Integer, EconomicEventPricing>> pricingRowMapper = new YukonRowMapper<Map.Entry<Integer, EconomicEventPricing>>() {
//            public Map.Entry<Integer, EconomicEventPricing> mapRow(YukonResultSet rs) throws SQLException {
//                EconomicEventPricing value = new EconomicEventPricing();
//                value.setId(rs.getInt("CCutEEPricingID"));
//                value.setRevision(rs.getInt("Revision"));
//                Date creationTime = rs.getTimestamp("CreationTime");
//                value.setCreationTime(creationTime);
//                // economicEvent is set later
//                return Maps.immutableEntry(rs.getInt("CCurtEconomicEventID"), value);
//            }
//        };
//
//        Function<EconomicEvent, Integer> func = new Function<EconomicEvent, Integer>() {
//            public Integer apply(EconomicEvent event) {
//                return event.getId();
//            }
//        };
//
//        Multimap<EconomicEvent, EconomicEventPricing> pointLookup = mappedSqlTemplate.multimappedQuery(
//                                                                                                       sqlGenerator,
//                                                                                                       eventCol,
//                                                                                                       new YukonRowMapperAdapter<Map.Entry<Integer, EconomicEventPricing>>(
//                                                                                                                                                                           pricingRowMapper),
//                                                                                                       func);
//
//        allPricings = new LinkedList<EconomicEventPricing>();
//        for (EconomicEvent event : eventCol) {
//            Collection<EconomicEventPricing> pricingCollection = pointLookup.get(event);
//            event.setRevisions(pricingCollection);
//            allPricings.addAll(pricingCollection);
//            for (EconomicEventPricing pricing : pricingCollection) {
//                pricing.setEvent(event);
//            }
//        }
//
//        setPricingWindowsForPricings(allPricings);
//    }
//
//    public void setPricingWindowsForPricings(Collection<EconomicEventPricing> pricingCol) {
//        ChunkingMappedSqlTemplate mappedSqlTemplate = new ChunkingMappedSqlTemplate(yukonJdbcTemplate);
//        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
//            public SqlFragmentSource generate(List<Integer> subList) {
//                SqlStatementBuilder sql = new SqlStatementBuilder();
//                sql.append("select * from CCurtEEPricingWindow");
//                sql.append("where CCurtPricingWindowID in (").appendArgumentList(subList).append(")");
//                return sql;
//            }
//        };
//
//        YukonRowMapper<Map.Entry<Integer, EconomicEventPricingWindow>> pricingWindowRowMapper = new YukonRowMapper<Map.Entry<Integer, EconomicEventPricingWindow>>() {
//            public Map.Entry<Integer, EconomicEventPricingWindow> mapRow(YukonResultSet rs) throws SQLException {
//                EconomicEventPricingWindow window = new EconomicEventPricingWindow();
//                window.setId(rs.getInt("CCurtEEPricingWindowID"));
//                window.setEnergyPrice(rs.getBigDecimal("EnergyPrice"));
//                window.setOffset(rs.getInt("Offset"));
//                // dummy eventPricing
//                // EconomicEventPricing pricingRevision = new
//                // EconomicEventPricing();
//                // pricingRevision.setId(rs.getInt("CCurtEEPricingID"));
//                // pricingWindow.setPricingRevision(pricingRevision);
//
//                return Maps.immutableEntry(rs.getInt("CCurtEEPricingID"), window);
//            }
//        };
//
//        Function<EconomicEventPricing, Integer> func = new Function<EconomicEventPricing, Integer>() {
//            public Integer apply(EconomicEventPricing pricing) {
//                return pricing.getId();
//            }
//        };
//
//        Multimap<EconomicEventPricing, EconomicEventPricingWindow> pointLookup = mappedSqlTemplate.multimappedQuery(
//                                                                                                                    sqlGenerator,
//                                                                                                                    pricingCol,
//                                                                                                                    new YukonRowMapperAdapter<Map.Entry<Integer, EconomicEventPricingWindow>>(
//                                                                                                                                                                                              pricingWindowRowMapper),
//                                                                                                                    func);
//
//        for (EconomicEventPricing pricing : pricingCol) {
//            Collection<EconomicEventPricingWindow> collection = pointLookup.get(pricing);
//            pricing.setWindows(collection);
//            for (EconomicEventPricingWindow window : collection) {
//                window.setPricingRevision(pricing);
//            }
//        }
//    }
//
//    public void setSelectionsforParticipants(Collection<EconomicEventParticipant> participantCol) {
//        ChunkingMappedSqlTemplate mappedSqlTemplate = new ChunkingMappedSqlTemplate(yukonJdbcTemplate);
//        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
//            public SqlFragmentSource generate(List<Integer> subList) {
//                SqlStatementBuilder sql = new SqlStatementBuilder();
//                sql.append("select * from CCurtEEParticipantSelection");
//                sql.append("where CCurtEEParticipantID in (").appendArgumentList(subList).append(")");
//                return sql;
//            }
//        };
//
//        YukonRowMapper<Map.Entry<Integer, EconomicEventParticipantSelection>> participantSelectionRowMapper = new YukonRowMapper<Map.Entry<Integer, EconomicEventParticipantSelection>>() {
//            public Map.Entry<Integer, EconomicEventParticipantSelection> mapRow(YukonResultSet rs) throws SQLException {
//                EconomicEventParticipantSelection selection = new EconomicEventParticipantSelection();
//                selection.setId(rs.getInt("CCurtEEParticipantSelectionID"));
//                selection.setConnectionAudit(rs.getString("ConnectionAudit"));
//                Date submitTime = rs.getTimestamp("SubmitTime");
//                selection.setSubmitTime(submitTime);
//                SelectionState state = SelectionState.valueOf(rs.getString("State"));
//                selection.setState(state);
//                // dummy eventParticipant
//                // EconomicEventParticipant participant = new
//                // EconomicEventParticipant();
//                // participant.setId(rs.getInt("CCurtEEParticipantID"));
//                // selection.setParticipant(participant);
//                // dummy eventPricing
//                EconomicEventPricing pricingRevision = new EconomicEventPricing();
//                pricingRevision.setId(rs.getInt("CCurtEEPricingID"));
//                selection.setPricingRevision(pricingRevision);
//                // selectionWindow left unset
//
//                return Maps.immutableEntry(rs.getInt("CCurtEEParticipantID"), selection);
//            }
//        };
//
//        Function<EconomicEventParticipant, Integer> func = new Function<EconomicEventParticipant, Integer>() {
//            public Integer apply(EconomicEventParticipant participant) {
//                return participant.getId();
//            }
//        };
//
//        Multimap<EconomicEventParticipant, EconomicEventParticipantSelection> pointLookup = 
//            mappedSqlTemplate.multimappedQuery(sqlGenerator, participantCol, new YukonRowMapperAdapter<Map.Entry<Integer, EconomicEventParticipantSelection>>(participantSelectionRowMapper), func);
//
//        allSelections = new LinkedList<EconomicEventParticipantSelection>();
//        for (EconomicEventParticipant participant : participantCol) {
//            Collection<EconomicEventParticipantSelection> selectionCol = pointLookup.get(participant);
//            participant.setSelections(selectionCol);
//            this.allSelections.addAll(selectionCol);
//            for (EconomicEventParticipantSelection selection : selectionCol) {
//                selection.setParticipant(participant);
//            }
//        }
//    }
//
//    public void setSelectionWindowsForSelections(Collection<EconomicEventParticipantSelection> selectionCol) {
//        ChunkingMappedSqlTemplate mappedSqlTemplate = new ChunkingMappedSqlTemplate(yukonJdbcTemplate);
//        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
//            public SqlFragmentSource generate(List<Integer> subList) {
//                SqlStatementBuilder sql = new SqlStatementBuilder();
//                sql.append("select * from CCurtEEParticipantWindow");
//                sql.append("where CCurtEEParticipantSelectionID in (").appendArgumentList(subList).append(")");
//                return sql;
//            }
//        };
//
//        YukonRowMapper<Map.Entry<Integer, EconomicEventParticipantSelectionWindow>> selectionWindowRowMapper = new YukonRowMapper<Map.Entry<Integer, EconomicEventParticipantSelectionWindow>>() {
//            public Map.Entry<Integer, EconomicEventParticipantSelectionWindow> mapRow(YukonResultSet rs) throws SQLException {
//                EconomicEventParticipantSelectionWindow selectionWindow = new EconomicEventParticipantSelectionWindow();
//                selectionWindow.setId(rs.getInt("CCurtEEParticipantWindowID"));
//                selectionWindow.setEnergyToBuy(rs.getBigDecimal("EnergyToBuy"));
//                // dummy eventParticipantSelection
//                // EconomicEventParticipantSelection selection = new
//                // EconomicEventParticipantSelection();
//                // selection.setId(rs.getInt("CCurtEEParticipantSelectionID"));
//                // selectionWindow.setSelection(selection);
//                // eventPricingWindow left unset
//                EconomicEventPricingWindow pricingWindow = new EconomicEventPricingWindow();
//                pricingWindow.setId(rs.getInt("CCurtEEPricingWindowID"));
//                selectionWindow.setWindow(pricingWindow);
//                return Maps.immutableEntry(rs.getInt("CCurtEEParticipantSelectionID"), selectionWindow);
//            }
//        };
//
//        Function<EconomicEventParticipantSelection, Integer> func = new Function<EconomicEventParticipantSelection, Integer>() {
//            public Integer apply(EconomicEventParticipantSelection selection) {
//                return selection.getId();
//            }
//        };
//
//        Multimap<EconomicEventParticipantSelection, EconomicEventParticipantSelectionWindow> pointLookup = mappedSqlTemplate.multimappedQuery(
//                                                                                                                                              sqlGenerator,
//                                                                                                                                              selectionCol,
//                                                                                                                                              new YukonRowMapperAdapter<Map.Entry<Integer, EconomicEventParticipantSelectionWindow>>(
//                                                                                                                                                                                                                                     selectionWindowRowMapper),
//                                                                                                                                              func);
//
//        for (EconomicEventParticipantSelection selection : selectionCol) {
//            Collection<EconomicEventParticipantSelectionWindow> selectionWindowCol = pointLookup.get(selection);
//            selection.setSelectionWindows(selectionWindowCol);
//        }
//    }
//}
