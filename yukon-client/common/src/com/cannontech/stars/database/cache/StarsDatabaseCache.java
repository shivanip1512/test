package com.cannontech.stars.database.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.database.db.device.lm.LMProgramDirectGroup;
import com.cannontech.database.db.pao.LMControlHistory;
import com.cannontech.database.db.web.YukonWebConfiguration;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.data.appliance.ApplianceCategory;
import com.cannontech.stars.database.data.lite.LiteApplianceCategory;
import com.cannontech.stars.database.data.lite.LiteLMControlHistory;
import com.cannontech.stars.database.data.lite.LiteLMProgramWebPublishing;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompanyFactory;
import com.cannontech.stars.database.data.lite.LiteStarsLMControlHistory;
import com.cannontech.stars.database.data.lite.LiteWebConfiguration;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.database.db.LMProgramWebPublishing;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsEnrLMProgram;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;

public class StarsDatabaseCache implements DBChangeListener {
    
    @Autowired private LiteStarsEnergyCompanyFactory lsecFactory;
    @Autowired private AsyncDynamicDataSource dataSource;
    @Autowired private ApplianceCategoryDao applianceCategoryDao;
    @Autowired private PaoDao paoDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private YukonUserDao userDao;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    
    private Logger log = YukonLogManager.getLogger(StarsDatabaseCache.class);
    
    public static final int DEFAULT_ENERGY_COMPANY_ID = EnergyCompanyDao.DEFAULT_ENERGY_COMPANY_ID;
    private static final Duration CTRL_HIST_CACHE_INVALID_INTERVAL = Duration.standardDays(7);
    
    private List<LiteStarsEnergyCompany> energyCompanies;
    
    private ConcurrentMap<Integer, LiteWebConfiguration> webConfigMap;
    
    // User id to user
    private Map<Integer, StarsYukonUser> starsYukonUsers;
    
    // Group id to control history
    private Map<Integer, LiteStarsLMControlHistory> lmCtrlHists;
    
    public synchronized static StarsDatabaseCache getInstance() {
        StarsDatabaseCache instance = YukonSpringHook.getBean("starsDatabaseCache", StarsDatabaseCache.class);
        return instance;
    }
    
