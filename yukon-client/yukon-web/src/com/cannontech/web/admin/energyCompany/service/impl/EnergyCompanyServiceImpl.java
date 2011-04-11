package com.cannontech.web.admin.energyCompany.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonSelectionList;
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
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.TransactionException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompanyFactory;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.database.db.stars.ECToGenericMapping;
import com.cannontech.database.db.stars.customer.CustomerAccount;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.SiteInformationDao;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.stars.service.DefaultRouteService;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.action.AccountAction;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.user.checker.UserCheckerBase;
import com.cannontech.web.admin.energyCompany.model.EnergyCompanyDto;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyService;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class EnergyCompanyServiceImpl implements EnergyCompanyService {

    private Logger log = YukonLogManager.getLogger(EnergyCompanyServiceImpl.class);

    private AccountThermostatScheduleDao accountThermostatScheduleDao;
    private ContactDao contactDao;
    private ContactNotificationDao contactNotificationDao;
    private DBPersistentDao dbPersistentDao;
    private DefaultRouteService defaultRouteService;
    private ECMappingDao ecMappingDao;
    private EnergyCompanyDao energyCompanyDao;
    private LiteStarsEnergyCompanyFactory energyCompanyFactory;
    private RolePropertyDao rolePropertyDao;
    private SiteInformationDao siteInformationDao;
    private StarsDatabaseCache starsDatabaseCache;
    private YukonGroupDao yukonGroupDao;
    private YukonListDao yukonListDao;
    private YukonUserDao yukonUserDao;
    
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
        if (StringUtils.isNotBlank(energyCompanyDto.getAdmin2Username()) 
            && yukonUserDao.findUserByUsername(energyCompanyDto.getAdmin2Username()) != null) {
            throw new UserNameUnavailableException("admin2Username");
        }
        
        /* Create a privilege group with EnergyCompany and Administrator role */
        LiteYukonGroup primaryOperatorGroup = yukonGroupDao.getLiteYukonGroup(energyCompanyDto.getPrimaryOperatorGroupId());
        String adminGroupName = energyCompanyDto.getName() + " Admin Grp";
        LiteYukonGroup adminGrp = StarsAdminUtil.createOperatorAdminGroup( adminGroupName, topLevelEc );
        
        /* Create the primary operator login */
        LiteYukonGroup[] adminsGroups = new LiteYukonGroup[] { primaryOperatorGroup, adminGrp };
        LiteYukonUser adminUser = StarsAdminUtil.createOperatorLogin(energyCompanyDto.getAdminUsername(), 
                                                 energyCompanyDto.getAdminPassword1(), LoginStatusEnum.ENABLED, adminsGroups, null);
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
                            null,
                            DbChangeType.ADD));
        
        /* Add Secondary Operator */
        if (StringUtils.isNotBlank(energyCompanyDto.getAdmin2Username())) {
            StarsAdminUtil.createOperatorLogin(energyCompanyDto.getAdmin2Username(), energyCompanyDto.getAdmin2Password1(),
                LoginStatusEnum.ENABLED, new LiteYukonGroup[] {primaryOperatorGroup}, liteEnergyCompany);
        }
        
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
    public boolean isOperator(LiteYukonUser user){
        for (LiteStarsEnergyCompany ec : starsDatabaseCache.getAllEnergyCompanies()) {
            if (ec.getOperatorLoginIDs().contains(user.getUserID())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void verifyViewPageAccess(LiteYukonUser user, int ecId) {
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
    public void deleteEnergyCompany(int energyCompanyId) {
        if (energyCompanyId == StarsDatabaseCache.DEFAULT_ENERGY_COMPANY_ID) {
            throw new IllegalArgumentException("The default energy company cannot be deleted.");
        }
        
        LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( energyCompanyId );
        
        String dbAlias = CtiUtilities.getDatabaseAlias();
        
        try {
            deleteAllOperatorLogins(energyCompany, dbAlias);
            
            deleteAllCustomerAccounts(energyCompany);
            
            deleteAllInventory(energyCompanyId, dbAlias);
            
            deleteAllWorkOrders(energyCompanyId, dbAlias);
            
            deleteAllDefaultThermostatSchedules(energyCompany);
            
            deleteAllSubstations(energyCompany);
            
            deleteAllServiceCompanies(energyCompany);
            
            deleteAllApplianceCategories(energyCompany);
            
            deleteAllCustomerSelectionLists(energyCompany);
            
            LiteYukonGroup liteGroup = deleteEnergyCompanyBase(energyCompany, dbAlias);
            
            deleteLoginGroupAndLogin(energyCompany, liteGroup);
        }catch (Exception e) {
            log.error( e.getMessage(), e );
        }
    }

    /**
     * @param energyCompany
     * @param liteGroup
     */
    private void deleteLoginGroupAndLogin(LiteStarsEnergyCompany energyCompany,
                                          LiteYukonGroup liteGroup) {
        // Delete the default operator login
        int defaultUserId = energyCompany.getUser().getUserID();
        if (defaultUserId != com.cannontech.user.UserUtils.USER_ADMIN_ID &&
                defaultUserId != com.cannontech.user.UserUtils.USER_DEFAULT_ID)
        {
            com.cannontech.database.data.user.YukonUser.deleteOperatorLogin(defaultUserId);
            ServerUtils.handleDBChange( energyCompany.getUser(), DbChangeType.DELETE );
        }
        
        // Delete the privilege group of the default operator login
        if (liteGroup != null) {
            com.cannontech.database.data.user.YukonGroup dftGroup = new com.cannontech.database.data.user.YukonGroup();
            dftGroup.setGroupID(new Integer(liteGroup.getGroupID()));

            dbPersistentDao.performDBChange(dftGroup, TransactionType.DELETE);
            ServerUtils.handleDBChange(liteGroup, DbChangeType.DELETE);
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
    private LiteYukonGroup deleteEnergyCompanyBase(LiteStarsEnergyCompany energyCompany, String dbAlias)
    throws CommandExecutionException, TransactionException {
        String sql;
        // Delete all other generic mappings
        sql = "DELETE FROM ECToGenericMapping WHERE EnergyCompanyID = " + energyCompany.getEnergyCompanyId();
        SqlStatement stmt = new SqlStatement(sql, dbAlias);
        stmt.execute();
        
        // Delete membership from the energy company hierarchy
        if (energyCompany.getParent() != null) {
            StarsAdminUtil.removeMember( energyCompany.getParent(), energyCompany.getLiteID() );
        }
        
        // Delete LM groups created for the default route
        if (energyCompany.getDefaultRouteId() >= 0) {
            defaultRouteService.removeDefaultRoute(energyCompany);
        }
        
        // Get the privilege group before the default login is deleted
        LiteYukonGroup liteGroup = energyCompany.getOperatorAdminGroup();
        
        // Delete the energy company!
        
        com.cannontech.database.data.company.EnergyCompanyBase ec =
                new com.cannontech.database.data.company.EnergyCompanyBase();
        ec.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
        ec.getEnergyCompany().setPrimaryContactId(energyCompany.getPrimaryContactID());
        
        dbPersistentDao.performDBChange(ec, TransactionType.DELETE);
        
        StarsDatabaseCache.getInstance().deleteEnergyCompany( energyCompany.getLiteID() );
        ServerUtils.handleDBChange( energyCompany, DbChangeType.DELETE );
        if (energyCompany.getPrimaryContactID() != CtiUtilities.NONE_ZERO_ID) {
            try {
                LiteContact liteContact = DaoFactory.getContactDao().getContact( energyCompany.getPrimaryContactID() );
                ServerUtils.handleDBChange( liteContact, DbChangeType.DELETE );
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
                com.cannontech.database.data.stars.LMProgramWebPublishing pubProg =
                    new com.cannontech.database.data.stars.LMProgramWebPublishing();
                pubProg.setProgramID( new Integer(liteProg.getProgramID()) );
                pubProg.getLMProgramWebPublishing().setWebSettingsID( new Integer(liteProg.getWebSettingsID()) );
                dbPersistentDao.performDBChange(pubProg, TransactionType.DELETE);

                energyCompany.deleteProgram( liteProg.getProgramID() );
                StarsDatabaseCache.getInstance().deleteWebConfiguration( liteProg.getWebSettingsID() );
            }
            com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
                    new com.cannontech.database.data.stars.appliance.ApplianceCategory();
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
            com.cannontech.database.data.stars.report.ServiceCompany company =
                    new com.cannontech.database.data.stars.report.ServiceCompany();
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
                energyCompany.getEnergyCompanyId(), com.cannontech.database.db.stars.Substation.TABLE_NAME );
        if (substations != null) {
            for (int i = 0; i < substations.length; i++) {
                com.cannontech.database.data.stars.Substation substation =
                        new com.cannontech.database.data.stars.Substation();
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
    private void deleteAllWorkOrders(int energyCompanyId, String dbAlias)
            throws CommandExecutionException {
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
            
            com.cannontech.database.data.stars.report.WorkOrderBase order =
                    new com.cannontech.database.data.stars.report.WorkOrderBase();
            order.setOrderID( new Integer(orderIDs[i]) );
            
            dbPersistentDao.performDBChange(order, TransactionType.DELETE);
            
        }
    }

    /**
     * @param energyCompanyId
     * @param stmt
     * @throws CommandExecutionException
     */
    private void deleteAllInventory(int energyCompanyId, String dbAlias) throws CommandExecutionException {
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
            
            com.cannontech.database.data.stars.hardware.InventoryBase inventory =
                    new com.cannontech.database.data.stars.hardware.InventoryBase();
            inventory.setInventoryID( new Integer(invIDs[i]) );
            
            dbPersistentDao.performDBChange(inventory, TransactionType.DELETE);
            
        }
    }

    /**
     * @param energyCompany
     * @throws TransactionException
     * @throws WebClientException
     */
    private void deleteAllCustomerAccounts(LiteStarsEnergyCompany energyCompany)
            throws TransactionException, WebClientException {
        // Delete all customer accounts
        Object[][] accounts = CustomerAccount.getAllCustomerAccounts( energyCompany.getEnergyCompanyId() );
        if (accounts != null) {
            
            for (int i = 0; i < accounts.length; i++) {
                int accountID = ((Integer) accounts[i][0]).intValue();
                
                LiteStarsCustAccountInformation liteAcctInfo = 
                    energyCompany.getCustAccountInformation( accountID, true );
                AccountAction.deleteCustomerAccount( liteAcctInfo, energyCompany );
                
            }
        }
    }

    /**
     * @param energyCompanyId
     * @param energyCompany
     * @param dbAlias
     * @return
     * @throws CommandExecutionException
     */
    private void deleteAllOperatorLogins(LiteStarsEnergyCompany energyCompany, String dbAlias)
    throws CommandExecutionException {

        // Delete operator logins (except the default login)
        
        String sql = "SELECT OperatorLoginID FROM EnergyCompanyOperatorLoginList WHERE EnergyCompanyID=" + energyCompany.getEnergyCompanyId();
        SqlStatement stmt = new SqlStatement( sql, dbAlias );
        stmt.execute();
        
        int[] userIDs = new int[ stmt.getRowCount() ];
        for (int i = 0; i < stmt.getRowCount(); i++)
            userIDs[i] = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
        
        for (int i = 0; i < userIDs.length; i++) {
            if (userIDs[i] == energyCompany.getUser().getUserID()) continue;
            
            try {
                com.cannontech.database.data.user.YukonUser.deleteOperatorLogin(userIDs[i]);
                ServerUtils.handleDBChange( DaoFactory.getYukonUserDao().getLiteYukonUser(userIDs[i]), DbChangeType.DELETE );
            } catch (UnsupportedOperationException e) {
                log.error(e);
            }
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

    
    // DI Setters
    @Autowired
    public void setAccountThermostatScheduleDao(AccountThermostatScheduleDao accountThermostatScheduleDao) {
        this.accountThermostatScheduleDao = accountThermostatScheduleDao;
    }
    
    @Autowired
    public void setContactDao(ContactDao contactDao) {
        this.contactDao = contactDao;
    }
    
    @Autowired
    public void setContactNotificationDao(ContactNotificationDao contactNotificationDao) {
        this.contactNotificationDao = contactNotificationDao;
    }
    
    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
    
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
    @Autowired
    public void setEnergyCompanyDao(EnergyCompanyDao energyCompanyDao) {
        this.energyCompanyDao = energyCompanyDao;
    }

    @Autowired
    public void setEnergyCompanyFactory(LiteStarsEnergyCompanyFactory energyCompanyFactory) {
        this.energyCompanyFactory = energyCompanyFactory;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setSiteInformationDao(SiteInformationDao siteInformationDao) {
        this.siteInformationDao = siteInformationDao;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setYukonGroupDao(YukonGroupDao yukonGroupDao) {
        this.yukonGroupDao = yukonGroupDao;
    }
    
    @Autowired
    public void setYukonListDao(YukonListDao yukonListDao) {
        this.yukonListDao = yukonListDao;
    }
    
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
    
    @Autowired
    public void setDefaultRouteService(DefaultRouteService defaultRouteService) {
        this.defaultRouteService = defaultRouteService;
    }
}