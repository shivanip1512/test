package com.cannontech.web.admin.energyCompany.service.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.core.dao.EnergyCompanyNameUnavailableException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.UserNameUnavailableException;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRoleProperty;
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
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.web.admin.energyCompany.model.EnergyCompanyDto;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyService;
import com.google.common.collect.Maps;

public class EnergyCompanyServiceImpl implements EnergyCompanyService {

    private EnergyCompanyDao energyCompanyDao;
    private YukonUserDao yukonUserDao;
    private YukonGroupDao yukonGroupDao;
    private ContactDao contactDao;
    private ContactNotificationDao contactNotificationDao;
    private DBPersistentDao dbPersistentDao;
    private LiteStarsEnergyCompanyFactory energyCompanyFactory;
    private StarsDatabaseCache starsDatabaseCache;
    
    @Override
    @Transactional
    public LiteStarsEnergyCompany createEnergyCompany(EnergyCompanyDto energyCompanyDto, LiteYukonUser user,
            boolean asMember, Integer parentId) throws Exception {
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
        if (!asMember) {
            rolePropMap.put(YukonRoleProperty.ADMIN_CONFIG_ENERGY_COMPANY.getPropertyId(), CtiUtilities.TRUE_STRING);
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
        LiteStarsEnergyCompany liteEnergyCompany = energyCompanyFactory.createEnergyCompany(energyCompany);
        starsDatabaseCache.addEnergyCompany(liteEnergyCompany);
        dbPersistentDao.processDatabaseChange(DbChangeType.ADD, DbChangeCategory.ENERGY_COMPANY, energyCompanyId);
        
        /* Add Secondary Operator */
        if (StringUtils.isNotBlank(energyCompanyDto.getAdmin2Username())) {
            StarsAdminUtil.createOperatorLogin(energyCompanyDto.getAdmin2Username(), energyCompanyDto.getAdmin2Password1(),
                LoginStatusEnum.ENABLED, new LiteYukonGroup[] {primaryOperatorGroup}, liteEnergyCompany);
        }
        
        /* Set Default Route */
        StarsAdminUtil.updateDefaultRoute(liteEnergyCompany, energyCompanyDto.getDefaultRouteId(), user);
        
        /* Add as member to parent */
        if (asMember) {
            StarsAdminUtil.addMember(starsDatabaseCache.getEnergyCompany(parentId), liteEnergyCompany, adminUser.getUserID());
        }
        
        return liteEnergyCompany;
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
    
}