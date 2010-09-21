package com.cannontech.web.stars.dr.consumer;

import java.util.ArrayList;
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

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.i18n.MessageCodeGenerator;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.account.exception.InvalidNotificationException;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.user.UserUtils;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.consumer.model.ContactNotificationOption;

@CheckRoleProperty(YukonRoleProperty.RESIDENTIAL_CONTACTS_ACCESS)
@Controller
public class ContactController extends AbstractConsumerController {
    private ContactDao contactDao;
    private ContactNotificationDao contactNotificationDao;
    private CustomerDao customerDao;
    private YukonListDao yukonListDao;
    private YukonUserDao yukonUserDao;
    private StarsDatabaseCache starsDatabaseCache;
    private EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao;

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
        contact.setContFirstName("New Contact");
        contact.setContLastName("New Contact");
        contact.setLoginID(UserUtils.USER_DEFAULT_ID);

        contactDao.addAdditionalContact(contact, customer);
        
        return "redirect:/spring/stars/consumer/contacts";
    }

    @RequestMapping(value = "/consumer/contacts/updateContact", method = RequestMethod.POST)
    public String updateContact(int contactId, String firstName,
            String lastName, LiteYukonUser user, HttpServletRequest request, ModelMap map) {

        try {
            accountCheckerService.checkContact(user, contactId);

            LiteContact contact = contactDao.getContact(contactId);
            contact.setContFirstName(firstName);
            contact.setContLastName(lastName);

            // If remove contact button was clicked, remove
            String remove = ServletRequestUtils.getStringParameter(request, "remove", null);

            if(remove != null) {
                contactDao.deleteContact(contactId);
                //clean-up user login, if exists
                if (contact.getLoginID() > 0) {
                    yukonUserDao.deleteUser(contact.getLoginID());
                }                
            } else {

                List<LiteContactNotification> notificationList = this.getNotifications(request,
                                                                                       contact);
                this.addNewNotification(request, contactId, notificationList);
                contact.setNotifications(notificationList);

                LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
                boolean autoCreateLogin = energyCompanyRolePropertyDao.checkProperty(YukonRoleProperty.AUTO_CREATE_LOGIN_FOR_ADDITIONAL_CONTACTS, energyCompany);
                // If Auto Create Login is true AND this is not the primary contact AND there is no login for this contact, create one.
                
                if (autoCreateLogin 
                        && !contactDao.isPrimaryContact(contactId) 
                        && contact.getLoginID() == UserUtils.USER_DEFAULT_ID) {

                    StarsYukonUser starsUser = (StarsYukonUser) request.getSession().getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
                    LiteStarsEnergyCompany liteEC = StarsDatabaseCache.getInstance().getEnergyCompany( starsUser.getEnergyCompanyID() );
                    LiteYukonGroup[] custGroups = liteEC.getResidentialCustomerGroups();

                    LiteYukonUser login = yukonUserDao.createLoginForAdditionalContact(firstName, lastName, custGroups[0]);
                    contact.setLoginID(login.getUserID());
                }

                contactDao.saveContact(contact);
            }
        } catch (InvalidNotificationException e) {
            map.addAttribute("failed", "true");
            map.addAttribute("notifCategory", e.getNotifCategory());
            map.addAttribute("notificationText", e.getNotificationText());
        }
        
        return "redirect:/spring/stars/consumer/contacts";
    }

    /**
     * Helper method to get all of the notifications for the contact out of the
     * request
     * @param request - Current request
     * @param contactId - Id of current contact
     * @return List of notifications for the contact
     * @throws InvalidNotificationException 
     */
    private List<LiteContactNotification> getNotifications(
            HttpServletRequest request, LiteContact contact) throws InvalidNotificationException {

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
                Integer notifCatId = typeMap.get(key);
                String notificationText = textMap.get(key);
                String disabledFlag = "N";
                try {
                    if (yukonListDao.isPhoneNumber(notifCatId) || yukonListDao.isFax(notifCatId)) {
                        notificationText = ServletUtils.formatPhoneNumberForStorage( notificationText );
                    } else if(yukonListDao.isPIN(notifCatId)) {
                        notificationText = ServletUtils.formatPin( notificationText );
                    } else if(yukonListDao.isEmail(notifCatId)) {
                        if (contactDao.isPrimaryContact(contact.getContactID())) {
                            LiteContactNotification email = contactNotificationDao.getFirstNotificationForContactByType(contact, ContactNotificationType.getTypeForNotificationCategoryId(notifCatId));
                            if (email != null) {
                                disabledFlag = email.getDisableFlag();
                            }
                        }
                    }
                    LiteContactNotification notification = getContactNotification(contact, notificationId);
                    if (notification != null) {
                        notification.setNotificationCategoryID(notifCatId);
                        notification.setNotification(notificationText);                        
                        notification.setDisableFlag(disabledFlag);
                        notificationList.add(notification);                        
                    }
                } catch (WebClientException e) {
                    String notifCategory = yukonListDao.getYukonListEntry(notifCatId).getEntryText();
                    throw new InvalidNotificationException(notifCategory, notificationText);
                }
            }
        }

        return notificationList;
    }

    private LiteContactNotification getContactNotification(LiteContact contact, int notificationId) {
        LiteContactNotification result = null;
        // notifications are already populated on Contact retrieval above
        for (LiteContactNotification notification : contact.getLiteContactNotifications()) {
            if (notification.getContactNotifID() == notificationId) {
                result = notification;
                break;
            }
        }
        return result;
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
            /* Default the notification to home phone. */
            LiteContactNotification notification = new LiteContactNotification(-1,
                                                                               contactId,
                                                                               ContactNotificationType.HOME_PHONE.getDefinitionId(),
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
    
    @Autowired
    public void setContactNotificationDao(
            ContactNotificationDao contactNotificationDao) {
        this.contactNotificationDao = contactNotificationDao;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setEnergyCompanyRolePropertyDao(EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao) {
        this.energyCompanyRolePropertyDao = energyCompanyRolePropertyDao;
    }
    
}