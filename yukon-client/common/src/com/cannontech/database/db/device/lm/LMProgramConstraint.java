/*
 * Created on Feb 16, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.device.lm;

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
	private String availableSeasons;    
	private String availableWeekdays; 
	private Integer maxHoursDaily;     
	private Integer maxHoursMonthly;    
	private Integer maxHoursSeasonal;    
	private Integer maxHoursAnnually;     
	private Integer minActivateTime;     
	private Integer minRestartTime;     
	private Integer maxDailyOps;        
	private Integer maxActivateTime;      
	private Integer holidayScheduleID;
	private Integer seasonScheduleID;     
	

	public static final String SETTER_COLUMNS[] = 
	{ 
		"CONSTRAINTID", "CONSTRAINTNAME", "AVAILABLESEASONS", "AVAILABLEWEEKDAYS", "MAXHOURSDAILY",
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
		getConstraintID(), getConstraintName(), getAvailableSeasons(), 
		getAvailableWeekdays(), getMaxHoursDaily(), getMaxHoursMonthly(), 
		getMaxHoursSeasonal(), getMaxHoursAnnually(), getMinActivateTime(), 
		getMinRestartTime(), getMaxDailyOps(), getMaxActivateTime(), 
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

public String getAvailableSeasons() {
	return availableSeasons;
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
			setAvailableSeasons( (String) results[2] );
			setAvailableWeekdays( (String) results[3] );
			setMaxHoursDaily( (Integer) results[4] );
			setMaxHoursMonthly( (Integer) results[5] );
			setMaxHoursSeasonal( (Integer) results[6] );
			setMaxHoursAnnually( (Integer) results[7] );
			setMinActivateTime( (Integer) results[8] );
			setMinRestartTime( (Integer) results[9] );
			setMaxDailyOps( (Integer) results[10] );
			setMaxActivateTime( (Integer) results[11] );
			setHolidayScheduleID( (Integer) results[12] );
			setSeasonScheduleID( (Integer) results[13] );
			
			
			
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

public void setAvailableSeasons(String newSeasons) {
	availableSeasons = newSeasons;
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
		getConstraintID(), getConstraintName(), getAvailableSeasons(), 
		getAvailableWeekdays(), getMaxHoursDaily(), getMaxHoursMonthly(), 
		getMaxHoursSeasonal(), getMaxHoursAnnually(), getMinActivateTime(), 
		getMinRestartTime(), getMaxDailyOps(), getMaxActivateTime(), 
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
}
