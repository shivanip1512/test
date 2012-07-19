package com.cannontech.database.db.point;

/**
 * This type was created in VisualAge.
 */
public class PointAnalog extends com.cannontech.database.db.DBPersistent {
	private Integer pointID = null;
	private Double deadband = new Double(-1);
	private Double multiplier = new Double(1.0);
	private Double dataOffset = new Double(0.0);

    public static final String CONSTRAINT_COLUMNS[] = { "POINTID" };

    public static final String VALUE_COLUMNS[] = { "DEADBAND", "MULTIPLIER", "DATAOFFSET" };

	public static final String TABLE_NAME = "PointAnalog";
/**
 * PointAnalogSetting constructor comment.
 */
public PointAnalog() {
	super();
}
/**
 * add method comment.
 */
@Override
public void add() throws java.sql.SQLException {

	Object addValues[]= { getPointID(), getDeadband(), getMultiplier(), getDataOffset() };
	add( PointAnalog.TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
@Override
public void delete() throws java.sql.SQLException {

	delete( PointAnalog.TABLE_NAME, CONSTRAINT_COLUMNS[0], getPointID() );
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
public Double getDeadband() {
	return deadband;
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
@Override
public void retrieve() throws java.sql.SQLException {

	Object constraintValues[] = { getPointID() };

	Object[] results = retrieve(VALUE_COLUMNS, PointAnalog.TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == VALUE_COLUMNS.length )
	{
		setDeadband( (Double) results[0] );
		setMultiplier( (Double) results[1] );
		setDataOffset( (Double) results[2] );
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
public void setDeadband(Double newValue) {
	this.deadband = newValue;
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
@Override
public void update() throws java.sql.SQLException {

	Object setValues[] = { getDeadband(), getMultiplier(), getDataOffset() };

	Object constraintValues[] = { getPointID() };

	update( PointAnalog.TABLE_NAME, VALUE_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
