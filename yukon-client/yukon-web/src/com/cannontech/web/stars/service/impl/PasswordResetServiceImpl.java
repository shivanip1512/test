package com.cannontech.web.stars.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.WebserverUrlResolver;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.login.model.PasswordResetInfo;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.tools.email.EmailException;
import com.cannontech.tools.email.EmailHtmlMessage;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.stars.service.PasswordResetService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;

public class PasswordResetServiceImpl implements PasswordResetService {
    private final Logger log = YukonLogManager.getLogger(PasswordResetServiceImpl.class);
    private final Cache<UUID, LiteYukonUser> userToPasswordSetCache =
            CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterWrite(1, TimeUnit.HOURS).build();
    
    @Autowired private ContactDao contactDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private ContactNotificationDao contactNotificationDao;
    @Autowired private EmailService emailService;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private WebserverUrlResolver webserverUrlResolver;

    @Override
    public PasswordResetInfo getPasswordResetInfo(String forgottenPasswordField) {
        // Check to see if we have a user that matches the information supplied
        PasswordResetInfo passwordResetInfo = getPasswordResetInfoByUsername(forgottenPasswordField);
                
        // It looks like they didn't supply a user name.  Lets see if they supplied an email.
        if (!passwordResetInfo.isValidUser()) {
            passwordResetInfo = getPasswordResetInfoByEmailAddress(forgottenPasswordField);
        }
        
        // It looks like they didn't supply a user name nor an email.  Lets see if they supplied an account number.
        if (!passwordResetInfo.isValidUser()) {
            passwordResetInfo = getPasswordResetInfoByAccountNumber(forgottenPasswordField);
        }
        
        return passwordResetInfo;
    }

    @Override
    public void sendPasswordResetEmail(String forgottenPasswordResetUrl, LiteContact liteContact,
            YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        List<LiteContactNotification> emailAddresses =
                contactNotificationDao.getNotificationsForContactByType(liteContact, ContactNotificationType.EMAIL);
        if (emailAddresses.isEmpty()) {
            throw new NotFoundException("A valid email address was not found for the supplied contact "+liteContact);
        }
        
        MessageSourceResolvable subjectKey =
                new YukonMessageSourceResolvable("yukon.web.modules.login.forgottenPassword.email.subject");
        String subject = messageSourceAccessor.getMessage(subjectKey);

        MessageSourceResolvable bodyKey =
                new YukonMessageSourceResolvable("yukon.web.modules.login.forgottenPassword.email.body",
                    forgottenPasswordResetUrl);
        String htmlBody = messageSourceAccessor.getMessage(bodyKey);
        
        for (LiteContactNotification emailAddress : emailAddresses) {
            try {
                EmailHtmlMessage emailMessage = 
                        new EmailHtmlMessage(InternetAddress.parse(emailAddress.getNotification()), subject, htmlBody,
                            htmlBody);
                emailService.sendMessage(emailMessage);
            } catch (MessagingException e) {
                log.error("error sending email", e);
                throw new EmailException(e);
            }
        }
    }
    
    @Override
    public String getPasswordResetUrl(String username, HttpServletRequest request, boolean useYukonExternalUrl) {
        LiteYukonUser user = yukonUserDao.findUserByUsername(username);
        String passwordResetKey = getPasswordKey(user);

        String defaultYukonExternalUrl = ServletUtil.getDefaultYukonExternalUrl(request);
        String postfix = request.getContextPath() + "/login/change-password?k=" + passwordResetKey;
        if (!useYukonExternalUrl) {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(defaultYukonExternalUrl);
            urlBuilder.append(postfix);
            return urlBuilder.toString();
        }
        String url = webserverUrlResolver.getUrl(postfix, defaultYukonExternalUrl);

        return url;
    }

    @Override
    public String getPasswordKey(LiteYukonUser user) {
        UUID passwordResetKey = UUID.randomUUID();
        userToPasswordSetCache.put(passwordResetKey, user);
        return passwordResetKey.toString();
    }

    @Override
    public LiteYukonUser findUserFromPasswordKey(String passwordKey) {
        return userToPasswordSetCache.getIfPresent(UUID.fromString(passwordKey));
    }

    @Override
    public void invalidatePasswordKey(String passwordKey) {
        userToPasswordSetCache.invalidate(UUID.fromString(passwordKey));
    }
    
    /**
     * This method will attempt to get the passwordResetInfo by the username.  If there are more than one contact
     * associated with an account it will use the first one.
     */
    private PasswordResetInfo getPasswordResetInfoByUsername(String forgottenPasswordField) {
        PasswordResetInfo passwordResetInfo = new PasswordResetInfo();
        
        passwordResetInfo.setUser(yukonUserDao.findUserByUsername(forgottenPasswordField));
        if (passwordResetInfo.isValidUser()) {
            List<LiteContact> passwordResetContacts =
                    contactDao.getContactsByLoginId(passwordResetInfo.getUser().getUserID());
            if (!passwordResetContacts.isEmpty()) {
                passwordResetInfo.setContact(passwordResetContacts.get(0));
            }
        }
        
        return passwordResetInfo;
    }

    /**
     * This method will attempt to get the passwordResetInfo by the email address.  It will attempt to get a user
     * attached to that email address or set the user to null if one doesn't exist.
     */
    private PasswordResetInfo getPasswordResetInfoByEmailAddress(String forgottenPasswordField) {
        PasswordResetInfo passwordResetInfo = new PasswordResetInfo();
        //get all contact notifications by email address
        List<LiteContactNotification> contactNotifications =
                contactNotificationDao.getNotificationsForNotificationByType(forgottenPasswordField,
                    ContactNotificationType.EMAIL);
        HashSet<Integer> contactIDs = Sets.newHashSet();
        //make sure if more then 1 email found it is for the same contact
        for (LiteContactNotification contactNotification : contactNotifications) {
            contactIDs.add(contactNotification.getContactID());
        }
        //if one only contact found attempt to get a user
        if (contactIDs.size() == 1) {
            int contactId = contactIDs.iterator().next();
            passwordResetInfo.setContact(contactDao.getContact(contactId));
            if (passwordResetInfo.isValidContact()) {
                passwordResetInfo.setUser(yukonUserDao.getLiteYukonUser(passwordResetInfo.getContact().getLoginID()));
            }
        }
        return passwordResetInfo;
    }

    /**
     * This method will attempt to get the passwordResetInfo by the account number. This method will use all
     * the energy companies in the system to track down the supplied account. After that it will use the
     * account to derive the user and primary contact information.
     */
    private PasswordResetInfo getPasswordResetInfoByAccountNumber(String forgottenPasswordField) {
        PasswordResetInfo passwordResetInfo = new PasswordResetInfo();
        CustomerAccount passwordResetCustomerAccount = 
                customerAccountDao.findByAccountNumber(forgottenPasswordField, ecDao.getAllEnergyCompanies());
        // getYukonUserByAccountId is more of a find method.  It returns null if it doesn't exist.
        if (passwordResetCustomerAccount != null) {
            passwordResetInfo.setUser(customerAccountDao.getYukonUserByAccountId(passwordResetCustomerAccount.getAccountId()));
            if (passwordResetInfo.isValidUser()) {
                // getLiteContact is more of a find method.  It returns null if it doesn't exist.
                passwordResetInfo.setContact(yukonUserDao.getLiteContact(passwordResetInfo.getUser().getUserID()));
            }
        }
        
        return passwordResetInfo;
    }
}
