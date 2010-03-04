package com.cannontech.web.stars.dr.operator.general;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.service.ContactNotificationFormattingService;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.general.service.ContactNotificationService;
import com.cannontech.stars.dr.general.service.ContactService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.menu.renderer.SelectMenuConfiguration;
import com.cannontech.web.stars.dr.operator.OperatorActionsFactory;
import com.cannontech.web.stars.dr.operator.general.model.DisplayableContact;
import com.cannontech.web.stars.dr.operator.general.model.DisplayableContactNotification;
import com.cannontech.web.stars.dr.operator.general.service.OperatorGeneralService;
import com.google.common.collect.Lists;

@Controller
@RequestMapping(value = "/operator/general/contacts/*")
public class OperatorGeneralContactsController {

	private OperatorGeneralService operatorGeneralService;
	private CustomerAccountDao customerAccountDao;
	private CustomerDao customerDao;
	private ContactDao contactDao;
	private ContactNotificationDao contactNotificationDao;
	private ContactNotificationService contactNotificationService;
	private ContactService contactService;
	private ContactNotificationFormattingService contactNotificationFormattingService;
	
	// CONTACTS LIST
	@RequestMapping
    public String contactList(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
		
		// account
		int accountId = ServletRequestUtils.getRequiredIntParameter(request, "accountId");
		int energyCompanyId = ServletRequestUtils.getRequiredIntParameter(request, "energyCompanyId");
		CustomerAccount customerAccount = customerAccountDao.getById(accountId);
		modelMap.addAttribute("accountNumber", customerAccount.getAccountNumber());
		modelMap.addAttribute("accountId", accountId);
		modelMap.addAttribute("energyCompanyId", energyCompanyId);
		
		// operatorTempMenu
		SelectMenuConfiguration operatorTempMenu = OperatorActionsFactory.getAccountActionsSelectMenuConfiguration(accountId, energyCompanyId, userContext);
		modelMap.addAttribute("operatorTempMenu", operatorTempMenu);
		
		// contacts
		List<DisplayableContact> contacts = Lists.newArrayList();
		
		// primary contact
		LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
		DisplayableContact primaryContact = operatorGeneralService.findDisplayableContact(customer.getPrimaryContactID(), true);
		contacts.add(primaryContact);
		
		// additional contacts
		List<LiteContact> additionalLiteContacts = contactDao.getAdditionalContactsForAccount(accountId);
		for (LiteContact additionalLiteContact : additionalLiteContacts) {
			
			DisplayableContact additionalContact = operatorGeneralService.findDisplayableContact(additionalLiteContact.getContactID(), false);
			contacts.add(additionalContact);
		}
		
		modelMap.addAttribute("contacts", contacts);
		
		return "operator/general/contacts/contactList.jsp";
	}
	
	
	// CONTACT EDIT
	@RequestMapping
    public String contactEdit(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
		
		// account
		int accountId = ServletRequestUtils.getRequiredIntParameter(request, "accountId");
		int energyCompanyId = ServletRequestUtils.getRequiredIntParameter(request, "energyCompanyId");
		CustomerAccount customerAccount = customerAccountDao.getById(accountId);
		modelMap.addAttribute("accountNumber", customerAccount.getAccountNumber());
		modelMap.addAttribute("accountId", accountId);
		modelMap.addAttribute("energyCompanyId", energyCompanyId);
		
		// contactId
		int contactId = ServletRequestUtils.getIntParameter(request, "contactId", 0);
		
		// operatorTempMenu
		SelectMenuConfiguration operatorTempMenu = OperatorActionsFactory.getAccountActionsSelectMenuConfiguration(accountId, energyCompanyId, userContext);
		modelMap.addAttribute("operatorTempMenu", operatorTempMenu);
		
		// notification types
		ContactNotificationType[] notificationTypes = ContactNotificationType.values();
		modelMap.addAttribute("notificationTypes", notificationTypes);
		
		// contact
		LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
		int primaryContactId = customer.getPrimaryContactID();
		DisplayableContact contact = null;
		if (contactId > 0) {
			contact = operatorGeneralService.findDisplayableContact(contactId, contactId == primaryContactId);
		}
		modelMap.addAttribute("contact", contact);
		
		// blank notifications
//		int newNotifs = ServletRequestUtils.getIntParameter(request, "newNotifs", contactId == 0 ? 4 : 0);
//		modelMap.addAttribute("newNotifs", newNotifs);

		return "operator/general/contacts/contactEdit.jsp";
	}
	
