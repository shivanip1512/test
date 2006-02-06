package com.cannontech.integration.crs;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.LogWriter;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.stars.customer.CustomerAccount;
import com.cannontech.database.data.stars.hardware.MeterHardwareBase;
import com.cannontech.database.data.stars.report.ServiceCompany;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.database.db.customer.Address;
import com.cannontech.database.db.customer.Customer;
import com.cannontech.database.db.stars.appliance.ApplianceAirConditioner;
import com.cannontech.database.db.stars.appliance.ApplianceBase;
import com.cannontech.database.db.stars.appliance.ApplianceWaterHeater;
import com.cannontech.database.db.stars.integration.CRSToSAM_PTJ;
import com.cannontech.database.db.stars.integration.CRSToSAM_PTJAdditionalMeterInstalls;
import com.cannontech.database.db.stars.integration.CRSToSAM_PremiseMeterChange;
import com.cannontech.database.db.stars.integration.FailureCRSToSAM_PTJ;
import com.cannontech.database.db.stars.integration.FailureCRSToSAM_PremMeterChg;
import com.cannontech.database.db.stars.report.WorkOrderBase;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.InventoryUtils;

public final class YukonCRSIntegrator 
{
	private Thread starter = null;
	
	private Thread worker = null;
	
	private com.cannontech.message.dispatch.ClientConnection dispatchConn = null;

	private GregorianCalendar nextImportTime = null;
	private static GregorianCalendar lastImportTime = null;

	public static boolean isService = true;
	private static LogWriter logger = null;
	
	/**
     * 30 minute interval for import attempts
	 */
	public static final int INTEGRATION_INTERVAL = 1800;
	//sleeper thread interval for service
	public static final long SLEEP = 10000;
    
	public YukonCRSIntegrator() 
    {
	    super();
	}

    public void figureNextImportTime()
    {
    	if( this.nextImportTime == null )
    	{
    		this.nextImportTime = new GregorianCalendar();
    	}
    	else
    	{
    		GregorianCalendar tempImp = new GregorianCalendar();
    		long nowInMilliSeconds = tempImp.getTime().getTime();
    		long aggIntInMilliSeconds = INTEGRATION_INTERVAL * 1000;
    		long tempSeconds = (nowInMilliSeconds-(nowInMilliSeconds%aggIntInMilliSeconds))+aggIntInMilliSeconds;
    
    		/**
             *  if it hasn't been at least one full import interval since we last did an
             *	import, wait until next scheduled import time 
             */
    		if( tempSeconds < (this.nextImportTime.getTime().getTime()+aggIntInMilliSeconds) )
    		{
    			tempSeconds += aggIntInMilliSeconds;
    		}
    	
    		this.nextImportTime = new GregorianCalendar();
    		this.nextImportTime.setTime(new java.util.Date(tempSeconds));
    	}
       
    }

    public GregorianCalendar getNextImportTime()
    {
    	return this.nextImportTime;
    }

    public void start()
    {
    	Runnable runner = new Runnable()
    	{
    		public void run()
    		{
    			CTILogger.info("Yukon to CRS Integration Task starting.");
    			logger = YukonToCRSFuncs.writeToImportLog(logger, 'N', "Yukon to CRS Integration Task starting.", "", "");
    			
    			figureNextImportTime();
    			
    			do
    			{
    				logger = YukonToCRSFuncs.changeLog(logger);
    				
    				java.util.Date now = null;
    				now = new java.util.Date();
    				
    				if( getNextImportTime().getTime().compareTo(now) <= 0)
    				{
    					CTILogger.info("Starting import process.");
    					logger = YukonToCRSFuncs.writeToImportLog(logger, 'N', "Starting import process.", "", "");
    					
    					ArrayList premMeterChanges = YukonToCRSFuncs.readCRSToSAM_PremiseMeterChange();
    					ArrayList incomingPTJs = YukonToCRSFuncs.readCRSToSAM_PTJ();
                                            
    					/**
                         * if no crs integration entries of any kind, report this and go back to waiting
                         */ 
                        if(premMeterChanges.size() < 1)
    					{
    						CTILogger.info("CRSToSAM_PremiseMeterChange table is empty.  No new premise or meter changes from CRS.");
    						logger = YukonToCRSFuncs.writeToImportLog(logger, 'N', "CRSToSAM_PremiseMeterChange table is empty.  No new premise or meter changes from CRS.", "", "");
    					}
    					else
    					{
                            runCRSToSAM_PremiseMeterChanger(premMeterChanges);
    					}
                        
                        if(incomingPTJs.size() < 1)
                        {
                            CTILogger.info("CRSToSAM_PTJ table is empty.  No new incoming PTJs from CRS.");
                            logger = YukonToCRSFuncs.writeToImportLog(logger, 'N', "CRSToSAM_PTJ table is empty.  No new incoming PTJs from CRS.", "", "");
                        }
                        else
                        {
                            runCRSToSAM_PTJ(incomingPTJs);
                        }
                        
    					figureNextImportTime();
    				}
    				try
    				{
    					Thread.sleep(SLEEP);
    
    				}
    				catch (InterruptedException ie)
    				{
    					CTILogger.info("Exiting the CRS Integrator unexpectedly...sleep failed!!!");
    					logger = YukonToCRSFuncs.writeToImportLog(logger, 'N', "Exiting the CRS Integrator unexpectedly...sleep failed!!!" + ie.toString(), "", "");
    						
    					break;
    				}
    			} while (isService);
    
    			CTILogger.info("Integrator import operations are complete.");
    			logger = YukonToCRSFuncs.writeToImportLog(logger, 'N', "CRS Integration service stopping completely.", "", "");
    			
    			logger.getPrintWriter().close();
    			logger = null;
    
    			//be sure the runner thread is NULL
    			starter = null;		
    		}
    	};
    
    	if( starter == null )
    	{
    		starter = new Thread( runner, "Importer" );
    		starter.start();
    	}
    
    }

