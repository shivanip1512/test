package com.cannontech.database.db.esub;

import com.cannontech.database.SqlUtils;

/**
 * Insert the type's description here.
 * Creation date: (12/21/2000 1:49:11 PM)
 * @author: 
 */
public class TCB extends com.cannontech.database.db.DBPersistent implements com.cannontech.esub.SubstationItem{
	private static final String tableName = "TCB";
	
	private int ID;
	private String name;
	private int substationID;
	private int statusID;

	// point ids
	private int mwID;
	private int mvarID;
	private int sf6TempID;
	private int sf6PSIID;
	private int sf6DensityID;
	private int sf6HeaterStatusID;
	private int sf6HeaterFailID;
	private int sf6LeakHoursID; 	

	private int tc1LastTripTimeID;
	private int tc1PreviousTripTime1ID;
	private int tc1PreviousTripTime2ID;
	private int tc1PreviousTripTime3ID;
	private int tc2LastTripTimeID;
	private int tc2PreviousTripTime1ID;
	private int tc2PreviousTripTime2ID;
	private int tc2PreviousTripTime3ID;
	
	private int graphDefinitionID;

	//sql
	private static String substationTCBSql =
	"SELECT TCBID,Name,StatusID,MVARID,MWID,SF6TEMPID,SF6PSIID,SF6DensityID,SF6HeaterStatusID,SF6HeaterFailID,SF6LeakHoursID,TC1LastTripTimeID,TC1PreviousTripTime1ID,TC1PreviousTripTime2ID,TC1PreviousTripTime3ID,TC2LastTripTimeID,TC2PreviousTripTime1ID,TC2PreviousTripTime2ID,TC2PreviousTripTime3ID,GraphDefinitionID FROM TCB WHERE SubstationID = ?";
/**
 * TCB constructor comment.
 */
public TCB() {
	super();
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	throw new RuntimeException("NOT IMPLEMENTED!");
}
/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException 
{
	throw new RuntimeException("NOT IMPLEMENTED!");
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 10:21:41 AM)
 */
public static TCB[] getAllTCBs(int substationID) 
{
	return getAllTCBs(substationID, "yukon" );
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 10:21:41 AM)
 */
public static TCB[] getAllTCBs(int substationID, String dbAlias) 
{	
	TCB[] retVal = null;
	
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	try
	{		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
		pstmt = conn.prepareStatement(substationTCBSql);
		pstmt.setInt(1, substationID );
		
		rset = pstmt.executeQuery();

		java.util.ArrayList tcbList = new java.util.ArrayList();
		while( rset.next() )
		{
			TCB tcb = new TCB();
			tcb.setSubstationID(substationID);
			
			tcb.setID( rset.getInt(1) );
			tcb.setName( rset.getString(2) );		
			tcb.setStatusID( rset.getInt(3) );
			tcb.setMvarID( rset.getInt(4) );
			tcb.setMwID( rset.getInt(5) );
			tcb.setSf6TempID( rset.getInt(6) );
			tcb.setSf6PSIID( rset.getInt(7) );
			tcb.setSf6DensityID( rset.getInt(8) );
			tcb.setSf6HeaterStatusID( rset.getInt(9) );
			tcb.setSf6HeaterFailID( rset.getInt(10) );
			tcb.setSf6LeakHoursID( rset.getInt(11) );
			tcb.setTc1LastTripTimeID( rset.getInt(12) );
			tcb.setTc1PreviousTripTime1ID( rset.getInt(13) );
			tcb.setTc1PreviousTripTime2ID( rset.getInt(14) );
			tcb.setTc1PreviousTripTime3ID( rset.getInt(15) );
			tcb.setTc2LastTripTimeID( rset.getInt(16) );
			tcb.setTc2PreviousTripTime1ID( rset.getInt(17) );
			tcb.setTc2PreviousTripTime2ID( rset.getInt(18) );
			tcb.setTc2PreviousTripTime3ID( rset.getInt(19) );						
			tcb.setGraphDefinitionID( rset.getInt(20) );
			
			tcbList.add(tcb);
		}

		retVal = new TCB[tcbList.size()];
		tcbList.toArray(retVal);
		
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
 * Creation date: (12/21/2000 1:51:36 PM)
 * @return int
 */
public int getGraphDefinitionID() {
	return graphDefinitionID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 1:51:36 PM)
 * @return int
 */
public int getID() {
	return ID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 1:51:36 PM)
 * @return int
 */
public int getMvarID() {
	return mvarID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 1:51:36 PM)
 * @return int
 */
public int getMwID() {
	return mwID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 1:51:36 PM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 1:51:36 PM)
 * @return int
 */
public int getSf6DensityID() {
	return sf6DensityID;
}
/**
 * Insert the method's description here.
 * Creation date: (1/5/2001 10:31:50 AM)
 * @return int
 */
public int getSf6HeaterFailID() {
	return sf6HeaterFailID;
}
/**
 * Insert the method's description here.
 * Creation date: (1/5/2001 10:31:50 AM)
 * @return int
 */
public int getSf6HeaterStatusID() {
	return sf6HeaterStatusID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 1:51:36 PM)
 * @return int
 */
public int getSf6LeakHoursID() {
	return sf6LeakHoursID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 1:51:36 PM)
 * @return int
 */
public int getSf6PSIID() {
	return sf6PSIID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 1:51:36 PM)
 * @return int
 */
public int getSf6TempID() {
	return sf6TempID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 1:51:36 PM)
 * @return int
 */
public int getStatusID() {
	return statusID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 1:51:36 PM)
 * @return int
 */
public int getSubstationID() {
	return substationID;
}
/**
 * Creation date: (9/4/2001 4:28:46 PM)
 * @return int
 */
public int getTc1LastTripTimeID() {
	return tc1LastTripTimeID;
}
/**
 * Creation date: (9/4/2001 4:28:46 PM)
 * @return int
 */
public int getTc1PreviousTripTime1ID() {
	return tc1PreviousTripTime1ID;
}
/**
 * Creation date: (9/4/2001 4:28:46 PM)
 * @return int
 */
public int getTc1PreviousTripTime2ID() {
	return tc1PreviousTripTime2ID;
}
/**
 * Creation date: (9/4/2001 4:28:46 PM)
 * @return int
 */
public int getTc1PreviousTripTime3ID() {
	return tc1PreviousTripTime3ID;
}
/**
 * Creation date: (9/4/2001 4:28:46 PM)
 * @return int
 */
public int getTc2LastTripTimeID() {
	return tc2LastTripTimeID;
}
/**
 * Creation date: (9/4/2001 4:28:46 PM)
 * @return int
 */
public int getTc2PreviousTripTime1ID() {
	return tc2PreviousTripTime1ID;
}
/**
 * Creation date: (9/4/2001 4:28:46 PM)
 * @return int
 */
public int getTc2PreviousTripTime2ID() {
	return tc2PreviousTripTime2ID;
}
/**
 * Creation date: (9/4/2001 4:28:46 PM)
 * @return int
 */
public int getTc2PreviousTripTime3ID() {
	return tc2PreviousTripTime3ID;
}
/**
 * This method was created by a SmartGuide.
 */
public void retrieve() throws java.sql.SQLException 
{
	String[] selectColumns =
	{
		"Name",
		"SubstationID",
		"StatusID",
		"MWID",
		"MVARID",
		"SF6TempID",
		"SF6PSIID",
		"SF6DensityID",
		"SF6HeaterStatusID",
		"SF6HeaterFailID",
		"SF6LeakHoursID",
		"TC1LASTTRIPTIMEID",
		"TC1PREVIOUSTRIPTIME1ID",
		"TC1PREVIOUSTRIPTIME2ID",
		"TC1PREVIOUSTRIPTIME3ID",
		"TC2LASTTRIPTIMEID",
		"TC2PREVIOUSTRIPTIME1ID",
		"TC2PREVIOUSTRIPTIME2ID",
		"TC2PREVIOUSTRIPTIME3ID",
		"GraphDefinitionID"
	};

	String[] constraintColumns =
	{
		"TCBID"
	};
	Object[] constraintValues =
	{
		new Integer(getID())
	};

	Object[] results = retrieve( selectColumns, tableName, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setName( (String) results[0] );
		setSubstationID( ((Integer) results[1]).intValue() );
		setStatusID( ((Integer) results[2]).intValue() );

		Integer mw = (Integer) results[3];
		Integer mvar = (Integer) results[4];
		Integer sf6Temp = (Integer) results[5];
		Integer sf6PSI = (Integer) results[6];
		Integer sf6Density = (Integer) results[7];
		Integer sf6HeaterStatus = (Integer) results[8];
		Integer sf6HeaterFail = (Integer) results[9];
		Integer sf6LeakHours = (Integer) results[10];	
		
		Integer tc1LastTripTime = (Integer) results[11];
		Integer tc1PreviousTripTime1 = (Integer) results[12];
		Integer tc1PreviousTripTime2 = (Integer) results[13];
		Integer tc1PreviousTripTime3 = (Integer) results[14];
		Integer tc2LastTripTime = (Integer) results[15];
		Integer tc2PreviousTripTime1 = (Integer) results[16];
		Integer tc2PreviousTripTime2 = (Integer) results[17];
		Integer tc2PreviousTripTime3 = (Integer) results[18];
		
		Integer gdef = (Integer) results[19];
		
		if( mw != null )
			setMwID( mw.intValue() );

		if( mvar != null )		
			setMvarID( mvar.intValue() );

		if( sf6Temp != null )
			setSf6TempID( sf6Temp.intValue() );

		if( sf6PSI != null )
			setSf6PSIID( sf6PSI.intValue() );

		if( sf6Density != null )
			setSf6DensityID( sf6Density.intValue() );

		if( sf6HeaterStatus != null )
			setSf6HeaterStatusID( sf6HeaterStatus.intValue() );

		if( sf6HeaterFail != null )
			setSf6HeaterFailID( sf6HeaterFail.intValue() );

		if( sf6LeakHours != null )
			setSf6LeakHoursID( sf6LeakHours.intValue() );
			
		if( gdef != null )	
			setGraphDefinitionID( gdef.intValue() );
	}		
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 1:51:36 PM)
 * @param newGraphDefinitionID int
 */
public void setGraphDefinitionID(int newGraphDefinitionID) {
	graphDefinitionID = newGraphDefinitionID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 1:51:36 PM)
 * @param newID int
 */
public void setID(int newID) {
	ID = newID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 1:51:36 PM)
 * @param newMvarID int
 */
public void setMvarID(int newMvarID) {
	mvarID = newMvarID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 1:51:36 PM)
 * @param newMwID int
 */
public void setMwID(int newMwID) {
	mwID = newMwID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 1:51:36 PM)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 1:51:36 PM)
 * @param newSf6DensityID int
 */
public void setSf6DensityID(int newSf6DensityID) {
	sf6DensityID = newSf6DensityID;
}
/**
 * Insert the method's description here.
 * Creation date: (1/5/2001 10:31:50 AM)
 * @param newSf6HeaterFailID int
 */
public void setSf6HeaterFailID(int newSf6HeaterFailID) {
	sf6HeaterFailID = newSf6HeaterFailID;
}
/**
 * Insert the method's description here.
 * Creation date: (1/5/2001 10:31:50 AM)
 * @param newSf6HeaterStatusID int
 */
public void setSf6HeaterStatusID(int newSf6HeaterStatusID) {
	sf6HeaterStatusID = newSf6HeaterStatusID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 1:51:36 PM)
 * @param newSf6LeakHoursID int
 */
public void setSf6LeakHoursID(int newSf6LeakHoursID) {
	sf6LeakHoursID = newSf6LeakHoursID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 1:51:36 PM)
 * @param newSf6PSIID int
 */
public void setSf6PSIID(int newSf6PSIID) {
	sf6PSIID = newSf6PSIID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 1:51:36 PM)
 * @param newSf6TempID int
 */
public void setSf6TempID(int newSf6TempID) {
	sf6TempID = newSf6TempID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 1:51:36 PM)
 * @param newStatusID int
 */
public void setStatusID(int newStatusID) {
	statusID = newStatusID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 1:51:36 PM)
 * @param newSubstationID int
 */
public void setSubstationID(int newSubstationID) {
	substationID = newSubstationID;
}
/**
 * Creation date: (9/4/2001 4:28:46 PM)
 * @param newTc1LastTripTimeID int
 */
public void setTc1LastTripTimeID(int newTc1LastTripTimeID) {
	tc1LastTripTimeID = newTc1LastTripTimeID;
}
/**
 * Creation date: (9/4/2001 4:28:46 PM)
 * @param newTc1PreviousTripTime1ID int
 */
public void setTc1PreviousTripTime1ID(int newTc1PreviousTripTime1ID) {
	tc1PreviousTripTime1ID = newTc1PreviousTripTime1ID;
}
/**
 * Creation date: (9/4/2001 4:28:46 PM)
 * @param newTc1PreviousTripTime2ID int
 */
public void setTc1PreviousTripTime2ID(int newTc1PreviousTripTime2ID) {
	tc1PreviousTripTime2ID = newTc1PreviousTripTime2ID;
}
/**
 * Creation date: (9/4/2001 4:28:46 PM)
 * @param newTc1PreviousTripTime3ID int
 */
public void setTc1PreviousTripTime3ID(int newTc1PreviousTripTime3ID) {
	tc1PreviousTripTime3ID = newTc1PreviousTripTime3ID;
}
/**
 * Creation date: (9/4/2001 4:28:46 PM)
 * @param newTc2LastTripTimeID int
 */
public void setTc2LastTripTimeID(int newTc2LastTripTimeID) {
	tc2LastTripTimeID = newTc2LastTripTimeID;
}
/**
 * Creation date: (9/4/2001 4:28:46 PM)
 * @param newTc2PreviousTripTime1ID int
 */
public void setTc2PreviousTripTime1ID(int newTc2PreviousTripTime1ID) {
	tc2PreviousTripTime1ID = newTc2PreviousTripTime1ID;
}
/**
 * Creation date: (9/4/2001 4:28:46 PM)
 * @param newTc2PreviousTripTime2ID int
 */
public void setTc2PreviousTripTime2ID(int newTc2PreviousTripTime2ID) {
	tc2PreviousTripTime2ID = newTc2PreviousTripTime2ID;
}
/**
 * Creation date: (9/4/2001 4:28:46 PM)
 * @param newTc2PreviousTripTime3ID int
 */
public void setTc2PreviousTripTime3ID(int newTc2PreviousTripTime3ID) {
	tc2PreviousTripTime3ID = newTc2PreviousTripTime3ID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 2:39:54 PM)
 * @return java.lang.String
 */
public String toString() {
	return getName();
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	throw new RuntimeException("Not implemented");
	/*String[] setColumns =
	{
		"Name",
		"SubstationID",
		"StatusID",
		"MWID",
		"MVARID",
		"SF6TempID",
		"SF6PSIID",
		"SF6DensityID",
		"SF6HeaterStatusID",
		"SF6HeaterFailID",
		"SF6LeakHoursID",
		"GraphDefinitionID"
	};

	Object[] setValues =
	{
		getName(),
		new Integer( getSubstationID() ),
		new Integer( getStatusID() ),
		new Integer( getMwID() ),
		new Integer( getMvarID() ),
		new Integer( getSf6TempID() ),
		new Integer( getSf6PSIID() ),
		new Integer( getSf6DensityID() ),
		new Integer( getSf6HeaterStatusID() ),
		new Integer( getSf6HeaterFailID() ),
		new Integer( getSf6LeakHoursID() ),
		new Integer( getGraphDefinitionID() )
	};

	String[] constraintColumns =
	{
		"TCBID"
	};

	Object[] constraintValues =
	{
		new Integer(getID())
	};

	update( tableName, setColumns, setValues, constraintColumns, constraintValues );*/
}
}
