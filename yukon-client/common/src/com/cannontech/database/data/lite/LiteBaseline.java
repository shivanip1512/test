package com.cannontech.database.data.lite;

import com.cannontech.database.db.baseline.Baseline;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 11:08:47 AM)
 * @author: 
 */
public class LiteBaseline extends LiteBase
{
	private String baselineName;
/**
 * LiteHolidaySchedule constructor comment.
 */
public LiteBaseline()
{
	super();

	setLiteType(LiteTypes.BASELINE);
}
/**
 * LiteHolidaySchedule constructor comment.
 */
public LiteBaseline(int baselineID)
{
	super();

	setLiteID( baselineID );
	setLiteType(LiteTypes.BASELINE);
}

/**
 * LiteHolidaySchedule constructor comment.
 */
public LiteBaseline(int baselineID, String blName_ )
{
	this( baselineID );
	setBaselineName( blName_ );
}

/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:13:50 AM)
 * @return int
 */
public int getBaselineID() 
{
	return getLiteID();
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:14:47 AM)
 * @return java.lang.String
 */
public String getBaselineName() {
	return baselineName;
}
/**
 * retrieve method comment.
 */
public void retrieve(String databaseAlias) 
{
 
   com.cannontech.database.SqlStatement s = 
      new com.cannontech.database.SqlStatement(
         "SELECT baselineID, baselineName "  + 
            "FROM " + com.cannontech.database.db.baseline.Baseline.TABLE_NAME +
            " where baselineID = " + getBaselineID(),
         com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

   try 
   {
      s.execute();

      if( s.getRowCount() <= 0 )
         throw new IllegalStateException("Unable to find baseline with ID = " + getLiteID() );


      setBaselineID( new Integer(s.getRow(0)[0].toString()).intValue() );
      setBaselineName( s.getRow(0)[1].toString() );
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
public void setBaselineID( int baselineID )
{
	setLiteID( baselineID );
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:13:50 AM)
 * @return void
 */
public void setBaselineName( String name )
{
	baselineName = name;
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:15:17 AM)
 * @return java.lang.String
 */
public String toString() 
{
	return getBaselineName();
}

public final java.util.Vector getAllBaselines(java.sql.Connection conn)
{
	java.util.Vector returnVector = new java.util.Vector();
	Integer baselineID = null;
	String 	baselineName = null;
	
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	String sql = "SELECT baselineID, baselineName FROM " +
	com.cannontech.database.db.baseline.Baseline.TABLE_NAME +
		" ORDER BY baselineID";

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
				baselineID = new Integer( rset.getInt("baselineID") );
				baselineName = rset.getString("baselineName");
				
				returnVector.addElement( new LiteBaseline(
						baselineID.intValue(), 
						baselineName ));				
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

public java.util.Vector getAllBaselines()
{
	java.util.Vector tempVector = null;
	
	try
	{
		java.sql.Connection conn = null;
	
		conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");

		tempVector = getAllBaselines(conn);

		conn.close();
	}
	catch( java.sql.SQLException e2 )
	{
		com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );
	}	
	
	return tempVector;
	 
	
}
}
