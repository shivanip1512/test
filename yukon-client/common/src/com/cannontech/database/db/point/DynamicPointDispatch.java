package com.cannontech.database.db.point;

/**
 * This type was created in VisualAge.
 */
public class DynamicPointDispatch extends com.cannontech.database.db.DBPersistent {
	
	private Integer pointID = null;
	private java.util.GregorianCalendar timeStamp = null;
	private Integer quality = null;
	private Double value = null;
	private Integer tags = null;
	private java.util.GregorianCalendar nextArchive = null;
	private Integer staleCount = null;
	
	public final static String TABLE_NAME = "DynamicPointDispatch";
/**
 * PointDispatch constructor comment.
 */
public DynamicPointDispatch() 
{
	super();
}
/**
 * PointDispatch constructor comment.
 */
public DynamicPointDispatch(Integer ptID) 
{
	super();
	setPointID( ptID );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	//***************************************
	//**  We should never add to this table!!!
	//***************************************
	
	//Object addValues[] = { getPointID(), getTimeStamp(), getQuality(), getValue(), getTags(), getNextArchive(), getStaleCount() };

	//add( this.TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( this.TABLE_NAME, "POINTID", getPointID() );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public java.util.GregorianCalendar getNextArchive() {
	return nextArchive;
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
 * @param newValue java.lang.String
 */
public Integer getStaleCount() {
	return staleCount;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public Integer getTags() {
	return tags;
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
 * @param pointID java.lang.Integer
 */
public final static boolean hasPointDispatch(Integer pointID) throws java.sql.SQLException {
	
	return hasPointDispatch(pointID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasPointDispatch(Integer pointID, String databaseAlias) throws java.sql.SQLException {

	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT * FROM " + TABLE_NAME + " WHERE PointID=" + pointID,
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
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	String selectColumns[] = { "TIMESTAMP", "QUALITY", "VALUE", "TAGS", "NEXTARCHIVE", "STALECOUNT" };

	String constraintColumns[] = { "POINTID" };
	Object constraintValues[] = { getPointID() };

	Object results[] = retrieve( selectColumns, this.TABLE_NAME, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		java.util.GregorianCalendar tempCal = new java.util.GregorianCalendar();
		tempCal.setTime( new java.util.Date(((java.sql.Timestamp)results[0]).getTime()) );
		setTimeStamp( tempCal );
		setQuality( (Integer) results[1] );
		setValue( (Double) results[2] );
		setTags( (Integer) results[3] );
		java.util.GregorianCalendar tempCal2 = new java.util.GregorianCalendar();
		tempCal2.setTime( new java.util.Date(((java.sql.Timestamp)results[4]).getTime()) );
		setNextArchive( tempCal2 );
		setStaleCount( (Integer) results[5] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
	
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setNextArchive(java.util.GregorianCalendar newValue) {
	this.nextArchive = newValue;
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
public void setStaleCount(Integer newValue) {
	this.staleCount = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setTags(Integer newValue) {
	this.tags = newValue;
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
public void update() throws java.sql.SQLException 
{
	//***************************************
	//**  We should never update this table!!!
	//***************************************
	
	//String setColumns[]= { "TIMESTAMP", "QUALITY", "VALUE", "TAGS", "NEXTARCHIVE", "STALECOUNT" };
	//Object setValues[] = { getTimeStamp(), getQuality(), getValue(), getTags(), getNextArchive(), getStaleCount() };
	//String constraintColumns[] = { "POINTID" };
	//Object constraintValues[] = { getPointID() };

	//update( this.TABLE_NAME, setColumns, setValues, constraintColumns, constraintValues );
}
}
