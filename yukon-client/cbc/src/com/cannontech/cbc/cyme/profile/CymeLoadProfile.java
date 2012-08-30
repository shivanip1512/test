package com.cannontech.cbc.cyme.profile;

import java.util.List;

import com.cannontech.cbc.cyme.profile.CymeType.GlobalUnit;
import com.cannontech.cbc.cyme.profile.CymeType.IntervalFormat;
import com.cannontech.cbc.cyme.profile.CymeType.ProfileType;
import com.cannontech.cbc.cyme.profile.CymeType.Season;
import com.cannontech.cbc.cyme.profile.CymeType.TimeInterval;
import com.cannontech.cbc.cyme.profile.CymeType.Unit;

public class CymeLoadProfile {

    private String id;
    private ProfileType profileType;
    private IntervalFormat intervalFormat;
    private TimeInterval timeInterval;
    private GlobalUnit globalUnit;
    private String networkId;
    private int year;
    private Season season;
    private String dayType;
    private Unit unit;
    private String phase;
    private List<Integer> values;
    
    public CymeLoadProfile() {
        
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProfileType getProfileType() {
        return profileType;
    }

    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }

    public IntervalFormat getIntervalFormat() {
        return intervalFormat;
    }

    public void setIntervalFormat(IntervalFormat intervalFormat) {
        this.intervalFormat = intervalFormat;
    }

    public TimeInterval getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(TimeInterval timeInterval) {
        this.timeInterval = timeInterval;
    }

    public GlobalUnit getGlobalUnit() {
        return globalUnit;
    }

    public void setGlobalUnit(GlobalUnit globalUnit) {
        this.globalUnit = globalUnit;
    }

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public String getDayType() {
        return dayType;
    }

    public void setDayType(String dayType) {
        this.dayType = dayType;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public List<Integer> getValues() {
        return values;
    }

    public void setValues(List<Integer> values) {
        this.values = values;
    }
}
