package com.cannontech.database.db.pao;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.point.PointTypes;

/**
 * This type was created in VisualAge.
 */

public class PAOExclusion extends com.cannontech.database.db.DBPersistent
{
	private Integer exclusionID = null;
	private Integer paoID = null; //new Integer(CtiUtilities.NONE_ID);
	private Integer excludedPaoID = null; //new Integer(CtiUtilities.NONE_ID);
	private Integer pointID = new Integer(PointTypes.SYS_PID_SYSTEM);
	private Integer value = new Integer(CtiUtilities.NONE_ID);
	private Integer functionID = new Integer(CtiUtilities.NONE_ID);
	private String funcName = CtiUtilities.STRING_NONE;
	private Integer funcRequeue = new Integer(CtiUtilities.NONE_ID);
	private String funcParams = CtiUtilities.STRING_NONE;


	public static final String SETTER_COLUMNS[] = 
	{ 
		"PaoID", "ExcludedPaoID", "PointID",
		"Value", "FunctionID", "FuncName", "FuncRequeue", "FuncParams"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "ExclusionID" };

	public static final String TABLE_NAME = "PAOExclusion";

	private static final String ALL_EXCL_SQL = 
			"SELECT ExclusionID, PaoID, ExcludedPaoID, PointID, Value, FunctionID," +
			"FuncName, FuncRequeue, FuncParams " +
			"FROM " + TABLE_NAME + 
			" WHERE PaoID= ?";


	/**
	 * PAOExclusion constructor comment.
	 */
	public PAOExclusion() {
		super();
	}

	/**
	 * PAOExclusion constructor comment.
	 */
	public PAOExclusion( Integer paoID_, Integer excludedPaoID_, Integer pointID_,
			Integer value_, Integer functionID_, String funcName_, Integer funcRequeue_, String funcParams_ )
	{
		this();
		
		setPaoID( paoID_ );
		setExcludedPaoID( excludedPaoID_ );
		setPointID( pointID_ );
		setValue( value_ );
		setFunctionID( functionID_ );
		setFuncName( funcName_ );
		setFuncRequeue( funcRequeue_ );
		setFuncParams( funcParams_);
	}
	

	/**
	 * PAOExclusion constructor comment.
	 */
	public PAOExclusion( Integer paoID_, Integer excludedPaoID_, Integer funcRequeue_ )
	{
		this();
		
		setPaoID( paoID_ );
		setExcludedPaoID( excludedPaoID_ );
		setFuncRequeue( funcRequeue_ );
	}
	
	/**
	 * add method comment.
	 */
	public void add() throws java.sql.SQLException 
	{
		if( getExclusionID() == null )
			setExclusionID( getNextPAOExclusionID(getDbConnection()) );

		Object addValues[] = 
		{
			getExclusionID(), getPaoID(),
			getExcludedPaoID(), getPointID(), getValue(), 
			getFunctionID(), getFuncName(), getFuncRequeue(),
			getFuncParams()
		};
	
		add( TABLE_NAME, addValues );
	}
	
	
	/**
	 * delete method comment.
	 */
	public void delete() throws java.sql.SQLException 
	{
		Object values[] = { getExclusionID() };
	
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
	}
	

