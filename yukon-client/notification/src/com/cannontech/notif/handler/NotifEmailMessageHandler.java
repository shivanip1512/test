package com.cannontech.notif.handler;

import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.functions.NotificationGroupFuncs;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.message.notif.NotifEmailMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.outputs.Contactable;
import com.cannontech.notif.outputs.StandardEmailHandler;
import com.cannontech.notif.server.NotifServerConnection;
import com.cannontech.tools.email.SimpleEmailMessage;

public class NotifEmailMessageHandler extends MessageHandler {

	public NotifEmailMessageHandler() {
	}

	public boolean canHandle(Message msg) {
        return msg instanceof NotifEmailMsg;
    }

    public void handleMessage(NotifServerConnection connection, Message msg_) {
        final NotifEmailMsg msg = (NotifEmailMsg) msg_;
        
        try {
			SimpleEmailMessage emailMsg = new SimpleEmailMessage();

			emailMsg.setSubject(msg.getSubject());
			emailMsg.setBody(msg.getBody());

			int notifGroupId = msg.getNotifGroupID();
			LiteNotificationGroup liteNotifGroup = NotificationGroupFuncs
					.getLiteNotificationGroup(notifGroupId);
			List contactables = Contactable
					.getContactablesForGroup(liteNotifGroup);

			for (Iterator iter = contactables.iterator(); iter.hasNext();) {
				Contactable contact = (Contactable) iter.next();
				List notifications = contact.getNotifications(StandardEmailHandler.checker);
				for (Iterator iter2 = notifications.iterator(); iter2.hasNext();) {
					LiteContactNotification addr = (LiteContactNotification) iter2.next();
					String emailTo = addr.getNotification();
					try {
						emailMsg.setRecipient(emailTo);
						emailMsg.send();
					} catch (MessagingException e) {
						CTILogger.warn("Unable to email message for " + contact
								+ " to address " + emailTo + ".", e);
					}
				}
			}
		} catch (MessagingException e) {
			CTILogger.error("Unable to create email message '" + msg.getSubject() + ".", e );
		}
    }
        

}
