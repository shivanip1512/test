package com.cannontech.notif.outputs;

import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.NotificationTypeChecker;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.notification.NotifType;
import com.cannontech.tools.email.SimpleEmailMessage;

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

            SimpleEmailMessage emailMsg = new SimpleEmailMessage();
            emailMsg.setSubject(emailSubject);
            emailMsg.setBody(emailBody);
            
            // override default from address (SystemRole.MAIL_FROM_ADDRESS) and set using address from XML
            if(StringUtils.isNotBlank(from)) {
                emailMsg.setFrom(from);
            }

            for (LiteContactNotification emailNotif : emailList) {
                boolean success = false;
                String emailTo = emailNotif.getNotification();
                
                // Set the recipient of the email message and attempt to send.
                // The try/catch here prevents a single bad address from
                // preventing other addresses in the contact from being tried.
                
                try {
                    emailMsg.setRecipient(emailTo);
                    emailMsg.send();
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
