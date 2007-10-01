package com.cannontech.common.device.profilePeak.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.cannontech.common.util.TimeUtil;

public class ProfilePeakResult {

    private int deviceId = -1;
    private String range = null;
    private String peakDate = "No Data available";
    private String usage = "No Data available";
    private String demand = "No Data available";
    private String averageDailyUsage = "No Data available";
    private String totalUsage = "No Data available";

    private boolean noData = false;
    private String runDate = null;
    private String startDate = null;
    private String stopDate = null;
    private long days = -1;

    private ProfilePeakResultType resultType = null;

    private String error = null;
	private String actualStopDate = null;

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getRunDate() {
        return runDate;
    }

    public void setRunDate(String runDate) {
        this.runDate = runDate;
    }

    public String getAverageDailyUsage() {
        return averageDailyUsage;
    }

    public void setAverageDailyUsage(String averageDailyUsage) {
        this.averageDailyUsage = averageDailyUsage;
    }

    public String getDemand() {
        return demand;
    }

    public void setDemand(String demand) {
        this.demand = demand;
    }

    public String getPeakDate() {
        return removeTimeSingle(peakDate);
    }

    public void setPeakDate(String peakDate) {
        this.peakDate = peakDate;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getTotalUsage() {
        return totalUsage;
    }

    public void setTotalUsage(String totalUsage) {
        this.totalUsage = totalUsage;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public boolean isNoData() {
        return noData;
    }

    public void setNoData(boolean noData) {
        this.noData = noData;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStopDate() {
        return stopDate;
    }

	public void setStopDate(String stopDate) {
		this.stopDate = stopDate;

		this.setActualStopDate(stopDate);
	}

	public String getActualStopDate() {
		return actualStopDate ;
	}
	
	public void setActualStopDate(String stopDate) {
		
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date actualStopDate = null;
		try {
			Date date = format.parse(stopDate);
			actualStopDate = TimeUtil.addUnit(date, Calendar.DAY_OF_MONTH, -1);
		} catch (ParseException e) {
			// do nothing - tried date parse
		}
		
		this.actualStopDate = format.format(actualStopDate);
	}

    public ProfilePeakResultType getResultType() {
        return resultType;
    }

    public void setResultType(ProfilePeakResultType resultType) {
        this.resultType = resultType;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getDays() {
        return days;
    }

    public void setDays(long days) {
        this.days = days;
    }

    // This function is used to truncate the time component off of a 
    // single timestamp object that is kept in string formate
    private String removeTimeSingle(String dateStr){
    	String modDateStr = "";
    	String[] datePieces = null;
    	datePieces = dateStr.split(" ");
    	modDateStr = datePieces[0];
    	return modDateStr;
    }
    
}
