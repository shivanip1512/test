package com.cannontech.notif.outputs;

import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.NotificationTypeChecker;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.notification.NotifType;
import com.cannontech.tools.email.SimpleEmailMessage;

public abstract class GenericEmailHandler extends OutputHandler {

    GenericEmailHandler(String type) {
        super(type);
    }

    public void handleNotification(NotificationBuilder notifFormatter,
            Contactable contact) {
        boolean atLeastOneSucceeded = false;
        try {
            CTILogger.debug("Using " + notifFormatter + " for " + contact);
            List emailList = contact.getNotifications(getTypeChecker());
            if (emailList.size() == 0) {
                CTILogger.warn("Unable to " + getNotificationMethod() + " notification for " + contact 
                               + ", no addresses exist.");
                return;
            }

            Notification notif = notifFormatter.buildNotification(contact);
            CTILogger.debug("Built " + notif + " with builder and contact");

            NotificationTransformer transformer = 
                new NotificationTransformer(contact.getEnergyCompany(), getType());
            Element outXml = transformer.transform(notif).getRootElement();

            String emailSubject = outXml.getChildTextNormalize("subject");
            String emailBody = outXml.getChildText("body");
            String from = outXml.getChildText("from");
            
            if (emailBody == null || emailSubject == null) {
                CTILogger.warn("Unable to " + getNotificationMethod() + " notification for " + contact 
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

            for (Iterator iter = emailList.iterator(); iter.hasNext();) {
                boolean success = false;
                LiteContactNotification emailNotif = (LiteContactNotification) iter.next();
                String emailTo = emailNotif.getNotification();
                // Set the recipient of the email message and attempt to send.
                // The try/catch here prevents a single bad address from
                // preventing other addresses in the contact from being tried.
                try {
                    emailMsg.setRecipient(emailTo);
                    emailMsg.send();
                    atLeastOneSucceeded = true;
                    success = true;
                    CTILogger.debug("Sent \"" + emailSubject + "\" to " + emailTo);
                } catch (MessagingException e) {
                    CTILogger.warn("Unable to " + getNotificationMethod() + " notification for " + contact 
                                   + " to address " + emailTo + ".",
                                   e);
                }
                notifFormatter.logIndividualNotification(emailNotif, contact, getNotificationMethod(), success);
            }
            
        } catch (Exception e) {
            CTILogger.error("Unable to " + getNotificationMethod() + " notification " 
                            + notifFormatter + " to " + contact + ".",
                            e);
        } finally {
            notifFormatter.notificationComplete(contact, getNotificationMethod(), atLeastOneSucceeded);
        }
    }

    public abstract NotificationTypeChecker getTypeChecker();
    public abstract NotifType getNotificationMethod();

}
