package com.cannontech.web.admin.energyCompany.general.service;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.model.Address;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.core.dao.impl.LiteYukonUserMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.stars.ECToGenericMapping;
import com.cannontech.web.admin.energyCompany.general.model.GeneralInfo;

public class GeneralInfoService {

    private ContactDao contactDao;
    private ContactNotificationDao contactNotificationDao;
    private AddressDao addressDao;
    private StarsDatabaseCache starsDatabaseCache;
    private EnergyCompanyDao energyCompanyDao;
    private YukonJdbcTemplate yukonJdbcTemplate;
    
    public GeneralInfo getGeneralInfo(LiteStarsEnergyCompany energyCompany) {
        GeneralInfo info = new GeneralInfo();
        info.setEcId(energyCompany.getEnergyCompanyId());
        info.setName(energyCompany.getName());
        
        LiteContact contact = contactDao.getContact(energyCompany.getPrimaryContactID());
        int contactId = contact.getContactID();
        int addressId = contact.getAddressID();
        
        LiteContactNotification phone = contactNotificationDao.getFirstNotificationForContactByType(contactId, ContactNotificationType.PHONE);
        if (phone != null) {
            info.setPhone(phone.getNotification());
        }
        
        LiteContactNotification fax = contactNotificationDao.getFirstNotificationForContactByType(contactId, ContactNotificationType.FAX);
        if (fax != null) {
            info.setFax(fax.getNotification());
        }
        
        LiteContactNotification email = contactNotificationDao.getFirstNotificationForContactByType(contactId, ContactNotificationType.EMAIL);
        if (email != null) { 
            info.setEmail(email.getNotification());
        }
        
        info.setAddress(Address.getDisplayableAddress((addressDao.getByAddressId(addressId))));
        
        info.setDefaultRouteId(energyCompany.getDefaultRouteId());
        
        return info;
    }

    @Transactional
    public void save(GeneralInfo generalInfo, LiteYukonUser user) throws Exception {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(generalInfo.getEcId());
        LiteContact contact = contactDao.getContact(energyCompany.getPrimaryContactID());
        int contactId = contact.getContactID();
        int addressId = contact.getAddressID();
        
        /* Name */
        energyCompanyDao.updateCompanyName(generalInfo.getName(), generalInfo.getEcId());
        
        /* Phone */
        LiteContactNotification phone = contactNotificationDao.getFirstNotificationForContactByType(contactId, ContactNotificationType.PHONE);
        if (StringUtils.isNotBlank(generalInfo.getPhone())) {
            if (phone == null) {
                phone = new LiteContactNotification(-1);
                phone.setContactID(contactId);
                phone.setNotificationCategoryID(YukonListEntryTypes.YUK_ENTRY_ID_PHONE);
                phone.setDisableFlag("Y");
            }
            phone.setNotification(generalInfo.getPhone());
            contactNotificationDao.saveNotification(phone);
        } else if (phone != null) {
            contactNotificationDao.removeNotification(phone.getContactNotifID());
        }
        
        /* Fax */
        LiteContactNotification fax = contactNotificationDao.getFirstNotificationForContactByType(contactId, ContactNotificationType.FAX);
        if (StringUtils.isNotBlank(generalInfo.getFax())) {
            if (fax == null) {
                fax = new LiteContactNotification(-1);
                fax.setContactID(contactId);
                fax.setNotificationCategoryID(YukonListEntryTypes.YUK_ENTRY_ID_FAX);
                fax.setDisableFlag("Y");
            }
            fax.setNotification(generalInfo.getFax());
            contactNotificationDao.saveNotification(fax);
        } else if (fax != null) {
            contactNotificationDao.removeNotification(fax.getContactNotifID());
        }
        
        /* Email */
        LiteContactNotification email = contactNotificationDao.getFirstNotificationForContactByType(contactId, ContactNotificationType.EMAIL);
        if (StringUtils.isNotBlank(generalInfo.getEmail())) {
            if (email == null) {
                email = new LiteContactNotification(-1);
                email.setContactID(contactId);
                email.setNotificationCategoryID(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL);
                email.setDisableFlag("Y");
            }
            email.setNotification(generalInfo.getEmail());
            contactNotificationDao.saveNotification(email);
        } else if (email != null) {
            contactNotificationDao.removeNotification(email.getContactNotifID());
        }
        
        /* Address */
        LiteAddress address = new LiteAddress(addressId, generalInfo.getAddress().getLocationAddress1(),
                        generalInfo.getAddress().getLocationAddress2(),
                        generalInfo.getAddress().getCityName(),
                        generalInfo.getAddress().getStateCode(),
                        generalInfo.getAddress().getZipCode());
        address.setCounty(generalInfo.getAddress().getCounty());
        
        addressDao.update(address);
        
        /* Route */
//        StarsAdminUtil.updateDefaultRoute( energyCompany, generalInfo.getDefaultRouteId(), user);
        
    }
    
    public LiteYukonUser getMemberLogin(int parentCompanyId, Collection<Integer> memberOperatorIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT yu.UserID, yu.UserName, yu.Status, yu.AuthType");
        sql.append("FROM ECToGenericMapping ec");
        sql.append(  "JOIN YukonUser yu on yu.UserId = ec.ItemID");
        sql.append("WHERE ec.EnergyCompanyID").eq_k(parentCompanyId);
        sql.append(  "AND ec.MappingCategory").eq(ECToGenericMapping.MAPPING_CATEGORY_MEMBER_LOGIN);
        sql.append(  "AND ec.ItemID").in(memberOperatorIds);
        
        try {
            return yukonJdbcTemplate.queryForObject(sql, new LiteYukonUserMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
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
    public void setAddressDao(AddressDao addressDao) {
        this.addressDao = addressDao;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setEnergyCompanyDao(EnergyCompanyDao energyCompanyDao) {
        this.energyCompanyDao = energyCompanyDao;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
}