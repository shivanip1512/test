package com.cannontech.integration.crs;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.LogWriter;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.stars.report.ServiceCompany;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.database.db.customer.Address;
import com.cannontech.database.db.customer.Customer;
import com.cannontech.database.db.stars.ECToGenericMapping;
import com.cannontech.database.data.stars.customer.AccountSite;
import com.cannontech.database.data.stars.customer.CustomerAccount;
import com.cannontech.database.data.stars.hardware.MeterHardwareBase;
import com.cannontech.database.db.stars.appliance.ApplianceAirConditioner;
import com.cannontech.database.db.stars.appliance.ApplianceBase;
import com.cannontech.database.db.stars.appliance.ApplianceWaterHeater;
import com.cannontech.database.db.stars.integration.CRSToSAM_PTJ;
import com.cannontech.database.db.stars.integration.CRSToSAM_PTJAdditionalMeterInstalls;
import com.cannontech.database.db.stars.integration.CRSToSAM_PremiseMeterChange;
import com.cannontech.database.db.stars.integration.FailureCRSToSAM_PTJ;
import com.cannontech.database.db.stars.integration.FailureCRSToSAM_PremMeterChg;
import com.cannontech.database.db.stars.report.ServiceCompanyDesignationCode;


/**
 * This funcs class is for use with the CRS Integration tool that will be run
 * at Xcel as part of the PMSI replacement project.  (12/13/2005)  
 * For reference: STARS is known as SAM (Switch Asset Manager) for the purposes of this project
 */
public class YukonToCRSFuncs 
{
	public static final String PTJ_TYPE_XCEL_INSTALL_STRING = "INSTL";
	public static final String PTJ_TYPE_XCEL_ACTIVATION_STRING = "ACT";
	public static final String PTJ_TYPE_XCEL_DEACTIVATION_STRING = "DEACT";
	public static final String PTJ_TYPE_XCEL_REPAIR_STRING = "RPAIR";
	public static final String PTJ_TYPE_XCEL_REMOVE_STRING = "REMVE";
	public static final String PTJ_TYPE_XCEL_OTHER_STRING = "OTHER";
	/**
	 * This method will bring in the contents of the CRSToSAM_PremiseMeterChange
	 * table in the form of CRSToSAM_PremiseMeterChange (DBPersistent) objects
	 */
	public static ArrayList readCRSToSAM_PremiseMeterChange()
	{
		return CRSToSAM_PremiseMeterChange.getAllCurrentPremiseMeterChangeEntries();
	}
	
    /**
     * This method will bring in the contents of the CRSToSAM_PTJ
     * table in the form of CRSToSAM_PTJ (DBPersistent) objects
     */
    public static ArrayList readCRSToSAM_PTJ()
    {
        return CRSToSAM_PTJ.getAllCurrentPTJEntries();
    }
    
    /**
     * This method will bring in the contents of the FailureCRSToSAM_PremMeterChg
     * table in the form of FailureCRSToSAM_PremMeterChg (DBPersistent) objects
     */
	public static ArrayList readFailureCRSToSAM_PremMeterChg()
	{
		return FailureCRSToSAM_PremMeterChg.getAllCurrentPremiseMeterChangeEntries();
	}

    /**
     * This method will bring in the contents of the FailureCRSToSAM_PTJ
     * table in the form of FailureCRSToSAM_PTJ (DBPersistent) objects
     */
	public static ArrayList readFailureCRSToSAM_PTJ()
	{
		return FailureCRSToSAM_PTJ.getAllCurrentFailurePTJEntries();
	}
	/**
	 * This method is called to log every event; it dutifully logs each entry to an 
	 * external log file location.  The parameter importStatus is a char that indicates
	 * a 'F' for failure, 'S' for success, or 'N' for non-import status event.
	 * It then returns the logger for continued use.
	 */
	public static LogWriter writeToImportLog(LogWriter logger, char importStatus, String event, String sqlEvent, String exception)
	{
		if (logger == null)
		{
			logger = changeLog(logger);
		}
		
		if(importStatus == 'F')
		{
			String output = "Failed import entry -- " +
						event + " -- " + sqlEvent 
						+ " -- " + exception; 
			logger.log( output, LogWriter.ERROR);
		}
		else if(importStatus == 'S')
		{
			String output = "Successful import entry: " +
						event + "----" + sqlEvent; 
			logger.log( output, LogWriter.INFO);
		}
		else
		{
			logger.log( event, LogWriter.INFO);
		}
			
		return logger;
	}
	
