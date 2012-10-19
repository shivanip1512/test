package com.cannontech.web.stars.dr.consumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.MessageCodeGenerator;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.consumer.model.ContactNotificationOption;
import com.cannontech.web.stars.dr.consumer.model.LiteContactValidator;

@CheckRoleProperty(YukonRoleProperty.RESIDENTIAL_CONTACTS_ACCESS)
@Controller
public class ContactController extends AbstractConsumerController {
    @Autowired private ContactDao contactDao;
    @Autowired private ContactNotificationDao contactNotificationDao;
    @Autowired private CustomerDao customerDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private YukonListDao yukonListDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private GlobalSettingDao globalSettingsDao;
    @Autowired private LiteContactValidator liteContactValidator;
    
    @RequestMapping(value = "/consumer/contacts", method = RequestMethod.GET)
    public String index(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    		YukonUserContext yukonUserContext,
            ModelMap map) {

    	// Get the primary contact
        int customerAccountId = customerAccount.getAccountId();
        LiteContact contact = contactDao.getPrimaryContactForAccount(customerAccountId);
        map.addAttribute("primaryContact", contact);

        // Get a list of additional contacts
        List<LiteContact> additionalContacts = contactDao.getAdditionalContactsForAccount(customerAccountId);
        map.addAttribute("additionalContacts", additionalContacts);
        
        promptForEmail(map, yukonUserContext.getYukonUser(), customerAccountId);

        // Get the YukonSelectionList for the contact notification types
        YukonSelectionList selectionList = yukonListDao.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_ID_CONTACT_TYPE);
        List<YukonListEntry> listEntries = selectionList.getYukonListEntries();

        // Build a list of ContactNotificationOption based on the list entries
        // in the notification type list
        List<ContactNotificationOption> notificationOptionList = new ArrayList<ContactNotificationOption>();
        for (YukonListEntry entry : listEntries) {

            int entryID = entry.getEntryID();
            ContactNotificationOption option = new ContactNotificationOption(entryID);
            String text = entry.getEntryText();
            if (CtiUtilities.STRING_NONE.equalsIgnoreCase(text)) {
            	// do not put the '(none)' option into the list
                continue;
            }
            String code = MessageCodeGenerator.generateCode("yukon.dr.consumer.contact",
                                                            text);
            YukonMessageSourceResolvable message = new YukonMessageSourceResolvable(code);
            option.setOptionText(message);

            notificationOptionList.add(option);
        }

