package com.cannontech.notif.handler;

import java.util.List;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.message.notif.NotifEmailMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.outputs.*;
import com.cannontech.notif.server.NotifServerConnection;
import com.cannontech.tools.email.SimpleEmailMessage;

public class NotifEmailMessageHandler implements MessageHandler<NotifEmailMsg> {
    private static final Logger log = YukonLogManager.getLogger(NotifEmailMessageHandler.class);

    @Override
    public Class<NotifEmailMsg> getSupportedMessageType() {
        return NotifEmailMsg.class;
    }

    @Override
	public void handleMessage(NotifServerConnection connection, Message message) {
        final NotifEmailMsg msg = (NotifEmailMsg) message;
        
        try {
			SimpleEmailMessage emailMsg = new SimpleEmailMessage();

			emailMsg.setSubject(msg.getSubject());
			emailMsg.setBody(msg.getBody());

			int notifGroupId = msg.getNotifGroupID();
			LiteNotificationGroup liteNotifGroup = DaoFactory.getNotificationGroupDao().getLiteNotificationGroup(notifGroupId);
            
			if(liteNotifGroup == null) {
                return; // we "handled" it, by not sending anything
			}
			
            if (liteNotifGroup.isDisabled()) {
                log.warn("Ignoring notification request because notification group is disabled: group=" + liteNotifGroup);
                return; // we "handled" it, by not sending anything
            }
            
			List<Contactable> contactables = NotifMapContactable.getContactablesForGroup(liteNotifGroup);

			for (Contactable contact : contactables) {
				List<LiteContactNotification> notifications = contact.getNotifications(StandardEmailHandler.checker);
				for (LiteContactNotification addr : notifications) {
					String emailTo = addr.getNotification();
					try {
						emailMsg.setRecipient(emailTo);
						emailMsg.send();
					} catch (MessagingException e) {
					    log.warn("Unable to email message for " + contact + " to address " + emailTo + ".", e);
					}
				}
			}
		} catch (MessagingException e) {
			log.error("Unable to create email message '" + msg.getSubject() + ".", e );
		}
    }
        

}
