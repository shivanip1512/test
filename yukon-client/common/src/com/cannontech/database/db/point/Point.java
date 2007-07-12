package com.cannontech.database.db.point;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.point.PointLogicalGroups;
import com.cannontech.database.data.point.PointTypes;

public class Point extends com.cannontech.database.db.DBPersistent 
{
	public static final Character PSEUDOFLAG_PSEUDO = new Character('P');
	public static final Character PSEUDOFLAG_REAL = new Character('R');
	public static final Character PSEUDOFLAG_SYSTEM = new Character('S');
	
	private Integer pointID = null;
	private String pointType = null;
	private String pointName = null;
	private Integer paoID = null;
	private String logicalGroup = PointLogicalGroups.getLogicalGrp(PointLogicalGroups.LGRP_DEFAULT);
	private Integer stateGroupID = null;
	private Character serviceFlag = null;
	private Character alarmInhibit = null;
	//private Character pseudoFlag = null;  This is a derived attribute
	private Integer pointOffset = null;
	private String archiveType = null;
	private Integer archiveInterval = null;
    private boolean archiveStatusData = false;
	
	public static final String CONSTRAINT_COLUMNS[] = { "POINTID" };
	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"PointType", "PointName", "PAObjectID", "LogicalGroup",
		"StateGroupID", "ServiceFlag", "AlarmInhibit", "PseudoFlag",
		"PointOffset", "ArchiveType", "ArchiveInterval"
	};
	

	public final static String TABLE_NAME = "Point";
/**
 * Point constructor comment.
 */
public Point() {
	super();
}

/**
 * Point constructor comment.
 */
