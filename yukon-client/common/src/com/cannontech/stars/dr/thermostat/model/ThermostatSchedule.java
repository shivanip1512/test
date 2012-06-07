package com.cannontech.stars.dr.thermostat.model;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.util.CtiUtilities;

/**
 * Model object which represents a schedule for a thermostat.
 */
public class ThermostatSchedule {

    private Integer id;
    private String name = CtiUtilities.STRING_NONE;
    private HardwareType thermostatType;
    private Integer accountId;
    private Integer inventoryId;

    private ThermostatSeason season = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HardwareType getThermostatType() {
        return thermostatType;
    }

    public void setThermostatType(HardwareType thermostatType) {
        this.thermostatType = thermostatType;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public ThermostatSeason getSeason() {
        return season;
    }

    public void setSeason(ThermostatSeason season) {
        this.season = season;
    }
    
    public ThermostatSchedule getCopy() {
    	
    	ThermostatSchedule schedule = new ThermostatSchedule();
    	schedule.setId(this.getId());
    	schedule.setName(this.getName());
    	schedule.setAccountId(this.accountId);
    	schedule.setInventoryId(this.getInventoryId());
    	schedule.setThermostatType(this.getThermostatType());
    	
    	schedule.setSeason(this.getSeason().getCopy());
    	
    	return schedule;
    }

}
