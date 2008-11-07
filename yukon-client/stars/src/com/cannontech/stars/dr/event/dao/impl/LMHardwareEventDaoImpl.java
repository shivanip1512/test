package com.cannontech.stars.dr.event.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;

public class LMHardwareEventDaoImpl implements LMHardwareEventDao {
    private static final String selectAllSql;
    private static final String selectAndSql;
    private static final String selectOrderSql;
    private static final String selectByIdSql;
    private static final String selecyByInventoryIdSql;
    private static final ParameterizedRowMapper<LiteLMHardwareEvent> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;

    static {
        selectAllSql = "SELECT ce.EventID,EventTypeID,ActionID,EventDateTime,Notes,InventoryID" +
                       " FROM LMCustomerEventBase ce, LMHardwareEvent he, ECToLMCustomerEventMapping map";
        
        selectAndSql = " AND map.EventID = ce.EventID AND ce.EventID = he.EventID";
        
        selectOrderSql = " ORDER BY EventDateTime DESC";
        
        selectByIdSql = selectAllSql + " WHERE ce.EventID = ?" + selectAndSql;
        
        selecyByInventoryIdSql = selectAllSql + " WHERE InventoryID = ?" + selectAndSql + selectOrderSql;
        
        rowMapper = createRowMapper();
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public LiteLMHardwareEvent getById(final int id) throws DataRetrievalFailureException {
        LiteLMHardwareEvent event = simpleJdbcTemplate.queryForObject(selectByIdSql, rowMapper, id);
        return event;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LiteLMHardwareEvent> getByInventoryId(final int inventoryId) {

        List<LiteLMHardwareEvent> eventList = Collections.emptyList();
        eventList = simpleJdbcTemplate.query(selecyByInventoryIdSql,
                                        rowMapper,
                                        inventoryId);
        return eventList;
    }

    private static ParameterizedRowMapper<LiteLMHardwareEvent> createRowMapper() {
        final ParameterizedRowMapper<LiteLMHardwareEvent> mapper = new ParameterizedRowMapper<LiteLMHardwareEvent>() {
            @Override
            public LiteLMHardwareEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
                final LiteLMHardwareEvent event = new LiteLMHardwareEvent();
                event.setInventoryID(rs.getInt("InventoryID"));
                event.setEventID(rs.getInt("EventID"));
                event.setEventTypeID(rs.getInt("EventTypeID"));
                event.setActionID(rs.getInt("ActionID"));
                event.setEventDateTime(rs.getTimestamp("EventDateTime").getTime());
                event.setNotes(rs.getString("Notes"));
                return event;
            }
        };
        return mapper;
    }
    
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    @Override
    public void deleteHardwareToMeterMapping(Integer inventoryId) {
        String delete = "DELETE FROM LMHardwareToMeterMapping WHERE LMHardwareInventoryId = ?";
        simpleJdbcTemplate.update(delete, inventoryId);
    }
    
}
