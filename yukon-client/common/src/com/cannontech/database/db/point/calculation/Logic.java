package com.cannontech.database.db.point.calculation;

/**
 * This type was created in VisualAge.
 */

public class Logic extends com.cannontech.database.db.DBPersistent {
	private Integer logicID = null;
	private String logicName = null;
	private Integer periodicRate = null;
	private String stateFlag = null;
	private String scriptName = null;

	private static final String tableName = "Logic";
/**
 * Point constructor comment.
 */
public Logic() {
	super();
	initialize( null, null, null, null, null );
}
/**
 * Point constructor comment.
 */
public Logic(Integer logicID) {
	super();
	initialize(logicID, null, null, null, null );
}
/**
 * Point constructor comment.
 */
public Logic( Integer logicID, String logicName, Integer periodicRate, String stateFlag, String scriptName ) {
	super();
	initialize( logicID, logicName, periodicRate, stateFlag, scriptName );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	Object addValues[]= { getLogicID(), getLogicName(), getPeriodicRate(), getStateFlag(), getScriptName() };

	add( this.tableName, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {
	delete( tableName, "LOGICID", getLogicID() );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getLogicID() {
	return logicID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getLogicName() {
	return logicName;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getPeriodicRate() {
	return periodicRate;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getScriptName() {
	return scriptName;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getStateFlag() {
	return stateFlag;
}
/**
 * This method was created in VisualAge.
 */
public void initialize( Integer logicID, String logicName, Integer periodicRate, String stateFlag, String scriptName ) {

	setLogicID( logicID );
	setLogicName( logicName );
	setPeriodicRate( periodicRate );
	setStateFlag( stateFlag );
	setScriptName( scriptName );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {
	String selectColumns[] = { "LOGICNAME", "PERIODRATE", "STATEFLAG", "SCRIPTNAME" };

	String constraintColumns[] = { "LOGICID" };
	Object constraintValues[] = { getLogicID() };

	Object results[] = retrieve( selectColumns, tableName, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setLogicName( (String) results[0] );
		setPeriodicRate( (Integer) results[1]);
		setStateFlag( (String) results[2]);
		setScriptName( (String) results[3]);
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
		
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setLogicID(Integer newValue) {
	this.logicID = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setLogicName(String newValue) {
	this.logicName = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setPeriodicRate(Integer newValue) {
	this.periodicRate = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setScriptName(String newValue) {
	this.scriptName = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setStateFlag(String newValue) {
	this.stateFlag = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {
	String setColumns[] = { "LOGICNAME", "PERIODICRATE", "STATEFLAG", "SCRIPTNAME" };
	Object setValues[]= { getLogicName(), getPeriodicRate(), getStateFlag(), getScriptName() };

	String constraintColumns[] = { "LOGICID" };
	Object constraintValues[] = { getLogicID() };

	update( tableName, setColumns, setValues, constraintColumns, constraintValues );
}
}
