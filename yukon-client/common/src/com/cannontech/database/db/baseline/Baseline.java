package com.cannontech.database.db.baseline;

import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.SqlUtils;

/**
 * Insert the type's description here.
 * Creation date: (7/24/2003 11:32:52 AM)
 * @author: 
 */
public class Baseline extends com.cannontech.database.db.DBPersistent 
{
	private Integer baselineID;
	private String baselineName;
	private Integer percentWindow;
	private Integer daysUsed;
	private Integer calcDays;
	private String excludedWeekdays;
	private Integer holidayScheduleId;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"BASELINEID", "BASELINENAME", "DAYSUSED", 
		"PERCENTWINDOW", "CALCDAYS", "EXCLUDEDWEEKDAYS", "HOLIDAYSCHEDULEID" 
	};

	public static final String CONSTRAINT_COLUMNS[] = { "baselineID" };

	public static final String TABLE_NAME = "Baseline";

/**
 * Baseline constructor comment.
 */
public Baseline() {
	super();
}
/**
 * Baseline constructor comment.
 */
public Baseline(Integer baseID, String name, Integer day, Integer pWindow, Integer cDay, String wDays, Integer holidayScheduleId) {
	super();
	baselineID = baseID;
	baselineName = name;
	percentWindow = pWindow;
	daysUsed = day;
	calcDays = cDay;
	excludedWeekdays = wDays;
	this.holidayScheduleId = holidayScheduleId;
	
}


@Override
public void add() throws java.sql.SQLException
{
	Object addValues[] = 
	{ 
		getBaselineID(), getBaselineName(),
		getDaysUsed(), getPercentWindow(), 
		getCalcDays(), getExcludedWeekdays(), 
		getHolidayScheduleId()
	};

	add( TABLE_NAME, addValues );
}


@Override
public void delete() throws java.sql.SQLException
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getBaselineID());
}
/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static boolean deleteAllBaselines(Integer blineID, java.sql.Connection conn)
{
	if( conn == null )
		throw new IllegalArgumentException("Database connection should not be (null)");

	try
	{
		java.sql.Statement stat = conn.createStatement();

		stat.execute("DELETE FROM " + TABLE_NAME + " WHERE " + CONSTRAINT_COLUMNS[0] + "= " + blineID + " AND WHERE NOT " + SETTER_COLUMNS[1] + "= Default Baseline");
		
		if (stat != null)
			stat.close();
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return false;
	}

	return true;
}
   
public static synchronized Integer getNextBaselineID( java.sql.Connection conn )
	{
		if( conn == null )
			throw new IllegalStateException("Database connection should not be null.");
	
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		
		try 
		{		
			stmt = conn.createStatement();
			 rset = stmt.executeQuery( "SELECT Max(BaselineID)+1 FROM " + TABLE_NAME );	
				
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
			SqlUtils.close(rset, stmt );
		}
		
		//strange, should not get here
		return new Integer(com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID);
	}

public java.lang.Integer getDaysUsed() {
	return daysUsed;
}

public java.lang.Integer getPercentWindow() {
	return percentWindow;
}

public java.lang.String getBaselineName() {
	return baselineName;
}

public java.lang.Integer getBaselineID() {
	return baselineID;
}

public java.lang.Integer getCalcDays() {
	return calcDays;
}

public String getExcludedWeekdays() {
	return excludedWeekdays;
}

public java.lang.Integer getHolidayScheduleId() {
	return holidayScheduleId;
}

@Override
public void retrieve() 
{
	Integer constraintValues[] = { getBaselineID() };	
	
	try
	{
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length )
		{
			setBaselineName( (String) results[1] );
			setDaysUsed( (Integer) results[2] );
			setPercentWindow( (Integer) results[3] );
			setCalcDays( (Integer) results[4] );
			setExcludedWeekdays( (String) results[5] );
			setHolidayScheduleId( (Integer) results[6] );
		}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
}

public void setDaysUsed(java.lang.Integer newdaysUsed) {
	daysUsed = newdaysUsed;
}

public void setPercentWindow(java.lang.Integer newpercentWindow) {
	percentWindow = newpercentWindow;
}

public void setBaselineName(java.lang.String newbaselineName) {
	baselineName = newbaselineName;
}

public void setBaselineID(java.lang.Integer newbaselineID) {
	baselineID = newbaselineID;
}

public void setCalcDays(java.lang.Integer newcalcDays) {
	calcDays = newcalcDays;
}

public void setExcludedWeekdays(java.lang.String newExcludedWeekdays) {
	excludedWeekdays = newExcludedWeekdays;
}

public void setHolidayScheduleId(java.lang.Integer holidayScheduleId) {
	this.holidayScheduleId = holidayScheduleId;
}

@Override
public void update() 
{
	Object setValues[] =
	{ 
		getBaselineID(),
		getBaselineName(), getDaysUsed(),
		getPercentWindow(), getCalcDays(),
		getExcludedWeekdays(), getHolidayScheduleId()
	};
	
	Object constraintValues[] = { getBaselineID() };
	
	try
	{
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
}

public final static boolean usesHolidaySchedule(int holSchID) throws java.sql.SQLException {
    JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
    String sql = "select count(*) from " + TABLE_NAME + " where holidayscheduleid = ?";
    int count = jdbcOps.queryForObject(sql, Integer.class, holSchID);
    return count > 0; 
}
}
