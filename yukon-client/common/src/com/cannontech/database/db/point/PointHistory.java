package com.cannontech.database.db.point;

/**
 * This type was created in VisualAge.
 */
public class PointHistory extends com.cannontech.database.db.DBPersistent {
	private Integer pointID = null;
	private java.util.GregorianCalendar timeStamp = null;
	private Integer quality = null;
	private Double value = null;
	
	private final static String tableName = "PointHistory";
/**
 * PointHistory constructor comment.
 */
public PointHistory() {
	super();
	initialize( null, null, null, null );
}
/**
 * PointHistory constructor comment.
 */
public PointHistory(Integer pointID) {
	super();
	initialize( pointID, null, null, null );
}
/**
 * PointHistory constructor comment.
 */
public PointHistory(Integer pointID, java.util.GregorianCalendar timeStamp) {
	super();
	initialize( pointID, timeStamp, null, null);
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {

	Object addValues[] = { getPointID(), getTimestamp(), getQuality(), getValue() };

	add( this.tableName, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( this.tableName, "POINTID", getPointID() );
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
 * @param newValue java.lang.String
 */
public Integer getQuality() {
	return quality;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public java.util.GregorianCalendar getTimestamp() {
	return timeStamp;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public Double getValue() {
	return value;
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 * @param historyType java.lang.String
 */
public void initialize(Integer pointID, java.util.GregorianCalendar timeStamp, Integer quality, Double value ) {

	setPointID( pointID );
	setTimestamp( timeStamp );
	setQuality( quality );
	setValue( value );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	String selectColumns[] = { "TIMESTAMP", "QUALITY", "VALUE" };

	String constraintColumns[] = { "POINTID" };
	Object constraintValues[] = { getPointID() };

	Object[] results = retrieve( selectColumns, this.tableName, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		java.util.GregorianCalendar tempCal = new java.util.GregorianCalendar();
		tempCal.setTime( new java.util.Date(((java.sql.Timestamp)results[0]).getTime()) );
		setTimestamp( tempCal );
		setQuality( (Integer) results[1] );
		setValue( (Double) results[2] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
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
public void setQuality(Integer newValue) {
	this.quality = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setTimestamp(java.util.GregorianCalendar newValue) {
	this.timeStamp = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setValue(Double newValue) {
	this.value = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {

	String setColumns[]= { "TIMESTAMP", "QUALITY", "VALUE"  };
	Object setValues[] = { getTimestamp(), getQuality(), getValue() };

	String constraintColumns[] = { "POINTID" };
	Object constraintValues[] = { getPointID() };

	update( this.tableName, setColumns, setValues, constraintColumns, constraintValues );
}
}
