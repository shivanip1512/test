package com.cannontech.database.db.device;

/**
 * This type was created in VisualAge.
 */
public class DynamicDeviceScanData extends com.cannontech.database.db.DBPersistent 
{	
	private Integer deviceID = null;
	private java.util.GregorianCalendar lastFreezeTime = null;
	private java.util.GregorianCalendar prevFreezeTime = null;
	private java.util.GregorianCalendar lastLPTime = null;
	private Integer lastFreezeNumber = null;
	private Integer prevFreezeNumber = null;
	private java.util.GregorianCalendar nextScan0 = null;
	private java.util.GregorianCalendar nextScan1 = null;
	private java.util.GregorianCalendar nextScan2 = null;
	private java.util.GregorianCalendar nextScan3 = null;

	public static final String columns[] = { "DEVICEID", "LASTFREEZETIME", "PREVFREEZETIME", "LASTLPTIME", "LASTFREEZENUMBER",
		"PREVFREEZENUMBER", "NEXTSCAN0", "NEXTSCAN1", "NEXTSCAN2", "NEXTSCAN3" };

	public static final String constraintColumns[] = { "DEVICEID" };
	
	public final static String tableName = "DynamicDeviceScanData";
/**
 * PointDispatch constructor comment.
 */
public DynamicDeviceScanData() {
	super();
	initialize( null, null ,null, null, null , null, null ,null, null, null );
}
/**
 * PointDispatch constructor comment.
 */
public DynamicDeviceScanData(Integer deviceID) {
	super();
	initialize( deviceID, null ,null, null, null ,null, null ,null, null, null );
}
/**
 * PointDispatch constructor comment.
 */
public DynamicDeviceScanData(Integer deviceID, java.util.GregorianCalendar lastFreezTime,
							java.util.GregorianCalendar prevFreezTime, 
							java.util.GregorianCalendar lastLPTime,
							Integer lastFreezeNumber, Integer prevFreezeNumber,
							java.util.GregorianCalendar nextScan0,
							java.util.GregorianCalendar nextScan1,
							java.util.GregorianCalendar nextScan2,
							java.util.GregorianCalendar nextScan3) 
{
	super();
	initialize(deviceID, lastFreezTime, prevFreezTime, 
				lastLPTime, lastFreezeNumber, prevFreezeNumber, nextScan0,
				nextScan1, nextScan2, nextScan3);
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	Object addValues[] = { getDeviceID(), getLastFreezeTime(), getPrevFreezeTime(), getLastLPTime(), getLastFreezeNumber(), getPrevFreezeNumber(), getNextScan0(), getNextScan1(), getNextScan2(), getNextScan3() };

	add( this.tableName, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( this.tableName, "DEVICEID", getDeviceID() );
}

/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param pointID java.lang.Integer
 */
public static boolean deleteDynamicDeviceScanData(Integer deviceID, java.sql.Connection conn )
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement(
               "DELETE FROM " + tableName + " WHERE deviceID=" + deviceID,
					conn );
	try
	{
		stmt.execute();
		return true;
	}
	catch(Exception e)
	{
		return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 1:27:32 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDeviceID() {
	return deviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 1:27:32 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getLastFreezeNumber() {
	return lastFreezeNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 1:27:32 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getLastFreezeTime() {
	return lastFreezeTime;
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 1:27:32 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getLastLPTime() {
	return lastLPTime;
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 1:27:32 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getNextScan0() {
	return nextScan0;
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 1:27:32 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getNextScan1() {
	return nextScan1;
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 1:27:32 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getNextScan2() {
	return nextScan2;
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 1:27:33 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getNextScan3() {
	return nextScan3;
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 1:27:33 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPrevFreezeNumber() {
	return prevFreezeNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 1:27:33 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getPrevFreezeTime() {
	return prevFreezeTime;
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasDynamicDeviceScanData(Integer deviceID) throws java.sql.SQLException {
	
	return hasDynamicDeviceScanData(deviceID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasDynamicDeviceScanData(Integer deviceID, String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT * FROM " + tableName + " WHERE deviceID=" + deviceID,
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
/**
 * This method was created in VisualAge.
 */
public void initialize( Integer deviceID, java.util.GregorianCalendar lastFreezTime,
						java.util.GregorianCalendar prevFreezTime, 
						java.util.GregorianCalendar lastLPTime,
						Integer lastFreezeNumber, Integer prevFreezeNumber,
						java.util.GregorianCalendar nextScan0,
						java.util.GregorianCalendar nextScan1,
						java.util.GregorianCalendar nextScan2,
						java.util.GregorianCalendar nextScan3) 
{

	setDeviceID( deviceID );
	setLastFreezeTime( lastFreezeTime );
	setPrevFreezeTime( prevFreezeTime ) ;
	setLastLPTime( lastLPTime );
	setLastFreezeNumber( lastFreezeNumber );
	setPrevFreezeNumber( prevFreezeNumber );
	setNextScan0(nextScan0);
	setNextScan1(nextScan1);
	setNextScan2(nextScan2);
	setNextScan3(nextScan3);
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getDeviceID() };

	Object results[] = retrieve( columns, this.tableName, constraintColumns, constraintValues );

	if( results.length == columns.length )
	{
		setDeviceID( (Integer) results[0] );
		java.util.GregorianCalendar tempCal = new java.util.GregorianCalendar();
		tempCal.setTime( new java.util.Date(((java.sql.Timestamp)results[1]).getTime()) );
		setLastFreezeTime( tempCal );
		tempCal = new java.util.GregorianCalendar();
		tempCal.setTime( new java.util.Date(((java.sql.Timestamp)results[2]).getTime()) );
		setPrevFreezeTime( tempCal );
		tempCal = new java.util.GregorianCalendar();
		tempCal.setTime( new java.util.Date(((java.sql.Timestamp)results[3]).getTime()) );
		setLastLPTime( tempCal );
		
		setLastFreezeNumber( (Integer) results[4] );
		setPrevFreezeNumber( (Integer) results[5] );
		
		tempCal = new java.util.GregorianCalendar();
		tempCal.setTime( new java.util.Date(((java.sql.Timestamp)results[6]).getTime()) );
		setNextScan0( tempCal );
		tempCal = new java.util.GregorianCalendar();
		tempCal.setTime( new java.util.Date(((java.sql.Timestamp)results[7]).getTime()) );
		setNextScan1( tempCal );
		tempCal = new java.util.GregorianCalendar();
		tempCal.setTime( new java.util.Date(((java.sql.Timestamp)results[8]).getTime()) );
		setNextScan2( tempCal );
		tempCal = new java.util.GregorianCalendar();
		tempCal.setTime( new java.util.Date(((java.sql.Timestamp)results[9]).getTime()) );
		setNextScan3( tempCal );		
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
	
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 1:27:32 PM)
 * @param newDeviceID java.lang.Integer
 */
public void setDeviceID(java.lang.Integer newDeviceID) {
	deviceID = newDeviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 1:27:32 PM)
 * @param newLastFreezeNumber java.lang.Integer
 */
public void setLastFreezeNumber(java.lang.Integer newLastFreezeNumber) {
	lastFreezeNumber = newLastFreezeNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 1:27:32 PM)
 * @param newLastFreezeTime java.util.GregorianCalendar
 */
public void setLastFreezeTime(java.util.GregorianCalendar newLastFreezeTime) {
	lastFreezeTime = newLastFreezeTime;
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 1:27:32 PM)
 * @param newLastLPTime java.util.GregorianCalendar
 */
public void setLastLPTime(java.util.GregorianCalendar newLastLPTime) {
	lastLPTime = newLastLPTime;
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 1:27:32 PM)
 * @param newNextScan0 java.util.GregorianCalendar
 */
public void setNextScan0(java.util.GregorianCalendar newNextScan0) {
	nextScan0 = newNextScan0;
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 1:27:32 PM)
 * @param newNextScan1 java.util.GregorianCalendar
 */
public void setNextScan1(java.util.GregorianCalendar newNextScan1) {
	nextScan1 = newNextScan1;
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 1:27:32 PM)
 * @param newNextScan2 java.util.GregorianCalendar
 */
public void setNextScan2(java.util.GregorianCalendar newNextScan2) {
	nextScan2 = newNextScan2;
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 1:27:33 PM)
 * @param newNextScan3 java.util.GregorianCalendar
 */
public void setNextScan3(java.util.GregorianCalendar newNextScan3) {
	nextScan3 = newNextScan3;
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 1:27:33 PM)
 * @param newPrevFreezeNumber java.lang.Integer
 */
public void setPrevFreezeNumber(java.lang.Integer newPrevFreezeNumber) {
	prevFreezeNumber = newPrevFreezeNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 1:27:33 PM)
 * @param newPrevFreezeTime java.util.GregorianCalendar
 */
public void setPrevFreezeTime(java.util.GregorianCalendar newPrevFreezeTime) {
	prevFreezeTime = newPrevFreezeTime;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getDeviceID(), getLastFreezeTime(), getPrevFreezeTime(), getLastLPTime(), getLastFreezeNumber(),
		getPrevFreezeNumber(), getNextScan0(), getNextScan1(), getNextScan2(), getNextScan3() };
	
	Object constraintValues[] = { getDeviceID() };

	update( this.tableName, columns, setValues, constraintColumns, constraintValues );
}
}
