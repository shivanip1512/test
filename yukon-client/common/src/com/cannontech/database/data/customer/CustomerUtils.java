package com.cannontech.database.data.customer;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CustomerUtils implements CustomerTypes 
{

	/**
	 * Constructor for CustomerUtils.
	 */
	public CustomerUtils() {
		super();
	}

	/**
	 * This method was created in VisualAge.
	 * @return int
	 * @param typeString java.lang.String
	 */
	public final static int getCustomerType(String typeString)
	{
		if( typeString.equalsIgnoreCase( STRING_RES_CUSTOMER ) )
			return CUSTOMER_RESIDENTIAL;
		else if( typeString.equalsIgnoreCase( STRING_CI_CUSTOMER ) )
			return CUSTOMER_CI;
		else
			return INVALID;
	}
	/**
	 * This method was created in VisualAge.
	 * @return int
	 * @param typeString java.lang.String
	 */
	public final static String getCustomerTypeString(int type)
	{
		switch( type )
		{
			case CUSTOMER_RESIDENTIAL:
				return STRING_RES_CUSTOMER;

			case CUSTOMER_CI:
				return STRING_CI_CUSTOMER;
	
			default:
				return STRING_INVALID;
		}
	
	}


}