package com.cannontech.stars.dr.thermostat.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.stars.util.StarsMsgUtils;

/**
 * Model object which represents a schedule season for a thermostat
 */
public class ThermostatSeason {

    private Integer id;
    private Integer scheduleId;
    private Integer webConfigurationId;
    private Date startDate;

    private Map<TimeOfWeek, List<ThermostatSeasonEntry>> seasonEntryMap = new HashMap<TimeOfWeek, List<ThermostatSeasonEntry>>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Integer getWebConfigurationId() {
        return webConfigurationId;
    }

    public void setWebConfigurationId(Integer webConfigurationId) {
        this.webConfigurationId = webConfigurationId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Map<TimeOfWeek, List<ThermostatSeasonEntry>> getSeasonEntryMap() {
        return seasonEntryMap;
    }

    public void setSeasonEntryMap(
            Map<TimeOfWeek, List<ThermostatSeasonEntry>> seasonEntryMap) {
        this.seasonEntryMap = seasonEntryMap;
    }

    /**
     * Helper method to add a season entry to the season entry map
     * @param entry - Season entry to add
     */
    public void addSeasonEntry(ThermostatSeasonEntry entry) {

        TimeOfWeek timeOfWeek = entry.getTimeOfWeek();

        if (!this.seasonEntryMap.containsKey(timeOfWeek)) {
            this.seasonEntryMap.put(timeOfWeek,
                                    new ArrayList<ThermostatSeasonEntry>());
        }

        List<ThermostatSeasonEntry> entryList = this.seasonEntryMap.get(timeOfWeek);
        entryList.add(entry);

    }

    public ThermostatMode getThermostatMode() {

        if (this.webConfigurationId == StarsMsgUtils.YUK_WEB_CONFIG_ID_COOL) {
            return ThermostatMode.COOL;
        } else if (this.webConfigurationId == StarsMsgUtils.YUK_WEB_CONFIG_ID_HEAT) {
            return ThermostatMode.HEAT;
        } else {
            throw new IllegalStateException("Web configuration id: " + this.webConfigurationId + " is not a valid configuration id for a thermostat season.");
        }

    }

    public void setThermostatMode(ThermostatMode mode) {

        if (ThermostatMode.COOL == mode) {
            this.webConfigurationId = StarsMsgUtils.YUK_WEB_CONFIG_ID_COOL;
        } else if (ThermostatMode.HEAT == mode) {
            this.webConfigurationId = StarsMsgUtils.YUK_WEB_CONFIG_ID_HEAT;
        } else {
            throw new IllegalArgumentException("Thermostat mode: " + mode + " is not a valid mode for a thermostat season.");
        }

    }

    public List<ThermostatSeasonEntry> getSeasonEntries(TimeOfWeek timeOfWeek) {
        return this.seasonEntryMap.get(timeOfWeek);
    }

    public List<ThermostatSeasonEntry> getAllSeasonEntries() {

        List<ThermostatSeasonEntry> entryList = new ArrayList<ThermostatSeasonEntry>();

        for (List<ThermostatSeasonEntry> list : this.seasonEntryMap.values()) {
            entryList.addAll(list);
        }

        return entryList;
    }

}
