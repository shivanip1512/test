/*
 * Created on Mar 1, 2004
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
public class LiteLMScenario extends LiteBase 
{
	private String scenarioName;
	private Integer scenarioType;
	/**
	 * LiteHolidaySchedule constructor comment.
	 */
	public LiteLMScenario()
	{
		super();

		setLiteType(LiteTypes.LMCONSTRAINT);
	}
	/**
	 * LiteHolidaySchedule constructor comment.
	 */
	public LiteLMScenario(int scenarioID)
	{
		super();

		setLiteID( scenarioID );
		setLiteType(LiteTypes.LMCONSTRAINT);
	}

	/**
	 * LiteHolidaySchedule constructor comment.
	 */
	public LiteLMScenario(int scenarioID, String conName_ )
	{
		this( scenarioID );
		setLMScenarioName( conName_ );
	}

	public LiteLMScenario(int scenarioID, String conName_, Integer type )
	{
		this( scenarioID );
		setLMScenarioName( conName_ );
		setLMScenarioType( type );
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/24/2001 11:13:50 AM)
	 * @return int
	 */
	public int getScenarioID() 
	{
		return getLiteID();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/24/2001 11:14:47 AM)
	 * @return java.lang.String
	 */
	public String getLMScenarioName() {
		return scenarioName;
	}

	public Integer getLMScenarioType() {
		return scenarioType;
	}
	/**
	 * retrieve method comment.
	 */
	public void retrieve(String databaseAlias) 
	{
 
	   com.cannontech.database.SqlStatement s = 
		  new com.cannontech.database.SqlStatement(
			 "SELECT scenarioID, scenarioName, scenarioType "  + 
				"FROM " + com.cannontech.database.db.device.lm.LMControlScenarioProgram.TABLE_NAME +
				" where scenarioID = " + getScenarioID(),
			 com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

	   try 
	   {
		  s.execute();

		  if( s.getRowCount() <= 0 )
			 throw new IllegalStateException("Unable to find scenario with ID = " + getLiteID() );


		  setLMScenarioID( new Integer(s.getRow(0)[0].toString()).intValue() );
		  setLMScenarioName( s.getRow(0)[1].toString() );
		  setLMScenarioType( new Integer(s.getRow(0)[2].toString()));
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
	public void setLMScenarioID( int conID )
	{
		setLiteID( conID );
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/24/2001 11:13:50 AM)
	 * @return void
	 */
	public void setLMScenarioName( String name )
	{
		scenarioName = name;
	}

	public void setLMScenarioType( Integer type )
	{
		scenarioType = type;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/24/2001 11:15:17 AM)
	 * @return java.lang.String
	 */
	public String toString() 
	{
		return getLMScenarioName();
	}

	public final java.util.Vector getAllLMScenarios(java.sql.Connection conn)
	{
		java.util.Vector returnVector = new java.util.Vector();
		Integer scenarioID = null;
		String 	scenarioName = null;
		Integer scenarioType = null;
	
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		String sql = "SELECT scenarioID, scenarioName, scenarioType FROM " +
		com.cannontech.database.db.device.lm.LMControlScenarioProgram.TABLE_NAME +
			" ORDER BY scenarioID";

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
					scenarioID = new Integer( rset.getInt("scenarioID") );
					scenarioName = rset.getString("scenarioName");
					scenarioType = new Integer( rset.getInt("scenarioType"));
				
					returnVector.addElement( new LiteLMScenario(
							scenarioID.intValue(), 
							scenarioName, scenarioType ));				
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

	public java.util.Vector getAllLMScenarios()
	{
		java.util.Vector tempVector = null;
	
		try
		{
			java.sql.Connection conn = null;
	
			conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");

			tempVector = getAllLMScenarios(conn);

			conn.close();
		}
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );
		}	
	
		return tempVector;
	 
	
	}

}
