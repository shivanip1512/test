package com.cannontech.integration.crs;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.LogWriter;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.db.customer.Customer;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.customer.CustomerAccount;
import com.cannontech.stars.database.data.event.EventWorkOrder;
import com.cannontech.stars.database.data.hardware.MeterHardwareBase;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.report.ServiceCompany;
import com.cannontech.stars.database.data.report.WorkOrderBase;
import com.cannontech.stars.database.db.hardware.LMHardwareBase;
import com.cannontech.stars.database.db.integration.CRSToSAM_PTJ;
import com.cannontech.stars.database.db.integration.CRSToSAM_PTJAdditionalMeters;
import com.cannontech.stars.database.db.integration.CRSToSAM_PremiseMeterChange;
import com.cannontech.stars.database.db.integration.SAMToCRS_PTJ;
import com.cannontech.stars.database.db.integration.SwitchReplacement;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.util.SwitchCommandQueue.SwitchCommand;

public final class YukonCRSIntegrator 
{
	private Thread starter = null;
	
	private Thread worker = null;
	
	private GregorianCalendar nextImportTime = null;

	public static boolean isService = true;
	private static LogWriter logger = null;
	
	/**
     * 15 minute interval for import attempts
	 */
	public static final int INTEGRATION_INTERVAL = 900;
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
    					
    					List<CRSToSAM_PremiseMeterChange> premMeterChanges = YukonToCRSFuncs.readCRSToSAM_PremiseMeterChange();
    					List<CRSToSAM_PTJ> incomingPTJs = YukonToCRSFuncs.readCRSToSAM_PTJ();
    					List<SwitchReplacement> switchReplacements = YukonToCRSFuncs.readSwitchReplacement();
                                            
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

