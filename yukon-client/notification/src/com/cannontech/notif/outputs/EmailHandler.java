package com.cannontech.notif.outputs;

import java.util.*;

import javax.mail.MessagingException;

import org.jdom.Element;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.notification.NotifMap;
import com.cannontech.tools.email.SimpleEmailMessage;


/**
 *  Handles all outgoing email messages for the NotificationServer.
 */
public class EmailHandler extends OutputHandler
{

    public static final Set EMAIL_NOTIFICATION_TYPES = new HashSet(1);
    
    static {
        EMAIL_NOTIFICATION_TYPES.add(new Integer(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL));
    }

    public EmailHandler() {
        super("email");
    }
    
    public void handleNotification(NotificationBuilder notifFormatter, Contactable contact) {
        try {
            List emailList = contact.getNotifications(EMAIL_NOTIFICATION_TYPES);
            
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
                // The try/catch here prevents a single bad address from preventing
                // other addresses in the contact from being used.
                try {
                    emailMsg.setRecipient(emailTo);
                    emailMsg.send();
                } catch (MessagingException e) {
                    CTILogger.warn("Unable to email notification " + notif + " to address " + emailTo + ".", e);
                }
            }
            
        } catch( Exception e ) {
            CTILogger.error("Unable to email notification " + notifFormatter + " to " + contact + ".", e );
        }
    }

    public int getNotificationMethod() {
        return NotifMap.METHOD_EMAIL;
    }
    
}
