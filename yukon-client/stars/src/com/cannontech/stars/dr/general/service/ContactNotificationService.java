package com.cannontech.stars.dr.general.service;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.user.YukonUserContext;

public interface ContactNotificationService {

	/**
	 * Creates/saves a basic LiteContactNotification object.
	 * - disableFlag = "N"
	 * - order = CtiUtilities.NONE_ZERO_ID
	 */
	public LiteContactNotification createNotification(LiteContact contact, ContactNotificationType notificationType, String notificationValue);
	
	/**
	 * Creates/saves a basic LiteContactNotification object.
	 * Formats notification based on user using ContactNotificationFormattingService (if needed)
	 * - disableFlag = "N"
	 * - order = CtiUtilities.NONE_ZERO_ID
	 */
	public LiteContactNotification createFormattedNotification(LiteContact contact, ContactNotificationType notificationType, String notificationValue, YukonUserContext userContext);
	
	public LiteContactNotification updateFormattedNotification(int contactId, int notificationId, ContactNotificationType notificationType, String newNotificationValue, YukonUserContext userContext);

	public void saveAsFirstNotificationOfType(int contactId, ContactNotificationType notificationType, String notificationValue, YukonUserContext userContext);
}