	/**
	 * This method was created in VisualAge.
	 * @return PAOExclusion[]
	 * @param Integer, Connection
	 */
	public static final PAOExclusion[] getAllPAOExclusions(int paoID, java.sql.Connection conn) throws java.sql.SQLException
	{
		java.util.ArrayList tmpList = new java.util.ArrayList(30);
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		try
		{		
			if( conn == null )
			{
				throw new IllegalStateException("Database connection should not be null.");
			}
			else
			{
				pstmt = conn.prepareStatement( ALL_EXCL_SQL );
				pstmt.setInt( 1, paoID );
				
				rset = pstmt.executeQuery();							
		
				while( rset.next() )
				{
					PAOExclusion item = new PAOExclusion();
	
					item.setExclusionID( new Integer(rset.getInt(1)) );
					item.setPaoID( new Integer(rset.getInt(2)) );
					item.setExcludedPaoID( new Integer(rset.getInt(3)) );
					item.setPointID( new Integer(rset.getInt(4)) );
					item.setValue( new Integer(rset.getInt(5)) );
					item.setFunctionID( new Integer(rset.getInt(6)) );
					item.setFuncName( rset.getString(7) );
					item.setFuncRequeue( new Integer(rset.getInt(8)) );
					item.setFuncParams( new String(rset.getString(9)) );

					tmpList.add( item );
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
				if( rset != null ) rset.close();
			} 
			catch( java.sql.SQLException e2 )
			{
				com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
			}	
		}
	
	
		PAOExclusion retVal[] = new PAOExclusion[ tmpList.size() ];
		tmpList.toArray( retVal );
		
		return retVal;
	}



	/**
	 * This method was created by Cannon Technologies Inc.
	 * @return boolean
	 * @param deviceID java.lang.Integer
	 */
	public static synchronized boolean deleteAllPAOExclusions(int paoID, java.sql.Connection conn )
	{
		try
		{
			if( conn == null )
				throw new IllegalStateException("Database connection should not be null.");
	
			java.sql.Statement stat = conn.createStatement();
			
			stat.execute( 
					"DELETE FROM " + TABLE_NAME + 
					" WHERE PAOid=" + paoID );
	
			if( stat != null )
				stat.close();
		}
		catch(Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			return false;
		}
	
	
		return true;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public final static Integer getNextPAOExclusionID( java.sql.Connection conn )
	{
		if( conn == null )
			throw new IllegalStateException("Database connection should not be null.");
		
			
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
			
		try 
		{		
			 stmt = conn.createStatement();
			 rset = stmt.executeQuery( "SELECT Max(ExclusionID)+1 FROM " + TABLE_NAME );	
					
			 //get the first returned result
			 rset.next();
			 return new Integer( rset.getInt(1) );
		}
		catch (java.sql.SQLException e) 
		{
			 e.printStackTrace();
		}
		finally 
		{
			 try 
			 {
				if ( stmt != null) stmt.close();
			 }
			 catch (java.sql.SQLException e2) 
			 {
				e2.printStackTrace();
			 }
		}
			
		//strange, should not get here
		return new Integer(CtiUtilities.NONE_ID);
	}
	
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public final static int[] getNextPAOExclusionID( java.sql.Connection conn, int idCount )
	{
		if( idCount <= 0 )
			idCount = 1;

		int id = getNextPAOExclusionID( conn ).intValue();
		int[] allIDs = new int[ idCount ];
		
		for( int i = 0; i < idCount; i++ )
			allIDs[i] = id++;
		
		
		return allIDs;
	}


	/**
	 * retrieve method comment.
	 */
	public void retrieve() throws java.sql.SQLException 
	{
		Object constraintValues[] = { getExclusionID() };
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length )
		{
			setPaoID( (Integer) results[0] );
			setExcludedPaoID( (Integer) results[1] );
			setPointID( (Integer) results[2] );
			setValue( (Integer) results[3] );
			setFunctionID( (Integer)results[4] );
			setFuncName( (String)results[5] );
			setFuncRequeue( (Integer)results[6] );
			setFuncParams( (String)results[7] );
		}
		else
			throw new Error(getClass() + " - Incorrect Number of results retrieved");
	
	}
	
	
	/**
	 * update method comment.
	 */
	public void update() throws java.sql.SQLException 
	{
		Object setValues[] =
		{
			getPaoID(),
			getExcludedPaoID(), getPointID(), getValue(), 
			getFunctionID(), getFuncName(), getFuncRequeue(),
			getFuncParams()
		};
						
		Object constraintValues[] = { getExclusionID() };
	
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
	/**
	 * @return
	 */
	public Integer getExcludedPaoID()
	{
		return excludedPaoID;
	}

	/**
	 * @return
	 */
	public Integer getExclusionID()
	{
		return exclusionID;
	}

	/**
	 * @return
	 */
	public String getFuncName()
	{
		return funcName;
	}

	/**
	 * @return
	 */
	public Integer getFuncRequeue()
	{
		return funcRequeue;
	}

	public String getFuncParams()
	{
		return funcParams;
	}
	/**
	 * @return
	 */
	public Integer getFunctionID()
	{
		return functionID;
	}

	/**
	 * @return
	 */
	public Integer getPaoID()
	{
		return paoID;
	}

	/**
	 * @return
	 */
	public Integer getPointID()
	{
		return pointID;
	}

	/**
	 * @return
	 */
	public Integer getValue()
	{
		return value;
	}

	/**
	 * @param integer
	 */
	public void setExcludedPaoID(Integer integer)
	{
		excludedPaoID = integer;
	}

	/**
	 * @param integer
	 */
	public void setExclusionID(Integer integer)
	{
		exclusionID = integer;
	}

	/**
	 * @param string
	 */
	public void setFuncName(String string)
	{
		funcName = string;
	}

	/**
	 * @param integer
	 */
	public void setFuncRequeue(Integer integer)
	{
		funcRequeue = integer;
	}

	public void setFuncParams(String string)
	{
		funcParams = string;
	}
	/**
	 * @param integer
	 */
	public void setFunctionID(Integer integer)
	{
		functionID = integer;
	}

	/**
	 * @param integer
	 */
	public void setPaoID(Integer integer)
	{
		paoID = integer;
	}

	/**
	 * @param integer
	 */
	public void setPointID(Integer integer)
	{
		pointID = integer;
	}

	/**
	 * @param integer
	 */
	public void setValue(Integer integer)
	{
		value = integer;
	}

}