package com.cannontech.web.stars.dr.consumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.MessageCodeGenerator;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.consumer.model.ContactNotificationOption;

@Controller
public class ContactController {

    private CustomerAccountDao customerAccountDao;
    private ContactDao contactDao;
    private CustomerDao customerDao;
    private YukonListDao yukonListDao;

    @RequestMapping(value = "/consumer/contacts", method = RequestMethod.GET)
    public String view(HttpServletRequest request, ModelMap map) {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        LiteYukonUser user = userContext.getYukonUser();

        // Only get contacts for the first account for the user
        List<CustomerAccount> accountList = customerAccountDao.getByUser(user);
        CustomerAccount account = accountList.get(0);
        map.addAttribute("account", account);

        // Get the primary contact
        int accountId = account.getAccountId();
        LiteContact contact = contactDao.getPrimaryContactForAccount(accountId);
        map.addAttribute("primaryContact", contact);

        // Get a list of additional contacts
        List<LiteContact> additionalContacts = contactDao.getAdditionalContactsForAccount(accountId);
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
    public String newContact(HttpServletRequest request) {

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        LiteYukonUser user = userContext.getYukonUser();

        int userId = user.getUserID();
        LiteCustomer customer = customerDao.getCustomerForUser(userId);

        LiteContact contact = new LiteContact(-1);
        contact.setContFirstName("");
        contact.setContLastName("");
        contact.setLoginID(userId);

        contactDao.addAdditionalContact(contact, customer);

        return "redirect:/spring/stars/consumer/contacts";
    }

    @RequestMapping(value = "/consumer/contacts/updateContact", method = RequestMethod.POST)
    public String updateContact(int contactId, String firstName,
            String lastName, HttpServletRequest request) {

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
        Map<String, Integer> idMap = this.getIntegerParameters(request,
                                                               "notificationId_");
        Map<String, Integer> typeMap = this.getIntegerParameters(request,
                                                                 "notificationType_");
        Map<String, String> textMap = this.getStringParameters(request,
                                                               "notificationText_");
        Map<String, String> removeMap = this.getStringParameters(request,
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

    @SuppressWarnings("unchecked")
    /**
     * Helper method to put all String parameters with a given prefix into a map
     */
    private Map<String, String> getStringParameters(HttpServletRequest request,
            String prefix) {

        Map<String, String> returnMap = new HashMap<String, String>();

        Map<String, Object> parameterMap = request.getParameterMap();
        for (String key : parameterMap.keySet()) {

            if (key.startsWith(prefix)) {
                String paramKey = key.substring(prefix.length());
                String[] value = (String[]) parameterMap.get(key);

                returnMap.put(paramKey, value[0]);
            }

        }

        return returnMap;
    }

    @SuppressWarnings("unchecked")
    /**
     * Helper method to put all Integer parameters with a given prefix into a
     * map
     */
    private Map<String, Integer> getIntegerParameters(
            HttpServletRequest request, String prefix) {

        Map<String, Integer> returnMap = new HashMap<String, Integer>();

        Map<String, Object> parameterMap = request.getParameterMap();
        for (String key : parameterMap.keySet()) {

            if (key.startsWith(prefix)) {
                String paramKey = key.substring(prefix.length());
                String[] object = (String[]) parameterMap.get(key);
                Integer value = Integer.valueOf(object[0]);

                returnMap.put(paramKey, value);
            }

        }

        return returnMap;
    }

    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
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

}
