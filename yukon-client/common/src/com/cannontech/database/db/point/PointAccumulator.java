package com.cannontech.database.db.point;

/**
 * This type was created in VisualAge.
 */
public class PointAccumulator extends com.cannontech.database.db.DBPersistent 
{
	private Integer pointID = null;
	private Double multiplier = new Double(1.0);
	private Double dataOffset = new Double(0.0);
	
	private static final String tableName = "PointAccumulator";
/**
 * PointAccumulator constructor comment.
 */
public PointAccumulator() 
{
	super();
}
/**
 * PointAccumulator constructor comment.
 */
public PointAccumulator(Integer newPointID, Double newMultiplier, Double newDataOffset ) 
{
	super();
	setPointID( newPointID );
	setMultiplier( newMultiplier );
	setDataOffset( newDataOffset );	
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {

	Object addValues[] = { getPointID(), getMultiplier(), getDataOffset() };

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
 * @return java.lang.Double
 */
public Double getDataOffset() {
	return dataOffset;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Double
 */
public Double getMultiplier() {
	return multiplier;
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
	String selectColumns[] = { "MULTIPLIER","DATAOFFSET" };

	String constraintColumns[] = { "POINTID" };
	Object constraintValues[] = { getPointID() };

	Object results[] = retrieve( selectColumns, this.tableName, constraintColumns, constraintValues );

	if( results.length== selectColumns.length )
	{
		setMultiplier( (Double) results[0] );
		setDataOffset( (Double) results[1] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Double
 */
public void setDataOffset(Double newValue) {
	this.dataOffset = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Double
 */
public void setMultiplier(Double newValue) {
	this.multiplier = newValue;
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
public void update() throws java.sql.SQLException {

	String setColumns[] = { "MULTIPLIER", "DATAOFFSET" };
	Object setValues[] = { getMultiplier(), getDataOffset() };

	String constraintColumns[] = { "POINTID" };
	Object constraintValues[]  = { getPointID() };

	update( this.tableName, setColumns, setValues, constraintColumns, constraintValues );
}
}