                        if(switchReplacements.size() < 1)
                        {
                            CTILogger.info("SwitchReplacement table is empty.  No new incoming SwitchReplacements.");
                            logger = YukonToCRSFuncs.writeToImportLog(logger, 'N', "SwitchReplacment table is empty.  No new incoming SwitchReplacements.", "", "");
                        }
                        else
                        {
                            runSwitchReplacement(switchReplacements);
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

    public void runCRSToSAM_PremiseMeterChanger(List<CRSToSAM_PremiseMeterChange> entries)
    {
    	int successCounter = 0;

    	for (CRSToSAM_PremiseMeterChange currentEntry : entries) {
            StringBuffer errorMsg = new StringBuffer("");

            if(currentEntry.getPremiseNumber() != null && currentEntry.getPremiseNumber().toString().length() > 0)
            {
                String accountNumber = currentEntry.getPremiseNumber().toString();
                CustomerAccount customerAccount = YukonToCRSFuncs.retrieveCustomerAccount(accountNumber);

                String lastName = currentEntry.getLastName();
                String firstName = currentEntry.getFirstName();
                String homePhone = currentEntry.getHomePhone();
                String workPhone = currentEntry.getWorkPhone();
                String streetAddress1 = currentEntry.getStreetAddress1();
                String streetAddress2 = currentEntry.getStreetAddress2();
                String cityName = currentEntry.getCityName();
                String state = currentEntry.getStateCode();
                String zipCode = currentEntry.getZipCode();
                String customerNumber = currentEntry.getNewDebtorNumber();
                String alternateTrackingNumber = currentEntry.getTransID();
                String oldMeterNumber = currentEntry.getOldMeterNumber();
                String newMeterNumber = currentEntry.getNewMeterNumber();
                String siteNumber = currentEntry.getSiteNumber();
                
                if(customerAccount != null)
                {
                    try
                    {
                        Contact contactDB = new Contact();
                        Customer customerDB = customerAccount.getCustomer().getCustomer();
                        contactDB.setContactID(customerDB.getPrimaryContactID());
                        contactDB = Transaction.createTransaction(Transaction.RETRIEVE, contactDB).execute();
                    
                        YukonToCRSFuncs.updateAllContactInfo(contactDB, firstName, lastName, homePhone, workPhone, null);
                        YukonToCRSFuncs.updateAccountSite(customerAccount, streetAddress1, streetAddress2, cityName, state, zipCode, null, siteNumber);
                        YukonToCRSFuncs.updateCustomer(customerDB, customerNumber);
                        
                        if(oldMeterNumber.compareTo(newMeterNumber) != 0)
                        {
                            MeterHardwareBase meterHardwareBase = MeterHardwareBase.retrieveMeterHardwareBase(oldMeterNumber, customerAccount.getEnergyCompanyID().intValue());
                            if( meterHardwareBase == null)
                                errorMsg.append("MeterNumber (" + oldMeterNumber + ") not found for account " + accountNumber + "; ");
                            else
                            {
                                meterHardwareBase.getMeterHardwareBase().setMeterNumber(newMeterNumber);
                                meterHardwareBase.getInventoryBase().setAlternateTrackingNumber(alternateTrackingNumber);
                                Transaction.createTransaction(Transaction.UPDATE, meterHardwareBase).execute();
                            }
                            successCounter++;
                            DBChangeMsg dbChangeMessage = new DBChangeMsg(
                                customerAccount.getCustomerAccount().getAccountID(),
                                DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB,
                                DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
                                DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
                                DbChangeType.UPDATE
                            );
				            dbChangeMessage.setSource("RunCRSToSam_PremiseMeterChange:ForceHandleDBChange");	//TODO verify if StarsDBCache handles
                            YukonToCRSFuncs.handleDBChangeMsg(dbChangeMessage);
                        }
                    }
                    catch (TransactionException e) 
                    {
                        e.printStackTrace();
                        errorMsg.append("One or all database operations failed; ");
                    }
                }
                else
                {
                    errorMsg.append("No CustomerAccount found for account " + accountNumber + "; ");
                }
            }
            else
            {
                errorMsg.append("Has no premise number specified;");
            }
            
            if( errorMsg.length() > 0)
            {
                YukonToCRSFuncs.moveToFailureCRSToSAM_PremMeterChg(currentEntry, errorMsg.toString());
                continue;
            }
            else
            {
                try
                {
                    Transaction.createTransaction(Transaction.DELETE, currentEntry).execute();
                }
                catch (TransactionException e) 
                {
                    e.printStackTrace();
                }
            }
        }
        CTILogger.info("CRSTOYUKON PremiseMeterChange Integration complete:  Imported " + successCounter + " PremiseMeterChanges.");
    }
    /**
     * Process the PTJ, create Yukon objects, etc.
     * @param entries
     */
    public void runCRSToSAM_PTJ(List<CRSToSAM_PTJ> entries)
    {
        int newWorkOrderCount = 0;
        for (CRSToSAM_PTJ currentEntry : entries) {
        	StringBuffer errorMsg = new StringBuffer("");

            String accountNumber = currentEntry.getPremiseNumber().toString();
            if(accountNumber.length() <= 0)
            	errorMsg.append("Invalid accountNumber length ("+ accountNumber.length());
            	
        	Integer ptjID = currentEntry.getPTJID( );
            String debtorNumber = currentEntry.getDebtorNumber( );
            String ptjType = currentEntry.getPTJType( );
            String consumType = currentEntry.getConsumptionType();
            Character servUtilType = currentEntry.getServUtilityType();
            String notes = currentEntry.getNotes( );
            String streetAddress1 = currentEntry.getStreetAddress1( );  
            String streetAddress2 = currentEntry.getStreetAddress2( );
            String cityName = currentEntry.getCityName( );      
            String stateCode = currentEntry.getStateCode( );
            String zipCode = currentEntry.getZipCode();
            String servCompZipCode = zipCode.toString();
            if( servCompZipCode.length() > 5)
            	servCompZipCode = servCompZipCode.substring(0, 5);
            String firstName = currentEntry.getFirstName( );  
            String lastName = currentEntry.getLastName( );
            String homePhone = currentEntry.getHomePhone( );
            String workPhone = currentEntry.getWorkPhone( );
            String crsContactPhone = currentEntry.getCRSContactPhone( );
            String crsLoggedUser = currentEntry.getCRSLoggedUser( );
            Character presenceReq = currentEntry.getPresenceRequired( );
            Character airCond = currentEntry.getAirConditioner( );
            Character waterHeater = currentEntry.getWaterHeater( );

            //TODO Meter Number support on install
            String meterNumber = currentEntry.getMeterNumber( );
            String siteNumber = currentEntry.getSiteNumber();

//            CMP = Company Use
//            CO = Commercial
//            DO = Domestic (Residential)
//            IN = Industrial
//            MFG = Manufacturing
//            MNC = Municipal
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
        	LiteStarsEnergyCompany liteStarsEnergyCompany = null;
        	LiteYukonUser liteYukonUser = null;
        	YukonListEntry workTypeEntry = null;
        	ServiceCompany serviceCompany = YukonToCRSFuncs.retrieveServiceCompany(servCompZipCode);
        	if( serviceCompany == null)
        		errorMsg.append("No serviceCompany found for zipcode " + servCompZipCode + "; ");
        	else
        	{
        		ecID_workOrder = serviceCompany.getEnergyCompanyID().intValue();
        		//Get the energyCompany from the zip code
        		liteStarsEnergyCompany = StarsDatabaseCache.getInstance().getEnergyCompany(ecID_workOrder);
        		YukonSelectionList serviceTypeList = liteStarsEnergyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE);
        		workTypeEntry = YukonToCRSFuncs.getServiceTypeEntry(serviceTypeList, ptjType);
	        	if( workTypeEntry == null)
	        		errorMsg.append("Invalid PTJType found: " + ptjType + "; ");
	        	
	        	liteYukonUser = DaoFactory.getYukonUserDao().findUserByUsername(crsLoggedUser);
	        	if( liteYukonUser == null)
	        		errorMsg.append("Invalid CRSLoggedUser found: " + crsLoggedUser + "; ");
        	}
			//Stop here, too many error to update anything data.
			if( errorMsg.length() > 0)//we have error messages to handle, don't go any further!
        	{
				YukonToCRSFuncs.moveToFailureCRSToSAM_PTJ(currentEntry, errorMsg.toString());
        		continue;
        	}
        	//Get the customer account from accountNumber
			MeterHardwareBase meterHardwareBase = null;
        	CustomerAccount customerAccount = YukonToCRSFuncs.retrieveCustomerAccount(accountNumber);
    		if( ptjType.equalsIgnoreCase(YukonToCRSFuncs.PTJ_TYPE_XCEL_INSTALL_STRING) ||
       				ptjType.equalsIgnoreCase(YukonToCRSFuncs.PTJ_TYPE_XCEL_REPAIR_STRING) ||
        			ptjType.equalsIgnoreCase(YukonToCRSFuncs.PTJ_TYPE_XCEL_OTHER_STRING))
            {
    			
    			if( customerAccount == null)
    			{
        			try{
        				//Create new Contact data object and ContactNotification objects
	        			com.cannontech.database.data.customer.Contact contact = new com.cannontech.database.data.customer.Contact();
	        			contact = YukonToCRSFuncs.createNewContact(contact, firstName, lastName, homePhone, workPhone, crsContactPhone);
	        			
	        			YukonSelectionList ciCustTypeList = liteStarsEnergyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CI_CUST_TYPE);
	        			YukonListEntry ciCustTypeEntry = YukonToCRSFuncs.getCICustTypeEntry(ciCustTypeList, consumType);

	        			//Create a new CustomerAccount data object
	        			customerAccount = new CustomerAccount();
	        			customerAccount = YukonToCRSFuncs.createNewCustomerAccount(customerAccount, accountNumber, contact.getContact().getContactID(), debtorNumber, 
	        														presenceReq, streetAddress1, streetAddress2, cityName, stateCode, zipCode, ecID_workOrder, lastName, 
	        														ciCustTypeEntry, siteNumber);
	        			//Create new ApplianceBase (and extension of) objects
	        			YukonToCRSFuncs.createNewAppliances(customerAccount.getCustomerAccount().getAccountID(), airCond, waterHeater, ciCustTypeEntry, liteStarsEnergyCompany);
	        			
	        			//Create New Inventory for meterNumbers
	        			YukonToCRSFuncs.createMeterHardwares(customerAccount.getCustomerAccount().getAccountID(), liteStarsEnergyCompany, meterNumber, currentEntry.getAdditionalMeters());
	            		
					} catch (TransactionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
	        	else	//CustomerAccount already exists....lets update it!
	        	{
	        		try
	                {
	                    Customer customerDB = customerAccount.getCustomer().getCustomer();
	                    Contact contactDB = new Contact();
	                    contactDB.setContactID(customerDB.getPrimaryContactID());
	    				contactDB = Transaction.createTransaction(Transaction.RETRIEVE, contactDB).execute();
	
	    				if(ptjType.equalsIgnoreCase(YukonToCRSFuncs.PTJ_TYPE_XCEL_REPAIR_STRING) ||
	    	        			ptjType.equalsIgnoreCase(YukonToCRSFuncs.PTJ_TYPE_XCEL_OTHER_STRING))	//Per Sharon, INSTL doesn't care if meter number exists.
	    				{
		    				meterHardwareBase = MeterHardwareBase.retrieveMeterHardwareBase(customerAccount.getCustomerAccount().getAccountID().intValue(), meterNumber, customerAccount.getEnergyCompanyID().intValue());
		            		if( meterHardwareBase == null)
		            			errorMsg.append("MeterNumber (" + meterNumber + ") Not found for account " + accountNumber + "; ");
		                	for (int i = 0; i < currentEntry.getAdditionalMeters().size(); i++)
		                	{
		                		CRSToSAM_PTJAdditionalMeters additionalMeter = currentEntry.getAdditionalMeters().get(i);
		                		MeterHardwareBase addtlMeterHardwareBase = MeterHardwareBase.retrieveMeterHardwareBase(customerAccount.getCustomerAccount().getAccountID().intValue(), additionalMeter.getMeterNumber(), customerAccount.getEnergyCompanyID().intValue());
			            		if( addtlMeterHardwareBase == null)
               		       			errorMsg.append("Additional MeterNumber (" + additionalMeter.getMeterNumber() + ") Not found for account " + accountNumber + "; ");
		                	}
	    				}
	    				//Stop here, too many error to update anything data.
	    				if( errorMsg.length() > 0)//we have error messages to handle, don't go any further!
	                	{
	    					YukonToCRSFuncs.moveToFailureCRSToSAM_PTJ(currentEntry, errorMsg.toString());
	    					continue;
	                	}

	    				if( ptjType.equalsIgnoreCase(YukonToCRSFuncs.PTJ_TYPE_XCEL_INSTALL_STRING) )
  						{
		        			//Create new ApplianceBase (and extension of) objects, Per David.
                            YukonSelectionList ciCustTypeList = liteStarsEnergyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CI_CUST_TYPE);
                            YukonListEntry ciCustTypeEntry = YukonToCRSFuncs.getCICustTypeEntry(ciCustTypeList, consumType);
		        			YukonToCRSFuncs.createNewAppliances(customerAccount.getCustomerAccount().getAccountID(), airCond, waterHeater, ciCustTypeEntry, liteStarsEnergyCompany);
  						}

	    				YukonToCRSFuncs.updateAllContactInfo(contactDB, firstName, lastName, homePhone, workPhone, crsContactPhone);
	    	            YukonToCRSFuncs.updateAccountSite(customerAccount, streetAddress1, streetAddress2, cityName, stateCode, zipCode, presenceReq, siteNumber);
	    	            YukonToCRSFuncs.updateCustomer(customerDB, debtorNumber);
	    	            //TODO create new appliance if they don't exist?
	    	            //TODO create new meternumbers if they don't exist?
	    	            
	                } catch (TransactionException e1) {
	    				errorMsg.append("Updating of Contact, Address, or ContactNotification(s) failed; ");
	    				//TODO handle error message
	    				e1.printStackTrace();
	    			}
	        	}
            }
    		else if( ptjType.equalsIgnoreCase(YukonToCRSFuncs.PTJ_TYPE_XCEL_ACTIVATION_STRING) || 
            		ptjType.equalsIgnoreCase(YukonToCRSFuncs.PTJ_TYPE_XCEL_DEACTIVATION_STRING) ||
            		ptjType.equalsIgnoreCase(YukonToCRSFuncs.PTJ_TYPE_XCEL_REMOVE_STRING))
    		{
    			if( customerAccount == null)
    			{
	        		errorMsg.append("No CustomerAccount found for account " + accountNumber + "; ");
					YukonToCRSFuncs.moveToFailureCRSToSAM_PTJ(currentEntry, errorMsg.toString());
					continue;
    			}	        		
    			else
	        	{
	        		ecID_customer = customerAccount.getEnergyCompanyID().intValue();
		        	if( ecID_customer != ecID_workOrder)
		        		errorMsg.append("Customer EnergyCompany (" + ecID_customer+") does not match Service Company Energy Company ("+ecID_workOrder+");");
	        	}
	        	
    			meterHardwareBase = MeterHardwareBase.retrieveMeterHardwareBase(customerAccount.getCustomerAccount().getAccountID().intValue(), meterNumber, customerAccount.getEnergyCompanyID().intValue());
        		if( meterHardwareBase == null)
        			errorMsg.append("MeterNumber (" + meterNumber + ") Not found for account " + accountNumber + "; ");
        		else	//check controllable device is attached to meter
        		{
        			boolean switchAssigned = MeterHardwareBase.hasSwitchAssigned(meterHardwareBase.getInventoryBase().getInventoryID().intValue());
        			if (!switchAssigned)
        				errorMsg.append("MeterNumber (" + meterNumber + ") has no controllable device attached for account " + accountNumber + "; ");
        		}
            	for (int i = 0; i < currentEntry.getAdditionalMeters().size(); i++)
            	{
            		CRSToSAM_PTJAdditionalMeters additionalMeter = currentEntry.getAdditionalMeters().get(i);
            		MeterHardwareBase addtlMeterHardwareBase = MeterHardwareBase.retrieveMeterHardwareBase(customerAccount.getCustomerAccount().getAccountID().intValue(), additionalMeter.getMeterNumber(), customerAccount.getEnergyCompanyID().intValue());
            		if( addtlMeterHardwareBase == null)
            			errorMsg.append("Additional MeterNumber (" + additionalMeter.getMeterNumber() + ") Not found for account " + accountNumber + "; ");
            		else	//check controllable device is attached to meter
            		{
            			boolean switchAssigned = MeterHardwareBase.hasSwitchAssigned(addtlMeterHardwareBase.getInventoryBase().getInventoryID().intValue());
            			if (!switchAssigned)
            				errorMsg.append("Additional MeterNumber (" + additionalMeter.getMeterNumber() + ") has no controllable device attached for account " + accountNumber + "; ");
            		}
            	}
            	
				//Stop here, too many error to update anything data.
				if( errorMsg.length() > 0)//we have error messages to handle, don't go any further!
            	{
					YukonToCRSFuncs.moveToFailureCRSToSAM_PTJ(currentEntry, errorMsg.toString());
					continue;
            	}
    		}
        	
        	//No errors, create work order!
        	YukonSelectionList serviceStatusList = liteStarsEnergyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS);
        	
        	//Different service status yields different work order state.
        	int servStat = YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_ASSIGNED;
        	if( ptjType.equalsIgnoreCase(YukonToCRSFuncs.PTJ_TYPE_XCEL_DEACTIVATION_STRING) )
        		servStat = YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_PROCESSED;
        	YukonListEntry workStatusEntry = YukonToCRSFuncs.getEntryByYukonDefID(serviceStatusList, servStat);

        	WorkOrderBase workOrder = new WorkOrderBase();
            workOrder.getWorkOrderBase().setOrderID(com.cannontech.stars.database.db.report.WorkOrderBase.getNextOrderID());
            workOrder.getWorkOrderBase().setOrderNumber(String.valueOf(workOrder.getWorkOrderBase().getOrderID()));	//orderNumber is the same as orderID, I guess...
            workOrder.getWorkOrderBase().setWorkTypeID(new Integer(workTypeEntry.getEntryID()));
            workOrder.getWorkOrderBase().setCurrentStateID(new Integer(workStatusEntry.getEntryID()));
            workOrder.getWorkOrderBase().setServiceCompanyID(serviceCompany.getServiceCompany().getCompanyID());
            workOrder.getWorkOrderBase().setOrderedBy(liteYukonUser.getUsername());
            String descString = "Meter Number: " + meterNumber + ";";
            if( airCond.charValue() == 'Y' || waterHeater.charValue() == 'Y')
            	descString += "  ApplianceType(s): " + (airCond.charValue() == 'Y'? "Air Conditioner;": "") + (waterHeater.charValue() == 'Y'? " Water Heater;": "");
            descString += (notes.length() > 0 ? ("   Notes: " + notes) : ""); 
            workOrder.getWorkOrderBase().setDescription(descString);
            workOrder.getWorkOrderBase().setAccountID(customerAccount.getCustomerAccount().getAccountID());
            workOrder.getWorkOrderBase().setAdditionalOrderNumber(String.valueOf(ptjID));
        	workOrder.setEnergyCompanyID(new Integer(ecID_workOrder));
        	
        	//Every New Work Order has a Pending event!
            workStatusEntry = YukonToCRSFuncs.getEntryByYukonDefID(serviceStatusList, YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_PENDING);
            EventWorkOrder eventWorkOrder = EventUtils.buildEventWorkOrder(liteYukonUser.getUserID(), workStatusEntry.getEntryID(), workOrder.getWorkOrderBase().getOrderID().intValue());
           	workOrder.getEventWorkOrders().add(0, eventWorkOrder);

           	//Then, every Work Order has an Assigned event, too!
            workStatusEntry = YukonToCRSFuncs.getEntryByYukonDefID(serviceStatusList, YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_ASSIGNED);
            eventWorkOrder = EventUtils.buildEventWorkOrder(liteYukonUser.getUserID(), workStatusEntry.getEntryID(), workOrder.getWorkOrderBase().getOrderID().intValue());
           	workOrder.getEventWorkOrders().add(0, eventWorkOrder);

           	//And...every new DEACT Work Order has a Processed Event
        	if( ptjType.equalsIgnoreCase(YukonToCRSFuncs.PTJ_TYPE_XCEL_DEACTIVATION_STRING))
        	{
                workStatusEntry = YukonToCRSFuncs.getEntryByYukonDefID(serviceStatusList, YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_PROCESSED);
                eventWorkOrder = EventUtils.buildEventWorkOrder(liteYukonUser.getUserID(), workStatusEntry.getEntryID(), workOrder.getWorkOrderBase().getOrderID().intValue());
               	workOrder.getEventWorkOrders().add(0, eventWorkOrder);
               	
               	/* Handle config for Deactivation PTJ*/
               	if( meterHardwareBase != null)
               	{
	               	ArrayList<LMHardwareBase> lmHardwares = MeterHardwareBase.retrieveAssignedSwitches(meterHardwareBase.getInventoryBase().getInventoryID().intValue());
	               	if( lmHardwares.size() > 0)
	               	{
	               		YukonListEntry devStateEntry = null;
                        int availableEntryID = -1;
	               		YukonSelectionList invDevStateList = liteStarsEnergyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS);
		               	for (int i = 0; i < invDevStateList.getYukonListEntries().size(); i++)
		        		{
		        			if( invDevStateList.getYukonListEntries().get(i).getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL )
		        			{
		        				devStateEntry = invDevStateList.getYukonListEntries().get(i);
		        			}
		        			if( invDevStateList.getYukonListEntries().get(i).getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL) {
                                availableEntryID = invDevStateList.getYukonListEntries().get(i).getEntryID();
                            }
		        		}
		               	
		               	InventoryBaseDao inventoryBaseDao = 
		        			YukonSpringHook.getBean("inventoryBaseDao", InventoryBaseDao.class);
		               	for (int i = 0; i < lmHardwares.size(); i++)
		               	{
		               		try{
		               			//Retrieve the lmhardwarebase data object
		               			LMHardwareBase hardware = lmHardwares.get(i);
		               			com.cannontech.stars.database.data.hardware.LMHardwareBase lmHardwareBase = new com.cannontech.stars.database.data.hardware.LMHardwareBase();
		               			lmHardwareBase.setInventoryID(hardware.getInventoryID());
		               			lmHardwareBase.setLMHardwareBase(hardware);
		               			lmHardwareBase = Transaction.createTransaction(Transaction.RETRIEVE, lmHardwareBase).execute();
		               			 
				       			//Update the lmHardwareBase data object
                                int currentState = lmHardwareBase.getInventoryBase().getCurrentStateID();
                                if(currentState == availableEntryID) {
                                    lmHardwareBase.getInventoryBase().setCurrentStateID(new Integer(devStateEntry.getEntryID()));
                                }
				       			lmHardwareBase = Transaction.createTransaction(Transaction.UPDATE, lmHardwareBase).execute();
                                LiteInventoryBase liteHardInvBase = inventoryBaseDao.getByInventoryId(lmHardwares.get(i).getInventoryID().intValue());
                                liteHardInvBase.setCurrentStateID(lmHardwareBase.getInventoryBase().getCurrentStateID().intValue());
				       			
				       			//Log the inventory (lmHardwarebase) state change.
				       			EventUtils.logSTARSEvent(liteYukonUser.getUserID(), EventUtils.EVENT_CATEGORY_INVENTORY, lmHardwareBase.getInventoryBase().getCurrentStateID().intValue(), lmHardwareBase.getInventoryBase().getInventoryID().intValue());

				       			//Add a config to the queue to deactivate the switch
				               	SwitchCommand switchCommand = new SwitchCommandQueue.SwitchCommand();
				       			switchCommand.setAccountID(workOrder.getWorkOrderBase().getAccountID().intValue());
				       			switchCommand.setCommandType(SwitchCommandQueue.SWITCH_COMMAND_DISABLE);
				       			switchCommand.setEnergyCompanyID(workOrder.getEnergyCompanyID().intValue());
				       			switchCommand.setInfoString("Deactivation Work Order");
				       			switchCommand.setInventoryID(lmHardwareBase.getInventoryBase().getInventoryID().intValue());
				       			SwitchCommandQueue.getInstance().addCommand(switchCommand, true);
				       			
		               		}catch(TransactionException te)
		               		{
		               			te.printStackTrace();
		               		}
		               	}
	               	}
               	}
        	}

