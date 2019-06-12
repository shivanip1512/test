package com.cannontech.web.admin;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.admin.config.model.AdminSetupEmailModel;
import com.cannontech.web.admin.validator.EmailTestValidator;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class EmailTestController {
    @Autowired private ContactDao contactDao;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private EmailTestValidator emailTestValidator;
    private static final Logger log = YukonLogManager.getLogger(EmailTestController.class); 
    
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
    public String sendEmail(@ModelAttribute("email") AdminSetupEmailModel email, BindingResult result, ModelMap model, YukonUserContext userContext, HttpServletResponse resp) throws MessagingException {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        String genBody = accessor.getMessage("yukon.web.modules.adminSetup.config.testEmail.body", userContext.getYukonUser().getUsername());
        String subject = accessor.getMessage("yukon.web.modules.adminSetup.config.testEmail.subject");
        Map<String, Object> json = new HashMap<>();
   
        emailTestValidator.validate(email, result);
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("email", email);
            return "config/emailTest.jsp";
        } else {
            InternetAddress[] postTo = InternetAddress.parse(email.getTo());
            EmailMessage testEmailMessage = new EmailMessage(postTo, subject, genBody);
            String alertBody = accessor.getMessage("yukon.web.modules.adminSetup.config.testEmail.alert", email.getTo());
            json.put("successMessage", alertBody);
            try {
                EmailService emailService = YukonSpringHook.getBean(EmailService.class);
                emailService.sendMessage(testEmailMessage);
            } catch (Exception e) {
                String alertMessageFailure = accessor.getMessage("yukon.web.modules.adminSetup.config.testEmail.failure", email.getTo(), globalSettingDao.getString(GlobalSettingType.SMTP_HOST));
                json.put("failureMessage", alertMessageFailure);
                log.error(e.getMessage(), e);
                return JsonUtils.writeResponse(resp, json);
            }
            log.info(accessor.getMessage("yukon.web.modules.adminSetup.config.testEmail.logSuccess", email.getTo(), globalSettingDao.getString(GlobalSettingType.SMTP_HOST)));
            resp.setContentType("application/json");
            return JsonUtils.writeResponse(resp, json);
        }
    }

}
