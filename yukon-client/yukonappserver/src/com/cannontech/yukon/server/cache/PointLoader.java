package com.cannontech.yukon.server.cache;

import com.cannontech.database.data.point.PointTypes;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
public class PointLoader implements Runnable {
	private java.util.ArrayList allPoints = null;
	private String databaseAlias = null;
/**
 * DeviceLoader constructor comment.
 */
public PointLoader(java.util.ArrayList pointArray, String alias) {
	super();
	this.allPoints = pointArray;
	this.databaseAlias = alias;
}

private void executeNonSQL92Query()
{
   
   String sqlString = 
      "SELECT POINTID,POINTNAME,POINTTYPE,PAOBJECTID, " +
      "POINTOFFSET,STATEGROUPID FROM POINT WHERE POINTID > " + PointTypes.SYS_PID_SYSTEM + " " + 
      "ORDER BY PAObjectID, POINTOFFSET";

   java.sql.Connection conn = null;
   java.sql.Statement stmt = null;
   java.sql.ResultSet rset = null;
   
   try
   {
      conn = com.cannontech.database.PoolManager.getInstance().getConnection( this.databaseAlias );
      stmt = conn.createStatement();
      rset = stmt.executeQuery(sqlString);

      while (rset.next())
      {
         int pointID = rset.getInt(1);
         String pointName = rset.getString(2).trim();
         String pointType = rset.getString(3).trim();
         int paobjectID = rset.getInt(4);
         int pointOffset = rset.getInt(5);
         int stateGroupID = rset.getInt(6);
                           
         com.cannontech.database.data.lite.LitePoint lp =
            new com.cannontech.database.data.lite.LitePoint( 
                  pointID, pointName, 
                  com.cannontech.database.data.point.PointTypes.getType(pointType),
                  paobjectID, pointOffset, stateGroupID );

         allPoints.add(lp);
      }
   }
   catch( java.sql.SQLException e )
   {
      com.cannontech.clientutils.CTILogger.error( e.getMessage(), e);
   }
   finally
   {
      try
      {
         if( stmt != null )
            stmt.close();
         if( conn != null )
            conn.close();
      }
      catch( java.sql.SQLException e )
      {
         com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
      }
      
   }

   loadPointTags();   
}

/**
 * run method comment.
 */
public void run() {
//temp code
java.util.Date timerStart = null;
java.util.Date timerStop = null;
//temp code

//temp code
timerStart = new java.util.Date();
//temp code
//	String sqlString = "SELECT POINTID,POINTNAME,POINTTYPE,PAOBJECTID, " +
//		"POINTOFFSET,STATEGROUPID FROM POINT WHERE POINTID > 0 ORDER BY PAObjectID, POINTOFFSET";

	String sqlString = "SELECT P.POINTID, POINTNAME, POINTTYPE, PAOBJECTID, POINTOFFSET, STATEGROUPID, FORMULA"+
						" FROM ( POINT P LEFT OUTER JOIN POINTUNIT PU "+
						" ON P.POINTID = PU.POINTID )  LEFT OUTER JOIN UNITMEASURE UM ON PU.UOMID = UM.UOMID "+
						" WHERE P.POINTID > " + PointTypes.SYS_PID_SYSTEM +
						" ORDER BY PAObjectID, POINTOFFSET ";
	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;
	
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection( this.databaseAlias );
		stmt = conn.createStatement();
		rset = stmt.executeQuery(sqlString);

		while (rset.next())
		{
			int pointID = rset.getInt(1);
			String pointName = rset.getString(2).trim();
			String pointType = rset.getString(3).trim();
			int paobjectID = rset.getInt(4);
			int pointOffset = rset.getInt(5);
			int stateGroupID = rset.getInt(6);
			long tags = com.cannontech.database.data.lite.LitePoint.POINT_UOFM_GRAPH;
			String formula = rset.getString(7);
			
         //process all the bit mask tags here
         if( "usage".equalsIgnoreCase(formula) )
				tags = com.cannontech.database.data.lite.LitePoint.POINT_UOFM_USAGE;
									
			com.cannontech.database.data.lite.LitePoint lp =
				new com.cannontech.database.data.lite.LitePoint( pointID, pointName, com.cannontech.database.data.point.PointTypes.getType(pointType),
																						paobjectID, pointOffset, stateGroupID, tags );

			allPoints.add(lp);
		}
	}
   catch( java.sql.SQLException e )
   {
      try
      { ///close all the stuff here
         if( stmt != null )
            stmt.close();
         if( conn != null )
            conn.close();
            
         stmt = null;
         conn = null;
      }
      catch( java.sql.SQLException ex )
      {
         com.cannontech.clientutils.CTILogger.error( ex.getMessage(), ex);
      }

      com.cannontech.clientutils.CTILogger.error(" DB : PointLoader query did not work, trying Query with a non SQL-92 query");
      //try using a nonw SQL-92 method, will be slower
      //  Oracle 8.1.X and less will use this
      executeNonSQL92Query();
   }
   finally
   {
      try
      {
         if( stmt != null )
            stmt.close();
         if( conn != null )
            conn.close();
      }
      catch( java.sql.SQLException e )
      {
         com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
      }
      
   }

