package com.cannontech.database.db.device.lm;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMProgramConstraint extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	private Integer constraintID;
	private String constraintName;
	private String availableWeekdays = "YYYYYYNN";

	//store in DB as seconds
	private Integer maxHoursDaily = new Integer(0);
	//store in DB as seconds	
	private Integer maxHoursMonthly = new Integer(0);
	//store in DB as seconds
	private Integer maxHoursSeasonal = new Integer(0);
	//store in DB as seconds
	private Integer maxHoursAnnually = new Integer(0);

	private Integer minActivateTime = new Integer(0);
	private Integer minRestartTime = new Integer(0);
	private Integer maxDailyOps = new Integer(0);
	private Integer maxActivateTime = new Integer(0);
	private Integer holidayScheduleID = new Integer(CtiUtilities.NONE_ID);
	private Integer seasonScheduleID = new Integer(CtiUtilities.NONE_ID);     
	

	public static final String SETTER_COLUMNS[] = 
	{ 
		"CONSTRAINTID", "CONSTRAINTNAME", "AVAILABLEWEEKDAYS", "MAXHOURSDAILY",
		"MAXHOURSMONTHLY", "MAXHOURSSEASONAL", "MAXHOURSANNUALLY", "MINACTIVATETIME",
		"MINRESTARTTIME", "MAXDAILYOPS", "MAXACTIVATETIME", "HOLIDAYSCHEDULEID",
		"SEASONSCHEDULEID"
		
	};

	public static final String CONSTRAINT_COLUMNS[] = { "constraintID" };

	public static final String TABLE_NAME = "LMProgramConstraints";

/**
 * LMProgramConstraint constructor comment.
 */
public LMProgramConstraint() {
	super();
}
/**
 * LMProgramConstraint constructor comment.
 */
public LMProgramConstraint(Integer cID) {
	super();
	constraintID = cID;
		
}

public LMProgramConstraint(Integer cID, String cName) {
	super();
	constraintID = cID;
	constraintName = cName;
	
}

public void add() throws java.sql.SQLException
{
	
	if(getConstraintID() == null)
		setConstraintID(getNextConstraintID());
	
	Object addValues[] = 
	{ 
		getConstraintID(), getConstraintName(),  
		getAvailableWeekdays(),
		new Integer(getMaxHoursDaily().intValue()*3600),
		new Integer(getMaxHoursMonthly().intValue()*3600),
		new Integer(getMaxHoursSeasonal().intValue()*3600),
		new Integer(getMaxHoursAnnually().intValue()*3600),
		getMinActivateTime(), getMinRestartTime(), getMaxDailyOps(), getMaxActivateTime(), 
		getHolidayScheduleID(), getSeasonScheduleID()
		
	};

	add( TABLE_NAME, addValues );
}


public void delete() throws java.sql.SQLException
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getConstraintID());
}

public final static Integer getNextConstraintID() throws java.sql.SQLException 
{	
	return getNextConstraintID(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}

public final static Integer getNextConstraintID(String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT Max(ConstraintID)+1 FROM " + TABLE_NAME ,
													databaseAlias );

	try
	{
		stmt.execute();
		return new Integer(stmt.getRow(0)[0].toString());
	}
	catch( Exception e )
	{
	   com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	   return new Integer(-5);
	}
}

/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean inUseByProgram(int constrID, String databaseAlias) throws java.sql.SQLException 
{
	SqlStatement stmt =	new SqlStatement(
		"SELECT * FROM " + LMProgram.TABLE_NAME +
		" WHERE ConstraintID = " + constrID,
		databaseAlias );

	try
	{
		stmt.execute();
		return (stmt.getRowCount() > 0 );
	}
	catch( Exception e )
	{
		return false;
	}
}

public final static boolean usesSeasonSchedule(int seasonSchID, String databaseAlias) throws java.sql.SQLException 
{
	SqlStatement stmt =	new SqlStatement(
		"SELECT * FROM " + TABLE_NAME +
		" WHERE SeasonScheduleID = " + seasonSchID,
		databaseAlias );

	try
	{
		stmt.execute();
		return (stmt.getRowCount() > 0 );
	}
	catch( Exception e )
	{
		return false;
	}
}

public final static boolean usesHolidaySchedule(int holSchID, String databaseAlias) throws java.sql.SQLException 
{
	SqlStatement stmt =	new SqlStatement(
		"SELECT * FROM " + TABLE_NAME +
		" WHERE HolidayScheduleID = " + holSchID,
		databaseAlias );

	try
	{
		stmt.execute();
		return (stmt.getRowCount() > 0 );
	}
	catch( Exception e )
	{
		return false;
	}
}

