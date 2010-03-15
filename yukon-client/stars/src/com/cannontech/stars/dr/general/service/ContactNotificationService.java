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
}

