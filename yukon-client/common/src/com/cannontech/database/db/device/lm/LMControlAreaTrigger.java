package com.cannontech.database.db.device.lm;

/**
 * This type was created in VisualAge.
 */
public class LMControlAreaTrigger extends com.cannontech.database.db.DBPersistent 
{
	public static final String TYPE_THRESHOLD = "Threshold";
	public static final String TYPE_STATUS = "Status";
	public static final int INVALID_INT_VALUE = 0;

	private Integer deviceID = null;
	private Integer triggerNumber = new Integer(0);
	private String triggerType = TYPE_THRESHOLD;
	private Integer pointID = new Integer(0);
	private Integer normalState = new Integer(0);
	private Double threshold = null;
	private String projectionType = com.cannontech.common.util.CtiUtilities.STRING_NONE;
	private Integer projectionPoints = new Integer(0);
	private Integer projectAheadDuration = new Integer(0);
	private Integer thresholdKickPercent = new Integer(0);
	private Double minRestoreOffset = new Double(0.0);
	private Integer peakPointID = new Integer(0);

	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"TriggerNumber", "TriggerType", "PointID", "NormalState",
		"Threshold", "ProjectionType", "ProjectionPoints",
		"ProjectAheadDuration", "ThresholdKickPercent", "MinRestoreOffset",
		"PeakPointID"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "deviceID" };


	public static final String TABLE_NAME = "LMControlAreaTrigger";
/**
 * LMGroupEmetcon constructor comment.
 */
public LMControlAreaTrigger() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getTriggerNumber(), getTriggerType(),
				getPointID(), getNormalState(), getThreshold(), getProjectionType(),
				getProjectionPoints(), getProjectAheadDuration(),
				getThresholdKickPercent(), getMinRestoreOffset(), getPeakPointID() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {
	delete( TABLE_NAME, "DeviceID", getDeviceID() );
}
/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static boolean deleteAllControlAreaTriggers(Integer ctrlAreaDeviceID, java.sql.Connection conn)
{
	//com.cannontech.database.SqlStatement sql = 
		//new com.cannontech.database.SqlStatement( databaseAlias );
	java.sql.PreparedStatement pstmt = null;		
	String sql = "DELETE FROM " + TABLE_NAME + " WHERE deviceID=" + ctrlAreaDeviceID;

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be (null)");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.executeUpdate();
		}		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( pstmt != null ) 
				pstmt.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}

	return true;
}
/**
 * This method was created in VisualAge.
 * @return LMControlAreaTrigger[]
 * @param stateGroup java.lang.Integer
 */
public static final LMControlAreaTrigger[] getAllControlAreaTriggers(Integer ctrlAreaDeviceID) throws java.sql.SQLException 
{
	return getAllControlAreaTriggers(ctrlAreaDeviceID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());												
}
/**
 * This method was created in VisualAge.
 * @return LMControlAreaTrigger[]
 * @param stateGroup java.lang.Integer
 */
public static final LMControlAreaTrigger[] getAllControlAreaTriggers(Integer ctrlAreaDeviceID, String databaseAlias) throws java.sql.SQLException
{
	java.util.ArrayList tmpList = new java.util.ArrayList(30);
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "SELECT MinRestoreOffset,NormalState,PeakPointID,PointID,ProjectAheadDuration, " +
		"ProjectionPoints,ProjectionType,Threshold,ThresholdKickPercent, "+
		"TriggerNumber,TriggerType,DeviceID " + 
		"FROM " + TABLE_NAME + " WHERE DEVICEID= ?";

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
			pstmt.setInt( 1, ctrlAreaDeviceID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				LMControlAreaTrigger item = new LMControlAreaTrigger();

				item.setDbConnection(conn);
				item.setMinRestoreOffset( new Double(rset.getDouble("MinRestoreOffset")) );
				item.setNormalState( new Integer(rset.getInt("NormalState")) );
				item.setPeakPointID( new Integer(rset.getInt("PeakPointID")) );
				item.setPointID( new Integer(rset.getInt("PointID")) );
				item.setProjectAheadDuration( new Integer(rset.getInt("ProjectAheadDuration")) );
				item.setProjectionPoints( new Integer(rset.getInt("ProjectionPoints")) );
				item.setProjectionType( rset.getString("ProjectionType") );
				item.setThreshold( new Double(rset.getDouble("Threshold")) );
				item.setThresholdKickPercent( new Integer(rset.getInt("ThresholdKickPercent")) );
				item.setTriggerNumber( new Integer(rset.getInt("TriggerNumber")) );
				item.setTriggerType( rset.getString("TriggerType") );
				item.setDeviceID( new Integer(rset.getInt("DeviceID")) );

				tmpList.add( item );
			}
					
		}		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( pstmt != null ) pstmt.close();
			if( conn != null ) conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}


	LMControlAreaTrigger retVal[] = new LMControlAreaTrigger[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
}
/**
 * Insert the method's description here.
 * Creation date: (3/21/2001 2:00:45 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDeviceID() {
	return deviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 11:00:50 AM)
 * @return java.lang.Double
 */
