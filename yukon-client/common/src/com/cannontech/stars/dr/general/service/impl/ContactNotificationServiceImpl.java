package com.cannontech.stars.dr.general.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.stars.dr.general.service.ContactNotificationService;

public class ContactNotificationServiceImpl implements ContactNotificationService {

	private ContactNotificationDao contactNotificationDao;
	
	@Override
	@Transactional
	public LiteContactNotification createNotification(LiteContact contact, ContactNotificationType notificationType, String notification) {
		
		LiteContactNotification notif = makeNotification(contact, notificationType, notification);
		contactNotificationDao.saveNotification(notif);
		
        return notif;
    }
	
	private LiteContactNotification makeNotification(LiteContact contact, ContactNotificationType notificationType, String notification) {
		
		LiteContactNotification notif = new LiteContactNotification(-1);
        notif.setContactID(contact.getContactID());
        notif.setNotificationCategoryID(notificationType.getDefinitionId());
        notif.setDisableFlag("N");
        notif.setNotification(notification);
        notif.setOrder(CtiUtilities.NONE_ZERO_ID);
        
        return notif;
	}
	
	@Autowired
	public void setContactNotificationDao(ContactNotificationDao contactNotificationDao) {
		this.contactNotificationDao = contactNotificationDao;
	}
}
