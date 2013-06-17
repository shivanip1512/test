package com.cannontech.database.data.holiday;

import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 10:35:21 AM)
 * @author: 
 */
public class HolidaySchedule extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	private com.cannontech.database.db.holiday.HolidaySchedule holidaySchedule = null;
	public final static int EMPTY_HOLIDAY_SCHEDULE_ID = 0;
	//objects of type com.cannontech.database.db.holiday.DateOfHoliday will only go in here
	private java.util.Vector dateOfHolidayVector = null;
/**
 * HolidaySchedule constructor comment.
 */
public HolidaySchedule() {
	super();
}
/**
 * HolidaySchedule constructor comment.
 */
public HolidaySchedule(Integer id)
{
	super();

	setHolidayScheduleID(id);
}
/**
 * HolidaySchedule constructor comment.
 */
public HolidaySchedule(Integer id, String name)
{
	super();

	setHolidayScheduleID(id);
	setHolidayScheduleName(name);
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 10:36:07 AM)
 */
public void add() throws java.sql.SQLException 
{
	getHolidaySchedule().add();
		
	for (int i = 0; i < getHolidayDatesVector().size(); i++)
		((com.cannontech.database.db.holiday.DateOfHoliday) getHolidayDatesVector().elementAt(i)).add();

}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 10:36:20 AM)
 */
public void delete() throws java.sql.SQLException 
{
	com.cannontech.database.db.holiday.DateOfHoliday.deleteAllDateHolidays(getHolidaySchedule().getHolidayScheduleId(), getDbConnection());

	getHolidaySchedule().delete();	
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2001 1:45:25 PM)
 * @return com.cannontech.message.dispatch.message.DBChangeMsg[]
 */
public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType)
{
	DBChangeMsg[] msgs = {
	        new DBChangeMsg(
	                        getHolidayScheduleID().intValue(),
	                        DBChangeMsg.CHANGE_HOLIDAY_SCHEDULE_DB,
	                        DBChangeMsg.CAT_HOLIDAY_SCHEDULE,
	                        DBChangeMsg.CAT_HOLIDAY_SCHEDULE,
	                        dbChangeType)
	};
	
	return msgs;
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 1:55:54 PM)
 * @return java.util.Vector
 */
public java.util.Vector getHolidayDatesVector()
{
	if (dateOfHolidayVector == null)
		dateOfHolidayVector = new java.util.Vector();

	return dateOfHolidayVector;
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 12:21:32 PM)
 * @return com.cannontech.database.data.holiday.HolidaySchedule
 */
private com.cannontech.database.db.holiday.HolidaySchedule getHolidaySchedule()
{
	if (holidaySchedule == null)
		holidaySchedule = new com.cannontech.database.db.holiday.HolidaySchedule();

	return holidaySchedule;
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 12:21:32 PM)
 * @return Integer
 */
public Integer getHolidayScheduleID()
{
	return getHolidaySchedule().getHolidayScheduleId();
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 12:21:32 PM)
 * @return String
 */
public String getHolidayScheduleName()
{
	return getHolidaySchedule().getHolidayScheduleName();
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 10:35:54 AM)
 */
public void retrieve() throws java.sql.SQLException 
{
	getHolidaySchedule().retrieve();

	java.util.Vector holidayDates = com.cannontech.database.db.holiday.DateOfHoliday.getAllHolidayDates(
				getHolidaySchedule().getHolidayScheduleId(), getDbConnection() );

	for( int i = 0; i < holidayDates.size(); i++ )
		getHolidayDatesVector().add( holidayDates.get(i) );


}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getHolidaySchedule().setDbConnection(conn);
	
	for( int i = 0; i < getHolidayDatesVector().size(); i++ )
		((com.cannontech.database.db.holiday.DateOfHoliday)getHolidayDatesVector().get(i)).setDbConnection(conn);

}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 12:21:32 PM)
 * @return com.cannontech.database.data.holiday.HolidaySchedule
 */
public void setHolidayScheduleID( Integer newID )
{
	getHolidaySchedule().setHolidayScheduleId( newID );
	
	for( int i = 0; i < getHolidayDatesVector().size(); i++ )
		((com.cannontech.database.db.holiday.DateOfHoliday)getHolidayDatesVector().get(i)).setHolidayScheduleID(newID);
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 12:21:32 PM)
 * @return com.cannontech.database.data.holiday.HolidaySchedule
 */
public void setHolidayScheduleName( String newName )
{
	getHolidaySchedule().setHolidayScheduleName( newName );	
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 10:36:31 AM)
 */
public String toString()
{
	return getHolidayScheduleName();
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 10:36:31 AM)
 */
public void update() throws java.sql.SQLException
{
	getHolidaySchedule().update();

	//delete all the Dates
	com.cannontech.database.db.holiday.DateOfHoliday.deleteAllDateHolidays(getHolidaySchedule().getHolidayScheduleId(), getDbConnection());

	for (int i = 0; i < getHolidayDatesVector().size(); i++)
		((com.cannontech.database.db.holiday.DateOfHoliday) getHolidayDatesVector().elementAt(i)).add();	
}
}
