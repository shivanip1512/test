package com.cannontech.database.db.device.lm;

/**
 * This type was created in VisualAge.
 */
public class LMControlArea extends com.cannontech.database.db.DBPersistent 
{
	public static final String OPSTATE_NONE = "None";
	public static final String OPSTATE_ENABLED = "Enabled";
	public static final String OPSTATE_DISABLED = "Disabled";
	public static final int OPTIONAL_VALUE_UNUSED = -1;

	private Integer deviceID = null;
	private String defOperationalState = OPSTATE_NONE;
	private Integer controlInterval = new Integer(0);
	private Integer minResponseTime = new Integer(0);
	private Integer defDailyStartTime = new Integer(0);
	private Integer defDailyStopTime = new Integer(0);
	private Character requireAllTriggersActiveFlag = new Character('f');
	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"DefOperationalState", "ControlInterval", "MinResponseTime", 
		"DefDailyStartTime", "DefDailyStopTime", "RequireAllTriggersActiveFlag" 
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };


	public static final String TABLE_NAME = "LMControlArea";
/**
 * LMGroupEmetcon constructor comment.
 */
public LMControlArea() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getDefOperationalState(), getControlInterval(), getMinResponseTime(), 
				getDefDailyStartTime(), getDefDailyStopTime(), getRequireAllTriggersActiveFlag() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {
	delete( TABLE_NAME, "DeviceID", getDeviceID() );
}
/**
 * Insert the method's description here.
 * Creation date: (3/15/2001 12:30:47 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getControlInterval() {
	return controlInterval;
}
/**
 * Insert the method's description here.
 * Creation date: (3/15/2001 12:30:47 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDefDailyStartTime() {
	return defDailyStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/15/2001 12:30:47 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDefDailyStopTime() {
	return defDailyStopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/15/2001 12:30:47 PM)
 * @return java.lang.String
 */
public java.lang.String getDefOperationalState() {
	return defOperationalState;
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
 * Creation date: (3/15/2001 12:30:47 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMinResponseTime() {
	return minResponseTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/15/2001 12:30:47 PM)
 * @return Character
 */
public Character getRequireAllTriggersActiveFlag() 
{
	return requireAllTriggersActiveFlag;
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
		setDefOperationalState( (String) results[0] );
		setControlInterval( (Integer) results[1] );
		setMinResponseTime( (Integer) results[2] );
		setDefDailyStartTime( (Integer) results[3] );
		setDefDailyStopTime( (Integer) results[4] );
		setRequireAllTriggersActiveFlag( new Character( ((String)results[5]).charAt(0) ) );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}
/**
 * Insert the method's description here.
 * Creation date: (3/15/2001 12:30:47 PM)
 * @param newControlInterval java.lang.Integer
 */
public void setControlInterval(java.lang.Integer newControlInterval) {
	controlInterval = newControlInterval;
}
/**
 * Insert the method's description here.
 * Creation date: (3/15/2001 12:30:47 PM)
 * @param newDefDailyStartTime java.lang.Integer
 */
public void setDefDailyStartTime(java.lang.Integer newDefDailyStartTime) {
	defDailyStartTime = newDefDailyStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/15/2001 12:30:47 PM)
 * @param newDefDailyStopTime java.lang.Integer
 */
public void setDefDailyStopTime(java.lang.Integer newDefDailyStopTime) {
	defDailyStopTime = newDefDailyStopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/15/2001 12:30:47 PM)
 * @param newDefOperationalState java.lang.String
 */
public void setDefOperationalState(java.lang.String newDefOperationalState) {
	defOperationalState = newDefOperationalState;
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
 * Creation date: (3/15/2001 12:30:47 PM)
 * @param newMinResponseTime java.lang.Integer
 */
public void setMinResponseTime(java.lang.Integer newMinResponseTime) {
	minResponseTime = newMinResponseTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/15/2001 12:30:47 PM)
 * @param newRequireAllTriggersActiveFlag Character
 */
public void setRequireAllTriggersActiveFlag(Character newRequireAllTriggersActiveFlag) 
{
	requireAllTriggersActiveFlag = newRequireAllTriggersActiveFlag;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getDefOperationalState(), getControlInterval(), getMinResponseTime(), 
			getDefDailyStartTime(), getDefDailyStopTime(), getRequireAllTriggersActiveFlag() };

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
