package com.cannontech.message.macs.message;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableSchedule extends com.cannontech.message.util.DefineCollectableMessage
{
	//The roguewave class id
	private static int classId = 140;

/**
 * DefineCollectableSchedule constructor comment.
 */
public DefineCollectableSchedule() {
	super();
}
/**
 * This method is called from CollectableStreamer to create a new instance
 * of Schedule.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new Schedule();
}
/**
 * getComparator method comment.
 */
public com.roguewave.tools.v2_0.Comparator getComparator() {
	return new Comparator() {
	  public int compare(Object x, Object y) {
	return (int) (((Schedule)x).getId() - ((Schedule)y).getId());
	  }
	};
	
}
/**
 * getCxxClassId method comment.
 */
public int getCxxClassId() {
	return this.classId;
}
/**
 * getCxxStringId method comment.
 */
public String getCxxStringId() {
	return DefineCollectable.NO_STRINGID;
}
/**
 * getJavaClass method comment.
 */
public Class getJavaClass() {
	return Schedule.class;
}
/**
 *  Changed scheduleId and holidayId to extract long instead of int.
 *  Added targetSelect, startCommand, stopCommand, repeatInterval
 *  Need to figure out a way to determine the targets type and ID still
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.restoreGuts( obj, vstr, polystr );
	Schedule schedule = (Schedule) obj;

	int scheduleId = (int) vstr.extractLong(); // watch cast!
	String scheduleName = (String) vstr.restoreObject( SimpleMappings.CString );
	String categoryName = (String) vstr.restoreObject( SimpleMappings.CString );
	String type = (String) vstr.restoreObject( SimpleMappings.CString );
	int holidayId = (int) vstr.extractLong(); // watch cast !
	String scriptFileName = (String) vstr.restoreObject( SimpleMappings.CString );
	String currentState = (String) vstr.restoreObject( SimpleMappings.CString );
	String startPolicy = (String) vstr.restoreObject( SimpleMappings.CString );
	String stopPolicy = (String) vstr.restoreObject( SimpleMappings.CString );	
	java.util.Date lastRunTime = (java.util.Date) vstr.restoreObject( SimpleMappings.Time );
	String lastRunStatus = (String) vstr.restoreObject( SimpleMappings.CString );
	int startDay = vstr.extractInt();
	int startMonth = vstr.extractInt();
	int startYear = vstr.extractInt();
	String startTime = (String) vstr.restoreObject( SimpleMappings.CString );
	String stopTime = (String) vstr.restoreObject( SimpleMappings.CString );
	String validWeekDays = (String) vstr.restoreObject( SimpleMappings.CString );
	int duration = vstr.extractInt();
	java.util.Date manualStartTime = (java.util.Date) vstr.restoreObject( SimpleMappings.Time );
	java.util.Date manualStopTime = (java.util.Date) vstr.restoreObject( SimpleMappings.Time );
	String targetSelect = (String) vstr.restoreObject( SimpleMappings.CString );
	String startCommand = (String) vstr.restoreObject( SimpleMappings.CString );
	String stopCommand  = (String) vstr.restoreObject( SimpleMappings.CString );
	int repeatInterval  = vstr.extractInt();
	java.util.Date nextRunTime = (java.util.Date) vstr.restoreObject( SimpleMappings.Time );
	java.util.Date nextStopTime = (java.util.Date) vstr.restoreObject( SimpleMappings.Time );	
	int template = vstr.extractInt();
	
	schedule.setId( scheduleId );
	schedule.setScheduleName( scheduleName );
	schedule.setCategoryName( categoryName );
	schedule.setType( type );
	schedule.setHolidayScheduleId( holidayId );
	schedule.setScriptFileName( scriptFileName );
	schedule.setCurrentState( currentState );
	schedule.setStartPolicy( startPolicy );
	schedule.setStopPolicy( stopPolicy );
	schedule.setLastRunTime( lastRunTime );
	schedule.setLastRunStatus( lastRunStatus );
	schedule.setStartDay( startDay );
	schedule.setStartMonth( startMonth );
	schedule.setStartYear( startYear );
	schedule.setStartTime( startTime );
	schedule.setStopTime( stopTime );
	schedule.setValidWeekDays( validWeekDays );
	schedule.setDuration( duration );
	schedule.setManualStartTime( manualStartTime );
	schedule.setManualStopTime( manualStopTime );
	schedule.setNextRunTime( nextRunTime );
	schedule.setNextStopTime( nextStopTime );
	schedule.setTargetSelect( targetSelect );
	schedule.setStartCommand( startCommand );
	schedule.setStopCommand( stopCommand );
	schedule.setRepeatInterval( repeatInterval );
	schedule.setTemplateType(template);
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.saveGuts( obj, vstr, polystr );
	Schedule schedule = (Schedule) obj;

	vstr.insertLong( schedule.getId() ); //changed to long
	
	vstr.saveObject( schedule.getScheduleName(), SimpleMappings.CString );
	vstr.saveObject( schedule.getCategoryName(), SimpleMappings.CString );
	vstr.saveObject( schedule.getType(), SimpleMappings.CString );
	vstr.insertLong( schedule.getHolidayScheduleId() ); //changed to long	
	vstr.saveObject( schedule.getScriptFileName(), SimpleMappings.CString );
	vstr.saveObject( schedule.getCurrentState(), SimpleMappings.CString );
	vstr.saveObject( schedule.getStartPolicy(), SimpleMappings.CString );
	vstr.saveObject( schedule.getStopPolicy(), SimpleMappings.CString );
	vstr.saveObject( schedule.getLastRunTime(), SimpleMappings.Time );
	vstr.saveObject( schedule.getLastRunStatus(), SimpleMappings.CString );
	vstr.insertInt( schedule.getStartDay() );
	vstr.insertInt( schedule.getStartMonth() );
	vstr.insertInt( schedule.getStartYear() );
	vstr.saveObject( schedule.getStartTime(), SimpleMappings.CString );
	vstr.saveObject( schedule.getStopTime(), SimpleMappings.CString );
	vstr.saveObject( schedule.getValidWeekDays(), SimpleMappings.CString );
	vstr.insertInt( schedule.getDuration() );
	vstr.saveObject( schedule.getManualStartTime(), SimpleMappings.Time );
	vstr.saveObject( schedule.getManualStopTime(), SimpleMappings.Time );
	vstr.saveObject( schedule.getTargetSelect(), SimpleMappings.CString );
	vstr.saveObject( schedule.getStartCommand(), SimpleMappings.CString );
	vstr.saveObject( schedule.getStopCommand(), SimpleMappings.CString );
	vstr.insertInt( schedule.getRepeatInterval() );
	vstr.insertInt( schedule.getTemplateType() );
	// no need to send the server the next run times
}
}
