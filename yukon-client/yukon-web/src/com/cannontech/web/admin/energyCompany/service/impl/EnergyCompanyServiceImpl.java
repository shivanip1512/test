package com.cannontech.web.admin.energyCompany.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.EnergyCompanyNameUnavailableException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.UserNameUnavailableException;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompanyFactory;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.user.checker.UserCheckerBase;
import com.cannontech.web.admin.energyCompany.model.EnergyCompanyDto;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyService;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class EnergyCompanyServiceImpl implements EnergyCompanyService {

    private EnergyCompanyDao energyCompanyDao;
    private YukonUserDao yukonUserDao;
    private YukonGroupDao yukonGroupDao;
    private ContactDao contactDao;
    private ContactNotificationDao contactNotificationDao;
    private DBPersistentDao dbPersistentDao;
    private LiteStarsEnergyCompanyFactory energyCompanyFactory;
    private StarsDatabaseCache starsDatabaseCache;
    private RolePropertyDao rolePropertyDao;
    private ECMappingDao ecMappingDao;
    
    @Override
    @Transactional
    public LiteStarsEnergyCompany createEnergyCompany(EnergyCompanyDto energyCompanyDto, LiteYukonUser user,
            Integer parentId) throws Exception {
        /* Check energy company name availability */
        try {
            energyCompanyDao.getEnergyCompanyByName(energyCompanyDto.getName());
            throw new EnergyCompanyNameUnavailableException("ec name unavailable");
        } catch (NotFoundException e) {/* Ignore */}
        
        /* Check usernames availability */
        if (yukonUserDao.getLiteYukonUser(energyCompanyDto.getAdminUsername()) != null) {
            throw new UserNameUnavailableException("adminUsername");
        }
        if (StringUtils.isNotBlank(energyCompanyDto.getAdmin2Username()) 
            && yukonUserDao.getLiteYukonUser(energyCompanyDto.getAdmin2Username()) != null) {
            throw new UserNameUnavailableException("admin2Username");
        }
        
        /* Create a privilege group with EnergyCompany and Administrator role */
        LiteYukonGroup primaryOperatorGroup = yukonGroupDao.getLiteYukonGroup(energyCompanyDto.getPrimaryOperatorGroupId());
        String operatorGroupIds = energyCompanyDto.getPrimaryOperatorGroupId().toString();
        if(StringUtils.isNotBlank(energyCompanyDto.getOperatorGroupIds())) {
            operatorGroupIds += "," + energyCompanyDto.getOperatorGroupIds();
        }
        Map<Integer, String> rolePropMap = Maps.newHashMap();
        rolePropMap.put(YukonRoleProperty.OPERATOR_GROUP_IDS.getPropertyId(), operatorGroupIds );
        rolePropMap.put(YukonRoleProperty.CUSTOMER_GROUP_IDS.getPropertyId(), energyCompanyDto.getResidentialGroupIds());
        if (parentId == null) {
            rolePropMap.put(YukonRoleProperty.ADMIN_EDIT_ENERGY_COMPANY.getPropertyId(), CtiUtilities.TRUE_STRING);
        }
        
        String adminGroupName = energyCompanyDto.getName() + " Admin Grp";
        LiteYukonGroup adminGrp = StarsAdminUtil.createOperatorAdminGroup( adminGroupName, rolePropMap );
        
        /* Create the default operator login */
        LiteYukonGroup[] adminsGroups = new LiteYukonGroup[] { primaryOperatorGroup, adminGrp };
        LiteYukonUser adminUser = StarsAdminUtil.createOperatorLogin(energyCompanyDto.getAdminUsername(), 
                                                 energyCompanyDto.getAdminPassword1(), LoginStatusEnum.ENABLED, adminsGroups, null);
        /* Create Contact */
        LiteContact contact = new LiteContact(-1, CtiUtilities.STRING_NONE, CtiUtilities.STRING_NONE);
        contactDao.saveContact(contact);
        if (StringUtils.isNotBlank(energyCompanyDto.getEmail())) {
            LiteContactNotification email = new LiteContactNotification(-1, contact.getContactID(), 
                ContactNotificationType.EMAIL.getDefinitionId(), (String) YNBoolean.NO.getDatabaseRepresentation(), energyCompanyDto.getEmail());
            contactNotificationDao.saveNotification(email);
        }
        
        /* Create Energy Company */
        int energyCompanyId = energyCompanyDao.createEnergyCompany(energyCompanyDto.getName(), 
                                                                   contact.getContactID(), adminUser.getUserID());
        EnergyCompany energyCompany = new EnergyCompany();
        energyCompany.setEnergyCompanyID(energyCompanyId);
        energyCompany.setName(energyCompanyDto.getName());
        energyCompany.setPrimaryContactID(contact.getContactID());
        energyCompany.setUserID(adminUser.getUserID());
        
        /* This method doesn't 'create' anything, it just news a LiteStarsEnergyCompany and injects dependencies. */
        LiteStarsEnergyCompany liteEnergyCompany = energyCompanyFactory.createEnergyCompany(energyCompany);
        
        /* This does nothing to StarsDatabaseCache,  I don't know who else would care. */
        dbPersistentDao.processDatabaseChange(DbChangeType.ADD, DbChangeCategory.ENERGY_COMPANY, energyCompanyId);
        
        /* Add Secondary Operator */
        if (StringUtils.isNotBlank(energyCompanyDto.getAdmin2Username())) {
            StarsAdminUtil.createOperatorLogin(energyCompanyDto.getAdmin2Username(), energyCompanyDto.getAdmin2Password1(),
                LoginStatusEnum.ENABLED, new LiteYukonGroup[] {primaryOperatorGroup}, liteEnergyCompany);
        }
        
        /* Set Default Route */
        StarsAdminUtil.updateDefaultRoute(liteEnergyCompany, energyCompanyDto.getDefaultRouteId(), user);
        /* StarsAdminUtil.updateDefaultRoute doesn't actually set the default route id on the LiteStarsEnergyCompany.
         * It only writes it to the database, to actually set it properly you have to set it to zero with resetDefaultRouteId()
         * and the first time someone uses the getDefaultRouteId() method it will lookup it up and set it on the cached 
         * LiteStarsEnergyCompany before returning it.  The first thing StarsAdminUtil.updateDefaultRoute actually does  
         * is call getDefaultRouteId() and at this point I think it needs to be -1 so the save to the db will actually happen.
         * Without calling resetDefaultRouteId() after the save, the cached LiteStarsEnergyCompany will still have a
         * defaultRouteId of -1.  Although I don't know why we couldn't just set it on the cached LiteStarsEnergyCompany now,
         * there is no setter method for the defaultRouteId.
         * 
         * This is the most soul crushing poo code I've ever witnessed. */
        liteEnergyCompany.resetDefaultRouteId();
        
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
    public boolean canManageMembers(LiteYukonUser user, int ecId) {
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
        boolean createAndDelete = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_CREATE_DELETE_ENERGY_COMPANY, user);
        boolean manageMembers = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, user);
        boolean isOperator = starsDatabaseCache.getEnergyCompany(ecId).getOperatorLoginIDs().contains(user.getUserID()); 
        
        /* Can delete
         * IF user is a 'super user'
         * OR user is operator of one of my parent energy companies and has manage members role property
         * OR user is energy companies operator and has create/delete energy company role property */
        return superUser 
            || (isParentOperator(user.getUserID(), ecId) && manageMembers && createAndDelete)
            || (isOperator && createAndDelete);
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
    }

    @Autowired
    public void setEnergyCompanyDao(EnergyCompanyDao energyCompanyDao) {
        this.energyCompanyDao = energyCompanyDao;
    }
    
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
    
    @Autowired
    public void setYukonGroupDao(YukonGroupDao yukonGroupDao) {
        this.yukonGroupDao = yukonGroupDao;
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
    public void setEnergyCompanyFactory(LiteStarsEnergyCompanyFactory energyCompanyFactory) {
        this.energyCompanyFactory = energyCompanyFactory;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
}