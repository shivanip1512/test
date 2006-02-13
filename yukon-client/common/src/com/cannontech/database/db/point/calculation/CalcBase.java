package com.cannontech.database.db.point.calculation;

/**
 * This type was created in VisualAge.
 */

public class CalcBase extends com.cannontech.database.db.DBPersistent {
	private Integer pointID = null;
	private String updateType = null;
	private Integer periodicRate = null;
    private char calculateQuality = 'N';

	private static final String tableName = "CalcBase";
/**
 * Point constructor comment.
 */
public CalcBase() {
	super();
	initialize( null, null, null, 'N' );
}
/**
 * Point constructor comment.
 */
public CalcBase(Integer pointID) {
	super();
	initialize(pointID, null, null, 'N' );
}
/**
 * Point constructor comment.
 */
public CalcBase(Integer pointID, String updateType, Integer periodicRate ) {
	super();
	initialize(pointID, updateType, periodicRate, 'N');
}

/**
 * Point constructor comment.
 */
public CalcBase(Integer pointID, String updateType, Integer periodicRate, char calcQual ) {
    super();
    initialize(pointID, updateType, periodicRate, calcQual);
}

/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	Object addValues[]= { getPointID(), getUpdateType(), getPeriodicRate() };

	add( this.tableName, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {
	delete( tableName, "POINTID", getPointID() );
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
 * @return java.lang.Integer
 */
public Integer getPointID() {
	return pointID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getUpdateType() {
	return updateType;
}

public char getCalculateQuality() {
    return calculateQuality;
}

/**
 * This method was created in VisualAge.
 * @param pointType java.lang.String
 * @param pointName java.lang.String
 * @param deviceID java.lang.Integer
 * @param logicalGroup java.lang.String
 * @param stateName java.lang.String
 * @param serviceFlag java.lang.Character
 * @param alarmInhibit java.lang.Character
 * @param alarmClass java.lang.Integer
 * @param pseudoFlag java.lang.Character
 */
public void initialize(Integer pointID, String updateType, Integer periodicRate, char calcQaul ) {

	setPointID( pointID ) ;
	setUpdateType( updateType );
	setPeriodicRate( periodicRate );
    setCalculateQuality( calcQaul );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {
	String selectColumns[] = { "UPDATETYPE", "PERIODICRATE", "QUALITYFLAG" };

	String constraintColumns[] = { "POINTID" };
	Object constraintValues[] = { getPointID() };

	Object results[] = retrieve( selectColumns, tableName, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setUpdateType( (String) results[0] );
		setPeriodicRate( (Integer) results[1]);
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
		
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
 * @param newValue java.lang.Integer
 */
public void setPointID(Integer newValue) {
	this.pointID = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setUpdateType(String newValue) {
	this.updateType = newValue;
}

public void setCalculateQuality(char newValue){
    this.calculateQuality = newValue;
}

/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {
	String setColumns[] = { "UPDATETYPE", "PERIODICRATE", "QUALITYFLAG" };
	Object setValues[]= { getUpdateType(), getPeriodicRate(), getCalculateQuality() };

	String constraintColumns[] = { "POINTID" };
	Object constraintValues[] = { getPointID() };

	update( tableName, setColumns, setValues, constraintColumns, constraintValues );
}
}
