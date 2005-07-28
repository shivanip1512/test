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

public TOUDay(String name) {
	super();
	dayName = name;
}

public TOUDay(Integer id) {
	super();
	dayID = id;
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
/*
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
}*/

public static synchronized Integer getNextTOUDayID( java.sql.Connection conn )
{
	if( conn == null )
		throw new IllegalStateException("Database connection should not be null.");

	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;
	
	try 
	{		
		stmt = conn.createStatement();
		rset = stmt.executeQuery( "SELECT Max(TOUDayID)+1 FROM " + TABLE_NAME );	
				
		//get the first returned result
		rset.next();
		return new Integer( rset.getInt(1) );
	}
	catch (java.sql.SQLException e) 
	{
		e.printStackTrace();
	}
	finally 
	{
		try 
		{
			if ( stmt != null) stmt.close();
		}
		catch (java.sql.SQLException e2) 
		{
			e2.printStackTrace();
		}
	}
	
	//strange, should not get here
	return new Integer(com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID);
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

public String toString()
{
	return dayName;
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
