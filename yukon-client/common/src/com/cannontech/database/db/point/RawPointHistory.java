package com.cannontech.database.db.point;

/**
 * This type was created in VisualAge.
 */
public class RawPointHistory extends com.cannontech.database.db.DBPersistent {
	
	private Integer changeID = null;
	private Integer pointID = null;
	private java.util.GregorianCalendar timeStamp = null;
	private Integer quality = null;
	private Double value = null;
	private Short millis = null;
	
	public final static String TABLE_NAME = "RawPointHistory";
/**
 * PointDispatch constructor comment.
 */
public RawPointHistory() {
	super();
	initialize( null, null ,null, null, null );
}
/**
 * PointDispatch constructor comment.
 */
public RawPointHistory(Integer pointID) {
	super();
	initialize( pointID, null ,null, null, null );
}
/**
 * PointDispatch constructor comment.
 */
public RawPointHistory( Integer changeID, Integer pointID,
												java.util.GregorianCalendar timeStamp, Integer quality,
												Double value ) 
{
	super();
	initialize( changeID, pointID, timeStamp, quality, value );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	Object addValues[] = { getChangeID(), getPointID(), getTimeStamp(), getQuality(), getValue() };

	add( this.TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException
{
	String constraintColumns[] = { "ChangeID" };
	Object constraintValues[] = { getChangeID() };
	
	delete( this.TABLE_NAME, constraintColumns, constraintValues  );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getChangeID() {
	return changeID;
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
public void initialize( Integer changeID, Integer pointID,
												java.util.GregorianCalendar timeStamp, Integer quality,
												Double value ) 
{
	setChangeID( changeID );
	setPointID( pointID );
	setTimeStamp( timeStamp ) ;
	setQuality( quality );
	setValue( value );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	String selectColumns[] = { "POINTID", "TIMESTAMP", "QUALITY", "VALUE", "millis" };

	String constraintColumns[] = { "CHANGEID" };
	Object constraintValues[] = { getChangeID() };

	Object results[] = retrieve( selectColumns, this.TABLE_NAME, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setPointID( (Integer) results[0] );
		java.util.GregorianCalendar tempCal = new java.util.GregorianCalendar();
		tempCal.setTime( new java.util.Date(((java.sql.Timestamp)results[1]).getTime()) );
		setTimeStamp( tempCal );
		setQuality( (Integer) results[2] );
		setValue( (Double) results[3] );
		
		setMillis( new Short( ((Integer)results[4]).shortValue() ) );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
	
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setChangeID(Integer newValue) {
	this.changeID = newValue;
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
	String setColumns[]= { "POINTID", "TIMESTAMP", "QUALITY", "VALUE", "millis" };
	Object setValues[] = { getPointID(), getTimeStamp(), getQuality(), getValue(), getMillis() };
	String constraintColumns[] = { "CHANGEID" };
	Object constraintValues[] = { getChangeID() };

	update( this.TABLE_NAME, setColumns, setValues, constraintColumns, constraintValues );
}

	/**
	 * @return
	 */
	public Short getMillis()
	{
		return millis;
	}

	/**
	 * @param short1
	 */
	public void setMillis(Short short1)
	{
		millis = short1;
	}

}
