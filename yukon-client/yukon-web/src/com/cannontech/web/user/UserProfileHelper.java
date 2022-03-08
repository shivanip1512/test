package com.cannontech.web.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.cannontech.common.events.dao.EventLogDao;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.ContactDto;
import com.cannontech.web.stars.dr.operator.service.OperatorAccountService;
import com.cannontech.web.stars.service.PasswordResetService;
import com.cannontech.web.user.model.UserProfile;

@Component
public class UserProfileHelper {

    @Autowired private AuthenticationService authenticationService;
    @Autowired private ContactDao contactDao;
    @Autowired private OperatorAccountService operatorAccountService;
    @Autowired private PasswordPolicyService passwordPolicyService;
    @Autowired private PasswordResetService passwordResetService;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private CustomerDao customerDao;

    private static final int passwordExpireDaysToWarn = 30;

    /**
     * Should be called early on in many methods.
     */
    public void isUserAuthorized(LiteYukonUser user, int userId) throws NotAuthorizedException {
        if (user.getUserID() != userId) {
            throw new NotAuthorizedException("User "+ user.getUsername() +" is not authorized to edit user with id ["+ userId +"]");
        }
    }

    public void setupPasswordData(ModelMap model, LiteYukonUser user) {
        PasswordPolicy pp = passwordPolicyService.getPasswordPolicy(user);
        model.addAttribute("passwordPolicy", pp);
        model.addAttribute("passwordWarning",
                           authenticationService.doesPasswordExpireInDays(user, passwordExpireDaysToWarn));
        model.addAttribute("userGroupId", user.getUserGroupId());
        model.addAttribute("userGroupName", userGroupDao.getLiteUserGroup(user.getUserGroupId()).getUserGroupName());
        model.addAttribute("changePwdKey", passwordResetService.getPasswordKey(user));
    
        UserAuthenticationInfo userAuthenticationInfo = yukonUserDao.getUserAuthenticationInfo(user.getUserID());

        model.addAttribute("passwordLastChangeTimestamp", userAuthenticationInfo.getLastChangedDate());
        AuthenticationCategory authCat = userAuthenticationInfo.getAuthenticationCategory();
        model.addAttribute("canResetPassword", authenticationService.supportsPasswordSet(authCat));
    }


    /**
     * Used by {@link #ProfileController.profile} and {@link #ProfileController.edit}
     * 
     * @param model
     * @param context
     * @return LiteYukonUser
     * 
     * @postcondition model["userProfile"] = UserProfile object
     * @postcondition model["notificationTypes"] = ContactNotificationType[]
     */
    public LiteYukonUser setupUserAndNotifications(ModelMap model, LiteYukonUser user, YukonUserContext context) {

        List<LiteContact> contacts = contactDao.getContactsByLoginId(user.getUserID());
        ContactDto contact = null;
        List<LiteContactNotification> nots = null;
        if(!contacts.isEmpty()) {
            contact = operatorAccountService.getContactDto(contacts.get(0).getContactID(), false, false, true, context);
            //get additional contacts
            LiteCICustomer customer = customerDao.getCICustomerForUser(user);
            if (customer != null) {
                model.addAttribute("displayAdditionalContacts", true);
                List<ContactDto> addContacts = new ArrayList<>();
                List<LiteContact> additionalLiteContacts = contactDao.getAdditionalContactsForCustomer(customer.getCustomerID());
                for (LiteContact additionalLiteContact : additionalLiteContacts) {
                    ContactDto additionalContact = operatorAccountService.getContactDto(additionalLiteContact.getContactID(), context);
                    addContacts.add(additionalContact);
                }
                model.addAttribute("additionalContacts", addContacts);
            }
        }
        model.addAttribute("userProfile", new UserProfile(user, contact, nots));
        model.addAttribute("notificationTypes", ContactNotificationType.values());
        
        return user;
    }

}
