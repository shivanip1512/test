package com.cannontech.stars.web.servlet;

import java.util.ArrayList;
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
import com.cannontech.database.data.lite.stars.LiteLMControlHistory;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMControlHistory;
import com.cannontech.database.data.lite.stars.LiteWebConfiguration;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.servlet.PILConnectionServlet;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsEnergyCompany;
import com.cannontech.stars.xml.serialize.StarsInventory;
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
    
    // List of web configurations
    private static ArrayList webConfigList = null;
	
    // List of stars yukon users
    private static ArrayList starsUserList = null;
	
	private PILConnectionServlet connToPIL = null;
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
		
//		connToPIL = (com.cannontech.servlet.PILConnectionServlet)
//				getServletContext().getAttribute(com.cannontech.servlet.PILConnectionServlet.SERVLET_CONTEXT_ID);
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
				e2.printStackTrace();
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
			e.printStackTrace();
		}
	}
    
	public com.cannontech.message.util.ClientConnection getClientConnection() {
		return connToDispatch;
	}
	
	public com.cannontech.message.porter.ClientConnection getPILConnection() {
		if (connToPIL == null) return null;
		return connToPIL.getConnection();
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
    	if (energyCompanies != null) {
    		for (int i = 0; i < energyCompanies.size(); i++)
    			((LiteStarsEnergyCompany) energyCompanies.get(i)).clear();
    	}
    	energyCompanies = null;
		webConfigList = null;
    	starsUserList = null;
    	
    	com.cannontech.database.cache.DefaultDatabaseCache.getInstance().releaseAllCache();
    	com.cannontech.database.cache.functions.YukonListFuncs.releaseAllConstants();
    }

	public static boolean isClientLocal() {
		return clientLocal;
	}

	public static void setClientLocal(boolean clientLocal) {
		SOAPServer.clientLocal = clientLocal;
	}
    
	public static YC getYC() {
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
    public static ArrayList getAllEnergyCompanies() {
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
    			e.printStackTrace();
    		}
    		finally {
    			try {
    				if (conn != null) conn.close();
    			}
    			catch (Exception e2) {
    				e2.printStackTrace();
    			}
    		}
	    	
	    	CTILogger.info( "All energy companies loaded" );
    	}
    	
    	return energyCompanies;
    }
    
    public static ArrayList getAllWebConfigurations() {
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
    
    public static ArrayList getAllStarsYukonUsers() {
    	if (starsUserList == null)
    		starsUserList = new ArrayList();
    	return starsUserList;
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
		ArrayList userList = getAllStarsYukonUsers();
		synchronized (userList) {
			for (int i = 0; i < userList.size(); i++) {
				StarsYukonUser user = (StarsYukonUser) userList.get(i);
				if (user.getUserID() == yukonUser.getUserID())
					return new StarsYukonUser(user);
			}
		}
		
		try {
			StarsYukonUser user = StarsYukonUser.newInstance( yukonUser );
			addStarsYukonUser( user );
			return user;
		}
		catch (InstantiationException ie) {
			CTILogger.error( ie.getMessage(), ie );
		}
		
		return null;
	}
	
	public static void addStarsYukonUser(StarsYukonUser user) {
		ArrayList userList = getAllStarsYukonUsers();
		synchronized (userList) { userList.add( user ); }
	}
	
	public static void deleteStarsYukonUser(int userID) {
		ArrayList userList = getAllStarsYukonUsers();
		synchronized (userList) {
			for (int i = 0; i < userList.size(); i++) {
				StarsYukonUser user = (StarsYukonUser) userList.get(i);
				if (user.getUserID() == userID) {
					userList.remove(i);
					return;
				}
			}
		}
	}
	
	public static void updateLMControlHistory(LiteStarsLMControlHistory liteCtrlHist) {
		ArrayList ctrlHist = liteCtrlHist.getLmControlHistory();
		
		int lastCtrlHistID = 0;
		if (ctrlHist.size() > 0)
			lastCtrlHistID = ((LiteLMControlHistory) ctrlHist.get(ctrlHist.size() - 1)).getLmCtrlHistID();
			
		com.cannontech.database.db.pao.LMControlHistory[] ctrlHists = com.cannontech.stars.util.LMControlHistoryUtil.getLMControlHistory(
				new Integer(liteCtrlHist.getGroupID()), new Integer(lastCtrlHistID) );
		if (ctrlHists != null) {
			for (int i= 0; i < ctrlHists.length; i++)
				ctrlHist.add( (LiteLMControlHistory) StarsLiteFactory.createLite(ctrlHists[i]) );
		}
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
					break;
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
					break;
				}
			}
		}
		else if (msg.getDatabase() == DBChangeMsg.CHANGE_PAO_DB) {
			for (int i = 0; i < companies.size(); i++) {
				LiteStarsEnergyCompany energyCompany = (LiteStarsEnergyCompany) companies.get(i);
				ArrayList inventory = energyCompany.getAllInventory();
				
				for (int j = 0; j < inventory.size(); j++) {
					LiteInventoryBase liteInv = (LiteInventoryBase) inventory.get(j);
					if (liteInv.getDeviceID() == msg.getId()) {
						handlePAOChange( msg, energyCompany, liteInv );
						break;
					}
				}
			}
		}
		else if (msg.getDatabase() == DBChangeMsg.CHANGE_YUKON_USER_DB) {
			LiteContact liteContact = YukonUserFuncs.getLiteContact( msg.getId() );
			if (liteContact != null) {
				for (int i = 0; i < companies.size(); i++) {
					LiteStarsEnergyCompany energyCompany = (LiteStarsEnergyCompany) companies.get(i);
					LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInfoByContact( liteContact.getContactID() );
					if (liteAcctInfo != null) {
						handleYukonUserChange( msg, energyCompany, liteAcctInfo );
						break;
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
							return;
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
	
	private void handlePAOChange(DBChangeMsg msg, LiteStarsEnergyCompany energyCompany, LiteInventoryBase liteInv) {
		switch( msg.getTypeOfChange() )
		{
			case DBChangeMsg.CHANGE_TYPE_ADD:
				// Don't need to do anything
				break;
				
			case DBChangeMsg.CHANGE_TYPE_UPDATE :
				LiteYukonPAObject litePao = PAOFuncs.getLiteYukonPAO( msg.getId() );
				// STARS only cares about MCT now
				if (!DeviceTypesFuncs.isMCT( litePao.getLiteType() ))
					return;
				
				StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteInv.getAccountID() );
				if (starsAcctInfo != null) {
					for (int i = 0; i < starsAcctInfo.getStarsInventories().getStarsInventoryCount(); i++) {
						StarsInventory starsInv = starsAcctInfo.getStarsInventories().getStarsInventory(i);
						if (starsInv.getDeviceID() == litePao.getYukonID()) {
							starsInv.getMCT().setDeviceName( litePao.getPaoName() );
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
						starsAcctInfo.setStarsUser( StarsLiteFactory.createStarsUser(liteUser) );
				}
				
				break;
				
			case DBChangeMsg.CHANGE_TYPE_DELETE:
				// Don't need to do anything
				break;
		}
	}
}