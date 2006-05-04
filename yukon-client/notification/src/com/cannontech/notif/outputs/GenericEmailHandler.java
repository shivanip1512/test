package com.cannontech.notif.outputs;

import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;

import org.jdom.Element;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.tools.email.SimpleEmailMessage;

public abstract class GenericEmailHandler extends OutputHandler {

    GenericEmailHandler(String type) {
        super(type);
    }

    public void handleNotification(NotificationBuilder notifFormatter,
            Contactable contact) {
        boolean success = false;
        try {
            List emailList = contact.getNotifications(getTypeChecker());
            if (emailList.size() == 0) {
                CTILogger.warn("Unable to email notification for " + contact 
                               + ", no addresses exist.");
                return;
            }

            Notification notif = notifFormatter.buildNotification(contact);

            NotificationTransformer transformer = 
                new NotificationTransformer(contact.getEnergyCompany(), getType());
            Element outXml = transformer.transform(notif).getRootElement();

            String emailSubject = outXml.getChildTextNormalize("subject");
            String emailBody = outXml.getChildText("body");

            SimpleEmailMessage emailMsg = new SimpleEmailMessage();
            emailMsg.setSubject(emailSubject);
            emailMsg.setBody(emailBody);

            for (Iterator iter = emailList.iterator(); iter.hasNext();) {
                LiteContactNotification emailNotif = (LiteContactNotification) iter.next();
                String emailTo = emailNotif.getNotification();
                // Send the recipient of the email message and attempt to send.
                // The try/catch here prevents a single bad address from
                // preventing
                // other addresses in the contact from being used.
                try {
                    emailMsg.setRecipient(emailTo);
                    emailMsg.send();
                    success = true;
                } catch (MessagingException e) {
                    CTILogger.warn("Unable to email notification for " + contact 
                                   + " to address " + emailTo + ".",
                                   e);
                }
            }
            
            notifFormatter.notificationComplete(contact, getNotificationMethod(), true);

        } catch (Exception e) {
            CTILogger.error("Unable to email notification " 
                            + notifFormatter + " to " + contact + ".",
                            e);
        } finally {
            notifFormatter.notificationComplete(contact, getNotificationMethod(), success);
        }
    }

    public abstract NotificationTypeChecker getTypeChecker();
    public abstract int getNotificationMethod();

}
