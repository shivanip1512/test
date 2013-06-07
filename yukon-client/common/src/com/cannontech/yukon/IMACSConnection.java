package com.cannontech.yukon;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.macs.ScheduleMessage;

/**
 * Interface for connections to metering and control server (MACS)
 * @author alauinger
 */
public interface IMACSConnection extends IServerConnection 
{
	public ScheduleMessage[] getCategories( String category );

	public java.util.Hashtable getCategoryNames(); 

	public ScheduleMessage[] retrieveSchedules();
	
	public boolean isScheduleNameExists(String scheduleName, int scheduleId);
	
	public boolean isScriptFileNameExists(String scriptFileName, int scheduleId);

	public void sendCreateSchedule(ScheduleMessage sched) throws java.io.IOException; 

	public void sendDeleteSchedule(int scheduleID) throws java.io.IOException;

	public void sendEnableDisableSchedule(ScheduleMessage sched) throws java.io.IOException;

	public void sendRetrieveAllSchedules() throws java.io.IOException; 

	public void sendRetrieveOneSchedule( int schedId ) throws java.io.IOException; 

	public void sendRetrieveScriptText(String scriptFileName) throws java.io.IOException;

	public void sendScriptFile(com.cannontech.messaging.message.macs.ScriptFileMessage file) throws java.io.IOException;

	public void sendStartStopSchedule(ScheduleMessage sched, java.util.Date startTime, java.util.Date stopTime, int command ) throws java.io.IOException;

	public void sendUpdateSchedule(ScheduleMessage sched ) throws java.io.IOException; 

	public void writeMsg( BaseMessage msg ) throws java.io.IOException; 
}