	// UPDATE CONTACT
	@RequestMapping
	@Transactional
    public String updateContact(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
		
		int accountId = ServletRequestUtils.getRequiredIntParameter(request, "accountId");
		int energyCompanyId = ServletRequestUtils.getRequiredIntParameter(request, "energyCompanyId");
		modelMap.addAttribute("accountId", accountId);
		modelMap.addAttribute("energyCompanyId", energyCompanyId);
		
		// contact
		int contactId = ServletRequestUtils.getIntParameter(request, "contactId", 0);
		
		String firstName = ServletRequestUtils.getStringParameter(request, "firstName", "");
		String lastName = ServletRequestUtils.getStringParameter(request, "lastName", "");

		LiteContact contact = null;
		if (contactId == 0) {
			
			contact = contactService.createContact(firstName, lastName, null);
			
			// add additional contact to customer
			CustomerAccount customerAccount = customerAccountDao.getById(accountId);
			LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
			contactDao.addAdditionalContact(contact, customer);
			
		} else {
			contact = contactDao.getContact(contactId);
			contactService.updateContact(contact, firstName, lastName, null);
		}
		modelMap.addAttribute("contactId", contact.getContactID());
		
		// other notifications
		Map<String, String> newNotificationTypesMap = ServletUtil.getStringParameters(request, "notificationType");
		Map<String, String> newNotificationValuesMap = ServletUtil.getStringParameters(request, "notificationValue");
		for (String notificationNumber : newNotificationTypesMap.keySet()) {
			
			String notificationTypeStr = newNotificationTypesMap.get(notificationNumber);
			String notificationValue = newNotificationValuesMap.get(notificationNumber);
			if (StringUtils.isNotBlank(notificationTypeStr) && StringUtils.isNotBlank(notificationValue)) {
				
				ContactNotificationType notificationType = ContactNotificationType.valueOf(notificationTypeStr);

				// new notification
				if (contactId == 0) {
					contactNotificationService.createFormattedNotification(contact, notificationType, notificationValue, userContext);
				
				// update notification
				} else {
					int notificationId = Integer.parseInt(notificationNumber);
					contactNotificationService.updateFormattedNotification(contactId, notificationId, notificationType, notificationValue, userContext);
				}

			// type or value is blank, delete
			} else {
				if (contactId > 0) {
					int notificationId = Integer.parseInt(notificationNumber);
					contactNotificationDao.removeNotification(notificationId);
				}
			}
		}
		
		// first notification
		String homePhone = ServletRequestUtils.getStringParameter(request, "homePhone");
		String workPhone = ServletRequestUtils.getStringParameter(request, "workPhone");
		String email = ServletRequestUtils.getStringParameter(request, "email");
		
		DisplayableContact displayableContact = operatorGeneralService.findDisplayableContact(contactId, false);
		DisplayableContactNotification firstHomePhoneNotification = displayableContact.getFirstHomePhoneNotification();
		DisplayableContactNotification firstWorkPhoneNotification = displayableContact.getFirstWorkPhoneNotification();
		DisplayableContactNotification firstEmailNotification = displayableContact.getFirstEmailNotification();
		
		if (firstHomePhoneNotification != null) {
			
			int homePhoneId = firstHomePhoneNotification.getNotification().getContactNotifID();
			if (StringUtils.isNotBlank(homePhone)) {
				contactNotificationService.updateFormattedNotification(contactId, homePhoneId, ContactNotificationType.HOME_PHONE, homePhone, userContext);
			} else {
				contactNotificationService.createFormattedNotification(contact, ContactNotificationType.HOME_PHONE, homePhone, userContext);
			}
		}
		
		

		return "redirect:contactEdit";
	}
	