        	try
        	{
        		workOrder.getWorkOrderBase().setDateReported(eventWorkOrder.getEventBase().getEventTimestamp());	//set the work order DateReported with the most recent event date.
            	workOrder = Transaction.createTransaction(Transaction.INSERT, workOrder).execute();
            	newWorkOrderCount++;
	            DBChangeMsg dbChangeMessage = new DBChangeMsg(
    				workOrder.getWorkOrderBase().getOrderID(),
    				DBChangeMsg.CHANGE_WORK_ORDER_DB,
    				DBChangeMsg.CAT_WORK_ORDER,
    				DBChangeMsg.CAT_WORK_ORDER,
    				DbChangeType.ADD
    			);
	            dbChangeMessage.setSource("RunCRSToSam_PTJ:ForceHandleDBChange");	//TODO verify if StarsDBCache handles
                YukonToCRSFuncs.handleDBChangeMsg(dbChangeMessage);
                
                if( VersionTools.crsPtjIntegrationExists() && servStat == YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_PROCESSED)
                {	//All Processed status must have an entry in SAMToCRS_PTJ             	
                	SAMToCRS_PTJ samToCrs_ptj = new SAMToCRS_PTJ(ptjID, Integer.valueOf(accountNumber), debtorNumber, 
                												workOrder.getWorkOrderBase().getOrderNumber(), "P", new Date(), liteYukonUser.getUsername());
                	samToCrs_ptj = Transaction.createTransaction(Transaction.INSERT, samToCrs_ptj).execute();
                }
                
    			//We made it...remove currentEntry from CRSToSAM_PTJ table
    			Transaction.createTransaction(Transaction.DELETE, currentEntry).execute();

			} catch (TransactionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        CTILogger.info("CRSTOYUKON PTJ Integration complete:  Imported " + newWorkOrderCount + " new Work Orders.");
    }
    		
