package com.cannontech.database.db.starshardware;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class LMHardware extends com.cannontech.database.db.DBPersistent 
{
	private Integer hardwareID = null;
	private Integer manufacturerSerialNumber = null;
	private String lmDeviceType = null;

	public static final String[] SETTER_COLUMNS = 
	{ 
		"ManufacturerSerialNumber", "LMDeviceType"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "HardwareID" };
	
	public static final String TABLE_NAME = "LMHardware";
/**
 * CustomerWebSettings constructor comment.
 */
public LMHardware() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
	{ 
		getHardwareID(), getManufacturerSerialNumber(), getLmDeviceType()
	};

	add( TABLE_NAME, addValues );
}
/**
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getHardwareID() );
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:16:48 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getHardwareID() {
	return hardwareID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:16:48 PM)
 * @return java.lang.String
 */
public java.lang.String getLmDeviceType() {
	return lmDeviceType;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:17:59 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getManufacturerSerialNumber() {
	return manufacturerSerialNumber;
}
/**
 */
public void retrieve() throws java.sql.SQLException 
{
	Object[] constraintValues =  { getHardwareID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setManufacturerSerialNumber( (Integer) results[0] );
		setLmDeviceType( (String) results[1] );
	}
	else
		throw new RuntimeException("Incorrect number of columns in result");
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:16:48 PM)
 * @param newHardwareID java.lang.Integer
 */
public void setHardwareID(java.lang.Integer newHardwareID) {
	hardwareID = newHardwareID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:16:48 PM)
 * @param newLmDeviceType java.lang.String
 */
public void setLmDeviceType(java.lang.String newLmDeviceType) {
	lmDeviceType = newLmDeviceType;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:17:59 PM)
 * @param newManufacturerSerialNumber java.lang.Integer
 */
public void setManufacturerSerialNumber(java.lang.Integer newManufacturerSerialNumber) {
	manufacturerSerialNumber = newManufacturerSerialNumber;
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	Object[] setValues = 
	{
		getManufacturerSerialNumber(), getLmDeviceType()
	};

	Object[] constraintValues =  { getHardwareID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
