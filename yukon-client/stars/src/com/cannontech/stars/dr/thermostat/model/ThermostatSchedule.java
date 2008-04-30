package com.cannontech.stars.dr.thermostat.model;

import java.util.HashMap;
import java.util.Map;

import com.cannontech.stars.dr.hardware.model.HardwareType;

/**
 * Model object which represents a schedule for a thermostat
 */
public class ThermostatSchedule {

    private Integer id;
    private String name;
    private HardwareType thermostatType;
    private Integer accountId;
    private Integer inventoryId;

    private Map<ThermostatMode, ThermostatSeason> seasonMap = new HashMap<ThermostatMode, ThermostatSeason>();

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

    public Map<ThermostatMode, ThermostatSeason> getSeasonMap() {
        return seasonMap;
    }

    public void setSeasonMap(Map<ThermostatMode, ThermostatSeason> seasonMap) {
        this.seasonMap = seasonMap;
    }

    public void addSeason(ThermostatSeason season) {
        ThermostatMode thermostatMode = season.getThermostatMode();
        this.seasonMap.put(thermostatMode, season);
    }

    public ThermostatSeason getSeason(ThermostatMode mode) {
        return this.seasonMap.get(mode);
    }

}
