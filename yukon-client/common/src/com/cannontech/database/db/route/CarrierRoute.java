package com.cannontech.database.db.route;

/**
 * This type was created in VisualAge.
 */
public class CarrierRoute extends com.cannontech.database.db.DBPersistent 
{
	private Integer routeID = null;
	private Integer busNumber = new Integer(1);
	private Integer ccuFixBits = new Integer(31);
	private Integer ccuVariableBits = new Integer(7);
	private String userLocked = "N";
	private String resetRptSettings =  "N";

	public static final String SETTER_COLUMNS[] = 
	{ 
		"BusNumber", "CCUFixBits", "CCUVariableBits",
		"UserLocked", "ResetRptSettings"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "RouteID" };
	
	public static final String TABLE_NAME = "CarrierRoute";
/**
 * CarrierRoute constructor comment.
 */
public CarrierRoute() 
{
	super();
}
/**
 * CarrierRoute constructor comment.
 */
public CarrierRoute( Integer routeID )
{
	super();

	setRouteID( routeID );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {

	Object addValues[] = { getRouteID(), getBusNumber(), 
				getCcuFixBits(), getCcuVariableBits(), getUserLocked(),
				getResetRptSettings() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getRouteID() );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getBusNumber() {
	return busNumber;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public Integer getCcuFixBits() {
	return ccuFixBits;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Character
 */
public Integer getCcuVariableBits() {
	return ccuVariableBits;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 3:15:09 PM)
 * @return java.lang.String
 */
public java.lang.String getResetRptSettings() {
	return resetRptSettings;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getRouteID() {
	return routeID;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 3:15:09 PM)
 * @return java.lang.String
 */
public java.lang.String getUserLocked() {
	return userLocked;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getRouteID() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		if( results[0] != null )
			setBusNumber( (Integer) results[0] );

		//Bytes are interpreted as Strings with
		//single characters and converted old ascii style
		if( results[1] != null )
			setCcuFixBits( (Integer) results[1] );

		if( results[2] != null )
			setCcuVariableBits( (Integer) results[2] );

		if( results[3] != null )
			setUserLocked( (String) results[3] );

		if( results[4] != null )
			setResetRptSettings( (String) results[4] );
	}


}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setBusNumber(Integer newValue) {
	this.busNumber = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setCcuFixBits(Integer newValue) {
	this.ccuFixBits = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Character
 */
public void setCcuVariableBits(Integer newValue) {
	this.ccuVariableBits = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 3:15:09 PM)
 * @param newResetRptSettings java.lang.String
 */
public void setResetRptSettings(java.lang.String newResetRptSettings) {
	resetRptSettings = newResetRptSettings;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setRouteID(Integer newValue) {
	this.routeID = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 3:15:09 PM)
 * @param newUserLocked java.lang.String
 */
public void setUserLocked(java.lang.String newUserLocked) {
	userLocked = newUserLocked;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getBusNumber(),
			getCcuFixBits(), getCcuVariableBits(), getUserLocked(),
			getResetRptSettings() };
	
	Object constraintValues[] = { getRouteID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