    @PostConstruct
    public void init() {
        dataSource.addDBChangeListener(this);
        dataSource.addDatabaseChangeEventListener(DbChangeCategory.APPLIANCE, new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                handleApplianceCategoryChange(event);
            }
        });
    }
    
    public void loadData() {
        getAllWebConfigurations();
        getAllEnergyCompanies();
    }
    
    public synchronized List<LiteStarsEnergyCompany> getAllEnergyCompanies() {
        
        if (energyCompanies == null) {
            energyCompanies = new ArrayList<>();
            
            EnergyCompany[] companies = EnergyCompany.getEnergyCompanies();
            if (companies == null) {
                return null;
            }
            
            synchronized (energyCompanies) {
                for (EnergyCompany energyCompany : companies) {
                    energyCompanies.add(lsecFactory.createEnergyCompany(energyCompany));
                }
            }
            
            log.info("All energy companies loaded");
        }
        
        return energyCompanies;
    }
    
    private synchronized Map<Integer, LiteWebConfiguration> getAllWebConfigurations() {
        
        if (webConfigMap == null) {
            webConfigMap = new ConcurrentHashMap<>();
            
            YukonWebConfiguration[] webConfigs = YukonWebConfiguration.getAllCustomerWebConfigurations();
            for (YukonWebConfiguration config : webConfigs) {
                LiteWebConfiguration webConfiguration =
                    (LiteWebConfiguration) StarsLiteFactory.createLite(config);
                webConfigMap.put(webConfiguration.getConfigID(), webConfiguration);
            }
            
            log.info("All customer web configurations loaded");
        }
        
        return webConfigMap;
    }
    
    public synchronized Map<Integer, StarsYukonUser> getAllStarsYukonUsers() {
        
        if (starsYukonUsers == null) {
            starsYukonUsers = new Hashtable<>();
        }
        return starsYukonUsers;
    }
    
    /**
     * @deprecated To get a LiteStarsEnergyCompany by user,
     *             use EnergyCompanyDao.getEnergyCompanyByOperator(),
     *             then StarsDatabaseCache.getEnergyCompany(YukonEnergyCompany yukonEnergyCompany).
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
            
            final Map<Integer, LiteStarsEnergyCompany> map = new HashMap<>(companies.size());
            for (LiteStarsEnergyCompany lsec : companies) {
                map.put(lsec.getEnergyCompanyId(), lsec);
            }
            
            return map;
        }
    }
    
    public LiteStarsEnergyCompany getEnergyCompany(YukonEnergyCompany yec) {
        return getEnergyCompany(yec.getEnergyCompanyId());
    }
    
    public LiteStarsEnergyCompany getEnergyCompany(int ecId) {
        
        List<LiteStarsEnergyCompany> companies = getAllEnergyCompanies();
        synchronized (companies) {
            for (LiteStarsEnergyCompany company : companies) {
                if (company.getEnergyCompanyId() == ecId) {
                    return company;
                }
            }
        }
        
        return null;
    }
    
    public void addEnergyCompany(LiteStarsEnergyCompany lsec) {
        
        List<LiteStarsEnergyCompany> companies = getAllEnergyCompanies();
        synchronized (companies) {
            companies.add(lsec);
        }
    }
    
    public void deleteEnergyCompany(int ecId) {
        
        List<LiteStarsEnergyCompany> companies = getAllEnergyCompanies();
        synchronized (companies) {
            for (LiteStarsEnergyCompany company : companies) {
                if (company.getEnergyCompanyId() == ecId) {
                    companies.remove(company);
                    return;
                }
            }
        }
    }
    
    public LiteStarsEnergyCompany getDefaultEnergyCompany() {
        return getEnergyCompany(DEFAULT_ENERGY_COMPANY_ID);
    }
    
    /**
     * @deprecated Use WebConfigurationDao.getById.
     */
    @Deprecated
    public LiteWebConfiguration getWebConfiguration(int configId) {
        
        Map<Integer, LiteWebConfiguration> configs = getAllWebConfigurations();
        LiteWebConfiguration result = configs.get(configId);
        
        return result;
    }
    
    public void addWebConfiguration(LiteWebConfiguration config) {
        Map<Integer, LiteWebConfiguration> configs = getAllWebConfigurations();
        configs.put(config.getConfigID(), config);
    }
    
    public LiteWebConfiguration deleteWebConfiguration(int configId) {
        
        Map<Integer, LiteWebConfiguration> configs = getAllWebConfigurations();
        LiteWebConfiguration result = configs.remove(configId);
        
        return result;
    }
    
    public boolean isStarsUser(final LiteYukonUser user) {
        
        final Map<Integer, StarsYukonUser> starsUsers = getAllStarsYukonUsers();
        synchronized (starsUsers) {
            StarsYukonUser starsUser = starsUsers.get(user.getUserID());
            boolean result = starsUser != null;
            return result;
        }
    }
    
    @Deprecated
    public StarsYukonUser getStarsYukonUser(LiteYukonUser user) {
        
        Map<Integer, StarsYukonUser> starsUsers = getAllStarsYukonUsers();
        Integer userID = new Integer(user.getUserID());
        
        synchronized (starsUsers) {
            StarsYukonUser starsUser = starsUsers.get(userID);
            if (starsUser != null) {
                return starsUser;
            }
        }
        
        try {
            StarsYukonUser starsUser = StarsYukonUser.newInstance(user);
            synchronized (starsUsers) {
                starsUsers.put(userID, starsUser);
            }
            return starsUser;
        } catch (InstantiationException ie) {
            log.warn(user.getUsername()
                + " is not a STARS user.  STARS data will not be available for this session.");
        }
        
        return null;
    }
    
    public void deleteStarsYukonUser(int userId) {
        
        Map<Integer, StarsYukonUser> starsUsers = getAllStarsYukonUsers();
        synchronized (starsUsers) {
            starsUsers.remove(new Integer(userId));
        }
    }
    
    @Override
    public void dbChangeReceived(DBChangeMsg msg) {
        
        log.debug(" ## DBChangeMsg ##\n" + msg);
        
        if (msg.getDatabase() == DBChangeMsg.CHANGE_PAO_DB) {
            
            if (msg.getDbChangeType() == DbChangeType.DELETE) {
                log.debug("DBChangeMsg for a deleted PAO: " + msg);
                return;
            }
            
            LiteYukonPAObject litePao;
            try {
                litePao = paoDao.getLiteYukonPAO(msg.getId());
            } catch (NotFoundException e) {
                log.debug("DBChangeMsg ignored for PAO: #" + msg.getId() + " because it no longer exists. " + msg);
                return;
            }
            
            for (LiteStarsEnergyCompany ec : getAllEnergyCompanies()) {
                
                PaoType paoType = litePao.getPaoType();
                if (paoType.getPaoCategory() == PaoCategory.ROUTE) {
                    boolean singleEc =
                        ecSettingDao.getBoolean(EnergyCompanySettingType.SINGLE_ENERGY_COMPANY, ec.getEnergyCompanyId());
                    if (!singleEc) {
                        handleRouteChange(msg, ec);
                    }
                } else if (paoType.isDirectProgram()) {
                    if (ec.getPrograms() != null) {
                        for (LiteLMProgramWebPublishing webProgram : ec.getPrograms()) {
                            if (webProgram.getDeviceID() == msg.getId()) {
                                handleLMProgramChange(msg, ec, webProgram);
                                return;
                            }
                        }
                    }
                } else if (paoType.isLoadGroup()) {
                    if (ec.getPrograms() != null) {
                        StarsEnrollmentPrograms categories = ec.getStarsEnrollmentPrograms();
                        for (LiteLMProgramWebPublishing webProgram : ec.getPrograms()) {
                            boolean groupFound = false;
                            
                            for (int groupId : webProgram.getGroupIDs()) {
                                if (groupId == msg.getId()) {
                                    handleLMGroupChange(msg, ec, webProgram);
                                    groupFound = true;
                                    break;
                                }
                            }
                            
                            if (!groupFound) {
                                // Program could contain a macro group, while a LM group in that macro group
                                // is changed
                                StarsEnrLMProgram starsProg =
                                    ServletUtils.getEnrollmentProgram(categories, webProgram.getProgramID());
                                for (int k = 0; k < starsProg.getAddressingGroupCount(); k++) {
                                    if (starsProg.getAddressingGroup(k).getEntryID() == msg.getId()) {
                                        handleLMGroupChange(msg, ec, webProgram);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (msg.getDatabase() == DBChangeMsg.CHANGE_YUKON_USER_DB) {
            
            if (msg.getCategory().equals(DBChangeMsg.CAT_YUKON_USER)) {
                
                LiteContact liteContact = userDao.getLiteContact(msg.getId());
                if (liteContact != null) {
                    
                    CustomerAccount account = null;
                    int contactId = liteContact.getContactID();
                    try {
                        account = customerAccountDao.getAccountByContactId(contactId);
                    } catch (EmptyResultDataAccessException e) {
                        log.warn("Unable to find CustomerAccount for contact id: " + contactId, e);
                    }
                    if (account != null) {
                        
                        LiteStarsEnergyCompany lsec = ecMappingDao.getCustomerAccountEC(account);
                        StarsCustAccountInformation accountInfo =
                            lsec.getStarsCustAccountInformation(account.getAccountId());
                        if (accountInfo != null && accountInfo.getStarsUser() != null) {
                            handleYukonUserChange(msg, lsec, accountInfo);
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
        
        List<Integer> ecIds = applianceCategoryDao.getEnergyCompaniesByApplianceCategoryId(appCatId);
        for (Integer ecId : ecIds) {
            
            LiteStarsEnergyCompany lsec = getEnergyCompany(ecId);
            
            switch (msg.getDbChangeType()) {
            case ADD:
                try {
                    // Create a lite appliance category from the supplied applianceCategoryId
                    ApplianceCategory appCat = new ApplianceCategory();
                    appCat.setApplianceCategoryID(appCatId);
                    appCat = Transaction.createTransaction(Transaction.RETRIEVE, appCat).execute();
                    LiteApplianceCategory liteAppCat =
                        (LiteApplianceCategory) StarsLiteFactory.createLite(appCat.getApplianceCategory());
                    LiteLMProgramWebPublishing liteLMProgWebPub =
                        (LiteLMProgramWebPublishing) StarsLiteFactory.createLite(lmProgramWebPublishing);
                    
                    // Add the lite appliance category to the supplied energy company
                    lsec.addProgram(liteLMProgWebPub, liteAppCat);
                } catch (TransactionException e) {
                    log.error(e.getMessage(), e);
                }
                break;
                
            case UPDATE:
                try {
                    // Create a lite appliance category from the supplied applianceCategoryId
                    ApplianceCategory appCat = new ApplianceCategory();
                    appCat.setApplianceCategoryID(appCatId);
                    appCat = Transaction.createTransaction(Transaction.RETRIEVE, appCat).execute();
                    LiteApplianceCategory liteAppCat =
                        (LiteApplianceCategory) StarsLiteFactory.createLite(appCat.getApplianceCategory());
                    LiteLMProgramWebPublishing liteLMProgWebPub =
                        (LiteLMProgramWebPublishing) StarsLiteFactory.createLite(lmProgramWebPublishing);
                    
                    // Add the lite appliance category to the supplied energy company
                    lsec.updateProgram(liteLMProgWebPub, liteAppCat);
                } catch (TransactionException e) {
                    log.error(e.getMessage(), e);
                }
                break;
                
            case DELETE:
                lsec.deleteProgram(lmProgramWebPublishing.getProgramID());
                break;
                
            case NONE:
                // Don't need to do anything
                break;
            }
        }
        
    }
    
    private synchronized void handleWebConfigurationChange(DBChangeMsg msg) {
        webConfigMap = null;
    }
    
    private void handleApplianceCategoryChange(DatabaseChangeEvent event) {
        
        int applianceCategoryId = event.getPrimaryKey();
        
        List<Integer> ecIds = new ArrayList<>();
        try {
            ecIds = applianceCategoryDao.getEnergyCompaniesByApplianceCategoryId(applianceCategoryId);
        } catch (NotFoundException ex) {
            // There are no longer any energy companies attached to this category
        }
        
        for (Integer ecId : ecIds) {
            
            LiteStarsEnergyCompany lsec = getEnergyCompany(ecId);
            
            switch (event.getChangeType()) {
            case ADD:
                try {
                    // Create a lite appliance category from the supplied applianceCategoryId
                    ApplianceCategory appCat = new ApplianceCategory();
                    appCat.setApplianceCategoryID(applianceCategoryId);
                    appCat = Transaction.createTransaction(Transaction.RETRIEVE, appCat).execute();
                    LiteApplianceCategory liteAppCat =
                        (LiteApplianceCategory) StarsLiteFactory.createLite(appCat.getApplianceCategory());
                    
                    // Add the lite appliance category to the supplied energy company
                    lsec.addApplianceCategory(liteAppCat);
                } catch (TransactionException e) {
                    log.error(e.getMessage(), e);
                }
                break;
                
            case UPDATE:
                try {
                    // Create a lite appliance category from the supplied applianceCategoryId
                    ApplianceCategory appCat = new ApplianceCategory();
                    appCat.setApplianceCategoryID(applianceCategoryId);
                    appCat = Transaction.createTransaction(Transaction.RETRIEVE, appCat).execute();
                    LiteApplianceCategory liteAppCat =
                        (LiteApplianceCategory) StarsLiteFactory.createLite(appCat.getApplianceCategory());
                    
                    // Update the lite appliance category to the supplied energy company
                    lsec.updateApplianceCategory(liteAppCat);
                } catch (TransactionException e) {
                    log.error(e.getMessage(), e);
                }
                break;
                
            case DELETE:
                lsec.deleteApplianceCategory(applianceCategoryId);
                break;
                
            case NONE:
                // Don't need to do anything
                break;
            }
        }
        synchronized (this) {
            webConfigMap = null;
        }
    }
    
    private void handleLMProgramChange(DBChangeMsg msg, LiteStarsEnergyCompany lsec, 
            LiteLMProgramWebPublishing webProgram) {
        
        switch (msg.getDbChangeType()) {
        case ADD:
            // Don't need to do anything
            break;
            
        case UPDATE:
            try {
                // Update group list of the LM program
                LMProgramDirectGroup[] groups = LMProgramDirectGroup.getAllDirectGroups(webProgram.getDeviceID());
                
                int[] groupIDs = new int[groups.length];
                for (int i = 0; i < groups.length; i++) {
                    groupIDs[i] = groups[i].getLmGroupDeviceID();
                }
                webProgram.setGroupIDs(groupIDs);
                
                StarsEnrollmentPrograms programs = lsec.getStarsEnrollmentPrograms();
                StarsEnrLMProgram program = ServletUtils.getEnrollmentProgram(programs, webProgram.getProgramID());
                if (program != null) {
                    try {
                        program.setYukonName(paoDao.getYukonPAOName(webProgram.getDeviceID()));
                    } catch (NotFoundException e) {
                        program.setYukonName(CtiUtilities.STRING_NONE);
                    }
                    StarsLiteFactory.setAddressingGroups(program, webProgram);
                }
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
            break;
            
        case DELETE:
            // Don't need to do anything
            break;
            
        case NONE:
            // Don't need to do anything
            break;
        }
    }
    
    private void handleLMGroupChange(DBChangeMsg msg, LiteStarsEnergyCompany lsec, 
            LiteLMProgramWebPublishing webProgram) {
        
        switch (msg.getDbChangeType()) {
        case ADD:
            // Don't need to do anything
            break;
            
        case UPDATE:
        case DELETE:
            try {
                // Update group list of the LM program
                LMProgramDirectGroup[] groups = LMProgramDirectGroup.getAllDirectGroups(webProgram.getDeviceID());
                
                int[] groupIDs = new int[groups.length];
                for (int i = 0; i < groups.length; i++) {
                    groupIDs[i] = groups[i].getLmGroupDeviceID();
                }
                
                int programId = webProgram.getProgramID();
                LiteLMProgramWebPublishing cachedProgram = lsec.getProgram(programId);
                cachedProgram.setGroupIDs(groupIDs);
                
                StarsEnrollmentPrograms programs = lsec.getStarsEnrollmentPrograms();
                StarsEnrLMProgram program = ServletUtils.getEnrollmentProgram(programs, programId);
                if (program != null) {
                    StarsLiteFactory.setAddressingGroups(program, webProgram);
                }
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
            
            break;
            
        case NONE:
            // Don't need to do anything
            break;
        }
    }
    
    private void handleYukonUserChange(DBChangeMsg msg, LiteStarsEnergyCompany lsec,
            StarsCustAccountInformation accountInfo) {
        switch (msg.getDbChangeType()) {
        case ADD:
            // Don't need to do anything
            break;
            
        case UPDATE:
            if (accountInfo.getStarsUser().getUserID() == msg.getId()) {
                LiteYukonUser user = userDao.getLiteYukonUser(msg.getId());
                accountInfo.setStarsUser(user);
            }
            break;
            
        case DELETE:
            // Don't need to do anything
            break;
            
        case NONE:
            // Don't need to do anything
            break;
        }
    }
    
    private void handleRouteChange(DBChangeMsg msg, LiteStarsEnergyCompany lsec) {
        
        switch (msg.getDbChangeType()) {
        case ADD:
            // Don't need to do anything
            break;
            
        case UPDATE:
            // Don't need to do anything
            break;
            
        case DELETE:
            try {
                StarsAdminUtil.removeRoute(lsec, msg.getId());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            break;
            
        case NONE:
            // Don't need to do anything
            break;
        }
    }
    
    private synchronized Map<Integer, LiteStarsLMControlHistory> getLMCtrlHistMap() {
        
        if (lmCtrlHists == null) {
            lmCtrlHists = new Hashtable<>();
        }
        
        return lmCtrlHists;
    }
    
    /**
     * Get control history from a given start date (control summary will always be returned as well).
     * To get only the control summary, set start date to null; to get the complete control history,
     * set start date to new java.util.Date(0).
     */
    public LiteStarsLMControlHistory getLMControlHistory(int groupId, ReadableInstant start) {
        
        Instant startDateTime = start.toInstant();
        
        LiteStarsLMControlHistory lmCtrlHist = getLMCtrlHistMap().get(groupId);
        if (lmCtrlHist == null) {
            lmCtrlHist = new LiteStarsLMControlHistory(groupId);
        }
        
        // Clear out the cache if we are loading older data. Note this means we never try to fill in gaps in
        // old data and always load new. Also if the data we are requesting is more than than 7 days newer
        // than what we already have loaded we will throw out the old data and just start loading new
        // information. Presumably this prevents us from trying to fill in days/months of data we don't really
        // need.
        if (startDateTime != null
            && (startDateTime.isBefore(lmCtrlHist.getLastSearchedStartTime()) || (new Duration(new Instant(
                lmCtrlHist.getLastSearchedStopTime()), startDateTime).isLongerThan(CTRL_HIST_CACHE_INVALID_INTERVAL)))) {
            lmCtrlHist = new LiteStarsLMControlHistory(groupId);
        }
        
        int maxControlHistID = LMControlHistoryUtil.getLastLMCtrlHistID();
        
        if (startDateTime != null) {
            
            if (lmCtrlHist.getLmControlHistory() == null) {
                // Either we have never loaded or we are loading older data than what is currently cached.
                // Load everything after the given date.
                Date dateFrom = StarsUtils.translateDate(startDateTime.toDate().getTime());
                LMControlHistory[] ctrlHist =
                    LMControlHistoryUtil.getLMControlHistory(groupId, dateFrom, null);
                
                List<LiteBase> ctrlHistList = new ArrayList<>();
                for (int i = 0; i < ctrlHist.length; i++) {
                    ctrlHistList.add(StarsLiteFactory.createLite(ctrlHist[i]));
                }
                
                lmCtrlHist.setLmControlHistory(ctrlHistList);
            } else {
                // We already have cached data and do not need to read earlier data, so load everything that
                // has been added since last time we updated the cache.
                LMControlHistory[] ctrlHist =
                    LMControlHistoryUtil.getLMControlHistory(groupId, lmCtrlHist.getLastSearchedCtrlHistID());
                for (int i = 0; i < ctrlHist.length; i++) {
                    lmCtrlHist.getLmControlHistory().add(StarsLiteFactory.createLite(ctrlHist[i]));
                }
            }
            
            lmCtrlHist.setLastSearchedCtrlHistID(maxControlHistID);
            
            if (startDateTime.isBefore(lmCtrlHist.getLastSearchedStartTime())) {
                lmCtrlHist.setLastSearchedStartTime(startDateTime.toDate().getTime());
            }
            
            lmCtrlHist.setLastSearchedStopTime(System.currentTimeMillis());
        }
        
        if (lmCtrlHist.getLmControlHistory() == null) {
            lmCtrlHist.setLmControlHistory(new ArrayList<LiteBase>());
        }
        
        // This block sets the lastControlHistory object and makes sure lastSearchedCtrlHistId is correct.
        // Loading cache objects is complete but the lastControlHistory object may be loaded if our cache is
        // empty for this group.
        if (lmCtrlHist.getLmControlHistory().size() > 0) {
            
            LiteLMControlHistory liteCtrlHist =
                (LiteLMControlHistory) lmCtrlHist.getLmControlHistory().get(lmCtrlHist.getLmControlHistory().size() - 1);
            
            maxControlHistID = Math.max(liteCtrlHist.getLmCtrlHistID(), maxControlHistID);
            lmCtrlHist.setLastSearchedCtrlHistID(maxControlHistID);
            
            // As far as I can tell the entire purpose of this is to set maxControlHistID on the
            // lastControlHistory object.
            LiteLMControlHistory lastCtrlHist = new LiteLMControlHistory(maxControlHistID);
            lastCtrlHist.setStartDateTime(liteCtrlHist.getStartDateTime());
            lastCtrlHist.setStopDateTime(liteCtrlHist.getStopDateTime());
            lastCtrlHist.setCurrentDailyTime(liteCtrlHist.getCurrentDailyTime());
            lastCtrlHist.setCurrentMonthlyTime(liteCtrlHist.getCurrentMonthlyTime());
            lastCtrlHist.setCurrentSeasonalTime(liteCtrlHist.getCurrentSeasonalTime());
            lastCtrlHist.setCurrentAnnualTime(liteCtrlHist.getCurrentAnnualTime());
            lmCtrlHist.setLastControlHistory(lastCtrlHist);
            
        } else {
            
            int startCtrlHistId = 0;
            if (lmCtrlHist.getLastControlHistory() != null) {
                startCtrlHistId = lmCtrlHist.getLastControlHistory().getLmCtrlHistID();
            }
            
            LMControlHistory lastCtrlHist =
                LMControlHistoryUtil.getLastLMControlHistory(groupId, startCtrlHistId);
            
            if (lastCtrlHist != null) {
                
                LiteLMControlHistory liteCtrlHist = (LiteLMControlHistory) StarsLiteFactory.createLite(lastCtrlHist);
                
                if (liteCtrlHist.getLmCtrlHistID() < maxControlHistID) {
                    liteCtrlHist.setLmCtrlHistID(maxControlHistID);
                }
                
                lmCtrlHist.setLastControlHistory(liteCtrlHist);
                
            } else {
                if (lmCtrlHist.getLastControlHistory() == null) {
                    lmCtrlHist.setLastControlHistory(new LiteLMControlHistory(maxControlHistID));
                }
            }
        }
        
        getLMCtrlHistMap().put(groupId, lmCtrlHist);
        
        return lmCtrlHist;
    }
    
}