package com.cannontech.database.db.point;

/**
 * This type was created in VisualAge.
 */
public class DynamicCalcHistorical extends com.cannontech.database.db.DBPersistent 
{	
	private Integer pointID = null;
	private java.util.GregorianCalendar lastUpdate = null;

	public final static String SETTER_COLUMNS[] = { "LastUpdate" };

	public final static String CONSTRAINT_COLUMNS[] = { "POINTID" };

	public final static String TABLE_NAME = "DynamicCalcHistorical";
/**
 * PointDispatch constructor comment.
 */
public DynamicCalcHistorical() 
{
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getPointID(), getLastUpdate() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, "POINTID", getPointID() );
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2002 1:55:02 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getLastUpdate() {
	return lastUpdate;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getPointID() {
	return pointID;
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
		setPointID( (Integer) results[0] );

		java.util.GregorianCalendar tempCal = new java.util.GregorianCalendar();
		tempCal.setTime( new java.util.Date(((java.sql.Timestamp)results[1]).getTime()) );
		setLastUpdate( tempCal );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2002 1:55:02 PM)
 * @param newLastUpdate java.util.GregorianCalendar
 */
public void setLastUpdate(java.util.GregorianCalendar newLastUpdate) {
	lastUpdate = newLastUpdate;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setPointID(Integer newValue) {
	this.pointID = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getLastUpdate() };
	Object constraintValues[] = { getPointID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
