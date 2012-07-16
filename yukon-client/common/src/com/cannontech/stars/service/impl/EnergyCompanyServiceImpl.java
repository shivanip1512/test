package com.cannontech.stars.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.events.loggers.StarsEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ExceptionHelper;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.EnergyCompanyNameUnavailableException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.UserNameUnavailableException;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.TransactionException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.company.EnergyCompanyBase;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.user.YukonGroup;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.SiteInformationDao;
import com.cannontech.stars.core.dao.WarehouseDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteApplianceCategory;
import com.cannontech.stars.database.data.lite.LiteLMProgramWebPublishing;
import com.cannontech.stars.database.data.lite.LiteServiceCompany;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompanyFactory;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.database.db.ECToGenericMapping;
import com.cannontech.stars.database.db.hardware.Warehouse;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.stars.model.EnergyCompanyDto;
import com.cannontech.stars.service.DefaultRouteService;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.user.checker.UserCheckerBase;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class EnergyCompanyServiceImpl implements EnergyCompanyService {

    private final String ecAdminLoginGroupExtension = " Admin Grp";
    private Logger log = YukonLogManager.getLogger(EnergyCompanyServiceImpl.class);

    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private ContactDao contactDao;
    @Autowired private ContactNotificationDao contactNotificationDao;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private DefaultRouteService defaultRouteService;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private LiteStarsEnergyCompanyFactory energyCompanyFactory;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private SiteInformationDao siteInformationDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private StarsEventLogService starsEventLogService;
    @Autowired private YukonGroupDao yukonGroupDao;
    @Autowired private YukonListDao yukonListDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private WarehouseDao warehouseDao;
    @Autowired private AccountService accountService;

    @Override
    @Transactional
    public LiteStarsEnergyCompany createEnergyCompany(EnergyCompanyDto energyCompanyDto, LiteYukonUser user,
            Integer parentId) throws WebClientException, TransactionException, CommandExecutionException {
        
        boolean topLevelEc = parentId == null;
        
        /* Check energy company name availability */
        try {
            energyCompanyDao.getEnergyCompanyByName(energyCompanyDto.getName());
            throw new EnergyCompanyNameUnavailableException();
        } catch (NotFoundException e) {/* Ignore */}
        
        /* Check usernames availability */
        if (yukonUserDao.findUserByUsername(energyCompanyDto.getAdminUsername()) != null) {
            throw new UserNameUnavailableException("adminUsername");
        }

        /* Create a privilege group with EnergyCompany and Administrator role */
        LiteYukonGroup primaryOperatorGroup = yukonGroupDao.getLiteYukonGroup(energyCompanyDto.getPrimaryOperatorGroupId());
        String adminGroupName = energyCompanyDto.getName() + ecAdminLoginGroupExtension;
        LiteYukonGroup adminGrp = StarsAdminUtil.createOperatorAdminGroup( adminGroupName, topLevelEc );
        
        /* Create the primary operator login */
        LiteYukonGroup[] adminsGroups = new LiteYukonGroup[] { primaryOperatorGroup, adminGrp };
        LiteYukonUser adminUser = 
                StarsAdminUtil.createOperatorLogin(energyCompanyDto.getAdminUsername(), energyCompanyDto.getAdminPassword1(), LoginStatusEnum.ENABLED, adminsGroups, null);

        /* Create Contact */
        LiteContact contact = new LiteContact(-1, CtiUtilities.STRING_NONE, CtiUtilities.STRING_NONE);
        contactDao.saveContact(contact);
        if (StringUtils.isNotBlank(energyCompanyDto.getEmail())) {
            LiteContactNotification email = new LiteContactNotification(-1, contact.getContactID(), 
                ContactNotificationType.EMAIL.getDefinitionId(), "N", energyCompanyDto.getEmail());
            contactNotificationDao.saveNotification(email);
        }
        
        /* Create Energy Company */
        EnergyCompany energyCompany = new EnergyCompany();
        energyCompany.setName(energyCompanyDto.getName());
        energyCompany.setPrimaryContactId(contact.getContactID());
        energyCompany.setUserId(adminUser.getUserID());
        energyCompanyDao.save(energyCompany);
        
        /* This method doesn't 'create' anything, it just news a LiteStarsEnergyCompany and injects dependencies. */
        LiteStarsEnergyCompany liteEnergyCompany = energyCompanyFactory.createEnergyCompany(energyCompany);
        
        /* This does nothing to StarsDatabaseCache,  I don't know who else would care. */
        dbPersistentDao.processDBChange(new DBChangeMsg(
                            energyCompany.getEnergyCompanyId(),
                            DBChangeMsg.CHANGE_ENERGY_COMPANY_DB,
                            DBChangeMsg.CAT_ENERGY_COMPANY,
                            DbChangeType.ADD));

        /* Set Default Route */
        defaultRouteService.updateDefaultRoute(liteEnergyCompany, energyCompanyDto.getDefaultRouteId(), user);
        
        /* Set Operator Group List */
        List<Integer> operatorGroupIdsList = com.cannontech.common.util.StringUtils.parseIntStringForList(energyCompanyDto.getOperatorGroupIds());
        Iterable<Integer> operatorGroupIds = Iterables.concat(Collections.singleton(energyCompanyDto.getPrimaryOperatorGroupId()), operatorGroupIdsList);
        ecMappingDao.addECToOperatorGroupMapping(energyCompany.getEnergyCompanyId(), operatorGroupIds);
        
        /* Set Residential Customer Group List */
        if (StringUtils.isNotBlank(energyCompanyDto.getResidentialGroupIds())) {
            List<Integer> customerGroupIds = com.cannontech.common.util.StringUtils.parseIntStringForList(energyCompanyDto.getResidentialGroupIds());
            ecMappingDao.addECToResidentialGroupMapping(energyCompany.getEnergyCompanyId(), customerGroupIds);
        }
        
        /* Add as member to parent */
        if (parentId != null) {
            StarsAdminUtil.addMember(starsDatabaseCache.getEnergyCompany(parentId), liteEnergyCompany, adminUser.getUserID());
        }
        
        starsDatabaseCache.addEnergyCompany(liteEnergyCompany);
        
        return liteEnergyCompany;
    }
    
    @Override
    public Set<LiteStarsEnergyCompany> getMemberCandidates(int ecId) {
        Set<LiteStarsEnergyCompany> allSet = Sets.newHashSet(starsDatabaseCache.getAllEnergyCompanies());
        
        Set<LiteStarsEnergyCompany> allAscendants = Sets.newHashSet(ECUtils.getAllAscendants(starsDatabaseCache.getEnergyCompany(ecId)));
        
        Set<LiteStarsEnergyCompany> alreadyMemberCompanies = Sets.newHashSet();
        for (LiteStarsEnergyCompany energyCompany : allSet) {
            if (energyCompany.getParent() != null) {
                alreadyMemberCompanies.add(energyCompany);
            }
        }
        
        Set<LiteStarsEnergyCompany> exclusionSet = Sets.newHashSet(alreadyMemberCompanies);
        exclusionSet.addAll(allAscendants);
        exclusionSet.add(starsDatabaseCache.getDefaultEnergyCompany());
        
        return Sets.difference(allSet, exclusionSet);
    }
    
    @Override
    public boolean canEditEnergyCompany(LiteYukonUser user, int ecId) {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);
        if (!configurationSource.getBoolean(MasterConfigBooleanKeysEnum.DEFAULT_ENERGY_COMPANY_EDIT)
                && yukonEnergyCompanyService.isDefaultEnergyCompany(energyCompany)) {
            return false;
        }
        boolean superUser = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_SUPER_USER, user);
        boolean edit = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_EDIT_ENERGY_COMPANY, user);
        boolean manage = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, user);
        boolean isOperator =energyCompany.getOperatorLoginIDs().contains(user.getUserID());
        boolean isParentOp = isParentOperator(user.getUserID(), ecId);
        
        /* Can edit 
         * IF user is a 'super user'
         * OR user is energy companies operator and has edit energy company role property
         * OR user is operator of one of the parent energy companies 
         *     AND has manage members role property 
         *     AND has edit energy company role property */
        boolean canEdit = superUser || (edit && (isOperator ||(isParentOp && manage)));
        
        return canEdit;
    }
    
    @Override
    public boolean canManageMembers(LiteYukonUser user) {
        boolean superUser = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_SUPER_USER, user);
        return superUser || rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, user);
    }
    
    @Override
    public boolean canCreateMembers(LiteYukonUser user) {
        boolean superUser = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_SUPER_USER, user);
        boolean manageMembers = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, user);
        boolean createAndDelete = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_CREATE_DELETE_ENERGY_COMPANY, user);
        
        return superUser || (manageMembers && createAndDelete);
    }
    
    @Override
    public boolean canDeleteEnergyCompany(LiteYukonUser user, int ecId) {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);
        if (yukonEnergyCompanyService.isDefaultEnergyCompany(energyCompany)) {
            return false;
        }

        boolean superUser = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_SUPER_USER, user);
        boolean isOperator = starsDatabaseCache.getEnergyCompany(ecId).getOperatorLoginIDs().contains(user.getUserID()); 
        
        /* Can delete
         * IF user is a 'super user' && is not an operator of the energy company */
        return superUser && !isOperator;
    }
    
    @Override
    public boolean isParentOperator(int userId, int ecId) {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);
        List<LiteStarsEnergyCompany> allAscendants = ECUtils.getAllAscendants(energyCompany);
        allAscendants.remove(energyCompany); /* Remove this ec since that is a different rp check */
        for (LiteStarsEnergyCompany parentEc : allAscendants) {
            if (parentEc.getOperatorLoginIDs().contains(userId)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isOperator(LiteYukonUser user) {
        for (LiteStarsEnergyCompany ec : starsDatabaseCache.getAllEnergyCompanies()) {
            if (ec.getOperatorLoginIDs().contains(user.getUserID())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isResidentialUser(LiteYukonUser user) {
        return rolePropertyDao.checkRole(YukonRole.RESIDENTIAL_CUSTOMER, user);
    }
    
    @Override
    public void verifyViewPageAccess(LiteYukonUser user, int ecId) {
        if (!configurationSource.getBoolean(MasterConfigBooleanKeysEnum.DEFAULT_ENERGY_COMPANY_EDIT)) {
            LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);
            if (yukonEnergyCompanyService.isDefaultEnergyCompany(energyCompany)) {
                throw new NotAuthorizedException("default energy company is not editable");
            }
        }
        if (rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_SUPER_USER, user)) return;
        /* Check my own and all my anticendants operator login list for this user's id. */
        for (int energyCompanyId : ecMappingDao.getParentEnergyCompanyIds(ecId)) {
            LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
            if(energyCompany.getOperatorLoginIDs().contains(user.getUserID())) return;
        }
        throw new NotAuthorizedException("User " + user.getUsername() + " is not authorized to view energy company with id " + ecId);
    }
    
    @Override
    public void verifyEditPageAccess(LiteYukonUser user, int ecId) {
        if (!canEditEnergyCompany(user, ecId)) {
            throw new NotAuthorizedException("User " + user.getUsername() + " is not authorized to edit energy company with id " + ecId);
        }
    }
    
    @Override
    public UserChecker createEcOperatorChecker() {
        
        UserCheckerBase checker = new UserCheckerBase() {
            @Override
            public boolean check(LiteYukonUser user) {
                boolean superUser = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_SUPER_USER, user);
                boolean isEcOperator = isOperator(user);
                
                if (superUser || isEcOperator) {
                    return true;
                } else {
                    return false;
                }
            };
            
        };
        return checker;
    }
    
    @Override
    @Transactional
    public void deleteEnergyCompany(LiteYukonUser user, int energyCompanyId) {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany( energyCompanyId );
        String energyCompanyName = energyCompany.getName();

        if (yukonEnergyCompanyService.isDefaultEnergyCompany(energyCompany)) {
            throw new IllegalArgumentException("The default energy company cannot be deleted.");
        }

        String dbAlias = CtiUtilities.getDatabaseAlias();
    
        deleteAllOperatorLogins(energyCompany, dbAlias);
        
        deleteAllCustomerAccounts(energyCompany, user);
        
        deleteAllInventory(energyCompanyId, dbAlias);
        
        deleteAllWorkOrders(energyCompanyId, dbAlias);
        
        deleteAllDefaultThermostatSchedules(energyCompany);
        
        deleteAllSubstations(energyCompany);
        
        deleteAllWarehouses(energyCompany);
        
        deleteAllServiceCompanies(energyCompany);
        
        deleteAllApplianceCategories(energyCompany);
        
        deleteAllCustomerSelectionLists(energyCompany);
        
        LiteYukonGroup liteGroup = deleteEnergyCompanyBase(energyCompany, dbAlias);
        
        deleteLoginGroupAndLogin(energyCompany, liteGroup);
        
        starsEventLogService.deleteEnergyCompany(user, energyCompanyName);
    }

    private void deleteAllWarehouses(LiteStarsEnergyCompany energyCompany) {
        List<Warehouse> warehouses = warehouseDao.getAllWarehousesForEnergyCompanyId(energyCompany.getEnergyCompanyId());
        
        for (Warehouse warehouse : warehouses) {
            warehouseDao.delete(warehouse);
        }
    }

    /**
     * @param energyCompany
     * @param liteGroup
     */
    private void deleteLoginGroupAndLogin(LiteStarsEnergyCompany energyCompany, LiteYukonGroup liteGroup) {
        // Delete the default operator login
        int defaultUserId = energyCompany.getUser().getUserID();
        if (defaultUserId != com.cannontech.user.UserUtils.USER_ADMIN_ID &&
                defaultUserId != com.cannontech.user.UserUtils.USER_DEFAULT_ID) {
            
            YukonUser.deleteOperatorLogin(defaultUserId);
            DBChangeMsg dbChange = new DBChangeMsg(defaultUserId,
                                   DBChangeMsg.CHANGE_YUKON_USER_DB,
                                   DBChangeMsg.CAT_YUKON_USER,
                                   DBChangeMsg.CAT_YUKON_USER,
                                   DbChangeType.DELETE);
            dbPersistentDao.processDBChange(dbChange);
        }
        
        // Delete the privilege group of the default operator login as long as it's not a system groupr and ends with with 'Admin Grp' 
        if (liteGroup != null && liteGroup.getGroupName().endsWith(ecAdminLoginGroupExtension) && liteGroup.getGroupID() > -1) {
            YukonGroup dftGroup = new YukonGroup();
            dftGroup.setGroupID(new Integer(liteGroup.getGroupID()));

            dbPersistentDao.performDBChange(dftGroup, TransactionType.DELETE);
        }
    }

    /**
     * @param energyCompanyId
     * @param energyCompany
     * @param stmt
     * @return
     * @throws CommandExecutionException
     * @throws TransactionException
     */
    private LiteYukonGroup deleteEnergyCompanyBase(LiteStarsEnergyCompany energyCompany, String dbAlias) {
        String sql;
        // Delete all other generic mappings
        sql = "DELETE FROM ECToGenericMapping WHERE EnergyCompanyID = " + energyCompany.getEnergyCompanyId();
        SqlStatement stmt = new SqlStatement(sql, dbAlias);
        try {
            stmt.execute();
        } catch (CommandExecutionException e) {
            ExceptionHelper.throwOrWrap(e);
        }        
        // Delete membership from the energy company hierarchy
        if (energyCompany.getParent() != null) {
            try {
                StarsAdminUtil.removeMember( energyCompany.getParent(), energyCompany.getLiteID() );
            } catch (TransactionException e) {
                e.printStackTrace();
            }
        }
        
        // Delete LM groups created for the default route
        if (energyCompany.getDefaultRouteId() >= 0) {
            defaultRouteService.removeDefaultRoute(energyCompany);
        }
        
        // Get the privilege group before the default login is deleted
        LiteYukonGroup liteGroup = energyCompany.getOperatorAdminGroup();
        
        // Delete the energy company!
        
        EnergyCompanyBase ec = new EnergyCompanyBase();
        ec.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
        ec.getEnergyCompany().setPrimaryContactId(energyCompany.getPrimaryContactID());
        
        dbPersistentDao.performDBChange(ec, TransactionType.DELETE);
        
        StarsDatabaseCache.getInstance().deleteEnergyCompany( energyCompany.getLiteID() );
        if (energyCompany.getPrimaryContactID() != CtiUtilities.NONE_ZERO_ID) {
            try {
                LiteContact liteContact = DaoFactory.getContactDao().getContact(energyCompany.getPrimaryContactID());
                DBChangeMsg dbChange = new DBChangeMsg(liteContact.getContactID(),
                                                       DBChangeMsg.CHANGE_CONTACT_DB,
                                                       DBChangeMsg.CAT_CUSTOMERCONTACT,
                                                       DBChangeMsg.CAT_CUSTOMERCONTACT,
                                                       DbChangeType.DELETE);
                dbPersistentDao.processDBChange(dbChange);
            }catch(EmptyResultDataAccessException ignore) {}
        }
        return liteGroup;
    }

    /**
     * @param energyCompany
     */
    private void deleteAllCustomerSelectionLists(
                                                 LiteStarsEnergyCompany energyCompany) {
        // Delete customer selection lists
        List<YukonSelectionList> energyCompanySelectionLists = 
            yukonListDao.getSelectionListsByEnergyCompanyId(energyCompany.getEnergyCompanyId());
        for (YukonSelectionList cList : energyCompanySelectionLists) {
            if (cList.getListId() == LiteStarsEnergyCompany.FAKE_LIST_ID) continue;
            
            Integer listID = new Integer( cList.getListId() );
            com.cannontech.database.data.constants.YukonSelectionList list =
                    new com.cannontech.database.data.constants.YukonSelectionList();
            list.setListID( listID );
            
            dbPersistentDao.performDBChange(list, TransactionType.DELETE);
        }
    }

    /**
     * @param energyCompany
     */
    private void deleteAllApplianceCategories(LiteStarsEnergyCompany energyCompany) {
        // Delete all appliance categories
        for (LiteApplianceCategory liteAppCat : energyCompany.getApplianceCategories()) {
            // Delete programs
            for (LiteLMProgramWebPublishing liteProg : liteAppCat.getPublishedPrograms()) {
                com.cannontech.stars.database.data.LMProgramWebPublishing pubProg =
                    new com.cannontech.stars.database.data.LMProgramWebPublishing();
                pubProg.setProgramID( new Integer(liteProg.getProgramID()) );
                pubProg.getLMProgramWebPublishing().setWebSettingsID( new Integer(liteProg.getWebSettingsID()) );
                dbPersistentDao.performDBChange(pubProg, TransactionType.DELETE);

                energyCompany.deleteProgram( liteProg.getProgramID() );
                StarsDatabaseCache.getInstance().deleteWebConfiguration( liteProg.getWebSettingsID() );
            }
            com.cannontech.stars.database.data.appliance.ApplianceCategory appCat =
                    new com.cannontech.stars.database.data.appliance.ApplianceCategory();
            StarsLiteFactory.setApplianceCategory( appCat.getApplianceCategory(), liteAppCat );
            dbPersistentDao.performDBChange(appCat, TransactionType.DELETE);
            
            energyCompany.deleteApplianceCategory( liteAppCat.getApplianceCategoryID() );
            StarsDatabaseCache.getInstance().deleteWebConfiguration( liteAppCat.getWebConfigurationID() );          
        }
    }

    /**
     * @param energyCompany
     */
    private void deleteAllServiceCompanies(LiteStarsEnergyCompany energyCompany) {
        // Delete all service companies
        for (int i = 0; i < energyCompany.getServiceCompanies().size(); i++) {
            LiteServiceCompany liteCompany = energyCompany.getServiceCompanies().get(i);
            com.cannontech.stars.database.data.report.ServiceCompany company =
                    new com.cannontech.stars.database.data.report.ServiceCompany();
            StarsLiteFactory.setServiceCompany( company, liteCompany );
            
            dbPersistentDao.performDBChange(company, TransactionType.DELETE);

        }
    }

    /**
     * @param energyCompany
     */
    private void deleteAllSubstations(LiteStarsEnergyCompany energyCompany) {
        // Delete all substations, CANNOT cancel the operation from now on
        ECToGenericMapping[] substations = ECToGenericMapping.getAllMappingItems(
                energyCompany.getEnergyCompanyId(), com.cannontech.stars.database.db.Substation.TABLE_NAME );
        if (substations != null) {
            for (int i = 0; i < substations.length; i++) {
                com.cannontech.stars.database.data.Substation substation =
                        new com.cannontech.stars.database.data.Substation();
                substation.setSubstationID( substations[i].getItemID() );
                
                dbPersistentDao.performDBChange(substation, TransactionType.DELETE);
                
            }
        }
    }

    /**
     * @param energyCompany
     */
    private void deleteAllDefaultThermostatSchedules(LiteStarsEnergyCompany energyCompany) {
        // Delete all default thermostat schedules
        List<AccountThermostatSchedule> schedules = accountThermostatScheduleDao.getAllThermostatSchedulesForEC(energyCompany.getEnergyCompanyId());

        for(AccountThermostatSchedule schedule : schedules){
            accountThermostatScheduleDao.deleteById(schedule.getAccountThermostatScheduleId());
        }
    }

    /**
     * @param energyCompanyId
     * @param stmt
     * @throws CommandExecutionException
     */
    private void deleteAllWorkOrders(int energyCompanyId, String dbAlias) {
        try {
            String sql;
            // Delete all work orders that don't belong to any account
            sql = "SELECT WorkOrderID FROM ECToWorkOrderMapping WHERE EnergyCompanyID=" + energyCompanyId;
            SqlStatement stmt = new SqlStatement(sql, dbAlias);
            stmt.execute();
            
            int[] orderIDs = new int[ stmt.getRowCount() ];
            for (int i = 0; i < stmt.getRowCount(); i++) {
                orderIDs[i] = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
            }
                
            for (int i = 0; i < orderIDs.length; i++) {
                
                com.cannontech.stars.database.data.report.WorkOrderBase order =
                        new com.cannontech.stars.database.data.report.WorkOrderBase();
                order.setOrderID( new Integer(orderIDs[i]) );
                
                dbPersistentDao.performDBChange(order, TransactionType.DELETE);
                
            }
        } catch (CommandExecutionException e) {
            ExceptionHelper.throwOrWrap(e);
        }
    }

    /**
     * @param energyCompanyId
     * @param stmt
     * @throws CommandExecutionException
     */
    private void deleteAllInventory(int energyCompanyId, String dbAlias) {
        try {
            String sql;
            // Delete all inventory
            sql = "SELECT InventoryID FROM ECToInventoryMapping WHERE EnergyCompanyID=" + energyCompanyId;
            SqlStatement stmt = new SqlStatement(sql, dbAlias);
            stmt.execute();
            
            int[] invIDs = new int[ stmt.getRowCount() ];
            for (int i = 0; i < stmt.getRowCount(); i++) {
                invIDs[i] = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
            }
            for (int i = 0; i < invIDs.length; i++) {
                
                com.cannontech.stars.database.data.hardware.InventoryBase inventory =
                        new com.cannontech.stars.database.data.hardware.InventoryBase();
                inventory.setInventoryID( new Integer(invIDs[i]) );
                
                dbPersistentDao.performDBChange(inventory, TransactionType.DELETE);
                
            }
        } catch (CommandExecutionException e) {
            ExceptionHelper.throwOrWrap(e);
        }
    }

    /**
     * @param energyCompany
     * @throws TransactionException
     * @throws WebClientException
     */
    private void deleteAllCustomerAccounts(LiteStarsEnergyCompany energyCompany, LiteYukonUser user) {
        List<Integer> accountIds = ecMappingDao.getAccountIds(energyCompany.getLiteID());
        for (Integer accountId : accountIds) {
            accountService.deleteAccount(accountId, user);
        }
    }

    /**
     * @param energyCompanyId
     * @param energyCompany
     * @param dbAlias
     * @return
     * @throws CommandExecutionException
     */
    private void deleteAllOperatorLogins(LiteStarsEnergyCompany energyCompany, String dbAlias) {

        // Delete operator logins (except the default login)
        
        try {
            String sql = "SELECT OperatorLoginID FROM EnergyCompanyOperatorLoginList WHERE EnergyCompanyID=" + energyCompany.getEnergyCompanyId();
            SqlStatement stmt = new SqlStatement( sql, dbAlias );
            stmt.execute();
            
            int[] userIDs = new int[ stmt.getRowCount() ];
            for (int i = 0; i < stmt.getRowCount(); i++)
                userIDs[i] = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
            
            for (int i = 0; i < userIDs.length; i++) {
                if (userIDs[i] == energyCompany.getUser().getUserID()) continue;
                
                try {
                    YukonUser.deleteOperatorLogin(userIDs[i]);
                    DBChangeMsg dbChange = new DBChangeMsg(userIDs[i],
                                                           DBChangeMsg.CHANGE_YUKON_USER_DB,
                                                           DBChangeMsg.CAT_YUKON_USER,
                                                           DBChangeMsg.CAT_YUKON_USER,
                                                           DbChangeType.DELETE);
                                     dbPersistentDao.processDBChange(dbChange);
                } catch (UnsupportedOperationException e) {
                    log.error(e);
                }
            }
        } catch (CommandExecutionException e) {
            ExceptionHelper.throwOrWrap(e);
        }
    }

    public void addRouteToEnergyCompany(int energyCompanyId, final int routeId) {
        
        // remove (i.e. steal) route from children
        final LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        callbackWithSelfAndEachDescendant(energyCompany, new SimpleCallback<LiteStarsEnergyCompany>() {
            @Override
            public void handle(LiteStarsEnergyCompany item) throws Exception {
                if (item.equals(energyCompany)) {
                    // might as well skip ourselves
                    return;
                }
                // just blindly remove it, don't care if it isn't really there
                
                // we don't want the full behavior of removeRouteFromEnergyCompany
                // because it is okay if our children still map routeId as the default
                ecMappingDao.deleteECToRouteMapping(item.getEnergyCompanyId(), routeId);
                dbPersistentDao.processDatabaseChange(DbChangeType.DELETE, 
                                                      DbChangeCategory.ENERGY_COMPANY_ROUTE,
                                                      item.getEnergyCompanyId());
            }
            
        });
        
        ecMappingDao.addEcToRouteMapping(energyCompanyId, routeId);
        
        dbPersistentDao.processDatabaseChange(DbChangeType.ADD, 
                                              DbChangeCategory.ENERGY_COMPANY_ROUTE,
                                              energyCompanyId);

    }

    public int removeRouteFromEnergyCompany(int energyCompanyId, final int routeId) {
        
        // make sure removed route isn't the default route
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        callbackWithSelfAndEachDescendant(energyCompany, new SimpleCallback<LiteStarsEnergyCompany>() {
            @Override
            public void handle(LiteStarsEnergyCompany item) throws Exception {
                if (routeId == item.getDefaultRouteId()) {
                    throw new RuntimeException("cannot delete the default route");
                }
            }
            
        });
        int rowsDeleted = ecMappingDao.deleteECToRouteMapping(energyCompanyId, routeId);
        
        dbPersistentDao.processDatabaseChange(DbChangeType.DELETE, 
                                              DbChangeCategory.ENERGY_COMPANY_ROUTE,
                                              energyCompanyId);

        return rowsDeleted;
    }

    public void addSubstationToEnergyCompany(int energyCompanyId, int substationId) {
        
        ecMappingDao.addECToSubstationMapping(energyCompanyId, substationId);
        
        dbPersistentDao.processDatabaseChange(DbChangeType.ADD, 
                                              DbChangeCategory.ENERGY_COMPANY_SUBSTATIONS,
                                              energyCompanyId);

    }
    
    @Transactional
    public int removeSubstationFromEnergyCompany(int energyCompanyId, int substationId) {
        
        int rowsDeleted = ecMappingDao.deleteECToSubstationMapping(energyCompanyId, substationId);
        siteInformationDao.resetSubstation(substationId);
        
        dbPersistentDao.processDatabaseChange(DbChangeType.DELETE, 
                                              DbChangeCategory.ENERGY_COMPANY_SUBSTATIONS,
                                              energyCompanyId);

        return rowsDeleted;
    }
    
    private static void callbackWithSelfAndEachDescendant(LiteStarsEnergyCompany energyCompany,
                                                   SimpleCallback<LiteStarsEnergyCompany> simpleCallback) {
        try {
            simpleCallback.handle(energyCompany);
        } catch (Exception e) {
            ExceptionHelper.throwOrWrap(e);
        }
        Iterable<LiteStarsEnergyCompany> children = energyCompany.getChildren();
        for (LiteStarsEnergyCompany liteStarsEnergyCompany : children) {
            callbackWithSelfAndEachDescendant(liteStarsEnergyCompany, simpleCallback);
        }
    }
    
}