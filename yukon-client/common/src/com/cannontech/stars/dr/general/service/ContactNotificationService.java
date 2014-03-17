package com.cannontech.stars.dr.general.service;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;

public interface ContactNotificationService {

    
    /**
     * Checks if formatting of entryText is valid for notificationType
     */
    public boolean isListEntryValid(ContactNotificationType notificationType, String entryText);

	/**
	 * Creates/saves a basic LiteContactNotification object.
	 * - disableFlag = "N"
	 * - order = CtiUtilities.NONE_ZERO_ID
	 */
	public LiteContactNotification createNotification(LiteContact contact, ContactNotificationType notificationType, String notificationValue);
}