public com.cannontech.message.dispatch.message.DBChangeMsg[] getDBChangeMsgs( int typeOfChange )
{
	com.cannontech.message.dispatch.message.DBChangeMsg[] msgs =
	{
		new com.cannontech.message.dispatch.message.DBChangeMsg(
			getConstraintID().intValue(),
			com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_LMCONSTRAINT_DB,
			com.cannontech.message.dispatch.message.DBChangeMsg.CAT_LMCONSTRAINT,
			com.cannontech.message.dispatch.message.DBChangeMsg.CAT_LMCONSTRAINT,
			typeOfChange)
	};


	return msgs;
}


public Integer getConstraintID() {
	return constraintID;
}

public String getConstraintName() {
	return constraintName;
}

public String getAvailableWeekdays() {
	return availableWeekdays;
}

public Integer getMaxHoursDaily() {
	return maxHoursDaily;
}

public Integer getMaxHoursMonthly() {
	return maxHoursMonthly;
}

public Integer getMaxHoursSeasonal() {
	return maxHoursSeasonal;
}

public Integer getMaxHoursAnnually() {
	return maxHoursAnnually;
}

public Integer getMinActivateTime() {
	return minActivateTime;
}

public Integer getMinRestartTime() {
	return minRestartTime;
}

public Integer getMaxDailyOps() {
	return maxDailyOps;
}

public Integer getMaxActivateTime() {
	return maxActivateTime;
}

public Integer getHolidayScheduleID() {
	return holidayScheduleID;
}

public Integer getSeasonScheduleID() {
	return seasonScheduleID;
}


public void retrieve() 
{
	Integer constraintValues[] = { getConstraintID() };	
	
	try
	{
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length )
		{
			setConstraintName( (String) results[1] );
			setAvailableWeekdays( (String) results[2] );
			setMaxHoursDaily( new Integer( ((Integer)results[3]).intValue()/3600) );
			setMaxHoursMonthly( new Integer( ((Integer)results[4]).intValue()/3600) );
			setMaxHoursSeasonal( new Integer( ((Integer)results[5]).intValue()/3600) );
			setMaxHoursAnnually( new Integer( ((Integer)results[6]).intValue()/3600) );
			
			setMinActivateTime( (Integer) results[7] );
			setMinRestartTime( (Integer) results[8] );
			setMaxDailyOps( (Integer) results[9] );
			setMaxActivateTime( (Integer) results[10] );
			setHolidayScheduleID( (Integer) results[11] );
			setSeasonScheduleID( (Integer) results[12] );
			
			
			
		}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
}


public void setConstraintID(Integer newConstraintID) {
	constraintID = newConstraintID;
}

public void setConstraintName(String newName) {
	constraintName = newName;
}

public void setAvailableWeekdays(String newDays) {
	availableWeekdays = newDays;
}

public void setMaxHoursDaily(Integer hours) {
	maxHoursDaily = hours;
}

public void setMaxHoursMonthly(Integer hours) {
	maxHoursMonthly = hours;
}

public void setMaxHoursSeasonal(Integer hours) {
	maxHoursSeasonal = hours;
}

public void setMaxHoursAnnually(Integer hours) {
	maxHoursAnnually = hours;
}

public void setMinActivateTime(Integer time) {
	minActivateTime = time;
}

public void setMinRestartTime(Integer time) {
	minRestartTime = time;
}

public void setMaxDailyOps(Integer covertOps) {
	maxDailyOps = covertOps;
}

public void setMaxActivateTime(Integer time) {
	maxActivateTime = time;
}

public void setHolidayScheduleID(Integer newID) {
	holidayScheduleID = newID;
}

public void setSeasonScheduleID(Integer newID) {
	seasonScheduleID = newID;
}

public void update() 
{
	Object setValues[] = 
	{ 
		getConstraintID(), getConstraintName(),  
		getAvailableWeekdays(),
		new Integer(getMaxHoursDaily().intValue()*3600),
		new Integer(getMaxHoursMonthly().intValue()*3600),
		new Integer(getMaxHoursSeasonal().intValue()*3600),
		new Integer(getMaxHoursAnnually().intValue()*3600),
		getMinActivateTime(), getMinRestartTime(), getMaxDailyOps(), getMaxActivateTime(), 
		getHolidayScheduleID(), getSeasonScheduleID()
		
	};

	Object constraintValues[] = { getConstraintID() };
	
	try
	{
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
}

public String toString()
{
	return getConstraintName();
}
}
