package com.cannontech.web.history;

/**
 * Insert the type's description here.
 * Creation date: (7/13/2001 11:12:35 AM)
 * @author: 
 */
public class HCurtailProgramActivity {
	private java.sql.Statement stmt = null;
	private long deviceId = 0;
	private long curtailReferenceId = 0;
	private java.util.Date actionDateTime = null;
	private java.util.Date notificationDateTime = null;
	private java.util.Date curtailmentStartTime = null;
	private java.util.Date curtailmentStopTime = null;
	private String runStatus = null;
	private String additionalInfo = null;
/**
 * HCurtailProgramActivity constructor comment.
 */
public HCurtailProgramActivity() {
	super();
}
	public java.util.Date getActionDateTime() {
		return actionDateTime;
	}
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public java.util.Date getCurtailmentStartTime() {
		return curtailmentStartTime;
	}
	public java.util.Date getCurtailmentStopTime() {
		return curtailmentStopTime;
	}
/**
 * Insert the method's description here.
 * Creation date: (7/13/2001 1:16:08 PM)
 * @return com.cannontech.web.history.HCurtailProgram
 */
public HCurtailProgram getCurtailProgram() {
	String queryStr = "SELECT YUKONPAOBJECT.PAONAME FROM YUKONPAOBJECT WHERE PAOBJECTID = " + deviceId;

	try {
		java.sql.ResultSet rset = stmt.executeQuery(queryStr);

		HCurtailProgram ret = new HCurtailProgram();
		if (rset.next()) {
			ret.setStatement(stmt);
			ret.setDeviceId(deviceId);
			ret.setProgramName( rset.getString(1) );
			return ret;
		}
	}
	catch (java.sql.SQLException se) {
		com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
	}
	
	return null;
}
	public long getCurtailReferenceId() {
		return curtailReferenceId;
	}
	public long getDeviceId() {
		return deviceId;
	}
/**
 * Insert the method's description here.
 * Creation date: (7/13/2001 1:41:28 PM)
 * @return int
 */
public int getDuration() {
	if (curtailmentStartTime == null || curtailmentStopTime == null)
		return 0;

	long startTimeMillis = curtailmentStartTime.getTime();
	long stopTimeMillis = curtailmentStopTime.getTime();
	int duration = (int)((stopTimeMillis - startTimeMillis) / (1000 * 60 * 60));

	return duration;
}
	public java.util.Date getNotificationDateTime() {
		return notificationDateTime;
	}
/**
 * Creation date: (8/9/2001 11:54:36 AM)
 * @return int
 */
public int getProgramState() {
	String queryStr = "SELECT PROGRAMSTATE FROM DYNAMICLMPROGRAM WHERE DEVICEID = " + deviceId;
	int programState = -1;
	
	try {
		java.sql.ResultSet rset = stmt.executeQuery(queryStr);

		if (rset.next())
			programState = rset.getInt(1);
	}
	catch (java.sql.SQLException se) {
		com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
	}
		
	return programState;
}
	public String getRunStatus() {
		return runStatus;
	}
	public void setActionDateTime(java.util.Date actionDateTime) {
		this.actionDateTime = actionDateTime;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public void setCurtailmentStartTime(java.util.Date curtailmentStartTime) {
		this.curtailmentStartTime = curtailmentStartTime;
	}
	public void setCurtailmentStopTime(java.util.Date curtailmentStopTime) {
		this.curtailmentStopTime = curtailmentStopTime;
	}
	public void setCurtailReferenceId(long curtailReferenceId) {
		this.curtailReferenceId = curtailReferenceId;
	}
	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}
	public void setNotificationDateTime(java.util.Date notificationDateTime) {
		this.notificationDateTime = notificationDateTime;
	}
	public void setRunStatus(String runStatus) {
		this.runStatus = runStatus;
	}
	public void setStatement(java.sql.Statement stmt) {
		this.stmt = stmt;
	}
}
