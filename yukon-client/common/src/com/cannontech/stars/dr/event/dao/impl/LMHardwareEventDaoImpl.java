package com.cannontech.stars.dr.event.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.database.data.lite.LiteLMHardwareEvent;
import com.cannontech.stars.dr.event.dao.LMCustomerEventBaseDao;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;

public class LMHardwareEventDaoImpl implements LMHardwareEventDao {
    private static final String selectAllSql;
    private static final String selectAndSql;
    private static final String selectOrderSql;
    private static final String selectByIdSql;
    private static final String selecyByInventoryIdSql;
    private static final String selecyByInventoryAndActionIdSql;
    private static final String insertCustomerEventSql;
    private static final String insertEcToCustomerEventSql;
    private static final String insertHardwareEventSql;    
    private static final String updateLmHwEventSql;
    private static final RowMapper<LiteLMHardwareEvent> rowMapper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private LMCustomerEventBaseDao customerEventBaseDao;

    static {
        selectAllSql = "SELECT ce.EventID,EventTypeID,ActionID,EventDateTime,Notes,InventoryID" 
            + " FROM LMCustomerEventBase ce, LMHardwareEvent he, ECToLMCustomerEventMapping map";

        selectAndSql = " AND map.EventID = ce.EventID AND ce.EventID = he.EventID";

        selectOrderSql = " ORDER BY EventDateTime DESC";

        selectByIdSql = selectAllSql + " WHERE ce.EventID = ?" + selectAndSql;

        selecyByInventoryIdSql = selectAllSql + " WHERE InventoryID = ?" + selectAndSql + selectOrderSql;

        selecyByInventoryAndActionIdSql = selectAllSql + " WHERE InventoryID = ? AND ActionID=?" + selectAndSql + selectOrderSql;

        insertCustomerEventSql = "INSERT INTO LMCustomerEventBase (EventID,EventTypeID,ActionID,EventDateTime,Notes,AuthorizedBy) VALUES (?,?,?,?,?,?)";       

        insertEcToCustomerEventSql = "INSERT INTO ECToLMCustomerEventMapping (EventID,EnergyCompanyID) VALUES (?,?)";
        
        insertHardwareEventSql = "INSERT INTO LMHardwareEvent (EventID,InventoryID) VALUES (?,?)";
        
        updateLmHwEventSql = "UPDATE LMCustomerEventBase SET EventDateTime=?,Notes=?,AuthorizedBy=? WHERE EventID=?";

        rowMapper = createRowMapper();
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public LiteLMHardwareEvent getById(final int id)
            throws DataRetrievalFailureException {
        LiteLMHardwareEvent event = jdbcTemplate.queryForObject(selectByIdSql,
                                                                      rowMapper,
                                                                      id);
        return event;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LiteLMHardwareEvent> getByInventoryId(final int inventoryId) {

        List<LiteLMHardwareEvent>  eventList = jdbcTemplate.query(selecyByInventoryIdSql,
                                                                        rowMapper,
                                                                        inventoryId);
        return eventList;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LiteLMHardwareEvent> getByInventoryAndActionId(int inventoryId,
            int actionId) {

        List<LiteLMHardwareEvent> eventList = jdbcTemplate.query(selecyByInventoryAndActionIdSql,
                                                                       rowMapper,
                                                                       inventoryId,
                                                                       actionId);
        return eventList;
    }

    @Override
    @Transactional
    public LiteLMHardwareEvent add(LiteLMHardwareEvent lmHwEvent,
            int energyCompanyId) {
        final int eventId = nextValueHelper.getNextValue("LMCustomerEventBase");
        lmHwEvent.setEventID(eventId);

        jdbcTemplate.update(insertCustomerEventSql,
                                  eventId,
                                  lmHwEvent.getEventTypeID(),
                                  lmHwEvent.getActionID(),
                                  new Timestamp(lmHwEvent.getEventDateTime()),
                                  SqlUtils.convertStringToDbValue(lmHwEvent.getNotes()),
                                  lmHwEvent.getAuthorizedBy());

        jdbcTemplate.update(insertEcToCustomerEventSql,
                                  eventId,
                                  energyCompanyId);

        jdbcTemplate.update(insertHardwareEventSql,
                                  eventId,
                                  lmHwEvent.getInventoryID());

        return lmHwEvent;
    }

    @Override
    @Transactional
    public LiteLMHardwareEvent update(LiteLMHardwareEvent lmHwEvent) {

        jdbcTemplate.update(updateLmHwEventSql,
                                  new Timestamp(lmHwEvent.getEventDateTime()),
                                  lmHwEvent.getNotes(),
                                  lmHwEvent.getAuthorizedBy(),
                                  lmHwEvent.getEventID());

        return lmHwEvent;
    }

    private static RowMapper<LiteLMHardwareEvent> createRowMapper() {
        final RowMapper<LiteLMHardwareEvent> mapper = new RowMapper<LiteLMHardwareEvent>() {
            @Override
            public LiteLMHardwareEvent mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                final LiteLMHardwareEvent event = new LiteLMHardwareEvent();
                event.setInventoryID(rs.getInt("InventoryID"));
                event.setEventID(rs.getInt("EventID"));
                event.setEventTypeID(rs.getInt("EventTypeID"));
                event.setActionID(rs.getInt("ActionID"));
                event.setEventDateTime(rs.getTimestamp("EventDateTime")
                                         .getTime());
                event.setNotes(rs.getString("Notes"));
                return event;
            }
        };
        return mapper;
    }

    private List<Integer> getAllLMHardwareEventIds(Integer inventoryId) {
        List<Integer> eventIds = new ArrayList<Integer>();
        String sql = "SELECT EventId FROM LMHardwareEvent WHERE InventoryId = ?";
        eventIds = jdbcTemplate.query(sql,
                                            new IntegerRowMapper(),
                                            inventoryId);

        return eventIds;
    }

    /**
     * Sql generator for deleting in LMHardwareEvent, useful for bulk deleteing
     * with chunking sql template.
     */
    private class LMHardwareEventDeleteSqlGenerator implements
            SqlGenerator<Integer> {

        @Override
        public String generate(List<Integer> eventIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM LMHardwareEvent WHERE EventId IN (",
                       eventIds,
                       ")");
            return sql.toString();
        }
    }

    @Override
    public void deleteHardwareToMeterMapping(Integer inventoryId) {
        String delete = "DELETE FROM LMHardwareToMeterMapping WHERE LMHardwareInventoryId = ?";
        jdbcTemplate.update(delete, inventoryId);
    }
}
