package com.cannontech.database.data.lite.stars;

import java.util.*;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.*;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.*;
import com.cannontech.message.dispatch.message.DBChangeMsg;

import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.util.*;
import com.cannontech.stars.xml.*;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.serialize.types.*;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteStarsEnergyCompany extends LiteBase {
	
	public static final int FAKE_LIST_ID = -9999;	// Magic number for YukonSelectionList ID, used for substation and service company list
	
	private String name = null;
	private int routeID = 0;
	private int webConfigID = CtiUtilities.NONE_ID;
	
	private ArrayList custAccountInfos = null;	// List of LiteStarsCustAccountInformation
	private ArrayList customerContacts = null;	// List of LiteCustomerContact
	private ArrayList addresses = null;			// List of LiteAddress
	private ArrayList lmPrograms = null;			// List of LiteLMProgram
	private ArrayList lmHardwares = null;			// List of LiteLMHardwareBase
	private ArrayList lmCtrlHists = null;			// List of LiteStarsLMControlHistory
	private ArrayList appCategories = null;		// List of LiteApplianceCategory
	private ArrayList workOrders = null;			// List of LiteWorkOrderBase
	private ArrayList serviceCompanies = null;	// List of LiteServiceCompany
	private ArrayList selectionLists = null;		// List of YukonSelectionList
	private ArrayList interviewQuestions = null;	// List of LiteInterviewQuestion
	private ArrayList customerFAQs = null;		// List of LiteCustomerFAQ
	private LiteStarsThermostatSettings dftThermSettings = null;
	
	private Object[][] thermModeSettings = null;	// Map between webConfigurationID(Integer) and StarsThermoModeSettings
	private int nextCallNo = 0;
	private int nextOrderNo = 0;
	
	
	// Cached XML messages
	private StarsCustomerSelectionLists starsCustSelLists = null;
	private StarsEnrollmentPrograms starsEnrPrograms = null;
	private StarsCustomerFAQs starsCustFAQs = null;
	private Hashtable starsWebConfigs = null;		// Map Integer(web config ID) to StarsWebConfig
	private Hashtable starsCustAcctInfos = null;	// Map Integer(account ID) to StarsCustAccountInformation
	
	
	public LiteStarsEnergyCompany() {
		super();
		setLiteType( LiteTypes.ENERGY_COMPANY );
	}
	
	public LiteStarsEnergyCompany(int companyID) {
		super();
		setLiteID( companyID );
		setLiteType( LiteTypes.ENERGY_COMPANY );
	}
	
	public LiteStarsEnergyCompany(com.cannontech.database.db.company.EnergyCompany energyCompany) {
		super();
		setLiteType( LiteTypes.ENERGY_COMPANY );
		setLiteID( energyCompany.getEnergyCompanyID().intValue() );
		setName( energyCompany.getName() );
		setRouteID( energyCompany.getRouteID().intValue() );
		setWebConfigID( energyCompany.getWebConfigID().intValue() );
	}
	
	public Integer getEnergyCompanyID() {
		return new Integer( getLiteID() );
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the routeID.
	 * @return int
	 */
	public int getRouteID() {
		return routeID;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the routeID.
	 * @param routeID The routeID to set
	 */
	public void setRouteID(int routeID) {
		this.routeID = routeID;
	}
	
	/**
	 * Returns the webConfigID.
	 * @return int
	 */
	public int getWebConfigID() {
		return webConfigID;
	}

	/**
	 * Sets the webConfigID.
	 * @param webConfigID The webConfigID to set
	 */
	public void setWebConfigID(int webConfigID) {
		this.webConfigID = webConfigID;
	}
	
	public void init() {
		getAllLMPrograms();
		getAllApplianceCategories();
		getAllServiceCompanies();
		getAllSelectionLists();
		loadDefaultThermostatSettings();
		getAllInterviewQuestions();
		getAllCustomerFAQs();
	}
	
	public void clear() {
		custAccountInfos = null;
		customerContacts = null;
		addresses = null;
		lmPrograms = null;
		lmHardwares = null;
		lmCtrlHists = null;
		appCategories = null;
		workOrders = null;
		serviceCompanies = null;
		selectionLists = null;
		interviewQuestions = null;
		customerFAQs = null;
		dftThermSettings = null;
		thermModeSettings = null;
		nextCallNo = 0;
		nextOrderNo = 0;
		
		starsCustSelLists = null;
		starsEnrPrograms = null;
		starsCustFAQs = null;
		starsWebConfigs = null;
		starsCustAcctInfos = null;
	}
    
    public synchronized ArrayList getAllLMPrograms() {
    	if (lmPrograms == null) {
    		lmPrograms = new ArrayList();
    		
    		com.cannontech.database.db.stars.ECToGenericMapping[] items =
    				com.cannontech.database.db.stars.ECToGenericMapping.getAllMappingItems( getEnergyCompanyID(), "LMPrograms" );
    				
    		for (int i = 0; i < items.length; i++) {
    			com.cannontech.database.data.lite.LiteYukonPAObject progPao =
    					com.cannontech.database.cache.functions.PAOFuncs.getLiteYukonPAO( items[i].getItemID().intValue() );
    			if (progPao == null) continue;
				
				com.cannontech.database.db.stars.LMProgramWebPublishing pubProgram =
						com.cannontech.database.db.stars.LMProgramWebPublishing.getLMProgramWebPublishing( items[i].getItemID() );
				
				LiteLMProgram program = new LiteLMProgram();
				program.setProgramID( progPao.getYukonID() );
				program.setProgramName( progPao.getPaoName() );
				program.setWebSettingsID( pubProgram.getWebSettingsID().intValue() );
				program.setChanceOfControlID( pubProgram.getChanceOfControlID().intValue() );
				program.setProgramCategory( items[i].getMappingCategory() );
				
				try {
					com.cannontech.database.db.device.lm.LMProgramDirectGroup[] groups =
							com.cannontech.database.db.device.lm.LMProgramDirectGroup.getAllDirectGroups( new Integer(progPao.getYukonID()) );
					int[] groupIDs = new int[ groups.length ];
					for (int j = 0; j < groups.length; j++)
						groupIDs[j] = groups[j].getLmGroupDeviceID().intValue();
					program.setGroupIDs( groupIDs );
				}
				catch (java.sql.SQLException e) {
					e.printStackTrace();
				}
				
				lmPrograms.add( program );
    		}
	    	
	    	CTILogger.info( "All LM programs loaded for energy company #" + getEnergyCompanyID() );
    	}
    	
    	return lmPrograms;
    }
    
    public synchronized ArrayList getAllApplianceCategories() {
    	if (appCategories == null) {
    		appCategories = new ArrayList();
    		
    		ArrayList lmPrograms = getAllLMPrograms();
    		com.cannontech.database.db.stars.appliance.ApplianceCategory[] appCats =
    				com.cannontech.database.db.stars.appliance.ApplianceCategory.getAllApplianceCategories( getEnergyCompanyID() );
    				
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
	    	
	    	CTILogger.info( "All appliance categories loaded for energy company #" + getEnergyCompanyID() );
    	}
    	
    	return appCategories;
    }

	public synchronized ArrayList getAllSelectionLists() {
		if (selectionLists == null) {
	        selectionLists = new ArrayList();
	        
	        com.cannontech.database.db.stars.ECToGenericMapping[] items =
					com.cannontech.database.db.stars.ECToGenericMapping.getAllMappingItems( getEnergyCompanyID(), YukonSelectionList.TABLE_NAME );
	        if (items != null) {
	        	for (int i = 0; i < items.length; i++)
	        		selectionLists.add( YukonListFuncs.getYukonSelectionList(items[i].getItemID().intValue()) );
	        }
	            
	        // Get substation list
	        com.cannontech.database.db.stars.Substation[] subs =
	        		com.cannontech.database.db.stars.Substation.getAllSubstations( getEnergyCompanyID() );
	        if (subs != null && subs.length > 0) {
		        YukonSelectionList subList = new YukonSelectionList();
		        subList.setListID( FAKE_LIST_ID );
		        subList.setListName( com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION );
		        
		        ArrayList entries = subList.getYukonListEntries();
		        for (int i = 0; i < subs.length; i++) {
		        	StarsSelectionListEntry entry = new StarsSelectionListEntry();
		        	entry.setEntryID( subs[i].getSubstationID().intValue() );
		        	entry.setContent( subs[i].getSubstationName() );
		        	
		        	entries.add( entry );
		        }
		        
		        selectionLists.add( subList );
	        }
	        
	        // Get service company list
	        com.cannontech.database.db.stars.report.ServiceCompany[] companies =
	        		com.cannontech.database.db.stars.report.ServiceCompany.getAllServiceCompanies( getEnergyCompanyID() );
	        if (companies != null && companies.length > 0) {
		        YukonSelectionList companyList = new YukonSelectionList();
		        companyList.setListID( FAKE_LIST_ID );
		        companyList.setListName( com.cannontech.database.db.stars.report.ServiceCompany.LISTNAME_SERVICECOMPANY );
		        
		        ArrayList entries = companyList.getYukonListEntries();
		        for (int i = 0; i < companies.length; i++) {
		        	StarsSelectionListEntry entry = new StarsSelectionListEntry();
		        	entry.setEntryID( companies[i].getCompanyID().intValue() );
		        	entry.setContent( companies[i].getCompanyName() );
		        	
		        	entries.add( entry );
		        }
		        
		        selectionLists.add( companyList );
	        }
			
			CTILogger.info( "All customer selection lists loaded for energy company #" + getEnergyCompanyID() );
		}
		
		return selectionLists;
	}
	
	public synchronized YukonListEntry getYukonListEntry(int yukonDefID) {
		ArrayList selectionLists = getAllSelectionLists();
		for (int i = 0; i < selectionLists.size(); i++) {
			YukonSelectionList list = (YukonSelectionList) selectionLists.get(i);
			if (list.getListID() == FAKE_LIST_ID) continue;
			
			ArrayList entries = list.getYukonListEntries();
			for (int j = 0; j < entries.size(); j++) {
				YukonListEntry entry = (YukonListEntry) entries.get(j);
				if (entry.getYukonDefID() == yukonDefID)
					return entry;
			}
		}
		
		return null;
	}
	
	public synchronized StarsSelectionListEntry getStarsSelectionListEntry(String listName, int entryID) {
		ArrayList selectionLists = getAllSelectionLists();
		for (int i = 0; i < selectionLists.size(); i++) {
			YukonSelectionList list = (YukonSelectionList) selectionLists.get(i);
			if (list.getListID() == FAKE_LIST_ID && list.getListName().equalsIgnoreCase(listName)) {
				ArrayList entries = list.getYukonListEntries();
				
				for (int j = 0; j < entries.size(); j++) {
					StarsSelectionListEntry entry = (StarsSelectionListEntry) entries.get(j);
					if (entry.getEntryID() == entryID)
						return entry;
				}
			}
		}
		
		return null;
	}
	
	public synchronized ArrayList getAllServiceCompanies() {
		if (serviceCompanies == null) {
			serviceCompanies = new ArrayList();
			
	        com.cannontech.database.db.stars.report.ServiceCompany[] companies =
	        		com.cannontech.database.db.stars.report.ServiceCompany.getAllServiceCompanies( getEnergyCompanyID() );
	        for (int i = 0; i < companies.length; i++) {
	        	LiteServiceCompany serviceCompany = new LiteServiceCompany();
	        	serviceCompany.setCompanyID( companies[i].getCompanyID().intValue() );
	        	serviceCompany.setCompanyName( companies[i].getCompanyName() );
	        	serviceCompany.setAddressID( companies[i].getAddressID().intValue() );
	        	serviceCompany.setMainPhoneNumber( companies[i].getMainPhoneNumber() );
	        	serviceCompany.setMainFaxNumber( companies[i].getMainFaxNumber() );
	        	serviceCompany.setPrimaryContactID( companies[i].getPrimaryContactID().intValue() );
	        	serviceCompany.setHiType( companies[i].getHIType() );
	        	
	        	serviceCompanies.add( serviceCompany );
	        }
			
			CTILogger.info( "All service companies loaded for energy company #" + getEnergyCompanyID() );
		}
		
		return serviceCompanies;
	}
	
	public synchronized void loadDefaultThermostatSettings() {
		if (dftThermSettings != null && thermModeSettings != null) return;
		
		String sql = "SELECT inv.InventoryID FROM ECToInventoryMapping map, "
				   + com.cannontech.database.db.stars.hardware.InventoryBase.TABLE_NAME + " inv "
				   + "WHERE inv.InventoryID = map.InventoryID AND map.EnergyCompanyID = " + getEnergyCompanyID()
				   + " AND inv.InventoryID < 0";
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
			if (stmt.getRowCount() > 0) {
				int dftInventoryID = ((java.math.BigDecimal) stmt.getRow(0)[0]).intValue();
		        dftThermSettings = new LiteStarsThermostatSettings();
		        dftThermSettings.setInventoryID( dftInventoryID );
		        
		        com.cannontech.database.data.stars.hardware.LMThermostatSeason[] seasons =
		        		com.cannontech.database.data.stars.hardware.LMThermostatSeason.getAllLMThermostatSeasons(
		        			new Integer(dftInventoryID) );
		        			
		        if (seasons != null) {
			        dftThermSettings.setThermostatSeasons( new ArrayList() );
			        thermModeSettings = new Object[ seasons.length ][];
			        
			        for (int j = 0; j < seasons.length; j++) {
			        	LiteLMThermostatSeason liteSeason = (LiteLMThermostatSeason) StarsLiteFactory.createLite( seasons[j] );
			        	dftThermSettings.getThermostatSeasons().add( liteSeason );
			        	
			        	thermModeSettings[j] = new Object[2];
			        	thermModeSettings[j][0] = new Integer( liteSeason.getWebConfigurationID() );
			        	LiteWebConfiguration liteConfig = SOAPServer.getWebConfiguration( liteSeason.getWebConfigurationID() );
			        	if (liteConfig.getAlternateDisplayName().equalsIgnoreCase("Summer"))
			        		thermModeSettings[j][1] = StarsThermoModeSettings.COOL;
			        	else
			        		thermModeSettings[j][1] = StarsThermoModeSettings.HEAT;
			        }
		        }
		        else
					CTILogger.info( "No default thermostat schedules found for energy company #" + getEnergyCompanyID() );
		        
		        com.cannontech.database.data.stars.event.LMThermostatManualEvent[] events =
		        		com.cannontech.database.data.stars.event.LMThermostatManualEvent.getAllLMThermostatManualEvents( new Integer(dftInventoryID) );
		        if (events != null) {
		        	for (int i = 0; i < events.length; i++)
		        		dftThermSettings.getThermostatManualEvents().add(
		        			(LiteLMThermostatManualEvent) StarsLiteFactory.createLite(events[i]) );
		        }
		        else
					CTILogger.info( "No default thermostat option found for energy company #" + getEnergyCompanyID() );
			}
			
			CTILogger.info( "Default thermostat settings loaded for energy company #" + getEnergyCompanyID() );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized LiteStarsThermostatSettings getDefaultThermostatSettings() {
		if (dftThermSettings == null)
			loadDefaultThermostatSettings();
		return dftThermSettings;
	}
	
	public synchronized StarsThermoModeSettings getThermModeSetting(int configID) {
		if (thermModeSettings == null)
			loadDefaultThermostatSettings();
		if (thermModeSettings == null) return null;
		
		for (int i = 0; i < thermModeSettings.length; i++) {
			if (((Integer) thermModeSettings[i][0]).intValue() == configID)
				return (StarsThermoModeSettings) thermModeSettings[i][1];
		}
		return null;
	}
	
	public synchronized Integer getThermSeasonWebConfigID(StarsThermoModeSettings setting) {
		if (thermModeSettings == null)
			loadDefaultThermostatSettings();
		if (thermModeSettings == null) return null;
		
		for (int i = 0; i < thermModeSettings.length; i++) {
			if (((StarsThermoModeSettings) thermModeSettings[i][1]).getType() == setting.getType())
				return (Integer) thermModeSettings[i][0];
		}
		return null;
	}
	
	public synchronized ArrayList getAllInterviewQuestions() {
		if (interviewQuestions == null) {
			interviewQuestions = new ArrayList();
			
			com.cannontech.database.db.stars.InterviewQuestion[] questions =
					com.cannontech.database.db.stars.InterviewQuestion.getAllInterviewQuestions( getEnergyCompanyID() );
			for (int i = 0; i < questions.length; i++) {
				LiteInterviewQuestion liteQuestion = new LiteInterviewQuestion();
				liteQuestion.setQuestionID( questions[i].getQuestionID().intValue() );
				liteQuestion.setQuestionType( questions[i].getQuestionType().intValue() );
				liteQuestion.setQuestion( questions[i].getQuestion() );
				liteQuestion.setMandatory( questions[i].getMandatory() );
				liteQuestion.setAnswerType( questions[i].getAnswerType().intValue() );
				liteQuestion.setExpectedAnswer( questions[i].getExpectedAnswer().intValue() );
				
				interviewQuestions.add( liteQuestion );
			}
			
			CTILogger.info( "All interview questions loaded for energy company #" + getEnergyCompanyID() );
		}
		
		return interviewQuestions;
	}
	
	public synchronized ArrayList getAllCustomerFAQs() {
		if (customerFAQs == null) {
			customerFAQs = new ArrayList();
			
			com.cannontech.database.db.stars.CustomerFAQ[] faqs =
					com.cannontech.database.db.stars.CustomerFAQ.getAllCustomerFAQs( getEnergyCompanyID() );
			for (int i = 0; i < faqs.length; i++) {
				LiteCustomerFAQ liteFAQ = new LiteCustomerFAQ();
				liteFAQ.setQuestionID( faqs[i].getQuestionID().intValue() );
				liteFAQ.setSubjectID( faqs[i].getSubjectID().intValue() );
				liteFAQ.setQuestion( faqs[i].getQuestion() );
				liteFAQ.setAnswer( faqs[i].getAnswer() );
				
				customerFAQs.add( liteFAQ );
			}
			
			CTILogger.info( "All customer FAQs loaded for energy company #" + getEnergyCompanyID() );
		}
		
		return customerFAQs;
	}
	
	public synchronized String getNextCallNumber() {
		if (nextCallNo == 0) {
			String sql = "SELECT CallNumber FROM CallReportBase WHERE CallID = "
					   + "(SELECT MAX(CallReportID) FROM ECToCallReportMapping WHERE EnergyCompanyID = " + getEnergyCompanyID() + ")";
			com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
					sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
					
			try {
				stmt.execute();
				if (stmt.getRowCount() > 0)
					nextCallNo = Integer.parseInt( (String) stmt.getRow(0)[0] ) + 1;
				else
					nextCallNo = 1;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return String.valueOf( nextCallNo++ );
	}
	
	public synchronized String getNextOrderNumber() {
		if (nextOrderNo == 0) {
			String sql = "SELECT OrderNumber FROM WorkOrderBase WHERE OrderID = "
					   + "(SELECT MAX(WorkOrderID) FROM ECToWorkOrderMapping WHERE EnergyCompanyID = " + getEnergyCompanyID() + ")";
			com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
					sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
					
			try {
				stmt.execute();
				if (stmt.getRowCount() > 0)
					nextOrderNo = Integer.parseInt( (String) stmt.getRow(0)[0] ) + 1;
				else
					nextOrderNo = 1;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return String.valueOf( nextOrderNo++ );
	}
	
	public ArrayList getAllCustomerContacts() {
		if (customerContacts == null)
			customerContacts = new ArrayList();
		
		return customerContacts;
	}
	
	public ArrayList getAllAddresses() {
		if (addresses == null)
			addresses = new ArrayList();
		
		return addresses;
	}
	
	public ArrayList getAllLMHardwares() {
		if (lmHardwares == null)
			lmHardwares = new ArrayList();
			
		return lmHardwares;
	}
	
	public ArrayList getAllWorkOrders() {
		if (workOrders == null)
			workOrders = new ArrayList();
		
		return workOrders;
	}
	
	public ArrayList getAllCustAccountInformation() {
		if (custAccountInfos == null)
			custAccountInfos = new ArrayList();
		
		return custAccountInfos;
	}
	
	public ArrayList getAllLMControlHistory() {
		if (lmCtrlHists == null)
			lmCtrlHists = new ArrayList();
		
		return lmCtrlHists;
	}
	
	
	public LiteInterviewQuestion[] getInterviewQuestions(int qType) {
		ArrayList qList = new ArrayList();
		ArrayList questions = getAllInterviewQuestions();
		
		synchronized (questions) {
			for (int i = 0; i < questions.size(); i++) {
				LiteInterviewQuestion liteQ = (LiteInterviewQuestion) questions.get(i);
				if (liteQ.getQuestionType() == qType)
					qList.add( liteQ );
			}
		}
		
		LiteInterviewQuestion[] qs = new LiteInterviewQuestion[ qList.size() ];
		qList.toArray( qs );
		
		return qs;
	}

	
	public LiteApplianceCategory getApplianceCategory(int applianceCategoryID) {
		ArrayList appCats = getAllApplianceCategories();
		LiteApplianceCategory appCat = null;
		
		synchronized (appCats) {
			for (int i = 0; i < appCats.size(); i++) {
				appCat = (LiteApplianceCategory) appCats.get(i);
				if (appCat.getApplianceCategoryID() == applianceCategoryID)
					return appCat;
			}
		}
		
		return null;
	}
	
	public LiteServiceCompany getServiceCompany(int serviceCompanyID) {
		ArrayList serviceCompanies = getAllServiceCompanies();
		LiteServiceCompany serviceCompany = null;
		
		synchronized (serviceCompanies) {
			for (int i = 0; i < serviceCompanies.size(); i++) {
				serviceCompany = (LiteServiceCompany) serviceCompanies.get(i);
				if (serviceCompany.getCompanyID() == serviceCompanyID)
					return serviceCompany;
			}
		}
		
		return null;
	}
	
	public LiteCustomerContact getCustomerContact(int contactID) {
		ArrayList contactList = getAllCustomerContacts();
		synchronized (contactList) {
			for (int i = 0; i < contactList.size(); i++) {
				LiteCustomerContact liteContact = (LiteCustomerContact) contactList.get(i);
				if (liteContact.getContactID() == contactID)
					return liteContact;
			}
		}
		
		LiteContact lContact = com.cannontech.database.cache.functions.CustomerContactFuncs.getCustomerContact( contactID );
		if (lContact != null) {
			LiteCustomerContact liteContact = new LiteCustomerContact();
			liteContact.setContactID( lContact.getContactID() );
			liteContact.setLastName( lContact.getContLastName() );
			liteContact.setFirstName( lContact.getContFirstName() );
			for (int i = 0; i < lContact.getLiteContactNotifications().size(); i++) {
				LiteContactNotification lNotif = (LiteContactNotification) lContact.getLiteContactNotifications().get(i);
				if (lNotif.getNotificationCategoryID() == com.cannontech.common.constants.YukonListEntryTypes.YUK_DEF_ID_HOME_PHONE)
					liteContact.setHomePhone( lNotif.getNotification() );
				else if (lNotif.getNotificationCategoryID() == com.cannontech.common.constants.YukonListEntryTypes.YUK_DEF_ID_WORK_PHONE)
					liteContact.setWorkPhone( lNotif.getNotification() );
				else if (lNotif.getNotificationCategoryID() == com.cannontech.common.constants.YukonListEntryTypes.YUK_DEF_ID_EMAIL)
					liteContact.setEmail( LiteCustomerContact.ContactNotification.newInstance(
							lNotif.getDisableFlag().equals("N"), lNotif.getNotification()) );
			}
			
			synchronized (contactList) { contactList.add( liteContact ); }
			return liteContact;
		}
/*		
		try {
			com.cannontech.database.db.contact.Contact contact = new com.cannontech.database.db.contact.Contact();
			contact.setContactID( new Integer(contactID) );
			contact = (com.cannontech.database.db.contact.Contact)
					Transaction.createTransaction( Transaction.RETRIEVE, contact ).execute();
			LiteCustomerContact liteContact = (LiteContact) StarsLiteFactory.createLite( contact );
			
			synchronized (contactList) { contactList.add( liteContact ); }
			return liteContact;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
*/		
		return null;
	}
	
	public void addCustomerContact(LiteCustomerContact liteContact) {
		ArrayList contactList = getAllCustomerContacts();
		synchronized (contactList) { contactList.add( liteContact ); }
		//ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_ADD );
	}

	public LiteCustomerContact deleteCustomerContact(int contactID) {
		ArrayList contactList = getAllCustomerContacts();
		synchronized (contactList) {
			for (int i = 0; i < contactList.size(); i++) {
				LiteCustomerContact liteContact = (LiteCustomerContact) contactList.get(i);
				if (liteContact.getContactID() == contactID) {
					contactList.remove(i);
					//ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_DELETE );
					return liteContact;
				}
			}
		}
		
		return null;
	}
	
	public LiteAddress getAddress(int addressID) {
		ArrayList addressList = getAllAddresses();
		LiteAddress liteAddr = null;
		
		synchronized (addressList) {
			for (int i = 0; i < addressList.size(); i++) {
				liteAddr = (LiteAddress) addressList.get(i);
				if (liteAddr.getAddressID() == addressID)
					return liteAddr;
			}
		}
		
		try {
			com.cannontech.database.db.customer.Address addr = new com.cannontech.database.db.customer.Address();
			addr.setAddressID( new Integer(addressID) );
			addr = (com.cannontech.database.db.customer.Address)
					Transaction.createTransaction( Transaction.RETRIEVE, addr ).execute();
			liteAddr = (LiteAddress) StarsLiteFactory.createLite( addr );
			
			synchronized (addressList) { addressList.add( liteAddr ); }
			return liteAddr;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void addAddress(LiteAddress liteAddr) {
		ArrayList addressList = getAllAddresses();
		synchronized (addressList) { addressList.add( liteAddr ); }
	}
	
	public LiteAddress deleteAddress(int addressID) {
		ArrayList addressList = getAllAddresses();
		synchronized (addressList) {
			for (int i = 0; i < addressList.size(); i++) {
				LiteAddress liteAddr = (LiteAddress) addressList.get(i);
				if (liteAddr.getAddressID() == addressID) {
					addressList.remove(i);
					return liteAddr;
				}
			}
		}
		
		return null;
	}
	
	public LiteLMProgram getLMProgram(int programID) {
		ArrayList lmProgramList = getAllLMPrograms();
		LiteLMProgram liteProg = null;
		
		synchronized (lmProgramList) {
			for (int i = 0; i < lmProgramList.size(); i++) {
				liteProg = (LiteLMProgram) lmProgramList.get(i);
				if (liteProg.getProgramID() == programID)
					return liteProg;
			}
		}
		
		return null;
	}
	
	public LiteLMHardwareBase getLMHardware(int inventoryID, boolean autoLoad) {
		ArrayList lmHardwareList = getAllLMHardwares();
		LiteLMHardwareBase liteHw = null;
		
		synchronized (lmHardwareList) {
			for (int i = 0; i < lmHardwareList.size(); i++) {
				liteHw = (LiteLMHardwareBase) lmHardwareList.get(i);
				if (liteHw.getInventoryID() == inventoryID)
					return liteHw;
			}
		}
		
		if (autoLoad) {
			try {
				com.cannontech.database.data.stars.hardware.LMHardwareBase hw = new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				hw.setInventoryID( new Integer(inventoryID) );
				hw = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
						Transaction.createTransaction( Transaction.RETRIEVE, hw ).execute();
				liteHw = (LiteLMHardwareBase) StarsLiteFactory.createLite( hw );
				
				synchronized (lmHardwareList) { lmHardwareList.add( liteHw ); }
				return liteHw;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public void addLMHardware(LiteLMHardwareBase liteHw) {
		ArrayList lmHardwareList = getAllLMHardwares();
		synchronized (lmHardwareList) { lmHardwareList.add( liteHw ); }
	}
	
	public LiteLMHardwareBase deleteLMHardware(int invID) {
		ArrayList lmHardwareList = getAllLMHardwares();
		LiteLMHardwareBase liteHw = null;
		
		synchronized (lmHardwareList) {
			for (int i = 0; i < lmHardwareList.size(); i++) {
				liteHw = (LiteLMHardwareBase) lmHardwareList.get(i);
				if (liteHw.getInventoryID() == invID) {
					lmHardwareList.remove(i);
					return liteHw;
				}
			}
		}
		
		return null;
	}
	
	public LiteStarsLMControlHistory getLMControlHistory(int groupID) {
		ArrayList lmCtrlHistList = getAllLMControlHistory();
		LiteStarsLMControlHistory lmCtrlHist = null;
		
		synchronized (lmCtrlHistList) {
			for (int i = 0; i < lmCtrlHistList.size(); i++) {
				lmCtrlHist = (LiteStarsLMControlHistory) lmCtrlHistList.get(i);
				if (lmCtrlHist.getGroupID() == groupID) {
					SOAPServer.updateLMControlHistory( lmCtrlHist );
					lmCtrlHist.updateStartIndices();
					return lmCtrlHist;
				}
			}
		}
		
		ArrayList ctrlHistList = new ArrayList();
		com.cannontech.database.db.pao.LMControlHistory[] ctrlHist = com.cannontech.database.db.stars.LMControlHistory.getLMControlHistory(
				new Integer(groupID), com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod.ALL );
		
		for (int i = 0; i < ctrlHist.length; i++)
			ctrlHistList.add( StarsLiteFactory.createLite(ctrlHist[i]) );
		
		lmCtrlHist = new LiteStarsLMControlHistory( groupID );
		lmCtrlHist.setLmControlHistory( ctrlHistList );
		lmCtrlHist.updateStartIndices();
		
		synchronized (lmCtrlHistList) { lmCtrlHistList.add( lmCtrlHist ); }
		return lmCtrlHist;
	}
	
	public LiteWorkOrderBase getWorkOrderBase(int orderID) {
        ArrayList workOrders = getAllWorkOrders();
        LiteWorkOrderBase workOrder = null;
        
        synchronized (workOrders) {
	        for (int i = 0; i < workOrders.size(); i++) {
	        	workOrder = (LiteWorkOrderBase) workOrders.get(i);
	        	if (workOrder.getOrderID() == orderID)
	        		return workOrder;
	        }
        }
        
        try {
        	com.cannontech.database.db.stars.report.WorkOrderBase order = new com.cannontech.database.db.stars.report.WorkOrderBase();
        	order.setOrderID( new Integer(orderID) );
        	order = (com.cannontech.database.db.stars.report.WorkOrderBase)
        			Transaction.createTransaction( Transaction.RETRIEVE, order ).execute();
        	workOrder = (LiteWorkOrderBase) StarsLiteFactory.createLite( order );
        	
        	synchronized (workOrders) { workOrders.add( workOrder ); }
        	return workOrder;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        return null;
	}
	
	public void addWorkOrderBase(LiteWorkOrderBase liteOrder) {
        ArrayList workOrders = getAllWorkOrders();
    	synchronized (workOrders) { workOrders.add( liteOrder ); }
	}
	
	public LiteStarsThermostatSettings getThermostatSettings(int inventoryID) {
		LiteStarsThermostatSettings settings = new LiteStarsThermostatSettings();
        settings.setInventoryID( inventoryID );
        
        com.cannontech.database.data.stars.hardware.LMThermostatSeason[] seasons =
        		com.cannontech.database.data.stars.hardware.LMThermostatSeason.getAllLMThermostatSeasons( new Integer(inventoryID) );
        if (seasons != null) {
	        settings.setThermostatSeasons( new ArrayList() );
	        
	        for (int i = 0; i < seasons.length; i++) {
	        	LiteLMThermostatSeason liteSeason = (LiteLMThermostatSeason) StarsLiteFactory.createLite( seasons[i] );
	        	settings.getThermostatSeasons().add( liteSeason );
	        }
        }
        
        com.cannontech.database.data.stars.event.LMThermostatManualEvent[] events =
        		com.cannontech.database.data.stars.event.LMThermostatManualEvent.getAllLMThermostatManualEvents( new Integer(inventoryID) );
        if (events != null) {
        	for (int i = 0; i < events.length; i++)
        		dftThermSettings.getThermostatManualEvents().add(
        			(LiteLMThermostatManualEvent) StarsLiteFactory.createLite(events[i]) );
        }
        
        return settings;
	}
	
	public LiteStarsCustAccountInformation getCustAccountInformation(int accountID, boolean autoLoad) {
		ArrayList custAcctInfoList = getAllCustAccountInformation();
		LiteStarsCustAccountInformation accountInfo = null;
		
		synchronized (custAcctInfoList) {
			for (int i = 0; i < custAcctInfoList.size(); i++) {
				accountInfo = (LiteStarsCustAccountInformation) custAcctInfoList.get(i);
				if (accountInfo.getCustomerAccount().getAccountID() == accountID) {
					updateCustAccountInformation( accountInfo );
					return accountInfo;
				}
			}
		}
		
		if (autoLoad) {
			try {
				com.cannontech.database.data.stars.customer.CustomerAccount account = new com.cannontech.database.data.stars.customer.CustomerAccount();
				account.setAccountID( new Integer(accountID) );
				account = (com.cannontech.database.data.stars.customer.CustomerAccount)
						Transaction.createTransaction( Transaction.RETRIEVE, account ).execute();
						
				return addCustAccountInformation( account );
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public LiteStarsCustAccountInformation addCustAccountInformation(com.cannontech.database.data.stars.customer.CustomerAccount account) {
		ArrayList custAcctInfoList = getAllCustAccountInformation();
		
		try {
            com.cannontech.database.db.stars.customer.CustomerAccount accountDB = account.getCustomerAccount();
            com.cannontech.database.data.customer.Customer customer = account.getCustomer();
            com.cannontech.database.db.customer.Customer customerDB = customer.getCustomer();
            com.cannontech.database.data.stars.customer.AccountSite site = account.getAccountSite();
            com.cannontech.database.db.stars.customer.AccountSite siteDB = site.getAccountSite();
            com.cannontech.database.data.stars.customer.SiteInformation siteInfo = site.getSiteInformation();
            com.cannontech.database.db.stars.customer.SiteInformation siteInfoDB = siteInfo.getSiteInformation();
            
            LiteStarsCustAccountInformation accountInfo = new LiteStarsCustAccountInformation( accountDB.getAccountID().intValue() );

            accountInfo.setCustomerAccount( (LiteCustomerAccount) StarsLiteFactory.createLite(accountDB) );
            accountInfo.setCustomer( (LiteCustomer) StarsLiteFactory.createLite(customer) );
            accountInfo.setAccountSite( (LiteAccountSite) StarsLiteFactory.createLite(siteDB) );
            accountInfo.setSiteInformation( (LiteSiteInformation) StarsLiteFactory.createLite(siteInfoDB) );
                
            com.cannontech.database.db.customer.Address streetAddr = site.getStreetAddress();
            LiteAddress liteAddr = (LiteAddress) StarsLiteFactory.createLite( streetAddr );
            addAddress( liteAddr );

            com.cannontech.database.db.customer.Address billAddr = account.getBillingAddress();
            liteAddr = (LiteAddress) StarsLiteFactory.createLite( billAddr );
            addAddress( liteAddr );
			
            Vector applianceVector = account.getApplianceVector();            
            accountInfo.setAppliances( new ArrayList() );

            for (int i = 0; i < applianceVector.size(); i++) {
                com.cannontech.database.data.stars.appliance.ApplianceBase appliance =
                            (com.cannontech.database.data.stars.appliance.ApplianceBase) applianceVector.elementAt(i);
                accountInfo.getAppliances().add( StarsLiteFactory.createLite(appliance) );
            }

            Vector inventoryVector = account.getInventoryVector();
            accountInfo.setInventories( new ArrayList() );

			int lmHwCatID = getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC ).getEntryID();
			int thermTypeID = getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_THERMOSTAT ).getEntryID();

            for (int i = 0; i < inventoryVector.size(); i++) {
                com.cannontech.database.db.stars.hardware.InventoryBase inventory =
                		(com.cannontech.database.db.stars.hardware.InventoryBase) inventoryVector.elementAt(i);

                if (inventory.getCategoryID().intValue() == lmHwCatID) {
                    // This is a LM hardware
                    com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
							new com.cannontech.database.data.stars.hardware.LMHardwareBase();
					hardware.setInventoryID( inventory.getInventoryID() );
					hardware = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
							Transaction.createTransaction( Transaction.RETRIEVE, hardware ).execute();
					
					LiteLMHardwareBase liteHw = (LiteLMHardwareBase) StarsLiteFactory.createLite( hardware );
                    addLMHardware( liteHw );
                    accountInfo.getInventories().add( inventory.getInventoryID() );

                	// And it is a thermostat
                	if (hardware.getLMHardwareBase().getLMHardwareTypeID().intValue() == thermTypeID)
	                	accountInfo.setThermostatSettings( getThermostatSettings(inventory.getInventoryID().intValue()) );
                }
            }
            
            ServerUtils.updateServiceCompanies( accountInfo, getLiteID() );

            accountInfo.setLmPrograms( new ArrayList() );

            for (int i = 0; i < applianceVector.size(); i++) {
                com.cannontech.database.data.stars.appliance.ApplianceBase appliance =
                        (com.cannontech.database.data.stars.appliance.ApplianceBase) applianceVector.elementAt(i);
                int progID = appliance.getApplianceBase().getLMProgramID().intValue();
                if (progID == 0) continue;
                
                LiteLMProgram liteProg = getLMProgram( progID );
                LiteStarsLMProgram prog = new LiteStarsLMProgram( liteProg );
                prog.setGroupID( appliance.getLMHardwareConfig().getAddressingGroupID().intValue() );
                
                com.cannontech.database.data.stars.event.LMProgramEvent[] events =
                		com.cannontech.database.data.stars.event.LMProgramEvent.getAllLMProgramEvents( accountDB.getAccountID(), new Integer(progID) );
                if (events != null) {
                	prog.setProgramHistory( new ArrayList() );
                	for (int j = 0; j < events.length; j++) {
                		LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( events[j] );
                		prog.getProgramHistory().add( liteEvent );
                	}
                }
                
                getLMControlHistory( appliance.getLMHardwareConfig().getAddressingGroupID().intValue() );
                
                accountInfo.getLmPrograms().add( prog );
            }
            
			StarsCallReport[] calls = StarsCallReportFactory.getStarsCallReports( accountDB.getAccountID() );
			if (calls != null) {
				accountInfo.setCallReportHistory( new ArrayList() );
				for (int i = 0; i < calls.length; i++)
					accountInfo.getCallReportHistory().add( calls[i] );
			}
				
	        com.cannontech.database.db.stars.report.WorkOrderBase[] orders =
	        		com.cannontech.database.db.stars.report.WorkOrderBase.getAllAccountWorkOrders( accountDB.getAccountID() );
	        if (orders != null) {
	        	accountInfo.setServiceRequestHistory( new ArrayList() );
	        	for (int i = 0; i < orders.length; i++) {
	        		LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) StarsLiteFactory.createLite( orders[i] );
	        		addWorkOrderBase( liteOrder );
	        		accountInfo.getServiceRequestHistory().add( orders[i].getOrderID() );
	        	}
	        }

            synchronized (custAcctInfoList) { custAcctInfoList.add( accountInfo ); }
            return accountInfo;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void deleteCustAccountInformation(LiteStarsCustAccountInformation liteAcctInfo) {
		// Remote all contacts from customerContacts
		deleteCustomerContact( liteAcctInfo.getCustomer().getPrimaryContactID() );
		ArrayList contacts = liteAcctInfo.getCustomer().getAdditionalContacts();
		for (int i = 0; i < contacts.size(); i++)
			deleteCustomerContact( ((Integer) contacts.get(i)).intValue() );
		
		// Remove all addresses from addresses
		deleteAddress( liteAcctInfo.getCustomerAccount().getBillingAddressID() );
		deleteAddress( liteAcctInfo.getAccountSite().getStreetAddressID() );
		
		// Remove all LM hardwares from lmHardwares
		for (int i = 0; i < liteAcctInfo.getInventories().size(); i++) {
			int invID = ((Integer) liteAcctInfo.getInventories().get(i)).intValue();
			ArrayList lmHarewares = getAllLMHardwares();
			
			synchronized (lmHardwares) {
				for (int j = 0; j < lmHardwares.size(); j++) {
					LiteLMHardwareBase liteHw = (LiteLMHardwareBase) lmHardwares.get(j);
					if (liteHw.getInventoryID() == invID) {
						lmHardwares.remove(j);
						break;
					}
				}
			}
		}
		
		// Remove all work orders from workOrders
		for (int i = 0; i < liteAcctInfo.getServiceRequestHistory().size(); i++) {
			int orderID = ((Integer) liteAcctInfo.getServiceRequestHistory().get(i)).intValue();
			ArrayList workOrders = getAllWorkOrders();
			
			synchronized (workOrders) {
				for (int j = 0; j < workOrders.size(); j++) {
					LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) workOrders.get(j);
					if (liteOrder.getOrderID() == orderID) {
						workOrders.remove(j);
						break;
					}
				}
			}
		}
		
		// Remove the customer account from custAccountInfos
		getAllCustAccountInformation().remove( liteAcctInfo );
	}
	
	public void updateCustAccountInformation(LiteStarsCustAccountInformation accountInfo) {
		for (int i = 0; i < accountInfo.getInventories().size(); i++) {
			Integer invID = (Integer) accountInfo.getInventories().get(i);
			LiteLMHardwareBase liteHw = getLMHardware( invID.intValue(), true );
			
			com.cannontech.database.data.stars.event.LMHardwareEvent[] events =
					com.cannontech.database.data.stars.event.LMHardwareEvent.getAllLMHardwareEvents( invID );
			ArrayList hwHist = new ArrayList();
			for (int j = 0; j < events.length; j++) {
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( events[j] );
				hwHist.add( liteEvent );
			}
			
			liteHw.setLmHardwareHistory( hwHist );
		}
		
		for (int i = 0; i < accountInfo.getLmPrograms().size(); i++) {
			LiteStarsLMProgram liteProg = (LiteStarsLMProgram) accountInfo.getLmPrograms().get(i);
                
            com.cannontech.database.data.stars.event.LMProgramEvent[] events =
            		com.cannontech.database.data.stars.event.LMProgramEvent.getAllLMProgramEvents(
            			new Integer(accountInfo.getCustomerAccount().getAccountID()), new Integer(liteProg.getLmProgram().getProgramID()) );
            if (events != null) {
	            ArrayList progHist = new ArrayList();
            	for (int j = 0; j < events.length; j++) {
            		LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( events[j] );
            		progHist.add( liteEvent );
            	}
            	
            	liteProg.setProgramHistory( progHist );
            }
            
            getLMControlHistory( liteProg.getGroupID() );
		}
	}
	
	public LiteStarsCustAccountInformation searchByAccountNumber(String accountNo) {
		ArrayList custAcctInfoList = getAllCustAccountInformation();
		
		LiteStarsCustAccountInformation accountInfo = null;
		for (int i = 0; i < custAcctInfoList.size(); i++) {
			accountInfo = (LiteStarsCustAccountInformation) custAcctInfoList.get(i);
			if (accountInfo.getCustomerAccount().getAccountNumber().equalsIgnoreCase( accountNo ))
				return accountInfo;
		}
		
		com.cannontech.database.data.stars.customer.CustomerAccount account =
				com.cannontech.database.data.stars.customer.CustomerAccount.searchByAccountNumber( getEnergyCompanyID(), accountNo );
		if (account == null) return null;
		
		return addCustAccountInformation( account );
	}
	
	
	/* The following methods are only used when SOAPClient exists locally */
	
	public StarsCustomerSelectionLists getStarsCustomerSelectionLists() {
		if (starsCustSelLists == null)
			starsCustSelLists = StarsLiteFactory.createStarsCustomerSelectionLists( getAllSelectionLists() );
		return starsCustSelLists;
	}
	
	public StarsEnrollmentPrograms getStarsEnrollmentPrograms(String category) {
		if (starsEnrPrograms == null)
			starsEnrPrograms = StarsLiteFactory.createStarsEnrollmentPrograms(
					getAllApplianceCategories(), category, getLiteID() );
		return starsEnrPrograms;
	}
	
	public StarsCustomerFAQs getStarsCustomerFAQs() {
		if (starsCustFAQs == null)
			starsCustFAQs = StarsLiteFactory.createStarsCustomerFAQs( getAllCustomerFAQs() );
		return starsCustFAQs;
	}
	
	private Hashtable getStarsWebConfigs() {
		if (starsWebConfigs == null)
			starsWebConfigs = new Hashtable();
		return starsWebConfigs;
	}
	
	public StarsWebConfig getStarsWebConfig(int webConfigID) {
		Hashtable starsWebConfigs = getStarsWebConfigs();
		synchronized (starsWebConfigs) {
			Integer configID = new Integer(webConfigID);
			StarsWebConfig starsWebConfig = (StarsWebConfig) starsWebConfigs.get( configID );
			if (starsWebConfig == null) {
				LiteWebConfiguration liteWebConfig = SOAPServer.getWebConfiguration( webConfigID );
				starsWebConfig = StarsLiteFactory.createStarsWebConfig( liteWebConfig );
				starsWebConfigs.put( configID, starsWebConfig );
			}
			
			return starsWebConfig;
		}
	}

	private Hashtable getStarsCustAcctInfos() {
		if (starsCustAcctInfos == null)
			starsCustAcctInfos = new Hashtable();
		return starsCustAcctInfos;
	}
	
	public StarsCustAccountInformation getStarsCustAccountInformation(LiteStarsCustAccountInformation liteAcctInfo) {
		Hashtable starsCustAcctInfos = getStarsCustAcctInfos();
		synchronized (starsCustAcctInfos) {
			Integer accountID = new Integer(liteAcctInfo.getAccountID());
			StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation) starsCustAcctInfos.get( accountID );
			if (starsAcctInfo == null) {
				starsAcctInfo = StarsLiteFactory.createStarsCustAccountInformation( liteAcctInfo, getLiteID(), true );
				starsCustAcctInfos.put( accountID, starsAcctInfo );
			}
			
			return starsAcctInfo;
		}
	}
	
	public void updateStarsCustAccountInformation(LiteStarsCustAccountInformation liteAcctInfo) {
		Hashtable starsCustAcctInfos = getStarsCustAcctInfos();
		synchronized (starsCustAcctInfos) {
			Integer accountID = new Integer(liteAcctInfo.getAccountID());
			StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation) starsCustAcctInfos.get( accountID );
			if (starsAcctInfo != null)
				StarsLiteFactory.setStarsCustAccountInformation( starsAcctInfo, liteAcctInfo, getLiteID(), true );
		}
	}
	
	public void deleteStarsCustAccountInformation(int accountID) {
		Hashtable starsCustAcctInfos = getStarsCustAcctInfos();
		synchronized (starsCustAcctInfos) { starsCustAcctInfos.remove( new Integer(accountID) ); }
	}

}
