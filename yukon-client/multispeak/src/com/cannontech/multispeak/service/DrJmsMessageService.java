package com.cannontech.multispeak.service;

import javax.jms.MessageListener;

import com.cannontech.stars.dr.jms.message.DrAttributeDataJmsMessage;
import com.cannontech.stars.dr.jms.message.EnrollmentJmsMessage;
import com.cannontech.stars.dr.jms.message.OptOutOptInJmsMessage;

public interface DrJmsMessageService extends MessageListener {

    /**
     * Sending enrollment notification messages to configured vendors.
     */
    public void enrollmentNotification(EnrollmentJmsMessage enrollmentJmsMessage);

    /**
     * Sending unenrollment notification messages to configured vendors.
     */

    public void unenrollmentNotification(EnrollmentJmsMessage enrollmentJmsMessage);

    /**
     * Sending optout notification messages to configured vendors.
     */

    public void optOutNotification(OptOutOptInJmsMessage optOutOptInJmsMessage);

    /**
     * Sending cancel optout notification messages to configured vendors.
     */

    public void optInNotification(OptOutOptInJmsMessage optOutOptInJmsMessage);

    /**
     * Sending interval Data notification messages to configured vendors.
     */
    public void intervalDataNotification(DrAttributeDataJmsMessage drDataJmsMessage);

    /**
     * Sending voltage notification messages to configured vendors.
     */
    public void voltageMeterReadingsNotification(DrAttributeDataJmsMessage drDataJmsMessage);

}
