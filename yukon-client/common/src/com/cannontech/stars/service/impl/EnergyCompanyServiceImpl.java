package com.cannontech.stars.service.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.naming.ConfigurationException;

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
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.EnergyCompanyNameUnavailableException;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.TransactionException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.company.EnergyCompanyBase;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.user.YukonGroup;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.database.db.user.UserGroup;
import com.cannontech.message.DbChangeManager;
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
    private Logger log = YukonLogManager.getLogger(EnergyCompanyServiceImpl.class);

    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private ContactDao contactDao;
    @Autowired private ContactNotificationDao contactNotificationDao;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private DefaultRouteService defaultRouteService;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private LiteStarsEnergyCompanyFactory energyCompanyFactory;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private SiteInformationDao siteInformationDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private StarsEventLogService starsEventLogService;
    @Autowired private YukonListDao yukonListDao;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private WarehouseDao warehouseDao;
    @Autowired private AccountService accountService;
    @Autowired private UserGroupDao userGroupDao ;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    @Transactional(rollbackFor = {ConfigurationException.class, RuntimeException.class})
    public LiteStarsEnergyCompany createEnergyCompany(EnergyCompanyDto energyCompanyDto, LiteYukonUser user, Integer parentId)
    throws TransactionException, CommandExecutionException, ConfigurationException, SQLException {
        
        boolean topLevelEc = parentId == null;
        
        /* Check energy company name availability */
        if(energyCompanyDao.findEnergyCompanyByName(energyCompanyDto.getName()) != null) {
            throw new EnergyCompanyNameUnavailableException();
        }

        /* Create a privilege group with EnergyCompany and Administrator role */
        UserGroup ecAdminUserGrp = 
                StarsAdminUtil.createOperatorAdminUserGroup(energyCompanyDto.getName(), energyCompanyDto.getPrimaryOperatorUserGroupId(), topLevelEc);
        
        /* Create the primary operator login */
        LiteYukonUser adminUser = 
                StarsAdminUtil.createOperatorLogin(energyCompanyDto.getAdminUsername(), energyCompanyDto.getAdminPassword1(), LoginStatusEnum.ENABLED, ecAdminUserGrp, null);

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
        dbChangeManager.processDbChange(energyCompany.getEnergyCompanyId(), 
                                        DBChangeMsg.CHANGE_ENERGY_COMPANY_DB,
                                        DBChangeMsg.CAT_ENERGY_COMPANY,
                                        DbChangeType.ADD);

        /* Set Default Route */
        defaultRouteService.updateDefaultRoute(liteEnergyCompany, energyCompanyDto.getDefaultRouteId(), user);
        
        /* Set Operator Group List */
        List<Integer> operatorUserGroupIdsList = com.cannontech.common.util.StringUtils.parseIntStringForList(energyCompanyDto.getOperatorUserGroupIds());
        Iterable<Integer> operatorUserGroupIds = Iterables.concat(Collections.singleton(energyCompanyDto.getPrimaryOperatorUserGroupId()), operatorUserGroupIdsList);
        ecMappingDao.addECToOperatorUserGroupMapping(energyCompany.getEnergyCompanyId(), operatorUserGroupIds);
        
        /* Set Residential Customer Group List */
        if (StringUtils.isNotBlank(energyCompanyDto.getResidentialUserGroupIds())) {
            List<Integer> customerUserGroupIds = com.cannontech.common.util.StringUtils.parseIntStringForList(energyCompanyDto.getResidentialUserGroupIds());
            ecMappingDao.addECToResidentialUserGroupMapping(energyCompany.getEnergyCompanyId(), customerUserGroupIds);
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
    public UserChecker createEcOperatorOrSuperUserChecker() {
        
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
    
        deleteOperatorLoginList(energyCompany);
        
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
        
        deletePrivilegeGroupsAndDefaultOperatorLogin(energyCompany, liteGroup);
        
        starsEventLogService.deleteEnergyCompany(user, energyCompanyName);
        log.info("Deleting energy company " + energyCompany.getName() + " id# " + energyCompanyId + " completed.");
    }

    private void deleteAllWarehouses(LiteStarsEnergyCompany energyCompany) {
        List<Warehouse> warehouses = warehouseDao.getAllWarehousesForEnergyCompanyId(energyCompany.getEnergyCompanyId());
        
        for (Warehouse warehouse : warehouses) {
            log.info("Deleting warehouse " + warehouse.getWarehouseName() + " id# " + warehouse.getWarehouseID() + " for energy company : " + energyCompany.getName());
            warehouseDao.delete(warehouse);
        }
    }

    /**
     * @param energyCompany
     * @param liteGroup
     */
    private void deletePrivilegeGroupsAndDefaultOperatorLogin(LiteStarsEnergyCompany energyCompany, LiteYukonGroup liteGroup) {
        // Delete the default operator login
        int defaultUserId = energyCompany.getUser().getUserID();
        if (defaultUserId != com.cannontech.user.UserUtils.USER_ADMIN_ID &&
                defaultUserId != com.cannontech.user.UserUtils.USER_DEFAULT_ID) {
            
            YukonUser.deleteOperatorLogin(defaultUserId);
            dbChangeManager.processDbChange(defaultUserId,
                                            DBChangeMsg.CHANGE_YUKON_USER_DB,
                                            DBChangeMsg.CAT_YUKON_USER,
                                            DBChangeMsg.CAT_YUKON_USER,
                                            DbChangeType.DELETE);
        }
        
        // Delete the privilege group of the default operator login as long as it's not a system group and ends with with 'Admin Grp' 
        if (liteGroup != null && liteGroup.getGroupName().endsWith(StarsAdminUtil.ROLE_GROUP_EXTENSION) && liteGroup.getGroupID() > -1) {
            YukonGroup dftGroup = new YukonGroup();
            dftGroup.setGroupID(new Integer(liteGroup.getGroupID()));

            log.info("Deleting role group id# " + dftGroup.getGroupID());
            dbPersistentDao.performDBChange(dftGroup, TransactionType.DELETE);
        }
        //Find and delete a privilege user group
        LiteUserGroup liteUserGroup = userGroupDao.findLiteUserGroupByUserGroupName(energyCompany.getName() + " " + StarsAdminUtil.USER_GROUP_EXTENSION);
        if(liteUserGroup != null){
            UserGroup userGroup = new UserGroup();
            userGroup.setUserGroupId(liteUserGroup.getUserGroupId());
            
            log.info("Deleting user group id# " + liteUserGroup.getUserGroupId());
            dbPersistentDao.performDBChange(userGroup, TransactionType.DELETE);
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
        log.info("Deleting energy company base id# " + energyCompany.getEnergyCompanyId());
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
        
        log.info("Deleting energy company id# " + energyCompany.getEnergyCompanyId());
        dbPersistentDao.performDBChange(ec, TransactionType.DELETE);
        
        StarsDatabaseCache.getInstance().deleteEnergyCompany( energyCompany.getLiteID() );
        if (energyCompany.getPrimaryContactID() != CtiUtilities.NONE_ZERO_ID) {
            try {
                LiteContact liteContact = DaoFactory.getContactDao().getContact(energyCompany.getPrimaryContactID());
                dbChangeManager.processDbChange(liteContact.getContactID(),
                                                DBChangeMsg.CHANGE_CONTACT_DB,
                                                DBChangeMsg.CAT_CUSTOMERCONTACT,
                                                DBChangeMsg.CAT_CUSTOMERCONTACT,
                                                DbChangeType.DELETE);
            }catch(EmptyResultDataAccessException ignore) {}
        }
        return liteGroup;
    }

    /**
     * @param energyCompany
     */
    private void deleteAllCustomerSelectionLists(LiteStarsEnergyCompany energyCompany) {
        // Delete customer selection lists
        List<YukonSelectionList> energyCompanySelectionLists = 
                yukonListDao.getSelectionListsByEnergyCompanyId(energyCompany.getEnergyCompanyId());
        for (YukonSelectionList cList : energyCompanySelectionLists) {
            if (cList.getListId() == LiteStarsEnergyCompany.FAKE_LIST_ID) continue;

            com.cannontech.database.data.constants.YukonSelectionList list =
                    new com.cannontech.database.data.constants.YukonSelectionList();
            list.setListID(cList.getListId());
            log.info("Deleting customer selection list id# " + cList.getListId());

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
            log.info("Deleting appliance category id# " + appCat.getApplianceCategory().getApplianceCategoryID());
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
            log.info("Deleting service company id# " + liteCompany.getCompanyID());
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
                substation.setSubstationID(substations[i].getItemID());
                log.info("Deleting Substation id# " + substations[i].getItemID());
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
            log.info("Deleting Thermostat Schedule " + schedule.getScheduleName() + " id# " + schedule.getAccountThermostatScheduleId());
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
                order.setOrderID(orderIDs[i]);
                log.info("Deleting Work Order id# " + orderIDs[i]);
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
                inventory.setInventoryID(invIDs[i]);
                log.info("Deleting inventory id# " + invIDs[i]);
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
     * @param energyCompany
     * @return
     */
    private void deleteOperatorLoginList(LiteStarsEnergyCompany energyCompany) {
        // Delete entries in EnergyCompanyOperatorLoginList for EnergyCompanyId exclude the default operator login
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM EnergyCompanyOperatorLoginList");
        sql.append("WHERE EnergyCompanyId").eq(energyCompany.getEnergyCompanyId());
        sql.append("AND OperatorLoginID").neq(energyCompany.getUser().getUserID());
        yukonJdbcTemplate.update(sql); 
        log.info("Deleting Operator Login list for energy company " + energyCompany.getName() + " id# " + energyCompany.getEnergyCompanyId());
    }

    @Override
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
                dbChangeManager.processDbChange(DbChangeType.DELETE, 
                                                DbChangeCategory.ENERGY_COMPANY_ROUTE,
                                                item.getEnergyCompanyId());
            }
            
        });
        
        ecMappingDao.addEcToRouteMapping(energyCompanyId, routeId);
        
        dbChangeManager.processDbChange(DbChangeType.ADD, 
                                        DbChangeCategory.ENERGY_COMPANY_ROUTE,
                                        energyCompanyId);

    }

    @Override
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
        
        dbChangeManager.processDbChange(DbChangeType.DELETE, 
                                        DbChangeCategory.ENERGY_COMPANY_ROUTE,
                                        energyCompanyId);

        return rowsDeleted;
    }

    @Override
    public void addSubstationToEnergyCompany(int energyCompanyId, int substationId) {
        
        ecMappingDao.addECToSubstationMapping(energyCompanyId, substationId);
        
        dbChangeManager.processDbChange(DbChangeType.ADD, 
                                        DbChangeCategory.ENERGY_COMPANY_SUBSTATIONS,
                                        energyCompanyId);

    }
    
    @Override
    @Transactional
    public int removeSubstationFromEnergyCompany(int energyCompanyId, int substationId) {
        
        int rowsDeleted = ecMappingDao.deleteECToSubstationMapping(energyCompanyId, substationId);
        siteInformationDao.resetSubstation(substationId);
        
        dbChangeManager.processDbChange(DbChangeType.DELETE, 
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