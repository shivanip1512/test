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

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
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

    private AccountEventLogService accountEventLogService;
    
	private OperatorAccountService operatorAccountService;
	private CustomerAccountDao customerAccountDao;
	private CustomerDao customerDao;
	private ContactDao contactDao;
	private YukonUserContextMessageSourceResolver messageSourceResolver;
	private ContactDtoValidator contactDtoValidator;
	private RolePropertyDao rolePropertyDao;
	
	// CONTACTS LIST
	@RequestMapping
    public String contactList(int accountId,
    						  YukonUserContext userContext,
    						  ModelMap modelMap,
    						  AccountInfoFragment accountInfoFragment) {
		
		boolean allowAccountEditing = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
		modelMap.addAttribute("mode", allowAccountEditing ? PageEditMode.EDIT : PageEditMode.VIEW);
		
		// contacts
		List<ContactDto> contacts = Lists.newArrayList();
		
		// primary contact
		CustomerAccount customerAccount = customerAccountDao.getById(accountId);
		LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
		ContactDto primaryContact = operatorAccountService.getContactDto(customer.getPrimaryContactID(), userContext);
		contacts.add(primaryContact);
		
		// additional contacts
		List<LiteContact> additionalLiteContacts = contactDao.getAdditionalContactsForAccount(accountId);
		for (LiteContact additionalLiteContact : additionalLiteContacts) {
			
			ContactDto additionalContact = operatorAccountService.getContactDto(additionalLiteContact.getContactID(), userContext);
			contacts.add(additionalContact);
		}
		modelMap.addAttribute("contacts", contacts);
		
		setupContactBasicModelMap(null, accountInfoFragment, modelMap);
		
		return "operator/contacts/contactList.jsp";
	}
	
	
	// CONTACT EDIT
	@RequestMapping
    public String contactEdit(int contactId,
    						  ModelMap modelMap, 
    						  YukonUserContext userContext,
    						  AccountInfoFragment accountInfoFragment) {
		
	    boolean allowAccountEditing = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
		
		// contactDto
		ContactDto contactDto = operatorAccountService.getContactDto(contactId, userContext);
		if (contactDto == null) {
		    rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
			contactDto = operatorAccountService.getBlankContactDto(4);
			modelMap.addAttribute("mode", PageEditMode.CREATE);
		} else {
		    modelMap.addAttribute("mode", allowAccountEditing ? PageEditMode.EDIT : PageEditMode.VIEW);
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
					    		ModelMap modelMap, 
					    		YukonUserContext userContext,
					    		FlashScope flashScope,
					    		AccountInfoFragment accountInfoFragment) {
		
		rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
		modelMap.addAttribute("mode", PageEditMode.EDIT);
		
		CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
		LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
		
		// validate/save
		boolean newContact = contactDto.getContactId() <= 0;
		contactDtoValidator.validate(contactDto, bindingResult);
		if (!bindingResult.hasErrors()) {
			operatorAccountService.saveContactDto(contactDto, customer);
		}
		
		setupContactEditModelMap(contactDto.getContactId(), accountInfoFragment, modelMap, userContext);
		if (bindingResult.hasErrors()) {
			List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
			flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
			return "operator/contacts/contactEdit.jsp";
		}
		
		String newContactName = contactDto.getFirstName()+" "+contactDto.getLastName();
		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.contact.contactUpdated"));
		if (newContact) {
			flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.contact.contactCreated"));
			
			accountEventLogService.contactAdded(userContext.getYukonUser(), 
			                                    accountInfoFragment.getAccountNumber(), 
			                                    newContactName);
		} else {
		    LiteContact contact = contactDao.getContact(contactDto.getContactId());
		    String oldContactName = contact.toString();
		    accountEventLogService.contactUpdated(userContext.getYukonUser(), 
                                                accountInfoFragment.getAccountNumber(), 
                                                newContactName);
		    
		    // Log contact name change
		    if (!oldContactName.equalsIgnoreCase(newContactName)) {
		        accountEventLogService.contactNameChanged(userContext.getYukonUser(), 
		                                                  accountInfoFragment.getAccountNumber(),
		                                                  oldContactName,
		                                                  newContactName);
		    }
        
		}
		
		return "redirect:contactList";
	}
	
	// ADD NOTIFICATION
	@RequestMapping(params = "newNotification")
    public String contactsAddNotification(@ModelAttribute("contactDto") ContactDto contactDto, 
								    		ModelMap modelMap, 
								    		YukonUserContext userContext,
								    		FlashScope flashScope,
								    		AccountInfoFragment accountInfoFragment) {
		
		rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
		modelMap.addAttribute("mode", PageEditMode.EDIT);
		
		contactDto.getOtherNotifications().get(contactDto.getOtherNotifications().size());
		setupContactEditModelMap(contactDto.getContactId(), accountInfoFragment, modelMap, userContext);
		
		return "operator/contacts/contactEdit.jsp";
	}
	
	// DELETE ADDITIONAL CONTACT
	@RequestMapping
    public String deleteAdditionalContact(int deleteAdditionalContactId,
										  ModelMap modelMap, 
										  YukonUserContext userContext,
										  FlashScope flashScope,
										  AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {

		rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
		
		LiteContact contact = contactDao.getContact(deleteAdditionalContactId);
		contactDao.deleteContact(deleteAdditionalContactId);
		
		// Log contact removal
		String contactName = contact.getContFirstName()+" "+contact.getContLastName();
		accountEventLogService.contactRemoved(userContext.getYukonUser(),
		                                      accountInfoFragment.getAccountNumber(),
		                                      contactName);
		
		setupContactBasicModelMap(null, accountInfoFragment, modelMap);
		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.contact.contactDeleted"));
		return "redirect:contactList";
	}
	
	// MISC MODEL ITEMS FOR CONTACT EDIT
	public void setupContactEditModelMap(Integer contactId, 
										 AccountInfoFragment accountInfoFragment,
										 ModelMap modelMap, 
										 YukonUserContext userContext) {
		
		// basics
		setupContactBasicModelMap(contactId, accountInfoFragment, modelMap);
		
		ContactDto currentContactDto = operatorAccountService.getContactDto(contactId, userContext);
		if (currentContactDto != null) {
			modelMap.addAttribute("firstName", currentContactDto.getFirstName());
			modelMap.addAttribute("lastName", currentContactDto.getLastName());
		}
		
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
		
			LiteYukonUser user = contactDao.getYukonUser(contactId);
			if(user.getUserID() != UserUtils.USER_DEFAULT_ID) {
			    modelMap.addAttribute("username", user.getUsername());
			}
		}
		
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
	}
	
	@Autowired
	public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
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
	public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
		this.messageSourceResolver = messageSourceResolver;
	}
	
	@Autowired
	public void setContactDtoValidator(ContactDtoValidator contactDtoValidator) {
		this.contactDtoValidator = contactDtoValidator;
	}
	
	@Autowired
	public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
	
}