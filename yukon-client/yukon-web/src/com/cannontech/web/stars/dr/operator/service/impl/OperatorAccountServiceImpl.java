package com.cannontech.web.stars.dr.operator.service.impl;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.ContactNotificationFormattingService;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.dao.AccountSiteDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.AccountSite;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.dr.general.dao.OperatorAccountSearchDao;
import com.cannontech.stars.dr.general.service.ContactNotificationService;
import com.cannontech.stars.dr.general.service.ContactService;
import com.cannontech.stars.dr.general.service.impl.AccountSearchResult;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.ContactDto;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
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
	private OperatorAccountSearchDao operatorAccountSearchDao;
	private AccountService accountService;
	
	@Override
	public int addAccount(UpdatableAccount updatableAccount, LiteYukonUser operator, OperatorGeneralUiExtras operatorGeneralUiExtras) {
	    int accountId = accountService.addAccount(updatableAccount, operator);
	    setupEmailNotification(accountId, operatorGeneralUiExtras);
	    return accountId;
	}
	
	@Override
	@Transactional
    public void updateAccount(int accountId, OperatorGeneralUiExtras operatorGeneralUiExtras) {
    	
    	// get objects
    	CustomerAccount customerAccount = customerAccountDao.getById(accountId);
    	AccountSite accountSite = accountSiteDao.getByAccountSiteId(customerAccount.getAccountSiteId());
        LiteAddress address = addressDao.getByAddressId(accountSite.getStreetAddressId());
        LiteAddress billingAddress = addressDao.getByAddressId(customerAccount.getBillingAddressId());
        
        // notifyOddsOfControl
        setupEmailNotification(accountId, operatorGeneralUiExtras);
    	
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
	
	private void setupEmailNotification(int accountId, OperatorGeneralUiExtras extras) {
	    LiteContact primaryContact = contactDao.getPrimaryContactForAccount(accountId);
	    LiteContactNotification emailNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, ContactNotificationType.EMAIL);
	    if (emailNotif != null) {
	        boolean emailDisabled = !extras.isNotifyOddsForControl();
	        emailNotif.setDisableFlag(BooleanUtils.toString(emailDisabled, "Y", "N"));
	        contactNotificationDao.saveNotification(emailNotif);
	    }
	}
	
	private void copyAddress(LiteAddress from, LiteAddress to) {
		
		to.setLocationAddress1(StringUtils.defaultString(from.getLocationAddress1()));
       	to.setLocationAddress2(StringUtils.defaultString(from.getLocationAddress2()));
        to.setCityName(StringUtils.defaultString(from.getCityName()));
        to.setStateCode(StringUtils.defaultString(from.getStateCode()));
        to.setZipCode(StringUtils.defaultString(from.getZipCode()));
        to.setCounty(StringUtils.defaultString(from.getCounty()));
	}

    @Override
    @Transactional
    public OperatorGeneralUiExtras getOperatorGeneralUiExtras(int accountId, YukonUserContext userContext) {
    	
    	OperatorGeneralUiExtras operatorGeneralUiExtras = new OperatorGeneralUiExtras();
    	
    	// get objects
    	CustomerAccount customerAccount = customerAccountDao.getById(accountId);
    	LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
        LiteContact primaryContact = contactDao.getContact(customer.getPrimaryContactID());
        LiteContactNotification emailNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, ContactNotificationType.EMAIL);
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
        return getContactDto(contactId, true, true, true, userContext);
    }
    
    @Override
    public ContactDto getContactDto(int contactId, boolean setDtoHomePhone, boolean setDtoWorkPhone, boolean setDtoEmail, YukonUserContext userContext) {

		LiteContact contact = contactDao.getContact(contactId);
		if (contact == null) {
		    return null;
		}

		ContactDto contactDto = new ContactDto();
    	contactDto.setContactId(contactId);
    	contactDto.setFirstName(contact.getContFirstName());
    	contactDto.setLastName(contact.getContLastName());
        
    	boolean isPrimary = contactDao.isPrimaryContact(contactId);
    	contactDto.setPrimary(isPrimary);
    	
    	List<LiteContactNotification> notificationsForContact = contactNotificationDao.getNotificationsForContact(contactId);
    	
    	if (setDtoHomePhone) {
            contactDto.setHomePhone(getAndFormatAndRemoveFirstOfType(notificationsForContact, ContactNotificationType.HOME_PHONE, userContext));
    	}
        if (setDtoWorkPhone) {
            contactDto.setWorkPhone(getAndFormatAndRemoveFirstOfType(notificationsForContact, ContactNotificationType.WORK_PHONE, userContext));
        }
        if (setDtoEmail) {
            contactDto.setEmail(getAndFormatAndRemoveFirstOfType(notificationsForContact, ContactNotificationType.EMAIL, userContext));
        }

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
    public void saveContactDto(ContactDto contactDto, LiteCustomer customer, LiteYukonUser user) {

        // save contact
        LiteContact contact;
        if (contactDto.getContactId() <= 0) {
            contact = contactService.createAdditionalContact(contactDto.getFirstName(), contactDto.getLastName(), customer, user);
        } else {
            contact = contactDao.getContact(contactDto.getContactId());
        }
        
        updateContactFromContactDto(contact, contactDto);
    }
    
    @Override
    @Transactional
    public void saveContactDto(ContactDto contactDto, LiteYukonUser user) {

        // save contact
        LiteContact contact;
        if (contactDto.getContactId() <= 0) {
            contact = contactService.createContact(contactDto.getFirstName(), contactDto.getLastName(), user);
        } else {
            contact = contactDao.getContact(contactDto.getContactId());
        }
        
        updateContactFromContactDto(contact, contactDto);
    }

    protected void updateContactFromContactDto(LiteContact contact, ContactDto contactDto) {
        // By using a null for loginId in the updateContact method the login id will not be updated.  
        // It does NOT mean we are removing the login from the contact. 
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
    public AccountInfoFragment getAccountInfoFragment(int accountId) {

        if (accountId > 0) {
            AccountSearchResult accountSearchResult =
                operatorAccountSearchDao.getAccountSearchResultForAccountId(accountId);

            AccountInfoFragment accountInfoFragment = new AccountInfoFragment(accountSearchResult);

            return accountInfoFragment;
        } else {
            return null;
        }
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
	
	@Autowired
	public void setOperatorAccountSearchDao(OperatorAccountSearchDao operatorAccountSearchDao) {
		this.operatorAccountSearchDao = operatorAccountSearchDao;
	}

	@Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
}
