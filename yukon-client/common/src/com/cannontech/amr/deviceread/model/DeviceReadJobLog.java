package com.cannontech.amr.deviceread.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.GregorianCalendar;

import com.cannontech.amr.deviceread.dao.DeviceReadJobLogDao;
import com.cannontech.amr.deviceread.dao.impl.DeviceReadJobLogDaoImpl;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

public class DeviceReadJobLog {
	public static String TABLE_NAME = "DeviceReadJobLog";
	
	private Integer deviceReadJobLogID;
	private Integer scheduleID;
	private String scheduleName;	//not part of the db table
	private GregorianCalendar startTime;
	private GregorianCalendar stopTime;

	public DeviceReadJobLog(Integer deviceReadJobLogID, Integer scheduleID, String scheduleName, GregorianCalendar startTime, GregorianCalendar stopTime) {
		super();
		this.deviceReadJobLogID = deviceReadJobLogID;
		this.scheduleID = scheduleID;
		this.scheduleName = scheduleName;
		this.startTime = startTime;
		this.stopTime = stopTime;
	}
	
	public Integer getDeviceReadJobLogID() {
		return deviceReadJobLogID;
	}
	public void setDeviceReadJobLogID(Integer deviceReadJobLogID) {
		this.deviceReadJobLogID = deviceReadJobLogID;
	}
	public Integer getScheduleID() {
		return scheduleID;
	}
	public void setScheduleID(Integer scheduleID) {
		this.scheduleID = scheduleID;
	}
	public String getScheduleName() {
		return scheduleName;
	}

	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	public GregorianCalendar getStartTime() {
		return startTime;
	}
	public void setStartTime(GregorianCalendar startTime) {
		this.startTime = startTime;
	}
	public GregorianCalendar getStopTime() {
		return stopTime;
	}
	public void setStopTime(GregorianCalendar stopTime) {
		this.stopTime = stopTime;
	}
	
	public static DeviceReadJobLog createDeviceReadJobLog( ResultSet rset) throws SQLException {

		Integer deviceReadJobLogID = rset.getInt("DeviceReadJobLogID");
		Integer scheduleID = rset.getInt("ScheduleID");
		String scheduleName = rset.getString("PaoName");
		Timestamp startTimeTS = rset.getTimestamp("StartTime");
		GregorianCalendar startTimeCal = new GregorianCalendar();
		startTimeCal.setTimeInMillis(startTimeTS.getTime());
        Timestamp stopTimeTS = rset.getTimestamp("StopTime");
        GregorianCalendar stopTimeCal = new GregorianCalendar();
		stopTimeCal.setTimeInMillis(stopTimeTS.getTime());

        DeviceReadJobLog deviceReadJobLog = 
        	new DeviceReadJobLog(deviceReadJobLogID, scheduleID, scheduleName, startTimeCal, stopTimeCal); 
        											
        return deviceReadJobLog;
    }
	@Override
	public String toString() {
		DeviceReadJobLogDao devReadJobLogDao = (DeviceReadJobLogDao) YukonSpringHook.getBean("deviceReadJobLogDao");
		return devReadJobLogDao.getScheduleDisplayName(this);
	}
}
