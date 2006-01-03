/*
 * Created on Sep 22, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.tou;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TOUSchedule extends com.cannontech.database.db.DBPersistent
{
	private Integer scheduleID;
	private String scheduleName;
	private String defaultRate;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"TOUScheduleName", "TOUDefaultRate"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "TOUScheduleID" };

	public static final String TABLE_NAME = "TOUSchedule";
/**
 * TOUSchedule constructor comment.
 */
public TOUSchedule() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 12:08:19 PM)
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getScheduleID(), getScheduleName(), getDefaultRate() };

	add( TABLE_NAME, addValues );
}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 12:08:42 PM)
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getScheduleID());
}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 1:31:31 PM)
 */
public Integer getScheduleID()
{
	return scheduleID;
}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 1:34:49 PM)
 */
public String getScheduleName() 
{
	return scheduleName;
}

public String getDefaultRate() 
{
	return defaultRate;
}

public static Integer getNextTOUScheduleID() 
{
    SqlStatement stmt = new SqlStatement("SELECT MAX(TOUSCHEDULEID) + 1 FROM " + TABLE_NAME, CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            return (new Integer(stmt.getRow(0)[0].toString()));
        }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
    
    return null;
}

public void retrieve() throws java.sql.SQLException
{
	Object constraintValues[] = { getScheduleID()};

	Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

	if (results.length == SETTER_COLUMNS.length)
	{
		setScheduleName((String) results[0]);
		setDefaultRate((String) results[1]);		
	}

}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 1:39:07 PM)
 * @param id java.lang.Integer
 */
public void setScheduleID(Integer id) 
{
	scheduleID = id;
}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 1:38:37 PM)
 * @param name java.lang.String
 */
public void setScheduleName(String name) 
{
	scheduleName = name;
}

public void setDefaultRate(String rate) 
{
	defaultRate = rate;
}

/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 12:08:59 PM)
 */
public void update() throws java.sql.SQLException
{
	Object setValues[] = { getScheduleName(), getDefaultRate()};
	Object constraintValues[] = { getScheduleID()};

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
}
}
