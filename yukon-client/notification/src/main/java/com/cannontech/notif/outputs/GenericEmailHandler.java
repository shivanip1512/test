package com.cannontech.notif.outputs;

import java.util.List;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.NotificationTypeChecker;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.notification.NotifType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.tools.email.EmailService;
import com.cannontech.tools.email.EmailMessage;

public abstract class GenericEmailHandler implements OutputHandler {
    private static final Logger log = YukonLogManager.getLogger(GenericEmailHandler.class);
    
    @Override
    public abstract NotifType getType();
    
    @Override
    public void handleNotification(NotificationBuilder notifFormatter, Contactable contact) {
        
        boolean atLeastOneSucceeded = false;
        
        try {
            log.debug("Using " + notifFormatter + " for " + contact);
            List<LiteContactNotification> emailList = contact.getNotifications(getTypeChecker());
            
            if (emailList.isEmpty()) {
                log.debug("Unable to " + getNotificationMethod() + " notification for " + contact 
                               + ", no addresses exist.");
                return;
            }

            Notification notif = notifFormatter.buildNotification(contact);
            log.debug("Built " + notif + " with builder and contact");

            NotificationTransformer transformer =  new NotificationTransformer(contact.getEnergyCompany(), getType().getOldName());
            Element outXml = transformer.transform(notif).getRootElement();

            String emailSubject = outXml.getChildTextNormalize("subject");
            String emailBody = outXml.getChildText("body");
            String from = outXml.getChildText("from");
            
            if (emailBody == null || emailSubject == null) {
                log.warn("Unable to " + getNotificationMethod() + " notification for " + contact 
                               + ", XML output did not contain subject and body.");
                return;
            }

            EmailService emailService = YukonSpringHook.getBean(EmailService.class);

            for (LiteContactNotification emailNotif : emailList) {
                boolean success = false;
                String emailTo = emailNotif.getNotification();
                
                // The try/catch here prevents a single bad address from
                // preventing other addresses in the contact from being tried.
                try {
                    InternetAddress fromAddr = 
                            StringUtils.isNotBlank(from) ? new InternetAddress(from) : null;
                    
                    EmailMessage message = 
                            new EmailMessage(
                                fromAddr,
                                InternetAddress.parse(emailTo), 
                                emailSubject, 
                                emailBody);
                    
                    emailService.sendMessage(message);
                    
                    atLeastOneSucceeded = true;
                    success = true;
                    log.debug("Sent \"" + emailSubject + "\" to " + emailTo);
                } catch (MessagingException e) {
                    log.warn("Unable to " + getNotificationMethod() + " notification for " + contact 
                                   + " to address " + emailTo + ".",
                                   e);
                }
                
                notifFormatter.logIndividualNotification(emailNotif, contact, getNotificationMethod(), success);
            }
            
        } catch (Exception e) {
            log.error("Unable to " + getNotificationMethod() + " notification " 
                            + notifFormatter + " to " + contact + ".",
                            e);
        } finally {
            notifFormatter.notificationComplete(contact, getNotificationMethod(), atLeastOneSucceeded);
        }
    }

    public abstract NotificationTypeChecker getTypeChecker();
    
    @Override
    public abstract NotifType getNotificationMethod();

}
