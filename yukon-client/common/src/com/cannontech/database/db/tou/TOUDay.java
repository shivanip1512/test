/*
 * Created on Dec 02, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.tou;

import com.cannontech.database.data.lite.LiteTOUDay;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TOUDay extends com.cannontech.database.db.DBPersistent
{
	private Integer dayID;
	private String dayName;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"TOUDayName"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "TOUDayID" };

	public static final String TABLE_NAME = "TOUDay";

/**
 * TOUSchedule constructor comment.
 */
public TOUDay() {
	super();
}

/**
 * Insert the method's description here.
 * Creation date: (12/02/2004 12:08:19 PM)
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDayID(), getDayName()};

	add( TABLE_NAME, addValues );
}

/**
 * Insert the method's description here.
 * Creation date: (12/02/2004 12:08:42 PM)
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getDayID());
}

/**
 * Insert the method's description here.
 * Creation date: (12/02/2004 1:31:31 PM)
 */
public Integer getDayID()
{
	return dayID;
}

/**
 * Insert the method's description here.
 * Creation date: (12/02/2004 1:34:49 PM)
 */
public String getDayName() 
{
	return dayName;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public final static Integer getNextTOUDayID()
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List touDays = cache.getAllTOUDays();
		java.util.Collections.sort(touDays);

		int counter = 1;
		int currentID;
		 														
		for(int i = 0; i < touDays.size(); i++)
		{
			currentID = ((LiteTOUDay)touDays.get(i)).getDayID();

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
 * Creation date: (12/02/2004 12:08:08 PM)
 */
public void retrieve() throws java.sql.SQLException
{
	Object constraintValues[] = { getDayID()};

	Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

	if (results.length == SETTER_COLUMNS.length)
	{
		setDayName((String) results[0]);
	}

}

/**
 * Insert the method's description here.
 * Creation date: (12/02/2004 1:39:07 PM)
 * @param id java.lang.Integer
 */
public void setDayID(Integer id) 
{
	dayID = id;
}

/**
 * Insert the method's description here.
 * Creation date: (12/02/2004 1:38:37 PM)
 * @param name java.lang.String
 */
public void setDayName(String name) 
{
	dayName = name;
}

/**
 * Insert the method's description here.
 * Creation date: (12/02/2004 12:08:59 PM)
 */
public void update() throws java.sql.SQLException
{
	Object setValues[] = { getDayName()};
	Object constraintValues[] = { getDayID()};

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
}
}
