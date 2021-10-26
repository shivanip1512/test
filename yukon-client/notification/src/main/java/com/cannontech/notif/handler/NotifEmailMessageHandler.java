package com.cannontech.notif.handler;

import java.util.List;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.NotificationGroupDao;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.message.notif.NotifEmailMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.outputs.*;
import com.cannontech.notif.server.NotifServerConnection;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.tools.email.EmailService;
import com.cannontech.tools.email.EmailMessage;

public class NotifEmailMessageHandler implements MessageHandler<NotifEmailMsg> {
    private static final Logger log = YukonLogManager.getLogger(NotifEmailMessageHandler.class);

    @Autowired private NotificationGroupDao notificationGroupDao;
    
    @Override
    public Class<NotifEmailMsg> getSupportedMessageType() {
        return NotifEmailMsg.class;
    }

    @Override
	public void handleMessage(NotifServerConnection connection, Message message) {
        final NotifEmailMsg msg = (NotifEmailMsg) message;
        
		int notifGroupId = msg.getNotifGroupID();
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
				    EmailMessage data = 
				            new EmailMessage(
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