    public void runSwitchReplacement(List<SwitchReplacement> entries)
    {
        SwitchReplacement currentEntry = null;

        for(int j = 0; j < entries.size(); j++)
    	{
        	StringBuffer errorMsg = new StringBuffer("");
            currentEntry = entries.get(j);
            String serialNumber = currentEntry.getSerialNumber();
            String woType = currentEntry.getWOType();
            String username = currentEntry.getUserName();

        	if( !woType.equalsIgnoreCase(YukonToCRSFuncs.PTJ_TYPE_XCEL_MAINTENANCE_STRING))
        		errorMsg.append("Invalid Work Order Type found: " + woType + "; ");

        	ArrayList<com.cannontech.stars.database.db.customer.CustomerAccount> customerAccounts = com.cannontech.stars.database.db.customer.CustomerAccount.searchBySerialNumber(serialNumber);
        	if( customerAccounts.size() < 1)
        		errorMsg.append("No Customer Account found for Serial Number: " + serialNumber);
        	else if( customerAccounts.size() > 1)
        	{
        		errorMsg.append("Multiple Customer Accounts found for Serial Number: " + serialNumber + " - (");
   				for (int i = 0; i < customerAccounts.size(); i++)
   					errorMsg.append(customerAccounts.get(i).getAccountNumber() + ", ");
        		errorMsg.append(")");
        	}
        	
			//Stop here, too many error to update anything data.
			if( errorMsg.length() > 0)//we have error messages to handle, don't go any further!
        	{
				YukonToCRSFuncs.moveToFailure_SwitchReplacement(currentEntry, errorMsg.toString());
        		continue;
        	}
        	
        	CustomerAccount customerAccount = new CustomerAccount();
        	customerAccount.setAccountID(customerAccounts.get(0).getAccountID());
        	try {
				Transaction.createTransaction(Transaction.RETRIEVE, customerAccount).execute();
			} catch (TransactionException e1) {
				e1.printStackTrace();
        		errorMsg.append("Failed DB RETRIEVE of Customer Account; ");
        		YukonToCRSFuncs.moveToFailure_SwitchReplacement(currentEntry, errorMsg.toString());
        		continue;
			}
        	
        	String zipCode = customerAccount.getAccountSite().getStreetAddress().getZipCode();
            String servCompZipCode = zipCode.toString();
            if( servCompZipCode.length() > 5)
            	servCompZipCode = servCompZipCode.substring(0, 5);
        	
        	
        	//Get the serviceCompany from the zipcode
        	LiteStarsEnergyCompany liteStarsEnergyCompany = null;
        	LiteYukonUser liteYukonUser = null;
        	YukonListEntry workTypeEntry = null;
        	ServiceCompany serviceCompany = YukonToCRSFuncs.retrieveServiceCompany(servCompZipCode);
        	if( serviceCompany == null)
        		errorMsg.append("No serviceCompany found for zipcode " + servCompZipCode + "; ");
        	else
        	{
        		//Get the energyCompany from the zip code
        		liteStarsEnergyCompany = StarsDatabaseCache.getInstance().getEnergyCompany(customerAccount.getEnergyCompanyID().intValue());
        		YukonSelectionList serviceTypeList = liteStarsEnergyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE);
        		workTypeEntry = YukonToCRSFuncs.getServiceTypeEntry(serviceTypeList, woType);
	        	if( workTypeEntry == null)
	        		errorMsg.append("Invalid Work Order Type found (No match for EnergyCompany): " + woType + "; ");
	        	
	        	liteYukonUser = DaoFactory.getYukonUserDao().findUserByUsername(username);
	        	if( liteYukonUser == null)
	        		errorMsg.append("Invalid UserName found: " + username + "; ");
        	}

			//Stop here, too many error to update anything data.
			if( errorMsg.length() > 0)//we have error messages to handle, don't go any further!
        	{
				YukonToCRSFuncs.moveToFailure_SwitchReplacement(currentEntry, errorMsg.toString());
        		continue;
        	}
			
        	//No errors, create work order!
        	YukonSelectionList serviceStatusList = liteStarsEnergyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS);
        	
