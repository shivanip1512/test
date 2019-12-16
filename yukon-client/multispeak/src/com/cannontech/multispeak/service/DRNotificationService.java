package com.cannontech.multispeak.service;

import javax.jms.MessageListener;

import com.cannontech.stars.dr.jms.notification.message.DRNotificationDataMessage;
import com.cannontech.stars.dr.jms.notification.message.EnrollmentNotificationMessage;
import com.cannontech.stars.dr.jms.notification.message.OptOutInNotificationMessage;

public interface DRNotificationService extends MessageListener {

    /**
     * Sending enrollment notification messages to configured vendors.
     */
    public void enrollmentNotification(EnrollmentNotificationMessage enrollmentNotificationMessage);

    /**
     * Sending unenrollment notification messages to configured vendors.
     */

    public void unenrollmentNotification(EnrollmentNotificationMessage enrollmentNotificationMessage);

    /**
     * Sending optout notification messages to configured vendors.
     */

    public void optOutNotification(OptOutInNotificationMessage optOutNotificationMessage);

    /**
     * Sending cancel optout notification messages to configured vendors.
     */

    public void optInNotification(OptOutInNotificationMessage optInNotificationMessage);

    /**
     * Sending Interval Data notification messages to configured vendors.
     */
    public void intervalDataNotification(DRNotificationDataMessage drNotificationDataMessage);

    /**
     * Sending voltage notification messages to configured vendors.
     */
    void voltageMeterReadingsNotification(DRNotificationDataMessage drNotificationDataMessage);

}
