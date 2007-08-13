package com.cannontech.notif.handler;

import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.message.notif.NotifEmailMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.outputs.*;
import com.cannontech.notif.server.NotifServerConnection;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.tools.email.SimpleEmailMessage;

public class NotifEmailMessageHandler extends MessageHandler {
    private Logger log = YukonLogManager.getLogger(NotifEmailMessageHandler.class);

	public NotifEmailMessageHandler() {
	}

    @SuppressWarnings("deprecation")
	public boolean handleMessage(NotifServerConnection connection, Message msg_) {
        if (!(msg_ instanceof NotifEmailMsg)) {
            return false;
        }
        final NotifEmailMsg msg = (NotifEmailMsg) msg_;
        
        try {
			SimpleEmailMessage emailMsg = new SimpleEmailMessage();

			emailMsg.setSubject(msg.getSubject());
			emailMsg.setBody(msg.getBody());

			int notifGroupId = msg.getNotifGroupID();
			LiteNotificationGroup liteNotifGroup = DaoFactory.getNotificationGroupDao()
					.getLiteNotificationGroup(notifGroupId);
            
			if(liteNotifGroup == null) {
                return true; // we "handled" it, by not sending anything
			}
			
            if (liteNotifGroup.isDisabled()) {
                log.warn("Ignoring notification request because notification group is disabled: group=" + liteNotifGroup);
                return true; // we "handled" it, by not sending anything
            }
            
			List contactables = NotifMapContactable
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
        return true;
    }
        

}
