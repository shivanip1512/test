package com.cannontech.multispeak.service;

import java.util.List;

import com.cannontech.stars.dr.jms.message.DrAttributeDataJmsMessage;
import com.cannontech.stars.dr.jms.message.DrProgramStatusJmsMessage;
import com.cannontech.stars.dr.jms.message.EnrollmentJmsMessage;
import com.cannontech.stars.dr.jms.message.OptOutOptInJmsMessage;

public interface DrJmsMessageService {

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
    public void intervalDataNotification(List<DrAttributeDataJmsMessage> drDataJmsMessage);

    /**
     * Sending voltage notification messages to configured vendors.
     */
    public void voltageMeterReadingsNotification(List<DrAttributeDataJmsMessage> drDataJmsMessage);
    
    /**
     * Sending Program Status notification messages to configured vendors.
     */
    public void programStatusNotification(DrProgramStatusJmsMessage drDataJmsMessage);

    /**
     * Sending Alarm and Event notification messages to configured vendors.
     */
    public void alarmAndEventNotification(List<DrAttributeDataJmsMessage> drAttributeDataJmsMessage);

    /**
     * Checks if any vendor configured for method supports.
     */
    public boolean isVendorMethodSupported();

}
