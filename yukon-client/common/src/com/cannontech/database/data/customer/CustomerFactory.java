package com.cannontech.database.data.customer;

/**
 * Insert the type's description here.
 * Creation date: (10/25/2001 12:31:55 PM)
 * @author: 
 */

public final class CustomerFactory 
{

	/**
	 * CustomerFactory constructor comment.
	 */
	public CustomerFactory() {
		super();
	}
	/**
	 * This method was created in VisualAge.
	 * @return com.cannontech.database.data.device.DeviceBase
	 * @param custType int
	 */
	public final static Customer createCustomer(int custType) 
	{
		Customer returnCustomer = null;
	
		switch( custType )
		{
			case CustomerTypes.CUSTOMER_RESIDENTIAL:
				returnCustomer = new Customer();
				break;

			case CustomerTypes.CUSTOMER_CI:
				returnCustomer = new CICustomerBase();
				break;
	
		}
	
		return returnCustomer;
	}
	
	
}
