package com.cannontech.notif.handler;

import java.util.*;

import javax.mail.MessagingException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.message.notif.NotifCustomerEmailMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.outputs.ContactableCustomer;
import com.cannontech.notif.outputs.StandardEmailHandler;
import com.cannontech.notif.server.NotifServerConnection;
import com.cannontech.tools.email.SimpleEmailMessage;

public class CustomerEmailMessageHandler extends MessageHandler {
    public static final Set<Integer> EMAIL_NOTIFICATION_TYPES = new HashSet<Integer>(1);
    
    static {
        EMAIL_NOTIFICATION_TYPES.add(new Integer(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL));
    }

    public boolean handleMessage(NotifServerConnection connection, Message msg_) {
        if (!(msg_ instanceof NotifCustomerEmailMsg)) {
            return false;
        }
        final NotifCustomerEmailMsg msg = (NotifCustomerEmailMsg) msg_;
        
        ContactableCustomer customer = new ContactableCustomer(DaoFactory.getCustomerDao().getLiteCICustomer(msg.getCustomerID()));
        
        try {
            SimpleEmailMessage emailMsg = new SimpleEmailMessage();

            emailMsg.setSubject(msg.getSubject());
            emailMsg.setBody(msg.getBody());
            
            for (Iterator iter = customer.getNotifications(StandardEmailHandler.checker).iterator(); iter.hasNext();) {
                LiteContactNotification addr = (LiteContactNotification) iter.next();
                String emailTo = addr.getNotification();
                try {
                    emailMsg.setRecipient(emailTo);
                    emailMsg.send();
                } catch (MessagingException e) {
                    CTILogger.warn("Unable to email message for " + customer + " to address " + emailTo + ".", e);
                }

            }
        } catch (MessagingException e) {
            CTILogger.error("Unable to email message '" + msg.getSubject() + "' to " + customer + ".", e );
        }
        return true;
    }

}
