/*
 * Created on Nov 9, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMControlHistory;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMControlHistory;
import com.cannontech.database.data.lite.stars.LiteWebConfiguration;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.web.YukonWebConfiguration;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsEnergyCompany;
import com.cannontech.stars.xml.serialize.StarsEnrLMProgram;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMProgram;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class StarsDatabaseCache implements com.cannontech.database.cache.DBChangeListener {
	
	public static final int DEFAULT_ENERGY_COMPANY_ID = -1;
	
	private static final int CTRL_HIST_CACHE_INVALID_INTERVAL = 7;	// 7 days
    
	// Instance of the SOAPServer object
	private static StarsDatabaseCache instance = null;
	
	// Array of all the energy companies (LiteStarsEnergyCompany)
	private ArrayList energyCompanies = null;
    
	// List of web configurations (LiteWebConfiguration)
	private ArrayList webConfigList = null;
	
	// Map from user ID (Integer) to stars users (StarsYukonUser)
	private Hashtable starsYukonUsers = null;
	
	// Map from Integer(GroupID) to LiteStarsLMControlHistory
	private Hashtable lmCtrlHists = null;
	
	private com.cannontech.message.dispatch.ClientConnection connToDispatch;
	
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
	
	private void init() {
		initDispatchConnection();
		
		com.cannontech.database.cache.DefaultDatabaseCache.getInstance().addDBChangeListener(this);
	}
	
	private void releaseAllCache() {
		energyCompanies = null;
		webConfigList = null;
		starsYukonUsers = null;
		lmCtrlHists = null;
	}
	
	public synchronized static StarsDatabaseCache getInstance() {
		if (instance == null) {
			instance = new StarsDatabaseCache();
			instance.init();
		}
		return instance;
	}
	
	public void loadData() {
		getAllWebConfigurations();
		
		ArrayList allCompanies = getAllEnergyCompanies();
		
		final LiteStarsEnergyCompany[] companies = new LiteStarsEnergyCompany[ allCompanies.size() ];
		allCompanies.toArray( companies );
		
		Thread initThrd = new Thread(new Runnable() {
			public void run() {
				for (int i = 0; i < companies.length; i++) {
					if (!ECUtils.isDefaultEnergyCompany( companies[i] )) {
						// Fire the data loading threads off, and wait for all of them to stop
						companies[i].loadAllInventory( false );
						companies[i].loadAllCustomerAccounts( false );
						companies[i].loadAllWorkOrders( false );
						companies[i].loadAllInventory( true );
						companies[i].loadAllCustomerAccounts( true );
						companies[i].loadAllWorkOrders( true );
					}
				}
			}
		});
		initThrd.start();
	}

	public void refreshCache() {
		if (energyCompanies != null) {
			synchronized (energyCompanies) {
				for (int i = 0; i < energyCompanies.size(); i++)
					((LiteStarsEnergyCompany) energyCompanies.get(i)).clear();
			}
			energyCompanies = null;
		}
		
		releaseAllCache();
		DefaultDatabaseCache.getInstance().releaseAllCache();
		YukonListFuncs.releaseAllConstants();
		
		// Reload data into the cache if necessary
		String preloadData = RoleFuncs.getGlobalPropertyValue( SystemRole.STARS_PRELOAD_DATA );
		if (CtiUtilities.isTrue( preloadData ))
			StarsDatabaseCache.getInstance().loadData();
	}

	public void refreshCache(LiteStarsEnergyCompany energyCompany) {
		webConfigList = null;
		YukonListFuncs.releaseAllConstants();
		
		// release cache for all descendants of the current company as well
		final ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
		for (int i = 0; i < descendants.size(); i++) {
			LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
			company.clear();
			
			Iterator it = getAllStarsYukonUsers().values().iterator();
			while (it.hasNext()) {
				StarsYukonUser user = (StarsYukonUser) it.next();
				if (user.getEnergyCompanyID() == company.getLiteID())
					it.remove();
			}
		}
		
		// Reload data into the cache if necessary
		String preloadData = RoleFuncs.getGlobalPropertyValue( SystemRole.STARS_PRELOAD_DATA );
		if (CtiUtilities.isTrue( preloadData )) {
			getAllWebConfigurations();
			
			Thread initThrd = new Thread(new Runnable() {
				public void run() {
					for (int i = 0; i < descendants.size(); i++) {
						LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
						if (!ECUtils.isDefaultEnergyCompany( company )) {
							// Fire the data loading threads off, and wait for all of them to stop
							company.loadAllInventory( false );
							company.loadAllCustomerAccounts( false );
							company.loadAllWorkOrders( false );
							company.loadAllInventory( true );
							company.loadAllCustomerAccounts( true );
							company.loadAllWorkOrders( true );
						}
					}
				}
			});
			initThrd.start();
		}
	}

	/*
	 * Start implementation of class functions
	 */
	public synchronized ArrayList getAllEnergyCompanies() {
		if (energyCompanies == null) {
			energyCompanies = new ArrayList();
	    	java.sql.Connection conn = null;
	    	
			try {
	    		conn = PoolManager.getInstance().getConnection(
	    				CtiUtilities.getDatabaseAlias() );
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

	public synchronized ArrayList getAllWebConfigurations() {
		if (webConfigList == null) {
			webConfigList = new ArrayList();
			
			com.cannontech.database.db.web.YukonWebConfiguration[] webConfigs =
					YukonWebConfiguration.getAllCustomerWebConfigurations();
			for (int i = 0; i < webConfigs.length; i++)
				webConfigList.add( (LiteWebConfiguration) StarsLiteFactory.createLite(webConfigs[i]) );
	    	
	    	CTILogger.info( "All customer web configurations loaded" );
		}
		
		return webConfigList;
	}

	public synchronized Hashtable getAllStarsYukonUsers() {
		if (starsYukonUsers == null)
			starsYukonUsers = new Hashtable();
		return starsYukonUsers;
	}

	public LiteStarsEnergyCompany getEnergyCompany(int energyCompanyID) {
		ArrayList companies = getAllEnergyCompanies();
		synchronized (companies) {
			for (int i = 0; i < companies.size(); i++) {
	    		LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) companies.get(i);
				if (company.getEnergyCompanyID().intValue() == energyCompanyID) {
					String preloadData = RoleFuncs.getGlobalPropertyValue( SystemRole.STARS_PRELOAD_DATA );
					if (CtiUtilities.isFalse(preloadData)) company.init();
					return company;
				}
			}
		}
		
		return null;
	}

	public void addEnergyCompany(LiteStarsEnergyCompany company) {
		ArrayList companies = getAllEnergyCompanies();
		synchronized (companies) { companies.add(company); }
	}

	public LiteStarsEnergyCompany deleteEnergyCompany(int energyCompanyID) {
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

	public LiteStarsEnergyCompany getDefaultEnergyCompany() {
		return getEnergyCompany( DEFAULT_ENERGY_COMPANY_ID );
	}

	public LiteWebConfiguration getWebConfiguration(int configID) {
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

	public void addWebConfiguration(LiteWebConfiguration config) {
		ArrayList webConfigList = getAllWebConfigurations();
		synchronized (webConfigList) { webConfigList.add(config); }
	}

	public LiteWebConfiguration deleteWebConfiguration(int configID) {
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

	public StarsYukonUser getStarsYukonUser(LiteYukonUser yukonUser) {
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

	public void deleteStarsYukonUser(int userID) {
		Hashtable starsUsers = getAllStarsYukonUsers();
		synchronized (starsUsers) { starsUsers.remove( new Integer(userID) ); }
	}
    
	public com.cannontech.message.util.ClientConnection getClientConnection() {
		return connToDispatch;
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
				Object contOwner = null;
				
				if (energyCompany.getPrimaryContactID() == msg.getId()) {
					contOwner = energyCompany;
				}
				else {
					ArrayList servCompanies = energyCompany.getServiceCompanies();
					for (int j = 0; j < servCompanies.size(); j++) {
						LiteServiceCompany servCompany = (LiteServiceCompany) servCompanies.get(j);
						if (servCompany.getPrimaryContactID() == msg.getId()) {
							contOwner = servCompany;
							break;
						}
					}
					
					if (contOwner == null) {
						Integer accountID = (Integer) energyCompany.getContactAccountIDMap().get( new Integer(msg.getId()) );
						if (accountID != null)
							contOwner = energyCompany.getStarsCustAccountInformation( accountID.intValue() );
					}
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
					if (!ECUtils.isSingleEnergyCompany( energyCompany ))
						handleRouteChange( msg, energyCompany );
				}
				else if (DeviceTypesFuncs.isLMProgramDirect( litePao.getType() )) {
					ArrayList programs = new ArrayList( energyCompany.getPrograms() );
					for (int j = 0; j < programs.size(); j++) {
						LiteLMProgramWebPublishing liteProg = (LiteLMProgramWebPublishing) programs.get(j);
						if (liteProg.getDeviceID() == msg.getId()) {
							handleLMProgramChange( msg, energyCompany, liteProg );
							return;
						}
					}
				}
				else if (DeviceTypesFuncs.isLmGroup( litePao.getType() )) {
					ArrayList programs = new ArrayList( energyCompany.getPrograms() );
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
						Integer accountID = (Integer) energyCompany.getContactAccountIDMap().get( new Integer(liteContact.getContactID()) );
						if (accountID != null) {
							StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( accountID.intValue() );
							if (starsAcctInfo != null && starsAcctInfo.getStarsUser() != null) {
								handleYukonUserChange( msg, energyCompany, starsAcctInfo );
								return;
							}
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
			case DBChangeMsg.CHANGE_TYPE_DELETE:
				energyCompany.deleteCustAccountInformation( liteAcctInfo );
				energyCompany.deleteStarsCustAccountInformation( liteAcctInfo.getAccountID() );
				break;
		}
	}
	
	private void handleContactChange(DBChangeMsg msg, LiteStarsEnergyCompany energyCompany, Object contOwner) {
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
					ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
					for (int i = 0; i < descendants.size(); i++) {
						LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
						for (int j = 0; j < company.getStarsServiceCompanies().getStarsServiceCompanyCount(); j++) {
							StarsServiceCompany starsSC = company.getStarsServiceCompanies().getStarsServiceCompany(j);
							if (starsSC.getPrimaryContact().getContactID() == liteContact.getContactID()) {
								StarsLiteFactory.setStarsCustomerContact( starsSC.getPrimaryContact(), liteContact );
								break;
							}
						}
					}
				}
				else if (contOwner instanceof StarsCustAccountInformation) {
					StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation) contOwner;
					
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
				
				break;
				
			case DBChangeMsg.CHANGE_TYPE_DELETE :
				if (contOwner instanceof StarsCustAccountInformation) {
					StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation) contOwner;
					LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation(
							starsAcctInfo.getStarsCustomerAccount().getAccountID(), true );
					
					Vector contacts = liteAcctInfo.getCustomer().getAdditionalContacts();
					for (int i = 0; i < contacts.size(); i++) {
						if (((LiteContact)contacts.get(i)).getContactID() == msg.getId()) {
							contacts.remove(i);
							break;
						}
					}
					
					for (int i = 0; i < starsAcctInfo.getStarsCustomerAccount().getAdditionalContactCount(); i++) {
						if (starsAcctInfo.getStarsCustomerAccount().getAdditionalContact(i).getContactID() == msg.getId()) {
							starsAcctInfo.getStarsCustomerAccount().removeAdditionalContact(i);
							break;
						}
					}
					
					energyCompany.getContactAccountIDMap().remove( new Integer(msg.getId()) );
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
				catch (java.sql.SQLException e) {
					CTILogger.error( e.getMessage(), e );
				}
				
				String newProgName = StarsUtils.getPublishedProgramName( liteProg );
				
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
				catch (java.sql.SQLException e) {
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
	
	private void handleYukonUserChange(DBChangeMsg msg, LiteStarsEnergyCompany energyCompany, StarsCustAccountInformation starsAcctInfo) {
		switch( msg.getTypeOfChange() ) {
			case DBChangeMsg.CHANGE_TYPE_ADD:
				// Don't need to do anything
				break;
				
			case DBChangeMsg.CHANGE_TYPE_UPDATE:
				if (starsAcctInfo.getStarsUser().getUserID() == msg.getId()) {
					LiteYukonUser liteUser = YukonUserFuncs.getLiteYukonUser( msg.getId() );
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
					StarsAdminUtil.removeRoute( energyCompany, msg.getId() );
				}
				catch (Exception e) {
					CTILogger.error( e.getMessage(), e );
				}
				break;
		}
	}
	
	private synchronized Hashtable getLMCtrlHistMap() {
		if (lmCtrlHists == null)
			lmCtrlHists = new Hashtable();
		
		return lmCtrlHists;
	}
	
	/**
	 * Get control history from a given start date (control summary will always be returned as well).
	 * To get only the control summary, set start date to null; to get the complete control history,
	 * set start date to new java.util.Date(0).
	 */
	public LiteStarsLMControlHistory getLMControlHistory(int groupID, Date startDate) {
		if (groupID == CtiUtilities.NONE_ZERO_ID) return null;
		
		LiteStarsLMControlHistory lmCtrlHist =
				(LiteStarsLMControlHistory) getLMCtrlHistMap().get( new Integer(groupID) );
		if (lmCtrlHist == null) lmCtrlHist = new LiteStarsLMControlHistory( groupID );
		
		if (startDate != null &&
			(startDate.getTime() < lmCtrlHist.getLastSearchedStartTime() ||
			(startDate.getTime() - lmCtrlHist.getLastSearchedStopTime()) * 0.001 / 3600 / 24 > CTRL_HIST_CACHE_INVALID_INTERVAL))
		{
			// Clear the cached control history if the request date is earlier than the cache start date,
			// or it is later than the cache stop date and the interval is too long.
			lmCtrlHist.clearLMControlHistory();
		}
		
		int lastSearchedID = LMControlHistoryUtil.getLastLMCtrlHistID();
		
		if (startDate != null) {
			if (lmCtrlHist.getLmControlHistory() == null) {
				Date dateFrom = StarsUtils.translateDate( startDate.getTime() );
				com.cannontech.database.db.pao.LMControlHistory[] ctrlHist =
						LMControlHistoryUtil.getLMControlHistory( groupID, dateFrom, null );
				
				ArrayList ctrlHistList = new ArrayList();
				for (int i = 0; i < ctrlHist.length; i++)
					ctrlHistList.add( StarsLiteFactory.createLite(ctrlHist[i]) );
				lmCtrlHist.setLmControlHistory( ctrlHistList );
			}
			else {
				com.cannontech.database.db.pao.LMControlHistory[] ctrlHist =
						LMControlHistoryUtil.getLMControlHistory( groupID, lmCtrlHist.getLastSearchedCtrlHistID() );
				for (int i = 0; i < ctrlHist.length; i++)
					lmCtrlHist.getLmControlHistory().add( StarsLiteFactory.createLite(ctrlHist[i]) );
			}
			
			lmCtrlHist.setLastSearchedCtrlHistID( lastSearchedID );
			lmCtrlHist.setLastSearchedStartTime( startDate.getTime() );
			lmCtrlHist.setLastSearchedStopTime( System.currentTimeMillis() );
		}
		
		if (lmCtrlHist.getLmControlHistory().size() > 0) {
			LiteLMControlHistory liteCtrlHist = (LiteLMControlHistory)
					lmCtrlHist.getLmControlHistory().get(lmCtrlHist.getLmControlHistory().size() - 1);
			
			lastSearchedID = Math.max( liteCtrlHist.getLmCtrlHistID(), lastSearchedID );
			lmCtrlHist.setLastSearchedCtrlHistID( lastSearchedID );
			
			LiteLMControlHistory lastCtrlHist = new LiteLMControlHistory( lastSearchedID );
			lastCtrlHist.setStartDateTime( liteCtrlHist.getStartDateTime() );
			lastCtrlHist.setStopDateTime( liteCtrlHist.getStopDateTime() );
			lastCtrlHist.setCurrentDailyTime( liteCtrlHist.getCurrentDailyTime() );
			lastCtrlHist.setCurrentMonthlyTime( liteCtrlHist.getCurrentMonthlyTime() );
			lastCtrlHist.setCurrentSeasonalTime( liteCtrlHist.getCurrentSeasonalTime() );
			lastCtrlHist.setCurrentAnnualTime( liteCtrlHist.getCurrentAnnualTime() );
			lmCtrlHist.setLastControlHistory( lastCtrlHist );
		}
		else {
			int startCtrlHistID = 0;
			if (lmCtrlHist.getLastControlHistory() != null)
				startCtrlHistID = lmCtrlHist.getLastControlHistory().getLmCtrlHistID();
			
			com.cannontech.database.db.pao.LMControlHistory lastCtrlHist =
					LMControlHistoryUtil.getLastLMControlHistory( groupID, startCtrlHistID );
			
			if (lastCtrlHist != null) {
				LiteLMControlHistory liteCtrlHist = (LiteLMControlHistory) StarsLiteFactory.createLite(lastCtrlHist);
				if (liteCtrlHist.getLmCtrlHistID() < lastSearchedID)
					liteCtrlHist.setLmCtrlHistID( lastSearchedID );
				lmCtrlHist.setLastControlHistory( liteCtrlHist );
			}
			else {
				if (lmCtrlHist.getLastControlHistory() == null)
					lmCtrlHist.setLastControlHistory( new LiteLMControlHistory(lastSearchedID) );
			}
		}
		
		getLMCtrlHistMap().put( new Integer(groupID), lmCtrlHist );
		return lmCtrlHist;
	}
}
