package com.cannontech.stars.dr.general.service;

import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.stars.dr.general.model.OperatorContactNotificationType;
import com.cannontech.user.YukonUserContext;

public interface ContactNotificationService {

	/**
	 * Creates/saves a basic LiteContactNotification object.
	 * - disableFlag = "N"
	 * - order = CtiUtilities.NONE_ZERO_ID
	 * @param contact
	 * @param notificationType
	 * @param notificationValue
	 * @return
	 */
	public LiteContactNotification createNotification(LiteContact contact, OperatorContactNotificationType notificationType, String notificationValue);
	
	/**
	 * Creates/saves a basic LiteContactNotification object.
	 * Formats notification based on user using ContactNotificationFormattingService (if needed)
	 * - disableFlag = "N"
	 * - order = CtiUtilities.NONE_ZERO_ID
	 * @param contact
	 * @param notificationType
	 * @param notificationValue
	 * @param userContext
	 * @return
	 */
	public LiteContactNotification createFormattedNotification(LiteContact contact, OperatorContactNotificationType notificationType, String notificationValue, YukonUserContext userContext);
	
	public LiteContactNotification updateFormattedNotification(int contactId, int notificationId, OperatorContactNotificationType notificationType, String newNotificationValue, YukonUserContext userContext);

	public void saveAsFirstNotificationOfType(int contactId, OperatorContactNotificationType notificationType, String notificationValue, YukonUserContext userContext);
}

