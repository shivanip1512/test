package com.cannontech.integration.crs;

import java.util.ArrayList;
import java.sql.Connection;
import java.util.Date;
import java.text.DateFormat;
import java.util.GregorianCalendar;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.NestedDBPersistent;
import com.cannontech.common.util.LogWriter;
import com.cannontech.database.Transaction;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.database.db.customer.Customer;
import com.cannontech.database.db.stars.CustomerFAQ;
import com.cannontech.database.db.stars.customer.CustomerAccount;
import com.cannontech.database.db.stars.integration.CRSToSAM_PremiseMeterChange;
import com.cannontech.database.db.stars.integration.FailureCRSToSAM_PremMeterChg;


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
        //NOT YET IMPLEMENTED
        return new ArrayList();
        /*
         *Don't forget that we will also want to bring in extra meter entries for each
         *account from the CRSToSAM_PTJAdditionalMeters table, if those entries exist.
         */
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
	
	public static Contact getContactFromPremiseNumber(Integer premNum)
    {
        SqlStatement stmt = new SqlStatement("SELECT * FROM " + Contact.TABLE_NAME + " WHERE CONTACTID IN " +
                "SELECT PRIMARYCONTACTID FROM " + Customer.TABLE_NAME + " WHERE CUSTOMERID IN " + 
                "(SELECT CUSTOMERID FROM " + CustomerAccount.TABLE_NAME + " WHERE ACCOUNTNUMBER = '" + premNum.toString() + "'", CtiUtilities.getDatabaseAlias());
        
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
    
    public static ContactNotification getWorkPhoneFromContactID(Integer contactID)
    {
        SqlStatement stmt = new SqlStatement("SELECT * FROM " + ContactNotification.TABLE_NAME + " WHERE CONTACTID = " + 
                                             contactID + " AND NOTIFICATIONCATEGORYID = " + YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE, CtiUtilities.getDatabaseAlias());
        
        try
        {
            stmt.execute();
            
            if( stmt.getRowCount() > 0 )
            {
                if(stmt.getRowCount() > 1)
                    throw new Exception("More than one value retrieved.  Should only be one.");
                    
                ContactNotification workPhone = new ContactNotification();
                workPhone.setContactNotifID(new Integer(stmt.getRow(0)[0].toString()));
                workPhone.setContactID(new Integer(stmt.getRow(0)[1].toString()));
                workPhone.setNotificationCatID(new Integer(stmt.getRow(0)[2].toString()));
                workPhone.setDisableFlag(stmt.getRow(0)[3].toString());
                workPhone.setNotification(stmt.getRow(0)[4].toString());
                workPhone.setOrdering(new Integer(stmt.getRow(0)[5].toString()));
                return workPhone;
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static ContactNotification getHomePhoneFromContactID(Integer contactID)
    {
        SqlStatement stmt = new SqlStatement("SELECT * FROM " + ContactNotification.TABLE_NAME + " WHERE CONTACTID = " + 
                                             contactID + " AND NOTIFICATIONCATEGORYID = " + YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE, CtiUtilities.getDatabaseAlias());
        
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
}
