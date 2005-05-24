package com.cannontech.database.db.customer;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.device.lm.LMProgramCurtailCustomerList;

/**
 * This type was created in VisualAge.
 */

public class CICustomerBase extends com.cannontech.database.db.DBPersistent 
{
	/* Set attributes to null when a user must enter them*/
	private Integer customerID = null;
	private Integer mainAddressID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Double custDmdLevel = new Double(0.0);
	private String curtailmentAgreement = com.cannontech.common.util.CtiUtilities.STRING_NONE;
	private Double curtailAmount = new Double(0.0);
	private String companyName = null;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"MainAddressID", "CustomerDemandLevel", "CurtailmentAgreement",
		"CurtailAmount", "CompanyName"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "CustomerID" };

	public static final String TABLE_NAME = "CICustomerBase";

	private static final String GET_CUSTOMER_FROM_CONTACT_SQL =
			"SELECT ci.CustomerID, ci.MainAddressID, " + 
			"ci.CustomerDemandLevel, ci.CurtailmentAgreement, " + 
			"ci.CurtailAmount, ci.CompanyName " +
			"FROM " + TABLE_NAME + " ci, Contact cnt, " + 
			Customer.TABLE_NAME + " cst, CustomerAdditionalContact cac " +
			"WHERE ci.CustomerID=cst.CustomerID " +
			"AND cac.CustomerID=cst.CustomerID " +
			"AND cac.ContactID=cnt.ContactID " +
			"AND cnt.ContactID=? " +
			"order by cac.ordering";


	/**
	 * CICustomerBase constructor comment.
	 */
	public CICustomerBase() {
		super();
	}
	/**
	 * add method comment.
	 */
	public void add() throws java.sql.SQLException 
	{
		Object addValues[] = 
		{ 
			getCustomerID(), getMainAddressID(),
			getCustDmdLevel(), getCurtailmentAgreement(),
			getCurtailAmount(), getCompanyName()
		};
	
		add( TABLE_NAME, addValues );
	}
	/**
	 * delete method comment.
	 */
	public void delete() throws java.sql.SQLException 
	{
		Integer values[] = { getCustomerID() };
	
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
	}
	
	
	/**
	 * Insert the method's description here.
	 * Creation date: (10/16/2001 3:29:40 PM)
	 * @return java.lang.Double
	 */
	public java.lang.Double getCurtailAmount() {
		return curtailAmount;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/4/2001 2:27:57 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getCurtailmentAgreement() {
		return curtailmentAgreement;
	}
	
	
	/**
	 * This method was created in VisualAge.
	 * @return CICustomerBase[]
	 * @param stateGroup java.lang.Integer
	 */
	public static final CICustomerBase[] getAllAvailableCustomers(String databaseAlias) throws java.sql.SQLException
	{
		java.util.ArrayList tmpList = new java.util.ArrayList(30);
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
		
	
		//get all the unused customers for Curtailment	
		String sql = "select c.CustomerID, c.CompanyName " +
						 "from " + TABLE_NAME + " c " +
						 "and c.CustomerID not in " +
						 "(select customerid " + 
						 "from " + LMProgramCurtailCustomerList.TABLE_NAME + ")";
	
		try
		{		
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(databaseAlias);
	
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
					CICustomerBase customer = new CICustomerBase();
					
					customer.setCustomerID( new Integer(rset.getInt(1)) );
					customer.setCompanyName( rset.getString(2) );
	
					tmpList.add( customer );
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
				if( conn != null ) conn.close();
			} 
			catch( java.sql.SQLException e2 )
			{
				com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
			}	
		}
	
	
		CICustomerBase retVal[] = new CICustomerBase[ tmpList.size() ];
		tmpList.toArray( retVal );
		
		return retVal;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/99 10:31:33 AM)
	 * @return java.lang.Integer
	 */
	public static synchronized Integer getCICustomerAddressID( Integer customerID, java.sql.Connection conn )
	{
		java.sql.PreparedStatement stat = null;
		java.sql.ResultSet rs = null;
		int value = -1;
	
		try
		{
			stat = conn.prepareStatement(
				 "select MainAddressID from " +
				 TABLE_NAME + " where CustomerID = ?" );
			
			stat.setInt( 1, customerID.intValue() );
			rs = stat.executeQuery();
	
			while( rs.next() )
				value = rs.getInt(1);
	
		}
		catch( Exception e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			try
			{
				if( stat != null )
					stat.close();
				if( rs != null )
					rs.close();
			}
			catch(java.sql.SQLException e )
			{
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			}
		}
	
		return new Integer(value);
	}
	/**
	 * Retrieve a CICustomerBase with a specific contactID.
	 * @return CustomerContact[]
	 * @param stateGroup java.lang.Integer
	 */
	public static final CICustomerBase getCICustomerContact(Integer contactID) throws java.sql.SQLException
	{
	 	return getCICustomerContact(contactID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
	}
	/**
	 * Retrieve a CICustomerBase with a specific contactID.
	 * @return CustomerContact[]
	 * @param stateGroup java.lang.Integer
	 */
	public static final CICustomerBase getCICustomerContact(Integer contactID, String databaseAlias) throws java.sql.SQLException
	{
	 	CICustomerBase retVal = null;
		
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
			
		try
		{		
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(databaseAlias);
	
			if( conn == null )
			{
				throw new IllegalStateException("Error getting database connection.");
			}
			else
			{
				//debug line
				com.cannontech.clientutils.CTILogger.debug(GET_CUSTOMER_FROM_CONTACT_SQL);
				
				pstmt = conn.prepareStatement(GET_CUSTOMER_FROM_CONTACT_SQL);
				pstmt.setInt( 1, contactID.intValue() );
				
				rset = pstmt.executeQuery();							
	
				// Just one please
				if( rset.next() )
				{
					retVal = new CICustomerBase();
	
					retVal.setCustomerID( new Integer(rset.getInt("CustomerID")));				
					retVal.setMainAddressID( new Integer(rset.getInt("MainAddressID")));
					retVal.setCustDmdLevel( new Double( rset.getFloat("CustomerDemandLevel")));
					retVal.setCurtailmentAgreement( rset.getString("CurtailmentAgreement"));
					retVal.setCurtailAmount( new Double( rset.getFloat("CurtailAmount")));
					retVal.setCompanyName( rset.getString("CompanyName"));
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
				if( conn != null ) conn.close();
			} 
			catch( java.sql.SQLException e2 )
			{
				com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
			}	
		}
	
		return retVal;
	}
	
	
	/**
	 * retrieve method comment.
	 */
	public void retrieve() throws java.sql.SQLException 
	{
		Integer constraintValues[] = { getCustomerID() };	
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length )
		{
			setMainAddressID( (Integer) results[0] );
			setCustDmdLevel( (Double)results[1] );
			setCurtailmentAgreement( (String) results[2] );
			setCurtailAmount( (Double)results[3] );
			setCompanyName( (String) results[4] );
		}
		else
			throw new Error(getClass() + " - Incorrect Number of results retrieved");
	
	}
	
	
	/**
	 * Insert the method's description here.
	 * Creation date: (10/16/2001 3:29:40 PM)
	 * @param newCurtailAmount java.lang.Double
	 */
	public void setCurtailAmount(java.lang.Double newCurtailAmount) {
		curtailAmount = newCurtailAmount;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/4/2001 2:27:57 PM)
	 * @param newCurtailmentAgreement java.lang.String
	 */
	public void setCurtailmentAgreement(java.lang.String newCurtailmentAgreement) {
		curtailmentAgreement = newCurtailmentAgreement;
	}
	
	/**
	 * update method comment.
	 */
	public void update() throws java.sql.SQLException 
	{
		Object setValues[] =
		{ 
			getMainAddressID(),
			getCustDmdLevel(), getCurtailmentAgreement(),
			getCurtailAmount(), getCompanyName()
		};
	
		Object constraintValues[] = { getCustomerID() };
	
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * Returns the companyName.
	 * @return String
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * Returns the custDmdLevel.
	 * @return Double
	 */
	public Double getCustDmdLevel() {
		return custDmdLevel;
	}

	/**
	 * Returns the customerID.
	 * @return Integer
	 */
	public Integer getCustomerID() {
		return customerID;
	}

	/**
	 * Returns the mainAddressID.
	 * @return Integer
	 */
	public Integer getMainAddressID() {
		return mainAddressID;
	}

	/**
	 * Sets the companyName.
	 * @param companyName The companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * Sets the custDmdLevel.
	 * @param custDmdLevel The custDmdLevel to set
	 */
	public void setCustDmdLevel(Double custDmdLevel) {
		this.custDmdLevel = custDmdLevel;
	}

	/**
	 * Sets the customerID.
	 * @param customerID The customerID to set
	 */
	public void setCustomerID(Integer customerID) {
		this.customerID = customerID;
	}

	/**
	 * Sets the mainAddressID.
	 * @param mainAddressID The mainAddressID to set
	 */
	public void setMainAddressID(Integer mainAddressID) {
		this.mainAddressID = mainAddressID;
	}

}
