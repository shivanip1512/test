package com.cannontech.yukon.server.cache;

import java.util.List;

import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.UnitOfMeasure;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
public class SystemPointLoader implements Runnable 
{
    private List<LitePoint> allPoints = null;
    private String databaseAlias = null;

/**
 * PointLoader constructor comment.
 */
public SystemPointLoader(List<LitePoint> pointArray,  String alias) 
{
    super();
    this.allPoints = pointArray;
    this.databaseAlias = alias;
}

private void executeNonSQL92Query()
{
   
   String sqlString = 
      "SELECT POINTID,POINTNAME,POINTTYPE,PAOBJECTID, " +
      "POINTOFFSET,STATEGROUPID FROM POINT " +
      "WHERE PAOBJECTID = 0 AND POINTID > 0 "+
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
                           
         LitePoint lp =
            new LitePoint( 
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
	   SqlUtils.close(rset, stmt, conn );
      
   }

}

/**
 * run method comment.
 */
public void run() {
java.util.Date timerStart = null;
java.util.Date timerStop = null;

timerStart = new java.util.Date();


    String sqlString = "SELECT P.POINTID, POINTNAME, POINTTYPE, PAOBJECTID, POINTOFFSET, STATEGROUPID, UM.FORMULA, UM.UOMID" +
                        " FROM ( POINT P LEFT OUTER JOIN POINTUNIT PU "+
                        " ON P.POINTID = PU.POINTID )  LEFT OUTER JOIN UNITMEASURE UM ON PU.UOMID = UM.UOMID "+
                        "WHERE PAOBJECTID = 0 AND P.POINTID > 0 "+
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
            String formula = rset.getString(7);
            int uofmID = rset.getInt(8);
            if( rset.wasNull() ) //if uomid is null, set it to an INVALID int
                uofmID = UnitOfMeasure.INVALID.getId();

            
         //process all the bit mask tags here
            long tags = LitePoint.POINT_UOFM_GRAPH;
         if( "usage".equalsIgnoreCase(formula) )
                tags = LitePoint.POINT_UOFM_USAGE;
                                    
            LitePoint lp =
                new LitePoint( pointID, pointName, com.cannontech.database.data.point.PointTypes.getType(pointType),
                                                                                        paobjectID, pointOffset, stateGroupID, tags, uofmID );

            allPoints.add(lp);
            
        }
    }
   catch( java.sql.SQLException e )
   {
	   SqlUtils.close(rset, stmt, conn );

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
        timerStop = new java.util.Date();
        com.cannontech.clientutils.CTILogger.info( 
            (timerStop.getTime() - timerStart.getTime())*.001 + " Secs for PointLoader (" + allPoints.size() + " found)" );
   }

}
}