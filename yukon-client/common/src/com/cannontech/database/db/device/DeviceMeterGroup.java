package com.cannontech.database.db.device;

/**
 * This type was created in VisualAge.
 */
public class DeviceMeterGroup extends com.cannontech.database.db.DBPersistent 
{
	private Integer deviceID = null;
	private String collectionGroup = com.cannontech.common.util.CtiUtilities.STRING_DEFAULT;
	private String testCollectionGroup = com.cannontech.common.util.CtiUtilities.STRING_DEFAULT;
	private String meterNumber = com.cannontech.common.util.CtiUtilities.STRING_DEFAULT;
	private String billingGroup = "MainCycle";

	public static final String SETTER_COLUMNS[] = 
	{ 
		"CollectionGroup", "TestCollectionGroup", "MeterNumber",
		"BillingGroup"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };
	
	public static final String TABLE_NAME = "DeviceMeterGroup";
/**
 * DeviceMeterGroup constructor comment.
 */
public DeviceMeterGroup() 
{
	super();
}
/**
 * DeviceMeterGroup constructor comment.
 */
public DeviceMeterGroup(Integer devID, String collectGroup, String testCollGroup, String mtrNumber, String billingGrp ) {
	super();
	setDeviceID( devID );
	setCollectionGroup( collectGroup );
	setTestCollectionGroup( testCollGroup );
	setMeterNumber( mtrNumber );
	setBillingGroup( billingGrp );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getCollectionGroup(), 
		getTestCollectionGroup(), getMeterNumber(), getBillingGroup() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( "DeviceMeterGroup", "DeviceID", getDeviceID() );
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 1:00:46 PM)
 * @return java.lang.String
 */
public java.lang.String getBillingGroup() {
	return billingGroup;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 1:00:46 PM)
 * @return java.lang.String
 */
public java.lang.String getCollectionGroup() {
	return collectionGroup;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer[]
 */
public final static String[] getDeviceCollectionGroups() throws java.sql.SQLException{

	return getDeviceCollectionGroups(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer[]
 */
public final static String[] getDeviceCollectionGroups(String databaseAlias) throws java.sql.SQLException
{
	String retVal[] = null;	
 	
 	com.cannontech.database.SqlStatement stmt =
 		new com.cannontech.database.SqlStatement(
	 		"SELECT DISTINCT CollectionGroup FROM " + TABLE_NAME, databaseAlias );
 												 
 	try
 	{											
		stmt.execute();

		retVal = new String[stmt.getRowCount()];
	
		for( int i = 0; i < stmt.getRowCount(); i++ )
			retVal[i] = new String( ((String)stmt.getRow(i)[0]) );	
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}	

 	return retVal;
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
 * @return java.lang.Integer[]
 */
public final static Integer[] getDeviceIDs_CollectionGroups(String databaseAlias, String cycleGroup) throws java.sql.SQLException
{
	Integer retVal[] = null;	

	String sqlString = "SELECT DISTINCT DEVICEID FROM " + TABLE_NAME + 
		" WHERE CollectionGroup = \'" + cycleGroup + "\'";
			
 	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement(sqlString, databaseAlias );
		
	try
 	{											
		stmt.execute();

		retVal = new Integer[stmt.getRowCount()];

		for( int i = 0; i < stmt.getRowCount(); i++ )
			retVal[i] = new Integer( ((java.math.BigDecimal) stmt.getRow(i)[0] ).intValue());
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}	

 	return retVal;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer[]
 */
public final static Integer[] getDeviceIDs_TestCollectionGroups(String databaseAlias, String areaCodeGroup) throws java.sql.SQLException
{
	Integer retVal[] = null;	

	String sqlString = "SELECT DISTINCT DEVICEID FROM " + TABLE_NAME + 
		" WHERE TestCollectionGroup = \'" + areaCodeGroup + "\'";

 	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement(sqlString, databaseAlias );
		
	try
 	{											
		stmt.execute();

		retVal = new Integer[stmt.getRowCount()];

		for( int i = 0; i < stmt.getRowCount(); i++ )
			retVal[i] = new Integer( ((java.math.BigDecimal) stmt.getRow(i)[0] ).intValue());
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}	

 	return retVal;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer[]
 */
public final static String[] getDeviceTestCollectionGroups() throws java.sql.SQLException{

	return getDeviceTestCollectionGroups(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer[]
 */
public final static String[] getDeviceTestCollectionGroups(String databaseAlias) throws java.sql.SQLException
{
	String retVal[] = null;	
 	
 	com.cannontech.database.SqlStatement stmt =
 		new com.cannontech.database.SqlStatement(
	 		"SELECT DISTINCT TestCollectionGroup FROM " + TABLE_NAME, databaseAlias );
 												 
 	try
 	{											
		stmt.execute();

		retVal = new String[stmt.getRowCount()];
	
		for( int i = 0; i < stmt.getRowCount(); i++ )
			retVal[i] = new String( ((String)stmt.getRow(i)[0]) );	
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}	

 	return retVal;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public String getMeterNumber() {
	return meterNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 1:00:46 PM)
 * @return java.lang.String
 */
public java.lang.String getTestCollectionGroup() {
	return testCollectionGroup;
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
		setCollectionGroup( (String) results[0] );
		setTestCollectionGroup( (String) results[1] );
		setMeterNumber( (String) results[2] );
		setBillingGroup( (String) results[3] );
	}

}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 1:00:46 PM)
 * @param newBillingGroup java.lang.String
 */
public void setBillingGroup(java.lang.String newBillingGroup) {
	billingGroup = newBillingGroup;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 1:00:46 PM)
 * @param newCollectionGroup java.lang.String
 */
public void setCollectionGroup(java.lang.String newCollectionGroup) {
	collectionGroup = newCollectionGroup;
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
 * @param newValue java.lang.Integer
 */
public void setMeterNumber(String newValue) {
	this.meterNumber = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 1:00:46 PM)
 * @param newTestCollectionGroup java.lang.String
 */
public void setTestCollectionGroup(java.lang.String newTestCollectionGroup) {
	testCollectionGroup = newTestCollectionGroup;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getCollectionGroup(), 
		getTestCollectionGroup(), getMeterNumber(), getBillingGroup() };

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
