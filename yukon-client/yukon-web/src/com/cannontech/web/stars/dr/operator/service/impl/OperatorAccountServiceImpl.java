package com.cannontech.web.stars.dr.operator.service.impl;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.model.Address;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.ContactNotificationFormattingService;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.stars.dr.account.dao.AccountSiteDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.AccountSite;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.general.service.ContactNotificationService;
import com.cannontech.stars.dr.general.service.ContactService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.model.ContactDto;
import com.cannontech.web.stars.dr.operator.model.ContactNotificationDto;
import com.cannontech.web.stars.dr.operator.model.OperatorGeneralUiExtras;
import com.cannontech.web.stars.dr.operator.service.OperatorAccountService;
import com.google.common.collect.Lists;

public class OperatorAccountServiceImpl implements OperatorAccountService {

	private CustomerDao customerDao;
	private CustomerAccountDao customerAccountDao;
	private ContactDao contactDao;
	private ContactNotificationDao contactNotificationDao;
	private AccountSiteDao accountSiteDao;
	private AddressDao addressDao;
	private RolePropertyDao rolePropertyDao;
	private ContactService contactService;
	private ContactNotificationService contactNotificationService;
	private ContactNotificationFormattingService contactNotificationFormattingService;
	
	@Override
	@Transactional
    public void updateAccount(int accountId, OperatorGeneralUiExtras operatorGeneralUiExtras) {
    	
    	// get objects
    	CustomerAccount customerAccount = customerAccountDao.getById(accountId);
    	LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
    	AccountSite accountSite = accountSiteDao.getByAccountSiteId(customerAccount.getAccountSiteId());
    	LiteContact primaryContact = contactDao.getContact(customer.getPrimaryContactID());
        LiteContactNotification emailNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL);
        LiteAddress address = addressDao.getByAddressId(accountSite.getStreetAddressId());
        LiteAddress billingAddress = addressDao.getByAddressId(customerAccount.getBillingAddressId());
        
        // notifyOddsOfControl
        if (emailNotif != null) {
	    	boolean emailDisabled = !operatorGeneralUiExtras.isNotifyOddsForControl();
	    	emailNotif.setDisableFlag(BooleanUtils.toString(emailDisabled, "Y", "N"));
	    	contactNotificationDao.saveNotification(emailNotif);
        }
    	
    	// notes
    	customerAccount.setAccountNotes(operatorGeneralUiExtras.getNotes());
    	customerAccountDao.update(customerAccount);
    	
    	// account site notes
    	accountSite.setPropertyNotes(operatorGeneralUiExtras.getAccountSiteNotes());
    	accountSiteDao.update(accountSite);
    	
