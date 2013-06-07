package com.cannontech.notif.handler;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.NotificationGroupDao;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.notif.EmailMessage;
import com.cannontech.notif.outputs.*;
import com.cannontech.notif.server.NotifServerConnection;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.tools.email.EmailService;
import com.cannontech.tools.email.EmailServiceMessage;

public class NotifEmailMessageHandler implements MessageHandler<EmailMessage> {
    private static final Logger log = YukonLogManager.getLogger(NotifEmailMessageHandler.class);

    @Autowired private NotificationGroupDao notificationGroupDao;
    
    @Override
    public Class<EmailMessage> getSupportedMessageType() {
        return EmailMessage.class;
    }

    @Override
	public void handleMessage(NotifServerConnection connection, BaseMessage message) {
        final EmailMessage msg = (EmailMessage) message;
        
			int notifGroupId = msg.getNotifGroupId();
		LiteNotificationGroup liteNotifGroup = notificationGroupDao.getLiteNotificationGroup(notifGroupId);
        
		if (liteNotifGroup == null) {
		    log.info("Ignoring notification request because notification group with id " + notifGroupId + " doesn't exist.");
            return; // we "handled" it, by not sending anything
		}
		
        if (liteNotifGroup.isDisabled()) {
            log.info("Ignoring notification request because notification group is disabled: group=" + liteNotifGroup);
            return; // we "handled" it, by not sending anything
        }
        
		List<Contactable> contactables = NotifMapContactable.getContactablesForGroup(liteNotifGroup);

		EmailService emailService = YukonSpringHook.getBean(EmailService.class);
		
		for (Contactable contact : contactables) {
			List<LiteContactNotification> notifications = contact.getNotifications(StandardEmailHandler.checker);
			for (LiteContactNotification addr : notifications) {
				String emailTo = addr.getNotification();
				try {
				    EmailServiceMessage data = 
				            new EmailServiceMessage(
                                InternetAddress.parse(emailTo),
                                msg.getSubject(),
                                msg.getBody());
				    
				    emailService.sendMessage(data);
				} catch (MessagingException e) {
				    log.warn("Unable to email message for " + contact + " to address " + emailTo + ".", e);
				}
			}
		}
    }
}
