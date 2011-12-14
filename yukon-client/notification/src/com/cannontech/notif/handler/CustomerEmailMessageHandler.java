package com.cannontech.notif.handler;

import java.util.*;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.message.notif.NotifCustomerEmailMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.outputs.ContactableCustomer;
import com.cannontech.notif.outputs.StandardEmailHandler;
import com.cannontech.notif.server.NotifServerConnection;
import com.cannontech.tools.email.SimpleEmailMessage;

public class CustomerEmailMessageHandler implements MessageHandler<NotifCustomerEmailMsg> {
    
    private static final Logger log = YukonLogManager.getLogger(CustomerEmailMessageHandler.class);
    
    public static final Set<Integer> EMAIL_NOTIFICATION_TYPES = new HashSet<Integer>(1);
    
    static {
        EMAIL_NOTIFICATION_TYPES.add(new Integer(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL));
    }

    @Override
    public Class<NotifCustomerEmailMsg> getSupportedMessageType() {
        return NotifCustomerEmailMsg.class;
    }
    
    @Override
    public void handleMessage(NotifServerConnection connection, Message message) {
        final NotifCustomerEmailMsg msg = (NotifCustomerEmailMsg) message;
        
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
                    log.warn("Unable to email message for " + customer + " to address " + emailTo + ".", e);
                }

            }
        } catch (MessagingException e) {
            log.error("Unable to email message '" + msg.getSubject() + "' to " + customer + ".", e );
        }
    }
}