        map.addAttribute("notificationOptionList", notificationOptionList);
        return "consumer/contacts/index.jsp";
    }
    
    /**
     * View the specified contact
     */
    @RequestMapping(value="/consumer/contacts/view", method = RequestMethod.GET)
    public String viewContact(int contactId, YukonUserContext user, ModelMap map) {
    	
        setupPageMode(user, map, PageEditMode.VIEW, contactId, true);
    	return "consumer/contacts/edit.jsp";
    }
    
    /**
     * Show a page with an empty contact for the user to create
     */
    @RequestMapping(value="/consumer/contacts/new", method = RequestMethod.GET)
    public String newContact(YukonUserContext user, ModelMap map) {

    	setupPageMode(user, map, PageEditMode.CREATE, null, true);
    	return "consumer/contacts/edit.jsp";
    }
    
    /**
     * Check new Contact for errors, then create it
     */
    @RequestMapping(value="/consumer/contacts/create", method = RequestMethod.POST)
    public String createContact(@ModelAttribute("contact") LiteContact contact,
    							BindingResult bindingResult,
    							YukonUserContext user,
    							FlashScope flashScope,
    							ModelMap map) {
    	
    	int userId = user.getYukonUser().getUserID();
        LiteCustomer customer = customerDao.getCustomerForUser(userId);
        
        //check the new contact for errors
        liteContactValidator.validate(contact, bindingResult);
        if(bindingResult.hasErrors()) {
        	flashScope.setError(YukonValidationUtils.errorsForBindingResult(bindingResult));
        	setupPageMode(user, map, PageEditMode.CREATE, contact.getContactID(), false);
        	map.addAttribute("actionUrl", "/spring/stars/consumer/contacts/create");
        	return "consumer/contacts/edit.jsp";
        } else {
        	flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.contact.contactCreated"));
        	contactDao.addAdditionalContact(contact, customer);
			return "redirect:/spring/stars/consumer/contacts";
        }
    }
    
    /**
     * Edit the specified Contact
     */
    @RequestMapping(value="/consumer/contacts/edit", method=RequestMethod.GET)
    public String editContact(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    						  int contactId,
    						  YukonUserContext user,
    						  ModelMap map) {
    	
    	if(checkPrimaryContact(customerAccount, contactId)){
    		map.put("primaryContact", true);
    	}
    	//check permissions for this user and contact
    	accountCheckerService.checkContact(user.getYukonUser(), Integer.valueOf(contactId));
        setupPageMode(user, map, PageEditMode.EDIT, Integer.valueOf(contactId), true);
        promptForEmail(map, user.getYukonUser(), customerAccount.getAccountId());
    	return "consumer/contacts/edit.jsp";
    }

    @RequestMapping(value = "/consumer/contacts/updateContact", method = RequestMethod.POST)
    public String updateContact(@ModelAttribute("contact")LiteContact contact,
    							BindingResult bindingResult, 
    							YukonUserContext user, 
    							FlashScope flashScope, 
    							ModelMap map) {
    	
    	//check permissions for this user and contact
    	accountCheckerService.checkContact(user.getYukonUser(), contact.getContactID());
    	
    	CustomerAccount account = customerAccountDao.getAccountByContactId(contact.getContactID());
    	
    	//validate contact and notifications
    	liteContactValidator.validate(contact, bindingResult);
    	if(bindingResult.hasErrors()){
    		if(checkPrimaryContact(account, contact.getContactID())){
        		map.put("primaryContact", true);
        	}
    		setupPageMode(user, map, PageEditMode.EDIT, contact.getContactID(), false);
    		promptForEmail(map, user.getYukonUser(), account.getAccountId());
    		List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
    		return "consumer/contacts/edit.jsp";
    	}

    	// If Auto Create Login is true AND this is not the primary contact AND there is no login for this contact, create one.
    	YukonEnergyCompany energyCompany =  yukonEnergyCompanyService.getEnergyCompanyByAccountId(account.getAccountId());
        boolean autoCreateLogin = energyCompanyRolePropertyDao.checkProperty(YukonRoleProperty.AUTO_CREATE_LOGIN_FOR_ADDITIONAL_CONTACTS, energyCompany);
        
        if (autoCreateLogin && !contactDao.isPrimaryContact(contact.getContactID()) 
                && contact.getLoginID() == UserUtils.USER_DEFAULT_ID) {
            List<LiteUserGroup> custUserGroups =  ecMappingDao.getResidentialUserGroups(energyCompany.getEnergyCompanyId());
            LiteYukonUser login = yukonUserDao.createLoginForAdditionalContact(contact.getContFirstName(), contact.getContLastName(), custUserGroups.get(0));
            contact.setLoginID(login.getUserID());
        }
        
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.consumer.contacts.contactSaved"));
        contactDao.saveContact(contact);
    	
    	return "redirect:/spring/stars/consumer/contacts";
    }
    
    @RequestMapping(value="/consumer/contacts/delete", method=RequestMethod.POST)
    public String deleteContact(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    							@ModelAttribute("contact")LiteContact contact,
    							BindingResult bindingResult,
    							YukonUserContext user,
    							FlashScope flashScope,
    							ModelMap map) {
    	//check permissions for this user and contact
    	accountCheckerService.checkContact(user.getYukonUser(), contact.getContactID());
    	
    	//check that they are not trying to delete the primary contact
        if(checkPrimaryContact(customerAccount, contact.getContactID())) {
        	flashScope.setError(new YukonMessageSourceResolvable("yukon.dr.consumer.contacts.cannotDeletePrimaryContact"));
        	return "redirect:/spring/stars/consumer/contacts";
        }
    	
    	contactDao.deleteContact(contact.getContactID());
        //clean-up user login, if exists
        if (contact.getLoginID() > 0) {
            yukonUserDao.deleteUser(contact.getLoginID());
        }
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.contact.contactDeleted"));
    	return "redirect:/spring/stars/consumer/contacts";
    }
    
    /**
     * checks to see if the contact is the primary contact
     * @return
     */
    private boolean checkPrimaryContact(CustomerAccount account, int contactId) {
    	//check that they are not trying to delete the primary contact
    	// Get the primary contact
        int customerAccountId = account.getAccountId();
        LiteContact primaryContact = contactDao.getPrimaryContactForAccount(customerAccountId);
        
        return primaryContact.getContactID() == contactId;
    }
    
    private void setupPageMode(YukonUserContext user, ModelMap map, PageEditMode mode, Integer contactId, Boolean fetchContact) {
    	
    	//set the page mode
    	map.addAttribute("mode", mode);
    	
    	if(fetchContact) {
	    	if(contactId != null) {
	    		map.addAttribute("contact", contactDao.getContact(contactId));
	    	}else{
	    		LiteContact contact = new LiteContact(-1);
	    		contact.setLoginID(UserUtils.USER_DEFAULT_ID);
	    		map.addAttribute("contact", contact);
	    		contactId = contact.getContactID();
	    	}
    	}
    	
    	switch(mode) {
    	case VIEW:
    		break;
    	case CREATE:
    		map.put("actionUrl", "/spring/stars/consumer/contacts/create");
    		break;
    	case EDIT:
    		map.put("actionUrl", "/spring/stars/consumer/contacts/updateContact");
    		break;
		default: break;
    	}
    	
    	//add notification template
    	LiteContactNotification notification = new LiteContactNotification(-1,
    			contactId,
    			ContactNotificationType.HOME_PHONE.getDefinitionId(),
    			"N",
    			"");
    	map.addAttribute("notificationTemplate", notification);
    	
    	// add the notification types for select fields
    	MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(user);
    	Map<Integer,String> referenceData = new HashMap<Integer, String>();
    	for (ContactNotificationType contactNotificationType : ContactNotificationType.values()) {
    		referenceData.put(contactNotificationType.getDefinitionId(), messageSourceAccessor.getMessage(contactNotificationType.getFormatKey()));
    	}
    	map.addAttribute("notificationOptionList", referenceData);
    }
    
    private void promptForEmail(ModelMap map, LiteYukonUser user, int accountId){
    	boolean promptForEmail = false;
    	//See if we need to prompt for an email address. step 1: Can we recover passwords?
        if(globalSettingsDao.checkSetting(GlobalSettingType.ENABLE_PASSWORD_RECOVERY)){
        	//Step 2: Can this user edit their password and access the contacts page?
        	if(rolePropertyDao.checkAllProperties(user, 
        											YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_CHANGE_LOGIN_PASSWORD, 
        											YukonRoleProperty.RESIDENTIAL_CONTACTS_ACCESS)){
        		//This user should be prompted unless they have a valid email address
        		promptForEmail = true;
        		
        		//Step 3: Does this user have an email address?
        		LiteContact primaryContact = contactDao.getPrimaryContactForAccount(accountId);
                LiteContactNotification emailNotification = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, ContactNotificationType.EMAIL);
                
                if(emailNotification != null){
                	promptForEmail = false;
                }
        	}
        }
        
        map.addAttribute("promptForEmail", promptForEmail);
    }
}