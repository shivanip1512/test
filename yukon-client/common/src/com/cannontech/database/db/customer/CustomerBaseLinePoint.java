package com.cannontech.database.db.customer;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class CustomerBaseLinePoint extends com.cannontech.database.db.DBPersistent 
{
	private Integer customerID = null;
	private Integer pointID = null;


	public static final String[] SETTER_COLUMNS = 
	{ 
		"PointID"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "CustomerID" };
	
	public static final String TABLE_NAME = "CustomerBaseLinePoint";

	/**
	 * CustomerBaseLinePoint constructor comment.
	 */
	public CustomerBaseLinePoint() {
		super();
	}
	/**
	 * @exception java.sql.SQLException The exception description.
	 */
	public void add() throws java.sql.SQLException 
	{
		Object[] addValues = 
		{ 
			getCustomerID(), getPointID()
		};
		
		add( TABLE_NAME, addValues );
	}
	/**
	 */
	public void delete() throws java.sql.SQLException 
	{
		delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getCustomerID() );
	}

	/**
	 */
	public void retrieve() throws java.sql.SQLException 
	{
		Object[] constraintValues =  { getCustomerID() };
	
		Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length )
		{
			setPointID( (Integer) results[0] );
		}
		//do not throw the ERROR here if we dont get back any columns!!!!
	
	}

	/**
	 * @exception java.sql.SQLException The exception description.
	 */
	public void update() throws java.sql.SQLException 
	{
		Object[] addValues = 
		{ 
			getCustomerID(), getPointID()
		};
			
		//Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
		//update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );

		delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getCustomerID() );
		add( TABLE_NAME, addValues );		
	}

	/**
	 * @return
	 */
	public Integer getCustomerID()
	{
		return customerID;
	}

	/**
	 * @return
	 */
	public Integer getPointID()
	{
		return pointID;
	}

	/**
	 * @param integer
	 */
	public void setCustomerID(Integer integer)
	{
		customerID = integer;
	}

	/**
	 * @param integer
	 */
	public void setPointID(Integer integer)
	{
		pointID = integer;
	}

}
