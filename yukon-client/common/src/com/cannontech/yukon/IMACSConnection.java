package com.cannontech.yukon;

import com.cannontech.message.macs.message.Schedule;
import com.cannontech.message.util.Message;

/**
 * Interface for connections to metering and control server (MACS)
 * @author alauinger
 */
public interface IMACSConnection extends IServerConnection 
{
	public Schedule[] getCategories( String category );

	public java.util.Hashtable getCategoryNames(); 

	public Schedule[] retrieveSchedules();

	public void sendCreateSchedule(Schedule sched) throws java.io.IOException; 

	public void sendDeleteSchedule(int scheduleID) throws java.io.IOException;

	public void sendEnableDisableSchedule(Schedule sched) throws java.io.IOException;

	public void sendRetrieveAllSchedules() throws java.io.IOException; 

	public void sendRetrieveOneSchedule( int schedId ) throws java.io.IOException; 

	public void sendRetrieveScriptText(String scriptFileName) throws java.io.IOException;

	public void sendScriptFile(com.cannontech.message.macs.message.ScriptFile file) throws java.io.IOException;

	public void sendStartStopSchedule(Schedule sched, java.util.Date startTime, java.util.Date stopTime, int command ) throws java.io.IOException;

	public void sendUpdateSchedule(Schedule sched ) throws java.io.IOException; 

	public void writeMsg( Message msg ) throws java.io.IOException; 
}
