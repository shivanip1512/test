package com.cannontech.stars.web.servlet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.messaging.JAXMServlet;
import javax.xml.messaging.ReqRespListener;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteWebConfiguration;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsEnergyCompany;
import com.cannontech.stars.xml.serialize.StarsEnrLMProgram;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMProgram;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;
import com.cannontech.yc.gui.YC;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class SOAPServer extends JAXMServlet implements ReqRespListener, com.cannontech.database.cache.DBChangeListener {
	
	public static final int DEFAULT_ENERGY_COMPANY_ID = -1;
	
	private static boolean clientLocal = true;
    
    // Instance of the SOAPServer object
    private static SOAPServer instance = null;
	
	// YC object used for sending command to porter
	private static com.cannontech.yc.gui.YC yc = null;
	
	// Array of all the energy companies (LiteStarsEnergyCompany)
	private static ArrayList energyCompanies = null;
    
	// List of web configurations (LiteWebConfiguration)
    private static ArrayList webConfigList = null;
	
	// Map from user ID (Integer) to stars users (StarsYukonUser)
	private static Hashtable starsYukonUsers = null;
	
	private com.cannontech.message.dispatch.ClientConnection connToDispatch;
    

	public SOAPServer() {
		super();
	}

	/*
	 * Implementation of ReqRespListener interface
	 */
	public void init() throws javax.servlet.ServletException {
		instance = this;
		initSOAPServer( instance );		
	}

	public SOAPMessage onMessage(SOAPMessage message) {
		StarsOperation respOper = new StarsOperation();

		try {
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( message );
			if (reqOper == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_NODE_NOT_FOUND, "Invalid request format") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
        	
			StarsSuccess success = new StarsSuccess();
			success.setDescription( "Thanks for waking me up :)" );
			respOper.setStarsSuccess( success );
            
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_RUNTIME_ERROR, "Server error: cannot process request") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			catch (Exception e2) {
				CTILogger.error( e2.getMessage(), e2 );
			}
		}

		return null;
	}
	
	private void initDispatchConnection() {
		String host = RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_MACHINE );

		int port = Integer.parseInt(
						RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_PORT ) );
		
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
		
		try {
			connToDispatch.connectWithoutWait();
		}
		catch ( Exception e ) {
			CTILogger.error( e.getMessage(), e );
		}
	}
    
	public com.cannontech.message.util.ClientConnection getClientConnection() {
		return connToDispatch;
	}
	

	private static void initSOAPServer(SOAPServer instance) {
		instance.initDispatchConnection();
		
		com.cannontech.database.cache.DefaultDatabaseCache.getInstance().addDBChangeListener(instance);	
		
		getAllEnergyCompanies();
		getAllWebConfigurations();
	}
    
	public static SOAPServer getInstance() {
		if (instance == null) {
			instance = new SOAPServer();
			initSOAPServer(instance);
		}
		
		return instance;
	}
    
    public static void refreshCache() {
    	synchronized (energyCompanies) {
			if (energyCompanies != null) {
				for (int i = 0; i < energyCompanies.size(); i++)
					((LiteStarsEnergyCompany) energyCompanies.get(i)).clear();
			}
			energyCompanies = null;
    	}
    	
		webConfigList = null;
		starsYukonUsers = null;
    	
    	com.cannontech.database.cache.DefaultDatabaseCache.getInstance().releaseAllCache();
    	com.cannontech.database.cache.functions.YukonListFuncs.releaseAllConstants();
    }
    
    public static void refreshCache(LiteStarsEnergyCompany company) {
    	company.clear();
    	webConfigList = null;
    	
    	// Send DB change messages to all yukon users, so existing logins will be invalidated
		synchronized (starsYukonUsers) {
			Iterator it = starsYukonUsers.values().iterator();
			while (it.hasNext()) {
				StarsYukonUser user = (StarsYukonUser) it.next();
				if (user.getEnergyCompanyID() == company.getLiteID()) {
					ServerUtils.handleDBChange( user.getYukonUser(), DBChangeMsg.CHANGE_TYPE_UPDATE );
					it.remove();
				}
			}
		}
		
		com.cannontech.database.cache.functions.YukonListFuncs.releaseAllConstants();
    }

	public static boolean isClientLocal() {
		return clientLocal;
	}

	public static void setClientLocal(boolean clientLocal) {
		SOAPServer.clientLocal = clientLocal;
	}
    
	public synchronized static YC getYC() {
		if (yc == null) {
			yc = new YC();
			yc.addObserver( new java.util.Observer() {
				public void update(java.util.Observable o, Object arg) {
					if (arg instanceof String) {
						CTILogger.info( (String)arg );
					}
					else {
						CTILogger.info( ((YC)o).getResultText() );
						((YC)o).clearResultText();
					}
				}
			});
		}
		
		return yc;
	}
    
    /*
     * Start implementation of class functions
     */
    public synchronized static ArrayList getAllEnergyCompanies() {
    	if (energyCompanies == null) {
    		energyCompanies = new ArrayList();
	    	java.sql.Connection conn = null;
	    	
    		try {
	    		conn = com.cannontech.database.PoolManager.getInstance().getConnection(
	    				com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		    	com.cannontech.database.db.company.EnergyCompany[] companies =
		    			com.cannontech.database.db.company.EnergyCompany.getEnergyCompanies( conn );
		    	if (companies == null) return null;
		    	
		    	synchronized (energyCompanies) {
			    	for (int i = 0; i < companies.length; i++)
			    		energyCompanies.add( new LiteStarsEnergyCompany(companies[i]) );
			    }
    		}
    		catch (Exception e) {
				CTILogger.error( e.getMessage(), e );
    		}
    		finally {
    			try {
    				if (conn != null) conn.close();
    			}
    			catch (Exception e2) {
    				CTILogger.error( e2.getMessage(), e2 );
    			}
    		}
	    	
	    	CTILogger.info( "All energy companies loaded" );
    	}
    	
    	return energyCompanies;
    }
    
    public synchronized static ArrayList getAllWebConfigurations() {
    	if (webConfigList == null) {
    		webConfigList = new ArrayList();
    		
    		com.cannontech.database.db.web.YukonWebConfiguration[] webConfigs =
    				com.cannontech.database.db.web.YukonWebConfiguration.getAllCustomerWebConfigurations();
    		for (int i = 0; i < webConfigs.length; i++)
    			webConfigList.add( (LiteWebConfiguration) StarsLiteFactory.createLite(webConfigs[i]) );
	    	
	    	CTILogger.info( "All customer web configurations loaded" );
    	}
    	
    	return webConfigList;
    }
    
	public static Hashtable getAllStarsYukonUsers() {
		if (starsYukonUsers == null)
			starsYukonUsers = new Hashtable();
		return starsYukonUsers;
    }
    
    public static LiteStarsEnergyCompany getEnergyCompany(int energyCompanyID) {
    	ArrayList companies = getAllEnergyCompanies();
		synchronized (companies) {
    		for (int i = 0; i < companies.size(); i++) {
	    		LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) companies.get(i);
    			if (company.getEnergyCompanyID().intValue() == energyCompanyID)
					return company;
    		}
		}
    	
    	return null;
    }
    
    public static void addEnergyCompany(LiteStarsEnergyCompany company) {
    	ArrayList companies = getAllEnergyCompanies();
		synchronized (companies) { companies.add(company); }
    }
    
    public static LiteStarsEnergyCompany deleteEnergyCompany(int energyCompanyID) {
    	ArrayList companies = getAllEnergyCompanies();
		synchronized (companies) {
    		for (int i = 0; i < companies.size(); i++) {
	    		LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) companies.get(i);
    			if (company.getEnergyCompanyID().intValue() == energyCompanyID) {
    				companies.remove( i );
    				return company;
    			}
    		}
		}
    	
    	return null;
    }
    
    public static LiteStarsEnergyCompany getDefaultEnergyCompany() {
    	return getEnergyCompany( DEFAULT_ENERGY_COMPANY_ID );
    }
	
	public static LiteWebConfiguration getWebConfiguration(int configID) {
		ArrayList webConfigList = getAllWebConfigurations();
		synchronized (webConfigList) {
			for (int i = 0; i < webConfigList.size(); i++) {
				LiteWebConfiguration config = (LiteWebConfiguration) webConfigList.get(i);
				if (config.getConfigID() == configID)
					return config;
			}
		}
		
		return null;
	}
	
	public static void addWebConfiguration(LiteWebConfiguration config) {
		ArrayList webConfigList = getAllWebConfigurations();
		synchronized (webConfigList) { webConfigList.add(config); }
	}
	
	public static LiteWebConfiguration deleteWebConfiguration(int configID) {
		ArrayList webConfigList = getAllWebConfigurations();
		synchronized (webConfigList) {
			for (int i = 0; i < webConfigList.size(); i++) {
				LiteWebConfiguration config = (LiteWebConfiguration) webConfigList.get(i);
				if (config.getConfigID() == configID) {
					webConfigList.remove( i );
					return config;
				}
			}
		}
		
		return null;
	}
	
	public static StarsYukonUser getStarsYukonUser(LiteYukonUser yukonUser) {
		Hashtable starsUsers = getAllStarsYukonUsers();
		Integer userID = new Integer( yukonUser.getUserID() );
		
		synchronized (starsUsers) {
			StarsYukonUser user = (StarsYukonUser) starsUsers.get( userID );
			if (user != null) {
				if (user.getYukonUser() == yukonUser)
					return user;
			}
		}
		
		try {
			StarsYukonUser user = StarsYukonUser.newInstance( yukonUser );
			synchronized (starsUsers) { starsUsers.put( userID, user ); }
			return user;
		}
		catch (InstantiationException ie) {
			CTILogger.error( ie.getMessage(), ie );
		}
		
		return null;
	}
	
	public static void deleteStarsYukonUser(int userID) {
		Hashtable starsUsers = getAllStarsYukonUsers();
		synchronized (starsUsers) { starsUsers.remove( new Integer(userID) ); }
	}
	
		
	public void handleDBChangeMsg(DBChangeMsg msg, LiteBase lBase)
	{
		if (msg.getSource().equals(com.cannontech.common.util.CtiUtilities.DEFAULT_MSG_SOURCE))
			return;
		
		CTILogger.debug(" ## DBChangeMsg ##\n" + msg);
		
		ArrayList companies = getAllEnergyCompanies();
		
		if (msg.getDatabase() == DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB) {
			for (int i = 0; i < companies.size(); i++) {
				LiteStarsEnergyCompany energyCompany = (LiteStarsEnergyCompany) companies.get(i);
				
				LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation( msg.getId(), false );
				if (liteAcctInfo != null) {
					handleCustomerAccountChange( msg, energyCompany, liteAcctInfo );
					return;
				}
			}
		}
		else if (msg.getDatabase() == DBChangeMsg.CHANGE_CONTACT_DB) {
			LiteContact liteContact = ContactFuncs.getContact( msg.getId() );
			
			for (int i = 0; i < companies.size(); i++) {
				LiteStarsEnergyCompany energyCompany = (LiteStarsEnergyCompany) companies.get(i);
				LiteBase contOwner = null;
				
				if (energyCompany.getPrimaryContactID() == msg.getId()) {
					contOwner = energyCompany;
				}
				else {
					ArrayList servCompanies = energyCompany.getAllServiceCompanies();
					for (int j = 0; j < servCompanies.size(); j++) {
						LiteServiceCompany servCompany = (LiteServiceCompany) servCompanies.get(j);
						if (servCompany.getPrimaryContactID() == msg.getId()) {
							contOwner = servCompany;
							break;
						}
					}
					
					if (contOwner == null)
						contOwner = energyCompany.getCustAccountInfoByContact( msg.getId() );
				}
				
				if (contOwner != null) {
					handleContactChange( msg, energyCompany, contOwner );
					return;
				}
			}
		}
		else if (msg.getDatabase() == DBChangeMsg.CHANGE_PAO_DB) {
			LiteYukonPAObject litePao = PAOFuncs.getLiteYukonPAO( msg.getId() );
			
			for (int i = 0; i < companies.size(); i++) {
				LiteStarsEnergyCompany energyCompany = (LiteStarsEnergyCompany) companies.get(i);
				
				if (litePao.getCategory() == PAOGroups.CAT_ROUTE) {
					if (!Boolean.valueOf(energyCompany.getEnergyCompanySetting( EnergyCompanyRole.SINGLE_ENERGY_COMPANY )).booleanValue())
						handleRouteChange( msg, energyCompany );
				}
				else if (DeviceTypesFuncs.isLMProgramDirect( litePao.getType() )) {
					ArrayList programs = energyCompany.getAllPrograms();
					for (int j = 0; j < programs.size(); j++) {
						LiteLMProgramWebPublishing liteProg = (LiteLMProgramWebPublishing) programs.get(j);
						if (liteProg.getDeviceID() == msg.getId()) {
							handleLMProgramChange( msg, energyCompany, liteProg );
							return;
						}
					}
				}
				else if (DeviceTypesFuncs.isLmGroup( litePao.getType() )) {
					ArrayList programs = energyCompany.getAllPrograms();
					StarsEnrollmentPrograms categories = energyCompany.getStarsEnrollmentPrograms();
					
					for (int j = 0; j < programs.size(); j++) {
						LiteLMProgramWebPublishing liteProg = (LiteLMProgramWebPublishing) programs.get(j);
						boolean groupFound = false;
						
						for (int k = 0; k < liteProg.getGroupIDs().length; k++) {
							if (liteProg.getGroupIDs()[k] == msg.getId()) {
								handleLMGroupChange( msg, energyCompany, liteProg );
								groupFound = true;
								break;
							}
						}
						
						if (!groupFound) {
							// Program could contain a macro group, while a LM group in that macro group is changed
							StarsEnrLMProgram starsProg = ServletUtils.getEnrollmentProgram( categories, liteProg.getProgramID() );
							for (int k = 0; k < starsProg.getAddressingGroupCount(); k++) {
								if (starsProg.getAddressingGroup(k).getEntryID() == msg.getId()) {
									handleLMGroupChange( msg, energyCompany, liteProg );
									break;
								}
							}
						}
					}
				}
				else if (DeviceTypesFuncs.isMCT( litePao.getType() )) {
					ArrayList inventory = energyCompany.getAllInventory();
					for (int j = 0; j < inventory.size(); j++) {
						LiteInventoryBase liteInv = (LiteInventoryBase) inventory.get(j);
						if (liteInv.getDeviceID() == msg.getId()) {
							handleDeviceChange( msg, energyCompany, liteInv );
							return;
						}
					}
				}
			}
		}
		else if (msg.getDatabase() == DBChangeMsg.CHANGE_YUKON_USER_DB) {
			if (msg.getCategory().equals( DBChangeMsg.CAT_YUKON_USER )) {
				LiteContact liteContact = YukonUserFuncs.getLiteContact( msg.getId() );
				if (liteContact != null) {
					for (int i = 0; i < companies.size(); i++) {
						LiteStarsEnergyCompany energyCompany = (LiteStarsEnergyCompany) companies.get(i);
						LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInfoByContact( liteContact.getContactID() );
						if (liteAcctInfo != null) {
							handleYukonUserChange( msg, energyCompany, liteAcctInfo );
							return;
						}
					}
				}
			}
		}
	}
	
	private void handleCustomerAccountChange( DBChangeMsg msg, LiteStarsEnergyCompany energyCompany, LiteStarsCustAccountInformation liteAcctInfo )
	{
		switch( msg.getTypeOfChange() )
		{
			case DBChangeMsg.CHANGE_TYPE_ADD:
				// Don't need to do anything, since customer account information is load-on-demand
				break;
				
			case DBChangeMsg.CHANGE_TYPE_UPDATE:
				energyCompany.getAddress( liteAcctInfo.getCustomerAccount().getBillingAddressID() ).retrieve();
				liteAcctInfo.getCustomerAccount().retrieve();
				
				LiteContact litePrimContact = energyCompany.getContact( liteAcctInfo.getCustomer().getPrimaryContactID(), liteAcctInfo );
				litePrimContact.retrieve( CtiUtilities.getDatabaseAlias() );;
				
				Vector contacts = liteAcctInfo.getCustomer().getAdditionalContacts();
				for (int i = 0; i < contacts.size(); i++)
					energyCompany.deleteContact( ((LiteContact)contacts.get(i)).getContactID() );
				
				liteAcctInfo.getCustomer().retrieve( CtiUtilities.getDatabaseAlias() );
				contacts = liteAcctInfo.getCustomer().getAdditionalContacts();
				for (int i = 0; i < contacts.size(); i++)
					energyCompany.addContact( (LiteContact)contacts.get(i), liteAcctInfo );
				
				energyCompany.getAddress( liteAcctInfo.getAccountSite().getStreetAddressID() ).retrieve();
				liteAcctInfo.getAccountSite().retrieve();
				
				liteAcctInfo.getSiteInformation().retrieve();
				
				energyCompany.updateStarsCustAccountInformation( liteAcctInfo );
				break;
				
			case DBChangeMsg.CHANGE_TYPE_DELETE:
				energyCompany.deleteCustAccountInformation( liteAcctInfo );
				energyCompany.deleteStarsCustAccountInformation( liteAcctInfo.getAccountID() );
				break;
		}
	}
	
	private void handleContactChange(DBChangeMsg msg, LiteStarsEnergyCompany energyCompany, LiteBase contOwner) {
		switch (msg.getTypeOfChange()) {
			case DBChangeMsg.CHANGE_TYPE_ADD :
				// Don't need to do anything
				break;
				
			case DBChangeMsg.CHANGE_TYPE_UPDATE :
				LiteContact liteContact = ContactFuncs.getContact( msg.getId() );
				
				if (contOwner instanceof LiteStarsEnergyCompany) {
					StarsEnergyCompany starsEC = energyCompany.getStarsEnergyCompany();
					StarsLiteFactory.setStarsEnergyCompany( starsEC, energyCompany );
				}
				else if (contOwner instanceof LiteServiceCompany) {
					for (int i = 0; i < energyCompany.getStarsServiceCompanies().getStarsServiceCompanyCount(); i++) {
						StarsServiceCompany starsSC = energyCompany.getStarsServiceCompanies().getStarsServiceCompany(i);
						if (starsSC.getPrimaryContact().getContactID() == liteContact.getContactID()) {
							StarsLiteFactory.setStarsCustomerContact( starsSC.getPrimaryContact(), liteContact );
							break;
						}
					}
				}
				else {
					LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) contOwner;
					
					if (liteAcctInfo.getCustomer().getPrimaryContactID() == liteContact.getContactID()) {
						// Do nothing
					}
					else {
						Vector contacts = liteAcctInfo.getCustomer().getAdditionalContacts();
						for (int i = 0; i < contacts.size(); i++) {
							if (((LiteContact)contacts.get(i)).getContactID() == liteContact.getContactID()) {
								contacts.set(i, liteContact);
								break;
							}
						}
					}
					
					StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteAcctInfo.getAccountID() );
					if (starsAcctInfo != null) {
						if (starsAcctInfo.getStarsCustomerAccount().getPrimaryContact().getContactID() == liteContact.getContactID()) {
							StarsLiteFactory.setStarsCustomerContact(starsAcctInfo.getStarsCustomerAccount().getPrimaryContact(), liteContact);
						}
						else {
							for (int i = 0; i < starsAcctInfo.getStarsCustomerAccount().getAdditionalContactCount(); i++) {
								if (starsAcctInfo.getStarsCustomerAccount().getAdditionalContact(i).getContactID() == liteContact.getContactID()) {
									StarsLiteFactory.setStarsCustomerContact(starsAcctInfo.getStarsCustomerAccount().getAdditionalContact(i), liteContact);
									break;
								}
							}
						}
					}
				}
				
				break;
				
			case DBChangeMsg.CHANGE_TYPE_DELETE :
				energyCompany.deleteContact( msg.getId() );
				
				if (contOwner instanceof LiteStarsCustAccountInformation) {
					LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) contOwner;
					
					Vector contacts = liteAcctInfo.getCustomer().getAdditionalContacts();
					for (int i = 0; i < contacts.size(); i++) {
						if (((LiteContact)contacts.get(i)).getContactID() == msg.getId()) {
							contacts.remove(i);
							break;
						}
					}
					
					StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteAcctInfo.getAccountID() );
					if (starsAcctInfo != null) {
						for (int i = 0; i < starsAcctInfo.getStarsCustomerAccount().getAdditionalContactCount(); i++) {
							if (starsAcctInfo.getStarsCustomerAccount().getAdditionalContact(i).getContactID() == msg.getId()) {
								starsAcctInfo.getStarsCustomerAccount().removeAdditionalContact(i);
								break;
							}
						}
					}
				}
				
				break;
		}
	}
	
	private void handleLMProgramChange(DBChangeMsg msg, LiteStarsEnergyCompany energyCompany, LiteLMProgramWebPublishing liteProg) {
		switch( msg.getTypeOfChange() )
		{
			case DBChangeMsg.CHANGE_TYPE_ADD:
				// Don't need to do anything
				break;
				
			case DBChangeMsg.CHANGE_TYPE_UPDATE :
				try {
					// Update group list of the LM program
					com.cannontech.database.db.device.lm.LMProgramDirectGroup[] groups =
							com.cannontech.database.db.device.lm.LMProgramDirectGroup.getAllDirectGroups( new Integer(liteProg.getDeviceID()) );
					
					int[] groupIDs = new int[ groups.length ];
					for (int i = 0; i < groups.length; i++)
						groupIDs[i] = groups[i].getLmGroupDeviceID().intValue();
					liteProg.setGroupIDs( groupIDs );
					
					StarsEnrLMProgram program = ServletUtils.getEnrollmentProgram( energyCompany.getStarsEnrollmentPrograms(), liteProg.getProgramID() );
					if (program != null) {
						program.setYukonName( PAOFuncs.getYukonPAOName(liteProg.getDeviceID()) );
						StarsLiteFactory.setAddressingGroups( program, liteProg );
					}
				}
				catch (SQLException e) {
					CTILogger.error( e.getMessage(), e );
				}
				
				String newProgName = ECUtils.getPublishedProgramName( liteProg );
				
				ArrayList accounts = energyCompany.getActiveAccounts();
				for (int i = 0; i < accounts.size(); i++) {
					StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation) accounts.get(i);
					for (int j = 0; j < starsAcctInfo.getStarsLMPrograms().getStarsLMProgramCount(); j++) {
						StarsLMProgram program = starsAcctInfo.getStarsLMPrograms().getStarsLMProgram(j);
						if (program.getProgramID() == liteProg.getProgramID()) {
							program.setProgramName( newProgName );
							break;
						}
					}
				}
				
				break;
				
			case DBChangeMsg.CHANGE_TYPE_DELETE :
				// Don't need to do anything
				break;
		}
	}
	
	private void handleLMGroupChange(DBChangeMsg msg, LiteStarsEnergyCompany energyCompany, LiteLMProgramWebPublishing liteProg) {
		switch( msg.getTypeOfChange() )
		{
			case DBChangeMsg.CHANGE_TYPE_ADD:
				// Don't need to do anything
				break;
				
			case DBChangeMsg.CHANGE_TYPE_UPDATE :
			case DBChangeMsg.CHANGE_TYPE_DELETE :
				try {
					// Update group list of the LM program
					com.cannontech.database.db.device.lm.LMProgramDirectGroup[] groups =
							com.cannontech.database.db.device.lm.LMProgramDirectGroup.getAllDirectGroups( new Integer(liteProg.getDeviceID()) );
					
					int[] groupIDs = new int[ groups.length ];
					for (int i = 0; i < groups.length; i++)
						groupIDs[i] = groups[i].getLmGroupDeviceID().intValue();
					liteProg.setGroupIDs( groupIDs );
					
					StarsEnrLMProgram program = ServletUtils.getEnrollmentProgram( energyCompany.getStarsEnrollmentPrograms(), liteProg.getProgramID() );
					if (program != null)
						StarsLiteFactory.setAddressingGroups( program, liteProg );
				}
				catch (SQLException e) {
					CTILogger.error( e.getMessage(), e );
				}
				
				break;
		}
	}
	
	private void handleDeviceChange(DBChangeMsg msg, LiteStarsEnergyCompany energyCompany, LiteInventoryBase liteInv) {
		switch( msg.getTypeOfChange() )
		{
			case DBChangeMsg.CHANGE_TYPE_ADD:
				// Don't need to do anything
				break;
				
			case DBChangeMsg.CHANGE_TYPE_UPDATE :
				StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteInv.getAccountID() );
				if (starsAcctInfo != null) {
					for (int i = 0; i < starsAcctInfo.getStarsInventories().getStarsInventoryCount(); i++) {
						StarsInventory starsInv = starsAcctInfo.getStarsInventories().getStarsInventory(i);
						if (starsInv.getDeviceID() == msg.getId()) {
							starsInv.getMCT().setDeviceName( PAOFuncs.getYukonPAOName(msg.getId()) );
							break;
						}
					}
				}
				
				break;
				
			case DBChangeMsg.CHANGE_TYPE_DELETE :
				// Don't need to do anything
				break;
		}
	}
	
	private void handleYukonUserChange(DBChangeMsg msg, LiteStarsEnergyCompany energyCompany, LiteStarsCustAccountInformation liteAcctInfo) {
		switch( msg.getTypeOfChange() ) {
			case DBChangeMsg.CHANGE_TYPE_ADD:
				// Don't need to do anything
				break;
				
			case DBChangeMsg.CHANGE_TYPE_UPDATE:
				LiteYukonUser liteUser = YukonUserFuncs.getLiteYukonUser( msg.getId() );
				LiteContact primContact = ContactFuncs.getContact( liteAcctInfo.getCustomer().getPrimaryContactID() );
				
				if (primContact.getLoginID() == msg.getId()) {
					// We only care about the login of primary contact now
					StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteAcctInfo.getAccountID() );
					if (starsAcctInfo != null)
						starsAcctInfo.setStarsUser( StarsLiteFactory.createStarsUser(liteUser, energyCompany) );
				}
				
				break;
				
			case DBChangeMsg.CHANGE_TYPE_DELETE:
				// Don't need to do anything
				break;
		}
	}
	
	private void handleRouteChange(DBChangeMsg msg, LiteStarsEnergyCompany energyCompany) {
		switch( msg.getTypeOfChange() ) {
			case DBChangeMsg.CHANGE_TYPE_ADD:
				// Don't need to do anything
				break;
				
			case DBChangeMsg.CHANGE_TYPE_UPDATE:
				// Don't need to do anything
				break;
				
			case DBChangeMsg.CHANGE_TYPE_DELETE:
				try {
					StarsAdmin.removeRoute( energyCompany, msg.getId() );
				}
				catch (Exception e) {
					CTILogger.error( e.getMessage(), e );
				}
				break;
		}
	}
}