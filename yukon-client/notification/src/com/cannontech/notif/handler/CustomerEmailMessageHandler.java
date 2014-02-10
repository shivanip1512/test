package com.cannontech.notif.handler;

import java.util.HashSet;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.message.notif.NotifCustomerEmailMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.outputs.ContactableCustomer;
import com.cannontech.notif.outputs.StandardEmailHandler;
import com.cannontech.notif.server.NotifServerConnection;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.tools.email.EmailService;
import com.cannontech.tools.email.EmailMessage;

public class CustomerEmailMessageHandler implements MessageHandler<NotifCustomerEmailMsg> {
    
    private static final Logger log = YukonLogManager.getLogger(CustomerEmailMessageHandler.class);
    
    @Autowired private EmailService emailService;
    
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
        
        ContactableCustomer customer = new ContactableCustomer(YukonSpringHook.getBean(CustomerDao.class).getLiteCICustomer(msg.getCustomerID()));
        
        for (LiteContactNotification notif : customer.getNotifications(StandardEmailHandler.checker)) {
            String emailTo = notif.getNotification();
            try {
                EmailMessage data = 
                        new EmailMessage(
                            InternetAddress.parse(emailTo), 
                            msg.getSubject(), 
                            msg.getBody());
                
                emailService.sendMessage(data);
            } catch (MessagingException e) {
                log.warn("Unable to email message for " + customer + " to address " + emailTo + ".", e);
            }
        }
    }
}
