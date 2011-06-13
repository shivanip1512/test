package com.cannontech.database.db.customer;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.spring.YukonSpringHook;

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
	private String altTrackNum = CtiUtilities.STRING_NONE;
    private String temperatureUnit = CtiUtilities.FAHRENHEIT_CHARACTER;



	public static final String SETTER_COLUMNS[] = 
	{ 
		"PrimaryContactID", "CustomerTypeID", "TimeZone", 
        "CustomerNumber", "RateScheduleID", "AltTrackNum",
        "TemperatureUnit"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "CustomerID" };

	public static final String TABLE_NAME = "Customer";


	public Customer() {
		super();
	}

    public void add() throws java.sql.SQLException 
	{
		if (getCustomerID() == null)
			setCustomerID( getNextCustomerID() );
		
		Object addValues[] = 
		{ 
			getCustomerID(), getPrimaryContactID(),
			getCustomerTypeID(), getTimeZone(),
			getCustomerNumber(), getRateScheduleID(),
			getAltTrackingNumber(), getTemperatureUnit()
		};
	
		add( TABLE_NAME, addValues );
	}

	public void delete() throws java.sql.SQLException 
	{
		Integer values[] = { getCustomerID() };
	
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
	}
	
    public static final Integer getNextCustomerID() {
        NextValueHelper nextValueHelper = YukonSpringHook.getNextValueHelper();
        return nextValueHelper.getNextValue("Customer");
    }	
	
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
			setAltTrackingNumber( (String) results[5]);
            setTemperatureUnit( (String) results[6] );
		}
		else
        {
			throw new Error(getClass() + " - Incorrect Number of results retrieved");
        }
	}
	
	public void update() throws java.sql.SQLException 
	{
		Object setValues[] =
		{ 
			getPrimaryContactID(),
			getCustomerTypeID(), getTimeZone(),
			getCustomerNumber(), getRateScheduleID(),
			getAltTrackingNumber(), getTemperatureUnit()
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

	public String getCustomerNumber()
	{
		return customerNumber;
	}
	
	public void setCustomerNumber(String custNum)
	{
		customerNumber = custNum;
	}
	
	public String getAltTrackingNumber()
	{
		return altTrackNum;
	}

	public void setAltTrackingNumber(String altNum)
	{
		altTrackNum = altNum;
	}
	
	public Integer getRateScheduleID()
	{
		return rateScheduleID;
	}

	public void setRateScheduleID(Integer rSched)
	{
		rateScheduleID = rSched;
	}
    
    public String getTemperatureUnit()
    {
        return temperatureUnit;
    }
    
    public void setTemperatureUnit( String temperatureUnit )
    {
        this.temperatureUnit = temperatureUnit;
    }

}
