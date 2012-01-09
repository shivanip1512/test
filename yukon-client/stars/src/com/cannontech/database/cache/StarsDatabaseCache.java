/*
 * Created on Nov 9, 2004
 *
 */
package com.cannontech.database.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteLMControlHistory;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompanyFactory;
import com.cannontech.database.data.lite.stars.LiteStarsLMControlHistory;
import com.cannontech.database.data.lite.stars.LiteWebConfiguration;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.database.db.stars.LMProgramWebPublishing;
import com.cannontech.database.db.web.YukonWebConfiguration;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsEnrLMProgram;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;
import com.google.common.collect.Lists;

/**
 * 
 * @author yao
 *
 */
public class StarsDatabaseCache implements DBChangeListener {

    public static final int DEFAULT_ENERGY_COMPANY_ID = EnergyCompany.DEFAULT_ENERGY_COMPANY_ID;
	private static final Duration CTRL_HIST_CACHE_INVALID_INTERVAL = Duration.standardDays(7);
	
    // Array of all the energy companies (LiteStarsEnergyCompany)
	private List<LiteStarsEnergyCompany> energyCompanies = null;
    
	// List of web configurations (LiteWebConfiguration)
	private ConcurrentMap<Integer,LiteWebConfiguration> webConfigMap = null;
	
	// Map from user ID (Integer) to stars users (StarsYukonUser)
	private Map<Integer,StarsYukonUser> starsYukonUsers = null;
	
	// Map from Integer(GroupID) to LiteStarsLMControlHistory
	private Map<Integer,LiteStarsLMControlHistory> lmCtrlHists = null;
	
    private AsyncDynamicDataSource dataSource;
    private ApplianceCategoryDao applianceCategoryDao;
    
    public StarsDatabaseCache() {
        
    }

