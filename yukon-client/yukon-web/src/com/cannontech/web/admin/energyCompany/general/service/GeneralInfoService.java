package com.cannontech.web.admin.energyCompany.general.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.model.Address;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.service.DefaultRouteService;
import com.cannontech.web.admin.energyCompany.general.model.GeneralInfo;

public class GeneralInfoService {

    @Autowired private ContactDao contactDao;
    @Autowired private ContactNotificationDao contactNotificationDao;
    @Autowired private AddressDao addressDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private DefaultRouteService defaultRouteService;
    @Autowired private DbChangeManager dbChangeManager;
    
    public GeneralInfo getGeneralInfo(EnergyCompany energyCompany) {
        GeneralInfo info = new GeneralInfo();
        info.setEcId(energyCompany.getId());
        info.setName(energyCompany.getName());
        
        LiteContact contact = contactDao.getContact(energyCompany.getContactId());
        
        LiteContactNotification phone = contactNotificationDao.getFirstNotificationForContactByType(contact, ContactNotificationType.PHONE);
        if (phone != null) {
            info.setPhone(phone.getNotification());
        }
        
        LiteContactNotification fax = contactNotificationDao.getFirstNotificationForContactByType(contact, ContactNotificationType.FAX);
        if (fax != null) {
            info.setFax(fax.getNotification());
        }
        
        LiteContactNotification email = contactNotificationDao.getFirstNotificationForContactByType(contact, ContactNotificationType.EMAIL);
        if (email != null) { 
            info.setEmail(email.getNotification());
        }
        
        if (contact != null) {
            info.setAddress(Address.getDisplayableAddress((addressDao.getByAddressId(contact.getAddressID()))));
        }
        
        info.setDefaultRouteId(defaultRouteService.getDefaultRouteId(energyCompany));
        
        LiteYukonUser parentLogin = ecMappingDao.findParentLogin(energyCompany.getId());
        if (parentLogin != null) {
            info.setParentLogin(parentLogin.getUserID());
        }
        
        return info;
    }
    
    @Transactional
    public void save(GeneralInfo generalInfo, LiteYukonUser user) {
        EnergyCompany energyCompany = ecDao.getEnergyCompany(generalInfo.getEcId());
        LiteContact contact = contactDao.getContact(energyCompany.getContactId());
        int contactId = contact.getContactID();
        int addressId = contact.getAddressID();
        
        /* Name */
        ecDao.updateCompanyName(generalInfo.getName(), generalInfo.getEcId());
        
        /* Phone */
        updateNotification(generalInfo.getPhone(), ContactNotificationType.PHONE, contactId);
        
        /* Fax */
        updateNotification(generalInfo.getFax(), ContactNotificationType.FAX, contactId);
        
        /* Email */
        updateNotification(generalInfo.getEmail(), ContactNotificationType.EMAIL, contactId);
        
        /* Address */
        addressDao.update(generalInfo.getAddress().getLiteAddress(addressId));

        dbChangeManager.processDbChange(contactId, DBChangeMsg.CHANGE_CONTACT_DB, DBChangeMsg.CAT_CUSTOMERCONTACT,
                                        DBChangeMsg.CAT_CUSTOMERCONTACT, DbChangeType.UPDATE);
        
        /* Route */
        defaultRouteService.updateDefaultRoute(energyCompany, generalInfo.getDefaultRouteId(), user);
        
        if (energyCompany.getParent() != null) {
            int parentEcId = energyCompany.getParent().getId();
            int energyCompanyId = energyCompany.getId();
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
}