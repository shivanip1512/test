package com.cannontech.database.db.point;

/**
 * This type was created in VisualAge.
 */
public class PeakPointHistory extends com.cannontech.database.db.DBPersistent {
	
	private Integer pointID = null;
	private java.util.GregorianCalendar timeStamp = null;
	private Double value = null;
	
	public final static String TABLE_NAME = "PeakPointHistory";
/**
 * PointDispatch constructor comment.
 */
public PeakPointHistory() {
	super();
	initialize( null, null, null );
}
/**
 * PointDispatch constructor comment.
 */
public PeakPointHistory(Integer pointID) {
	super();
	initialize( pointID, null, null );
}
/**
 * PointDispatch constructor comment.
 */
public PeakPointHistory( Integer pointID, java.util.GregorianCalendar timeStamp, Double value ) 
{
	super();
	initialize( pointID, timeStamp, value );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	Object addValues[] = { getPointID(), getTimeStamp(), getValue() };

	add( this.TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException
{
	String constraintColumns[] = { "POINTID" };
	Object constraintValues[] = { getPointID() };
	
	delete( this.TABLE_NAME, constraintColumns, constraintValues  );
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
public java.util.GregorianCalendar getTimeStamp() {
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
 */
public void initialize( Integer pointID, java.util.GregorianCalendar timeStamp, Double value ) 
{
	setPointID( pointID );
	setTimeStamp( timeStamp ) ;
	setValue( value );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	String selectColumns[] = { "TIMESTAMP", "VALUE" };

	String constraintColumns[] = { "POINTD" };
	Object constraintValues[] = { getPointID() };

	Object results[] = retrieve( selectColumns, this.TABLE_NAME, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		java.util.GregorianCalendar tempCal = new java.util.GregorianCalendar();
		tempCal.setTime( new java.util.Date(((java.sql.Timestamp)results[0]).getTime()) );
		setTimeStamp( tempCal );
		setValue( (Double) results[1] );
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
public void setTimeStamp(java.util.GregorianCalendar newValue) {
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
	String setColumns[]= { "TIMESTAMP", "VALUE" };
	Object setValues[] = {  getTimeStamp(), getValue() };
	String constraintColumns[] = { "POINTID" };
	Object constraintValues[] = { getPointID() };

	update( this.TABLE_NAME, setColumns, setValues, constraintColumns, constraintValues );
}
}
