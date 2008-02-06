package com.cannontech.database.db.device.lm;

import java.util.Date;

import com.cannontech.user.UserUtils;

public class LMControlHistory {
    private int lmCtrlHistID;
    private int paObjectID;
    private Date startDateTime;
    private int soeTag;
    private int controlDuration;
    private String controlType;
    private int currentDailyTime;
    private int currentMonthlyTime;
    private int currentSeasonalTime;
    private int currentAnnualTime;
    private String activeRestore;
    private double reductionValue;
    private Date stopDateTime;
    
    public LMControlHistory() { 
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final LMControlHistory other = (LMControlHistory) obj;
        if (lmCtrlHistID != other.lmCtrlHistID)
            return false;
        return true;
    }

    public String getActiveRestore() {
        return activeRestore;
    }

    public void setActiveRestore(String activeRestore) {
        this.activeRestore = activeRestore;
    }

    public int getControlDuration() {
        return controlDuration;
    }

    public void setControlDuration(int controlDuration) {
        this.controlDuration = controlDuration;
    }

    public String getControlType() {
        return controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
    }

    public int getCurrentAnnualTime() {
        return currentAnnualTime;
    }

    public void setCurrentAnnualTime(int currentAnnualTime) {
        this.currentAnnualTime = currentAnnualTime;
    }

    public int getCurrentDailyTime() {
        return currentDailyTime;
    }

    public void setCurrentDailyTime(int currentDailyTime) {
        this.currentDailyTime = currentDailyTime;
    }

    public int getCurrentMonthlyTime() {
        return currentMonthlyTime;
    }

    public void setCurrentMonthlyTime(int currentMonthlyTime) {
        this.currentMonthlyTime = currentMonthlyTime;
    }

    public int getCurrentSeasonalTime() {
        return currentSeasonalTime;
    }

    public void setCurrentSeasonalTime(int currentSeasonalTime) {
        this.currentSeasonalTime = currentSeasonalTime;
    }

    public int getLmCtrlHistID() {
        return lmCtrlHistID;
    }

    public void setLmCtrlHistID(int lmCtrlHistID) {
        this.lmCtrlHistID = lmCtrlHistID;
    }

    public int getPaObjectID() {
        return paObjectID;
    }

    public void setPaObjectID(int paObjectID) {
        this.paObjectID = paObjectID;
    }

    public double getReductionValue() {
        return reductionValue;
    }

    public void setReductionValue(double reductionValue) {
        this.reductionValue = reductionValue;
    }

    public int getSoeTag() {
        return soeTag;
    }

    public void setSoeTag(int soeTag) {
        this.soeTag = soeTag;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getStopDateTime() {
        return stopDateTime;
    }

    public void setStopDateTime(Date stopDateTime) {
        this.stopDateTime = stopDateTime;
    }

}
