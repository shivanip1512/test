package com.cannontech.database.db.capcontrol;

/**
 * This type was created in VisualAge.
 */
public class CapControlFeeder extends com.cannontech.database.db.DBPersistent 
{
	private Integer feederID = null;
	private Double peakSetPoint = null;
	private Double offPeakSetPoint = null;
	private Integer bandwidth = null;	
	private Integer currentVarLoadPointID = null;
	private Integer currentWattLoadPointID = null;	
	private Integer mapLocationID = null;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"PeakSetPoint", "OffPeakSetPoint", "Bandwidth",
		"CurrentVarLoadPointID", "CurrentWattLoadPointID", "MapLocationID"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "FeederID" };


	public static final String TABLE_NAME = "CapControlFeeder";
/**
 * DeviceTwoWayFlags constructor comment.
 */
public CapControlFeeder()
{
	super();
}
/**
 * DeviceTwoWayFlags constructor comment.
 */
public CapControlFeeder(Integer feedID) 
{
	super();
	setFeederID( feedID );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
	{
		getFeederID(), getPeakSetPoint(), 
		getOffPeakSetPoint(), getBandwidth(),
		getCurrentVarLoadPointID(), getCurrentWattLoadPointID(), 
		getMapLocationID()
	};

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getFeederID() );	
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getBandwidth() {
	return bandwidth;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:42:02 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentVarLoadPointID() {
	return currentVarLoadPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:42:02 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentWattLoadPointID() {
	return currentWattLoadPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 6:46:13 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getFeederID() {
	return feederID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:42:02 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMapLocationID() {
	return mapLocationID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Double
 */
public Double getOffPeakSetPoint() {
	return offPeakSetPoint;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Double
 */
public Double getPeakSetPoint() {
	return peakSetPoint;
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 *
 * This method returns all the Feeders that are not assgined
 *  to a SubBus.
 */
public static CapControlFeeder[] getUnassignedFeeders()
{
	java.util.Vector returnVector = null;
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;


	String sql = "SELECT FeederID FROM " + TABLE_NAME + " where " +
					 " FeederID not in (select FeederID from " + CCFeederSubAssignment.TABLE_NAME +
					 ") ORDER BY FeederID";

	try
	{		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());

		if( conn == null )
		{
			throw new IllegalStateException("Error getting database connection.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			
			rset = pstmt.executeQuery();
			returnVector = new java.util.Vector(5); //rset.getFetchSize()
	
			while( rset.next() )
			{				
				returnVector.addElement( 
						new CapControlFeeder(  new Integer(rset.getInt("FeederID")) ) );
			}
					
		}		
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
	}
	finally
	{
		try
		{
			if( pstmt != null ) 
				pstmt.close();
			if( conn != null ) 
				conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			e2.printStackTrace();//something is up
		}	
	}


	CapControlFeeder[] feeders = new CapControlFeeder[returnVector.size()];
	return (CapControlFeeder[])returnVector.toArray( feeders );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getFeederID() };
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setPeakSetPoint( (Double) results[0] );
		setOffPeakSetPoint( (Double) results[1] );
		setBandwidth( (Integer) results[2] );
		setCurrentVarLoadPointID( (Integer) results[3] );
		setCurrentWattLoadPointID( (Integer) results[4] );
		setMapLocationID( (Integer) results[5] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}
/**
 * This method was created in VisualAge.
 */
public void setBandwidth(Integer newValue) {
	this.bandwidth = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:42:02 PM)
 * @param newCurrentVarLoadPointID java.lang.Integer
 */
public void setCurrentVarLoadPointID(java.lang.Integer newCurrentVarLoadPointID) {
	currentVarLoadPointID = newCurrentVarLoadPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:42:02 PM)
 * @param newCurrentWattLoadPointID java.lang.Integer
 */
public void setCurrentWattLoadPointID(java.lang.Integer newCurrentWattLoadPointID) {
	currentWattLoadPointID = newCurrentWattLoadPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 6:46:13 PM)
 * @param newFeederID java.lang.Integer
 */
public void setFeederID(java.lang.Integer newFeederID) {
	feederID = newFeederID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:42:02 PM)
 * @param newMapLocationID java.lang.Integer
 */
public void setMapLocationID(java.lang.Integer newMapLocationID) {
	mapLocationID = newMapLocationID;
}
/**
 * This method was created in VisualAge.
 */
public void setOffPeakSetPoint(Double newValue) {
	this.offPeakSetPoint = newValue;
}
/**
 * This method was created in VisualAge.
 */
public void setPeakSetPoint(Double newValue) {
	this.peakSetPoint = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[]= 
	{ 
		getPeakSetPoint(), 
		getOffPeakSetPoint(), getBandwidth(),
		getCurrentVarLoadPointID(), getCurrentWattLoadPointID(), 
		getMapLocationID()
	};


	Object constraintValues[] = { getFeederID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
