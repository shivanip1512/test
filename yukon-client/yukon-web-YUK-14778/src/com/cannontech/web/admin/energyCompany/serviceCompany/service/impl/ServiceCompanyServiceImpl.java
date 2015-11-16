package com.cannontech.web.admin.energyCompany.serviceCompany.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.model.DesignationCodeDto;
import com.cannontech.common.model.ServiceCompanyDto;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.DesignationCodeDao;
import com.cannontech.core.dao.ServiceCompanyDao;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.admin.energyCompany.serviceCompany.service.ServiceCompanyService;

public class ServiceCompanyServiceImpl implements ServiceCompanyService {
    
    @Autowired private AddressDao addressDao;
    @Autowired private ContactDao contactDao;
    @Autowired private ContactNotificationDao contactNotificationDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private DesignationCodeDao designationCodeDao;
    @Autowired private ServiceCompanyDao serviceCompanyDao;
    
    private static final int noServiceCompanyId = 0;
    
    @Override
    public ServiceCompanyDto getServiceCompany(int serviceCompanyId) {
        ServiceCompanyDto serviceCompany = serviceCompanyDao.getCompanyById(serviceCompanyId);

        serviceCompany.setDesignationCodes(designationCodeDao.getDesignationCodesByServiceCompanyId(serviceCompanyId));
        LiteContactNotification email =
            contactNotificationDao.getFirstNotificationForContactByType(serviceCompany.getPrimaryContact(),
                ContactNotificationType.EMAIL);
        if (email != null) {
            serviceCompany.setEmailContactNotification(email.getNotification());
        }

        return serviceCompany;
    }

    @Override
    @Transactional
    public void createServiceCompany(ServiceCompanyDto serviceCompany, int energyCompanyId) {
        // Address
        addressDao.add(serviceCompany.getAddress());

        // Primary Contact
        serviceCompany.getPrimaryContact().setContactID(-1);
        contactDao.saveContact(serviceCompany.getPrimaryContact());

        // Contact Notification(s)
        LiteContactNotification emailNotification =
            new LiteContactNotification(-1, serviceCompany.getPrimaryContact().getContactID(),
                ContactNotificationType.EMAIL.getDefinitionId(), "N", serviceCompany.getEmailContactNotification());
        contactNotificationDao.saveNotification(emailNotification);

        // create the service company (with updated dependent ids)
        serviceCompanyDao.create(serviceCompany, energyCompanyId);

        // now that we have an id for the service company, assign the designation codes
        if (serviceCompany.getDesignationCodes() != null) {
            // Fix up the designation code service company ids.
            for (DesignationCodeDto designationCode : serviceCompany.getDesignationCodes()) {
                designationCode.setServiceCompanyId(serviceCompany.getCompanyId());
            }

            updateDesignationCodes(serviceCompany);
        }
        sendServiceCompanyChangeMessage(serviceCompany.getCompanyId(), DbChangeType.ADD);
    }

    @Override
    @Transactional
    public void updateServiceCompany(ServiceCompanyDto serviceCompany) {
        // update address
        addressDao.update(serviceCompany.getAddress());

        // update primary contact
        contactDao.saveContact(serviceCompany.getPrimaryContact());

        LiteContactNotification emailNotification =
            contactNotificationDao.getFirstNotificationForContactByType(serviceCompany.getPrimaryContact(),
                ContactNotificationType.EMAIL);

        if (emailNotification != null) {
            // update notification
            emailNotification.setNotification(serviceCompany.getEmailContactNotification());
        } else {
            emailNotification =
                new LiteContactNotification(-1, serviceCompany.getPrimaryContact().getContactID(),
                    ContactNotificationType.EMAIL.getDefinitionId(), "N", serviceCompany.getEmailContactNotification());
        }

        contactNotificationDao.saveNotification(emailNotification);

        // update service company
        serviceCompanyDao.update(serviceCompany);

        // update designation codes
        if (serviceCompany.getDesignationCodes() != null) {
            updateDesignationCodes(serviceCompany);
        }
        sendServiceCompanyChangeMessage(serviceCompany.getCompanyId(), DbChangeType.UPDATE);
    }

    private void updateDesignationCodes(ServiceCompanyDto serviceCompany) {
        // update service company designation codes
        List<DesignationCodeDto> existingDesignationCodes =
            designationCodeDao.getDesignationCodesByServiceCompanyId(serviceCompany.getCompanyId());
        List<DesignationCodeDto> newDesignationCodes = serviceCompany.getDesignationCodes();
        List<DesignationCodeDto> addDesignationCodes = new ArrayList<>();
        List<DesignationCodeDto> removeDesignationCodes = new ArrayList<>();

        // buildup the list of designation codes to remove
        for (DesignationCodeDto designationCode : existingDesignationCodes) {
            boolean found = false;
            // does designationCode exist in the new list? search for designationCode.id
            for (DesignationCodeDto check : newDesignationCodes) {
                if (designationCode.getId() == check.getId()) {
                    found = true;
                    break;
                }
            }

            // if not found, add to the removeDesignationCodes list
            if (!found) {
                removeDesignationCodes.add(designationCode);
            }
        }

        for (DesignationCodeDto designationCode : newDesignationCodes) {
            if (designationCode.getId() == 0 && designationCode.getValue() != null) {
                addDesignationCodes.add(designationCode);
            }
        }

        if (!addDesignationCodes.isEmpty()) {
            designationCodeDao.bulkAdd(addDesignationCodes);
        }

        if (!removeDesignationCodes.isEmpty()) {
            designationCodeDao.bulkDelete(removeDesignationCodes);
        }
    }

    @Override
    @Transactional
    public void deleteServiceCompany(ServiceCompanyDto serviceCompany) {
        // Remove all of the inventory attached to this service company
        serviceCompanyDao.moveInventory(serviceCompany.getCompanyId(), noServiceCompanyId);

        // first delete dependent service company designation codes
        designationCodeDao.bulkDelete(serviceCompany.getDesignationCodes());

        // then, delete service company
        serviceCompanyDao.delete(serviceCompany.getCompanyId());

        // now delete dependent address
        if (serviceCompany.getAddress() != null) {
            addressDao.remove(serviceCompany.getAddress().getAddressID());
        }

        // and finally delete dependent primary contact
        if (serviceCompany.getPrimaryContact() != null) {
            // notifications will get deleted here as well
            contactDao.deleteContact(serviceCompany.getPrimaryContact());
        }
        sendServiceCompanyChangeMessage(serviceCompany.getCompanyId(), DbChangeType.DELETE);
    }

    private void sendServiceCompanyChangeMessage(Integer serviceCompanyId, DbChangeType dbChangeType) {
        dbChangeManager.processDbChange(dbChangeType, DbChangeCategory.SERVICE_COMPANY, serviceCompanyId);
    }
}
