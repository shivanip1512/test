/*
 * Created on May 17, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.lite;

import com.cannontech.database.db.device.lm.LMProgramDirectGear;
/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

//At long last, we can reveal the lite gear 
public class LiteGear extends LiteBase
{
	private String gearName;
	private String gearType;
	private int ownerID;
	private int gearNumber;
/**
 * LiteHolidaySchedule constructor comment.
 */
public LiteGear()
{
	super();

	setLiteType(LiteTypes.GEAR);
}
/**
 * LiteHolidaySchedule constructor comment.
 */
public LiteGear(int gearID)
{
	super();

	setLiteID( gearID );
	setLiteType(LiteTypes.GEAR);
}

/**
 * LiteHolidaySchedule constructor comment.
 */
public LiteGear(int gearID, String conName_ )
{
	this( gearID );
	setGearName( conName_ );
}

public LiteGear(int gearID, String conName_, String type )
{
	this( gearID );
	setGearName( conName_ );
	setGearType( type );
}
/**
 * Insert the method's description here.
 * @return int
 */
public int getGearID() 
{
	return getLiteID();
}
/**
 * Insert the method's description here.
 * @return java.lang.String
 */
public String getGearName() {
	return gearName;
}

public String getGearType() {
	return gearType;
}

public int getOwnerID() {
	return ownerID;
}

public int getGearNumber() {
	return gearNumber;
}
/**
 * retrieve method comment.
 */
public void retrieve(String databaseAlias) 
{
 
   com.cannontech.database.SqlStatement s = 
	  new com.cannontech.database.SqlStatement(
		 "SELECT gearID, gearName, controlMethod, deviceID, gearNumber "  + 
			"FROM " + LMProgramDirectGear.TABLE_NAME +
			" where gearID = " + getGearID(),
		 com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

   try 
   {
	  s.execute();

	  if( s.getRowCount() <= 0 )
		 throw new IllegalStateException("Unable to find gear with ID = " + getLiteID() );


	  setGearID( new Integer(s.getRow(0)[0].toString()).intValue() );
	  setGearName( s.getRow(0)[1].toString() );
	  setGearType( s.getRow(0)[2].toString());
	  setOwnerID( new Integer(s.getRow(0)[3].toString()).intValue() );
	  setGearNumber( new Integer(s.getRow(0)[4].toString()).intValue() );
   }
   catch( Exception e )
   {
	  com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
   }
      
}
/**
 * Insert the method's description here.
 * @return void
 */
public void setGearID( int conID )
{
	setLiteID( conID );
}
/**
 * Insert the method's description here.
 * @return void
 */
public void setGearName( String name )
{
	gearName = name;
}

public void setGearType( String type )
{
	gearType = type;
}

public void setOwnerID(int id)
{
	ownerID = id;
}

public void setGearNumber(int newNum)
{
	gearNumber = newNum;
}
/**
 * Insert the method's description here.
 * @return java.lang.String
 */
public String toString() 
{
	return getGearName();
}

/*
public final java.util.Vector getSpecificLiteGears(java.sql.Connection conn)
{
	java.util.Vector returnVector = new java.util.Vector();
	int gearID = null;
	String 	gearName = null;
	String gearType = null;
	Integer
	
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	String sql = "SELECT gearID, gearName, controlMethod, deviceID, gearNumber "  + 
			"FROM " + LMProgramDirectGear.TABLE_NAME +
		" ORDER BY gearID";

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
				gearID = new Integer( rset.getInt("gearID") );
				gearName = rset.getString("gearName");
				gearType =  rset.getString("gearType");
				ownerID = new Integer( rset.getInt("ownerID"));
				gearNumber = 
				
				returnVector.addElement( new LiteGear(
						gearID.intValue(), 
						gearName, gearType ));				
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

public java.util.Vector getAllGears()
{
	java.util.Vector tempVector = null;
	
	try
	{
		java.sql.Connection conn = null;
	
		conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");

		tempVector = getAllGears(conn);

		conn.close();
	}
	catch( java.sql.SQLException e2 )
	{
		com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );
	}	
	
	return tempVector;
}
*/	 

}