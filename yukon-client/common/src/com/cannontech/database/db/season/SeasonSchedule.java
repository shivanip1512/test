/*
 * Created on May 4, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.season;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SeasonSchedule extends com.cannontech.database.db.DBPersistent
{
	private Integer seasonScheduleID;
	private String seasonScheduleName;

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
 * Creation date: (8/23/2001 12:08:19 PM)
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getSeasonID(), getSeasonName()};

	add( TABLE_NAME, addValues );
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2001 12:08:42 PM)
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getSeasonID());
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 1:31:31 PM)
 */
public Integer getSeasonID()
{
	return seasonScheduleID;
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 1:34:49 PM)
 */
public String getSeasonName() {
	return seasonScheduleName;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public final static Integer getNextSeasonID()
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List seasonSchedules = cache.getAllSeasons();
		java.util.Collections.sort(seasonSchedules);

		int counter = 1;
		int currentID;
		 														
		for(int i=0;i<seasonSchedules.size();i++)
		{
			currentID = ((com.cannontech.database.data.lite.LiteSeason)seasonSchedules.get(i)).getSeasonID();

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
	Object constraintValues[] = { getSeasonID()};

	Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

	if (results.length == SETTER_COLUMNS.length)
	{
		setSeasonName((String) results[0]);		
	}

}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 1:39:07 PM)
 * @param id java.lang.Integer
 */
public void setSeasonID(Integer id) {
	seasonScheduleID = id;
	}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 1:38:37 PM)
 * @param name java.lang.String
 */
public void setSeasonName(String name) {
	seasonScheduleName = name;
	
	}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2001 12:08:59 PM)
 */
public void update() throws java.sql.SQLException
{
	Object setValues[] = { getSeasonName()};
	Object constraintValues[] = { getSeasonID()};

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
}
}

