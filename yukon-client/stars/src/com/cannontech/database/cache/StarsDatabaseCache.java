/*
 * Created on Nov 9, 2004
 *
 */
package com.cannontech.database.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
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
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompanyFactory;
import com.cannontech.database.data.lite.stars.LiteStarsLMControlHistory;
import com.cannontech.database.data.lite.stars.LiteWebConfiguration;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.database.db.web.YukonWebConfiguration;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.util.OptOutEventQueue;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsEnergyCompany;
import com.cannontech.stars.xml.serialize.StarsEnrLMProgram;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;

/**
 * 
 * @author yao
 *
 */
public class StarsDatabaseCache implements DBChangeListener {

    public static final int DEFAULT_ENERGY_COMPANY_ID = EnergyCompany.DEFAULT_ENERGY_COMPANY_ID;
	
	private static final int CTRL_HIST_CACHE_INVALID_INTERVAL = 7;	// 7 days
	
    // Array of all the energy companies (LiteStarsEnergyCompany)
	private List<LiteStarsEnergyCompany> energyCompanies = null;
    
	// List of web configurations (LiteWebConfiguration)
	private List<LiteWebConfiguration> webConfigList = null;
	
	// Map from user ID (Integer) to stars users (StarsYukonUser)
	private Map<Integer,StarsYukonUser> starsYukonUsers = null;
	
	// Map from Integer(GroupID) to LiteStarsLMControlHistory
	private Map<Integer,LiteStarsLMControlHistory> lmCtrlHists = null;
	
    private AsyncDynamicDataSource dataSource;
    private StarsCustAccountInformationDao starsCustAccountInformationDao;
    
    public StarsDatabaseCache() {
        
    }