public java.lang.Double getMinRestoreOffset() {
	return minRestoreOffset;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:16:14 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getNormalState() {
	return normalState;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:16:14 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPeakPointID() {
	return peakPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:16:14 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPointID() {
	return pointID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:16:14 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getProjectAheadDuration() {
	return projectAheadDuration;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:16:14 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getProjectionPoints() {
	return projectionPoints;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:16:14 AM)
 * @return java.lang.String
 */
public java.lang.String getProjectionType() {
	return projectionType;
}
/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 11:00:50 AM)
 * @return java.lang.Double
 */
public java.lang.Double getThreshold() {
	return threshold;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:16:14 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getThresholdKickPercent() {
	return thresholdKickPercent;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:16:14 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getTriggerNumber() {
	return triggerNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:16:14 AM)
 * @return java.lang.String
 */
public java.lang.String getTriggerType() {
	return triggerType;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getDeviceID() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setTriggerNumber( (Integer) results[0] );
		setTriggerType( (String) results[1] );
		setPointID( (Integer) results[2] );
		setNormalState( (Integer) results[3] );
		setThreshold( (Double) results[4] );
		setProjectionType( (String) results[5] );
		setProjectionPoints( (Integer) results[6] );
		setProjectAheadDuration( (Integer) results[7] );
		setThresholdKickPercent( (Integer) results[8] );
		setMinRestoreOffset( (Double) results[9] );
		setPeakPointID( (Integer) results[10] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}
/**
 * Insert the method's description here.
 * Creation date: (3/21/2001 2:00:46 PM)
 * @param newCtrlAreaDeviceID java.lang.Integer
 */
public void setDeviceID(java.lang.Integer newDeviceID) 
{
	deviceID = newDeviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 11:00:50 AM)
 * @param newMinRestoreOffset java.lang.Double
 */
public void setMinRestoreOffset(java.lang.Double newMinRestoreOffset) {
	minRestoreOffset = newMinRestoreOffset;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:16:14 AM)
 * @param newNormalState java.lang.Integer
 */
public void setNormalState(java.lang.Integer newNormalState) {
	normalState = newNormalState;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:16:14 AM)
 * @param newPeakPointID java.lang.Integer
 */
public void setPeakPointID(java.lang.Integer newPeakPointID) {
	peakPointID = newPeakPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:16:14 AM)
 * @param newPointID java.lang.Integer
 */
public void setPointID(java.lang.Integer newPointID) {
	pointID = newPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:16:14 AM)
 * @param newProjectAheadDuration java.lang.Integer
 */
public void setProjectAheadDuration(java.lang.Integer newProjectAheadDuration) {
	projectAheadDuration = newProjectAheadDuration;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:16:14 AM)
 * @param newProjectionPoints java.lang.Integer
 */
public void setProjectionPoints(java.lang.Integer newProjectionPoints) {
	projectionPoints = newProjectionPoints;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:16:14 AM)
 * @param newProjectionType java.lang.String
 */
public void setProjectionType(java.lang.String newProjectionType) {
	projectionType = newProjectionType;
}
/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 11:00:50 AM)
 * @param newThreshold java.lang.Double
 */
public void setThreshold(java.lang.Double newThreshold) {
	threshold = newThreshold;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:16:14 AM)
 * @param newThresholdKickPercent java.lang.Integer
 */
public void setThresholdKickPercent(java.lang.Integer newThresholdKickPercent) {
	thresholdKickPercent = newThresholdKickPercent;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:16:14 AM)
 * @param newTriggerNumber java.lang.Integer
 */
public void setTriggerNumber(java.lang.Integer newTriggerNumber) {
	triggerNumber = newTriggerNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:16:14 AM)
 * @param newTriggerType java.lang.String
 */
public void setTriggerType(java.lang.String newTriggerType) {
	triggerType = newTriggerType;
}
/**
 * 
 */
public String toString()
{
	return getTriggerType() + " (PointID: " + getPointID() + ")";
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getTriggerNumber(), getTriggerType(),
				getPointID(), getNormalState(), getThreshold(), getProjectionType(),
				getProjectionPoints(), getProjectAheadDuration(),
				getThresholdKickPercent(), getMinRestoreOffset(), getPeakPointID() };

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
