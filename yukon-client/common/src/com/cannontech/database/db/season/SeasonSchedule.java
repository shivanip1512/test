package com.cannontech.database.db.season;

/**
 * Insert the type's description here.
 * Creation date: (6/22/2004 10:35:21 AM)
 * @author: 
 */
public class SeasonSchedule extends com.cannontech.database.db.DBPersistent
{
	private Integer scheduleID;
	private String scheduleName;


	public static final String SETTER_COLUMNS[] = 
	{ 
		"ScheduleName"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "ScheduleID" };

	public static final String TABLE_NAME = "SeasonSchedule";
/**
 * SeasonSchedule constructor comment.
 */
public SeasonSchedule() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 12:08:19 PM)
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getScheduleID(), getScheduleName()};

	add( TABLE_NAME, addValues );
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 12:08:42 PM)
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getScheduleID());
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 1:31:31 PM)
 */
public Integer getScheduleID()
{
	return scheduleID;
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 1:34:49 PM)
 */
public String getScheduleName() {
	return scheduleName;}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public final static Integer getNextSeasonScheduleID()
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List seasonSchedules = cache.getAllSeasonSchedules();
		java.util.Collections.sort(seasonSchedules);

		int counter = 1;
		int currentID;
		 														
		for(int i=0;i<seasonSchedules.size();i++)
		{
			currentID = ((com.cannontech.database.data.lite.LiteSeasonSchedule)seasonSchedules.get(i)).getScheduleID();

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
 * Creation date: (6/22/2004 12:08:08 PM)
 */
public void retrieve() throws java.sql.SQLException
{
	Object constraintValues[] = { getScheduleID()};

	Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

	if (results.length == SETTER_COLUMNS.length)
	{
		setScheduleName((String) results[0]);		
	}

}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 1:39:07 PM)
 * @param id java.lang.Integer
 */
public void setScheduleID(Integer id) {
	scheduleID = id;
	}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 1:38:37 PM)
 * @param name java.lang.String
 */
public void setScheduleName(String name) {
	scheduleName = name;
	
	}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 12:08:59 PM)
 */
public void update() throws java.sql.SQLException
{
	Object setValues[] = { getScheduleName()};
	Object constraintValues[] = { getScheduleID()};

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
}
}
