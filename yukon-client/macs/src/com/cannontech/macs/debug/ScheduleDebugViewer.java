package com.cannontech.macs.debug;

/**
 * Insert the type's description here.
 * Creation date: (3/5/2001 10:36:05 AM)
 * @author: 
 */
import com.cannontech.messaging.message.macs.ScheduleMessage;

public class ScheduleDebugViewer extends com.cannontech.debug.gui.AbstractListDataViewer 
{
	private ScheduleMessage schedule = null;
/**
 * ScheduleDebugViewer constructor comment.
 */
public ScheduleDebugViewer() {
	super();
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Dialog
 */
public ScheduleDebugViewer(java.awt.Dialog owner) {
	super(owner);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 */
public ScheduleDebugViewer(java.awt.Dialog owner, String title) {
	super(owner, title);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 * @param modal boolean
 */
public ScheduleDebugViewer(java.awt.Dialog owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Dialog
 * @param modal boolean
 */
public ScheduleDebugViewer(java.awt.Dialog owner, boolean modal) {
	super(owner, modal);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Frame
 */
public ScheduleDebugViewer(java.awt.Frame owner) {
	super(owner);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public ScheduleDebugViewer(java.awt.Frame owner, String title) {
	super(owner, title);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 * @param modal boolean
 */
public ScheduleDebugViewer(java.awt.Frame owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public ScheduleDebugViewer(java.awt.Frame owner, boolean modal) {
	super(owner, modal);
}
/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 10:36:05 AM)
 */
public Object getValue() {
	return schedule;
}
/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 10:36:05 AM)
 */
public void setValue(Object o) 
{
	schedule = (ScheduleMessage)o;
	java.util.Vector data = new java.util.Vector(30);

	try
	{
		data.addElement("Sched Name : " + schedule.getScheduleName());
		data.addElement("Cat   Name : " + schedule.getCategoryName());
		data.addElement("State      : " + schedule.getCurrentState());
		data.addElement("Duration   : " + schedule.getDuration() );
		data.addElement("Holiday ID : " + schedule.getHolidayScheduleId());
		data.addElement("Sched ID   : " + schedule.getId());
		data.addElement("Last Run   : " + schedule.getLastRunStatus() );		
		data.addElement("Manul Start: " + schedule.getManualStartTime() );
		data.addElement("Manul Stop : " + schedule.getManualStopTime() );
		data.addElement("Next Start : " + schedule.getNextRunTime() );
		data.addElement("Next Stop  : " + schedule.getNextStopTime() );
		data.addElement("Repeat Int : " + schedule.getRepeatInterval() );
		data.addElement("Scrpt Name : " + schedule.getScriptFileName() );		
		data.addElement("Start Cmd  : " + schedule.getStartCommand() );
		data.addElement("Start Day  : " + schedule.getStartDay() );
		data.addElement("Start Mnth : " + schedule.getStartMonth() );
		data.addElement("Start Plcy : " + schedule.getStartPolicy() );
		data.addElement("Start Time : " + schedule.getStartTime() );
		data.addElement("Start Year : " + schedule.getStartYear() );		
		data.addElement("Stop Cmd   : " + schedule.getStopCommand() );
		data.addElement("Stop Plcy  : " + schedule.getStopPolicy() );
		data.addElement("Stop Time  : " + schedule.getStopTime() );
		data.addElement("Target     : " + schedule.getTargetPAObjectId());
		data.addElement("Valid WkDys: " + schedule.getValidWeekDays() );		

		data.addElement("");
		data.addElement("NON-PERSISTANT DATA");
		data.addElement("------------");
		data.addElement("DScrpt Name: " + schedule.getNonPersistantData().getScript().getFileName() );
		data.addElement("DScrpt Text: " + schedule.getNonPersistantData().getScript().getFileContents() );

		if( schedule.getNonPersistantData().getCategories() != null )
		{
			StringBuffer str = new StringBuffer();
			while( schedule.getNonPersistantData().getCategories().hasMoreElements() )
			{
				str.append( schedule.getNonPersistantData().getCategories().nextElement().toString() );
				str.append(",");
			}
			data.addElement("DCategries : " + str );
		}
			
	}
	catch(Throwable t )  // Catch ALL things and just print them out
	{
		com.cannontech.clientutils.CTILogger.info("*** Throwable caught in : " + this.getClass() + " : " + t.getMessage() );
	}
		
	getJListInfo().setListData(data);
}
}
