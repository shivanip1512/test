package com.cannontech.stars.core.login.service.impl;

import java.util.List;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.login.model.PasswordResetInfo;
import com.cannontech.stars.core.login.service.ForgottenPasswordService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.tools.email.DefaultEmailMessage;
import com.cannontech.tools.email.EmailMessageHolder;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class ForgottenPasswordServiceImpl implements ForgottenPasswordService {
    private Logger logger = YukonLogManager.getLogger(ForgottenPasswordServiceImpl.class);
    
    @Autowired private ContactDao contactDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private ContactNotificationDao contactNotificationDao;
    @Autowired private EmailService emailService;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
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

    public void sendPasswordResetEmail(String forgottenPasswordResetUrl, LiteContact liteContract, YukonUserContext userContext) {
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        List<LiteContactNotification> emailAddresses = contactNotificationDao.getNotificationsForContactByType(liteContract, ContactNotificationType.EMAIL);
        
        MessageSourceResolvable subjectKey = new YukonMessageSourceResolvable("yukon.web.modules.login.forgottenPassword.email.subject");
        String subject = messageSourceAccessor.getMessage(subjectKey);

        MessageSourceResolvable bodyKey = new YukonMessageSourceResolvable("yukon.web.modules.login.forgottenPassword.email.body", forgottenPasswordResetUrl);
        String body = messageSourceAccessor.getMessage(bodyKey);
        
        for (LiteContactNotification emailAddress : emailAddresses) {
            EmailMessageHolder emailMessage = new DefaultEmailMessage(emailAddress.getNotification(), subject, body);
        
            try {
                emailService.sendMessage(emailMessage);
            } catch (MessagingException e) {
                logger.error(e);
            }
        }
    }
    
    private PasswordResetInfo getPasswordResetInfoByUsername(String forgottenPasswordField) {
        PasswordResetInfo passwordResetInfo = new PasswordResetInfo();
        
        passwordResetInfo.setUser(yukonUserDao.findUserByUsername(forgottenPasswordField));
        if (passwordResetInfo.isValidUser()) {
            List<LiteContact> passwordResetContacts = contactDao.getContactsByLoginId(passwordResetInfo.getUser().getUserID());
            if (!passwordResetContacts.isEmpty()) {
                passwordResetInfo.setContact(passwordResetContacts.get(0));
            }
        }
        
        return passwordResetInfo;
    }

    private PasswordResetInfo getPasswordResetInfoByEmailAddress(String forgottenPasswordField) {
        PasswordResetInfo passwordResetInfo = new PasswordResetInfo();
        
        // getContactByEmailNotif is more of a find method.  It returns null if it doesn't exist.
        passwordResetInfo.setContact(contactDao.getContactByEmailNotif(forgottenPasswordField));
        if (passwordResetInfo.isValidContact()) {
            passwordResetInfo.setUser(yukonUserDao.getLiteYukonUser(passwordResetInfo.getContact().getLoginID()));
        }
        
        return passwordResetInfo;
    }

    private PasswordResetInfo getPasswordResetInfoByAccountNumber(String forgottenPasswordField) {
        PasswordResetInfo passwordResetInfo = new PasswordResetInfo();
        
        List<LiteEnergyCompany> allEnergyCompanies = energyCompanyDao.getAllEnergyCompanies();
        List<Integer> allEnergyCompanyIds = Lists.transform(allEnergyCompanies, new Function<LiteEnergyCompany, Integer>() {
            @Override
            public Integer apply(LiteEnergyCompany liteEnergyCompany) {
                return liteEnergyCompany.getEnergyCompanyID();
            }});
        
        CustomerAccount passwordResetCustomerAccount = customerAccountDao.findByAccountNumber(forgottenPasswordField, allEnergyCompanyIds);
        if (passwordResetCustomerAccount != null) {
            // getContactByEmailNotif is more of a find method.  It returns null if it doesn't exist.
            passwordResetInfo.setContact(contactDao.getContactByEmailNotif(forgottenPasswordField));
            if (passwordResetInfo.isValidContact()) {
                passwordResetInfo.setUser(yukonUserDao.getLiteYukonUser(passwordResetInfo.getContact().getLoginID()));
            }
        }
        
        return passwordResetInfo;
    }
}