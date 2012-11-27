package com.cannontech.web.stars.dr.consumer;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.controlHistory.model.ControlPeriod;
import com.cannontech.stars.dr.displayable.model.DisplayableProgram;
import com.cannontech.stars.dr.optout.model.OptOutEnabled;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.service.OptOutStatusService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_ACCOUNT_GENERAL)
@Controller
public class GeneralController extends AbstractConsumerController {
    private static final String viewName = "consumer/general.jsp";
    
    @Autowired private ContactDao contactDao;
    @Autowired private ContactNotificationDao contactNotificationDao;
    @Autowired private OptOutStatusService optOutStatusService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private GlobalSettingDao globalSettingDao;

    @RequestMapping(value = "/consumer/general", method = RequestMethod.GET)
    public String view(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, ModelMap map) {
        
        List<DisplayableProgram> displayablePrograms = 
            displayableProgramDao.getControlHistorySummary(
                                      customerAccount.getAccountId(),
                                      yukonUserContext,
                                      ControlPeriod.PAST_DAY);
        map.addAttribute("displayablePrograms", displayablePrograms);
        
        boolean promptForEmail = false;
        boolean isNotEnrolled = displayablePrograms.size() == 0;
        map.addAttribute("isNotEnrolled", isNotEnrolled);
        
        OptOutEnabled optOutEnabled = optOutStatusService.getOptOutEnabled(yukonUserContext.getYukonUser());

        if(rolePropertyDao.checkProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_PROGRAMS_OPT_OUT, yukonUserContext.getYukonUser())){
	        if(!optOutEnabled.isOptOutEnabled() || !optOutEnabled.isCommunicationEnabled()){
	            if(!optOutEnabled.isCommunicationEnabled()){
	                map.addAttribute("optOutDisabledKey", "optOutsAndCommuncationDisabledWarning");
	            }else{
	                map.addAttribute("optOutDisabledKey", "optOutsDisabledWarning");
	            }
	        }
        }else if(!optOutEnabled.isCommunicationEnabled()){
        	map.addAttribute("optOutDisabledKey", "communcationDisabledWarning");
        }
        
        //See if we need to prompt for an email address. step 1: Can we recover passwords?
        if(globalSettingDao.checkSetting(GlobalSettingType.ENABLE_PASSWORD_RECOVERY)){
        	//Step 2: Can this user edit their password and access the contacts page?
        	if(rolePropertyDao.checkAllProperties(yukonUserContext.getYukonUser(), 
        											YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_CHANGE_LOGIN_PASSWORD, 
        											YukonRoleProperty.RESIDENTIAL_CONTACTS_ACCESS)){
        		//This user should be prompted unless they have a valid email address
        		promptForEmail = true;
        		
        		//Step 3: Does this user have an email address?
        		LiteContact primaryContact = contactDao.getPrimaryContactForAccount(customerAccount.getAccountId());
                LiteContactNotification emailNotification = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, ContactNotificationType.EMAIL);
                
                if(emailNotification != null){
                	promptForEmail = false;
                }
                
                map.addAttribute("contactId", primaryContact.getContactID());
        	}
        }
        
        map.addAttribute("promptForEmail", promptForEmail);
        
        if (isNotEnrolled) return viewName; // if there are no programs enrolled there is nothing more to show
        
        int accountId = customerAccount.getAccountId();
		List<OptOutEvent> scheduledOptOuts = optOutEventDao.getAllScheduledOptOutEvents(accountId);
        map.addAttribute("scheduledOptOuts", scheduledOptOuts);
        
        // Get primary contact's first email address (used for odds for control)
        LiteContact primaryContact = contactDao.getPrimaryContactForAccount(accountId);
        LiteContactNotification emailNotification = 
        	contactNotificationDao.getFirstNotificationForContactByType(
        			primaryContact, ContactNotificationType.EMAIL);
        
        String email = "";
        boolean emailEnabled = false;
        if(emailNotification != null) {
        	email = emailNotification.getNotification();
        	emailEnabled = !emailNotification.isDisabled();
        }
        
        map.addAttribute("email", email);
        map.addAttribute("emailEnabled", emailEnabled);
        
        // if any of the enrolled programs have a chance of control, show notification
        boolean showNotification = false;
        for(DisplayableProgram program : displayablePrograms) {
            String chanceOfControl = program.getProgram().getChanceOfControl();
        	if(!StringUtils.isEmpty(chanceOfControl) && !chanceOfControl.equalsIgnoreCase(CtiUtilities.STRING_NONE)) {
        		showNotification = true;
        		break;
        	}
        }
        map.addAttribute("showNotification", showNotification);
        
        return viewName;
    }
    
    @RequestMapping(value = "/consumer/general/updateOddsForControlNotification", method = RequestMethod.POST)
    public String updateOddsForControlNotification(
    		@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    		String oddsForControlEmail, HttpServletRequest request, 
    		YukonUserContext yukonUserContext) {
    	
    	int accountId = customerAccount.getAccountId();
    	LiteContact primaryContact = contactDao.getPrimaryContactForAccount(accountId);
        LiteContactNotification emailNotification = 
        	contactNotificationDao.getFirstNotificationForContactByType(
        			primaryContact, ContactNotificationType.EMAIL);
        
        boolean oddsForControlNotification = 
        	ServletRequestUtils.getBooleanParameter(request, "oddsForControlNotification", false);
        
        String disabled = (oddsForControlNotification)? "n" : "y";
        if(emailNotification == null) {
        	emailNotification = 
        		new LiteContactNotification(
        				-1, 
        				primaryContact.getContactID(), 
        				YukonListEntryTypes.YUK_ENTRY_ID_EMAIL, 
        				disabled, 
        				oddsForControlEmail);
        	
        } else {
        	emailNotification.setDisableFlag(disabled);
        	if(!StringUtils.isBlank(oddsForControlEmail)) {
        		emailNotification.setNotification(oddsForControlEmail);
        	}
        }
        
        contactNotificationDao.saveNotification(emailNotification);
    	
    	return "redirect:/stars/consumer/general";
    }
}