	public static LogWriter changeLog(LogWriter logger)
	{
		Date now = new Date();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(now);
		int day = cal.get(GregorianCalendar.DAY_OF_MONTH);
		
		try
		{
			String dataDir = "../log/";
			String opName = "import" + day;
			String filename = dataDir + opName  + ".log";
			java.io.File file = new java.io.File( filename );
				
			//if this log file hasn't been modified today, assume it is a month old and start over.
			if(file.exists() && file.lastModified() < (now.getTime() - 86400000))
			{
				file.delete();
			}
				
			if(! file.exists() || logger == null)
			{
				java.io.FileOutputStream out = new java.io.FileOutputStream(filename, true);
				java.io.PrintWriter writer = new java.io.PrintWriter(out, true);
				logger = new LogWriter(opName, LogWriter.DEBUG, writer);
				logger.log("NEW DAY OF THE MONTH, NEW LOG", LogWriter.INFO );
				logger.log("Initializing " + opName, LogWriter.INFO );
				logger.log("Version: " + "XCEL-3.3B" + ".", LogWriter.INFO );
			}
		}
		catch( java.io.FileNotFoundException e )
		{
			e.printStackTrace();
		}
		
		return logger;
	}
	
	public static Contact getContactFromAccountNumber(String acctNumber)
    {
        SqlStatement stmt = new SqlStatement("SELECT CONT.* " + 
        				" FROM " + Contact.TABLE_NAME + " CONT, " + Customer.TABLE_NAME + " CUST, " + com.cannontech.database.db.stars.customer.CustomerAccount.TABLE_NAME + " CA " +
        				" WHERE CONT.CONTACTID = CUST.PRIMARYCONTACTID " +
        				" AND CUST.CUSTOMERID = CA.CUSTOMERID " +
        				" AND ACCOUNTNUMBER = '" + acctNumber + "'", CtiUtilities.getDatabaseAlias());
        
        try
        {
            stmt.execute();
            
            if( stmt.getRowCount() > 0 )
            {
                if(stmt.getRowCount() > 1)
                    throw new Exception("More than one value retrieved.  Should only be one.");
                    
                Contact firstContact = new Contact();
                firstContact.setContactID(new Integer(stmt.getRow(0)[0].toString()));
                firstContact.setContFirstName(stmt.getRow(0)[1].toString());
                firstContact.setContLastName(stmt.getRow(0)[2].toString());
                firstContact.setLogInID(new Integer(stmt.getRow(0)[3].toString()));
                firstContact.setAddressID(new Integer(stmt.getRow(0)[4].toString()));
                return firstContact;
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
	/**
	 * Returns CustomerAccount with acctNumber loaded from database.
	 * @param acctNumber
	 * @return
	 */
	public static CustomerAccount retrieveCustomerAccount(String acctNumber)
    {
        SqlStatement stmt = new SqlStatement("SELECT CA.ACCOUNTID, ACCOUNTSITEID, ACCOUNTNUMBER, CUSTOMERID, BILLINGADDRESSID, ACCOUNTNOTES, ENERGYCOMPANYID " + 
        				" FROM " + com.cannontech.database.db.stars.customer.CustomerAccount.TABLE_NAME + " CA, ECTOACCOUNTMAPPING MAP " +
        				" WHERE CA.ACCOUNTID = MAP.ACCOUNTID " +
        				" AND ACCOUNTNUMBER = '" + acctNumber + "'", CtiUtilities.getDatabaseAlias());
        
        try
        {
            stmt.execute();
            
            if( stmt.getRowCount() > 0 )
            {
                if(stmt.getRowCount() > 1)
                    throw new Exception("More than one value retrieved.  Should only be one.");
                    
                CustomerAccount custAccount = new CustomerAccount();
                custAccount.setAccountID(new Integer(stmt.getRow(0)[0].toString()));
                custAccount.setAccountSiteID(new Integer(stmt.getRow(0)[1].toString()));
                custAccount.getCustomerAccount().setAccountNumber(stmt.getRow(0)[2].toString());
                custAccount.setCustomerID(new Integer(stmt.getRow(0)[3].toString()));
                custAccount.setAddressID(new Integer(stmt.getRow(0)[4].toString()));
                custAccount.getCustomerAccount().setAccountNotes(stmt.getRow(0)[5].toString());
                custAccount.setEnergyCompanyID(new Integer(stmt.getRow(0)[6].toString()));
                
                //Load the other pieces we care about, todate (no need to load the other vectors, we'll worry about that later.
                Transaction.createTransaction(Transaction.RETRIEVE, custAccount.getAccountSite()).execute();
                Transaction.createTransaction(Transaction.RETRIEVE, custAccount.getCustomer().getCustomer()).execute();
                return custAccount;
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        return null;
    }

    public static ContactNotification retrieveContactNotification(Integer contactID, int notifCatID)
    {
        SqlStatement stmt = new SqlStatement("SELECT * FROM " + ContactNotification.TABLE_NAME + " WHERE CONTACTID = " + 
                                             contactID + " AND NOTIFICATIONCATEGORYID = " + notifCatID, CtiUtilities.getDatabaseAlias());
        
        try
        {
            stmt.execute();
            
            if( stmt.getRowCount() > 0 )
            {
                if(stmt.getRowCount() > 1)
                    throw new Exception("More than one value retrieved.  Should only be one.");
                    
                ContactNotification homePhone = new ContactNotification();
                homePhone.setContactNotifID(new Integer(stmt.getRow(0)[0].toString()));
                homePhone.setContactID(new Integer(stmt.getRow(0)[1].toString()));
                homePhone.setNotificationCatID(new Integer(stmt.getRow(0)[2].toString()));
                homePhone.setDisableFlag(stmt.getRow(0)[3].toString());
                homePhone.setNotification(stmt.getRow(0)[4].toString());
                homePhone.setOrdering(new Integer(stmt.getRow(0)[5].toString()));
                return homePhone;
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        
        return null;
    }
    public static ServiceCompany retrieveServiceCompany(String code)
    {
        SqlStatement stmt = new SqlStatement("SELECT COMPANYID, COMPANYNAME, ADDRESSID, MAINPHONENUMBER, MAINFAXNUMBER, PRIMARYCONTACTID, HITYPE, ENERGYCOMPANYID " +
        									" FROM " + com.cannontech.database.db.stars.report.ServiceCompany.TABLE_NAME + " SC, " + 
        									ServiceCompanyDesignationCode.TABLE_NAME + " SCDC, " +
        									ECToGenericMapping.TABLE_NAME + " MAP " +
        									" WHERE SC.COMPANYID = SCDC.SERVICECOMPANYID " +
        									" AND MAP.MAPPINGCATEGORY = '" + com.cannontech.database.db.stars.report.ServiceCompany.TABLE_NAME + "' " +
        									" AND SC.COMPANYID = MAP.ITEMID " + 
        									" AND DESIGNATIONCODEVALUE = '" + code + "'", CtiUtilities.getDatabaseAlias());
        try
        {
            stmt.execute();
            
            if( stmt.getRowCount() > 0 )
            {
                if(stmt.getRowCount() > 1)
                    throw new Exception("More than one value retrieved.  Should only be one.");
                    
                ServiceCompany serviceCompany = new ServiceCompany();
                serviceCompany.setCompanyID(new Integer(stmt.getRow(0)[0].toString()));
                serviceCompany.getServiceCompany().setCompanyName(stmt.getRow(0)[1].toString());
                serviceCompany.setAddressID(new Integer(stmt.getRow(0)[2].toString()));
                serviceCompany.getServiceCompany().setMainPhoneNumber(stmt.getRow(0)[3].toString());
                serviceCompany.getServiceCompany().setMainFaxNumber(stmt.getRow(0)[4].toString());
                serviceCompany.setContactID(new Integer(stmt.getRow(0)[5].toString()));
                serviceCompany.getServiceCompany().setHIType(stmt.getRow(0)[6].toString());
                serviceCompany.setEnergyCompanyID(new Integer(stmt.getRow(0)[7].toString()));
                return serviceCompany;
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static YukonListEntry getEntryByYukonDefID(YukonSelectionList selectionList, int defID)
    {
    	ArrayList listEntries = selectionList.getYukonListEntries();
    	for( int i = 0; i < listEntries.size(); i++)
    	{
    		YukonListEntry listEntry = (YukonListEntry) listEntries.get(i);
    		if( listEntry.getYukonDefID() == defID)
    			return listEntry;
    	}
    	return null;
    }

    public static Customer updateCustomer(Customer customer, String debtorNumber)
    {
    	//TODO add support for bad entry
        boolean isChanged = false;

        if(debtorNumber.length() > 0 && !debtorNumber.equalsIgnoreCase(customer.getAltTrackingNumber()))
        {
            customer.setAltTrackingNumber(debtorNumber);
            isChanged = true;
        }

        if( isChanged)
    	{
	    	try {
	    		Transaction t = Transaction.createTransaction(Transaction.UPDATE, customer);
	    		customer = (Customer)t.execute();
			} catch (TransactionException e) {
				e.printStackTrace();
			}
    	}
    	return customer;
    }

    public static Contact updateContact(Contact contact, String newFirstName, String newLastName)
    {
    	//TODO add support for bad entry
        boolean isChanged = false;

        if(newLastName.length() > 0 && !newLastName.equalsIgnoreCase(contact.getContLastName()))
        {
            contact.setContLastName(newLastName);
            isChanged = true;
        }
        if(newFirstName.length() > 0 && !newFirstName.equalsIgnoreCase(contact.getContFirstName()))
        {
            contact.setContFirstName(newFirstName);
            isChanged = true;
        }
    	if( isChanged)
    	{
	    	try {
	    		Transaction t = Transaction.createTransaction(Transaction.UPDATE, contact);
	    		contact = (Contact)t.execute();
			} catch (TransactionException e) {
				e.printStackTrace();
			}
    	}
    	return contact;
    }
    
    public static void updateContactNotification(Integer contactID, int notifCatID, String newValue)
    {
    	//TODO add support for bad entry
        ContactNotification contNotif = YukonToCRSFuncs.retrieveContactNotification(contactID, notifCatID);
        if( contNotif != null)
        {
            if(newValue.length() > 0 && !newValue.equalsIgnoreCase(contNotif.getNotification()))
            {
            	try{
    				contNotif.setNotification(newValue);
					Transaction.createTransaction(Transaction.UPDATE, contNotif).execute();
        		} catch (TransactionException e) {
        			e.printStackTrace();
        		}
            }
        }
        else
        {
        	//TODO create new contact notification
        }
    }
    
    public static void createMeterHardwares(Integer accountID, Integer energyCompanyID, String meterNumber, ArrayList additionalMeters) throws TransactionException
    {
    	if( meterNumber != null && meterNumber.length() > 0)
    	{
    		MeterHardwareBase meterHardwareBase = MeterHardwareBase.retrieveMeterHardwareBase(accountID.intValue(), meterNumber, energyCompanyID.intValue());
    		updateMeterHardware(meterHardwareBase, accountID, meterNumber);
    	}
    	for (int i = 0; i < additionalMeters.size(); i++)
    	{
    		CRSToSAM_PTJAdditionalMeterInstalls additionalMeter = (CRSToSAM_PTJAdditionalMeterInstalls)additionalMeters.get(i);
    		MeterHardwareBase meterHardwareBase = MeterHardwareBase.retrieveMeterHardwareBase(accountID.intValue(), additionalMeter.getMeterNumber(), energyCompanyID.intValue());
    		updateMeterHardware(meterHardwareBase, accountID, meterNumber);
    	}
	}
    /**
     * If meterHardwareBase is null, a new MeterHardwareBase object is Inserted into db, else the existing object is Updated in db. 
     * @param meterHardwareBase
     * @param accountID
     * @param meterNumber
     * @return
     * @throws TransactionException
     */
    public static MeterHardwareBase updateMeterHardware( MeterHardwareBase meterHardwareBase, Integer accountID, String meterNumber) throws TransactionException
    {
    	if(meterHardwareBase == null)
		{	//MeterNumber inventory does not exist yet, add it.
			meterHardwareBase = new MeterHardwareBase();
			meterHardwareBase.setAccountID(accountID);
			meterHardwareBase.getMeterHardwareBase().setMeterNumber(meterNumber);
//			meterHardwareBase.getMeterHardwareBase().setMeterTypeID();	//TODO ? meterType
			meterHardwareBase.getInventoryBase().setCategoryID(new Integer(CtiUtilities.NONE_ZERO_ID));	//TODO ? correct type
			meterHardwareBase = (MeterHardwareBase)Transaction.createTransaction(Transaction.INSERT, meterHardwareBase).execute();
		}
		else{
			boolean isChanged = false;
			if (meterHardwareBase.getInventoryBase().getAccountID().intValue() != accountID.intValue())
			{	//AccountID chagned!
				meterHardwareBase.setAccountID(accountID);
				isChanged = true;
			}
			if( !meterHardwareBase.getMeterHardwareBase().getMeterNumber().equalsIgnoreCase(meterNumber))
			{
				meterHardwareBase.getMeterHardwareBase().setMeterNumber(meterNumber);
				isChanged = true;
			}
			if( isChanged)
				meterHardwareBase = (MeterHardwareBase)Transaction.createTransaction(Transaction.UPDATE, meterHardwareBase).execute();
		}
		
		return meterHardwareBase;
    }

	public static void createNewAppliances(Integer accountID, Character airCond, Character waterHeater) throws TransactionException {
		if( airCond.charValue() == 'Y')
		{
			ApplianceBase applianceBase = new ApplianceBase();
			applianceBase.setAccountID(accountID);
			ApplianceAirConditioner applianceAirCond = new ApplianceAirConditioner();
			applianceAirCond.setApplianceID(applianceBase.getApplianceID());
			Transaction.createTransaction(Transaction.INSERT, applianceBase).execute();
			applianceAirCond.setApplianceID(applianceBase.getApplianceID());
			Transaction.createTransaction(Transaction.INSERT, applianceAirCond).execute();
		}
		if (waterHeater.charValue() == 'Y')
		{
			ApplianceBase applianceBase = new ApplianceBase();
			applianceBase.setAccountID(accountID);
			ApplianceWaterHeater applianceWaterHeater = new ApplianceWaterHeater();
			applianceWaterHeater.setApplianceID(applianceBase.getApplianceID());
			Transaction.createTransaction(Transaction.INSERT, applianceBase).execute();
			applianceWaterHeater.setApplianceID(applianceBase.getApplianceID());
			Transaction.createTransaction(Transaction.INSERT, applianceWaterHeater).execute();
		}
	}

	public static CustomerAccount createNewCustomerAccount(CustomerAccount customerAccount, String accountNumber, 
    													Integer contactID, String debtorNumber,
    													Character presenceReq, String streetAddress1, String streetAddress2, String cityName, String stateCode, String zipCode,
    													int ecID_workOrder) throws TransactionException
    {
		
    	com.cannontech.database.data.customer.Customer customer = new com.cannontech.database.data.customer.Customer();
		customer.getCustomer().setPrimaryContactID(contactID);
		customer.getCustomer().setCustomerTypeID(new Integer(CustomerTypes.CUSTOMER_RESIDENTIAL));
		customer.getCustomer().setAltTrackingNumber(debtorNumber);
		
		//Create a new customeraccount
		customerAccount = new CustomerAccount();
		customerAccount.getCustomerAccount().setAccountNumber(accountNumber);
		customerAccount.setCustomer(customer);
		customerAccount.getAccountSite().getAccountSite().setCustAtHome(presenceReq.toString());
		customerAccount.getAccountSite().getStreetAddress().setLocationAddress1(streetAddress1);
		customerAccount.getAccountSite().getStreetAddress().setLocationAddress2(streetAddress2);
		customerAccount.getAccountSite().getStreetAddress().setCityName(cityName);
		customerAccount.getAccountSite().getStreetAddress().setStateCode(stateCode);
		customerAccount.getAccountSite().getStreetAddress().setZipCode(zipCode);
		customerAccount.setEnergyCompanyID(new Integer(ecID_workOrder));
		customerAccount = (CustomerAccount)Transaction.createTransaction(Transaction.INSERT, customerAccount).execute();
		return customerAccount;
	}

	public static com.cannontech.database.data.customer.Contact createNewContact(com.cannontech.database.data.customer.Contact contact, String firstName, String lastName, String homePhone, String workPhone, String crsContactPhone) throws TransactionException
    {
		contact.getContact().setContactID(Contact.getNextContactID());
		contact.getContact().setContFirstName(firstName);
		contact.getContact().setContLastName(lastName);
		
		if( homePhone.length() > 0)
		{
			ContactNotification homeNotif = new ContactNotification();
			homeNotif.setContactID(contact.getContact().getContactID());
			homeNotif.setNotificationCatID(new Integer(YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE));
			homeNotif.setNotification(homePhone);
			contact.getContactNotifVect().add(homeNotif);
		}
		if( workPhone.length() > 0)
		{
			ContactNotification workNotif = new ContactNotification();
			workNotif.setContactID(contact.getContact().getContactID());
			workNotif.setNotificationCatID(new Integer(YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE));
			workNotif.setNotification(workPhone);
			contact.getContactNotifVect().add(workNotif);
		}
		if( crsContactPhone.length() > 0)
		{
			ContactNotification crsNotif = new ContactNotification();
			crsNotif.setContactID(contact.getContact().getContactID());
			crsNotif.setNotificationCatID(new Integer(YukonListEntryTypes.YUK_ENTRY_ID_CALL_BACK_PHONE));
			crsNotif.setNotification(crsContactPhone);
			contact.getContactNotifVect().add(crsNotif);
		}
		contact = (com.cannontech.database.data.customer.Contact)Transaction.createTransaction(Transaction.INSERT, contact).execute();
		return contact;
	}

	public static YukonListEntry getServiceTypeEntry(YukonSelectionList selectionList, String entryText)
    {
    	//These codes are Xcel Energy defined, 5 char codes.
    	int lookupDefID =  0;
    	if( entryText.equalsIgnoreCase(PTJ_TYPE_XCEL_INSTALL_STRING))
    		lookupDefID = YukonListEntryTypes.YUK_DEF_ID_SERV_TYPE_INSTALL;
    	else if( entryText.equalsIgnoreCase(PTJ_TYPE_XCEL_ACTIVATION_STRING))
    		lookupDefID = YukonListEntryTypes.YUK_DEF_ID_SERV_TYPE_ACTIVATION;
    	else if( entryText.equalsIgnoreCase(PTJ_TYPE_XCEL_DEACTIVATION_STRING))
    		lookupDefID = YukonListEntryTypes.YUK_DEF_ID_SERV_TYPE_DEACTIVATION;
    	else if( entryText.equalsIgnoreCase(PTJ_TYPE_XCEL_REMOVE_STRING))
    		lookupDefID = YukonListEntryTypes.YUK_DEF_ID_SERV_TYPE_REMOVAL;
    	else if( entryText.equalsIgnoreCase(PTJ_TYPE_XCEL_REPAIR_STRING))
    		lookupDefID = YukonListEntryTypes.YUK_DEF_ID_SERV_TYPE_REPAIR;
    	else if( entryText.equalsIgnoreCase(PTJ_TYPE_XCEL_OTHER_STRING))
    		lookupDefID = YukonListEntryTypes.YUK_DEF_ID_SERV_TYPE_OTHER;
    	
    	ArrayList listEntries = selectionList.getYukonListEntries();
    	for( int i = 0; i < listEntries.size(); i++)
    	{
    		YukonListEntry listEntry = (YukonListEntry) listEntries.get(i);
    		if( listEntry.getYukonDefID() == lookupDefID)
    			return listEntry;
    	}
    	return null;
    }

	public static AccountSite updateAccountSite(AccountSite accountSite, String streetAddress1, String streetAddress2, String cityName, String stateCode, String zipCode, Character presenceReq) {
//		TODO add support for bad entry
    	boolean isChanged = false;

    	if( streetAddress1.length() > 0 && !streetAddress1.equalsIgnoreCase(accountSite.getStreetAddress().getLocationAddress1()))
    	{
    		accountSite.getStreetAddress().setLocationAddress1(streetAddress1);
    		isChanged = true;
    	}
    	if( cityName.length() > 0 && !cityName.equalsIgnoreCase(accountSite.getStreetAddress().getCityName()))
    	{
    		accountSite.getStreetAddress().setCityName(cityName);
    		isChanged = true;
    	}
    	if( stateCode.length() > 0 && !stateCode.equalsIgnoreCase(accountSite.getStreetAddress().getStateCode()))
    	{
    		accountSite.getStreetAddress().setStateCode(stateCode);
    		isChanged = true;
    	}
    	if( zipCode.length() > 0 && !zipCode.equalsIgnoreCase(accountSite.getStreetAddress().getZipCode()))
    	{
    		accountSite.getStreetAddress().setZipCode(zipCode);
    		isChanged = true;
    	}
    	if( presenceReq != null && presenceReq.toString().length() > 0 && !presenceReq.toString().equalsIgnoreCase(accountSite.getAccountSite().getCustAtHome()))
    	{
    		accountSite.getAccountSite().setCustAtHome(presenceReq.toString());
    		isChanged = true;
    	}
    	if( isChanged)
    	{
	    	try {
	    		Transaction t = Transaction.createTransaction(Transaction.UPDATE, accountSite);
	    		accountSite = (AccountSite)t.execute();
			} catch (TransactionException e) {
				e.printStackTrace();
			}
    	}
    	return accountSite;
	}
}
