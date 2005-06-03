package com.cannontech.database.db.customer;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;

/**
 * Maps points to a CICustomer
 */
public class CICustomerPointData extends com.cannontech.database.db.DBPersistent 
{
	private Integer customerID = null;
	private Integer pointID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private String type = CtiUtilities.STRING_NONE;
	private String optionalLabel = CtiUtilities.STRING_NONE;


	public static final String TYPE_DEMAND = "Demand";
	public static final String TYPE_SETTLEMENT = "Settlement";
	public static final String TYPE_BASELINE = "Baseline";
	public static final String TYPE_CURTAILABLE = "Curtailable";


	public static final String SETTER_COLUMNS[] = 
	{ 
		"Type", "OptionalLabel"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "CustomerID", "PointID" };

	public static final String TABLE_NAME = "CICustomerPointData";


	/**
	 * Customer constructor comment.
	 */
	public CICustomerPointData() {
		super();
	}
	/**
	 * add method comment.
	 */
	public void add() throws java.sql.SQLException 
	{
		Object addValues[] = 
		{ 
			getCustomerID(), getPointID(),
			getType(), getOptionalLabel()
		};
	
		add( TABLE_NAME, addValues );
	}
	/**
	 * delete method comment.
	 */
	public void delete() throws java.sql.SQLException 
	{
		Integer values[] = { getCustomerID(), getPointID() };
	
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
	}
	
	/**
	 * Returns all the CICustomerPointData rows for the given CICustomerID
	 * 
	 */
	public static final CICustomerPointData[] getAllCICustomerPointData( int customerID )
	{
		java.util.ArrayList tmpList = new java.util.ArrayList(30);
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		//get all the unused customers for Curtailment	
		String sql = "select PointID, Type, OptionalLabel from " + 
						 TABLE_NAME + 
						 " where CustomerID = " + customerID +
						 " order by type";	
		try
		{		
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	
			if( conn == null )
			{
				throw new IllegalStateException("Error getting database connection.");
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				rset = pstmt.executeQuery();							
		
				while( rset.next() )
				{
					CICustomerPointData ciCustPtData = new CICustomerPointData();
					ciCustPtData.setCustomerID( new Integer(customerID) );
					ciCustPtData.setPointID( new Integer(rset.getInt(1)) );
					ciCustPtData.setType( rset.getString(2) );
					ciCustPtData.setOptionalLabel( rset.getString(3) );
	
					tmpList.add( ciCustPtData );
				}
						
			}		
		}
		catch( java.sql.SQLException e )
		{
			CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			try
			{
				if( pstmt != null ) pstmt.close();
				if( conn != null ) conn.close();
			} 
			catch( java.sql.SQLException e2 )
			{
				CTILogger.error( e2.getMessage(), e2 );//something is up
			}	
		}
	
	
		CICustomerPointData retVal[] = new CICustomerPointData[ tmpList.size() ];
		tmpList.toArray( retVal );
		return retVal;
	}
	
	/**
	 * retrieve method comment.
	 */
	public void retrieve() throws java.sql.SQLException 
	{
		Integer constraintValues[] = { getCustomerID(), getPointID() };	
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length )
		{
			setType( (String)results[0] );
			setOptionalLabel( (String)results[1] );
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
			getType(), getOptionalLabel()
		};
	
		Object constraintValues[] = { getCustomerID(), getPointID() };
	
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * Returns the customerID.
	 * @return Integer
	 */
	public Integer getCustomerID() {
		return customerID;
	}

	/**
	 * Sets the customerID.
	 * @param customerID The customerID to set
	 */
	public void setCustomerID(Integer customerID) {
		this.customerID = customerID;
	}

	/**
	 * @return
	 */
	public String getOptionalLabel()
	{
		return optionalLabel;
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
	public String getType()
	{
		return type;
	}

	/**
	 * @param string
	 */
	public void setOptionalLabel(String string)
	{
		optionalLabel = string;
	}

	/**
	 * @param integer
	 */
	public void setPointID(Integer integer)
	{
		pointID = integer;
	}

	/**
	 * @param string
	 */
	public void setType(String string)
	{
		type = string;
	}

}