    	// usePrimaryAddressForBilling
    	if (operatorGeneralUiExtras.isUsePrimaryAddressForBilling()) {
    		
    		boolean newBillingaddress = billingAddress == null;
    		if (newBillingaddress) {
    			billingAddress = new LiteAddress();
    		}
    		
    		copyAddress(address, billingAddress);
    		
    		if (newBillingaddress) {
    			addressDao.add(billingAddress);
    			customerAccount.setBillingAddressId(billingAddress.getAddressID());
    			customerAccountDao.add(customerAccount);
    		} else {
    			addressDao.update(billingAddress);
    		}
    	}
    }
	
	private void copyAddress(LiteAddress from, LiteAddress to) {
		
		to.setLocationAddress1(from.getLocationAddress1());
        if (StringUtils.isBlank(from.getLocationAddress2())) {
        	to.setLocationAddress2(CtiUtilities.STRING_NONE);
        } else {
        	to.setLocationAddress2(from.getLocationAddress2());
        }
        to.setCityName(from.getCityName());
        to.setStateCode(from.getStateCode());
        to.setZipCode(from.getZipCode());
        to.setCounty(from.getCounty());
	}

    @Override
    @Transactional
    public OperatorGeneralUiExtras getOperatorGeneralUiExtras(int accountId, YukonUserContext userContext) {
    	
    	OperatorGeneralUiExtras operatorGeneralUiExtras = new OperatorGeneralUiExtras();
    	
    	// get objects
    	CustomerAccount customerAccount = customerAccountDao.getById(accountId);
    	LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
        LiteContact primaryContact = contactDao.getContact(customer.getPrimaryContactID());
        LiteContactNotification emailNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL);
        AccountSite accountSite = accountSiteDao.getByAccountSiteId(customerAccount.getAccountSiteId());
        LiteAddress address = addressDao.getByAddressId(accountSite.getStreetAddressId());
        LiteAddress billingAddress = addressDao.getByAddressId(customerAccount.getBillingAddressId());
        
        boolean hasOddForControlRole = rolePropertyDao.checkRole(YukonRole.ODDS_FOR_CONTROL, userContext.getYukonUser());
        operatorGeneralUiExtras.setHasOddsForControlRole(hasOddForControlRole);
        
        // notifyOddsOfControl
        if(emailNotif != null) {
        	operatorGeneralUiExtras.setNotifyOddsForControl(!emailNotif.isDisabled());
        }else {
        	operatorGeneralUiExtras.setNotifyOddsForControl(false);
        }
        
        // notes
        operatorGeneralUiExtras.setNotes(com.cannontech.common.util.StringUtils.stripNone(customerAccount.getAccountNotes()));
        
        // account site notes
        operatorGeneralUiExtras.setAccountSiteNotes(com.cannontech.common.util.StringUtils.stripNone(accountSite.getPropertyNotes()));
        
        // usePrimaryAddressForBilling
        // true if there is no billing address
        // else false unless address and billing address match
        if(billingAddress == null) {
        	
        	operatorGeneralUiExtras.setUsePrimaryAddressForBilling(true);
        	
        } else {
        	
        	operatorGeneralUiExtras.setUsePrimaryAddressForBilling(false);
        	if (billingAddress != null && address != null) {
        		
        		if (billingAddress.getLocationAddress1().equals(address.getLocationAddress1())
        			&& billingAddress.getLocationAddress2().equals(address.getLocationAddress2())
        			&& billingAddress.getCityName().equals(address.getCityName())
        			&& billingAddress.getStateCode().equals(address.getStateCode())
        			&& billingAddress.getZipCode().equals(address.getZipCode())) {
        			
        			operatorGeneralUiExtras.setUsePrimaryAddressForBilling(true);
        		}
        	}
        }
        
    	
    	
    	return operatorGeneralUiExtras;
    }
    
    @Override
    public ContactDto getBlankContactDto(int additionalBlankNotifications) {
    	
    	ContactDto contactDto = new ContactDto();
    	
    	// addAdditionalBlankNotifications
    	addAdditionalBlankNotifications(contactDto, additionalBlankNotifications);
    	
    	return contactDto;
    }
    
    // GET CONTACT DTO
    @Override
    public ContactDto getContactDto(int contactId, YukonUserContext userContext) {

    	LiteContact contact = null;
    	if (contactId <= 0) {
    		return null;
    	}
    	try {
    		contact = contactDao.getContact(contactId);
    	} catch (DataAccessException e) {
    		return new ContactDto();
    	}
    	boolean isPrimary = contactDao.isPrimaryContact(contactId);
    	
    	ContactDto contactDto = new ContactDto();
    	contactDto.setContactId(contactId);
    	contactDto.setFirstName(contact.getContFirstName());
    	contactDto.setLastName(contact.getContLastName());
    	contactDto.setPrimary(isPrimary);
    	
    	List<LiteContactNotification> notificationsForContact = contactNotificationDao.getNotificationsForContact(contactId);
    	
    	contactDto.setHomePhone(getAndFormatAndRemoveFirstOfType(notificationsForContact, ContactNotificationType.HOME_PHONE, userContext));
    	contactDto.setWorkPhone(getAndFormatAndRemoveFirstOfType(notificationsForContact, ContactNotificationType.WORK_PHONE, userContext));
    	contactDto.setEmail(getAndFormatAndRemoveFirstOfType(notificationsForContact, ContactNotificationType.EMAIL, userContext));

    	for (LiteContactNotification notification : notificationsForContact) {
    		
    		String formattedNotification = contactNotificationFormattingService.formatNotification(notification, userContext);
    			
			ContactNotificationDto contactNotificationDto = new ContactNotificationDto();
			contactNotificationDto.setNotificationId(notification.getContactNotifID());
			contactNotificationDto.setContactNotificationType(notification.getContactNotificationType());
			contactNotificationDto.setNotificationValue(formattedNotification);
			
			contactDto.getOtherNotifications().add(contactNotificationDto);
    	}
    	
    	return contactDto;
    }
    
    private String getAndFormatAndRemoveFirstOfType(List<LiteContactNotification> notificationsForContact, ContactNotificationType contactNotificationType, YukonUserContext userContext) {
    	
    	LiteContactNotification firstOfType = removeFirstOfType(notificationsForContact, contactNotificationType);
    	if (firstOfType != null) {
    		return contactNotificationFormattingService.formatNotification(firstOfType, userContext);
    	}
    	
    	return null;
    }
    
    private void addAdditionalBlankNotifications(ContactDto contactDto, int additionalBlankNotifications) {
    	
    	for (int i = 0; i < additionalBlankNotifications; i++) {
    		
    		ContactNotificationDto contactNotificationDto = new ContactNotificationDto();
			contactNotificationDto.setNotificationId(0);
			contactNotificationDto.setContactNotificationType(null);
			contactNotificationDto.setNotificationValue("");
			
    		contactDto.getOtherNotifications().add(contactNotificationDto);
    	}
    }
    
    @Override
    @Transactional
    public void saveContactDto(ContactDto contactDto, LiteCustomer customer) {

        // save contact
        LiteContact contact;
        if (contactDto.getContactId() <= 0) {
            contact = contactService.createAdditionalContact(contactDto.getFirstName(), contactDto.getLastName(), customer.getCustomerID(), null);
        } else {
            contact = contactDao.getContact(contactDto.getContactId());
        }
        contactService.updateContact(contact, contactDto.getFirstName(), contactDto.getLastName(), null);

        // save notifications
        List<LiteContactNotification> notificationsToSave = Lists.newArrayListWithExpectedSize(5);
        List<LiteContactNotification> existingNotifications = contactNotificationDao.getNotificationsForContact(contact.getContactID());

        processNotificationForType(existingNotifications, ContactNotificationType.HOME_PHONE, contactDto.getHomePhone(), contact, notificationsToSave);
        processNotificationForType(existingNotifications, ContactNotificationType.WORK_PHONE, contactDto.getWorkPhone(), contact, notificationsToSave);
        processNotificationForType(existingNotifications, ContactNotificationType.EMAIL, contactDto.getEmail(), contact, notificationsToSave);

        List<ContactNotificationDto> otherNotifications = contactDto.getOtherNotifications();
        for (ContactNotificationDto contactNotificationDto : otherNotifications) {
            processNotificationForType(existingNotifications, contactNotificationDto.getContactNotificationType(), contactNotificationDto.getNotificationValue(), contact, notificationsToSave);
        }

        // delete anything left in existing
        for (LiteContactNotification contactNotification : existingNotifications) {
            contactNotificationDao.removeNotification(contactNotification.getContactNotifID());
        }

        int order = 1;
        for (LiteContactNotification contactNotification : notificationsToSave) {
            contactNotification.setOrder(order++);
            contactNotificationDao.saveNotification(contactNotification);
        }

        contactDto.setContactId(contact.getLiteID());
    }

    private void processNotificationForType(List<LiteContactNotification> existingNotifications,
								            ContactNotificationType type, String notificationText, LiteContact contact,
								            List<LiteContactNotification> allNotifications) {
    	
        if (type == null || StringUtils.isBlank(notificationText)){
        	return;
        }

        LiteContactNotification notification = removeFirstOfType(existingNotifications, type);
        if (notification != null) {
            notification.setNotification(notificationText);
            allNotifications.add(notification);
        } else {
            // need new object
            LiteContactNotification newNotification = contactNotificationService.createNotification(contact, type, notificationText);
            allNotifications.add(newNotification);
        }
    }
    
    private LiteContactNotification removeFirstOfType(List<LiteContactNotification> notificationsForContact, ContactNotificationType contactNotificationType) {
    	
    	for (int i = 0; i < notificationsForContact.size(); i++) {
    		LiteContactNotification notification = notificationsForContact.get(i);
    		if (notification.getContactNotificationType() == contactNotificationType) {
    			return notificationsForContact.remove(i);
    		}
    	}
    	
    	return null;
    }
    
    @Override
    public AccountInfoFragment getAccountInfoFragment(int accountId, int energyCompanyId) {
    	
    	CustomerAccount customerAccount = customerAccountDao.getById(accountId);
    	AccountSite accountSite = accountSiteDao.getByAccountSiteId(customerAccount.getAccountSiteId());
    	LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
    	LiteAddress liteAddress = addressDao.getByAddressId(accountSite.getStreetAddressId());
    	LiteContact primaryContact = contactDao.getContact(customer.getPrimaryContactID());
    	LiteContactNotification homePhoneNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE);
        LiteContactNotification workPhoneNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE);
        
    	AccountInfoFragment accountInfoFragment = new AccountInfoFragment(accountId, energyCompanyId, customerAccount.getAccountNumber());

    	// name
    	accountInfoFragment.setFirstName(primaryContact.getContFirstName());
    	accountInfoFragment.setLastName(primaryContact.getContLastName());
    	
    	// company name
    	if(customer instanceof LiteCICustomer) {
    		accountInfoFragment.setCompanyName(((LiteCICustomer) customer).getCompanyName());
        }
    	
    	// phone
    	accountInfoFragment.setHomePhoneNotif(homePhoneNotif);
    	accountInfoFragment.setWorkPhoneNotif(workPhoneNotif);
    	
    	// address
    	Address address = Address.getDisplayableAddress(liteAddress);
    	accountInfoFragment.setAddress(address);
    	
    	return accountInfoFragment;
    }
	
	@Autowired
	public void setAccountSiteDao(AccountSiteDao accountSiteDao) {
		this.accountSiteDao = accountSiteDao;
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
	public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
		this.customerAccountDao = customerAccountDao;
	}
	
	@Autowired
	public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}
	
	@Autowired
	public void setAddressDao(AddressDao addressDao) {
		this.addressDao = addressDao;
	}
	
	@Autowired
	public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
	
	@Autowired
	public void setContactService(ContactService contactService) {
		this.contactService = contactService;
	}
	
	@Autowired
	public void setContactNotificationService(ContactNotificationService contactNotificationService) {
		this.contactNotificationService = contactNotificationService;
	}
	
	@Autowired
	public void setContactNotificationFormattingService(ContactNotificationFormattingService contactNotificationFormattingService) {
		this.contactNotificationFormattingService = contactNotificationFormattingService;
	}
}
