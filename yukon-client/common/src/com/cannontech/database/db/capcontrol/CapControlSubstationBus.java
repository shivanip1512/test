package com.cannontech.database.db.capcontrol;

import com.cannontech.database.db.point.calculation.CalcComponentTypes;
/**
 * This type was created in VisualAge.
 */
public class CapControlSubstationBus extends com.cannontech.database.db.DBPersistent 
{
	public static final String CNTRL_INDIVIDUAL_FEEDER = "IndividualFeeder";
	public static final String CNTRL_SUBSTATION_BUS = "SubstationBus";
	public static final String CNTRL_BUSOPTIMIZED_FEEDER= "BusOptimizedFeeder";
	public static final String CNTRL_MANUAL_ONLY= "ManualOnly";
	
	
	private Integer substationBusID = null;
	private String controlMethod = CNTRL_INDIVIDUAL_FEEDER;
	private Integer maxDailyOperation = new Integer(0);
	private Character maxOperationDisableFlag = new Character('N');
	private Double peakSetPoint = null;
	private Double offPeakSetPoint = null;
	private Integer peakStartTime = null;
	private Integer peakStopTime = null;
	private Integer currentVarLoadPointID = null;
	private Integer currentWattLoadPointID = null;	
	private Double upperBandwidth = new Double(300.0);
	private Integer controlInterval = new Integer(0);
	private Integer minResponseTime = new Integer(30);
	private Integer minConfirmPercent = new Integer(15);
	private Integer failurePercent = new Integer(15);
	private String daysOfWeek = new String("YYYYYNNN");
	private Integer mapLocationID = new Integer(0);  //not used as of 11-12-2001
	private Double lowerBandwidth = new Double(0.0);
	private String controlUnits = CalcComponentTypes.LABEL_KVAR;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"ControlMethod", "MaxDailyOperation", "MaxOperationDisableFlag",
		"PeakSetPoint", "OffPeakSetPoint", "PeakStartTime", "PeakStopTime",
		"CurrentVarLoadPointID", "CurrentWattLoadPointID", "UpperBandwidth",
		"ControlInterval", "MinResponseTime", "MinConfirmPercent",
		"FailurePercent", "DaysOfWeek", "MapLocationID",
		"LowerBandwidth", "ControlUnits"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "SubstationBusID" };


	public static final String TABLE_NAME = "CapControlSubstationBus";

/**
 * DeviceTwoWayFlags constructor comment.
 */
public CapControlSubstationBus()
{
	super();
}


/**
 * DeviceTwoWayFlags constructor comment.
 */
public CapControlSubstationBus(Integer subID) 
{
	super();
	setSubstationBusID( subID );
}


/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
	{
		getSubstationBusID(), getControlMethod(), getMaxDailyOperation(), 
		getMaxOperationDisableFlag(), getPeakSetPoint(), 
		getOffPeakSetPoint(), getPeakStartTime(), getPeakStopTime(),
		getCurrentVarLoadPointID(), getCurrentWattLoadPointID(), 
		getUpperBandwidth(), getControlInterval(), getMinResponseTime(), 
		getMinConfirmPercent(), getFailurePercent(),
		getDaysOfWeek(), getMapLocationID(),
		getLowerBandwidth(), getControlUnits()
	};

	add( TABLE_NAME, addValues );
}


/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getSubstationBusID() );	
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getControlInterval() {
	return controlInterval;
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:42:02 PM)
 * @return java.lang.String
 */
public java.lang.String getControlMethod() {
	return controlMethod;
}


/**
 * Insert the method's description here.
 * Creation date: (7/5/2002 10:22:53 AM)
 * @return java.lang.String
 */
