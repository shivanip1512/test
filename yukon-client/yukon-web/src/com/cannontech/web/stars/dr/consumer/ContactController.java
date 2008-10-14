package com.cannontech.web.stars.dr.consumer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.user.YukonGroup;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.i18n.MessageCodeGenerator;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.user.UserUtils;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.consumer.model.ContactNotificationOption;

@CheckRole(ResidentialCustomerRole.ROLEID)
@CheckRoleProperty(ResidentialCustomerRole.CONTACTS_ACCESS)
@Controller
public class ContactController extends AbstractConsumerController {
    private ContactDao contactDao;
    private CustomerDao customerDao;
    private YukonListDao yukonListDao;
    private YukonUserDao yukonUserDao;

    @RequestMapping(value = "/consumer/contacts", method = RequestMethod.GET)
    public String view(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            ModelMap map) {

        // Get the primary contact
        int customerAccountId = customerAccount.getAccountId();
        LiteContact contact = contactDao.getPrimaryContactForAccount(customerAccountId);
        map.addAttribute("primaryContact", contact);

        // Get a list of additional contacts
        List<LiteContact> additionalContacts = contactDao.getAdditionalContactsForAccount(customerAccountId);
        map.addAttribute("additionalContacts", additionalContacts);

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

        return "consumer/contacts.jsp";
    }

    @RequestMapping(value = "/consumer/contacts/newContact", method = RequestMethod.POST)
    public String newContact(LiteYukonUser user) {

        int userId = user.getUserID();
        LiteCustomer customer = customerDao.getCustomerForUser(userId);

        LiteContact contact = new LiteContact(-1);
        contact.setContFirstName("");
        contact.setContLastName("");
        contact.setLoginID(-9999);

        contactDao.addAdditionalContact(contact, customer);
        
        return "redirect:/spring/stars/consumer/contacts";
    }

    @RequestMapping(value = "/consumer/contacts/updateContact", method = RequestMethod.POST)
    public String updateContact(int contactId, String firstName,
            String lastName, LiteYukonUser user, HttpServletRequest request) {

        accountCheckerService.checkContact(user, contactId);
        
        LiteContact contact = contactDao.getContact(contactId);
        contact.setContFirstName(firstName);
        contact.setContLastName(lastName);
        
        // If remove contact button was clicked, remove
        String remove = ServletRequestUtils.getStringParameter(request, "remove", null);
        
        if(remove != null) {
            contactDao.removeAdditionalContact(contactId);
            
        } else {
            
            
            List<LiteContactNotification> notificationList = this.getNotifications(request,
                                                                                   contactId);
    
            this.addNewNotification(request, contactId, notificationList);
    
            contact.setNotifications(notificationList);
            
            if(DaoFactory.getAuthDao().checkRoleProperty(user.getUserID(), ConsumerInfoRole.CREATE_LOGIN_FOR_ACCOUNT) || 
                    DaoFactory.getAuthDao().checkRoleProperty(user.getUserID(), ResidentialCustomerRole.CREATE_LOGIN_FOR_ACCOUNT)) {
            
                StarsYukonUser starsUser = (StarsYukonUser) request.getSession().getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
                // If not primary contact and no login exists, create it.
                if(!contactDao.isPrimaryContact(contactId) && contact.getLoginID() == -9999) {
               
                    YukonUser login = new YukonUser();
                    LiteStarsEnergyCompany liteEC = StarsDatabaseCache.getInstance().getEnergyCompany( starsUser.getEnergyCompanyID() );
                    LiteYukonGroup[] custGroups = liteEC.getResidentialCustomerGroups();
                    String time = new Long(Calendar.getInstance().getTimeInMillis()).toString();
                    String firstInitial= "";
                    if(firstName != null) {
                        firstInitial = firstName.toLowerCase().substring(0,1);
                    }
                    String newUserName = firstInitial + lastName.toLowerCase();
                    if (yukonUserDao.getLiteYukonUser( newUserName ) != null) {
                        newUserName = lastName.toLowerCase() + time.substring(time.length() - 2);
                    }
                    login.getYukonUser().setUsername(newUserName);
                    login.getYukonUser().setAuthType(AuthType.NONE);
                    login.getYukonGroups().addElement(((YukonGroup)LiteFactory.convertLiteToDBPers(custGroups[0])).getYukonGroup());
                    login.getYukonUser().setStatus(UserUtils.STATUS_ENABLED);
                    try {
                        login = Transaction.createTransaction(Transaction.INSERT, login).execute();
                    } catch (TransactionException e) {
                        CTILogger.error(e);
                    }
                    LiteYukonUser liteUser = new LiteYukonUser( login.getUserID().intValue() );
                    ServerUtils.handleDBChange(liteUser, DBChangeMsg.CHANGE_TYPE_ADD);
                    contact.setLoginID(login.getUserID().intValue());
                }
            }
            
            contactDao.saveContact(contact);
        }
        
        return "redirect:/spring/stars/consumer/contacts";
    }

    /**
     * Helper method to get all of the notifications for the contact out of the
     * request
     * @param request - Current request
     * @param contactId - Id of current contact
     * @return List of notifications for the contact
     */
    private List<LiteContactNotification> getNotifications(
            HttpServletRequest request, int contactId) {

        // Create a map for each of the values in the notifications
        Map<String, Integer> idMap = ServletUtil.getIntegerParameters(request,
                                                               "notificationId_");
        Map<String, Integer> typeMap = ServletUtil.getIntegerParameters(request,
                                                                 "notificationType_");
        Map<String, String> textMap = ServletUtil.getStringParameters(request,
                                                               "notificationText_");
        Map<String, String> removeMap = ServletUtil.getStringParameters(request,
                                                                 "removeNotification_");

        List<LiteContactNotification> notificationList = new ArrayList<LiteContactNotification>();
        // Create a notification for each entry in the maps
        for (String key : idMap.keySet()) {

            Integer notificationId = idMap.get(key);

            // If the remove image was clicked, the key won't be in the remove
            // map but '{key}.x' and '{key}.y' will be
            String keyX = key + ".x";
            String keyY = key + ".y";

            if (!removeMap.containsKey(key) && !removeMap.containsKey(keyX) && !removeMap.containsKey(keyY)) {
                Integer notificationType = typeMap.get(key);
                String notificationText = textMap.get(key);

                LiteContactNotification notification = new LiteContactNotification(notificationId,
                                                                                   contactId,
                                                                                   notificationType,
                                                                                   "N",
                                                                                   notificationText);
                notificationList.add(notification);
            }
        }

        return notificationList;
    }

    /**
     * Helper method to add a new notification to the notification list if the
     * 'Add Notification' button was clicked
     * @param request - Current request
     * @param contactId - Id of contact to add notification for
     * @param notificationList - Contact's list of notifications
     */
    private void addNewNotification(HttpServletRequest request, int contactId,
            List<LiteContactNotification> notificationList) {

        // Add notification button was clicked - add a new notification
        String addNotification = ServletRequestUtils.getStringParameter(request,
                                                                        "addNotification",
                                                                        null);
        if (addNotification != null) {
            LiteContactNotification notification = new LiteContactNotification(-1,
                                                                               contactId,
                                                                               0,
                                                                               "N",
                                                                               "");
            notificationList.add(notification);
        }
    }

    @Autowired
    public void setContactDao(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    @Autowired
    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Autowired
    public void setYukonListDao(YukonListDao yukonListDao) {
        this.yukonListDao = yukonListDao;
    }
    
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
}
