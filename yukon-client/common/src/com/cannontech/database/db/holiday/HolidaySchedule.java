package com.cannontech.database.db.holiday;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Insert the type's description here.
 * Creation date: (8/23/2001 11:54:45 AM)
 * @author: 
 */
public class HolidaySchedule extends com.cannontech.database.db.DBPersistent
{
	private Integer holidayScheduleId;
	private String holidayScheduleName;


	public static final String SETTER_COLUMNS[] = 
	{ 
		"HolidayScheduleName"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "HolidayScheduleID" };

	public static final String TABLE_NAME = "HolidaySchedule";
/**
 * HolidaySchedule constructor comment.
 */
public HolidaySchedule() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2001 12:08:19 PM)
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getHolidayScheduleId(), getHolidayScheduleName()};

	add( TABLE_NAME, addValues );
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2001 12:08:42 PM)
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getHolidayScheduleId());
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 1:31:31 PM)
 */
public Integer getHolidayScheduleId()
{
	return holidayScheduleId;
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 1:34:49 PM)
 */
public String getHolidayScheduleName() {
	return holidayScheduleName;}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public final static Integer getNextHolidayScheduleID()
{
	IDatabaseCache cache = DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List holidaySchedules = cache.getAllHolidaySchedules();
		java.util.Collections.sort(holidaySchedules);

		int counter = 1;
		int currentID;
		 														
		for(int i=0;i<holidaySchedules.size();i++)
		{
			currentID = ((com.cannontech.database.data.lite.LiteHolidaySchedule)holidaySchedules.get(i)).getHolidayScheduleID();

			if( currentID > counter )
				break;
			else
				counter = currentID + 1;
		}		
		
		return new Integer( counter );
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2001 12:08:08 PM)
 */
public void retrieve() throws java.sql.SQLException
{
	Object constraintValues[] = { getHolidayScheduleId()};

	Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

	if (results.length == SETTER_COLUMNS.length)
	{
		setHolidayScheduleName((String) results[0]);		
	}

}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 1:39:07 PM)
 * @param id java.lang.Integer
 */
public void setHolidayScheduleId(Integer id) {
	holidayScheduleId = id;
	}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 1:38:37 PM)
 * @param name java.lang.String
 */
public void setHolidayScheduleName(String name) {
	holidayScheduleName = name;
	
	}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2001 12:08:59 PM)
 */
public void update() throws java.sql.SQLException
{
	Object setValues[] = { getHolidayScheduleName()};
	Object constraintValues[] = { getHolidayScheduleId()};

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
}
}
