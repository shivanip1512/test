package com.cannontech.stars.dr.general.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.service.ContactNotificationFormattingService;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.stars.dr.general.service.ContactNotificationService;
import com.cannontech.user.YukonUserContext;

public class ContactNotificationServiceImpl implements ContactNotificationService {

	private ContactNotificationFormattingService contactNotificationFormattingService;
	private ContactNotificationDao contactNotificationDao;
	
	@Override
	public LiteContactNotification createNotification(LiteContact contact, ContactNotificationType notificationType, String notification) {
		
		LiteContactNotification notif = makeNotification(contact, notificationType, notification);
		contactNotificationDao.saveNotification(notif);
		
        return notif;
    }
	
	@Override
	public LiteContactNotification createFormattedNotification(LiteContact contact, ContactNotificationType notificationType, String notification, YukonUserContext userContext) {
		
        LiteContactNotification notif = makeNotification(contact, notificationType, notification);
        
        String formattedNotification = contactNotificationFormattingService.formatNotification(notif, userContext);
        notif.setNotification(formattedNotification);
        
        contactNotificationDao.saveNotification(notif);
        
        return notif;
    }
	
	@Override
	public LiteContactNotification updateFormattedNotification(int contactId, int notificationId, ContactNotificationType notificationType, String newNotificationValue, YukonUserContext userContext) {
		
		LiteContactNotification notification = contactNotificationDao.getNotificationForContact(notificationId);
		notification.setNotificationCategoryID(notificationType.getDefinitionId());
		notification.setNotification(newNotificationValue);
		
		String formattedNotification = contactNotificationFormattingService.formatNotification(notification, userContext);
		notification.setNotification(formattedNotification);
		contactNotificationDao.saveNotification(notification);
		
		return notification;
	}
	
	@Override
	public void saveAsFirstNotificationOfType(int contactId, ContactNotificationType notificationType, String notificationValue, YukonUserContext userContext) {
		
		
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
	public void setContactNotificationFormattingService(ContactNotificationFormattingService contactNotificationFormattingService) {
		this.contactNotificationFormattingService = contactNotificationFormattingService;
	}
	
	@Autowired
	public void setContactNotificationDao(ContactNotificationDao contactNotificationDao) {
		this.contactNotificationDao = contactNotificationDao;
	}
}
