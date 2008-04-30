package com.cannontech.stars.dr.thermostat.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.hardware.model.CustomerAction;
import com.cannontech.stars.dr.hardware.model.CustomerEventType;
import com.cannontech.stars.dr.thermostat.dao.CustomerEventDao;
import com.cannontech.stars.dr.thermostat.model.CustomerThermostatEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.util.YukonListEntryHelper;

/**
 * Implementation class for CustomerEventDao
 */
public class CustomerEventDaoImpl implements CustomerEventDao {

    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;

    private ECMappingDao ecMappingDao;

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }

    @Override
    public ThermostatManualEvent getLastManualEvent(int inventoryId) {

        LiteStarsEnergyCompany energyCompany = ecMappingDao.getInventoryEC(inventoryId);

        // Query to get the row from LMThermostatManualEvent where the row's
        // event id matches the event id from the row in LMCustomerEventBase
        // where that row's EventDateTime is the most recent for a given
        // inventoryid.
        // In english: get the LMThermostatManualEvent row for the most recent
        // event for the thermostat
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ltmeOuter.*, lceb.EventDateTime");
        sql.append("FROM LMThermostatManualEvent ltmeOuter, LMCustomerEventBase lceb");
        sql.append("WHERE ltmeOuter.EventId = ");
        sql.append("    (SELECT lceb1.eventid");
        sql.append("     FROM LMThermostatManualEvent ltme1");
        sql.append("         JOIN LMCustomerEventBase lceb1 ON lceb1.EventId = ltme1.EventId");
        sql.append("     WHERE ltme1.InventoryId = ltmeOuter.InventoryId");
        sql.append("     AND lceb1.EventDateTime =");
        sql.append("         (SELECT ");
        sql.append("          MAX(lceb2.EventDateTime) EventDateTime");
        sql.append("          FROM LMThermostatManualEvent ltme2, LMCustomerEventBase lceb2");
        sql.append("          WHERE lceb2.EventId = ltme2.EventId");
        sql.append("          and ltme2.inventoryid = ltmeOuter.inventoryid");
        sql.append("          )");
        sql.append("     )");
        sql.append("AND ltmeOuter.EventId = lceb.EventId");
        sql.append("AND ltmeOuter.InventoryId = ?");

        ThermostatManualEvent manualEvent = new ThermostatManualEvent();
        try {
            manualEvent = simpleJdbcTemplate.queryForObject(sql.toString(),
                                                            new ThermostatManualEventMapper(energyCompany),
                                                            inventoryId);
        } catch (EmptyResultDataAccessException e) {
            // ignore - This thermostat has no events
        }

        return manualEvent;
    }

    @Override
    @Transactional
    public void save(CustomerThermostatEvent event) {

        // Get next eventid
        int eventId = nextValueHelper.getNextValue("LMCustomerEventBase");
        event.setEventId(eventId);

        // Get current time
        Date date = new Date();
        event.setDate(date);

        int thermostatId = event.getThermostatId();
        LiteStarsEnergyCompany energyCompany = ecMappingDao.getInventoryEC(thermostatId);

        // Insert row into LMCustomerEventBase
        SqlStatementBuilder baseSql = new SqlStatementBuilder();
        baseSql.append("INSERT INTO LMCustomerEventBase");
        baseSql.append("(EventId, EventTypeId, ActionId, EventDateTime, Notes, AuthorizedBy)");
        baseSql.append("VALUES (?,?,?,?,?,?)");

        CustomerEventType eventType = event.getEventType();
        int eventTypeId = YukonListEntryHelper.getListEntryId(energyCompany,
                                                              eventType);

        CustomerAction action = event.getAction();
        int actionId = YukonListEntryHelper.getListEntryId(energyCompany,
                                                           action);

        String notes = event.getNotes();
        String authorizedBy = event.getAuthorizedBy();
        simpleJdbcTemplate.update(baseSql.toString(),
                                  eventId,
                                  eventTypeId,
                                  actionId,
                                  date,
                                  notes,
                                  authorizedBy);

        // Insert row into ECToLMCustomerEventMapping
        SqlStatementBuilder mappingSql = new SqlStatementBuilder();
        mappingSql.append("INSERT INTO ECToLMCustomerEventMapping");
        mappingSql.append("(EnergyCompanyId, EventId)");
        mappingSql.append("VALUES (?,?)");

        Integer energyCompanyId = energyCompany.getEnergyCompanyID();
        simpleJdbcTemplate.update(mappingSql.toString(),
                                  energyCompanyId,
                                  eventId);

        // Save event-specific data into db
        if (eventType.equals(CustomerEventType.HARDWARE)) {
            this.saveHardwareEvent(event);
        } else if (eventType.equals(CustomerEventType.THERMOSTAT_MANUAL) && event instanceof ThermostatManualEvent) {
            this.saveManualEvent((ThermostatManualEvent) event, energyCompany);
        }

    }

    /**
     * Helper method to add a row to the LMHardwareEvent table
     * @param event - Event to add row for
     */
    private void saveHardwareEvent(CustomerThermostatEvent event) {

        // Insert row into LMHardwareEvent
        SqlStatementBuilder mappingSql = new SqlStatementBuilder();
        mappingSql.append("INSERT INTO LMHardwareEvent");
        mappingSql.append("(EventId, InventoryId)");
        mappingSql.append("VALUES (?,?)");

        simpleJdbcTemplate.update(mappingSql.toString(),
                                  event.getEventId(),
                                  event.getThermostatId());
    }

    /**
     * Helper method to add a row to the LMThermostatManualEvent table
     * @param event - Manual event to add row for
     */
    private void saveManualEvent(ThermostatManualEvent event,
            LiteStarsEnergyCompany energyCompany) {

        // Insert row into LMThermostatManualEvent
        SqlStatementBuilder eventSql = new SqlStatementBuilder();
        eventSql.append("INSERT INTO LMThermostatManualEvent");
        eventSql.append("(EventId, InventoryId, PreviousTemperature, HoldTemperature, OperationStateId, FanOperationId)");
        eventSql.append("VALUES (?,?,?,?,?,?)");

        Integer previousTemperature = event.getPreviousTemperature();
        boolean holdTemperature = event.isHoldTemperature();

        ThermostatMode mode = event.getMode();
        int modeEntryId = YukonListEntryHelper.getListEntryId(energyCompany,
                                                              mode);

        ThermostatFanState fanState = event.getFanState();
        int fanStateEntryId = YukonListEntryHelper.getListEntryId(energyCompany,
                                                                  fanState);

        Integer eventId = event.getEventId();
        Integer thermostatId = event.getThermostatId();
        simpleJdbcTemplate.update(eventSql.toString(),
                                  eventId,
                                  thermostatId,
                                  previousTemperature,
                                  (holdTemperature) ? "Y" : "N",
                                  modeEntryId,
                                  fanStateEntryId);

    }

    /**
     * Helper class to map a result set into a LiteLMThermostatManualEvent
     */
    private class ThermostatManualEventMapper implements
            ParameterizedRowMapper<ThermostatManualEvent> {

        private LiteStarsEnergyCompany energyCompany;

        public ThermostatManualEventMapper(LiteStarsEnergyCompany energyCompany) {
            this.energyCompany = energyCompany;
        }

        @Override
        public ThermostatManualEvent mapRow(ResultSet rs, int rowNum)
                throws SQLException {

            int id = rs.getInt("EventId");
            int inventoryId = rs.getInt("InventoryId");
            int previousTemp = rs.getInt("PreviousTemperature");
            String holdTemp = rs.getString("HoldTemperature");
            Date date = rs.getTimestamp("EventDateTime");

            ThermostatManualEvent event = new ThermostatManualEvent();
            event.setEventId(id);
            event.setThermostatId(inventoryId);
            event.setPreviousTemperature(previousTemp);

            // A temp of -1 indicates this event was a 'run program' event. This
            // should really be handled with a column in the table or some other
            // more solid way
            if (previousTemp == -1) {
                event.setRunProgram(true);
            }

            boolean hold = ("y".equalsIgnoreCase(holdTemp)) ? true : false;
            event.setHoldTemperature(hold);

            // Convert the operation state entryid into a ThermostatMode enum
            // value
            int operationStateId = rs.getInt("OperationStateId");
            int modeDefinitionId = YukonListEntryHelper.getYukonDefinitionId(energyCompany,
                                                                             YukonSelectionListDefs.YUK_LIST_NAME_THERMOSTAT_MODE,
                                                                             operationStateId);

            ThermostatMode mode = ThermostatMode.valueOf(modeDefinitionId);
            event.setMode(mode);

            // Convert the fan operation entryid into a ThermostatFanState enum
            // value
            int fanOperationId = rs.getInt("FanOperationId");
            int fanStateDefinitionId = YukonListEntryHelper.getYukonDefinitionId(energyCompany,
                                                                                 YukonSelectionListDefs.YUK_LIST_NAME_THERMOSTAT_FAN_STATE,
                                                                                 fanOperationId);
            ThermostatFanState fanState = ThermostatFanState.valueOf(fanStateDefinitionId);
            event.setFanState(fanState);

            event.setDate(date);

            event.setEventType(CustomerEventType.THERMOSTAT_MANUAL);
            event.setAction(CustomerAction.MANUAL_OPTION);

            return event;
        }
    }

}