	// we've only defaulted the point tags to GRAPH in our constructor (mainly because status points have no unitmeasure)
	// now we must load the points that have unitmeasures tag values
//	loadPointTags();

}

//  removed this function, load time was awful.  Fixed (bettered, rather) by using a query outer join
// in the PointLoader.
private synchronized void loadPointTags()
{
	if( allPoints == null )
		return;
	
	//temp code
	java.util.Date timerStart = null;
	java.util.Date timerStop = null;
	//temp code
	
	//temp code
	timerStart = new java.util.Date();
	//temp code
	
	int ptUpdateCnt = 0;
	
	String sqlString = "SELECT PU.POINTID, UM.FORMULA " +
		"FROM POINTUNIT PU , UNITMEASURE UM WHERE PU.UOMID = UM.UOMID";
	
	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;
	try
	{
		System.out.println(" START TAG LOADER QUERY");
		conn = com.cannontech.database.PoolManager.getInstance().getConnection( this.databaseAlias );
		stmt = conn.createStatement();
		rset = stmt.executeQuery(sqlString);
		System.out.println(" END TAG LOADER QUERY");
		//All points NOT in the unitmeasure table have been defaulted to GRAPH tag in the allPoints loader.
		while (rset.next())
		{
			boolean isStatus = true;
			int pointID = rset.getInt(1);
			String formula = rset.getString(2);

			for( int i = 0; i < allPoints.size(); i++ )
			{
				 com.cannontech.database.data.lite.LitePoint point =((com.cannontech.database.data.lite.LitePoint)allPoints.get(i));
				 if( point.getPointID() == pointID )
				 {
					// tags may need to be changed here if there are more tags added to this bit field
					long tags = com.cannontech.database.data.lite.LitePoint.POINT_UOFM_GRAPH;      //default value of tags for now.

					if( formula.equalsIgnoreCase("usage"))
					{
						 tags = com.cannontech.database.data.lite.LitePoint.POINT_UOFM_USAGE;
						 ptUpdateCnt++;
					}

					((com.cannontech.database.data.lite.LitePoint)allPoints.get(i)).setTags(tags);
					break;
				 }
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
			if( stmt != null )
				 stmt.close();
			if( conn != null )
				conn.close();
		}
		catch( java.sql.SQLException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}

		//temp code
		timerStop = new java.util.Date();
		com.cannontech.clientutils.CTILogger.info( 
			(timerStop.getTime() - timerStart.getTime())*.001 + " Secs for loadAllPointTags(" + ptUpdateCnt + " updated)" );
		//temp code

	}
}


}