    public void setAsyncDynamicDataSource(final AsyncDynamicDataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Autowired
    public void setApplianceCategoryDao(ApplianceCategoryDao applianceCategoryDao) {
        this.applianceCategoryDao = applianceCategoryDao;
    }

    public void init() {		
        dataSource.addDBChangeListener(this);
        dataSource.addDatabaseChangeEventListener(DbChangeCategory.APPLIANCE, new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                handleApplianceCategoryChange(event);
            }
        });
	}
	
	public synchronized static StarsDatabaseCache getInstance() {
		StarsDatabaseCache instance = YukonSpringHook.getBean("starsDatabaseCache", StarsDatabaseCache.class);
		return instance;
	}
	
    public void loadData() {
        getAllWebConfigurations();
        
        getAllEnergyCompanies();
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

	private synchronized Map<Integer,LiteWebConfiguration> getAllWebConfigurations() {
	    
		if (webConfigMap == null) {
			webConfigMap = new ConcurrentHashMap<Integer,LiteWebConfiguration>();
			
			YukonWebConfiguration[] webConfigs =
					YukonWebConfiguration.getAllCustomerWebConfigurations();
			for (int i = 0; i < webConfigs.length; i++) {
                LiteWebConfiguration webConfiguration = (LiteWebConfiguration) StarsLiteFactory.createLite(webConfigs[i]);
                webConfigMap.put(webConfiguration.getConfigID(), webConfiguration );
            }
	    	
	    	CTILogger.info( "All customer web configurations loaded" );
		}
		
		return webConfigMap;
	}

	public synchronized Map<Integer,StarsYukonUser> getAllStarsYukonUsers() {
		if (starsYukonUsers == null)
			starsYukonUsers = new Hashtable<Integer,StarsYukonUser>();
		return starsYukonUsers;
	}

	/**
	 *@deprecated To get a LiteStarsEnergyCompany by user, 
	 *    use YukonEnergyCompanyService.getEnergyCompanyByOperator(),
	 *    then StarsDatabaseCache.getEnergyCompany(YukonEnergyCompany yukonEnergyCompany).
	 */
	@Deprecated
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
                Integer key = value.getEnergyCompanyId();
                map.put(key, value);
            }
            return map;
        }
	}
	
	public LiteStarsEnergyCompany getEnergyCompany(YukonEnergyCompany yukonEnergyCompany) {
	    return getEnergyCompany(yukonEnergyCompany.getEnergyCompanyId());
	}
	
	public LiteStarsEnergyCompany getEnergyCompany(int energyCompanyID) {
        List<LiteStarsEnergyCompany> companies = getAllEnergyCompanies();
		synchronized (companies) {
			for (int i = 0; i < companies.size(); i++) {
	    		LiteStarsEnergyCompany company = companies.get(i);
				if (company.getEnergyCompanyId() == energyCompanyID) {
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
				if (company.getEnergyCompanyId() == energyCompanyID) {
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

	/**
	 * @deprecated Use WebConfigurationDao.getById.
	 */
	@Deprecated
	public LiteWebConfiguration getWebConfiguration(int configID) {
        Map<Integer, LiteWebConfiguration> configs = getAllWebConfigurations();
        LiteWebConfiguration result = configs.get(configID);
        return result;
	}

	public void addWebConfiguration(LiteWebConfiguration config) {
        Map<Integer, LiteWebConfiguration> configs = getAllWebConfigurations();
        configs.put(config.getConfigID(), config);
	}

	public LiteWebConfiguration deleteWebConfiguration(int configID) {
        Map<Integer, LiteWebConfiguration> configs = getAllWebConfigurations();
        LiteWebConfiguration result = configs.remove(configID);
        return result;
	}
	
	public boolean isStarsUser(final LiteYukonUser yukonUser) {
	    final Map<Integer,StarsYukonUser> starsUsers = getAllStarsYukonUsers();
	    synchronized (starsUsers) {
	        StarsYukonUser user = starsUsers.get(yukonUser.getUserID());
	        boolean result = user != null;
	        return result;
	    }
	}

	@Deprecated
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
		
        if (msg.getDatabase() == DBChangeMsg.CHANGE_PAO_DB) {
		    LiteYukonPAObject litePao = null;
            
            /*
             * Why look this up if it has just been deleted from cache?
             *TODO: Will need to add more functionality to handle deletes and adds if STARS is tied
             *together more firmly with Yukon in the future.  (Example might be MCT inventory contents.) 
             */
            if(msg.getDbChangeType() != DbChangeType.DELETE)
                litePao = DaoFactory.getPaoDao().getLiteYukonPAO( msg.getId() );
            else {
                CTILogger.debug("DBChangeMsg for a deleted PAO: " + msg);
                return;
            }
            
    		for (int i = 0; i < companies.size(); i++) {
    			LiteStarsEnergyCompany energyCompany = companies.get(i);
    			
    			if (litePao.getPaoType().getPaoCategory().getCategoryId() == PAOGroups.CAT_ROUTE) {
    				if (!ECUtils.isSingleEnergyCompany( energyCompany ))
    					handleRouteChange( msg, energyCompany );
    			}
    			else if (DeviceTypesFuncs.isLMProgramDirect( litePao.getPaoType().getDeviceTypeId() )) {
    				if (energyCompany.getPrograms() != null) {
	    				for (LiteLMProgramWebPublishing liteProg : energyCompany.getPrograms()) {
	    					if (liteProg.getDeviceID() == msg.getId()) {
	    						handleLMProgramChange( msg, energyCompany, liteProg );
	    						return;
	    					}
	    				}
    				}
    			}
    			else if (DeviceTypesFuncs.isLmGroup( litePao.getPaoType().getDeviceTypeId() )) {
    				StarsEnrollmentPrograms categories = energyCompany.getStarsEnrollmentPrograms();
    				
    				if (energyCompany.getPrograms() != null) {
	    				for (LiteLMProgramWebPublishing liteProg : energyCompany.getPrograms()) {
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
    			}
    		}
    	}
		else if (msg.getDatabase() == DBChangeMsg.CHANGE_YUKON_USER_DB) {
			if (msg.getCategory().equals( DBChangeMsg.CAT_YUKON_USER )) {
				LiteContact liteContact = DaoFactory.getYukonUserDao().getLiteContact( msg.getId() );
				if (liteContact != null) {
						CustomerAccountDao customerAccountDao = YukonSpringHook.getBean("customerAccountDao", CustomerAccountDao.class);
						CustomerAccount customerAccount = null;
						try {
						    customerAccount = customerAccountDao.getAccountByContactId(liteContact.getContactID());
						}catch(EmptyResultDataAccessException e) {
						    CTILogger.error("Unable to find CustomerAccount for contact id: " + liteContact.getContactID(), e);
						}
                        if (customerAccount != null) {
                            ECMappingDao ecMappingDao = YukonSpringHook.getBean("ecMappingDao", ECMappingDao.class);
                            LiteStarsEnergyCompany energyCompany = ecMappingDao.getCustomerAccountEC(customerAccount);
							StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( customerAccount.getAccountId());
							if (starsAcctInfo != null && starsAcctInfo.getStarsUser() != null) {
								handleYukonUserChange( msg, energyCompany, starsAcctInfo );
								return;
							}
						}
				}
			}
		} else if (msg.getDatabase() == DBChangeMsg.CHANGE_WEB_CONFIG_DB) {
            handleWebConfigurationChange(msg);
        } else if (msg.getDatabase() == DBChangeMsg.CHANGE_STARS_PUBLISHED_PROGRAM_DB) {
            handlePublishedProgramChange(msg);
        }

	}

	private synchronized void handlePublishedProgramChange(DBChangeMsg msg) {
	    LMProgramWebPublishing lmProgramWebPublishing = LMProgramWebPublishing.getLMProgramWebPublishing(msg.getId());
	    int appCatId = lmProgramWebPublishing.getApplianceCategoryID();

	    List<Integer> energyCompanyIds = applianceCategoryDao.getEnergyCompaniesByApplianceCategoryId(appCatId);
	    for (Integer energyCompanyId : energyCompanyIds) {
	        LiteStarsEnergyCompany energyCompany = getEnergyCompany(energyCompanyId);

	        switch( msg.getDbChangeType() ) {
	            case ADD:

	                try {
	                    // Create a lite appliance category from the supplied applianceCategoryId
	                    com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
	                        new com.cannontech.database.data.stars.appliance.ApplianceCategory();
	                    appCat.setApplianceCategoryID(appCatId);
	                    appCat = Transaction.createTransaction( Transaction.RETRIEVE, appCat ).execute();
	                    LiteApplianceCategory liteAppCat = (LiteApplianceCategory) StarsLiteFactory.createLite( appCat.getApplianceCategory() );
	                    LiteLMProgramWebPublishing liteLMProgWebPub = (LiteLMProgramWebPublishing) StarsLiteFactory.createLite( lmProgramWebPublishing );

	                    // Add the lite appliance category to the supplied energy company
	                    energyCompany.addProgram(liteLMProgWebPub, liteAppCat);
	                } catch (TransactionException e) {
	                    CTILogger.error( e.getMessage(), e );
	                }
	                break;

	            case UPDATE:
                    try {
                        // Create a lite appliance category from the supplied applianceCategoryId
                        com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
                            new com.cannontech.database.data.stars.appliance.ApplianceCategory();
                        appCat.setApplianceCategoryID(appCatId);
                        appCat = Transaction.createTransaction( Transaction.RETRIEVE, appCat ).execute();
                        LiteApplianceCategory liteAppCat = (LiteApplianceCategory) StarsLiteFactory.createLite( appCat.getApplianceCategory() );
                        LiteLMProgramWebPublishing liteLMProgWebPub = (LiteLMProgramWebPublishing) StarsLiteFactory.createLite( lmProgramWebPublishing );

                        // Add the lite appliance category to the supplied energy company
                        energyCompany.updateProgram(liteLMProgWebPub, liteAppCat);
                    } catch (TransactionException e) {
                        CTILogger.error( e.getMessage(), e );
                    }
	                break;

	            case DELETE:
	                energyCompany.deleteProgram(lmProgramWebPublishing.getProgramID());
	                break;
	        }
	    }

	}

    private synchronized void handleWebConfigurationChange(DBChangeMsg msg) {
        webConfigMap = null;
    }

	private void handleApplianceCategoryChange(DatabaseChangeEvent event) {
	    int applianceCategoryId = event.getPrimaryKey();

	    List<Integer> energyCompanyIds = Lists.newArrayList();
	    try {
	        energyCompanyIds = applianceCategoryDao.getEnergyCompaniesByApplianceCategoryId(applianceCategoryId);
	    } catch (NotFoundException ex) {/* There are no longer any energy companies attached to this appliance category*/}

	    for (Integer energyCompanyId : energyCompanyIds) {
	        LiteStarsEnergyCompany energyCompany = getEnergyCompany(energyCompanyId);

            switch (event.getChangeType()) {
	            case ADD:
	                try {
	                    // Create a lite appliance category from the supplied applianceCategoryId
	                    com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
	                        new com.cannontech.database.data.stars.appliance.ApplianceCategory();
	                    appCat.setApplianceCategoryID(applianceCategoryId);
	                    appCat = Transaction.createTransaction( Transaction.RETRIEVE, appCat ).execute();
	                    LiteApplianceCategory liteAppCat = (LiteApplianceCategory) StarsLiteFactory.createLite( appCat.getApplianceCategory() );

	                    // Add the lite appliance category to the supplied energy company
	                    energyCompany.addApplianceCategory(liteAppCat);
	                } catch (TransactionException e) {
	                    CTILogger.error( e.getMessage(), e );
	                }
	                break;

	            case UPDATE:
                    try {
                        // Create a lite appliance category from the supplied applianceCategoryId
                        com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
                            new com.cannontech.database.data.stars.appliance.ApplianceCategory();
                        appCat.setApplianceCategoryID(applianceCategoryId);
                        appCat = Transaction.createTransaction( Transaction.RETRIEVE, appCat ).execute();
                        LiteApplianceCategory liteAppCat = (LiteApplianceCategory) StarsLiteFactory.createLite( appCat.getApplianceCategory() );

                        // Update the lite appliance category to the supplied energy company
                        energyCompany.updateApplianceCategory(liteAppCat);
                    } catch (TransactionException e) {
                        CTILogger.error( e.getMessage(), e );
                    }
	                break;

	            case DELETE:
	                energyCompany.deleteApplianceCategory(applianceCategoryId);
	                break;
	        }
	    }
	    synchronized (this) {
	        webConfigMap = null;
	    }
	}

	private void handleLMProgramChange(DBChangeMsg msg, LiteStarsEnergyCompany energyCompany, LiteLMProgramWebPublishing liteProg) {
		switch( msg.getDbChangeType())
		{
			case ADD:
				// Don't need to do anything
				break;
				
			case UPDATE :
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
				
			case DELETE :
				// Don't need to do anything
				break;
		}
	}
	
	private void handleLMGroupChange(DBChangeMsg msg, LiteStarsEnergyCompany energyCompany, LiteLMProgramWebPublishing liteProg) {
		switch( msg.getDbChangeType())
		{
			case ADD:
				// Don't need to do anything
				break;
				
			case UPDATE :
			case DELETE :
				try {
					// Update group list of the LM program
					com.cannontech.database.db.device.lm.LMProgramDirectGroup[] groups =
							com.cannontech.database.db.device.lm.LMProgramDirectGroup.getAllDirectGroups( new Integer(liteProg.getDeviceID()) );
					
					int[] groupIDs = new int[ groups.length ];
					for (int i = 0; i < groups.length; i++) {
						groupIDs[i] = groups[i].getLmGroupDeviceID().intValue();
					}
					
					int programID = liteProg.getProgramID();
					LiteLMProgramWebPublishing cachedProgram = energyCompany.getProgram(programID);
					cachedProgram.setGroupIDs( groupIDs );
					
					StarsEnrLMProgram program = ServletUtils.getEnrollmentProgram( energyCompany.getStarsEnrollmentPrograms(), programID );
					if (program != null)
						StarsLiteFactory.setAddressingGroups( program, liteProg );
				}
				catch (java.sql.SQLException e) {
					CTILogger.error( e.getMessage(), e );
				}
				
				break;
		}
	}
	
	private void handleYukonUserChange(DBChangeMsg msg, LiteStarsEnergyCompany energyCompany, StarsCustAccountInformation starsAcctInfo) {
		switch( msg.getDbChangeType() ) {
			case ADD:
				// Don't need to do anything
				break;
				
			case UPDATE:
				if (starsAcctInfo.getStarsUser().getUserID() == msg.getId()) {
					LiteYukonUser liteUser = DaoFactory.getYukonUserDao().getLiteYukonUser( msg.getId() );
					starsAcctInfo.setStarsUser( StarsLiteFactory.createStarsUser(liteUser, energyCompany) );
				}
				
				break;
				
			case DELETE:
				// Don't need to do anything
				break;
		}
	}

	private void handleRouteChange(DBChangeMsg msg, LiteStarsEnergyCompany energyCompany) {
		switch( msg.getDbChangeType()) {
			case ADD:
				// Don't need to do anything
				break;
				
			case UPDATE:
				// Don't need to do anything
				break;
				
			case DELETE:
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
	public LiteStarsLMControlHistory getLMControlHistory(int groupID, ReadableInstant startReadableInstant) {
		/* STARS cannot assume that a zero group ID means no control history...should
		 * still show something
		 */
		//if (groupID == CtiUtilities.NONE_ZERO_ID) return null;
		Instant startDateTime = startReadableInstant.toInstant();
	    
		LiteStarsLMControlHistory lmCtrlHist =
				getLMCtrlHistMap().get( new Integer(groupID));
		if (lmCtrlHist == null) lmCtrlHist = new LiteStarsLMControlHistory( groupID );
		
		if (startDateTime != null &&
			 (startDateTime.isBefore(lmCtrlHist.getLastSearchedStartTime()) ||
			   (new Duration(new Instant(lmCtrlHist.getLastSearchedStopTime()),
			                 startDateTime).isLongerThan(CTRL_HIST_CACHE_INVALID_INTERVAL)))) {
			// Clear the cached control history if the request date is earlier than the cache start date,
			// or it is later than the cache stop date and the interval is too long.
			lmCtrlHist = new LiteStarsLMControlHistory( groupID );
		}
		
		int lastSearchedID = LMControlHistoryUtil.getLastLMCtrlHistID();
		
		if (startDateTime != null) {
			if (lmCtrlHist.getLmControlHistory() == null) {
				Date dateFrom = StarsUtils.translateDate( startDateTime.toDate().getTime() );
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
			if (startDateTime.isBefore(lmCtrlHist.getLastSearchedStartTime())) {
				lmCtrlHist.setLastSearchedStartTime(startDateTime.toDate().getTime());
			}
			lmCtrlHist.setLastSearchedStopTime( System.currentTimeMillis() );
		}
		
		if (lmCtrlHist.getLmControlHistory() == null) {
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
		} else {
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
			} else {
				if (lmCtrlHist.getLastControlHistory() == null)
					lmCtrlHist.setLastControlHistory( new LiteLMControlHistory(lastSearchedID) );
			}
		}
		
		getLMCtrlHistMap().put( new Integer(groupID), lmCtrlHist );
		return lmCtrlHist;
	}
}
