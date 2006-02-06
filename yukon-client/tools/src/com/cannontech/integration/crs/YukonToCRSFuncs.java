package com.cannontech.integration.crs;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.LogWriter;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.stars.report.ServiceCompany;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.database.db.customer.Customer;
import com.cannontech.database.db.stars.ECToGenericMapping;
import com.cannontech.database.data.stars.customer.CustomerAccount;
import com.cannontech.database.db.stars.integration.CRSToSAM_PTJ;
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
                Transaction.createTransaction(Transaction.RETRIEVE, custAccount.getBillingAddress());
                Transaction.createTransaction(Transaction.RETRIEVE, custAccount.getCustomer().getCustomer());
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
}
