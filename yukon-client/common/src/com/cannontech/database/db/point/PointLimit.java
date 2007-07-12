package com.cannontech.database.db.point;

import com.cannontech.database.SqlUtils;

/**
 * This type was created in VisualAge.
 */
public class PointLimit extends com.cannontech.database.db.DBPersistent 
{	
	private Integer pointID = null;
	private Integer limitNumber = new Integer(1);
	private Double highLimit = new Double(0.0);
	private Double lowLimit = new Double(0.0);
	private Integer limitDuration = new Integer(0);

	public static final String CONSTRAINT_COLUMNS[] = { "POINTID", "LimitNumber" };
	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"HighLimit", "LowLimit", "LimitDuration" 
	};

	public static final String TABLE_NAME = "PointLimits";
/**
 * PointLimit constructor comment.
 */
public PointLimit() 
{
	super();
}
/**
 * PointLimit constructor comment.
 */
public PointLimit(Integer pointID, Integer limitNumber, Double highLimit, Double lowLimit, Integer newLimitDuration) 
{
	super();
	initialize( pointID, limitNumber, highLimit, lowLimit, newLimitDuration );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getPointID(), getLimitNumber(), 
		getHighLimit(), getLowLimit(), getLimitDuration() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	Object constraintValues[] = { getPointID(), getLimitNumber() };
	
	delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
}

/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param pointID java.lang.Integer
 */
public static boolean deletePointLimits(Integer pointID, java.sql.Connection conn )
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement(
            "DELETE FROM " + TABLE_NAME + " WHERE PointID=" + pointID,
				conn );
	try
	{
		stmt.execute();
	}
	catch(Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return false;
	}

	return true;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Double
 */
public Double getHighLimit() {
	return highLimit;
}
/**
 * Insert the method's description here.
 * Creation date: (11/28/00 10:04:43 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getLimitDuration() {
	return limitDuration;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getLimitNumber() {
	return limitNumber;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Double
 */
public Double getLowLimit() {
	return lowLimit;
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
 * @return com.cannontech.database.db.point.PointLimit[]
 * @param pointID java.lang.Integer
 */
public final static PointLimit[] getPointLimits(Integer pointID) throws java.sql.SQLException {
	
	return getPointLimits(pointID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.PointLimit[]
 * @param pointID java.lang.Integer
 */
public final static PointLimit[] getPointLimits(Integer pointID, String databaseAlias) throws java.sql.SQLException 
{
	java.util.ArrayList tmpList = new java.util.ArrayList(50);
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	String sql = "SELECT LimitNumber,HighLimit,LowLimit," +
	 			 "LimitDuration FROM " + TABLE_NAME + 
	 			 " WHERE PointID= ?";

	try
	{		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(databaseAlias);

		if( conn == null )
		{
			throw new IllegalStateException("Error getting database connection.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, pointID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				tmpList.add( new PointLimit(
							pointID,
							new Integer(rset.getInt("LimitNumber")),
							new Double(rset.getDouble("HighLimit")),
							new Double(rset.getDouble("LowLimit")),
							new Integer(rset.getInt("LimitDuration")) ) );
			}
					
		}		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		SqlUtils.close(rset, pstmt, conn );
	}

	PointLimit retVal[] = new PointLimit[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 * @param limitNumber java.lang.Integer
 * @param highLimit java.lang.Double
 * @param lowLimit java.lang.Double
 * @param dataFilterType java.lang.String
 */
private void initialize(Integer pointID, Integer limitNumber, Double highLimit, Double lowLimit, Integer limitDuration ) 
{

	setPointID( pointID );
	setLimitNumber( limitNumber );
	setHighLimit( highLimit );
	setLowLimit( lowLimit );
	setLimitDuration( limitDuration );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getPointID(), getLimitNumber() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setHighLimit( (Double) results[0] );
		setLowLimit( (Double) results[1] );
		setLimitDuration( (Integer) results[2] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Double
 */
public void setHighLimit(Double newValue) {
	this.highLimit = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (11/28/00 10:04:43 AM)
 * @param newAlarmDuration java.lang.Integer
 */
public void setLimitDuration(java.lang.Integer newLimitDuration) {
	limitDuration = newLimitDuration;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setLimitNumber(Integer newValue) {
	this.limitNumber = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Double
 */
public void setLowLimit(Double newValue) {
	this.lowLimit = newValue;
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
	Object setValues[] = { getHighLimit(), getLowLimit(), getLimitDuration() };

	Object constraintValues[] = { getPointID(), getLimitNumber() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
