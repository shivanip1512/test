package com.cannontech.database.db.customer;

import com.cannontech.common.util.CtiUtilities;

/**
 * This type was created in VisualAge.
 */

public class Customer extends com.cannontech.database.db.DBPersistent 
{
	/* Set attributes to null when a user must enter them*/
	private Integer customerID = null;
	private Integer primaryContactID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Integer customerTypeID = new Integer(CtiUtilities.NONE_ZERO_ID);	
	private String timeZone = "";
	private String customerNumber = CtiUtilities.STRING_NONE;
	private Integer rateScheduleID = new Integer(CtiUtilities.NONE_ZERO_ID);



	public static final String SETTER_COLUMNS[] = 
	{ 
		"PrimaryContactID", "CustomerTypeID", "TimeZone", "CustomerNumber", "RateScheduleID"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "CustomerID" };

	public static final String TABLE_NAME = "Customer";


	/**
	 * Customer constructor comment.
	 */
	public Customer() {
		super();
	}
	/**
	 * add method comment.
	 */
	public void add() throws java.sql.SQLException 
	{
		Object addValues[] = 
		{ 
			getCustomerID(), getPrimaryContactID(),
			getCustomerTypeID(), getTimeZone(),
			getCustomerNumber(), getRateScheduleID()
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
	 * Creation date: (12/14/99 10:31:33 AM)
	 * @return java.lang.Integer
	 */
	public static synchronized Integer getNextCustomerID( java.sql.Connection conn )
	{
		if( conn == null )
			throw new IllegalStateException("Database connection should not be null.");
	
		
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		
		try 
		{		
		    stmt = conn.createStatement();
			 rset = stmt.executeQuery( "SELECT Max(CustomerID)+1 FROM " + TABLE_NAME );	
				
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
		    	if ( rset != null) rset.close();
				if ( stmt != null) stmt.close();
		    }
		    catch (java.sql.SQLException e2) 
		    {
				e2.printStackTrace();
		    }
		}
		
		//strange, should not get here
		return new Integer(CtiUtilities.NONE_ZERO_ID);
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
			setPrimaryContactID( (Integer) results[0] );
			setCustomerTypeID( (Integer) results[1] );
			setTimeZone( (String) results[2] );
			setCustomerNumber( (String) results[3] );
			setRateScheduleID( (Integer) results[4] );
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
			getPrimaryContactID(),
			getCustomerTypeID(), getTimeZone(),
			getCustomerNumber(), getRateScheduleID()
		};
	
		Object constraintValues[] = { getCustomerID() };
	
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
	 * Returns the customerTypeID.
	 * @return Integer
	 */
	public Integer getCustomerTypeID() {
		return customerTypeID;
	}

	/**
	 * Returns the primaryContactID.
	 * @return Integer
	 */
	public Integer getPrimaryContactID() {
		return primaryContactID;
	}

	/**
	 * Returns the timeZone.
	 * @return String
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * Sets the customerTypeID.
	 * @param customerTypeID The customerTypeID to set
	 */
	public void setCustomerTypeID(Integer customerTypeID) {
		this.customerTypeID = customerTypeID;
	}

	/**
	 * Sets the primaryContactID.
	 * @param primaryContactID The primaryContactID to set
	 */
	public void setPrimaryContactID(Integer primaryContactID) {
		this.primaryContactID = primaryContactID;
	}

	/**
	 * Sets the timeZone.
	 * @param timeZone The timeZone to set
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * @return
	 */
	public String getCustomerNumber()
	{
		return customerNumber;
	}

	/**
	 * @param string
	 */
	public void setCustomerNumber(String custNum)
	{
		customerNumber = custNum;
	}
	
	public Integer getRateScheduleID()
	{
		return rateScheduleID;
	}

	/**
	 * @param string
	 */
	public void setRateScheduleID(Integer rSched)
	{
		rateScheduleID = rSched;
	}

}
