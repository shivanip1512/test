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
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.controlhistory.model.ControlPeriod;
import com.cannontech.stars.dr.displayable.model.DisplayableProgram;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_ACCOUNT_GENERAL)
@Controller
public class GeneralController extends AbstractConsumerController {
    private static final String viewName = "consumer/general.jsp";
    
    private ContactDao contactDao;
    private ContactNotificationDao contactNotificationDao;

    @RequestMapping(value = "/consumer/general", method = RequestMethod.GET)
    public String view(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, ModelMap map) {
        
        List<DisplayableProgram> displayablePrograms = displayableProgramDao.getDisplayablePrograms(customerAccount, yukonUserContext,ControlPeriod.PAST_DAY);
        map.addAttribute("displayablePrograms", displayablePrograms);
        
        boolean isNotEnrolled = displayablePrograms.size() == 0;
        map.addAttribute("isNotEnrolled", isNotEnrolled);

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
        	if(!StringUtils.isEmpty(program.getProgram().getChanceOfControl())) {
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
    	
    	return "redirect:/spring/stars/consumer/general";
    }
    
    @Autowired
    public void setContactDao(ContactDao contactDao) {
		this.contactDao = contactDao;
	}
    
    @Autowired
    public void setContactNotificationDao(ContactNotificationDao contactNotificationDao) {
		this.contactNotificationDao = contactNotificationDao;
	}
    
}
