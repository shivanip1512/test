package com.cannontech.database.db.esub;

import com.cannontech.database.SqlUtils;

/**
 * Insert the type's description here.
 * Creation date: (12/19/2000 9:45:44 AM)
 * @author: 
 */
public class DCB extends com.cannontech.database.db.DBPersistent  implements com.cannontech.esub.SubstationItem{
	private static final String tableName = "DCB";
	
	private int id;
	private String name;
	private int substationID;
	private int statusID;
	
	//analog points	
	private int mwID;
	private int mvarID;
	private int lastTripTime;
	private int previousTripTime1;
	private int previousTripTime2;
	private int previousTripTime3;
		
	//graph
	private int graphDefinitionID;

	//SQL
	private static final String substationDCBSql = 
	"SELECT DCBID,Name,StatusID,MWID,MVARID,LastTripTime,PreviousTripTime1,PreviousTripTime2,PreviousTripTime3,GraphDefinitionID FROM " + tableName + " WHERE SubstationID = ?";
/**
 * DCB constructor comment.
 */
public DCB() {
	super();
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	throw new RuntimeException("Not implemented");
	/*Object[] vals = 
	{ 
		getName(), 
		new Integer(getSubstationID()), 
		new Integer(getStatusID()), 
		new Integer(getMwID()), 
		new Integer(getMvarID()),
		new Integer(getGraphDefinitionID())
	};

	add( tableName, vals );*/
}
/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException 
{
	delete( tableName, "DCBID", new Integer(getID()) );
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 10:21:41 AM)
 */
public static DCB[] getAllDCBs(int substationID) 
{
	return getAllDCBs(substationID, "yukon" );
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 10:21:41 AM)
 */
public static DCB[] getAllDCBs(int substationID, String dbAlias) 
{	
	DCB[] retVal = null;
	
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	try
	{		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
		pstmt = conn.prepareStatement(substationDCBSql);
		pstmt.setInt(1, substationID);
		
		rset = pstmt.executeQuery();

		java.util.ArrayList dcbList = new java.util.ArrayList();
		while( rset.next() )
		{
			DCB dcb = new DCB();
			dcb.setSubstationID( substationID );
			
			dcb.setId( rset.getInt(1) );
			dcb.setName( rset.getString(2) );
			dcb.setStatusID( rset.getInt(3) );
			dcb.setMwID( rset.getInt(4) );
			dcb.setMvarID( rset.getInt(5) );
			dcb.setLastTripTime( rset.getInt(6) );
			dcb.setPreviousTripTime1( rset.getInt(7) );
			dcb.setPreviousTripTime2( rset.getInt(8) );
			dcb.setPreviousTripTime3( rset.getInt(9) );			
			dcb.setGraphDefinitionID( rset.getInt(10) );

			dcbList.add(dcb);
		}

		retVal = new DCB[dcbList.size()];
		dcbList.toArray(retVal);
		
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
 * Creation date: (12/19/2000 3:44:41 PM)
 * @return int
 */
public int getGraphDefinitionID() {
	return graphDefinitionID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:48:40 AM)
 * @return int
 */
public int getID() {
	return id;
}
/**
 * Creation date: (9/4/2001 1:51:41 PM)
 * @return int
 */
public int getLastTripTime() {
	return lastTripTime;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:48:40 AM)
 * @return int
 */
public int getMvarID() {
	return mvarID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:48:40 AM)
 * @return int
 */
public int getMwID() {
	return mwID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:48:40 AM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Creation date: (9/4/2001 1:51:10 PM)
 * @return int
 */
public int getPreviousTripTime1() {
	return previousTripTime1;
}
/**
 * Creation date: (9/4/2001 1:51:10 PM)
 * @return int
 */
public int getPreviousTripTime2() {
	return previousTripTime2;
}
/**
 * Creation date: (9/4/2001 1:51:10 PM)
 * @return int
 */
public int getPreviousTripTime3() {
	return previousTripTime3;
}
/**
 * Creation date: (9/4/2001 2:30:49 PM)
 * @return int
 */
public int getStatusID() {
	return statusID;
}
/**
 * Creation date: (9/4/2001 1:51:41 PM)
 * @return java.lang.String
 */
public final static java.lang.String getSubstationDCBSql() {
	return substationDCBSql;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:48:40 AM)
 * @return int
 */
public int getSubstationID() {
	return substationID;
}
/**
 * Creation date: (9/4/2001 1:30:01 PM)
 * @return java.lang.String
 */
public final static java.lang.String getTableName() {
	return tableName;
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
		"LastTripTime",
		"PreviousTripTime1",
		"PreviousTripTime2",
		"PreviousTripTime3",
		"GraphDefinitionID"
	};

	String[] constraintColumns =
	{
		"DCBID"
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

		// The rest are possibly null
		Integer status = (Integer) results[2];
		Integer mw = (Integer) results[3];
		Integer mvar = (Integer) results[4];		
		Integer lastTripTime = (Integer) results[5];
		Integer previousTripTime1 = (Integer) results[6];
		Integer previousTripTime2 = (Integer) results[7];
		Integer previousTripTime3 = (Integer) results[8];
		Integer gdef = (Integer) results[9];

		if( status != null )
			setStatusID( statusID );

		if( mw != null )
			setMwID( mw.intValue() );

		if( mvar != null )		
			setMvarID( mvar.intValue() );

		if( lastTripTime != null )
			setLastTripTime( lastTripTime.intValue() );

		if( previousTripTime1 != null )
			setPreviousTripTime1( previousTripTime1.intValue() );

		if( previousTripTime2 != null )
			setPreviousTripTime2( previousTripTime2.intValue() );

		if( previousTripTime3 != null )
			setPreviousTripTime3( previousTripTime3.intValue() );
			
		if( gdef != null )	
			setGraphDefinitionID( gdef.intValue() );
	}		
		
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 3:44:41 PM)
 * @param newGraphDefinitionID int
 */
public void setGraphDefinitionID(int newGraphDefinitionID) {
	graphDefinitionID = newGraphDefinitionID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:48:40 AM)
 * @param newId int
 */
public void setId(int newId) {
	id = newId;
}
/**
 * Creation date: (9/4/2001 1:51:41 PM)
 * @param newLastTripTime int
 */
public void setLastTripTime(int newLastTripTime) {
	lastTripTime = newLastTripTime;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:48:40 AM)
 * @param newMvarID int
 */
public void setMvarID(int newMvarID) {
	mvarID = newMvarID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:48:40 AM)
 * @param newMwID int
 */
public void setMwID(int newMwID) {
	mwID = newMwID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:48:40 AM)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Creation date: (9/4/2001 1:51:10 PM)
 * @param newPreviousTripTime1 int
 */
public void setPreviousTripTime1(int newPreviousTripTime1) {
	previousTripTime1 = newPreviousTripTime1;
}
/**
 * Creation date: (9/4/2001 1:51:10 PM)
 * @param newPreviousTripTime2 int
 */
public void setPreviousTripTime2(int newPreviousTripTime2) {
	previousTripTime2 = newPreviousTripTime2;
}
/**
 * Creation date: (9/4/2001 1:51:10 PM)
 * @param newPreviousTripTime3 int
 */
public void setPreviousTripTime3(int newPreviousTripTime3) {
	previousTripTime3 = newPreviousTripTime3;
}
/**
 * Creation date: (9/4/2001 2:30:49 PM)
 * @param newStatusID int
 */
public void setStatusID(int newStatusID) {
	statusID = newStatusID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:48:40 AM)
 * @param newSubstationID int
 */
public void setSubstationID(int newSubstationID) {
	substationID = newSubstationID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 2:40:09 PM)
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
		"GraphDefinitionID"
	};

	Object[] setValues =
	{
		getName(),
		new Integer( getSubstationID() ),
		new Integer( getStatusID() ),
		new Integer( getMwID() ),
		new Integer( getMvarID() ),
		new Integer( getGraphDefinitionID() )
	};

	String[] constraintColumns =
	{
		"DCBID"
	};

	Object[] constraintValues =
	{
		new Integer(getID())
	};

	update( tableName, setColumns, setValues, constraintColumns, constraintValues );*/
}
}
