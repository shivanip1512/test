package com.cannontech.database.data.lite.stars;

import java.util.*;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.db.stars.CustomerListEntry;
import com.cannontech.database.db.stars.CustomerSelectionList;

import com.cannontech.stars.web.servlet.SOAPServer;
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
	
	private String name = null;
	private int routeID = 0;
	
	private ArrayList custAccountInfos = null;
	private ArrayList custContacts = null;
	private ArrayList custAddresses = null;
	private ArrayList lmPrograms = null;
	private ArrayList lmHardwares = null;
	private ArrayList lmCtrlHists = null;
	private ArrayList appCategories = null;
	private ArrayList workOrders = null;
	private ArrayList serviceCompanies = null;
	private Hashtable selectionLists = null;
	private ArrayList interviewQuestions = null;
	private LiteStarsThermostatSettings dftThermSettings = null;
	private Object[][] thermModeSettings = null;
	
	public LiteStarsEnergyCompany() {
		super();
		setLiteType( LiteTypes.STARS_ENERGY_COMPANY );
	}
	
	public LiteStarsEnergyCompany(int companyID) {
		super();
		setLiteID( companyID );
		setLiteType( LiteTypes.STARS_ENERGY_COMPANY );
	}
	
	public LiteStarsEnergyCompany(com.cannontech.database.db.company.EnergyCompany energyCompany) {
		super();
		setLiteType( LiteTypes.STARS_ENERGY_COMPANY );
		setLiteID( energyCompany.getEnergyCompanyID().intValue() );
		setName( energyCompany.getName() );
		setRouteID( energyCompany.getRouteID().intValue() );
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
	
	public void init() {
		getAllLMPrograms();
		getAllApplianceCategories();
		getAllServiceCompanies();
		getAllSelectionLists();
		loadDefaultThermostatSettings();
		getAllInterviewQuestions();
	}
	
	public void clear() {
		custAccountInfos = null;
		custContacts = null;
		custAddresses = null;
		lmPrograms = null;
		lmHardwares = null;
		lmCtrlHists = null;
		appCategories = null;
		workOrders = null;
		serviceCompanies = null;
		selectionLists = null;
		interviewQuestions = null;
		dftThermSettings = null;
		thermModeSettings = null;
	}
    
    public ArrayList getAllLMPrograms() {
    	if (lmPrograms == null) {
    		lmPrograms = new ArrayList();
    		
    		List lmProgList = com.cannontech.database.cache.DefaultDatabaseCache.getInstance().getAllLMPrograms();
    		com.cannontech.database.db.stars.ECToGenericMapping[] items =
    				com.cannontech.database.db.stars.ECToGenericMapping.getAllMappingItems( getEnergyCompanyID(), "LMPrograms" );
    				
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
	    	
	    	CTILogger.info( "All LM programs loaded for energy company #" + getEnergyCompanyID() );
    	}
    	
    	return lmPrograms;
    }
    
    public ArrayList getAllApplianceCategories() {
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

	public Hashtable getAllSelectionLists() {
		if (selectionLists == null) {
	        selectionLists = new Hashtable();
	        
	        // Get all selection lists
	        com.cannontech.database.db.stars.CustomerSelectionList[] selLists =
	        		com.cannontech.database.data.stars.CustomerSelectionList.getAllSelectionLists( getEnergyCompanyID() );
	        
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
	        subList.setListName( com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION );
	        
	        com.cannontech.database.db.stars.Substation[] subs =
	        		com.cannontech.database.db.stars.Substation.getAllSubstations( getEnergyCompanyID() );
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
	        companyList.setListName( com.cannontech.database.db.stars.report.ServiceCompany.LISTNAME_SERVICECOMPANY );
	        
	        com.cannontech.database.db.stars.report.ServiceCompany[] companies =
	        		com.cannontech.database.db.stars.report.ServiceCompany.getAllServiceCompanies( getEnergyCompanyID() );
	        listEntries = new StarsSelectionListEntry[ companies.length ];
	        
	        for (int i = 0; i < companies.length; i++) {
	        	listEntries[i] = new StarsSelectionListEntry();
	        	listEntries[i].setEntryID( companies[i].getCompanyID().intValue() );
	        	listEntries[i].setContent( companies[i].getCompanyName() );
	        }
	        
	        companyList.setListEntries( listEntries );
	        selectionLists.put( companyList.getListName(), companyList );
			
			CTILogger.info( "All customer selection lists loaded for energy company #" + getEnergyCompanyID() );
		}
		
		return selectionLists;
	}
	
	public ArrayList getAllServiceCompanies() {
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
	
	public void loadDefaultThermostatSettings() {
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
		        
		        com.cannontech.database.db.stars.hardware.LMThermostatManualOption option =
		        		com.cannontech.database.db.stars.hardware.LMThermostatManualOption.getLMThermostatManualOption( new Integer(dftInventoryID) );
		        if (option != null)
		        	dftThermSettings.setThermostatOption( (LiteLMThermostatManualOption) StarsLiteFactory.createLite(option) );
			}
			
			CTILogger.info( "Default thermostat settings loaded for energy company #" + getEnergyCompanyID() );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		if (dftThermSettings.getThermostatSeasons() == null)
			CTILogger.info( "No default thermostat schedules found for energy company #" + getEnergyCompanyID() );
		if (dftThermSettings.getThermostatOption() == null)
			CTILogger.info( "No default thermostat option found for energy company #" + getEnergyCompanyID() );
	}
	
	public LiteStarsThermostatSettings getDefaultThermostatSettings() {
		if (dftThermSettings == null)
			loadDefaultThermostatSettings();
		return dftThermSettings;
	}
	
	public StarsThermoModeSettings getThermModeSetting(int configID) {
		if (thermModeSettings == null)
			loadDefaultThermostatSettings();
		if (thermModeSettings == null) return null;
		
		for (int i = 0; i < thermModeSettings.length; i++) {
			if (((Integer) thermModeSettings[i][0]).intValue() == configID)
				return (StarsThermoModeSettings) thermModeSettings[i][1];
		}
		return null;
	}
	
	public Integer getThermSeasonWebConfigID(StarsThermoModeSettings setting) {
		if (thermModeSettings == null)
			loadDefaultThermostatSettings();
		if (thermModeSettings == null) return null;
		
		for (int i = 0; i < thermModeSettings.length; i++) {
			if (((StarsThermoModeSettings) thermModeSettings[i][1]).getType() == setting.getType())
				return (Integer) thermModeSettings[i][0];
		}
		return null;
	}
	
	public ArrayList getAllInterviewQuestions() {
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
	
	public ArrayList getAllCustomerContacts() {
		if (custContacts == null)
			custContacts = new ArrayList();
		
		return custContacts;
	}
	
	public ArrayList getAllCustomerAddresses() {
		if (custAddresses == null)
			custAddresses = new ArrayList();
		
		return custAddresses;
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
		
		for (int i = 0; i < questions.size(); i++) {
			LiteInterviewQuestion liteQ = (LiteInterviewQuestion) questions.get(i);
			if (liteQ.getQuestionType() == qType)
				qList.add( liteQ );
		}
		
		LiteInterviewQuestion[] qs = new LiteInterviewQuestion[ qList.size() ];
		qList.toArray( qs );
		
		return qs;
	}

	
	public LiteApplianceCategory getApplianceCategory(int applianceCategoryID) {
		ArrayList appCats = getAllApplianceCategories();
		
		LiteApplianceCategory appCat = null;
		for (int i = 0; i < appCats.size(); i++) {
			appCat = (LiteApplianceCategory) appCats.get(i);
			if (appCat.getApplianceCategoryID() == applianceCategoryID)
				return appCat;
		}
		
		return null;
	}
	
	public LiteServiceCompany getServiceCompany(int serviceCompanyID) {
		ArrayList serviceCompanies = getAllServiceCompanies();
		
		LiteServiceCompany serviceCompany = null;
		for (int i = 0; i < serviceCompanies.size(); i++) {
			serviceCompany = (LiteServiceCompany) serviceCompanies.get(i);
			if (serviceCompany.getCompanyID() == serviceCompanyID)
				return serviceCompany;
		}
		
		return null;
	}
	
	public LiteCustomerContact getCustomerContact(int contactID) {
		ArrayList custContactList = getAllCustomerContacts();
		
		LiteCustomerContact liteContact = null;
		for (int i = 0; i < custContactList.size(); i++) {
			liteContact = (LiteCustomerContact) custContactList.get(i);
			if (liteContact.getContactID() == contactID)
				return liteContact;
		}
		
		try {
			com.cannontech.database.db.customer.CustomerContact contact = new com.cannontech.database.db.customer.CustomerContact();
			contact.setContactID( new Integer(contactID) );
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
	
	public void addCustomerContact(LiteCustomerContact liteContact) {
		ArrayList custContactList = getAllCustomerContacts();
		custContactList.add( liteContact );
	}
	
	public void deleteCustomerContact(LiteCustomerContact liteContact) {
		ArrayList custContactList = getAllCustomerContacts();
		custContactList.remove( liteContact );
	}
	
	public LiteCustomerAddress getCustomerAddress(int addressID) {
		ArrayList custAddressList = getAllCustomerAddresses();
		
		LiteCustomerAddress liteAddr = null;
		for (int i = 0; i < custAddressList.size(); i++) {
			liteAddr = (LiteCustomerAddress) custAddressList.get(i);
			if (liteAddr.getAddressID() == addressID)
				return liteAddr;
		}
		
		try {
			com.cannontech.database.db.customer.CustomerAddress addr = new com.cannontech.database.db.customer.CustomerAddress();
			addr.setAddressID( new Integer(addressID) );
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
	
	public LiteCustomerAddress addCustomerAddress(com.cannontech.database.db.customer.CustomerAddress addr) {
		ArrayList custAddressList = getAllCustomerAddresses();
		
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
	
	public LiteLMProgram getLMProgram(int programID) {
		ArrayList lmProgramList = getAllLMPrograms();
		
		LiteLMProgram liteProg = null;
		for (int i = 0; i < lmProgramList.size(); i++) {
			liteProg = (LiteLMProgram) lmProgramList.get(i);
			if (liteProg.getProgramID() == programID)
				break;
		}
		
		return liteProg;
	}
	
	public LiteLMHardwareBase getLMHardware(int inventoryID, boolean autoLoad) {
		ArrayList lmHardwareList = getAllLMHardwares();
		
		LiteLMHardwareBase liteHw = null;
		for (int i = 0; i < lmHardwareList.size(); i++) {
			liteHw = (LiteLMHardwareBase) lmHardwareList.get(i);
			if (liteHw.getInventoryID() == inventoryID)
				return liteHw;
		}
		
		if (autoLoad) {
			try {
				com.cannontech.database.data.stars.hardware.LMHardwareBase hw = new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				hw.setInventoryID( new Integer(inventoryID) );
				hw = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
						Transaction.createTransaction( Transaction.RETRIEVE, hw ).execute();
				liteHw = (LiteLMHardwareBase) StarsLiteFactory.createLite( hw );
				
				lmHardwareList.add( liteHw );
				return liteHw;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public LiteLMHardwareBase addLMHardware(com.cannontech.database.data.stars.hardware.LMHardwareBase hw) {
		ArrayList lmHardwareList = getAllLMHardwares();
		
		try {
			LiteLMHardwareBase liteHw = (LiteLMHardwareBase) StarsLiteFactory.createLite( hw );
			lmHardwareList.add( liteHw );
			return liteHw;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public LiteStarsLMControlHistory getLMControlHistory(int groupID) {
		ArrayList lmCtrlHistList = getAllLMControlHistory();
		
		LiteStarsLMControlHistory lmCtrlHist = null;
		for (int i = 0; i < lmCtrlHistList.size(); i++) {
			lmCtrlHist = (LiteStarsLMControlHistory) lmCtrlHistList.get(i);
			if (lmCtrlHist.getGroupID() == groupID) {
				SOAPServer.updateLMControlHistory( lmCtrlHist );
				lmCtrlHist.updateStartIndices();
				return lmCtrlHist;
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
		
		lmCtrlHistList.add( lmCtrlHist );
		return lmCtrlHist;
	}
	
	public LiteWorkOrderBase getWorkOrderBase(int orderID) {
        ArrayList workOrders = getAllWorkOrders();
        
        LiteWorkOrderBase workOrder = null;
        for (int i = 0; i < workOrders.size(); i++) {
        	workOrder = (LiteWorkOrderBase) workOrders.get(i);
        	if (workOrder.getOrderID() == orderID)
        		return workOrder;
        }
        
        try {
        	com.cannontech.database.db.stars.report.WorkOrderBase order = new com.cannontech.database.db.stars.report.WorkOrderBase();
        	order.setOrderID( new Integer(orderID) );
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
	
	public LiteWorkOrderBase addWorkOrderBase(com.cannontech.database.db.stars.report.WorkOrderBase order) {
        ArrayList workOrders = getAllWorkOrders();
        
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
        
        com.cannontech.database.db.stars.hardware.LMThermostatManualOption option =
        		com.cannontech.database.db.stars.hardware.LMThermostatManualOption.getLMThermostatManualOption( new Integer(inventoryID) );
        if (option != null)
        	settings.setThermostatOption( (LiteLMThermostatManualOption) StarsLiteFactory.createLite(option) );
        
        return settings;
	}
	
	public LiteLMThermostatManualOption getThermostatOption(int inventoryID) {
        com.cannontech.database.db.stars.hardware.LMThermostatManualOption option =
        		com.cannontech.database.db.stars.hardware.LMThermostatManualOption.getLMThermostatManualOption( new Integer(inventoryID) );
        if (option != null) return null;
        
		return (LiteLMThermostatManualOption) StarsLiteFactory.createLite( option );
	}
	
	public LiteStarsCustAccountInformation getCustAccountInformation(int accountID, boolean autoLoad) {
		ArrayList custAcctInfoList = getAllCustAccountInformation();
		
		LiteStarsCustAccountInformation accountInfo = null;
		for (int i = 0; i < custAcctInfoList.size(); i++) {
			accountInfo = (LiteStarsCustAccountInformation) custAcctInfoList.get(i);
			if (accountInfo.getCustomerAccount().getAccountID() == accountID) {
				updateCustAccountInformation( accountInfo );
				return accountInfo;
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
            addCustomerAddress( streetAddr );

            com.cannontech.database.db.customer.CustomerAddress billAddr = account.getBillingAddress();
            addCustomerAddress( billAddr );

            com.cannontech.database.db.customer.CustomerContact primContact = customer.getPrimaryContact();
			LiteCustomerContact litePrimContact = (LiteCustomerContact) StarsLiteFactory.createLite( primContact );
            addCustomerContact( litePrimContact );

            Vector contactList = customer.getCustomerContactVector();
            for (int i = 0; i < contactList.size(); i++) {
                com.cannontech.database.db.customer.CustomerContact contact = (com.cannontech.database.db.customer.CustomerContact) contactList.elementAt(i);
				LiteCustomerContact liteContact = (LiteCustomerContact) StarsLiteFactory.createLite( contact );
                addCustomerContact( liteContact );
            }
			
            Vector applianceVector = account.getApplianceVector();            
            accountInfo.setAppliances( new ArrayList() );

            for (int i = 0; i < applianceVector.size(); i++) {
                com.cannontech.database.data.stars.appliance.ApplianceBase appliance =
                            (com.cannontech.database.data.stars.appliance.ApplianceBase) applianceVector.elementAt(i);
                accountInfo.getAppliances().add( StarsLiteFactory.createStarsAppliance(appliance, getLiteID()) );
            }

            Vector inventoryVector = account.getInventoryVector();
            accountInfo.setInventories( new ArrayList() );

			Hashtable selectionLists = getAllSelectionLists();
			int lmHardwareCatID = StarsCustListEntryFactory.getStarsCustListEntry(
				(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_INVENTORYCATEGORY),
				CustomerListEntry.YUKONDEF_INVCAT_ONEWAYREC ).getEntryID();
			int thermostatTypeID = StarsCustListEntryFactory.getStarsCustListEntry(
				(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_DEVICETYPE),
				CustomerListEntry.YUKONDEF_DEVTYPE_THERMOSTAT ).getEntryID();

            for (int i = 0; i < inventoryVector.size(); i++) {
                com.cannontech.database.db.stars.hardware.InventoryBase inventory =
                		(com.cannontech.database.db.stars.hardware.InventoryBase) inventoryVector.elementAt(i);

                if (inventory.getCategoryID().intValue() == lmHardwareCatID) {
                    // This is a LM hardware
                    com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
							new com.cannontech.database.data.stars.hardware.LMHardwareBase();
					hardware.setInventoryID( inventory.getInventoryID() );
					hardware = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
							Transaction.createTransaction( Transaction.RETRIEVE, hardware ).execute();
							
                    addLMHardware( hardware );
                    accountInfo.getInventories().add( inventory.getInventoryID() );

                	// And it is a thermostat
                	if (hardware.getLMHardwareBase().getLMHardwareTypeID().intValue() == thermostatTypeID)
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
            
			StarsCallReport[] calls = StarsCallReportFactory.getStarsCallReports( getEnergyCompanyID(), accountDB.getAccountID() );
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
	        		addWorkOrderBase( orders[i] );
	        		accountInfo.getServiceRequestHistory().add( orders[i].getOrderID() );
	        	}
	        }
	        
	        if (primContact.getLogInID().intValue() >= 0) {
		        com.cannontech.database.db.customer.CustomerLogin login = com.cannontech.database.db.customer.CustomerLogin.getCustomerLogin( primContact.getLogInID() );
		        accountInfo.setYukonUser( (com.cannontech.database.data.lite.LiteYukonUser) StarsLiteFactory.createLite(login) );
	        }

            custAcctInfoList.add( accountInfo );
            return accountInfo;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
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
            			new Integer(accountInfo.getCustomerAccount().getAccountID()), new Integer(liteProg.getProgramID()) );
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

}
