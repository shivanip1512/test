/*
 * Created on Nov 15, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yukon.server.cache.bypass;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.db.customer.Customer;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class YukonCustomerLookup 
{
	/**
	 * Constructor for DaoFactory.getYukonUserDao().
	 */
	public YukonCustomerLookup()
	{
		super();
	}
   
    /*
     * Grab a customer straight from the DB using the primaryContactId 
     */
	public static LiteCustomer loadSpecificCustomerByPrimaryContactID(int primaryContactId)
	{
		com.cannontech.database.SqlStatement stmt =
			new com.cannontech.database.SqlStatement("SELECT CUSTOMERID, CUSTOMERTYPEID FROM " +
                                                       Customer.TABLE_NAME + " WHERE PRIMARYCONTACTID = " + primaryContactId, "yukon");
		LiteCustomer theCustomer = null;
		
		try
		{
			stmt.execute();
			
			//it found one
			if( stmt.getRowCount() > 0 )
			{
			    int customerID = ((java.math.BigDecimal) stmt.getRow(0)[0]).intValue();
                int customerType = ((java.math.BigDecimal) stmt.getRow(0)[1]).intValue();
                theCustomer = LiteFactory.createCICustomerOrLiteCustomer(CtiUtilities.getDatabaseAlias(), customerID, customerType);
            }
		}
		catch( Exception e )
		{
			com.cannontech.clientutils.CTILogger.error( "Error retrieving customer for primaryContactId " + primaryContactId + ": " + e.getMessage(), e );
		}
          
        return theCustomer;
    }

    /*
     * Grab a contact straight from the DB using the CustomerID 
     */
/*    public static LiteCustomer loadSpecificCustomer(int customerID)
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
                theCustomer = LiteFactory.createCICustomerOrLiteCustomer(CtiUtilities.getDatabaseAlias(), customerID, customerType);
            }
        }
        catch( Exception e )
        {
            com.cannontech.clientutils.CTILogger.error( "Error retrieving customer for customerID " + customerID + ": " + e.getMessage(), e );
        }
          
        return theCustomer;
    }
*/    
    public static LiteCustomer loadSpecificCustomer(int customerID)
    {
        LiteCustomer liteCustomer = new LiteCustomer(customerID);
        liteCustomer.retrieve(CtiUtilities.getDatabaseAlias());
        
        if(liteCustomer.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI)
        {   //retrieve the CICustomerBase object instead.
            liteCustomer = new LiteCICustomer(customerID);
            liteCustomer.retrieve(CtiUtilities.getDatabaseAlias());
        }
          
        return liteCustomer;
    }
    
}
