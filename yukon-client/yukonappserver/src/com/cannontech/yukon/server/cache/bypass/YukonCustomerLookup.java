/*
 * Created on Nov 15, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yukon.server.cache.bypass;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.db.customer.Customer;
import com.cannontech.database.db.user.YukonRole;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class YukonCustomerLookup 
{
	/**
	 * Constructor for YukonUserFuncs.
	 */
	public YukonCustomerLookup()
	{
		super();
	}
   
    /*
     * Grab a customer straight from the DB using the contactID 
     */
	public static LiteCustomer loadSpecificCustomerByContactID(int contactID)
	{
		com.cannontech.database.SqlStatement stmt =
			new com.cannontech.database.SqlStatement("SELECT CUSTOMERID FROM " +
                                                       Customer.TABLE_NAME + " WHERE PRIMARYCONTACTID = " + contactID, "yukon");
		LiteCustomer theCustomer = null;
		
		try
		{
			stmt.execute();
			
			//it found one
			if( stmt.getRowCount() > 0 )
			{
			    theCustomer = new LiteCustomer(((java.math.BigDecimal) stmt.getRow(0)[0]).intValue());
			    theCustomer.retrieve(CtiUtilities.YUKONDBALIAS);
            }
		}
		catch( Exception e )
		{
			com.cannontech.clientutils.CTILogger.error( "Error retrieving customer for contactID " + contactID + ": " + e.getMessage(), e );
		}
          
        return theCustomer;
    }

    /*
     * Grab a contact straight from the DB using the ContactID 
     */
    public static LiteCustomer loadSpecificCustomer(int customerID)
    {
        LiteCustomer theCustomer = null;
        
        try
        {
            theCustomer = new LiteCustomer(customerID);
            theCustomer.retrieve(CtiUtilities.YUKONDBALIAS);
        }
        catch( Exception e )
        {
            com.cannontech.clientutils.CTILogger.error( "Error retrieving customer with ID: "+ customerID + "  " + e.getMessage(), e );
        }
          
        return theCustomer;
    }
    
}
