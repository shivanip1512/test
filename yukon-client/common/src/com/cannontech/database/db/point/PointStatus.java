package com.cannontech.database.db.point;

/**
 * This type was created in VisualAge.
 */
public class PointStatus extends com.cannontech.database.db.DBPersistent 
{
	public static final int DEFAULT_CMD_TIMEOUT = 0;
	
	private Integer pointID = null;
	private Integer initialState = new Integer(0);

	public static final String CONSTRAINT_COLUMNS[] = { "POINTID" };

	public static final String SETTER_COLUMNS[] = { "InitialState" };

	public static final String TABLE_NAME = "PointStatus";	
/**
 * PointStatus constructor comment.
 */
public PointStatus() 
{
	super();
}
/**
 * add method comment.
 */
@Override
public void add() throws java.sql.SQLException {

	Object addValues[] = { getPointID(), getInitialState() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
@Override
public void delete() throws java.sql.SQLException {

	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getPointID() );
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
 * retrieve method comment.
 */
@Override
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getPointID() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setInitialState( (Integer) results[0] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
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
 * update method comment.
 */
@Override
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getInitialState() };

	Object constraintValues[] = { getPointID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}

}
