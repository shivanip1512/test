package com.cannontech.web.stars.dr.operator.general.service.impl;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.stars.dr.account.dao.AccountSiteDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.AccountSite;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.general.model.DisplayableContact;
import com.cannontech.web.stars.dr.operator.general.model.DisplayableContactNotification;
import com.cannontech.web.stars.dr.operator.general.model.OperatorGeneralUiExtras;
import com.cannontech.web.stars.dr.operator.general.service.OperatorGeneralService;
import com.google.common.collect.Lists;

public class OperatorGeneralServiceImpl implements OperatorGeneralService {

	private CustomerDao customerDao;
	private CustomerAccountDao customerAccountDao;
	private ContactDao contactDao;
	private ContactNotificationDao contactNotificationDao;
	private AccountSiteDao accountSiteDao;
	private AddressDao addressDao;
	private RolePropertyDao rolePropertyDao;
	
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
        operatorGeneralUiExtras.setNotes(customerAccount.getAccountNotes());
        
        // account site notes
        operatorGeneralUiExtras.setAccountSiteNotes(accountSite.getPropertyNotes());
        
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
    public DisplayableContact findDisplayableContact(int contactId, boolean isPrimary) {

    	LiteContact contact = null;
    	try {
    		contact = contactDao.getContact(contactId);
    	} catch (DataAccessException e) {
    		return null;
    	}
    	
    	DisplayableContactNotification firstHomePhoneNotification = null;
    	DisplayableContactNotification firstWorkPhoneNotification = null;
    	DisplayableContactNotification firstEmailNotification = null;
    	List<DisplayableContactNotification> otherDisplayableContactNotifications = Lists.newArrayList();
    	
    	List<LiteContactNotification> notificationsForContact = contactNotificationDao.getNotificationsForContact(contactId);
    	for (LiteContactNotification notification : notificationsForContact) {
    		
    		if (firstHomePhoneNotification == null && notification.getContactNotificationType() == ContactNotificationType.HOME_PHONE) {
    			firstHomePhoneNotification = new DisplayableContactNotification(notification);
    		} else if (firstWorkPhoneNotification == null && notification.getContactNotificationType() == ContactNotificationType.WORK_PHONE) {
    			firstWorkPhoneNotification = new DisplayableContactNotification(notification);
    		} else if (firstHomePhoneNotification == null && notification.getContactNotificationType() == ContactNotificationType.EMAIL) {
    			firstEmailNotification = new DisplayableContactNotification(notification);
    		} else {
    			
    			DisplayableContactNotification displayableContactNotification = new DisplayableContactNotification(notification);
    			otherDisplayableContactNotifications.add(displayableContactNotification);
    		}
    	}
    	
    	DisplayableContact displayableContact = new DisplayableContact(contact, firstHomePhoneNotification, firstWorkPhoneNotification, firstEmailNotification, otherDisplayableContactNotifications, isPrimary);
    	return displayableContact;
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
}
