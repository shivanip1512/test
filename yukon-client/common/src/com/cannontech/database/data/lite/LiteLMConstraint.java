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
public class LiteLMConstraint extends LiteBase 
{
	private String constraintName;
	private Integer constraintType;
	/**
	 * LiteHolidaySchedule constructor comment.
	 */
	public LiteLMConstraint()
	{
		super();

		setLiteType(LiteTypes.LMCONSTRAINT);
	}
	/**
	 * LiteHolidaySchedule constructor comment.
	 */
	public LiteLMConstraint(int constraintID)
	{
		super();

		setLiteID( constraintID );
		setLiteType(LiteTypes.LMCONSTRAINT);
	}

	/**
	 * LiteHolidaySchedule constructor comment.
	 */
	public LiteLMConstraint(int constraintID, String conName_ )
	{
		this( constraintID );
		setConstraintName( conName_ );
	}

	public LiteLMConstraint(int constraintID, String conName_, Integer type )
	{
		this( constraintID );
		setConstraintName( conName_ );
		setLMConstraintType( type );
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/24/2001 11:13:50 AM)
	 * @return int
	 */
	public int getConstraintID() 
	{
		return getLiteID();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/24/2001 11:14:47 AM)
	 * @return java.lang.String
	 */
	public String getConstraintName() {
		return constraintName;
	}

	public Integer getLMConstraintType() {
		return constraintType;
	}
	/**
	 * retrieve method comment.
	 */
	public void retrieve(String databaseAlias) 
	{
 
	   com.cannontech.database.SqlStatement s = 
		  new com.cannontech.database.SqlStatement(
			 "SELECT constraintID, constraintName"  + 
				" FROM " + com.cannontech.database.db.device.lm.LMProgramConstraint.TABLE_NAME +
				" where constraintID = " + getConstraintID(),
			 com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

	   try 
	   {
		  s.execute();

		  if( s.getRowCount() <= 0 )
			 throw new IllegalStateException("Unable to find constraint with ID = " + getLiteID() );


		  setConstraintID( new Integer(s.getRow(0)[0].toString()).intValue() );
		  setConstraintName( s.getRow(0)[1].toString() );
		  setLMConstraintType( new Integer(s.getRow(0)[2].toString()));
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
	public void setConstraintID( int conID )
	{
		setLiteID( conID );
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/24/2001 11:13:50 AM)
	 * @return void
	 */
	public void setConstraintName( String name )
	{
		constraintName = name;
	}

	public void setLMConstraintType( Integer type )
	{
		constraintType = type;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/24/2001 11:15:17 AM)
	 * @return java.lang.String
	 */
	public String toString() 
	{
		return getConstraintName();
	}

	public final java.util.Vector getAllLMConstraints(java.sql.Connection conn)
	{
		java.util.Vector returnVector = new java.util.Vector();
		Integer constraintID = null;
		String 	constraintName = null;
		Integer constraintType = null;
	
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		String sql = "SELECT constraintID, constraintName, constraintType FROM " +
		com.cannontech.database.db.device.lm.LMProgramConstraint.TABLE_NAME +
			" ORDER BY constraintID";

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
					constraintID = new Integer( rset.getInt("constraintID") );
					constraintName = rset.getString("constraintName");
					constraintType = new Integer( rset.getInt("constraintType"));
				
					returnVector.addElement( new LiteLMConstraint(
							constraintID.intValue(), 
							constraintName, constraintType ));				
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

	public java.util.Vector getAllLMConstraints()
	{
		java.util.Vector tempVector = null;
	
		try
		{
			java.sql.Connection conn = null;
	
			conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");

			tempVector = getAllLMConstraints(conn);

			conn.close();
		}
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );
		}	
	
		return tempVector;
	 
	
	}

}
