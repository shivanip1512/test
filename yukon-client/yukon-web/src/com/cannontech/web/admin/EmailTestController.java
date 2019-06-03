package com.cannontech.web.admin;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.tools.email.EmailService;
import com.cannontech.web.admin.config.model.AdminSetupEmailModel;
import com.cannontech.web.admin.validator.EmailTestValidator;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class EmailTestController {
    @Autowired private ContactDao contactDao;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private EmailTestValidator emailTestValidator;
    
    @RequestMapping(value="/config/emailTestPopup", method=RequestMethod.GET)
    public String testEmailPage(ModelMap model, LiteYukonUser user) throws Exception {
        
        String userDefaultEmail = contactDao.getUserEmail(user);
        String mailFromAddress = globalSettingDao.getString(GlobalSettingType.MAIL_FROM_ADDRESS); 
        AdminSetupEmailModel email = new AdminSetupEmailModel();
        email.setFrom(mailFromAddress);
        email.setTo(userDefaultEmail);
        model.addAttribute("email", email);
        return "config/emailTest.jsp";
    }
    
    @RequestMapping(value="/config/emailTest", method=RequestMethod.POST)
    public String sendEmail(@ModelAttribute("email") AdminSetupEmailModel email, BindingResult result, ModelMap model, LiteYukonUser user, HttpServletResponse resp) throws MessagingException {
        
        String userDefaultEmail = contactDao.getUserEmail(user);
        LiteContact liteContact = contactDao.findContactByEmail(userDefaultEmail);
        String genBody = "YUKON SYSTEM TEST: A test email notification was requested by a user with the following information:\nContact Name: " + liteContact.getContFirstName()
        + " " + liteContact.getContLastName()+"\nUsername: "+user.getUsername();
        String mailFromAddress = email.getFrom();
        
      //if user has not entered preferred MAIL_FROM_ADDRESS, set to AdminSetupEmailModel default yukon@eaton.com
        if (mailFromAddress == null) { 
            email.setTo("");
            mailFromAddress = email.getFrom();
        }
        
        InternetAddress mailFromInternetAddress = new InternetAddress(mailFromAddress);
        emailTestValidator.validate(email, result);
        if (result.hasErrors())
        {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("email",email);
            return "config/emailTest.jsp";
        }
        
        InternetAddress postTo = new InternetAddress(email.getTo());
        InternetAddress[] postArray = new InternetAddress[]{postTo};
        InternetAddress[] postBcc = null;
        InternetAddress[] postCc = null;
        
        EmailMessage testEmailMessage = new EmailMessage(mailFromInternetAddress, postArray,postCc, postBcc,"Yukon Test Email", genBody);
        try
        {
            EmailService emailService = YukonSpringHook.getBean(EmailService.class);
            emailService.sendMessage(testEmailMessage);
        }
        catch( Exception e )
        {
            CTILogger.error( e.getMessage(), e );
        }
        return null;
    }

}