    public void runCRSToSAM_PremiseMeterChanger(ArrayList entries)
    {
        CRSToSAM_PremiseMeterChange currentEntry = null;
    	FailureCRSToSAM_PremMeterChg currentFailure = null;
    	
        ArrayList failures = new ArrayList();
    	ArrayList successArrayList = new ArrayList();
    	boolean badEntry = false;
    	int successCounter = 0;
    	Connection conn = null;
        
        for(int j = 0; j < entries.size(); j++)
    	{
    		badEntry = false;
            
            currentEntry = (CRSToSAM_PremiseMeterChange)entries.get(j);
            String accountNumber = currentEntry.getPremiseNumber().toString();
            
            if(accountNumber.length() > 0)
            {
                Contact currentContact = YukonToCRSFuncs.getContactFromAccountNumber(accountNumber);
                ContactNotification workNotify = null;
                ContactNotification homeNotify = null;
                
                String lastName = currentEntry.getLastName();
                String firstName = currentEntry.getFirstName();
                String homePhone = currentEntry.getHomePhone();
                String workPhone = currentEntry.getWorkPhone();
                String streetAddress = currentEntry.getStreetAddress();
                String cityName = currentEntry.getCityName();
                String state = currentEntry.getStateCode();
                String zipCode = currentEntry.getZipCode();
                String customerNumber = currentEntry.getNewDebtorNumber();
                String altTrackingNum = currentEntry.getTransID();
                String oldMeterNumber = currentEntry.getOldMeterNumber();
                String newMeterNumber = currentEntry.getNewMeterNumber();
                
                updateContact(currentContact, firstName, lastName);
/*                if(lastName.length() > 0)
                    currentContact.setContLastName(lastName);
                if(firstName.length() > 0)
                    currentContact.setContFirstName(firstName);
*/
                updateContactNotification(currentContact.getContactID(), YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE, homePhone);
/*                if(homePhone.length() > 0)
                {
                    homeNotify = YukonToCRSFuncs.getHomePhoneFromContactID(currentContact.getContactID());
                    homeNotify.setNotification(homePhone);
                }
*/
                updateContactNotification(currentContact.getContactID(), YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE, workPhone);
/*                if(workPhone.length() > 0)
                {
                    workNotify = YukonToCRSFuncs.getWorkPhoneFromContactID(currentContact.getContactID());
                    workNotify.setNotification(workPhone);
                }
*/
                updateAddress(currentContact.getAddressID(), streetAddress, cityName, state, zipCode);
/*                if(streetAddress.length() > 0 || cityName.length() > 0 || state.length() > 0
                        || zipCode.length() > 0)
                {
                    Address changingAddress = new Address();
                    changingAddress.setAddressID(currentContact.getAddressID());
                    //Transaction.createTransaction( Transaction.RETRIEVE, changingAddress).execute();
                }
*/                
               
                /*ONE TRANSACTION FAILURE SHOULD FAIL THE WHOLE THING
                com.cannontech.database.db.*/
                
            }
            else
            {
                badEntry = true;
            }
        }
    }
    /**
     * Process the PTJ, create Yukon objects, etc.
     * @param entries
     */
    public void runCRSToSAM_PTJ(ArrayList entries)
    {
        CRSToSAM_PTJ currentEntry = null;
    	FailureCRSToSAM_PTJ currentFailure = null;
    	
        ArrayList failures = new ArrayList();
    	ArrayList successArrayList = new ArrayList();
//    	boolean badEntry = false;
    	int successCounter = 0;
    	Connection conn = null;
        
        for(int j = 0; j < entries.size(); j++)
    	{
        	StringBuffer errorMsg = new StringBuffer("");
//    		badEntry = false;
            
            currentEntry = (CRSToSAM_PTJ)entries.get(j);

            String accountNumber = currentEntry.getPremiseNumber().toString();
            if(accountNumber.length() <= 0)
            	errorMsg.append("Invalid accountNumber length ("+ accountNumber.length());
            	
        	Integer ptjID = currentEntry.getPTJID( );
            String debtorNumber = currentEntry.getDebtorNumber( );
            String ptjType = currentEntry.getPTJType( );
            Date timestamp = currentEntry.getTimestamp();
            String consumType = currentEntry.getConsumptionType();
            Character servUtilType = currentEntry.getServUtilityType();
            String notes = currentEntry.getNotes( );
            String streetAddress = currentEntry.getStreetAddress( );  
            String cityName = currentEntry.getCityName( );      
            String stateCode = currentEntry.getStateCode( );
            String zipCode = currentEntry.getZipCode( );
            String firstName = currentEntry.getFirstName( );  
            String lastName = currentEntry.getLastName( );
            String homePhone = currentEntry.getHomePhone( );
            String workPhone = currentEntry.getWorkPhone( );
            String crsContactPhone = currentEntry.getCRSContactPhone( );
            String crsLoggedUser = currentEntry.getCRSLoggedUser( );
            Character presenceReq = currentEntry.getPresenceRequired( );
            Character airCond = currentEntry.getAirConditioner( );
            Character waterHeater = currentEntry.getWaterHeater( );
//              String serviceNumber = currentEntry.getServiceNumber( );	//not applicable yet
            //TODO Meter Number support on install
            String meterNumber = currentEntry.getMeterNumber( );

            if( !(consumType.equalsIgnoreCase("CO") || consumType.equalsIgnoreCase("DO") || 
            	consumType.equalsIgnoreCase("IN") || consumType.equalsIgnoreCase("CMP") ||
            	consumType.equalsIgnoreCase("MNC") || consumType.equalsIgnoreCase("MFG") ) ){
                	errorMsg.append("Invalid ConsumptionType found: " + consumType + "; ");
            }
                		
            if( servUtilType.charValue() != 'E')
            	errorMsg.append("Invalid ServiceUtilityType found: " + servUtilType+ "; ");
                
            int ecID_customer = EnergyCompany.DEFAULT_ENERGY_COMPANY_ID;
        	int ecID_workOrder = EnergyCompany.DEFAULT_ENERGY_COMPANY_ID;            	

        	//Get the serviceCompany from the zipcode
        	ServiceCompany serviceCompany = YukonToCRSFuncs.retrieveServiceCompany(currentEntry.getZipCode());
        	if( serviceCompany == null)
        		errorMsg.append("No serviceCompany found for zipcode " + currentEntry.getZipCode() + "; ");
        	else
        		ecID_workOrder = serviceCompany.getEnergyCompanyID().intValue();
       	
        	//Get the customer account from accountNumber
        	CustomerAccount customerAccount = YukonToCRSFuncs.retrieveCustomerAccount(accountNumber);
        	if( customerAccount == null)
        	{
        		if( ptjType.equalsIgnoreCase("INSTL") || ptjType.equalsIgnoreCase("OTHER"))	//Xcel Energy 5 char code for install or other ptjtype
            	{
        			try{
        				//Create new Contact data object and ContactNotification objects
	        			com.cannontech.database.data.customer.Contact contact = new com.cannontech.database.data.customer.Contact();
	        			contact = createNewContact(contact, firstName, lastName, homePhone, workPhone, crsContactPhone);
	        			
	        			//Create a new CustomerAccount data object
	        			customerAccount = new CustomerAccount();
	        			customerAccount = createNewCustomerAccount(customerAccount, accountNumber, contact.getContact().getContactID(), debtorNumber, 
	        														presenceReq, streetAddress, cityName, stateCode, zipCode, ecID_workOrder);
	        			//Create new ApplianceBase (and extension of) objects
	        			createNewAppliances(customerAccount.getCustomerAccount().getAccountID(), airCond, waterHeater);
	        			
	        			createMeterHardware (customerAccount.getCustomerAccount().getAccountID(), meterNumber, currentEntry.getAdditionalMeters());
	            		
					} catch (TransactionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
        		else
        			errorMsg.append("No CustomerAccount found for account " + accountNumber + "; ");
        	}
        	else	//CustomerAccount already exists....lets update it!
        	{
        		try
                {
                    Customer customerDB = customerAccount.getCustomer().getCustomer();
                    Contact contactDB = new Contact();
                    contactDB.setContactID(customerDB.getPrimaryContactID());
    				contactDB = (Contact)Transaction.createTransaction(Transaction.RETRIEVE, contactDB).execute();
    	            contactDB = updateContact(contactDB, firstName, lastName);
    	            
    	            updateAddress(customerAccount.getBillingAddress().getAddressID(), streetAddress, cityName, stateCode, zipCode);
    	            updateContactNotification(contactDB.getContactID(), YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE, homePhone);
    	            updateContactNotification(contactDB.getContactID(), YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE, workPhone);
    	            updateContactNotification(contactDB.getContactID(), YukonListEntryTypes.YUK_ENTRY_ID_CALL_BACK_PHONE, crsContactPhone);
    	            updateCustomer(customerDB, debtorNumber);
    				
    			} catch (TransactionException e1) {
    				errorMsg.append("Updating of Contact, Address, or ContactNotification(s) failed; ");
    				//TODO handle error message
    				e1.printStackTrace();
    			}
        	}

        	if( customerAccount != null)//we should have found an existing one or loaded a new one by now.
        	{
        		ecID_customer = customerAccount.getEnergyCompanyID().intValue();
	        	if( ecID_customer != ecID_workOrder)
	        		errorMsg.append("Customer EnergyCompany (" + ecID_customer+") does not match Service Company Energy Company ("+ecID_workOrder+");");
        	}
        	else
        		errorMsg.append("No CustomerAccount found for account " + accountNumber + "; ");
        	
        	
        	if( ptjType.equalsIgnoreCase("ACT"))
        	{
//        		mete
        	}
          	//Get the energyCompany from the zip code
        	LiteStarsEnergyCompany liteStarsEnergyCompany = new LiteStarsEnergyCompany( ecID_workOrder);
        	YukonSelectionList serviceTypeList = liteStarsEnergyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE);
        	YukonListEntry workTypeEntry = getServiceTypeEntry(serviceTypeList, ptjType);
        	if( workTypeEntry == null)
        		errorMsg.append("Invalid PTJType found: " + ptjType + "; ");
        	
        	LiteYukonUser liteYukonUser = YukonUserFuncs.getLiteYukonUser(crsLoggedUser);
        	if( liteYukonUser == null)
        		errorMsg.append("Invalid CRSLoggedUser found: " + crsLoggedUser + "; ");
        	
        	if( errorMsg.length() > 0)//we have error messages to handle, don't go any further!
        	{
        		//TODO Add to failures
        		FailureCRSToSAM_PTJ failureCrsToSam = new FailureCRSToSAM_PTJ(currentEntry);
        		failureCrsToSam.setErrorMsg(errorMsg.toString());
        		failureCrsToSam.setDatetime(new Date());
        		failures.add(failureCrsToSam);
        		break;
        	}

        	
        	YukonSelectionList serviceStatusList = liteStarsEnergyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS);
        	YukonListEntry workStatusEntry = getEntryByYukonDefID(serviceStatusList, YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_ASSIGNED);

            WorkOrderBase workOrderDB = new WorkOrderBase();
            workOrderDB.setOrderID(workOrderDB.getNextOrderID());
            workOrderDB.setOrderNumber(String.valueOf(workOrderDB.getOrderID()));	//orderNumber is the same as orderID, I guess...
            workOrderDB.setWorkTypeID(new Integer(workTypeEntry.getEntryID()));
            workOrderDB.setCurrentStateID(new Integer(workStatusEntry.getEntryID()));
            workOrderDB.setServiceCompanyID(serviceCompany.getServiceCompany().getCompanyID());
            workOrderDB.setOrderedBy(liteYukonUser.getUsername());
            workOrderDB.setDescription(notes);
            workOrderDB.setAccountID(customerAccount.getCustomerAccount().getAccountID());
            workOrderDB.setAdditionalOrderNumber(String.valueOf(ptjID));

            try {
            	com.cannontech.database.data.stars.report.WorkOrderBase workOrder = new com.cannontech.database.data.stars.report.WorkOrderBase();
            	workOrder.setWorkOrderBase(workOrderDB);
            	workOrder.setEnergyCompanyID(new Integer(ecID_workOrder));
				Transaction.createTransaction(Transaction.INSERT, workOrder).execute();
				//Need to have a pending AND an assigned entry, but no need to insert and then update the work order, just create the extra event!
            	workStatusEntry = getEntryByYukonDefID(serviceStatusList, YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_PENDING);
                EventUtils.logSTARSEvent(liteYukonUser.getUserID(), EventUtils.EVENT_CATEGORY_WORKORDER, workStatusEntry.getEntryID(), workOrder.getWorkOrderBase().getOrderID().intValue());
                EventUtils.logSTARSEvent(liteYukonUser.getUserID(), EventUtils.EVENT_CATEGORY_WORKORDER, workOrder.getWorkOrderBase().getCurrentStateID().intValue(), workOrder.getWorkOrderBase().getOrderID().intValue());
			} catch (TransactionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            /*ONE TRANSACTION FAILURE SHOULD FAIL THE WHOLE THING
            com.cannontech.database.db.*/
        }
    }

    private void createMeterHardware(Integer accountID, String meterNumber, ArrayList additionalMeters) throws TransactionException
    {
    	if( meterNumber != null && meterNumber.length() > 0)
    	{
			MeterHardwareBase meterHardwareBase = new MeterHardwareBase();
			meterHardwareBase.setAccountID(accountID);
			meterHardwareBase.getMeterHardwareBase().setMeterNumber(meterNumber);
	//		meterHardwareBase.getMeterHardwareBase().setMeterTypeID();	//TODO ? meterType
			meterHardwareBase.getInventoryBase().setCategoryID(new Integer(CtiUtilities.NONE_ZERO_ID));	//TODO ? correct type
			Transaction.createTransaction(Transaction.INSERT, meterHardwareBase).execute();
    	}
    	for (int i = 0; i < additionalMeters.size(); i++)
    	{
    		CRSToSAM_PTJAdditionalMeterInstalls additionalMeter = (CRSToSAM_PTJAdditionalMeterInstalls)additionalMeters.get(i);
			MeterHardwareBase meterHardwareBase = new MeterHardwareBase();
			meterHardwareBase.setAccountID(accountID);
			meterHardwareBase.getMeterHardwareBase().setMeterNumber(additionalMeter.getMeterNumber());
	//		meterHardwareBase.getMeterHardwareBase().setMeterTypeID();	//TODO ? meterType
			meterHardwareBase.getInventoryBase().setCategoryID(new Integer(CtiUtilities.NONE_ZERO_ID));	//TODO ? correct type
			Transaction.createTransaction(Transaction.INSERT, meterHardwareBase).execute();
    	}
	}

	private void createNewAppliances(Integer accountID, Character airCond, Character waterHeater) throws TransactionException {
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

	private CustomerAccount createNewCustomerAccount(CustomerAccount customerAccount, String accountNumber, 
    													Integer contactID, String debtorNumber,
    													Character presenceReq, String streetAddress, String cityName, String stateCode, String zipCode,
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
		customerAccount.getAccountSite().getStreetAddress().setLocationAddress1(streetAddress);
		customerAccount.getAccountSite().getStreetAddress().setCityName(cityName);
		customerAccount.getAccountSite().getStreetAddress().setStateCode(stateCode);
		customerAccount.getAccountSite().getStreetAddress().setZipCode(zipCode);
		customerAccount.setEnergyCompanyID(new Integer(ecID_workOrder));
		customerAccount = (CustomerAccount)Transaction.createTransaction(Transaction.INSERT, customerAccount).execute();
		return customerAccount;
	}

	private com.cannontech.database.data.customer.Contact createNewContact(com.cannontech.database.data.customer.Contact contact, String firstName, String lastName, String homePhone, String workPhone, String crsContactPhone) throws TransactionException
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

	private YukonListEntry getServiceTypeEntry(YukonSelectionList selectionList, String entryText)
    {
    	//These codes are Xcel Energy defined, 5 char codes.
    	int lookupDefID =  0;
    	if( entryText.equalsIgnoreCase("INSTL"))
    		lookupDefID = YukonListEntryTypes.YUK_DEF_ID_SERV_TYPE_INSTALL;
    	else if( entryText.equalsIgnoreCase("ACT"))
    		lookupDefID = YukonListEntryTypes.YUK_DEF_ID_SERV_TYPE_ACTIVATION;
    	else if( entryText.equalsIgnoreCase("DEACT"))
    		lookupDefID = YukonListEntryTypes.YUK_DEF_ID_SERV_TYPE_DEACTIVATION;
    	else if( entryText.equalsIgnoreCase("REMVE"))
    		lookupDefID = YukonListEntryTypes.YUK_DEF_ID_SERV_TYPE_REMOVAL;
    	else if( entryText.equalsIgnoreCase("RPAIR"))
    		lookupDefID = YukonListEntryTypes.YUK_DEF_ID_SERV_TYPE_REPAIR;
    	else if( entryText.equalsIgnoreCase("OTHER"))
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

    private YukonListEntry getEntryByYukonDefID(YukonSelectionList selectionList, int defID)
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

    private void updateAddress(Integer addressID, String newStreet, String newCity, String newState, String newZipCode)
    {
    	//TODO add support for bad entry
    	boolean isChanged = false;
    	Address address = new Address();
    	address.setAddressID(addressID);

    	try {
    		Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, address);			    
			address = (Address)t.execute();
		} catch (TransactionException e) {
			e.printStackTrace();
		}

    	if( newStreet.length() > 0 && !newStreet.equalsIgnoreCase(address.getLocationAddress1()))
    	{
    		address.setLocationAddress1(newStreet);
    		isChanged = true;
    	}
    	if( newCity.length() > 0 && !newCity.equalsIgnoreCase(address.getCityName()))
    	{
    		address.setCityName(newCity);
    		isChanged = true;
    	}
    	if( newState.length() > 0 && !newState.equalsIgnoreCase(address.getStateCode()))
    	{
    		address.setStateCode(newState);
    		isChanged = true;
    	}
    	if( newZipCode.length() > 0 && !newZipCode.equalsIgnoreCase(address.getZipCode()))
    	{
    		address.setZipCode(newZipCode);
    		isChanged = true;
    	}

    	if( isChanged)
    	{
	    	try {
	    		Transaction t = Transaction.createTransaction(Transaction.UPDATE, address);
	    		address = (Address)t.execute();
			} catch (TransactionException e) {
				e.printStackTrace();
			}
    	}
    }
	
    private Customer updateCustomer(Customer customer, String debtorNumber)
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

    private Contact updateContact(Contact contact, String newFirstName, String newLastName)
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
    
    private void updateContactNotification(Integer contactID, int notifCatID, String newValue)
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
    		/*
    		//validation
    		StringBuffer errorMsg = new StringBuffer("Failed due to: ");
    		badEntry = false;
    		updateDeviceID = null;
    		       
            if(templateName.length() < 1)
            {
                CTILogger.info("Import entry with name " + name + " has no specified 410 template.");
                logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " has no specified 410 template.", "", "");
                badEntry = true;
                errorMsg.append("has no 410 template specified; "); 
            }
            else
            {
                template410 = DBFuncs.get410FromTemplateName(templateName);
                if(template410.getDevice().getDeviceID().intValue() == -12)
                {
                    CTILogger.info("Import entry with name " + name + " specifies a template MCT410 not in the Yukon database.");
                    logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " specifies a template MCT410 not in the Yukon database.", "", "");
                    badEntry = true;
                    errorMsg.append("has an unknown MCT410 template; ");
                }
            }
            
            if(template410 instanceof MCT410CL)
            {
                currentIL = null;
                currentCL = new MCT410CL();
            }
            else
            {
                currentCL = null;
                currentIL = new MCT410IL();
            }
    		if(name.length() < 1 || name.length() > 60)
    		{
    			CTILogger.info("Import entry with address " + address + " has a name with an improper length.");
    			logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with address " + address + " has a name with an improper length.", "", "");
    			badEntry = true;
    			errorMsg.append("improper name length; ");			
    		}
    		else if(name.indexOf('/') != -1 || name.indexOf(',') != -1 || name.indexOf('/') != -1 || name.indexOf('/') != -1)
    		{
    			CTILogger.info("Import entry with address " + address + " has a name that uses invalid characters.");
    			logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with address " + address + " has a name that uses invalid characters.", "", "");
    			badEntry = true;
    			errorMsg.append("invalid name chars; ");			
    		}
    		else
    		{
    			updateDeviceID = DBFuncs.getDeviceIDByAddress(address);
    			if( updateDeviceID != null)
    		   	{
    				CTILogger.info("Address " + address + " is already used by an MCT-410 in the Yukon database.  Attempting to modify device.");
    			   	logger = ImportFuncs.writeToImportLog(logger, 'F', "Address " + address + " is already used by an MCT-410 in the Yukon database.  Attempting to modify device.", "", "");
    		   	}
    			boolean isDuplicate = DBFuncs.IsDuplicateName(name);
    			if(isDuplicate)
    			{
    				CTILogger.info("Name " + name + " is already used by an MCT-410 in the Yukon database.");
    				logger = ImportFuncs.writeToImportLog(logger, 'F', "Name " + name + " is already used by an MCT-410 in the Yukon database.", "", "");
    				badEntry = true;
    				errorMsg.append("is using an existing MCT-410 name; ");
    			}
    		}		
    		if(template410 instanceof MCT410IL && (new Integer(address).intValue() < 1000000 || new Integer(address).intValue() > 2796201))
    		{
    			CTILogger.info("Import entry with name " + name + " has an incorrect MCT410 address.");
    			logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " has an incorrect MCT410 address.", "", "");
    			badEntry = true;
    			errorMsg.append("address out of MCT410IL range; ");	
    		}
            else if(template410 instanceof MCT410CL && (new Integer(address).intValue() < 0 || new Integer(address).intValue() > 2796201))
            {
                CTILogger.info("Import entry with name " + name + " has an incorrect MCT410 address.");
                logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " has an incorrect MCT410 address.", "", "");
                badEntry = true;
                errorMsg.append("address out of MCT410CL range; "); 
            }
    		if(meterNumber.length() < 1)
    		{
    			CTILogger.info("Import entry with name " + name + " has no meter number.");
    			logger = logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " has no meter number.", "", "");
    			badEntry = true;
    			errorMsg.append("has no meter number specified; ");	
    		}
    		if(collectionGrp.length() < 1)
    		{
    			CTILogger.info("Import entry with name " + name + " has no collection group.");
    			logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " has no collection group.", "", "");
    			badEntry = true;
    			errorMsg.append("has no collection group specified; ");	
    		}
    		if(altGrp.length() < 1)
    		{
    			CTILogger.info("Import entry with name " + name + " has no alternate group.");
    			logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " has no alternate group.", "", "");
    			badEntry = true;
    			errorMsg.append("has no alternate group specified; ");	
    		}
    		if(routeName.length() < 1)
    		{
    			CTILogger.info("Import entry with name " + name + " has no specified route.");
    			logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " has no specified route.", "", "");
    			badEntry = true;
    			errorMsg.append("has no route specified; ");	
    		}
    		else
    		{
    			routeID = DBFuncs.getRouteFromName(routeName);
    			if(routeID.intValue() == -12)
    			{
    				CTILogger.info("Import entry with name " + name + " specifies a route not in the Yukon database.");
    				logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " specifies a route not in the Yukon database.", "", "");
    				badEntry = true;
    				errorMsg.append("has an unknown route; ");
    			}
    		}
    		
    		//failure handling
    		if(badEntry)
    		{
    			GregorianCalendar now = new GregorianCalendar();
    			currentFailure = new ImportFail(address, name, routeName, meterNumber, collectionGrp, altGrp, templateName, errorMsg.toString(), now.getTime());
    			failures.addElement(currentFailure);
    		}
    		else if( updateDeviceID != null)
    		{
    			YukonPAObject pao = new YukonPAObject();
    			pao.setPaObjectID(updateDeviceID);
        
    			try
    			{
    				//update the paobject if the name has changed
    				Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, pao);			    
    				pao = (YukonPAObject)t.execute();
    
    				if( !pao.getPaoName().equals(name))
    				{
    					pao.setPaoName(name);
    					t = Transaction.createTransaction(Transaction.UPDATE, pao);
    					pao = (YukonPAObject)t.execute();
    				}
            
    				//update the deviceMeterGroup table if meternumber, collectiongroup or alternate group changed 
    				DeviceMeterGroup dmg = new DeviceMeterGroup();
    				dmg.setDeviceID(updateDeviceID);
    				t = Transaction.createTransaction(Transaction.RETRIEVE, dmg);
    				dmg = (DeviceMeterGroup)t.execute();
            
    				if( !dmg.getMeterNumber().equals(meterNumber) || !dmg.getCollectionGroup().equals(collectionGrp)||
    						!dmg.getTestCollectionGroup().equals(altGrp))
    				{
    					dmg.setMeterNumber(meterNumber);
    					dmg.setCollectionGroup(collectionGrp);
    					dmg.setTestCollectionGroup(altGrp);
    					t = Transaction.createTransaction( Transaction.UPDATE, dmg);
    					dmg = (DeviceMeterGroup)t.execute();
    				}
            
    				//update teh deviceRotues table if hte routeID has changed.
    				DeviceRoutes dr = new DeviceRoutes();
    				dr.setDeviceID(updateDeviceID);
    				t = Transaction.createTransaction(Transaction.RETRIEVE, dr);
    				dr = (DeviceRoutes)t.execute();
    				if( dr.getRouteID().intValue() != routeID.intValue())
    				{
    					dr.setRouteID(routeID);
    					t = Transaction.createTransaction(Transaction.UPDATE, dr);
    					dr = (DeviceRoutes)t.execute();
    				}
            
    			} catch (TransactionException e)
    			{
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
    		//actual 410 creation
    		else
    		{
    			Integer deviceID = DBFuncs.getNextMCTID();
    			GregorianCalendar now = new GregorianCalendar();
    			lastImportTime = now;
    			Integer templateID = template410.getPAObjectID();
    			MCT400SeriesBase current410;
    			if(currentIL != null)
    			{
    				currentIL = (MCT410IL)template410;
    				current410 = currentIL;
    			}
    			else if(currentCL != null)
    			{
    				currentCL = (MCT410CL)template410;
    				current410 = currentCL;
    			}
    			current410 = template410;
    			current410.setPAOName(name);
    			current410.setDeviceID(deviceID);
    			current410.setAddress(new Integer(address));
    			current410.getDeviceMeterGroup().setMeterNumber(meterNumber);
    			current410.getDeviceMeterGroup().setCollectionGroup(collectionGrp);
    			current410.getDeviceMeterGroup().setTestCollectionGroup(altGrp);
    			current410.getDeviceRoutes().setRouteID(routeID);
    			com.cannontech.database.data.multi.MultiDBPersistent objectsToAdd = new com.cannontech.database.data.multi.MultiDBPersistent();
    			objectsToAdd.getDBPersistentArrayList().add(current410);
    			
    			//grab the points we need off the template
    			ArrayList points = DBFuncs.getPointsForPAO(templateID);
    			boolean hasPoints = false;
    			for (int i = 0; i < points.size(); i++)
    			{
    				((com.cannontech.database.data.point.PointBase) points.get(i)).setPointID(new Integer(DBFuncs.getNextPointID() + i));
    				((com.cannontech.database.data.point.PointBase) points.get(i)).getPoint().setPaoID(deviceID);
    				objectsToAdd.getDBPersistentArrayList().add(points.get(i));
    				hasPoints = true;
    			}
    			
    			try
    			{
    				/*
    				 * Do we want to do this every iteration or just use one
    				 * connection for the whole import run??
    				 */
    				/*conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
    				
    				if(hasPoints)
    				{				
    					objectsToAdd.setDbConnection(conn);
    					objectsToAdd.add();
    					
    				}
    				else
    				{
    					current410.setDbConnection(conn);
    					current410.add();
    				}
    				
    				successArrayList.addElement(imps.elementAt(j));
    				logger = ImportFuncs.writeToImportLog(logger, 'S', "MCT-410 " + name + " with address " + address + ".", "", "");
    				synchronized(paoIDsForPorter)
    				{				
    					paoIDsForPorter.addElement(current410.getPAObjectID());
    				}
    				successCounter++;
    			}
    			catch( java.sql.SQLException e )
    			{
    				e.printStackTrace();
    				StringBuffer tempErrorMsg = new StringBuffer(e.toString());
    				currentFailure = new ImportFail(address, name, routeName, meterNumber, collectionGrp, altGrp, templateName, tempErrorMsg.toString(), now.getTime());
    				failures.addElement(currentFailure);
    				logger = ImportFuncs.writeToImportLog(logger, 'F', "MCT410 with name " + name + "failed on INSERT into database.", e.toString(), e.toString());
    			}
    			finally
    			{
    				try
    				{
    					if( conn != null )
    					{
    						conn.commit();
    						conn.close();
    					}
    				}
    				catch( java.sql.SQLException e )
    				{
    					e.printStackTrace();
    				}
    			}
    		}
    	}
    	conn = null;	
    	//remove executed ImportData entries
    	try
    	{
    		conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
    		ImportFuncs.flushImportTable(imps, conn);
    	}
    	catch( java.sql.SQLException e )
    	{
    		e.printStackTrace();
    		logger = ImportFuncs.writeToImportLog(logger, 'F', "PREVIOUSLY USED IMPORT ENTRIES NOT REMOVED: THEY WOULD NOT DELETE!!!", e.toString(), e.toString());
    	}
    	finally
    	{
    		try
    		{
    			if( conn != null )
    			{
    				conn.commit();
    				conn.close();
    			}
    		}
    		catch( java.sql.SQLException e )
    		{
    			e.printStackTrace();
    		}
    	}
    	
    	conn = null;
    	//store failures
    	try
    	{
    		//having trouble with fail adds...want to make sure these work
    		for(int m = 0; m < failures.size(); m++)
    		{
    			((NestedDBPersistent)failures.elementAt(m)).setOpCode(Transaction.INSERT);
    		}		
    		
    		conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
    		ImportFuncs.storeFailures(successArrayList, failures, conn);
    	}
    	catch( java.sql.SQLException e )
    	{
    		e.printStackTrace();
    		logger = ImportFuncs.writeToImportLog(logger, 'F', "FAILURES NOT RECORDED: THEY WOULD NOT INSERT!!!", e.toString(), e.toString());
    	}
    	finally
    	{
    		try
    		{
    			if( conn != null )
    			{
    				conn.commit();
    				conn.close();
    			}
    		}
    		catch( java.sql.SQLException e )
    		{
    			e.printStackTrace();
    		}
    	}
    	
    	//send off a big DBChangeMsg so all Yukon entities know what's goin' on...
    	DBFuncs.generateBulkDBChangeMsg(DBChangeMsg.CHANGE_PAO_DB, "DEVICE", DeviceTypes.STRING_MCT_410IL[1], getDispatchConnection());
        DBFuncs.generateBulkDBChangeMsg(DBChangeMsg.CHANGE_PAO_DB, "DEVICE", DeviceTypes.STRING_MCT_410CL[1], getDispatchConnection());
    	DBFuncs.generateBulkDBChangeMsg(DBChangeMsg.CHANGE_POINT_DB, DBChangeMsg.CAT_POINT, PointTypes.getType(PointTypes.SYSTEM_POINT), getDispatchConnection());
    	
    	DBFuncs.writeTotalSuccess(successCounter);
    	DBFuncs.writeTotalAttempted(imps.size());
    	Date now = new Date();
    	DBFuncs.writeLastImportTime(now);
    	
    	try
    	{
    		getDispatchConnection().disconnect();
    		dispatchConn = null;
    	}
    	catch(java.io.IOException ioe)
    	{
    		logger = ImportFuncs.writeToImportLog(logger, 'N', "Error disconnecting from dispatch: " + ioe.toString(), "", "");
    		CTILogger.info("An exception occured disconnecting from dispatch");
    	}
    	
    }
    
    public void runCRSToSAM_PTJ(ArrayList entries) { }
    
    /** 
     * Stop us
     */
    public void stop()
    {
    	try
    	{
    		Thread t = starter;
    		starter = null;
    		t.interrupt();
    		Thread w = worker;
    		worker = null;
    		w.interrupt();
    	}
    	catch (Exception e)
    	{}
    }
    
    public boolean isRunning()
    {
    	return starter != null;
    }
    
    /**
     * Starts the application.
     */
    public static void main(java.lang.String[] args)
    {
    	ClientSession session = ClientSession.getInstance(); 
    	if(!session.establishSession()){
    		System.exit(-1);			
    	}
    	  	
    	if(session == null) 		
    		System.exit(-1);
    				
    	YukonCRSIntegrator bulkImporter = new YukonCRSIntegrator();
    	bulkImporter.start();	
    }

    public void stopApplication()
    {
    	logger = YukonToCRSFuncs.writeToImportLog(logger, 'N', "Forced stop on import application.", "", "");
    	isService = false;
    
    	//System.exit(0);
    }
    
    private synchronized ClientConnection getDispatchConnection()
    {
    	if( dispatchConn == null || !dispatchConn.isValid() )
    	{
    		String host = RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_MACHINE );
    		String portStr = RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_PORT );
    		int port = 1510;
    		
    		try 
    		{
    			port = Integer.parseInt(portStr);
    		} 
    		catch(NumberFormatException nfe) 
    		{
    			CTILogger.warn("Bad value for DISPATCH-PORT");		
    		}
    		
    		CTILogger.debug("attempting to connect to dispatch @" + host + ":" + port);	
    		dispatchConn = new ClientConnection();
    
    		Registration reg = new Registration();
    		reg.setAppName("Yukon CRS Integrator");
    		reg.setAppIsUnique(0);
    		reg.setAppKnownPort(0);
    		reg.setAppExpirationDelay( 3600 );  // 1 hour should be OK
    
    		dispatchConn.setHost(host);
    		dispatchConn.setPort(port);
    		dispatchConn.setAutoReconnect(true);
    		dispatchConn.setRegistrationMsg(reg);
    		
    		try
    		{
    			dispatchConn.connectWithoutWait();
    		}
    		catch( Exception e )
    		{
    			e.printStackTrace();
    		}
    	}
    	return dispatchConn;
    }

}