package com.cannontech.yukon.server.cache;

import com.cannontech.database.SqlUtils;

/**
 * @author rneuharth
 * Aug 1, 2002 at 2:58:33 PM
 * 
 * A undefined generated comment
 */
public class YukonImageLoader implements Runnable
{
   private java.util.ArrayList allStateImages = null;
   private String databaseAlias = null;

	/**
	 * Constructor for StateImageLoader.
	 */
	public YukonImageLoader(java.util.ArrayList sImageArray, String alias) 
   {
      super();
      this.allStateImages = sImageArray;
      this.databaseAlias = alias;
   }

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
   
   //temp code
   java.util.Date timerStart = null;
   java.util.Date timerStop = null;
   //temp code
   
   //temp code
   timerStart = new java.util.Date();
   //temp code
   
      //get all the customer contacts that are assigned to a customer
      String sqlString = "SELECT ImageID,ImageCategory,ImageName,ImageValue from " +
                  com.cannontech.database.db.state.YukonImage.TABLE_NAME +
                  " where ImageID > " + com.cannontech.database.db.state.YukonImage.NONE_IMAGE_ID;
   
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
            int imgID = rset.getInt(1);            
            String imgCat = rset.getString(2).trim();
            String imgName = rset.getString(3).trim();
            byte[] bStream = rset.getBytes(4);
            
            com.cannontech.database.data.lite.LiteYukonImage lsi =
               new com.cannontech.database.data.lite.LiteYukonImage(
                  imgID, 
                  imgCat, 
                  imgName,
                  bStream );

   
            allStateImages.add( lsi );
         }
      }
      catch( java.sql.SQLException e )
      {
         com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
      }
      finally
      {
    	  SqlUtils.close(rset, stmt, conn );
   //temp code
   timerStop = new java.util.Date();
   com.cannontech.clientutils.CTILogger.info( 
       (timerStop.getTime() - timerStart.getTime())*.001 + 
       " Secs for StateImageLoader (" + allStateImages.size() + " loaded)" );
   //temp code
      }
   
   }

}
