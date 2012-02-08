package com.cannontech.stars.dr.thermostat.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.temperature.Temperature;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.hardware.model.CustomerAction;
import com.cannontech.stars.dr.hardware.model.CustomerEventType;
import com.cannontech.stars.dr.thermostat.dao.CustomerEventDao;
import com.cannontech.stars.dr.thermostat.model.CustomerThermostatEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.util.YukonListEntryHelper;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

/**
 * Implementation class for CustomerEventDao
 */
public class CustomerEventDaoImpl implements CustomerEventDao {

    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public ThermostatManualEvent getLastManualEvent(int inventoryId) {

        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByInventoryId(inventoryId);

        // Query to get the row from LMThermostatManualEvent where the row's
        // event id matches the event id from the row in LMCustomerEventBase
        // where that row's EventDateTime is the most recent for a given
        // inventoryid.
        // In english: get the LMThermostatManualEvent row for the most recent
        // event for the thermostat
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT LMTME.*, LMCEB.EventDateTime");
        sql.append("FROM LMThermostatManualEvent LMTME, LMCustomerEventBase LMCEB");
        sql.append("WHERE LMTME.EventId = LMCEB.EventId");
        sql.append("  AND LMTME.InventoryId").eq(inventoryId);
        sql.append("ORDER BY LMCEB.EventDateTime DESC");
        
        List<ThermostatManualEvent> eventList = yukonJdbcTemplate.query(sql,  new ThermostatManualEventMapper(yukonEnergyCompany));

        // Build up the most relivent manual thermostat event grabbing the most recent temperatures for both heat and cool. If a past heat or cool
        // temperature doesn't exist for a given thermostat, use the default temperature
        ThermostatManualEvent manualEvent = new ThermostatManualEvent();
    	if(eventList.size() > 0) {
    		manualEvent = eventList.get(0);
    		fillInMissingTemperature(manualEvent, eventList);
    	}
    	
        return manualEvent;
    }
    
    /**
     * This method goes through and tries to set any missing temperatures with the latest know value for that temperature.
     * Example:  if you have the events  [87, null], [76, null], and [null, 89] you would get [87, 89]
     */
    private void fillInMissingTemperature(ThermostatManualEvent latestManualEvent, List<ThermostatManualEvent> eventList) {
        // Patch cool temperature if it doesn't exist
        if (latestManualEvent.getPreviousCoolTemperature().toFahrenheit().getValue() == 0) {
            for (ThermostatManualEvent manualEvent : eventList) {
                if (manualEvent.getPreviousCoolTemperature().toFahrenheit().getValue() != 0) {
                    latestManualEvent.setPreviousCoolTemperature(manualEvent.getPreviousCoolTemperature());
                    break;
                }
            }
        }
        
        // Use the default value for a non existing temperature
        if (latestManualEvent.getPreviousCoolTemperature().toFahrenheit().getValue() == 0) {
            latestManualEvent.setPreviousCoolTemperature(ThermostatManualEvent.DEFAULT_TEMPERATURE);
        }
        
        // Patch heat temperature if it doesn't exist
        if (latestManualEvent.getPreviousHeatTemperature().toFahrenheit().getValue() == 0) {
            for (ThermostatManualEvent manualEvent : eventList) {
                if (manualEvent.getPreviousHeatTemperature().toFahrenheit().getValue() != 0) {
                    latestManualEvent.setPreviousHeatTemperature(manualEvent.getPreviousHeatTemperature());
                    break;
                }
            }
        }

        // Use the default value for a non existing temperature
        if (latestManualEvent.getPreviousHeatTemperature().toFahrenheit().getValue() == 0) {
            latestManualEvent.setPreviousHeatTemperature(ThermostatManualEvent.DEFAULT_TEMPERATURE);
        }
    }
    
