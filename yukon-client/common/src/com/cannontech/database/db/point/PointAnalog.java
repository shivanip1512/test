package com.cannontech.database.db.point;

import com.cannontech.database.data.point.PointTypes;

/**
 * This type was created in VisualAge.
 */
public class PointAnalog extends com.cannontech.database.db.DBPersistent {
	private Integer pointID = null;
	private Double deadband = new Double(-1);
	private String transducerType = PointTypes.getType(PointTypes.TRANSDUCER_NONE);
	private Double multiplier = new Double(1.0);
	private Double dataOffset = new Double(0.0);


	public static final String TABLE_NAME = "PointAnalog";
/**
 * PointAnalogSetting constructor comment.
 */
public PointAnalog() {
	super();
}
/**
 * PointAnalogSetting constructor comment.
 */
public PointAnalog(Integer pointID, Double deadband, String transducerType, Double multiplier, Double dataOffset) {
	super();
	initialize(pointID, deadband, transducerType, multiplier, dataOffset);
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {

	Object addValues[]= { getPointID(), getDeadband(), getTransducerType(), getMultiplier(), getDataOffset() };
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
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getTransducerType() {
	return transducerType;
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 * @param pointOffset java.lang.Integer
 * @param deadband java.lang.Double
 * @param transducerType java.lang.String
 */
public void initialize(Integer pointID, Double deadband, String transducerType, Double multiplier, Double dataOffset ) {

	setPointID( pointID );
	setDeadband( deadband );
	setTransducerType( transducerType );
	setMultiplier( multiplier );
	setDataOffset( dataOffset );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {
	String selectColumns[] = { "DEADBAND", "TRANSDUCERTYPE", "MULTIPLIER", "DATAOFFSET" };

	String constraintColumns[] = { "POINTID" };
	Object constraintValues[] = { getPointID() };

	Object[] results = retrieve(selectColumns, this.TABLE_NAME, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setDeadband( (Double) results[0] );
		setTransducerType( (String) results[1] );
		setMultiplier( (Double) results[2] );
		setDataOffset( (Double) results[3] );
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
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setTransducerType(String newValue) {
	this.transducerType = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {
	String setColumns[] = { "DEADBAND", "TRANSDUCERTYPE", "MULTIPLIER", "DATAOFFSET" };
	Object setValues[] = { getDeadband(), getTransducerType(), getMultiplier(), getDataOffset() };

	String constraintColumns[] = { "POINTID" };
	Object constraintValues[] = { getPointID() };

	update( this.TABLE_NAME, setColumns, setValues, constraintColumns, constraintValues );
}
}
