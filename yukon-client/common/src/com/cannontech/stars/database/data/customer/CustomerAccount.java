package com.cannontech.stars.database.data.customer;

import java.util.ArrayList;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.customer.Customer;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.data.event.EventAccount;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class CustomerAccount extends DBPersistent {

    private com.cannontech.stars.database.db.customer.CustomerAccount customerAccount = null;
    private com.cannontech.database.db.customer.Address billingAddress = null;

    private com.cannontech.stars.database.data.customer.AccountSite accountSite = null;
    private com.cannontech.database.data.customer.Customer customer = null;
    private Integer energyCompanyID = null;

    private Vector<Integer> applianceVector = null;	// Vector of appliance IDs (Integer)
    private Vector<Integer> inventoryVector = null;	// Vector of inventory IDs (Integer)

    public CustomerAccount() {
        super();
    }

    public void setAccountID(Integer newID) {
        getCustomerAccount().setAccountID(newID);
        getAccountSite().setAccountSiteID(newID);
    }

    public void setAddressID(Integer addressID)
    {
    	getCustomerAccount().setBillingAddressID(addressID);
    	getBillingAddress().setAddressID(addressID);
    }
    
    public void setCustomerID(Integer customerID)
    {
    	getCustomer().setCustomerID(customerID);
    	getCustomerAccount().setCustomerID(customerID);
    }
    
    public void setAccountSiteID(Integer accountSiteID)
    {
    	getCustomerAccount().setAccountSiteID(accountSiteID);
    	getAccountSite().setAccountSiteID(accountSiteID);
    }
    
    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getCustomerAccount().setDbConnection(conn);
        getAccountSite().setDbConnection(conn);
        getBillingAddress().setDbConnection(conn);
        if (getCustomer() != null) getCustomer().setDbConnection(conn);
    }

    @Override
    @SuppressWarnings("cast")
    public void delete() throws java.sql.SQLException {
		for (int i = 0; i < getInventoryVector().size(); i++) {
			Integer invID = getInventoryVector().get(i);
			
			// Don't delete hardware information from the database
			com.cannontech.stars.database.data.hardware.LMHardwareBase.clearLMHardware( invID.intValue() );
			
			com.cannontech.stars.database.db.hardware.InventoryBase invDB =
					new com.cannontech.stars.database.db.hardware.InventoryBase();
			invDB.setInventoryID( invID );
			invDB.setDbConnection( getDbConnection() );
			invDB.retrieve();
			
			invDB.setAccountID( new Integer(com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID) );
			invDB.setRemoveDate( new java.util.Date() );
			invDB.update();
			
			getDbConnection().commit();	// commit it often to avoid deadlock
		}
		
		// Delete program events
        com.cannontech.stars.database.data.event.LMProgramEvent.deleteLMProgramEvents(
				getCustomerAccount().getAccountID().intValue() );
    	
    	// hardware configuration has already been deleted, so we just need to use the DB object here
		com.cannontech.stars.database.data.appliance.ApplianceBase app =
    			new com.cannontech.stars.database.data.appliance.ApplianceBase();
        for (int i = 0; i < getApplianceVector().size(); i++) {
        	Integer appID = getApplianceVector().get(i);
        	app.setApplianceID( appID );
            app.setDbConnection( getDbConnection() );
            app.delete();
            
            getDbConnection().commit();	// commit it often to avoid deadlock
        }
    	
    	// Delete work orders
    	com.cannontech.stars.database.data.report.WorkOrderBase.deleteAllWorkOrders(
    			getCustomerAccount().getAccountID().intValue() );
    	
    	// Delete call reports
    	delete("CallReportBase", "AccountID", getCustomerAccount().getAccountID());
        
        // Delete thermostat schedules
    	AccountThermostatScheduleDao accountThermostatScheduleDao = 
    		YukonSpringHook.getBean("accountThermostatScheduleDao", AccountThermostatScheduleDao.class);
    	accountThermostatScheduleDao.deleteAllByAccountId(getCustomerAccount().getAccountID());
        
		// delete from the mapping table
		delete( "ECToAccountMapping", "AccountID", getCustomerAccount().getAccountID() );
    	
        ArrayList<EventAccount> accountChanges = EventAccount.retrieveEventAccounts(getCustomerAccount().getAccountID());
        for(int i = 0; i < accountChanges.size(); i++)
        {
            try
            {
                Transaction.createTransaction( Transaction.DELETE, accountChanges.get(i) ).execute();
            }
            catch(TransactionException e)
            {
                CTILogger.error( e.getMessage(), e );
            }
        }
        
        getCustomerAccount().delete();
        getAccountSite().delete();
        if(getBillingAddress().getAddressID() != CtiUtilities.NONE_ZERO_ID) {
            getBillingAddress().delete();
        }
		
		// TODO: In the future, a CICustomer may not be deleted when its account is deleted
		if (getCustomer() != null) getCustomer().delete();
        
        setDbConnection(null);
    }

    @Override
    public void add() throws java.sql.SQLException {
    	if (energyCompanyID == null)
    		throw new java.sql.SQLException( "setEnergyCompanyID() must be called before this function" );
    	NextValueHelper nextValueHelper = YukonSpringHook.getBean("nextValueHelper", NextValueHelper.class);
    	
        getBillingAddress().add();
        
        getCustomerAccount().setBillingAddressID( getBillingAddress().getAddressID() );
        int addrID = getBillingAddress().getAddressID().intValue();
        
    	if (getCustomerAccount().getCustomerID().intValue() == 0) {
    		if (getCustomer() instanceof com.cannontech.database.data.customer.CICustomerBase)
    			((com.cannontech.database.data.customer.CICustomerBase)getCustomer()).getAddress().setAddressID( nextValueHelper.getNextValue("Address") );
    		
    		getCustomer().add();
    		getCustomerAccount().setCustomerID( getCustomer().getCustomer().getCustomerID() );
    	}

		if (getCustomerAccount().getAccountSiteID().intValue() == com.cannontech.stars.database.db.customer.AccountSite.NONE_INT) {
			getAccountSite().getStreetAddress().setAddressID( nextValueHelper.getNextValue("Address") );
        	getAccountSite().add();
        	getCustomerAccount().setAccountSiteID( getAccountSite().getAccountSite().getAccountSiteID() );
		}
		
        getCustomerAccount().add();

        // add to the mapping table
        Object[] addValues = {
            getEnergyCompanyID(),
            getCustomerAccount().getAccountID()
        };
        add( "ECToAccountMapping", addValues );

        setDbConnection(null);
    }

    @Override
    public void update() throws java.sql.SQLException {
        getCustomerAccount().update();

		if (getCustomer() != null)
			getCustomer().update();
		
        getAccountSite().update();
        getBillingAddress().update();

        setDbConnection(null);
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        getCustomerAccount().retrieve();

        getCustomer().setCustomerID(getCustomerAccount().getCustomerID());
        getCustomer().retrieve();
        
        getBillingAddress().setAddressID( getCustomerAccount().getBillingAddressID() );
        getBillingAddress().retrieve();

        getAccountSite().setAccountSiteID( getCustomerAccount().getAccountSiteID() );
        getAccountSite().retrieve();
		
		setApplianceVector( com.cannontech.stars.database.db.appliance.ApplianceBase.getApplianceIDs(
				getCustomerAccount().getAccountID(), getDbConnection()) );
		
		setInventoryVector( com.cannontech.stars.database.db.hardware.InventoryBase.getInventoryIDs(
				getCustomerAccount().getAccountID(), getDbConnection()) );


        Object[] results = retrieve( new String[]{"EnergyCompanyID"}, "ECToAccountMapping", new String[]{"AccountID"}, new Object[]{getCustomerAccount().getAccountID()});

        if (results.length == 1)
            setEnergyCompanyID((Integer) results[0] );
        else
            CTILogger.error("No Energy Company Loaded for AccountNumber: " + getCustomerAccount().getAccountNumber() + " (ID:" + getCustomerAccount().getAccountID().toString() + ")");
		
		
		setDbConnection(null);
    }

    public com.cannontech.stars.database.db.customer.CustomerAccount getCustomerAccount() {
        if (customerAccount == null)
            customerAccount = new com.cannontech.stars.database.db.customer.CustomerAccount();
        return customerAccount;
    }

    public void setCustomerAccount(com.cannontech.stars.database.db.customer.CustomerAccount newCustomerAccount) {
        customerAccount = newCustomerAccount;
    }

    public com.cannontech.stars.database.data.customer.AccountSite getAccountSite() {
        if (accountSite == null)
            accountSite = new com.cannontech.stars.database.data.customer.AccountSite();
        return accountSite;
    }

    public void setAccountSite(com.cannontech.stars.database.data.customer.AccountSite newAccountSite) {
        accountSite = newAccountSite;
    }

    public com.cannontech.database.db.customer.Address getBillingAddress() {
        if (billingAddress == null)
            billingAddress = new com.cannontech.database.db.customer.Address();
        return billingAddress;
    }

    public void setBillingAddress(com.cannontech.database.db.customer.Address newBillingAddress) {
        billingAddress = newBillingAddress;
    }

    public com.cannontech.database.data.customer.Customer getCustomer() {
    	if( customer == null)
    		customer = new Customer();
        return customer;
    }

    public void setCustomer(com.cannontech.database.data.customer.Customer newCustomer) {
        customer = newCustomer;
    }

    public Vector<Integer> getApplianceVector() {
        if (applianceVector == null)
            applianceVector = new Vector<Integer>(3);
        return applianceVector;
    }

    public void setApplianceVector(Vector<Integer> newApplianceVector) {
        applianceVector = newApplianceVector;
    }

    public Vector<Integer> getInventoryVector() {
        if (inventoryVector == null)
            inventoryVector = new Vector<Integer>(3);
        return inventoryVector;
    }

    public void setInventoryVector(Vector<Integer> newInventoryVector) {
        inventoryVector = newInventoryVector;
    }

	/**
	 * Returns the energyCompanyID.
	 * @return Integer
	 */
	public Integer getEnergyCompanyID() {
		return energyCompanyID;
	}

	/**
	 * Sets the energyCompanyID.
	 * @param energyCompanyID The energyCompanyID to set
	 */
	public void setEnergyCompanyID(Integer energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
	}

}