package com.cannontech.database.db.device;

/**
 * This type was created in VisualAge.
 *
 * The methods in this class (udpate,retrieve,delete,add)
 * only execute fully if all of its attributes are not NULL.
 *
 * 
 */
public class DeviceWindow extends com.cannontech.database.db.DBPersistent 
{
	//possible types
	public static final String TYPE_SCAN = "SCAN";
	public static final String TYPE_PEAK = "PEAK";
	public static final String TYPE_ALT_RATE = "ALTERNATE RATE";
	
	private Integer deviceID = null;
	private String type = TYPE_SCAN;
	private Integer winOpen = new Integer(0);
	private Integer winClose = new Integer(0);
	private Integer alternateOpen = new Integer(0);
	private Integer alternateClose = new Integer(0);

	public static final String SETTER_COLUMNS[] = 
	{ 
		"Type", "WinOpen", "WinClose",
		"AlternateOpen", "AlternateClose"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };
	
	public static final String TABLE_NAME = "DeviceWindow";

/**
 * DeviceDialupSettings constructor comment.
 */
public DeviceWindow() 
{
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	if( getWinOpen().intValue() == 0 && getWinClose().intValue() == 0 )
		return;

	Object addValues[] = { getDeviceID(), getType(), 
			getWinOpen(), getWinClose(), getAlternateOpen(), 
			getAlternateClose() };
	
	if( isValidValues(addValues) )
		add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	if( getDeviceID() != null )
		delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getDeviceID() );
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/2001 3:31:31 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getAlternateClose() {
	return alternateClose;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/2001 1:46:38 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getAlternateOpen() {
	return alternateOpen;
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
 * Creation date: (9/27/2001 9:44:16 AM)
 * @return java.lang.String
 */
public java.lang.String getType() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/2001 4:58:19 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getWinClose() {
	return winClose;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/2001 4:58:19 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getWinOpen() {
	return winOpen;
}
/**
 * Insert the method's description here.
 * Creation date: (9/27/2001 10:30:24 AM)
 * @return boolean
 */
private boolean isValidValues( Object[] values ) 
{
	if( values == null )
		return false;

	for( int i = 0; i < values.length; i++ )
		if( values[i] == null )
			return false;


	return true;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	if( getDeviceID() != null )
	{
		Object constraintValues[] = { getDeviceID() } ;

		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

		if( results.length == SETTER_COLUMNS.length )
		{
			setType( (String) results[0] );
			setWinOpen( (Integer) results[1] );
			setWinClose( (Integer) results[2] );
			setAlternateOpen( (Integer) results[3] );
			setAlternateClose( (Integer) results[4] );
		}
		//do not throw the ERROR here if we dont get back any columns!!!!
	}

}
/**
 * Insert the method's description here.
 * Creation date: (10/3/2001 3:31:31 PM)
 * @param newAlternateClose java.lang.Integer
 */
public void setAlternateClose(java.lang.Integer newAlternateClose) {
	alternateClose = newAlternateClose;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/2001 1:46:38 PM)
 * @param newAlternateOpen java.lang.Integer
 */
public void setAlternateOpen(java.lang.Integer newAlternateOpen) {
	alternateOpen = newAlternateOpen;
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
 * Creation date: (9/27/2001 9:44:16 AM)
 * @param newType java.lang.String
 */
public void setType(java.lang.String newType) {
	type = newType;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/2001 4:58:19 PM)
 * @param newWinClose java.lang.Integer
 */
public void setWinClose(java.lang.Integer newWinClose) {
	winClose = newWinClose;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/2001 4:58:19 PM)
 * @param newWinOpen java.lang.Integer
 */
public void setWinOpen(java.lang.Integer newWinOpen) {
	winOpen = newWinOpen;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	if( getWinOpen().intValue() == 0 
		 && getWinClose().intValue() == 0 )
	{
		delete();
		return;
	}
	
	Object setValues[] = { getType(), 
			getWinOpen(), getWinClose(), getAlternateOpen(), 
			getAlternateClose() };

	Object constraintValues[] = { getDeviceID() };
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		if ( results[0] != null )
		{
			if( isValidValues(setValues) && isValidValues(constraintValues) )
				update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
		}
		else
		{
			add();
		}
	}
	else
	{
		add();
	}
	
}
}
