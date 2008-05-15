package com.cannontech.web.stars.dr.consumer.contactus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;

@Repository
public class ContactUsDaoImpl implements ContactUsDao {
    private ECMappingDao ecMappingDao;
    private ContactDao contactDao;
    private AddressDao addressDao;
    
    @Override
    public ContactUs getContactUsByCustomerAccount(final CustomerAccount customerAccount) {
        LiteStarsEnergyCompany energyCompany = ecMappingDao.getCustomerAccountEC(customerAccount);
        
        LiteContact contact = contactDao.getContact(energyCompany.getPrimaryContactID());
        LiteAddress address = addressDao.getByAddressId(contact.getAddressID());
        
        final ContactUs contactUs = new ContactUs();
        contactUs.setAddress(address);
        
        String companyName = energyCompany.getName();
        contactUs.setCompanyName(companyName);

        String phoneNumber = getNotification(contact, YukonListEntryTypes.YUK_ENTRY_ID_PHONE);
        contactUs.setPhoneNumber(phoneNumber);
        
        String faxNumber = getNotification(contact, YukonListEntryTypes.YUK_ENTRY_ID_FAX);
        contactUs.setFaxNumber(faxNumber);
        
        String email = getNotification(contact, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL);
        contactUs.setEmail(email);
        
        return contactUs;
    }
    
    private String getNotification(LiteContact contact, int notifCatId) {
        LiteContactNotification notification = contactDao.getContactNotification(contact, notifCatId);
        String result = (notification != null) ? notification.getNotification() : "";
        return result;
    }

    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
    @Autowired
    public void setContactDao(ContactDao contactDao) {
        this.contactDao = contactDao;
    }
    
    @Autowired
    public void setAddressDao(AddressDao addressDao) {
        this.addressDao = addressDao;
    }
}