public java.lang.String getControlUnits() {
	return controlUnits;
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
 * Creation date: (9/26/00 10:27:13 AM)
 * @return java.lang.String
 */
public java.lang.String getDaysOfWeek() {
	return daysOfWeek;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getFailurePercent() {
	return failurePercent;
}


/**
 * Insert the method's description here.
 * Creation date: (7/5/2002 10:22:53 AM)
 * @return java.lang.Double
 */
public java.lang.Double getLowerBandwidth() {
	return lowerBandwidth;
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
 * @return java.lang.Integer
 */
public Integer getMaxDailyOperation() {
	return maxDailyOperation;
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:42:02 PM)
 * @return java.lang.Character
 */
public java.lang.Character getMaxOperationDisableFlag() {
	return maxOperationDisableFlag;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getMinConfirmPercent() {
	return minConfirmPercent;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getMinResponseTime() {
	return minResponseTime;
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
 * Insert the method's description here.
 * Creation date: (11/13/2001 3:58:59 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPeakStartTime() {
	return peakStartTime;
}


/**
 * Insert the method's description here.
 * Creation date: (11/13/2001 3:58:59 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPeakStopTime() {
	return peakStopTime;
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:42:02 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSubstationBusID() {
	return substationBusID;
}


/**
 * Insert the method's description here.
 * Creation date: (7/5/2002 10:22:53 AM)
 * @return java.lang.Double
 */
public java.lang.Double getUpperBandwidth() {
	return upperBandwidth;
}


/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 *
 * This method returns a SubBus's name that already uses
 *  the pointID for its VAR point, if returns null if the
 *  pointID is not yet used.
 */
public static com.cannontech.common.util.NativeIntVector getUsedVARPointIDs( Integer excludedSubBusId, Integer excludedFeederID )
{
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	com.cannontech.common.util.NativeIntVector vect = new com.cannontech.common.util.NativeIntVector(10);

   if( excludedFeederID == null )
      excludedFeederID = new Integer(0);
      
   if( excludedSubBusId == null )
      excludedSubBusId = new Integer(0);
      
//	String sql = "SELECT CurrentVarLoadPointID FROM " + TABLE_NAME +
//					 " where SubstationBusID <> " + excludedPAOId;

   //Get all the used Var PointIDs in the CapControlSubBus table and the Feeder table                
   String sql = "select p.pointid from " + com.cannontech.database.db.point.Point.TABLE_NAME + 
      " p where p.pointid in " +
      "(select currentvarloadpointid from " + TABLE_NAME + 
         " where substationbusid <> " + excludedSubBusId + ")" +
      "or p.pointid in " +
      "(select currentvarloadpointid from " + CapControlFeeder.TABLE_NAME + 
         " where feederid <> " + excludedFeederID + ")";
                

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
	
			while( rset.next() )
				vect.add( rset.getInt(1) );					
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
			if( conn != null ) 
				conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}


	return vect;
}


/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getSubstationBusID() };
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setControlMethod( (String) results[0] );
		setMaxDailyOperation( (Integer) results[1] );
		setMaxOperationDisableFlag( new Character(results[2].toString().charAt(0)) );		
		setPeakSetPoint( (Double) results[3] );
		setOffPeakSetPoint( (Double) results[4] );

		setPeakStartTime( (Integer)results[5] );
		setPeakStopTime( (Integer)results[6] );
		
		setCurrentVarLoadPointID( (Integer) results[7] );
		setCurrentWattLoadPointID( (Integer) results[8] );
		setUpperBandwidth( (Double) results[9] );
		setControlInterval( (Integer) results[10] );
		setMinResponseTime( (Integer) results[11] );
		setMinConfirmPercent( (Integer) results[12] );
		setFailurePercent( (Integer) results[13] );
		setDaysOfWeek( (String) results[14] );
		setMapLocationID( (Integer) results[15] );
		setLowerBandwidth( (Double) results[16] );
		setControlUnits( (String) results[17] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}


/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setControlInterval(Integer newValue) {
	this.controlInterval = newValue;
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:42:02 PM)
 * @param newControlMethod java.lang.String
 */
public void setControlMethod(java.lang.String newControlMethod) {
	controlMethod = newControlMethod;
}


/**
 * Insert the method's description here.
 * Creation date: (7/5/2002 10:22:53 AM)
 * @param newControlUnits java.lang.String
 */
public void setControlUnits(java.lang.String newControlUnits) {
	controlUnits = newControlUnits;
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
 * Creation date: (9/26/00 10:27:13 AM)
 * @param newDaysOfWeek java.lang.String
 */
public void setDaysOfWeek(java.lang.String newDaysOfWeek) {
	daysOfWeek = newDaysOfWeek;
}


/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setFailurePercent(Integer newValue) {
	this.failurePercent = newValue;
}


/**
 * Insert the method's description here.
 * Creation date: (7/5/2002 10:22:53 AM)
 * @param newLowerBandwidth java.lang.Double
 */
public void setLowerBandwidth(java.lang.Double newLowerBandwidth) {
	lowerBandwidth = newLowerBandwidth;
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
 * @param newValue java.lang.Integer
 */
public void setMaxDailyOperation(Integer newValue) {
	this.maxDailyOperation = newValue;
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:42:02 PM)
 * @param newMaxOperationDisableFlag java.lang.Character
 */
public void setMaxOperationDisableFlag(java.lang.Character newMaxOperationDisableFlag) {
	maxOperationDisableFlag = newMaxOperationDisableFlag;
}


/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setMinConfirmPercent(Integer newValue) {
	this.minConfirmPercent = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setMinResponseTime(Integer newValue) {
	this.minResponseTime = newValue;
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
 * Insert the method's description here.
 * Creation date: (11/13/2001 3:58:59 PM)
 * @param newPeakStartTime java.lang.Integer
 */
public void setPeakStartTime(java.lang.Integer newPeakStartTime) {
	peakStartTime = newPeakStartTime;
}


/**
 * Insert the method's description here.
 * Creation date: (11/13/2001 3:58:59 PM)
 * @param newPeakStopTime java.lang.Integer
 */
public void setPeakStopTime(java.lang.Integer newPeakStopTime) {
	peakStopTime = newPeakStopTime;
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:42:02 PM)
 * @param newSubstationBusID java.lang.Integer
 */
public void setSubstationBusID(java.lang.Integer newSubstationBusID) {
	substationBusID = newSubstationBusID;
}


/**
 * Insert the method's description here.
 * Creation date: (7/5/2002 10:22:53 AM)
 * @param newUpperBandwidth java.lang.Double
 */
public void setUpperBandwidth(java.lang.Double newUpperBandwidth) {
	upperBandwidth = newUpperBandwidth;
}


/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[]= 
	{ 
		getControlMethod(), getMaxDailyOperation(), 
		getMaxOperationDisableFlag(), getPeakSetPoint(), 
		getOffPeakSetPoint(), getPeakStartTime(), getPeakStopTime(),
		getCurrentVarLoadPointID(), getCurrentWattLoadPointID(), 
		getUpperBandwidth(), getControlInterval(), getMinResponseTime(), 
		getMinConfirmPercent(), getFailurePercent(),
		getDaysOfWeek(), getMapLocationID(),
		getLowerBandwidth(), getControlUnits()
	};


	Object constraintValues[] = { getSubstationBusID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}