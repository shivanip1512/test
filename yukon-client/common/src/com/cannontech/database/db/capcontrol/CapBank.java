package com.cannontech.database.db.capcontrol;

/**
 * This type was created in VisualAge.
 */
public class CapBank extends com.cannontech.database.db.DBPersistent 
{
	private Integer deviceID = null;
	private String operationalState = null;
	private String controllerType = com.cannontech.common.util.CtiUtilities.STRING_NONE;
	private Integer controlDeviceID = new Integer(com.cannontech.database.db.device.Device.SYSTEM_DEVICE_ID);
	private Integer controlPointID = new Integer(com.cannontech.database.data.point.PointTypes.SYS_PID_SYSTEM);
	private Integer bankSize = null;
	private String typeOfSwitch = com.cannontech.common.util.CtiUtilities.STRING_NONE;
	private String switchManufacture = com.cannontech.common.util.CtiUtilities.STRING_NONE;
	private Integer mapLocationID = new Integer(0);
	private Integer recloseDelay = new Integer(0);

	public static final String SETTER_COLUMNS[] = 
	{ 
		"OperationalState", "ControllerType", "ControlDeviceID",
		"ControlPointID", "BankSize", "TypeOfSwitch",
		"SwitchManufacture", "MapLocationID", "RecloseDelay"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "CapBank";
/**
 * DeviceTwoWayFlags constructor comment.
 */
public CapBank() {
	super();
}
/**
 * DeviceTwoWayFlags constructor comment.
 */
public CapBank(Integer capBankID) {
	super();
	setDeviceID( capBankID );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException
{
	Object[] addValues = 
	{  
		getDeviceID(), getOperationalState(),
		getControllerType(), getControlDeviceID(), 
		getControlPointID(), getBankSize(), 
		getTypeOfSwitch(), getSwitchManufacture(),
		getMapLocationID(), getRecloseDelay()
	};

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( this.TABLE_NAME, "DeviceID", getDeviceID() );
	
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 11:42:46 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getBankSize() {
	return bankSize;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getControlDeviceID() {
	return controlDeviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 11:35:12 AM)
 * @return java.lang.String
 */
public java.lang.String getControllerType() {
	return controllerType;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getControlPointID() {
	return controlPointID;
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
 * Creation date: (12/6/2001 2:05:57 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMapLocationID() {
	return mapLocationID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 11:35:12 AM)
 * @return java.lang.String
 */
public java.lang.String getOperationalState() {
	return operationalState;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 11:43:31 AM)
 * @return java.lang.String
 */
public java.lang.String getSwitchManufacture() {
	return switchManufacture;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 11:43:31 AM)
 * @return java.lang.String
 */
public java.lang.String getTypeOfSwitch() {
	return typeOfSwitch;
}

public Integer getRecloseDelay() {
	return recloseDelay;
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 *
 * This method returns all the CapBanks that are not assgined
 *  to a Feeder.
 */
public static java.util.Vector getUnassignedCapBanksList()
{
	java.util.Vector returnVector = null;
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;


	String sql = "SELECT DeviceID FROM " + TABLE_NAME + " where " +
					 " deviceid not in (select deviceid from " + CCFeederBankList.TABLE_NAME +
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
						new CapBank(  new Integer(rset.getInt("DeviceID")) ) );
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
		setOperationalState( (String) results[0] );
		setControllerType( (String) results[1] );
		setControlDeviceID( (Integer) results[2] );
		setControlPointID( (Integer) results[3] );
		setBankSize( (Integer) results[4] );
		setTypeOfSwitch( (String) results[5] );
		setSwitchManufacture( (String) results[6] );
		setMapLocationID( (Integer) results[7] );
		setRecloseDelay( (Integer) results[8] );
	}

}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 11:42:46 AM)
 * @param newBankSize java.lang.Integer
 */
public void setBankSize(java.lang.Integer newBankSize) {
	bankSize = newBankSize;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setControlDeviceID(Integer newValue) {
	this.controlDeviceID = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 11:35:12 AM)
 * @param newControllerType java.lang.String
 */
public void setControllerType(java.lang.String newControllerType) {
	controllerType = newControllerType;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setControlPointID(Integer newValue) {
	this.controlPointID = newValue;
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
 * Creation date: (12/6/2001 2:05:57 PM)
 * @param newMapLocationID java.lang.Integer
 */
public void setMapLocationID(java.lang.Integer newMapLocationID) {
	mapLocationID = newMapLocationID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 11:35:12 AM)
 * @param newOperationalState java.lang.String
 */
public void setOperationalState(java.lang.String newOperationalState) {
	operationalState = newOperationalState;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 11:43:31 AM)
 * @param newSwitchManufacture java.lang.String
 */
public void setSwitchManufacture(java.lang.String newSwitchManufacture) {
	switchManufacture = newSwitchManufacture;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 11:43:31 AM)
 * @param newTypeOfSwitch java.lang.String
 */
public void setTypeOfSwitch(java.lang.String newTypeOfSwitch) {
	typeOfSwitch = newTypeOfSwitch;
}

public void setRecloseDelay(Integer newValue) {
	recloseDelay = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{

	Object setValues[]= 
	{  
		getOperationalState(),
		getControllerType(), getControlDeviceID(), 
		getControlPointID(), getBankSize(), 
		getTypeOfSwitch(), getSwitchManufacture(),
		getMapLocationID(), getRecloseDelay()
	};

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
