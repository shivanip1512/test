package com.cannontech.database.db.point;

/**
 * This type was created in VisualAge.
 */
public class PointUnit extends com.cannontech.database.db.DBPersistent 
{
	public static final int DEFAULT_DECIMAL_PLACES = 3;

	private Integer pointID = null;
	private Integer uomID = new Integer(com.cannontech.database.db.point.PointUnit.UOMID_KWH);
	private Integer decimalPlaces = new Integer(DEFAULT_DECIMAL_PLACES);
	private Double highReasonabilityLimit = new Double(com.cannontech.common.util.CtiUtilities.INVALID_MAX_DOUBLE);
	private Double lowReasonabilityLimit = new Double(com.cannontech.common.util.CtiUtilities.INVALID_MIN_DOUBLE);
	
	public static final String CONSTRAINT_COLUMNS[] = { "POINTID" };
	public static final String SETTER_COLUMNS[] = 
	{ 
		"UomID", "DecimalPlaces", "HighReasonabilityLimit", "LowReasonabilityLimit"
	};

	public final static String TABLE_NAME = "PointUnit";



	//some predefined UOMID's that should be in all Databases!
	public static final int UOMID_KW = 0;
	public static final int UOMID_KWH = 1;
	public static final int UOMID_KVA = 2;
	public static final int UOMID_KVAR = 3;
	public static final int UOMID_KVAH = 4;
	public static final int UOMID_KVARH = 5;
	public static final int UOMID_KVOLTS = 6;
	public static final int UOMID_KQ = 7;
	public static final int UOMID_AMPS = 8;
	public static final int UOMID_COUNTS = 9;	
/**
 * PointUnit constructor comment.
 */
public PointUnit() 
{
	super();
}
/**
 * PointUnit constructor comment.
 */
public PointUnit(Integer pointID, Integer umID, Integer newDecimalPlaces, Double highReasonValue, Double lowReasonValue ) 
{
	super();
	
	setPointID( pointID );
	setUomID( umID ) ;
	setDecimalPlaces( newDecimalPlaces );
	setHighReasonabilityLimit( highReasonValue );
	setLowReasonabilityLimit( lowReasonValue );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getPointID(), getUomID(), getDecimalPlaces(),
			getHighReasonabilityLimit(), getLowReasonabilityLimit() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getPointID() );
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 3:26:27 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDecimalPlaces() {
	return decimalPlaces;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 3:18:55 PM)
 * @return java.lang.Double
 */
public java.lang.Double getHighReasonabilityLimit() {
	return highReasonabilityLimit;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 3:18:55 PM)
 * @return java.lang.Double
 */
public java.lang.Double getLowReasonabilityLimit() {
	return lowReasonabilityLimit;
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
 * Creation date: (9/21/2001 3:15:41 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getUomID() {
	return uomID;
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
		setUomID( (Integer) results[0] );
		setDecimalPlaces( (Integer) results[1] );
		setHighReasonabilityLimit( (Double) results[2] );
		setLowReasonabilityLimit( (Double) results[3] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
	
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 3:26:27 PM)
 * @param newDecimalPlaces java.lang.Integer
 */
public void setDecimalPlaces(java.lang.Integer newDecimalPlaces) {
	decimalPlaces = newDecimalPlaces;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 3:18:55 PM)
 * @param newHighReasonabilityLimit java.lang.Double
 */
public void setHighReasonabilityLimit(java.lang.Double newHighReasonabilityLimit) {
	highReasonabilityLimit = newHighReasonabilityLimit;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 3:18:55 PM)
 * @param newLowReasonabilityLimit java.lang.Double
 */
public void setLowReasonabilityLimit(java.lang.Double newLowReasonabilityLimit) {
	lowReasonabilityLimit = newLowReasonabilityLimit;
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
 * Creation date: (9/21/2001 3:15:41 PM)
 * @param newUomID java.lang.Integer
 */
public void setUomID(java.lang.Integer newUomID) {
	uomID = newUomID;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getUomID(), getDecimalPlaces(),
			getHighReasonabilityLimit(), getLowReasonabilityLimit() };
	
	Object constraintValues[] = { getPointID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
