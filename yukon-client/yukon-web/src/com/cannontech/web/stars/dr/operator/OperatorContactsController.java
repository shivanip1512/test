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
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.ContactDto;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.model.DisplayableContactNotificationType;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.service.OperatorAccountService;
import com.cannontech.web.stars.dr.operator.validator.ContactDtoValidator;
import com.google.common.collect.Lists;

@Controller
@RequestMapping(value = "/operator/contacts/*")
public class OperatorContactsController {

    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private OperatorAccountService operatorAccountService;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private CustomerDao customerDao;
    @Autowired private ContactDao contactDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ContactDtoValidator contactDtoValidator;
    @Autowired private RolePropertyDao rolePropertyDao;
    
    // CONTACTS LIST
    @RequestMapping("contactList")
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
    
    
    // CONTACT VIEW
    @RequestMapping("view")
    public String view(int contactId,ModelMap model,YukonUserContext context, AccountInfoFragment fragment) {
        model.addAttribute("mode", PageEditMode.VIEW);
        
        // contactDto
        ContactDto contactDto = operatorAccountService.getContactDto(contactId, context);
        model.addAttribute("contactDto", contactDto);
        
        setupContactModel(contactDto.getContactId(), fragment, model, context);
        
        return "operator/contacts/contactEdit.jsp";
    }
    
    // CONTACT CREATE
    @RequestMapping("create")
    public String create(int contactId, ModelMap model, YukonUserContext context, AccountInfoFragment fragment) {
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        model.addAttribute("mode", PageEditMode.CREATE);
        
        ContactDto contactDto = operatorAccountService.getBlankContactDto(4);
        model.addAttribute("contactDto", contactDto);
        
        setupContactModel(contactDto.getContactId(), fragment, model, context);
        
        return "operator/contacts/contactEdit.jsp";
    }
    
    // CONTACT EDIT
    @RequestMapping("edit")
    public String edit(int contactId, ModelMap model, YukonUserContext context, AccountInfoFragment fragment) {
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        model.addAttribute("mode", PageEditMode.EDIT);
        
        // contactDto
        ContactDto contactDto = operatorAccountService.getContactDto(contactId, context);
        model.addAttribute("contactDto", contactDto);
        
        setupContactModel(contactDto.getContactId(), fragment, model, context);
        
        return "operator/contacts/contactEdit.jsp";
    }
    
    // UPDATE CONTACT
    @RequestMapping("contactUpdate")
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
        
        // retrieve version of original contact name before save for logging
        ContactDto oldContactDto = operatorAccountService.getContactDto(contactDto.getContactId(), userContext);
        String oldContactName = (oldContactDto == null) ? "" : StarsUtils.formatName(oldContactDto.getFirstName(), oldContactDto.getLastName());
        
        if (!bindingResult.hasErrors()) {
            operatorAccountService.saveContactDto(contactDto, customer, userContext.getYukonUser());
        }
        
        setupContactModel(contactDto.getContactId(), accountInfoFragment, modelMap, userContext);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return "operator/contacts/contactEdit.jsp";
        }
        
        // after save, store new contact name for logging
        String newContactName = StarsUtils.formatName(contactDto.getFirstName(), contactDto.getLastName());
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.contact.contactUpdated"));
        if (newContact) {
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.contact.contactCreated"));
            
            accountEventLogService.contactAdded(userContext.getYukonUser(), 
                                                accountInfoFragment.getAccountNumber(), 
                                                newContactName);
        } else {
            // Log contact name change
            if (!oldContactName.equalsIgnoreCase(newContactName)) {
                accountEventLogService.contactNameChanged(userContext.getYukonUser(), 
                                                          accountInfoFragment.getAccountNumber(),
                                                          oldContactName,
                                                          newContactName);
            }
            accountEventLogService.contactUpdated(userContext.getYukonUser(), 
                                                  accountInfoFragment.getAccountNumber(), 
                                                  newContactName);
        }
        
        return "redirect:contactList";
    }
    
    // ADD NOTIFICATION
    @RequestMapping(value="contactUpdate", params = "newNotification")
    public String contactsAddNotification(@ModelAttribute("contactDto") ContactDto contactDto, 
                                            ModelMap modelMap, 
                                            YukonUserContext userContext,
                                            AccountInfoFragment accountInfoFragment) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        modelMap.addAttribute("mode", PageEditMode.EDIT);
        
        contactDto.getOtherNotifications().get(contactDto.getOtherNotifications().size());
        setupContactModel(contactDto.getContactId(), accountInfoFragment, modelMap, userContext);
        
        return "operator/contacts/contactEdit.jsp";
    }
    
    // DELETE ADDITIONAL CONTACT
    @RequestMapping("deleteAdditionalContact")
    public String deleteAdditionalContact(int contactId,
                                          ModelMap modelMap, 
                                          YukonUserContext userContext,
                                          FlashScope flashScope,
                                          AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {

        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        
        LiteContact contact = contactDao.getContact(contactId);
        if (contact != null) {
            contactDao.deleteContact(contact);
            
            // Log contact removal
            String contactName = contact.getContFirstName()+" "+contact.getContLastName();
            accountEventLogService.contactRemoved(userContext.getYukonUser(),
                                                  accountInfoFragment.getAccountNumber(),
                                                  contactName);
        }
        setupContactBasicModelMap(null, accountInfoFragment, modelMap);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.contact.contactDeleted"));
        return "redirect:contactList";
    }
    
    // MISC MODEL ITEMS FOR CONTACT EDITING/VIEWING
    public void setupContactModel(Integer contactId, 
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
            if(user.getUserID() != UserUtils.USER_NONE_ID) {
                modelMap.addAttribute("username", user.getUsername());
            }
        }
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
    }
    
    
}