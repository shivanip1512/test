package com.cannontech.stars.dr.thermostat.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.YukonEnergyCompany;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.core.service.EnergyCompanyService;
import com.cannontech.stars.dr.hardware.model.CustomerAction;
import com.cannontech.stars.dr.hardware.model.CustomerEventType;
import com.cannontech.stars.dr.thermostat.dao.CustomerEventDao;
import com.cannontech.stars.dr.thermostat.model.CustomerThermostatEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;

/**
 * Implementation class for CustomerEventDao
 */
public class CustomerEventDaoImpl implements CustomerEventDao {

    private EnergyCompanyService energyCompanyService;
    private NextValueHelper nextValueHelper;
    private StarsDatabaseCache starsDatabaseCache;
    private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public ThermostatManualEvent getLastManualEvent(int inventoryId) {

        YukonEnergyCompany yukonEnergyCompany = energyCompanyService.getEnergyCompanyByInventoryId(inventoryId);

        // Query to get the row from LMThermostatManualEvent where the row's
        // event id matches the event id from the row in LMCustomerEventBase
        // where that row's EventDateTime is the most recent for a given
        // inventoryid.
        // In english: get the LMThermostatManualEvent row for the most recent
        // event for the thermostat
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT event.*, base.EventDateTime");
        sql.append("FROM LMThermostatManualEvent event, LMCustomerEventBase base");
        sql.append("WHERE event.EventId = base.EventId");
        sql.append("  AND event.InventoryId").eq(inventoryId);
        sql.append("ORDER BY base.EventDateTime DESC");
        
        ThermostatManualEvent manualEvent = new ThermostatManualEvent();
        List<ThermostatManualEvent> eventList = 
            yukonJdbcTemplate.queryForLimitedResults(sql,  new ThermostatManualEventMapper(yukonEnergyCompany), 1);

        // If any events are found, use the first one which is most recent
    	if(eventList.size() > 0) {
    		manualEvent = eventList.get(0);
    	}
    	
        return manualEvent;
    }

    @Override
    @Transactional
    public void save(CustomerThermostatEvent event) {
        YukonEnergyCompany yukonEnergyCompany = 
            energyCompanyService.getEnergyCompanyByInventoryId(event.getThermostatId());
        
        // Get next eventid
        int eventId = nextValueHelper.getNextValue("LMCustomerEventBase");
        event.setEventId(eventId);

        // Get current time
        Date date = new Date();
        event.setDate(date);

        
        LiteStarsEnergyCompany liteStarsEnergyCompany = 
            starsDatabaseCache.getEnergyCompany(yukonEnergyCompany.getEnergyCompanyId());
        
        CustomerEventType eventType = event.getEventType();
        YukonListEntry eventTypeListEntry = 
            liteStarsEnergyCompany.getYukonListEntry(eventType.getDefinitionId());

        CustomerAction action = event.getAction();
        YukonListEntry customerActionListEntry = 
            liteStarsEnergyCompany.getYukonListEntry(action.getDefinitionId());

        String notes = event.getNotes();
        String authorizedBy = event.getAuthorizedBy();

        // Insert row into LMCustomerEventBase
        SqlStatementBuilder baseSql = new SqlStatementBuilder();
        baseSql.append("INSERT INTO LMCustomerEventBase");
        baseSql.append("(EventId, EventTypeId, ActionId, EventDateTime, Notes, AuthorizedBy)");
        baseSql.values(eventId, eventTypeListEntry.getEntryID(), customerActionListEntry.getEntryID(), date, notes, authorizedBy);
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

        Integer previousTemperature = event.getPreviousTemperature();
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
        String holdTempatureStr = (holdTemperature) ? "Y" : "N";
        
        // Insert row into LMThermostatManualEvent
        SqlStatementBuilder eventSql = new SqlStatementBuilder();
        eventSql.append("INSERT INTO LMThermostatManualEvent");
        eventSql.append("(EventId, InventoryId, PreviousTemperature, HoldTemperature, OperationStateId, FanOperationId)");
        eventSql.values(eventId, thermostatId, previousTemperature, holdTempatureStr,
                        modeListEntry.getEntryID(), fanStateEntry.getEntryID());

        yukonJdbcTemplate.update(eventSql);

    }

    /**
     * Helper class to map a result set into a LiteLMThermostatManualEvent
     */
    private class ThermostatManualEventMapper implements
            ParameterizedRowMapper<ThermostatManualEvent> {

        private YukonEnergyCompany yukonEnergyCompany;

        public ThermostatManualEventMapper(YukonEnergyCompany yukonEnergyCompany) {
            this.yukonEnergyCompany = yukonEnergyCompany;
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

            LiteStarsEnergyCompany liteStarsEnergyCompany = 
                starsDatabaseCache.getEnergyCompany(yukonEnergyCompany.getEnergyCompanyId());
            
            // Convert the operation state entryid into a ThermostatMode enum
            // value
            int operationStateId = rs.getInt("OperationStateId");
            YukonListEntry operationStateListEntry = liteStarsEnergyCompany.getYukonListEntry(operationStateId);

            int modeDefinitionId = operationStateListEntry.getYukonDefID();
            ThermostatMode mode = ThermostatMode.valueOf(modeDefinitionId);
            event.setMode(mode);

            // Convert the fan operation entryid into a ThermostatFanState enum
            // value
            int fanOperationId = rs.getInt("FanOperationId");
            YukonListEntry fanOperationListEntry = liteStarsEnergyCompany.getYukonListEntry(fanOperationId);
            int fanStateDefinitionId = fanOperationListEntry.getYukonDefID();
            ThermostatFanState fanState = ThermostatFanState.valueOf(fanStateDefinitionId);
            event.setFanState(fanState);

            event.setDate(date);

            event.setEventType(CustomerEventType.THERMOSTAT_MANUAL);
            event.setAction(CustomerAction.MANUAL_OPTION);

            return event;
        }
    }

    // DI Setters
    @Autowired
    public void setEnergyCompanyService(EnergyCompanyService energyCompanyService) {
        this.energyCompanyService = energyCompanyService;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
}