public Point(Integer pointID, String pointType, String pointName, Integer newPaoID, String logicalGroup, Integer stateGroupID, Character serviceFlag, Character alarmInhibit, Integer pointOffset, String archiveType, Integer archiveInterval ) {
	super();

	initialize(pointID, pointType, pointName, newPaoID, logicalGroup, 
		stateGroupID, serviceFlag, alarmInhibit, pointOffset, archiveType, archiveInterval );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[]= { getPointID(), getPointType(), 
		getPointName(), getPaoID(), getLogicalGroup(), 
		getStateGroupID(), getServiceFlag(), getAlarmInhibit(), 
		getPseudoFlag(), getPointOffset(), 
		getArchiveType(), getArchiveInterval() };

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
 * This method was created in VisualAge.
 * @return java.lang.Character
 */
public Character getAlarmInhibit() {
	return alarmInhibit;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getArchiveInterval() {
	return this.archiveInterval;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getArchiveType() {
	return this.archiveType;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getLogicalGroup() {
	return logicalGroup;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public final static int getNextPointID()
{
	int retVal = 0;
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
		
	try
	{		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(
								com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

		if( conn == null )
		{
			throw new IllegalStateException("Error getting database connection.");
		}
		else
		{
			pstmt = conn.prepareStatement("select max(pointid) AS maxid from point");
			rset = pstmt.executeQuery();							

			// Just one please
			if( rset.next() )
				retVal = rset.getInt("maxid") + 1;
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

	return retVal;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 2:10:36 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPaoID() {
	return paoID;
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
 * @return java.lang.Integer
 */
public String getPointName() {
	return pointName;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getPointOffset() {
	return pointOffset;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getPointType() {
	return pointType;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Character
 */
public Character getPseudoFlag() 
{
	if( getPointID() != null && getPointOffset() != null )
	{
		if( getPointID().intValue() > 0 )
		{
			return ( getPointOffset().intValue() > 0 
						? PSEUDOFLAG_REAL
						: PSEUDOFLAG_PSEUDO );
		}
		else
			return PSEUDOFLAG_SYSTEM;

	}
	else
	{
		com.cannontech.clientutils.CTILogger.info("***** Unrecognized PSEUDO_FLAG found in : " + this.getClass().getName() );
		return new Character('?');
	}
	
	//return pseudoFlag;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Character
 */
public Character getServiceFlag() {
	return serviceFlag;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getStateGroupID() {
	return this.stateGroupID;
}
/**
 * This method was created in VisualAge.
 * @param pointType java.lang.String
 * @param pointName java.lang.String
 * @param paoID java.lang.Integer
 * @param logicalGroup java.lang.String
 * @param stateName java.lang.String
 * @param serviceFlag java.lang.Character
 * @param alarmInhibit java.lang.Character
 * @param pseudoFlag java.lang.Character
 */
public void initialize(Integer pointID, String pointType, String pointName, Integer newPaoID, String logicalGroup, Integer stateGroupID, Character serviceFlag, Character alarmInhibit, Integer pointOffset, String archiveType, Integer archiveInterval ) 
{
	setPointID( pointID ) ;
	setPointType( pointType );
	setPointName( pointName );
	setPaoID( newPaoID );
	setLogicalGroup( logicalGroup );
	setStateGroupID( stateGroupID );
	setServiceFlag( serviceFlag );
	setAlarmInhibit( alarmInhibit );
	setPointOffset( pointOffset );
	setArchiveType( archiveType );
	setArchiveInterval( archiveInterval );
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
		setPointType( (String) results[0] );
		setPointName( (String) results[1]);
		setPaoID( (Integer) results[2] );
		setLogicalGroup( (String) results[3] );
		setStateGroupID( (Integer) results[4] );

		String temp;

		temp = (String) results[5];
		if( temp != null )
			setServiceFlag( new  Character( temp.charAt(0) ) );
		
		temp = (String) results[6];
		if( temp != null )	
			setAlarmInhibit( new Character( temp.charAt(0) ) );

		temp = (String) results[7];
		//if( temp != null )
			//setPseudoFlag( new Character( temp.charAt(0) ) );		

		setPointOffset( (Integer) results[8] );
		setArchiveType( (String) results[9] );
		setArchiveInterval( (Integer) results[10] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
		
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Character
 */
public void setAlarmInhibit(Character newValue) {
	this.alarmInhibit = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setArchiveInterval(Integer newValue) {
	this.archiveInterval = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setArchiveType(String newValue) {
	if ( newValue.equalsIgnoreCase(PointTypes.ARCHIVE_NONE)){
		this.setArchiveStatusData(false);
	}else{
		this.setArchiveStatusData(true);
	}
	
	this.archiveType = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setLogicalGroup(String newValue) {
	this.logicalGroup = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 2:10:36 PM)
 * @param newPaoID java.lang.Integer
 */
public void setPaoID(java.lang.Integer newPaoID) {
	paoID = newPaoID;
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
 * @param newValue java.lang.Integer
 */
public void setPointName(String newValue) {
	this.pointName = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setPointOffset(Integer newValue) {
	this.pointOffset = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setPointType(String newValue) {
	this.pointType = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Character
 */
public void setServiceFlag(Character newValue) {
	this.serviceFlag = newValue;
}
/**
 * This method was created in VisualAge.
 * @param stateGroupID java.lang.Integer
 */
public void setStateGroupID(Integer stateGroupID) {
	this.stateGroupID = stateGroupID;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[]= { getPointType(), 
		getPointName(), getPaoID(), getLogicalGroup(), 
		getStateGroupID(), getServiceFlag(), getAlarmInhibit(), 
		getPseudoFlag(), getPointOffset(), 
		getArchiveType(), getArchiveInterval() };


	Object constraintValues[] = { getPointID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}

public boolean isOutOfService() {
	return CtiUtilities.isTrue( getServiceFlag() );
}

public void setOutOfService( boolean val ) {
	setServiceFlag( 
		val ? CtiUtilities.trueChar : CtiUtilities.falseChar );
}

public boolean isAlarmsDisabled() {
	return CtiUtilities.isTrue( getAlarmInhibit() );
}

public void setAlarmsDisabled( boolean val ) {
	setAlarmInhibit( 
		val ? CtiUtilities.trueChar : CtiUtilities.falseChar );
}

public boolean isArchiveStatusData() {
	return archiveStatusData;
}

public void setArchiveStatusData(boolean archiveStatusData) {
	this.archiveStatusData = archiveStatusData;
}

}