        	//Different service status yields different work order state.
        	int servStat = YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_ASSIGNED;
        	YukonListEntry workStatusEntry = YukonToCRSFuncs.getEntryByYukonDefID(serviceStatusList, servStat);

        	WorkOrderBase workOrder = new WorkOrderBase();
            workOrder.getWorkOrderBase().setOrderID(com.cannontech.stars.database.db.report.WorkOrderBase.getNextOrderID());
            workOrder.getWorkOrderBase().setOrderNumber(String.valueOf(workOrder.getWorkOrderBase().getOrderID()));	//orderNumber is the same as orderID, I guess...
            workOrder.getWorkOrderBase().setWorkTypeID(new Integer(workTypeEntry.getEntryID()));
            workOrder.getWorkOrderBase().setCurrentStateID(new Integer(workStatusEntry.getEntryID()));
            workOrder.getWorkOrderBase().setServiceCompanyID(serviceCompany.getServiceCompany().getCompanyID());
            workOrder.getWorkOrderBase().setOrderedBy(liteYukonUser.getUsername());
            String descString = "Replace Switch: " + serialNumber;
            workOrder.getWorkOrderBase().setDescription(descString);
            workOrder.getWorkOrderBase().setAccountID(customerAccount.getCustomerAccount().getAccountID());
//            workOrder.getWorkOrderBase().setAdditionalOrderNumber(String.valueOf(replacementID));
        	workOrder.setEnergyCompanyID(customerAccount.getEnergyCompanyID());
        	
