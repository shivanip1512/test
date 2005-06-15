package com.cannontech.notif.outputs;

import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;

import org.jdom.Element;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.roles.yukon.VoiceServerRole;
import com.cannontech.tools.email.SimpleEmailMessage;


/**
 *  Handles all outgoing email messages for the NotificationServer.
 */
public class EmailHandler extends OutputHandler
{

    private NotificationTransformer _transformer;

    public EmailHandler() {
        super(Contactable.EMAIL);
        
        String xslRootDirectory = RoleFuncs.getGlobalPropertyValue(VoiceServerRole.TEMPLATE_ROOT);
        _transformer = new NotificationTransformer(xslRootDirectory, getType());
    }
    
    public void handleNotification(NotificationBuilder notifFormatter, Contactable contact) {
        try
        {
            List emailList = contact.getEmailList();
            
            Notification notif = notifFormatter.buildNotification(contact);
            
            Element outXml = _transformer.transform(notif).getRootElement();
            
            String emailSubject = outXml.getChildTextNormalize("subject");
            String emailBody = outXml.getChildText("body");
            
            SimpleEmailMessage emailMsg = new SimpleEmailMessage();
            emailMsg.setSubject(emailSubject);
            emailMsg.setBody(emailBody);
            
            for (Iterator iter = emailList.iterator(); iter.hasNext();) {
                String emailTo = (String) iter.next();
                // Send the recipient of the email message and attempt to send.
                // The try/catch here prevents a single bad address from preventing
                // other addresses in the contact from being used.
                try {
                    emailMsg.setRecipient(emailTo);
                    emailMsg.send(); //TODO uncomment
                } catch (MessagingException e) {
                    CTILogger.warn("Unable to email notification " + notif + " to address " + emailTo + ".", e);
                }
            }
            
        }
        catch( Exception e )
        {
            CTILogger.error("Unable to email notification " + notifFormatter + " to " + contact + ".", e );
        }
    }
    
}
