package com.cannontech.database.db.port;

/**
 * This type was created in VisualAge.
 */
import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

public class CommPort extends DBPersistent 
{
	public static final int DEFAULT_SHARED_SOCKET_NUMBER = 1025;
	public static final String SHARE_ACS = "ACS";
	public static final String SHARE_VALMET = "VALMET";
	public static final String SHARE_ILEX = "ILEX";
	
	private Integer portID = null;
	private Character alarmInhibit = null;
	private String commonProtocol = null;
	private Integer performThreshold = null;
	private Character performanceAlarm = null;

	private String sharedPortType = null;
	private Integer sharedSocketNumber = null;

	public static final String CONSTRAINT_COLUMNS[] = { "PortID" };
	
	public static String SETTER_COLUMNS[] = 
	{ 
		"AlarmInhibit", "CommonProtocol", "PerformThreshold", 
		"PerformanceAlarm", "SharedPortType", "SharedSocketNumber" 
	};
	
	public static final String TABLE_NAME = "CommPort";
/**
 * CommPort constructor comment.
 */
public CommPort() 
{
	super();
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 */
public CommPort(Integer portNumber) {
	super();
	initialize( portNumber, null, null, null, null );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {

	Object values[] = { getPortID(), 
		getAlarmInhibit(), getCommonProtocol(), 
		getPerformThreshold(), getPerformanceAlarm(), getSharedPortType(),
		getSharedSocketNumber() };

	add( TABLE_NAME, values );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {
	
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getPortID() );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Character
 */
public Character getAlarmInhibit() {
	return alarmInhibit;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer[]
 */
public static Integer[] getAllPortNumbers() throws SQLException{

	return getAllPortNumbers(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer[]
 */
public static Integer[] getAllPortNumbers(String databaseAlias) throws SQLException{

	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT PortID FROM CommPort",
													databaseAlias );

	Integer[] retVal = null;
	try
	{
		stmt.execute();

		retVal = new Integer[stmt.getRowCount()];

		for( int i = 0; i < retVal.length; i++ )
		{
			retVal[i] = new Integer( ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue());
		}	
		
	}
	catch( Exception e )
	{	
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
															
	return retVal;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getCommonProtocol() {
	return commonProtocol;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Character
 */
public Character getPerformanceAlarm() {
	return performanceAlarm;
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
 * @return java.lang.Integer
 */
public Integer getPortID() {
	return portID;
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 3:07:11 PM)
 * @return java.lang.String
 */
public java.lang.String getSharedPortType() {
	return sharedPortType;
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 3:07:11 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSharedSocketNumber() {
	return sharedSocketNumber;
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 * @param description java.lang.String
 * @param portType java.lang.Integer
 * @param currentState java.lang.String
 * @param disableFlag java.lang.Character
 * @param alarmInhibit java.lang.Character
 * @param commonProtocol java.lang.String
 * @param performThreshold java.lang.Integer
 * @param performanceAlarm java.lang.Character
 */
private void initialize( Integer portID, Character alarmInhibit, String commonProtocol, 
								 Integer performThreshold, Character performanceAlarm ) 
{

	setPortID( portID );
	setAlarmInhibit( alarmInhibit );
	setCommonProtocol( commonProtocol );
	setPerformThreshold( performThreshold );
	setPerformanceAlarm( performanceAlarm );
	
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object keyColumnValues[] = { getPortID() };
	
 	if( getPortID() == null )
 		return;

 	Object result[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, keyColumnValues);
 	
	if( result.length == SETTER_COLUMNS.length )
	{	
		setAlarmInhibit( new Character( ((String)result[0]).charAt(0) ) );
		setCommonProtocol( (String) result[1] );
		setPerformThreshold(  (Integer) result[2] );
		setPerformanceAlarm( new Character( ((String)result[3]).charAt(0) ));
		setSharedPortType( (String) result[4] );
		setSharedSocketNumber(  (Integer) result[5] );		
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");


	
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Character
 */
public void setAlarmInhibit(Character newValue) {
	this.alarmInhibit = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setCommonProtocol(String newValue) {
	this.commonProtocol = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Character
 */
public void setPerformanceAlarm(Character newValue) {
	this.performanceAlarm = newValue;
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
 * @param newValue java.lang.Integer
 */
public void setPortID(Integer newValue) {
	this.portID = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 3:07:11 PM)
 * @param newSharedPortType java.lang.String
 */
public void setSharedPortType(java.lang.String newSharedPortType) {
	sharedPortType = newSharedPortType;
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 3:07:11 PM)
 * @param newSharedPortNumber java.lang.Integer
 */
public void setSharedSocketNumber(java.lang.Integer newSharedPortNumber) {
	sharedSocketNumber = newSharedPortNumber;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{	
	Object setColumnValue[] = {  getAlarmInhibit(), 
		getCommonProtocol(), getPerformThreshold(), 
		getPerformanceAlarm(), getSharedPortType(), getSharedSocketNumber() };
	
	Object constraintColumnValue[] = { getPortID() };

	update( TABLE_NAME, SETTER_COLUMNS, setColumnValue, CONSTRAINT_COLUMNS, constraintColumnValue );

}
}