	// ADD NOTIFICATION DIALOG
	@RequestMapping
    public String addNotificationDialog(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
		
		int accountId = ServletRequestUtils.getRequiredIntParameter(request, "accountId");
		int energyCompanyId = ServletRequestUtils.getRequiredIntParameter(request, "energyCompanyId");
		int contactId = ServletRequestUtils.getRequiredIntParameter(request, "contactId");
		modelMap.addAttribute("accountId", accountId);
		modelMap.addAttribute("energyCompanyId", energyCompanyId);
		modelMap.addAttribute("contactId", contactId);
		
		ContactNotificationType[] notificationTypes = ContactNotificationType.values();
		modelMap.addAttribute("notificationTypes", notificationTypes);

		return "operator/general/contacts/addNotificationDialog.jsp";
	}
	
	// ADD NOTIFICATION
	@RequestMapping
    public String addNotification(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
		
		int accountId = ServletRequestUtils.getRequiredIntParameter(request, "accountId");
		int energyCompanyId = ServletRequestUtils.getRequiredIntParameter(request, "energyCompanyId");
		int contactId = ServletRequestUtils.getRequiredIntParameter(request, "contactId");
		modelMap.addAttribute("accountId", accountId);
		modelMap.addAttribute("energyCompanyId", energyCompanyId);
		modelMap.addAttribute("contactId", contactId);
		
		LiteContact contact = contactDao.getContact(contactId);
		String notificationTypeStr = ServletRequestUtils.getRequiredStringParameter(request, "notificationType");
		ContactNotificationType notificationType = ContactNotificationType.valueOf(notificationTypeStr);
		String notificationValue = ServletRequestUtils.getRequiredStringParameter(request, "notificationValue");
		
		LiteContactNotification notification = contactNotificationService.createNotification(contact, notificationType, notificationValue);
		contactNotificationDao.saveNotification(notification);

		return "redirect:contactEdit";
	}
	
	// REMOVE NOTIFICATION
	@RequestMapping
    public String contactsRemoveNotification(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
		
		int accountId = ServletRequestUtils.getRequiredIntParameter(request, "accountId");
		int energyCompanyId = ServletRequestUtils.getRequiredIntParameter(request, "energyCompanyId");
		int contactId = ServletRequestUtils.getRequiredIntParameter(request, "contactId");
		modelMap.addAttribute("accountId", accountId);
		modelMap.addAttribute("energyCompanyId", energyCompanyId);
		modelMap.addAttribute("contactId", contactId);
		
		int removeNotificationId = ServletRequestUtils.getRequiredIntParameter(request, "removeNotificationId");
		contactNotificationDao.removeNotification(removeNotificationId);

		return "redirect:contactEdit";
	}
	
	// DELETE ADDITIONAL CONTACT
	@RequestMapping
    public String deleteAdditionalContact(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
		
		int accountId = ServletRequestUtils.getRequiredIntParameter(request, "accountId");
		int energyCompanyId = ServletRequestUtils.getRequiredIntParameter(request, "energyCompanyId");
		int contactId = ServletRequestUtils.getRequiredIntParameter(request, "contactId");
		modelMap.addAttribute("accountId", accountId);
		modelMap.addAttribute("energyCompanyId", energyCompanyId);

		contactDao.removeAdditionalContact(contactId);

		return "redirect:contactList";
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
	public void setOperatorGeneralService(OperatorGeneralService operatorGeneralService) {
		this.operatorGeneralService = operatorGeneralService;
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
	public void setContactNotificationService(ContactNotificationService contactNotificationService) {
		this.contactNotificationService = contactNotificationService;
	}
	
	@Autowired
	public void setContactService(ContactService contactService) {
		this.contactService = contactService;
	}
	
	@Autowired
	public void setContactNotificationFormattingService(ContactNotificationFormattingService contactNotificationFormattingService) {
		this.contactNotificationFormattingService = contactNotificationFormattingService;
	}
}
