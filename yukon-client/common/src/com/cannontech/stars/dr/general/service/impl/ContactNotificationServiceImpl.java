package com.cannontech.stars.dr.general.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.stars.dr.general.service.ContactNotificationService;
import com.cannontech.util.Validator;

public class ContactNotificationServiceImpl implements ContactNotificationService {

	@Autowired private ContactNotificationDao contactNotificationDao;
    @Autowired private PhoneNumberFormattingService phoneNumberFormattingService;
	
    @Override
    public boolean isListEntryValid(ContactNotificationType notificationType, String entry) {
        boolean isValidEntry = true;
            if (notificationType.isFaxType()) {
                isValidEntry = !phoneNumberFormattingService.isHasInvalidCharacters(entry);
            } else if (notificationType.isPhoneType()) {
                isValidEntry = !phoneNumberFormattingService.isHasInvalidCharacters(entry);
            } else if (notificationType.isEmailType()) {
                isValidEntry = Validator.isEmailAddress(entry);
            } else if (notificationType.isPinType()) {
                isValidEntry = Validator.isNumber(entry);
            }
        return isValidEntry;
    }

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
}
