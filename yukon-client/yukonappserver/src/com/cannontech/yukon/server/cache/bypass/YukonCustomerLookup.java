/*
 * Created on Nov 15, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yukon.server.cache.bypass;

import java.util.Map;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.*;
import com.cannontech.database.db.customer.CICustomerBase;
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
			new com.cannontech.database.SqlStatement("SELECT CUSTOMERID, CUSTOMERTYPEID FROM " +
                                                       Customer.TABLE_NAME + " WHERE PRIMARYCONTACTID = " + contactID, "yukon");
		LiteCustomer theCustomer = null;
		
		try
		{
			stmt.execute();
			
			//it found one
			if( stmt.getRowCount() > 0 )
			{
			    int customerID = ((java.math.BigDecimal) stmt.getRow(0)[0]).intValue();
                int customerType = ((java.math.BigDecimal) stmt.getRow(0)[0]).intValue();
                theCustomer = LiteFactory.createCICustomerOrLiteCustomer(CtiUtilities.YUKONDBALIAS, customerID, customerType);
            }
		}
		catch( Exception e )
		{
			com.cannontech.clientutils.CTILogger.error( "Error retrieving customer for contactID " + contactID + ": " + e.getMessage(), e );
		}
          
        return theCustomer;
    }

    /*
     * Grab a contact straight from the DB using the CustomerID 
     */
    public static LiteCustomer loadSpecificCustomer(int customerID)
    {
        com.cannontech.database.SqlStatement stmt =
            new com.cannontech.database.SqlStatement("SELECT CUSTOMERTYPEID FROM " +
                                                       Customer.TABLE_NAME + " WHERE CUSTOMERID = " + customerID, "yukon");
        LiteCustomer theCustomer = null;
        
        try
        {
            stmt.execute();
            
            //it found one
            if( stmt.getRowCount() > 0 )
            {
                int customerType = ((java.math.BigDecimal) stmt.getRow(0)[0]).intValue();
                theCustomer = LiteFactory.createCICustomerOrLiteCustomer(CtiUtilities.YUKONDBALIAS, customerID, customerType);
            }
        }
        catch( Exception e )
        {
            com.cannontech.clientutils.CTILogger.error( "Error retrieving customer for customerID " + customerID + ": " + e.getMessage(), e );
        }
          
        return theCustomer;
    }
    
}
