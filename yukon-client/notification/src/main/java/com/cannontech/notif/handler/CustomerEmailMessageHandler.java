package com.cannontech.notif.handler;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.message.notif.NotifCustomerEmailMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.outputs.ContactableCustomer;
import com.cannontech.notif.outputs.StandardEmailHandler;
import com.cannontech.notif.server.NotifServerConnection;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.tools.email.EmailService;

public class CustomerEmailMessageHandler implements MessageHandler<NotifCustomerEmailMsg> {
    
    private static final Logger log = YukonLogManager.getLogger(CustomerEmailMessageHandler.class);
    
    @Autowired private EmailService emailService;
    
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
