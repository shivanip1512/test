package com.cannontech.database.data.customer;

/**
 * Insert the type's description here.
 * Creation date: (10/25/2001 11:05:13 AM)
 * @author: 
 */
public interface CustomerTypes
{
	public final static int CUSTOMER_OFFSET			= 0;
   public static final String STRING_INVALID			= "(invalid)";


	public final static int INVALID					= CUSTOMER_OFFSET;
	public final static int CUSTOMER_RESIDENTIAL	= CUSTOMER_OFFSET + 1;
	public final static int CUSTOMER_CI				= CUSTOMER_OFFSET + 2;
   

	public static final String STRING_RES_CUSTOMER = "Residential Customer";
	public static final String STRING_CI_CUSTOMER = "Commercial/Industrial Customer";
	
	
	public static final String[] STRING_ALL_CUSTOMER_TYPES = 
	{
		STRING_INVALID,
		STRING_RES_CUSTOMER,
		STRING_CI_CUSTOMER
	};

}
