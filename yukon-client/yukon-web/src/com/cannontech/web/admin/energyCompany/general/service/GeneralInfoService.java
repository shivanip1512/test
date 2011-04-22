package com.cannontech.web.admin.energyCompany.general.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.model.Address;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.stars.service.DefaultRouteService;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.web.admin.energyCompany.general.model.GeneralInfo;

public class GeneralInfoService {

    private ContactDao contactDao;
    private ContactNotificationDao contactNotificationDao;
    private AddressDao addressDao;
    private StarsDatabaseCache starsDatabaseCache;
    private EnergyCompanyDao energyCompanyDao;
    private ECMappingDao ecMappingDao;
    private DefaultRouteService defaultRouteService;
    private DBPersistentDao dbPersistentDao;
    
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
        
        LiteYukonUser parentLogin = ecMappingDao.findParentLogin(energyCompany.getEnergyCompanyId());
        if (parentLogin != null) {
            info.setParentLogin(parentLogin.getUserID());
        }
        
        return info;
    }
    
    @Transactional
    public void save(GeneralInfo generalInfo, LiteYukonUser user) throws CommandExecutionException, WebClientException {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(generalInfo.getEcId());
        LiteContact contact = contactDao.getContact(energyCompany.getPrimaryContactID());
        int contactId = contact.getContactID();
        int addressId = contact.getAddressID();
        
        /* Name */
        energyCompanyDao.updateCompanyName(generalInfo.getName(), generalInfo.getEcId());
        
        dbPersistentDao.processDBChange(new DBChangeMsg(
                                                        energyCompany.getEnergyCompanyId(),
                                                        DBChangeMsg.CHANGE_ENERGY_COMPANY_DB,
                                                        DBChangeMsg.CAT_ENERGY_COMPANY,
                                                        DBChangeMsg.CAT_ENERGY_COMPANY,
                                                        DbChangeType.UPDATE));
        
        /* Phone */
        updateNotification(generalInfo.getPhone(), ContactNotificationType.PHONE, contactId);
        
        /* Fax */
        updateNotification(generalInfo.getFax(), ContactNotificationType.FAX, contactId);
        
        /* Email */
        updateNotification(generalInfo.getEmail(), ContactNotificationType.EMAIL, contactId);
        
        /* Address */
        addressDao.update(generalInfo.getAddress().getLiteAddress(addressId));

        dbPersistentDao.processDBChange(new DBChangeMsg(contactId,
                                                        DBChangeMsg.CHANGE_CONTACT_DB,
                                                        DBChangeMsg.CAT_CUSTOMERCONTACT,
                                                        DBChangeMsg.CAT_CUSTOMERCONTACT,
                                                        DbChangeType.UPDATE));
        
        /* Route */
        defaultRouteService.updateDefaultRoute(energyCompany, generalInfo.getDefaultRouteId(), user);
        
        if (energyCompany.getParent() != null) {
            int parentEcId = energyCompany.getParent().getEnergyCompanyId();
            int energyCompanyId = energyCompany.getEnergyCompanyId();
            Integer parentLogin = generalInfo.getParentLogin();
            if (parentLogin == null) {
                ecMappingDao.removeParentLogin(parentEcId, energyCompanyId);
            } else {
                ecMappingDao.saveParentLogin(parentEcId, energyCompanyId, parentLogin);
            }
        }
        
    }
    
    /* Helper Methods */
    
    /**
     * Updates the notification, either saving or removing it, does nothing if not supplied and notification doesn't exist.
     */
    private void updateNotification(String notification, ContactNotificationType notifType, int contactId) {
        LiteContactNotification liteNotification = contactNotificationDao.getFirstNotificationForContactByType(contactId, notifType);
        if (StringUtils.isNotBlank(notification)) {
            if (liteNotification == null) {
                liteNotification = new LiteContactNotification(-1);
                liteNotification.setContactID(contactId);
                liteNotification.setNotificationCategoryID(notifType.getDefinitionId());
                liteNotification.setDisableFlag("Y");
            }
            liteNotification.setNotification(notification);
            contactNotificationDao.saveNotification(liteNotification);
        } else if (liteNotification != null) {
            contactNotificationDao.removeNotification(liteNotification.getContactNotifID());
        }
        
    }
    
    /* Dependencies */
    
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
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
    @Autowired
    public void setDefaultRouteService(DefaultRouteService defaultRouteService) {
        this.defaultRouteService = defaultRouteService;
    }
    
    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
    
}