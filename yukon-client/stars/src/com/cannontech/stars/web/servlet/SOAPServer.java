package com.cannontech.stars.web.servlet;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

import javax.xml.messaging.JAXMServlet;
import javax.xml.messaging.ReqRespListener;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.database.db.stars.CustomerSelectionList;
import com.cannontech.database.db.stars.CustomerListEntry;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.servlet.PILConnectionServlet;

import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.xml.*;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;
import com.cannontech.stars.xml.util.XMLUtil;

/*
 * Class of the reenable check timer task
 */
class ReenableCheckTask extends TimerTask {
	public void run() {
		try {
			CTILogger.debug( "*** Start running reenable check task ***" );
			
			EnergyCompany[] companies = SOAPServer.getAllEnergyCompanies();
			if (companies == null) return;
			
			for (int i = 0; i < companies.length; i++) {
				Hashtable selectionLists = SOAPServer.getAllSelectionLists( companies[i].getEnergyCompanyID() );
				if (selectionLists == null) continue;
				
				int reenableActionID = StarsCustListEntryFactory.getStarsCustListEntry(
						(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_LMCUSTOMERACTION),
						CustomerListEntry.YUKONDEF_ACT_FUTUREACTIVATION ).getEntryID();
				int completeActionID = StarsCustListEntryFactory.getStarsCustListEntry(
						(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_LMCUSTOMERACTION),
						CustomerListEntry.YUKONDEF_ACT_COMPLETED ).getEntryID();
				int programEventID = StarsCustListEntryFactory.getStarsCustListEntry(
						(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_LMCUSTOMEREVENT),
						CustomerListEntry.YUKONDEF_LMPROGRAMEVENT ).getEntryID();
				int hardwareEventID = StarsCustListEntryFactory.getStarsCustListEntry(
						(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_LMCUSTOMEREVENT),
						CustomerListEntry.YUKONDEF_LMHARDWAREEVENT ).getEntryID();
				
				com.cannontech.database.db.stars.event.LMCustomerEventBase[] hwEvents =
						com.cannontech.database.db.stars.event.LMCustomerEventBase.getAllCustomerEvents( hardwareEventID, reenableActionID );
				com.cannontech.database.db.stars.event.LMCustomerEventBase[] progEvents =
						com.cannontech.database.db.stars.event.LMCustomerEventBase.getAllCustomerEvents( programEventID, reenableActionID );
						
				for (int j = 0; j < hwEvents.length; j++) {
					if (hwEvents[j].getEventDateTime().before( new Date() )) {
						// Send yukon switch command to enable the LM hardware
						com.cannontech.database.db.stars.event.LMHardwareEvent hwEvent = new com.cannontech.database.db.stars.event.LMHardwareEvent();
						hwEvent.setEventID( hwEvents[i].getEventID() );
						hwEvent = (com.cannontech.database.db.stars.event.LMHardwareEvent)
								Transaction.createTransaction( Transaction.RETRIEVE, hwEvent ).execute();
								
						com.cannontech.database.db.stars.hardware.LMHardwareBase hw = new com.cannontech.database.db.stars.hardware.LMHardwareBase();
						hw.setInventoryID( hwEvent.getInventoryID() );
						hw = (com.cannontech.database.db.stars.hardware.LMHardwareBase)
								Transaction.createTransaction( Transaction.RETRIEVE, hw ).execute();
								
						String cmd = "putconfig service in serial " + hw.getManufacturerSerialNumber();
						ServerUtils.sendCommand( cmd, ServerUtils.getClientConnection() );
						
						CTILogger.debug( "*** Send service in command to serial " + hw.getManufacturerSerialNumber() );
						
						// Update the customer event table
						hwEvents[j].setActionID( new Integer(completeActionID) );
						hwEvents[j].setEventDateTime( new Date() );
						hwEvents[j] = (com.cannontech.database.db.stars.event.LMCustomerEventBase)
								Transaction.createTransaction( Transaction.UPDATE, hwEvents[j] ).execute();
						
						// Update the lite object
						LiteLMHardware liteHw = SOAPServer.getLMHardware( companies[i].getEnergyCompanyID(), hw.getInventoryID(), false );
						if (liteHw != null) {
							ArrayList hwHist = liteHw.getLmHardwareHistory();
							if (hwHist != null) {
								for (int k = hwHist.size() - 1; k >= 0; k--) {
									LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) hwHist.get(k);
									if (liteEvent.getEventID() == hwEvents[j].getEventID().intValue()) {
										StarsLiteFactory.setLiteLMCustomerEvent( liteEvent, hwEvents[j] );
										break;
									}
								}
							}
						}
					}
				}
						
				for (int j = 0; j < progEvents.length; j++) {
					Date now = new Date();
					if (progEvents[j].getEventDateTime().before( now )) {
						progEvents[j].setActionID( new Integer(completeActionID) );
						progEvents[j].setEventDateTime( now );
						progEvents[j] = (com.cannontech.database.db.stars.event.LMCustomerEventBase)
								Transaction.createTransaction( Transaction.UPDATE, progEvents[j] ).execute();
						
						// Update the lite object
						com.cannontech.database.db.stars.event.LMProgramEvent progEvent = new com.cannontech.database.db.stars.event.LMProgramEvent();
						progEvent.setEventID( progEvents[j].getEventID() );
						progEvent = (com.cannontech.database.db.stars.event.LMProgramEvent)
								Transaction.createTransaction( Transaction.RETRIEVE, progEvent ).execute();
								
						ArrayList liteAcctInfoList = SOAPServer.getAllCustAccountInformation( companies[i].getEnergyCompanyID() );
						for (int k = 0; k < liteAcctInfoList.size(); k++) {
							LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) liteAcctInfoList.get(k);
							if (liteAcctInfo.getCustomerAccount().getAccountID() != progEvent.getAccountID().intValue()) continue;
							
							ArrayList programs = liteAcctInfo.getLmPrograms();
							if (programs != null) {
								for (int l = 0; l < programs.size(); l++) {
									LiteStarsLMProgram liteProg = (LiteStarsLMProgram) programs.get(l);
									if (liteProg.getLmProgramID() != progEvent.getLMProgramID().intValue()) continue;
									
									ArrayList progHist = liteProg.getProgramHistory();
									if (progHist != null) {
										for (int m = progHist.size() - 1; m >= 0; m--) {
											LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) progHist.get(m);
											if (liteEvent.getEventID() == progEvent.getEventID().intValue()) {
												StarsLiteFactory.setLiteLMCustomerEvent( liteEvent, progEvents[j] );
												break;
											}
										}
									}
									break;
								}
							}
							break;
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class SOAPServer extends JAXMServlet implements ReqRespListener, com.cannontech.database.cache.DBChangeListener {
    
    // Instance of the SOAPServer object
    private static SOAPServer instance = null;
    
    // Timer object for periodical tasks
    private static Timer timer = new Timer();
    
    // Period in milliseconds between reenable checks
    private static final long REENABLE_CHECK_PERIOD = 1000 * 3600 * 24;
	
	private com.cannontech.message.dispatch.ClientConnection connToDispatch;
	
	// Array of all the energy companies
	private static EnergyCompany[] energyCompanies = null;
    
    // Map of energy company to customer account info lists
    // key: Integer energyCompanyID, value: ArrayList custAccountInfos
    private static Hashtable custAccountInfoTable = new Hashtable();
    
    // Map of energy company to customer contact list
    // key: Integer energyCompanyID, value: ArrayList custContacts
    private static Hashtable custContactTable = new Hashtable();
    
    // Map of energy company to customer address list
    // key: Integer energyCompanyID, value: ArrayList custAddresses
    private static Hashtable custAddressTable = new Hashtable();
    
    // Map of energy company to LM program list
    // key: Integer energyCompanyID, value: ArrayList lmPrograms
    private static Hashtable lmProgramTable = new Hashtable();
    
    // Map of energy company to LM hardware list
    // key: Integer energyCompanyID, value: ArrayList lmHardwares
    private static Hashtable lmHardwareTable = new Hashtable();
    
    // Map of energy company to LM control history list
    // key: Integer energyCompanyID, value: ArrayList lmCtrlHistList
    private static Hashtable lmCtrlHistTable = new Hashtable();
    
    // Map of energy company to appliance category list
    // key: Integer energyCompanyID, value: ArrayList appCategories
    private static Hashtable appCatTable = new Hashtable();
    
    // Map of energy company to work order list
    // key: Integer energyCompanyID, value: ArrayList workOrders
    private static Hashtable workOrderTable = new Hashtable();
	
	// Map of energy company to customer selection lists
	// key: Integer energyCompanyID, value: Hashtable selectionLists
    private static Hashtable selectionListTable = new Hashtable();
    
    // List of web configurations
    private static ArrayList webConfigList = null;
    

    public SOAPServer() {
        super();
    }
    
    public static SOAPServer getInstance() {
    	if (instance == null)
    		instance = new SOAPServer();
    		
    	return instance;
    }
    
    /*
     * Start implementation of DBChangeListener
     */
    public com.cannontech.message.util.ClientConnection getClientConnection() {
		return connToDispatch;
	}

	public void handleDBChangeMsg(com.cannontech.message.dispatch.message.DBChangeMsg msg, com.cannontech.database.data.lite.LiteBase lBase)
	{
		if (((DBChangeMsg)msg).getSource().equals(com.cannontech.common.util.CtiUtilities.DEFAULT_MSG_SOURCE))
			return;
		
		com.cannontech.clientutils.CTILogger.debug(" ## DBChangeMsg ##\n" + msg);
		
		if( msg.getDatabase() == msg.CHANGE_CUSTOMER_CONTACT_DB )
		{
			com.cannontech.database.data.lite.LiteCustomerContact obj = (com.cannontech.database.data.lite.LiteCustomerContact) lBase;
		}
	}

    /*
     * Start implementation of ReqRespListener
     */
    public void init(javax.servlet.ServletConfig config) throws javax.servlet.ServletException {
    	super.init( config );
    	
    	ServerUtils.setConnectionContainer( (PILConnectionServlet)
    			getServletContext().getAttribute(PILConnectionServlet.SERVLET_CONTEXT_ID) );
    			
    	com.cannontech.database.db.company.EnergyCompany[] companies = getAllEnergyCompanies();
    	if (companies != null) {
	    	for (int i = 0; i < companies.length; i++) {
	    		getAllLMPrograms( companies[i].getEnergyCompanyID() );
	    		getAllApplianceCategories( companies[i].getEnergyCompanyID() );
	    		getAllSelectionLists( companies[i].getEnergyCompanyID() );
	    		
	    		custAccountInfoTable.put( companies[i].getEnergyCompanyID(), new ArrayList() );
	    		custContactTable.put( companies[i].getEnergyCompanyID(), new ArrayList() );
	    		custAddressTable.put( companies[i].getEnergyCompanyID(), new ArrayList() );
	    		lmHardwareTable.put( companies[i].getEnergyCompanyID(), new ArrayList() );
	    		lmCtrlHistTable.put( companies[i].getEnergyCompanyID(), new ArrayList() );
	    		workOrderTable.put( companies[i].getEnergyCompanyID(), new ArrayList() );
	    	}
    	}
		getAllWebConfigurations();
    	
    	initDispatchConnection();    	
    	startTimers();
    }

    public SOAPMessage onMessage(SOAPMessage message) {
        StarsOperation respOper = new StarsOperation();

        try {
            String reqStr = SOAPUtil.parseSOAPBody( message );
            StringReader sr = new StringReader( reqStr );
            StarsOperation reqOper = StarsOperation.unmarshal( sr );

            if (reqOper.getStarsLogin() != null) {
                StarsSuccess success = new StarsSuccess();
                success.setDescription( "User login successful" );
                respOper.setStarsSuccess( success );
            }
            else {
                StarsSuccess success = new StarsSuccess();
                success.setDescription( "Operation successful" );
                respOper.setStarsSuccess( success );
            }
        }
        catch (Exception e) {
            e.printStackTrace();

            StarsFailure failure = new StarsFailure();
            failure.setStatusCode( StarsConstants.FAILURE_CODE_OPERATION_FAILED );
            failure.setDescription( e.getMessage() );
            respOper.setStarsFailure( failure );
        }

        try {
            StringWriter sw = new StringWriter();
            respOper.marshal( sw );
            String respStr = XMLUtil.removeXMLDecl( sw.toString() );
            return SOAPUtil.buildSOAPMessage(respStr, "");
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }

        return null;
    }
    
    /*
     * Start implementation of class functions
     */
	void initDispatchConnection() 
	{
		String host = null;
		int port;
		try
		{
			java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");
			host = bundle.getString("dispatch_machine");
			port = (new Integer(bundle.getString("dispatch_port"))).intValue();
		}
		catch ( java.util.MissingResourceException mre )
		{
			mre.printStackTrace();
			host = "127.0.0.1";
			port = 1510;
		}
		catch ( NumberFormatException nfe )
		{
			nfe.printStackTrace();
			port = 1510;
		}
	
		connToDispatch = new com.cannontech.message.dispatch.ClientConnection();
	
		com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
		reg.setAppName("Yukon STARS");
		reg.setAppIsUnique(0);
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay( 1000000 );
		
		connToDispatch.setHost(host);
		connToDispatch.setPort(port);
		connToDispatch.setAutoReconnect(true);
		connToDispatch.setRegistrationMsg(reg);
	
		try
		{
			connToDispatch.connectWithoutWait();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	
		com.cannontech.database.cache.DefaultDatabaseCache.getInstance().addDBChangeListener(this);	
		//dbChangeListener = new DBChangeMessageListener();
		//dbChangeListener.start();
	}
    
    void startTimers() {
    	/* Right now, we just have the reenable check timer */
    	// Timer is set to run at midnight every day
    	Calendar startTime = Calendar.getInstance();
    	startTime.set( Calendar.SECOND, 0 );
    	startTime.set( Calendar.MINUTE, 0 );
    	startTime.set( Calendar.HOUR, 0 );
    	
    	timer.schedule( new ReenableCheckTask(), startTime.getTime(), REENABLE_CHECK_PERIOD );
    }
    
    public static EnergyCompany[] getAllEnergyCompanies() {
    	if (energyCompanies == null) {
    		try {
	    		java.sql.Connection conn = com.cannontech.database.PoolManager.getInstance().getConnection(
	    				com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		    	energyCompanies = com.cannontech.database.db.company.EnergyCompany.getEnergyCompanies( conn );
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	
    	CTILogger.info( "All energy companies loaded" );
    	
    	return energyCompanies;
    }
    
    public static ArrayList getAllLMPrograms(Integer energyCompanyID) {
    	ArrayList lmPrograms = (ArrayList) lmProgramTable.get( energyCompanyID );
    	
    	if (lmPrograms == null) {
    		lmPrograms = new ArrayList();
    		lmProgramTable.put( energyCompanyID, lmPrograms );
    		
    		List lmProgList = com.cannontech.database.cache.DefaultDatabaseCache.getInstance().getAllLMPrograms();
    		com.cannontech.database.db.stars.ECToGenericMapping[] items =
    				com.cannontech.database.db.stars.ECToGenericMapping.getAllMappingItems( energyCompanyID, "LMPrograms" );
    				
    		for (int i = 0; i < items.length; i++) {
    			com.cannontech.database.data.lite.LiteYukonPAObject progPao = null;
    			for (int j = 0; j < lmProgList.size(); j++) {
    				if (((com.cannontech.database.data.lite.LiteYukonPAObject) lmProgList.get(j)).getYukonID() == items[i].getItemID().intValue()
    					&& ((com.cannontech.database.data.lite.LiteYukonPAObject) lmProgList.get(j)).getType() == com.cannontech.database.data.pao.PAOGroups.LM_DIRECT_PROGRAM) {
    					progPao = (com.cannontech.database.data.lite.LiteYukonPAObject) lmProgList.get(j);
    					break;
    				}
    			}
    			
    			if (progPao == null) continue;
				
				com.cannontech.database.db.stars.LMProgramWebPublishing pubProgram =
						com.cannontech.database.db.stars.LMProgramWebPublishing.getLMProgramWebPublishing( items[i].getItemID() );
    			
				LiteLMProgram program = new LiteLMProgram();
				program.setProgramID( progPao.getYukonID() );
				program.setProgramName( progPao.getPaoName() );
				program.setWebSettingsID( pubProgram.getWebSettingsID().intValue() );
				program.setProgramCategory( items[i].getMappingCategory() );
				
				lmPrograms.add( program );
    		}
    	}
    	
    	CTILogger.info( "All LM programs loaded for energy company #" + energyCompanyID.toString() );
    	
    	return lmPrograms;
    }
    
    public static ArrayList getAllApplianceCategories(Integer energyCompanyID) {
    	ArrayList appCategories = (ArrayList) appCatTable.get( energyCompanyID );
    	
    	if (appCategories == null) {
    		appCategories = new ArrayList();
    		appCatTable.put( energyCompanyID, appCategories );
    		
    		ArrayList lmPrograms = getAllLMPrograms( energyCompanyID );
    		com.cannontech.database.db.stars.appliance.ApplianceCategory[] appCats =
    				com.cannontech.database.db.stars.appliance.ApplianceCategory.getAllApplianceCategories( energyCompanyID );
    				
    		for (int i = 0; i < appCats.length; i++) {
    			LiteApplianceCategory appCat = new LiteApplianceCategory();
    			appCat.setApplianceCategoryID( appCats[i].getApplianceCategoryID().intValue() );
    			appCat.setCategoryID( appCats[i].getCategoryID().intValue() );
    			appCat.setDescription( appCats[i].getDescription() );
    			appCat.setWebConfigurationID( appCats[i].getWebConfigurationID().intValue() );
    			
    			com.cannontech.database.db.stars.LMProgramWebPublishing[] pubProgs =
    					com.cannontech.database.db.stars.LMProgramWebPublishing.getAllLMProgramWebPublishing( appCats[i].getApplianceCategoryID() );
    			LiteLMProgram[] pubPrograms = new LiteLMProgram[ pubProgs.length ];
    			for (int j = 0; j < pubProgs.length; j++) {
    				for (int k = 0; k < lmPrograms.size(); k++) {
    					if (((LiteLMProgram) lmPrograms.get(k)).getProgramID() == pubProgs[j].getLMProgramID().intValue()) {
    						pubPrograms[j] = (LiteLMProgram) lmPrograms.get(k);
    						break;
    					}
    				}
    			}
    			appCat.setPublishedPrograms( pubPrograms );
    			
    			appCategories.add( appCat );
    		}
    	}
    	
    	CTILogger.info( "All appliance categories loaded for energy company #" + energyCompanyID.toString() );
    	
    	return appCategories;
    }
    
    public static ArrayList getAllWebConfigurations() {
    	if (webConfigList == null) {
    		webConfigList = new ArrayList();
    		
    		com.cannontech.database.db.stars.CustomerWebConfiguration[] webConfigs =
    				com.cannontech.database.db.stars.CustomerWebConfiguration.getAllCustomerWebConfigurations();
    		for (int i = 0; i < webConfigs.length; i++) {
    			LiteWebConfiguration webConfig = new LiteWebConfiguration();
    			webConfig.setConfigID(webConfigs[i].getConfigurationID().intValue() );
    			webConfig.setLogoLocation( webConfigs[i].getLogoLocation() );
    			webConfig.setDescription( webConfigs[i].getDescription() );
    			webConfig.setAlternateDisplayName( webConfigs[i].getAlternateDisplayName() );
    			webConfig.setUrl( webConfigs[i].getURL() );
    			
    			webConfigList.add( webConfig );
    		}
    	}
    	
    	CTILogger.info( "All customer web configurations loaded" );
    	
    	return webConfigList;
    }

	public static Hashtable getAllSelectionLists(Integer energyCompanyID) {
		Hashtable selectionLists = (Hashtable) selectionListTable.get( energyCompanyID );
		
		if (selectionLists == null) {
	        selectionLists = new Hashtable();
			selectionListTable.put( energyCompanyID, selectionLists );
	        
	        // Get all selection lists
	        com.cannontech.database.db.stars.CustomerSelectionList[] selLists =
	        		com.cannontech.database.data.stars.CustomerSelectionList.getAllSelectionLists( energyCompanyID );
	        
	        for (int i = 0; i < selLists.length; i++) {
	        	LiteCustomerSelectionList selectionList = new LiteCustomerSelectionList();
	        	selectionList.setListID( selLists[i].getListID().intValue() );
	        	selectionList.setListName( selLists[i].getListName() );
	        	
	        	com.cannontech.database.db.stars.CustomerListEntry[] entries =
	        			com.cannontech.database.data.stars.CustomerListEntry.getAllListEntries( selLists[i].getListID() );
	        	StarsSelectionListEntry[] listEntries = new StarsSelectionListEntry[ entries.length ];
	        			
	        	for (int j = 0; j < entries.length; j++) {
	        		listEntries[j] = new StarsSelectionListEntry();
	        		listEntries[j].setEntryID( entries[j].getEntryID().intValue() );
	        		listEntries[j].setContent( entries[j].getEntryText() );
	        		listEntries[j].setYukonDefinition( entries[j].getYukonDefinition() );
	        	}
	        	
	        	selectionList.setListEntries( listEntries );
	        	selectionLists.put( selectionList.getListName(), selectionList );
	        }
	            
	        // Get substation list
	        LiteCustomerSelectionList subList = new LiteCustomerSelectionList();
	        subList.setListID( -1 );
	        subList.setListName( "Substation" );
	        
	        com.cannontech.database.db.stars.Substation[] subs =
	        		com.cannontech.database.db.stars.Substation.getAllSubstations( energyCompanyID );
	        StarsSelectionListEntry[] listEntries = new StarsSelectionListEntry[ subs.length ];
	        
	        for (int i = 0; i < subs.length; i++) {
	        	listEntries[i] = new StarsSelectionListEntry();
	        	listEntries[i].setEntryID( subs[i].getSubstationID().intValue() );
	        	listEntries[i].setContent( subs[i].getSubstationName() );
	        }
	        
	        subList.setListEntries( listEntries );
	        selectionLists.put( subList.getListName(), subList );
	        
	        // Get service company list
	        LiteCustomerSelectionList companyList = new LiteCustomerSelectionList();
	        companyList.setListID( -1 );
	        companyList.setListName( "ServiceCompany" );
	        
	        com.cannontech.database.db.stars.report.ServiceCompany[] companies =
	        		com.cannontech.database.db.stars.report.ServiceCompany.getAllServiceCompanies( energyCompanyID );
	        listEntries = new StarsSelectionListEntry[ companies.length ];
	        
	        for (int i = 0; i < companies.length; i++) {
	        	listEntries[i] = new StarsSelectionListEntry();
	        	listEntries[i].setEntryID( companies[i].getCompanyID().intValue() );
	        	listEntries[i].setContent( companies[i].getCompanyName() );
	        }
	        
	        companyList.setListEntries( listEntries );
	        selectionLists.put( companyList.getListName(), companyList );
		}
		
		CTILogger.info( "All customer selection lists loaded for energy company #" + energyCompanyID.toString() );
		
		return selectionLists;
	}
	
	public static ArrayList getAllCustomerContacts(Integer energyCompanyID) {
		ArrayList contacts = (ArrayList) custContactTable.get( energyCompanyID );
		if (contacts == null) {
			contacts = new ArrayList();
			custContactTable.put( energyCompanyID, contacts );
		}
		
		return contacts;
	}
	
	public static ArrayList getAllCustomerAddresses(Integer energyCompanyID) {
		ArrayList addrs = (ArrayList) custAddressTable.get( energyCompanyID );
		if (addrs == null) {
			addrs = new ArrayList();
			custAddressTable.put( energyCompanyID, addrs );
		}
		
		return addrs;
	}
	
	public static ArrayList getAllLMHardwares(Integer energyCompanyID) {
		ArrayList lmHardwares = (ArrayList) lmHardwareTable.get( energyCompanyID );
		if (lmHardwares == null) {
			lmHardwares = new ArrayList();
			lmHardwareTable.put( energyCompanyID, lmHardwares );
		}
			
		return lmHardwares;
	}
	
	public static ArrayList getAllWorkOrders(Integer energyCompanyID) {
		ArrayList workOrders = (ArrayList) workOrderTable.get( energyCompanyID );
		if (workOrders == null) {
			workOrders = new ArrayList();
			workOrderTable.put( energyCompanyID, workOrders );
		}
		
		return workOrders;
	}
	
	public static ArrayList getAllCustAccountInformation(Integer energyCompanyID) {
		ArrayList acctInfoList = (ArrayList) custAccountInfoTable.get( energyCompanyID );
		if (acctInfoList == null) {
			acctInfoList = new ArrayList();
			custAccountInfoTable.put( energyCompanyID, acctInfoList );
		}
		
		return acctInfoList;
	}
	
	public static ArrayList getAllLMControlHistory(Integer energyCompanyID) {
		ArrayList ctrlHist = (ArrayList) lmCtrlHistTable.get( energyCompanyID );
		if (ctrlHist == null) {
			ctrlHist = new ArrayList();
			lmCtrlHistTable.put( energyCompanyID, ctrlHist );
		}
		
		return ctrlHist;
	}
	
	
	public static LiteCustomerContact getCustomerContact(Integer energyCompanyID, Integer contactID) {
		ArrayList custContactList = getAllCustomerContacts( energyCompanyID );
		
		LiteCustomerContact liteContact = null;
		for (int i = 0; i < custContactList.size(); i++) {
			liteContact = (LiteCustomerContact) custContactList.get(i);
			if (liteContact.getContactID() == contactID.intValue())
				return liteContact;
		}
		
		try {
			com.cannontech.database.db.customer.CustomerContact contact = new com.cannontech.database.db.customer.CustomerContact();
			contact.setContactID( contactID );
			contact = (com.cannontech.database.db.customer.CustomerContact)
					Transaction.createTransaction( Transaction.RETRIEVE, contact ).execute();
			liteContact = (LiteCustomerContact) StarsLiteFactory.createLite( contact );
			
			custContactList.add( liteContact );
			return liteContact;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void addCustomerContact(Integer energyCompanyID, LiteCustomerContact liteContact) {
		ArrayList custContactList = getAllCustomerContacts( energyCompanyID );
		custContactList.add( liteContact );
	}
	
	public static void updateCustomerContact(LiteCustomerContact liteContact) {
/*	    
    	DBChangeMsg msg = new DBChangeMsg(
    		liteContact.getContactID(),
    		DBChangeMsg.CHANGE_CUSTOMER_CONTACT_DB,
    		DBChangeMsg.CAT_CUSTOMERCONTACT,
    		DBChangeMsg.CAT_CUSTOMERCONTACT,
    		DBChangeMsg.CHANGE_TYPE_UPDATE
    		);
    	ServerUtils.getClientConnection().write( msg );*/
	}
	
	public static void deleteCustomerContact(Integer energyCompanyID, LiteCustomerContact liteContact) {
		ArrayList custContactList = getAllCustomerContacts( energyCompanyID );
		custContactList.remove( liteContact );
	}
	
	public static LiteCustomerAddress getCustomerAddress(Integer energyCompanyID, Integer addressID) {
		ArrayList custAddressList = getAllCustomerAddresses( energyCompanyID );
		
		LiteCustomerAddress liteAddr = null;
		for (int i = 0; i < custAddressList.size(); i++) {
			liteAddr = (LiteCustomerAddress) custAddressList.get(i);
			if (liteAddr.getAddressID() == addressID.intValue())
				return liteAddr;
		}
		
		try {
			com.cannontech.database.db.customer.CustomerAddress addr = new com.cannontech.database.db.customer.CustomerAddress();
			addr.setAddressID( addressID );
			addr = (com.cannontech.database.db.customer.CustomerAddress)
					Transaction.createTransaction( Transaction.RETRIEVE, addr ).execute();
			liteAddr = (LiteCustomerAddress) StarsLiteFactory.createLite( addr );
			
			custAddressList.add( liteAddr );
			return liteAddr;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static LiteCustomerAddress addCustomerAddress(Integer energyCompanyID, com.cannontech.database.db.customer.CustomerAddress addr) {
		ArrayList custAddressList = getAllCustomerAddresses( energyCompanyID );
		
		try {
			LiteCustomerAddress liteAddr = (LiteCustomerAddress) StarsLiteFactory.createLite( addr );
			custAddressList.add( liteAddr );
			return liteAddr;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static LiteLMHardware getLMHardware(Integer energyCompanyID, Integer inventoryID, boolean autoLoad) {
		ArrayList lmHardwareList = getAllLMHardwares( energyCompanyID );
		
		LiteLMHardware liteHw = null;
		for (int i = 0; i < lmHardwareList.size(); i++) {
			liteHw = (LiteLMHardware) lmHardwareList.get(i);
			if (liteHw.getInventoryID() == inventoryID.intValue())
				return liteHw;
		}
		
		if (autoLoad) {
			try {
				com.cannontech.database.data.stars.hardware.LMHardwareBase hw = new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				hw.setInventoryID( inventoryID );
				hw = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
						Transaction.createTransaction( Transaction.RETRIEVE, hw ).execute();
				liteHw = (LiteLMHardware) StarsLiteFactory.createLite( hw );
				
				lmHardwareList.add( liteHw );
				return liteHw;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static LiteLMHardware addLMHardware(Integer energyCompanyID, com.cannontech.database.data.stars.hardware.LMHardwareBase hw) {
		ArrayList lmHardwareList = getAllLMHardwares( energyCompanyID );
		
		try {
			LiteLMHardware liteHw = (LiteLMHardware) StarsLiteFactory.createLite( hw );
			lmHardwareList.add( liteHw );
			return liteHw;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static LiteStarsLMControlHistory getLMControlHistory(Integer energyCompanyID, Integer groupID) {
		ArrayList lmCtrlHistList = getAllLMControlHistory( energyCompanyID );
		
		LiteStarsLMControlHistory lmCtrlHist = null;
		for (int i = 0; i < lmCtrlHistList.size(); i++) {
			lmCtrlHist = (LiteStarsLMControlHistory) lmCtrlHistList.get(i);
			if (lmCtrlHist.getGroupID() == groupID.intValue()) {
				lmCtrlHist.updateStartIndices();
				return lmCtrlHist;
			}
		}
		
		ArrayList ctrlHistList = new ArrayList();
		com.cannontech.database.db.pao.LMControlHistory[] ctrlHist = com.cannontech.database.db.stars.LMControlHistory.getLMControlHistory(
				groupID, com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod.ALL );
		
		for (int i = 0; i < ctrlHist.length; i++) {
			LiteLMControlHistory liteCtrlHist = new LiteLMControlHistory();
			liteCtrlHist.setStartDateTime( ctrlHist[i].getStartDateTime().getTime() );
			liteCtrlHist.setControlDuration( ctrlHist[i].getControlDuration().longValue() );
			liteCtrlHist.setControlType( ctrlHist[i].getControlType() );
			liteCtrlHist.setCurrentDailyTime( ctrlHist[i].getCurrentDailyTime().longValue() );
			liteCtrlHist.setCurrentMonthlyTime( ctrlHist[i].getCurrentMonthlyTime().longValue() );
			liteCtrlHist.setCurrentSeasonalTime( ctrlHist[i].getCurrentSeasonalTime().longValue() );
			liteCtrlHist.setCurrentAnnualTime( ctrlHist[i].getCurrentAnnualTime().longValue() );
			
			ctrlHistList.add( liteCtrlHist );
		}
		
		lmCtrlHist = new LiteStarsLMControlHistory( groupID.intValue() );
		lmCtrlHist.setLmControlHistory( ctrlHistList );
		lmCtrlHist.updateStartIndices();
		
		lmCtrlHistList.add( lmCtrlHist );
		return lmCtrlHist;
	}
	
	public static LiteWorkOrderBase getWorkOrderBase(Integer energyCompanyID, Integer orderID) {
        ArrayList workOrders = getAllWorkOrders( energyCompanyID );
        
        LiteWorkOrderBase workOrder = null;
        for (int i = 0; i < workOrders.size(); i++) {
        	workOrder = (LiteWorkOrderBase) workOrders.get(i);
        	if (workOrder.getOrderID() == orderID.intValue())
        		return workOrder;
        }
        
        try {
        	com.cannontech.database.db.stars.report.WorkOrderBase order = new com.cannontech.database.db.stars.report.WorkOrderBase();
        	order.setOrderID( orderID );
        	order = (com.cannontech.database.db.stars.report.WorkOrderBase)
        			Transaction.createTransaction( Transaction.RETRIEVE, order ).execute();
        	workOrder = (LiteWorkOrderBase) StarsLiteFactory.createLite( order );
        	
        	workOrders.add( workOrder );
        	return workOrder;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        return null;
	}
	
	public static LiteWorkOrderBase addWorkOrderBase(Integer energyCompanyID, com.cannontech.database.db.stars.report.WorkOrderBase order) {
        ArrayList workOrders = getAllWorkOrders( energyCompanyID );
        
        try {
        	LiteWorkOrderBase workOrder = (LiteWorkOrderBase) StarsLiteFactory.createLite( order );
        	workOrders.add( workOrder );
        	return workOrder;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        return null;
	}
	
	public static StarsAppliance addAppliance(Integer energyCompanyID, com.cannontech.database.data.stars.appliance.ApplianceBase app) {
		ArrayList custAcctInfoList = getAllCustAccountInformation( energyCompanyID );
		
		Hashtable selectionLists = (Hashtable) getAllSelectionLists( energyCompanyID);
		
        StarsAppliance starsApp = new StarsAppliance();
        starsApp.setApplianceID( app.getApplianceBase().getApplianceID().intValue() );
        starsApp.setApplianceCategoryID( app.getApplianceBase().getApplianceCategoryID().intValue() );
        if (app.getLMHardwareConfig() != null)
        	starsApp.setInventoryID( app.getLMHardwareConfig().getInventoryID().intValue() );
        if (app.getLMProgram() != null)
        	starsApp.setLmProgramID( app.getLMProgram().getPAObjectID().intValue() );
        starsApp.setCategoryName( StarsCustListEntryFactory.getStarsCustListEntry(
        		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_APPLIANCECATEGORY),
        		app.getApplianceCategory().getCategoryID().intValue()).getContent() );
        starsApp.setManufacturer( "" );
        starsApp.setManufactureYear( "" );
        starsApp.setLocation( "" );
        starsApp.setServiceCompany( new ServiceCompany() );
        starsApp.setNotes( app.getApplianceBase().getNotes() );
        
        return starsApp;
	}
	
	public static LiteStarsCustAccountInformation getCustAccountInformation(Integer energyCompanyID, Integer accountID) {
		ArrayList custAcctInfoList = getAllCustAccountInformation( energyCompanyID );
		
		LiteStarsCustAccountInformation accountInfo = null;
		for (int i = 0; i < custAcctInfoList.size(); i++) {
			accountInfo = (LiteStarsCustAccountInformation) custAcctInfoList.get(i);
			if (accountInfo.getCustomerAccount().getAccountID() == accountID.intValue())
				return accountInfo;
		}
		
		try {
			com.cannontech.database.data.stars.customer.CustomerAccount account = new com.cannontech.database.data.stars.customer.CustomerAccount();
			account.setAccountID( accountID );
			account = (com.cannontech.database.data.stars.customer.CustomerAccount)
					Transaction.createTransaction( Transaction.RETRIEVE, account ).execute();
					
			return addCustAccountInformation( energyCompanyID, account );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static LiteStarsCustAccountInformation getCustAccountInformation(Integer energyCompanyID, String accountNo) {
		ArrayList custAcctInfoList = getAllCustAccountInformation( energyCompanyID );
		
		LiteStarsCustAccountInformation accountInfo = null;
		for (int i = 0; i < custAcctInfoList.size(); i++) {
			accountInfo = (LiteStarsCustAccountInformation) custAcctInfoList.get(i);
			if (accountInfo.getCustomerAccount().getAccountNumber().equalsIgnoreCase( accountNo ))
				return accountInfo;
		}
		
		try {
			com.cannontech.database.data.stars.customer.CustomerAccount account =
					com.cannontech.database.data.stars.customer.CustomerAccount.searchByAccountNumber( energyCompanyID, accountNo );
			if (account == null) return null;
			
			return addCustAccountInformation( energyCompanyID, account );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static LiteStarsCustAccountInformation addCustAccountInformation(Integer energyCompanyID, com.cannontech.database.data.stars.customer.CustomerAccount account) {
		ArrayList custAcctInfoList = getAllCustAccountInformation( energyCompanyID );
		
		try {
            LiteStarsCustAccountInformation accountInfo = new LiteStarsCustAccountInformation();

            com.cannontech.database.db.stars.customer.CustomerAccount accountDB = account.getCustomerAccount();
            com.cannontech.database.data.stars.customer.CustomerBase customer = account.getCustomerBase();
            com.cannontech.database.db.stars.customer.CustomerBase customerDB = customer.getCustomerBase();
            com.cannontech.database.data.stars.customer.AccountSite site = account.getAccountSite();
            com.cannontech.database.db.stars.customer.AccountSite siteDB = site.getAccountSite();
            com.cannontech.database.data.stars.customer.SiteInformation siteInfo = site.getSiteInformation();
            com.cannontech.database.db.stars.customer.SiteInformation siteInfoDB = siteInfo.getSiteInformation();
            
            accountInfo.setCustomerAccount( (LiteCustomerAccount) StarsLiteFactory.createLite(accountDB) );
            accountInfo.setCustomerBase( (LiteCustomerBase) StarsLiteFactory.createLite(customer) );
            accountInfo.setAccountSite( (LiteAccountSite) StarsLiteFactory.createLite(siteDB) );
            accountInfo.setSiteInformation( (LiteSiteInformation) StarsLiteFactory.createLite(siteInfoDB) );
                
            com.cannontech.database.db.customer.CustomerAddress streetAddr = site.getStreetAddress();
            addCustomerAddress( energyCompanyID, streetAddr );

            com.cannontech.database.db.customer.CustomerAddress billAddr = account.getBillingAddress();
            addCustomerAddress( energyCompanyID, billAddr );

            com.cannontech.database.db.customer.CustomerContact primContact = customer.getPrimaryContact();
			LiteCustomerContact litePrimContact = (LiteCustomerContact) StarsLiteFactory.createLite( primContact );
            addCustomerContact( energyCompanyID, litePrimContact );

            Vector contactList = customer.getCustomerContactVector();
            for (int i = 0; i < contactList.size(); i++) {
                com.cannontech.database.db.customer.CustomerContact contact = (com.cannontech.database.db.customer.CustomerContact) contactList.elementAt(i);
				LiteCustomerContact liteContact = (LiteCustomerContact) StarsLiteFactory.createLite( contact );
                addCustomerContact( energyCompanyID, liteContact );
            }

			Hashtable selectionLists = (Hashtable) selectionListTable.get( energyCompanyID );
			
            Vector applianceVector = account.getApplianceVector();            
            accountInfo.setAppliances( new ArrayList() );

            for (int i = 0; i < applianceVector.size(); i++) {
                com.cannontech.database.data.stars.appliance.ApplianceBase appliance =
                            (com.cannontech.database.data.stars.appliance.ApplianceBase) applianceVector.elementAt(i);
                com.cannontech.database.db.stars.appliance.ApplianceCategory category = appliance.getApplianceCategory();
                com.cannontech.database.db.stars.hardware.LMHardwareConfiguration config = appliance.getLMHardwareConfig();

                StarsAppliance starsApp = new StarsAppliance();
                starsApp.setApplianceID( appliance.getApplianceBase().getApplianceID().intValue() );
                starsApp.setApplianceCategoryID( category.getApplianceCategoryID().intValue() );
                if (config.getInventoryID() != null)
                    starsApp.setInventoryID( config.getInventoryID().intValue() );
                else
                    starsApp.setInventoryID( -1 );
                starsApp.setLmProgramID( appliance.getApplianceBase().getLMProgramID().intValue() );
                starsApp.setCategoryName( StarsCustListEntryFactory.getStarsCustListEntry(
                		(LiteCustomerSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_APPLIANCECATEGORY ),
                		category.getCategoryID().intValue()).getContent() );
                starsApp.setManufacturer( "" );
                starsApp.setManufactureYear( "" );
                starsApp.setLocation( "" );
                starsApp.setServiceCompany( new ServiceCompany() );
                if (appliance.getApplianceBase().getNotes() != null)
                    starsApp.setNotes( appliance.getApplianceBase().getNotes() );
                else
                    starsApp.setNotes( "" );

                accountInfo.getAppliances().add( starsApp );
            }

            Vector inventoryVector = account.getInventoryVector();
            accountInfo.setInventories( new ArrayList() );

            for (int i = 0; i < inventoryVector.size(); i++) {
                com.cannontech.database.data.stars.hardware.InventoryBase inventory =
                		(com.cannontech.database.data.stars.hardware.InventoryBase) inventoryVector.elementAt(i);

                if (inventory instanceof com.cannontech.database.data.stars.hardware.LMHardwareBase) {
                    // This is a LM hardware
                    com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
							(com.cannontech.database.data.stars.hardware.LMHardwareBase) inventory;
                    addLMHardware( energyCompanyID, hardware );
                    accountInfo.getInventories().add( hardware.getInventoryBase().getInventoryID() );
                }
            }

            accountInfo.setLmPrograms( new ArrayList() );

            for (int i = 0; i < applianceVector.size(); i++) {
                com.cannontech.database.data.stars.appliance.ApplianceBase appliance =
                        (com.cannontech.database.data.stars.appliance.ApplianceBase) applianceVector.elementAt(i);
                com.cannontech.database.data.device.lm.LMProgramBase program = appliance.getLMProgram();
                if (program.getPAObjectID().intValue() == 0) continue;
                
                LiteStarsLMProgram liteProg = new LiteStarsLMProgram();
                liteProg.setLmProgramID( program.getPAObjectID().intValue() );
                liteProg.setGroupID( appliance.getLMHardwareConfig().getAddressingGroupID().intValue() );
                
                com.cannontech.database.data.stars.event.LMProgramEvent[] events =
                		com.cannontech.database.data.stars.event.LMProgramEvent.getAllLMProgramEvents( accountDB.getAccountID(), program.getPAObjectID() );
                if (events != null) {
                	liteProg.setProgramHistory( new ArrayList() );
                	for (int j = 0; j < events.length; j++) {
                		LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( events[j] );
                		liteProg.getProgramHistory().add( liteEvent );
                	}
                }
                
                getLMControlHistory( energyCompanyID, appliance.getLMHardwareConfig().getAddressingGroupID() );
                
                accountInfo.getLmPrograms().add( liteProg );
            }
            
			StarsCallReport[] calls = StarsCallReportFactory.getStarsCallReports( energyCompanyID, accountDB.getAccountID() );
			if (calls != null) {
				accountInfo.setCallReportHistory( new ArrayList() );
				for (int i = 0; i < calls.length; i++)
					accountInfo.getCallReportHistory().add( calls[i] );
			}
				
	        com.cannontech.database.db.stars.report.WorkOrderBase[] orders =
	        		com.cannontech.database.db.stars.report.WorkOrderBase.getAllCustomerSiteWorkOrders( customerDB.getCustomerID(), accountDB.getAccountSiteID() );
	        if (orders != null) {
	        	accountInfo.setServiceRequestHistory( new ArrayList() );
	        	for (int i = 0; i < orders.length; i++) {
	        		addWorkOrderBase( energyCompanyID, orders[i] );
	        		accountInfo.getServiceRequestHistory().add( orders[i].getOrderID() );
	        	}
	        }

            custAcctInfoList.add( accountInfo );
            return accountInfo;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}