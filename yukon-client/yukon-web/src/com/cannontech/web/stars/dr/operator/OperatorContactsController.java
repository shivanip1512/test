package com.cannontech.web.stars.dr.operator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.model.ContactDto;
import com.cannontech.web.stars.dr.operator.model.DisplayableContactNotificationType;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.service.OperatorAccountService;
import com.cannontech.web.stars.dr.operator.validator.ContactDtoValidator;
import com.google.common.collect.Lists;

@Controller
@RequestMapping(value = "/operator/contacts/*")
public class OperatorContactsController {

	private OperatorAccountService operatorAccountService;
	private CustomerAccountDao customerAccountDao;
	private CustomerDao customerDao;
	private ContactDao contactDao;
	private ContactNotificationDao contactNotificationDao;
	private YukonUserContextMessageSourceResolver messageSourceResolver;
	private ContactDtoValidator contactDtoValidator;
	
	// CONTACTS LIST
	@RequestMapping
    public String contactList(int accountId,
    						  YukonUserContext userContext,
    						  ModelMap modelMap,
    						  AccountInfoFragment accountInfoFragment) {
		
		// contacts
		List<ContactDto> contacts = Lists.newArrayList();
		
		// primary contact
		CustomerAccount customerAccount = customerAccountDao.getById(accountId);
		LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
		ContactDto primaryContact = operatorAccountService.getContactDto(customer.getPrimaryContactID(), 0, userContext);
		contacts.add(primaryContact);
		
		// additional contacts
		List<LiteContact> additionalLiteContacts = contactDao.getAdditionalContactsForAccount(accountId);
		for (LiteContact additionalLiteContact : additionalLiteContacts) {
			
			ContactDto additionalContact = operatorAccountService.getContactDto(additionalLiteContact.getContactID(), 0, userContext);
			contacts.add(additionalContact);
		}
		modelMap.addAttribute("contacts", contacts);
		
		setupContactBasicModelMap(null, accountInfoFragment, modelMap);
		return "operator/contacts/contactList.jsp";
	}
	
	
	// CONTACT EDIT
	@RequestMapping
    public String contactEdit(int contactId,
    						  Integer additionalBlankNotifications,
    						  ModelMap modelMap, 
    						  YukonUserContext userContext,
    						  AccountInfoFragment accountInfoFragment) {
		
		// contactDto
		if (additionalBlankNotifications == null) {
			additionalBlankNotifications = 0;
		}
		ContactDto contactDto = operatorAccountService.getContactDto(contactId, additionalBlankNotifications, userContext);
		if (contactDto == null) {
			contactDto = operatorAccountService.getBlankContactDto(4);
		} else {
			modelMap.addAttribute("hasPendingNewNotification", additionalBlankNotifications > 0);
		}
		modelMap.addAttribute("contactDto", contactDto);
		
		setupContactEditModelMap(contactDto.getContactId(), accountInfoFragment, modelMap, userContext);
		return "operator/contacts/contactEdit.jsp";
	}
	
	// UPDATE CONTACT
	@RequestMapping
    public String contactUpdate(@ModelAttribute("contactDto") ContactDto contactDto, 
    							BindingResult bindingResult,
					    		Integer additionalBlankNotifications,
					    		boolean hasPendingNewNotification,
					    		ModelMap modelMap, 
					    		YukonUserContext userContext,
					    		FlashScope flashScope,
					    		AccountInfoFragment accountInfoFragment) {
		
		// validate/save
		contactDtoValidator.validate(contactDto, bindingResult);
		if (!bindingResult.hasErrors()) {
			operatorAccountService.saveContactDto(contactDto);
		}
		
		setupContactEditModelMap(contactDto.getContactId(), accountInfoFragment, modelMap, userContext);
		if (bindingResult.hasErrors()) {
			List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
			flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
			return "operator/contacts/contactEdit.jsp";
		}
		
		// additionalBlankNotifications
		if (additionalBlankNotifications == null) {
			additionalBlankNotifications = 0;
		}
		modelMap.addAttribute("additionalBlankNotifications", additionalBlankNotifications);
		
		if (additionalBlankNotifications <= 0) {
			flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.contactEdit.contactUpdated"));
		}
		return "redirect:contactEdit";
	}
	
	// REMOVE NOTIFICATION
	@RequestMapping
    public String contactsRemoveNotification(int contactId,
    										 int removeNotificationId,
    										 ModelMap modelMap, 
    										 FlashScope flashScope,
    										 AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {
		
		contactNotificationDao.removeNotification(removeNotificationId);
		
		setupContactBasicModelMap(contactId, accountInfoFragment, modelMap);
		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.contactEdit.notificationDeleted"));
		return "redirect:contactEdit";
	}
	
	// DELETE ADDITIONAL CONTACT
	@RequestMapping
    public String deleteAdditionalContact(int deleteAdditionalContactId,
										  ModelMap modelMap, 
										  FlashScope flashScope,
										  AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {

		contactDao.removeAdditionalContact(deleteAdditionalContactId);
		
		setupContactBasicModelMap(null, accountInfoFragment, modelMap);
		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.contactEdit.contactDeleted"));
		return "redirect:contactList";
	}
	
	// MISC MODEL ITEMS FOR CONTACT EDIT
	public void setupContactEditModelMap(Integer contactId, 
										 AccountInfoFragment accountInfoFragment,
										 ModelMap modelMap, 
										 YukonUserContext userContext) {
		
		// basics
		setupContactBasicModelMap(contactId, accountInfoFragment, modelMap);
		
		// notification types
		MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
		List<DisplayableContactNotificationType> notificationTypes = Lists.newArrayListWithCapacity(ContactNotificationType.values().length);
		for (ContactNotificationType contactNotificationType : ContactNotificationType.values()) {
			notificationTypes.add(new DisplayableContactNotificationType(contactNotificationType, messageSourceAccessor.getMessage(contactNotificationType.getFormatKey())));
		}
		modelMap.addAttribute("notificationTypes", notificationTypes);
	}
	
	private void setupContactBasicModelMap(Integer contactId, AccountInfoFragment accountInfoFragment, ModelMap modelMap) {
		
		// contact
		if (contactId != null && contactId > 0) {
			modelMap.addAttribute("contactId", contactId);
		}
		
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
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
	public void setOperatorAccountService(OperatorAccountService operatorAccountService) {
		this.operatorAccountService = operatorAccountService;
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
	public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
		this.messageSourceResolver = messageSourceResolver;
	}
	
	@Autowired
	public void setContactDtoValidator(ContactDtoValidator contactDtoValidator) {
		this.contactDtoValidator = contactDtoValidator;
	}
}
