package com.cannontech.database.db.device.lm;

/**
 * This type was created in VisualAge.
 */

public class LMProgram extends com.cannontech.database.db.DBPersistent 
{
	private Integer deviceID = null;
	private String controlType = null;
	private String availableSeasons = null;
	private String availableWeekDays = null;
	private Integer maxHoursDaily = null;
	private Integer maxHoursMonthly = null;
	private Integer maxHoursSeasonal = null;
	private Integer maxHoursAnnually = null;
	private Integer minActivateTime = null;
	private Integer minRestartTime = null;


	public static final String SETTER_COLUMNS[] = 
	{ 
		"ControlType", "AvailableSeasons", "AvailableWeekDays", 
		"maxHoursDaily", "maxHoursMonthly", "maxHoursSeasonal" ,
		"maxHoursAnnually", "minActivateTime", "minRestartTime"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "LMProgram";

/**
 * LMGroupVersacomSerial constructor comment.
 */
public LMProgram() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getControlType(), getAvailableSeasons(), getAvailableWeekDays(), 
				getMaxHoursDaily(), getMaxHoursMonthly(), getMaxHoursSeasonal(),
				getMaxHoursAnnually(), getMinActivateTime(), getMinRestartTime() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, "DeviceID", getDeviceID() );
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 10:51:39 AM)
 * @return java.lang.String
 */
public java.lang.String getAvailableSeasons() {
	return availableSeasons;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 10:51:39 AM)
 * @return java.lang.String
 */
public java.lang.String getAvailableWeekDays() {
	return availableWeekDays;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 10:47:47 AM)
 * @return java.lang.String
 */
public java.lang.String getControlType() {
	return controlType;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getDeviceID() {
	return deviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 10:51:39 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMaxHoursAnnually() {
	return maxHoursAnnually;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 10:51:39 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMaxHoursDaily() {
	return maxHoursDaily;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 10:51:39 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMaxHoursMonthly() {
	return maxHoursMonthly;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 10:51:39 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMaxHoursSeasonal() {
	return maxHoursSeasonal;
}
/**
 * Insert the method's description here.
 * Creation date: (5/8/2001 1:53:54 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMinActivateTime() {
	return minActivateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 10:47:47 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMinRestartTime() {
	return minRestartTime;
}

/**
 * This method was created in VisualAge.
 * @param
 * @return java.util.Vector of Integers
 *
 * This method returns all the LMProgram ID's that are not assgined
 *  to a Control Area.
 */
public static java.util.Vector getUnassignedPrograms()
{
	java.util.Vector returnVector = null;
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;


	String sql = "SELECT DeviceID FROM " + TABLE_NAME + " where " +
					 " deviceid not in (select lmprogramdeviceid from " + LMControlAreaProgram.TABLE_NAME +
					 ") ORDER BY deviceid";

	try
	{		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());

		if( conn == null )
		{
			throw new IllegalStateException("Error getting database connection.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			
			rset = pstmt.executeQuery();
			returnVector = new java.util.Vector(5); //rset.getFetchSize()
	
			while( rset.next() )
			{				
				returnVector.addElement( 
						new Integer(rset.getInt("DeviceID")) );
			}
					
		}		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( pstmt != null ) 
				pstmt.close();
			if( conn != null ) 
				conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}


	return returnVector;
}

/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getDeviceID() };	
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setControlType( (String) results[0] );
		setAvailableSeasons( (String) results[1] );
		setAvailableWeekDays( (String) results[2] );
		setMaxHoursDaily( (Integer) results[3] );
		setMaxHoursMonthly( (Integer) results[4] );
		setMaxHoursSeasonal( (Integer) results[5] );
		setMaxHoursAnnually( (Integer) results[6] );
		setMinActivateTime( (Integer) results[7] );
		setMinRestartTime( (Integer) results[8] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 10:51:39 AM)
 * @param newAvailableSeasons java.lang.String
 */
public void setAvailableSeasons(java.lang.String newAvailableSeasons) {
	availableSeasons = newAvailableSeasons;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 10:51:39 AM)
 * @param newAvailableWeekDays java.lang.String
 */
public void setAvailableWeekDays(java.lang.String newAvailableWeekDays) {
	availableWeekDays = newAvailableWeekDays;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 10:47:47 AM)
 * @param newControlType java.lang.String
 */
public void setControlType(java.lang.String newControlType) {
	controlType = newControlType;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setDeviceID(Integer newValue) {
	this.deviceID = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 10:51:39 AM)
 * @param newMaxHoursAnnually java.lang.Integer
 */
public void setMaxHoursAnnually(java.lang.Integer newMaxHoursAnnually) {
	maxHoursAnnually = newMaxHoursAnnually;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 10:51:39 AM)
 * @param newMaxHoursDaily java.lang.Integer
 */
public void setMaxHoursDaily(java.lang.Integer newMaxHoursDaily) {
	maxHoursDaily = newMaxHoursDaily;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 10:51:39 AM)
 * @param newMaxHoursMonthly java.lang.Integer
 */
public void setMaxHoursMonthly(java.lang.Integer newMaxHoursMonthly) {
	maxHoursMonthly = newMaxHoursMonthly;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 10:51:39 AM)
 * @param newMaxHoursSeasonal java.lang.Integer
 */
public void setMaxHoursSeasonal(java.lang.Integer newMaxHoursSeasonal) {
	maxHoursSeasonal = newMaxHoursSeasonal;
}
/**
 * Insert the method's description here.
 * Creation date: (5/8/2001 1:53:54 PM)
 * @param newMinActivateTime java.lang.Integer
 */
public void setMinActivateTime(java.lang.Integer newMinActivateTime) {
	minActivateTime = newMinActivateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 10:47:47 AM)
 * @param newMinRestartTime java.lang.Integer
 */
public void setMinRestartTime(java.lang.Integer newMinRestartTime) {
	minRestartTime = newMinRestartTime;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getControlType(), getAvailableSeasons(), getAvailableWeekDays(), 
				getMaxHoursDaily(), getMaxHoursMonthly(), getMaxHoursSeasonal(),
				getMaxHoursAnnually(), getMinActivateTime(), getMinRestartTime() };

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