    @Override
    @Transactional
    public void save(CustomerThermostatEvent event) {
        YukonEnergyCompany yukonEnergyCompany = 
            yukonEnergyCompanyService.getEnergyCompanyByInventoryId(event.getThermostatId());
        
        // Get next eventid
        int eventId = nextValueHelper.getNextValue("LMCustomerEventBase");
        event.setEventId(eventId);

        // Get current time
        Date date = new Date();
        event.setDate(date);

        
        LiteStarsEnergyCompany liteStarsEnergyCompany = 
            starsDatabaseCache.getEnergyCompany(yukonEnergyCompany.getEnergyCompanyId());
        
        CustomerEventType eventType = event.getEventType();
        int eventTypeId = YukonListEntryHelper.getListEntryId(liteStarsEnergyCompany, eventType);

        CustomerAction action = event.getAction();
        int actionId = YukonListEntryHelper.getListEntryId(liteStarsEnergyCompany, action);

        String notes = event.getNotes();
        String authorizedBy = event.getAuthorizedBy();

        // Insert row into LMCustomerEventBase
        SqlStatementBuilder baseSql = new SqlStatementBuilder();
        baseSql.append("INSERT INTO LMCustomerEventBase");
        baseSql.append("(EventId, EventTypeId, ActionId, EventDateTime, Notes, AuthorizedBy)");
        baseSql.values(eventId, eventTypeId, actionId, date, notes, authorizedBy);
        yukonJdbcTemplate.update(baseSql);

        // Insert row into ECToLMCustomerEventMapping
        SqlStatementBuilder mappingSql = new SqlStatementBuilder();
        mappingSql.append("INSERT INTO ECToLMCustomerEventMapping");
        mappingSql.append("(EnergyCompanyId, EventId)");
        mappingSql.values(yukonEnergyCompany.getEnergyCompanyId(), eventId);
        yukonJdbcTemplate.update(mappingSql);

        // Save event-specific data into db
        if (eventType.equals(CustomerEventType.HARDWARE)) {
            saveHardwareEvent(event);
        } else if (eventType.equals(CustomerEventType.THERMOSTAT_MANUAL) && event instanceof ThermostatManualEvent) {
            saveManualEvent((ThermostatManualEvent) event, yukonEnergyCompany);
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

        yukonJdbcTemplate.update(mappingSql.toString(),
                                  event.getEventId(),
                                  event.getThermostatId());
    }

    /**
     * Helper method to add a row to the LMThermostatManualEvent table
     * @param event - Manual event to add row for
     */
    private void saveManualEvent(ThermostatManualEvent event, YukonEnergyCompany yukonEnergyCompany) {

        Temperature previousHeatTemperature = event.getPreviousHeatTemperature();
        Temperature previousCoolTemperature = event.getPreviousCoolTemperature();
        boolean holdTemperature = event.isHoldTemperature();

        LiteStarsEnergyCompany liteStarsEnergyCompany = 
            starsDatabaseCache.getEnergyCompany(yukonEnergyCompany.getEnergyCompanyId());

        ThermostatMode mode = event.getMode();
        YukonListEntry modeListEntry = 
            liteStarsEnergyCompany.getYukonListEntry(mode.getDefinitionId());

        ThermostatFanState fanState = event.getFanState();
        YukonListEntry fanStateEntry = 
            liteStarsEnergyCompany.getYukonListEntry(fanState.getDefinitionId());

        Integer eventId = event.getEventId();
        Integer thermostatId = event.getThermostatId();
        
        // Insert row into LMThermostatManualEvent
        SqlStatementBuilder eventSql = new SqlStatementBuilder();
        eventSql.append("INSERT INTO LMThermostatManualEvent");
        eventSql.append("(EventId, InventoryId, HoldTemperature, OperationStateId, FanOperationId, PreviousCoolTemperature, PreviousHeatTemperature)");
        eventSql.values(eventId, thermostatId, YNBoolean.valueOf(holdTemperature), modeListEntry.getEntryID(), fanStateEntry.getEntryID(), 
                        (previousCoolTemperature == null) ? null : previousCoolTemperature.toFahrenheit().getValue(), 
                        (previousHeatTemperature == null) ? null : previousHeatTemperature.toFahrenheit().getValue());

        yukonJdbcTemplate.update(eventSql);

    }

    /**
     * Helper class to map a result set into a LiteLMThermostatManualEvent
     */
    private class ThermostatManualEventMapper implements ParameterizedRowMapper<ThermostatManualEvent> {

        private YukonEnergyCompany yukonEnergyCompany;

        public ThermostatManualEventMapper(YukonEnergyCompany yukonEnergyCompany) {
            this.yukonEnergyCompany = yukonEnergyCompany;
        }

        @Override
        public ThermostatManualEvent mapRow(ResultSet rs, int rowNum)
                throws SQLException {

            int id = rs.getInt("EventId");
            int inventoryId = rs.getInt("InventoryId");
            Temperature previousCoolTemp = Temperature.fromFahrenheit(rs.getDouble("PreviousCoolTemperature"));
            Temperature previousHeatTemp = Temperature.fromFahrenheit(rs.getDouble("PreviousHeatTemperature"));
            String holdTemp = rs.getString("HoldTemperature");
            Date date = rs.getTimestamp("EventDateTime");

            ThermostatManualEvent event = new ThermostatManualEvent();
            event.setEventId(id);
            event.setThermostatId(inventoryId);
            event.setPreviousCoolTemperature(previousCoolTemp);
            event.setPreviousHeatTemperature(previousHeatTemp);

            // A temp of -1 indicates this event was a 'run program' event. This
            // should really be handled with a column in the table or some other
            // more solid way
            if (previousCoolTemp.toFahrenheit().getValue() == -1 &&
                 previousHeatTemp.toFahrenheit().getValue() == -1 ) {
                event.setRunProgram(true);
            }

            boolean hold = ("y".equalsIgnoreCase(holdTemp)) ? true : false;
            event.setHoldTemperature(hold);

            LiteStarsEnergyCompany liteStarsEnergyCompany = 
                starsDatabaseCache.getEnergyCompany(yukonEnergyCompany.getEnergyCompanyId());
            
            // Convert the operation state entryid into a ThermostatMode enum
            // value
            int operationStateId = rs.getInt("OperationStateId");
            int modeDefinitionId = YukonListEntryHelper.getYukonDefinitionId(liteStarsEnergyCompany,
                                                                             YukonSelectionListDefs.YUK_LIST_NAME_THERMOSTAT_MODE,
                                                                             operationStateId);

            
            ThermostatMode mode = ThermostatMode.valueOf(modeDefinitionId);
            event.setMode(mode);

            // Convert the fan operation entryid into a ThermostatFanState enum
            // value
            int fanOperationId = rs.getInt("FanOperationId");
            int fanStateDefinitionId = YukonListEntryHelper.getYukonDefinitionId(liteStarsEnergyCompany,
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