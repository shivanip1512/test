package com.cannontech.database.data.lite;

import com.cannontech.database.data.point.PointTypes;

/*
 */
public class LitePoint extends LiteBase
{
	private String pointName = null;
	private int pointType = 0;
	private int paobjectID = 0;
	private int pointOffset = 0;
	private int stateGroupID = 0;
	
	boolean showPointOffsets = true;

	//used to represent a NON filler location for lite points
	public final static LitePoint NONE_LITE_PT = new LitePoint(
			PointTypes.SYS_PID_SYSTEM, 
			"System Point",
			PointTypes.INVALID_POINT,
			0,0,0 );
		
	// tags is used as a bit represention of data about this point
	long tags = 0x00000000;			// not used

	public final static long POINT_UOFM_GRAPH = 0x00000001;  //KW, KVAR, MVAR...
	public final static long POINT_UOFM_USAGE = 0x00000002;	 //KWH, KVAH, KVARH...

	//ADD NEW TAG VALUES HERE
/**
 * LitePoint
 */
public LitePoint( int pntID ) 
{
	super();
	setLiteID(pntID);
	setLiteType(LiteTypes.POINT);
}
/**
 * LiteDevice
 */
public LitePoint( int pntID, String pntName, int pntType, int paoID, int pntOffset, int stateGroupid ) 
{
	super();
	
	setLiteID(pntID);	
	pointName = new String(pntName);
	setPointType( pntType );
	setPaobjectID( paoID );
	pointOffset = pntOffset;
	setLiteType(LiteTypes.POINT);
	stateGroupID = stateGroupid;
}

/**
 * LiteDevice
 */
public LitePoint( int pntID, String pntName ) 
{
	this( pntID );	
	setPointName( pntName );
}

/**
 * LiteDevice
 */
public LitePoint( int pntID, String pntName, int pntType, int paoID, int pntOffset,
				int stateGroupid, long pntTag )
{
	super();
	
	setLiteID(pntID);
	pointName = new String(pntName);
	pointType = pntType;
	setPaobjectID( paoID );
	pointOffset = pntOffset;
	setLiteType(LiteTypes.POINT);
	stateGroupID = stateGroupid;
	tags = pntTag;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 1:56:09 PM)
 * @return int
 */
public int getPaobjectID() {
	return paobjectID;
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public int getPointID() {
	return getLiteID();
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public String getPointName() {
	return pointName;
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public int getPointOffset() {
	return pointOffset;
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public int getPointType() {
	return pointType;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 9:35:03 AM)
 * @return boolean
 */
public boolean getShowPointOffsets() {
	return showPointOffsets;
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2001 5:24:00 PM)
 * @return int
 */
public int getStateGroupID() {
	return stateGroupID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/2001 3:00:31 PM)
 * @return long
 */
public long getTags() {
	return tags;
}

private synchronized void loadPointTags( String databaseAlias )
{
	String sqlString = "SELECT UM.FORMULA " +
		"FROM POINTUNIT PU , UNITMEASURE UM WHERE PU.UOMID = UM.UOMID " + 
		"AND PU.POINTID = " + getPointID();
	
 	com.cannontech.database.SqlStatement stmt =
 		new com.cannontech.database.SqlStatement(
         sqlString,
         databaseAlias );

	try
	{
 		stmt.execute();
		String formula = (String) stmt.getRow(0)[0];

		// tags may need to be changed here if there are more tags added to this bit field
		long tags = com.cannontech.database.data.lite.LitePoint.POINT_UOFM_GRAPH;      //default value of tags for now.

		if( "usage".equalsIgnoreCase(formula) )
			tags = com.cannontech.database.data.lite.LitePoint.POINT_UOFM_USAGE;

		setTags( tags );
	}
	catch( Exception e )
	{
	  com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
}

private void executeNonSQL92Retrieve( String databaseAlias )
{
   
   String sqlString = 
      "SELECT POINTNAME,POINTTYPE,PAOBJECTID, " +
      "POINTOFFSET,STATEGROUPID FROM POINT " +
		"WHERE POINTID = " + getPointID() + " "  +       
      "ORDER BY PAObjectID, POINTOFFSET";

 	com.cannontech.database.SqlStatement stmt =
 		new com.cannontech.database.SqlStatement(
         sqlString,
         databaseAlias );
   
   try
   {
 		stmt.execute();
		setPointName( ((String) stmt.getRow(0)[0]) );
		setPointType( PointTypes.getType(((String) stmt.getRow(0)[1])) );
		setPaobjectID( ((java.math.BigDecimal) stmt.getRow(0)[2]).intValue() );
		setPointOffset( ((java.math.BigDecimal) stmt.getRow(0)[3]).intValue() );
		setStateGroupID( ((java.math.BigDecimal) stmt.getRow(0)[4]).intValue() );
   }
   catch( Exception e )
   {
      com.cannontech.clientutils.CTILogger.error( e.getMessage(), e);
   }

   loadPointTags( databaseAlias );   
}

/**
 * retrieve method comment.
 */
public void retrieve(String databaseAlias) 
{
   //nearly the same as the PointLoader's run() method
   String sqlString = 
            "SELECT POINTNAME, POINTTYPE, PAOBJECTID, POINTOFFSET, STATEGROUPID, FORMULA"+
            " FROM ( POINT P LEFT OUTER JOIN POINTUNIT PU "+
            " ON P.POINTID = PU.POINTID )  LEFT OUTER JOIN UNITMEASURE UM ON PU.UOMID = UM.UOMID "+
            " WHERE P.POINTID = " + Integer.toString(getPointID());
   
 	com.cannontech.database.SqlStatement stmt =
 		new com.cannontech.database.SqlStatement(
         sqlString,
         databaseAlias );


 	try
 	{
 		stmt.execute();
		setPointName( ((String) stmt.getRow(0)[0]) );
		setPointType( PointTypes.getType(((String) stmt.getRow(0)[1])) );
		setPaobjectID( ((java.math.BigDecimal) stmt.getRow(0)[2]).intValue() );
		setPointOffset( ((java.math.BigDecimal) stmt.getRow(0)[3]).intValue() );
		setStateGroupID( ((java.math.BigDecimal) stmt.getRow(0)[4]).intValue() );
      
      
      String formula = (String) stmt.getRow(0)[5];      
      if( "usage".equalsIgnoreCase(formula) )
         setTags( LitePoint.POINT_UOFM_USAGE );
      else
         setTags( LitePoint.POINT_UOFM_GRAPH );      
 	}
 	catch( Exception e )
 	{
      com.cannontech.clientutils.CTILogger.error(" DB : LitePoint.retrieve() query did not work, trying Query with a non SQL-92 query");
      //try using a nonw SQL-92 method, will be slower
      //  Oracle 8.1.X and less will use this
 		executeNonSQL92Retrieve( databaseAlias );
 	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 1:56:09 PM)
 * @param newPaobjectID int
 */
public void setPaobjectID(int newPaobjectID) {
	paobjectID = newPaobjectID;
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public void setPointID(int newValue) 
{
	setLiteID(newValue);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public void setPointName(String newValue) {
	this.pointName = new String(newValue);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public void setPointOffset(int newValue) {
	this.pointOffset = newValue;
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public void setPointType(int newValue) {
	this.pointType = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 9:35:37 AM)
 * @param val boolean
 */
public void setShowPointOffsets(boolean val)
{
	showPointOffsets = val;
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2001 5:24:00 PM)
 * @param newStateGroupID int
 */
public void setStateGroupID(int newStateGroupID) {
	stateGroupID = newStateGroupID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/2001 3:01:22 PM)
 * @param newtags long
 */
public void setTags(long newtags)
{
	tags = newtags;
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public String toString()
{
	if (showPointOffsets)
	{
		if( getPointType() == PointTypes.CALCULATED_POINT
			 || getPointType() == PointTypes.INVALID_POINT )
		{
			return getPointName();
		}
		else
		{
			if (getPointOffset() == 0)
			{
				return "#p" + " " + pointName;
			}
			else
				return "#" + getPointOffset() + " " + pointName;
		}

	}
	else
		return getPointName();
}
}
