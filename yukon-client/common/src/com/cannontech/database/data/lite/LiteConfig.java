/*
 * Created on Dec 18, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.lite;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

import com.cannontech.database.db.config.MCTConfig;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 11:08:47 AM)
 * @author: 
 */
public class LiteConfig extends LiteBase
{
	private String configName;
/**
 * LiteHolidaySchedule constructor comment.
 */
public LiteConfig()
{
	super();

	setLiteType(LiteTypes.CONFIG);
}
/**
 * LiteHolidaySchedule constructor comment.
 */
public LiteConfig(int configID)
{
	super();

	setLiteID( configID );
	setLiteType(LiteTypes.CONFIG);
}

/**
 * LiteHolidaySchedule constructor comment.
 */
public LiteConfig(int configID, String conName_ )
{
	this( configID );
	setConfigName( conName_ );
}

/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:13:50 AM)
 * @return int
 */
public int getConfigID() 
{
	return getLiteID();
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:14:47 AM)
 * @return java.lang.String
 */
public String getConfigName() {
	return configName;
}
/**
 * retrieve method comment.
 */
public void retrieve(String databaseAlias) 
{
 
   com.cannontech.database.SqlStatement s = 
	  new com.cannontech.database.SqlStatement(
		 "SELECT configID, configName "  + 
			"FROM " + com.cannontech.database.db.config.MCTConfig.TABLE_NAME +
			" where configID = " + getConfigID(),
		 com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

   try 
   {
	  s.execute();

	  if( s.getRowCount() <= 0 )
		 throw new IllegalStateException("Unable to find config with ID = " + getLiteID() );


	  setConfigID( new Integer(s.getRow(0)[0].toString()).intValue() );
	  setConfigName( s.getRow(0)[1].toString() );
   }
   catch( Exception e )
   {
	  com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
   }
      
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:13:50 AM)
 * @return void
 */
public void setConfigID( int conID )
{
	setLiteID( conID );
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:13:50 AM)
 * @return void
 */
public void setConfigName( String name )
{
	configName = name;
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:15:17 AM)
 * @return java.lang.String
 */
public String toString() 
{
	return getConfigName();
}

public final java.util.Vector getAllConfigs(java.sql.Connection conn)
{
	java.util.Vector returnVector = new java.util.Vector();
	Integer configID = null;
	String 	configName = null;
	
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	String sql = "SELECT configID, configName FROM " +
	com.cannontech.database.db.config.MCTConfig.TABLE_NAME +
		" ORDER BY configID";

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be (null).");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
						
			rset = pstmt.executeQuery();
	
			while( rset.next() )
			{
				configID = new Integer( rset.getInt("configID") );
				configName = rset.getString("configName");
				
				returnVector.addElement( new LiteConfig(
						configID.intValue(), 
						configName ));				
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
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}


	return returnVector;
}

public java.util.Vector getAllConfigs()
{
	java.util.Vector tempVector = null;
	
	try
	{
		java.sql.Connection conn = null;
	
		conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");

		tempVector = getAllConfigs(conn);

		conn.close();
	}
	catch( java.sql.SQLException e2 )
	{
		com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );
	}	
	
	return tempVector;
	 
	
}
}