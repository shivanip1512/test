package com.cannontech.database.db.point;

/**
 * This type was created in VisualAge.
 */
public class DynamicAccumulator extends com.cannontech.database.db.DBPersistent 
{	
	private Integer pointID = null;
	private Integer prevPulses = null;
	private Integer presentPulses = null;

	public final static String columns[] = { "POINTID", "TIMESTAMP", "PREVIOUSPULSES", "PRESENTPULSES" };

	public final static String constraintColumns[] = { "POINTID" };

	public final static String TABLE_NAME = "DynamicAccumulator";
/**
 * PointDispatch constructor comment.
 */
public DynamicAccumulator() 
{
	super();
	initialize( null, null ,null );
}
/**
 * PointDispatch constructor comment.
 */
public DynamicAccumulator(Integer pointID) 
{
	super();
	initialize( pointID, null ,null );
}
/**
 * PointDispatch constructor comment.
 */
public DynamicAccumulator( Integer pointID, Integer prevPulses, Integer presentPulses )
{
	super();
	initialize( pointID, prevPulses, presentPulses );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getPointID(), getPrevPulses(), getPresentPulses() };

	add( this.TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( this.TABLE_NAME, "POINTID", getPointID() );
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
 * Creation date: (11/2/00 3:15:18 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPresentPulses() {
	return presentPulses;
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 3:15:18 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPrevPulses() {
	return prevPulses;
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasDynamicAccumulator(Integer pointID) throws java.sql.SQLException 
{	
	return hasDynamicAccumulator(pointID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasDynamicAccumulator(Integer pointID, String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT * FROM " + TABLE_NAME + " WHERE pointID=" + pointID,
													databaseAlias );

	try
	{
		stmt.execute();
		return (stmt.getRowCount() > 0 );
	}
	catch( Exception e )
	{
		return false;
	}
}
/**
 * This method was created in VisualAge.
 */
public void initialize( Integer pointID, Integer prevPulses, Integer presentPulses )
{
	setPointID( pointID );
	setPrevPulses( prevPulses ) ;
	setPresentPulses( presentPulses ) ;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getPointID() };

	Object results[] = retrieve( columns, this.TABLE_NAME, constraintColumns, constraintValues );

	if( results.length == columns.length )
	{
		setPointID( (Integer) results[0] );
		setPrevPulses( (Integer) results[2] );
		setPresentPulses( (Integer) results[3] );
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
 * Insert the method's description here.
 * Creation date: (11/2/00 3:15:18 PM)
 * @param newPresentPulses java.lang.Integer
 */
public void setPresentPulses(java.lang.Integer newPresentPulses) {
	presentPulses = newPresentPulses;
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 3:15:18 PM)
 * @param newPrevPulses java.lang.Integer
 */
public void setPrevPulses(java.lang.Integer newPrevPulses) {
	prevPulses = newPrevPulses;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getPointID(), getPrevPulses(), getPresentPulses() };
	Object constraintValues[] = { getPointID() };

	update( this.TABLE_NAME, columns, setValues, constraintColumns, constraintValues );
}
}
