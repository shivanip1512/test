package com.cannontech.database.db.device;

/**
 * This type was created in VisualAge.
 */
public class DeviceTwoWayFlags extends com.cannontech.database.db.DBPersistent 
{
   private Integer deviceID = null;
	private Character monthlyStats = new Character('N');
	private Character twentyFourHourStats = new Character('N');
	private Character hourlyStats = new Character('N');
	private Character failureAlarm = new Character('N');
	private Integer performThreshold = new Integer(0);
	private Character performAlarm = new Character('N');
	private Character performTwentyFourAlarm = new Character('N');


	public static final String[] SETTER_COLUMNS = 
	{ 
		"MonthlyStats", "TwentyFourHourStats", "HourlyStats", 
		"FailureAlarm", "PerformanceThreshold", "PerformanceAlarm", 
		"PerformanceTwentyFourAlarm" 
	};

	public static final String[] CONSTRAINT_COLUMNS = { "DeviceID" };

	public static final String TABLE_NAME = "Device2WayFlags";

/**
 * DeviceTwoWayFlags constructor comment.
 */
public DeviceTwoWayFlags() 
{
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = { getDeviceID(), getMonthlyStats(), 
		getTwentyFourHourStats(), getHourlyStats(), getFailureAlarm(), 
		getPerformThreshold(), getPerformAlarm(), getPerformTwentyFourAlarm() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getDeviceID() );	
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getDeviceID() {
	return deviceID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Character
 */
public Character getFailureAlarm() {
	return failureAlarm;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Character
 */
public Character getHourlyStats() {
	return hourlyStats;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Character
 */
public Character getMonthlyStats() {
	return monthlyStats;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Character
 */
public Character getPerformAlarm() {
	return performAlarm;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getPerformThreshold() {
	return performThreshold;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Character
 */
public Character getPerformTwentyFourAlarm() {
	return performTwentyFourAlarm;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Character
 */
public Character getTwentyFourHourStats() {
	return twentyFourHourStats;
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
		if( results[0] != null )
			setMonthlyStats( new Character( ((String) results[0]).charAt(0) ) );

		if( results[1] != null )
			setTwentyFourHourStats( new Character( ((String) results[1]).charAt(0) ) );

		if( results[2] != null )
			setHourlyStats( new Character( ((String) results[2]).charAt(0) ) );

		if( results[3] != null )
			setFailureAlarm( new Character( ((String) results[3]).charAt(0) ) );

		if( results[4] != null )
			setPerformThreshold( (Integer) results[4] );
			
		if( results[5] != null )
			setPerformAlarm( new Character( ((String) results[5]).charAt(0 ) ) );

		if( results[6] != null )
			setPerformTwentyFourAlarm( new Character( ((String) results[6]).charAt(0) ) );			
	}

}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setDeviceID(Integer newValue) {
	this.deviceID = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Character
 */
public void setFailureAlarm(Character newValue) {
	this.failureAlarm = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Character
 */
public void setHourlyStats(Character newValue) {
	this.hourlyStats = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Character
 */
public void setMonthlyStats(Character newValue) {
	this.monthlyStats = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Character
 */
public void setPerformAlarm(Character newValue) {
	this.performAlarm = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setPerformThreshold(Integer newValue) {
	this.performThreshold = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Character
 */
public void setPerformTwentyFourAlarm(Character newValue) {
	this.performTwentyFourAlarm = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Character
 */
public void setTwentyFourHourStats(Character newValue) {
	this.twentyFourHourStats = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[]= { getMonthlyStats(), getTwentyFourHourStats(), 
				getHourlyStats(), getFailureAlarm(), getPerformThreshold(), getPerformAlarm(), 
				getPerformTwentyFourAlarm() };
	
	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