    public void setAsyncDynamicDataSource(final AsyncDynamicDataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public void setStarsCustAccountInformationDao(
            StarsCustAccountInformationDao starsCustAccountInformationDao) {
        this.starsCustAccountInformationDao = starsCustAccountInformationDao;
    }
    
	public void init() {		
        dataSource.addDBChangeListener(this);
	}
	
	private void releaseAllCache() {
		energyCompanies = null;
		webConfigList = null;
		starsYukonUsers = null;
		lmCtrlHists = null;
	}
	
	public synchronized static StarsDatabaseCache getInstance() {
		StarsDatabaseCache instance = YukonSpringHook.getBean("starsDatabaseCache", StarsDatabaseCache.class);
		return instance;
	}
	
    public void loadData() {
        getAllWebConfigurations();
        
        // Force all contacts to be loaded (since this can take a long time, and slow down the first time login)
        DefaultDatabaseCache.getInstance().getAllContacts();
        
        getAllEnergyCompanies();
    }

	public void refreshCache() {
		if (energyCompanies != null) {
			synchronized (energyCompanies) {
				for (int i = 0; i < energyCompanies.size(); i++)
					energyCompanies.get(i).clear();
			}
			energyCompanies = null;
		}
		
		releaseAllCache();
		DefaultDatabaseCache.getInstance().releaseAllCache();
		DaoFactory.getYukonListDao().releaseAllConstants();
		
		SwitchCommandQueue.getInstance().syncFromFile();
		OptOutEventQueue.getInstance().syncFromFile();
		
		// Reload data into the cache if necessary
		String preloadData = DaoFactory.getRoleDao().getGlobalPropertyValue( SystemRole.STARS_PRELOAD_DATA );
		if (CtiUtilities.isTrue( preloadData ))
			StarsDatabaseCache.getInstance().loadData();
	}

	public void refreshCache(LiteStarsEnergyCompany energyCompany) {
		webConfigList = null;
		DaoFactory.getYukonListDao().releaseAllConstants();
		
		// release cache for all descendants of the current company as well
		final List<LiteStarsEnergyCompany> descendants = ECUtils.getAllDescendants( energyCompany );
        for (final LiteStarsEnergyCompany company : descendants) {
            company.clear();
            
            Iterator<StarsYukonUser> it = getAllStarsYukonUsers().values().iterator();
            while (it.hasNext()) {
                StarsYukonUser user = it.next();
                if (user.getEnergyCompanyID() == company.getLiteID())
                    it.remove();
            }
        }
		
		SwitchCommandQueue.getInstance().syncFromFile();
		OptOutEventQueue.getInstance().syncFromFile();
		
		// Reload data into the cache if necessary
		String preloadData = DaoFactory.getRoleDao().getGlobalPropertyValue( SystemRole.STARS_PRELOAD_DATA );
		if (CtiUtilities.isTrue( preloadData )) {
			getAllWebConfigurations();
		}
	}
    
	/*
	 * Start implementation of class functions
	 */
	public synchronized List<LiteStarsEnergyCompany> getAllEnergyCompanies() {
		if (energyCompanies == null) {
			energyCompanies = new ArrayList<LiteStarsEnergyCompany>();
	    	
            com.cannontech.database.db.company.EnergyCompany[] companies =
                com.cannontech.database.db.company.EnergyCompany.getEnergyCompanies();
            
            if (companies == null) return null;
	    	
            LiteStarsEnergyCompanyFactory factory = 
                YukonSpringHook.getBean("liteStarsEnergyCompanyFactory", LiteStarsEnergyCompanyFactory.class);
            
            synchronized (energyCompanies) {
                for (int i = 0; i < companies.length; i++)
                    energyCompanies.add(factory.createEnergyCompany(companies[i]));
            }
	    	
	    	CTILogger.info( "All energy companies loaded" );
		}
		
		return energyCompanies;
	}

	public synchronized List<LiteWebConfiguration> getAllWebConfigurations() {
		if (webConfigList == null) {
			webConfigList = new ArrayList<LiteWebConfiguration>();
			
			com.cannontech.database.db.web.YukonWebConfiguration[] webConfigs =
					YukonWebConfiguration.getAllCustomerWebConfigurations();
			for (int i = 0; i < webConfigs.length; i++)
				webConfigList.add( (LiteWebConfiguration) StarsLiteFactory.createLite(webConfigs[i]) );
	    	
	    	CTILogger.info( "All customer web configurations loaded" );
		}
		
		return webConfigList;
	}

	public synchronized Map<Integer,StarsYukonUser> getAllStarsYukonUsers() {
		if (starsYukonUsers == null)
			starsYukonUsers = new Hashtable<Integer,StarsYukonUser>();
		return starsYukonUsers;
	}

	public LiteStarsEnergyCompany getEnergyCompanyByUser(final LiteYukonUser user) {
		StarsYukonUser starsYukonUser = getStarsYukonUser(user);
		LiteStarsEnergyCompany energyCompany = getEnergyCompany(starsYukonUser.getEnergyCompanyID());
		return energyCompany;
	}
	
	public Map<Integer, LiteStarsEnergyCompany> getAllEnergyCompanyMap() {
	    List<LiteStarsEnergyCompany> companies = getAllEnergyCompanies();
	    synchronized (companies) {
            final Map<Integer, LiteStarsEnergyCompany> map = new HashMap<Integer, LiteStarsEnergyCompany>(companies.size());
            for (LiteStarsEnergyCompany value : companies) {
                Integer key = value.getEnergyCompanyID();
                map.put(key, value);
            }
            return map;
        }
	}
	
	public LiteStarsEnergyCompany getEnergyCompany(int energyCompanyID) {
        List<LiteStarsEnergyCompany> companies = getAllEnergyCompanies();
		synchronized (companies) {
			for (int i = 0; i < companies.size(); i++) {
	    		LiteStarsEnergyCompany company = companies.get(i);
				if (company.getEnergyCompanyID().intValue() == energyCompanyID) {
					return company;
				}
			}
		}
		
		return null;
	}

	public void addEnergyCompany(LiteStarsEnergyCompany company) {
        List<LiteStarsEnergyCompany> companies = getAllEnergyCompanies();
		synchronized (companies) { companies.add(company); }
	}

	public LiteStarsEnergyCompany deleteEnergyCompany(int energyCompanyID) {
        List<LiteStarsEnergyCompany> companies = getAllEnergyCompanies();
		synchronized (companies) {
			for (int i = 0; i < companies.size(); i++) {
	    		LiteStarsEnergyCompany company = companies.get(i);
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
        List<LiteWebConfiguration> webConfigList = getAllWebConfigurations();
		synchronized (webConfigList) {
			for (int i = 0; i < webConfigList.size(); i++) {
				LiteWebConfiguration config = webConfigList.get(i);
				if (config.getConfigID() == configID)
					return config;
			}
		}
		
		return null;
	}

	public void addWebConfiguration(LiteWebConfiguration config) {
        List<LiteWebConfiguration> webConfigList = getAllWebConfigurations();
		synchronized (webConfigList) { webConfigList.add(config); }
	}

	public LiteWebConfiguration deleteWebConfiguration(int configID) {
        List<LiteWebConfiguration> webConfigList = getAllWebConfigurations();
		synchronized (webConfigList) {
			for (int i = 0; i < webConfigList.size(); i++) {
				LiteWebConfiguration config = webConfigList.get(i);
				if (config.getConfigID() == configID) {
					webConfigList.remove( i );
					return config;
				}
			}
		}
		
		return null;
	}
	
	public boolean isStarsUser(final LiteYukonUser yukonUser) {
	    final Map<Integer,StarsYukonUser> starsUsers = getAllStarsYukonUsers();
	    synchronized (starsUsers) {
	        StarsYukonUser user = starsUsers.get(yukonUser.getUserID());
	        boolean result = user != null;
	        return result;
	    }
	}

	public StarsYukonUser getStarsYukonUser(LiteYukonUser yukonUser) {
        Map<Integer,StarsYukonUser> starsUsers = getAllStarsYukonUsers();
		Integer userID = new Integer( yukonUser.getUserID() );
		
		synchronized (starsUsers) {
			StarsYukonUser user = starsUsers.get( userID );
			if (user != null) {
					return user;
			}
		}
		
		try {
			StarsYukonUser user = StarsYukonUser.newInstance( yukonUser );
			synchronized (starsUsers) { starsUsers.put( userID, user ); }
			return user;
		}
		catch (InstantiationException ie) {
			CTILogger.warn( yukonUser.getUsername() + " is not a STARS user.  STARS data will not be available for this session." );
		}
		
		return null;
	}

	public void deleteStarsYukonUser(int userID) {
        Map<Integer,StarsYukonUser> starsUsers = getAllStarsYukonUsers();
		synchronized (starsUsers) { starsUsers.remove( new Integer(userID) ); }
	}
	
	public void dbChangeReceived(DBChangeMsg msg)
	{
		CTILogger.debug(" ## DBChangeMsg ##\n" + msg);
		
        List<LiteStarsEnergyCompany> companies = getAllEnergyCompanies();
		
		if (msg.getDatabase() == DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB) {
			for (int i = 0; i < companies.size(); i++) {
				LiteStarsEnergyCompany energyCompany = companies.get(i);
				if( energyCompany.getEnergyCompanyID().intValue() != EnergyCompany.DEFAULT_ENERGY_COMPANY_ID)
				{				
					LiteStarsCustAccountInformation liteAcctInfo = 
					    starsCustAccountInformationDao.getById(msg.getId(), energyCompany.getEnergyCompanyID());
					if (liteAcctInfo != null) {
						handleCustomerAccountChange( msg, energyCompany, liteAcctInfo );
						return;
					}
				}
			}
		}
		else if (msg.getDatabase() == DBChangeMsg.CHANGE_CONTACT_DB) {
			
			for (int i = 0; i < companies.size(); i++) {
				LiteStarsEnergyCompany energyCompany = companies.get(i);
				Object contOwner = null;
				
				if (energyCompany.getPrimaryContactID() == msg.getId()) {
					contOwner = energyCompany;
				}
				else {
                    List<LiteServiceCompany> servCompanies = energyCompany.getServiceCompanies();
                    for (final LiteServiceCompany servCompany : servCompanies) {
                        if (servCompany.getPrimaryContactID() == msg.getId()) {
                            contOwner = servCompany;
                            break;
                        }
                    }
					
					if (contOwner == null) {
					    CustomerAccountDao customerAccountDao = YukonSpringHook.getBean("customerAccountDao", CustomerAccountDao.class);
					    CustomerAccount customerAccount = null;
					    try {
					        customerAccount = customerAccountDao.getAccountByContactId(msg.getId());
					    }catch(EmptyResultDataAccessException empty) {}
						if (customerAccount != null) {
							contOwner = energyCompany.getStarsCustAccountInformation(customerAccount.getAccountId());
						}
					}
				}
				
				if (contOwner != null) {
					handleContactChange( msg, energyCompany, contOwner );
					return;
				}
			}
		}
		else if (msg.getDatabase() == DBChangeMsg.CHANGE_PAO_DB) {
		    LiteYukonPAObject litePao = null;
            
            /*
             * Why look this up if it has just been deleted from cache?
             *TODO: Will need to add more functionality to handle deletes and adds if STARS is tied
             *together more firmly with Yukon in the future.  (Example might be MCT inventory contents.) 
             */
            if(msg.getTypeOfChange() != DBChangeMsg.CHANGE_TYPE_DELETE)
                litePao = DaoFactory.getPaoDao().getLiteYukonPAO( msg.getId() );
            else {
                CTILogger.debug("DBChangeMsg for a deleted PAO: " + msg);
                return;
            }
            
    		for (int i = 0; i < companies.size(); i++) {
    			LiteStarsEnergyCompany energyCompany = companies.get(i);
    			
    			if (litePao.getCategory() == PAOGroups.CAT_ROUTE) {
    				if (!ECUtils.isSingleEnergyCompany( energyCompany ))
    					handleRouteChange( msg, energyCompany );
    			}
    			else if (DeviceTypesFuncs.isLMProgramDirect( litePao.getType() )) {
    				List<LiteLMProgramWebPublishing> programs = new ArrayList<LiteLMProgramWebPublishing>( energyCompany.getPrograms() );
    				for (int j = 0; j < programs.size(); j++) {
    					LiteLMProgramWebPublishing liteProg = programs.get(j);
    					if (liteProg.getDeviceID() == msg.getId()) {
    						handleLMProgramChange( msg, energyCompany, liteProg );
    						return;
    					}
    				}
    			}
    			else if (DeviceTypesFuncs.isLmGroup( litePao.getType() )) {
    				List<LiteLMProgramWebPublishing> programs = new ArrayList<LiteLMProgramWebPublishing>( energyCompany.getPrograms() );
    				StarsEnrollmentPrograms categories = energyCompany.getStarsEnrollmentPrograms();
    				
    				for (int j = 0; j < programs.size(); j++) {
    					LiteLMProgramWebPublishing liteProg = programs.get(j);
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
    				
    				StarsInventoryBaseDao starsInventoryBaseDao = 
    					YukonSpringHook.getBean("starsInventoryBaseDao", StarsInventoryBaseDao.class);
    				
    				LiteInventoryBase inventoryBase = starsInventoryBaseDao.getById(msg.getId());
    				if (inventoryBase != null) {
                        handleDeviceChange(msg, energyCompany, inventoryBase);
                        return;
                    }
    			}
    		}
    	}
		else if (msg.getDatabase() == DBChangeMsg.CHANGE_YUKON_USER_DB) {
			if (msg.getCategory().equals( DBChangeMsg.CAT_YUKON_USER )) {
				LiteContact liteContact = DaoFactory.getYukonUserDao().getLiteContact( msg.getId() );
				if (liteContact != null) {
					for (int i = 0; i < companies.size(); i++) {
						LiteStarsEnergyCompany energyCompany = companies.get(i);
						CustomerAccountDao customerAccountDao = YukonSpringHook.getBean("CustomerAccountDao", CustomerAccountDao.class);
                        CustomerAccount customerAccount = customerAccountDao.getAccountByContactId(msg.getId());
                        if (customerAccount != null) {
							StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( customerAccount.getAccountId());
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
				break;
		}
	}
	
	private void handleContactChange(DBChangeMsg msg, LiteStarsEnergyCompany energyCompany, Object contOwner) {
		switch (msg.getTypeOfChange()) {
			case DBChangeMsg.CHANGE_TYPE_ADD :
				// Don't need to do anything
				break;
				
			case DBChangeMsg.CHANGE_TYPE_UPDATE :
				LiteContact liteContact = DaoFactory.getContactDao().getContact( msg.getId() );
				
				if (contOwner instanceof LiteStarsEnergyCompany) {
					StarsEnergyCompany starsEC = energyCompany.getStarsEnergyCompany();
					StarsLiteFactory.setStarsEnergyCompany( starsEC, energyCompany );
				}
				else if (contOwner instanceof LiteServiceCompany) {
                    List<LiteStarsEnergyCompany> descendants = ECUtils.getAllDescendants( energyCompany );
					for (int i = 0; i < descendants.size(); i++) {
						LiteStarsEnergyCompany company = descendants.get(i);
						for (int j = 0; j < company.getStarsServiceCompanies().getStarsServiceCompanyCount(); j++) {
							StarsServiceCompany starsSC = company.getStarsServiceCompanies().getStarsServiceCompany(j);
							if (starsSC.getPrimaryContact() != null && starsSC.getPrimaryContact().getContactID() == liteContact.getContactID()) {
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
					LiteStarsCustAccountInformation liteAcctInfo = 
					    starsCustAccountInformationDao.getById(starsAcctInfo.getStarsCustomerAccount().getAccountID(), energyCompany.getEnergyCompanyID());
                    Vector<LiteContact> contacts = liteAcctInfo.getCustomer().getAdditionalContacts();
					for (int i = 0; i < contacts.size(); i++) {
						if (contacts.get(i).getContactID() == msg.getId()) {
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
					if (program != null) 
                    {
						try
                        {
						    program.setYukonName( DaoFactory.getPaoDao().getYukonPAOName(liteProg.getDeviceID()) );
                        }
                        catch(NotFoundException e)
                        {
                            program.setYukonName( CtiUtilities.STRING_NONE );
                        }
                        StarsLiteFactory.setAddressingGroups( program, liteProg );
					}
				}
				catch (java.sql.SQLException e) {
					CTILogger.error( e.getMessage(), e );
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
							starsInv.getMCT().setDeviceName( DaoFactory.getPaoDao().getYukonPAOName(msg.getId()) );
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
					LiteYukonUser liteUser = DaoFactory.getYukonUserDao().getLiteYukonUser( msg.getId() );
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
	
	private synchronized Map<Integer,LiteStarsLMControlHistory> getLMCtrlHistMap() {
		if (lmCtrlHists == null)
			lmCtrlHists = new Hashtable<Integer,LiteStarsLMControlHistory>();
		
		return lmCtrlHists;
	}
	
	/**
	 * Get control history from a given start date (control summary will always be returned as well).
	 * To get only the control summary, set start date to null; to get the complete control history,
	 * set start date to new java.util.Date(0).
	 */
	public LiteStarsLMControlHistory getLMControlHistory(int groupID, Date startDate) {
		/* STARS cannot assume that a zero group ID means no control history...should
		 * still show something
		 */
		//if (groupID == CtiUtilities.NONE_ZERO_ID) return null;
		
		LiteStarsLMControlHistory lmCtrlHist =
				getLMCtrlHistMap().get( new Integer(groupID));
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
				
				List<LiteBase> ctrlHistList = new ArrayList<LiteBase>();
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
		
		if(lmCtrlHist.getLmControlHistory() == null)
		{
			lmCtrlHist.setLmControlHistory(new ArrayList<LiteBase>());
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