        	//Every New Work Order has a Pending event!
            workStatusEntry = YukonToCRSFuncs.getEntryByYukonDefID(serviceStatusList, YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_PENDING);
            EventWorkOrder eventWorkOrder = EventUtils.buildEventWorkOrder(liteYukonUser.getUserID(), workStatusEntry.getEntryID(), workOrder.getWorkOrderBase().getOrderID().intValue());
           	workOrder.getEventWorkOrders().add(0, eventWorkOrder);

           	//Then, every Work Order has an Assigned event, too!
            workStatusEntry = YukonToCRSFuncs.getEntryByYukonDefID(serviceStatusList, YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_ASSIGNED);
            eventWorkOrder = EventUtils.buildEventWorkOrder(liteYukonUser.getUserID(), workStatusEntry.getEntryID(), workOrder.getWorkOrderBase().getOrderID().intValue());
           	workOrder.getEventWorkOrders().add(0, eventWorkOrder);

        	try
        	{
        		workOrder.getWorkOrderBase().setDateReported(eventWorkOrder.getEventBase().getEventTimestamp());	//set the work order DateReported with the most recent event date.
            	workOrder = Transaction.createTransaction(Transaction.INSERT, workOrder).execute();

	            DBChangeMsg dbChangeMessage = new DBChangeMsg(
    				workOrder.getWorkOrderBase().getOrderID(),
    				DBChangeMsg.CHANGE_WORK_ORDER_DB,
    				DBChangeMsg.CAT_WORK_ORDER,
    				DBChangeMsg.CAT_WORK_ORDER,
    				DbChangeType.ADD
    			);
	            dbChangeMessage.setSource("RunSwitchReplacement:ForceHandleDBChange");	//TODO verify if StarsDBCache handles
                YukonToCRSFuncs.handleDBChangeMsg(dbChangeMessage);
                
    			//We made it...remove currentEntry from SwitchReplacement table
    			Transaction.createTransaction(Transaction.DELETE, currentEntry).execute();

			} catch (TransactionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

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
    
}