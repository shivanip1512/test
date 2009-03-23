package com.cannontech.stars.dr.thermostat.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cannontech.stars.util.StarsMsgUtils;

/**
 * Model object which represents a schedule season for a thermostat
 */
public class ThermostatSeason {

    private Integer id;
    private Integer scheduleId;
    private Integer webConfigurationId;
    private Date coolStartDate;
    private Date heatStartDate;
    // This is a temporary workaround which is intended to be replaced with
    // more majors changes (see YUK-7069).
    private final static Comparator<ThermostatSeasonEntry> entrySorter = new Comparator<ThermostatSeasonEntry>() {
		@Override
		public int compare(ThermostatSeasonEntry entry1,
				ThermostatSeasonEntry entry2) {
			if (entry1 == entry2)
				return 0;
			if (entry1 == null)
				return -1;
			if (entry2 == null)
				return 1;
			return entry1.getStartTime() == null ? -1 : entry1.getStartTime()
					.compareTo(entry2.getStartTime());
		}
	};

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

    public Date getCoolStartDate() {
        return coolStartDate;
    }

    public void setCoolStartDate(Date coolStartDate) {
        this.coolStartDate = coolStartDate;
    }
    
    public Date getHeatStartDate() {
		return heatStartDate;
	}
    
    public void setHeatStartDate(Date heatStartDate) {
		this.heatStartDate = heatStartDate;
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

        if (mode.equals(ThermostatMode.COOL)) {
            this.webConfigurationId = StarsMsgUtils.YUK_WEB_CONFIG_ID_COOL;
        } else if (mode.equals(ThermostatMode.HEAT)) {
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

    // This is a temporary workaround which is intended to be replaced with
    // more majors changes (see YUK-7069).
    public String getMode() {
    	if (seasonEntryMap.size() == 0) {
    		return "ALL";
    	}

		List<ThermostatSeasonEntry> weekdayEntries =
			seasonEntryMap.get(TimeOfWeek.WEEKDAY);
		List<ThermostatSeasonEntry> saturdayEntries =
			seasonEntryMap.get(TimeOfWeek.SATURDAY);
		List<ThermostatSeasonEntry> sundayEntries =
			seasonEntryMap.get(TimeOfWeek.SUNDAY);
		String junk = StringUtils.join(seasonEntryMap.keySet(), ':');
		System.out.print(junk);

		if (weekdayEntries == null) {
			weekdayEntries = seasonEntryMap.get(TimeOfWeek.MONDAY);
		}
		if (weekdayEntries != null) {
			weekdayEntries = new ArrayList<ThermostatSeasonEntry>(weekdayEntries);
			Collections.sort(weekdayEntries, entrySorter);
		}
		if (saturdayEntries != null) {
			saturdayEntries = new ArrayList<ThermostatSeasonEntry>(saturdayEntries);
			Collections.sort(saturdayEntries, entrySorter);
		}
		if (sundayEntries != null) {
			sundayEntries = new ArrayList<ThermostatSeasonEntry>(sundayEntries);
			Collections.sort(sundayEntries, entrySorter);
		}

		if (equalsIgnoringTimeOfWeek(saturdayEntries, sundayEntries)) {
			if (equalsIgnoringTimeOfWeek(saturdayEntries, weekdayEntries)) {
				return "ALL";
			}
			return "WEEKDAY_WEEKEND";
		}

		return "WEEKDAY_SAT_SUN";
    }

    // This is a temporary workaround which is intended to be replaced with
    // more majors changes (see YUK-7069).
    private boolean equalsIgnoringTimeOfWeek(List<ThermostatSeasonEntry> list1,
			List<ThermostatSeasonEntry> list2) {
    	if (list1 == list2) return true;
    	if (list1 == null || list2 == null) return false;
    	Iterator<ThermostatSeasonEntry> list2Iter = list2.iterator();
    	for (ThermostatSeasonEntry entry1 : list1) {
    		if (!list2Iter.hasNext() || !entry1.equalsIgnoringTimeOfWeek(list2Iter.next())) {
    			return false;
    		}
    	}
    	return true;
	}
}
