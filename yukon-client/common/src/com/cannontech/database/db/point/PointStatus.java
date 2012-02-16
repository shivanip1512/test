package com.cannontech.database.db.point;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.point.StateControlType;

/**
 * This type was created in VisualAge.
 */
public class PointStatus extends com.cannontech.database.db.DBPersistent 
{
	public static final int DEFAULT_CMD_TIMEOUT = 0;
	
	private Integer pointID = null;
	private Integer initialState = new Integer(0);
	private String controlType = com.cannontech.database.data.point.PointTypes.getType(com.cannontech.database.data.point.PointTypes.CONTROLTYPE_NONE);
	private Character controlInhibit = new Character('N');
	private Integer controlOffset = new Integer(1);
	private Integer closeTime1 = new Integer(0);
	private Integer closeTime2 = new Integer(0);
	private String stateZeroControl = StateControlType.OPEN.getControlName();
	private String stateOneControl = StateControlType.CLOSE.getControlName();
	private Integer commandTimeOut = new Integer(DEFAULT_CMD_TIMEOUT);

	public static final String CONSTRAINT_COLUMNS[] = { "POINTID" };

	public static final String SETTER_COLUMNS[] = 
	{ 
		"InitialState", "ControlType", "ControlInhibit", "ControlOffset",
		"CloseTime1", "CloseTime2", "StateZeroControl", "StateOneControl",
		"CommandTimeOut"
	};

	public static final String TABLE_NAME = "PointStatus";	
/**
 * PointStatus constructor comment.
 */
public PointStatus() 
{
	super();
}
/**
 * PointStatus constructor comment.
 */
public PointStatus( Integer ptID ) 
{
	super();

	setPointID( ptID );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {

	Object addValues[] = { getPointID(), getInitialState(), 
			getControlType(), getControlInhibit(), getControlOffset(), 
			getCloseTime1(), getCloseTime2(),
			getStateZeroControl(), getStateOneControl(),
			getCommandTimeOut() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getPointID() );
}
/**
 * Insert the method's description here.
 * Creation date: (9/19/2001 2:33:55 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCloseTime1() {
	return closeTime1;
}
/**
 * Insert the method's description here.
 * Creation date: (9/19/2001 2:33:55 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCloseTime2() {
	return closeTime2;
}
/**
 * Insert the method's description here.
 * Creation date: (12/13/2001 4:13:21 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCommandTimeOut() {
	return commandTimeOut;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Character
 */
public Character getControlInhibit() {
	return controlInhibit;
}
/**
 * Insert the method's description here.
 * Creation date: (9/19/2001 2:33:55 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getControlOffset() {
	return controlOffset;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getControlType() {
	return controlType;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getInitialState() {
	return initialState;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getPointID() {
	return pointID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/2001 10:29:25 AM)
 * @return java.lang.String
 */
public java.lang.String getStateOneControl() {
	return stateOneControl;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/2001 10:29:25 AM)
 * @return java.lang.String
 */
public java.lang.String getStateZeroControl() {
	return stateZeroControl;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getPointID() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setInitialState( (Integer) results[0] );
		setControlType( (String) results[1] );

		String temp = (String) results[2];
		if( temp != null )
			setControlInhibit( new Character( temp.charAt(0)) );

		setControlOffset( (Integer) results[3] );
		setCloseTime1( (Integer) results[4] );
		setCloseTime2( (Integer) results[5] );
		setStateZeroControl( (String) results[6] );
		setStateOneControl( (String) results[7] );
		setCommandTimeOut( (Integer) results[8] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
}
/**
 * Insert the method's description here.
 * Creation date: (9/19/2001 2:33:55 PM)
 * @param newCloseTime1 java.lang.Integer
 */
public void setCloseTime1(java.lang.Integer newCloseTime1) {
	closeTime1 = newCloseTime1;
}
/**
 * Insert the method's description here.
 * Creation date: (9/19/2001 2:33:55 PM)
 * @param newCloseTime2 java.lang.Integer
 */
public void setCloseTime2(java.lang.Integer newCloseTime2) {
	closeTime2 = newCloseTime2;
}
/**
 * Insert the method's description here.
 * Creation date: (12/13/2001 4:13:21 PM)
 * @param newCommandTimeOut java.lang.Integer
 */
public void setCommandTimeOut(java.lang.Integer newCommandTimeOut) {
	commandTimeOut = newCommandTimeOut;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Character
 */
public void setControlInhibit(Character newValue) {
	this.controlInhibit = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (9/19/2001 2:33:55 PM)
 * @param newControlOffset java.lang.Integer
 */
public void setControlOffset(java.lang.Integer newControlOffset) {
	controlOffset = newControlOffset;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setControlType(String newValue) {
	this.controlType = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setInitialState(Integer newValue) {
	this.initialState = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setPointID(Integer newValue) {
	this.pointID = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/2001 10:29:25 AM)
 * @param newStateOneControl java.lang.String
 */
public void setStateOneControl(java.lang.String newStateOneControl) {
	stateOneControl = newStateOneControl;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/2001 10:29:25 AM)
 * @param newStateZeroControl java.lang.String
 */
public void setStateZeroControl(java.lang.String newStateZeroControl) {
	stateZeroControl = newStateZeroControl;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getInitialState(), getControlType(), 
		getControlInhibit(), getControlOffset(), 
		getCloseTime1(), getCloseTime2(),
		getStateZeroControl(), getStateOneControl(), getCommandTimeOut() };

	Object constraintValues[] = { getPointID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );	
}


public boolean isControlDisabled() {
	return CtiUtilities.isTrue( getControlInhibit() );
}

public void setControlDisabled( boolean val ) {
	setControlInhibit( 
		val ? CtiUtilities.trueChar : CtiUtilities.falseChar );
}

}
